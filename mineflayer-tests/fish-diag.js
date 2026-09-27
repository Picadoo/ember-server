// Phase-1 (2026-09-27 CST): fishing bite diagnostics — op bot casts into open surface water and reports the
// server-side hook state via /corerpg life fishdebug (block / sky light / biome every 2 s) plus PlayerFishEvent states.
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NAME = process.env.BOT || 'RpgBot'
const WAIT = +(process.env.WAIT || 40000), CASTS = +(process.env.CASTS || 3)
;(async () => {
  const b = await joinPlay(NAME, { log: false }); b.loadPlugin(pathfinder); await wait(3000)
  const mv = new Movements(b); mv.canDig = false; b.pathfinder.setMovements(mv)
  if (process.env.TP) { b.chat('/tp ' + process.env.TP); await wait(2500) }
  b.chat(`/ni give ${NAME} tool_ember_rod 1`); await wait(1200)
  const me = b.entity.position
  const water = b.findBlocks({ matching: bl => bl && bl.name === 'water', maxDistance: 40, count: 400 })
    .map(v => b.blockAt(v)).filter(bl => bl && bl.metadata === 0 && b.blockAt(bl.position.offset(0, 1, 0)).name === 'air'
      && (process.env.ANYSKY || bl.skyLight >= 15))
    .sort((a, c) => a.position.distanceTo(me) - c.position.distanceTo(me))
  console.log('open surface water candidates', water.length, water.slice(0, 5).map(w => w.position.toString()).join(' '))
  const w = process.env.AT ? b.blockAt(new Vec3(...process.env.AT.split(',').map(Number))) : water[0]
  if (!w) { console.log('no water'); process.exit(3) }
  if (!process.env.TP) { b.pathfinder.setGoal(new goals.GoalNear(w.position.x, w.position.y + 1, w.position.z, 3)); await wait(10000); b.pathfinder.setGoal(null) }
  console.log('bot at', b.entity.position.toString(), 'target', w.position.toString())
  const rod = b.inventory.items().find(i => i.name === 'fishing_rod'); if (rod) await b.equip(rod, 'hand')
  let T = Date.now()
  b.on('message', m => { const s = strip(m.toString()); if (/fishdebug|生活/.test(s)) console.log('  ', ((Date.now() - T) / 1000).toFixed(1) + 's', s) })
  b.chat('/corerpg life fishdebug'); await wait(500)
  T = Date.now()
  for (let i = 0; i < CASTS; i++) {
    await b.lookAt(w.position.offset(0.5, 1, 0.5), true)
    T = Date.now(); console.log('cast', i + 1)
    let bite = null
    const onSound = (name) => { if (/bobber|splash/i.test(String(name))) bite = bite || ((Date.now() - T) / 1000).toFixed(1) }
    b.on('soundEffectHeard', onSound); b.on('hardcodedSoundEffectHeard', (id) => { onSound('id' + id) })
    const p = b.fish().then(() => 'caught').catch(e => 'err:' + e.message)
    const res = await Promise.race([p, wait(WAIT).then(() => 'timeout')])
    b.removeListener('soundEffectHeard', onSound)
    console.log('  result', res, 'biteSound', bite, 'after', ((Date.now() - T) / 1000).toFixed(1) + 's')
    if (res === 'timeout') { b.activateItem(); await wait(1500) }
    await wait(1000)
  }
  b.chat('/corerpg life fishdebug'); await wait(800)
  console.log('inv', b.inventory.items().map(i => i.name + 'x' + i.count).join(','))
  b.quit(); await wait(500); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 60000 + CASTS * (WAIT + 5000))
