// Real-combat balance harness (2026-09-27 CST, CoreRpg 1.9.0 stat layer).
// Fresh NON-OP bots get their gate level (/corerpg progress raid_clear via the op bot), the tier gear the mainline hands out,
// enhance levels via the real /corerpg enhance (op-given shards), Sharpness via /enchant, then fight ON FOOT (pathfinder,
// attack at ~full charge every 650 ms). Logs HP min/end, deaths, per-wave times and boss TTK.
// env: D=weekly|abyss|calamity|guild|raid  N (players)  LV  BLADE (ni id)  TAL (ni id)  EB/ET (enhance levels)  SHARP
const { joinPlay, ensureLevel } = require('./lib/proxy-login')
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const ALL = {
  weekly: { n: 1, lv: 20, blade: 'gear_ember_t1_blade', tal: 'gear_ember_charm', eb: 2, et: 1, ticket: 'ticket_ember_weekly', dp: 'EmberWeekly',
    boss: /【深核·终局】/, done: /深核·周 通关/, waves: [/深核·前厅/, /深核·中核/, /深核·深室/, /深核·终局/] },
  abyss: { n: 1, lv: 25, blade: 'gear_ember_t1_blade', tal: 'gear_ember_t1_talisman', eb: 3, et: 2, ticket: 'ticket_ember_abyss', dp: 'EmberAbyss',
    boss: /第 8 层 ——/, done: /余烬深渊 顶层通关/, waves: [1, 2, 3, 4, 5, 6, 7, 8].map(i => new RegExp('第 ' + i + ' 层 ——')) },
  calamity: { n: 1, lv: 30, blade: 'gear_ember_t2_blade', tal: 'gear_ember_t1_talisman', eb: 3, et: 2 },
  guild: { n: 2, lv: 30, blade: 'gear_ember_t2_blade', tal: 'gear_ember_t1_talisman', eb: 3, et: 2, dp: 'EmberGuildBoss',
    boss: /终局：余烬灾厄使/, done: /余烬盟约 Boss 通关/, waves: [/波次 1/, /精英：/, /终局：/] },
  raid: { n: 3, lv: 35, blade: 'gear_ember_t2_blade', tal: 'gear_ember_t2_talisman', eb: 4, et: 3, ticket: 'ticket_ember_raid', dp: 'EmberRaid',
    boss: /【终厅】使徒/, done: /余烬团本 通关/, waves: [/【左道】/, /【右道】/, /【汇合】/, /【终厅】/] },
}
const SHARP = Number(process.env.SHARP != null ? process.env.SHARP : 2)
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton'])
const since = (b, n) => b.chatLog.slice(n).join(' | ')
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const find = (b, id) => b.inventory.items().find(i => niId(i) === id)

async function fighter(b, st, BLADE) {
  st.minHp = 99; st.deaths = 0; st.hits = 0; st.tl = []; let dead = false; const T0 = Date.now()
  b.on('health', () => { if (b.health > 0) { st.minHp = Math.min(st.minHp, b.health); dead = false } else if (!dead) { dead = true; st.deaths++ } })
  b.on('death', () => { if (!dead) { dead = true; st.deaths++ } })
  let lastHp = 20; b.on('health', () => { if (b.health > 0) lastHp = b.health })
  // world changes also fire 'respawn'; only count it as a death when HP was already near zero
  b.on('respawn', () => { if (!dead && lastHp <= 6) st.deaths++; dead = false; lastHp = 20 })
  const iv = setInterval(() => { if (st.running) st.tl.push(Math.round(b.health || 0)) }, 2000)
  while (st.running) {
    if (!b.entity || b.health <= 0) { await wait(800); continue }
    const bl = find(b, BLADE); if (bl && (!b.heldItem || b.heldItem.slot !== bl.slot)) { try { await b.equip(bl, 'hand') } catch (e) {} }
    const e = Object.values(b.entities).filter(x => x && x !== b.entity && MOBS.has(x.name) && x.position.distanceTo(b.entity.position) < 40)
      .sort((x, y) => x.position.distanceTo(b.entity.position) - y.position.distanceTo(b.entity.position))[0]
    if (!e) { b.pathfinder.setGoal(null); await wait(500); continue }
    const d = e.position.distanceTo(b.entity.position)
    if (d > 2.8) { b.pathfinder.setGoal(new goals.GoalFollow(e, 1.5), true); await wait(250); continue }
    b.pathfinder.setGoal(null)
    try { await b.lookAt(e.position.offset(0, (e.height || 1.8) * 0.85, 0), true) } catch (x) {}
    b.attack(e); st.hits++
    await wait(650)
  }
  clearInterval(iv); b.pathfinder.setGoal(null)
}

