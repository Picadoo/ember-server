const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline'
})
const chat = []
bot.on('message', m => {
  const s = m.toString()
  chat.push(s)
  console.log('[chat]', s)
})
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chatCmd(c, w = 1400) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(w)
}
function countWeekly() {
  return bot.inventory.items().filter(i => {
    const lore = (((i.nbt || {}).value || {}).display || {}).value
    const loreArr = lore && lore.Lore && lore.Lore.value && lore.Lore.value.value
    const name = (i.customName || '') + ' ' + (Array.isArray(loreArr) ? loreArr.join(' ') : '')
    const dn = i.displayName || ''
    return /ticket_ember_weekly|周票|余烬周/.test(name + ' ' + dn + ' ' + (i.name || ''))
  }).reduce((a, i) => a + i.count, 0)
}
function invHint() {
  return bot.inventory.items().map(i => ({
    name: i.name, count: i.count, customName: i.customName || null
  }))
}
bot.once('spawn', async () => {
  const out = { buy1: null, buy2: null, weeklyBefore: 0, weeklyAfter: 0, weeklyAfter2: 0, refuse: [], ok: false }
  try {
    await wait(2500)
    await chatCmd('/gamemode 1', 400)
    await chatCmd('/corerpg tickets', 1200)
    out.weeklyBefore = countWeekly()
    console.log('WEEKLY_BEFORE', out.weeklyBefore, JSON.stringify(invHint()))

    await chatCmd('/corerpg cash give RpgBot 400', 1200)
    await chatCmd('/corerpg cash', 1000)
    await chatCmd('/corerpg shop buy weekly_ticket', 1800)
    out.buy1 = chat.filter(c => /商城|周票|限购|硬顶|晶钻/.test(c)).slice(-8)
    out.weeklyAfter = countWeekly()
    console.log('WEEKLY_AFTER_BUY1', out.weeklyAfter, JSON.stringify(out.buy1))

    await chatCmd('/corerpg shop buy weekly_ticket', 1800)
    out.buy2 = chat.filter(c => /商城|周票|限购|硬顶|晶钻/.test(c)).slice(-8)
    out.weeklyAfter2 = countWeekly()
    console.log('WEEKLY_AFTER_BUY2', out.weeklyAfter2, JSON.stringify(out.buy2))

    await chatCmd('/corerpg tickets', 1200)
    await chatCmd('/corerpg vip', 1000)
    await chatCmd('/corerpg vip claim', 1200)

    const boughtOk = out.buy1.some(c => /已购买周票/.test(c))
    const refused = out.buy2.some(c => /限购已满|硬顶/.test(c))
    const gained = out.weeklyAfter >= out.weeklyBefore + 1
    out.ok = boughtOk && refused && gained
    out.notes.push('boughtOk=' + boughtOk, 'refused=' + refused, 'gained=' + gained,
      'before=' + out.weeklyBefore, 'after1=' + out.weeklyAfter, 'after2=' + out.weeklyAfter2)
    console.log('RESULT', JSON.stringify(out, null, 2))
    console.log(out.ok ? 'WEEKLY_TICKET_SMOKE_OK' : 'WEEKLY_TICKET_SMOKE_FAIL')
  } catch (e) {
    console.error('FAIL', e)
    out.notes.push(String(e))
    console.log('RESULT', JSON.stringify(out, null, 2))
  } finally {
    bot.quit()
    setTimeout(() => process.exit(out.ok ? 0 : 1), 500)
  }
})
bot.on('kicked', r => { console.error('KICKED', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('TIMEOUT'); process.exit(2) }, 90000)
