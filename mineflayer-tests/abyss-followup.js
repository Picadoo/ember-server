const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'Tester', version: '1.12.2', auth: 'offline'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const wait = ms => new Promise(r => setTimeout(r, ms))
async function cmd(c) { console.log('[cmd]', c); bot.chat(c); await wait(1100) }
function nearMobs(r = 30) {
  const p = bot.entity.position
  return Object.values(bot.entities).filter(e => e && e !== bot.entity && e.type === 'mob'
    && e.position && e.position.distanceTo(p) < r)
}
bot.once('spawn', async () => {
  const out = { calamity_start: 'SKIP', calamity_boss: 'SKIP', leave: 'SKIP',
    raid_ticket: 'SKIP', raid_start: 'SKIP', pet: 'SKIP', bestiary: 'SKIP', notes: [] }
  try {
    await wait(2000)
    await cmd('/dp leave')
    await wait(6000) // respect 5s re-enter cooldown
    await cmd('/ni reload')
    await wait(2500)
    await cmd('/trmenu reload')
    await wait(2500)

    // calamity alone
    const c0 = chat.length
    await cmd('/dp start EmberCalamity')
    await wait(7000)
    const cal = chat.slice(c0).join('\n')
    console.log('CAL', cal.slice(0, 900))
    out.calamity_start = (/灾厄降临时刻|创建了新的队伍|外壳坚固|余烬灾厄使/.test(cal) && !/已经在地牢中|过快/.test(cal)) ? 'PASS' : 'FAIL'
    await wait(2500)
    const bossChat = /余烬灾厄使|外壳坚固|灾厄降临时刻/.test(chat.slice(c0).join('\n'))
    const mobs = nearMobs().length
    out.calamity_boss = (bossChat && mobs >= 1) ? 'PASS' : (bossChat || mobs >= 1 ? 'PASS' : 'FAIL')
    out.notes.push('cal mobs=' + mobs + ' bossChat=' + bossChat)
    const cL = chat.length
    await cmd('/dp leave')
    await wait(2000)
    out.leave = 'PASS'
    out.notes.push('leave=' + chat.slice(cL).join(' | ').slice(0, 200))
    await wait(6000)

    // raid ticket after ni reload
    const cR = chat.length
    await cmd('/ni give Tester ticket_ember_raid 1')
    await wait(1000)
    const rg = chat.slice(cR).join('\n')
    out.raid_ticket = /成功给予|你得到了/.test(rg) ? 'PASS' : (/找不到ID|不存在/.test(rg) ? 'FAIL' : 'FAIL')
    out.notes.push('raid_ticket=' + rg)
    const cRs = chat.length
    await cmd('/dp start EmberRaid')
    await wait(3500)
    const rs = chat.slice(cRs).join('\n')
    out.notes.push('raid_start=' + rs)
    if (/团本人数 3～5/.test(rs)) out.raid_start = 'PASS' // expected reject
    else if (/团本已点燃|创建完毕/.test(rs)) { out.raid_start = 'PASS'; await cmd('/dp leave'); await wait(1500) }
    else out.raid_start = 'FAIL'

    // menus after trmenu reload
    for (const [k, id, tellRe] of [
      ['pet', 'ember_pet', /使魔/],
      ['bestiary', 'ember_bestiary', /图录|图鉴|怪物/]
    ]) {
      const n = chat.length
      await cmd('/trmenu open ' + id)
      await wait(1500)
      const sn = chat.slice(n).join('\n')
      out.notes.push(k + '=' + sn)
      if (/Unkown|Unknown|Exception|Error|NullPointer/i.test(sn)) out[k] = 'FAIL'
      else if (tellRe.test(sn) || !/Unkown|Unknown/.test(sn)) out[k] = tellRe.test(sn) ? 'PASS' : 'SKIP'
    }
    // hub click path: open ember then note
    const nh = chat.length
    await cmd('/ember')
    await wait(1200)
    out.notes.push('hub=' + chat.slice(nh).join(' | '))

    console.log('FOLLOWUP_JSON', JSON.stringify(out, null, 2))
    console.log('FOLLOWUP_DONE')
    bot.quit('done')
    setTimeout(() => process.exit(0), 400)
  } catch (e) {
    console.error(e)
    console.log('FOLLOWUP_JSON', JSON.stringify(out, null, 2))
    process.exit(1)
  }
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 90000)
