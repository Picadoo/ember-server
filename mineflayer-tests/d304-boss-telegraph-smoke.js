const { joinPlay } = require('./lib/proxy-login')
const cons = require('./lib/console')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
function strip(s) { return String(s).replace(/\u00a7./g, '').replace(/§./g, '') }

async function prep(name) {
  const bot = await joinPlay(name, { log: false })
  bot.actionBars = []
  try {
    bot._client.on('chat', (pkt) => {
      if (pkt && pkt.position === 2) {
        let t = ''
        try {
          const m = typeof pkt.message === 'string' ? JSON.parse(pkt.message) : pkt.message
          t = (m && (m.text || (m.extra || []).map(e => e.text || '').join(''))) || String(pkt.message || '')
        } catch (_) { t = String(pkt.message || '') }
        bot.actionBars.push(strip(t))
      }
    })
  } catch (_) {}
  await wait(500)
  cons.send('op ' + name); await wait(250)
  for (const q of ['q01','q02','q03','q04','q05','q06','q07']) {
    cons.send('corerpg p1 runs firstclear ' + name + ' ' + q); await wait(100)
    if (q !== 'q01') { cons.send('corerpg p1 runs unlock ' + name + ' ' + q); await wait(80) }
  }
  cons.send('corerpg stamina set ' + name + ' 200'); await wait(200)
  for (let i = 0; i < 18; i++) { cons.send('corerpg progress ' + name + ' raid_clear'); await wait(50) }
  await wait(400)
  return bot
}

async function clearRoom(name) {
  for (let k = 0; k < 10; k++) {
    const m = cons.mark()
    cons.send('corerpg p1 runs weaken ' + name)
    await wait(300)
    const lines = cons.since(m).join('\n')
    if (/没有进行中/.test(lines)) return false
    const nMatch = lines.match(/削弱 (\d+) 只/)
    const n = nMatch ? Number(nMatch[1]) : -1
    if (n === 0) return true
    cons.send('execute ' + name + ' ~ ~ ~ kill @e[type=!Player,r=28]')
    await wait(400)
    cons.send('corerpg p1 heal ' + name)
    await wait(100)
  }
  const m2 = cons.mark(); cons.send('corerpg p1 runs weaken ' + name); await wait(350)
  return /削弱 0 只/.test(cons.since(m2).join('\n'))
}

async function clearToBoss(bot, mapKey, spots) {
  const name = bot.username
  const n0 = bot.chatLog.length
  bot.actionBars = []
  bot.chat('/corerpg p1 enter ' + mapKey)
  await wait(6000)
  cons.send('gamemode creative ' + name); await wait(250)
  for (let i = 0; i < spots.length; i++) {
    cons.send('tp ' + name + ' ' + spots[i]); await wait(900)
    if (i < spots.length - 1) await clearRoom(name)
  }
  cons.send('gamemode survival ' + name); await wait(700)
  for (let w = 0; w < 18; w++) {
    const m = cons.mark(); cons.send('corerpg p1 runs list'); await wait(400)
    if (/boss=[1-9]|boss=\d+\.\d+\/[1-9]/.test(cons.since(m).join('\n'))) break
  }
  let cast = false, bar = false
  for (let t = 0; t < 40; t++) {
    const chunk = bot.chatLog.slice(n0).map(strip).join('\n')
    if (/蓄力/.test(chunk)) cast = true
    if (bot.actionBars.some(a => /蓄力/.test(a))) bar = true
    if (cast) break
    await wait(500)
  }
  const n1 = bot.chatLog.length
  cons.send('corerpg p1 runs weaken ' + name + ' 0.4')
  await wait(2500)
  const halfChunk = bot.chatLog.slice(n1).map(strip).join('\n')
  bot.chat('/dp leave'); await wait(4000)
  return {
    castChat: cast, castBar: bar, halfOk: /半血/.test(halfChunk),
    halfSample: halfChunk.replace(/\n/g, ' | ').slice(0, 350),
    castSample: bot.chatLog.slice(n0).map(strip).join(' | ').slice(0, 450),
    bars: bot.actionBars.filter(a => /蓄力|踩|破招|撞墙/.test(a)).slice(0, 8)
  }
}

;(async () => {
  const result = { ok: true, maps: {}, menu: {} }
  const adv = fs.readFileSync('/workspace/minecraft/plugins/TrMenu/menus/ember_p1_adventure.yml', 'utf8')
  result.menu.phaseCount = (adv.match(/§e相位：/g) || []).length
  result.menu.ok = result.menu.phaseCount >= 7
  if (!result.menu.ok) result.ok = false

  const A = await prep('D304A')
  result.maps.q01 = await clearToBoss(A, 'q01', ['0 64 28', '30 64 38', '32 64 68', '0 64 102'])
  if (!result.maps.q01.castChat) result.ok = false
  await wait(1500)
  result.maps.q07 = await clearToBoss(A, 'q07', ['0 64 33', '40 64 65', '-2 64 98', '0 64 144'])
  if (!result.maps.q07.castChat) result.ok = false

  const B = await prep('D304B')
  const C = await prep('D304C')
  A.chat('/dungeon-team disband'); await wait(500)
  A.chat('/dungeon-team create'); await wait(800)
  A.chat('/dungeon-team invite D304B'); await wait(600)
  B.chat('/dungeon-team request join D304A'); await wait(800)
  A.chat('/dungeon-team invite D304C'); await wait(600)
  C.chat('/dungeon-team request join D304A'); await wait(1000)

  const nR = A.chatLog.length
  A.actionBars = []
  A.chat('/corerpg p1 enter r02'); await wait(6500)
  const enterChunk = A.chatLog.slice(nR).map(strip).join('\n')
  const enterOk = /本局：/.test(enterChunk) && /名片：/.test(enterChunk)
  cons.send('gamemode creative D304A'); await wait(200)
  for (const spot of ['0 64 30', '0 64 55', '0 64 85', '0 64 115']) {
    cons.send('tp D304A ' + spot)
    cons.send('tp D304B D304A')
    cons.send('tp D304C D304A')
    await wait(800)
    await clearRoom('D304A')
  }
  cons.send('gamemode survival D304A'); await wait(700)
  let castR = false, phaseR = false
  for (let t = 0; t < 36; t++) {
    const chunk = A.chatLog.slice(nR).map(strip).join('\n')
    if (/蓄力/.test(chunk)) castR = true
    if (/半血转阶段|砸地接横扫|半血/.test(chunk)) phaseR = true
    if (A.actionBars.some(a => /蓄力/.test(a))) castR = true
    if (castR) break
    await wait(500)
  }
  const nH = A.chatLog.length
  cons.send('corerpg p1 runs weaken D304A 0.4'); await wait(2200)
  const halfR = A.chatLog.slice(nH).map(strip).join('\n')
  if (/半血转阶段|砸地接横扫|半血/.test(halfR)) phaseR = true
  A.chat('/dp leave'); await wait(3500)
  result.maps.r02 = {
    enterOk, castChat: castR, phaseOk: phaseR,
    sample: A.chatLog.slice(nR).map(strip).join(' | ').slice(0, 500),
    halfSample: halfR.replace(/\n/g, ' | ').slice(0, 280),
    bars: A.actionBars.filter(a => /蓄力|踩|破招/.test(a)).slice(0, 8)
  }
  if (!enterOk || !castR) result.ok = false

  console.log('D304_SMOKE_RESULT', JSON.stringify(result, null, 2))
  try { A.quit(); B.quit(); C.quit() } catch (_) {}
  process.exit(result.ok ? 0 : 1)
})().catch(e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 700000)
