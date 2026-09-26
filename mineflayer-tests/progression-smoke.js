// Progression smoke (2026-09-26 23:10): ember level XP/level-up/talent; combat cap; pass Lv rewards free+paid;
// VIP tier via top-up; season reset. Fresh non-op bot via proxy; RpgBot (op) runs console-equivalent grants.
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Prog' + Math.floor(Math.random() * 90000 + 10000))
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay('RpgBot', { log: false }); const c = async (cmd, ms = 900) => { op.chat(cmd); await wait(ms) }
  const b = await joinPlay(NEW, { log: false }); b.on('message', m => console.log(`[${NEW}]`, m.toString()))
  const run = async (cmds, ms = 1300) => { const n = b.chatLog.length; for (const x of cmds) { b.chat(x); await wait(ms) } return strip(b.chatLog.slice(n).join(' | ')) }
  const opRun = async (cmds, ms = 900) => { const n = b.chatLog.length; for (const x of cmds) await c(x, ms); await wait(800); return strip(b.chatLog.slice(n).join(' | ')) }
  await wait(1500)
  r.level0 = await run(['/corerpg level', '/corerpg talent'])
  r.check_start = /Lv\.10 · 经验 0\/60/.test(r.level0) ? 'PASS' : 'FAIL'
  r.sign = await run(['/corerpg sign'])
  r.check_sign = /等级经验 \+20/.test(r.sign) && /经验 \+10/.test(r.sign) ? 'PASS' : 'FAIL'
  r.clear = await opRun([`/corerpg progress ${NEW} daily_clear`])
  r.check_levelup = /升级！余烬等级 Lv\.11 · 天赋点 \+1/.test(r.clear) && /Lv\.11 · 20\/72/.test(r.clear) ? 'PASS' : 'FAIL'
  r.talent_after = await run(['/corerpg talent'])
  r.combat = await opRun(Array(5).fill(`/corerpg xpreward ${NEW} boss`))
  r.combat_grants = (r.combat.match(/等级经验 \+(\d+)/g) || []).map(s => +s.match(/\d+/)[0])
  r.check_combat_cap = r.combat_grants.reduce((a, x) => a + x, 0) === 150 ? 'PASS' : 'FAIL'
  // pass Lv1 → free claim
  r.passxp = await opRun(Array(5).fill(`/corerpg passxp ${NEW} daily_clear`), 600)
  r.claim_free = await run(['/corerpg pass claim', '/corerpg mail claim all', '/corerpg pass rewards'], 1600)
  r.check_pass_free = /已寄出 1 封/.test(r.claim_free) ? 'PASS' : 'FAIL'
  // VIP via top-up; buy paid track; claim paid
  r.topup = await opRun([`/corerpg cash give ${NEW} 100`], 1200)
  r.check_vip_tier = /晋升 勋阶I/.test(r.topup) ? 'PASS' : 'FAIL'
  r.vip = await run(['/corerpg vip', '/corerpg vip claim'])
  r.check_vip_perks = /勋阶I/.test(r.vip) && /余烬币 \+120/.test(r.vip) && /余烬经验 \+1%/.test(r.vip) ? 'PASS' : 'FAIL'
  r.paid = await run(['/corerpg shop buy pass_unlock', '/corerpg pass claim', '/corerpg mail claim all'], 1700)
  r.check_pass_paid = /已寄出 1 封/.test(r.paid) ? 'PASS' : 'FAIL'
  r.claim_again = await run(['/corerpg pass claim'])
  r.check_no_double = /没有可领取/.test(r.claim_again) ? 'PASS' : 'FAIL'
  // season reset (admin) → progress reset; then restore S1
  r.season = await opRun([`/corerpg pass season reset S2smoke`], 1500)
  r.pass_after_reset = await run(['/corerpg pass'])
  r.check_season_reset = /新赛季 S2smoke/.test(r.season) && /赛季 S2smoke · 等级 Lv\.0/.test(r.pass_after_reset) && /付费轨：未开通/.test(r.pass_after_reset) ? 'PASS' : 'FAIL'
  r.nonadmin_reset = await run(['/corerpg pass season reset HACK'])
  r.check_reset_admin_only = /需要 corerpg.admin/.test(r.nonadmin_reset) ? 'PASS' : 'FAIL'
  await c('/corerpg pass season reset S1', 1500)
  // AFK kill xp is silent; check counter via /corerpg level after a real kill is covered elsewhere
  r.level_end = await run(['/corerpg level'])
  b.quit(); op.quit()
  for (const k of Object.keys(r)) if (typeof r[k] === 'string' && !k.startsWith('check') && k !== 'name') r[k] = r[k].slice(0, 260)
  console.log('PROG_RESULT', JSON.stringify(r, null, 2)); process.exit(0)
})().catch(e => { console.error('FATAL', e); process.exit(1) })
