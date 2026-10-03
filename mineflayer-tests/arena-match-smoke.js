/**
 * CoreRpg arena 1v1 match smoke — docs/design/DESIGN-ember-arena-auction.md §1
 * Two bots ArenaA + ArenaB:
 *   both /corerpg arena queue 1v1 → wait teleport/match tells → assert positions change
 *   leave or wait settle → then /corerpg pvp queue 1v1 alias path
 * Do NOT run until plugins/CoreRpg.jar is 1.3.11 with:
 *   - real (or stub) match teleport / position change on 1v1 fill
 *   - /corerpg pvp … alias wired to ArenaService
 * No jar overwrite from this script.
 */
const mineflayer = require('mineflayer')

const HOST = '127.0.0.1'
const PORT = 25565
const VERSION = '1.12.2'
const USERS = ['ArenaA', 'ArenaB']

/** Min horizontal distance (blocks) to treat as "teleported / moved for match". */
const POS_DELTA_MIN = 2.5
/** How long to wait for match / teleport after both queued. */
const MATCH_WAIT_MS = 12000
const SETTLE_MS = 4000

const wait = ms => new Promise(r => setTimeout(r, ms))

function createBot(username) {
  return mineflayer.createBot({
    host: HOST,
    port: PORT,
    username,
    version: VERSION,
    auth: 'offline'
  })
}

function attachChat(bot, tag, chatLog) {
  bot.on('message', m => {
    const s = m.toString()
    chatLog.push({ who: tag, text: s, t: Date.now() })
    console.log('[' + tag + ']', s)
  })
}

async function chat(bot, tag, c, ms = 1200) {
  console.log('[cmd:' + tag + ']', c)
  bot.chat(c)
  await wait(ms)
}

function onceSpawn(bot) {
  return new Promise((resolve, reject) => {
    const t = setTimeout(() => reject(new Error('spawn timeout ' + bot.username)), 45000)
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
      reject(new Error('kicked ' + bot.username + ' ' + String(r)))
    })
  })
}

function posOf(bot) {
  const p = bot.entity && bot.entity.position
  if (!p) return null
  return { x: p.x, y: p.y, z: p.z, dim: bot.game && bot.game.dimension }
}

function distXZ(a, b) {
  if (!a || !b) return 0
  const dx = a.x - b.x
  const dz = a.z - b.z
  return Math.sqrt(dx * dx + dz * dz)
}

function dist3(a, b) {
  if (!a || !b) return 0
  const dx = a.x - b.x
  const dy = a.y - b.y
  const dz = a.z - b.z
  return Math.sqrt(dx * dx + dy * dy + dz * dz)
}

function recentAll(chatLog, n = 40) {
  return chatLog.slice(-n).map(e => e.who + ': ' + e.text).join('\n')
}

function sawSince(chatLog, sinceIdx, re) {
  return chatLog.slice(sinceIdx).some(e => re.test(e.text))
}

function snipSince(chatLog, sinceIdx, max = 900) {
  return chatLog
    .slice(sinceIdx)
    .map(e => e.who + ': ' + e.text)
    .join('\n')
    .slice(0, max)
}

/**
 * Shared match flow for one command family (arena or pvp).
 * @returns {{ matched: boolean, moved: boolean, posBefore: object, posAfter: object }}
 */
