/**
 * CoreRpg free ticket grant smoke — DESIGN-ember-economy-monetization.md / cash + abyss
 * Expect free grants (CoreRpg 1.3.10+):
 *   余烬日票   ticket_ember_daily  ×3 / day
 *   余烬周票   ticket_ember_weekly ×1 / week
 *   余烬深渊票 ticket_ember_abyss  ×1 / day
 * Flow: clear tickets → reconnect and/or /corerpg ticket* to trigger grant
 *       → inventory count OR /corerpg ticket status (if exists)
 * Do NOT run until plugins/CoreRpg.jar is 1.3.10 with weekly+abyss free grant
 *   (1.3.9 and earlier: join only grants daily ×3 via CashService; no ticket status).
 * No jar overwrite from this script.
 */
const mineflayer = require('mineflayer')

const HOST = '127.0.0.1'
const PORT = 25565
const USER = 'RpgBot'
const VERSION = '1.12.2'

/** Expected free grant counts after a clean day/week reset. */
const EXPECT = {
  daily: { ni: 'ticket_ember_daily', name: /余烬日票/, count: 3 },
  weekly: { ni: 'ticket_ember_weekly', name: /余烬周票/, count: 1 },
  abyss: { ni: 'ticket_ember_abyss', name: /余烬深渊票/, count: 1 }
}

const chatLog = []
const wait = ms => new Promise(r => setTimeout(r, ms))

function attachChat(bot) {
  bot.on('message', m => {
    const s = m.toString()
    chatLog.push(s)
    console.log('[chat]', s)
  })
}

async function chat(bot, c, ms = 1200) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(ms)
}

function recent(n = 28) {
  return chatLog.slice(-n).join('\n')
}

function saw(re, n = 28) {
  return re.test(recent(n))
}

function itemText(i) {
  const parts = [
    i.name || '',
    i.customName || '',
    typeof i.displayName === 'string' ? i.displayName : '',
    ...(Array.isArray(i.lore) ? i.lore : [])
  ]
  return parts.join(' ')
}

/** Count stacks matching a ticket kind (display name / lore NI id / paper+keyword). */
function countTicket(bot, kind) {
  let n = 0
  for (const i of bot.inventory.items()) {
    const t = itemText(i)
    const hit =
      kind.name.test(t) ||
      t.includes(kind.ni) ||
      (kind === EXPECT.daily && /日票|ticket_ember_daily/i.test(t)) ||
      (kind === EXPECT.weekly && /周票|ticket_ember_weekly/i.test(t)) ||
      (kind === EXPECT.abyss && /深渊票|ticket_ember_abyss/i.test(t))
    if (hit) n += i.count || 1
  }
  return n
}

function dumpTicketInv(bot, tag) {
  const rows = bot.inventory.items()
    .map(i => ({
      name: i.name,
      count: i.count,
      dn: i.customName || i.displayName || '',
      lore: (i.lore || []).slice(0, 3)
    }))
    .filter(r =>
      /paper|ticket|余烬|日票|周票|深渊/i.test(
        r.name + ' ' + r.dn + ' ' + (r.lore || []).join(' ')
      )
    )
  console.log('[inv:' + tag + ']', JSON.stringify(rows))
  const counts = {
    daily: countTicket(bot, EXPECT.daily),
    weekly: countTicket(bot, EXPECT.weekly),
    abyss: countTicket(bot, EXPECT.abyss)
  }
  console.log('[ticket-counts:' + tag + ']', counts)
  return counts
}

function createBot() {
  return mineflayer.createBot({
    host: HOST,
    port: PORT,
    username: USER,
    version: VERSION,
    auth: 'offline'
  })
}

function onceSpawn(bot) {
  return new Promise((resolve, reject) => {
    const t = setTimeout(() => reject(new Error('spawn timeout')), 45000)
    bot.once('spawn', () => {
      clearTimeout(t)
      resolve()
    })
    bot.once('error', e => {
      clearTimeout(t)
      reject(e)
    })
    bot.once('kicked', r => {
      clearTimeout(t)
      reject(new Error('kicked ' + r))
    })
  })
}

async function quitSoft(bot) {
  try {
    bot.quit('ticket-grant reconnect')
  } catch (_) {}
  await wait(1500)
}

