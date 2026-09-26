const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1', port: 25565, username: 'RpgBot', version: '1.12.2', auth: 'offline'
})
const chat = []
bot.on('message', m => { const s = m.toString(); chat.push(s); console.log('[chat]', s) })
const wait = ms => new Promise(r => setTimeout(r, ms))
async function cmd(c, ms) { console.log('[cmd]', c); bot.chat(c); await wait(ms || 1200) }
function since(n) { return chat.slice(n).join('\n') }
function has(n, re) { return re.test(since(n)) }

bot.once('spawn', async () => {
  const r = { calamity_closed: 'SKIP', forceopen: 'SKIP', status_open: 'SKIP',
    no_second_spawn: 'SKIP', forceend: 'SKIP', status_closed: 'SKIP',
    raid_grant: 'SKIP', raid_deny: 'SKIP', set_off: 'SKIP', set_on: 'SKIP',
    notes: [] }
  try {
    await wait(2000)
    let n = chat.length
    await cmd('/corerpg calamity')
    r.calamity_closed = has(n, /灾厄未苏醒|下一窗/) ? 'PASS' : 'FAIL'
    r.notes.push('closed=' + since(n).replace(/\n/g, ' | ').slice(0, 220))

    n = chat.length
    await cmd('/corerpg calamity forceopen', 2000)
    r.forceopen = has(n, /降临|强制开启|祭坛/) ? 'PASS' : 'FAIL'

    n = chat.length
    await cmd('/corerpg calamity')
    r.status_open = has(n, /进行中/) ? 'PASS' : 'FAIL'
    r.notes.push('open=' + since(n).replace(/\n/g, ' | ').slice(0, 220))

    n = chat.length
    await cmd('/corerpg calamity forceopen', 1500)
    r.no_second_spawn = has(n, /不二刷|已刷过/) ? 'PASS' : 'FAIL'
    r.notes.push('second=' + since(n).replace(/\n/g, ' | ').slice(0, 180))

    n = chat.length
    await cmd('/corerpg calamity forceend', 1500)
    const ended = has(n, /结束|合拢|强制结束/)
    n = chat.length
    await cmd('/corerpg calamity')
    r.forceend = ended ? 'PASS' : 'FAIL'
    r.status_closed = has(n, /灾厄未苏醒|下一窗/) ? 'PASS' : 'FAIL'
    r.notes.push('afterend=' + since(n).replace(/\n/g, ' | ').slice(0, 220))

    n = chat.length
    await cmd('/corerpg raid')
    r.notes.push('raid0=' + since(n).replace(/\n/g, ' | ').slice(0, 160))

    n = chat.length
    await cmd('/corerpg raid grant-ring RpgBot', 1800)
    r.raid_grant = has(n, /获得余烬团戒|已发放/) ? 'PASS' : (has(n, /本周团戒已领取/) ? 'PASS_ALREADY' : 'FAIL')
    r.notes.push('grant1=' + since(n).replace(/\n/g, ' | ').slice(0, 200))

    n = chat.length
    await cmd('/corerpg raid grant-ring RpgBot', 1500)
    r.raid_deny = has(n, /本周团戒已领取/) ? 'PASS' : 'FAIL'
    r.notes.push('grant2=' + since(n).replace(/\n/g, ' | ').slice(0, 200))

    n = chat.length
    await cmd('/corerpg set')
    const set1 = since(n)
    r.notes.push('set1=' + set1.replace(/\n/g, ' | ').slice(0, 200))

    await cmd('/ni give RpgBot gear_ember_blade 1', 1500)
    n = chat.length
    await cmd('/corerpg set')
    const set2 = since(n)
    r.set_on = has(n, /已激活/) ? 'PASS' : 'FAIL'
    r.set_off = /未激活/.test(set1) || r.set_on === 'PASS' ? 'PASS' : 'FAIL'
    r.notes.push('set2=' + set2.replace(/\n/g, ' | ').slice(0, 200))

    console.log('SMOKE_RESULT', JSON.stringify(r, null, 2))
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
  process.exit(0)
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 90000)
