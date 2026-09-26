const mineflayer=require('mineflayer')
const bot=mineflayer.createBot({host:'127.0.0.1',port:25565,username:'RpgBot',version:'1.12.2',auth:'offline'})
bot.on('message',m=>console.log('[chat]',m.toString()))
const wait=ms=>new Promise(r=>setTimeout(r,ms))
async function chat(c){console.log('[cmd]',c);bot.chat(c);await wait(1200)}
bot.once('spawn',async()=>{
 await wait(2500)
 await chat('/corerpg covenant set blaze')
 await chat('/corerpg skill info')
 // spawn a vanilla zombie near bot via console-style plugin spawn or summon
 await chat('/summon zombie ~ ~ ~')
 await wait(800)
 await chat('/corerpg skill')
 await wait(500)
 await chat('/corerpg skill') // expect CD
 await chat('/corerpg skill info')
 console.log('SKILL_SMOKE_DONE'); process.exit(0)
})
bot.on('kicked',r=>{console.error('kicked',r);process.exit(1)})
bot.on('error',e=>{console.error(e);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},60000)
