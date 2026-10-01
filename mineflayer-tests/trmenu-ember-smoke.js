// TrMenu ember menus smoke (non-op, current topology: proxy → AuthMe → play via joinPlay).
// Regression for B2.122: TrMenu/TabooLib `tell:` parses `[text](args)` as component syntax and ate
// `[团本]`/`[深渊]` + their colour; menus now use 【..】. Asserts the raw chat packets keep them.
const { joinPlay } = require('./lib/proxy-login')
const nbt = require('prismarine-nbt')
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s).replace(/§./g, '')
function nameOf (it) { try { return (nbt.simplify(it.nbt).display || {}).Name || '' } catch (e) { return '' } }
function findSlot (w, re) { for (let i = 0; i < w.slots.length; i++) if (w.slots[i] && re.test(strip(nameOf(w.slots[i])))) return i; return -1 }
function waitWindow (bot, ms = 5000) {
  return new Promise((res) => { const t = setTimeout(() => res(null), ms); bot.once('windowOpen', (w) => { clearTimeout(t); setTimeout(() => res(w), 1200) }) })
}
const results = []
const check = (name, ok, info) => { results.push({ name, ok }); console.log(`[trmenu] ${ok ? 'PASS' : 'FAIL'}  ${name} — ${info}`) }

async function openSub (bot, hubRe, startRe) {
  let p = waitWindow(bot); bot.chat('/ember'); const hub = await p
  if (!hub) return { err: 'hub did not open' }
  p = waitWindow(bot); bot.clickWindow(findSlot(hub, hubRe), 0, 0).catch(() => {}); const sub = await p
  if (!sub) return { err: 'submenu did not open' }
  const n = bot.rawPackets.length
  bot.clickWindow(findSlot(sub, startRe), 0, 0).catch(() => {})
  await wait(3000)
  if (bot.currentWindow) { bot.closeWindow(bot.currentWindow); await wait(600) }
  return { title: sub.title, packets: bot.rawPackets.slice(n) }
}

;(async () => {
  const bot = await joinPlay(process.env.MC_USER || 'EmberTestOp', { log: false, timeout: 60000 })
  bot.rawPackets = []
  bot._client.on('chat', (pk) => bot.rawPackets.push(pk.message))
  await wait(1500)
  const raid = await openSub(bot, /^团本$/, /开始协作/)
  const rp = (raid.packets || []).find((m) => m.includes('尝试进入')) || ''
  check('ember_raid click tell keeps 【团本】 + blue', /"color":"blue","text":"【团本】 "/.test(rp), raid.err || rp.slice(0, 160))
  const abyss = await openSub(bot, /^深渊$/, /开始下潜/)
  const ap = (abyss.packets || []).find((m) => m.includes('尝试下潜')) || ''
  check('ember_abyss click tell keeps 【深渊】 + dark_purple', /"color":"dark_purple","text":"【深渊】 "/.test(ap), abyss.err || ap.slice(0, 160))
  const all = [...(raid.packets || []), ...(abyss.packets || [])].join('\n')
  check('no swallowed [..](..) component syntax', !/\]\(|"text":""\},\{"text":"(团本|深渊)"/.test(all), `${(raid.packets || []).length + (abyss.packets || []).length} packets`)
  bot.quit()
  const fail = results.filter((r) => !r.ok).length
  console.log(`[trmenu] PASS=${results.length - fail} FAIL=${fail}`)
  setTimeout(() => process.exit(fail ? 2 : 0), 300)
})().catch((e) => { console.error('[trmenu] FATAL', e.message || e); process.exit(1) })
