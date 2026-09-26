// Tiny helper: connect as an op bot, run commands (argv), print chat, quit.
// usage: node op-cmd.js "/corerpg reload" "/dp reload"
const mineflayer = require('mineflayer')
const USER = process.env.OP_BOT || 'RpgBot'
const cmds = process.argv.slice(2)
const b = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: USER, version: '1.12.2', auth: 'offline' })
const wait = ms => new Promise(r => setTimeout(r, ms))
b.on('message', m => console.log('[chat]', m.toString()))
b.on('kicked', r => { console.log('KICKED', r); process.exit(1) })
b.on('error', e => { console.log('ERR', e.message); process.exit(1) })
b.once('spawn', async () => {
  await wait(1500)
  for (const c of cmds) { console.log('[cmd]', c); b.chat(c); await wait(Number(process.env.CMD_WAIT || 1500)) }
  await wait(1000); b.quit(); process.exit(0)
})
setTimeout(() => process.exit(2), 120000)
