/**
 * Smoke test for the current topology: proxy :25565 → login server (AuthMe /register|/login)
 * → play server, via lib/proxy-login joinPlay(). Waits to land on play, chats hello, quits.
 */
const { joinPlay } = require('../lib/proxy-login')

const HOST = process.env.MC_HOST || '127.0.0.1'
const PORT = Number(process.env.MC_PORT || 25565)
const USERNAME = process.env.MC_USER || 'EmberTestOp'
const timeoutMs = Number(process.env.MC_TIMEOUT_MS || 45000)

;(async () => {
  const bot = await joinPlay(USERNAME, { host: HOST, port: PORT, timeout: timeoutMs, log: false })
  console.log(`[smoke] on play server as ${bot.username} at ${bot.entity.position} (${bot.game.dimension})`)
  bot.chat('hello from mineflayer smoke test')
  await new Promise((r) => setTimeout(r, 500))
  console.log('[smoke] quitting — OK')
  bot.quit('smoke ok')
  process.exit(0)
})().catch((e) => {
  console.error(`[smoke] FAIL connecting via ${HOST}:${PORT}:`, e.message || e)
  process.exit(1)
})
