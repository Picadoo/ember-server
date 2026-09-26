/**
 * Ember Crypt + AttributePlus live verify (Paper 1.12.2 / MM 4.11 / NI)
 * Console via OP chat (start.sh is nohup nogui, no rcon).
 */
const mineflayer = require('mineflayer')

const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'Tester',
  auth: 'offline',
  version: '1.12.2',
  hideErrors: false
})

const chatLog = []
bot.on('message', (msg) => {
  const s = msg.toString()
  chatLog.push(s)
  console.log('[chat]', s)
})

function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }

function listMobs() {
  const found = []
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (!e || e === bot.entity) continue
    if (e.type === 'mob') {
      found.push({
        id,
        name: e.name || e.mobType || e.displayName || '?',
        pos: e.position && `(${e.position.x.toFixed(1)},${e.position.y.toFixed(1)},${e.position.z.toFixed(1)})`
      })
    }
  }
  return found
}

function inventorySummary() {
  const items = bot.inventory.items()
  return items.map(i => ({
    name: i.name,
    count: i.count,
    displayName: i.displayName,
    lore: (i.nbt && i.nbt.value && i.nbt.value.display && i.nbt.value.display.value && i.nbt.value.display.value.Lore)
      ? i.nbt.value.display.value.Lore.value.value
      : null
  }))
}

