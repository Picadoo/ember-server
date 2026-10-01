/**
 * Mineflayer check: /corecraft /corefish /corecombat (console) + sample NI gives to the bot.
 * Needs the play-server console FIFO (README「控制台 FIFO」). Bot: MC_USER (default EmberTestOp), not op.
 */
const { runCheck } = require('./helpers/check-common')
runCheck({
  title: 'craft/fish/combat',
  checks: [
    { cmd: 'corecraft check', expect: [/NI_registered=5/, /CraftNmsHooks\.active=true/, /plate_ember_iron \(shaped\) -> plate_ember_iron OK/, /crystal_ember_enchant_from_core \(shapeless\) -> crystal_ember_enchant OK/] },
    { cmd: 'corefish check', expect: [/FishNmsHooks\.active=true overrideLoot=true/, /tableEntryCount=7/, /fish_ember_cod\(\d+ OK\)/, /treasure_ember_relic\(\d+ OK\)/] },
    { cmd: 'corecombat check', expect: [/TotemNmsHooks\.active=true/, /ShieldNmsHooks\.active=true/, /totem totem_ember_life OK/, /shield shield_ember_guard OK/] }
  ],
  gives: [
    { id: 'totem_ember_life', cn: '余烬续命图腾' },
    { id: 'shield_ember_guard', cn: '余烬守护盾' },
    { id: 'fish_ember_cod', cn: '余烬鳕鱼' },
    { id: 'plate_ember_iron', cn: '余烬铁板' }
  ]
}).catch((e) => { console.error(e); process.exit(1) })
