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
    for (const c of ['/dp reload','/ni reload','/trmenu reload']) {
      bot.chat(c); await sleep(2500)
    }
    bot.chat('/ni give RpgBot ticket_ember_daily 3')
    await sleep(1200)
    bot.chat('/dp import ember_arena')
    await sleep(4000)
    bot.chat('/dp start EmberDaily')
    await sleep(10000)
    bot.chat('/dp leave')
    await sleep(2000)
    console.log('FILTER', chat.filter(c=>/Dungeon|地牢|Ember|日票|错误|Error|失败|成功|reload|无法|条件|ticket|余烬|Incorrect|Unknown|dp |加载|地图|开始|权限/i.test(c)).join(' || '))
    console.log('DONE')
    bot.quit(); setTimeout(()=>process.exit(0),400)
  } catch(e){ console.error(e); process.exit(1) }
})
bot.on('error', e=>{console.error(e);process.exit(1)})
bot.on('kicked', r=>{console.error('kicked',r);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},90000)
