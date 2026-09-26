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
    name: i.name, count: i.count,
    customName: i.customName || null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}
function hasShard(items) {
  return items.some(i => {
    const lore = (i.lore || []).join('\n')
    const nm = (i.customName || '') + ' ' + lore
    return /mat_ember_shard|余烬碎片/.test(nm) || (i.name === 'redstone' && /mat_ember_shard|余烬碎片/.test(lore + nm))
  })
}
bot.once('spawn', async () => {
  const out = { reload: {}, spawn: false, kills: 0, shard: false, lore: {}, inv: [], notes: [] }
  try {
    console.log('[retest] spawn', bot.entity.position.toString())
    bot.chat('/gamemode 0')
    await sleep(400)
    bot.chat('/effect Tester resistance 120 10 true')
    await sleep(200)
    bot.chat('/effect Tester regeneration 120 5 true')
    await sleep(200)
    bot.chat('/tp -40 65 270')
    await sleep(800)

    bot.chat('/mm reload')
    await sleep(2500)
    out.reload.mm = chat.filter(c => /Mythic|重载|成功加载|怪物/.test(c)).slice(-8)
    bot.chat('/ni reload')
    await sleep(2000)
    out.reload.ni = chat.filter(c => /Neige|重载|物品/.test(c)).slice(-6)
    console.log('RELOAD_MM', JSON.stringify(out.reload.mm))
    console.log('RELOAD_NI', JSON.stringify(out.reload.ni))

    bot.chat('/clear')
    await sleep(500)
    bot.chat('/minecraft:kill @e[type=!Player,r=32]')
    await sleep(1000)
    bot.chat('/give Tester diamond_sword 1')
    await sleep(400)

    // Kill up to 5 zombies to overcome 0.85 NI chance / confirm command drop
    for (let k = 1; k <= 5; k++) {
      bot.chat('/clear Tester redstone')
      await sleep(300)
      // clear leftover NI items but keep sword
      bot.chat('/mm m spawn EmberCryptZombie 1 world,-40,65,270')
      await sleep(1200)
      const before = nearMobs().length
      console.log('SPAWN_ROUND', k, 'mobs', before)
      if (before > 0) out.spawn = true
      for (let i = 0; i < 100; i++) {
        const ms = nearMobs()
        if (!ms.length) { console.log('dead round', k, 'at', i); out.kills++; break }
        await bot.lookAt(ms[0].position.offset(0, 1, 0), true)
        bot.attack(ms[0])
        await sleep(180)
      }
      await sleep(1500)
      const items = inv()
      console.log('INV_ROUND', k, JSON.stringify(items))
      if (hasShard(items)) {
        out.shard = true
        out.inv = items
        console.log('SHARD_FOUND_ROUND', k)
        break
      }
      // also pick up ground items
      const drops = Object.values(bot.entities).filter(e => e.name === 'item' || e.displayName === 'item' || e.type === 'object')
      console.log('GROUND_ENTITIES', drops.length)
    }

    if (!out.shard) out.inv = inv()

    // lore check via ni give gear
    bot.chat('/ni give Tester gear_ember_blade 1')
    await sleep(600)
    bot.chat('/ni give Tester gear_ember_charm 1')
    await sleep(600)
    const after = inv()
    out.invFinal = after
    const blade = after.find(i => (i.lore || []).some(l => /gear_ember_blade/.test(l)) || /余烬之刃/.test(i.customName || ''))
    const charm = after.find(i => (i.lore || []).some(l => /gear_ember_charm/.test(l)) || /余烬护符/.test(i.customName || ''))
    out.lore.blade = blade ? blade.lore : null
    out.lore.charm = charm ? charm.lore : null
    const loreText = JSON.stringify(out.lore)
    out.lore.aligned =
      /物理伤害/.test(loreText) && /生命力/.test(loreText) && /物理防御/.test(loreText) &&
      !/攻击力:/.test(loreText) && !/生命值:/.test(loreText) && !/防御力:/.test(loreText)

    console.log('RESULT_JSON', JSON.stringify(out))
    console.log('SHARD', out.shard ? 'PASS' : 'FAIL')
    console.log('LORE_ALIGNED', out.lore.aligned ? 'PASS' : 'FAIL')
    console.log('DONE')
    bot.quit()
    setTimeout(() => process.exit(0), 500)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})
bot.on('error', e => { console.error(e); process.exit(1) })
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 180000)
