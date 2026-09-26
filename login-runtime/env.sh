#!/usr/bin/env bash
export JAVA_HOME="${JAVA_HOME:-/workspace/minecraft/tools/jdk8u504-b01}"
export PATH="$JAVA_HOME/bin:$PATH"
RUNTIME_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export RUNTIME_DIR
