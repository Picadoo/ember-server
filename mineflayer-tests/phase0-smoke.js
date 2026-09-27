// Phase-0 smoke (2026-09-27 CST, CoreRpg 1.11.0): starter food, life vendor (/corerpg life), fishing, quest state checks
// (covenant / talent / enhance +3 incl. already-done), enhance coin fee, dungeon command hints + full HP on entry + reconnect grace.
// Needs RpgBot op. usage: node phase0-smoke.js
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Pz' + Math.floor(Math.random() * 90000 + 10000))
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
const since = (b, n) => strip(b.chatLog.slice(n).join(' | '))
async function say(b, cmd, ms) { const n = b.chatLog.length; b.chat(cmd); await wait(ms || 1500); return since(b, n) }
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay('RpgBot', { log: false })
  let b = await joinPlay(NEW, { log: false }); await wait(5000)
  r.bread_start = count(b, 'food_ember_bread'); r.check_starter_food = r.bread_start >= 16 ? 'PASS' : 'FAIL'
  r.life_show = (await say(b, '/corerpg life')).slice(0, 200)
  r.buy_nocoin = await say(b, '/corerpg life buy bread'); r.check_nocoin = /余烬币不足/.test(r.buy_nocoin) ? 'PASS' : 'FAIL'
  for (const c of [`/corerpg coin give ${NEW} 2000`, `/ni give ${NEW} fish_ember_cod 5`, `/ni give ${NEW} fish_ember_puffer 1`,
    `/ni give ${NEW} mat_ember_shard 40`, `/ni give ${NEW} gear_ember_t1_blade 1`,
    `/corerpg progress ${NEW} raid_clear`, `/corerpg progress ${NEW} raid_clear`]) { op.chat(c); await wait(900) }
  await wait(1500)
  r.buy_bread = await say(b, '/corerpg life buy bread'); r.check_buy_bread = count(b, 'food_ember_bread') >= r.bread_start + 8 ? 'PASS' : 'FAIL'
  r.buy_rod = await say(b, '/corerpg life buy rod'); r.check_rod = count(b, 'tool_ember_rod') === 1 ? 'PASS' : 'FAIL'
  r.cook = await say(b, '/corerpg life cook'); r.check_cook = count(b, 'food_ember_grilled_fish') === 5 ? 'PASS' : 'FAIL'
  r.potion_gate = await say(b, '/corerpg life buy heal_potion'); r.check_lv_gate = /需要生活 Lv\.2/.test(r.potion_gate) ? 'PASS' : 'FAIL'
  // water near hub for fishing?
  const water = b.findBlocks({ matching: bl => bl && (bl.name === 'water' || bl.name === 'flowing_water'), maxDistance: 64, count: 5 })
  r.water_near_hub = water.map(v => v.toString())
  // quest: covenant / talent
  op.chat(`/corerpg quest set ${NEW} 2 2`); await wait(2000)
  r.q_cov = await say(b, '/corerpg covenant set blaze', 2500); r.check_q_cov = /点亮一个天赋节点/.test(r.q_cov) ? 'PASS' : 'FAIL'
  r.q_tal = await say(b, '/corerpg talent unlock blaze_root', 2500); r.check_q_tal = /附魔一次/.test(r.q_tal) ? 'PASS' : 'FAIL'
  // enhance step
  op.chat(`/corerpg quest set ${NEW} 2 5`); await wait(2000)
  const bl = b.inventory.items().find(i => niId(i) === 'gear_ember_t1_blade'); await b.equip(bl, 'hand'); await wait(500)
  const coin0 = await say(b, '/corerpg coin'); r.coin_before = coin0.slice(0, 80)
  let enh = ''
  for (let i = 0; i < 8 && !/达到余烬等级 Lv\.20/.test(enh); i++) enh += ' || ' + await say(b, '/corerpg enhance', 1500)
  r.enhance_log = enh.slice(0, 600); r.check_enh_fee = /手续费/.test(enh) ? 'PASS' : 'FAIL'
  r.check_q_enh = /达到余烬等级 Lv\.20|周烬的门/.test(enh) ? 'PASS' : 'FAIL'
  op.chat(`/corerpg quest set ${NEW} 2 5`); await wait(12500) // 10 s ticker or immediate checkPassive
  r.q_already = since(b, b.chatLog.length - 12).slice(-300); r.check_q_already = /已完成过：把一件余烬装备强化到 \+3/.test(strip(b.chatLog.join('|'))) ? 'PASS' : 'FAIL'
  r.stats = (await say(b, '/corerpg stats')).slice(0, 300); r.check_stats_cov = /誓约：/.test(r.stats) ? 'PASS' : 'FAIL'
  // dungeon
  let n = b.chatLog.length; b.chat('/dp start EmberDaily'); await wait(9000)
  r.dp_start = since(b, n).slice(0, 200)
  r.hp_entry = b.health; r.check_full_hp = b.health >= 39.5 ? 'PASS' : 'FAIL (' + b.health + ')'
  r.d_hub = await say(b, '/hub'); r.check_d_hub = /dp leave/.test(r.d_hub) ? 'PASS' : 'FAIL'
  r.d_ember = await say(b, '/ember'); r.check_d_ember = /dp leave/.test(r.d_ember) ? 'PASS' : 'FAIL'
  r.d_stats = (await say(b, '/corerpg stats')).slice(0, 120); r.check_d_stats = /\[属性\]/.test(r.d_stats) ? 'PASS' : 'FAIL'
  r.d_quest = (await say(b, '/corerpg quest')).slice(0, 120); r.check_d_quest = /\[主线\]/.test(r.d_quest) ? 'PASS' : 'FAIL'
  r.d_skill = (await say(b, '/corerpg skill')).slice(0, 160); r.check_d_skill = /技能|烬斩|冷却/.test(r.d_skill) && !/不可用/.test(r.d_skill) ? 'PASS' : 'FAIL'
  r.d_other = (await say(b, '/corerpg life')).slice(0, 120); r.check_d_other = /不可用/.test(r.d_other) ? 'PASS' : 'FAIL'
  // reconnect
  b.quit(); await wait(4000)
  b = await joinPlay(NEW, { log: false }); await wait(4000)
  r.reconnect = strip(b.chatLog.join(' | ')).slice(-250); r.check_reconnect = /回到了副本中/.test(r.reconnect) ? 'PASS' : 'FAIL'
  r.leave = (await say(b, '/dp leave', 4000)).slice(0, 120)
  console.log('PHASE0_RESULT ' + JSON.stringify(r, null, 1))
  b.quit(); op.quit(); await wait(1000); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 240000)
