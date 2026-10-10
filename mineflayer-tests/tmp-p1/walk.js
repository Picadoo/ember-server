// globals: PTS = [[x,z],...] ; walks to each point with forward only (jump disabled), logs y profile
const out = []; bot.clearControlStates()
for (const [x, z] of PTS) {
  const t0 = Date.now(); let stuck = 0, last = bot.entity.position.clone()
  while (Date.now() - t0 < 20000) {
    const p = bot.entity.position
    const dx = x + 0.5 - p.x, dz = z + 0.5 - p.z
    if (Math.hypot(dx, dz) < 0.8) break
    await bot.look(Math.atan2(-dx, -dz), 0, true)
    bot.setControlState('forward', true); bot.setControlState('jump', false)
    await wait(250)
    if (bot.entity.position.distanceTo(last) < 0.05) { if (++stuck > 12) break } else stuck = 0
    last = bot.entity.position.clone()
  }
  bot.clearControlStates()
  const p = bot.entity.position
  out.push([x, z, +p.x.toFixed(1), +p.y.toFixed(2), +p.z.toFixed(1), stuck > 12 ? 'STUCK' : 'ok'])
}
return out
