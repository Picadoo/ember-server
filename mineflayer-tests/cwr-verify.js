const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', version: '1.12.2'
})
bot.on('message', (msg) => console.log('[chat]', msg.toString()))
bot.once('spawn', async () => {
  bot.chat('/cwr check')
  await sleep(800)
  bot.chat('/cwr testspawn')
  await sleep(1200)
  bot.chat('/cwr check')
  await sleep(800)
  // wait a bit — natural should not rise
  let before = countMobs()
  console.log('[verify] mobs before wait', before)
  await sleep(6000)
  let after = countMobs()
  console.log('[verify] mobs after wait', after)
  bot.chat('/coresmelt check')
  await sleep(600)
  bot.chat('/coreenchant check')
  await sleep(600)
  bot.quit()
})
function countMobs() {
  let n = 0
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (e && e !== bot.entity && e.type === 'mob') n++
  }
  return n
}
bot.on('error', (e) => { console.error(e); process.exit(1) })
bot.on('end', () => process.exit(0))
setTimeout(() => { console.error('timeout'); process.exit(2) }, 30000)
function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }
