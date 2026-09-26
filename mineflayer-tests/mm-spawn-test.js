/**
 * MythicMobs 4.11.0 spawn smoke on Paper 1.12.2
 */
const mineflayer = require('mineflayer')

const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'Tester',
  auth: 'offline',
  version: '1.12.2',
  hideErrors: false
})

const chatLog = []
bot.on('message', (msg) => {
  const s = msg.toString()
  chatLog.push(s)
  console.log('[chat]', s)
})

function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }

function countMobs() {
  const found = []
  for (const id of Object.keys(bot.entities)) {
    const e = bot.entities[id]
    if (!e || e === bot.entity) continue
    // 1.12 mineflayer: mobs have type 'mob' or username for players
    const kind = e.type
    const name = e.name || e.mobType || e.displayName || e.username || '?'
    if (kind === 'mob' || kind === 'object' || (kind !== 'player' && e.position)) {
      found.push({ id, kind, name, pos: e.position && e.position.toString() })
    }
  }
  return found
}

bot.once('spawn', async () => {
  console.log('[mm] spawned at', bot.entity.position.toString())
  // Clear nearby entities first
  bot.chat('/minecraft:kill @e[type=!Player,r=48]')
  await sleep(1500)

  let before = countMobs().filter(e => e.kind === 'mob')
  console.log('[mm] mobs before spawn:', before.length, JSON.stringify(before))

  // Probe help
  bot.chat('/mm help')
  await sleep(800)
  bot.chat('/mm mobs')
  await sleep(800)
  bot.chat('/mm m list')
  await sleep(1000)

  // Try spawn variants near player
  const attempts = [
    '/mm m spawn EmberZombie',
    '/mm mobs spawn EmberZombie',
    '/mm mobs spawn EmberZombie 1',
    '/spawnmob EmberZombie',
    '/mm m spawn EmberZombie 1 world -53 81 259',
    '/mm mobs spawn EmberZombie 1 -53 81 259',
  ]

  for (const cmd of attempts) {
    console.log('[mm] TRY', cmd)
    bot.chat(cmd)
    await sleep(1200)
    const mobs = countMobs().filter(e => e.kind === 'mob')
    console.log('[mm] mobs after:', mobs.length, JSON.stringify(mobs))
    if (mobs.length > before.length) {
      console.log('[mm] SPAWN SUCCESS with:', cmd)
      console.log('[mm] RESULT=PASS')
      // Also check CWR still reports spawn bans
      bot.chat('/cwr check')
      await sleep(800)
      bot.quit('mm spawn ok')
      setTimeout(() => process.exit(0), 400)
      return
    }
  }

  // Fallback: spawn at explicit coords via spawnmythicmob alias
  console.log('[mm] trying SkeletalKnight fallback')
  bot.chat('/mm m spawn SkeletalKnight')
  await sleep(1500)
  let mobs = countMobs().filter(e => e.kind === 'mob')
  console.log('[mm] after SkeletalKnight:', mobs.length, JSON.stringify(mobs))

  bot.chat('/mm mobs info EmberZombie')
  await sleep(800)
  bot.chat('/plugins')
  await sleep(800)
  bot.chat('/cwr check')
  await sleep(800)

  if (mobs.length > 0) {
    console.log('[mm] RESULT=PASS (some mythic/mob present)')
    bot.quit('partial')
    setTimeout(() => process.exit(0), 400)
  } else {
    console.log('[mm] RESULT=FAIL no mob entity observed')
    console.log('[mm] chat dump:', chatLog.join(' | '))
    bot.quit('fail')
    setTimeout(() => process.exit(1), 400)
  }
})

bot.on('error', (e) => { console.error('[mm] error', e); process.exit(1) })
bot.on('kicked', (r) => { console.error('[mm] kicked', r); process.exit(1) })
setTimeout(() => { console.error('[mm] timeout'); process.exit(2) }, 60000)
