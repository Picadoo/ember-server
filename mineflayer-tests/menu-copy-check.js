// B2.109 — TrMenu copy assertions (non-op bot via proxy → AuthMe → play; console via lib/console.js):
//  · hub lore: weekly/abyss/raid stamina numbers == live costs (B2.113)
//  · raid menu: open tell / 次数说明 lore+tell use the live raid cost (B2.118); 开始协作 tell keeps 【团本】 and
//    states no cost (B2.110/B2.122); 次数说明 states who pays and when it is refunded (B2.124)
//  · abyss menu: 次数说明 lore+tell, low-stamina grey lore+tell use the live abyss cost (B2.120);
//    开始下潜 tell keeps 【深渊】, no 「体力 30」 (B2.111/B2.122)
// Costs come from `/corerpg stamina` ("消耗 日常30 · 周45 · 精英40 · 深渊30 · 团50").
const { joinPlay } = require('./lib/proxy-login')
const con = require('./lib/console')
const nbt = require('prismarine-nbt')
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s).replace(/§./g, '')
const U = process.env.MC_USER || 'EmberTestOp'
const res = []
const check = (name, ok, info) => { res.push(ok); console.log(`[copy] ${ok ? 'PASS' : 'FAIL'}  ${name} — ${String(info).slice(0, 220)}`) }
function disp (it) { try { const d = nbt.simplify(it.nbt).display || {}; return { name: strip(d.Name || ''), lore: (d.Lore || []).map(strip) } } catch (e) { return { name: '', lore: [] } } }
function slotOf (w, re) { for (let i = 0; i < w.slots.length; i++) if (w.slots[i] && re.test(disp(w.slots[i]).name)) return i; return -1 }
function loreOf (w, re) { const i = slotOf(w, re); return i < 0 ? [] : disp(w.slots[i]).lore }
function waitWindow (bot, ms = 5000) { return new Promise((res) => { const t = setTimeout(() => res(null), ms); bot.once('windowOpen', (w) => { clearTimeout(t); setTimeout(() => res(w), 1500) }) }) }
;(async () => {
  const bot = await joinPlay(U, { log: false })
  const pk = []; bot._client.on('chat', (m) => pk.push(m.message))
  const n0 = bot.chatLog.length; bot.chat('/corerpg stamina'); await wait(1500)
  const st = strip(bot.chatLog.slice(n0).join(' '))
  const cost = (k) => Number((st.match(new RegExp(k + '(\\d+)')) || [])[1])
  const C = { weekly: cost('周'), abyss: cost('深渊'), raid: cost('团') }
  console.log('[copy] live costs', JSON.stringify(C))
  const openHub = async () => { if (bot.currentWindow) { bot.closeWindow(bot.currentWindow); await wait(500) } const p = waitWindow(bot); bot.chat('/ember'); return p }
  const openSub = async (name) => { const hub = await openHub(); const p = waitWindow(bot); pk.length = 0; bot.clickWindow(slotOf(hub, new RegExp('^' + name + '$')), 0, 0).catch(() => {}); return p }
  const click = async (w, re) => { pk.length = 0; bot.clickWindow(slotOf(w, re), 0, 0).catch(() => {}); await wait(2500); return pk.join('\n') }
  // hub
  const hub = await openHub()
  check('hub 周常 lore cost', loreOf(hub, /^周常/).some((l) => l.includes(`进本消耗 ${C.weekly} 体力`)), loreOf(hub, /^周常/).join(' / '))
  check('hub 深渊 lore cost', loreOf(hub, /^深渊$/).some((l) => l.includes(`消耗 ${C.abyss} 体力`)), loreOf(hub, /^深渊$/).join(' / '))
  check('hub 团本 lore cost', loreOf(hub, /^团本$/).some((l) => l.includes(`消耗 ${C.raid} 体力（本周首次免费）`)), loreOf(hub, /^团本$/).join(' / '))
  // raid
  let w = await openSub('团本'); await wait(300)
  const openTell = pk.join('\n')
  check('raid open tell', openTell.includes('"color":"blue","text":"【团本】 "') && openTell.includes(`消耗 ${C.raid} 体力（本周首次免费）`), openTell)
  check('raid 次数说明 lore', loreOf(w, /次数说明/).some((l) => l.includes(`其后 ${C.raid} 体力`)), loreOf(w, /次数说明/).join(' / '))
  { const L = loreOf(w, /次数说明/).join(' / '); check('raid 次数说明 refund rule (B2.124)', L.includes('只扣发起者 · 没进成本自动退还') && L.includes('进本后失败 / 超时不返还（含首免）') && !L.includes('进本即扣'), L) }
  let t = await click(w, /次数说明/)
  check('raid 次数说明 tell', t.includes(`其后 ${C.raid} 体力；需 3～5 人`), t)
  w = await openSub('团本')
  t = await click(w, /开始协作/)
  check('raid 开始协作 tell', t.includes('"color":"blue","text":"【团本】 "') && t.includes('尝试进入') && !/消耗 \d+ 体力|团本已点燃/.test(t), t)
  // abyss (full stamina)
  w = await openSub('深渊')
  check('abyss 次数说明 lore', loreOf(w, /次数说明/).some((l) => l.includes(`消耗 ${C.abyss} 体力（与日常同池）`)), loreOf(w, /次数说明/).join(' / '))
  t = await click(w, /次数说明/)
  check('abyss 次数说明 tell', t.includes(`深渊消耗 ${C.abyss} 体力`), t)
  w = await openSub('深渊')
  t = await click(w, /^开始下潜$/)
  check('abyss 开始下潜 tell', t.includes('"color":"dark_purple","text":"【深渊】 "') && t.includes('尝试下潜') && !/体力 30/.test(t), t)
  // abyss (low stamina: grey variant)
  if (bot.currentWindow) { bot.closeWindow(bot.currentWindow); await wait(500) }
  const n1 = bot.chatLog.length; bot.chat('/corerpg stamina'); await wait(1200)
  const before = Number((strip(bot.chatLog.slice(n1).join(' ')).match(/\[体力\] (\d+)\//) || [])[1])
  await con.run(`corerpg stamina set ${U} ${Math.max(0, C.abyss - 20)}`, null)
  try {
    await wait(1500)
    w = await openSub('深渊')
    check('abyss 灰显 lore', loreOf(w, /体力不足/).some((l) => l.includes(`需要 ${C.abyss} 体力`)), loreOf(w, /体力不足/).join(' / '))
    t = await click(w, /体力不足/)
    check('abyss 灰显 tell', t.includes(`需 ${C.abyss} 点体力`), t)
  } finally { await con.run(`corerpg stamina set ${U} ${Number.isFinite(before) ? before : 90}`, null) }
  if (bot.currentWindow) bot.closeWindow(bot.currentWindow)
  bot.quit()
  const fail = res.filter((x) => !x).length
  console.log(`[copy] PASS=${res.length - fail} FAIL=${fail}`)
  setTimeout(() => process.exit(fail ? 2 : 0), 300)
})().catch((e) => { console.error('[copy] FATAL', e.message || e); process.exit(1) })
