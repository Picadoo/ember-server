/**
 * S0 体力药使用 · 独立正式短验收（测试岗，勿照抄插件岗 smoke）
 * 硬条 1–5 → /tmp/s0-potion-use-test.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))

const OP = 'S0pOpA'
const USER = 'S0pU' + Math.floor(Math.random() * 900 + 100)   // use path
const GRANT = 'S0pG' + Math.floor(Math.random() * 900 + 100)  // grant no-precount

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  op: OP,
  user: USER,
  grantUser: GRANT,
  corerpg_expected: '1.15.16',
  checks: {},
  criteria: {},
  evidence: {},
  pass: true,
  chat_use: [],
  chat_grant: []
}

function note(k, ok, detail) {
  out.checks[k] = { ok: !!ok, detail: String(detail || '') }
  if (!ok) out.pass = false
  console.log((ok ? 'PASS' : 'FAIL') + ' ' + k + ' :: ' + detail)
}

function strip(s) {
  return String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')
}
function nameOf(it) {
  try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' }
}
function countByName(b, needle) {
  return b.inventory.items().filter(i => nameOf(i).includes(needle)).reduce((a, i) => a + i.count, 0)
}
function findByName(b, needle) {
  return b.inventory.items().find(i => nameOf(i).includes(needle))
}
function parseShow(lines) {
  const s = lines.filter(c => c.includes('[体力]') && c.includes('/') && c.includes('药剂今日')).slice(-1)[0] || ''
  const m = strip(s).match(/(\d+)\s*\/\s*(\d+).*药剂今日\s*(\d+)\s*\/\s*(\d+)/)
  if (!m) return { raw: strip(s), stamina: -1, max: -1, potionToday: -1, potionCap: -1 }
  return { raw: strip(s), stamina: +m[1], max: +m[2], potionToday: +m[3], potionCap: +m[4] }
}
async function consumeHeld(b) {
  try {
    await b.consume()
  } catch (e) {
    console.log('consume fallback', e.message)
    try { b.activateItem(); await wait(1800); b.deactivateItem() } catch (_) {}
  }
  await wait(1500)
}

;(async () => {
  // --- code/doc 旁证：禁显示名 ---
  const staminaSrc = fs.readFileSync('/workspace/minecraft/CoreRpg/src/main/java/town/sunshine/corerpg/StaminaService.java', 'utf8')
  const niYml = fs.readFileSync('/workspace/minecraft/plugins/NeigeItems/Items/ember-stamina.yml', 'utf8')
  const jarYml = require('child_process').execSync('unzip -p /workspace/minecraft/plugins/CoreRpg.jar plugin.yml', { encoding: 'utf8' })
  const jarVer = (jarYml.match(/^version:\s*(.+)$/m) || [])[1] || '?'
  const logTail = fs.readFileSync('/workspace/minecraft/server-runtime/logs/latest.log', 'utf8')
  const logVer = (logTail.match(/CoreRpg ([\d.]+) enabled/) || [])[1] || '?'
  note('corerpg_jar_1_15_16', jarVer === '1.15.16', `jar=${jarVer}`)
  note('corerpg_log_1_15_16', logVer === '1.15.16', `log=${logVer}`)
  const idOnly = /getNiId\(stack\)/.test(staminaSrc) && /onPotionConsume/.test(staminaSrc)
    && !/getDisplayName\(\).*POTION|displayName.*stamina|equalsIgnoreCase\(.*体力药/.test(staminaSrc.replace(/\/\*[\s\S]*?\*\//g, '').replace(/\/\/.*/g, ''))
  const niComment = niYml.includes('禁显示名匹配') && niYml.includes('consumable_ember_stamina_30')
  note('no_displayname_match_code', idOnly && niComment, `idOnly=${idOnly} niComment=${niComment}`)

  const op = await joinPlay(OP, { log: false })
  op.on('message', m => console.log('[op]', m.toString()))
  await wait(1800)

  // confirm op
  op.chat('/gamemode creative')
  await wait(800)

  const c = async (cmd, ms = 1100) => {
    console.log('[cmd]', cmd)
    op.chat(cmd)
    await wait(ms)
  }

  await c(`/lp user ${OP} permission set corerpg.admin true`, 700)
  await c(`/lp user ${OP} parent add admin`, 700)

  // ========== USE PATH (USER) ==========
  const b = await joinPlay(USER, { log: false })
  const chat = []
  b.on('message', m => { const s = m.toString(); chat.push(s); out.chat_use.push(s); console.log('[use]', s) })
  b.on('kicked', r => { console.log('KICKED use', r) })
  await wait(2800)

  await c(`/lp user ${USER} permission set corerpg.use true`, 600)
  await c(`/clear ${USER}`, 900)
  await c(`/corerpg stamina set ${USER} 20`, 1100)
  b.chat('/corerpg stamina show')
  await wait(1000)
  let base = parseShow(chat)
  note('baseline_show', base.stamina === 20 && base.potionToday === 0, JSON.stringify(base))
  out.evidence.baseline = base

  // --- 1) _30 drink ---
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1600)
  let n30 = countByName(b, '体力药·小')
  note('h1_ni_give_30', n30 >= 1, `count=${n30} inv=${b.inventory.items().map(i => nameOf(i) || i.name).join('|')}`)
  let pre30, after30, n30After, reply30
  if (n30 >= 1) {
    const it = findByName(b, '体力药·小')
    try { await b.equip(it, 'hand'); await wait(400) } catch (e) { console.log('equip30', e.message) }
    b.chat('/corerpg stamina show'); await wait(800)
    pre30 = parseShow(chat)
    await consumeHeld(b)
    b.chat('/corerpg stamina show'); await wait(1000)
    after30 = parseShow(chat)
    n30After = countByName(b, '体力药·小')
    reply30 = chat.filter(x => /回复|\+30|药剂今日/.test(x)).slice(-4).map(strip).join(' || ')
    const stamOk = after30.stamina === pre30.stamina + 30
    const potOk = after30.potionToday === pre30.potionToday + 30
    const bottleOk = n30After === 0
    note('h1_stamina_plus_30', stamOk, `stam ${pre30.stamina}->${after30.stamina}`)
    note('h1_potion_today_plus_30', potOk, `pot ${pre30.potionToday}->${after30.potionToday}`)
    note('h1_bottle_consumed', bottleOk, `count ${n30}->${n30After}`)
    out.evidence.use30 = { pre: pre30, after: after30, bottleBefore: n30, bottleAfter: n30After, reply: reply30 }
  } else {
    note('h1_stamina_plus_30', false, 'no bottle')
    note('h1_potion_today_plus_30', false, 'no bottle')
    note('h1_bottle_consumed', false, 'no bottle')
  }

  // --- 2) _45 same pool ---
  await c(`/clear ${USER}`, 800)
  await c(`/corerpg stamina set ${USER} 20`, 1000)
  b.chat('/corerpg stamina show'); await wait(800)
  let mid = parseShow(chat)
  await c(`/ni give ${USER} consumable_ember_stamina_45 1`, 1600)
  let n45 = countByName(b, '体力药·周')
  note('h2_ni_give_45', n45 >= 1, `count=${n45}`)
  let pre45, after45, n45After
  if (n45 >= 1) {
    const it = findByName(b, '体力药·周')
    try { await b.equip(it, 'hand'); await wait(400) } catch (e) {}
    b.chat('/corerpg stamina show'); await wait(700)
    pre45 = parseShow(chat)
    // expect pot still 30 from previous drink (same player day pool)
    await consumeHeld(b)
    b.chat('/corerpg stamina show'); await wait(1000)
    after45 = parseShow(chat)
    n45After = countByName(b, '体力药·周')
    const stamOk = after45.stamina === pre45.stamina + 45
    const potOk = after45.potionToday === pre45.potionToday + 45
    const pool75 = after45.potionToday === 75
    note('h2_stamina_plus_45', stamOk, `stam ${pre45.stamina}->${after45.stamina}`)
    note('h2_potion_today_plus_45', potOk, `pot ${pre45.potionToday}->${after45.potionToday}`)
    note('h2_same_pool_75', pool75, `potionToday=${after45.potionToday} (expect 30+45=75)`)
    note('h2_bottle_consumed', n45After === 0, `count=${n45After}`)
    out.evidence.use45 = { midBeforeGive: mid, pre: pre45, after: after45, bottleAfter: n45After }
  } else {
    note('h2_stamina_plus_45', false, 'no bottle')
    note('h2_potion_today_plus_45', false, 'no bottle')
    note('h2_same_pool_75', false, 'no bottle')
    note('h2_bottle_consumed', false, 'no bottle')
  }

  // --- 3) over-cap refuse, keep bottle ---
  await c(`/clear ${USER}`, 800)
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1400)
  b.chat('/corerpg stamina show'); await wait(900)
  let preCap = parseShow(chat)
  const nCapBefore = countByName(b, '体力药·小')
  const over = preCap.potionToday + 30 > 90
  note('h3_precondition_overcap', over && nCapBefore >= 1, `pot=${preCap.potionToday} n=${nCapBefore} over=${over}`)
  if (over && nCapBefore >= 1) {
    const it = findByName(b, '体力药·小')
    try { await b.equip(it, 'hand'); await wait(400) } catch (e) {}
    const chatMark = chat.length
    await consumeHeld(b)
    await wait(500)
    const rej = chat.slice(chatMark).filter(x => /上限|药剂/.test(x)).map(strip)
    b.chat('/corerpg stamina show'); await wait(900)
    const afterCap = parseShow(chat)
    const nCapAfter = countByName(b, '体力药·小')
    const rejOk = rej.some(x => x.includes('上限')) || afterCap.potionToday === preCap.potionToday
    note('h3_reject_msg', rej.some(x => /已达上限/.test(x)), rej.join(' || ') || '(no msg)')
    note('h3_no_stamina_change', afterCap.stamina === preCap.stamina && afterCap.potionToday === preCap.potionToday,
      `stam ${preCap.stamina}->${afterCap.stamina} pot ${preCap.potionToday}->${afterCap.potionToday}`)
    note('h3_bottle_kept', nCapAfter >= 1, `before=${nCapBefore} after=${nCapAfter}`)
    out.evidence.overcap = { pre: preCap, after: afterCap, bottleBefore: nCapBefore, bottleAfter: nCapAfter, reject: rej }
  } else {
    note('h3_reject_msg', false, 'precondition fail')
    note('h3_no_stamina_change', false, 'precondition fail')
    note('h3_bottle_kept', false, 'precondition fail')
  }

  // ========== 4) GRANT no precount (fresh GRANT account) ==========
  const g = await joinPlay(GRANT, { log: false })
  const gchat = []
  g.on('message', m => { const s = m.toString(); gchat.push(s); out.chat_grant.push(s); console.log('[grant]', s) })
  await wait(2800)
  await c(`/lp user ${GRANT} permission set corerpg.use true`, 600)
  await c(`/clear ${GRANT}`, 900)
  await c(`/corerpg stamina set ${GRANT} 20`, 1000)
  g.chat('/corerpg stamina show'); await wait(1000)
  const gBefore = parseShow(gchat)
  await c(`/corerpg cash give ${GRANT} 200 nocount`, 900)
  await c(`/clear ${GRANT}`, 700)
  g.chat('/corerpg stamina show'); await wait(800)
  const gPreBuy = parseShow(gchat)
  g.chat('/corerpg shop buy daily_ticket')
  await wait(2200)
  const buyMsgs = gchat.filter(x => /商城|体力药|获得|NeigeItems/.test(x)).slice(-6).map(strip)
  g.chat('/corerpg stamina show'); await wait(1000)
  const gAfter = parseShow(gchat)
  const nGrant = countByName(g, '体力药·小')
  const potUnchanged = gAfter.potionToday === gPreBuy.potionToday
  note('h4_got_ni_bottle', nGrant >= 1, `n=${nGrant} :: ${buyMsgs.join(' || ')}`)
  note('h4_potion_today_unchanged', potUnchanged, `pot ${gPreBuy.potionToday}->${gAfter.potionToday}`)
  out.evidence.grant = { before: gBefore, preBuy: gPreBuy, after: gAfter, n: nGrant, msgs: buyMsgs }

  // Also: ni give path no precount (reward/发放)
  await c(`/clear ${GRANT}`, 700)
  g.chat('/corerpg stamina show'); await wait(700)
  const gPreNi = parseShow(gchat)
  await c(`/ni give ${GRANT} consumable_ember_stamina_30 1`, 1400)
  g.chat('/corerpg stamina show'); await wait(800)
  const gAfterNi = parseShow(gchat)
  const nNi = countByName(g, '体力药·小')
  note('h4b_ni_give_no_precount', gAfterNi.potionToday === gPreNi.potionToday && nNi >= 1,
    `pot ${gPreNi.potionToday}->${gAfterNi.potionToday} n=${nNi}`)

  // ========== criteria rollup ==========
  out.criteria.c1_use_30 = {
    ok: !!(out.checks.h1_stamina_plus_30 && out.checks.h1_stamina_plus_30.ok
      && out.checks.h1_potion_today_plus_30 && out.checks.h1_potion_today_plus_30.ok
      && out.checks.h1_bottle_consumed && out.checks.h1_bottle_consumed.ok),
    detail: out.evidence.use30 || null
  }
  out.criteria.c2_use_45_same_pool = {
    ok: !!(out.checks.h2_stamina_plus_45 && out.checks.h2_stamina_plus_45.ok
      && out.checks.h2_potion_today_plus_45 && out.checks.h2_potion_today_plus_45.ok
      && out.checks.h2_same_pool_75 && out.checks.h2_same_pool_75.ok
      && out.checks.h2_bottle_consumed && out.checks.h2_bottle_consumed.ok),
    detail: out.evidence.use45 || null
  }
  out.criteria.c3_overcap_refuse = {
    ok: !!(out.checks.h3_reject_msg && out.checks.h3_reject_msg.ok
      && out.checks.h3_bottle_kept && out.checks.h3_bottle_kept.ok
      && out.checks.h3_no_stamina_change && out.checks.h3_no_stamina_change.ok),
    detail: out.evidence.overcap || null
  }
  out.criteria.c4_grant_no_precount = {
    ok: !!(out.checks.h4_got_ni_bottle && out.checks.h4_got_ni_bottle.ok
      && out.checks.h4_potion_today_unchanged && out.checks.h4_potion_today_unchanged.ok),
    detail: out.evidence.grant || null
  }
  out.criteria.c5_no_displayname_ops = {
    ok: !!(out.checks.no_displayname_match_code && out.checks.no_displayname_match_code.ok
      && out.checks.corerpg_jar_1_15_16 && out.checks.corerpg_jar_1_15_16.ok
      && out.checks.corerpg_log_1_15_16 && out.checks.corerpg_log_1_15_16.ok),
    detail: { jar: jarVer, log: logVer, idOnly, niComment }
  }
  for (const k of Object.keys(out.criteria)) {
    if (!out.criteria[k].ok) out.pass = false
  }

  // cleanup
  await c(`/deop ${OP}`, 600)
  await c(`/lp user ${OP} permission unset corerpg.admin`, 600)
  await c(`/lp user ${OP} parent remove admin`, 600)
  try { op.quit() } catch (_) {}
  try { b.quit() } catch (_) {}
  try { g.quit() } catch (_) {}
  await wait(500)

  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
  try { fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n') } catch (_) {}
  const opsPlay = fs.readFileSync('/workspace/minecraft/server-runtime/ops.json', 'utf8').trim()
  const opsLogin = fs.readFileSync('/workspace/minecraft/login-runtime/ops.json', 'utf8').trim()
  note('ops_final_empty', opsPlay === '[]' && opsLogin === '[]', `play=${opsPlay} login=${opsLogin}`)
  out.evidence.ops = { play: opsPlay, login: opsLogin }
  if (!out.checks.ops_final_empty.ok) out.pass = false
  // fold ops into c5
  if (!out.checks.ops_final_empty.ok) out.criteria.c5_no_displayname_ops.ok = false

  out.verdict = out.pass ? 'PASS' : 'FAIL'
  fs.writeFileSync('/tmp/s0-potion-use-test.json', JSON.stringify(out, null, 2))
  console.log('VERDICT', out.verdict)
  console.log(JSON.stringify(out.criteria, null, 2))
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.log('ERR', e && e.stack || e)
  try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n') } catch (_) {}
  try { fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n') } catch (_) {}
  out.pass = false
  out.verdict = 'FAIL'
  out.error = String(e && e.stack || e)
  try { fs.writeFileSync('/tmp/s0-potion-use-test.json', JSON.stringify(out, null, 2)) } catch (_) {}
  process.exit(1)
})
setTimeout(() => {
  console.log('TIMEOUT')
  try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n') } catch (_) {}
  try { fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n') } catch (_) {}
  process.exit(2)
}, 240000)
