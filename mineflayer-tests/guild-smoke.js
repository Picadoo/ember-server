/**
 * CoreRpg guild / alliance (盟约) smoke — docs/design/DESIGN-ember-guild-ladder.md §1
 * Expect: /corerpg guild|alliance · create · info · donate · invite · leave · disband
 * Do NOT run until plugins/CoreRpg.jar contains GuildService (or equiv) + guild wiring.
 * No jar overwrite from this script.
 */
const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'RpgBot',
  version: '1.12.2',
  auth: 'offline'
})
bot.on('message', m => console.log('[chat]', m.toString()))
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(1200)
}
bot.once('spawn', async () => {
  await wait(2500)

  // --- guild root + lifecycle (DESIGN §1 + expected invite/leave/disband) ---
  await chat('/corerpg guild')
  await chat('/corerpg guild create 余烬测')
  await chat('/corerpg guild info')
  await chat('/corerpg guild donate mat_ember_shard 10')
  await chat('/corerpg guild invite SmokeMate')
  await chat('/corerpg guild leave')
  // recreate so disband has a target if leave succeeded
  await chat('/corerpg guild create 余烬测')
  await chat('/corerpg guild disband')

  // --- alliance alias (same command surface) ---
  await chat('/corerpg alliance')
  await chat('/corerpg alliance create 余烬测')
  await chat('/corerpg alliance info')
  await chat('/corerpg alliance donate mat_ember_bone_dust 5')
  await chat('/corerpg alliance invite SmokeMate')
  await chat('/corerpg alliance leave')
  await chat('/corerpg alliance create 余烬测')
  await chat('/corerpg alliance disband')

  console.log('GUILD_SMOKE_DONE')
  process.exit(0)
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 120000)
