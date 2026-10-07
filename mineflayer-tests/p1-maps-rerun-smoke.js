// P1 map-restore rerun smoke (2026-10-08, after 704dcebd): Q01–Q07 solo + R01–R03 3-bot raid, entered via the
// TrMenu adventure menu (/ember_p1_adventure → click the map icon), rooms cleared by the bot's own melee hits.
// No op: every privileged prep step (stamina / unlock / first-clear flags / effects / tp / weaken) goes through the
// play-server console FIFO (lib/console.js). Per run it reports: entered, rooms cleared, boss killed, settle seen,
// weaken fallbacks used, and the server-log counts of 'was suffocating' warnings + '[P1 run] <id> anomaly' lines.
// Raid mode also collects the D303 (本局/名片, 本间 已清, 首领厅, 半血 cue) and D304 (蓄力 chat + ActionBar) lines.
// env: MODE=q|raid  MAPS=q01,q02,..|r01,r02,r03  BOT=<solo name>  PARTY=A,B,C  OUT=/tmp/result.json
const fs = require('fs')
const { joinPlay } = require('./lib/proxy-login')
const cons = require('./lib/console')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const MODE = process.env.MODE || 'q'
const MAPS = (process.env.MAPS || (MODE === 'raid' ? 'r01,r02,r03' : 'q01,q02,q03,q04,q05,q06,q07')).split(',')
// window before the 1 HP weaken (kills still by the bot). Gear-less test bots die in seconds in Q02+ and their own
// damage is not what this smoke measures (geometry / reachability is), so the default weakens almost at once.
const SOFT_MS = Number(process.env.SOFT_MS || 0)
const OUT = process.env.OUT || `/tmp/p1-rerun-${MODE}.json`
const HOSTILE = new Set(['zombie', 'skeleton', 'husk', 'stray', 'wither_skeleton', 'zombie_villager', 'vindication_illager',
  'vindicator', 'evocation_illager', 'witch', 'zombie_pigman', 'spider', 'cave_spider', 'blaze'])
const SLOT = { q01: 19, q02: 20, q03: 21, q04: 22, q05: 23, q06: 24, q07: 25, r01: 37, r02: 39, r03: 41 } // ember_p1_adventure layout
const DUNGEON = { q01: 'EmberQ01', q02: 'EmberQ02', q03: 'EmberQ03', q04: 'EmberQ04', q05: 'EmberQ05', q06: 'EmberQ06', q07: 'EmberQ07', r01: 'EmberQ0R1', r02: 'EmberQ0R2', r03: 'EmberQ0R3' }
// room / boss geometry straight from plugins/CoreRpg/ember-v1-runs.yml (maps + raids) via python3+PyYAML
fs.mkdirSync('/tmp/p1rerun', { recursive: true }) // chat logs per bot
const DEFS = JSON.parse(require('child_process').execSync(`python3 -c "
import yaml,json,sys
d=yaml.safe_load(open('/workspace/minecraft/plugins/CoreRpg/ember-v1-runs.yml'))
o={}
for s in ('maps','raids'):
  for k,v in d[s].items():
    if isinstance(v,dict) and v.get('rooms'):
      b=v.get('boss',{}); o[k]={'name':v.get('name'),'rooms':[{'id':i,'label':r.get('label'),'trigger':r['trigger'],'p0':r['points'][0]} for i,r in v['rooms'].items()],'boss_area':b.get('area'),'safe':v.get('safe')}
json.dump(o,sys.stdout)"`).toString())

