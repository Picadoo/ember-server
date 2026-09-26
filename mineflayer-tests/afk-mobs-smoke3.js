const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
bot.on('kicked', r => console.log('[kicked]', JSON.stringify(r)))
bot.on('error', e => console.error('[err]', e.message))
const sleep = ms => new Promise(r => setTimeout(r, ms))
const cmd = async (c, w = 700) => { console.log('[cmd]', c); bot.chat(c); await sleep(w) }
function near(r = 16) {
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
async function smash(pred, label) {
  for (let i = 0; i < 80; i++) {
    const ms = near().filter(pred)
    if (!ms.length) return true
    const t = ms[0]
    try { await bot.lookAt(t.position.offset(0, 1, 0), true) } catch (_) {}
    bot.attack(t)
    await sleep(120)
  }
  return near().filter(pred).length === 0
}
bot.once('spawn', async () => {
  const out = { shard: false, bone: false, names: [], spawners: [], spawnOk: false, notes: [] }
  try {
    await sleep(1500)
    await cmd('/mvtp Tester ember_afk', 2000)
    await cmd('/tp -46 70 250', 800)
    await cmd('/gamemode 1', 400)
    // Hologram flavor (safe chat)
    await cmd('/hd remove ember_afk_hub', 500)
    await cmd('/hd create ember_afk_hub &6挂机庭 &7| Ember AFK', 800)
    await cmd('/hd addline ember_afk_hub &a碎片·骨尘 &8MM→NI', 600)
    await cmd('/hd addline ember_afk_hub &7EmberAfkZombie / EmberAfkSkeleton', 600)
    await cmd('/hd movehere ember_afk_hub', 500)

    await cmd('/minecraft:kill @e[type=!Player,r=40]', 1200)
    await cmd('/clear', 400)
    await cmd('/give Tester diamond_sword 1', 500)
    const sword = bot.inventory.items().find(i => i.name === 'diamond_sword')
    if (sword) { await bot.equip(sword, 'hand'); console.log('EQUIPPED') }
    await cmd('/effect Tester resistance 200 10 true', 250)
    await cmd('/effect Tester strength 200 5 true', 250)
    // Stay creative for reliable melee (same fallback as ember-kill-drop-retest)

    await cmd('/mm m spawn EmberAfkZombie 1 ember_afk,-46,70,250', 1200)
    out.spawnOk = chat.some(c => /Spawned 1x EmberAfkZombie/.test(c))
    console.log('NEAR', near().map(e => e.name))

    for (let k = 0; k < 10 && !out.shard; k++) {
      if (!near().filter(e => /zombie/i.test(e.name || '')).length) {
        await cmd('/mm m spawn EmberAfkZombie 1 ember_afk,-46,70,250', 1000)
      }
      const dead = await smash(e => /zombie/i.test(e.name || ''), 'Z')
      console.log('Z_ROUND', k, 'dead', dead)
      await sleep(1500)
      console.log('INV', JSON.stringify(inv()))
      out.shard = has(/mat_ember_shard|余烬碎片/)
    }

    for (let k = 0; k < 10 && !out.bone; k++) {
      if (!near().filter(e => /skeleton/i.test(e.name || '')).length) {
        await cmd('/mm m spawn EmberAfkSkeleton 1 ember_afk,-48,70,242', 1000)
      }
      const dead = await smash(e => /skeleton/i.test(e.name || ''), 'S')
      console.log('S_ROUND', k, 'dead', dead)
      await sleep(1500)
      console.log('INV', JSON.stringify(inv()))
      out.bone = has(/mat_ember_bone_dust|余烬骨尘/)
    }

    // Confirm display names via /kill after waiting for spawner
    await sleep(12000)
    await cmd('/minecraft:kill @e[type=!Player,r=40]', 1000)
    out.names = chat.filter(c => /Killed|挂机庭|灰烬庭院/.test(c)).slice(-25)
    await cmd('/mm s info EmberAfk_Z1', 600)
    await cmd('/mm s info EmberAfk_S1', 600)
    out.spawners = chat.filter(c => /MobSpawn:|Stats for Spawner EmberAfk/.test(c)).slice(-8)
    out.niChat = chat.filter(c => /Neige|余烬|成功给予|你得到了/.test(c)).slice(-15)
    console.log('RESULT', JSON.stringify(out, null, 2))
  } catch (e) {
    console.error(e)
    out.notes.push(String(e))
    console.log('RESULT', JSON.stringify(out, null, 2))
  } finally {
    bot.quit(); setTimeout(() => process.exit(0), 400)
  }
})
setTimeout(() => { console.error('timeout'); process.exit(2) }, 180000)
