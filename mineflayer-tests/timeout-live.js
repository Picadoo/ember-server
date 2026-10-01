// Long live check of a DungeonPlus timeout task (B2.115 weekly 1500s / B2.116 raid 2400s): bots enter
// the player way, stay alive (console resistance/regeneration/saturation) and wait for the timeout
// message; prints the raw chat packet. LINE=weekly|raid|elite (B2.112 720s), BOTS=comma list (raid needs 3).
const { joinPlay } = require('./lib/proxy-login')
const con = require('./lib/console')
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s).replace(/§./g, '')
const LINE = process.env.LINE || 'weekly'
const CFG = { weekly: { gate: 20, secs: 1500, re: /周常超时失败|深核·周 超时失败/ }, raid: { gate: 35, secs: 2400, re: /团本超时失败|余烬团本 超时失败/ }, elite: { gate: 40, secs: 720, re: /试炼失败|精英试炼超时失败/ } }[LINE]
const NAMES = (process.env.BOTS || (LINE === 'raid' ? 'EmberTestR1,EmberTestR2,EmberTestR3' : LINE === 'elite' ? 'EmberTestE' : 'EmberTestW')).split(',')
const say = async (b, cmd, ms = 1200) => { const n = b.chatLog.length; b.chat(cmd); await wait(ms); return strip(b.chatLog.slice(n).join(' | ')) }
const level = async (b) => Number((await say(b, '/corerpg level')).match(/等级 Lv\.(\d+)/)?.[1] || 0)
;(async () => {
  const bots = []
  for (const n of NAMES) bots.push(await joinPlay(n, { log: false }))
  // a bot that quit mid-run rejoins inside its old instance ("副本中该命令不可用"): leave it first
  for (const b of bots) await say(b, '/dp leave', 1500)
  await wait(6000) // DP refuses re-entry within 5s of leaving
  const raw = []
  bots[0]._client.on('chat', (m) => { raw.push(m.message); if (/超时|失败|开始/.test(m.message)) console.log(`[timeout] ${new Date().toTimeString().slice(0, 8)} packet ${m.message.slice(0, 300)}`) })
  for (const b of bots) {
    let l = await level(b)
    for (let i = 0; i < 80 && l < CFG.gate; i++) { await con.run(`corerpg progress ${b.username} raid_clear`, null); if (i % 4 === 3) l = await level(b) }
    l = await level(b); console.log(`[timeout] ${b.username} Lv.${l}`)
    await con.run(`corerpg stamina set ${b.username} 90`, null)
    await con.run(`mvtp ${b.username} ember_hub`, null)
  }
  const A = bots[0]
  if (bots.length > 1) {
    await say(A, '/dungeon-team disband', 600); await say(A, '/dungeon-team create', 1200)
    for (const b of bots.slice(1)) { await say(A, `/dungeon-team invite ${b.username}`, 1000); await say(b, `/dungeon-team request join ${A.username}`, 1500) }
  }
  const enter = await say(A, `/corerpg enter ${LINE}`, 8000)
  console.log(`[timeout] enter: ${enter.slice(0, 300)}`)
  if (!/开始|集结|试炼/.test(enter) || /需要余烬|已通关|不足/.test(enter)) { console.log('[timeout] FAIL did not start'); process.exit(2) }
  const t0 = Date.now()
  const buff = async () => { for (const b of bots) for (const e of ['resistance 600 4', 'regeneration 600 4', 'saturation 600 4']) await con.run(`effect ${b.username} ${e} true`, null) }
  await buff()
  let hit = null
  while (!hit && Date.now() - t0 < (CFG.secs + 240) * 1000) {
    await wait(5000)
    if ((Date.now() - t0) % 300000 < 5000) await buff()
    hit = raw.find((m) => CFG.re.test(m))
  }
  console.log(hit ? `[timeout] PASS after ${Math.round((Date.now() - t0) / 1000)}s: ${hit}` : '[timeout] FAIL no timeout message')
  const msgs = raw.filter((m) => /超时失败/.test(m)); for (const m of msgs) console.log(`[timeout] msg ${m}`)
  await wait(5000)
  for (const b of bots) { await con.run(`mvtp ${b.username} ember_hub`, null); b.quit() }
  setTimeout(() => process.exit(hit ? 0 : 2), 500)
})().catch((e) => { console.error('[timeout] FATAL', e.message || e); process.exit(1) })
