#!/usr/bin/env bash
# Stop the whole Ember stack (proxy first so nobody lands on a half-stopped backend).
set -uo pipefail
M=/workspace/minecraft
"$M/proxy-runtime/stop.sh"
"$M/server-runtime/stop.sh"
"$M/login-runtime/stop.sh"
