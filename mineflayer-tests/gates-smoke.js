// Level gates + bounty XP smoke (2026-09-26 23:15). Fresh non-op bot via proxy (Lv10); RpgBot (op) grants XP/items.
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Gate' + Math.floor(Math.random() * 90000 + 10000))
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay('RpgBot', { log: false }); const c = async (cmd, ms = 900) => { op.chat(cmd); await wait(ms) }
  const b = await joinPlay(NEW, { log: false }); b.on('message', m => console.log(`[${NEW}]`, m.toString()))
  const run = async (cmds, ms = 2000) => { const n = b.chatLog.length; for (const x of cmds) { b.chat(x); await wait(ms) } return strip(b.chatLog.slice(n).join(' | ')) }
  await wait(1500)
  // Lv10: daily allowed
  r.daily = await run(['/dp start EmberDaily'], 4000); await run(['/dp leave'], 7000)
  r.check_daily_lv10 = /余烬窟·日 开始/.test(r.daily) ? 'PASS' : 'FAIL'
  // Lv10: weekly refused, ticket kept
  const wt0 = count(b, 'ticket_ember_weekly'); r.weekly_tickets_before = wt0
  r.weekly_refused = await run(['/dp start EmberWeekly'], 3000)
  r.weekly_tickets_after_refuse = count(b, 'ticket_ember_weekly')
  r.check_weekly_refused = /需要余烬等级 Lv\.20/.test(r.weekly_refused) && r.weekly_tickets_after_refuse === wt0 && wt0 > 0 ? 'PASS' : 'FAIL'
  r.abyss_refused = await run(['/dp start EmberAbyss'], 3000)
  r.check_abyss_refused = /需要余烬等级 Lv\.25/.test(r.abyss_refused) && count(b, 'ticket_ember_abyss') > 0 ? 'PASS' : 'FAIL'
  r.calamity_refused = await run(['/corerpg calamity join'])
  r.check_calamity_refused = /灾厄公共窗 需要余烬等级 Lv\.30（当前 Lv\.10）/.test(r.calamity_refused) ? 'PASS' : 'FAIL'
  // raise to Lv20 (5× raid_clear = 1250 XP ≥ 1140) → weekly starts, ticket consumed
  for (let i = 0; i < 5; i++) await c(`/corerpg progress ${NEW} raid_clear`, 700)
  r.level = await run(['/corerpg level'], 1500)
  r.weekly_ok = await run(['/dp start EmberWeekly'], 4000); await run(['/dp leave'], 7000)
  r.check_weekly_lv20 = /Lv\.20/.test(r.level) && /开始/.test(r.weekly_ok) && count(b, 'ticket_ember_weekly') === wt0 - 1 ? 'PASS' : 'FAIL'
  // bounty: kill until claimable, then claim → ember + pass XP
  await c(`/mvtp ${NEW} world`, 3000); await c(`/tp ${NEW} 600 100 600`, 2500)
  await c(`/effect ${NEW} strength 300 100`, 500); await c(`/effect ${NEW} resistance 300 5`, 500)
  let status = ''
  for (let round = 0; round < 16; round++) {
    await c(`/effect ${NEW} strength 300 100`, 300); await c(`/effect ${NEW} resistance 300 5`, 300); await c(`/effect ${NEW} regeneration 300 5`, 300)
    const p = b.entity.position
    await c(`/mm m spawn EmberAfkZombie 4 world,${(p.x + 2).toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`, 800)
    for (let i = 0; i < 40; i++) { const m = b.nearestEntity(e => e.type === 'mob' && e.position.distanceTo(b.entity.position) < 8); if (!m) break; await b.lookAt(m.position.offset(0, 1, 0)); b.attack(m); await wait(350) }
    status = await run(['/corerpg bounty'], 1200)
    if (/可领取/.test(status)) break
  }
  r.bounty_status = status
  r.bounty_claim = await run(['/corerpg bounty claim'], 2000)
  r.check_bounty_xp = /悬赏完成/.test(r.bounty_claim) && /等级经验 \+40/.test(r.bounty_claim) && /\[战令\] (经验 \+15|今日战令经验已达上限)/.test(r.bounty_claim) ? 'PASS' : 'FAIL'
  r.bounty_again = await run(['/corerpg bounty claim'])
  r.check_bounty_once = /已领取/.test(r.bounty_again) ? 'PASS' : 'FAIL'
  await c(`/mvtp ${NEW} ember_hub`, 2000)
  b.quit(); op.quit()
  for (const k of Object.keys(r)) if (typeof r[k] === 'string' && !k.startsWith('check') && k !== 'name') r[k] = r[k].slice(0, 240)
  console.log('GATES_RESULT', JSON.stringify(r, null, 2)); process.exit(0)
})().catch(e => { console.error('FATAL', e); process.exit(1) })
