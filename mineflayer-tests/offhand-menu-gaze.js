/** B-flex-1 菜单目视补测：/ember → 套装窗 · 读 hub V lore + set O lore / Open tell */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '').replace(/\u00a7./g, '')
const OP = 'OfOpB29'
const USER = 'OfLtB29'
const CONSOLE = '/workspace/minecraft/server-runtime/console.in'
const OUT = '/tmp/offhand-menu-gaze.json'

function nameOf(it) {
  try { return strip(it.nbt.value.display.value.Name.value).trim() } catch (e) { return '' }
}
function loreOf(it) {
  try { return (it.nbt.value.display.value.Lore.value.value || []).map(l => strip(l)) } catch (e) { return [] }
}

;(async () => {
  const r = { pass: true, checks: {}, evidence: {} }
  const note = (k, ok, d) => { r.checks[k] = { ok: !!ok, detail: d }; if (!ok) r.pass = false; console.log((ok?'PASS':'FAIL'), k, '::', d) }

  const op = await joinPlay(OP, { log: false })
  await wait(1200)
  try { fs.writeFileSync(CONSOLE, `op ${OP}\n`); await wait(1000) } catch (_) {}
  const c = async (cmd, ms=800) => { console.log('[op]', cmd); op.chat(cmd); await wait(ms) }
  await c(`/lp user ${OP} parent add admin`, 800)
  await c(`/op ${OP}`, 500)

  const b = await joinPlay(USER, { log: true })
  const chat = []
  b.on('message', m => chat.push(m.toString()))
  await wait(2200)
  await c(`/mvtp ${USER} ember_hub`, 1500)

  // open hub
  const n0 = chat.length
  b.chat('/ember')
  await wait(2000)
  const hubTitle = strip((b.currentWindow && b.currentWindow.title) || '')
  note('hub_open', /冒险枢纽|余烬/.test(hubTitle), hubTitle)

  // read slot 31 (V 套装) lore from hub window
  let hubSlot = null
  try { hubSlot = b.currentWindow.slots[31] } catch (_) {}
  const hubLore = hubSlot ? loreOf(hubSlot) : []
  const hubName = hubSlot ? nameOf(hubSlot) : ''
  r.evidence.hubIcon = { name: hubName, lore: hubLore }
  note('hub_V_offhand_lore', /副手/.test(hubLore.join(' ')) && /副手槽/.test(hubLore.join(' ')), JSON.stringify({ hubName, hubLore }))

  // click into set
  const n1 = chat.length
  try { await b.clickWindow(31, 0, 0) } catch (e) { console.log('click', e.message) }
  await wait(2500)
  const setTitle = strip((b.currentWindow && b.currentWindow.title) || '')
  const setTells = chat.slice(n1).map(strip)
  r.evidence.set = { title: setTitle, tells: setTells }
  note('set_open', /套装/.test(setTitle), setTitle)
  note('set_open_tell', setTells.some(s => /副手饰品/.test(s)), JSON.stringify(setTells.slice(0, 10)))

  // read O icon slot 31 in set
  let setSlot = null
  try { setSlot = b.currentWindow.slots[31] } catch (_) {}
  const setLore = setSlot ? loreOf(setSlot) : []
  const setName = setSlot ? nameOf(setSlot) : ''
  r.evidence.setIcon = { name: setName, lore: setLore }
  note('set_O_offhand_lore', /副手/.test(setName + setLore.join(' ')), JSON.stringify({ setName, setLore }))

  try { b.closeWindow(b.currentWindow) } catch (_) {}
  await c(`/lp user ${OP} parent remove admin`, 500)
  await c(`/deop ${OP}`, 500)
  try { fs.writeFileSync(CONSOLE, `deop ${OP}\n`); await wait(600) } catch (_) {}
  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json', '[]\n')
  try { fs.writeFileSync('/workspace/minecraft/login-runtime/ops.json', '[]\n') } catch (_) {}
  r.ops = fs.readFileSync('/workspace/minecraft/server-runtime/ops.json', 'utf8').trim()
  note('ops_empty', r.ops === '[]', r.ops)
  try { b.quit(); op.quit() } catch (_) {}
  fs.writeFileSync(OUT, JSON.stringify(r, null, 2))
  console.log('WROTE', OUT, 'pass=' + r.pass)
  process.exit(r.pass ? 0 : 1)
})().catch(e => { console.error(e); try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n'); fs.writeFileSync(CONSOLE, `deop ${OP}\n`) } catch(_){}; process.exit(1) })
setTimeout(() => { try { fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n') } catch(_){}; process.exit(2) }, 120000)
