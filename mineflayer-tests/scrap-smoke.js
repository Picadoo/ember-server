const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline' })
let lastClick = null
bot.on('message', m => {
  console.log('[chat]', m.toString())
  const walk = o => { if (!o || typeof o !== 'object') return; if (o.clickEvent && o.clickEvent.value) lastClick = o.clickEvent.value; (o.extra || []).forEach(walk) }
  walk(m.json)
})
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c) { console.log('[cmd]', c); bot.chat(c); await wait(1100) }
bot.once('spawn', async () => {
  await wait(2000)
  await chat('/ni reload')
  await wait(1500)
  await chat('/ni give RpgBot gear_ember_blade 2')
  await chat('/ni give RpgBot mat_ember_reforge_stone 5')
  await wait(600)
  const blade = bot.inventory.items().find(i => i.name === 'iron_sword')
  if (blade) { try { await bot.equip(blade, 'hand'); console.log('[equip]', blade.slot) } catch (e) { console.log(e.message) } }
  await chat('/corerpg scrap info')
  await chat('/corerpg reforge')
  await chat('/corerpg scrap') // B2.137: preview + clickable [确认分解], nothing destroyed yet
  if (lastClick) await chat(lastClick) // the click runs /corerpg scrap confirm <token>
  else console.log('[warn] no [确认分解] click event seen')
  console.log('SCRAP_SMOKE_DONE')
  process.exit(0)
})
bot.on('kicked', r => { console.error(r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => process.exit(2), 80000)
