---
title: TDS Web Map Service (WMS)
last_updated: 2024-12-20
sidebar: quickstart_sidebar
toc: false
permalink: adding_wms.html
---

The TDS WMS implementation uses the ncWMS software developed by Jon Blower ([Reading E-Science Center](http://www.met.reading.ac.uk/resc/home/){:target="_blank"} at the University of Reading).
It supports [OGC Web Map Service (WMS)](https://www.ogc.org/standards/wms){:target="_blank"} versions 1.3.0 and 1.1.1.

## Which Files Can Be Served Through The WMS server?

Data files must contain gridded data.
The [NetCDF-Java Common Data Model](https://docs.unidata.ucar.edu/netcdf-java/{{site.netcdf-java_docset_version}}/userguide/){:target="_blank"} must be able to identify the coordinate system used. Check this by opening in the `Grid Panel` of the [ToolsUI](https://docs.unidata.ucar.edu/netcdf-java/{{site.netcdf-java_docset_version}}/userguide/toolsui_ref.html){:target="_blank"} application.
There should be one or more variables shown as a `GeoGrid`.

## Enabling And Using WMS

The WMS service **is enabled by default** in the TDS. 

As long as the WMS service is enabled, datasets can be configured to have a WMS access method in the TDS catalog configuration files similar to how other services are configured.
The service element's serviceType and base attribute values must be as follows:

~~~
<service name="wms" serviceType="WMS" base="/thredds/wms/" />
~~~

The dataset to be served must reference this service (or a containing compound service) by the service name:

~~~
<dataset ID="sample" name="Sample Data" urlPath="sample.nc">
  <serviceName>wms</serviceName>
</dataset>
~~~

WMS clients may not be able to directly use the THREDDS catalogs to find the WMS services but the catalogs are useful for users to browse and for separate search services (e.g., [OGC catalog services](https://www.ogc.org/standards/cat){:target="_blank"}).

## WMS Configuration

Additional WMS configuration options can be set in the `threddsConfig.xml` file. 

Further WMS configuration properties are set in the wmsConfig.xml file.
These properties are mainly related with styling of WMS images.
Similar to the `threddsConfig.xml` file, the WMS configuration file (wmsConfig.xml) is found in the `$tds.content.root.path{}/content/thredds` directory.
A detailed description of the wmsConfig.xml file can be on the [Customizing WMS](../adminguide/customizing_wms.html) reference page.

If you are installing a new TDS, you should find a default `wmsConfig.xml` file (along with other configuration files) in your content`/thredds` directory after you first deploy the TDS.
If you are upgrading from a TDS version before version `4.2.20100615.*`, you will have to copy the default file from `${tomcat_home}/webapps/thredds/WEB-INF/altContent/startup/wmsConfig.xml`.

## Godiva3 configuration

Godiva3 has a few configuration options that may be useful, such as changing the map size or adding new base layers.
Similar to the `wmsConfig.xml` file, the Godiva3 configuration file (`godiva3.properties`) is found in the `${tds.content.root.path}/thredds` directory.
Configuration options can be found in the [ncWMS documentation](https://reading-escience-centre.gitbooks.io/ncwms-user-guide/content/03-config.html#server){:target="_blank"}.

## Change to CRS List in WMS GetCapabilities Documents

The number of CRS listed in the WMS GetCapabilities documents has been reduced between TDS 4.1 and 4.2. 

{%include note.html content="
For more information, view the FAQ entry [What happened to the long list of CRSs in my WMS GetCapabilities documents?](tds_faq.html#what-happened-to-the-long-list-of-crss-in-my-wms-getcapabilities-documents).
" %}

## Add a JVM Option to Avoid an X Server Bug

WMS uses a number of graphics packages.
In some situations, WMS can run into an X Server bug that can cause Tomcat to crash.
This can be avoided by telling the code there is no display device. 
You may see error messages like the following:

~~~java
java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11GraphicsEnvironment
~~~

To avoid this situation, the graphics code needs to be told that there is no graphics console available.
This can be done by setting the `java.awt.headless` system property to `true` which can be done using `JAVA_OPTS`:

~~~bash
JAVA_OPTS="-Xmx1024m -Xms256m -server -Djava.awt.headless=true"
export JAVA_OPT
~~~

What the option means:

`-Djava.awt.headless=true` sets the value of the `java.awt.headless` system property to `true`.
Setting this system property to true prevent graphics rendering code from assuming that a graphics console exists.
More on using the headless mode in Java SE here.

## Add a JVM Option to Avoid `java.util.prefs` Problem Storing System Preferences

Some libraries that WMS depends on use the `java.util.prefs` package and there are some known issues that can crop up with storing system preferences.
This problem can be avoided by setting the `java.util.prefs.systemRoot` system property to point to a directory in which the TDS can write.
The given directory must exist and must contain a directory named ".systemPrefs" which must be writable by the user under which Tomcat is run.

~~~bash
JAVA_OPTS="-Xmx1024m -Xms256m -server -Djava.util.prefs.systemRoot=$CATALINA_HOME/content/thredds/javaUtilPrefs"
export JAVA_OPT
~~~

What the option means:

`-Djava.util.prefs.systemRoot=<directory>` sets the value of the `java.util.prefs.systemRoot` system property to the given directory path.
The `java.util.prefs` code will use the given directory to persist the system (as opposed to user) preferences.
More information on the issue can be found on the TDS FAQ page.

## Serving Remote Datasets

The TDS can also serve remote datasets with the WMS protocol if configured to do so.
It must be explicitly configured in the threddsConfig.xml configuration file. This is done by adding an allowRemote element to the WMS element as follows:

~~~xml
<WMS>
  <allow>true</allow>
  <allowRemote>true</allowRemote>
  ...
</WMS>
~~~

A slight extension of the WMS Dataset URL format allows the TDS to serve remote datasets.
The dataset is identified by adding the parameter dataset whose value is a URL:

~~~
http://servername:8080/thredds/wms?dataset=datasetURL
~~~

The URL must be a dataset readable by the NetCDF-Java library, typically an OPeNDAP dataset on another server.
It must have gridded data with identifiable coordinate systems (see above).
For example, an OPeNDAP URL might be

~~~
http://las.pfeg.noaa.gov/cgi-bin/nph-dods/data/oceanwatch/nrt/gac/AG14day.nc
~~~

This can be served remotely as a WMS dataset with this URL:

~~~
http://servername:8080/thredds/wms?dataset=http://las.pfeg.noaa.gov/cgi-bin/nph-dods/data/oceanwatch/nrt/gac/AG14day.nc
~~~
