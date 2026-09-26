/**
 * 余烬服体验复验1 — 普通号 PickyBot（勿 OP / 勿 RpgBot）
 * 复验：进日常扣票、/ember 技能槽释放+shift info、誓约提示；顺带记 stub 观感
 */
const fs = require('fs')
const path = require('path')
const mineflayer = require('mineflayer')

const LOG_DIR = path.join(__dirname, 'logs')
const LOG_FILE = path.join(LOG_DIR, 'experience-reverify1.log')
fs.mkdirSync(LOG_DIR, { recursive: true })
const logStream = fs.createWriteStream(LOG_FILE, { flags: 'w' })

const stamp = () => new Date().toISOString()
const log = (line) => {
  const s = `[${stamp()}] ${line}`
  console.log(s)
  logStream.write(s + '\n')
}
const sleep = (ms) => new Promise((r) => setTimeout(r, ms))
const strip = (s) => String(s || '').replace(/\u00a7./g, '')

function slotName(it) {
  if (!it) return ''
  return strip(it.customName || it.displayName || it.name || '')
}

function countTickets(bot) {
  const items = bot.inventory.items()
  const hits = items.filter((i) => /余烬日票|ticket_ember_daily|日票/i.test(slotName(i) + ' ' + (i.name || '')))
  const total = hits.reduce((n, i) => n + (i.count || 1), 0)
  return { total, detail: hits.map((i) => ({ name: slotName(i) || i.name, count: i.count, slot: i.slot })) }
}

function attach(bot) {
  bot.on('message', (m) => log(`[chat] ${m.toString()}`))
  bot.on('windowOpen', (win) => {
    let title = ''
    try {
      title = typeof win.title === 'string' ? win.title : (win.title && win.title.toString ? win.title.toString() : JSON.stringify(win.title))
    } catch (e) {
      title = String(win.title)
    }
    log(`[windowOpen] title=${strip(title)} raw=${JSON.stringify(title)}`)
    const names = []
    for (let i = 0; i < Math.min((win.slots || []).length, 54); i++) {
      const it = win.slots[i]
      if (!it) continue
      const n = slotName(it)
      if (!n) continue
      names.push(`#${i}:${n}`)
    }
    log(`[windowItems] ${names.join(' | ')}`)
  })
  bot.on('windowClose', () => log('[windowClose]'))
}

async function closeWin(bot) {
  try {
    if (bot.currentWindow) bot.closeWindow(bot.currentWindow)
  } catch (_) {}
  await sleep(400)
}

async function clickByRegex(bot, re, label, button = 0, mode = 0) {
  const win = bot.currentWindow
  if (!win) {
    log(`[click] no window for ${label}`)
    return false
  }
  // Prefer top inventory (0..53) menu icons over player inv
  let it = null
  for (let i = 0; i < Math.min(win.slots.length, 54); i++) {
    const s = win.slots[i]
    if (s && re.test(slotName(s))) {
      it = s
      break
    }
  }
  if (!it) {
    log(`[click] miss ${label} re=${re}`)
    return false
  }
  log(`[click] ${label} slot=${it.slot} name=${slotName(it)} btn=${button} mode=${mode}`)
  try {
    await bot.clickWindow(it.slot, button, mode)
  } catch (e) {
    log(`[clickErr] ${e.message}`)
    return false
  }
  await sleep(2200)
  return true
}

