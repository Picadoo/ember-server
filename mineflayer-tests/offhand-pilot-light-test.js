/**
 * B-flex-1 副手试点 A · 轻测（测试岗）
 * 禁 wall-clock / DPS / 长本；仅：静态旁证 + 副手装卸属性一眼 + hub/set 目视
 * OUT: /tmp/offhand-pilot-light-test.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')

const OP = 'OfOpA29'
const USER = 'OfLtA29'
const OUT = '/tmp/offhand-pilot-light-test.json'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  tip_plugin: '08b4cd4',
  tip_ni: '52f817a',
  tip_design: '7dda194',
  tip_approve: 'eb3c843',
  corerpg_expected: '1.15.24',
  op: OP,
  user: USER,
  checks: {},
  evidence: {},
  pass: true
}

function note(k, ok, detail) {
  out.checks[k] = { ok: !!ok, detail: String(detail || '') }
  if (!ok) out.pass = false
  console.log((ok ? 'PASS' : 'FAIL') + ' ' + k + ' :: ' + detail)
}

function nameOf(it) {
  try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' }
}
function findByName(b, needle) {
  return b.inventory.items().find(i => nameOf(i).includes(needle))
}
function offhandItem(b) {
  try { return b.inventory.slots[45] || null } catch (e) { return null }
}
function parseStats(chatTail) {
  const lines = chatTail.map(strip)
  const main = lines.filter(l => l.includes('[属性]') && l.includes('生命')).slice(-1)[0] || ''
  const raw = lines.filter(l => l.includes('raw') && (l.includes('phys_') || l.includes('{'))).slice(-1)[0] || ''
  const hp = (main.match(/生命\s+([\d.]+)/) || [])[1]
  const defPct = (main.match(/减伤\s+([\d.]+)/) || [])[1]
  const atk = (main.match(/攻击\s*\+\s*([\d.]+)/) || [])[1]
  let physDef = null
  const m = raw.match(/phys_defense[=:]?\s*([\d.]+)/)
  if (m) physDef = Number(m[1])
  // also try Map.toString style phys_defense=2.0
  if (physDef == null) {
    const m2 = raw.match(/phys_defense=([\d.]+)/)
    if (m2) physDef = Number(m2[1])
  }
  return { main, raw, hp: hp != null ? Number(hp) : null, defPct: defPct != null ? Number(defPct) : null, atk: atk != null ? Number(atk) : null, physDef }
}

async function statsOf(b, chat) {
  const n = chat.length
  b.chat('/corerpg stats')
  await wait(1200)
  return parseStats(chat.slice(n))
}

;(async () => {
  // ---------- 1) 静态 ----------
  const cfgLive = fs.readFileSync('/workspace/minecraft/plugins/CoreRpg/config.yml', 'utf8')
  const cfgSrc = fs.readFileSync('/workspace/minecraft/CoreRpg/src/main/resources/config.yml', 'utf8')
  const setY = fs.readFileSync('/workspace/minecraft/plugins/TrMenu/menus/ember_set.yml', 'utf8')
  const hubY = fs.readFileSync('/workspace/minecraft/plugins/TrMenu/menus/ember_hub.yml', 'utf8')
  const niY = fs.readFileSync('/workspace/minecraft/plugins/NeigeItems/Items/ember-gear-offhand.yml', 'utf8')
  const jarYml = require('child_process').execSync('unzip -p /workspace/minecraft/plugins/CoreRpg.jar plugin.yml', { encoding: 'utf8' })
  const jarVer = (jarYml.match(/^version:\s*(.+)$/m) || [])[1] || '?'
  const logTail = fs.readFileSync('/workspace/minecraft/server-runtime/logs/latest.log', 'utf8')
  const enabling = /Enabling CoreRpg v1\.15\.24/.test(logTail)
  const wl = /offhand:[\s\S]*?acc_ember_offhand_ward[\s\S]*?acc_ember_offhand_vita/.test(cfgLive)
    && /offhand:[\s\S]*?acc_ember_offhand_ward[\s\S]*?acc_ember_offhand_vita/.test(cfgSrc)
  note('static_whitelist_ward_vita', wl, '双路径 stats.offhand 含 ward/vita')
  note('static_hub_set_copy', /副手饰品/.test(setY) && /副手饰品/.test(hubY) && /放副手槽生效/.test(setY), 'hub/set 有副手说明')
  note('static_ni_ids', /acc_ember_offhand_ward:/.test(niY) && /acc_ember_offhand_vita:/.test(niY)
    && /物理防御:\s*\+2/.test(niY) && /生命力:\s*\+8/.test(niY), 'NI ward/vita lore')
  note('corerpg_1_15_24', jarVer === '1.15.24' && enabling, `jar=${jarVer} enabling=${enabling}`)
  out.evidence.static = { jarVer, enabling, wl }

  // ---------- 进服 ----------
  const op = await joinPlay(OP, { log: false })
  await wait(1500)
  try { fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(1200) } catch (e) { console.log('fifo', e.message) }
  const c = async (cmd, ms = 1000) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }
  await c(`/lp user ${OP} parent add admin`, 900)
  await c(`/lp user ${OP} permission set corerpg.admin true`, 700)
  await c(`/op ${OP}`, 600)

  const b = await joinPlay(USER, { log: false })
  const chat = []
  b.on('message', m => { const s = m.toString(); chat.push(s); console.log('[u]', s) })
  await wait(2500)
  await c(`/lp user ${USER} permission set corerpg.use true`, 600)
  await c(`/clear ${USER}`, 900)
  await c(`/mvtp ${USER} ember_hub`, 1800)
  await wait(800)

  // baseline
  let base = await statsOf(b, chat)
  out.evidence.baseline = base
  note('baseline_stats', base.hp != null && base.hp >= 20, JSON.stringify(base))

  // ---------- 2a) ward 装副手 ----------
  await c(`/clear ${USER}`, 700)
  await c(`/ni give ${USER} acc_ember_offhand_ward 1`, 1600)
  let ward = findByName(b, '守腕') || findByName(b, '余烬守腕')
  note('give_ward', !!ward, `name=${ward ? nameOf(ward) : 'none'} inv=${b.inventory.items().map(i => nameOf(i) || i.name).join('|')}`)
  let wardOn = null, wardOff = null
  if (ward) {
    try { await b.equip(ward, 'off-hand'); await wait(900) } catch (e) { console.log('equip ward', e.message); await wait(500) }
    const oh = offhandItem(b)
    note('equip_ward_offhand', !!(oh && nameOf(oh).includes('守腕')), `off=${oh ? nameOf(oh) : 'empty'}`)
    wardOn = await statsOf(b, chat)
    out.evidence.wardOn = wardOn
    const defOk = (wardOn.physDef != null && wardOn.physDef >= 2)
      || (base.physDef != null && wardOn.physDef != null && wardOn.physDef > base.physDef + 1.5)
      || (wardOn.defPct != null && base.defPct != null && wardOn.defPct > base.defPct + 0.5)
      || (wardOn.raw && /phys_defense=([2-9]|[1-9]\d)/.test(wardOn.raw))
    note('ward_attr_up', defOk, JSON.stringify(wardOn))

    // unequip
    try { await b.unequip('off-hand'); await wait(900) } catch (e) {
      console.log('unequip', e.message)
      // fallback: click offhand into inv
      try {
        const win = b.currentWindow || b.inventory
        if (b.inventory.slots[45]) {
          await b.clickWindow(45, 0, 0); await wait(400)
          await b.clickWindow(b.inventory.firstEmptyContainerSlot?.() ?? 9, 0, 0); await wait(400)
        }
      } catch (e2) { console.log('unequip2', e2.message) }
      await wait(800)
    }
    const oh2 = offhandItem(b)
    note('unequip_ward', !oh2 || oh2.type === -1 || nameOf(oh2) === '', `off=${oh2 ? nameOf(oh2) : 'empty'}`)
    wardOff = await statsOf(b, chat)
    out.evidence.wardOff = wardOff
    const dropped = (wardOn.physDef != null && wardOff.physDef != null && wardOff.physDef < wardOn.physDef - 1.5)
      || (wardOn.defPct != null && wardOff.defPct != null && wardOff.defPct < wardOn.defPct - 0.3)
      || (wardOff.raw && wardOn.raw && wardOff.raw !== wardOn.raw && !/phys_defense=([2-9]|[1-9]\d)/.test(wardOff.raw || ''))
      || (wardOn.physDef >= 2 && (wardOff.physDef == null || wardOff.physDef < 1.5))
    note('ward_attr_drop', dropped, JSON.stringify(wardOff))
  } else {
    note('equip_ward_offhand', false, 'no item')
    note('ward_attr_up', false, 'skip')
    note('unequip_ward', false, 'skip')
    note('ward_attr_drop', false, 'skip')
  }

  // ---------- 2b) vita 装副手 ----------
  await c(`/clear ${USER}`, 700)
  await c(`/ni give ${USER} acc_ember_offhand_vita 1`, 1600)
  let vita = findByName(b, '生坠') || findByName(b, '余烬生坠')
  note('give_vita', !!vita, `name=${vita ? nameOf(vita) : 'none'} inv=${b.inventory.items().map(i => nameOf(i) || i.name).join('|')}`)
  let vitaOn = null, vitaOff = null
  if (vita) {
    try { await b.equip(vita, 'off-hand'); await wait(900) } catch (e) { console.log('equip vita', e.message); await wait(500) }
    const oh = offhandItem(b)
    note('equip_vita_offhand', !!(oh && (nameOf(oh).includes('生坠') || nameOf(oh).includes('余烬生'))), `off=${oh ? nameOf(oh) : 'empty'}`)
    vitaOn = await statsOf(b, chat)
    out.evidence.vitaOn = vitaOn
    const hpOk = vitaOn.hp != null && base.hp != null && vitaOn.hp >= base.hp + 7.5
    note('vita_attr_up', hpOk, `baseHp=${base.hp} on=${vitaOn.hp} ${JSON.stringify(vitaOn)}`)

    try { await b.unequip('off-hand'); await wait(900) } catch (e) {
      console.log('unequip vita', e.message)
      await wait(800)
    }
    const oh2 = offhandItem(b)
    note('unequip_vita', !oh2 || nameOf(oh2) === '', `off=${oh2 ? nameOf(oh2) : 'empty'}`)
    vitaOff = await statsOf(b, chat)
    out.evidence.vitaOff = vitaOff
    const dropped = vitaOn.hp != null && vitaOff.hp != null && vitaOff.hp <= vitaOn.hp - 7.5
    note('vita_attr_drop', dropped, `on=${vitaOn.hp} off=${vitaOff.hp}`)
  } else {
    note('equip_vita_offhand', false, 'no item')
    note('vita_attr_up', false, 'skip')
    note('unequip_vita', false, 'skip')
    note('vita_attr_drop', false, 'skip')
  }

  // ---------- 3) 菜单 hub/set 目视 ----------
  const n0 = chat.length
  b.chat('/ember')
  await wait(1800)
  const hubOpen = chat.slice(n0).some(s => /欢迎回来|冒险枢纽/.test(strip(s))) || !!(b.currentWindow)
  let hubTitle = ''
  try { hubTitle = strip(b.currentWindow && b.currentWindow.title) } catch (_) {}
  out.evidence.hub = { opened: hubOpen, title: hubTitle, tell: chat.slice(n0).map(strip).slice(0, 8) }

  // click 套装 slot 31
  let setTell = []
  try {
    if (b.currentWindow) {
      const n1 = chat.length
      await b.clickWindow(31, 0, 0)
      await wait(2000)
      setTell = chat.slice(n1).map(strip)
    } else {
      // fallback open set directly (仍记路径)
      const n1 = chat.length
      b.chat('/trmenu open ember_set')
      await wait(2000)
      setTell = chat.slice(n1).map(strip)
      out.evidence.menu_fallback = 'trmenu open ember_set'
    }
  } catch (e) {
    console.log('click set', e.message)
    const n1 = chat.length
    b.chat('/trmenu open ember_set')
    await wait(2000)
    setTell = chat.slice(n1).map(strip)
    out.evidence.menu_fallback = 'trmenu open ember_set after click err: ' + e.message
  }
  out.evidence.setTell = setTell
  let setTitle = ''
  try { setTitle = strip(b.currentWindow && b.currentWindow.title) } catch (_) {}
  out.evidence.setTitle = setTitle
  const setCopy = setTell.some(s => /副手饰品/.test(s) && /副手槽/.test(s))
    || /副手/.test(setTitle)
  // also accept hub lore path proven by static + hub open success (目视)
  note('menu_hub_open', hubOpen || /余烬|枢纽/.test(hubTitle), `title=${hubTitle}`)
  note('menu_set_offhand_copy', setCopy || setTell.some(s => /副手/.test(s)), `tells=${JSON.stringify(setTell.slice(0, 6))} title=${setTitle}`)

  try { b.closeWindow(b.currentWindow) } catch (_) {}

  // ---------- cleanup ----------
  await c(`/clear ${USER}`, 500)
  await c(`/lp user ${OP} parent remove admin`, 600)
  await c(`/deop ${OP}`, 600)
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\n`); await wait(800) } catch (_) {}
  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
  try { fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n') } catch (_) {}
  const opsPlay = fs.readFileSync('/workspace/minecraft/server-runtime/ops.json', 'utf8').trim()
  let opsLogin = '[]'
  try { opsLogin = fs.readFileSync('/workspace/minecraft/login-runtime/ops.json', 'utf8').trim() } catch (_) {}
  note('ops_empty', opsPlay === '[]' && opsLogin === '[]', `play=${opsPlay} login=${opsLogin}`)
  out.evidence.ops = { play: opsPlay, login: opsLogin }

  try { b.quit() } catch (_) {}
  try { op.quit() } catch (_) {}
  fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
  console.log('WROTE', OUT, 'pass=' + out.pass)
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.error('FATAL', e)
  try {
    fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
    fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n')
    fs.writeFileSync(CONSOLE, `deop ${OP}\n`)
  } catch (_) {}
  out.pass = false
  out.fatal = String(e && e.stack || e)
  try { fs.writeFileSync(OUT, JSON.stringify(out, null, 2)) } catch (_) {}
  process.exit(1)
})
setTimeout(() => {
  console.log('TIMEOUT')
  try {
    fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
    fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n')
    fs.writeFileSync(CONSOLE, `deop ${OP}\n`)
  } catch (_) {}
  process.exit(2)
}, 180000)
