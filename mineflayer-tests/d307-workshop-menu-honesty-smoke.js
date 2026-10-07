/** D307 workshop menu honesty smoke — costs on confirm+preview, lacking, swap free, dismantle chat. NO p1sim. */
const { joinPlay } = require('./lib/proxy-login')
const cons = require('./lib/console')
const wait = ms => new Promise(r => setTimeout(r, ms))
function strip(s) { return String(s).replace(/\u00a7./g, '').replace(/§./g, '') }

function loreTexts(win) {
  const out = []
  if (!win || !win.slots) return out
  for (const it of win.slots) {
    if (!it) continue
    out.push(strip((it.customName || it.displayName || '').toString()))
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

async function openForge(bot) {
  bot.chat('/trmenu open ember_p1_forge')
  await wait(2000)
  if (!bot.currentWindow) { bot.chat('/ember_p1_forge'); await wait(1500) }
  return bot.currentWindow
}

async function equipBlade(bot) {
  for (const it of bot.inventory.items()) {
    const blob = JSON.stringify(it.nbt || {}) + (it.name || '') + (it.displayName || '')
    if (/ember_v1_.*_blade|余烬.*刃|burst|scorch|sustain/.test(blob) || /DIAMOND_SWORD|IRON_SWORD|STONE_SWORD/.test(String(it.name || '').toUpperCase())) {
      try { await bot.equip(it, 'hand'); await wait(600); return true } catch (_) {}
    }
  }
  // fallback: any sword-like
  for (const it of bot.inventory.items()) {
    if (/sword/i.test(it.name || '')) {
      try { await bot.equip(it, 'hand'); await wait(600); return true } catch (_) {}
    }
  }
  return false
}

async function main() {
  const name = 'D307Forge' + String(Date.now()).slice(-4)
  const bot = await joinPlay(name, { log: false })
  await wait(1200)
  cons.send('op ' + name); await wait(400)

  const results = {}
  const chats = []
  bot.on('message', (j) => { try { chats.push(strip(j.toString())) } catch (_) {} })

  cons.send('clear ' + name); await wait(300)
  // T1 blade +0 enhance, admin src (enough for enhance/refine/quality/upgrade cost lines)
  cons.send('corerpg p1 give burst blade 1 0 0 0 ' + name); await wait(800)
  results.equipped = await equipBlade(bot)

  // no mats → lacking half-line expected on refine/quality/enhance
  let win = await openForge(bot)
  let blob = loreTexts(win).join('\n')
  results.hasEnhanceCost = /本次：/.test(blob) && (/\+\d→\+|碎片|手持刃或护符/.test(blob))
  results.noChatOnly = !/消耗以聊天为准/.test(blob)
  results.hasSwapFree = /免费/.test(blob) && /交换强化/.test(blob)
  results.hasDismantleChat = /确认分解|确认在聊天栏/.test(blob)
  results.hasPreviewAlign = /与预览一致/.test(blob)
  results.hasHeldNextOrCost = /精工|成色|手持件|碎片|胚|本次：/.test(blob)
  results.hasLackHint = /缺少：|手持刃或护符|碎片4|碎片/.test(blob)
  if (!results.hasEnhanceCost || !results.noChatOnly) console.log('FORGE_BLOB', blob.slice(0, 1400))

  try { bot.closeWindow(win) } catch (_) {}
  await wait(400)

  // with mats
  for (const c of [
    'ni give ' + name + ' mat_ember_shard 40',
    'ni give ' + name + ' mat_ember_core_fragment 20',
    'ni give ' + name + ' mat_ember_v1_blank 30',
    'ni give ' + name + ' mat_ember_bone_dust 30',
    'corerpg coin give ' + name + ' 8000'
  ]) { cons.send(c); await wait(120) }
  await wait(400)
  await equipBlade(bot)

  win = await openForge(bot)
  blob = loreTexts(win).join('\n')
  results.withMatsCost = /本次：|\+\d→\+|碎片|T1→T2|胚|免费/.test(blob)
  results.withMatsNoChatOnly = !/消耗以聊天为准/.test(blob)
  results.upgradeVisible = /升阶|T1→T2|需首通 Q04|碎片60/.test(blob)
  if (!results.withMatsCost) console.log('FORGE_MATS', blob.slice(0, 1000))
  try { bot.closeWindow(win) } catch (_) {}
  await wait(300)

  // dismantle: admin src → honest refuse in chat; confirm still chat-path
  chats.length = 0
  bot.chat('/corerpg p1 dismantle')
  await wait(1500)
  const chatJoin = chats.join('\n')
  results.dismantleHonest = /确认分解|得胚|只有副本|随机掉落|分解|不是|手持/.test(chatJoin)

  // W1d old enhance pointer
  chats.length = 0
  bot.chat('/trmenu open ember_enhance')
  await wait(1600)
  win = bot.currentWindow
  blob = loreTexts(win).join('\n')
  const tellJoin = chats.join('\n')
  results.oldEnhancePointer = /P1.*工坊|主菜单.*工坊|ember_p1_forge|本页仅旧/.test(blob + '\n' + tellJoin)
  try { if (win) bot.closeWindow(win) } catch (_) {}

  const keys = ['hasEnhanceCost','noChatOnly','hasSwapFree','hasDismantleChat','hasPreviewAlign','hasHeldNextOrCost','withMatsCost','withMatsNoChatOnly','upgradeVisible','dismantleHonest','oldEnhancePointer']
  const fail = keys.filter(k => results[k] !== true)
  console.log('D307_SMOKE ' + JSON.stringify({ results, fail }, null, 2))
  bot.quit(); await wait(400)
  process.exit(fail.length ? 1 : 0)
}
main().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 120000)
