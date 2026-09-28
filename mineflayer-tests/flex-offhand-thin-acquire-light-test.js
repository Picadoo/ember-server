/**
 * B-flex-3 灰粮 stub 币购副手 · 轻测（测试岗）
 * 禁长测 / 挑刺 / DPS / 宣称 B0.1 已清；不改文件
 * 路径：静态 rg → 热更后 /ember→补给·生活 → 点守腕/生坠购入 → 周限 → 旧 offers 不回归
 * OUT: /tmp/flex-offhand-thin-acquire-light-test.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const { execSync } = require('child_process')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')

const OP = 'FxOfOp29'
const USER = 'FxOfLt29'
const OUT = '/tmp/flex-offhand-thin-acquire-light-test.json'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const ROOT = '/workspace/minecraft'
const HUB_LIFE = 17 // key k
const LIFE_WARD = 37 // O
const LIFE_VITA = 42 // W
const LIFE_BREAD = 11 // A

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  tip_build: '58baffe',
  op: OP,
  user: USER,
  checks: {},
  evidence: {},
  pass: true,
  menu_skip: false
}

function note(k, ok, detail) {
  out.checks[k] = { ok: !!ok, detail: String(detail || '') }
  if (!ok) out.pass = false
  console.log((ok ? 'PASS' : 'FAIL') + ' ' + k + ' :: ' + detail)
}
function nameOf(it) {
  try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' }
}
function loreOf(it) {
  try { return (it.nbt.value.display.value.Lore.value.value || []).map(l => strip(l)) } catch (e) { return [] }
}
function niId(it) {
  try {
    for (const l of (it.nbt.value.display.value.Lore.value.value || [])) {
      const p = strip(l).trim()
      if (/^[a-z0-9_]+$/.test(p)) return p
    }
  } catch (e) {}
  return null
}
function titleOf(b) {
  try { return strip(b.currentWindow && b.currentWindow.title) } catch (e) { return '' }
}
function invNames(b) {
  return b.inventory.items().map(i => nameOf(i) || i.name).filter(Boolean)
}
function countByName(b, needle) {
  return b.inventory.items().filter(i => nameOf(i).includes(needle)).reduce((a, i) => a + i.count, 0)
}
function cleanupOps() {
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\n`) } catch (_) {}
  try { fs.writeFileSync(ROOT + '/server-runtime/ops.json', '[]\n') } catch (_) {}
  try { fs.writeFileSync(ROOT + '/login-runtime/ops.json', '[]\n') } catch (_) {}
}
async function waitWindow(b, pred, ms = 4500) {
  const t0 = Date.now()
  while (Date.now() - t0 < ms) {
    const tit = titleOf(b)
    if (b.currentWindow && (!pred || pred(tit))) return tit
    await wait(120)
  }
  return titleOf(b)
}
async function clickSlot(b, slot, ms = 2800) {
  try { b.setControlState('sneak', false) } catch (_) {}
  await wait(150)
  if (!b.currentWindow) { console.log('clickSlot no window', slot); return false }
  try {
    await Promise.race([
      b.clickWindow(slot, 0, 0),
      wait(ms).then(() => { throw new Error('timeout') })
    ])
  } catch (e) { console.log('clickSlot', slot, e.message) }
  await wait(1000)
  return true
}
function slotName(b, slot) {
  try {
    const it = b.currentWindow && b.currentWindow.slots[slot]
    return it ? (nameOf(it) || it.name) : ''
  } catch (e) { return '' }
}
function slotLore(b, slot) {
  try {
    const it = b.currentWindow && b.currentWindow.slots[slot]
    return it ? loreOf(it) : []
  } catch (e) { return [] }
}

;(async () => {
  // ---------- 1) 静态 ----------
  const lifeP = fs.readFileSync(ROOT + '/plugins/CoreRpg/life.yml', 'utf8')
  const lifeS = fs.readFileSync(ROOT + '/CoreRpg/src/main/resources/life.yml', 'utf8')
  const lifeMenu = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_life.yml', 'utf8')
  const hubY = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_hub.yml', 'utf8')
  const setY = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_set.yml', 'utf8')
  const niY = fs.readFileSync(ROOT + '/plugins/NeigeItems/Items/ember-gear-offhand.yml', 'utf8')

  const dualSame = lifeP === lifeS
  const wardBlk = /offhand_ward:\s*\n(?:[ \t]+.+\n)*?[ \t]+coin:\s*80\s*\n(?:[ \t]+.+\n)*?[ \t]+weekly:\s*1\s*\n(?:[ \t]+.+\n)*?[ \t]+give:\s*\{\s*acc_ember_offhand_ward:\s*1\s*\}/.test(lifeP)
    || (/offhand_ward:[\s\S]*?coin:\s*80[\s\S]*?weekly:\s*1[\s\S]*?give:\s*\{\s*acc_ember_offhand_ward:\s*1\s*\}/.test(lifeP)
        && !/offhand_ward:[\s\S]*?life_level:/.test(lifeP.split('offhand_vita:')[0]))
  // simpler structured checks
  const wardSec = (lifeP.match(/offhand_ward:[\s\S]*?(?=offhand_vita:|consumables:)/) || [])[0] || ''
  const vitaSec = (lifeP.match(/offhand_vita:[\s\S]*?(?=consumables:)/) || [])[0] || ''
  const wardOk = /coin:\s*80/.test(wardSec) && /weekly:\s*1/.test(wardSec)
    && /acc_ember_offhand_ward:\s*1/.test(wardSec) && !/life_level:/.test(wardSec)
  const vitaOk = /coin:\s*80/.test(vitaSec) && /weekly:\s*1/.test(vitaSec)
    && /acc_ember_offhand_vita:\s*1/.test(vitaSec) && !/life_level:/.test(vitaSec)
  note('static_life_offers', wardOk && vitaOk && dualSame,
    `ward=${wardOk} vita=${vitaOk} dual=${dualSame}`)

  const oIcon = /O:\s*\n[\s\S]*?余烬守腕[\s\S]*?corerpg life buy offhand_ward/.test(lifeMenu)
  const wIcon = /W:\s*\n[\s\S]*?余烬生坠[\s\S]*?corerpg life buy offhand_vita/.test(lifeMenu)
  const layoutOW = /#OV#I#WZ#/.test(lifeMenu)
  const noTeach = !/手打|输入\s*\/corerpg|教.*\/corerpg/.test(lifeMenu)
  // lore blocks for O/W must not tell player to type /corerpg
  const oLore = (lifeMenu.match(/O:\s*\n[\s\S]*?lore:(\[[^\]]+\]|[\s\S]*?actions:)/) || [])[1] || ''
  const wLore = (lifeMenu.match(/W:\s*\n[\s\S]*?lore:(\[[^\]]+\]|[\s\S]*?actions:)/) || [])[1] || ''
  const loreNoCmd = !/\/corerpg/.test(oLore) && !/\/corerpg/.test(wLore)
  note('static_ember_life_menu', oIcon && wIcon && layoutOW && noTeach && loreNoCmd,
    `O=${oIcon} W=${wIcon} layout=${layoutOW} loreNoCmd=${loreNoCmd}`)

  const hubHalf = /工坊灰粮可购/.test(hubY)
  const setHalf = /工坊灰粮可购/.test(setY)
  const tipFiles = execSync('git -C ' + ROOT + ' show --name-only --pretty=format: 58baffe', { encoding: 'utf8' })
  const niUntouched = !/NeigeItems|ember-gear-offhand/.test(tipFiles)
  const combatUntouched = !/ash.?brace|灰箍|stamina|体力门|ember.?blade|护符|forge\.yml|ember_shop/i.test(tipFiles)
  const niAttrs = /物理防御:\s*\+2/.test(niY) && /生命力:\s*\+8/.test(niY)
  note('static_side_evidence', hubHalf && setHalf && niUntouched && combatUntouched && niAttrs,
    `hub=${hubHalf} set=${setHalf} niUntouched=${niUntouched} combatUntouched=${combatUntouched} niAttrs=${niAttrs}`)

  out.evidence.static = { wardOk, vitaOk, dualSame, oIcon, wIcon, layoutOW, hubHalf, setHalf, tipFiles: tipFiles.trim().split('\n').filter(Boolean) }

  // ---------- 热更 ----------
  try {
    fs.writeFileSync(CONSOLE, 'corerpg reload\n')
    await wait(2500)
    fs.writeFileSync(CONSOLE, 'trmenu reload\n')
    await wait(3500)
    out.evidence.reload = 'corerpg reload + trmenu reload via console.in'
  } catch (e) {
    out.evidence.reload = 'fail: ' + e.message
  }

  // ---------- 进服 ----------
  let op, b
  try {
    op = await joinPlay(OP, { log: false })
    await wait(1500)
    try { fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(1200) } catch (e) {}
    const c = async (cmd, ms = 1000) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }
    await c(`/lp user ${OP} parent add admin`, 900)
    await c(`/lp user ${OP} permission set corerpg.admin true`, 700)
    await c(`/op ${OP}`, 600)

    b = await joinPlay(USER, { log: false })
    const chat = []
    b.on('message', m => { const s = m.toString(); chat.push(s); console.log('[u]', strip(s)) })
    await wait(2500)
    await c(`/lp user ${USER} permission set corerpg.use true`, 600)
    await c(`/clear ${USER}`, 900)
    await c(`/mvtp ${USER} ember_hub`, 1800)
    await c(`/corerpg coin give ${USER} 500`, 1200)
    await wait(600)

    // open hub → life
    try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
    await wait(300)
    b.chat('/ember')
    let tit = await waitWindow(b, t => /冒险枢纽|余烬|枢纽/.test(t) || t.length > 0, 4500)
    out.evidence.hub_title = tit
    const hubLifeName = slotName(b, HUB_LIFE)
    note('menu_hub_life_icon', /补给|生活/.test(hubLifeName), `slot17=${hubLifeName}`)
    await clickSlot(b, HUB_LIFE, 3000)
    tit = await waitWindow(b, t => /补给|生活/.test(t), 4500)
    if (!/补给|生活/.test(tit)) {
      // fallback console open
      fs.writeFileSync(CONSOLE, `trmenu open ember_life ${USER}\n`)
      await wait(1800)
      tit = await waitWindow(b, t => /补给|生活/.test(t), 4000)
      out.evidence.life_open_via = 'console_fallback'
    } else {
      out.evidence.life_open_via = 'hub_click'
    }
    out.evidence.life_title = tit
    note('menu_life_open', /补给|生活/.test(tit), `title=${tit}`)

    // inspect O/W icons
    const wardIcon = slotName(b, LIFE_WARD)
    const vitaIcon = slotName(b, LIFE_VITA)
    const breadIcon = slotName(b, LIFE_BREAD)
    const wardLore = slotLore(b, LIFE_WARD).join('|')
    const vitaLore = slotLore(b, LIFE_VITA).join('|')
    note('menu_icons_OW', /守腕/.test(wardIcon) && /生坠/.test(vitaIcon),
      `O=${wardIcon} W=${vitaIcon} loreO=${wardLore} loreW=${vitaLore}`)
    note('menu_old_bread_present', /面包/.test(breadIcon), `A=${breadIcon}`)
    note('menu_lore_no_hand_cmd', !/\/corerpg/.test(wardLore) && !/\/corerpg/.test(vitaLore),
      `wardLore has /corerpg? ${/\/corerpg/.test(wardLore)}`)

    // buy ward
    const n0 = chat.length
    const wardBefore = countByName(b, '守腕')
    await clickSlot(b, LIFE_WARD, 3000)
    await wait(800)
    // menu may close; reopen if needed for second buy
    const buy1 = chat.slice(n0).map(strip).join(' || ')
    out.evidence.buy_ward_chat = buy1
    const wardAfter = countByName(b, '守腕')
    const wardGot = wardAfter > wardBefore || /守腕/.test(buy1) || /offhand_ward|已购|→/.test(buy1)
    note('menu_buy_ward', wardGot && !/不足|已用完/.test(buy1.split('||').filter(x => /生活|守腕/.test(x)).join('')),
      `before=${wardBefore} after=${wardAfter} chat=${buy1.slice(0, 240)} inv=${invNames(b).join('|')}`)

    // ensure life menu open for vita
    if (!/补给|生活/.test(titleOf(b))) {
      fs.writeFileSync(CONSOLE, `trmenu open ember_life ${USER}\n`)
      await wait(1600)
      await waitWindow(b, t => /补给|生活/.test(t), 3500)
    }
    const n1 = chat.length
    const vitaBefore = countByName(b, '生坠')
    await clickSlot(b, LIFE_VITA, 3000)
    await wait(800)
    const buy2 = chat.slice(n1).map(strip).join(' || ')
    out.evidence.buy_vita_chat = buy2
    const vitaAfter = countByName(b, '生坠')
    const vitaGot = vitaAfter > vitaBefore || /生坠/.test(buy2)
    note('menu_buy_vita', vitaGot, `before=${vitaBefore} after=${vitaAfter} chat=${buy2.slice(0, 240)}`)

    // weekly limit: buy ward again should refuse
    if (!/补给|生活/.test(titleOf(b))) {
      fs.writeFileSync(CONSOLE, `trmenu open ember_life ${USER}\n`)
      await wait(1600)
      await waitWindow(b, t => /补给|生活/.test(t), 3500)
    }
    const n2 = chat.length
    const wardMid = countByName(b, '守腕')
    await clickSlot(b, LIFE_WARD, 3000)
    await wait(800)
    const buy3 = chat.slice(n2).map(strip).join(' || ')
    out.evidence.weekly_chat = buy3
    const weeklyOk = /本周次数已用完|每周/.test(buy3) || countByName(b, '守腕') === wardMid
    // stronger: expect the exact weekly message
    const weeklyMsg = /本周次数已用完/.test(buy3)
    note('menu_weekly_limit', weeklyMsg, `chat=${buy3.slice(0, 240)} count=${countByName(b, '守腕')}`)

    // old offers don't regress: bread still buyable (or at least icon present + command path)
    if (!/补给|生活/.test(titleOf(b))) {
      fs.writeFileSync(CONSOLE, `trmenu open ember_life ${USER}\n`)
      await wait(1600)
      await waitWindow(b, t => /补给|生活/.test(t), 3500)
    }
    const breadStill = /面包/.test(slotName(b, LIFE_BREAD))
    const n3 = chat.length
    const breadBefore = b.inventory.items().filter(i => /bread|面包/i.test(nameOf(i) || i.name)).reduce((a, i) => a + i.count, 0)
    await clickSlot(b, LIFE_BREAD, 3000)
    await wait(800)
    const buyBread = chat.slice(n3).map(strip).join(' || ')
    out.evidence.buy_bread_chat = buyBread
    const breadAfter = b.inventory.items().filter(i => /bread|面包/i.test(nameOf(i) || i.name) || i.name === 'bread').reduce((a, i) => a + i.count, 0)
    const breadOk = breadStill && (breadAfter > breadBefore || /面包|bread|生活/.test(buyBread))
    note('menu_old_offers_ok', breadOk, `icon=${slotName(b, LIFE_BREAD)} before=${breadBefore} after=${breadAfter} chat=${buyBread.slice(0, 200)}`)

    // inventory evidence NI ids if lore has them
    const ids = b.inventory.items().map(i => niId(i)).filter(Boolean)
    out.evidence.inv_ni_ids = ids
    out.evidence.inv_names = invNames(b)

    try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
    await c(`/lp user ${OP} parent remove admin`, 700)
    await c(`/deop ${OP}`, 600)
  } catch (e) {
    console.log('MENU_ERR', e.stack || e.message)
    out.menu_skip = true
    note('menu_runtime', false, 'exception: ' + (e.message || e))
  } finally {
    cleanupOps()
    try { if (b) b.quit() } catch (_) {}
    try { if (op) op.quit() } catch (_) {}
    await wait(800)
  }

  out.ops = JSON.parse(fs.readFileSync(ROOT + '/server-runtime/ops.json', 'utf8'))
  fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
  console.log('OUT', OUT, 'pass=' + out.pass)
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.log('FATAL', e.stack)
  cleanupOps()
  process.exit(2)
})
setTimeout(() => { console.log('TIMEOUT'); cleanupOps(); process.exit(2) }, 180000)
