#!/bin/sh
set -e
mvn test
mvn test jacoco:report
mvn jxr:jxr checkstyle:checkstyle
mvn package
