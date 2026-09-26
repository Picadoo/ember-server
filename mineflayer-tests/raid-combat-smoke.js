// Real-combat EmberRaid smoke (docs/ember-raid-channel-spec.md), same style as calamity-combat-smoke.js.
// 3 bots form a DungeonPlus team, enter EmberRaid, fight wave1 -> wave2 -> wave3 -> final-hall boss, kill it.
// Checks: 2-person refusal, wave callouts, clear box, 团戒 weekly first clear (once), repeat clear = no ring,
// 同袍 set (blade in hand + ring in inventory) visible + on-kill proc, non-participant gets nothing,
// optional mid-raid claim-ring exploit probe, optional week-rollover check (WEEK_ROLL=1, needs MYSQL_* env).
// env: RA/RB/RC (party; RA = leader, RC fights without a blade = set negative control), OUT (non-participant),
//      OP_BOT (operator bot), RUNS (default 2), WEEK_ROLL=1, DEOP (default on)
const mineflayer = require('mineflayer')
const { execFileSync } = require('child_process')
const HOST = '127.0.0.1', PORT = 25565
const RA = process.env.RA || 'RdA926', RB = process.env.RB || 'RdB926', RC = process.env.RC || 'RdC926'
const OUT = process.env.OUT || 'RdOut926'
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const RUNS = Number(process.env.RUNS || 2)
const PARTY = [RA, RB, RC]
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton'])
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')

