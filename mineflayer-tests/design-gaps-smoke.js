// Design-gap smoke (2026-09-26 23:00): T3 enchant with tier crystal gating; MM elite/boss level reward + daily cap;
// pass XP (sign + passxp cap); vip tier-0 token; shop placeholders gone. Fresh non-op bot via proxy; RpgBot (op) hands items.
const { Vec3 } = require('vec3')
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Gap' + Math.floor(Math.random() * 90000 + 10000))
const TABLE = new Vec3(-22, 58, 104)
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
const nameOf = it => { try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' } }
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay('RpgBot', { log: false }); const c = async (cmd, ms = 1200) => { op.chat(cmd); await wait(ms) }
  const b = await joinPlay(NEW, { log: false }); b.on('message', m => console.log(`[${NEW}]`, m.toString()))
  const since = n => b.chatLog.slice(n).join(' | ')
  await wait(1500)
  // --- vip tier-0 + sign pass xp ---
  let n = b.chatLog.length; b.chat('/corerpg vip claim'); await wait(1500); r.vip = since(n)
  r.check_vip = /余烬币 \+20/.test(r.vip) ? 'PASS' : 'FAIL'
  n = b.chatLog.length; b.chat('/corerpg sign'); await wait(1500); r.sign = since(n)
  r.check_sign_passxp = /\[战令\] 经验 \+10/.test(r.sign) ? 'PASS' : 'FAIL'
  n = b.chatLog.length; for (let i = 0; i < 6; i++) await c(`/corerpg passxp ${NEW} daily_clear`, 600)
  await wait(800); b.chat('/corerpg pass'); await wait(1500); r.passxp = since(n)
  r.check_pass_cap = /今日 100\/100/.test(r.passxp) && /今日战令经验已达上限/.test(r.passxp) && /Lv\.1/.test(r.passxp) ? 'PASS' : 'FAIL'
  // --- kill levels: MM brute killed by the bot (real onDeath hook), then cap via console grants ---
  const lv0 = b.experience.level
  await c(`/mvtp ${NEW} world`, 3000); await c(`/tp ${NEW} 0 100 0`, 1500)
  await c(`/effect ${NEW} strength 120 20`, 600); await c(`/effect ${NEW} resistance 120 5`, 600)
  await c(`/ni give ${NEW} gear_ember_t3_blade 1`, 1000)
  const blade = b.inventory.items().find(i => niId(i) === 'gear_ember_t3_blade'); if (blade) await b.equip(blade, 'hand')
  const p = b.entity.position
  n = b.chatLog.length
  await c(`/mm m spawn EmberDailyBrute 1 world,${(p.x + 2).toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`, 1500)
  for (let i = 0; i < 40; i++) {
    const mob = b.nearestEntity(e => e.type === 'mob' && e.position.distanceTo(b.entity.position) < 8)
    if (!mob) break
    await b.lookAt(mob.position.offset(0, 1, 0)); b.attack(mob); await wait(650)
  }
  await wait(1500); r.kill_msg = since(n); r.lv_after_kill = b.experience.level
  r.check_mm_elite = /击杀精英 · 经验等级 \+1/.test(r.kill_msg) ? 'PASS' : 'FAIL'
  n = b.chatLog.length; for (let i = 0; i < 4; i++) await c(`/corerpg xpreward ${NEW} boss`, 600)
  await wait(800); r.cap_msgs = since(n); r.lv_after_cap = b.experience.level
  r.check_kill_cap = r.lv_after_cap - lv0 === 6 ? 'PASS' : 'FAIL'
  // --- T3 enchant, tier gating (slot0 needs 1+3=4 crystals) ---
  await c(`/mvtp ${NEW} ember_hub`, 3000); await c(`/tp ${NEW} -22 58 106`, 2000)
  await c(`/ni give ${NEW} crystal_ember_enchant 3`, 1200)
  const tryEnchant = async () => {
    const ench = await b.openEnchantmentTable(b.blockAt(TABLE))
    const mv = async (dest, pred) => { for (let i = ench.inventoryStart; i < ench.inventoryEnd; i++) { const it = ench.slots[i]; if (it && pred(it)) { try { await b.moveSlotItem(i, dest) } catch (e) {} await wait(400); return true } } return false }
    await mv(0, it => niId(it) === 'gear_ember_t3_blade'); await mv(1, it => niId(it) === 'crystal_ember_enchant')
    await Promise.race([new Promise(res => ench.once('ready', res)), wait(5000)])
    const offers = (ench.enchantments || []).map(o => o.level)
    let err = null; try { await ench.enchant(0); await wait(1000) } catch (e) { err = e.message }
    try { await ench.takeTargetItem() } catch (e) {}
    ench.close(); await wait(800); return { offers, err }
  }
  n = b.chatLog.length; r.try3 = await tryEnchant(); r.try3_msg = since(n)
  r.check_gate_short = /需要 §?e?4/.test(r.try3_msg) || /需要 4/.test(strip(r.try3_msg)) ? 'PASS' : 'FAIL'
  // relog → server truth: blade still unenchanted, crystals still 3
  b.quit(); await wait(2500)
  let b2 = await joinPlay(NEW, { log: false }); await wait(1000)
  const bl2 = b2.inventory.items().find(i => niId(i) === 'gear_ember_t3_blade')
  r.after_short = { crystals: count(b2, 'crystal_ember_enchant'), enchants: bl2 && bl2.enchants }
  await c(`/ni give ${NEW} crystal_ember_enchant 3`, 1200)
  r.levels_before = b2.experience.level
  r.try6 = await (async () => {
    const ench = await b2.openEnchantmentTable(b2.blockAt(TABLE))
    const mv = async (dest, pred) => { for (let i = ench.inventoryStart; i < ench.inventoryEnd; i++) { const it = ench.slots[i]; if (it && pred(it)) { try { await b2.moveSlotItem(i, dest) } catch (e) {} await wait(400); return true } } return false }
    await mv(0, it => niId(it) === 'gear_ember_t3_blade'); await mv(1, it => niId(it) === 'crystal_ember_enchant')
    await Promise.race([new Promise(res => ench.once('ready', res)), wait(5000)])
    const offers = (ench.enchantments || []).map(o => o.level)
    let err = null; try { await ench.enchant(0); await wait(1000) } catch (e) { err = e.message }
    try { await ench.takeTargetItem() } catch (e) {}
    try { await ench.takeTargetItem() } catch (e) {}
    ench.close(); await wait(800); return { offers, err }
  })()
  b2.quit(); await wait(2500)
  const b3 = await joinPlay(NEW, { log: false }); await wait(1000)
  const bl3 = b3.inventory.items().find(i => niId(i) === 'gear_ember_t3_blade')
  r.after_ok = { crystals: count(b3, 'crystal_ember_enchant'), enchants: bl3 && bl3.enchants, levels: b3.experience.level }
  r.check_t3_enchant = r.after_ok.enchants && r.after_ok.enchants.some(e => e.name === 'sharpness' && e.lvl === 2) && r.after_ok.crystals === 2 ? 'PASS' : 'FAIL'
  // --- shop: placeholders hidden ---
  b3.chat('/ember'); for (let i = 0; i < 20 && !b3.currentWindow; i++) await wait(200); await wait(500)
  const hub = b3.currentWindow; const shopSlot = hub ? hub.slots.findIndex((it, i) => it && i < hub.inventoryStart && nameOf(it) === '商城') : -1
  if (shopSlot >= 0) { try { await b3.clickWindow(shopSlot, 0, 0) } catch (e) {} await wait(1800) }
  const w = b3.currentWindow; r.shop_items = w ? w.slots.slice(0, w.inventoryStart).filter(Boolean).map(nameOf).filter(s => s.trim()) : null
  r.check_shop = r.shop_items && !r.shop_items.some(s => /首充|加速券|仓库扩容|刃焰红|礼包/.test(s)) && r.shop_items.some(s => /日票/.test(s)) ? 'PASS' : 'FAIL'
  b3.quit(); op.quit()
  console.log('GAPS_RESULT', JSON.stringify(r, null, 2)); process.exit(0)
})().catch(e => { console.error('FATAL', e); process.exit(1) })
