// Audit fix verification (CoreRpg 1.4.10 + menu/DP config fixes) as a fresh non-op bot.
const mineflayer = require('mineflayer')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const BOT = process.env.BOT || 'AudA926'
const b = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: BOT, version: '1.12.2', auth: 'offline' })
const log = []; b.on('message', m => log.push(m.toString()))
const since = n => log.slice(n).join(' | ')
async function run(c, ms) { const n = log.length; b.chat(c); await wait(ms || 1500); return since(n) }
async function openHub() { if (b.currentWindow) b.closeWindow(b.currentWindow); await wait(400); b.chat('/ember'); for (let i = 0; i < 20 && !b.currentWindow; i++) await wait(200); return b.currentWindow }
async function click(namePart) {
  const w = b.currentWindow; if (!w) return 'no-window'
  const slot = w.slots.findIndex((it, i) => it && i < w.inventoryStart && (()=>{ let n=''; try { n = it.nbt.value.display.value.Name.value } catch (e) { n = it.customName || '' } return strip(n).trim() === namePart })())
  if (slot < 0) return 'no-item:' + namePart
  try { await b.clickWindow(slot, 0, 0) } catch (e) {}
  await wait(1500); return 'clicked@' + slot
}
b.once('spawn', async () => {
  const r = {}
  await wait(2500)
  r.spawn = await run('/corerpg spawn EmberZombie 1')
  r.check_spawn_refused = /需要 corerpg.admin/.test(r.spawn) ? 'PASS' : 'FAIL'
  r.ring = await run('/corerpg raid claim-ring')
  r.check_ring_selfclaim_refused = /通关箱自动结算/.test(r.ring) && !/获得余烬团戒/.test(r.ring) ? 'PASS' : 'FAIL'
  r.pass1 = await run('/corerpg pass free'); r.pass2 = await run('/corerpg pass free')
  r.check_pass_daily = /今日补给已寄出/.test(r.pass1) && /今日补给已领取/.test(r.pass2) ? 'PASS' : 'FAIL'
  r.claim = await run('/corerpg mail claim all')
  r.ec = await run('/corerpg enderchest', 1500); r.ec_window = b.currentWindow ? strip(b.currentWindow.title) : null
  r.check_enderchest = r.ec_window ? 'PASS' : 'FAIL'
  if (b.currentWindow) b.closeWindow(b.currentWindow)
  r.stub = await run('/dp start ember_daily', 3000)
  r.check_stub_refused = /仅供 OP 编辑地图/.test(r.stub) ? 'PASS' : 'FAIL'
  // hub menu → 挂机庭 (console mvtp)
  const tpd = () => new Promise(res => { const t = setTimeout(() => res(false), 5000); b.once('forcedMove', () => { clearTimeout(t); res(true) }) })
  // storage menu → 末影箱 button
  await wait(1000); await openHub(); r.st_click1 = await click('仓库'); await wait(1500); r.st_title = b.currentWindow ? strip(b.currentWindow.title) : null
  r.st_click2 = await click('末影箱'); await wait(1000); r.st_after = b.currentWindow ? strip(b.currentWindow.title) : null
  r.check_ec_button = r.st_after && /Ender|末影/i.test(r.st_after) ? 'PASS' : 'FAIL'
  await wait(1500); await openHub(); r.cal_click1 = await click('灾厄'); await wait(1500)
  r.cal_title = b.currentWindow ? strip(b.currentWindow.title) : null
  let moved = tpd(); r.cal_click2 = await click('奔赴灾厄'); r.cal_teleported = await moved
  r.pos_after_cal = b.entity.position.floored().toString()
  r.check_calamity_button = r.cal_teleported ? 'PASS' : 'FAIL'
  await openHub(); r.hub_title = b.currentWindow ? strip(b.currentWindow.title) : null
  r.pos_before = b.entity.position.floored().toString()
  moved = tpd(); r.afk_click = await click('挂机庭'); r.afk_teleported = await moved
  r.pos_after_afk = b.entity.position.floored().toString()
  r.check_afk_button = r.afk_teleported ? 'PASS' : 'FAIL'
  console.log('AUDIT_RESULT', JSON.stringify(r, null, 2)); process.exit(0)
})
