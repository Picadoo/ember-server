// Shared runner for the small check-*.js scripts (current topology since 2026-09-26):
// bot joins via proxy :25565 → AuthMe → play (lib/proxy-login joinPlay); the bot is NOT op —
// the admin "check" commands and NI gives are sent on the play-server console (lib/console.js,
// see README「控制台 FIFO」) and their output is read from server-runtime/logs/latest.log.
const { joinPlay } = require('../../lib/proxy-login')
const con = require('../../lib/console')
const sleep = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s || '').replace(/§./g, '')
function nbtName (it) {
  try { const d = it.nbt.value.display.value; return d.Name ? d.Name.value : '' } catch (_) { return '' }
}

// spec: { title, checks: [{ cmd, expect: [RegExp] }], gives: [{ id, cn }] }
async function runCheck (spec) {
  const user = process.env.MC_USER || 'EmberTestOp'
  if (!con.available()) { console.error('[check] console FIFO missing — see README「控制台 FIFO」'); process.exit(1) }
  const fails = []
  // 1) console checks (no bot needed)
  for (const c of spec.checks || []) {
    const m = con.mark(); con.send(c.cmd)
    const deadline = Date.now() + 5000
    let missing = c.expect
    while (Date.now() < deadline && missing.length) {
      await sleep(200)
      const out = con.since(m).join('\n')
      missing = c.expect.filter((re) => !re.test(out))
    }
    for (const re of missing) fails.push(`${c.cmd}: missing ${re}`)
    console.log(`[check] ${c.cmd} → ${missing.length ? 'MISSING ' + missing.join(' ') : 'ok'}`)
  }
  // 2) NI gives land in the bot's inventory with the expected Chinese name
  if ((spec.gives || []).length) {
    const bot = await joinPlay(user, { log: false })
    try {
      await con.run(`clear ${user}`, /Cleared|No items/i)
      for (const g of spec.gives) {
        await con.run(`ni give ${user} ${g.id} 1`, null)
        let it = null
        for (let i = 0; i < 20 && !it; i++) { await sleep(150); it = bot.inventory.items().find((x) => strip(nbtName(x)).includes(g.cn)) }
        console.log(`[check] ni give ${g.id} → ${it ? `${it.name} "${strip(nbtName(it))}"` : 'NOT RECEIVED'}`)
        if (!it) fails.push(`ni give ${g.id}: no item named ${g.cn}`)
      }
    } finally {
      await con.run(`clear ${user}`, /Cleared|No items/i)
      bot.quit('check done')
    }
  }
  console.log(fails.length ? `[check] ${spec.title} PROOF INCOMPLETE\n  - ${fails.join('\n  - ')}` : `[check] ${spec.title} PROOF OK`)
  process.exit(fails.length ? 2 : 0)
}

module.exports = { runCheck }
