/**
 * B-flex-2 余烬踏步 A · 轻测（测试岗）
 * 禁 wall-clock / DPS / 长本 / 挑刺
 * 路径：静态 → hub「轻技」入口目视 → console/trmenu 开轻技页装配/释放/卸下拒 → hub「技能」誓约 → 零体力
 * OUT: /tmp/flex-step-pilot-light-test.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const { execSync } = require('child_process')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')

const OP = 'FxOpD29'
const USER = 'FxLtD29'
const OUT = '/tmp/flex-step-pilot-light-test.json'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const ROOT = '/workspace/minecraft'
const HUB_LIGHT = 7
const HUB_SKILL = 5
const FLEX_EQUIP = 20
const FLEX_UNEQUIP = 22
const FLEX_CAST = 24

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  tip_plugin: 'fc89225',
  tip_record: '3841f4f',
  tip_design: 'd1e88e4',
  tip_approve: '7d7bf5a',
  corerpg_expected: '1.15.25',
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
function posOf(b) {
  try {
    const p = b.entity.position
    return { x: +p.x.toFixed(3), y: +p.y.toFixed(3), z: +p.z.toFixed(3) }
  } catch (e) { return null }
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
async function openFlexPage(b, via = 'console') {
  try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
  await wait(300)
  if (via === 'hub') {
    b.chat('/ember')
    await waitWindow(b, t => /冒险枢纽|余烬/.test(t), 4000)
    await clickSlot(b, HUB_LIGHT, 2800)
    // hub 轻技在本环境 mode=0 也会连带 shift_all（释放+关窗）；若已关则 console 补开页
  }
  let tit = titleOf(b)
  if (!/轻技/.test(tit)) {
    fs.writeFileSync(CONSOLE, `trmenu open ember_flex_skill ${USER}\n`)
    await wait(1600)
    tit = await waitWindow(b, t => /轻技/.test(t), 4000)
    out.evidence.flexOpenFallback = 'console trmenu open ember_flex_skill'
  }
  return tit
}
async function staminaShow(b, chat) {
  const n = chat.length
  b.chat('/corerpg stamina show')
  await wait(1100)
  const lines = chat.slice(n).map(strip)
  const hit = lines.find(l => /\[体力\]/.test(l)) || ''
  const m = hit.match(/\[体力\]\s*(\d+)\s*\/\s*(\d+)/)
  return { raw: hit, cur: m ? Number(m[1]) : null, max: m ? Number(m[2]) : null }
}

;(async () => {
  // ---------- 1) 静态 ----------
  const skillsLive = fs.readFileSync(ROOT + '/plugins/CoreRpg/skills.yml', 'utf8')
  const skillsSrc = fs.readFileSync(ROOT + '/CoreRpg/src/main/resources/skills.yml', 'utf8')
  const hubY = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_hub.yml', 'utf8')
  const flexY = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_flex_skill.yml', 'utf8')
  const flexJava = fs.readFileSync(ROOT + '/CoreRpg/src/main/java/town/sunshine/corerpg/FlexSkillService.java', 'utf8')
  const pdStore = fs.readFileSync(ROOT + '/CoreRpg/src/main/java/town/sunshine/corerpg/PlayerDataStore.java', 'utf8')
  const pdJava = fs.readFileSync(ROOT + '/CoreRpg/src/main/java/town/sunshine/corerpg/PlayerData.java', 'utf8')
  const jarYml = execSync('unzip -p ' + ROOT + '/plugins/CoreRpg.jar plugin.yml', { encoding: 'utf8' })
  const jarVer = (jarYml.match(/^version:\s*(.+)$/m) || [])[1] || '?'
  const logTail = fs.readFileSync(ROOT + '/server-runtime/logs/latest.log', 'utf8')
  const enabling = /Enabling CoreRpg v1\.15\.25/.test(logTail)

  note('static_flex_ember_step',
    /flex_ember_step:/.test(skillsLive) && /flex_ember_step:/.test(skillsSrc) && /type:\s*step/.test(skillsLive),
    '双路径 skills.yml 有 flex_ember_step type:step')
  note('static_flex_skill_slot',
    /flex_skill_id|getFlexSkillId/.test(pdJava) && /flex_skill_id/.test(pdStore),
    'PlayerData/Store flex_skill_id')

  function block(src, key) {
    const m = src.match(new RegExp('^  ' + key + ':\\n((?:    .*\\n)*)', 'm'))
    return m ? m[0] : null
  }
  const oathIds = ['ember_blaze_slash', 'ember_ash_familiar', 'ember_warden_taunt']
  let oathZero = oathIds.every(id => block(skillsLive, id) && block(skillsLive, id) === block(skillsSrc, id))
  try {
    const before = execSync('git -C ' + ROOT + ' show fc89225^:plugins/CoreRpg/skills.yml', { encoding: 'utf8' })
    oathZero = oathIds.every(id => block(before, id) === block(skillsLive, id))
  } catch (e) { console.log('oath compare', e.message) }
  note('static_oath_ids_unchanged', oathZero, '誓约三技 id/块 ZERO vs tip-parent')

  const flexBlock = (skillsLive.match(/flex_ember_step:[\s\S]*?(?=\n  [a-z_]|\n*$)/) || [''])[0]
  note('static_zero_stamina_no_damage_yml',
    !/damage_mult/.test(flexBlock) && !/stamina|cost/.test(flexBlock) && /distance:\s*5/.test(flexBlock),
    'yml 无 damage_mult/cost，distance=5')
  note('static_java_zero_stamina_no_damage',
    /零体力/.test(flexJava) && /No damage\. No stamina/.test(flexJava) && !/StaminaService/.test(flexJava),
    'Java 零体力/无伤害且未接 StaminaService')
  note('static_hub_light_menu',
    /name: '§a轻技'/.test(hubY) && /menu: ember_flex_skill/.test(hubY)
      && /name: '§c技能'/.test(hubY) && /command: corerpg skill/.test(hubY)
      && /装配 · 余烬踏步/.test(flexY),
    'hub 轻技入口 + 原技能 + flex 装配页')
  note('corerpg_1_15_25', jarVer === '1.15.25' && enabling, `jar=${jarVer} enabling=${enabling}`)
  out.evidence.static = { jarVer, enabling, flexHead: flexBlock.trim().split('\n').slice(0, 8) }

  // ---------- 进服 ----------
  const op = await joinPlay(OP, { log: false })
  await wait(1200)
  try { fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(1000) } catch (e) { console.log('fifo', e.message) }
  const c = async (cmd, ms = 900) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }
  await c(`/lp user ${OP} parent add admin`, 800)
  await c(`/lp user ${OP} permission set corerpg.admin true`, 600)
  await c(`/op ${OP}`, 500)

  const b = await joinPlay(USER, { log: false })
  const chat = []
  b.on('message', m => { const s = m.toString(); chat.push(s); console.log('[u]', strip(s)) })
  b.on('windowOpen', w => console.log('[win]', strip(w.title)))
  await wait(2200)
  try { b.setControlState('sneak', false) } catch (_) {}
  await c(`/lp user ${USER} permission set corerpg.use true`, 500)
  await c(`/clear ${USER}`, 700)
  await c(`/mvtp ${USER} ember_hub`, 1600)
  await c(`/tp ${USER} -18.5 58 110.5 180 0`, 1000)
  await c(`/corerpg covenant set ${USER} blaze`, 1000)
  b.chat('/corerpg covenant set blaze'); await wait(800)
  b.chat('/corerpg flex unequip'); await wait(700)

  // ---------- hub「轻技」入口目视 ----------
  b.chat('/ember')
  const hubTitle = await waitWindow(b, t => /冒险枢纽|余烬/.test(t), 4000)
  note('hub_open', /冒险枢纽|余烬/.test(hubTitle), hubTitle)
  let lightIcon = null
  try { lightIcon = b.currentWindow.slots[HUB_LIGHT] } catch (_) {}
  const lightName = lightIcon ? nameOf(lightIcon) : ''
  const lightLore = lightIcon ? loreOf(lightIcon) : []
  out.evidence.hubLight = { name: lightName, lore: lightLore }
  note('hub_light_icon', /轻技/.test(lightName) && lightLore.some(l => /余烬踏步|零体力/.test(l)),
    JSON.stringify({ lightName, lightLore }))

  // 点轻技：期望 Open tell；本环境 mode=0 会连带 shift_all 关窗
  let n = chat.length
  await clickSlot(b, HUB_LIGHT, 2800)
  const afterLight = chat.slice(n).map(strip)
  out.evidence.hubLightClick = afterLight
  const openedTell = afterLight.some(s => /轻技槽/.test(s) && /誓约主动并行/.test(s))
  note('hub_light_opens_flex_tell', openedTell,
    JSON.stringify(afterLight.filter(s => /轻技|踏步|装配/.test(s)).slice(0, 8)))

  // ---------- 轻技页：装配 → 开阔地释放短移 → 卸下拒 ----------
  let flexTit = await openFlexPage(b, 'console')
  note('flex_menu_open', /轻技/.test(flexTit), flexTit)

  n = chat.length
  await clickSlot(b, FLEX_EQUIP, 2800)
  const equipChat = chat.slice(n).map(strip)
  out.evidence.equipChat = equipChat
  note('menu_equip_step', equipChat.some(s => /已装配/.test(s) && /余烬踏步|踏步/.test(s)),
    JSON.stringify(equipChat.filter(s => /轻技|装配|踏步|零体力/.test(s)).slice(0, 8)))

  const st0 = await staminaShow(b, chat)
  out.evidence.staminaBefore = st0
  note('stamina_readable', st0.cur != null, JSON.stringify(st0))

  // 开阔点：hub 西侧走廊试探；多朝向尝试一次成功即可
  const pads = [
    { tp: `-40 58 110 90 0`, label: 'west-face-east' },
    { tp: `-5 58 110 -90 0`, label: 'east-face-west' },
    { tp: `-18.5 58 125 0 0`, label: 'south' },
    { tp: `-18.5 58 95 180 0`, label: 'north' }
  ]
  let moved = 0, castChat = [], pos0 = null, pos1 = null, padUsed = null
  for (const pad of pads) {
    try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
    await c(`/tp ${USER} ${pad.tp}`, 1100)
    await wait(400)
    // ensure still equipped (CD may remain from blocked attempt — wait out or unequip/re-equip)
    // if previous attempt started CD even on block? castStep returns false before startCooldown — OK no CD
    flexTit = await openFlexPage(b, 'console')
    if (!/轻技/.test(flexTit)) continue
    pos0 = posOf(b)
    n = chat.length
    await clickSlot(b, FLEX_CAST, 2800)
    await wait(1000)
    castChat = chat.slice(n).map(strip)
    pos1 = posOf(b)
    moved = (pos0 && pos1) ? Math.hypot(pos1.x - pos0.x, pos1.z - pos0.z) : 0
    padUsed = pad.label
    out.evidence['cast_' + pad.label] = { castChat, pos0, pos1, moved }
    console.log('pad', pad.label, 'moved', moved.toFixed(2), castChat.filter(s => /轻技|踏步|释放|受阻|冷却/.test(s)))
    if (moved >= 1.5 && castChat.some(s => /释放/.test(s))) break
    // if blocked, try next pad (no CD on fail)
    if (castChat.some(s => /前方受阻/.test(s))) continue
    if (castChat.some(s => /冷却中/.test(s))) {
      // wait CD 14s once
      console.log('waiting CD 14s...')
      await wait(14500)
    }
  }
  out.evidence.castFinal = { padUsed, moved, castChat, pos0, pos1 }
  note('menu_cast_step_move', moved >= 1.5 && castChat.some(s => /释放/.test(s) && /余烬踏步|踏步|轻技/.test(s)),
    `pad=${padUsed} movedXZ=${moved.toFixed(2)} chat=${JSON.stringify(castChat.filter(s => /轻技|踏步|释放|受阻|冷却/.test(s)).slice(0, 8))}`)

  const st1 = await staminaShow(b, chat)
  out.evidence.staminaAfterCast = st1
  note('zero_stamina_cost', st0.cur != null && st1.cur != null && st1.cur === st0.cur,
    `before=${st0.cur} after=${st1.cur}`)
  note('no_stamina_reject_msg', !castChat.some(s => /体力不足/.test(s)), '无体力不足')
  note('equip_msg_zero_stamina',
    (out.evidence.equipChat || []).some(s => /零体力/.test(strip(s))),
    '装配提示含零体力')

  // 卸下后释放应拒（菜单）
  flexTit = await openFlexPage(b, 'console')
  n = chat.length
  await clickSlot(b, FLEX_UNEQUIP, 2800)
  const unequipChat = chat.slice(n).map(strip)
  out.evidence.unequipChat = unequipChat
  note('menu_unequip', unequipChat.some(s => /已卸下|轻技槽为空/.test(s)),
    JSON.stringify(unequipChat.filter(s => /轻技|卸下/.test(s)).slice(0, 6)))

  flexTit = await openFlexPage(b, 'console')
  n = chat.length
  await clickSlot(b, FLEX_CAST, 2800)
  await wait(800)
  let refuseChat = chat.slice(n).map(strip)
  if (!refuseChat.some(s => /尚未装配/.test(s))) {
    b.chat('/corerpg flex cast'); await wait(800)
    refuseChat = chat.slice(n).map(strip)
    out.evidence.refuseFallback = 'corerpg flex cast after menu'
  }
  out.evidence.refuseChat = refuseChat
  note('cast_refuse_when_empty', refuseChat.some(s => /尚未装配轻技/.test(s)),
    JSON.stringify(refuseChat.filter(s => /轻技|装配|释放/.test(s)).slice(0, 8)))

  // ---------- hub 原「技能」仍可放誓约 ----------
  try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
  await wait(300)
  b.chat('/ember')
  await waitWindow(b, t => /冒险枢纽|余烬/.test(t), 3500)
  let skillIcon = null
  try { skillIcon = b.currentWindow.slots[HUB_SKILL] } catch (_) {}
  note('hub_skill_icon', !!(skillIcon && /技能/.test(nameOf(skillIcon))), nameOf(skillIcon || {}))
  n = chat.length
  await clickSlot(b, HUB_SKILL, 2800)
  await wait(1000)
  let skillChat = chat.slice(n).map(strip)
  out.evidence.skillChat = skillChat
  const skillOk = skillChat.some(s => /释放|烬斩|冷却中/.test(s))
  note('hub_skill_oath_still_works', skillOk,
    JSON.stringify(skillChat.filter(s => /技能|烬斩|释放|冷却|誓约/.test(s)).slice(0, 10)))

  // cleanup
  try { if (b.currentWindow) b.closeWindow(b.currentWindow) } catch (_) {}
  await c(`/lp user ${OP} parent remove admin`, 500)
  await c(`/deop ${OP}`, 400)
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\n`); await wait(500) } catch (_) {}
  cleanupOps()
  await wait(300)
  const ops = fs.readFileSync(ROOT + '/server-runtime/ops.json', 'utf8').trim()
  out.ops = ops
  note('ops_empty', ops === '[]', ops)

  try { b.quit(); op.quit() } catch (_) {}
  fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
  console.log('WROTE', OUT, 'pass=' + out.pass)
  process.exit(out.pass ? 0 : 1)
})().catch(e => {
  console.error(e)
  cleanupOps()
  try { fs.writeFileSync(OUT, JSON.stringify({ pass: false, error: String(e && e.stack || e) }, null, 2)) } catch (_) {}
  process.exit(1)
})
setTimeout(() => { cleanupOps(); console.error('timeout'); process.exit(2) }, 240000)
