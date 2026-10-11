#!/usr/bin/env bash
# D12195/D12196: Paper restart-script. Return immediately; detach waiter from JVM group.
set -euo pipefail
cd "$(dirname "$0")"
setsid nohup ./_restart_waiter.sh >/dev/null 2>&1 &
disown || true
exit 0
