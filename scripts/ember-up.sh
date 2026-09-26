#!/usr/bin/env bash
# Start the whole Ember stack: login + play backends (127.0.0.1 only), then the public proxy.
set -uo pipefail
M=/workspace/minecraft
"$M/login-runtime/start.sh"
"$M/server-runtime/start.sh"
"$M/proxy-runtime/start.sh"
