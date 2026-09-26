/**
 * CoreRpg auction + arena smoke — DESIGN-ember-arena-auction.md
 * Cover likely command surface for 1.3.8:
 *   /corerpg auction|ah  list|sell|buy|cancel|collect
 *   /corerpg arena|pvp   queue 1v1|2v2 · leave · stats
 * Do NOT run until plugins/CoreRpg.jar is 1.3.8 with AuctionService + ArenaService.
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
function recent(n = 20) {
  return chatLog.slice(-n).join('\n')
}

async function equipWhitelistShard() {
  // NI mat_ember_shard is typically a dye / bone / paper-like stack; try common types
  const items = bot.inventory.items()
  const hit = items.find(i => {
    const n = (i.name || '').toLowerCase()
    const dn = (i.displayName || '').toString()
    return /shard|ember|dye|bone|paper|prismarine|nether_star|glowstone/i.test(n + ' ' + dn)
  }) || items[items.length - 1]
  if (hit) {
    try {
      await bot.equip(hit, 'hand')
      console.log('[equip]', hit.name, 'slot', hit.slot)
    } catch (e) {
      console.log('[equip-fail]', e.message)
    }
  } else {
    console.log('[equip] no inventory item to hold for sell')
  }
}

bot.once('spawn', async () => {
  try {
    await wait(2500)

    // funds + whitelist sell stock (auction.yml)
    await chat('/corerpg coin give RpgBot 5000')
    await chat('/corerpg coin')
    await chat('/ni give RpgBot mat_ember_shard 16')
    await chat('/ni give RpgBot mat_ember_bone_dust 8')
    await wait(500)
    await equipWhitelistShard()

    // ========== auction / ah ==========
    console.log('[plan] --- auction root + list ---')
    await chat('/corerpg auction')
    await chat('/corerpg auction list')
    await chat('/corerpg auction list 1')
    await chat('/corerpg ah')
    await chat('/corerpg ah list')

    console.log('[plan] --- auction sell (held whitelist) ---')
    await equipWhitelistShard()
    await chat('/corerpg auction sell 100')
    await chat('/corerpg auction')
    // alias path
    await equipWhitelistShard()
    await chat('/corerpg ah sell 50')

    console.log('[plan] --- auction buy / cancel / collect ---')
    // id 1 may or may not exist; expect clear tell either way
    await chat('/corerpg auction buy 1')
    await chat('/corerpg ah buy 1')
    await chat('/corerpg auction cancel 1')
    await chat('/corerpg ah cancel 1')
    await chat('/corerpg auction collect')
    await chat('/corerpg ah collect')
    await chat('/corerpg auction help')
    console.log('AUCTION_SNIP', recent(28).slice(0, 900))

    // ========== arena / pvp ==========
    console.log('[plan] --- arena root + stats ---')
    await chat('/corerpg arena')
    await chat('/corerpg arena stats')
    await chat('/corerpg pvp')
    await chat('/corerpg pvp stats')

    console.log('[plan] --- arena queue 1v1 / leave ---')
    await chat('/corerpg arena queue 1v1')
    await chat('/corerpg arena')
    await chat('/corerpg arena leave')

    console.log('[plan] --- arena queue 2v2 / leave ---')
    await chat('/corerpg arena queue 2v2')
    await chat('/corerpg arena leave')

    console.log('[plan] --- pvp alias queue ---')
    await chat('/corerpg pvp queue 1v1')
    await chat('/corerpg pvp leave')
    await chat('/corerpg pvp queue 2v2')
    await chat('/corerpg pvp leave')

    // daily box (source ArenaService also exposes claim)
    await chat('/corerpg arena claim')
    await chat('/corerpg arena help')
    console.log('ARENA_SNIP', recent(28).slice(0, 900))

    console.log('AUCTION_ARENA_SMOKE_DONE')
    process.exit(0)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})

bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 150000)
