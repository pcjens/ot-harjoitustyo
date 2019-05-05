#!/bin/sh
set -e
mvn test
mvn test jacoco:report
mvn jxr:jxr checkstyle:checkstyle
mvn package
cp target/Roguesque-1.0.jar .
zip Roguesque-1.0.zip Roguesque-1.0.jar items.csv
rm Roguesque-1.0.jar
