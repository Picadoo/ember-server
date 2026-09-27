// Phase-1 (2026-09-27 CST): forge pity end to end on the op bot — T1 blade +7 with 2 gems + Fire Aspect I
// → T2 (+3, gem 2 returned because slot 2 needs +5) → T3 (+1). Checks lore markers, enchants, costs, coin.
const { joinPlay } = require('./lib/proxy-login')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
function lore(it) { try { return it.nbt.value.display.value.Lore.value.value.map(strip) } catch (e) { return [] } }
function niId(it) { for (const p of lore(it)) if (/^[a-z0-9_]+$/.test(p.trim())) return p.trim(); return null }
function ench(it) { try { return (it.nbt.value.ench || it.nbt.value.Enchantments).value.value.map(e => e.id.value + ':' + e.lvl.value).join(',') } catch (e) { return '' } }
const count = (b, id) => b.inventory.items().filter(i => niId(i) === id).reduce((a, i) => a + i.count, 0)
;(async () => {
  const b = await joinPlay('RpgBot', { log: false }); await wait(2500)
  const say = async (c, ms = 900) => { const n = b.chatLog.length; b.chat(c); await wait(ms); return b.chatLog.slice(n).map(strip) }
  const out = {}
  await say('/clear RpgBot')
  await say('/ni give RpgBot gear_ember_t1_blade 1', 1200)
  const held = async () => { const it = b.inventory.items().find(i => /^gear_ember_t\d_blade$/.test(niId(i) || '')); if (it) await b.equip(it, 'hand'); await wait(400); return b.heldItem }
  await held()
  await say('/corerpg enhance set 7')
  await say('/ni give RpgBot gem_ember_sharp 1'); await say('/ni give RpgBot gem_ember_steady 1')
  await say('/corerpg socket insert gem_ember_sharp'); await say('/corerpg socket insert gem_ember_steady')
  await say('/enchant RpgBot 20 1')
  let h = await held(); out.t1 = { id: niId(h), lore: lore(h).filter(l => /强化|孔|#ember/.test(l)), ench: ench(h) }
  out.noMat = (await say('/corerpg forge confirm')).join(' | ')
  await say('/ni give RpgBot mat_ember_core_fragment 10'); await say('/ni give RpgBot mat_ember_shard 40')
  await say('/corerpg coin set RpgBot 0'); await say('/corerpg coin give RpgBot 1600')
  out.preview1 = (await say('/corerpg forge')).join(' | ')
  out.forge1 = (await say('/corerpg forge confirm', 1500)).join(' | ')
  h = await held(); out.t2 = { id: niId(h), lore: lore(h).filter(l => /强化|孔|#ember/.test(l)), ench: ench(h) }
  out.after1 = { frag: count(b, 'mat_ember_core_fragment'), shard: count(b, 'mat_ember_shard'), steadyBack: count(b, 'gem_ember_steady') }
  out.coin1 = (await say('/corerpg coin')).join(' | ')
  await say('/ni give RpgBot core_ember_compact 3'); await say('/ni give RpgBot mat_ember_core_fragment 25'); await say('/ni give RpgBot mat_calamity_ember 6')
  await say('/corerpg coin give RpgBot 5000')
  out.forge2 = (await say('/corerpg forge confirm', 1500)).join(' | ')
  h = await held(); out.t3 = { id: niId(h), lore: lore(h).filter(l => /强化|孔|#ember/.test(l)), ench: ench(h) }
  out.after2 = { compact: count(b, 'core_ember_compact'), frag: count(b, 'mat_ember_core_fragment'), cal: count(b, 'mat_calamity_ember'), sharpBack: count(b, 'gem_ember_sharp') }
  out.coin2 = (await say('/corerpg coin')).join(' | ')
  out.t3forge = (await say('/corerpg forge')).join(' | ')
  console.log('FORGE_RESULT ' + JSON.stringify(out, null, 1))
  b.quit(); await wait(500); process.exit(0)
})().catch(e => { console.log('ERR', e.stack); process.exit(1) })
setTimeout(() => { console.log('TIMEOUT'); process.exit(2) }, 120000)
