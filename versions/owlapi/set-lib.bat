set ver-lib=%1
set use-lib=%2
set sub-lib=%3

del /Q %use-lib%\%sub-lib%\*.*
copy %ver-lib%\%sub-lib%\*.* %use-lib%\%sub-lib%
