const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({ host:'127.0.0.1', port:25565, username:'RpgBot', version:'1.12.2', auth:'offline' })
bot.on('message', m => console.log('[chat]', m.toString()))
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c){ console.log('[cmd]', c); bot.chat(c); await wait(900) }
bot.once('spawn', async () => {
  await wait(2000)
  // clear junk
  for (const it of bot.inventory.items()) {
    try { await bot.tossStack(it) } catch (e) {}
  }
  await wait(1500)
  await chat('/clear RpgBot')
  await wait(800)
  await chat('/ni give RpgBot ticket_ember_daily 3')
  await wait(1200)
  console.log('[inv]', bot.inventory.items().map(i => i.customName + ' x' + i.count))
  await chat('/dp start EmberDaily')
  await wait(5000)
  console.log('[pos]', bot.entity.position)
  // look for green wool nearby as daily marker
  const blocks = bot.findBlocks({ matching: (b) => b && (b.name==='wool' || b.name==='emerald_block' || b.name==='obsidian' || b.name==='quartz_block' || b.name==='netherrack'), maxDistance: 16, count: 20 })
  console.log('[nearby special]', blocks.length, blocks.slice(0,5))
  for (const c of ['/dp leave','/dungeon leave','/dp quit']) { await chat(c); await wait(700) }
  console.log('DONE')
  process.exit(0)
})
bot.on('kicked', r => { console.error(r); process.exit(1) })
setTimeout(() => process.exit(2), 90000)
