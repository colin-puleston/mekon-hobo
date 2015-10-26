rmdir /S /Q mekon-hobo
javadoc -classpath ..\lib\owlapi-bin.jar;..\lib\owlapitools-bin.jar;..\lib\HermiT.jar -d mekon-hobo -overview mekon-hobo-overview.html -sourcepath ..\mekon\src;..\mekon-owl\src;..\hobo\src -subpackages uk -exclude org.semanticweb.owlapi.* 2> MK-OUTPUT
