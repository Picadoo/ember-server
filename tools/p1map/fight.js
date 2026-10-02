// globals: MAXMS, RADIUS, SLOT, SKIP (substring of names to ignore), STOPIDLE (ms with no target → stop)
const vec = require('vec3')
const isHostile = e => e.type === 'mob' && e.name !== 'armor_stand' && !String(e.metadata[2] || '').includes(SKIP || '###') && (e.metadata[7] === undefined || e.metadata[7] > 0)
const t0 = Date.now(); let lastAtk = 0, hits = 0, idleSince = Date.now(), kills = new Set(), seen = new Set()
const hpLog = []; let minHp = bot.health, drinks = 0, nextDrink = 0
bot.setQuickBarSlot(SLOT)
try {
  while (Date.now() - t0 < MAXMS) {
    if (!bot.entity || bot.health <= 0) { hpLog.push('dead'); break }
    minHp = Math.min(minHp, bot.health)
    if (typeof DRINK !== 'undefined' && DRINK && bot.health < 11 && Date.now() > nextDrink) {
      const pi = bot.inventory.slots.slice(36, 45).findIndex(x => x && x.name === 'potion')
      if (pi >= 0) {
        bot.clearControlStates(); bot.setQuickBarSlot(pi); bot.activateItem(); await wait(1800); bot.deactivateItem()
        bot.setQuickBarSlot(SLOT); drinks++; nextDrink = Date.now() + 15500
      }
    }
    const t = bot.nearestEntity(e => isHostile(e) && e.position.distanceTo(bot.entity.position) < RADIUS)
    if (!t) { bot.clearControlStates(); if (Date.now() - idleSince > STOPIDLE) break; await wait(200); continue }
    idleSince = Date.now(); seen.add(t.id)
    const d = t.position.distanceTo(bot.entity.position)
    await bot.lookAt(t.position.offset(0, (t.height || 1.8) * 0.8, 0), true)
    if (d > 2.6) {
      bot.setControlState('forward', true); bot.setControlState('sprint', d > 5)
      bot.setControlState('jump', !!bot.entity.isCollidedHorizontally)
    } else {
      bot.clearControlStates()
      if (Date.now() - lastAtk > 700) { bot.attack(t); lastAtk = Date.now(); hits++ }
    }
    await wait(100)
  }
} finally { bot.clearControlStates() }
return { ms: Date.now() - t0, hits, drinks, seen: seen.size, hp: +bot.health.toFixed(2), minHp: +minHp.toFixed(2), pos: bot.entity && bot.entity.position.floored(), left: Object.values(bot.entities).filter(isHostile).map(e => [e.metadata[2], e.position.floored().toString(), e.metadata[7]]) }