function trackBars(bot) {
  bot.actionBars = []
  bot._client.on('chat', pkt => {
    if (!pkt || pkt.position !== 2) return
    let t = ''
    try { const m = typeof pkt.message === 'string' ? JSON.parse(pkt.message) : pkt.message; t = (m.text || '') + (m.extra || []).map(e => e.text || '').join('') } catch (_) { t = String(pkt.message || '') }
    bot.actionBars.push(strip(t))
  })
}
async function prep(name) {
  const bot = await joinPlay(name, { log: false })
  trackBars(bot)
  bot.on('message', m => fs.appendFileSync(`/tmp/p1rerun/chat-${name}.log`, new Date().toTimeString().slice(0, 8) + ' ' + m.toString() + '\n'))
  await wait(800)
  bot.chat('/dp leave'); await wait(3000) // a reconnect within 120 s lands back in an unfinished run
  const all = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']
  for (const q of all) {
    // MODE=q: unlock every map (Q02+ require the previous map) but leave the target maps' first clear unset is not
    // needed for this smoke → first-clear flags set like d304-boss-telegraph-smoke.js so no first-clear choice UI pops
    cons.send(`corerpg p1 runs firstclear ${name} ${q}`); await wait(120)
    if (q !== 'q01') { cons.send(`corerpg p1 runs unlock ${name} ${q}`); await wait(120) }
  }
  cons.send(`give ${name} diamond_sword 1`); await wait(600)
  const sw = bot.inventory.items().find(i => i.name === 'diamond_sword'); if (sw) { try { await bot.equip(sw, 'hand') } catch (_) {} }
  return bot
}
function buff(name) {
  cons.send(`effect ${name} resistance 1800 4 true`)
  cons.send(`effect ${name} strength 1800 20 true`)
}
function inBox(p, b, g) { g = g || 0; return p.x >= Math.min(b[0], b[3]) - g && p.x <= Math.max(b[0], b[3]) + 1 + g && p.y >= Math.min(b[1], b[4]) - g && p.y <= Math.max(b[1], b[4]) + 1 + g && p.z >= Math.min(b[2], b[5]) - g && p.z <= Math.max(b[2], b[5]) + 1 + g }
function hostiles(bot, box) {
  return Object.values(bot.entities).filter(e => e && e !== bot.entity && HOSTILE.has(e.name) && (!box || inBox(e.position, box, 4)))
    .sort((a, b) => a.position.distanceTo(bot.entity.position) - b.position.distanceTo(bot.entity.position))
}
async function runLine(runId) {
  const m = cons.mark(); cons.send('corerpg p1 runs list'); await wait(450)
  const l = cons.since(m).find(x => x.includes(runId) && x.includes('world='))
  return l ? strip(l) : ''
}
const field = (l, k) => { const m = l.match(new RegExp(k + '=(\\S+)')); return m ? m[1] : null }

