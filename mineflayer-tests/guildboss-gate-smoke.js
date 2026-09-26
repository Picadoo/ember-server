// CoreRpg 1.4.9 guild boss gate smoke (EmberGuildBoss).
// L (non-op, DP leader, guild member) + M (non-op teammate). OP_BOT (RpgBot) = guild leader + command issuer + hub observer.
// Checks: (a) bare `/dp start EmberGuildBoss` refused for non-op team; (b) `/corerpg guild boss` with 19 contribution refused,
// nothing spent; (c) with 20: contribution spent, dungeon starts via console, both members enter, clear, reward box once per
// member, return to ember_hub; (d) a second bare /dp start after the run is still refused (pass is one-time).
const mineflayer = require('mineflayer')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const L_NAME = process.env.L || 'GbL926', M_NAME = process.env.M || 'GbM926'
const MAX_MS = Number(process.env.MAX_MS || 8 * 60000)
const DONE = /盟约周 Boss 通关奖励已发放/
const BOX = { mat_ember_shard: 8, cosmetic_calamity_shard: 1, mat_ember_core_fragment: 1, gem_ember_sharp: 1, mat_ember_bone_dust: 4 }
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager', 'pig_zombie', 'vindication_illager', 'iron_golem', 'blaze'])
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
const mobsNear = (bot, r) => Object.values(bot.entities).filter(e => e && e !== bot.entity && MOBS.has(e.name) && e.position.distanceTo(bot.entity.position) < r)
async function contrib(bot) {
  const n = bot.chatLog.length; bot.chat('/corerpg guild info'); await wait(1200)
  const m = strip(since(bot, n)).match(/我的贡献\s*(\d+)/); return m ? Number(m[1]) : null
}
;(async () => {
  const r = {}
  const op = await mk(OP_BOT); await wait(1200)
  const L = await mk(L_NAME); await wait(600); const M = await mk(M_NAME); await wait(1500)
  const c = async (s, ms) => { console.log('[cmd]', s); op.chat(s); await wait(ms || 900) }
  await c(`/mvtp ${OP_BOT} ember_hub`, 2500)
  const hubPos = op.entity.position.clone()
  for (const p of [L_NAME, M_NAME]) {
    await c(`/deop ${p}`, 400); await c(`/clear ${p}`); await c(`/gamemode survival ${p}`); await c(`/mvtp ${p} world`, 1500)
    await c(`/effect ${p} resistance 1800 4 true`); await c(`/effect ${p} regeneration 1800 4 true`); await c(`/effect ${p} strength 1800 9 true`)
    await c(`/give ${p} diamond_sword 1`)
  }
  // guild: L joins OP_BOT's guild (if not already)
  await c(`/corerpg guild invite ${L_NAME}`, 1200); L.chat('/corerpg guild accept'); await wait(1500)
  r.contrib_start = await contrib(L)
  // bring L to exactly 19 contribution (10 shard = 1)
  const need19 = 19 - (r.contrib_start || 0)
  if (need19 > 0) { await c(`/ni give ${L_NAME} mat_ember_shard ${need19 * 10}`, 1500); L.chat(`/corerpg guild donate mat_ember_shard ${need19 * 10}`); await wait(1500) }
  r.contrib_before_insufficient = await contrib(L)
  for (const b of [L, M]) { const it = b.inventory.items().find(i => i.name === 'diamond_sword'); if (it) try { await b.equip(it, 'hand') } catch (e) {} }
  // DP team L + M
  L.chat('/dungeon-team disband'); await wait(500); L.chat('/dungeon-team create'); await wait(1000)
  L.chat(`/dungeon-team invite ${M_NAME}`); await wait(900); M.chat(`/dungeon-team request join ${L_NAME}`); await wait(1500)
  // (a) bare start refused
  let n = L.chatLog.length; L.chat('/dp start EmberGuildBoss'); await wait(4000)
  r.a_bare_start = since(L, n)
  r.check_a_bare_refused = /需由队长执行 \/corerpg guild boss/.test(r.a_bare_start) && !/已点燃/.test(r.a_bare_start) ? 'PASS' : 'FAIL'
  // (b) insufficient contribution
  n = L.chatLog.length; L.chat('/corerpg guild boss'); await wait(3000)
  r.b_insufficient = since(L, n)
  r.contrib_after_insufficient = await contrib(L)
  r.check_b_insufficient_refused = /贡献不足/.test(r.b_insufficient) && !/已点燃/.test(r.b_insufficient) && r.contrib_after_insufficient === r.contrib_before_insufficient ? 'PASS' : 'FAIL'
  // (c) top up to 20 and start via the guild path
  await c(`/ni give ${L_NAME} mat_ember_shard 10`, 1500); L.chat('/corerpg guild donate mat_ember_shard 10'); await wait(1500)
  r.contrib_before_boss = await contrib(L)
  const inv0 = { [L_NAME]: niCounts(L), [M_NAME]: niCounts(M) }
  r.inHubBefore = [L_NAME, M_NAME].filter(p => op.players[p] && op.players[p].entity && op.players[p].entity.position.distanceTo(hubPos) < 30)
  const nL = L.chatLog.length, nM = M.chatLog.length
  L.chat('/corerpg guild boss')
  let entered = false; const tS = Date.now()
  while (Date.now() - tS < 20000) { await wait(1000); if (/已点燃/.test(since(L, nL)) && /已点燃/.test(since(M, nM))) { entered = true; break } }
  r.c_start_chat_L = since(L, nL).slice(0, 400)
  r.c_entered_both = entered
  if (entered) {
    const t0 = Date.now(), lastTp = {}; let running = true
    const loops = [L, M].map(async f => {
      while (running && Date.now() - t0 < MAX_MS) {
        const e = mobsNear(f, 80).sort((a, b) => a.position.distanceTo(f.entity.position) - b.position.distanceTo(f.entity.position))[0]
        if (!e) { await wait(700); continue }
        const d = e.position.distanceTo(f.entity.position)
        if (d > 3 && Date.now() - (lastTp[f.username] || 0) > 1600) { op.chat(`/tp ${f.username} ${(e.position.x + 1.2).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.6).toFixed(1)}`); lastTp[f.username] = Date.now() }
        if (d <= 3.5) { try { await f.lookAt(e.position.offset(0, (e.height || 1.8) * 0.8, 0), true) } catch (x) {} f.attack(e) }
        await wait(650)
      }
    })
    while (Date.now() - t0 < MAX_MS && !DONE.test(since(L, nL))) await wait(1000)
    running = false; await Promise.all(loops)
    r.c_fight = { done: DONE.test(since(L, nL)), ms: Date.now() - t0 }
    await wait(10000)
  }
  r.contrib_after_boss = await contrib(L)
  for (const b of [L, M]) {
    const d = diff(inv0[b.username], niCounts(b)); r['delta_' + b.username] = d
    const log = since(b, b === L ? nL : nM)
    r['boxMsgs_' + b.username] = (log.match(/通关奖励已发放/g) || []).length
    r['inHub_' + b.username] = !!(op.players[b.username] && op.players[b.username].entity && op.players[b.username].entity.position.distanceTo(hubPos) < 30)
  }
  r.check_c_contrib_spent = r.contrib_before_boss === 20 && r.contrib_after_boss === 0 ? 'PASS' : 'FAIL'
  r.check_c_clear = r.c_fight && r.c_fight.done ? 'PASS' : 'FAIL'
  r.check_c_box_once = [L_NAME, M_NAME].every(p => r['boxMsgs_' + p] === 1 && Object.entries(BOX).every(([k, v]) => (r['delta_' + p][k] || 0) >= v) && (r['delta_' + p].gem_ember_sharp || 0) <= 2) ? 'PASS' : 'FAIL'
  r.check_c_hub = r.inHubBefore.length === 0 && [L_NAME, M_NAME].every(p => r['inHub_' + p]) ? 'PASS' : 'FAIL'
  // (d) pass is one-time: bare start again is refused
  await wait(2000)
  L.chat('/dungeon-team disband'); await wait(500); L.chat('/dungeon-team create'); await wait(800)
  n = L.chatLog.length; L.chat('/dp start EmberGuildBoss'); await wait(4000)
  r.d_bare_after = since(L, n)
  r.check_d_bare_after_refused = /需由队长执行/.test(r.d_bare_after) && !/已点燃/.test(r.d_bare_after) ? 'PASS' : 'FAIL'
  L.chat('/dungeon-team disband'); await wait(600)
  console.log('GUILDBOSS_RESULT', JSON.stringify(r, null, 2))
  await wait(500); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
