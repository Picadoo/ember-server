// Login-server idle check: join via the proxy, stay unauthenticated for IDLE_MS (default 30s) on the
// void login world, then /login (or /register) and require landing on the play server.
// Regression for the "Flying is not enabled on this server" kick after ~4s (AuthMe freezes the player
// mid-air in the void login world → vanilla fly check) and for unauthenticated players falling into
// the void. Fix: login-runtime allow-flight=true + stone platform at the login spawn (8,63,8) +
// AuthMe spawn.yml / teleportUnAuthedToSpawn.
const mineflayer = require('mineflayer')
const { pwFor } = require('./lib/proxy-login')
// AuthMe restrictions.timeout is 30s (kicks unauthenticated players by design), so idle just under it
const IDLE_MS = Number(process.env.IDLE_MS || 28000)
const name = process.env.MC_USER || 'EmberTestOp'
const wait = (ms) => new Promise((r) => setTimeout(r, ms))
const bot = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: name, version: '1.12.2', auth: 'offline' })
const chat = []
let kicked = null
bot.on('message', (m) => chat.push(m.toString()))
bot.on('kicked', (r) => { kicked = String(r) })
bot.on('error', (e) => { kicked = kicked || 'error ' + e.message })
const done = (ok, msg) => { console.log(`[idle] ${ok ? 'PASS' : 'FAIL'} — ${msg}`); try { bot.quit() } catch (_) {} setTimeout(() => process.exit(ok ? 0 : 2), 300) }
bot.once('spawn', async () => {
  const t0 = Date.now()
  console.log(`[idle] on login server at ${bot.entity.position}; idling ${IDLE_MS / 1000}s unauthenticated…`)
  while (Date.now() - t0 < IDLE_MS) { if (kicked) return done(false, `kicked after ${((Date.now() - t0) / 1000).toFixed(1)}s: ${kicked}`); await wait(250) }
  const idlePos = bot.entity.position.clone()
  console.log(`[idle] after ${IDLE_MS / 1000}s still connected at ${idlePos} onGround=${bot.entity.onGround}`)
  if (idlePos.y < 60) return done(false, `fell into the void (y=${idlePos.y.toFixed(1)}) — spawn platform missing`)
  const pw = pwFor(name)
  const all = chat.join(' | ')
  const landed = new Promise((res) => bot.once('respawn', res))
  if (/register to the server/i.test(all)) bot.chat(`/register ${pw} ${pw}`); else bot.chat(`/login ${pw}`)
  const r = await Promise.race([landed.then(() => 'play'), wait(15000).then(() => 'timeout')])
  if (r !== 'play') return done(false, `no transfer to play after login (kicked=${kicked})`)
  await wait(2500)
  done(!kicked, `survived ${IDLE_MS / 1000}s unauthenticated, then logged in and reached play (dimension=${bot.game.dimension}, pos=${bot.entity.position})`)
})
setTimeout(() => done(false, 'overall timeout'), IDLE_MS + 60000)
