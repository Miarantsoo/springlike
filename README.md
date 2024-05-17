## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

Utilisation du framework:
    ** Ce framework est basé sur Java 8 **

    -L'utilisateur devra executer le fichier batch "jar_transform.bat" pour compresser le framework en un fichier jar
    -Il mettra ensuite ce fichier .jar dans son repertoire "lib", dans son projet web
    -Il annotera ses futurs controllers par l'annotation "@Controller"  
    -Il créera un fichier "web.xml" dans "WEB-INF" dans laquelle il va declarer le servlet du FrontController et le package de controller, ceci se fera comme ci-dessous
        " <?xml version="1.0" encoding="UTF-8"?>
            <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
                    version="4.0">
                <display-name>Test WEB</display-name>

                <servlet>
                    <servlet-name>mainController</servlet-name>
                    <servlet-class>itu.etu2779.controller.FrontController</servlet-class>
                    <init-param>
                        <param-name>controllerChecker</param-name>
                        <param-value>controller</param-value>
                    </init-param>
                </servlet>
                <servlet-mapping>
                    <servlet-name>mainController</servlet-name>
                    <url-pattern>/</url-pattern>
                </servlet-mapping>
            </web-app> "