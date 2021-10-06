In order to build and run mekon-jena plugin:

1) Copy contents of "lib" folder from Apache Jena 4 installation
into empty placeholder folder provided within the general library
folder, "lib/jena"

2) Remove comment constructs from around the relevant line in
the "build-plugin-properties.xml" ANT build config file, i.e.

    <!-- mekon-jena build activation property -->
    <!--
    <property name="mekon-jena.active" value="true"/>
    -->

NOTE: The required dependencies for the mekon-jena plug-in are
incompatible with those for the mekon-stardog plug-in, so these
two plug-ins cannot be built and used together in a single
installation