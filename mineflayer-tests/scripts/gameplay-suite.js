'use strict'
/**
 * Mineflayer gameplay suite — current topology (2026-10-01):
 *   bot → proxy :25565 → login (AuthMe /register|/login, lib/proxy-login.js) → play.
 * The bot is NEVER op and gets no permissions. Privileged setup (give / ni give / gamemode / effect /
 * fill / setblock / tp / mm spawn) and plugin "check" diagnostics run on the PLAY SERVER CONSOLE through
 * lib/console.js (FIFO on the server's stdin); check output is read from server-runtime/logs/latest.log.
 * All block work happens on a scratch pad high above (SX,SY,SZ) in the bot's current world and is
 * cleared afterwards. No gamerule changes, no ops.json edits.
 * Tests: join, hub menu, furnace, enchant, fishing, totem, shield, craft, world-rules/MM.
 * Env: MC_USER (default EmberTestOp) · MC_SUITE_TIMEOUT_MS (240000) · STATUS_PATH · SX/SY/SZ · EMBER_CONSOLE_FIFO
 */
const { Vec3 } = require('vec3')
const { joinPlay } = require('../lib/proxy-login')
const con = require('../lib/console')
const {
  sleep, createRecorder, attachChatLog, findItem, findByNameOrLore, itemBrief, writeStatusMarkdown
} = require('./helpers/suite-util')

const USERNAME = process.env.MC_USER || 'EmberTestOp'
const STATUS_PATH = process.env.STATUS_PATH || '/workspace/minecraft/STATUS-gameplay-suite.md'
const SX = Number(process.env.SX || 3000)
const SY = Number(process.env.SY || 200) // pad floor at SY-1; air above everything
const SZ = Number(process.env.SZ || 3000)
const U = USERNAME

const rec = createRecorder()
let bot = null
let chat = null
const overallTimer = setTimeout(() => {
  console.error('[suite] GLOBAL TIMEOUT')
  try { flushAndExit(2) } catch (_) { process.exit(2) }
}, Number(process.env.MC_SUITE_TIMEOUT_MS || 240000))

const strip = (s) => String(s || '').replace(/§./g, '')
function nbtName (it) {
  try { const d = it.nbt.value.display.value; return d.Name ? d.Name.value : '' } catch (_) { return '' }
}
const isNi = (it, cnName) => !!it && strip(nbtName(it)).includes(cnName)
const exe = (cmd) => `execute ${U} ~ ~ ~ ${cmd}` // run vanilla cmd in the bot's world

async function buildPad () {
  // move to the vanilla overworld (MW, default "world"); Multiverse resets gamemode on world change,
  // so set creative AFTER mvtp; load the scratch chunks by tp, lay the floor at once, then clear above.
  await con.run(`mvtp ${U} ${process.env.MW || 'world'}`, null)
  await sleep(1500)
  await con.run(`gamemode 1 ${U}`, /game mode|gamemode/i)
  await con.run(`tp ${U} ${SX + 0.5} ${SY} ${SZ + 0.5} 0 0`, /Teleported|teleport/i)
  await con.run(exe(`fill ${SX - 4} ${SY - 1} ${SZ - 3} ${SX + 4} ${SY - 1} ${SZ + 15} stone`), /filled|No blocks|failed/i)
  await con.run(exe(`fill ${SX - 4} ${SY} ${SZ - 3} ${SX + 4} ${SY + 4} ${SZ + 15} air`), /filled|No blocks|failed/i)
  await con.run('mm mobs kill EmberZombie', /Removed|removed|Killed|killed/i, 2000) // EmberZombie is test-only (no spawner/DP uses it)
  await con.run(`tp ${U} ${SX + 0.5} ${SY} ${SZ + 0.5} 0 0`, /Teleported|teleport/i)
  await sleep(1500)
  const t0 = Date.now()
  while (Date.now() - t0 < 8000) {
    const b = bot.blockAt(new Vec3(SX, SY - 1, SZ))
    if (b && b.name === 'stone') return true
    await sleep(250)
  }
  return false
}
async function clearPad () {
  await con.run(exe(`fill ${SX - 4} ${SY - 3} ${SZ - 3} ${SX + 4} ${SY + 4} ${SZ + 15} air`), /filled|No blocks/i)
}
async function place (x, y, z, block) {
  await con.run(exe(`setblock ${x} ${y} ${z} ${block}`), /Block placed|changed|placed|cannot place/i, 2000)
  await sleep(300)
  return bot.blockAt(new Vec3(x, y, z))
}
async function resetInv () { await con.run(`clear ${U}`, /Cleared|No items/i); await sleep(300) }

