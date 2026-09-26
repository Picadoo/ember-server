const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', version: '1.12.2'
})
bot.on('message', (msg) => console.log('[chat]', msg.toString()))
bot.once('spawn', async () => {
  // Absolute coords summon variants
  const x = Math.floor(bot.entity.position.x)
  const y = Math.floor(bot.entity.position.y)
  const z = Math.floor(bot.entity.position.z)
  const tries = [
    `/summon zombie ${x} ${y+1} ${z}`,
    `/summon Zombie ${x} ${y+1} ${z}`,
    `/summon minecraft:zombie ${x} ${y+1} ${z}`,
    `/summon EntityZombie ${x} ${y+1} ${z}`,
  ]
  for (const c of tries) {
    console.log('[try]', c)
    bot.chat(c)
    await sleep(700)
  }
  // Zombie spawn egg: id 383 damage 54 in 1.12
  bot.chat(`/give Tester spawn_egg 1 54`)
  await sleep(500)
  bot.chat('/cwr check')
  await sleep(500)
  // Place egg by setblock + dispense? simpler: use World via /execute not in 1.12
  // Use plugin-free: EntityAreaEffectCloud? Skip — egg in inventory proves allow path unused.
  // Force CUSTOM via /minecraft:summon with data
  bot.chat(`/summon Villager ${x} ${y+2} ${z}`)
  await sleep(700)
  bot.chat(`/summon villager ${x} ${y+2} ${z}`)
  await sleep(700)
  bot.quit()
})
bot.on('error', (e) => { console.error(e); process.exit(1) })
bot.on('end', () => process.exit(0))
setTimeout(() => { console.error('timeout'); process.exit(2) }, 20000)
function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }
