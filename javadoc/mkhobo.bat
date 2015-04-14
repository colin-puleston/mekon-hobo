rmdir /S /Q hobo
javadoc -classpath ..\lib\owlapi-bin.jar;..\lib\owlapitools-bin.jar;..\lib\HermiT.jar -d hobo -overview hobo-overview.html -sourcepath ..\hobo\src;..\mekon\src;..\mekon-owl\src -subpackages uk -exclude org.semanticweb.owlapi.* 2> MK-OUTPUT