let opQ = Promise.resolve()
async function runOne(D, op) {
  const CFG = ALL[D]
  const N = CFG.n, LV = CFG.lv, BLADE = CFG.blade, TAL = CFG.tal, EB = CFG.eb, ET = CFG.et
  const tag = D.slice(0, 2).toUpperCase() + Math.floor(Math.random() * 9000 + 1000)
  const names = Array.from({ length: N }, (_, i) => `Bal${tag}${'abcde'[i]}`)
  const r = { D, N, LV, BLADE, TAL, EB, ET, SHARP, names }
  const c = (s, ms) => { opQ = opQ.then(async () => { op.chat(s); await wait(ms || 700) }); return opQ }
  const bots = []
  for (const n of names) {
    const b = await joinPlay(n, { log: false }); b.loadPlugin(pathfinder); bots.push(b); await wait(800)
    b.on('message', m => { const s = m.toString(); if (process.env.VERBOSE) console.log(`[${n}]`, s) })
  }
  await wait(3000)
  for (const b of bots) {
    const mv = new Movements(b); mv.canDig = false; mv.allow1by1towers = false; mv.scafoldingBlocks = []; b.pathfinder.setMovements(mv)
    await c(`/clear ${b.username}`); await c(`/mvtp ${b.username} ember_hub`, 1500); await c(`/effect ${b.username} clear`)
    await ensureLevel(op, b, LV)
    await c(`/clear ${b.username}`)
    await c(`/ni give ${b.username} ${BLADE} 1`, 1000); await c(`/ni give ${b.username} ${TAL} 1`, 1000)
    await c(`/ni give ${b.username} mat_ember_shard 64`, 800); await c(`/ni give ${b.username} mat_ember_bone_dust 20`, 800)
    for (const [id, lv] of [[TAL, ET], [BLADE, EB]]) {
      for (let i = 0; i < 12; i++) {
        const it = find(b, id); if (!it) break
        try { await b.equip(it, 'hand') } catch (e) {}
        await wait(300); const n0 = b.chatLog.length; b.chat('/corerpg enhance info'); await wait(900)
        const m = strip(since(b, n0)).match(/当前 \+(\d+)/); const cur = m ? Number(m[1]) : 0
        if (cur >= lv) break
        b.chat('/corerpg enhance'); await wait(900)
      }
    }
    const bl = find(b, BLADE); if (bl) { await b.equip(bl, 'hand'); await wait(300) }
    if (SHARP > 0) await c(`/enchant ${b.username} sharpness ${SHARP}`)
    await c(`/clear ${b.username} minecraft:quartz`); // noop safety
    const n0 = b.chatLog.length; b.chat('/corerpg stats'); await wait(1200); r['stats_' + b.username] = strip(since(b, n0)).slice(0, 200)
    await c(`/effect ${b.username} saturation 1 20 true`); await c(`/effect ${b.username} instant_health 1 3 true`)
  }
  const L = bots[0]
  if (N > 1 && CFG.dp) {
    L.chat('/dungeon-team disband'); await wait(600); L.chat('/dungeon-team create'); await wait(1200)
    for (const b of bots.slice(1)) { L.chat(`/dungeon-team invite ${b.username}`); await wait(900); b.chat(`/dungeon-team request join ${L.username}`); await wait(1200) }
  }
  const sts = bots.map(() => ({ running: true }))
  const t0 = Date.now(); const n0 = L.chatLog.length
  let bossEntity = null, bossSeenAt = null, bossDeadAt = null
  if (D === 'calamity') {
    for (const b of bots) { await c(`/mvtp ${b.username} ember_event`, 1500); await c(`/tp ${b.username} -96.5 64 262.5`, 1000) }
    await c('/mm m spawn EmberCalamityBoss 1 ember_event,-96.5,64,268.5', 1000)
    L.on('entityDead', e => { if (e.name === 'wither_skeleton' && !bossDeadAt) bossDeadAt = Date.now() })
    L.on('entityGone', e => { if (e.name === 'wither_skeleton' && !bossDeadAt && bossSeenAt) bossDeadAt = Date.now() })
  } else if (D === 'guild') {
    await c(`/corerpg coin give ${L.username} 6000`); await c(`/ni give ${L.username} mat_ember_shard 200`, 900)
    L.chat(`/corerpg guild create G${tag}`); await wait(1500)
    for (const b of bots.slice(1)) { L.chat(`/corerpg guild invite ${b.username}`); await wait(900); b.chat('/corerpg guild accept'); await wait(900) }
    L.chat('/corerpg guild donate mat_ember_shard 200'); await wait(1200)
    const k = L.chatLog.length; L.chat('/corerpg guild boss'); await wait(4000); r.guildBoss = strip(since(L, k)).slice(0, 300)
  } else {
    for (const b of bots) await c(`/ni give ${b.username} ${CFG.ticket} 1`, 800)
    await wait(1000); L.chat(`/dp start ${CFG.dp}`)
  }
  const fights = bots.map((b, i) => fighter(b, sts[i], BLADE))
  const waveT = {}; let bossStart = null, doneAt = null, endHp = null
  const maxMs = Number(process.env.MAXMS || 10 * 60000)
  while (Date.now() - t0 < maxMs) {
    const log = strip(since(L, n0))
    if (CFG.waves) CFG.waves.forEach((re, i) => { if (waveT[i] == null && re.test(log)) waveT[i] = Math.round((Date.now() - t0) / 1000) })
    if (CFG.boss && !bossStart && CFG.boss.test(log)) bossStart = Date.now()
    if (D === 'calamity') {
      const be = Object.values(L.entities).find(e => e.name === 'wither_skeleton')
      if (be && !bossSeenAt) { bossSeenAt = Date.now(); bossStart = bossSeenAt }
      if (bossDeadAt) { doneAt = bossDeadAt; break }
    } else if (CFG.done.test(log)) { doneAt = Date.now(); break }
    if (bots.every((b, i) => sts[i].deaths > 0) || /挑战失败/.test(log)) break
    await wait(250)
  }
  endHp = bots.map(b => +(b.health || 0).toFixed(1))
  const maxHp = bots.map((b, i) => { const m = (r['stats_' + b.username] || '').match(/生命 ([\d.]+)/); return m ? Number(m[1]) : 20 })
  sts.forEach(s => { s.running = false }); await Promise.all(fights)
  r.cleared = !!doneAt
  r.totalSec = Math.round(((doneAt || Date.now()) - t0) / 1000)
  r.bossTTK = bossStart && doneAt ? Math.round((doneAt - bossStart) / 1000) : null
  r.waveStartSec = waveT
  r.note = 'hp values are client-side (health scale caps the display at 40)'
  r.players = bots.map((b, i) => ({ name: b.username, maxHp: maxHp[i], endHp: endHp[i], endPct: Math.round(100 * endHp[i] / Math.min(40, maxHp[i])), minHp: sts[i].minHp, deaths: sts[i].deaths, hits: sts[i].hits, hp2s: sts[i].tl.join(',') }))
  if (!doneAt) r.tail = strip(since(L, n0)).slice(-600)
  console.log('BALANCE_RESULT', JSON.stringify(r))
  if (D === 'calamity' && !doneAt) { await c('/mvtp RpgBot ember_event', 1500); await c('/minecraft:kill @e[type=wither_skeleton]', 800); await c('/mvtp RpgBot ember_hub', 1000) }
  for (const b of bots) await c(`/mvtp ${b.username} ember_hub`, 600)
  await wait(1000); for (const b of bots) b.quit()
  return r
}
;(async () => {
  const op = await joinPlay('RpgBot', { log: false })
  const list = (process.env.D || 'weekly').split(',')
  await Promise.all(list.map((d, i) => wait(i * 4000).then(() => runOne(d, op).catch(e => console.log('ERR', d, e.message)))))
  await wait(1500); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 20 * 60000)
