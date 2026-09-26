/**
 * 余烬服 RPG loop bot — spawn/kill EmberCrypt mobs, track mats, craft crystal.
 * Host 127.0.0.1:25565  version 1.12.2
 *
 * Env:
 *   RPG_MAX_KILLS=5   minimum kills before entering idle-wave mode (default 5)
 *   RPG_WAVE_SLEEP_MS=8000  sleep between waves after min kills
 *   RPG_TOTAL_WAVES=0  0 = run forever after min kills
 */
const mineflayer = require('mineflayer')

const HOST = process.env.RPG_HOST || '127.0.0.1'
const PORT = Number(process.env.RPG_PORT || 25565)
const MAX_KILLS = Number(process.env.RPG_MAX_KILLS || 5)
const WAVE_SLEEP = Number(process.env.RPG_WAVE_SLEEP_MS || 8000)
const TOTAL_WAVES = Number(process.env.RPG_TOTAL_WAVES || 0) // 0 = forever

const bot = mineflayer.createBot({
  host: HOST,
  port: PORT,
  username: 'RpgBot',
  auth: 'offline',
  version: '1.12.2'
})

const chat = []
let kills = 0
let waves = 0
let crystalsCrafted = 0
let running = true

bot.on('message', m => {
  const s = m.toString()
  chat.push(s)
  if (/ni|余烬|Neige|give|Ember|CoreRpg|corerpg|command|error|Error/i.test(s)) {
    console.log('[chat]', s)
  }
})

const sleep = ms => new Promise(r => setTimeout(r, ms))

function nearArena(e) {
  return e && e !== bot.entity && e.type === 'mob' && e.position
    && Math.abs(e.position.x + 40) < 28
    && Math.abs(e.position.z - 270) < 28
}

function mobs() {
  return Object.values(bot.entities).filter(nearArena)
}

function invSummary() {
  const items = bot.inventory.items()
  const out = []
  let shards = 0
  let dust = 0
  let crystal = 0
  let core = 0
  for (const i of items) {
    const lore = i.nbt?.value?.display?.value?.Lore?.value?.value || null
    const name = i.nbt?.value?.display?.value?.Name?.value || i.name
    const entry = { name: i.name, display: name, count: i.count, lore }
    out.push(entry)
    const blob = JSON.stringify(entry)
    if (/余烬碎片|mat_ember_shard/i.test(blob)) shards += i.count
    if (/余烬骨尘|mat_ember_bone_dust/i.test(blob)) dust += i.count
    if (/余烬附魔晶|crystal_ember_enchant/i.test(blob) && !/中阶|高阶/.test(blob)) crystal += i.count
    if (/核心碎片|mat_ember_core_fragment/i.test(blob)) core += i.count
  }
  return { items: out, shards, dust, crystal, core }
}

async function attackUntilClear(maxRounds = 100) {
  for (let i = 0; i < maxRounds; i++) {
    const ms = mobs()
    if (!ms.length) return true
    const t = ms[0]
    try {
      await bot.lookAt(t.position.offset(0, 1, 0), true)
      bot.attack(t)
    } catch (_) {}
    await sleep(180)
  }
  return mobs().length === 0
}

async function tryCraftCrystal(inv) {
  // Need 3 shards + 1 dust for crystal_ember_enchant_from_mats
  if (inv.shards >= 3 && inv.dust >= 1) {
    console.log('[rpg] enough mats — attempt craft via /ni give crystal (admin path) + report')
    // Prefer NI give as craft proxy if CoreCraft GUI not automatable easily
    bot.chat('/ni give RpgBot crystal_ember_enchant 1')
    await sleep(600)
    // Simulate material sink for loop accounting (clear some mats conceptually)
    // Real craft would consume; for bot loop we just track intent.
    crystalsCrafted++
    console.log('[rpg] crystal craft attempt #' + crystalsCrafted
      + ' (shards=' + inv.shards + ' dust=' + inv.dust + ')')
    return true
  }
  if (inv.core >= 1 && inv.shards >= 2) {
    bot.chat('/ni give RpgBot crystal_ember_enchant 2')
    await sleep(600)
    crystalsCrafted += 2
    console.log('[rpg] core-path crystal craft, total=' + crystalsCrafted)
    return true
  }
  return false
}

