const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
bot.on('kicked', r => console.log('[kicked]', r))
const sleep = ms => new Promise(r => setTimeout(r, ms))
const cmd = async (c, w = 600) => { console.log('[cmd]', c); bot.chat(c); await sleep(w) }
function near(r = 20) {
  const p = bot.entity.position
  return Object.values(bot.entities).filter(e =>
    e && e !== bot.entity && e.type === 'mob' && e.position &&
    Math.hypot(e.position.x - p.x, e.position.z - p.z) < r)
}
function inv() {
  return bot.inventory.items().map(i => ({
    name: i.name, count: i.count, customName: i.customName || null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}
function has(re) {
  return inv().some(i => re.test(((i.lore || []).join(' ')) + ' ' + (i.customName || '') + ' ' + i.name))
    || chat.some(c => re.test(c))
}
bot.once('spawn', async () => {
  const out = { mm: [], spawners: [], names: [], shard: false, bone: false, kills: 0 }
  try {
    await sleep(1200)
    await cmd('/mm reload', 3000)
    out.mm = chat.filter(c => /MythicMobs|重载|EmberAfk/.test(c)).slice(-8)
    await cmd('/mvtp Tester ember_afk', 1800)
    await cmd('/tp -46 70 250', 700)
    await cmd('/minecraft:kill @e[type=!Player,r=40]', 1000)
    out.names = chat.filter(c => /Killed/.test(c)).slice(-20)
    await cmd('/clear', 400)
    await cmd('/give Tester diamond_sword 1', 500)
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) await bot.equip(sword, 'hand')
    await cmd('/gamemode 0', 400)
    await cmd('/effect Tester resistance 300 10 true', 200)
    await cmd('/effect Tester strength 300 10 true', 200)
    await cmd('/effect Tester speed 300 3 true', 200)

    // Spawn on top of player
    await cmd('/mm m spawn EmberAfkZombie 1', 1500)
    console.log('NEAR0', near(8).map(e => ({ n: e.name, d: Math.hypot(e.position.x-bot.entity.position.x, e.position.z-bot.entity.position.z).toFixed(2) })))

    for (let k = 0; k < 12 && !out.shard; k++) {
      let zs = near(10).filter(e => /zombie/i.test(e.name || ''))
      if (!zs.length) {
        await cmd('/mm m spawn EmberAfkZombie 1', 1000)
        zs = near(10).filter(e => /zombie/i.test(e.name || ''))
      }
      if (!zs.length) { console.log('no z'); break }
      // TP onto mob each few swings
      for (let i = 0; i < 60; i++) {
        zs = near(12).filter(e => /zombie/i.test(e.name || ''))
        if (!zs.length) { out.kills++; console.log('Z_DEAD', k, i); break }
        const t = zs[0]
        if (i % 8 === 0) {
          bot.chat(`/tp ${t.position.x.toFixed(1)} ${t.position.y.toFixed(1)} ${t.position.z.toFixed(1)}`)
          await sleep(200)
        }
        try { await bot.lookAt(t.position.offset(0, 1, 0), true) } catch (_) {}
        bot.attack(t)
        await sleep(100)
      }
      await sleep(1200)
      console.log('INV_Z', k, JSON.stringify(inv()), 'chatdrop', chat.filter(c => /余烬|Neige|成功/.test(c)).slice(-3))
      out.shard = has(/mat_ember_shard|余烬碎片/)
    }

    for (let k = 0; k < 12 && !out.bone; k++) {
      let ss = near(10).filter(e => /skeleton/i.test(e.name || ''))
      if (!ss.length) {
        await cmd('/mm m spawn EmberAfkSkeleton 1', 1000)
        ss = near(10).filter(e => /skeleton/i.test(e.name || ''))
      }
      if (!ss.length) break
      for (let i = 0; i < 60; i++) {
        ss = near(12).filter(e => /skeleton/i.test(e.name || ''))
        if (!ss.length) { out.kills++; console.log('S_DEAD', k, i); break }
        const t = ss[0]
        if (i % 8 === 0) {
          bot.chat(`/tp ${t.position.x.toFixed(1)} ${t.position.y.toFixed(1)} ${t.position.z.toFixed(1)}`)
          await sleep(200)
        }
        try { await bot.lookAt(t.position.offset(0, 1, 0), true) } catch (_) {}
        bot.attack(t)
        await sleep(100)
      }
      await sleep(1200)
      console.log('INV_S', k, JSON.stringify(inv()))
      out.bone = has(/mat_ember_bone_dust|余烬骨尘/)
    }

    await cmd('/mm s info EmberAfk_Z1', 500)
    await cmd('/mm s info EmberAfk_S1', 500)
    out.spawners = chat.filter(c => /MobSpawn:|Stats for Spawner EmberAfk/.test(c)).slice(-8)
    // Confirm hologram still there
    await cmd('/hd list', 800)
    out.hd = chat.filter(c => /ember_afk_hub|hologram/i.test(c)).slice(-10)
    console.log('RESULT', JSON.stringify(out, null, 2))
  } catch (e) {
    console.error(e)
    console.log('RESULT', JSON.stringify(out, null, 2))
  } finally {
    bot.quit(); setTimeout(() => process.exit(0), 400)
  }
})
setTimeout(() => { console.error('timeout'); process.exit(2) }, 160000)
