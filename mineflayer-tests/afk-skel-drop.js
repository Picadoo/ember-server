const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'OpReload', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function near(r=10) {
  const p = bot.entity.position
  return Object.values(bot.entities).filter(e => e && e !== bot.entity && e.type==='mob' && e.position && Math.hypot(e.position.x-p.x,e.position.z-p.z)<r)
}
function inv() {
  return bot.inventory.items().map(i => ({
    name: i.name, count: i.count, customName: i.customName||null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value||null
  }))
}
bot.once('spawn', async () => {
  try {
    await sleep(1000)
    bot.chat('/mvtp OpReload ember_afk'); await sleep(1800)
    bot.chat('/tp -48 70 242'); await sleep(700)
    bot.chat('/minecraft:kill @e[type=!Player,r=25]'); await sleep(900)
    bot.chat('/clear'); await sleep(400)
    bot.chat('/give OpReload diamond_sword 1'); await sleep(400)
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) await bot.equip(sword, 'hand')
    bot.chat('/gamemode 0'); await sleep(300)
    bot.chat('/effect OpReload resistance 120 10 true'); await sleep(200)
    bot.chat('/effect OpReload strength 120 8 true'); await sleep(200)
    bot.chat('/mm m spawn EmberAfkSkeleton 1'); await sleep(1200)
    for (let i=0;i<80;i++) {
      const ms = near(12).filter(e=>/skeleton/i.test(e.name||''))
      if (!ms.length) { console.log('S_DEAD', i); break }
      try { await bot.lookAt(ms[0].position.offset(0,1,0), true) } catch(_){}
      bot.attack(ms[0]); await sleep(150)
    }
    await sleep(2000)
    console.log('INV', JSON.stringify(inv()))
    console.log('CHAT', chat.filter(c=>/骨尘|碎片|成功|你得到了|挂机庭|MobSpawn/.test(c)).slice(-15))
    bot.chat('/mm s info EmberAfk_Z1'); await sleep(500)
    bot.chat('/mm s info EmberAfk_Z2'); await sleep(500)
    bot.chat('/mm s info EmberAfk_S1'); await sleep(500)
    bot.chat('/hd list'); await sleep(700)
    console.log('SPAWNERS', chat.filter(c=>/MobSpawn:|Stats for Spawner EmberAfk|ember_afk_hub/.test(c)).slice(-15))
  } catch(e) { console.error(e) }
  finally { bot.quit(); setTimeout(()=>process.exit(0),400) }
})
setTimeout(()=>{console.error('timeout');process.exit(2)},60000)
