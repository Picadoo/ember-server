/**
 * 余烬服体验验收 pass1 — 自己当玩家走总控路径，打 chat/window 日志。
 * 超时 90s；用户名冲突时自动试 PickyBot2。
 */
const fs = require('fs')
const path = require('path')
const mineflayer = require('mineflayer')

const LOG_DIR = path.join(__dirname, 'logs')
const LOG_FILE = path.join(LOG_DIR, 'experience-pass1.log')
fs.mkdirSync(LOG_DIR, { recursive: true })
const logStream = fs.createWriteStream(LOG_FILE, { flags: 'w' })

function stamp() {
  return new Date().toISOString()
}
function log(line) {
  const s = `[${stamp()}] ${line}`
  console.log(s)
  logStream.write(s + '\n')
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

let windowDepth = 0
let maxWindowDepth = 0
const windowTitles = []
const stubHits = []
const chatAll = []

function strip(s) {
  return String(s || '').replace(/\u00a7./g, '')
}

function noteStub(text, where) {
  const t = strip(text)
  if (/即将|暂未|敬请期待|coming soon|not yet|未开放|占位/i.test(t)) {
    stubHits.push({ where, text: t })
    log(`[STUB?] ${where}: ${t}`)
  }
}

function attachBot(bot, username) {
  bot.on('message', (m) => {
    const s = m.toString()
    chatAll.push(s)
    log(`[chat] ${s}`)
    noteStub(s, 'chat')
  })
  bot.on('windowOpen', (win) => {
    windowDepth += 1
    if (windowDepth > maxWindowDepth) maxWindowDepth = windowDepth
    let title = ''
    try {
      title = typeof win.title === 'string' ? win.title : (win.title && win.title.toString ? win.title.toString() : JSON.stringify(win.title))
    } catch (e) {
      title = String(win.title)
    }
    const plainTitle = strip(title)
    windowTitles.push(plainTitle)
    log(`[windowOpen] depth=${windowDepth} title="${plainTitle}" raw=${JSON.stringify(title)}`)
    noteStub(plainTitle, 'windowTitle')
    const slots = (win.slots || []).filter(Boolean)
    const names = []
    for (const it of slots) {
      const dn = it.customName || it.displayName || it.name || ''
      const plain = strip(dn)
      if (!plain) continue
      names.push(plain)
      noteStub(plain, `slot:${plain}`)
      // lore if present
      try {
        const nbt = it.nbt
        if (nbt && nbt.value && nbt.value.display) {
          const lore = nbt.value.display.value && nbt.value.display.value.Lore
          if (lore && lore.value && lore.value.value) {
            for (const line of lore.value.value) {
              const ls = strip(typeof line === 'string' ? line : (line.value || line))
              noteStub(ls, `lore:${plain}`)
            }
          }
        }
      } catch (_) {}
    }
    log(`[windowItems] count=${names.length} | ${names.slice(0, 40).join(' | ')}`)
  })
  bot.on('windowClose', () => {
    windowDepth = Math.max(0, windowDepth - 1)
    log(`[windowClose] depth=${windowDepth}`)
  })
}

async function cmd(bot, text, waitMs = 2000) {
  log(`[cmd] ${text}`)
  bot.chat(text)
  await sleep(waitMs)
}

async function closeWin(bot) {
  try {
    if (bot.currentWindow) bot.closeWindow(bot.currentWindow)
  } catch (_) {}
  await sleep(400)
}

async function runPass(username) {
  return new Promise((resolve) => {
    log(`=== experience-pass1 start user=${username} ===`)
    const bot = mineflayer.createBot({
      host: '127.0.0.1',
      port: 25565,
      username,
      auth: 'offline',
      version: '1.12.2'
    })
    attachBot(bot, username)

    let settled = false
    const finish = (code, reason) => {
      if (settled) return
      settled = true
      log(`=== finish code=${code} reason=${reason} maxWindowDepth=${maxWindowDepth} windows=${JSON.stringify(windowTitles)} stubHits=${stubHits.length} ===`)
      try { bot.quit() } catch (_) {}
      setTimeout(() => {
        resolve({ code, reason, username })
      }, 500)
    }

    bot.on('error', (e) => {
      log(`[error] ${e && e.message ? e.message : e}`)
      finish(1, `error:${e && e.message}`)
    })
    bot.on('kicked', (r) => {
      const reason = typeof r === 'string' ? r : JSON.stringify(r)
      log(`[kicked] ${reason}`)
      // name conflict / already online → retry other name
      if (/already|logged|冲突|在线|duplicate|You are already/i.test(reason) || /PickyBot/i.test(reason)) {
        finish(3, `kicked_conflict:${reason}`)
      } else {
        finish(1, `kicked:${reason}`)
      }
    })

    const hardTimeout = setTimeout(() => {
      log('[timeout] 90s hard stop')
      finish(2, 'timeout90s')
    }, 88000)

    bot.once('spawn', async () => {
      try {
        clearTimeout(hardTimeout)
        // soft budget from spawn
        const soft = setTimeout(() => {
          log('[timeout] soft 85s from spawn — wrapping up')
          finish(0, 'soft_timeout_wrap')
        }, 85000)

        await sleep(2500)
        log('[step] spawn welcome observed')

        // 1) hub menu
        await cmd(bot, '/ember', 2800)
        await closeWin(bot)
        await cmd(bot, '/trmenu open ember_hub', 2500)
        // click 日常 if possible
        if (bot.currentWindow) {
          const daily = bot.currentWindow.slots.find((s) => s && /日常|余烬窟|Daily/i.test(strip(s.customName || s.displayName || '')))
          if (daily) {
            log(`[click] daily slot=${daily.slot} name=${strip(daily.customName || daily.displayName || '')}`)
            try { await bot.clickWindow(daily.slot, 0, 0) } catch (e) { log(`[clickErr] ${e.message}`) }
            await sleep(2200)
          }
          const enhance = bot.currentWindow && bot.currentWindow.slots.find((s) => s && /强化/i.test(strip(s.customName || s.displayName || '')))
          if (enhance) {
            log(`[click] enhance slot=${enhance.slot}`)
            try { await bot.clickWindow(enhance.slot, 0, 0) } catch (e) { log(`[clickErr] ${e.message}`) }
            await sleep(2000)
          }
        }
        await closeWin(bot)
        await cmd(bot, '/trmenu open ember_daily', 2200)
        await closeWin(bot)
        await cmd(bot, '/trmenu open ember_enhance', 2200)
        await closeWin(bot)
        await cmd(bot, '/trmenu open ember_coming', 2000)
        await closeWin(bot)

        // 2) covenant / skill
        await cmd(bot, '/corerpg covenant info', 1800)
        await cmd(bot, '/corerpg covenant', 1500)
        // try set if unset (harmless if already set)
        await cmd(bot, '/corerpg covenant set blaze', 1800)
        await cmd(bot, '/corerpg skill info', 1800)
        // spawn a mob then skill
        await cmd(bot, '/mm m spawn EmberDailyZombie 1', 1800)
        // look at nearby
        const mobs = Object.values(bot.entities).filter((e) => e.type === 'mob')
        log(`[info] entities=${Object.keys(bot.entities).length} mobs=${mobs.length}`)
        if (mobs.length) {
          try { await bot.lookAt(mobs[0].position.offset(0, 1, 0)) } catch (_) {}
          await sleep(400)
        }
        await cmd(bot, '/corerpg skill', 1600)
        await cmd(bot, '/corerpg skill', 1200) // CD feedback

        // 3) enhance
        await cmd(bot, '/ni give ' + username + ' gear_ember_blade 1', 1500)
        await cmd(bot, '/ni give ' + username + ' mat_ember_shard 16', 1200)
        await sleep(400)
        const blade = bot.inventory.items().find((i) =>
          /余烬|刃|blade|sword/i.test(strip(i.customName || i.displayName || i.name || ''))
        )
        log(`[inv] items=${bot.inventory.items().length} blade=${blade ? (blade.customName || blade.displayName || blade.name) : 'none'}`)
        if (blade) {
          try { await bot.equip(blade, 'hand'); log('[equip] blade ok') } catch (e) { log(`[equip] ${e.message}`) }
        }
        await cmd(bot, '/corerpg enhance help', 1500)
        await cmd(bot, '/corerpg enhance info', 1500)
        await cmd(bot, '/corerpg enhance', 1600)

        // 4) cash / shop / daily
        await cmd(bot, '/corerpg cash', 1500)
        await cmd(bot, '/corerpg shop', 1500)
        await cmd(bot, '/corerpg shop buy daily_ticket', 1600)
        await cmd(bot, '/ni give ' + username + ' ticket_ember_daily 2', 1400)
        await cmd(bot, '/dp start EmberDaily', 5000)
        // leave if entered
        await cmd(bot, '/dp leave', 2000)

        clearTimeout(soft)
        log('[step] path complete')
        log(`[summary] maxNest=${maxWindowDepth} windows=${windowTitles.join(' > ')} stubs=${stubHits.length}`)
        finish(0, 'ok')
      } catch (e) {
        log(`[fatal] ${e && e.stack ? e.stack : e}`)
        finish(1, `exception:${e && e.message}`)
      }
    })
  })
}

;(async () => {
  let result = await runPass('PickyBot')
  if (result.code === 3) {
    log('=== retry with PickyBot2 ===')
    result = await runPass('PickyBot2')
  }
  try { logStream.end() } catch (_) {}
  process.exit(result.code === 0 ? 0 : result.code)
})()
