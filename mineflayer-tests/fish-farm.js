// Phase-1 (2026-09-27 CST): non-op bot fishes like a player at the hub's east still-water pond for DUR ms,
// logs every cast (bite time) and the NI catches, then (optionally) runs the life exchanges end to end.
const { joinPlay } = require('./lib/proxy-login'); const { Vec3 } = require('vec3')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NAME = process.env.BOT || 'Pf53177', DUR = +(process.env.DUR || 600000)
const SPOT = (process.env.SPOT || '62.5 70 104.5'), AT = new Vec3(...(process.env.AT || '62,69,101').split(',').map(Number))
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
;(async () => {
  const op = await joinPlay('RpgBot', { log: false })
  const b = await joinPlay(NAME, { log: false }); await wait(2500)
  op.chat(`/tp ${NAME} ${SPOT}`); await wait(2500); op.quit()
  if (!b.inventory.items().find(i => niId(i) === 'tool_ember_rod')) { b.chat('/corerpg life buy rod'); await wait(1500) }
  const rod = b.inventory.items().find(i => niId(i) === 'tool_ember_rod'); await b.equip(rod, 'hand')
  const times = []; let fails = 0; const t0 = Date.now()
  while (Date.now() - t0 < DUR) {
    const r = b.inventory.items().find(i => niId(i) === 'tool_ember_rod')
    if (!r) { b.chat('/corerpg life buy rod'); await wait(1500); const r2 = b.inventory.items().find(i => niId(i) === 'tool_ember_rod'); if (!r2) break; await b.equip(r2, 'hand') }
    else if (b.heldItem !== r && niId(b.heldItem) !== 'tool_ember_rod') await b.equip(r, 'hand')
    await b.lookAt(AT.offset(0.5, 1, 0.5), true)
    const T = Date.now()
    const res = await Promise.race([b.fish().then(() => 'ok').catch(e => 'err'), wait(45000).then(() => 'to')])
    if (res === 'ok') times.push((Date.now() - T) / 1000); else { fails++; if (res === 'to') { b.activateItem(); await wait(800) } }
    await wait(400)
  }
  const ids = ['fish_ember_cod', 'fish_ember_salmon', 'fish_ember_puffer', 'treasure_ember_relic', 'treasure_ember_pearl', 'junk_ember_boot', 'junk_ember_bone']
  const inv = {}; for (const id of ids) inv[id] = count(b, id)
  times.sort((a, c) => a - c)
  const avg = times.reduce((a, c) => a + c, 0) / (times.length || 1)
  const r = { minutes: ((Date.now() - t0) / 60000).toFixed(1), catches: times.length, fails, avgBite: avg.toFixed(1), p10: times[Math.floor(times.length * 0.1)], median: times[Math.floor(times.length / 2)], p90: times[Math.floor(times.length * 0.9)], inv }
  let n = b.chatLog.length; b.chat('/corerpg life'); await wait(1500); r.life = strip(b.chatLog.slice(n, n + 1).join(''))
  console.log('FARM_RESULT ' + JSON.stringify(r))
  b.quit(); await wait(800); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, (+(process.env.DUR || 600000)) + 120000)
