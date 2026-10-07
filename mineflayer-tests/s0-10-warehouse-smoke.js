/**
 * D261 / S0-10 light live acceptance (non-OP):
 * 1) legacy /corerpg warehouse deposit|withdraw|unlock refused (view-only)
 * 2) hub vault: p1 stash deposit + p1 vault take still work
 * Needs: play stack up, proxy :25565, AuthMe. Does NOT restart servers.
 */
const { joinPlay } = require('./lib/proxy-login')
const { execSync } = require('child_process')
const wait = ms => new Promise(r => setTimeout(r, ms))
const USER = process.env.MC_USER || 'FreshW10'
const C = '/workspace/minecraft/scripts/console.sh'

function cons(cmd) {
  try { return execSync(`${C} play ${JSON.stringify(cmd)} 1.2`, { encoding: 'utf8', timeout: 15000 }) }
  catch (e) { return (e.stdout || '') + (e.stderr || '') + String(e.message || e) }
}

function strip(s) { return String(s || '').replace(/§./g, '') }

function tailMatch(bot, n0, re) {
  return strip(bot.chatLog.slice(n0).join('\n')).match(re)
}

async function chat(bot, msg, ms = 1600) {
  const n0 = bot.chatLog.length
  bot.chat(msg)
  await wait(ms)
  return { n0, text: strip(bot.chatLog.slice(n0).join('\n')) }
}

const results = []
function check(name, ok, detail) {
  results.push({ name, ok: !!ok, detail: detail || '' })
  console.log(`${ok ? 'PASS' : 'FAIL'} ${name}${detail ? ' — ' + detail : ''}`)
}

;(async () => {
  cons(`deop ${USER}`)
  await wait(500)
  const bot = await joinPlay(USER, { timeout: 60000, settle: 3000 })
  await wait(2000)
  cons(`deop ${USER}`)
  await wait(400)

  // --- (1) legacy warehouse view-only ---
  let r = await chat(bot, '/corerpg warehouse', 1800)
  check('A1 warehouse list/view works',
    /材料仓|仅供查看|空仓|格/.test(r.text) && !/Exception|SEVERE/.test(r.text),
    r.text.slice(0, 180))

  r = await chat(bot, '/corerpg warehouse deposit', 1800)
  check('A2 deposit refused',
    /旧功能已关闭|只可查看|枢纽/.test(r.text) && !/已存入/.test(r.text),
    r.text.slice(0, 180))

  r = await chat(bot, '/corerpg warehouse withdraw 1', 1800)
  check('A3 withdraw refused',
    /旧功能已关闭|只可查看|枢纽/.test(r.text) && !/已取出/.test(r.text),
    r.text.slice(0, 180))

  r = await chat(bot, '/corerpg warehouse unlock', 1800)
  check('A4 unlock refused',
    /旧功能已关闭|只可查看|枢纽/.test(r.text) && !/已用|解锁/.test(r.text),
    r.text.slice(0, 180))

  // --- (2) hub vault deposit + take ---
  cons(`ni give ${USER} mat_ember_shard 8`)
  await wait(1200)
  r = await chat(bot, '/corerpg p1 stash', 2500)
  const stashOk = /一键存入/.test(r.text) && /材料|碎片|shard/i.test(r.text)
  const stashEmpty = /没有可存/.test(r.text)
  check('B1 p1 stash hub deposit',
    stashOk,
    (stashEmpty ? 'EMPTY_INV ' : '') + r.text.slice(0, 220))

  // If stash silent, still try take — if shards were deposited, take works
  r = await chat(bot, '/corerpg p1 vault take mat_ember_shard 2', 2200)
  const takeOk = /取出/.test(r.text) && !/没有/.test(r.text)
  const takeEmpty = /没有/.test(r.text)
  check('B2 p1 vault take withdraw',
    takeOk,
    r.text.slice(0, 200))

  // If take failed because nothing deposited, diagnose: stash may need window or NI id differ
  if (!takeOk) {
    r = await chat(bot, '/corerpg p1 vault', 1500)
    check('B2diag vault open', true, r.text.slice(0, 120))
  }

  bot.quit('s0-10 smoke done')
  const fail = results.filter(x => !x.ok).length
  console.log(`\nSUMMARY ${results.length - fail}/${results.length} PASS, ${fail} FAIL`)
  process.exit(fail ? 1 : 0)
})().catch(e => {
  console.error('FAIL join/setup', e.message || e)
  process.exit(2)
})
