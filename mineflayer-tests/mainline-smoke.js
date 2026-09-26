// Mainline quest smoke (CoreRpg 1.8.0, 2026-09-27 CST). Fresh non-op bot via proxy → auto-start ch1 → real play through ch1+ch2
// (NPC right-click on Adyeshach ember_guide, MM kills in ember_afk, sign, enchant/anvil at the hub workshop, EmberDaily clear) → Lv20.
// Part B: a second fresh bot is jumped through chapters 3–6 with /corerpg quest set and the real hook paths
// (/corerpg progress <p> <source> = DP clear hook, bot /corerpg calamity join, NPC talk) + /corerpg quest event for calamity_boss/bounty.
// OP_BOT (RpgBot, op) only issues setup commands (buffs, mob spawns, tp, xp levels).
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Main' + Math.floor(Math.random() * 90000 + 10000))
const NEW2 = process.env.NEW2 || ('MainB' + Math.floor(Math.random() * 9000 + 1000))
const OP_BOT = process.env.OP_BOT || 'RpgBot'
const NPC = new Vec3(-16.5, 58, 106.5)
const WORKSHOP = { table: new Vec3(-22, 58, 104), anvil: new Vec3(-20, 58, 104) }
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager', 'pig_zombie', 'vindication_illager'])
const since = (b, n) => b.chatLog.slice(n).join(' | ')
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const blade = b => b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
function mobsNear(b, r) { return Object.values(b.entities).filter(e => e && e !== b.entity && MOBS.has(e.name) && e.position.distanceTo(b.entity.position) < (r || 40)) }
async function level(b) { const n = b.chatLog.length; b.chat('/corerpg level'); await wait(1200); const m = strip(since(b, n)).match(/等级 Lv\.(\d+)/); return m ? Number(m[1]) : 0 }
async function quest(b) { const n = b.chatLog.length; b.chat('/corerpg quest'); await wait(1200); return strip(since(b, n)) }
async function talkNpc(b, c) {
  await c(`/mvtp ${b.username} ember_hub`, 2500); await c(`/tp ${b.username} -16.5 58 108.5`, 2000)
  const npc = Object.values(b.entities).find(e => e.name === 'villager' && e.position.distanceTo(NPC) < 2)
  if (!npc) return 'no-npc'
  await b.lookAt(npc.position.offset(0, 1.5, 0)); await wait(300)
  const n = b.chatLog.length; b.activateEntityAt(npc, npc.position.offset(0, 1, 0)); b.activateEntity(npc); await wait(2500)
  return strip(since(b, n))
}
async function killMm(b, c, mob, need, re) {
  let got = 0
  for (let round = 0; round < 12 && got < need; round++) {
    await c(`/effect ${b.username} strength 300 20 true`, 300); await c(`/effect ${b.username} resistance 300 4 true`, 300)
    const p = b.entity.position
    await c(`/mm m spawn ${mob} 3 ember_afk,${(p.x + 2).toFixed(1)},${p.y.toFixed(1)},${(p.z + 2).toFixed(1)}`, 1500)
    const t0 = Date.now()
    while (Date.now() - t0 < 25000) {
      const e = mobsNear(b, 12).sort((x, y) => x.position.distanceTo(b.entity.position) - y.position.distanceTo(b.entity.position))[0]
      if (!e) break
      if (e.position.distanceTo(b.entity.position) > 3) { await c(`/tp ${b.username} ${(e.position.x + 1).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.5).toFixed(1)}`, 600) }
      try { await b.lookAt(e.position.offset(0, 1.4, 0), true) } catch (x) {}
      b.attack(e); await wait(600)
    }
    const m = [...strip(b.chatLog.join(' | ')).matchAll(re)].pop(); got = m ? Number(m[1]) : got
    if (/目标：/.test(strip(b.chatLog.slice(-6).join(' '))) && got === 0) break
  }
  return got
}
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay(OP_BOT, { log: false }); await wait(1000)
  const c = async (s, ms) => { op.chat(s); await wait(ms || 900) }
  const b = await joinPlay(NEW, { log: false }); b.on('message', m => console.log(`[${NEW}]`, m.toString())); await wait(5000)
  // ---- auto-start + starter weapon ----
  r.autostart = /第1章 · 余烬初醒/.test(strip(b.chatLog.join(' | ')))
  r.starter_blade = !!blade(b)
  r.check_ch1_start = r.autostart && r.starter_blade ? 'PASS' : 'FAIL'
  r.level_start = await level(b)
  // ch1.1 talk
  r.talk1 = await talkNpc(b, c)
  r.check_talk1 = /这城烧了三十年/.test(r.talk1) ? 'PASS' : 'FAIL'
  // ch1.2/1.3 AFK kills (6 zombies, then 4 zombies or skeletons)
  await c(`/mvtp ${NEW} ember_afk`, 3000); await c(`/gamemode survival ${NEW}`)
  const bl = blade(b); if (bl) { try { await b.equip(bl, 'hand') } catch (e) {} }
  r.kills_zombie = await killMm(b, c, 'EmberAfkZombie', 6, /挂机庭僵尸 (\d+)\/6/g)
  let q = await quest(b); r.after_zombies = q.slice(0, 200)
  r.kills_skel = await killMm(b, c, 'EmberAfkSkeleton', 4, /挂机庭的怪（僵尸或骷髅） (\d+)\/4/g)
  q = await quest(b); r.check_kills = /▶ 在枢纽签到一次/.test(q) ? 'PASS' : 'FAIL'
  // ch1.4 sign
  let n = b.chatLog.length; b.chat('/corerpg sign'); await wait(2000); r.sign = strip(since(b, n)).slice(0, 300)
  // ch1.5 talk → chapter 2
  r.talk2 = await talkNpc(b, c)
  r.check_ch1_done = /第1章「余烬初醒」完成/.test(r.talk2) && /第2章 · 残窟之门/.test(r.talk2) ? 'PASS' : 'FAIL'
  r.level_after_ch1 = await level(b)
  // ch2.1/2.2 EmberDaily (solo team) — kills + clear
  const bl2 = blade(b); if (bl2) { try { await b.equip(bl2, 'hand') } catch (e) {} }
  await c(`/effect ${NEW} strength 1800 20 true`); await c(`/effect ${NEW} resistance 1800 4 true`); await c(`/effect ${NEW} regeneration 1800 4 true`)
  b.chat('/dungeon-team disband'); await wait(500); b.chat('/dungeon-team create'); await wait(1000)
  n = b.chatLog.length; b.chat('/dp start EmberDaily'); await wait(4000); r.daily_start = strip(since(b, n)).slice(0, 200)
  const t0 = Date.now(); let lastTp = 0
  while (Date.now() - t0 < 6 * 60000 && !/余烬窟·日 通关！/.test(strip(since(b, n)))) {
    const e = mobsNear(b, 80).sort((x, y) => x.position.distanceTo(b.entity.position) - y.position.distanceTo(b.entity.position))[0]
    if (!e) { await wait(700); continue }
    const d = e.position.distanceTo(b.entity.position)
    if (d > 3 && Date.now() - lastTp > 1600) { op.chat(`/tp ${NEW} ${(e.position.x + 1.2).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.6).toFixed(1)}`); lastTp = Date.now() }
    if (d <= 3.5) { try { await b.lookAt(e.position.offset(0, (e.height || 1.8) * 0.8, 0), true) } catch (x) {} b.attack(e) }
    await wait(650)
  }
  r.daily_ms = Date.now() - t0
  await wait(12000)
  const all = strip(b.chatLog.join(' | '))
  r.check_daily_kills = /✓ 在 余烬窟·日 击败守墓者|击败守墓者 10\/10|窟里的尸骸穿着旧城卫的甲/.test(all) ? 'PASS' : 'FAIL'
  r.check_daily_clear = /你带回了窟底的火/.test(all) ? 'PASS' : 'FAIL'
  // ch2.3 enchant at the workshop table (needs crystal from ch1 reward + vanilla levels)
  await c(`/mvtp ${NEW} ember_hub`, 2500); await c(`/xp 30L ${NEW}`, 800); await c(`/tp ${NEW} -22 58 106`, 2000)
  try {
    const ench = await b.openEnchantmentTable(b.blockAt(WORKSHOP.table))
    const mv = async (dest, pred) => { for (let i = ench.inventoryStart; i < ench.inventoryEnd; i++) { const it = ench.slots[i]; if (it && pred(it)) { try { await b.moveSlotItem(i, dest) } catch (e) {} await wait(400); return !!ench.slots[dest] } } return false }
    r.put_blade = await mv(0, it => /gear_ember_(t\d_)?blade/.test(niId(it) || ''))
    r.put_crystal = await mv(1, it => niId(it) === 'crystal_ember_enchant')
    await Promise.race([new Promise(res => ench.once('ready', res)), wait(6000)])
    const idx = (ench.enchantments || []).findIndex(o => o.level > 0)
    try { await ench.enchant(idx < 0 ? 0 : idx); await wait(1200) } catch (e) { r.enchant_err = e.message }
    try { await ench.takeTargetItem() } catch (e) {}
    ench.close(); await wait(800)
  } catch (e) { r.enchant_flow_err = e.message }
  q = await quest(b); r.check_enchant = /✓ 在工坊附魔台附魔一次/.test(q) ? 'PASS' : 'FAIL'
  // ch2.2 anvil repair (blade damaged by the kills; shards from ch1 reward)
  try {
    const win = await b.openBlock(b.blockAt(WORKSHOP.anvil)); await wait(800)
    const mva = async (dest, pred, all) => { for (let i = win.inventoryStart; i < win.inventoryEnd; i++) { const it = win.slots[i]; if (it && pred(it)) { try { await b.clickWindow(i, 0, 0) } catch (e) {} await wait(300); try { await b.clickWindow(dest, all ? 0 : 1, 0) } catch (e) {} await wait(300); if (b.inventory.selectedItem || win.selectedItem) { try { await b.clickWindow(i, 0, 0) } catch (e) {} } await wait(300); return !!win.slots[dest] } } return false }
    r.anvil_put_blade = await mva(0, it => /gear_ember_(t\d_)?blade/.test(niId(it) || ''), true)
    r.anvil_put_shard = await mva(1, it => niId(it) === 'mat_ember_shard', false)
    await wait(1200); r.anvil_output = !!win.slots[2]
    if (win.slots[2]) { try { await b.clickWindow(2, 0, 1) } catch (e) {} await wait(1000) }
    b.closeWindow(win); await wait(800)
  } catch (e) { r.anvil_flow_err = e.message }
  q = await quest(b); r.check_anvil = /✓ 在工坊铁砧修理一次/.test(q) || /碎片熔进刃口/.test(strip(b.chatLog.join(' | '))) ? 'PASS' : 'FAIL' // ch2 may close at once (Lv20)
  r.level_after_ch2 = await level(b)
  const all2 = strip(b.chatLog.join(' | '))
  q = await quest(b); r.quest_after = q.slice(0, 300)
  r.check_lv20 = r.level_after_ch2 >= 20 ? 'PASS' : 'FAIL'
  r.check_ch3 = /第3章 周烬试炼/.test(q) && /第2章「残窟之门」完成/.test(all2) && /精炼|t1_blade|T1/.test(all2) ? 'PASS' : 'FAIL'
  b.chat('/dungeon-team disband'); await wait(500)

  // ---- Part B: later chapter hooks on a second fresh bot ----
  const B = await joinPlay(NEW2, { log: false }); await wait(5000)
  const step = async (label, fn, re) => { const n0 = B.chatLog.length; await fn(); await wait(1800); const t = strip(since(B, n0)); r['b_' + label] = re.test(t) ? 'PASS' : 'FAIL'; if (!re.test(t)) r['b_' + label + '_log'] = t.slice(0, 300) }
  await c(`/corerpg quest set ${NEW2} 3 1`, 1200)
  await step('weekly_clear', () => c(`/corerpg progress ${NEW2} weekly_clear`), /地底更深处有回响/)
  await c(`/corerpg quest set ${NEW2} 3 3`, 1200)
  await step('bounty', () => c(`/corerpg quest event ${NEW2} bounty`), /没能回来的人/)
  for (let i = 0; i < 20; i++) await c(`/corerpg progress ${NEW2} raid_clear`, 250) // → Lv25+ (level step)
  await wait(2000); r.b_level25 = /第4章 · 深渊回响/.test(strip(B.chatLog.join(' | '))) ? 'PASS' : 'FAIL'
  await step('abyss_clear', () => c(`/corerpg progress ${NEW2} abyss_clear`), /烧焦的城徽/)
  await c(`/corerpg quest set ${NEW2} 5 0`, 1200)
  for (let i = 0; i < 6; i++) await c(`/corerpg progress ${NEW2} raid_clear`, 250) // ≥Lv30 for the calamity gate
  await step('calamity_join', async () => { B.chat('/corerpg calamity join'); await wait(1500) }, /祭坛上的灰烬在旋转/)
  await step('calamity_boss', () => c(`/corerpg quest event ${NEW2} calamity_boss`), /城里有人记得它的名字/)
  await c(`/corerpg quest set ${NEW2} 5 3`, 1200)
  await step('daily_x2', async () => { await c(`/corerpg progress ${NEW2} daily_clear`); await c(`/corerpg progress ${NEW2} daily_clear`) }, /同袍们在集结/)
  await c(`/corerpg quest set ${NEW2} 6 0`, 1200)
  await step('talk_ch6', async () => { r.b_talk_raw = (await talkNpc(B, c)).slice(0, 120) }, /最后一个守誓人/)
  await step('raid_clear', () => c(`/corerpg progress ${NEW2} raid_clear`), /终厅的火，终于熄了/)
  await c(`/corerpg quest set ${NEW2} 6 3`, 1200)
  await step('final', async () => { await talkNpc(B, c) }, /第一卷「旧誓余烬」完成/)
  r.b_quest_done = (await quest(B)).slice(0, 120)
  console.log('MAINLINE_RESULT', JSON.stringify(r, null, 2))
  await wait(500); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
