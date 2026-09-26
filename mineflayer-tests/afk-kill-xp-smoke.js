// AFK kill → ember XP smoke (2026-09-26): 3 EmberAfkZombie kills by a fresh bot via proxy → kill XP 6/100.

const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms)); const strip = s => String(s||'').replace(/§./g,'')
;(async()=>{ const N='KillXp'+Math.floor(Math.random()*9000+1000)
 const op=await joinPlay('RpgBot',{log:false}); const c=async(x,ms=1200)=>{op.chat(x);await wait(ms)}
 const b=await joinPlay(N,{log:false}); await wait(1000)
 await c(`/mvtp ${N} world`,3000); await c(`/tp ${N} 0 100 0`,1500); await c(`/effect ${N} strength 60 20`); await c(`/effect ${N} resistance 60 5`)
 const p=b.entity.position; let hits=0, dead=0; b.on('entityDead',()=>dead++)
 for (let k=0;k<3;k++){ await c(`/mm m spawn EmberAfkZombie 1 world,${(p.x+2).toFixed(1)},${p.y.toFixed(1)},${p.z.toFixed(1)}`,1000)
  for(let i=0;i<20;i++){const m=b.nearestEntity(e=>e.type==='mob'&&e.position.distanceTo(b.entity.position)<8); if(!m)break; await b.lookAt(m.position.offset(0,1,0)); b.attack(m); hits++; await wait(600)} }
 const n=b.chatLog.length; b.chat('/corerpg level'); await wait(1500); console.log('HITS',hits,'DEAD',dead,'NEAR',Object.values(b.entities).filter(e=>e.type==='mob').map(e=>e.name).join(','));console.log('LEVEL', strip(b.chatLog.slice(n).join(' | ')))
 b.quit(); op.quit(); process.exit(0)})().catch(e=>{console.log('FATAL',e.message);process.exit(1)})
