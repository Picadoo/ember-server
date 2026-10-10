#!/usr/bin/env bash
# b.sh join NAME | b.sh log NAME [from] | b.sh eval NAME 'js' | b.sh chat NAME 'msg' [waitms]
P=http://127.0.0.1:8765
case "$1" in
  join) curl -s "$P/join?name=$2" ;;
  quit) curl -s "$P/quit?name=$2" ;;
  log) curl -s "$P/log?name=$2&from=${3:-0}" ;;
  eval) curl -s -X POST --data-binary "$3" "$P/eval?name=$2" ;;
  chat) curl -s -X POST --data-binary "const n=bot.chatLog.length; bot.chat($(node -e 'console.log(JSON.stringify(process.argv[1]))' "$3")); await wait(${4:-1500}); return bot.chatLog.slice(n).map(s=>s.replace(/§./g,''))" "$P/eval?name=$2" ;;
esac
echo
