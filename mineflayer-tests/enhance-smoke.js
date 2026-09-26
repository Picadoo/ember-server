const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline' })
bot.on('message', m => console.log('[chat]', m.toString()))
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c){ console.log('[cmd]', c); bot.chat(c); await wait(900) }
async function run() {
  await wait(2500)
  await chat('/ni give RpgBot gear_ember_blade 1')
  await chat('/ni give RpgBot mat_ember_shard 64')
  await chat('/ni give RpgBot mat_ember_bone_dust 32')
  await chat('/ni give RpgBot mat_ember_core_fragment 16')
  await chat('/ni give RpgBot mat_ember_protect_scroll 5')
  await chat('/ni give RpgBot gem_ember_sharp 3')
  await wait(500)
  // hold blade: try to find in inventory and equip
  const blade = bot.inventory.items().find(i => (i.customName || '').includes('余烬') || (i.displayName||'').includes('blade') || i.name.includes('sword'))
  console.log('[inv swords]', bot.inventory.items().filter(i=>i.name.includes('sword')|| (i.customName||'').includes('刃')).map(i=>({slot:i.slot,name:i.name,dn:i.customName})))
  if (blade) {
    try { await bot.equip(blade, 'hand'); console.log('[equip] blade ok slot', blade.slot) } catch(e){ console.log('[equip]', e.message) }
  }
  await chat('/corerpg enhance info')
  await chat('/corerpg enhance')
  await chat('/corerpg enhance')
  await chat('/corerpg enhance')
  await chat('/corerpg enhance')
  await chat('/corerpg enhance') // aim +5
  await chat('/corerpg socket list')
  await chat('/corerpg socket insert gem_ember_sharp')
  await chat('/corerpg socket list')
  await chat('/corerpg enhance info')
  console.log('ENHANCE_SMOKE_DONE')
  process.exit(0)
}
bot.once('spawn', () => run().catch(e => { console.error(e); process.exit(1) }))
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 120000)
