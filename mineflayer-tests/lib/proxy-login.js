// Connect a bot the way a real player does since the 2026-09-26 proxy change:
// proxy :25565 → login server (AuthMe /register or /login) → AuthMe sends us to "play".
// Bot passwords live in /workspace/minecraft/secrets/bot-passwords.json (gitignored, generated on first use).
// usage: const { joinPlay } = require('./lib/proxy-login'); const bot = await joinPlay('RpgBot')
const mineflayer = require('mineflayer')
const crypto = require('crypto')
const fs = require('fs')
const PW_FILE = '/workspace/minecraft/secrets/bot-passwords.json'
const wait = ms => new Promise(r => setTimeout(r, ms))
function pwFor(name) {
  let d = {}; try { d = JSON.parse(fs.readFileSync(PW_FILE, 'utf8')) } catch (e) {}
  if (!d[name]) { d[name] = crypto.randomBytes(8).toString('hex'); fs.writeFileSync(PW_FILE, JSON.stringify(d, null, 1), { mode: 0o600 }) }
  return d[name]
}
function joinPlay(name, opts = {}) {
  const host = opts.host || '127.0.0.1'; const port = opts.port || 25565
  const b = mineflayer.createBot({ host, port, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = b.chatLog || []
  b.on('message', m => { const s = m.toString(); b.chatLog.push(s); if (opts.log !== false) console.log(`[${name}]`, s) })
  b.on('kicked', r => console.log(`[${name}] KICKED`, r)); b.on('error', e => console.log(`[${name}] ERR`, e.message))
  return new Promise((res, rej) => {
    const to = setTimeout(() => rej(new Error('joinPlay timeout (auth or transfer to play failed)')), opts.timeout || 45000)
    b.once('end', () => { clearTimeout(to); rej(new Error('ended before reaching play')) })
    b.once('spawn', async () => {
      const pw = pwFor(name)
      const all = () => b.chatLog.join(' | ')
      for (let i = 0; i < 50 && !/register to the server|login with the command/i.test(all()); i++) await wait(200)
      b.once('respawn', async () => { await wait(opts.settle || 2500); clearTimeout(to); b.removeAllListeners('end'); b.authLog = all(); res(b) })
      let n = b.chatLog.length
      if (/register to the server/i.test(all())) b.chat(`/register ${pw} ${pw}`)
      else b.chat(`/login ${pw}`)
      await wait(3000)
      let tail = b.chatLog.slice(n).join(' | ')
      if (/isn't registered/i.test(tail)) { b.chat(`/register ${pw} ${pw}`); await wait(3000); tail = b.chatLog.slice(n).join(' | ') }
      if (/wrong password|already have registered|temporarily banned/i.test(tail)) console.log(`[${name}] auth problem: ${tail}`)
    })
  })
}
// Since CoreRpg 1.7.0 dungeon entry is level-gated (daily 10 · weekly 20 · abyss 25 · calamity 30 · raid 35).
// ensureLevel: op bot pushes a non-op test bot to >= lvl with `/corerpg progress <p> raid_clear` (+250 ember XP each).
async function ensureLevel(op, bot, lvl) {
  const cur = async () => { const n = bot.chatLog.length; bot.chat('/corerpg level'); await wait(1200)
    const m = bot.chatLog.slice(n).join(' ').replace(/§./g, '').match(/等级 Lv\.(\d+)/); return m ? Number(m[1]) : 0 }
  let l = await cur()
  for (let i = 0; i < 40 && l < lvl; i++) { op.chat(`/corerpg progress ${bot.username} raid_clear`); await wait(700); if (i % 4 === 3) l = await cur() }
  l = await cur(); console.log(`[ensureLevel] ${bot.username} Lv.${l} (want ${lvl})`); return l
}
module.exports = { joinPlay, pwFor, ensureLevel }
