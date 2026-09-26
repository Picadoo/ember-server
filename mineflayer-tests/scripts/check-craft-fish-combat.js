/**
 * Mineflayer check: /corecraft /corefish /corecombat + sample NI gives
 */
const mineflayer = require('mineflayer')

const bot = mineflayer.createBot({
  host: process.env.MC_HOST || '127.0.0.1',
  port: Number(process.env.MC_PORT || 25565),
  username: process.env.MC_USER || 'Tester',
  auth: 'offline',
  version: '1.12.2'
})

const timer = setTimeout(() => { console.error('timeout'); process.exit(1) }, 45000)
const lines = []
bot.on('message', (m) => {
  const s = m.toString()
  lines.push(s)
  console.log('[chat]', s)
})

bot.once('spawn', () => {
  console.log('[check] spawned')
  bot.chat('/corecraft check')
  setTimeout(() => bot.chat('/corefish check'), 600)
  setTimeout(() => bot.chat('/corecombat check'), 1200)
  setTimeout(() => bot.chat('/ni give Tester totem_ember_life 1'), 1800)
  setTimeout(() => bot.chat('/ni give Tester shield_ember_guard 1'), 2200)
  setTimeout(() => bot.chat('/ni give Tester fish_ember_cod 1'), 2600)
  setTimeout(() => bot.chat('/ni give Tester plate_ember_iron 1'), 3000)
  setTimeout(() => {
    clearTimeout(timer)
    const joined = lines.join('\n')
    const ok = /NI_registered=3/.test(joined)
      && /CraftNmsHooks\.active=true/.test(joined)
      && /FishNmsHooks\.active=true/.test(joined)
      && /overrideLoot=true/.test(joined)
      && /TotemNmsHooks\.active=true/.test(joined)
      && /ShieldNmsHooks\.active=true/.test(joined)
      && /totem_ember_life OK/.test(joined)
      && /shield_ember_guard OK/.test(joined)
    console.log(ok ? '[check] PROOF OK' : '[check] PROOF INCOMPLETE')
    if (!ok) {
      console.log('--- joined chat ---')
      console.log(joined)
    }
    bot.quit('ok')
    process.exit(ok ? 0 : 2)
  }, 4500)
})

bot.on('error', (e) => { console.error(e); process.exit(1) })