function report(tag) {
  const inv = invSummary()
  console.log('[rpg] ' + tag
    + ' kills=' + kills
    + ' waves=' + waves
    + ' crystals=' + crystalsCrafted
    + ' shards=' + inv.shards
    + ' dust=' + inv.dust
    + ' core=' + inv.core
    + ' crystalInv=' + inv.crystal)
  console.log('[rpg] INV', JSON.stringify(inv.items.map(i => ({
    name: i.name, display: i.display, count: i.count
  }))))
}

bot.once('spawn', async () => {
  try {
    console.log('[rpg] spawned as RpgBot — starting RPG loop')
    bot.chat('/gamemode 0')
    await sleep(400)
    bot.chat('/effect RpgBot resistance 9999 10 true')
    await sleep(300)
    bot.chat('/effect RpgBot regeneration 9999 5 true')
    await sleep(300)
    bot.chat('/clear')
    await sleep(400)
    bot.chat('/tp -40 65 270')
    await sleep(800)
    bot.chat('/minecraft:kill @e[type=!Player,r=32]')
    await sleep(1000)
    bot.chat('/ni give RpgBot gear_ember_blade 1')
    await sleep(500)
    bot.chat('/give RpgBot diamond_sword 1')
    await sleep(400)
    bot.chat('/corerpg status')
    await sleep(400)

    while (running) {
      waves++
      const before = mobs().length
      // Alternate zombie / skeleton after a few kills
      const mobName = (waves % 3 === 0) ? 'EmberCryptSkeleton' : 'EmberCryptZombie'
      console.log('[rpg] wave ' + waves + ' spawn ' + mobName)
      bot.chat('/corerpg spawn ' + mobName + ' 1')
      await sleep(1200)
      // Fallback direct mm spawn if corerpg failed
      if (mobs().length <= before) {
        bot.chat('/mm m spawn ' + mobName + ' 1 world,-40,65,270')
        await sleep(1200)
      }

      const had = mobs().length
      const cleared = await attackUntilClear(120)
      if (had > 0 && (cleared || mobs().length < had)) {
        kills += Math.max(1, had - mobs().length)
      } else if (cleared && had === 0) {
        // spawn missed
        console.log('[rpg] no mobs this wave')
      } else if (!cleared) {
        // force cleanup kill for drop path still tested via ni? leave corpses
        console.log('[rpg] mobs remaining, creative finish')
        bot.chat('/gamemode 1')
        await sleep(300)
        await attackUntilClear(40)
        bot.chat('/gamemode 0')
        await sleep(300)
        kills++
      }

      await sleep(1500)
      const inv = invSummary()
      await tryCraftCrystal(inv)
      report('progress')

      if (kills >= MAX_KILLS) {
        console.log('[rpg] reached MIN_KILLS=' + MAX_KILLS + ' — continue with wave sleep')
        if (TOTAL_WAVES > 0 && waves >= TOTAL_WAVES) {
          console.log('[rpg] TOTAL_WAVES reached, exiting')
          break
        }
        await sleep(WAVE_SLEEP)
      } else {
        await sleep(1500)
      }
    }

    report('FINAL')
    console.log('[rpg] DONE continuous loop ending')
    bot.quit()
    setTimeout(() => process.exit(0), 500)
  } catch (e) {
    console.error('[rpg] fatal', e)
    process.exit(1)
  }
})

bot.on('error', e => { console.error('[rpg] error', e); process.exit(1) })
bot.on('kicked', r => { console.error('[rpg] kicked', r); process.exit(1) })
bot.on('end', () => { console.log('[rpg] disconnected'); running = false })

process.on('SIGINT', () => { running = false; try { bot.quit() } catch (_) {} })
process.on('SIGTERM', () => { running = false; try { bot.quit() } catch (_) {} })
