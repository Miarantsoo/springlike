@echo off

set "project=%~dp0"
set dev=C:\Users\Miarantsoa\ITU\S4\Web Dynamic\Framework\testWeb\lib
set lib=%project%lib\

javac -d bin -sourcepath src src/itu/etu2779/controller/*.java

jar -cvf springlike.jar -C "%project%\bin" .

copy "%project%\springlike.jar" "%dev%"

del "%project%\springlike.jar"