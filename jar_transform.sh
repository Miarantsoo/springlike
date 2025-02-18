#!/bin/bash

project=$(dirname "$0")
dev="/home/miarantsoa/ITU/S6/Framework/avion/lib"
lib="${project}/lib/"

classpath="${lib}paranamer-2.8.jar:${lib}servlet-api.jar:${lib}gson-2.8.8.jar"
echo "$classpath"

javac -d bin -sourcepath src -cp "$classpath" \
    src/itu/etu2779/controller/*.java \
    src/itu/etu2779/utils/*.java \
    src/itu/etu2779/annotation/*.java \
    src/itu/etu2779/mapping/*.java \
    src/itu/etu2779/servlet/*.java

jar -cvf springlike.jar -C "${project}/bin" .

cp "${project}/springlike.jar" "$dev"

rm "${project}/springlike.jar"