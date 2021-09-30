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
