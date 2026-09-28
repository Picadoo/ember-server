/** 补测：开阔地短移一眼（装配已在主测 PASS；此处仅位移） */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const OP = 'FxMvOp1'
const USER = 'FxMvU1'
const OUT = '/tmp/flex-cast-move-once.json'

;(async () => {
  const r = { pass: false, evidence: {} }
  const op = await joinPlay(OP, { log: false })
  await wait(800)
  fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(700)
  op.chat(`/lp user ${OP} parent add admin`); await wait(600)
  op.chat(`/op ${OP}`); await wait(400)
  const c = async (cmd, ms=800) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }

  const b = await joinPlay(USER, { log: true })
  const chat = []
  b.on('message', m => chat.push(strip(m.toString())))
  await wait(2000)
  await c(`/lp user ${USER} permission set corerpg.use true`, 500)
  // open pad in world: platform + air corridor south
  await c(`/mvtp ${USER} world`, 1500)
  await c(`/tp ${USER} 200 80 200 0 0`, 1000)
  // build small quartz pad + clear 8 blocks south air (temp; not committed)
  fs.writeFileSync(CONSOLE, [
    `execute ${USER} ~ ~ ~ fill ~-1 ~-1 ~-1 ~1 ~-1 ~8 quartz_block`,
    `execute ${USER} ~ ~ ~ fill ~-1 ~ ~-1 ~1 ~2 ~8 air`,
    `tp ${USER} 200 80 200 0 0`
  ].join('\n') + '\n')
  await wait(2000)
  // also via op chat fill if execute form differs on 1.12
  await c(`/tp ${USER} 200 80 200`, 600)
  // 1.12 fill via op at coords
  await c(`/fill 199 79 199 201 79 208 quartz_block`, 800)
  await c(`/fill 199 80 199 201 82 208 air`, 800)
  await c(`/tp ${USER} 200 80 200 0 0`, 1000)

  b.chat('/corerpg flex equip flex_ember_step'); await wait(900)
  const n0 = chat.length
  const pos0 = { x: b.entity.position.x, y: b.entity.position.y, z: b.entity.position.z }
  // face south explicitly
  await c(`/tp ${USER} ${pos0.x.toFixed(1)} ${pos0.y.toFixed(1)} ${pos0.z.toFixed(1)} 0 0`, 800)
  b.chat('/corerpg flex cast'); await wait(1500)
  const pos1 = { x: b.entity.position.x, y: b.entity.position.y, z: b.entity.position.z }
  const moved = Math.hypot(pos1.x - pos0.x, pos1.z - pos0.z)
  const castChat = chat.slice(n0)
  r.evidence = { pos0, pos1, moved, castChat }
  r.pass = moved >= 1.5 && castChat.some(s => /释放/.test(s) && /踏步|轻技/.test(s))
  console.log('RESULT', JSON.stringify(r, null, 2))

  // stamina check
  const n1 = chat.length
  b.chat('/corerpg stamina show'); await wait(900)
  r.evidence.stamina = chat.slice(n1)

  // menu cast path once more from open flex page
  b.chat('/corerpg flex unequip'); await wait(600)
  b.chat('/corerpg flex equip flex_ember_step'); await wait(700)
  await wait(14500) // CD
  fs.writeFileSync(CONSOLE, `trmenu open ember_flex_skill ${USER}\n`); await wait(1500)
  await c(`/tp ${USER} 200 80 200 0 0`, 800)
  const n2 = chat.length
  const p0 = { x: b.entity.position.x, z: b.entity.position.z }
  try { await Promise.race([b.clickWindow(24, 0, 0), wait(2800)]) } catch (e) { console.log('click', e.message) }
  await wait(1200)
  const p1 = { x: b.entity.position.x, z: b.entity.position.z }
  const moved2 = Math.hypot(p1.x - p0.x, p1.z - p0.z)
  r.evidence.menuCast = { moved2, chat: chat.slice(n2), p0, p1 }
  r.passMenu = moved2 >= 1.5 && chat.slice(n2).some(s => /释放/.test(s))
  console.log('MENU_CAST', r.evidence.menuCast)

  await c(`/lp user ${OP} parent remove admin`, 400)
  await c(`/deop ${OP}`, 400)
  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
  fs.writeFileSync(OUT, JSON.stringify(r, null, 2))
  try { b.quit(); op.quit() } catch (_) {}
  process.exit(r.pass || r.passMenu ? 0 : 1)
})().catch(e => { console.error(e); fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n'); process.exit(1) })
setTimeout(() => { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n'); process.exit(2) }, 120000)
