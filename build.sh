#!/bin/sh
set -e
mvn test
mvn test jacoco:report
mvn jxr:jxr checkstyle:checkstyle
mvn package
cp target/Roguesque-1.0-RC01.jar .
zip Roguesque-1.0-RC01.zip Roguesque-1.0-RC01.jar items.csv
rm Roguesque-1.0-RC01.jar
