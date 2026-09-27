// Phase-0 follow-up (2026-09-27 CST): HP samples right after entering a daily instance; fishing at the hub pool with the vendor rod.
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NEW = process.env.NEW || ('Pf' + Math.floor(Math.random() * 90000 + 10000))
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, re) => b.inventory.items().filter(i => re.test(niId(i) || '')).reduce((a, i) => a + i.count, 0)
;(async () => {
  const r = { name: NEW }
  const op = await joinPlay('RpgBot', { log: false })
  const b = await joinPlay(NEW, { log: false }); b.loadPlugin(pathfinder); await wait(4000)
  const mv = new Movements(b); mv.canDig = false; b.pathfinder.setMovements(mv)
  for (const c of [`/corerpg coin give ${NEW} 500`, `/corerpg progress ${NEW} raid_clear`, `/corerpg progress ${NEW} raid_clear`]) { op.chat(c); await wait(900) }
  b.chat('/corerpg life buy rod'); await wait(1500)
  // fishing
  const water = b.findBlocks({ matching: bl => bl && bl.name === 'water', maxDistance: 48, count: 40 }).map(v => new Vec3(v.x, v.y, v.z))
  r.water = water.slice(0, 3).map(String)
  if (water.length) {
    const w = water[0]; b.pathfinder.setGoal(new goals.GoalNear(w.x, w.y + 1, w.z, 3))
    await wait(9000); b.pathfinder.setGoal(null)
    r.pos = b.entity.position.toString()
    const rod = b.inventory.items().find(i => niId(i) === 'tool_ember_rod'); if (rod) await b.equip(rod, 'hand')
    await b.lookAt(w.offset(0.5, 1, 0.5), true)
    let got = 0; const t0 = Date.now()
    while (Date.now() - t0 < 60000) { try { await Promise.race([b.fish(), wait(15000)]) ; got++ } catch (e) { r.fish_err = e.message; await wait(1000) } }
    r.casts = got; r.caught = count(b, /^(fish_|treasure_|junk_)/)
  }
  const n = b.chatLog.length; b.chat('/corerpg life'); await wait(1500); r.life = strip(b.chatLog.slice(n, n + 1).join(''))
  // HP right after entering the daily
  const hp = []; const t1 = Date.now(); b.on('health', () => hp.push(((Date.now() - t1) / 1000).toFixed(1) + ':' + b.health.toFixed(1)))
  b.on('respawn', () => hp.push(((Date.now() - t1) / 1000).toFixed(1) + ':WORLD'))
  b.chat('/dp start EmberDaily'); await wait(10000)
  r.hp = hp.join(' ')
  b.chat('/dp leave'); await wait(3000)
  console.log('HPF_RESULT ' + JSON.stringify(r, null, 1)); b.quit(); op.quit(); await wait(800); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 150000)
