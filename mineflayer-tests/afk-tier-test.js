// Phase 3 (CoreRpg 1.14.0) tiered AFK zones: a fresh NON-OP bot per tier at the gate level with that stage's gear.
// Checks: gate refusal below the level, /corerpg afk n teleport to the pad, real on-foot combat (skills on CD, eats bread),
// kills / TTK / HP, material gain, mobs inside blocks, death → keepInventory + respawn at the tier pad, region gate kick.
// env: T=1..4  DUR (fight s, default 180)  COV (default blaze)  KICK=1 (tp into tier 4 to test the region gate)  NODEATH=1
const { joinPlay, ensureLevel } = require('./lib/proxy-login')
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const TIERS = {
  1: { lv: 10, blade: 'gear_ember_blade', tal: 'gear_ember_charm', eb: 0, et: 0, sharp: 0, pad: [-4.5, 73, 250.5] },
  2: { lv: 20, blade: 'gear_ember_t1_blade', tal: 'gear_ember_charm', eb: 2, et: 1, sharp: 2, pad: [200.5, 111, 234.5] },
  3: { lv: 30, blade: 'gear_ember_t2_blade', tal: 'gear_ember_t1_talisman', eb: 3, et: 2, sharp: 2, pad: [400.5, 111, 234.5] },
  4: { lv: 40, blade: 'gear_ember_t2_blade', tal: 'gear_ember_t2_talisman', eb: 4, et: 3, sharp: 2, pad: [600.5, 111, 234.5] },
}
const T = Number(process.env.T || 1); const CFG = TIERS[T]; const DUR = Number(process.env.DUR || 180) * 1000
const COV = process.env.COV || 'blaze'
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray'])
const MATS = ['mat_ember_shard', 'mat_ember_bone_dust', 'mat_ember_core_fragment', 'mat_ember_soul_dust', 'mat_calamity_ember']
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const find = (b, id) => b.inventory.items().find(i => niId(i) === id)
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
const mats = b => Object.fromEntries(MATS.map(m => [m, count(b, m)]))
const invSig = b => b.inventory.items().map(i => (niId(i) || i.name) + 'x' + i.count).sort().join(',')
const since = (b, n) => strip(b.chatLog.slice(n).join(' | '))

