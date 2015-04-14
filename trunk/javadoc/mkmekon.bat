rmdir /S /Q mekon
javadoc -classpath ..\lib\owlapi-bin.jar;..\lib\owlapitools-bin.jar;..\lib\HermiT.jar -d mekon -overview mekon-overview.html -sourcepath ..\mekon\src;..\mekon-owl\src -subpackages uk -exclude org.semanticweb.owlapi.* 2> MK-OUTPUT