// fight inside box until runs list reports `done(line)`; weaken fallback after softMs; hard stop after hardMs
async function fight(bots, leader, runId, box, done, softMs, hardMs, label, R) {
  const t0 = Date.now(); let lastTp = 0, weakens = 0, lastWeaken = 0, lastPoll = 0, line = '', lastHeal = 0
  while (Date.now() - t0 < hardMs) {
    if (Date.now() - lastHeal > 700) { for (const b of bots) cons.send(`corerpg p1 heal ${b.username}`); lastHeal = Date.now() }
    if (Date.now() - lastPoll > 1500) {
      line = await runLine(runId); lastPoll = Date.now(); if (!line || done(line)) break
      // weaken (1 HP) whatever is alive once softMs passed; re-applied for late spawns / adds; the bot still lands every kill
      const alive = Number(field(line, 'alive') || 0)
      if (alive > 0 && Date.now() - t0 >= softMs && Date.now() - lastWeaken > 2000) { cons.send(`corerpg p1 runs weaken ${leader.username}`); weakens++; lastWeaken = Date.now() }
    }
    let e = hostiles(leader, box)[0]
    const aliveNow = Number(field(line, 'alive') || 0)
    if (!e && aliveNow > 0 && Date.now() - t0 > 5000) {
      // nothing left inside the room box but the director still counts a live mob: look wider and record where it is
      e = hostiles(leader, null).filter(x => x.position.distanceTo(leader.entity.position) < 64)[0]
      if (!e && Date.now() - (R._lastSel || 0) > 4000) {
        // still nothing visible: let the server pick the nearest live room mob and put the leader next to it,
        // then record where that was (evidence for an unreachable perch / wall pocket)
        R._lastSel = Date.now()
        const typ = ['skeleton', 'zombie', 'vindication_illager'][Math.floor(Date.now() / 4000) % 3]
        cons.send(`execute ${leader.username} ~ ~ ~ tp ${leader.username} @e[type=${typ},r=96,c=1]`)
        await wait(900)
        const p = leader.entity.position, hb = leader.blockAt(p.offset(0, 1.5, 0)), fb = leader.blockAt(p)
        R.selectorTp = R.selectorTp || []
        if (R.selectorTp.length < 12) R.selectorTp.push({ room: label, type: typ, at: `${p.x.toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`, feet: fb && fb.name, head: hb && hb.name, t: new Date().toTimeString().slice(0, 8) })
        e = hostiles(leader, null).filter(x => x.position.distanceTo(leader.entity.position) < 6)[0]
      }
      if (e) {
        const hb = leader.blockAt(e.position.offset(0, 1.5, 0)), fb = leader.blockAt(e.position)
        const key = `${e.name}@${e.position.x.toFixed(0)},${e.position.y.toFixed(0)},${e.position.z.toFixed(0)}`
        R.outside = R.outside || {}
        if (!R.outside[key]) R.outside[key] = { room: label, feet: fb && fb.name, head: hb && hb.name, at: new Date().toTimeString().slice(0, 8) }
      }
    }
    if (e) {
      const d = e.position.distanceTo(leader.entity.position)
      if (d > 3 && Date.now() - lastTp > 1200) {
        cons.send(`tp ${leader.username} ${e.position.x.toFixed(1)} ${e.position.y.toFixed(1)} ${e.position.z.toFixed(1)}`)
        for (const b of bots) if (b !== leader) cons.send(`tp ${b.username} ${leader.username}`)
        lastTp = Date.now()
      }
      for (const b of bots) {
        const t = hostiles(b, box)[0]
        if (t && t.position.distanceTo(b.entity.position) <= 4) { try { await b.lookAt(t.position.offset(0, (t.height || 1.8) * 0.8, 0), true) } catch (_) {} b.attack(t) }
      }
    }
    await wait(350)
  }
  R.steps.push({ step: label, ms: Date.now() - t0, weakenCalls: weakens, end: line.replace(/^.*?world=\S+\s*/, '').slice(0, 160) })
  return line
}

async function formTeam(bots) {
  const L = bots[0]
  for (const b of bots) { b.chat('/dungeon-team leave'); await wait(300) }
  L.chat('/dungeon-team disband'); await wait(600); L.chat('/dungeon-team create'); await wait(1000)
  for (const b of bots.slice(1)) { L.chat(`/dungeon-team invite ${b.username}`); await wait(900); b.chat(`/dungeon-team request join ${L.username}`); await wait(1200) }
}

async function openMenuClick(bot, key, R) {
  const opened = new Promise(res => { const to = setTimeout(() => res(null), 8000); bot.once('windowOpen', w => { clearTimeout(to); res(w) }) })
  bot.chat('/ember_p1_adventure')
  const w = await opened
  if (!w) { R.menu = 'no window'; return false }
  await wait(900)
  const it = w.slots[SLOT[key]]
  R.menu = { title: strip(w.title), slot: SLOT[key], icon: it ? strip((it.customName || it.displayName || it.name)) : null }
  try { await bot.clickWindow(SLOT[key], 0, 0) } catch (e) { R.menu.clickErr = e.message }
  await wait(600)
  if (bot.currentWindow) { try { bot.closeWindow(bot.currentWindow) } catch (_) {} }
  return true
}

