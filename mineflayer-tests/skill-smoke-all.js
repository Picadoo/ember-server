const mineflayer=require('mineflayer')
const bot=mineflayer.createBot({host:'127.0.0.1',port:25565,username:'RpgBot',version:'1.12.2',auth:'offline'})
bot.on('message',m=>console.log('[chat]',m.toString()))
const wait=ms=>new Promise(r=>setTimeout(r,ms))
async function chat(c){console.log('[cmd]',c);bot.chat(c);await wait(1200)}
bot.once('spawn',async()=>{
 await wait(2000)
 await chat('/corerpg spawn EmberCryptZombie 1')
 await wait(800)
 // ash mark
 await chat('/corerpg covenant set ash')
 await chat('/corerpg skill info')
 await chat('/corerpg skill')
 // warden
 await chat('/corerpg covenant set warden')
 await chat('/corerpg skill info')
 await chat('/corerpg skill')
 console.log('SKILL_ALL_DONE'); process.exit(0)
})
bot.on('kicked',r=>{console.error(r);process.exit(1)})
bot.on('error',e=>{console.error(e);process.exit(1)})
setTimeout(()=>process.exit(2),90000)
