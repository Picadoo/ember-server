// Real new-player path smoke (2026-09-26). Fresh non-op name NEW (default NewP<rand>).
// 1) login server :25566 — AuthMe /register (random password, not stored); 2) play server :25565 — first spawn in ember_hub,
// /ember menu → 签到, 日常 → 开始挑战 → clear → rewards + xp + return hub; 战令 free track; 商城 日票 (no cash);
// 仓库 overview/deposit; 工坊 enchanting table (catalyst crystal) + anvil repair with shards.
// OP_BOT (RpgBot) only observes at the hub spawn and hands out the combat buffs / starter blade (noted in report).
const mineflayer = require('mineflayer'); const { Vec3 } = require('vec3'); const crypto = require('crypto')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('NewP' + Math.floor(Math.random() * 90000 + 10000))
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const PW = crypto.randomBytes(6).toString('hex')
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager', 'pig_zombie', 'vindication_illager'])
const WORKSHOP = { table: new Vec3(-22, 58, 104), anvil: new Vec3(-20, 58, 104), craft: new Vec3(-24, 58, 104) }
function mk(name, port) {
  const b = mineflayer.createBot({ host: '127.0.0.1', port, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = []; b.on('message', m => { const s = m.toString(); b.chatLog.push(s); console.log(`[${name}:${port}]`, s) })
  b.on('kicked', r => console.log(`[${name}:${port}] KICKED`, r)); b.on('error', e => console.log(`[${name}:${port}] ERR`, e.message))
  return new Promise((res, rej) => { b.once('spawn', () => res(b)); b.once('end', () => rej(new Error('ended before spawn'))) })
}
const since = (b, n) => b.chatLog.slice(n).join(' | ')
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
function niCounts(bot) { const o = {}; for (const it of bot.inventory.items()) { const k = niId(it) || ('vanilla:' + it.name); o[k] = (o[k] || 0) + it.count } return o }
function diff(a, b) { const d = {}; for (const k of new Set([...Object.keys(a), ...Object.keys(b)])) { const v = (b[k] || 0) - (a[k] || 0); if (v) d[k] = v } return d }
function nameOf(it) { try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' } }
async function menuOpen(b) { if (b.currentWindow) { b.closeWindow(b.currentWindow); await wait(500) } b.chat('/ember'); for (let i = 0; i < 25 && !b.currentWindow; i++) await wait(200); await wait(400); return !!b.currentWindow }
async function click(b, name) {
  const w = b.currentWindow; if (!w) return 'no-window'
  const slot = w.slots.findIndex((it, i) => it && i < w.inventoryStart && nameOf(it) === name)
  if (slot < 0) return 'no-item:' + name
  try { await b.clickWindow(slot, 0, 0) } catch (e) {}
  await wait(1600); return 'ok'
}
async function path(b, ...names) { await menuOpen(b); const out = []; for (const n of names) out.push(await click(b, n)); return out.join(',') }
;(async () => {
  const r = { name: NEW }
  // ---- 1. login server ----
  const lg = await mk(NEW, 25566); await wait(2500)
  r.login_prompt = lg.chatLog.join(' | ').slice(0, 200)
  let n = lg.chatLog.length; lg.chat(`/register ${PW} ${PW}`); await wait(3000)
  r.register = since(lg, n).slice(0, 300)
  r.check_register = /registered|注册成功/i.test(r.register) ? 'PASS' : 'FAIL'
  lg.quit(); await wait(1500)
  // ---- 2. play server ----
  const op = await mk(OP_BOT, 25565); await wait(1000)
  op.chat('/mvtp ember_hub'); await wait(3000); const hubPos = op.entity.position.clone()
  const b = await mk(NEW, 25565); await wait(3000)
  r.first_spawn_seen_in_hub = !!(op.players[NEW] && op.players[NEW].entity && op.players[NEW].entity.position.distanceTo(hubPos) < 20)
  r.check_first_spawn_hub = r.first_spawn_seen_in_hub ? 'PASS' : 'FAIL'
  r.join_chat = b.chatLog.join(' | ').slice(0, 400)
  r.starter_inv = niCounts(b); r.xp0 = b.experience.level
  // menu → sign
  r.hub_open = await menuOpen(b); r.hub_title = b.currentWindow ? strip(b.currentWindow.title) : null
  n = b.chatLog.length; r.sign_click = await click(b, '签到'); r.sign = since(b, n).slice(0, 200)
  r.check_sign = /签到成功/.test(r.sign) ? 'PASS' : 'FAIL'
  // pass free track
  n = b.chatLog.length; r.pass_click = await path(b, '战令', '免费轨 · 领取补给'); await wait(1500); r.pass = since(b, n).slice(0, 300)
  r.check_pass = /今日补给已寄出/.test(r.pass) && /领取完成/.test(r.pass) ? 'PASS' : 'FAIL'
  // shop daily ticket (no cash)
  n = b.chatLog.length; r.shop_click = await path(b, '商城', '日票 ×1'); r.shop = since(b, n).slice(0, 200)
  r.check_shop = /晶钻不足|已购买/.test(r.shop) ? 'PASS' : 'FAIL'
  // warehouse overview + deposit shards in main hand
  n = b.chatLog.length; r.wh_click = await path(b, '仓库', '材料仓总览'); r.wh = since(b, n).slice(0, 200)
  const shard = b.inventory.items().find(i => niId(i) === 'mat_ember_shard')
  if (b.currentWindow) { b.closeWindow(b.currentWindow); await wait(400) }
  if (shard) { await b.equip(shard, 'hand'); await wait(300) }
  n = b.chatLog.length; r.wh_dep_click = await path(b, '仓库', '存入主手'); r.wh_dep = since(b, n).slice(0, 200)
  r.check_warehouse = /材料仓/.test(r.wh) && /已存入/.test(r.wh_dep) ? 'PASS' : 'FAIL'
  if (b.currentWindow) { b.closeWindow(b.currentWindow); await wait(400) }
  // ---- daily dungeon via menu ----
  const c = async (s, ms) => { op.chat(s); await wait(ms || 800) }
  await c(`/effect ${NEW} resistance 900 4 true`); await c(`/effect ${NEW} regeneration 900 4 true`); await c(`/effect ${NEW} strength 900 6 true`)
  if (!b.inventory.items().some(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))) { await c(`/ni give ${NEW} gear_ember_blade 1`, 1500); r.blade_given_by_op = true }
  const blade = b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || '')); if (blade) await b.equip(blade, 'hand')
  const inv0 = niCounts(b), xpBefore = b.experience.level, dN = b.chatLog.length
  r.daily_click = await path(b, '日常 · 余烬窟', '开始挑战'); await wait(3000)
  r.daily_start = since(b, dN).slice(0, 300)
  const t0 = Date.now(); let lastTp = 0
  while (Date.now() - t0 < 8 * 60000 && !/通关奖励已发放/.test(since(b, dN))) {
    const e = Object.values(b.entities).filter(x => x !== b.entity && MOBS.has(x.name) && x.position.distanceTo(b.entity.position) < 80).sort((a, c2) => a.position.distanceTo(b.entity.position) - c2.position.distanceTo(b.entity.position))[0]
    if (!e) { await wait(700); continue }
    const d = e.position.distanceTo(b.entity.position)
    if (d > 3 && Date.now() - lastTp > 1600) { op.chat(`/tp ${NEW} ${(e.position.x + 1.2).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.6).toFixed(1)}`); lastTp = Date.now() }
    if (d <= 3.5) { try { await b.lookAt(e.position.offset(0, 1.5, 0), true) } catch (x) {} b.attack(e) }
    await wait(650)
  }
  r.daily_done = /通关奖励已发放/.test(since(b, dN)); r.daily_ms = Date.now() - t0
  await wait(9000)
  r.daily_delta = diff(inv0, niCounts(b)); r.xp_after_daily = b.experience.level; r.xp_gain = r.xp_after_daily - xpBefore
  r.daily_back_in_hub = !!(op.players[NEW] && op.players[NEW].entity && op.players[NEW].entity.position.distanceTo(hubPos) < 30)
  r.check_daily = r.daily_done && r.daily_delta.ticket_ember_daily === -1 && (r.daily_delta.crystal_ember_enchant || 0) >= 1 && r.xp_gain === 3 && r.daily_back_in_hub ? 'PASS' : 'FAIL'
  // ---- workshop: enchant ----
  await c(`/tp ${NEW} -22 58 106`, 2000)
  const bladeNow = b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
  r.blade_durability_used = bladeNow && bladeNow.durabilityUsed
  try {
    const tb = b.blockAt(WORKSHOP.table); r.table_block = tb && tb.name
    const ench = await b.openEnchantmentTable(tb)
    const mv = async (dest, pred) => { for (let i = ench.inventoryStart; i < ench.inventoryEnd; i++) { const it = ench.slots[i]; if (it && pred(it)) { try { await b.moveSlotItem(i, dest) } catch (e) {} await wait(400); return !!ench.slots[dest] } } return false }
    r.put_blade = await mv(0, it => /gear_ember_(t\d_)?blade/.test(niId(it) || ''))
    r.put_crystal = await mv(1, it => niId(it) === 'crystal_ember_enchant')
    await Promise.race([new Promise(res => ench.once('ready', res)), wait(6000)])
    r.enchant_offers = (ench.enchantments || []).map(o => o.level)
    const xpE = b.experience.level
    const idx = (ench.enchantments || []).findIndex(o => o.level > 0)
    try { await ench.enchant(idx < 0 ? 0 : idx); await wait(1000); r.enchanted = true } catch (e) { r.enchant_err = e.message }
    r.xp_after_enchant = b.experience.level; r.enchant_xp_cost = xpE - r.xp_after_enchant
    try { await ench.takeTargetItem() } catch (e) {}
    ench.close(); await wait(500)
    const bl = b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
    r.blade_enchants = bl && bl.enchants
  } catch (e) { r.enchant_flow_err = e.message }
  r.check_enchant = r.enchanted && r.blade_enchants && r.blade_enchants.some(e => !/unbreaking|durability/i.test(e.name)) ? 'PASS' : 'FAIL'
  // ---- workshop: anvil repair with shards ----
  try {
    const bl = b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
    const sh = b.inventory.items().find(i => niId(i) === 'mat_ember_shard')
    r.anvil_before = { durUsed: bl && bl.durabilityUsed, xp: b.experience.level, shards: sh && sh.count }
    if (!sh) { await c(`/ni give ${NEW} mat_ember_shard 4`, 1500); r.shards_given_by_op = true }
    const sh2 = b.inventory.items().find(i => niId(i) === 'mat_ember_shard')
    const win = await b.openBlock(b.blockAt(WORKSHOP.anvil)); await wait(800)
    const mva = async (dest, pred, all) => { for (let i = win.inventoryStart; i < win.inventoryEnd; i++) { const it = win.slots[i]; if (it && pred(it)) { try { await b.clickWindow(i, 0, 0) } catch (e) {} await wait(300); try { await b.clickWindow(dest, all ? 0 : 1, 0) } catch (e) {} await wait(300); if (b.inventory.selectedItem || win.selectedItem) { try { await b.clickWindow(i, 0, 0) } catch (e) {} } await wait(300); return !!win.slots[dest] } } return false }
    r.anvil_put_blade = await mva(0, it => /gear_ember_(t\d_)?blade/.test(niId(it) || ''), true)
    r.anvil_put_shard = await mva(1, it => niId(it) === 'mat_ember_shard', false)
    await wait(1200)
    const out = win.slots[2]; r.anvil_output = out ? { name: out.name, durUsed: out.durabilityUsed } : null
    if (out) { try { await b.clickWindow(2, 0, 1) } catch (e) { r.anvil_take_err = e.message } await wait(800); r.anvil_combined = true }
    b.closeWindow(win); await wait(600)
    const bl2 = b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
    r.anvil_after = { durUsed: bl2 && bl2.durabilityUsed, xp: b.experience.level }
  } catch (e) { r.anvil_flow_err = e.message }
  r.check_anvil = r.anvil_combined && r.anvil_after && r.anvil_before && r.anvil_after.durUsed < r.anvil_before.durUsed ? 'PASS' : (r.anvil_before && r.anvil_before.durUsed === 0 ? 'SKIP(no damage)' : 'FAIL')
  r.workshop_blocks = { craft: b.blockAt(WORKSHOP.craft) && b.blockAt(WORKSHOP.craft).name, anvil: b.blockAt(WORKSHOP.anvil) && b.blockAt(WORKSHOP.anvil).name }
  // server-side truth: client prediction can show a ghost anvil result, so re-login and re-read inventory
  try {
    b.quit(); await wait(2500)
    const b2 = await mk(NEW, 25565); await wait(3000)
    const bl3 = b2.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
    const sh3 = b2.inventory.items().find(i => niId(i) === 'mat_ember_shard')
    r.relog = { durUsed: bl3 && bl3.durabilityUsed, xp: b2.experience.level, shards: sh3 && sh3.count, enchants: bl3 && bl3.enchants }
    if (r.check_anvil === 'PASS' && !(r.relog.durUsed < r.anvil_before.durUsed)) r.check_anvil = 'FAIL(ghost: server did not apply)'
    b2.quit()
  } catch (e) { r.relog_err = e.message }
  console.log('NEWPLAYER_RESULT', JSON.stringify(r, null, 2)); await wait(500); process.exit(0)
})().catch(e => { console.error('FATAL', e); process.exit(1) })
