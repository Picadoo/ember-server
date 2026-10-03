/**
 * CoreRpg pet feed / 魂尘 smoke — docs/design/DESIGN-ember-pet-bestiary.md
 * Expect (CoreRpg 1.3.9+): feed.enabled + mat_ember_soul_dust consume → pet level up
 * Flow: NI give soul dust (if exists) → unlock/summon pet_ember_ashling → /corerpg pet feed
 * Do NOT run until plugins/CoreRpg.jar is 1.3.9 with real feed (not「暂未开放魂尘」stub).
 * No jar overwrite from this script.
 */
const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'RpgBot',
  version: '1.12.2',
  auth: 'offline'
})

const chatLog = []
bot.on('message', m => {
  const s = m.toString()
  chatLog.push(s)
  console.log('[chat]', s)
})

const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(1200)
}
function recent(n = 24) {
  return chatLog.slice(-n).join('\n')
}
function saw(re, n = 24) {
  return re.test(recent(n))
}

bot.once('spawn', async () => {
  try {
    await wait(2500)

    // egg for unlock / summon
    await chat('/ni give RpgBot pet_ember_ashling 1')
    await wait(400)

    // --- soul dust: give if NI id exists, else note ---
    const beforeDust = chatLog.length
    await chat('/ni give RpgBot mat_ember_soul_dust 16')
    await wait(600)
    const dustChat = chatLog.slice(beforeDust).join('\n')
    const dustMissing = /找不到ID|找不到物品|未知物品|不存在|Unknown|无效|没有该物品|Item not found/i.test(dustChat)
    const dustOk = /成功给予|你得到了|已给予|gave|Given/i.test(dustChat)
    if (dustMissing || (!dustOk && /错误|失败|无法/i.test(dustChat))) {
      console.log('NOTE_SOUL_DUST_MISSING', 'mat_ember_soul_dust not giveable via NI — skip stock; still probe feed')
      console.log('SOUL_DUST_SNIP', dustChat.slice(0, 400))
    } else if (dustOk) {
      console.log('[plan] mat_ember_soul_dust ×16 given')
    } else {
      // ambiguous tell — note and continue (item may still be in inv)
      console.log('NOTE_SOUL_DUST_AMBIGUOUS', dustChat.slice(0, 400))
    }

    // --- unlock + summon ashling ---
    await chat('/corerpg pet list')
    await chat('/corerpg pet unlock pet_ember_ashling')
    // if already unlocked, unlock tell is OK; ensure egg for first-time
    if (saw(/需要.*蛋|持有.*蛋|没有.*蛋|库存不足|不足/i)) {
      await chat('/ni give RpgBot pet_ember_ashling 1')
      await wait(400)
      await chat('/corerpg pet unlock pet_ember_ashling')
    }
    await chat('/corerpg pet summon pet_ember_ashling')
    await chat('/corerpg pet list')

    // --- feed (1.3.9: consume mat_ember_soul_dust; 1.3.8 stub: 暂未开放魂尘) ---
    const beforeFeed = chatLog.length
    await chat('/corerpg pet feed')
    await wait(800)
    // optional second feed if first leveled
    await chat('/corerpg pet feed')
    await wait(600)
    const feedChat = chatLog.slice(beforeFeed).join('\n')
    console.log('PET_FEED_SNIP', feedChat.slice(0, 800))
    await chat('/corerpg pet list')

    console.log('PET_FEED_SMOKE_DONE')
    process.exit(0)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 90000)
