const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat=[]
bot.on('message', m => { const s=m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function mobs() {
  return Object.values(bot.entities).filter(e => e && e!==bot.entity && e.type==='mob'
    && e.position && Math.abs(e.position.x+40)<20 && Math.abs(e.position.z-270)<20)
}
function inv() {
  return bot.inventory.items().map(i => ({
    name:i.name, count:i.count,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}
bot.once('spawn', async () => {
  try {
    bot.chat('/gamemode 0')
    await sleep(400)
    bot.chat('/effect Tester resistance 60 10 true')
    await sleep(300)
    bot.chat('/effect Tester regeneration 60 10 true')
    await sleep(300)
    bot.chat('/clear')
    await sleep(400)
    bot.chat('/tp -40 65 270')
    await sleep(800)
    bot.chat('/minecraft:kill @e[type=!Player,r=24]')
    await sleep(1000)
    bot.chat('/give Tester diamond_sword 1')
    await sleep(500)
    bot.chat('/mm m spawn EmberCryptZombie 1 world,-40,65,270')
    await sleep(1200)
    console.log('mobs', JSON.stringify(mobs().map(e=>({name:e.name,hp:e.metadata,pos:e.position}))))
    // Attack until dead
    for (let i=0;i<80;i++) {
      const ms = mobs()
      if (!ms.length) { console.log('all dead at round', i); break }
      const t = ms[0]
      await bot.lookAt(t.position.offset(0,1,0), true)
      bot.attack(t)
      await sleep(200)
    }
    await sleep(2000)
    console.log('inv after melee', JSON.stringify(inv()))
    console.log('chat ni/ember', chat.filter(c=>/ni|余烬|Neige|give|Ember|command/i.test(c)).join(' | '))
    // If still alive, use damage via effect harm? or /damage not in 1.12
    if (mobs().length) {
      console.log('still alive, creative one-hit try')
      bot.chat('/gamemode 1')
      await sleep(400)
      for (let i=0;i<30;i++) {
        const ms=mobs(); if(!ms.length) break
        await bot.lookAt(ms[0].position.offset(0,1,0), true)
        bot.attack(ms[0])
        await sleep(150)
      }
      await sleep(1500)
      console.log('inv after creative', JSON.stringify(inv()))
    }
    // Check server: also simulate drop command
    bot.chat('/ni give Tester mat_ember_shard 1')
    await sleep(600)
    console.log('FINAL_INV', JSON.stringify(inv()))
    console.log('DONE')
    bot.quit()
    setTimeout(()=>process.exit(0),400)
  } catch(e){ console.error(e); process.exit(1) }
})
bot.on('error', e=>{console.error(e);process.exit(1)})
bot.on('kicked', r=>{console.error('kicked',r);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},90000)
