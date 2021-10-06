In order to build and run mekon-stardog plugin:

1) Copy relevant contents of Stardog 7 installation into empty
placeholder structure provided within the general library
folder, under "lib/stardog"

2) Remove comment constructs from around the relevant line in
the "build-plugin-properties.xml" ANT build config file, i.e.

    <!-- mekon-stardog build activation property -->
    <!--
    <property name="mekon-stardog.active" value="true"/>
    -->

NOTE: The required dependencies for the mekon-stardog plug-in are
incompatible with those for the mekon-jena plug-in, so these two
plug-ins cannot be built and used together in a single installation