async function run(username) {
  return new Promise((resolve) => {
    log(`=== experience-reverify1 start user=${username} ===`)
    const bot = mineflayer.createBot({
      host: '127.0.0.1',
      port: 25565,
      username,
      auth: 'offline',
      version: '1.12.2'
    })
    attach(bot)

    let settled = false
    const finish = (code, reason) => {
      if (settled) return
      settled = true
      log(`=== finish code=${code} reason=${reason} ===`)
      try {
        bot.quit()
      } catch (_) {}
      setTimeout(() => resolve({ code, reason, username }), 500)
    }

    bot.on('error', (e) => {
      log(`[error] ${e && e.message ? e.message : e}`)
      finish(1, `error:${e && e.message}`)
    })
    bot.on('kicked', (r) => {
      const reason = typeof r === 'string' ? r : JSON.stringify(r)
      log(`[kicked] ${reason}`)
      if (/already|logged|冲突|在线|duplicate|You are already/i.test(reason)) {
        finish(3, `kicked_conflict:${reason}`)
      } else {
        finish(1, `kicked:${reason}`)
      }
    })

    const hard = setTimeout(() => {
      log('[timeout] 120s hard stop')
      finish(2, 'timeout120s')
    }, 115000)

    bot.once('spawn', async () => {
      try {
        await sleep(2500)
        log('[step] spawned')

        // --- tickets ---
        let t0 = countTickets(bot)
        log(`[ticket] before=${JSON.stringify(t0)}`)
        if (t0.total < 1) {
          log('[step] no daily ticket — try shop buy (no OP give)')
          bot.chat('/corerpg shop buy daily_ticket')
          log('[cmd] /corerpg shop buy daily_ticket')
          await sleep(1800)
          t0 = countTickets(bot)
          log(`[ticket] after_shop=${JSON.stringify(t0)}`)
          if (t0.total < 1) {
            bot.chat('/corerpg cash')
            log('[cmd] /corerpg cash')
            await sleep(1200)
          }
        }

        // --- 1) dungeon start ---
        log('[step] dp start EmberDaily')
        bot.chat('/dp start EmberDaily')
        log('[cmd] /dp start EmberDaily')
        await sleep(5500)
        const t1 = countTickets(bot)
        log(`[ticket] after_start=${JSON.stringify(t1)}`)
        log(`[world] ${bot.game && bot.game.dimension} pos=${bot.entity ? bot.entity.position : '?'}`)

        bot.chat('/dp leave')
        log('[cmd] /dp leave')
        await sleep(2500)
        // some DP versions need different leave
        bot.chat('/dungeon leave')
        log('[cmd] /dungeon leave')
        await sleep(1500)

        await closeWin(bot)

        // --- 2) /ember hub → skill ---
        log('[step] /ember hub skill')
        bot.chat('/ember')
        log('[cmd] /ember')
        await sleep(2800)

        // record row1 titles from slots 0..8 roughly
        if (bot.currentWindow) {
          const row1 = []
          for (let i = 0; i < 9; i++) {
            const it = bot.currentWindow.slots[i]
            if (it) row1.push(`#${i}:${slotName(it)}`)
          }
          log(`[hub-row1] ${row1.join(' | ')}`)
        }

        // left-click 技能 to cast
        const skillOk = await clickByRegex(bot, /^技能$|§c技能|技能/, 'skill_cast', 0, 0)
        await sleep(1500)

        // reopen for shift-click info
        await closeWin(bot)
        bot.chat('/ember')
        log('[cmd] /ember')
        await sleep(2500)

        // mineflayer shift-click: mode 1 = shift, button 0 = left
        // Also try setControlState sneak + click
        try {
          bot.setControlState('sneak', true)
          await sleep(200)
        } catch (_) {}
        const infoOk = await clickByRegex(bot, /^技能$|技能/, 'skill_shift_info', 0, 1)
        try {
          bot.setControlState('sneak', false)
        } catch (_) {}
        await sleep(1500)

        // if shift didn't fire info, also try direct command as observation of skill info wording
        bot.chat('/corerpg skill info')
        log('[cmd] /corerpg skill info')
        await sleep(1600)

        await closeWin(bot)

        // --- covenant page tip ---
        log('[step] covenant page')
        bot.chat('/ember')
        log('[cmd] /ember')
        await sleep(2200)
        await clickByRegex(bot, /^誓约$/, 'covenant_open', 0, 0)
        await sleep(2000)
        await closeWin(bot)

        // also open via trmenu in case click failed
        bot.chat('/trmenu open ember_covenant')
        log('[cmd] /trmenu open ember_covenant')
        await sleep(2200)
        await closeWin(bot)

        // optional: menu path daily start (if still have ticket)
        const t2 = countTickets(bot)
        log(`[ticket] mid=${JSON.stringify(t2)}`)
        if (t2.total >= 1) {
          bot.chat('/trmenu open ember_daily')
          log('[cmd] /trmenu open ember_daily')
          await sleep(2000)
          await clickByRegex(bot, /开始挑战/, 'daily_start_btn', 0, 0)
          await sleep(4000)
          bot.chat('/dp leave')
          log('[cmd] /dp leave')
          await sleep(2000)
        }

        clearTimeout(hard)
        log('[step] path complete')
        log(`[meta] skillClick=${skillOk} shiftInfoClick=${infoOk}`)
        finish(0, 'ok')
      } catch (e) {
        log(`[fatal] ${e && e.stack ? e.stack : e}`)
        finish(1, `exception:${e && e.message}`)
      }
    })
  })
}

;(async () => {
  let result = await run('PickyBot')
  if (result.code === 3) {
    log('=== retry with PickyBot3 ===')
    result = await run('PickyBot3')
  }
  try {
    logStream.end()
  } catch (_) {}
  process.exit(result.code === 0 ? 0 : result.code)
})()
