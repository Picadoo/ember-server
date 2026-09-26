// Proxy security smoke (2026-09-26): proxy :25565 → login (AuthMe) → play.
// 1) registered bot reaches play only after /login; 2) direct backend connections fail;
// 3) an unauthenticated bot can't reach play or run commands; 4) offline UUIDs unchanged.
const mineflayer = require('mineflayer'); const os = require('os'); const crypto = require('crypto')
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
function offlineUuid(name) { const h = crypto.createHash('md5').update('OfflinePlayer:' + name).digest(); h[6] = h[6] & 0x0f | 0x30; h[8] = h[8] & 0x3f | 0x80; const x = h.toString('hex'); return `${x.slice(0, 8)}-${x.slice(8, 12)}-${x.slice(12, 16)}-${x.slice(16, 20)}-${x.slice(20)}` }
function tryConnect(host, port, name) {
  return new Promise(res => {
    const b = mineflayer.createBot({ host, port, username: name, version: '1.12.2', auth: 'offline' })
    const done = v => { try { b.quit() } catch (e) {} res(v) }
    b.once('spawn', () => done('SPAWNED')); b.once('kicked', r => done('KICKED ' + String(r).slice(0, 160)))
    b.once('error', e => done('ERROR ' + e.message)); setTimeout(() => done('TIMEOUT'), 12000)
  })
}
;(async () => {
  const r = {}
  const extIp = Object.values(os.networkInterfaces()).flat().find(i => i.family === 'IPv4' && !i.internal)?.address
  r.ext_ip = extIp
  for (const p of [25566, 25567]) {
    r['direct_ext_' + p] = extIp ? await tryConnect(extIp, p, 'DirectX' + p) : 'no-ext-ip'
    r['direct_local_' + p] = await tryConnect('127.0.0.1', p, 'DirectL' + p)
  }
  r.check_direct = Object.entries(r).filter(([k]) => k.startsWith('direct_')).every(([, v]) => v !== 'SPAWNED') ? 'PASS' : 'FAIL'
  // unauthenticated bot: stays on login, commands blocked
  const u = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: 'NoAuth' + Math.floor(Math.random() * 9000 + 1000), version: '1.12.2', auth: 'offline' })
  u.log = []; u.on('message', m => u.log.push(m.toString())); let respawns = 0; u.on('respawn', () => respawns++)
  await new Promise(res => u.once('spawn', res)); await wait(2500)
  const cmds = ['/server play', '/mvtp ember_hub', '/ember', '/corerpg sign', '/dp start EmberDaily', '/spawn', '/op NoAuth', '/send NoAuth play']
  const out = {}
  for (const c of cmds) { const n = u.log.length; u.chat(c); await wait(1200); out[c] = u.log.slice(n).join(' | ').slice(0, 120) }
  r.noauth_cmds = out; r.noauth_respawns = respawns; r.noauth_window = !!u.currentWindow
  r.check_noauth = respawns === 0 && !u.currentWindow && Object.values(out).every(v => !/已|成功|Teleport|传送|Connecting/i.test(v)) ? 'PASS' : 'FAIL'
  u.quit(); await wait(1000)
  // authed bot reaches play; UUID = offline UUID
  const name = process.env.BOT || 'ProxyT1'
  const b = await joinPlay(name, { log: false })
  r.authed_reached_play = /Successful login|Successfully registered/.test(b.authLog)
  r.authed_uuid = b.player && b.player.uuid; r.offline_uuid = offlineUuid(name)
  b.chat('/ember'); await wait(2500); r.authed_menu = b.currentWindow ? String(b.currentWindow.title).replace(/§./g, '') : null
  r.check_authed = r.authed_reached_play && r.authed_menu && r.authed_uuid === r.offline_uuid ? 'PASS' : 'FAIL'
  b.quit()
  console.log('PROXY_RESULT', JSON.stringify(r, null, 2)); process.exit(0)
})().catch(e => { console.error('FATAL', e); process.exit(1) })
