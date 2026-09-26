// CoreRpg 1.4.9 guild boss refund path. Run while EmberGuildBoss/option.yml carries a temporary always-false
// start condition (REFUND-TEST) → /corerpg guild boss must refund contribution + weekly use.
// PHASE=fail  : set contribution to 20, run guild boss, expect 启动失败（已退还…）, contribution back to 20
// PHASE=retry : (after the condition is removed + /dp reload) run guild boss again → must start (weekly use was refunded)
const mineflayer = require('mineflayer')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const OP_BOT = process.env.OP_BOT || 'RpgBot', L_NAME = process.env.L || 'GbR926', PHASE = process.env.PHASE || 'fail'
function mk(name) {
  const b = mineflayer.createBot({ host: '127.0.0.1', port: 25565, username: name, version: '1.12.2', auth: 'offline' })
  b.chatLog = []; b.on('message', m => { const s = m.toString(); b.chatLog.push(s); console.log(`[${name}]`, s) })
  b.on('error', e => console.log(`[${name}] ERR`, e.message)); return new Promise(res => b.once('spawn', () => res(b)))
}
const since = (b, n) => b.chatLog.slice(n).join(' | ')
async function contrib(bot) { const n = bot.chatLog.length; bot.chat('/corerpg guild info'); await wait(1200); const m = strip(since(bot, n)).match(/我的贡献\s*(\d+)/); return m ? Number(m[1]) : null }
;(async () => {
  const r = { phase: PHASE }
  const op = await mk(OP_BOT); await wait(1000); const L = await mk(L_NAME); await wait(1500)
  const c = async (s, ms) => { op.chat(s); await wait(ms || 900) }
  if (PHASE === 'fail') {
    await c(`/deop ${L_NAME}`); await c(`/mvtp ${L_NAME} world`, 1500)
    await c(`/corerpg guild invite ${L_NAME}`, 1200); L.chat('/corerpg guild accept'); await wait(1500)
    const need = 20 - ((await contrib(L)) || 0)
    if (need > 0) { await c(`/ni give ${L_NAME} mat_ember_shard ${need * 10}`, 1500); L.chat(`/corerpg guild donate mat_ember_shard ${need * 10}`); await wait(1500) }
  }
  if (PHASE === 'retry') { await c(`/effect ${L_NAME} resistance 120 4 true`); await c(`/effect ${L_NAME} regeneration 120 4 true`) }
  r.contrib_before = await contrib(L)
  L.chat('/dungeon-team disband'); await wait(500); L.chat('/dungeon-team create'); await wait(900)
  const n = L.chatLog.length; L.chat('/corerpg guild boss'); await wait(6000)
  r.chat = since(L, n)
  r.contrib_after = await contrib(L)
  if (PHASE === 'fail') {
    r.check_refund = /启动失败（已退还贡献与次数）/.test(r.chat) && !/已点燃/.test(r.chat) && r.contrib_before === 20 && r.contrib_after === 20 ? 'PASS' : 'FAIL'
  } else {
    r.check_weekly_refunded = /本周 1\/1/.test(r.chat) && /已点燃/.test(r.chat) && r.contrib_after === r.contrib_before - 20 ? 'PASS' : 'FAIL'
    await wait(1500); L.chat('/dp leave'); await wait(3000); L.chat('/dungeon-team disband'); await wait(500)
  }
  console.log('REFUND_RESULT', JSON.stringify(r, null, 2)); await wait(300); process.exit(0)
})().catch(e => { console.error(e); process.exit(1) })