async function probeTicketStatus(bot) {
  const before = chatLog.length
  // Prefer dedicated ticket surface if 1.3.10 adds it; fall back to cash/abyss.
  await chat(bot, '/corerpg ticket status')
  await chat(bot, '/corerpg ticket')
  const slice = chatLog.slice(before).join('\n')
  const unknown =
    /Unknown command|未知命令|没有该命令|不正确的参数|用法|usage|无效子命令|未知子命令/i.test(slice) &&
    !/日票|周票|深渊票|ticket_ember|今日|本周|granted|免费/i.test(slice)
  if (unknown || !/日票|周票|深渊票|ticket_ember|免费|granted/i.test(slice)) {
    console.log('NOTE_TICKET_STATUS_MISSING', 'no usable /corerpg ticket status — probe cash + abyss')
    await chat(bot, '/corerpg cash')
    await chat(bot, '/corerpg abyss')
  } else {
    console.log('TICKET_STATUS_SNIP', slice.slice(0, 900))
  }
}

async function tryClearGrantFlags(bot) {
  // 1.3.10 may expose admin/reset; probe without failing the smoke.
  const before = chatLog.length
  await chat(bot, '/corerpg ticket clear')
  await chat(bot, '/corerpg ticket reset')
  await chat(bot, '/corerpg ticket grant')
  const slice = chatLog.slice(before).join('\n')
  console.log('TICKET_CLEAR_GRANT_SNIP', slice.slice(0, 700))
  return /已清|已重置|已发放|免费|日票|周票|深渊|grant|reset|clear/i.test(slice)
}

async function phaseClearAndArm(bot) {
  await wait(2000)
  console.log('[plan] --- clear tickets ---')
  await chat(bot, '/clear ' + USER)
  await wait(600)
  dumpTicketInv(bot, 'after-clear')

  console.log('[plan] --- probe ticket status / clear-grant cmds ---')
  await probeTicketStatus(bot)
  const cmdTriggered = await tryClearGrantFlags(bot)

  // If ticket grant/reset already fired and filled inv, record; still reconnect for onJoin path.
  dumpTicketInv(bot, 'after-ticket-cmds')
  return cmdTriggered
}

async function phaseAfterGrant(bot, tag) {
  await wait(2500)
  await probeTicketStatus(bot)
  const counts = dumpTicketInv(bot, tag)

  const okDaily = counts.daily >= EXPECT.daily.count
  const okWeekly = counts.weekly >= EXPECT.weekly.count
  const okAbyss = counts.abyss >= EXPECT.abyss.count

  console.log('EXPECT_FREE', {
    daily: EXPECT.daily.count,
    weekly: EXPECT.weekly.count,
    abyss: EXPECT.abyss.count
  })
  console.log('GRANT_CHECK', {
    daily: okDaily ? 'PASS' : 'FAIL',
    weekly: okWeekly ? 'PASS' : 'FAIL',
    abyss: okAbyss ? 'PASS' : 'FAIL',
    counts
  })

  if (okDaily && okWeekly && okAbyss) {
    console.log('TICKET_GRANT_SMOKE_PASS')
  } else {
    console.log('TICKET_GRANT_SMOKE_INCOMPLETE', counts)
  }
  return counts
}

async function main() {
  console.log('[plan] expected free: daily 3 · weekly 1 · abyss 1 (CoreRpg 1.3.10+)')

  // --- session 1: clear + probe ---
  let bot = createBot()
  attachChat(bot)
  await onceSpawn(bot)
  await phaseClearAndArm(bot)

  // Ensure inventory empty of tickets before reconnect grant
  await chat(bot, '/clear ' + USER)
  await wait(500)
  dumpTicketInv(bot, 'pre-reconnect')

  console.log('[plan] --- reconnect to trigger onJoin / daily grant ---')
  await quitSoft(bot)

  // --- session 2: onJoin grant ---
  bot = createBot()
  attachChat(bot)
  await onceSpawn(bot)
  await phaseAfterGrant(bot, 'post-reconnect')

  // Optional: if still short, try explicit grant once more then re-check
  const late = dumpTicketInv(bot, 'final')
  if (
    late.daily < EXPECT.daily.count ||
    late.weekly < EXPECT.weekly.count ||
    late.abyss < EXPECT.abyss.count
  ) {
    console.log('[plan] --- retry /corerpg ticket grant + cash ---')
    await chat(bot, '/corerpg ticket grant')
    await chat(bot, '/corerpg cash')
    await wait(800)
    await phaseAfterGrant(bot, 'post-grant-retry')
  }

  console.log('TICKET_GRANT_SMOKE_DONE')
  try {
    bot.quit('done')
  } catch (_) {}
  setTimeout(() => process.exit(0), 400)
}

main().catch(e => {
  console.error(e)
  process.exit(1)
})

setTimeout(() => {
  console.error('timeout')
  process.exit(2)
}, 120000)
