/**
 * AFK zone smoke: /mm reload, set spawners, join ember_afk, spawn/kill AFK mobs, check NI drops.
 */
const mineflayer = require('mineflayer')

const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'Tester',
  auth: 'offline',
  version: '1.12.2'
})

const chat = []
bot.on('message', m => {
  const s = m.toString()
  chat.push(s)
  console.log('[chat]', s)
})
bot.on('error', e => console.error('[err]', e.message))
bot.on('kicked', r => console.log('[kicked]', r))

const sleep = ms => new Promise(r => setTimeout(r, ms))
const cmd = async (c, wait = 800) => {
  console.log('[cmd]', c)
  bot.chat(c)
  await sleep(wait)
}

function nearMobs(cx, cy, cz, r = 20) {
  return Object.values(bot.entities).filter(e =>
    e && e !== bot.entity && e.type === 'mob' && e.position &&
    Math.abs(e.position.x - cx) < r && Math.abs(e.position.z - cz) < r
  )
}

function invSnap() {
  return bot.inventory.items().map(i => ({
    name: i.name,
    count: i.count,
    customName: i.customName || null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}

function hasMat(items, id, cn) {
  return items.some(i => {
    const lore = (i.lore || []).join('\n')
    const nm = (i.customName || '') + ' ' + lore
    return new RegExp(id + '|' + cn).test(nm)
  })
}

bot.once('spawn', async () => {
  const out = {
    mmReload: [],
    setSpawner: [],
    world: null,
    spawnedIds: [],
    nameHints: [],
    shard: false,
    bone: false,
    kills: { z: 0, s: 0 },
    notes: []
  }
  try {
    await sleep(1500)
    console.log('[pos]', bot.entity.position.toString())

    await cmd('/gamemode 1', 400)
    await cmd('/mm reload', 3500)
    out.mmReload = chat.filter(c => /Mythic|重载|成功加载|怪物|Loaded|Error|error|EmberAfk/i.test(c)).slice(-15)
    console.log('MM_RELOAD', JSON.stringify(out.mmReload))

    // Hot-set spawner MobName (yml already updated; ensure live)
    for (const [s, m] of [
      ['EmberAfk_Z1', 'EmberAfkZombie'],
      ['EmberAfk_Z2', 'EmberAfkZombie'],
      ['EmberAfk_S1', 'EmberAfkSkeleton']
    ]) {
      await cmd(`/mm s set ${s} MobName ${m}`, 600)
    }
    await cmd('/mm s info EmberAfk_Z1', 800)
    await cmd('/mm s info EmberAfk_S1', 800)
    out.setSpawner = chat.filter(c => /EmberAfk|MobName|Spawner|Set|设置|成功/i.test(c)).slice(-20)
    console.log('SPAWNER', JSON.stringify(out.setSpawner))

    await cmd('/mvtp Tester ember_afk', 2000)
    out.world = bot.game && bot.game.dimension
    console.log('[pos2]', bot.entity.position.toString(), 'dim', out.world)

    // Clear area and force-spawn AFK mobs near hub coords
    await cmd('/tp -46 70 250', 1000)
    await cmd('/minecraft:kill @e[type=!Player,r=40]', 1200)
    await cmd('/clear', 500)
    await cmd('/give Tester diamond_sword 1', 400)
    await cmd('/gamemode 0', 400)
    await cmd('/effect Tester resistance 180 10 true', 300)
    await cmd('/effect Tester regeneration 180 5 true', 300)

    // Force spawn dedicated IDs (don't wait only on spawner cooldown)
    await cmd('/mm m spawn EmberAfkZombie 2 ember_afk,-46,70,250', 1500)
    await cmd('/mm m spawn EmberAfkSkeleton 1 ember_afk,-48,70,242', 1500)

    let mobs = nearMobs(-46, 70, 250, 24)
    out.spawnedIds = mobs.map(e => ({
      type: e.name || e.mobType || e.entityType || e.type,
      custom: e.username || e.displayName || null,
      pos: e.position && [Math.round(e.position.x), Math.round(e.position.y), Math.round(e.position.z)]
    }))
    // nametags often in metadata; also scrape chat from /mm m list if any
    await cmd('/mm m list', 1000)
    out.nameHints = chat.filter(c => /挂机庭|EmberAfk|僵尸|骷髅/i.test(c)).slice(-20)
    console.log('MOBS', JSON.stringify(out.spawnedIds))
    console.log('NAMES', JSON.stringify(out.nameHints))

    // Kill zombies for shard
    for (let round = 0; round < 6 && !out.shard; round++) {
      mobs = nearMobs(-46, 70, 250, 24).filter(e => /zombie/i.test(String(e.name || e.mobType || '')))
      if (!mobs.length) {
        await cmd('/mm m spawn EmberAfkZombie 1 ember_afk,-46,70,250', 1000)
        mobs = nearMobs(-46, 70, 250, 24).filter(e => /zombie/i.test(String(e.name || e.mobType || '')))
      }
      if (!mobs.length) { out.notes.push('no zombie visible round ' + round); break }
      for (let i = 0; i < 80; i++) {
        const zs = nearMobs(-46, 70, 250, 24).filter(e => /zombie/i.test(String(e.name || e.mobType || '')))
        if (!zs.length) { out.kills.z++; break }
        await bot.lookAt(zs[0].position.offset(0, 1, 0), true)
        bot.attack(zs[0])
        await sleep(160)
      }
      await sleep(1200)
      const items = invSnap()
      console.log('INV_Z', round, JSON.stringify(items))
      if (hasMat(items, 'mat_ember_shard', '余烬碎片')) {
        out.shard = true
        break
      }
    }

    // Kill skeletons for bone dust
    for (let round = 0; round < 6 && !out.bone; round++) {
      let sk = nearMobs(-46, 70, 250, 24).filter(e => /skeleton/i.test(String(e.name || e.mobType || '')))
      if (!sk.length) {
        await cmd('/mm m spawn EmberAfkSkeleton 1 ember_afk,-48,70,242', 1000)
        sk = nearMobs(-46, 70, 250, 24).filter(e => /skeleton/i.test(String(e.name || e.mobType || '')))
      }
      if (!sk.length) { out.notes.push('no skeleton visible round ' + round); break }
      for (let i = 0; i < 80; i++) {
        const ss = nearMobs(-46, 70, 250, 24).filter(e => /skeleton/i.test(String(e.name || e.mobType || '')))
        if (!ss.length) { out.kills.s++; break }
        await bot.lookAt(ss[0].position.offset(0, 1, 0), true)
        bot.attack(ss[0])
        await sleep(160)
      }
      await sleep(1200)
      const items = invSnap()
      console.log('INV_S', round, JSON.stringify(items))
      if (hasMat(items, 'mat_ember_bone_dust', '余烬骨尘')) {
        out.bone = true
        break
      }
    }

    // Also check chat for NI give messages
    const niChat = chat.filter(c => /余烬碎片|余烬骨尘|成功给予|你得到了/i.test(c))
    console.log('NI_CHAT', JSON.stringify(niChat.slice(-10)))
    if (niChat.some(c => /碎片/.test(c))) out.shard = true
    if (niChat.some(c => /骨尘/.test(c))) out.bone = true

    // Confirm spawners still active after wait
    await sleep(2000)
    const after = nearMobs(-46, 70, 250, 30)
    out.notes.push('mobs_after_wait=' + after.length)

    console.log('RESULT', JSON.stringify(out, null, 2))
  } catch (e) {
    console.error('FAIL', e)
    out.notes.push(String(e))
    console.log('RESULT', JSON.stringify(out, null, 2))
  } finally {
    bot.quit()
    setTimeout(() => process.exit(0), 500)
  }
})
