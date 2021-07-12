rmdir /S /Q javadoc\mekon-hobo
call ant core
call ant plugins
javadoc -classpath build\lib\* -d javadoc\mekon-hobo -overview javadoc\mekon-hobo-overview.html -sourcepath mekon\src;mekon-owl\src;hobo\src -subpackages uk -exclude org.semanticweb.owlapi.* 2> MKJAVADOC-OUTPUT
