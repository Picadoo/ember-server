#!/usr/bin/env bash
# Shared console-FIFO helpers, sourced by {server,login,proxy}-runtime/{start,stop}.sh.
# Each server's stdin is <runtime>/console.fifo; a holder process (`sleep infinity`, pid in
# console-holder.pid) keeps a writer open so the server never reads EOF. Without this,
# `nohup … &` in a non-interactive shell gives the server /dev/null as stdin: no console
# commands possible, and Waterfall busy-loops at 100% CPU on the EOF.
# Send commands with scripts/console.sh <play|login|proxy> "cmd". All paths are relative to the
# runtime dir (callers cd there first).

ember_fifo_holder_alive() {
  local pid
  [ -f console-holder.pid ] || return 1
  pid=$(cat console-holder.pid)
  [ -n "$pid" ] && [ "$(ps -o comm= -p "$pid" 2>/dev/null)" = "sleep" ]
}

# Create console.fifo (if needed) and start the holder (if not running).
ember_fifo_prepare() {
  if [ ! -p console.fifo ]; then rm -f console.fifo; mkfifo -m 600 console.fifo; fi
  ember_fifo_holder_alive && return 0
  # The open() below blocks until the server opens the read end; that is fine, it is backgrounded.
  nohup sleep infinity > console.fifo 2>/dev/null &
  echo $! > console-holder.pid
}

# Write one console line; gives up after 3s when no server is reading the FIFO.
ember_fifo_send() {
  [ -p console.fifo ] || { echo "no console.fifo in $(pwd)" >&2; return 1; }
  timeout 3 bash -c 'printf "%s\n" "$1" > console.fifo' _ "$1"
}

# Stop the holder (by its recorded PID only) after the server is down.
ember_fifo_release() {
  if ember_fifo_holder_alive; then kill "$(cat console-holder.pid)" 2>/dev/null || true; fi
  rm -f console-holder.pid
}

# Graceful stop: console command → wait → SIGTERM → wait → SIGKILL. Args: pid stopcmd waitSec
ember_graceful_stop() {
  local pid=$1 cmd=$2 secs=${3:-60} i
  if ember_fifo_send "$cmd" 2>/dev/null; then
    echo "Sent '$cmd' to console, waiting for pid $pid…"
    for i in $(seq 1 "$secs"); do kill -0 "$pid" 2>/dev/null || return 0; sleep 1; done
  fi
  echo "Console stop did not finish; SIGTERM $pid"
  kill "$pid" 2>/dev/null || true
  for i in $(seq 1 30); do kill -0 "$pid" 2>/dev/null || return 0; sleep 1; done
  echo "Force kill $pid"
  kill -9 "$pid" 2>/dev/null || true
}
