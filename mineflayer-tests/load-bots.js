/**
 * Concurrent bot load harness for Spark CPU profiling.
 * Usage (after /spark profiler start --timeout 120 as OP):
 *   node load-bots.js [count=4]
 * Bots: join → /ember → skill info → wander; non-OP names LoadBot0..
 */
const mineflayer = require('mineflayer')
const COUNT = Math.max(1, Math.min(20, parseInt(process.argv[2] || '4', 10)))
const HOST = '127.0.0.1'
const PORT = 25565
const VERSION = '1.12.2'
const wait = ms => new Promise(r => setTimeout(r, ms))

async function runBot(i) {
  const name = 'LoadBot' + i
  const bot = mineflayer.createBot({ host: HOST, port: PORT, username: name, version: VERSION, auth: 'offline' })
  await new Promise((resolve, reject) => {
    const t = setTimeout(() => reject(new Error('spawn timeout ' + name)), 45000)
    bot.once('spawn', () => { clearTimeout(t); resolve() })
    bot.once('error', e => { clearTimeout(t); reject(e) })
  })
  console.log('[' + name + '] joined')
  bot.chat('/ember')
  await wait(800 + i * 100)
  bot.chat('/corerpg skill info')
  await wait(1000)
  // light wander loop ~60s
  const end = Date.now() + 60000
  while (Date.now() < end) {
    try {
      const yaw = Math.random() * Math.PI * 2
      await bot.look(yaw, 0, false)
      bot.setControlState('forward', true)
      await wait(400)
      bot.setControlState('forward', false)
      await wait(600)
    } catch (_) { break }
  }
  try { bot.quit() } catch (_) {}
  console.log('[' + name + '] done')
}

;(async () => {
  console.log('load-bots count=' + COUNT)
  await Promise.all(Array.from({ length: COUNT }, (_, i) => runBot(i).catch(e => console.error(e))))
  console.log('LOAD_BOTS_DONE')
  process.exit(0)
})()
