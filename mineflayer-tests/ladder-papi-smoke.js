/**
 * Ember ladder PAPI smoke — RpgBot parses hologram placeholders from docs/ember-holograms.md
 * Run only after CoreRpg jar description includes ladder wiring.
 *
 *   cd /workspace/minecraft/mineflayer-tests && node ladder-papi-smoke.js
 */
const mineflayer = require('mineflayer')

const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'RpgBot',
  version: '1.12.2',
  auth: 'offline'
})

const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const chatLog = []

bot.on('message', (m) => {
  const s = m.toString()
  chatLog.push(s)
  console.log('[chat]', s)
})

/** Placeholders from docs/ember-holograms.md §5 — ranks 1–3 name+value + power_score */
const PLACEHOLDERS = []
for (const board of ['power', 'abyss', 'speed']) {
  for (let n = 1; n <= 3; n++) {
    PLACEHOLDERS.push(`%ember_ladder_${board}_${n}_name%`)
    PLACEHOLDERS.push(`%ember_ladder_${board}_${n}_value%`)
  }
}
PLACEHOLDERS.push('%ember_power_score%')

async function parseOne(ph) {
  const before = chatLog.length
  const cmd = `/papi parse me ${ph}`
  console.log('[cmd]', cmd)
  bot.chat(cmd)
  await wait(1200)
  const replies = chatLog.slice(before).map((s) => s.trim()).filter(Boolean)
  const result = replies.length ? replies[replies.length - 1] : '(no reply)'
  console.log('[result]', ph, '=>', result)
  return { placeholder: ph, result, replies }
}

bot.once('spawn', async () => {
  try {
    await wait(2500)
    console.log('LADDER_PAPI_SMOKE_START placeholders=', PLACEHOLDERS.length)

    // sanity: known-working CoreRpg PAPI
    await parseOne('%corerpg_coin%')

    const rows = []
    for (const ph of PLACEHOLDERS) {
      rows.push(await parseOne(ph))
    }

    console.log('--- SUMMARY ---')
    for (const row of rows) {
      const lit = row.result.includes(row.placeholder.replace(/%/g, '')) ||
        row.result === row.placeholder ||
        /%ember_/.test(row.result)
      console.log(
        lit ? 'LITERAL/UNWIRED' : 'OK',
        row.placeholder,
        '=>',
        row.result
      )
    }
    console.log('LADDER_PAPI_SMOKE_DONE')
    process.exit(0)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})

bot.on('kicked', (r) => {
  console.error('kicked', r)
  process.exit(1)
})
bot.on('error', (e) => {
  console.error(e)
  process.exit(1)
})
setTimeout(() => {
  console.error('timeout')
  process.exit(2)
}, 120000)
