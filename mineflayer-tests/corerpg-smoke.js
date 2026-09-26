const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline' })
bot.on('message', m => console.log('[chat]', m.toString()))
function wait(ms){ return new Promise(r => setTimeout(r, ms)) }
async function run() {
  await wait(2500)
  const cmds = [
    '/corerpg status',
    '/corerpg sign',
    '/corerpg activity',
    '/corerpg bounty',
    '/corerpg bounty progress',
    '/corerpg coin',
    '/papi parse me %corerpg_coin%',
    '/papi parse me %corerpg_activity%',
    '/papi parse me %corerpg_signed%',
    '/ember'
  ]
  for (const c of cmds) {
    console.log('[cmd]', c)
    bot.chat(c)
    await wait(1500)
  }
  await wait(1000)
  console.log('SMOKE_DONE')
  process.exit(0)
}
bot.once('spawn', () => run().catch(e => { console.error(e); process.exit(1) }))
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 90000)
