#!/usr/bin/env bash
export JAVA_HOME="${JAVA_HOME:-/workspace/minecraft/tools/jdk8u504-b01}"
export PATH="$JAVA_HOME/bin:$PATH"
RUNTIME_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export RUNTIME_DIR
STOCK_JAR="${STOCK_JAR:-/workspace/minecraft/paper/paper-1.12.2-1620.jar}"
CUSTOM_JAR="${CUSTOM_JAR:-$RUNTIME_DIR/paper-custom.jar}"
REBUILT_JAR=""
for f in \
  "$CUSTOM_JAR" \
  /workspace/minecraft/Paper/Paper-Server/target/paper-1.12.2.jar \
  /workspace/minecraft/Paper/paperclip.jar
do
  if [ -f "$f" ]; then REBUILT_JAR="$f"; break; fi
done
if [ -z "$REBUILT_JAR" ]; then
  shopt -s nullglob
  for f in /workspace/minecraft/Paper/work/Paperclip/target/*.jar /workspace/minecraft/Paper/*.jar; do
    case "$f" in *sources*|*original*) continue ;; esac
    if [ -f "$f" ]; then REBUILT_JAR="$f"; break; fi
  done
  shopt -u nullglob
fi
export STOCK_JAR CUSTOM_JAR REBUILT_JAR
