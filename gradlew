#!/usr/bin/env sh
# Gradle wrapper script minimal

# Répertoire du projet (là où se trouve ce script)
DIR="$(CDPATH= cd -- "$(dirname "$0")" && pwd)"

# Utilise JAVA_HOME si défini, sinon "java" du PATH
if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi

CLASSPATH="$DIR/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVA_EXE" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
