// fight-kite.js — botd eval body: fight the current room / boss like a player would (Ember real-gear playtest).
// Globals (set by fight-kite.sh): MAXMS, SLOT, RADIUS, STOPIDLE, SKIP (name substring to ignore), DRINK (1 = drink a hotbar
// heal potion below 55 % max HP, 15.5 s apart), MOVE ('stand' | 'kite'), DOOR ([x,z] room entry or null),
// ROOM ([x0,z0,x1,z1] room / boss-hall box or null), REACT (ms before reacting to a telegraph), MISS (0..1 chance to
// not react to one telegraph), HOLDMS (kite: wait at the doorway this long for the melee to come), REACH (swing distance,
// centre to centre, default 3.0).
//  stand = the 2026-10-04 behaviour: run at the nearest mob, stand and swing every 0.7 s (no movement while in reach).
//  kite  = a moderately competent player, nothing frame-perfect:
//    · pulls the room: steps back to the doorway after triggering it and lets the melee come (HOLDMS);
//    · ≥3 melee within 4 blocks → back-pedals ~2 s away from the pack (bias toward the doorway, never through walls /
//      off ledges / out of the room box), still swinging at whatever is in reach; then fights ~0.8 s before the next one;
//    · strafes left/right (random 0.9–1.6 s) while swinging, steps back when a mob is closer than 1.6;
//    · goes for ranged / casters when no melee is close; ignores the skippable treasure rabbit;
//    · drinks at 55 % while back-pedalling (no swings during the 1.8 s drink);
//    · boss telegraphs: reads the run chat line (蓄力 / 第二段 …), after REACT ms moves out of the announced shape
//      (circle → out of radius, band / line / charge → sidestep, preferring the left side, cone → back off,
//      locked circle → sidestep); MISS of them are ignored (did not notice);
//    · caster lines: when a caster within 7 blocks starts its wind-up (it is slowed 10 levels), sidestep 2–3 blocks
//      (same REACT / MISS).  NOTE 2026-10-04: never fired — Paper 1.12 does not send a mob's potion effect added after
//      spawn to clients, so the playtest bots did NOT dodge caster lines (they only see the flame particles a human sees).
const MODE = (typeof MOVE !== 'undefined' && MOVE) || 'stand'
const RX = typeof REACT !== 'undefined' ? REACT : 350, MISSP = typeof MISS !== 'undefined' ? MISS : 0.15
const HOLD = typeof HOLDMS !== 'undefined' ? HOLDMS : 6000
const RCH = typeof REACH !== 'undefined' ? REACH : 3.0
const DR = typeof DRINK !== 'undefined' && DRINK
const D = typeof DOOR !== 'undefined' ? DOOR : null, BOX = typeof ROOM !== 'undefined' ? ROOM : null
const strip = s => String(s || '').replace(/\u00a7./g, '')
const isHostile = e => e.type === 'mob' && e.name !== 'armor_stand' && !String(e.metadata[2] || '').includes(SKIP || '###') && (e.metadata[7] === undefined || e.metadata[7] > 0)
const roleOf = e => {
  const n = String(e.metadata[2] || ''); const c = (n.match(/\u00a7(.)/) || [])[1]
  if (e.name === 'rabbit') return 'treasure'
  if (c === 'c') return 'boss'
  if (c === '5') return 'caster'
  if (e.name === 'skeleton' || e.name === 'stray') return 'ranged'
  if (c === '8') return 'heavy'
  if (c === '6') return 'elite'
  return 'melee'
}
const MELEE = { melee: 1, heavy: 1, elite: 1 }
const t0 = Date.now(); let lastAtk = 0, hits = 0, idleSince = Date.now(), seen = new Set()
let minHp = bot.health, drinks = 0, nextDrink = 0, dead = false, why = ''
let kites = 0, kiteUntil = 0, kiteNext = 0, kiteYaw = 0, strafeDir = 0, strafeUntil = 0, drinkUntil = 0
let dodges = 0, missed = 0, casterDodges = 0, dodge = null, lastLine = null, roomLbl = ''
const holdUntil = Date.now() + (MODE === 'kite' && D ? HOLD : 0)
const maxHp = () => (((bot.entity.attributes || {})['generic.maxHealth'] || {}).value || 20)
const P = () => bot.entity.position
const dist2 = (x, z) => Math.hypot(x - P().x, z - P().z)
// world direction (dx,dz) → control states relative to the current yaw (forward = (-sin y, -cos y), right = (cos y, -sin y))
function steer (dx, dz, sprint) {
  const l = Math.hypot(dx, dz); bot.clearControlStates(); if (l < 1e-6) return
  dx /= l; dz /= l; const y = bot.entity.yaw
  const f = -dx * Math.sin(y) - dz * Math.cos(y), s = dx * Math.cos(y) - dz * Math.sin(y)
  if (f > 0.38) bot.setControlState('forward', true); if (f < -0.38) bot.setControlState('back', true)
  if (s > 0.38) bot.setControlState('right', true); if (s < -0.38) bot.setControlState('left', true)
  if (sprint && f > 0.9) bot.setControlState('sprint', true)
  if (bot.entity.isCollidedHorizontally) bot.setControlState('jump', true)
}
const solid = b => b && b.boundingBox === 'block'
function inArea (x, z) {
  if (D && Math.hypot(x - (D[0] + 0.5), z - (D[1] + 0.5)) < 2.2) return true
  if (!BOX) return true
  return x >= BOX[0] + 0.3 && x <= BOX[2] + 0.7 && z >= BOX[1] + 0.3 && z <= BOX[3] + 0.7
}
function free (x, z) {
  if (!inArea(x, z)) return false
  const y = Math.floor(P().y + 0.01)
  const at = (dy) => bot.blockAt(new (require('vec3'))(Math.floor(x), y + dy, Math.floor(z)))
  return !solid(at(0)) && !solid(at(1)) && (solid(at(-1)) || solid(at(-2)))
}
// best open direction (unit [dx,dz]) close to the wanted one; checks 1.5 and 3 blocks ahead
function openDir (wx, wz, bias) {
  const wl = Math.hypot(wx, wz) || 1; wx /= wl; wz /= wl
  let best = null, bs = -9
  for (let i = 0; i < 16; i++) {
    const a = i * Math.PI / 8, dx = Math.cos(a), dz = Math.sin(a)
    if (!free(P().x + dx * 1.5, P().z + dz * 1.5) || !free(P().x + dx * 3, P().z + dz * 3)) continue
    let s = dx * wx + dz * wz
    if (bias) s += 0.5 * (dx * bias[0] + dz * bias[1])
    if (s > bs) { bs = s; best = [dx, dz] }
  }
  return best
}
function doorBias () {
  if (!D) return null
  const dx = D[0] + 0.5 - P().x, dz = D[1] + 0.5 - P().z, l = Math.hypot(dx, dz)
  return l > 2 ? [dx / l, dz / l] : null
}
// a safe point for a telegraph, or null (= not inside / nothing to do)
function sideStep (ox, oz, dx, dz, need, preferLeft) {
  // perpendicular offsets: left of the facing (dx,dz) is (dz,-dx) in MC coordinates (right = (-dz, dx))
  const sides = preferLeft ? [[dz, -dx], [-dz, dx]] : [[-dz, dx], [dz, -dx]]
  for (const [sx, sz] of sides) {
    const x = P().x + sx * need, z = P().z + sz * need
    if (free(x, z) && free(P().x + sx * need / 2, P().z + sz * need / 2)) return [x, z]
  }
  // fall back to backing away from the origin
  const bx = P().x - ox, bz = P().z - oz, bl = Math.hypot(bx, bz) || 1
  const x = P().x + bx / bl * need, z = P().z + bz / bl * need
  return free(x, z) ? [x, z] : null
}
function awayFrom (cx, cz, r) {
  const dx = P().x - cx, dz = P().z - cz, l = Math.hypot(dx, dz)
  if (l > r + 0.6) return null
  const need = r + 1.4 - l, od = openDir(l > 0.2 ? dx : 1, l > 0.2 ? dz : 0)
  if (!od) return null
  return [P().x + od[0] * need, P().z + od[1] * need]
}
function telegraph (line) {
  const s = strip(line)
  let m = s.match(/第二段「(.+?)」— 右移 ([\d.]+) 格 · 左侧安全（([\d.]+) 秒）/)
  const tele = m ? null : s.match(/蓄力「(.+?)」— (.+)（([\d.]+) 秒）/)
  if (!m && !tele) return
  const boss = bot.nearestEntity(e => isHostile(e) && roleOf(e) === 'boss')
  if (!boss) return
  if (Math.random() < MISSP) { missed++; return }
  const warn = Number((m || tele)[3]) * 1000, bx = boss.position.x, bz = boss.position.z
  let dx = P().x - bx, dz = P().z - bz; const dl = Math.hypot(dx, dz) || 1; dx /= dl; dz /= dl
  let pt = null
  if (m && lastLine) { // Q06 second band: origin slid `shift` to the boss's right, same direction → stand on its left
    const N = Number(m[2]), [ox, oz, ldx, ldz, W] = lastLine
    const rx = -ldz, rz = ldx, perp = (P().x - ox) * rx + (P().z - oz) * rz
    const want = N - W / 2 - 1.2
    if (perp > want) { const k = perp - want; const x = P().x - rx * k, z = P().z - rz * k; pt = free(x, z) ? [x, z] : sideStep(ox, oz, ldx, ldz, k, true) }
  } else if (tele) {
    const h = tele[2]; let q
    if ((q = h.match(/以首领为中心半径 ([\d.]+) 圆形/))) pt = awayFrom(bx, bz, Number(q[1]))
    else if ((q = h.match(/前方 ([\d.]+) 格处半径 ([\d.]+) 圆形/))) pt = awayFrom(bx + dx * Number(q[1]), bz + dz * Number(q[1]), Number(q[2]))
    else if ((q = h.match(/锁定 (\S+) 脚下(?: · 半径 ([\d.]+) 格)?/))) { if (q[1] === bot.username) pt = sideStep(P().x, P().z, dx, dz, Number(q[2] || 2.5) + 1.3, false) }
    else if ((q = h.match(/正前直线 长 ([\d.]+) 宽 ([\d.]+)/) || h.match(/正前 ([\d.]+)～([\d.]+) 格条带 宽 ([\d.]+)/) || h.match(/直线冲撞 ([\d.]+) 格 · 条带宽 ([\d.]+)/))) {
      const W = Number(q.length === 4 ? q[3] : q[2]), L = Number(q.length === 4 ? q[2] : q[1])
      lastLine = [bx, bz, dx, dz, W]
      if (dl <= L + 0.6) pt = sideStep(bx, bz, dx, dz, W / 2 + 1.3, true)
    } else if ((q = h.match(/扇形 ([\d.]+) 格/))) pt = awayFrom(bx, bz, Number(q[1]))
  }
  if (pt) { dodge = { x: pt[0], z: pt[1], at: Date.now() + RX, end: Date.now() + warn + 250, kind: 'boss' }; dodges++ }
}
let chatAt = (bot.chatLog || []).length
for (const l of (bot.chatLog || []).slice(-20)) { const r = strip(l).match(/\[余烬\] (\S+) · 敌人/); if (r) roomLbl = r[1] }
const onEffect = (e, eff) => {
  if (MODE !== 'kite' || !e || !eff || eff.id !== 2 || eff.amplifier < 5 || dodge) return
  if (!isHostile(e) || roleOf(e) !== 'caster') return
  const cx = e.position.x, cz = e.position.z; let dx = P().x - cx, dz = P().z - cz; const dl = Math.hypot(dx, dz)
  if (dl > 7) return // line_length 6–7: out of reach
  if (Math.random() < MISSP) { missed++; return }
  dx /= dl || 1; dz /= dl || 1
  const pt = sideStep(cx, cz, dx, dz, 2.4, Math.random() < 0.5)
  if (pt) { dodge = { x: pt[0], z: pt[1], at: Date.now() + RX, end: Date.now() + 1200, kind: 'caster' }; casterDodges++ }
}
bot.on('entityEffect', onEffect)
bot.setQuickBarSlot(SLOT)
try {
  while (Date.now() - t0 < MAXMS) {
    if (!bot.entity || bot.health <= 0) { dead = true; why = 'hp0'; break }
    const log = bot.chatLog || []
    let stop = false
    for (; chatAt < log.length; chatAt++) {
      const s = strip(log[chatAt])
      if (/你已倒下|本局失败/.test(s)) { dead = /你已倒下/.test(s) || dead; why = s.slice(0, 40); stop = true }
      const r = s.match(/\[余烬\] (\S+) · 敌人/); if (r) roomLbl = r[1]
      if (MODE === 'kite' && !stop) telegraph(s)
    }
    if (stop) break
    minHp = Math.min(minHp, bot.health)
    const now = Date.now()
    // ---- drinking (both modes; kite keeps back-pedalling while it drinks)
    if (drinkUntil && now >= drinkUntil) { bot.deactivateItem(); bot.setQuickBarSlot(SLOT); drinkUntil = 0 }
    if (!drinkUntil && DR && bot.health < 0.55 * maxHp() && now > nextDrink) {
      const pi = bot.inventory.slots.slice(36, 45).findIndex(x => x && x.name === 'potion')
      if (pi >= 0) {
        if (MODE !== 'kite') { bot.clearControlStates(); bot.setQuickBarSlot(pi); bot.activateItem(); await wait(1800); bot.deactivateItem(); bot.setQuickBarSlot(SLOT); drinks++; nextDrink = Date.now() + 15500; continue }
        bot.setQuickBarSlot(pi); bot.activateItem(); drinkUntil = now + 1800; drinks++; nextDrink = now + 15500
      }
    }
    const all = Object.values(bot.entities).filter(e => isHostile(e) && e.position.distanceTo(P()) < RADIUS && (MODE !== 'kite' || roleOf(e) !== 'treasure'))
    if (MODE !== 'kite') {
      // ---------------- stand: unchanged 2026-10-04 behaviour
      const t = bot.nearestEntity(e => isHostile(e) && e.position.distanceTo(P()) < RADIUS)
      if (!t) { bot.clearControlStates(); if (Date.now() - idleSince > STOPIDLE) break; await wait(200); continue }
      idleSince = Date.now(); seen.add(t.id)
      const d = t.position.distanceTo(P())
      await bot.lookAt(t.position.offset(0, (t.height || 1.8) * 0.8, 0), true)
      if (d > 2.6) {
        bot.setControlState('forward', true); bot.setControlState('sprint', d > 5)
        bot.setControlState('jump', !!bot.entity.isCollidedHorizontally)
      } else {
        bot.clearControlStates()
        if (Date.now() - lastAtk > 700) { bot.attack(t); lastAtk = Date.now(); hits++ }
      }
      await wait(100); continue
    }
    // ---------------- kite
    const byD = all.map(e => [e, e.position.distanceTo(P())]).sort((a, b) => a[1] - b[1])
    const near = byD.filter(([e, d]) => MELEE[roleOf(e)] && d < 4)
    const meleeAny = byD.find(([e]) => MELEE[roleOf(e)] || roleOf(e) === 'boss')
    let t = null
    const closeMelee = byD.find(([e, d]) => (MELEE[roleOf(e)] || roleOf(e) === 'boss') && d < 5)
    if (closeMelee) t = closeMelee[0]
    else { const rc = byD.find(([e]) => roleOf(e) === 'ranged' || roleOf(e) === 'caster'); t = rc ? rc[0] : (byD[0] && byD[0][0]) }
    if (!t && !dodge) { bot.clearControlStates(); if (Date.now() - idleSince > STOPIDLE) break; await wait(200); continue }
    idleSince = Date.now(); if (t) seen.add(t.id)
    const d = t ? t.position.distanceTo(P()) : 99
    const swing = () => { if (t && !drinkUntil && d <= RCH && Date.now() - lastAtk > 700) { bot.attack(t); lastAtk = Date.now(); hits++ } }
    // 1) telegraph dodge
    if (dodge && now >= dodge.at) {
      if (now > dodge.end) dodge = null
      else {
        const dx = dodge.x - P().x, dz = dodge.z - P().z
        if (Math.hypot(dx, dz) > 0.5) {
          await bot.look(Math.atan2(-dx, -dz), 0, true); steer(dx, dz, true)
        } else { bot.clearControlStates(); if (t) { await bot.lookAt(t.position.offset(0, (t.height || 1.8) * 0.8, 0), true); swing() } }
        await wait(100); continue
      }
    }
    if (!t) { bot.clearControlStates(); await wait(100); continue }
    await bot.lookAt(t.position.offset(0, (t.height || 1.8) * 0.8, 0), true)
    // 2) pull: right after the trigger, step back to the doorway and let the melee come
    if (now < holdUntil && D && !(meleeAny && meleeAny[1] < 4.5)) {
      const dx = D[0] + 0.5 - P().x, dz = D[1] + 0.5 - P().z
      if (Math.hypot(dx, dz) > 0.8) steer(dx, dz, false); else bot.clearControlStates()
      swing(); await wait(100); continue
    }
    // 3) back-pedal from a pack (or while drinking)
    if ((near.length >= 3 && now >= kiteNext) || (drinkUntil && near.length >= 1)) {
      if (now >= kiteUntil) { kiteUntil = now + 1700 + Math.random() * 500; kiteNext = kiteUntil + 800; kites++ }
    }
    if (now < kiteUntil || (drinkUntil && near.length >= 1)) {
      const src = near.length ? near : byD.filter(([e, dd]) => MELEE[roleOf(e)] && dd < 6)
      let cx = 0, cz = 0; for (const [e] of src) { cx += e.position.x; cz += e.position.z }
      if (src.length) { cx /= src.length; cz /= src.length } else { cx = t.position.x; cz = t.position.z }
      const od = openDir(P().x - cx, P().z - cz, doorBias())
      if (od) steer(od[0], od[1], false); else bot.clearControlStates()
      swing(); await wait(100); continue
    }
    // 4) fight: close in, strafe while swinging
    if (d > RCH - 0.2) {
      const dx = t.position.x - P().x, dz = t.position.z - P().z
      steer(dx, dz, d > 5)
    } else {
      if (now >= strafeUntil) { strafeDir = Math.random() < 0.5 ? -1 : 1; strafeUntil = now + 900 + Math.random() * 700 }
      const y = bot.entity.yaw, rx = Math.cos(y) * strafeDir, rz = -Math.sin(y) * strafeDir
      let mx = 0, mz = 0
      if (free(P().x + rx * 1.2, P().z + rz * 1.2)) { mx += rx; mz += rz } else strafeDir = -strafeDir
      if (d < 1.6) { const bx = P().x - t.position.x, bz = P().z - t.position.z, bl = Math.hypot(bx, bz) || 1; if (free(P().x + bx / bl, P().z + bz / bl)) { mx += bx / bl; mz += bz / bl } }
      steer(mx, mz, false)
      swing()
    }
    await wait(100)
  }
} finally { bot.clearControlStates(); if (drinkUntil) { try { bot.deactivateItem(); bot.setQuickBarSlot(SLOT) } catch (e) {} } bot.removeListener('entityEffect', onEffect) }
return { mode: MODE, ms: Date.now() - t0, hits, drinks, kites, dodges, casterDodges, missed, seen: seen.size, dead, why, room: roomLbl, hp: bot.entity ? +bot.health.toFixed(2) : 0, minHp: +minHp.toFixed(2), pos: bot.entity && bot.entity.position.floored(), left: Object.values(bot.entities).filter(isHostile).map(e => [strip(e.metadata[2]), e.position.floored().toString(), e.metadata[7]]).slice(0, 8) }
