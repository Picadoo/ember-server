// Local test daemon: keeps mineflayer bots connected and runs JS against them.
// GET /join?name=X · POST /eval?name=X (body = async JS, `bot`, `bots`, `wait`, `con` in scope) · GET /log?name=X&from=N
const http = require('http')
const { joinPlay } = require('../lib/proxy-login')
const con = require('../lib/console')
const wait = ms => new Promise(r => setTimeout(r, ms))
const bots = {}
const AsyncFunction = Object.getPrototypeOf(async function () {}).constructor
function out (res, code, obj) { res.writeHead(code, { 'content-type': 'application/json' }); res.end(JSON.stringify(obj, null, 1)) }
http.createServer(async (req, res) => {
  const u = new URL(req.url, 'http://x'); const name = u.searchParams.get('name')
  let body = ''; req.on('data', c => body += c); await new Promise(r => req.on('end', r))
  try {
    if (u.pathname === '/join') {
      if (bots[name] && bots[name].entity) return out(res, 200, { ok: true, already: true })
      const b = await joinPlay(name, { log: false, timeout: 60000 })
      b.on('end', () => { console.log('END', name); delete bots[name] })
      b.on('kicked', r => b.chatLog.push('KICKED ' + r))
      bots[name] = b; return out(res, 200, { ok: true })
    }
    if (u.pathname === '/quit') { if (bots[name]) { bots[name].quit(); delete bots[name] } return out(res, 200, { ok: true }) }
    if (u.pathname === '/log') { const b = bots[name]; if (!b) return out(res, 404, { err: 'no bot' })
      const from = Number(u.searchParams.get('from') || 0); return out(res, 200, { n: b.chatLog.length, lines: b.chatLog.slice(from).map(s => s.replace(/§./g, '')) }) }
    if (u.pathname === '/eval') {
      const b = bots[name]; const f = new AsyncFunction('bot', 'bots', 'wait', 'con', 'require', body)
      const r = await f(b, bots, wait, con, require); return out(res, 200, { r })
    }
    if (u.pathname === '/list') return out(res, 200, Object.keys(bots))
    out(res, 404, {})
  } catch (e) { out(res, 500, { err: String(e && e.stack || e) }) }
}).listen(Number(process.env.BOTD_PORT || 8765), '127.0.0.1', () => console.log('botd up'))
process.on('unhandledRejection', e => console.log('UNHANDLED', e))
