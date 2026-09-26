const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', auth: 'offline', version: '1.12.2'
})
const chat=[]
bot.on('message', m => { const s=m.toString(); chat.push(s); console.log('[chat]', s) })
const sleep = ms => new Promise(r => setTimeout(r, ms))
function inv() {
  return bot.inventory.items().map(i => ({
    name:i.name, count:i.count,
    customName: i.customName||null,
    lore: i.nbt?.value?.display?.value?.Lore?.value?.value || null
  }))
}
bot.once('spawn', async () => {
  try {
    bot.chat('/clear')
    await sleep(500)
    bot.chat('/ni give Tester gear_ember_blade 1')
    await sleep(700)
    bot.chat('/ni give Tester gear_ember_charm 1')
    await sleep(700)
    // also check if shard still somehow - and dump inventory
    const items = inv()
    console.log('LORE_INV', JSON.stringify(items))
    const text = JSON.stringify(items)
    const ok = /物理伤害/.test(text) && /生命力/.test(text) && /物理防御/.test(text)
    const old = /攻击力:/.test(text) || /生命值:/.test(text) || /防御力:/.test(text)
    console.log('LORE_AP_KEYS', ok ? 'PASS' : 'FAIL')
    console.log('LORE_OLD_KEYS_PRESENT', old ? 'YES' : 'NO')
    console.log('DONE')
    bot.quit(); setTimeout(()=>process.exit(0),400)
  } catch(e){ console.error(e); process.exit(1) }
})
bot.on('error', e=>{console.error(e);process.exit(1)})
bot.on('kicked', r=>{console.error('kicked',r);process.exit(1)})
setTimeout(()=>{console.error('timeout');process.exit(2)},30000)
