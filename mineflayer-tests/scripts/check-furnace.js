const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: process.env.MC_HOST || '127.0.0.1',
  port: Number(process.env.MC_PORT || 25565),
  username: process.env.MC_USER || 'Tester',
  auth: 'offline',
  version: '1.12.2'
})
const timer = setTimeout(() => { console.error('timeout'); process.exit(1) }, 30000)
bot.once('spawn', () => {
  console.log('[check] spawned')
  bot.chat('/coresmelt check')
  setTimeout(() => {
    clearTimeout(timer)
    bot.quit('ok')
    process.exit(0)
  }, 2000)
})
bot.on('message', (m) => console.log('[chat]', m.toString()))
bot.on('error', (e) => { console.error(e); process.exit(1) })
