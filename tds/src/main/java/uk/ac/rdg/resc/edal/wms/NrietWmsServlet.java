package uk.ac.rdg.resc.edal.wms;


import com.nriet.nbase.GridData;
import com.nriet.nbase.inner.util.RawHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.JFreeChart;
import org.joda.time.DateTime;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.rdg.resc.edal.dataset.Dataset;
import uk.ac.rdg.resc.edal.dataset.DiscreteLayeredDataset;
import uk.ac.rdg.resc.edal.domain.HorizontalDomain;
import uk.ac.rdg.resc.edal.domain.PointCollectionDomain;
import uk.ac.rdg.resc.edal.domain.TemporalDomain;
import uk.ac.rdg.resc.edal.domain.VerticalDomain;
import uk.ac.rdg.resc.edal.exceptions.EdalException;
import uk.ac.rdg.resc.edal.feature.DiscreteFeature;
import uk.ac.rdg.resc.edal.feature.Feature;
import uk.ac.rdg.resc.edal.feature.MapFeature;
import uk.ac.rdg.resc.edal.feature.PointCollectionFeature;
import uk.ac.rdg.resc.edal.geometry.BoundingBox;
import uk.ac.rdg.resc.edal.geometry.LineString;
import uk.ac.rdg.resc.edal.graphics.Charting;
import uk.ac.rdg.resc.edal.graphics.exceptions.EdalLayerNotFoundException;
import uk.ac.rdg.resc.edal.graphics.formats.InvalidFormatException;
import uk.ac.rdg.resc.edal.graphics.utils.*;
import uk.ac.rdg.resc.edal.grid.HorizontalGrid;
import uk.ac.rdg.resc.edal.grid.RegularAxisImpl;
import uk.ac.rdg.resc.edal.metadata.VariableMetadata;
import uk.ac.rdg.resc.edal.position.HorizontalPosition;
import uk.ac.rdg.resc.edal.position.VerticalPosition;
import uk.ac.rdg.resc.edal.util.Array2D;
import uk.ac.rdg.resc.edal.util.CollectionUtils;
import uk.ac.rdg.resc.edal.util.GISUtils;
import uk.ac.rdg.resc.edal.util.TimeUtils;
import uk.ac.rdg.resc.edal.wms.exceptions.EdalUnsupportedOperationException;
import uk.ac.rdg.resc.edal.wms.util.WmsUtils;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.*;

public class NrietWmsServlet {
    private static final Logger log = LoggerFactory.getLogger(NrietWmsServlet.class);
    private static float min = 0.7f;
    private static float max = 0.95f;

    public static final int AXIS_RESOLUTION = 500;


    protected NrietWmsServlet() {

    }

