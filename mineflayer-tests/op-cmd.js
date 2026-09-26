// Tiny helper: connect as an op bot (via proxy → AuthMe login → play), run commands (argv), print chat, quit.
// usage: node op-cmd.js "/corerpg reload" "/dp reload"   (the bot must be in ops.json for op commands)
const { joinPlay } = require('./lib/proxy-login')
const USER = process.env.OP_BOT || 'RpgBot'
const cmds = process.argv.slice(2)
const wait = ms => new Promise(r => setTimeout(r, ms))
;(async () => {
  const b = await joinPlay(USER, { log: false })
  b.on('message', m => console.log('[chat]', m.toString()))
  b.on('kicked', r => { console.log('KICKED', r); process.exit(1) })
  for (const c of cmds) { console.log('[cmd]', c); b.chat(c); await wait(Number(process.env.CMD_WAIT || 1500)) }
  await wait(1000); b.quit(); process.exit(0)
})().catch(e => { console.log('ERR', e.message); process.exit(1) })
setTimeout(() => process.exit(2), 120000)
