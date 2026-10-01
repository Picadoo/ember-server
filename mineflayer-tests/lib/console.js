// Play-server console bridge for tests (no op, no RCON): writes commands into the FIFO that the
// play server's stdin is attached to, and reads results from server-runtime/logs/latest.log.
// Start the play server with a console FIFO (see mineflayer-tests/README.md「控制台 FIFO」):
//   mkfifo -m 600 /tmp/ember-play-console.fifo
//   (setsid nohup tail -f /dev/null > /tmp/ember-play-console.fifo &)      # keeps a writer open
//   cd server-runtime && setsid bash -c 'source ./env.sh; exec "$JAVA_HOME/bin/java" -Xms512M -Xmx1536M \
//     -jar "$RUNTIME_DIR/paper-custom.jar" nogui < /tmp/ember-play-console.fifo > logs/stdout.log 2>&1' &
const fs = require('fs')
const FIFO = process.env.EMBER_CONSOLE_FIFO || '/tmp/ember-play-console.fifo'
const LOG = process.env.EMBER_PLAY_LOG || '/workspace/minecraft/server-runtime/logs/latest.log'
const wait = ms => new Promise(r => setTimeout(r, ms))

function available () {
  try { return fs.statSync(FIFO).isFIFO() } catch (e) { return false }
}
// Never blocks forever: O_NONBLOCK open fails (ENXIO) when no server is reading the FIFO.
function send (cmd) {
  const c = String(cmd).replace(/^\//, '').replace(/[\r\n]/g, ' ')
  const fd = fs.openSync(FIFO, fs.constants.O_WRONLY | fs.constants.O_NONBLOCK)
  try { fs.writeSync(fd, c + '\n') } finally { fs.closeSync(fd) }
  console.log('[console]', c)
}
function readLines () { try { return fs.readFileSync(LOG, 'utf8').split('\n') } catch (e) { return [] } }
function mark () { return readLines().length }
function since (m) { return readLines().slice(Math.max(0, m - 1)) }
async function waitLog (re, m, timeoutMs = 4000) {
  const end = Date.now() + timeoutMs
  while (Date.now() < end) {
    const hit = since(m).find(l => re.test(l))
    if (hit) return hit
    await wait(150)
  }
  return null
}
// send + wait for a log line matching re; returns the line or null
async function run (cmd, re, timeoutMs = 4000) {
  const m = mark(); send(cmd)
  if (!re) { await wait(300); return null }
  return waitLog(re, m, timeoutMs)
}
module.exports = { FIFO, LOG, available, send, mark, since, waitLog, run }