async function runMap(bots, key) {
  const leader = bots[0], def = DEFS[key]
  const R = { map: key, name: null, steps: [], cues: {} }
  const logMark = cons.mark(); const chatN = bots.map(b => b.chatLog.length); for (const b of bots) b.actionBars = []
  if (bots.length > 1) await formTeam(bots)
  for (const b of bots) cons.send(`corerpg stamina set ${b.username} 120`) // each run reserves 30; refill before every map
  await wait(500)
  const tStart = new Date()
  R.startAt = tStart.toTimeString().slice(0, 8)
  // enter via menu (retry once for the DP 5 s start cooldown)
  let runId = null, world = null
  for (let attempt = 0; attempt < 3 && !runId; attempt++) {
    await openMenuClick(leader, key, R)
    const hit = await cons.waitLog(new RegExp(`\\[P1 run\\] (\\S+) bound to (dungeon_${DUNGEON[key]}_\\w+)`), logMark, 20000)
    if (hit) { const m = hit.match(/\[P1 run\] (\S+) bound to (\S+)/); runId = m[1]; world = m[2] }
    else { R['enterFail' + attempt] = strip(leader.chatLog.slice(chatN[0]).join(' | ')).slice(-300); await wait(7000) }
  }
  R.entered = !!runId; R.runId = runId; R.world = world
  if (!runId) return finish(R, bots, logMark, chatN)
  await wait(3500)
  for (const b of bots) { buff(b.username); cons.send(`gamemode survival ${b.username}`) }
  await wait(800)
  // rooms
  const rooms = def.rooms
  R.roomsCleared = 0
  for (let i = 0; i < rooms.length; i++) {
    const r = rooms[i]
    const p = r.p0
    cons.send(`tp ${leader.username} ${p[0] + 0.5} ${p[1]} ${p[2] + 0.5}`)
    for (const b of bots.slice(1)) cons.send(`tp ${b.username} ${leader.username}`)
    await wait(700)
    const nextId = i + 1 < rooms.length ? rooms[i + 1].id : 'boss'
    const line = await fight(bots, leader, runId, r.trigger, l => field(l, 'next') === nextId && field(l, 'active') === '-', SOFT_MS, 120000, r.id, R)
    if (line && field(line, 'next') === nextId) R.roomsCleared++
    else { R.stuckAt = `${r.id} (${r.label})`; for (const b of bots) b.chat('/dp leave'); await wait(4000); break }
    await wait(1800) // door_delay
  }
  if (R.roomsCleared === rooms.length) {
    // boss: walk into the hall via the rb safe point, wait for the spawn
    const rb = def.safe.rb
    cons.send(`tp ${leader.username} ${rb[0] + 0.5} ${rb[1]} ${rb[2] + 0.5}`)
    for (const b of bots.slice(1)) cons.send(`tp ${b.username} ${leader.username}`)
    let line = ''
    for (let w = 0; w < 40; w++) { // wait_in_area: the boss shows once the (living) party stands in the hall
      await wait(1000); line = await runLine(runId); if (/boss=\d/.test(line) || !line) break
      if (w % 4 === 3) { cons.send(`tp ${leader.username} ${rb[0] + 0.5} ${rb[1]} ${rb[2] + 0.5}`); for (const b of bots.slice(1)) cons.send(`tp ${b.username} ${leader.username}`) }
      for (const b of bots) cons.send(`corerpg p1 heal ${b.username}`)
    }
    R.bossAtS = Math.round((Date.now() - tStart.getTime()) / 1000) // s since menu click
    R.bossSpawned = /boss=\d/.test(line)
    if (R.bossSpawned) {
      const area = def.boss_area || [rb[0] - 16, rb[1] - 1, rb[2] - 4, rb[0] + 16, rb[1] + 10, rb[2] + 28]
      // phase window: let the boss cast at least once (D304), then push to 40 % for the half-HP cue (D303/D304)
      if (MODE === 'raid' || process.env.CAST_WAIT) {
        // D303/D304 window: push the boss to 45 % at once (half-HP / adds cue), then keep the party topped up
        // (gear-less bots, raid dmg ×1.4) until a 蓄力 cast shows up in chat or ActionBar (≤ 25 s)
        cons.send(`corerpg p1 runs weaken ${leader.username} 0.45`)
        const tc = Date.now()
        while (Date.now() - tc < 25000) {
          const ch = bots.map((b, i) => b.chatLog.slice(chatN[i]).map(strip).join('\n')).join('\n')
          const bar = bots.some(b => (b.actionBars || []).some(a => /蓄力/.test(a)))
          if (/蓄力/.test(ch) && bar && Date.now() - tc > 4000) break
          for (const b of bots) cons.send(`corerpg p1 heal ${b.username}`)
          await wait(400)
        }
      }
      line = await fight(bots, leader, runId, area, l => /boss=dead/.test(l) || !/boss=\d/.test(l), SOFT_MS, 150000, 'boss', R)
    }
  }
  await wait(5000)
  const res = finish(R, bots, logMark, chatN)
  if (!res.cleared) { for (const b of bots) b.chat('/dp leave'); await wait(5000) }
  return res
}