;(async () => {
  const r = { T, lv: CFG.lv, cov: COV }
  const op = await joinPlay(process.env.OP_BOT || 'RpgBot', { log: false })
  let opQ = Promise.resolve(); const c = (s, ms) => { opQ = opQ.then(async () => { op.chat(s); await wait(ms || 700) }); return opQ }
  const name = process.env.NAME || `Afk${T}x${Math.floor(Math.random() * 9000 + 1000)}`
  const b = await joinPlay(name, { log: false }); b.loadPlugin(pathfinder); r.name = name
  if (process.env.VERBOSE) b.on('message', m => console.log('[bot]', m.toString()))
  await wait(2500)
  const mv = new Movements(b); mv.canDig = false; mv.allow1by1towers = false; mv.scafoldingBlocks = []; b.pathfinder.setMovements(mv)
  await c(`/mvtp ${name} ember_hub`, 1500); await c(`/clear ${name}`)
  // gate refusal while still below the level (fresh account = Lv10)
  if (T > 1) { const n = b.chatLog.length; b.chat(`/corerpg afk ${T}`); await wait(1500); r.gateBelow = since(b, n).slice(0, 160) }
  // exact gate level (fresh Lv10 account): cumulative XP Lv20 1140 · Lv30 3480 · Lv40 7020
  const plan = { 1: [], 2: ['raid_clear', 'raid_clear', 'raid_clear', 'raid_clear', 'abyss_clear', 'daily_clear'],
    3: Array(13).fill('raid_clear').concat(['weekly_clear', 'sign', 'sign']), 4: Array(28).fill('raid_clear').concat(['sign']) }[T]
  for (const src of plan) await c(`/corerpg progress ${name} ${src}`, 600)
  await wait(1500); { const n = b.chatLog.length; b.chat('/corerpg level'); await wait(1200); const m = since(b, n).match(/等级 Lv\.(\d+)/); r.level = m ? Number(m[1]) : 0 }
  await c(`/clear ${name}`); await c(`/effect ${name} clear`)
  await c(`/ni give ${name} ${CFG.blade} 1`, 1000); await c(`/ni give ${name} ${CFG.tal} 1`, 1000)
  await c(`/ni give ${name} mat_ember_shard 64`, 800); await c(`/ni give ${name} mat_ember_bone_dust 20`, 800); await c(`/corerpg coin give ${name} 2000`, 800)
  for (const [id, lv] of [[CFG.tal, CFG.et], [CFG.blade, CFG.eb]]) {
    for (let i = 0; i < 12 && lv > 0; i++) {
      const it = find(b, id); if (!it) break
      try { await b.equip(it, 'hand') } catch (e) {}
      await wait(300); const n0 = b.chatLog.length; b.chat('/corerpg enhance info'); await wait(900)
      const m = since(b, n0).match(/当前 \+(\d+)/); if ((m ? Number(m[1]) : 0) >= lv) break
      b.chat('/corerpg enhance'); await wait(900)
    }
  }
  // strip leftover enhance mats so drop gains are measured from 0
  await c(`/clear ${name} redstone`); await c(`/clear ${name} sulphur`)
  const bl0 = find(b, CFG.blade); if (bl0) { await b.equip(bl0, 'hand'); await wait(300) }
  if (CFG.sharp) await c(`/enchant ${name} sharpness ${CFG.sharp}`)
  await c(`/ni give ${name} food_ember_bread 24`, 900)
  if (T > 1) { b.chat('/corerpg covenant set ' + COV); await wait(1200) }
  { const n0 = b.chatLog.length; b.chat('/corerpg stats'); await wait(1200); r.stats = since(b, n0).slice(0, 200) }
  const maxHp = (() => { const m = (r.stats || '').match(/生命 ([\d.]+)/); return Math.min(40, m ? Number(m[1]) : 20) })() // client sees health scaled to hearts_display_cap 40
  await c(`/effect ${name} instant_health 1 3 true`)
  // enter via the real player command
  { const n0 = b.chatLog.length; b.chat(`/corerpg afk ${T}`); await wait(3000); r.enter = since(b, n0).slice(0, 260) }
  const pad = CFG.pad; r.padDist = +b.entity.position.distanceTo({ x: pad[0], y: pad[1], z: pad[2] }).toFixed(1)
  r.world = null
  if (T === 1) { b.pathfinder.setGoal(new goals.GoalNearXZ(-54, 228, 3)); await wait(20000); b.pathfinder.setGoal(null) }
  const m0 = mats(b)
  // fight
  const st = { minHp: 99, deaths: 0, hits: 0, casts: 0, kills: 0, ttk: [], eats: 0, inBlock: 0, mobsSeen: new Set(), tl: [] }
  const hitAt = new Map()
  b.on('health', () => { if (b.health > 0) st.minHp = Math.min(st.minHp, b.health) })
  b.on('death', () => { st.deaths++ })
  st.dmg = []; let prevH = null
  b.on('health', () => { if (prevH != null && b.health < prevH && b.health > 0) { const near = Object.values(b.entities).filter(x => x && MOBS.has(x.name) && x.position.distanceTo(b.entity.position) < 4).map(x => x.name[0]).join(''); st.dmg.push(+(prevH - b.health).toFixed(1) + near) } prevH = b.health })
  b.on('entityGone', e => { if (hitAt.has(e.id)) { st.kills++; st.ttk.push((Date.now() - hitAt.get(e.id)) / 1000); hitAt.delete(e.id) } })
  const SKCD = { blaze: 8, ash: 10, warden: 12 }[COV] || 0; let lastSk = 0, lastEat = 0
  const t0 = Date.now()
  const iv = setInterval(() => {
    st.tl.push(Math.round(b.health || 0))
    for (const e of Object.values(b.entities)) {
      if (!e || !MOBS.has(e.name)) continue; st.mobsSeen.add(e.id)
      const blk = b.blockAt(e.position.offset(0, 1.2, 0)); if (blk && blk.boundingBox === 'block') st.inBlock++
    }
  }, 3000)
  while (Date.now() - t0 < DUR) {
    if (!b.entity || b.health <= 0) { await wait(800); continue }
    if (b.food < 18 && Date.now() - lastEat > 4000) {
      const br = find(b, 'food_ember_bread'); if (br) { try { await b.equip(br, 'hand'); await b.consume(); st.eats++ } catch (e) {} lastEat = Date.now() }
    }
    const bl = find(b, CFG.blade); if (bl && (!b.heldItem || b.heldItem.slot !== bl.slot)) { try { await b.equip(bl, 'hand') } catch (e) {} }
    const e0 = Object.values(b.entities).filter(x => x && x !== b.entity && MOBS.has(x.name) && x.position.distanceTo(b.entity.position) < 30)
      .sort((x, y) => x.position.distanceTo(b.entity.position) - y.position.distanceTo(b.entity.position))
    // like a player: an archer within 12 blocks gets priority over melee mobs further than 3.5
    const ranged = e0.find(x => (x.name === 'skeleton' || x.name === 'stray') && x.position.distanceTo(b.entity.position) < 12)
    const e = (ranged && !(e0[0] && e0[0].position.distanceTo(b.entity.position) < 3.5 && e0[0] !== ranged && Math.random() < 0.5)) ? ranged : e0[0]
    if (!e) { b.pathfinder.setGoal(null); await wait(500); continue }
    const d = e.position.distanceTo(b.entity.position)
    if (d > 2.8) { b.pathfinder.setGoal(new goals.GoalFollow(e, 1.5), true); await wait(250); continue }
    b.pathfinder.setGoal(null)
    try { await b.lookAt(e.position.offset(0, (e.height || 1.8) * 0.85, 0), true) } catch (x) {}
    b.attack(e); st.hits++; if (!hitAt.has(e.id)) hitAt.set(e.id, Date.now())
    if (SKCD && T > 1 && Date.now() - lastSk > SKCD * 1000 + 400) { await wait(120); b.chat('/corerpg skill'); lastSk = Date.now(); st.casts++ }
    await wait(650)
  }
  clearInterval(iv); b.pathfinder.setGoal(null)
  const m1 = mats(b)
  r.fightSec = Math.round((Date.now() - t0) / 1000); r.kills = st.kills; r.killsPerMin = +(st.kills / (r.fightSec / 60)).toFixed(1)
  r.ttkMed = st.ttk.length ? +st.ttk.sort((a, b) => a - b)[Math.floor(st.ttk.length / 2)].toFixed(1) : null
  r.maxHp = maxHp; r.minHpPct = Math.round(100 * st.minHp / maxHp); r.endHpPct = Math.round(100 * (b.health || 0) / maxHp); r.deaths = st.deaths
  r.avgHpPct = Math.round(100 * st.tl.reduce((a, x) => a + x, 0) / Math.max(1, st.tl.length) / maxHp)
  r.dmg = st.dmg.slice(0, 80).join(' '); r.hits = st.hits; r.casts = st.casts; r.eats = st.eats; r.mobsSeen = st.mobsSeen.size; r.mobInBlockSamples = st.inBlock
  r.gain = Object.fromEntries(MATS.map(m => [m, m1[m] - m0[m]]))
  { const n0 = b.chatLog.length; b.chat('/corerpg afk'); await wait(1500); r.afkStatus = since(b, n0).replace(/\s+/g, ' ').slice(-200) }
  // death → keepInventory + respawn on the pad
  if (!process.env.NODEATH) {
    const before = invSig(b); const n0 = b.chatLog.length
    await c(`/kill ${name}`, 1500)
    for (let i = 0; i < 10 && b.health <= 0; i++) { try { b.respawn && b.respawn() } catch (e) {} ; await wait(800) }
    await wait(2500)
    r.respawnPadDist = +b.entity.position.distanceTo({ x: pad[0], y: pad[1], z: pad[2] }).toFixed(1)
    r.keptInventory = invSig(b) === before
    if (!r.keptInventory) r.invDiff = { before: before.slice(0, 200), after: invSig(b).slice(0, 200) }
    r.resistAfterRespawn = Object.values(b.entity.effects || {}).map(e => e.id).includes(11)
  }
  if (process.env.KICK) {
    await c(`/tp ${name} 600.5 111 240.5`, 3500)
    await wait(3000)
    const p = b.entity.position; r.kickPos = `${p.x.toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`
    r.kickMsg = since(b, b.chatLog.length - 6).match(/\[挂机\][^|]*/g)
  }
  b.chat('/hub'); await wait(1500)
  console.log('AFK_RESULT ' + JSON.stringify(r))
  b.quit(); op.quit(); await wait(500); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 20 * 60000)
