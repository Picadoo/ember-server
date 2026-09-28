/**
 * B2.45 烬砧灰箍抢口文案 · 薄验收（测试岗）
 * 仅：forge→炼部件悬停主句；part 灰箍A + 说明I 主/旁句
 * 禁改配置 / 禁长测 / 勿宣称 B0.1 已清
 * OUT: /tmp/ash-brace-contention-copy-light.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const { execSync } = require('child_process')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')

const OP = 'AvOpB245'
const USER = 'AvLtB245'
const OUT = '/tmp/ash-brace-contention-copy-light.json'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const ROOT = '/workspace/minecraft'
const TIP = '3066746'

const HUB_FORGE = 26
const FORGE_PART = 22 // P 炼部件
const PART_A = 13     // A 余烬灰箍
const PART_I = 39     // I 说明  layout ####I##Z# row3 → slots 27-35 → I at 31? 
// Layout:
//  - '#########'  0-8
//  - '#   A   #'  9-17  A at 13
//  - '#   C   #'  18-26 C at 22
//  - '####I##Z#'  27-35 I at 31, Z at 34

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  tip_build: TIP,
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
function loreOf(it) {
  try { return (it.nbt.value.display.value.Lore.value.value || []).map(l => strip(l)) } catch (e) { return [] }
}
function titleOf(b) {
  try { return strip(b.currentWindow && b.currentWindow.title) } catch (e) { return '' }
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
  await wait(900)
  return true
}

;(async () => {
  // ---------- 1) 静态 ----------
  const forgeY = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_forge.yml', 'utf8')
  const partMenu = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_part.yml', 'utf8')
  const mainHits = []
  for (const [f, t] of [['ember_forge.yml', forgeY], ['ember_part.yml', partMenu]]) {
    const m = t.match(/优先沉铁锭/g)
    if (m) for (const _ of m) mainHits.push(f)
  }
  const sideHits = (partMenu.match(/有铁锭先炼灰箍沉底/g) || []).length
  const costForge = /碎片×12\s*\+\s*余烬铁锭×2/.test(forgeY)
  const costPart = /余烬碎片 ×12/.test(partMenu) && /余烬铁锭 ×2/.test(partMenu)
  let partDiff = ''
  try {
    partDiff = execSync('git -C ' + ROOT + ' diff ' + TIP + ' -- plugins/CoreRpg/part.yml', { encoding: 'utf8' })
  } catch (e) { partDiff = 'ERR:' + e.message }
  note('rg_main_优先沉铁锭', mainHits.length === 2,
    `count=${mainHits.length} files=${JSON.stringify(mainHits)}`)
  note('rg_side_有铁锭先炼灰箍沉底', sideHits === 1, `count=${sideHits} in ember_part.yml`)
  note('cost_x12_x2_kept', costForge && costPart,
    `forge=${costForge} part=${costPart}`)
  note('part_yml_no_diff', partDiff.trim() === '',
    partDiff.trim() === '' ? 'ZERO vs tip ' + TIP : partDiff.slice(0, 200))
  out.evidence.static = { mainHits, sideHits, costForge, costPart, partDiffEmpty: partDiff.trim() === '' }

  // ---------- 进服菜单轻测 ----------
  cleanupOps()
  const op = await joinPlay(OP, { log: false })
  await wait(1200)
  try { fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(1000) } catch (e) { console.log('fifo', e.message) }
  const c = async (cmd, ms = 900) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }
  await c(`/lp user ${OP} parent add admin`, 800)
  await c(`/op ${OP}`, 500)

  const b = await joinPlay(USER, { log: false })
  b.on('message', m => console.log('[u]', strip(m.toString())))
  b.on('windowOpen', w => console.log('[win]', strip(w.title)))
  await wait(2200)
  try { b.setControlState('sneak', false) } catch (_) {}
  await c(`/lp user ${USER} permission set corerpg.use true`, 500)
  await c(`/clear ${USER}`, 700)
  await c(`/mvtp ${USER} ember_hub`, 1600)
  await c(`/tp ${USER} -18.5 58 110.5 180 0`, 1000)

  b.chat('/ember')
  let hubTitle = await waitWindow(b, t => /冒险枢纽|余烬/.test(t), 4000)
  note('hub_open', /冒险枢纽|余烬/.test(hubTitle), hubTitle)

  await clickSlot(b, HUB_FORGE, 2800)
  let forgeTitle = await waitWindow(b, t => /锻炉|锻造/.test(t), 4000)
  if (!/锻炉|锻造/.test(forgeTitle)) {
    fs.writeFileSync(CONSOLE, `trmenu open ember_forge ${USER}\n`)
    await wait(1600)
    forgeTitle = await waitWindow(b, t => /锻炉|锻造/.test(t), 4000)
    out.evidence.forgeOpenFallback = 'console trmenu open ember_forge'
  }
  note('forge_open', /锻炉|锻造/.test(forgeTitle), forgeTitle)

  let pIcon = null
  try { pIcon = b.currentWindow && b.currentWindow.slots[FORGE_PART] } catch (_) {}
  const pName = pIcon ? nameOf(pIcon) : ''
  const pLore = pIcon ? loreOf(pIcon) : []
  out.evidence.forgeP = { name: pName, lore: pLore }
  const pMain = pLore.some(l => /优先沉铁锭/.test(l))
  const pCost = pLore.some(l => /×12/.test(l) && /×2/.test(l)) || pLore.some(l => /碎片×12/.test(l))
  note('forge_P_hover_main', /炼部件/.test(pName) && pMain,
    JSON.stringify({ pName, pLore }))
  note('forge_P_cost_visible', pCost, JSON.stringify(pLore.filter(l => /灰箍|×12|×2|碎片/.test(l))))

  await clickSlot(b, FORGE_PART, 2800)
  let partTitle = await waitWindow(b, t => /炼部件/.test(t), 4000)
  if (!/炼部件/.test(partTitle)) {
    fs.writeFileSync(CONSOLE, `trmenu open ember_part ${USER}\n`)
    await wait(1600)
    partTitle = await waitWindow(b, t => /炼部件/.test(t), 4000)
    out.evidence.partOpenFallback = 'console trmenu open ember_part'
  }
  note('part_menu_open', /炼部件/.test(partTitle), partTitle)

  let aIcon = null
  try { aIcon = b.currentWindow && b.currentWindow.slots[PART_A] } catch (_) {}
  const aName = aIcon ? nameOf(aIcon) : ''
  const aLore = aIcon ? loreOf(aIcon) : []
  out.evidence.partA = { name: aName, lore: aLore }
  note('part_A_hover_main', /灰箍/.test(aName) && aLore.some(l => /优先沉铁锭/.test(l)),
    JSON.stringify({ aName, aLore }))
  note('part_A_cost_x12_x2', aLore.some(l => /×12/.test(l)) && aLore.some(l => /×2/.test(l)),
    JSON.stringify(aLore.filter(l => /消耗|×12|×2|碎片|铁锭/.test(l))))

  // find I by name across slots if layout offset differs
  let iIcon = null
  let iSlot = 31
  try {
    if (b.currentWindow) {
      for (let s = 0; s < 36; s++) {
        const it = b.currentWindow.slots[s]
        if (it && /说明/.test(nameOf(it))) { iIcon = it; iSlot = s; break }
      }
      if (!iIcon) iIcon = b.currentWindow.slots[31]
    }
  } catch (_) {}
  const iName = iIcon ? nameOf(iIcon) : ''
  const iLore = iIcon ? loreOf(iIcon) : []
  out.evidence.partI = { slot: iSlot, name: iName, lore: iLore }
  note('part_I_hover_side', /说明/.test(iName) && iLore.some(l => /有铁锭先炼灰箍沉底/.test(l)),
    JSON.stringify({ iSlot, iName, iLore }))

  // ---------- cleanup ----------
  try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
  await c(`/clear ${USER}`, 500)
  await c(`/lp user ${OP} parent remove admin`, 600)
  await c(`/deop ${OP}`, 600)
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\n`); await wait(800) } catch (_) {}
  cleanupOps()
  await wait(400)
  const opsPlay = fs.readFileSync(ROOT + '/server-runtime/ops.json', 'utf8').trim()
  let opsLogin = '[]'
  try { opsLogin = fs.readFileSync(ROOT + '/login-runtime/ops.json', 'utf8').trim() } catch (_) {}
  note('ops_empty', opsPlay === '[]' && (opsLogin === '[]' || opsLogin === ''), `play=${opsPlay} login=${opsLogin}`)
  out.evidence.ops = { play: opsPlay, login: opsLogin }

  fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
  console.log('VERDICT ' + (out.pass ? 'PASS' : 'FAIL'))
  console.log('OUT ' + OUT)
  try { op.quit() } catch (_) {}
  try { b.quit() } catch (_) {}
  await wait(500)
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.log('ERR', e.stack)
  cleanupOps()
  try { fs.writeFileSync(OUT, JSON.stringify({ ...out, err: String(e.stack) }, null, 2)) } catch (_) {}
  process.exit(1)
})
setTimeout(() => { console.log('TIMEOUT'); cleanupOps(); process.exit(2) }, 120000)
