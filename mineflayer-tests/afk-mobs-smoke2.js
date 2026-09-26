const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function nearMobs(r = 18) {
  const p = bot.entity.position
  return Object.values(bot.entities).filter(e =>
    e && e !== bot.entity && e.type === 'mob' && e.position &&
    Math.abs(e.position.x - p.x) < r && Math.abs(e.position.z - p.z) < r)
}
function inv() {
  return bot.inventory.items().map(i => ({
    name: i.name, count: i.count, customName: i.customName || null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}
function hasId(list, re) {
  return list.some(i => re.test(((i.lore || []).join(' ') + ' ' + (i.customName || '') + ' ' + i.name)))
}
async function killUntil(pred, maxRounds = 8) {
  let got = false
  for (let round = 0; round < maxRounds && !got; round++) {
    let ms = nearMobs().filter(pred)
    if (!ms.length) return { got, rounds: round, reason: 'none' }
    for (let i = 0; i < 120; i++) {
      ms = nearMobs().filter(pred)
      if (!ms.length) { console.log('DEAD', pred.name || 'mob', 'round', round, 'tick', i); break }
      try { await bot.lookAt(ms[0].position.offset(0, 1, 0), true) } catch (_) {}
      bot.attack(ms[0])
      await sleep(150)
    }
    await sleep(1500)
    const items = inv()
    console.log('INV', round, JSON.stringify(items))
    got = true // caller checks
    return { got: true, rounds: round, items }
  }
  return { got: false, rounds: maxRounds, items: inv() }
}
bot.once('spawn', async () => {
  const out = { shard: false, bone: false, displayKill: [], spawner: {}, notes: [] }
  try {
    await sleep(1200)
    bot.chat('/mvtp Tester ember_afk'); await sleep(2000)
    bot.chat('/tp -46 70 250'); await sleep(800)
    bot.chat('/gamemode 1'); await sleep(300)
    // Flavor sign at hub
    bot.chat('/setblock -46 71 248 standing_sign 0 replace {Text1:"{\\"text\\":\\"§6挂机庭\\"}",Text2:"{\\"text\\":\\"§7Ember AFK\\"}",Text3:"{\\"text\\":\\"§a碎片·骨尘\\"}",Text4:"{\\"text\\":\\"§8MM→NI\\"}"}')
    await sleep(600)
    bot.chat('/minecraft:kill @e[type=!Player,r=35]'); await sleep(1000)
    bot.chat('/clear'); await sleep(400)
    bot.chat('/give Tester diamond_sword 1'); await sleep(500)
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) await bot.equip(sword, 'hand')
    bot.chat('/gamemode 0'); await sleep(300)
    bot.chat('/effect Tester resistance 180 10 true'); await sleep(200)
    bot.chat('/effect Tester strength 180 4 true'); await sleep(200)

    // Spawn AFK mobs
    bot.chat('/mm m spawn EmberAfkZombie 3 ember_afk,-46,70,250'); await sleep(1200)
    bot.chat('/mm m spawn EmberAfkSkeleton 3 ember_afk,-48,70,242'); await sleep(1200)
    console.log('MOBS', nearMobs().map(e => ({ n: e.name, p: [e.position.x|0,e.position.z|0] })))

    // Kill zombies for shard (70%) — up to 8 kills
    for (let k = 0; k < 8 && !out.shard; k++) {
      let zs = nearMobs().filter(e => /zombie/i.test(e.name || ''))
      if (!zs.length) {
        bot.chat('/mm m spawn EmberAfkZombie 1 ember_afk,-46,70,250'); await sleep(1000)
        zs = nearMobs().filter(e => /zombie/i.test(e.name || ''))
      }
      if (!zs.length) { out.notes.push('no_zombie'); break }
      for (let i = 0; i < 100; i++) {
        zs = nearMobs().filter(e => /zombie/i.test(e.name || ''))
        if (!zs.length) { console.log('Z_DEAD', k, i); break }
        try { await bot.lookAt(zs[0].position.offset(0, 1, 0), true) } catch (_) {}
        bot.attack(zs[0]); await sleep(140)
      }
      await sleep(1200)
      const items = inv()
      console.log('INV_Z', k, JSON.stringify(items))
      if (hasId(items, /mat_ember_shard|余烬碎片/)) { out.shard = true; break }
      if (chat.some(c => /余烬碎片|mat_ember_shard/.test(c))) { out.shard = true; break }
    }

    // Kill skeletons for bone dust
    for (let k = 0; k < 8 && !out.bone; k++) {
      let ss = nearMobs().filter(e => /skeleton/i.test(e.name || ''))
      if (!ss.length) {
        bot.chat('/mm m spawn EmberAfkSkeleton 1 ember_afk,-48,70,242'); await sleep(1000)
        ss = nearMobs().filter(e => /skeleton/i.test(e.name || ''))
      }
      if (!ss.length) { out.notes.push('no_skel'); break }
      for (let i = 0; i < 100; i++) {
        ss = nearMobs().filter(e => /skeleton/i.test(e.name || ''))
        if (!ss.length) { console.log('S_DEAD', k, i); break }
        try { await bot.lookAt(ss[0].position.offset(0, 1, 0), true) } catch (_) {}
        bot.attack(ss[0]); await sleep(140)
      }
      await sleep(1200)
      const items = inv()
      console.log('INV_S', k, JSON.stringify(items))
      if (hasId(items, /mat_ember_bone_dust|余烬骨尘/)) { out.bone = true; break }
      if (chat.some(c => /余烬骨尘|mat_ember_bone_dust/.test(c))) { out.bone = true; break }
    }

    out.displayKill = chat.filter(c => /挂机庭|灰烬庭院|Killed|killed|余烬|Neige|成功给予|你得到了/i.test(c)).slice(-30)
    bot.chat('/mm s info EmberAfk_Z1'); await sleep(700)
    bot.chat('/mm s info EmberAfk_Z2'); await sleep(700)
    bot.chat('/mm s info EmberAfk_S1'); await sleep(700)
    out.spawner = chat.filter(c => /MobSpawn|Stats for Spawner EmberAfk/i.test(c)).slice(-10)
    // Wait for natural spawner respawn names via kill message
    await sleep(10000)
    bot.chat('/minecraft:kill @e[type=Zombie,r=30]'); await sleep(500)
    bot.chat('/minecraft:kill @e[type=Skeleton,r=30]'); await sleep(800)
    out.displayKill = chat.filter(c => /挂机庭|灰烬庭院|Killed|余烬|Neige|成功给予|你得到了/i.test(c)).slice(-40)

    console.log('RESULT', JSON.stringify(out, null, 2))
  } catch (e) {
    console.error('ERR', e)
    out.notes.push(String(e))
    console.log('RESULT', JSON.stringify(out, null, 2))
  } finally {
    bot.quit(); setTimeout(() => process.exit(0), 400)
  }
})
bot.on('error', e => console.error(e))
bot.on('kicked', r => console.log('kicked', r))
setTimeout(() => { console.error('timeout'); process.exit(2) }, 150000)
