// Live menu text dump (non-op bot, proxy → AuthMe → play): opens /ember, optionally clicks into a
// submenu by item name, and prints every item's name + lore lines (colour codes kept), flagging
// unreplaced %placeholders%. Used to live-verify TrMenu copy windows (B2.113/B2.118/B2.120/B2.121…).
//   node menu-lore-dump.js                 # hub only
//   node menu-lore-dump.js 团本            # hub → 团本 submenu
//   GREP='体力' node menu-lore-dump.js 深渊  # only lines matching GREP
//   CLICK='次数说明' node menu-lore-dump.js 团本   # also click that submenu item, print the chat it sends
//                                                 (and dump the next menu if the click opens one)
// Chat (raw JSON packets) received while opening the submenu / after CLICK is printed as [chat].
const { joinPlay } = require('./lib/proxy-login')
const nbt = require('prismarine-nbt')
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s).replace(/§./g, '')
const GREP = process.env.GREP ? new RegExp(process.env.GREP) : null
function disp (it) { try { const d = nbt.simplify(it.nbt).display || {}; return { name: d.Name || '', lore: d.Lore || [] } } catch (e) { return { name: '', lore: [] } } }
function waitWindow (bot, ms = 5000) { return new Promise((res) => { const t = setTimeout(() => res(null), ms); bot.once('windowOpen', (w) => { clearTimeout(t); setTimeout(() => res(w), 1500) }) }) }
function dump (label, w) {
  const n = w.inventoryStart != null ? w.inventoryStart : w.slots.length - 36
  console.log(`[menu] === ${label} title=${w.title}`)
  let unrepl = 0
  for (let i = 0; i < n; i++) {
    const it = w.slots[i]; if (!it) continue
    const d = disp(it); if (!strip(d.name).trim()) continue
    const lines = [d.name, ...d.lore]
    unrepl += lines.filter((l) => /%[a-z0-9_]+%/i.test(l)).length
    const shown = GREP ? lines.filter((l) => GREP.test(strip(l))) : lines
    if (!shown.length) continue
    console.log(`[menu] slot ${i} ${strip(d.name)}`)
    for (const l of shown) console.log(`[menu]    ${l}`)
  }
  console.log(`[menu] ${label} unreplacedPAPI=${unrepl}`)
}
;(async () => {
  const bot = await joinPlay(process.env.MC_USER || 'EmberTestOp', { log: false })
  await wait(1000)
  let p = waitWindow(bot); bot.chat('/ember'); const hub = await p
  if (!hub) throw new Error('hub did not open')
  const sub = process.argv[2]
  if (!sub) dump('ember_hub', hub)
  else {
    let slot = -1
    for (let i = 0; i < hub.slots.length; i++) if (hub.slots[i] && strip(disp(hub.slots[i]).name) === sub) { slot = i; break }
    if (slot < 0) throw new Error('no hub item named ' + sub)
    const pk = []; bot._client.on('chat', (m) => pk.push(m.message))
    p = waitWindow(bot); bot.clickWindow(slot, 0, 0).catch(() => {}); const w = await p
    if (!w) throw new Error('submenu did not open')
    dump(sub, w)
    for (const m of pk.splice(0)) console.log(`[chat] open ${sub}: ${m}`)
    if (process.env.CLICK) {
      const re = new RegExp(process.env.CLICK); let cs = -1
      for (let i = 0; i < w.slots.length; i++) if (w.slots[i] && re.test(strip(disp(w.slots[i]).name))) { cs = i; break }
      if (cs < 0) throw new Error('no submenu item matching ' + process.env.CLICK)
      const pw = waitWindow(bot, 4000); bot.clickWindow(cs, 0, 0).catch(() => {})
      const w2 = await pw; if (!w2) await wait(1000)
      for (const m of pk.splice(0)) console.log(`[chat] click ${strip(disp(w.slots[cs]).name)}: ${m}`)
      if (w2) dump(strip(disp(w.slots[cs]).name), w2) // the click opened another menu: dump it too
    }
  }
  bot.quit(); setTimeout(() => process.exit(0), 300)
})().catch((e) => { console.error('[menu] FATAL', e.message || e); process.exit(1) })