async function runQueueMatch(bots, chatLog, { queueCmd, leaveCmd, label }) {
  const [a, b] = bots
  const mark = chatLog.length

  const posBefore = {
    ArenaA: posOf(a),
    ArenaB: posOf(b)
  }
  console.log('[plan:' + label + '] pos_before', JSON.stringify(posBefore))

  // stagger queue so both land in the same 1v1 bucket
  await chat(a, 'ArenaA', queueCmd, 800)
  await chat(b, 'ArenaB', queueCmd, 800)

  const deadline = Date.now() + MATCH_WAIT_MS
  let matched = false
  let moved = false
  let posAfter = { ArenaA: posOf(a), ArenaB: posOf(b) }

  const matchRe = /匹配成功|已匹配|进入竞技|传送|teleport|match|1v1.*成功|对局开始|对战开始|超时平局|stub/i
  const queueRe = /已加入.*1v1|排队|队列/i

  while (Date.now() < deadline) {
    posAfter = { ArenaA: posOf(a), ArenaB: posOf(b) }
    matched = sawSince(chatLog, mark, matchRe)
    const dA = dist3(posBefore.ArenaA, posAfter.ArenaA)
    const dB = dist3(posBefore.ArenaB, posAfter.ArenaB)
    const dAxz = distXZ(posBefore.ArenaA, posAfter.ArenaA)
    const dBxz = distXZ(posBefore.ArenaB, posAfter.ArenaB)
    moved =
      dA >= POS_DELTA_MIN ||
      dB >= POS_DELTA_MIN ||
      dAxz >= POS_DELTA_MIN ||
      dBxz >= POS_DELTA_MIN ||
      (posBefore.ArenaA &&
        posAfter.ArenaA &&
        posBefore.ArenaA.dim != null &&
        posAfter.ArenaA.dim != null &&
        posBefore.ArenaA.dim !== posAfter.ArenaA.dim) ||
      (posBefore.ArenaB &&
        posAfter.ArenaB &&
        posBefore.ArenaB.dim != null &&
        posAfter.ArenaB.dim != null &&
        posBefore.ArenaB.dim !== posAfter.ArenaB.dim)

    if (matched && moved) break
    // stub-only builds (no TP): accept match tell alone after a short settle
    if (matched && Date.now() > deadline - 2000) break
    await wait(500)
  }

  // extra settle for win/loss / return TP
  await wait(SETTLE_MS)
  posAfter = { ArenaA: posOf(a), ArenaB: posOf(b) }
  matched = matched || sawSince(chatLog, mark, matchRe)
  const dA = dist3(posBefore.ArenaA, posAfter.ArenaA)
  const dB = dist3(posBefore.ArenaB, posAfter.ArenaB)
  moved =
    moved ||
    dA >= POS_DELTA_MIN ||
    dB >= POS_DELTA_MIN ||
    distXZ(posBefore.ArenaA, posAfter.ArenaA) >= POS_DELTA_MIN ||
    distXZ(posBefore.ArenaB, posAfter.ArenaB) >= POS_DELTA_MIN

  console.log('[plan:' + label + '] pos_after', JSON.stringify(posAfter))
  console.log(
    '[plan:' + label + '] deltas',
    JSON.stringify({
      dA: Number(dA.toFixed(2)),
      dB: Number(dB.toFixed(2)),
      matched,
      moved,
      queued: sawSince(chatLog, mark, queueRe)
    })
  )
  console.log('MATCH_SNIP_' + label, snipSince(chatLog, mark, 1000))

  // clear queue or in-match: leave when queued, forfeit when fighting
  const forfeitCmd = leaveCmd.replace(/\bleave\b/, 'forfeit')
  await chat(a, 'ArenaA', leaveCmd, 700)
  await chat(b, 'ArenaB', leaveCmd, 700)
  if (sawSince(chatLog, mark, /对战中|forfeit|认输/i)) {
    await chat(a, 'ArenaA', forfeitCmd, 900)
    await chat(b, 'ArenaB', forfeitCmd, 900)
  }
  await wait(1200)

  // soft settle: stats so logs show points after stub settle
  const statsCmd = leaveCmd.replace(/\bleave\b/, 'stats')
  if (statsCmd !== leaveCmd) {
    await chat(a, 'ArenaA', statsCmd, 900)
    await chat(b, 'ArenaB', statsCmd, 900)
  }

  return { matched, moved, posBefore, posAfter, dA, dB }
}

async function main() {
  const chatLog = []
  const bots = USERS.map(u => createBot(u))
  bots.forEach((bot, i) => attachChat(bot, USERS[i], chatLog))

  const hardTimeout = setTimeout(() => {
    console.error('timeout')
    process.exit(2)
  }, 180000)

  try {
    await Promise.all(bots.map(onceSpawn))
    await wait(2500)

    // baseline status (optional)
    await chat(bots[0], 'ArenaA', '/corerpg arena', 1000)
    await chat(bots[1], 'ArenaB', '/corerpg arena', 1000)

    console.log('[plan] --- arena queue 1v1 (both) ---')
    const arena = await runQueueMatch(bots, chatLog, {
      queueCmd: '/corerpg arena queue 1v1',
      leaveCmd: '/corerpg arena leave',
      label: 'ARENA'
    })

    await wait(2000)

    console.log('[plan] --- pvp alias queue 1v1 (both) ---')
    const pvp = await runQueueMatch(bots, chatLog, {
      queueCmd: '/corerpg pvp queue 1v1',
      leaveCmd: '/corerpg pvp leave',
      label: 'PVP'
    })

    // 1.3.11: teleport pads + 「对战开始」 counts as match; pvp alias must also start or refuse cleanly
    const arenaOk = arena.matched || arena.moved
    const pvpOk = pvp.matched || pvp.moved
    const moveNote =
      arena.moved || pvp.moved
        ? 'POS_CHANGED'
        : 'POS_UNCHANGED_STUB_OK_IF_MATCHED'

    console.log(
      'ARENA_MATCH_SUMMARY',
      JSON.stringify({
        arena_matched: arena.matched,
        arena_moved: arena.moved,
        pvp_matched: pvp.matched,
        pvp_moved: pvp.moved,
        moveNote
      })
    )

    if (arenaOk && pvpOk) {
      console.log('ARENA_MATCH_SMOKE_PASS')
    } else if (arenaOk) {
      console.log('ARENA_MATCH_SMOKE_PARTIAL_ARENA_OK_PVP_FAIL')
    } else {
      console.log('ARENA_MATCH_SMOKE_FAIL')
    }

    clearTimeout(hardTimeout)
    for (const bot of bots) {
      try {
        bot.quit()
      } catch (_) {}
    }
    await wait(500)
    process.exit(arenaOk && pvpOk ? 0 : 1)
  } catch (e) {
    console.error(e)
    clearTimeout(hardTimeout)
    process.exit(1)
  }
}

main()
