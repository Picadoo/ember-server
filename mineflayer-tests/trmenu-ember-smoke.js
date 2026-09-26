const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'MenuBot', auth: 'offline', version: '1.12.2'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
bot.on('windowOpen', win => {
  console.log('[window]', win.title, 'slots', win.slots.filter(Boolean).length)
  const titles = win.slots.filter(s => s && s.displayName).map(s => s.displayName)
  console.log('[items]', titles.slice(0, 20).join(' | '))
})
const sleep = ms => new Promise(r => setTimeout(r, ms))
bot.once('spawn', async () => {
  try {
    await sleep(1500)
    bot.chat('/ember')
    await sleep(2500)
    bot.chat('/menu')
    await sleep(2500)
    bot.chat('/trmenu list')
    await sleep(1500)
    bot.chat('/trmenu open ember_hub')
    await sleep(2500)
    console.log('CHAT_FILTER', chat.filter(c => /ember|menu|TrMenu|余烬|error|未知|Unknown/i.test(c)).join(' || '))
    console.log('DONE')
    bot.quit()
    setTimeout(() => process.exit(0), 400)
  } catch (e) { console.error(e); process.exit(1) }
})
bot.on('error', e => { console.error(e); process.exit(1) })
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 45000)
