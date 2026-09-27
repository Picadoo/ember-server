/**
 * S0 体力药使用闭环自检：ni give → consume → 体力涨/瓶减/药剂计数；超顶拒且不扣瓶；发放不预扣。
 * usage: node stamina-potion-use-smoke.js
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))

const OP = process.env.OP_BOT || 'RpgBot'
const USER = process.env.MC_USER || ('Pu_' + Math.floor(Math.random() * 9000 + 1000))
const out = { user: USER, op: OP, checks: {}, pass: true, chat: [], version: '1.15.16' }

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
function dumpNbtKeys(it) {
  try { return Object.keys(it.nbt.value || {}).join(',') } catch (e) { return '' }
}
function findPotion(b, needle) {
  return b.inventory.items().find(i => {
    const n = nameOf(i)
    return n.includes(needle) || (i.name === 'potion' && n.includes('体力'))
  })
}
function countPotion(b, needle) {
  return b.inventory.items().filter(i => nameOf(i).includes(needle)).reduce((a, i) => a + i.count, 0)
}
function parseShow(lines) {
  // [体力] 20/90 · 银行 0 · 药剂今日 0/90
  const s = lines.filter(c => c.includes('[体力]') && c.includes('/')).slice(-1)[0] || ''
  const m = strip(s).match(/(\d+)\s*\/\s*(\d+).*药剂今日\s*(\d+)\s*\/\s*(\d+)/)
  if (!m) return { raw: s, stamina: -1, max: -1, potionToday: -1, potionCap: -1 }
  return { raw: s, stamina: +m[1], max: +m[2], potionToday: +m[3], potionCap: +m[4] }
}

;(async () => {
  const op = await joinPlay(OP, { log: false })
  const opChat = []
  op.on('message', m => { const s = m.toString(); opChat.push(s); console.log('[op]', s) })
  await wait(1500)

  const b = await joinPlay(USER, { log: false })
  const chat = []
  b.on('message', m => { const s = m.toString(); chat.push(s); out.chat.push(s); console.log('[chat]', s) })
  b.on('kicked', r => { console.log('KICKED', r); process.exit(1) })
  await wait(2500)

  const c = async (cmd, ms = 1200) => { console.log('[cmd]', cmd); op.chat(cmd); await wait(ms) }

  await c(`/lp user ${USER} permission set corerpg.use true`, 600)
  await c(`/lp user ${OP} permission set corerpg.admin true`, 600)
  await c(`/clear ${USER}`, 800)
  await c(`/corerpg stamina set ${USER} 20`, 1000)
  // Force potion day counter reset by setting stamina day? not exposed — use raw: drink after set.
  // If player already had potionToday from prior, wipe via mysql/yml is hard; show first.
  b.chat('/corerpg stamina show')
  await wait(1000)
  let show0 = parseShow(chat)
  note('show_baseline', show0.stamina >= 0, JSON.stringify(show0))

  // If potionToday already high, we still can test over-cap; for use+30 need room.
  // Reset by setting stamina and hoping day is fresh — if potionToday>60, drain via note and skip soft.
  await c(`/ni reload`, 2000)
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1500)
  await wait(500)
  let n30 = countPotion(b, '体力药·小')
  note('ni_give_30', n30 >= 1, `count=${n30} names=${b.inventory.items().map(i => nameOf(i) || i.name).join('|')}`)
  if (n30 >= 1) {
    const it = findPotion(b, '体力药·小')
    console.log('nbtkeys', dumpNbtKeys(it), 'name', nameOf(it))
    try { await b.equip(it, 'hand'); await wait(400) } catch (e) { console.log('equip err', e.message) }
    const before = parseShow(chat)
    b.chat('/corerpg stamina show'); await wait(800)
    const pre = parseShow(chat)
    const stamBefore = pre.stamina
    const potBefore = pre.potionToday
    try {
      await b.consume()
    } catch (e) {
      console.log('consume err', e.message)
      // fallback activate
      try { b.activateItem(); await wait(1800); b.deactivateItem() } catch (_) {}
    }
    await wait(1500)
    b.chat('/corerpg stamina show'); await wait(1000)
    const after = parseShow(chat)
    const n30After = countPotion(b, '体力药·小')
    const gained = after.stamina - stamBefore
    const potGain = after.potionToday - potBefore
    const reply = chat.filter(c => c.includes('回复') || c.includes('药剂')).slice(-5).join(' || ')
    note('use_30_stamina_up', gained >= 30 || (stamBefore >= 90 && potGain === 30), `stam ${stamBefore}->${after.stamina} pot ${potBefore}->${after.potionToday} :: ${reply}`)
    note('use_30_bottle_gone', n30After === 0, `count=${n30After}`)
    note('use_30_potion_count', potGain === 30, `potGain=${potGain} after=${after.potionToday}`)
  }

  // _45
  await c(`/clear ${USER}`, 800)
  await c(`/corerpg stamina set ${USER} 20`, 800)
  b.chat('/corerpg stamina show'); await wait(800)
  let pre45show = parseShow(chat)
  await c(`/ni give ${USER} consumable_ember_stamina_45 1`, 1500)
  let n45 = countPotion(b, '体力药·周')
  note('ni_give_45', n45 >= 1, `count=${n45}`)
  if (n45 >= 1) {
    const it = findPotion(b, '体力药·周')
    try { await b.equip(it, 'hand'); await wait(400) } catch (e) {}
    const potBefore = parseShow(chat).potionToday
    // refresh
    b.chat('/corerpg stamina show'); await wait(600)
    const pre = parseShow(chat)
    try { await b.consume() } catch (e) { try { b.activateItem(); await wait(1800); b.deactivateItem() } catch (_) {} }
    await wait(1500)
    b.chat('/corerpg stamina show'); await wait(1000)
    const after = parseShow(chat)
    const n45After = countPotion(b, '体力药·周')
    const potGain = after.potionToday - pre.potionToday
    const gained = after.stamina - pre.stamina
    note('use_45_stamina_up', gained >= 45 || (pre.stamina >= 90 && potGain === 45), `stam ${pre.stamina}->${after.stamina} pot ${pre.potionToday}->${after.potionToday}`)
    note('use_45_bottle_gone', n45After === 0, `count=${n45After}`)
    note('use_45_potion_count', potGain === 45, `potGain=${potGain}`)
  }

  // Over-cap: set potion near cap by using potions until 75+, or force via three 30s if room.
  // Simpler: set stamina low, give _30, manually... we can't set potionToday.
  // Use remaining room: if potionToday is 75 after 30+45, next 30 should fail.
  b.chat('/corerpg stamina show'); await wait(800)
  let cur = parseShow(chat)
  await c(`/clear ${USER}`, 600)
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1200)
  // If room < 30, should reject; else drink until near cap then reject.
  while (cur.potionToday >= 0 && cur.potionToday + 30 <= 90) {
    // drink to fill toward cap
    const it = findPotion(b, '体力药·小')
    if (!it) {
      await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1000)
    }
    const it2 = findPotion(b, '体力药·小')
    if (!it2) break
    try { await b.equip(it2, 'hand'); await wait(300); await b.consume() } catch (e) {
      try { b.activateItem(); await wait(1800); b.deactivateItem() } catch (_) {}
    }
    await wait(1200)
    b.chat('/corerpg stamina show'); await wait(700)
    cur = parseShow(chat)
    if (cur.potionToday + 30 > 90) break
    if (cur.potionToday >= 90) break
    // give another for next loop if needed
    if (countPotion(b, '体力药·小') === 0 && cur.potionToday + 30 <= 90) {
      await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1000)
    }
    // safety
    if (cur.potionToday >= 60 && cur.potionToday + 30 <= 90) {
      // one more to push over potential
    }
    if ((out._loops = (out._loops || 0) + 1) > 5) break
  }
  // Now ensure we have a bottle and are over room for +30
  await c(`/clear ${USER}`, 600)
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1200)
  b.chat('/corerpg stamina show'); await wait(800)
  cur = parseShow(chat)
  const nBeforeCap = countPotion(b, '体力药·小')
  const itCap = findPotion(b, '体力药·小')
  if (itCap && cur.potionToday + 30 > 90) {
    try { await b.equip(itCap, 'hand'); await wait(300); await b.consume() } catch (e) {
      try { b.activateItem(); await wait(1800); b.deactivateItem() } catch (_) {}
    }
    await wait(1500)
    const rejectMsg = chat.filter(c => c.includes('上限') || c.includes('药剂')).slice(-3).join(' || ')
    b.chat('/corerpg stamina show'); await wait(800)
    const afterCap = parseShow(chat)
    const nAfterCap = countPotion(b, '体力药·小')
    note('over_cap_reject', rejectMsg.includes('上限') || afterCap.potionToday === cur.potionToday, rejectMsg || afterCap.raw)
    note('over_cap_bottle_kept', nAfterCap >= 1, `before=${nBeforeCap} after=${nAfterCap}`)
  } else {
    note('over_cap_reject', false, `could not reach over-cap state potionToday=${cur.potionToday} n=${nBeforeCap}`)
    note('over_cap_bottle_kept', false, 'skipped')
  }

  // Grant no pre-count: shop buy daily_ticket
  await c(`/corerpg cash give ${USER} 200 nocount`, 800)
  // Reset daily bought? may already bought — try anyway
  await c(`/clear ${USER}`, 600)
  b.chat('/corerpg stamina show'); await wait(800)
  const beforeGrant = parseShow(chat)
  // Player buys
  b.chat('/corerpg shop buy daily_ticket')
  await wait(2000)
  const grantMsg = chat.filter(c => c.includes('商城') || c.includes('体力药') || c.includes('获得')).slice(-5).join(' || ')
  b.chat('/corerpg stamina show'); await wait(800)
  const afterGrant = parseShow(chat)
  const nAfterGrant = countPotion(b, '体力药·小')
  const potUnchanged = afterGrant.potionToday === beforeGrant.potionToday
  note('grant_no_precount', potUnchanged && (nAfterGrant >= 1 || grantMsg.includes('限购') || grantMsg.includes('上限')), `pot ${beforeGrant.potionToday}->${afterGrant.potionToday} n=${nAfterGrant} :: ${grantMsg}`)

  // Cleanup ops
  await c(`/deop ${OP}`, 500)
  await c(`/deop ${USER}`, 500)
  await c(`/lp user ${OP} permission unset corerpg.admin`, 500)

  op.quit(); b.quit()
  fs.writeFileSync('/tmp/stamina-potion-use-smoke.json', JSON.stringify(out, null, 2))
  console.log('RESULT', out.pass ? 'PASS' : 'FAIL', JSON.stringify(out.checks, null, 2))
  // wipe ops
  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.log('ERR', e && e.stack || e)
  try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n') } catch (_) {}
  process.exit(1)
})
setTimeout(() => { console.log('TIMEOUT'); try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n') } catch (_) {}; process.exit(2) }, 180000)