function mk(name) {
  const b = mineflayer.createBot({ host: HOST, port: PORT, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = []
  b.satProcs = 0
  b.on('message', m => { const s = m.toString(); b.chatLog.push(s); console.log(`[${name}]`, s) })
  b.on('kicked', r => console.log(`[${name}] KICKED`, r))
  b.on('error', e => console.log(`[${name}] ERR`, e.message))
  b.on('entityEffect', (e, eff) => { if (b.entity && e === b.entity && eff.id === 23) { b.satProcs++; console.log(`[${name}] SATURATION proc #${b.satProcs}`) } })
  return new Promise(res => b.once('spawn', () => res(b)))
}
function niCounts(bot) {
  const out = {}
  for (const it of bot.inventory.items()) {
    let id = null
    try {
      const lore = it.nbt && it.nbt.value.display && it.nbt.value.display.value.Lore
      if (lore) for (const l of lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) { id = p; break } }
    } catch (e) {}
    const k = id || ('vanilla:' + it.name)
    out[k] = (out[k] || 0) + it.count
  }
  return out
}
function diff(a, b) {
  const d = {}
  for (const k of new Set([...Object.keys(a), ...Object.keys(b)])) { const v = (b[k] || 0) - (a[k] || 0); if (v) d[k] = v }
  return d
}
const since = (bot, n) => bot.chatLog.slice(n).join(' | ')
function mobsNear(bot, r) {
  return Object.values(bot.entities).filter(e => e && e !== bot.entity && MOBS.has(e.name) && e.isValid !== false &&
    e.position.distanceTo(bot.entity.position) < (r || 64))
}

// All party bots fight until `doneRe` shows up in the leader's chat, or timeout.
async function fightRun(op, bots, leader, doneRe, label, maxMs) {
  const t0 = Date.now(), lastTp = {}, hits = {}
  let n0 = leader.chatLog.length
  let running = true
  const loops = bots.map(async f => {
    hits[f.username] = 0
    while (running && Date.now() - t0 < maxMs) {
      const list = mobsNear(f, 80).sort((a, b) => a.position.distanceTo(f.entity.position) - b.position.distanceTo(f.entity.position))
      const e = list[0]
      if (!e) { await wait(700); continue }
      const d = e.position.distanceTo(f.entity.position)
      if (d > 3 && Date.now() - (lastTp[f.username] || 0) > 1600) {
        op.chat(`/tp ${f.username} ${(e.position.x + 1.2).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.6).toFixed(1)}`)
        lastTp[f.username] = Date.now()
      }
      if (d <= 3.5) {
        try { await f.lookAt(e.position.offset(0, e.height ? e.height * 0.8 : 1.4, 0), true) } catch (x) {}
        f.attack(e); hits[f.username]++
      }
      await wait(650)
    }
  })
  const waveSeen = {}
  while (Date.now() - t0 < maxMs) {
    const log = since(leader, n0)
    // wave-transition snapshot: mobs still alive when the next wave is announced (should be ~0 if the kill gate is per-group)
    for (const [k, re] of [['wave1_end', /左道肃清/], ['wave2_end', /通道打开/], ['wave3_end', /终厅裂开/]]) {
      if (!waveSeen[k] && re.test(log)) waveSeen[k] = { atMs: Date.now() - t0, mobsAlive: mobsNear(leader, 80).length }
    }
    if (doneRe.test(log)) break
    await wait(1000)
  }
  running = false
  await Promise.all(loops)
  const done = doneRe.test(since(leader, n0))
  console.log(`[${label}] run done=${done} ms=${Date.now() - t0} hits=${JSON.stringify(hits)}`)
  return { done, ms: Date.now() - t0, hits, waveSeen }
}

;(async () => {
  const r = { notes: [] }
  const op = await mk(OP_BOT)
  await wait(1500)
  const bots = {}
  for (const n of PARTY.concat([OUT])) { bots[n] = await mk(n); await wait(600) }
  const A = bots[RA], B = bots[RB], C = bots[RC], O = bots[OUT]
  const party = [A, B, C]
  await wait(1500)
  const c = async (s, ms) => { console.log('[cmd]', s); op.chat(s); await wait(ms || 900) }

  // --- setup (test buffs only; mobs/drop chain untouched). party bots are NOT op'd. ---
  for (const p of PARTY.concat([OUT])) {
    await c(`/clear ${p}`); await c(`/gamemode survival ${p}`)
    await c(`/mvtp ${p} ember_hub`, 1500)
    await c(`/effect ${p} clear`)
  }
  for (const p of PARTY) {
    await c(`/effect ${p} resistance 1800 4 true`); await c(`/effect ${p} regeneration 1800 4 true`)
    await c(`/effect ${p} strength 1800 9 true`)
  }
  // A, B: 余烬之刃 (NI) as weapon. C: vanilla diamond sword only (no blade -> set must stay off).
  await c(`/ni give ${RA} gear_ember_blade 1`, 1200); await c(`/ni give ${RB} gear_ember_blade 1`, 1200)
  await c(`/give ${RC} diamond_sword 1`, 1200)
  const equipWeapon = async (bot) => {
    const it = bot.inventory.items().find(i => i.name === 'iron_sword' || i.name === 'diamond_sword')
    if (it) { try { await bot.equip(it, 'hand') } catch (e) { r.notes.push('equip ' + bot.username + ' ' + e.message) } }
  }
  for (const b of party) await equipWeapon(b)

  // raid ring status before
  for (const b of party) { const n = b.chatLog.length; b.chat('/corerpg raid'); await wait(900); r['ringStatus0_' + b.username] = since(b, n) }
  let n = A.chatLog.length; A.chat('/corerpg set'); await wait(1000); r.set_before_ring_A = since(A, n)

  // --- team: A leader, B joins -> 2-person refusal ---
  A.chat('/dungeon-team disband'); await wait(600)
  A.chat('/dungeon-team create'); await wait(1200)
  A.chat(`/dungeon-team invite ${RB}`); await wait(1000)
  B.chat(`/dungeon-team request join ${RA}`); await wait(1200)
  await c(`/ni give ${RA} ticket_ember_raid 1`, 1200)
  n = A.chatLog.length; A.chat('/dp start EmberRaid'); await wait(2500)
  r.twoPlayerStart = since(A, n)
  r.check_twoPlayerRefused = /人数 3～5，当前 \(2\)/.test(r.twoPlayerStart) ? 'PASS' : 'FAIL'
  A.chat(`/dungeon-team invite ${RC}`); await wait(1000)
  C.chat(`/dungeon-team request join ${RA}`); await wait(1500)

  const outInv0 = niCounts(O), outN0 = O.chatLog.length
  const runs = []
  for (let run = 1; run <= RUNS; run++) {
    const R = { run }
    const tk0 = {}
    // DP js-condition checks the ticket on EVERY member (missing on anyone -> refused), so each gets one per run.
    for (const p of (run === 1 ? [RB, RC] : PARTY)) await c(`/ni give ${p} ticket_ember_raid 1`, 1000)
    if (run > 1) await wait(6000) // DP 5s restart cooldown
    await wait(1000)
    const inv0 = {}, chatN = {}, sat0 = {}
    for (const b of party) { inv0[b.username] = niCounts(b); chatN[b.username] = b.chatLog.length; sat0[b.username] = b.satProcs }
    R.ticketsBefore = PARTY.map(p => inv0[p].ticket_ember_raid || 0)
    A.chat('/dp start EmberRaid')
    // wait until teleported into the dungeon (enter callout)
    const tS = Date.now()
    while (Date.now() - tS < 30000 && !/团本已点燃/.test(since(A, chatN[RA]))) await wait(500)
    R.entered = /团本已点燃/.test(since(A, chatN[RA]))
    R.ticketMsg = /已扣除余烬团本票/.test(since(A, chatN[RA]))
    if (!R.entered) { R.startChat = since(A, chatN[RA]).slice(0, 500); runs.push(R); break }
    await wait(3000)
    for (const b of party) await equipWeapon(b)
    // exploit probe (run 1 only): non-op participant asks for the ring before any clear
    if (run === 1 && process.env.CLAIM_PROBE !== '0') {
      const bn = B.chatLog.length, bInv = niCounts(B)
      B.chat('/corerpg raid claim-ring'); await wait(1500)
      R.claimProbe = { chat: since(B, bn), delta: diff(bInv, niCounts(B)) }
    }
    const fr = await fightRun(op, party, A, /余烬团本 通关|使徒倒下/, 'RUN' + run, 15 * 60000)
    R.fight = fr
    await wait(9000) // reward script + mvtp hub
    const log = since(A, chatN[RA])
    R.callouts = {
      enter: /团本已点燃/.test(log), left: /【左道】/.test(log), right: /【右道】/.test(log),
      merge: /【汇合】/.test(log), hall: /【终厅】使徒/.test(log), clear: /余烬团本 通关/.test(log)
    }
    for (const b of party) {
      R['delta_' + b.username] = diff(inv0[b.username], niCounts(b))
      R['ringChat_' + b.username] = (since(b, chatN[b.username]).match(/[^|]*(团戒|团本之戒)[^|]*/g) || []).map(s => s.trim())
      R['satProcs_' + b.username] = b.satProcs - sat0[b.username]
    }
    if (!fr.done) R.tailChat = log.slice(-800)
    runs.push(R)
    if (!fr.done) break
    // set status after this run
    for (const b of party) { const k = b.chatLog.length; b.chat('/corerpg set'); await wait(1000); R['set_' + b.username] = since(b, k) }
  }
  r.runs = runs
  r.outDelta = diff(outInv0, niCounts(O))
  r.outRingChat = (since(O, outN0).match(/[^|]*(团戒|团本之戒|同袍之证)[^|]*/g) || [])

  // --- weekly checks ---
  const r1 = runs[0] || {}, r2 = runs[1] || {}
  r.check_run1_clear = r1.fight && r1.fight.done ? 'PASS' : 'FAIL'
  r.check_run1_ring_each = PARTY.every(p => ((r1['delta_' + p] || {}).acc_ember_raid_ring || 0) === 1) ? 'PASS' : 'FAIL'
  r.check_run1_no_forced_T3 = PARTY.every(p => !((r1['delta_' + p] || {}).gear_ember_t3_blade)) ? 'PASS(no guaranteed T3)' : 'INFO(T3 rolled)'
  if (RUNS >= 2) {
    r.check_run2_clear = r2.fight && r2.fight.done ? 'PASS' : 'FAIL'
    r.check_run2_no_ring = r2.fight && r2.fight.done && PARTY.every(p => !((r2['delta_' + p] || {}).acc_ember_raid_ring)) ? 'PASS' : 'FAIL'
    r.check_run2_deny_msg = PARTY.every(p => (r2['ringChat_' + p] || []).some(s => /本周团戒已领取/.test(s))) ? 'PASS' : 'FAIL'
    r.check_set_proc_A_or_B = ((r2['satProcs_' + RA] || 0) + (r2['satProcs_' + RB] || 0)) > 0 ? 'PASS' : 'FAIL'
    r.check_set_no_proc_C = (r2['satProcs_' + RC] || 0) === 0 ? 'PASS' : 'FAIL'
  }
  r.check_set_no_proc_run1 = PARTY.every(p => (r1['satProcs_' + p] || 0) === 0) ? 'PASS' : 'FAIL'
  r.check_set_active_A = /已激活/.test(r1['set_' + RA] || '') ? 'PASS' : 'FAIL'
  r.check_set_inactive_C = /未激活/.test(r1['set_' + RC] || '') ? 'PASS' : 'FAIL'
  r.check_nonparticipant_nothing = Object.keys(r.outDelta).length === 0 && r.outRingChat.length === 0 ? 'PASS' : 'FAIL'
  if (r1.claimProbe) r.check_no_midraid_claim = (r1.claimProbe.delta.acc_ember_raid_ring || 0) === 0 ? 'PASS' : 'FAIL(ring claimable before clear)'

  // --- week rollover: A offline -> raidRingWeek set to last week in MySQL -> rejoin -> grant-ring must pay out ---
  if (process.env.WEEK_ROLL === '1') {
    A.quit(); await wait(3000)
    const sql = `UPDATE cr_players SET data = REGEXP_REPLACE(data, 'raidRingWeek: [^\\n]*', 'raidRingWeek: ''2000-W01''') WHERE name='${RA}'`
    try {
      execFileSync('mysql', ['-h', process.env.MYSQL_HOST || '127.0.0.1', '-u', process.env.MYSQL_USER, '-p' + process.env.MYSQL_PASSWORD, 'ember', '-e', sql])
      const A2 = await mk(RA); await wait(2000)
      const i0 = niCounts(A2)
      await c(`/corerpg raid grant-ring ${RA}`, 2000)
      r.weekRoll_delta = diff(i0, niCounts(A2))
      await c(`/corerpg raid grant-ring ${RA}`, 2000)
      r.weekRoll_delta2 = diff(i0, niCounts(A2))
      r.check_week_rollover = (r.weekRoll_delta.acc_ember_raid_ring || 0) === 1 && (r.weekRoll_delta2.acc_ember_raid_ring || 0) === 1 ? 'PASS' : 'FAIL'
    } catch (e) { r.check_week_rollover = 'ERROR ' + e.message.split('\n')[0] }
  }

  A.chat('/dungeon-team disband'); await wait(800)
  if (process.env.DEOP !== '0') for (const p of PARTY.concat([OUT])) await c(`/deop ${p}`, 500)
  console.log('RAID_RESULT', JSON.stringify(r, null, 2))
  await wait(500)
  process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
