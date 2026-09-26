const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'OpReload', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function near(r=8) {
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
    bot.chat('/mm reload'); await sleep(3000)
    bot.chat('/mvtp OpReload ember_afk'); await sleep(1800)
    bot.chat('/tp -46 70 250'); await sleep(700)
    bot.chat('/minecraft:kill @e[type=!Player,r=40]'); await sleep(1000)
    bot.chat('/clear'); await sleep(400)
    bot.chat('/give OpReload diamond_sword 1'); await sleep(500)
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) await bot.equip(sword, 'hand')
    bot.chat('/gamemode 0'); await sleep(300)
    bot.chat('/effect OpReload resistance 120 10 true'); await sleep(200)
    bot.chat('/effect OpReload strength 120 8 true'); await sleep(200)

    // Control: Daily zombie (known-good config) in AFK world
    console.log('=== CONTROL Daily ===')
    bot.chat('/mm m spawn EmberDailyZombie 1'); await sleep(1200)
    for (let i=0;i<80;i++) {
      const ms = near(10).filter(e=>/zombie/i.test(e.name||''))
      if (!ms.length) { console.log('CTRL_DEAD', i); break }
      try { await bot.lookAt(ms[0].position.offset(0,1,0), true) } catch(_){}
      bot.attack(ms[0]); await sleep(150)
    }
    await sleep(2000)
    console.log('CTRL_INV', JSON.stringify(inv()))
    console.log('CTRL_CHAT', chat.filter(c=>/成功给予|你得到了|余烬碎片|NeigeItems >/.test(c)).slice(-5))

    bot.chat('/clear'); await sleep(400)
    bot.chat('/give OpReload diamond_sword 1'); await sleep(400)
    const sword2 = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword2) await bot.equip(sword2, 'hand')

    console.log('=== AFK zombie ===')
    bot.chat('/mm m spawn EmberAfkZombie 1'); await sleep(1200)
    for (let i=0;i<80;i++) {
      const ms = near(10).filter(e=>/zombie/i.test(e.name||''))
      if (!ms.length) { console.log('AFK_DEAD', i); break }
      try { await bot.lookAt(ms[0].position.offset(0,1,0), true) } catch(_){}
      bot.attack(ms[0]); await sleep(150)
    }
    await sleep(2000)
    console.log('AFK_INV', JSON.stringify(inv()))
    console.log('AFK_CHAT', chat.filter(c=>/成功给予|你得到了|余烬碎片|余烬骨尘|NeigeItems >/.test(c)).slice(-8))
    console.log('SPAWNER', chat.filter(c=>/挂机庭|灰烬庭院|Spawned EmberAfk|重载完毕/.test(c)).slice(-15))
  } catch(e) { console.error(e) }
  finally { bot.quit(); setTimeout(()=>process.exit(0),400) }
})
setTimeout(()=>{console.error('timeout');process.exit(2)},90000)
