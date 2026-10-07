/** D306 hub daily-routing honesty smoke — decision card + four facts + gates. NO p1sim. */
const { joinPlay } = require('./lib/proxy-login')
const cons = require('./lib/console')
const wait = ms => new Promise(r => setTimeout(r, ms))
function strip(s) { return String(s).replace(/\u00a7./g, '').replace(/§./g, '') }

function loreTexts(win) {
  const out = []
  if (!win || !win.slots) return out
  for (const it of win.slots) {
    if (!it) continue
    const name = (it.customName || it.displayName || '').toString()
    out.push(strip(name))
    const lore = (it.nbt && it.nbt.value && it.nbt.value.display && it.nbt.value.display.value
      && it.nbt.value.display.value.Lore && it.nbt.value.display.value.Lore.value)
    if (lore && lore.value) {
      for (const line of lore.value) out.push(strip(typeof line === 'string' ? line : (line.value || String(line))))
    } else if (it.lore) {
      for (const line of it.lore) out.push(strip(line))
    }
  }
  return out
}

async function openHub(bot) {
  bot.chat('/trmenu open ember_hub')
  await wait(1800)
  if (!bot.currentWindow) {
    bot.chat('/ember')
    await wait(1500)
  }
  return bot.currentWindow
}

async function main() {
  const name = 'D306Route' + String(Date.now()).slice(-4)
  const bot = await joinPlay(name, { log: false })
  await wait(800)
  cons.send('op ' + name); await wait(200)

  const results = {}
  const tells = []
  bot.on('message', (j) => { try { tells.push(strip(j.toString())) } catch (_) {} })

  // --- pre-Q07: no abyss/raid recommend ---
  await wait(400)
  let win = await openHub(bot)
  let blob = loreTexts(win).join('\n')
  results.preHasToday = /今天该打哪/.test(blob)
  results.preHasPrimary = /优先：/.test(blob)
  results.preFourFacts = /体力/.test(blob) && /精选/.test(blob) && /花样/.test(blob) && /挂机/.test(blob)
  results.preNoRaidRec = !/优先：.*团本/.test(blob) && !/优先：.*深渊/.test(blob)
  results.preCostsPlain = /主线\s*30/.test(blob) && /深渊\s*30/.test(blob) && /团本\s*50/.test(blob)
  const tellJoin = tells.join('\n')
  results.openTell = /今天：/.test(tellJoin) || /优先：/.test(tellJoin)
  if (!results.preHasToday) console.log('HUB_PRE_SAMPLE', blob.slice(0, 900))
  try { bot.closeWindow(win) } catch (_) {}
  await wait(500)

  // --- low stamina: no mainline recommend ---
  cons.send('corerpg stamina set ' + name + ' 10'); await wait(400)
  // try alternate commands
  cons.send('corerpg stamina setamount ' + name + ' 10'); await wait(200)
  tells.length = 0
  win = await openHub(bot)
  blob = loreTexts(win).join('\n')
  results.lowStamina = /挂机\/补给|体力不足/.test(blob)
  results.lowNoMainPri = !/优先：.*主线/.test(blob) || /体力不足/.test(blob)
  if (!results.lowStamina) console.log('LOW_STAMINA_BLOB', blob.slice(0, 700))
  try { bot.closeWindow(win) } catch (_) {}
  await wait(400)

  // restore stamina + Q07 clear for featured / afk_full paths
  cons.send('corerpg stamina set ' + name + ' 90'); await wait(200)
  cons.send('corerpg stamina give ' + name + ' 90'); await wait(200)
  for (const q of ['q01','q02','q03','q04','q05','q06','q07']) {
    cons.send('corerpg p1 runs firstclear ' + name + ' ' + q); await wait(60)
  }
  await wait(400)

  // force afk full if possible — try period / admin
  cons.send('corerpg p1 afk setkills ' + name + ' 2400'); await wait(200)
  cons.send('corerpg afk setkills ' + name + ' 2400'); await wait(200)

  tells.length = 0
  win = await openHub(bot)
  blob = loreTexts(win).join('\n')
  // After Q07 with stamina, primary should be featured OR adventure (if full) OR mainline done — never locked abyss as 优先
  results.postHasPri = /优先：/.test(blob)
  results.postNoFakeLock = !/优先：.*深渊/.test(blob) && !/优先：.*团本/.test(blob)
  results.postFour = /体力/.test(blob) && /精选/.test(blob) && /花样/.test(blob) && /挂机/.test(blob)
  // afk full prefer adventure if we managed to set kills
  results.afkFullOrFeatured = /去冒险（挂机今日已满）/.test(blob) || /本周精选/.test(blob) || /优先：/.test(blob)
  try { bot.closeWindow(win) } catch (_) {}
  await wait(400)

  // W1d adventure cross-link
  bot.chat('/trmenu open ember_p1_adventure')
  await wait(1800)
  const adv = loreTexts(bot.currentWindow).join('\n')
  results.advCross = /今天该打哪|今日优先/.test(adv)

  // D305 hub half-line still present (don't reopen nameplates)
  bot.chat('/trmenu open ember_hub')
  await wait(1500)
  const hub2 = loreTexts(bot.currentWindow).join('\n')
  results.d305Hub = /四层各有名片|养什么/.test(hub2)

  console.log('RESULTS', JSON.stringify(results, null, 2))
  const need = ['preHasToday','preHasPrimary','preFourFacts','preNoRaidRec','preCostsPlain','lowStamina','postHasPri','postNoFakeLock','advCross']
  const fail = need.filter(k => !results[k])
  if (fail.length) {
    console.log('FAIL', fail.join(','))
    process.exit(2)
  }
  console.log('PASS D306 smoke')
  try { bot.quit() } catch (_) {}
  process.exit(0)
}

main().catch(e => { console.error(e); process.exit(1) })
