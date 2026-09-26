const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function nearMobs() {
  return Object.values(bot.entities).filter(e => e && e !== bot.entity && e.type === 'mob'
    && e.position && Math.abs(e.position.x + 40) < 24 && Math.abs(e.position.z - 270) < 24)
}
function inv() {
  return bot.inventory.items().map(i => ({
    name: i.name, count: i.count, displayName: i.displayName,
    lore: (i.nbt && i.nbt.value && i.nbt.value.display && i.nbt.value.display.value
      && i.nbt.value.display.value.Lore && i.nbt.value.display.value.Lore.value
      && i.nbt.value.display.value.Lore.value.value) || null
  }))
}
function hasShard(list) {
  return list.some(i => {
    const lore = (i.lore || []).join(' ')
    return /mat_ember_shard|余烬.*碎片|ember_shard/i.test(lore + ' ' + (i.displayName || '') + ' ' + i.name)
  })
}
bot.once('spawn', async () => {
  try {
    console.log('SPAWNED', bot.entity.position)
    bot.chat('/mm reload')
    await sleep(1500)
    bot.chat('/ni reload')
    await sleep(1000)
    bot.chat('/gamemode 0')
    await sleep(400)
    bot.chat('/effect Tester resistance 120 10 true')
    await sleep(200)
    bot.chat('/effect Tester strength 120 5 true')
    await sleep(200)
    bot.chat('/clear')
    await sleep(500)
    bot.chat('/tp -40 65 270')
    await sleep(800)
    bot.chat('/minecraft:kill @e[type=!Player,r=30]')
    await sleep(1200)
    bot.chat('/give Tester diamond_sword 1')
    await sleep(500)
    // equip sword
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) await bot.equip(sword, 'hand')
    await sleep(300)
    console.log('INV_BEFORE', JSON.stringify(inv()))
    bot.chat('/mm m spawn EmberCryptZombie 1 world,-40,65,270')
    await sleep(1500)
    console.log('MOBS', JSON.stringify(nearMobs().map(e => ({ name: e.name, type: e.type, pos: e.position }))))
    let killed = false
    for (let i = 0; i < 100; i++) {
      const ms = nearMobs()
      if (!ms.length) { console.log('DEAD_AT', i); killed = true; break }
      const t = ms[0]
      try { await bot.lookAt(t.position.offset(0, 1, 0), true) } catch (_) {}
      bot.attack(t)
      await sleep(180)
    }
    if (!killed) {
      console.log('FALLBACK_CREATIVE')
      bot.chat('/gamemode 1')
      await sleep(400)
      for (let i = 0; i < 40; i++) {
        const ms = nearMobs()
        if (!ms.length) { killed = true; console.log('DEAD_CREATIVE', i); break }
        try { await bot.lookAt(ms[0].position.offset(0, 1, 0), true) } catch (_) {}
        bot.attack(ms[0])
        await sleep(150)
      }
      bot.chat('/gamemode 0')
      await sleep(300)
    }
    await sleep(2500)
    const after = inv()
    console.log('INV_AFTER', JSON.stringify(after))
    console.log('HAS_SHARD', hasShard(after))
    console.log('CHAT_DROP', chat.filter(c => /ni |Neige|余烬|shard|give|Mythic|重载|Spawned|command/i.test(c)).join(' || '))
    console.log('RESULT', hasShard(after) ? 'PASS_INV' : 'FAIL_INV')
    bot.quit()
    setTimeout(() => process.exit(0), 500)
  } catch (e) {
    console.error('ERR', e)
    process.exit(1)
  }
})
bot.on('error', e => { console.error('error', e); process.exit(1) })
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 120000)
