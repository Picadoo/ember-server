// Daily maps check (release v2026.10.01 worlds): EmberTestOp (not op) enters daily_ash / daily_crypt
// the player way (`/corerpg enter <id>`, ticket + stamina + Lv.10 gate) and must land in a fresh
// DungeonPlus instance. Prereqs (level, ticket) are granted on the play console (lib/console.js).
const { joinPlay } = require('./lib/proxy-login')
const con = require('./lib/console')
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s).replace(/§./g, '')
const U = process.env.MC_USER || 'EmberTestOp'
const LINES = (process.env.LINES || 'daily_ash,daily_crypt').split(',')
const results = []
;(async () => {
  const bot = await joinPlay(U, { log: false })
  const said = async (cmd, ms = 1500) => { const n = bot.chatLog.length; bot.chat(cmd); await wait(ms); return strip(bot.chatLog.slice(n).join(' | ')) }
  // level gate (daily Lv.10)
  let lvl = Number((await said('/corerpg level')).match(/等级 Lv\.(\d+)/)?.[1] || 0)
  for (let i = 0; i < 20 && lvl < 10; i++) { await con.run(`corerpg progress ${U} raid_clear`, null); if (i % 3 === 2) lvl = Number((await said('/corerpg level')).match(/等级 Lv\.(\d+)/)?.[1] || 0) }
  lvl = Number((await said('/corerpg level')).match(/等级 Lv\.(\d+)/)?.[1] || 0)
  console.log(`[daily] ${U} Lv.${lvl}`)
  for (const line of LINES) {
    await con.run(`mvtp ${U} ember_hub`, null); await wait(1500)
    await con.run(`ni give ${U} ticket_ember_daily 1`, null)
    await con.run(`corerpg stamina set ${U} 100`, null)
    const worldBefore = bot.game.dimension
    let spawned = null
    const onPos = () => { spawned = bot.entity.position.clone() }
    bot.once('forcedMove', onPos)
    const m = con.mark()
    const out = await said(`/corerpg enter ${line}`, 7000)
    const log = con.since(m).join('\n')
    const inst = (log.match(/dungeon_EmberDaily\w+/) || [])[0]
    const ok = /开始！/.test(out)
    results.push({ line, ok })
    console.log(`[daily] ${ok ? 'PASS' : 'FAIL'}  ${line} — chat="${out.slice(0, 220)}" pos=${spawned || bot.entity.position} ${inst ? 'instance=' + inst : ''}`)
    await said('/dp leave', 2500)
    await wait(6000) // DungeonPlus refuses re-entry within 5s of leaving an instance
  }
  await con.run(`mvtp ${U} ember_hub`, null)
  await con.run(`clear ${U}`, null)
  bot.quit()
  const fail = results.filter((r) => !r.ok).length
  console.log(`[daily] PASS=${results.length - fail} FAIL=${fail}`)
  setTimeout(() => process.exit(fail ? 2 : 0), 300)
})().catch((e) => { console.error('[daily] FATAL', e.message || e); process.exit(1) })
