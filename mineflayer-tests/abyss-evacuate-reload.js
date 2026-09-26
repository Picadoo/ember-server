const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const wait = ms => new Promise(r => setTimeout(r, ms))
async function cmd(c) { console.log('[cmd]', c); bot.chat(c); await wait(1500) }
bot.once('spawn', async () => {
  try {
    await wait(2500)
    await cmd('/dp leave')
    await wait(1000)
    await cmd('/dp reload')
    await wait(2500)
    await cmd('/trmenu reload')
    await wait(2000)
    // probe commands (may be unknown until jar 1.4.6)
    const n0 = chat.length
    await cmd('/corerpg abyss')
    await wait(800)
    await cmd('/corerpg abyss progress RpgBot 0')
    await wait(800)
    await cmd('/corerpg abyss settle RpgBot')
    await wait(800)
    await cmd('/corerpg abyss evacuate')
    await wait(1000)
    const probe = chat.slice(n0).join('\n')
    console.log('--- ABYSS_CMD_PROBE ---')
    console.log(probe.slice(0, 2000))
    const unknown = /unknown|未知|不正确|Invalid|没有此|用法|Usage|progress|settle|evacuate|深渊/i.test(probe)
    console.log('ABYSS_RELOAD_DONE', JSON.stringify({
      dp_reload: chat.some(s => /reload|重载|DungeonPlus|成功/i.test(s)),
      trmenu_reload: chat.some(s => /TrMenu|菜单|reload|重载/i.test(s)),
      cmd_probe_snip: probe.replace(/\n/g, ' | ').slice(0, 500)
    }))
    bot.quit('done')
    setTimeout(() => process.exit(0), 400)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 60000)
