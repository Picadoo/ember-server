/**
 * B-flex-4 踏步少开菜单热键 · 潜行+Q · 薄验收（测试岗）
 * PASS: 静态 + sneak+Q 短移/CD · 裸Q丢物 · 副手裸F只换手 · 菜单装配/释放/lore
 * 禁 wall-clock / DPS / 挑刺；勿宣称 B0.1 已清；本岗不改配置
 * OUT: /tmp/flex-hotkey-sneak-drop-light-test.json
 */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const { execSync } = require('child_process')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')

const OP = 'FxHkOp29'
const USER = 'FxHkU29'
const OUT = '/tmp/flex-hotkey-sneak-drop-light-test.json'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const ROOT = '/workspace/minecraft'
const FLEX_EQUIP = 20
const FLEX_CAST = 24
const TIP_BUILD = '005a03b'

const out = {
  when: new Date().toISOString(),
  zone: 'Asia/Shanghai',
  role: '余烬-测试岗',
  tip_build: TIP_BUILD,
  corerpg_expected: '1.15.28',
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
  try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return (it && it.name) || '' }
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
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\nlp user ${OP} parent remove admin\n`) } catch (_) {}
  try { fs.writeFileSync(ROOT + '/server-runtime/ops.json', '[]\n') } catch (_) {}
  try { fs.writeFileSync(ROOT + '/login-runtime/ops.json', '[]\n') } catch (_) {}
}
function invNames(b) {
  const names = []
  for (const it of (b.inventory.items() || [])) {
    const n = nameOf(it) || it.name || ('id' + it.type)
    names.push(n + 'x' + it.count)
  }
  return names
}
function countByHint(b, hintRe) {
  let n = 0
  for (const it of (b.inventory.items() || [])) {
    const nm = nameOf(it) || it.name || ''
    if (hintRe.test(nm)) n += it.count
  }
  return n
}
function offhandItem(b) {
  try { return b.inventory.slots[45] || null } catch (e) { return null }
}
function dropQ(b) {
  // 1.12.2 PlayerDigging status 4 = DROP_ITEM (Q)
  b._client.write('block_dig', {
    status: 4,
    location: { x: 0, y: 0, z: 0 },
    face: 0
  })
}
function swapF(b) {
  // 1.12.2 PlayerDigging status 6 = Swap Item With Offhand (F)
  b._client.write('block_dig', {
    status: 6,
    location: { x: 0, y: 0, z: 0 },
    face: 0
  })
}
async function clickSlot(b, slot, ms = 2800) {
  try { b.setControlState('sneak', false) } catch (_) {}
  await wait(150)
  if (!b.currentWindow) { console.log('clickSlot no window', slot); return false }
  try {
    await Promise.race([b.clickWindow(slot, 0, 0), wait(ms)])
    return true
  } catch (e) {
    console.log('clickSlot', slot, e.message)
    return false
  }
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

function staticChecks() {
  const flexSrc = fs.readFileSync(ROOT + '/CoreRpg/src/main/java/town/sunshine/corerpg/FlexSkillService.java', 'utf8')
  const pluginSrc = fs.readFileSync(ROOT + '/CoreRpg/src/main/java/town/sunshine/corerpg/CoreRpgPlugin.java', 'utf8')
  const skillsLive = fs.readFileSync(ROOT + '/plugins/CoreRpg/skills.yml', 'utf8')
  const skillsSrc = fs.readFileSync(ROOT + '/CoreRpg/src/main/resources/skills.yml', 'utf8')
  const flexMenu = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_flex_skill.yml', 'utf8')
  const hubMenu = fs.readFileSync(ROOT + '/plugins/TrMenu/menus/ember_hub.yml', 'utf8')

  const hasDrop = /PlayerDropItemEvent/.test(flexSrc)
  const hasSneak = /isSneaking\(\)/.test(flexSrc)
  const hasCast = /cast\(player\)/.test(flexSrc) || /cast\(Player/.test(flexSrc)
  const hasListener = /implements\s+Listener/.test(flexSrc)
  note('static_flex_drop_sneak_cast', hasDrop && hasSneak && hasCast && hasListener,
    `Drop=${hasDrop} sneak=${hasSneak} cast=${hasCast} Listener=${hasListener}`)

  const reg = /registerEvents\(\s*flexSkillService/.test(pluginSrc)
  note('static_register_flex', reg, 'CoreRpgPlugin registerEvents(flexSkillService')

  const cdOk = /flex_ember_step:[\s\S]*?cooldown_seconds:\s*14/.test(skillsLive)
    && /flex_ember_step:[\s\S]*?distance:\s*5\.0/.test(skillsLive)
    && /flex_ember_step:[\s\S]*?cooldown_seconds:\s*14/.test(skillsSrc)
    && /flex_ember_step:[\s\S]*?distance:\s*5\.0/.test(skillsSrc)
  note('static_cd_distance_unchanged', cdOk, 'cooldown_seconds:14 distance:5.0 双路径')

  const hotkey = /flex:\s*\n\s*hotkey:\s*sneak_drop/.test(skillsLive)
    && /flex:\s*\n\s*hotkey:\s*sneak_drop/.test(skillsSrc)
  note('static_flex_hotkey_optional', hotkey, 'flex.hotkey: sneak_drop')

  const loreOk = /潜行\+Q/.test(flexMenu) && /潜行\+Q/.test(hubMenu)
  note('static_trmenu_lore_sneak_q', loreOk, 'ember_flex_skill + ember_hub lore 潜行+Q')

  let ver = ''
  try {
    ver = execSync(`rg -n "Enabling CoreRpg v" ${ROOT}/server-runtime/logs/latest.log | tail -1`, { encoding: 'utf8' }).trim()
  } catch (_) {}
  note('static_runtime_1_15_28', /1\.15\.28/.test(ver), ver || 'no Enabling line')
  out.evidence.static = { hasDrop, hasSneak, hasCast, reg, cdOk, hotkey, loreOk, ver }
}

;(async () => {
  try {
    staticChecks()

    const op = await joinPlay(OP, { log: false })
    await wait(800)
    fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(700)
    op.chat(`/lp user ${OP} parent add admin`); await wait(600)
    op.chat(`/op ${OP}`); await wait(400)
    const c = async (cmd, ms = 800) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }

    const b = await joinPlay(USER, { log: true })
    const chat = []
    b.on('message', m => chat.push(strip(m.toString())))
    await wait(2000)
    await c(`/lp user ${USER} permission set corerpg.use true`, 500)
    await c(`/gamemode survival ${USER}`, 600)
    await c(`/clear ${USER}`, 800)

    // open pad
    await c(`/mvtp ${USER} world`, 1500)
    await c(`/fill 199 79 199 201 79 208 quartz_block`, 800)
    await c(`/fill 199 80 199 201 82 208 air`, 800)
    await c(`/tp ${USER} 200 80 200 0 0`, 1000)

    // --- menu: open flex page, check lore, equip ---
    fs.writeFileSync(CONSOLE, `trmenu open ember_flex_skill ${USER}\n`); await wait(1600)
    const flexTitle = await waitWindow(b, t => /轻技|踏步|余烬/.test(t) || !!t, 4000)
    out.evidence.flexTitle = flexTitle
    let loreHits = []
    try {
      const slots = b.currentWindow && b.currentWindow.slots || []
      for (const it of slots) {
        if (!it) continue
        const lore = loreOf(it)
        if (lore.some(l => /潜行\+Q/.test(l))) loreHits.push({ name: nameOf(it), lore })
      }
    } catch (e) { console.log('lore scan', e.message) }
    out.evidence.menuLoreHits = loreHits
    note('menu_lore_sneak_q', loreHits.length > 0, JSON.stringify(loreHits.slice(0, 3)))

    let n = chat.length
    await clickSlot(b, FLEX_EQUIP, 2800)
    await wait(700)
    const equipChat = chat.slice(n)
    out.evidence.equipChat = equipChat.filter(s => /轻技|装配|踏步/.test(s)).slice(0, 8)
    note('menu_equip_step', equipChat.some(s => /已装配/.test(s) && /踏步/.test(s)),
      JSON.stringify(out.evidence.equipChat))

    try { await b.closeWindow(b.currentWindow) } catch (_) {}
    await wait(500)

    // give toss bait + ensure held
    await c(`/give ${USER} dirt 8`, 700)
    try { b.setQuickBarSlot(0) } catch (_) {}
    await wait(400)

    // --- sneak+Q cast ---
    await c(`/tp ${USER} 200 80 200 0 0`, 900)
    try { b.setControlState('sneak', false) } catch (_) {}
    await wait(300)
    try { b.setControlState('sneak', true) } catch (_) {}
    await wait(500) // let server see sneaking

    const dirtBefore = countByHint(b, /dirt|泥土|Dirt/i) || (b.inventory.items().filter(i => i.type === 3).reduce((a, i) => a + i.count, 0))
    const pos0 = posOf(b)
    n = chat.length
    dropQ(b)
    await wait(1500)
    try { b.setControlState('sneak', false) } catch (_) {}
    const pos1 = posOf(b)
    const moved = pos0 && pos1 ? Math.hypot(pos1.x - pos0.x, pos1.z - pos0.z) : 0
    const sneakChat = chat.slice(n)
    const dirtAfterSneak = countByHint(b, /dirt|泥土|Dirt/i) || (b.inventory.items().filter(i => i.type === 3).reduce((a, i) => a + i.count, 0))
    out.evidence.sneakQ = { pos0, pos1, moved, chat: sneakChat.filter(s => /轻技|踏步|冷却|释放|前方/.test(s)).slice(0, 10), dirtBefore, dirtAfterSneak }
    const castOk = moved >= 1.5 || sneakChat.some(s => /释放/.test(s) && /踏步|轻技/.test(s)) || sneakChat.some(s => /冷却/.test(s))
    const noDropOnSneak = dirtAfterSneak >= dirtBefore // cancelled drop
    note('key_sneak_q_cast', castOk && noDropOnSneak,
      `moved=${moved.toFixed(2)} noDrop=${noDropOnSneak} chat=${JSON.stringify(out.evidence.sneakQ.chat)}`)

    // wait CD before bare Q / menu cast reuse
    await wait(14500)

    // --- bare Q should still drop ---
    await c(`/tp ${USER} 200 80 200 0 0`, 800)
    await c(`/give ${USER} cobblestone 4`, 700)
    try { b.setQuickBarSlot(0) } catch (_) {}
    // prefer holding cobble
    const cobble = b.inventory.items().find(i => i.type === 4 || /cobble|圆石/i.test(nameOf(i) || i.name || ''))
    if (cobble) {
      try { await b.equip(cobble, 'hand'); await wait(500) } catch (e) { console.log('equip cobble', e.message) }
    }
    try { b.setControlState('sneak', false) } catch (_) {}
    await wait(400)
    const cobbleBefore = b.inventory.items().filter(i => i.type === 4).reduce((a, i) => a + i.count, 0)
    n = chat.length
    dropQ(b)
    await wait(1200)
    const cobbleAfter = b.inventory.items().filter(i => i.type === 4).reduce((a, i) => a + i.count, 0)
    const bareChat = chat.slice(n)
    const dropped = cobbleAfter < cobbleBefore
    const noCastBare = !bareChat.some(s => /释放/.test(s) && /踏步|轻技/.test(s))
    out.evidence.bareQ = { cobbleBefore, cobbleAfter, dropped, chat: bareChat.filter(s => /轻技|踏步|释放|冷却/.test(s)).slice(0, 6) }
    note('key_bare_q_drop', dropped && noCastBare,
      `before=${cobbleBefore} after=${cobbleAfter} noCast=${noCastBare}`)

    // --- offhand bare F: ward / vita / ash brace ---
    async function offhandFTest(niId, hintRe, label) {
      await c(`/clear ${USER}`, 700)
      await c(`/ni give ${USER} ${niId} 1`, 1400)
      await c(`/give ${USER} stick 1`, 600)
      const item = b.inventory.items().find(i => hintRe.test(nameOf(i) || i.name || ''))
      if (!item) {
        note(`offhand_f_${label}`, false, 'give failed ' + niId + ' inv=' + JSON.stringify(invNames(b).slice(0, 8)))
        return
      }
      try { await b.equip(item, 'off-hand'); await wait(900) } catch (e) { console.log('equip oh', label, e.message); await wait(500) }
      const stick = b.inventory.items().find(i => i.type === 280 || /stick|木棍/i.test(nameOf(i) || i.name || ''))
      if (stick) { try { await b.equip(stick, 'hand'); await wait(500) } catch (e) { console.log('equip stick', e.message) } }
      const oh0 = offhandItem(b)
      const hand0 = b.heldItem
      out.evidence['oh_before_' + label] = { oh: oh0 ? nameOf(oh0) : null, hand: hand0 ? (nameOf(hand0) || hand0.name) : null }
      // ensure flex still equipped so a mistaken hotkey would cast
      b.chat('/corerpg flex equip flex_ember_step'); await wait(700)
      try { b.setControlState('sneak', false) } catch (_) {}
      await wait(300)
      n = chat.length
      const posA = posOf(b)
      swapF(b)
      await wait(1000)
      const posB = posOf(b)
      const movedF = posA && posB ? Math.hypot(posB.x - posA.x, posB.z - posA.z) : 0
      const fChat = chat.slice(n)
      const oh1 = offhandItem(b)
      const hand1 = b.heldItem
      const casted = fChat.some(s => /释放/.test(s) && /踏步|轻技/.test(s)) || movedF >= 1.5
      const swapped = (oh1 && hand0 && (nameOf(oh1) || '').includes('棍')) || (hand1 && oh0 && hintRe.test(nameOf(hand1) || ''))
        || (oh1 && hand1 && (nameOf(oh1) || '') !== (nameOf(oh0) || '') && (nameOf(hand1) || '') !== (nameOf(hand0) || ''))
      out.evidence['oh_after_' + label] = {
        oh: oh1 ? nameOf(oh1) : null, hand: hand1 ? (nameOf(hand1) || hand1.name) : null,
        movedF, casted, swapped, chat: fChat.filter(s => /轻技|踏步|释放|冷却/.test(s)).slice(0, 6)
      }
      note(`offhand_f_${label}`, !casted,
        `casted=${casted} swapped~=${!!swapped} moved=${movedF.toFixed(2)} oh0=${out.evidence['oh_before_' + label].oh} oh1=${out.evidence['oh_after_' + label].oh}`)
    }

    await offhandFTest('acc_ember_offhand_ward', /守腕/, 'ward')
    await wait(400)
    await offhandFTest('acc_ember_offhand_vita', /生坠/, 'vita')
    await wait(400)
    await offhandFTest('part_ember_ash_brace', /灰箍|灰/, 'ash_brace')

    // --- menu cast still works (after CD) ---
    await wait(14500)
    await c(`/clear ${USER}`, 600)
    await c(`/tp ${USER} 200 80 200 0 0`, 800)
    b.chat('/corerpg flex equip flex_ember_step'); await wait(700)
    fs.writeFileSync(CONSOLE, `trmenu open ember_flex_skill ${USER}\n`); await wait(1500)
    await waitWindow(b, null, 3000)
    n = chat.length
    const p0 = posOf(b)
    await clickSlot(b, FLEX_CAST, 2800)
    await wait(1200)
    const p1 = posOf(b)
    const movedMenu = p0 && p1 ? Math.hypot(p1.x - p0.x, p1.z - p0.z) : 0
    const menuCastChat = chat.slice(n)
    out.evidence.menuCast = { movedMenu, chat: menuCastChat.filter(s => /轻技|踏步|释放|冷却|前方/.test(s)).slice(0, 8), p0, p1 }
    note('menu_cast_still', movedMenu >= 1.5 || menuCastChat.some(s => /释放/.test(s)),
      `moved=${movedMenu.toFixed(2)} chat=${JSON.stringify(out.evidence.menuCast.chat)}`)

    // cleanup
    await c(`/lp user ${OP} parent remove admin`, 400)
    await c(`/deop ${OP}`, 400)
    cleanupOps()
    fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
    console.log('RESULT', JSON.stringify({ pass: out.pass, checks: out.checks }, null, 2))
    try { b.quit(); op.quit() } catch (_) {}
    process.exit(out.pass ? 0 : 1)
  } catch (e) {
    console.error('FATAL', e)
    out.pass = false
    out.evidence.fatal = String(e && e.stack || e)
    cleanupOps()
    fs.writeFileSync(OUT, JSON.stringify(out, null, 2))
    process.exit(1)
  }
})()
setTimeout(() => { cleanupOps(); process.exit(2) }, 180000)
