// (1) Gate: non-op party's bare `/dp start EmberCalamity` must be refused (OP-only js-condition);
//     also records what `/dp start EmberGuildBoss` does for a non-op (GB_PROBE=1).
// (2) MM chance drops: spawn MM mobs next to a non-op bot, kill them, count NI drops from the
//     formerly-dead "~onDeath >0 X" lines (now plain chance). env: MOBS="EmberRaidFootman:8,EmberAbyssZombie:8"
const mineflayer = require('mineflayer')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const A_NAME = process.env.A || 'GtA926', B_NAME = process.env.B || 'GtB926'
const MOBS = (process.env.MOBS || 'EmberRaidFootman:8,EmberAbyssZombie:8').split(',').map(s => s.split(':'))
function mk(name) {
  const b = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = []; b.on('message', m => { const s = m.toString(); b.chatLog.push(s); console.log(`[${name}]`, s) })
  b.on('kicked', r => console.log(`[${name}] KICKED`, r)); b.on('error', e => console.log(`[${name}] ERR`, e.message))
  return new Promise(res => b.once('spawn', () => res(b)))
}
function niCounts(bot) {
  const out = {}
  for (const it of bot.inventory.items()) {
    let id = null
    try { const lore = it.nbt && it.nbt.value.display && it.nbt.value.display.value.Lore
      if (lore) for (const l of lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) { id = p; break } } } catch (e) {}
    const k = id || ('vanilla:' + it.name); out[k] = (out[k] || 0) + it.count
  }
  return out
}
function diff(a, b) { const d = {}; for (const k of new Set([...Object.keys(a), ...Object.keys(b)])) { const v = (b[k] || 0) - (a[k] || 0); if (v) d[k] = v } return d }
const since = (b, n) => b.chatLog.slice(n).join(' | ')
;(async () => {
  const r = {}
  const op = await mk(OP_BOT); await wait(1200)
  const A = await mk(A_NAME); const B = await mk(B_NAME); await wait(1500)
  const c = async (s, ms) => { console.log('[cmd]', s); op.chat(s); await wait(ms || 900) }
  for (const p of [A_NAME, B_NAME]) { await c(`/deop ${p}`, 500); await c(`/mvtp ${p} world`, 1500) }
  // ---- (1) gate ----
  A.chat('/dungeon-team disband'); await wait(500)
  A.chat('/dungeon-team create'); await wait(900); A.chat(`/dungeon-team invite ${B_NAME}`); await wait(900)
  B.chat(`/dungeon-team request join ${A_NAME}`); await wait(1200)
  let n = A.chatLog.length; A.chat('/dp start EmberCalamity'); await wait(4000)
  r.calamity_nonop = since(A, n)
  r.check_calamity_nonop_refused = /仅供 OP/.test(r.calamity_nonop) && !/灾厄降临时刻/.test(r.calamity_nonop) ? 'PASS' : 'FAIL'
  if (process.env.GB_PROBE === '1') {
    n = A.chatLog.length; A.chat('/dp start EmberGuildBoss'); await wait(4000)
    r.guildboss_nonop = since(A, n)
    if (/已点燃/.test(r.guildboss_nonop)) { A.chat('/dp leave'); B.chat('/dp leave'); await wait(3000) }
  }
  A.chat('/dungeon-team disband'); await wait(800)
  // ---- (2) MM chance drops ----
  await c(`/effect ${A_NAME} resistance 600 4 true`); await c(`/effect ${A_NAME} regeneration 600 4 true`)
  await c(`/effect ${A_NAME} strength 600 9 true`); await c(`/give ${A_NAME} diamond_sword 1`, 1200)
  const sw = A.inventory.items().find(i => i.name === 'diamond_sword'); if (sw) await A.equip(sw, 'hand')
  const inv0 = niCounts(A), an = A.chatLog.length
  const p = A.entity.position
  let kills = 0
  for (const [mob, cnt] of MOBS) {
    await c(`/mm m spawn ${mob} ${cnt} world,${(p.x + 3).toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`, 1500)
    const t0 = Date.now()
    while (Date.now() - t0 < 90000) {
      const es = Object.values(A.entities).filter(e => e !== A.entity && ['zombie', 'skeleton', 'husk'].includes(e.name) && e.position.distanceTo(A.entity.position) < 12)
      if (!es.length) break
      const e = es.sort((a, b) => a.position.distanceTo(A.entity.position) - b.position.distanceTo(A.entity.position))[0]
      if (e.position.distanceTo(A.entity.position) > 3) { op.chat(`/tp ${A_NAME} ${(e.position.x + 1).toFixed(1)} ${e.position.y.toFixed(1)} ${e.position.z.toFixed(1)}`); await wait(700) }
      try { await A.lookAt(e.position.offset(0, 1.5, 0), true) } catch (x) {}
      A.attack(e); await wait(600)
    }
    kills += Number(cnt)
  }
  await wait(3000)
  r.mm_spawned = MOBS.map(x => x.join('×')).join(', ')
  r.mm_delta = diff(inv0, niCounts(A))
  r.mm_chat = (since(A, an).match(/NeigeItems > 你得到了[^|]*/g) || [])
  r.check_chance_drop_fired = (r.mm_delta.mat_ember_shard || 0) > 0 ? 'PASS' : 'FAIL'
  console.log('GATE_DROPS_RESULT', JSON.stringify(r, null, 2))
  await wait(500); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
