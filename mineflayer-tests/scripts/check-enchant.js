const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: process.env.MC_HOST || '127.0.0.1',
  port: Number(process.env.MC_PORT || 25565),
  username: process.env.MC_USER || 'Tester',
  auth: 'offline',
  version: '1.12.2'
})
const timer = setTimeout(() => { console.error('timeout'); process.exit(1) }, 30000)
const lines = []
bot.on('message', (m) => {
  const s = m.toString()
  lines.push(s)
  console.log('[chat]', s)
})
bot.once('spawn', () => {
  console.log('[check] spawned')
  bot.chat('/coreenchant check')
  setTimeout(() => {
    bot.chat('/ni give Tester crystal_ember_enchant 1')
  }, 800)
  setTimeout(() => {
    clearTimeout(timer)
    const joined = lines.join('\n')
    const ok = /catalyst_ni_id=crystal_ember_enchant/.test(joined)
      && /hasCatalystOverride=true/.test(joined)
      && /cost=1/.test(joined)
      && /cost=2/.test(joined)
      && /cost=3/.test(joined)
    console.log(ok ? '[check] PROOF OK' : '[check] PROOF INCOMPLETE')
    bot.quit('ok')
    process.exit(ok ? 0 : 2)
  }, 3500)
})
bot.on('error', (e) => { console.error(e); process.exit(1) })
