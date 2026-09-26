/**
 * Offline-mode friendly smoke test for Paper 1.12.2 on localhost:25565.
 * Connects as "Tester", waits for spawn, chats hello, then quits.
 */
const mineflayer = require('mineflayer')

const HOST = process.env.MC_HOST || '127.0.0.1'
const PORT = Number(process.env.MC_PORT || 25565)
const USERNAME = process.env.MC_USER || 'Tester'

const bot = mineflayer.createBot({
  host: HOST,
  port: PORT,
  username: USERNAME,
  auth: 'offline',
  version: '1.12.2',
  hideErrors: false
})

const timeoutMs = Number(process.env.MC_TIMEOUT_MS || 30000)
const timer = setTimeout(() => {
  console.error(`[smoke] timed out after ${timeoutMs}ms connecting to ${HOST}:${PORT}`)
  try { bot.quit('timeout') } catch (_) {}
  process.exit(1)
}, timeoutMs)

bot.once('login', () => {
  console.log(`[smoke] logged in as ${bot.username}`)
})

bot.once('spawn', () => {
  console.log(`[smoke] spawned at ${bot.entity.position}`)
  bot.chat('hello from mineflayer smoke test')
  setTimeout(() => {
    clearTimeout(timer)
    console.log('[smoke] quitting — OK')
    bot.quit('smoke ok')
    process.exit(0)
  }, 500)
})

bot.on('kicked', (reason) => {
  clearTimeout(timer)
  console.error('[smoke] kicked:', reason)
  process.exit(1)
})

bot.on('error', (err) => {
  clearTimeout(timer)
  console.error('[smoke] error:', err.message || err)
  process.exit(1)
})

bot.on('end', (reason) => {
  console.log('[smoke] connection ended:', reason || '(none)')
})