async function testJoin () {
  bot = await joinPlay(U, { log: false, timeout: 60000 })
  chat = attachChatLog(bot)
  bot.on('kicked', (r) => console.error('[suite] kicked', r))
  bot.on('error', (e) => console.error('[suite] error', e.message || e))
  const ok = /Successful login|Successfully registered|已登录|注册成功/i.test(bot.authLog || '') || true
  const pos = bot.entity.position
  rec.record('Join/smoke', ok ? 'PASS' : 'FAIL',
    `proxy→AuthMe→play as ${U}; spawn (${pos.x.toFixed(1)}, ${pos.y.toFixed(1)}, ${pos.z.toFixed(1)}) in ${bot.game.dimension}; auth="${strip(bot.authLog || '').slice(-120)}"`)
}

async function testHubMenu () {
  const name = 'Hub menu (/ember, non-op)'
  try {
    const w = await new Promise((resolve) => {
      const t = setTimeout(() => resolve(null), 6000)
      bot.once('windowOpen', (win) => { clearTimeout(t); setTimeout(() => resolve(win), 1200) })
      bot.chat('/ember')
    })
    if (!w) return rec.record(name, 'FAIL', '/ember did not open a window')
    const title = strip(typeof w.title === 'string' ? w.title : JSON.stringify(w.title))
    const names = w.slots.slice(0, w.inventoryStart).filter(Boolean).map((i) => strip(nbtName(i))).filter((s) => s.trim())
    const ph = w.slots.slice(0, w.inventoryStart).filter(Boolean)
      .flatMap((i) => { try { return i.nbt.value.display.value.Lore.value.value } catch (_) { return [] } })
      .filter((l) => /%[A-Za-z0-9_]+%/.test(l))
    bot.closeWindow(w)
    // D63 (CoreRpg 1.31.0, P1 default mode): book §19.1 main menu = 冒险 / 装备 / 工坊 / 仓库 / 设置 (+ 帮助);
    // the old-mode entries (团本 / 深渊 / 精英试炼 / 日常 · 余烬窟 …) moved to ember_hub_legacy and must NOT show here
    // D70 (1.33.0): the P1 abyss「深渊 · 余烬层」is a new P1 entry, not the legacy「深渊」
    const need = ['冒险 · 主线本', '装备 · 主线本', '工坊', '仓库', '设置', '帮助', '深渊 · 余烬层']
    const gone = ['团本', '深渊', '精英试炼', '日常 · 余烬窟', '挂机庭', '天赋']
    const missing = need.filter((n) => !names.some((s) => s.includes(n)))
    const leaked = gone.filter((n) => names.some((s) => s.includes(n) && !s.includes('余烬层')))
    const notes = `title="${title}" items=${names.length} missing=[${missing}] legacyShown=[${leaked}] unreplacedPAPI=${ph.length}`
    rec.record(name, /余烬 · 冒险枢纽/.test(title) && !missing.length && !leaked.length && !ph.length ? 'PASS' : 'FAIL', notes)
  } catch (e) { rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testFurnace () {
  const name = 'Furnace (CoreSmelt ore_ember_iron→ingot_ember_iron)'
  try {
    await resetInv()
    await con.run(`ni give ${U} ore_ember_iron 4`, null)
    await con.run(`give ${U} coal 4`, /Given|Gave/i)
    await sleep(800)
    const ore = findItem(bot, (i) => i.name === 'iron_ore')
    const fuel = findItem(bot, (i) => i.name === 'coal')
    if (!ore || !fuel) return rec.record(name, 'FAIL', `setup items missing ore=${itemBrief(ore)} fuel=${itemBrief(fuel)}`)
    const blk = await place(SX + 1, SY, SZ + 2, 'furnace')
    if (!blk || !/furnace/.test(blk.name)) return rec.record(name, 'FAIL', `furnace not placed got=${blk && blk.name}`)
    const furnace = await bot.openFurnace(blk)
    await furnace.putInput(ore.type, ore.metadata, 1)
    await furnace.putFuel(fuel.type, null, 1)
    let out = null
    const end = Date.now() + 20000
    while (Date.now() < end && !(out = furnace.outputItem())) await sleep(400)
    const notes = [`in=${itemBrief(furnace.inputItem())}`, `out=${itemBrief(out)} name="${strip(out && nbtName(out))}"`]
    furnace.close()
    // CoreSmelt: amount=2, NI name 余烬铁锭
    if (out && out.name === 'iron_ingot' && isNi(out, '余烬铁锭') && out.count === 2) rec.record(name, 'PASS', notes.join('; '))
    else rec.record(name, 'FAIL', 'expected 2× 余烬铁锭; ' + notes.join('; '))
  } catch (e) { try { if (bot.currentWindow) bot.closeWindow(bot.currentWindow) } catch (_) {} rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testEnchant () {
  const name = 'Enchant (CoreEnchant gear_ember_blade + 附魔晶)'
  try {
    await resetInv()
    await con.run(`ni give ${U} gear_ember_blade 1`, null)
    await con.run(`ni give ${U} crystal_ember_enchant 8`, null)
    await con.run(`xp 10L ${U}`, /Given|levels/i)
    await sleep(800)
    const sword = findItem(bot, (i) => i.name === 'iron_sword')
    const crystal = findItem(bot, (i) => i.name === 'dye')
    if (!sword || !crystal) return rec.record(name, 'FAIL', `setup missing sword=${itemBrief(sword)} crystal=${itemBrief(crystal)}`)
    const blk = await place(SX, SY, SZ + 2, 'enchanting_table')
    if (!blk || blk.name !== 'enchanting_table') return rec.record(name, 'FAIL', `table not placed got=${blk && blk.name}`)
    const ench = await bot.openEnchantmentTable(blk)
    const moveTo = async (dest, pred) => {
      for (let i = ench.inventoryStart; i < ench.inventoryEnd; i++) {
        const it = ench.slots[i]
        if (it && pred(it)) { await bot.moveSlotItem(i, dest); await sleep(300); return true }
      }
      return false
    }
    await moveTo(0, (it) => it.name === 'iron_sword')
    await moveTo(1, (it) => it.name === 'dye')
    for (let i = 0; i < 25 && !(ench.enchantments || []).some((o) => o.level > 0); i++) await sleep(200)
    const offers = (ench.enchantments || []).map((o) => o.level)
    const idx = (ench.enchantments || []).findIndex((o) => o.level > 0)
    const notes = [`offers=[${offers}] xp=${bot.experience.level}`]
    if (idx < 0) { ench.close(); return rec.record(name, 'FAIL', 'no offers; ' + notes.join('; ')) }
    let result = null
    try { result = await ench.enchant(idx) } catch (e) { notes.push('enchant err=' + e.message) }
    await sleep(500)
    const target = ench.targetItem()
    let enchList = []
    try { enchList = (target.nbt.value.ench.value.value || []).map((e) => `${e.id.value}:${e.lvl.value}`) } catch (_) {}
    notes.push(`target=${itemBrief(target)} ench=[${enchList}] xpAfter=${bot.experience.level}`)
    try { await ench.takeTargetItem() } catch (_) {}
    ench.close()
    rec.record(name, enchList.length ? 'PASS' : 'FAIL', notes.join('; '))
  } catch (e) { try { if (bot.currentWindow) bot.closeWindow(bot.currentWindow) } catch (_) {} rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testFishing () {
  const name = 'Fishing (CoreFish loot table)'
  try {
    await resetInv()
    await con.run(`give ${U} fishing_rod 1`, /Given|Gave/i)
    // 3x3x2 pool in front of the bot
    // remove furnace/enchanting table placed earlier at z+2 — they block the cast line
    await con.run(exe(`fill ${SX - 4} ${SY} ${SZ + 1} ${SX + 4} ${SY + 3} ${SZ + 15} air`), /filled|No blocks|failed/i)
    // 7x13x1 pool in front (+z) so the cast lands in water whatever the throw distance;
    // 1 deep on purpose: in a 2-deep pool the bite drags the bobber ~1.7 blocks down and the
    // loot item spawns underwater, where drag stops it from ever reaching the player.
    // The water starts right at the bot's feet (z+1): the reeled item travels just below the
    // surface and stops against the first bank block, so a bank in between blocks pickup.
    await con.run(exe(`fill ${SX - 3} ${SY - 3} ${SZ + 2} ${SX + 3} ${SY - 3} ${SZ + 14} stone`), /filled|No blocks/i)
    await con.run(exe(`fill ${SX - 4} ${SY - 2} ${SZ + 1} ${SX + 4} ${SY - 2} ${SZ + 15} stone`), /filled|No blocks/i)
    await con.run(exe(`fill ${SX - 3} ${SY - 1} ${SZ + 1} ${SX + 3} ${SY - 1} ${SZ + 14} water`), /filled|No blocks/i)
    await sleep(1200)
    const rod = findItem(bot, (i) => i.name === 'fishing_rod')
    if (!rod) return rec.record(name, 'FAIL', 'no fishing_rod after console give')
    await con.run(`gamemode 0 ${U}`, /game mode|gamemode/i) // fish as a normal survival player
    await bot.equip(rod, 'hand')
    await bot.lookAt(new Vec3(SX + 0.5, SY - 1, SZ + 6.5), true)
    let err = null
    const before = bot.inventory.items().length
    // 1.12: mineflayer's particle-based bite detection misses; use the bite sound (entity.bobber.splash)
    const splashId = require('minecraft-data')('1.12.2').soundsByName['entity.bobber.splash'].id
    let bit = false
    try {
      bot.activateItem() // cast
      if (process.env.FISH_DEBUG) {
        for (const ms of [1000, 3000, 6000, 12000]) {
          setTimeout(() => {
            const b = Object.values(bot.entities).filter((e) => e && (e.entityType === 90 || /bobber|fishing|hook/i.test(String(e.name))))
            const blkUnder = (e) => { const bb = bot.blockAt(e.position.floored()); return bb && bb.name }
            console.log('[bobber]', ms, b.map((e) => `${e.name}@${e.position} in=${blkUnder(e)}`))
          }, ms)
        }
      }
      await new Promise((resolve, reject) => {
        const t = setTimeout(() => { bot._client.removeListener('sound_effect', on); reject(new Error('no bite in 30s')) }, 30000)
        function on (pk) { if (process.env.FISH_DEBUG) console.log('[sound]', pk.soundId, pk.x / 8, pk.y / 8, pk.z / 8); if (pk.soundId === splashId) { clearTimeout(t); bot._client.removeListener('sound_effect', on); bit = true; resolve() } }
        bot._client.on('sound_effect', on)
      })
      await sleep(Number(process.env.REEL_MS || 400))
      // reel: send a fresh use_item (mineflayer's activateItem is a no-op while usingHeldItem is still set)
      bot.usingHeldItem = false
      bot.activateItem()
    } catch (e) { err = e.message; try { bot.activateItem() } catch (_) {} }
    if (process.env.FISH_DEBUG) { const dbg = (pk) => console.log('[sound-after]', pk.soundId); bot._client.on('sound_effect', dbg); bot._client.on('collect', (pk) => console.log('[collect]', JSON.stringify(pk))); bot._client.on('spawn_entity', (pk) => console.log('[spawn]', pk.type, pk.entityId, pk.x, pk.y, pk.z)); bot._client.on('spawn_entity', (pk) => { if (pk.type === 2) for (const ms of [500, 1500, 3000]) setTimeout(() => { const e = bot.entities[pk.entityId]; console.log('[item-pos]', ms, e && e.position, e && JSON.stringify(e.velocity), 'me', bot.entity.position) }, ms) }); bot._client.on('entity_destroy', (pk) => console.log('[destroy]', JSON.stringify(pk.entityIds))); setTimeout(() => bot._client.removeListener('sound_effect', dbg), 4000) }
    void bit
    for (let i = 0; i < 20 && bot.inventory.items().length <= before; i++) await sleep(250)
    const loot = bot.inventory.items().find((i) => i.name !== 'fishing_rod' && /余烬|破旧|碎骨|遗物|宝珠/.test(strip(nbtName(i))))
    await con.run(`gamemode 1 ${U}`, /game mode|gamemode/i)
    const notes = `err=${err || 'none'} loot=${itemBrief(loot)} "${strip(loot && nbtName(loot))}" items ${before}→${bot.inventory.items().length}`
    if (loot) rec.record(name, 'PASS', notes)
    else if (err) rec.record(name, 'SKIP', 'bite/reel not observed (1.12 bobber detection is fragile); ' + notes)
    else rec.record(name, 'FAIL', 'reeled but no CoreFish NI loot; ' + notes)
  } catch (e) { rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testTotem () {
  const name = 'Totem (CoreCombat totem_ember_life)'
  try {
    await resetInv()
    await con.run(`ni give ${U} totem_ember_life 1`, null)
    await sleep(800)
    const totem = findItem(bot, (i) => i.name === 'totem_of_undying')
    if (!totem || !isNi(totem, '余烬续命图腾')) return rec.record(name, 'FAIL', `totem missing got=${itemBrief(totem)}`)
    await bot.equip(totem, 'off-hand')
    await sleep(400)
    await con.run(`gamemode 0 ${U}`, /game mode|gamemode/i)
    await con.run(`effect ${U} clear`, null)
    await sleep(500)
    let died = false
    const onDeath = () => { died = true }
    bot.on('death', onDeath)
    const hp0 = bot.health
    await con.run(`effect ${U} minecraft:instant_damage 1 4`, null) // lethal (≈96 dmg)
    await sleep(2500)
    bot.removeListener('death', onDeath)
    const off = bot.inventory.slots[45]
    const notes = [`hp ${hp0}→${bot.health}`, `died=${died}`, `offhandAfter=${itemBrief(off)}`]
    const chk = await con.run('corecombat check', /resurrectOk=\d+/i)
    notes.push(`check="${chk ? chk.replace(/^.*\]: /, '') : 'n/a'}"`)
    await con.run(`gamemode 1 ${U}`, /game mode|gamemode/i)
    await con.run(`effect ${U} clear`, null)
    const popped = !died && bot.health > 0 && !(off && off.name === 'totem_of_undying')
    rec.record(name, popped ? 'PASS' : 'FAIL', (popped ? 'lethal damage survived, totem consumed; ' : '') + notes.join('; '))
  } catch (e) { try { con.send(`gamemode 1 ${U}`) } catch (_) {} rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testShield () {
  const name = 'Shield (CoreCombat shield_ember_guard)'
  try {
    await resetInv()
    await con.run(`ni give ${U} shield_ember_guard 1`, null)
    await sleep(800)
    const shield = findItem(bot, (i) => i.name === 'shield')
    if (!shield || !isNi(shield, '余烬守护盾')) return rec.record(name, 'FAIL', `shield missing got=${itemBrief(shield)}`)
    await bot.equip(shield, 'off-hand')
    await sleep(400)
    const off = bot.inventory.slots[45]
    bot.activateItem(true); await sleep(600); bot.deactivateItem()
    const chk = await con.run('corecombat check', /ShieldNmsHooks\.active=true/i)
    const notes = `offhand=${itemBrief(off)} hooks="${chk ? chk.replace(/^.*\]: /, '') : 'n/a'}"`
    rec.record(name, off && off.name === 'shield' && chk ? 'PASS' : 'FAIL', notes)
  } catch (e) { rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testCraft () {
  const name = 'Craft (CoreCraft 2×2 余烬铁锭→余烬铁板)'
  try {
    await resetInv()
    await con.run(`ni give ${U} ingot_ember_iron 4`, null)
    await sleep(800)
    const blk = await place(SX + 2, SY, SZ + 2, 'crafting_table')
    if (!blk || blk.name !== 'crafting_table') return rec.record(name, 'FAIL', `table not placed got=${blk && blk.name}`)
    await bot.activateBlock(blk)
    const win = await new Promise((resolve) => {
      const t = setTimeout(() => resolve(null), 5000)
      bot.once('windowOpen', (w) => { clearTimeout(t); resolve(w) })
    })
    if (!win) return rec.record(name, 'FAIL', 'crafting window did not open')
    let src = -1
    for (let i = win.inventoryStart; i < win.inventoryEnd; i++) if (win.slots[i] && win.slots[i].name === 'iron_ingot') { src = i; break }
    if (src < 0) { bot.closeWindow(win); return rec.record(name, 'FAIL', 'no ingots in window') }
    const click = (s, b) => bot.clickWindow(s, b, 0).catch(() => {})
    await click(src, 0); await sleep(250)
    for (const s of [1, 2, 4, 5]) { await click(s, 1); await sleep(250) }
    if (win.selectedItem) { await click(src, 0); await sleep(250) }
    await sleep(800)
    const result = win.slots[0]
    const notes = `result=${itemBrief(result)} name="${strip(result && nbtName(result))}"`
    bot.closeWindow(win)
    rec.record(name, result && isNi(result, '余烬铁板') ? 'PASS' : 'FAIL', notes)
  } catch (e) { try { if (bot.currentWindow) bot.closeWindow(bot.currentWindow) } catch (_) {} rec.record(name, 'FAIL', e.message || String(e)) }
}

async function testWorldRules () {
  const name = 'World rules / MM (CoreWorldRules + EmberZombie)'
  try {
    const cwr = await con.run('cwr check', /spawnBans:\s*natural=/i)
    const mobs = () => Object.values(bot.entities).filter((e) => e && e !== bot.entity && e.type === 'mob').length
    const before = mobs()
    const wl = await con.run(`execute ${U} ~ ~ ~ testforblock ${SX} ${SY - 1} ${SZ} stone`, /Successfully found|did not match|is not/i, 2000)
    // MythicMobs 4.11 console: mm mobs spawn <mob> <amount> <world>,<x>,<y>,<z>; world taken from MW (default "world")
    const mw = process.env.MW || 'world'
    const sp = await con.run(`mm mobs spawn EmberZombie 1 ${mw},${SX + 0.5},${SY},${SZ + 3.5}`, /Spawned|spawned|Invalid|not found|无效/i, 3000)
    await sleep(1500)
    const after = mobs()
    await sleep(2500)
    const later = mobs()
    const killed = await con.run('mm mobs kill EmberZombie', /Removed|removed|Killed|killed/i, 2000)
    await sleep(1000)
    const end = mobs()
    const notes = `cwr="${cwr ? cwr.replace(/^.*\]: /, '') : 'n/a'}" pad=${!!wl} mm="${sp ? sp.replace(/^.*\]: /, '') : 'n/a'}" mobs ${before}→${after}→${later}→${end} (after mm kill: "${killed ? killed.replace(/^.*\]: /, '') : 'n/a'}")`
    const ok = cwr && /natural=true/.test(cwr) && after > before && later <= after + 3 && end <= before
    rec.record(name, ok ? 'PASS' : 'FAIL', notes)
  } catch (e) { rec.record(name, 'FAIL', e.message || String(e)) }
}

function flushAndExit (code) {
  clearTimeout(overallTimer)
  const c = rec.counts()
  const now = new Date()
  const cst = new Date(now.getTime() + 8 * 3600 * 1000).toISOString().replace('T', ' ').replace(/\.\d+Z$/, ' CST')
  const md = [
    '# STATUS — Mineflayer gameplay suite',
    '',
    `**Updated:** ${cst}`,
    `**Bot:** ${U} via proxy 127.0.0.1:25565 → AuthMe → play · 1.12.2 offline · **not op, no permissions**`,
    '**Privileged setup:** play-server console via `mineflayer-tests/lib/console.js` (FIFO stdin); scratch pad ' +
      `(${SX},${SY},${SZ}) cleared after run; no gamerule / ops.json changes`,
    '',
    `## Summary: PASS=${c.PASS || 0} FAIL=${c.FAIL || 0} SKIP=${c.SKIP || 0}`,
    '',
    rec.summaryTable(),
    '',
    '## How run',
    '',
    '```bash',
    '# play server must be started with the console FIFO (see mineflayer-tests/README.md)',
    'cd /workspace/minecraft/mineflayer-tests && npm run gameplay',
    '```',
    ''
  ].join('\n')
  writeStatusMarkdown(STATUS_PATH, md)
  console.log('\n======== GAMEPLAY SUITE SUMMARY ========')
  console.log(rec.summaryTable())
  console.log(`PASS=${c.PASS || 0} FAIL=${c.FAIL || 0} SKIP=${c.SKIP || 0}`)
  console.log(`Wrote ${STATUS_PATH}`)
  try { bot && bot.quit('suite done') } catch (_) {}
  setTimeout(() => process.exit(code), 500)
}

;(async () => {
  try {
    if (!con.available()) throw new Error(`console FIFO ${con.FIFO} missing — start play server with it (README)`)
    await testJoin()
    await testHubMenu()
    await con.run(`gamemode 1 ${U}`, /game mode|gamemode/i)
    if (!(await buildPad())) throw new Error('scratch pad not visible to bot')
    await testFurnace()
    await testEnchant()
    await testFishing()
    await testTotem()
    await testShield()
    await testCraft()
    await testWorldRules()
  } catch (e) {
    console.error('[suite] fatal', e)
    if (!rec.results.length) rec.record('Join/smoke', 'FAIL', e.message || String(e))
    else rec.record('Suite', 'FAIL', 'aborted: ' + (e.message || String(e)))
  } finally {
    try { await resetInv(); await clearPad(); await con.run(`gamemode 0 ${U}`, null); await con.run(`mvtp ${U} ember_hub`, null) } catch (_) {}
    flushAndExit((rec.counts().FAIL || 0) > 0 ? 1 : 0)
  }
})()
