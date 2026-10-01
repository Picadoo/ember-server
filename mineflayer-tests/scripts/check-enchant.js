/**
 * Mineflayer check: /coreenchant check (console) + catalyst NI give to the bot.
 * Needs the play-server console FIFO (README「控制台 FIFO」). Bot: MC_USER (default EmberTestOp), not op.
 */
const { runCheck } = require('./helpers/check-common')
runCheck({
  title: 'enchant',
  checks: [
    { cmd: 'coreenchant check', expect: [/catalyst_ni_id=crystal_ember_enchant/, /hasCatalystOverride=true/, /EnchantNmsHooks\.active=true/, /registeredTables=8/, /table=ember_blade offers=3/, /cost=1/, /cost=2/, /cost=3/] }
  ],
  gives: [{ id: 'crystal_ember_enchant', cn: '余烬附魔晶' }]
}).catch((e) => { console.error(e); process.exit(1) })
