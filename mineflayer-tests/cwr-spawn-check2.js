const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', version: '1.12.2'
})
bot.on('message', (msg) => console.log('[chat]', msg.toString()))
bot.once('spawn', async () => {
  console.log('[t2] pos', bot.entity.position)
  // Clear nearby vanilla leftovers so we can observe new natural attempts
  bot.chat('/minecraft:kill @e[type=!Player,r=64]')
  await sleep(1200)
  bot.chat('/cwr check')
  await sleep(600)
  // 1.12 summon names are capitalized entity names
  bot.chat('/summon Zombie ~ ~1 ~')
  await sleep(600)
  bot.chat('/summon Cow ~2 ~1 ~')
  await sleep(600)
  bot.chat('/summon Pig ~-2 ~1 ~')
  await sleep(800)
  let mobs = 0
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (e && e !== bot.entity && e.type === 'mob') {
      mobs++
      console.log('[t2] mob', e.name || e.mobType || e.entityType, e.position)
    }
  }
  console.log('[t2] mob count after CUSTOM summon:', mobs)
  // Wait ~8s for natural spawn attempts; with bans+limits should stay flat
  await sleep(8000)
  let mobs2 = 0
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (e && e !== bot.entity && e.type === 'mob') mobs2++
  }
  console.log('[t2] mob count after wait:', mobs2)
  bot.chat('/cwr check')
  await sleep(800)
  bot.quit()
})
bot.on('error', (e) => { console.error(e); process.exit(1) })
bot.on('end', () => process.exit(0))
setTimeout(() => { console.error('timeout'); process.exit(2) }, 35000)
function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }
