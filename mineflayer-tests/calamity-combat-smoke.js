// Real-combat Calamity window test: kill EmberCalamityBoss as a player, verify A drops / daily chest B / daily limit / no 2nd spawn.
const mineflayer = require('mineflayer')
const HOST = '127.0.0.1', PORT = 25565
const FIGHTER = process.env.FIGHTER || 'CalBotA'
const WATCHER = process.env.WATCHER || 'CalWatch'
const BX = -96.5, BY = 64, BZ = 266.5
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
function bosses(bot) {
  return Object.values(bot.entities).filter(e => e && e.name === 'wither_skeleton' && e.position.distanceTo(bot.entity.position) < 64)
}

async function fight(op, f, label) {
  const t0 = Date.now()
  let boss = null
  while (Date.now() - t0 < 20000 && !(boss = bosses(f)[0])) await wait(500)
  if (!boss) return { killed: false, reason: 'no boss seen' }
  const id = boss.id
  let hits = 0, lastTp = 0
  while (Date.now() - t0 < 240000) {
    const e = f.entities[id]
    if (!e || !e.isValid) break
    const d = e.position.distanceTo(f.entity.position)
    if (d > 3 && Date.now() - lastTp > 1500) {
      op.chat(`/tp ${f.username} ${(e.position.x + 1.5).toFixed(1)} ${(e.position.y).toFixed(1)} ${(e.position.z).toFixed(1)}`)
      lastTp = Date.now()
    }
    if (d <= 3.5) {
      await f.lookAt(e.position.offset(0, 1.6, 0), true)
      f.attack(e); hits++
    }
    await wait(650)
  }
  const gone = !f.entities[id] || !f.entities[id].isValid
  console.log(`[${label}] fight done killed=${gone} hits=${hits} ms=${Date.now() - t0}`)
  return { killed: gone, hits, ms: Date.now() - t0 }
}

;(async () => {
  const r = { notes: [] }
  const op = await mk('RpgBot')
  await wait(1500)
  const f = await mk(FIGHTER)
  const w = await mk(WATCHER)
  await wait(1500)
  const c = async (s, ms) => { console.log('[cmd]', s); op.chat(s); await wait(ms || 900) }
  await c(`/op ${FIGHTER}`)
  await c('/corerpg calamity forceend', 1500)
  for (const p of [FIGHTER, WATCHER]) {
    await c(`/clear ${p}`); await c(`/gamemode survival ${p}`)
    await c(`/mvtp ${p} ember_event`, 2000)
    await c(`/effect ${p} resistance 900 4 true`); await c(`/effect ${p} regeneration 900 4 true`); await c(`/effect ${p} saturation 900 4 true`)
  }
  await c(`/effect ${FIGHTER} strength 900 9 true`)
  await c(`/give ${FIGHTER} diamond_sword 1`)
  await c(`/tp ${FIGHTER} ${BX} ${BY + 1} ${BZ - 4}`)
  await c(`/tp ${WATCHER} ${BX} ${BY + 1} ${BZ - 22}`, 2500)
  await c('/mm mobs kill EmberCalamityBoss', 1500)
  r.fighterPos = f.entity.position.toString(); r.watcherPos = w.entity.position.toString()
  r.preexistingBosses = bosses(f).length
  f.setQuickBarSlot(0)
  const sword = f.inventory.items().find(i => i.name === 'diamond_sword'); if (sword) await f.equip(sword, 'hand')

  // ---- Window 1 ----
  let fn = f.chatLog.length, wn = w.chatLog.length
  let invF0 = niCounts(f), invW0 = niCounts(w)
  let n = op.chatLog.length
  await c('/corerpg calamity forceopen', 3000)
  r.w1_open = /强制开启|降临/.test(since(op, n)) ? 'PASS' : 'FAIL'
  r.w1_bossCount = bosses(f).length
  if (process.env.ASSIST) {
    const b0 = bosses(f)[0]
    if (b0) {
      await c(`/tp ${WATCHER} ${(b0.position.x - 1.5).toFixed(1)} ${b0.position.y.toFixed(1)} ${b0.position.z.toFixed(1)}`, 1500)
      const e = w.entities[b0.id]
      if (e) { for (let i = 0; i < 3; i++) { await w.lookAt(e.position.offset(0, 1.6, 0), true); w.attack(e); await wait(700) } r.assistHits = 3 }
      await c(`/tp ${WATCHER} ${BX} ${BY + 1} ${BZ - 22}`, 800)
    }
  }
  const k1 = await fight(op, f, 'W1')
  r.w1_kill = k1
  await wait(4000)
  r.w1_fighterDelta = diff(invF0, niCounts(f)); r.w1_watcherDelta = diff(invW0, niCounts(w))
  r.w1_fighterChat = since(f, fn).slice(0, 600); r.w1_watcherChat = since(w, wn).slice(0, 600)

  // same-window: no second spawn
  n = op.chatLog.length
  await c('/corerpg calamity forceopen', 4000)
  r.w1_second_forceopen_msg = since(op, n)
  r.w1_bossesAfterSecondForceopen = bosses(f).length
  n = op.chatLog.length
  await c('/corerpg calamity', 1200)
  r.w1_status = since(op, n)
  n = op.chatLog.length
  await c('/corerpg calamity forceend', 1500)
  r.w1_end = since(op, n)
  n = op.chatLog.length
  await c('/corerpg calamity', 1200)
  r.w1_status_after_end = since(op, n)
  fn = f.chatLog.length
  f.chat('/corerpg calamity'); await wait(1200)
  r.w1_fighter_status = since(f, fn)

  // ---- Window 2 (same day) ----
  await c(`/tp ${FIGHTER} ${BX} ${BY + 1} ${BZ - 4}`, 1500)
  fn = f.chatLog.length; wn = w.chatLog.length
  invF0 = niCounts(f); invW0 = niCounts(w)
  n = op.chatLog.length
  await c('/corerpg calamity forceopen', 3000)
  r.w2_open = since(op, n)
  r.w2_bossCount = bosses(f).length
  const k2 = await fight(op, f, 'W2')
  r.w2_kill = k2
  await wait(4000)
  r.w2_fighterDelta = diff(invF0, niCounts(f)); r.w2_watcherDelta = diff(invW0, niCounts(w))
  r.w2_fighterChat = since(f, fn).slice(0, 600); r.w2_watcherChat = since(w, wn).slice(0, 600)
  n = op.chatLog.length
  await c('/corerpg calamity forceend', 1500)
  r.w2_end = since(op, n)
  n = op.chatLog.length
  await c('/corerpg calamity', 1200)
  r.w2_status_after_end = since(op, n)
  r.finalBosses = bosses(f).length

  console.log('COMBAT_RESULT', JSON.stringify(r, null, 2))
  await wait(500)
  process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
