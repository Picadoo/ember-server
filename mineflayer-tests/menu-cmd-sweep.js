// Runs every player-side command referenced by TrMenu menus as a fresh non-op bot and records the reply.
// Flags: unknown command / no permission / usage-only replies. env: BOT, CMDS_FILE (one command per line)
const mineflayer = require('mineflayer'); const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const BOT = process.env.BOT || 'SwpA926'
const cmds = fs.readFileSync(process.env.CMDS_FILE, 'utf8').split('\n').map(s => s.trim()).filter(Boolean)
const b = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: BOT, version: '1.12.2', auth: 'offline' })
const log = []; b.on('message', m => log.push(m.toString()))
b.once('spawn', async () => {
  await wait(2500); const out = []
  for (const c of cmds) {
    const n = log.length; b.chat('/' + c.replace(/%player_name%/g, BOT)); await wait(1300)
    const reply = log.slice(n).join(' | ').slice(0, 300)
    const bad = /Unknown command|未知命令|I'm sorry|permission|权限|需要 corerpg/i.test(reply) || reply === ''
    out.push({ cmd: c, bad, reply }); console.log((bad ? 'BAD ' : 'ok  ') + c + '  =>  ' + reply)
    if (b.currentWindow) b.closeWindow(b.currentWindow)
  }
  fs.writeFileSync(process.env.OUT || '/tmp/sweep.json', JSON.stringify(out, null, 1)); process.exit(0)
})
b.on('kicked', r => { console.log('KICKED', r); process.exit(1) })
