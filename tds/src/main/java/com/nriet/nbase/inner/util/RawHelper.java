//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.nriet.nbase.inner.util;

import com.nriet.nbase.GridData;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

public class RawHelper {
    protected RawHelper() {
    }

    public static byte[] grid2raw2(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(1);
            int eleNum = vals.length / ny;
            dos.writeByte(eleNum);
            dos.writeByte(1);
            
            byte noVal;
            if (signed) {
                noVal = 127;
                dos.writeShort(2);
            } else {
                noVal = -1;
                dos.writeShort(1);
            }

            dos.writeByte(2);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            byte v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.write(noVal);
                        } else {
                            v = (byte)Math.round(vals[i][j]);
                            dos.write(v);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.write(noVal);
                        } else {
                            v = (byte)Math.round(vals[i][j]);
                            dos.write(v);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return buf;
    }

    public static byte[] grid2raw3(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(2);
            int eleNum = vals.length / ny;
            dos.writeByte(eleNum);
            dos.writeByte(1);
            
            short noVal;
            if (signed) {
                noVal = 32767;
                dos.writeShort(2);
            } else {
                noVal = -1;
                dos.writeShort(1);
            }

            dos.writeByte(3);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            short v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j]);
                            dos.writeShort(v);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j]);
                            dos.writeShort(v);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return buf;
    }

    public static byte[] grid2raw4(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(2);
            int eleNum = vals.length / ny;
            dos.writeByte(eleNum);
            dos.writeByte(1);
            
            short noVal;
            if (signed) {
                dos.writeShort(2);
                noVal = 32767;
            } else {
                dos.writeShort(1);
                noVal = -1;
            }

            dos.writeByte(4);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            short v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j] * 10.0F);
                            dos.writeShort(v);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j] * 10.0F);
                            dos.writeShort(v);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return buf;
    }

    public static byte[] grid2raw5(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(2);
            int eleNum = vals.length / ny;
            dos.writeByte(eleNum);
            dos.writeByte(1);
            
            short noVal;
            if (signed) {
                noVal = 32767;
                dos.writeShort(2);
            } else {
                noVal = -1;
                dos.writeShort(1);
            }

            dos.writeByte(5);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            short v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j] * 100.0F);
                            dos.writeShort(v);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.writeShort(noVal);
                        } else {
                            v = (short)Math.round(vals[i][j] * 100.0F);
                            dos.writeShort(v);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return buf;
    }

    public static byte[] grid2raw6(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(1);
            dos.writeByte(2);
            dos.writeByte(1);
            byte noVal_1;
            byte noVal_2;
            byte noVal_3;
            if (signed) {
                noVal_1 = 127;
                noVal_2 = -9;
                noVal_3 = -1;
                dos.writeShort(2);
            } else {
                noVal_1 = -1;
                noVal_2 = -1;
                noVal_3 = -1;
                dos.writeShort(1);
            }

            dos.writeByte(6);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            byte[] tmp;
            int ws;
            int wd;
            int v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] != invalidVal && vals[i + ny][j] != invalidVal) {
                            tmp = new byte[3];
                            ws = (int)(vals[i][j] * 10.0F);
                            wd = (int)(vals[i + ny][j] * 10.0F);
                            ws <<= 12;
                            v = ws | wd;
                            tmp[0] = (byte)(v >> 16 & 255);
                            tmp[1] = (byte)(v >> 8 & 255);
                            tmp[2] = (byte)(v & 255);
                            dos.write(tmp);
                        } else {
                            dos.write(noVal_1);
                            dos.write(noVal_2);
                            dos.write(noVal_3);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] != invalidVal && vals[i + ny][j] != invalidVal) {
                            tmp = new byte[3];
                            ws = (int)(vals[i][j] * 10.0F);
                            wd = (int)(vals[i + ny][j] * 10.0F);
                            ws <<= 12;
                            v = ws | wd;
                            tmp[0] = (byte)(v >> 16 & 255);
                            tmp[1] = (byte)(v >> 8 & 255);
                            tmp[2] = (byte)(v & 255);
                            dos.write(tmp);
                        } else {
                            dos.write(noVal_1);
                            dos.write(noVal_2);
                            dos.write(noVal_3);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var23) {
            var23.printStackTrace();
        }

        return buf;
    }

    public static byte[] combine(byte[][] srcRaws) {
        byte[] buf = null;

        try {
            int len = 43 + 8 * srcRaws.length + (srcRaws[0].length - 51) * srcRaws.length;
            buf = new byte[len];
            ByteBuffer bb = ByteBuffer.wrap(buf);
            bb.put(srcRaws[0], 0, 43);
            bb.putShort(15, (short)srcRaws.length);

            int i;
            for(i = 0; i < srcRaws.length; ++i) {
                bb.put(srcRaws[i], 43, 8);
            }

            for(i = srcRaws.length - 1; i >= 0; --i) {
                bb.put(srcRaws[i], 51, srcRaws[0].length - 51);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return buf;
    }

    public static GridData[] raw2grid(byte[] buf) {
        ByteBuffer bb = ByteBuffer.wrap(buf);
        int byteLen = bb.get();
        int eleNum = bb.get();
        int unitNum = bb.get();
        int signFlag = bb.getShort();
        boolean signed = false;
        if (2 == signFlag) {
            signed = true;
        } else {
            signed = false;
        }

        int algoFlag = bb.get();
        GridData[] gridDatas = null;
        int noVal=99999;
        if (2 == algoFlag) {
            gridDatas = _raw2grid2(bb, noVal, signed);
        } else if (3 == algoFlag) {
            gridDatas = _raw2grid3(bb, noVal, signed);
        } else if (4 == algoFlag) {
            gridDatas = _raw2grid4(bb, noVal, signed);
        } else if (5 == algoFlag) {
            gridDatas = _raw2grid5(bb, noVal, signed);
        } else if (6 == algoFlag) {
            gridDatas = _raw2grid6(bb, noVal, signed);
        }

        return gridDatas;
    }

    protected static GridData[] _raw2grid2(ByteBuffer bb, int noVal, boolean signed) {
        int proj = bb.get();
        int ny = bb.getInt();
        int nx = bb.getInt();
        int gridNy = bb.getShort();
        int gridNx = bb.getShort();
        float starty = bb.getFloat();
        float dy = bb.getFloat();
        float startx = bb.getFloat();
        float dx = bb.getFloat();
        bb.getFloat();
        bb.getFloat();
        Date[] times = new Date[gridNy];

        for(int i = 0; i < gridNy; ++i) {
            long mills = bb.getLong();
            times[i] = new Date(mills);
        }

        GridData[] gridDatas = new GridData[gridNy];
        float[][] vals = new float[ny][nx];

        for(int i = 0; i < gridNy; ++i) {
            GridData gridData = new GridData();

            for(int m = 0; m < ny; ++m) {
                for(int n = 0; n < nx; ++n) {
                    byte b = bb.get();
                    int v = b;
                    if (!signed) {
                        v = b & 255;
                    }

                    float val = (float)v;
                    vals[m][n] = val;
                }
            }

            gridData.setVals(vals);
            gridData.setInvalidVal((float)noVal);
            gridData.setStartx(startx);
            gridData.setEndx(startx + (float)(nx - 1) * dx);
            gridData.setStarty(starty);
            gridData.setEndy(starty + (float)(ny - 1) * dy);
            gridData.setNx(nx);
            gridData.setNy(ny);
            gridData.setDx(dx);
            gridData.setDy(dy);
            gridData.setTime(times[i]);
            gridDatas[i] = gridData;
        }

        return gridDatas;
    }

    protected static GridData[] _raw2grid3(ByteBuffer bb, int noVal, boolean signed) {
        int proj = bb.get();
        int ny = bb.getInt();
        int nx = bb.getInt();
        int gridNy = bb.getShort();
        int gridNx = bb.getShort();
        float starty = bb.getFloat();
        float dy = bb.getFloat();
        float startx = bb.getFloat();
        float dx = bb.getFloat();
        bb.getFloat();
        bb.getFloat();
        Date[] times = new Date[gridNy];

        for(int i = 0; i < gridNy; ++i) {
            long mills = bb.getLong();
            times[i] = new Date(mills);
        }

        GridData[] gridDatas = new GridData[gridNy];
        float[][] vals = new float[ny][nx];

        for(int i = 0; i < gridNy; ++i) {
            GridData gridData = new GridData();

            for(int m = 0; m < ny; ++m) {
                for(int n = 0; n < nx; ++n) {
                    short b = bb.getShort();
                    int v = b;
                    if (!signed) {
                        v = b & '\uffff';
                    }

                    float val = (float)v;
                    vals[m][n] = val;
                }
            }

            gridData.setVals(vals);
            gridData.setInvalidVal((float)noVal);
            gridData.setStartx(startx);
            gridData.setEndx(startx + (float)(nx - 1) * dx);
            gridData.setStarty(starty);
            gridData.setEndy(starty + (float)(ny - 1) * dy);
            gridData.setNx(nx);
            gridData.setNy(ny);
            gridData.setDx(dx);
            gridData.setDy(dy);
            gridData.setTime(times[i]);
            gridDatas[i] = gridData;
        }

        return gridDatas;
    }

    protected static GridData[] _raw2grid4(ByteBuffer bb, int noVal, boolean signed) {
        int proj = bb.get();
        int ny = bb.getInt();
        int nx = bb.getInt();
        int gridNy = bb.getShort();
        int gridNx = bb.getShort();
        float starty = bb.getFloat();
        float dy = bb.getFloat();
        float startx = bb.getFloat();
        float dx = bb.getFloat();
        bb.getFloat();
        bb.getFloat();
        Date[] times = new Date[gridNy];

        for(int i = 0; i < gridNy; ++i) {
            long mills = bb.getLong();
            times[i] = new Date(mills);
        }

        GridData[] gridDatas = new GridData[gridNy];
        float[][] vals = new float[ny][nx];

        for(int i = 0; i < gridNy; ++i) {
            GridData gridData = new GridData();

            for(int m = 0; m < ny; ++m) {
                for(int n = 0; n < nx; ++n) {
                    short b = bb.getShort();
                    int v = b;
                    if (!signed) {
                        v = b & '\uffff';
                    }

                    float val = (float)v;
                    if (v != noVal) {
                        val = (float)v / 10.0F;
                    }

                    vals[m][n] = val;
                }
            }

            gridData.setVals(vals);
            gridData.setInvalidVal((float)noVal);
            gridData.setStartx(startx);
            gridData.setEndx(startx + (float)(nx - 1) * dx);
            gridData.setStarty(starty);
            gridData.setEndy(starty + (float)(ny - 1) * dy);
            gridData.setNx(nx);
            gridData.setNy(ny);
            gridData.setDx(dx);
            gridData.setDy(dy);
            gridData.setTime(times[i]);
            gridDatas[i] = gridData;
        }

        return gridDatas;
    }

    protected static GridData[] _raw2grid5(ByteBuffer bb, int noVal, boolean signed) {
        int proj = bb.get();
        int ny = bb.getInt();
        int nx = bb.getInt();
        int gridNy = bb.getShort();
        int gridNx = bb.getShort();
        float starty = bb.getFloat();
        float dy = bb.getFloat();
        float startx = bb.getFloat();
        float dx = bb.getFloat();
        bb.getFloat();
        bb.getFloat();
        Date[] times = new Date[gridNy];

        for(int i = 0; i < gridNy; ++i) {
            long mills = bb.getLong();
            times[i] = new Date(mills);
        }

        GridData[] gridDatas = new GridData[gridNy];
        float[][] vals = new float[ny][nx];

        for(int i = 0; i < gridNy; ++i) {
            GridData gridData = new GridData();

            for(int m = 0; m < ny; ++m) {
                for(int n = 0; n < nx; ++n) {
                    short b = bb.getShort();
                    int v = b;
                    if (!signed) {
                        v = b & '\uffff';
                    }

                    float val = (float)v;
                    if (v != noVal) {
                        val = (float)v / 100.0F;
                    }

                    vals[m][n] = val;
                }
            }

            gridData.setVals(vals);
            gridData.setInvalidVal((float)noVal);
            gridData.setStartx(startx);
            gridData.setEndx(startx + (float)(nx - 1) * dx);
            gridData.setStarty(starty);
            gridData.setEndy(starty + (float)(ny - 1) * dy);
            gridData.setNx(nx);
            gridData.setNy(ny);
            gridData.setDx(dx);
            gridData.setDy(dy);
            gridData.setTime(times[i]);
            gridDatas[i] = gridData;
        }

        return gridDatas;
    }

    protected static GridData[] _raw2grid6(ByteBuffer bb, int noVal, boolean signed) {
        int proj = bb.get();
        int ny = bb.getInt();
        int nx = bb.getInt();
        int gridNy = bb.getShort();
        int gridNx = bb.getShort();
        float starty = bb.getFloat();
        float dy = bb.getFloat();
        float startx = bb.getFloat();
        float dx = bb.getFloat();
        bb.getFloat();
        bb.getFloat();
        Date[] times = new Date[gridNy];

        for(int i = 0; i < gridNy; ++i) {
            long mills = bb.getLong();
            times[i] = new Date(mills);
        }

        long newPos = (long)(bb.position() * 8);
//        BitBuffer bitBuffer = new BitBuffer(bb.array());
//        bitBuffer.position(newPos);
        GridData[] gridDatas = new GridData[gridNy];
        float[][] vals = new float[ny * 2][nx];

        for(int i = 0; i < gridNy; ++i) {
            GridData gridData = new GridData();

            for(int m = 0; m < ny; ++m) {
                for(int n = 0; n < nx; ++n) {
                    int ws = bb.getInt(12);
                    int wd = bb.getInt(12);
                    float wsVal = (float)ws;
                    float wdVal = (float)wd;
                    if (ws != noVal) {
                        wsVal = (float)ws / 10.0F;
                    }

                    if (wd != noVal) {
                        wdVal = (float)wd / 10.0F;
                    }

                    vals[m][n] = wsVal;
                    vals[m + ny][n] = wdVal;
                }
            }

            gridData.setVals(vals);
            gridData.setInvalidVal((float)noVal);
            gridData.setStartx(startx);
            gridData.setEndx(startx + (float)(nx - 1) * dx);
            gridData.setStarty(starty);
            gridData.setEndy(starty + (float)(ny - 1) * dy);
            gridData.setNx(nx);
            gridData.setNy(ny);
            gridData.setDx(dx);
            gridData.setDy(dy);
            gridData.setTime(times[i]);
            gridDatas[i] = gridData;
        }

        return gridDatas;
    }

    public static byte[] _grid2raw2(float[][] vals, float invalidVal, float startx, float starty, float dx, float dy, int nx, int ny, Date time, boolean signed) {
        byte[] buf = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(1);
            int eleNum = vals.length / ny;
            dos.writeByte(eleNum);
            dos.writeByte(1);
//            
            byte noVal;
            if (signed) {
                noVal = -128;
                dos.writeShort(2);
            } else {
                noVal = -1;
                dos.writeShort(1);
            }

            dos.writeByte(2);
            dos.writeByte(1);
            dos.writeInt(ny);
            dos.writeInt(nx);
            dos.writeShort(1);
            dos.writeShort(1);
            float startLat = dy < 0.0F ? starty : starty + (float)(ny - 1) * dy;
            dos.writeFloat(startLat);
            dos.writeFloat(-Math.abs(dy));
            dos.writeFloat(startx);
            dos.writeFloat(dx);
            dos.writeFloat(0.0F);
            dos.writeFloat(0.0F);
            dos.writeLong(time.getTime());
            int i;
            int j;
            byte v;
            if (dy > 0.0F) {
                for(i = ny - 1; i >= 0; --i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.write(noVal);
                        } else {
                            v = (byte)Math.round(vals[i][j]);
                            dos.write(v);
                        }
                    }
                }
            } else {
                for(i = 0; i < ny; ++i) {
                    for(j = 0; j < nx; ++j) {
                        if (vals[i][j] == invalidVal) {
                            dos.write(noVal);
                        } else {
                            v = (byte)Math.round(vals[i][j]);
                            dos.write(v);
                        }
                    }
                }
            }

            dos.close();
            bos.close();
            buf = bos.toByteArray();
        } catch (Exception var19) {
            var19.printStackTrace();
        }

        return buf;
    }
}
