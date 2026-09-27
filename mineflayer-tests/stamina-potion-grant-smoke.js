/** Grant no-precount + quick use recheck after CashService shop gate fix */
const { joinPlay } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const OP = 'RpgBot'
const USER = 'Pg_' + Math.floor(Math.random() * 9000 + 1000)
const strip = s => String(s||'').replace(/§./g,'').replace(/\u00a7./g,'')
const nameOf = it => { try { return strip(it.nbt.value.display.value.Name.value).trim() } catch(e){ return '' } }
const countPotion = (b, needle) => b.inventory.items().filter(i => nameOf(i).includes(needle)).reduce((a,i)=>a+i.count,0)
const parseShow = lines => {
  const s = lines.filter(c => c.includes('[体力]') && c.includes('/')).slice(-1)[0] || ''
  const m = strip(s).match(/(\d+)\s*\/\s*(\d+).*药剂今日\s*(\d+)\s*\/\s*(\d+)/)
  return m ? {stam:+m[1], pot:+m[3], raw:s} : {stam:-1, pot:-1, raw:s}
}
;(async () => {
  const out = {user:USER, checks:{}, pass:true}
  const note = (k,ok,d)=>{ out.checks[k]={ok:!!ok,detail:String(d||'')}; if(!ok) out.pass=false; console.log((ok?'PASS':'FAIL')+' '+k+' :: '+d) }
  const op = await joinPlay(OP,{log:false})
  op.on('message', m => console.log('[op]', m.toString()))
  await wait(1200)
  const b = await joinPlay(USER,{log:false})
  const chat=[]
  b.on('message', m => { chat.push(m.toString()); console.log('[chat]', m.toString()) })
  await wait(2500)
  const c = async (cmd,ms=1000)=>{ console.log('[cmd]',cmd); op.chat(cmd); await wait(ms) }
  await c(`/lp user ${OP} permission set corerpg.admin true`, 500)
  await c(`/clear ${USER}`, 800)
  await c(`/corerpg stamina set ${USER} 10`, 800)
  // Push potionToday to 75 via use (30+45) without caring about bank
  await c(`/ni give ${USER} consumable_ember_stamina_30 1`, 1200)
  let it = b.inventory.items().find(i => nameOf(i).includes('体力药·小'))
  await b.equip(it,'hand'); await wait(300); try{await b.consume()}catch(e){}
  await wait(1200)
  await c(`/ni give ${USER} consumable_ember_stamina_45 1`, 1200)
  it = b.inventory.items().find(i => nameOf(i).includes('体力药·周'))
  await b.equip(it,'hand'); await wait(300); try{await b.consume()}catch(e){}
  await wait(1200)
  b.chat('/corerpg stamina show'); await wait(800)
  const before = parseShow(chat)
  note('pre_pot_75', before.pot === 75, JSON.stringify(before))
  await c(`/clear ${USER}`, 600)
  await c(`/corerpg cash give ${USER} 200 nocount`, 800)
  b.chat('/corerpg shop buy daily_ticket'); await wait(2000)
  const buyMsg = chat.filter(c => c.includes('商城')||c.includes('体力药')||c.includes('获得')).slice(-4).join(' || ')
  b.chat('/corerpg stamina show'); await wait(800)
  const after = parseShow(chat)
  const n = countPotion(b, '体力药·小')
  note('grant_ni_got', n >= 1, `n=${n} :: ${buyMsg}`)
  note('grant_pot_unchanged', after.pot === before.pot, `pot ${before.pot}->${after.pot}`)
  // drink the granted bottle should still refuse (75+30>90) and keep bottle
  it = b.inventory.items().find(i => nameOf(i).includes('体力药·小'))
  if (it) {
    await b.equip(it,'hand'); await wait(300); try{await b.consume()}catch(e){}
    await wait(1200)
    const rej = chat.filter(c => c.includes('上限')).slice(-1)[0] || ''
    note('granted_overcap_refuse', rej.includes('上限'), rej)
    note('granted_bottle_kept', countPotion(b,'体力药·小') >= 1, 'n='+countPotion(b,'体力药·小'))
  }
  await c(`/deop ${OP}`, 400)
  op.quit(); b.quit()
  fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n')
  fs.writeFileSync('/tmp/stamina-potion-grant-smoke.json', JSON.stringify(out,null,2))
  console.log('RESULT', out.pass?'PASS':'FAIL', JSON.stringify(out.checks,null,2))
  process.exit(out.pass?0:1)
})().catch(e=>{ console.log('ERR',e); try{fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n')}catch(_){}; process.exit(1)})
setTimeout(()=>{try{fs.writeFileSync('/workspace/minecraft/server-runtime/ops.json','[]\n')}catch(_){}; process.exit(2)},120000)