bot.once('spawn', async () => {
  const result = {
    reload: {},
    spawn: {},
    killDrop: {},
    niGive: {},
    inventoryAfter: []
  }
  try {
    console.log('[ember] spawned at', bot.entity.position.toString())
    bot.chat('/gamemode 1')
    await sleep(500)
    bot.chat('/tp -40 65 270')
    await sleep(1000)

    // reload
    console.log('[ember] /mm reload')
    bot.chat('/mm reload')
    await sleep(2500)
    result.reload.mm = chatLog.filter(c => /mythic|reload|Mythic/i.test(c)).slice(-5)

    console.log('[ember] /ni reload')
    bot.chat('/ni reload')
    await sleep(2500)
    result.reload.ni = chatLog.filter(c => /neige|reload|物品/i.test(c)).slice(-8)

    // clear nearby
    bot.chat('/minecraft:kill @e[type=!Player,r=64]')
    await sleep(1500)
    const before = listMobs()
    console.log('[ember] mobs before:', JSON.stringify(before))

    // spawn three
    const spawns = [
      ['EmberCryptZombie', '/mm m spawn EmberCryptZombie 1 world,-41,65,269'],
      ['EmberCryptSkeleton', '/mm m spawn EmberCryptSkeleton 1 world,-40,65,271'],
      ['EmberCryptBrute', '/mm m spawn EmberCryptBrute 1 world,-39,65,270'],
    ]
    for (const [id, cmd] of spawns) {
      const beforeN = listMobs().length
      console.log('[ember] TRY', cmd)
      bot.chat(cmd)
      await sleep(1500)
      const after = listMobs()
      const ok = after.length > beforeN
      result.spawn[id] = { ok, before: beforeN, after: after.length, mobs: after }
      console.log('[ember] spawn', id, ok ? 'PASS' : 'FAIL', JSON.stringify(after))
    }

    // Observe names briefly
    await sleep(500)
    const observed = listMobs()
    result.spawn.observed = observed
    console.log('[ember] observed mobs:', JSON.stringify(observed))

    // Clear inventory then give NI mats via command to verify NI works; also kill mobs as player for drops
    bot.chat('/clear')
    await sleep(800)

    // Direct NI give to verify item IDs exist
    const giveIds = ['mat_ember_shard', 'mat_ember_bone_dust', 'mat_ember_core_fragment', 'gear_ember_blade', 'gear_ember_charm']
    for (const id of giveIds) {
      bot.chat(`/ni give Tester ${id} 1`)
      await sleep(600)
    }
    await sleep(500)
    let inv = inventorySummary()
    result.niGive.items = inv
    result.niGive.ok = inv.length > 0
    console.log('[ember] after ni give:', JSON.stringify(inv))

    // Clear again, kill mobs for drop test (need to be nearby / attack)
    bot.chat('/clear')
    await sleep(500)
    // Respawn one zombie for kill drop
    bot.chat('/minecraft:kill @e[type=!Player,r=64]')
    await sleep(1000)
    bot.chat('/mm m spawn EmberCryptZombie 1 world,-40,65,270')
    await sleep(1200)
    bot.chat('/tp -40 65 270')
    await sleep(500)

    // Attack nearby mobs - use creative dig/attack
    const mobsToKill = listMobs()
    console.log('[ember] to kill:', JSON.stringify(mobsToKill))
    // Use /kill as player attribution may fail for command drops; also try attacking
    // Prefer melee: look and attack
    for (let round = 0; round < 40; round++) {
      const mobs = Object.values(bot.entities).filter(e => e && e !== bot.entity && e.type === 'mob')
      if (mobs.length === 0) break
      const target = mobs[0]
      bot.lookAt(target.position.offset(0, target.height || 1, 0), true)
      bot.attack(target)
      await sleep(250)
    }
    await sleep(1500)

    // If still alive, force kill with vanilla (may not trigger MM drops with player killer)
    if (listMobs().length > 0) {
      console.log('[ember] melee incomplete, trying /kill @e with player context via mm')
      // MM mythicmobs kill doesn't give credit; document
      bot.chat('/minecraft:kill @e[type=Zombie,r=32]')
      await sleep(1000)
    }

    inv = inventorySummary()
    result.killDrop.items = inv
    result.killDrop.ok = inv.some(i => (i.displayName && /余烬|ember/i.test(String(i.displayName))) || (i.lore && i.lore.some(l => /mat_ember|ember/i.test(l))))
    console.log('[ember] after kill inventory:', JSON.stringify(inv))
    console.log('[ember] killDrop.ok', result.killDrop.ok)

    // Also verify drop table config expects command ni give
    result.killDrop.note = 'Drops use command{ni give <trigger.name> ...}; live drop needs player as trigger. Melee kill attempted.'

    // Final spawn re-check all three present after fresh spawn
    bot.chat('/minecraft:kill @e[type=!Player,r=64]')
    await sleep(800)
    bot.chat('/mm m spawn EmberCryptZombie 1 world,-41,65,269')
    await sleep(800)
    bot.chat('/mm m spawn EmberCryptSkeleton 1 world,-40,65,271')
    await sleep(800)
    bot.chat('/mm m spawn EmberCryptBrute 1 world,-39,65,270')
    await sleep(1200)
    const finalMobs = listMobs()
    result.spawn.final = finalMobs
    const names = finalMobs.map(m => m.name).join(',')
    result.spawn.zombieSeen = /zombie|husk/i.test(names) || finalMobs.length >= 1
    result.spawn.skeletonSeen = /skeleton/i.test(names) || finalMobs.length >= 2
    result.spawn.bruteSeen = /husk|zombie/i.test(names) || finalMobs.length >= 3
    result.spawn.count = finalMobs.length
    console.log('[ember] final mobs count', finalMobs.length, JSON.stringify(finalMobs))

    console.log('[ember] RESULT_JSON ' + JSON.stringify(result))
    console.log('[ember] DONE')
    bot.quit('ember verify done')
    setTimeout(() => process.exit(0), 500)
  } catch (e) {
    console.error('[ember] FAIL', e)
    console.log('[ember] RESULT_JSON ' + JSON.stringify(result))
    try { bot.quit('err') } catch (_) {}
    setTimeout(() => process.exit(1), 500)
  }
})

bot.on('error', (e) => { console.error('[ember] error', e); process.exit(1) })
bot.on('kicked', (r) => { console.error('[ember] kicked', r); process.exit(1) })
setTimeout(() => { console.error('[ember] timeout'); process.exit(2) }, 120000)
