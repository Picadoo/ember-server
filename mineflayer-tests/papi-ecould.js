const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'RpgBot', auth: 'offline', version: '1.12.2'
})
const chat=[]
bot.on('message', m => { const s=m.toString(); chat.push(s); console.log('[chat]',s) })
const sleep=ms=>new Promise(r=>setTimeout(r,ms))
bot.once('spawn', async () => {
  try {
    await sleep(1000)
    for (const c of [
      '/papi ecloud download Player',
      '/papi ecloud download Server',
      '/papi ecloud download player',
      '/papi ecloud download server',
      '/papi reload',
      '/papi list'
    ]) { bot.chat(c); await sleep(3500) }
    console.log('FILTER', chat.filter(c=>/papi|Placeholder|download|Player|Server|成功|失败|error|Expansion|registered/i.test(c)).join(' || '))
    bot.quit(); setTimeout(()=>process.exit(0),400)
  } catch(e){ console.error(e); process.exit(1) }
})
bot.on('error', e=>{console.error(e);process.exit(1)})
bot.on('kicked', r=>{console.error('kicked',r);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},90000)
