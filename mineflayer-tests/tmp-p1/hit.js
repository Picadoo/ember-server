const n = HITS, sub = TGT, gap = GAP
const t = bot.nearestEntity(e => e.type === 'mob' && String(e.metadata[2] || e.name).includes(sub))
if (!t) return 'no target'
bot.setQuickBarSlot(SLOT)
await wait(300)
const hp = () => t.metadata[7]
const out = []
for (let i = 0; i < n; i++) {
  await bot.lookAt(t.position.offset(0, 1.2, 0), true)
  await wait(gap)
  const before = hp(); bot.attack(t); await wait(400)
  out.push(Math.round((before - hp())*100)/100)
}
return { name: t.metadata[2], dist: +bot.entity.position.distanceTo(t.position).toFixed(2), hp: hp(), out, held: bot.heldItem && bot.heldItem.name }
