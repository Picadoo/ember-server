const fs = require('fs')
const path = require('path')
const mineflayer = require('mineflayer')
const LOG = path.join(__dirname, 'logs/experience-pass1.log')
const out = fs.createWriteStream(LOG, { flags: 'a' })
const log = (l) => { const s = `[${new Date().toISOString()}] ${l}`; console.log(s); out.write(s+'\n') }
const sleep = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s||'').replace(/\u00a7./g,'')

function slotName(it){ return strip(it.customName || it.displayName || it.name || '') }

function attach(bot){
  bot.on('message', m => log(`[chat] ${m.toString()}`))
  bot.on('windowOpen', win => {
    let title=''; try{ title = typeof win.title==='string'?win.title:JSON.stringify(win.title)}catch(e){title=String(win.title)}
    log(`[windowOpen] title=${strip(title)} raw=${title}`)
    const names = (win.slots||[]).filter(Boolean).map(slotName).filter(Boolean)
    log(`[windowItems] ${names.slice(0,45).join(' | ')}`)
  })
  bot.on('windowClose', () => log('[windowClose]'))
}

async function clickByRegex(bot, re, label){
  const win = bot.currentWindow
  if(!win){ log(`[click] no window for ${label}`); return false }
  const it = win.slots.find(s => s && re.test(slotName(s)))
  if(!it){ log(`[click] miss ${label} re=${re}`); return false }
  log(`[click] ${label} slot=${it.slot} name=${slotName(it)}`)
  try { await bot.clickWindow(it.slot, 0, 0) } catch(e){ log(`[clickErr] ${e.message}`); return false }
  await sleep(2200)
  return true
}

async function run(username, mode){
  return new Promise((resolve) => {
    log(`=== pass1b start user=${username} mode=${mode} ===`)
    const bot = mineflayer.createBot({host:'127.0.0.1',port:25565,username,auth:'offline',version:'1.12.2'})
    attach(bot)
    let done=false
    const finish=(c,r)=>{ if(done)return; done=true; log(`=== pass1b finish ${c} ${r} ===`); try{bot.quit()}catch(_){}; setTimeout(()=>resolve(c),400) }
    bot.on('error', e => finish(1, String(e)))
    bot.on('kicked', r => finish(1, 'kicked:'+JSON.stringify(r)))
    setTimeout(()=>finish(2,'timeout'), 75000)
    bot.once('spawn', async () => {
      try {
        await sleep(2000)
        if (mode === 'menu') {
          await sleep(500)
          bot.chat('/ember'); log('[cmd] /ember'); await sleep(2500)
          // hub -> daily
          await clickByRegex(bot, /日常|余烬窟/, 'daily')
          // daily -> start
          await clickByRegex(bot, /开始挑战/, 'startDaily')
          await sleep(4000)
          bot.chat('/dp leave'); log('[cmd] /dp leave'); await sleep(1500)
          // reopen hub -> enhance
          bot.chat('/ember'); log('[cmd] /ember'); await sleep(2200)
          await clickByRegex(bot, /^强化$/, 'enhanceMenu')
          await sleep(1500)
          await clickByRegex(bot, /强化信息|查看|info|强化一次|执行强化|开始强化/i, 'enhanceAction')
          await sleep(1200)
          // covenant icon
          try{ if(bot.currentWindow) bot.closeWindow(bot.currentWindow) }catch(_){}
          await sleep(400)
          bot.chat('/ember'); log('[cmd] /ember'); await sleep(2000)
          await clickByRegex(bot, /^誓约$/, 'covenantMenu')
          await sleep(1500)
          finish(0,'menu_ok')
          return
        }
        // mode full as OP RpgBot
        const cmd = async (c,w=1600)=>{ log('[cmd] '+c); bot.chat(c); await sleep(w) }
        await cmd('/corerpg covenant set blaze', 1200)
        await cmd('/corerpg skill info', 1200)
        await cmd('/mm m spawn EmberDailyZombie 1', 1500)
        const mobs = Object.values(bot.entities).filter(e=>e.type==='mob')
        log(`[info] mobs=${mobs.length}`)
        if(mobs[0]){ try{await bot.lookAt(mobs[0].position.offset(0,1,0))}catch(_){} await sleep(300) }
        await cmd('/corerpg skill', 1400)
        await cmd('/ni give '+username+' gear_ember_blade 1', 1200)
        await cmd('/ni give '+username+' mat_ember_shard 32', 1000)
        await sleep(400)
        const blade = bot.inventory.items().find(i => /刃|blade|sword/i.test(slotName(i)) || /余烬.*刃/.test(slotName(i)))
        const swords = bot.inventory.items().filter(i=>/sword|刃|blade/i.test(i.name+' '+slotName(i)))
        log(`[inv] swords=${JSON.stringify(swords.map(i=>({n:i.name,dn:slotName(i),slot:i.slot})))}`)
        if(blade){ try{await bot.equip(blade,'hand'); log('[equip] ok '+slotName(blade))}catch(e){log('[equip] '+e.message)} }
        else if(swords[0]){ try{await bot.equip(swords[0],'hand'); log('[equip] fallback '+slotName(swords[0]))}catch(e){log('[equip] '+e.message)} }
        await cmd('/corerpg enhance info', 1200)
        await cmd('/corerpg enhance', 1400)
        await cmd('/corerpg cash', 1000)
        await cmd('/corerpg shop buy daily_ticket', 1200)
        await cmd('/ni give '+username+' ticket_ember_daily 2', 1000)
        await cmd('/dp start EmberDaily', 6000)
        await cmd('/dp leave', 2000)
        await cmd('/ember', 2200)
        finish(0,'full_ok')
      } catch(e){ log('[fatal] '+e.stack); finish(1,e.message) }
    })
  })
}

;(async()=>{
  const a = await run('PickyBot', 'menu')
  const b = await run('RpgBot', 'full')
  try{out.end()}catch(_){}
  process.exit(a===0 && b===0 ? 0 : 1)
})()
