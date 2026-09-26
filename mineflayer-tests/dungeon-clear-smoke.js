// Generic real-combat DungeonPlus clear smoke (style of raid-combat-smoke.js).
// Party bots start in `world` (NOT the hub) so arriving in ember_hub afterwards proves the reward-script mvtp ran.
// Checks: ticket spent per member, clear reached, expected clear-box NI items per member, return to ember_hub.
// env: DUNGEON (EmberDaily|EmberWeekly|EmberAbyss|EmberCalamity|EmberGuildBoss), PARTY="A,B", OP_BOT,
//      TICKET (NI id, empty = no ticket), EXPECT="id:n,id:n" (clear box per member), DONE (regex of the $end text),
//      MAX_MS (fight timeout), HUB_CHECK (default 1)
const mineflayer = require('mineflayer')
const HOST = '127.0.0.1', PORT = 25565
const DUNGEON = process.env.DUNGEON || 'EmberDaily'
const PARTY = (process.env.PARTY || 'DcA926,DcB926').split(',')
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const TICKET = process.env.TICKET === undefined ? 'ticket_ember_daily' : process.env.TICKET
const EXPECT = (process.env.EXPECT || '').split(',').filter(Boolean).map(s => { const [k, v] = s.split(':'); return [k, Number(v)] })
const DONE = new RegExp(process.env.DONE || '通关')
const MAX_MS = Number(process.env.MAX_MS || 10 * 60000)
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager', 'pig_zombie', 'vindication_illager'])
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')

function mk(name) {
  const b = mineflayer.createBot({ host: HOST, port: PORT, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = []
  b.on('message', m => { const s = m.toString(); b.chatLog.push(s); console.log(`[${name}]`, s) })
  b.on('kicked', r => console.log(`[${name}] KICKED`, r))
  b.on('error', e => console.log(`[${name}] ERR`, e.message))
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

;(async () => {
  const r = { dungeon: DUNGEON, party: PARTY, notes: [] }
  const op = await mk(OP_BOT)
  await wait(1500)
  const bots = []
  for (const n of PARTY) { bots.push(await mk(n)); await wait(600) }
  const L = bots[0]
  await wait(1500)
  const c = async (s, ms) => { console.log('[cmd]', s); op.chat(s); await wait(ms || 900) }

  // op waits at the hub spawn: seeing a party bot there after the clear = mvtp ember_hub ran
  await c(`/mvtp ${OP_BOT} ember_hub`, 2500)
  const hubPos = op.entity.position.clone()
  for (const p of PARTY) {
    await c(`/clear ${p}`); await c(`/gamemode survival ${p}`); await c(`/effect ${p} clear`)
    await c(`/mvtp ${p} world`, 1500)
    await c(`/effect ${p} resistance 1800 4 true`); await c(`/effect ${p} regeneration 1800 4 true`)
    await c(`/effect ${p} strength 1800 9 true`)
    await c(`/give ${p} diamond_sword 1`)
    if (TICKET) await c(`/ni give ${p} ${TICKET} 1`, 1100)
  }
  await wait(1500)
  for (const b of bots) {
    const it = b.inventory.items().find(i => i.name === 'diamond_sword')
    if (it) { try { await b.equip(it, 'hand') } catch (e) {} }
  }
  r.inHubBefore = PARTY.filter(p => op.players[p] && op.players[p].entity && op.players[p].entity.position.distanceTo(hubPos) < 30)

  // team
  if (bots.length > 1) {
    L.chat('/dungeon-team disband'); await wait(500)
    L.chat('/dungeon-team create'); await wait(1000)
    for (const b of bots.slice(1)) {
      L.chat(`/dungeon-team invite ${b.username}`); await wait(900)
      b.chat(`/dungeon-team request join ${L.username}`); await wait(1200)
    }
  }
  const inv0 = {}, chatN = {}
  for (const b of bots) { inv0[b.username] = niCounts(b); chatN[b.username] = b.chatLog.length }
  const opN = op.chatLog.length
  L.chat(`/dp start ${DUNGEON}`)
  const tS = Date.now()
  let entered = false
  while (Date.now() - tS < 30000) {
    await wait(1000)
    if (mobsNear(L, 80).length > 0 || /开始|已开启|点燃|降临|第 .?1.? 层/.test(since(L, chatN[L.username]))) { entered = true; break }
  }
  r.entered = entered
  r.startChat = since(L, chatN[L.username]).slice(0, 400)
  if (!entered) { console.log('DUNGEON_RESULT', JSON.stringify(r, null, 2)); process.exit(0) }

  // fight
  const t0 = Date.now(), lastTp = {}, hits = {}
  let running = true
  const loops = bots.map(async f => {
    hits[f.username] = 0
    while (running && Date.now() - t0 < MAX_MS) {
      const e = mobsNear(f, 80).sort((a, b) => a.position.distanceTo(f.entity.position) - b.position.distanceTo(f.entity.position))[0]
      if (!e) { await wait(700); continue }
      const d = e.position.distanceTo(f.entity.position)
      if (d > 3 && Date.now() - (lastTp[f.username] || 0) > 1600) {
        op.chat(`/tp ${f.username} ${(e.position.x + 1.2).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.6).toFixed(1)}`)
        lastTp[f.username] = Date.now()
      }
      if (d <= 3.5) { try { await f.lookAt(e.position.offset(0, (e.height || 1.8) * 0.8, 0), true) } catch (x) {} f.attack(e); hits[f.username]++ }
      await wait(650)
    }
  })
  while (Date.now() - t0 < MAX_MS && !DONE.test(since(L, chatN[L.username]))) await wait(1000)
  running = false
  await Promise.all(loops)
  r.fight = { done: DONE.test(since(L, chatN[L.username])), ms: Date.now() - t0, hits }
  await wait(10000) // $end delay + reward script + mvtp

  for (const b of bots) {
    const d = diff(inv0[b.username], niCounts(b))
    r['delta_' + b.username] = d
    r['ticketSpent_' + b.username] = TICKET ? (d[TICKET] === -1) : 'n/a'
    r['boxOk_' + b.username] = EXPECT.every(([k, v]) => (d[k] || 0) >= v)
    r['inHub_' + b.username] = !!(op.players[b.username] && op.players[b.username].entity &&
      op.players[b.username].entity.position.distanceTo(hubPos) < 30)
  }
  r.leaderLog = since(L, chatN[L.username]).split(' | ').filter(s => !/Teleported|advancement|无法对队友/.test(s)).join(' | ').slice(0, 1500)
  r.check_clear = r.fight.done ? 'PASS' : 'FAIL'
  r.check_ticket = TICKET ? (PARTY.every(p => r['ticketSpent_' + p] === true) ? 'PASS' : 'FAIL') : 'n/a'
  r.check_box = EXPECT.length ? (PARTY.every(p => r['boxOk_' + p]) ? 'PASS' : 'FAIL') : 'n/a'
  r.check_hub = process.env.HUB_CHECK === '0' ? 'n/a' : (r.inHubBefore.length === 0 && PARTY.every(p => r['inHub_' + p]) ? 'PASS' : 'FAIL')
  if (bots.length > 1) { L.chat('/dungeon-team disband'); await wait(600) }
  console.log('DUNGEON_RESULT', JSON.stringify(r, null, 2))
  await wait(500)
  process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
