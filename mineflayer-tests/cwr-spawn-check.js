const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'Tester',
  version: '1.12.2'
})

const lines = []
bot.on('message', (msg) => {
  const t = msg.toString()
  lines.push(t)
  console.log('[chat]', t)
})

bot.once('spawn', async () => {
  console.log('[spawn-check] joined at', bot.entity.position)
  bot.chat('/coreworldrules check')
  await sleep(1000)
  // CUSTOM spawn (plugin/command) — should succeed
  bot.chat('/minecraft:summon zombie ~ ~ ~')
  await sleep(800)
  bot.chat('/minecraft:summon cow ~1 ~ ~')
  await sleep(800)
  // Count nearby living non-players roughly via entities
  let living = 0
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (e && e.type === 'mob') living++
  }
  console.log('[spawn-check] nearby mob entities after CUSTOM summon:', living)
  bot.chat('/cwr check')
  await sleep(1000)
  console.log('[spawn-check] DONE')
  bot.quit()
})

bot.on('error', (e) => { console.error(e); process.exit(1) })
bot.on('end', () => process.exit(0))
setTimeout(() => { console.error('timeout'); process.exit(2) }, 25000)

function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }
