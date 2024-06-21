@echo off

set "project=%~dp0"
set dev=C:\Users\Miarantsoa\ITU\S4\Web Dynamic\Framework\testWeb\lib
set lib=%project%lib\

set classpath="%lib%paranamer-2.8.jar;%lib%servlet-api.jar"
echo %classpath%

javac -d bin -sourcepath src -cp %classpath% src/itu/etu2779/controller/*.java src/itu/etu2779/utils/*.java src/itu/etu2779/annotation/*.java src/itu/etu2779/mapping/*.java src/itu/etu2779/servlet/*.java

jar -cvf springlike.jar -C "%project%\bin" .

copy "%project%\springlike.jar" "%dev%"

del "%project%\springlike.jar"
