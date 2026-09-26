// Newbie path, NO op help at all (2026-09-27 CST, CoreRpg 1.8.1): fresh non-op bot via proxy → spawn in ember_hub →
// WALK (mineflayer-pathfinder) to 引路人·灰烛 → chapter 1 (menu → 挂机庭, fight AFK mobs on foot, deaths allowed: keepInventory) →
// /corerpg sign → /hub → walk back to the NPC → chapter 2 menu daily (开始挑战) fought on foot → clear.
// No other bot issues commands for this player. Needs: npm i --no-save mineflayer-pathfinder
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Newb' + Math.floor(Math.random() * 90000 + 10000))
const NPC = new Vec3(-16.5, 58, 106.5)
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager', 'pig_zombie', 'vindication_illager'])
const since = (b, n) => b.chatLog.slice(n).join(' | ')
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
function nameOf(it) { try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' } }
const blade = b => b.inventory.items().find(i => /gear_ember_(t\d_)?blade/.test(niId(i) || ''))
async function goto(b, pos, range, ms) {
  b.pathfinder.setGoal(new goals.GoalNear(pos.x, pos.y, pos.z, range || 2))
  const t0 = Date.now(); while (Date.now() - t0 < (ms || 30000)) { if (b.entity.position.distanceTo(pos) <= (range || 2) + 0.8) break; await wait(300) }
  b.pathfinder.setGoal(null); return b.entity.position.distanceTo(pos)
}
async function menuOpen(b) { if (b.currentWindow) { b.closeWindow(b.currentWindow); await wait(500) } b.chat('/ember'); for (let i = 0; i < 25 && !b.currentWindow; i++) await wait(200); await wait(400); return !!b.currentWindow }
async function click(b, name) { const w = b.currentWindow; if (!w) return 'no-window'; const slot = w.slots.findIndex((it, i) => it && i < w.inventoryStart && nameOf(it) === name); if (slot < 0) return 'no-item:' + name; try { await b.clickWindow(slot, 0, 0) } catch (e) {} await wait(1600); return 'ok' }
async function path(b, ...names) { await menuOpen(b); const out = []; for (const n of names) out.push(await click(b, n)); return out.join(',') }
async function fight(b, untilRe, ms, n0) {
  const t0 = Date.now(); let deaths = 0
  b.on('death', () => deaths++)
  while (Date.now() - t0 < ms && !untilRe.test(strip(since(b, n0)))) {
    if (!b.entity || b.health <= 0) { await wait(1000); continue }
    const bl = blade(b); if (bl && (!b.heldItem || b.heldItem.slot !== bl.slot)) { try { await b.equip(bl, 'hand') } catch (e) {} }
    const e = Object.values(b.entities).filter(x => x && x !== b.entity && MOBS.has(x.name) && x.position.distanceTo(b.entity.position) < 60)
      .sort((x, y) => x.position.distanceTo(b.entity.position) - y.position.distanceTo(b.entity.position))[0]
    if (!e) { b.pathfinder.setGoal(null); await wait(800); continue }
    const d = e.position.distanceTo(b.entity.position)
    if (d > 2.8) { b.pathfinder.setGoal(new goals.GoalFollow(e, 1.5), true) }
    else { b.pathfinder.setGoal(null); try { await b.lookAt(e.position.offset(0, (e.height || 1.8) * 0.85, 0), true) } catch (x) {} b.attack(e) }
    await wait(500)
  }
  b.pathfinder.setGoal(null); return { ms: Date.now() - t0, deaths }
}
async function talkNpc(b) {
  const dist = await goto(b, NPC.offset(0, 0, 2), 1.5, 40000)
  const npc = Object.values(b.entities).find(e => e.name === 'villager' && e.position.distanceTo(NPC) < 2)
  if (!npc) return { dist, text: 'no-npc' }
  await b.lookAt(npc.position.offset(0, 1.5, 0)); await wait(300)
  const n = b.chatLog.length; b.activateEntityAt(npc, npc.position.offset(0, 1, 0)); b.activateEntity(npc); await wait(2500)
  return { dist: +dist.toFixed(2), text: strip(since(b, n)) }
}
;(async () => {
  const r = { name: NEW }
  const b = await joinPlay(NEW, { log: false }); b.on('message', m => console.log(`[${NEW}]`, m.toString())); b.loadPlugin(pathfinder)
  await wait(5000)
  const mv = new Movements(b); mv.canDig = false; mv.allow1by1towers = false; mv.scafoldingBlocks = []; b.pathfinder.setMovements(mv)
  r.spawn = b.entity.position.toString(); r.auto_start = /第1章 · 余烬初醒/.test(strip(b.chatLog.join(' | '))); r.starter = !!blade(b)
  r.check_start = r.auto_start && r.starter ? 'PASS' : 'FAIL'
  // 1) walk from spawn to the NPC and talk
  const t1 = await talkNpc(b); r.walk_dist_left = t1.dist; r.talk1 = t1.text.slice(0, 120)
  r.check_walk_talk = /这城烧了三十年/.test(t1.text) ? 'PASS' : 'FAIL'
  // 2) menu → 挂机庭, fight on foot
  r.afk_click = await path(b, '挂机庭'); await wait(3500)
  r.afk_spawn = b.game && b.entity.position.toString()
  let n = b.chatLog.length
  r.afk_fight = await fight(b, /在枢纽签到一次（|骨头碎了/, 6 * 60000, n)
  r.check_afk = /骨头碎了/.test(strip(since(b, n))) ? 'PASS' : 'FAIL'
  r.blade_after_afk = !!blade(b)
  // 3) sign, /hub, walk to NPC
  n = b.chatLog.length; b.chat('/corerpg sign'); await wait(2000); r.check_sign = /每日点一次名/.test(strip(since(b, n))) ? 'PASS' : 'FAIL'
  n = b.chatLog.length; b.chat('/hub'); await wait(3500); r.hub = strip(since(b, n)).slice(0, 80); r.hub_pos = b.entity.position.toString()
  const t2 = await talkNpc(b); r.talk2 = t2.text.slice(0, 200)
  r.check_ch1_done = /第1章「余烬初醒」完成/.test(t2.text) && /原版经验 \+5/.test(t2.text) ? 'PASS' : 'FAIL'
  // 4) chapter 2 daily via menu, fought on foot
  n = b.chatLog.length
  r.daily_click = await path(b, '日常 · 余烬窟', '开始挑战'); await wait(4000)
  r.daily_start = strip(since(b, n)).slice(0, 160)
  r.daily_fight = await fight(b, /余烬窟·日 通关！/, 6 * 60000, n)
  await wait(12000)
  const t = strip(since(b, n))
  r.check_daily = /余烬窟·日 通关！/.test(t) ? 'PASS' : 'FAIL'
  r.check_daily_quest = /窟里的尸骸穿着旧城卫的甲/.test(t) && /你带回了窟底的火/.test(t) ? 'PASS' : 'FAIL'
  r.elite_xp = /击杀精英|首领/.test(t)
  n = b.chatLog.length; b.chat('/corerpg quest'); await wait(1500); r.quest_now = strip(since(b, n)).slice(0, 260)
  n = b.chatLog.length; b.chat('/corerpg level'); await wait(1500); r.level_now = strip(since(b, n)).slice(0, 120)
  console.log('NEWBIE_RESULT', JSON.stringify(r, null, 2)); await wait(500); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
