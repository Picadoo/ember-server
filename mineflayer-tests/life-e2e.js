// Phase-1 (2026-09-27 CST): life exchanges end to end on the fishing bot — relic x5 -> reforge stone,
// pearl x3 + core fragment x3 -> stable charm, junk -> soul dust, soul dust x10 -> pet egg -> /corerpg pet unlock.
// Op top-ups are printed (TOPUP) so the report can tell fished items from given ones.
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const NAME = process.env.BOT || 'Pf53177'
function niId(it) { try { for (const l of it.nbt.value.display.value.Lore.value.value) { const p = strip(l).trim(); if (/^[a-z0-9_]+$/.test(p)) return p } } catch (e) {} return null }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
;(async () => {
  const op = await joinPlay('RpgBot', { log: false })
  const b = await joinPlay(NAME, { log: false }); await wait(2500)
  const say = async (who, c, ms = 1000) => { const n = who.chatLog.length; who.chat(c); await wait(ms); return who.chatLog.slice(n).map(strip).join(' | ') }
  const ids = ['treasure_ember_relic', 'treasure_ember_pearl', 'junk_ember_boot', 'junk_ember_bone', 'mat_ember_core_fragment', 'mat_ember_soul_dust', 'mat_ember_reforge_stone', 'mat_ember_stable_charm', 'pet_ember_ashling']
  const snap = () => { const o = {}; for (const id of ids) o[id] = count(b, id); return o }
  const r = { before: snap() }
  r.lifeBefore = (await say(b, '/corerpg life', 1500)).split(' | ')[0]
  const lv = +((r.lifeBefore.match(/Lv\.(\d+)/) || [])[1] || 1)
  if (lv < 5) { r.TOPUP_xp = 300; await say(op, `/corerpg life xp ${NAME} 300`) }
  if (count(b, 'mat_ember_core_fragment') < 3) { r.TOPUP_frag = 3; await say(op, `/ni give ${NAME} mat_ember_core_fragment 3`) }
  await say(op, `/corerpg coin give ${NAME} 1500`)
  r.reforge = await say(b, '/corerpg life buy reforge_stone')
  r.stable = await say(b, '/corerpg life buy stable_charm')
  r.stable2 = await say(b, '/corerpg life buy stable_charm')
  r.dust = await say(b, '/corerpg life buy soul_dust 2', 2000); r.dustBone = await say(b, '/corerpg life buy soul_dust_bone 2', 2000)
  const dust = count(b, 'mat_ember_soul_dust'); if (dust < 10) { r.TOPUP_dust = 10 - dust; await say(op, `/ni give ${NAME} mat_ember_soul_dust ${10 - dust}`) }
  r.pet = await say(b, '/corerpg life buy pet_ashling')
  r.pet2 = await say(b, '/corerpg life buy pet_ashling')
  const egg = b.inventory.items().find(i => niId(i) === 'pet_ember_ashling'); if (egg) { await b.equip(egg, 'hand'); await wait(400) }
  r.summon = await say(b, '/corerpg pet summon pet_ember_ashling', 1500)
  r.after = snap()
  r.lifeAfter = (await say(b, '/corerpg life', 1500))
  console.log('LIFE_E2E ' + JSON.stringify(r, null, 1))
  b.quit(); op.quit(); await wait(800); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 120000)
