rmdir /S /Q mekon-hobo
javadoc -classpath ..\lib\owlapi\*.jar -d mekon-hobo -overview mekon-hobo-overview.html -sourcepath ..\mekon\src;..\mekon-owl\src;..\hobo\src -subpackages uk -exclude org.semanticweb.owlapi.* 2> MK-OUTPUT