function finish(R, bots, logMark, chatN) {
  const logs = cons.since(logMark)
  const rid = R.runId || '###'
  R.bossKilled = logs.some(l => l.includes(`${rid} boss killed`))
  R.settle = logs.filter(l => l.includes(`${rid} settle`)).length
  R.suffocating = logs.filter(l => /was suffocating/.test(l) && (!R.world || l.includes(R.world)))
  R.anomalies = logs.filter(l => l.includes(`${rid} anomaly`))
  R.fellCatch = logs.filter(l => l.includes(`${rid} fall-catch`)).length
  R.playerWallDeaths = logs.filter(l => /suffocated in a wall/.test(l)).length
  R.cleared = R.bossKilled && R.settle > 0
  const L = bots[0], chat = L.chatLog.slice(chatN[0]).map(strip)
  const allChat = bots.map((b, i) => b.chatLog.slice(chatN[i]).map(strip).join('\n')).join('\n')
  R.cues.enterReveal = chat.find(l => /本局：/.test(l)) || null
  R.cues.raidStart = chat.find(l => /团本开始|主线本|走进前方房间/.test(l)) || null
  R.cues.roomClear = chat.filter(l => /本间：.*已清/.test(l))
  R.cues.bossHall = chat.find(l => /首领厅/.test(l)) || null
  R.cues.halfHp = chat.filter(l => /半血/.test(l)).slice(0, 3)
  R.cues.castChat = chat.filter(l => /蓄力/.test(l)).slice(0, 3)
  R.cues.castBar = [...new Set(bots.flatMap(b => (b.actionBars || []).filter(a => /蓄力/.test(a))))].slice(0, 3)
  R.cues.share = chat.filter(l => /烬核|分摊|靠拢/.test(l)).slice(0, 3)
  R.cues.clearLine = chat.filter(l => /通关|结算到账/.test(l)).slice(0, 3)
  R.deaths = (allChat.match(/你倒下了|倒下了|died|was slain/g) || []).length
  R.mem = require('child_process').execSync("free -m | awk '/Mem:/{print $7}'").toString().trim()
  delete R._lastSel
  console.log('RUN_RESULT', JSON.stringify(R))
  return R
}

;(async () => {
  const names = MODE === 'raid' ? (process.env.PARTY || 'P1RrA1008,P1RrB1008,P1RrC1008').split(',') : [process.env.BOT || 'P1MapQ1008']
  const bots = []
  for (const n of names) { bots.push(await prep(n)); await wait(800) }
  if (bots.length === 1) { bots[0].chat('/dungeon-team disband'); await wait(600); bots[0].chat('/dungeon-team create'); await wait(1000) }
  const out = []
  for (const k of MAPS) {
    const free = Number(require('child_process').execSync("free -m | awk '/Mem:/{print $7}'").toString().trim())
    if (free < 1000) { out.push({ map: k, skipped: 'available memory < 1 GB: ' + free }); continue }
    try { out.push(await runMap(bots, k)) } catch (e) { out.push({ map: k, error: e.message }) }
    fs.writeFileSync(OUT, JSON.stringify(out, null, 1))
    // back to the hub before the next map (instance closes itself after settle; leave if still inside)
    await wait(8000)
    await wait(4000)
  }
  fs.writeFileSync(OUT, JSON.stringify(out, null, 1))
  for (const b of bots) cons.send(`effect ${b.username} clear`)
  await wait(1000)
  for (const b of bots) { try { b.quit() } catch (_) {} }
  await wait(1000); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('global timeout'); process.exit(2) }, Number(process.env.GLOBAL_MS || 50 * 60000))
