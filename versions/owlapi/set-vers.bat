set root=..\..

set ver-lib=v%1
set use-lib=%root%\lib
set mekon-owl-util=%root%\mekon-owl\src\uk\ac\manchester\cs\mekon\owl\util

copy %ver-lib%\OWLAPIVersion.java %mekon-owl-util%

call set-lib %ver-lib% %use-lib% owlapi
call set-lib %ver-lib% %use-lib% fact
call set-lib %ver-lib% %use-lib% jfact
call set-lib %ver-lib% %use-lib% hermit
call set-lib %ver-lib% %use-lib% elk
