/**
 * Mineflayer check: /coresmelt check (console). Needs the play-server console FIFO (README「控制台 FIFO」).
 */
const { runCheck } = require('./helpers/check-common')
runCheck({
  title: 'furnace',
  checks: [
    { cmd: 'coresmelt check', expect: [/FurnaceNmsHooks\.active=true ruleCount=6/, /ore_ember_iron -> ingot_ember_iron OK/, /fish_ember_cod -> food_ember_grilled_fish OK/] }
  ],
  gives: [{ id: 'ore_ember_iron', cn: '余烬铁矿' }]
}).catch((e) => { console.error(e); process.exit(1) })