    /**
     * 多个单时次raw格式数据合并
     *
     * @param srcRaws
     * @return
     */
    public static byte[] combine(byte[][] srcRaws) {
        byte[] buf = null;
        try {
            int len = 43 + 8 * srcRaws.length + (srcRaws[0].length - 51) * srcRaws.length;
            buf = new byte[len];
            ByteBuffer bb = ByteBuffer.wrap(buf);
            bb.put(srcRaws[0], 0, 43);
            bb.putShort(15, (short) srcRaws.length); // 更新存取格点数据时间逻辑所占行数
            for (int i = srcRaws.length - 1; i >= 0; i--) {
                bb.put(srcRaws[i], 43, 8); // 各个时次数据时间（顺序） 2017-09-15 13:45修改
            }
            for (int i = srcRaws.length - 1; i >= 0; i--) {
                bb.put(srcRaws[i], 51, srcRaws[0].length - 51);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }

    public static GridData getGridData(RequestParams params, WmsCatalogue catalogue){
        GetMapParameters getMapParams = new GetMapParameters(params, catalogue);
        PlottingDomainParams plottingParameters = getMapParams.getPlottingDomainParameters();

        String[] layerNames = getMapParams.getStyleParameters().getLayerNames();
        LayerNameMapper layerNameMapper = catalogue.getLayerNameMapper();
        List<Feature<?>> features = new ArrayList<>();
        for (String layerName : layerNames) {
            if (!catalogue.isDownloadable(layerName)) {
                throw new InvalidFormatException(
                        "The format \"application/nraw\" is not enabled for this layer.\nIf you think this is an error, please contact the server administrator and get them to enable Download for this dataset");
            }
            Dataset dataset = catalogue
                    .getDatasetFromId(layerNameMapper.getDatasetIdFromLayerName(layerName));
            VariableMetadata metadata = dataset
                    .getVariableMetadata(layerNameMapper.getVariableIdFromLayerName(layerName));
            if (metadata.isScalar()) {
                Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                        .extractGeneralMapFeatures(dataset,
                                layerNameMapper.getVariableIdFromLayerName(layerName),
                                plottingParameters);

                features.addAll(mapFeatures);
            } else {
                Set<VariableMetadata> children = metadata.getChildren();
                for (VariableMetadata child : children) {
                    Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                            .extractGeneralMapFeatures(dataset,
                                    child.getParameter().getVariableId(), plottingParameters);
                    features.addAll(mapFeatures);
                }
            }
        }
        MapFeature mapFeature=((MapFeature)features.get(0));
        BoundingBox boundingBox= mapFeature.getDomain().getHorizontalGrid().getBoundingBox();
        RegularAxisImpl xAxis= (RegularAxisImpl) mapFeature.getDomain().getHorizontalGrid().getXAxis();
        RegularAxisImpl yAxis= (RegularAxisImpl) mapFeature.getDomain().getHorizontalGrid().getYAxis();
        //GeographicBoundingBox geographicBoundingBox= mapFeature.getDomain().getHorizontalGrid().getGeographicBoundingBox();

        GridData gridData = new GridData();
        gridData.setStartx((float)boundingBox.getMinX());
        gridData.setEndx((float)boundingBox.getMaxX());
        gridData.setStarty((float)boundingBox.getMinY());
        gridData.setEndy((float)boundingBox.getMaxY());
        gridData.setDx((float)xAxis.getCoordinateSpacing());
        gridData.setDy((float)yAxis.getCoordinateSpacing());
        gridData.setNx(xAxis.size());
        gridData.setNy(yAxis.size());
        gridData.setInvalidVal(Float.NaN);
        gridData.setTime(new Date());

        if (features.size() == 1) {
            Array2D<Number> array2D=mapFeature.getValues(layerNames[0]);
            int ySize = array2D.getYSize();
            int xSize = array2D.getXSize();
           float[][] data = new float[ySize][xSize];
            int y = 0;
            int x = 0;
            for (Number value:array2D){
                if(value!=null) {
                    data[y][x] = value.floatValue();
                }else {
                    data[y][x] = Float.NaN;
                }
                ++x;
                if (x == xSize) {
                    x = 0;
                    ++y;
                }
            }
            gridData.setVals(data);
        } else {
           float[][] data = new float[yAxis.size()*features.size()][xAxis.size()];
            for (int i = 0; i < features.size(); i++) {
                Array2D<Number> array2D=((MapFeature)features.get(i)).getValues(layerNames[i]);
                int y = 0+(i*yAxis.size());
                int x = 0;
                for (Number value:array2D){
                    if(value!=null) {
                        data[y][x] = value.floatValue();
                    }else {
                        data[y][x] = Float.NaN;
                    }
                    ++x;
                    if (x == xAxis.size()) {
                        x = 0;
                        ++y;
                    }
                }
            }
            gridData.setVals(data);
        }
        return gridData;
    }

    public static void getWmsMapNraw(RequestParams params, HttpServletResponse httpServletResponse,
                                     WmsCatalogue catalogue){
        GetMapParameters getMapParams = new GetMapParameters(params, catalogue);
        PlottingDomainParams plottingParameters = getMapParams.getPlottingDomainParameters();

        String[] layerNames = getMapParams.getStyleParameters().getLayerNames();
        LayerNameMapper layerNameMapper = catalogue.getLayerNameMapper();
        List<Feature<?>> features = new ArrayList<>();
        for (String layerName : layerNames) {
            if (!catalogue.isDownloadable(layerName)) {
                throw new InvalidFormatException(
                        "The format \"application/nraw\" is not enabled for this layer.\nIf you think this is an error, please contact the server administrator and get them to enable Download for this dataset");
            }
            Dataset dataset = catalogue
                    .getDatasetFromId(layerNameMapper.getDatasetIdFromLayerName(layerName));
            VariableMetadata metadata = dataset
                    .getVariableMetadata(layerNameMapper.getVariableIdFromLayerName(layerName));
            if (metadata.isScalar()) {
                Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                        .extractGeneralMapFeatures(dataset,
                                layerNameMapper.getVariableIdFromLayerName(layerName),
                                plottingParameters);

                features.addAll(mapFeatures);
            } else {
                Set<VariableMetadata> children = metadata.getChildren();
                for (VariableMetadata child : children) {
                    Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                            .extractGeneralMapFeatures(dataset,
                                    child.getParameter().getVariableId(), plottingParameters);
                    features.addAll(mapFeatures);
                }
            }
        }
        httpServletResponse.setContentType("application/nraw");
        httpServletResponse.addHeader("Content-Disposition", "attachment;filename="+layerNames[0]+".raw");
        String rawMethod=params.getString("rawMethod","2");
        String srs=params.getString("SRS");
        String projection="1";
        //投影方式，1：等经纬；2：兰伯特；3：墨卡托
        switch (srs) {
            case "EPSG:4326":
                projection="1";
                break;
            case "EPSG:3034":
                projection="2";
                break;
            case "EPSG:3035":
                projection="2";
                break;
            case "EPSG:2163":
                projection="2";
                break;
            case "EPSG:3857":
                projection="3";
                break;
            case "EPSG:3395":
                projection="3";
                break;
        }
        boolean signed=Boolean.valueOf(params.getString("rawSigned","false"));
        try {
            GridData gridData = getGridData(params,catalogue);
            byte[] nraw=null;
            if (features.size() == 1) {
                switch (rawMethod) {
                    case "2":
                        nraw = RawHelper.grid2raw2(gridData.getVals(), gridData.getInvalidVal(), gridData.getStartx(), gridData.getStarty(), gridData.getDx(), gridData.getDy(), gridData.getNx(), gridData.getNy(), gridData.getTime(), signed);
                        break;
                    case "3":
                        nraw = RawHelper.grid2raw3(gridData.getVals(), gridData.getInvalidVal(), gridData.getStartx(), gridData.getStarty(), gridData.getDx(), gridData.getDy(), gridData.getNx(), gridData.getNy(), gridData.getTime(), signed);

                        break;
                    case "4":
                        nraw = RawHelper.grid2raw4(gridData.getVals(), gridData.getInvalidVal(), gridData.getStartx(), gridData.getStarty(), gridData.getDx(), gridData.getDy(), gridData.getNx(), gridData.getNy(), gridData.getTime(), signed);

                        break;
                    case "5":
                        nraw = RawHelper.grid2raw5(gridData.getVals(), gridData.getInvalidVal(), gridData.getStartx(), gridData.getStarty(), gridData.getDx(), gridData.getDy(), gridData.getNx(), gridData.getNy(), gridData.getTime(), signed);

                        break;
                }
            } else {
                float[][] datas=gridData.getVals();
                boolean convertWind=Boolean.valueOf(params.getString("convertWind","false"));//是否转换风速风向
                if(convertWind) {//
                    int nx = gridData.getNx();
                    int ny = gridData.getNy();
                    for (int i = 0; i < nx / 2; i++) {
                        for (int j = 0; j < ny; j++) {
                            float[] wind = uv2wind(datas[i][j], datas[(i + 1) * 2][j]);
                            datas[i][j] = wind[0];
                            datas[(i + 1) * 2][j] = wind[1];
                        }
                    }
                }
                nraw = RawHelper.grid2raw6(datas, gridData.getInvalidVal(), gridData.getStartx(), gridData.getStarty(), gridData.getDx(), gridData.getDy(), gridData.getNx(), gridData.getNy(), gridData.getTime(),signed);
            }
//            GridData[] gridData2 = RawHelper.raw2grid(nraw);
            if (null != nraw) {
                httpServletResponse.getOutputStream().write(nraw);
            }
        } catch (IOException e) {
            log.error("Problem writing CoverageJSON to output stream", e);
        }
    }

    public static float[] uv2wind(float u, float v) {
        float[] arr = new float[2];
        BigDecimal wsDec = new BigDecimal(Math.hypot(u, v));
        wsDec = wsDec.setScale(1, RoundingMode.HALF_UP);
        float ws = wsDec.floatValue();
        BigDecimal wdDec;
        if (u == 0 && v == 0) {
            wdDec = new BigDecimal(Math.atan2(u, v) / Math.PI * 180);
        } else {
            wdDec = new BigDecimal(Math.atan2(u, v) / Math.PI * 180 + 180);
        }
        wdDec = wdDec.setScale(1, RoundingMode.HALF_UP);
        float wd = wdDec.floatValue();
        arr[0] = ws;
        arr[1] = wd % 360;
        return arr;
    }
    public static void getWmsMapGridData(RequestParams params, HttpServletResponse httpServletResponse,
                                     WmsCatalogue catalogue){
        GetMapParameters getMapParams = new GetMapParameters(params, catalogue);
        PlottingDomainParams plottingParameters = getMapParams.getPlottingDomainParameters();

        String[] layerNames = getMapParams.getStyleParameters().getLayerNames();
        LayerNameMapper layerNameMapper = catalogue.getLayerNameMapper();
        List<Feature<?>> features = new ArrayList<>();
        for (String layerName : layerNames) {
            if (!catalogue.isDownloadable(layerName)) {
                throw new InvalidFormatException(
                        "The format \"application/nraw\" is not enabled for this layer.\nIf you think this is an error, please contact the server administrator and get them to enable Download for this dataset");
            }
            Dataset dataset = catalogue
                    .getDatasetFromId(layerNameMapper.getDatasetIdFromLayerName(layerName));
            VariableMetadata metadata = dataset
                    .getVariableMetadata(layerNameMapper.getVariableIdFromLayerName(layerName));
            if (metadata.isScalar()) {
                Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                        .extractGeneralMapFeatures(dataset,
                                layerNameMapper.getVariableIdFromLayerName(layerName),
                                plottingParameters);

                features.addAll(mapFeatures);
            } else {
                Set<VariableMetadata> children = metadata.getChildren();
                for (VariableMetadata child : children) {
                    Collection<? extends DiscreteFeature<?, ?>> mapFeatures = GraphicsUtils
                            .extractGeneralMapFeatures(dataset,
                                    child.getParameter().getVariableId(), plottingParameters);
                    features.addAll(mapFeatures);
                }
            }
        }
        httpServletResponse.setContentType("application/json");
        GridData gridData = getGridData(params,catalogue);


        try{
            // 获取PrintWriter以写入响应体
            PrintWriter out = httpServletResponse.getWriter();
            out.print(gridData);
            out.flush();
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    public static void getTransect(RequestParams params, HttpServletResponse httpServletResponse,
                               WmsCatalogue catalogue) throws EdalException {
        //String outputFormat = params.getMandatoryString("format");
        //if (!"image/png".equals(outputFormat) && !"image/jpeg".equals(outputFormat)
        //        && !"image/jpg".equals(outputFormat)) {
        //    throw new InvalidFormatException(
        //            outputFormat + " is not a valid output format for a transect plot");
        //}
        String[] layers = params.getMandatoryString("layers").split(",");
        CoordinateReferenceSystem crs = GISUtils.getCrs(params.getMandatoryString("CRS"));
        LineString lineString = new LineString(params.getMandatoryString("linestring"), crs);
        String timeStr = params.getString("time");

        String elevationStr = params.getString("elevation");
        Double zValue = null;
        if (elevationStr != null) {
            zValue = Double.parseDouble(elevationStr);
        }
        StringBuilder copyright = new StringBuilder();
        Map<PointCollectionFeature, String> pointCollectionFeatures2Labels = new HashMap<>();
        /* Do we also want to plot a vertical section plot? */
        boolean verticalSection = false;
        List<HorizontalPosition> verticalSectionHorizontalPositions = new ArrayList<>();
        DiscreteLayeredDataset<?, ?> gridDataset = null;
        String varId = null;
        Set<String> copyrights = new LinkedHashSet<>();
        PlottingStyleParameters defaults = null;
        for (String layerName : layers) {
            Dataset dataset = WmsUtils.getDatasetFromLayerName(layerName, catalogue);
            if (dataset == null) {
                throw new EdalLayerNotFoundException(
                        "The layer " + layerName + " was not found on this server");
            }
            if (dataset instanceof DiscreteLayeredDataset<?, ?>) {
                gridDataset = (DiscreteLayeredDataset<?, ?>) dataset;
                varId = catalogue.getLayerNameMapper().getVariableIdFromLayerName(layerName);
                EnhancedVariableMetadata layerMetadata = WmsUtils.getLayerMetadata(layerName,
                        catalogue);
                String layerCopyright = layerMetadata.getCopyright();
                defaults = layerMetadata.getDefaultPlottingParameters();
                if (layerCopyright != null && !"".equals(layerCopyright)) {
                    copyrights.add(layerCopyright);
                }

                VariableMetadata metadata = gridDataset.getVariableMetadata(varId);
                VerticalDomain verticalDomain = metadata.getVerticalDomain();
                final VerticalPosition zPos;
                if (zValue != null && verticalDomain != null) {
                    zPos = new VerticalPosition(zValue, verticalDomain.getVerticalCrs());
                } else {
                    zPos = null;
                }
                if (verticalDomain != null && layers.length == 1) {
                    verticalSection = true;
                }

                final DateTime time;
                TemporalDomain temporalDomain = metadata.getTemporalDomain();
                if (timeStr != null) {
                    time = TimeUtils.iso8601ToDateTime(timeStr, temporalDomain.getChronology());
                } else {
                    time = null;
                }
                HorizontalDomain hDomain = metadata.getHorizontalDomain();
                final List<HorizontalPosition> transectPoints;
                if (hDomain instanceof HorizontalGrid) {
                    transectPoints = GISUtils.getOptimalTransectPoints((HorizontalGrid) hDomain,
                            lineString);
                } else {
                    transectPoints = lineString.getPointsOnPath(AXIS_RESOLUTION);
                }
                if (verticalSection) {
                    verticalSectionHorizontalPositions = transectPoints;
                }

                PointCollectionDomain pointCollectionDomain = new PointCollectionDomain(
                        transectPoints, zPos, time);

                PointCollectionFeature feature = gridDataset.extractPointCollection(
                        CollectionUtils.setOf(varId), pointCollectionDomain);
                pointCollectionFeatures2Labels.put(feature,
                        catalogue.getLayerMetadata(metadata).getTitle());
            } else {
                throw new EdalUnsupportedOperationException(
                        "Only gridded datasets are supported for transect plots");
            }
        }

        if (defaults == null) {
            defaults = new PlottingStyleParameters(new ArrayList<>(), "default", Color.black,
                    Color.black, new Color(0, true), false, ColourPalette.MAX_NUM_COLOURS, 1f);
        }

        for (String layerCopyright : copyrights) {
            copyright.append(layerCopyright);
            copyright.append('\n');
        }
        if (copyright.length() > 0) {
            copyright.deleteCharAt(copyright.length() - 1);
        }

        JFreeChart chart = Charting.createTransectPlot(pointCollectionFeatures2Labels, lineString,
                false, copyright.toString());// , catalogue.getLayerNameMapper());

        httpServletResponse.setContentType("application/json");
        //CoverageJsonConverter converter = new CoverageJsonConverterImpl();

        //converter.checkFeaturesSupported(features);
        //try {
        //    if (features.size() == 1) {
        //        converter.convertFeatureToJson(httpServletResponse.getOutputStream(),
        //                features.get(0));
        //    } else {
        //        /*
        //         * vectors are currently multiple features each with one
        //         * parameter TODO group features with identical domain into
        //         * single feature
        //         */
        //        converter.convertFeaturesToJson(httpServletResponse.getOutputStream(),
        //                features);
        //    }
        //} catch (IOException e) {
        //    log.error("Problem writing CoverageJSON to output stream", e);
        //}

        return;
        //
        //if (verticalSection) {
        //    /*
        //     * This can only be true if we have an AbstractGridDataset, so we
        //     * can use our previous cast
        //     */
        //    String paletteName = params.getString("palette", defaults.getPalette());
        //    int numColourBands = params.getPositiveInt("numcolorbands",
        //            defaults.getNumColorBands());
        //
        //    /*
        //     * define an extent for the vertical section if parameter present
        //     */
        //    String sectionElevationStr = params.getString("section-elevation");
        //    Extent<Double> zExtent = extractSectionElevation(sectionElevationStr);
        //
        //    List<ProfileFeature> profileFeatures = new ArrayList<>();
        //    TemporalDomain temporalDomain = gridDataset.getVariableMetadata(varId)
        //            .getTemporalDomain();
        //    DateTime time = null;
        //    if (timeStr != null) {
        //        time = TimeUtils.iso8601ToDateTime(timeStr, temporalDomain.getChronology());
        //    }
        //    for (HorizontalPosition pos : verticalSectionHorizontalPositions) {
        //        PlottingDomainParams plottingParams = new PlottingDomainParams(1, 1, null, null,
        //                null, pos, null, time);
        //        List<? extends ProfileFeature> features = gridDataset.extractProfileFeatures(
        //                CollectionUtils.setOf(varId), plottingParams.getBbox(),
        //                plottingParams.getZExtent(), plottingParams.getTExtent(),
        //                plottingParams.getTargetHorizontalPosition(), plottingParams.getTargetT());
        //        profileFeatures.addAll(features);
        //    }
        //
        //    Extent<Float> scaleRange;
        //    if (zExtent != null) {
        //        scaleRange = getExtentOfFeatures(profileFeatures);
        //    } else {
        //        List<Extent<Float>> scaleRanges = GetMapStyleParams.getColorScaleRanges(params,
        //                defaults.getColorScaleRange());
        //        if (scaleRanges == null || scaleRanges.isEmpty()) {
        //            scaleRange = GraphicsUtils.estimateValueRange(gridDataset, varId);
        //        } else {
        //            scaleRange = scaleRanges.get(0);
        //            if (scaleRange == null || scaleRange.isEmpty()) {
        //                scaleRange = GraphicsUtils.estimateValueRange(gridDataset, varId);
        //            }
        //        }
        //    }
        //    ScaleRange colourScale = new ScaleRange(scaleRange.getLow(), scaleRange.getHigh(),
        //            params.getBoolean("logscale", defaults.isLogScaling()));
        //    ColourScheme colourScheme = new SegmentColourScheme(colourScale,
        //            GraphicsUtils.parseColour(params.getString("belowmincolor",
        //                    GraphicsUtils.colourToString(defaults.getBelowMinColour()))),
        //            GraphicsUtils.parseColour(params.getString("abovemaxcolor",
        //                    GraphicsUtils.colourToString(defaults.getAboveMaxColour()))),
        //            GraphicsUtils.parseColour(params.getString("bgcolor",
        //                    GraphicsUtils.colourToString(defaults.getNoDataColour()))),
        //            paletteName, numColourBands);
        //
        //    JFreeChart verticalSectionChart = Charting.createVerticalSectionChart(profileFeatures,
        //            lineString, colourScheme, zValue, zExtent);
        //    chart = Charting.addVerticalSectionChart(chart, verticalSectionChart);
        //}
        //int width = params.getPositiveInt("width", 700);
        //int height = params.getPositiveInt("height", verticalSection ? 1000 : 600);
        //
        //httpServletResponse.setContentType(outputFormat);
        //try {
        //    if ("image/png".equals(outputFormat)) {
        //        ChartUtilities.writeChartAsPNG(httpServletResponse.getOutputStream(), chart, width,
        //                height);
        //    } else {
        //        /* Must be a JPEG */
        //        ChartUtilities.writeChartAsJPEG(httpServletResponse.getOutputStream(), chart, width,
        //                height);
        //    }
        //} catch (IOException e) {
        //    log.error("Cannot write to output stream", e);
        //    throw new EdalException("Problem writing data to output stream", e);
        //}
    }
}
