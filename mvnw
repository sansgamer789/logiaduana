#!/bin/sh
MAVEN_OPTS=""
exec java $MAVEN_OPTS -classpath ".mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain "$@"
