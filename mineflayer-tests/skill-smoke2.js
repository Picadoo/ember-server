const mineflayer=require('mineflayer')
const bot=mineflayer.createBot({host:'127.0.0.1',port:25565,username:'RpgBot',version:'1.12.2',auth:'offline'})
bot.on('message',m=>console.log('[chat]',m.toString()))
const wait=ms=>new Promise(r=>setTimeout(r,ms))
async function chat(c){console.log('[cmd]',c);bot.chat(c);await wait(1500)}
bot.once('spawn',async()=>{
 await wait(2500)
 await chat('/corerpg covenant set blaze')
 // Mythic spawn via plugin (console dispatch)
 await chat('/corerpg spawn EmberCryptZombie 1')
 await wait(1500)
 // face nearby entities if any
 const mobs=Object.values(bot.entities).filter(e=>e.type==='mob'|| (e.name&&e.name.toLowerCase().includes('zombie')))
 console.log('[info] nearby entity count', Object.keys(bot.entities).length, 'mobs', mobs.length)
 if(mobs.length){
   const m=mobs[0]
   bot.lookAt(m.position.offset(0,1,0))
   await wait(400)
 }
 await chat('/corerpg skill')
 await wait(400)
 await chat('/corerpg skill') // CD
 await chat('/corerpg skill info')
 // also try ash/warden info paths quickly via covenant switch if allowed
 console.log('SKILL_SMOKE2_DONE'); process.exit(0)
})
bot.on('kicked',r=>{console.error('kicked',r);process.exit(1)})
bot.on('error',e=>{console.error(e);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},90000)
