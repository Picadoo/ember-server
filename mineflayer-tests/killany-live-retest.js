// Raid/GuildBoss kill-any live harness A — tip f73c0db / design a76d248
// A1: 3-bot Raid enter via /corerpg enter raid · wave2 dual $kill (卫兵×3+射手×4) → 通道打开
// A2: guild-seed → /corerpg guild boss · wave1 dual $kill (潮尸×5+骨潮×3) → 第一波肃清
// Zero gameplay YAML changes. Early-stop after target wave. ops=[] on exit.
const { joinPlay, ensureLevel } = require('./lib/proxy-login')
const fs = require('fs')
const wait = ms => new Promise(r => setTimeout(r, ms))
const strip = s => String(s || '').replace(/§./g, '')
const since = (b, n) => strip((b.chatLog || []).slice(n).join(' | '))
const OUT = process.env.OUT || '/tmp/killany-live-retest.json'
const LOG = process.env.LOG || '/tmp/killany-live-retest.log'
const OP = process.env.OP_BOT || 'KaOp928'
const RA = process.env.RA || 'KaRa928'
const RB = process.env.RB || 'KaRb928'
const RC = process.env.RC || 'KaRc928'
const GL = process.env.GL || 'KaGb928' // guild boss leader (solo min=1)
const PARTY = [RA, RB, RC]
const MOBS = new Set(['zombie', 'skeleton', 'husk', 'wither_skeleton', 'stray', 'zombie_villager'])
const GUARD = new Set(['zombie']) // 通道卫兵 / 潮尸
const ARCHER = new Set(['skeleton', 'stray']) // 通道射手 / 骨潮
const MAX_WAVE_MS = Number(process.env.MAX_WAVE_MS || 8 * 60 * 1000)
const ENTER_MS = Number(process.env.ENTER_MS || 25000)

function logLine(...a) {
  const s = a.map(x => typeof x === 'string' ? x : JSON.stringify(x)).join(' ')
  console.log(s)
  try { fs.appendFileSync(LOG, s + '\n') } catch (e) {}
}

function mobsNear(bot, r, typeSet) {
  return Object.values(bot.entities).filter(e =>
    e && e !== bot.entity && MOBS.has(e.name) && e.isValid !== false &&
    (!typeSet || typeSet.has(e.name)) &&
    e.position.distanceTo(bot.entity.position) < (r || 80))
}

function worldHint(bot) {
  try {
    // mineflayer 1.12: dimension is overworld/nether/end; Multiverse name often in scoreboard or via chat only
    const dim = bot.game && bot.game.dimension
    const wn = (bot.world && (bot.world.name || bot.world.path)) || ''
    return { dim, wn: String(wn), pos: bot.entity ? bot.entity.position : null }
  } catch (e) { return { err: e.message } }
}

async function mk(name, log) {
  const b = await joinPlay(name, { log: !!log })
  return b
}

;(async () => {
  try { fs.writeFileSync(LOG, '') } catch (e) {}
  const r = {
    tip: 'f73c0db', design: 'a76d248', startedAt: new Date().toISOString(),
    a1: {}, a2: {}, notes: [], opsEnd: null
  }
  const op = await mk(OP, false)
  await wait(1500)
  let opQ = Promise.resolve()
  const c = (s, ms) => {
    opQ = opQ.then(async () => { logLine('[op]', s); op.chat(s); await wait(ms || 800) })
    return opQ
  }

  // Bootstrap OP via console FIFO (vanilla op) then LP admin
  try {
    fs.writeFileSync('/workspace/minecraft/server-runtime/console.in', `op ${OP}\n`)
    await wait(1200)
  } catch (e) { r.notes.push('console_op_fifo:' + e.message) }
  await c(`/lp user ${OP} parent add admin`, 1000)
  await c(`/lp user ${OP} permission set corerpg.admin true`, 900)
  await c(`/op ${OP}`, 600)

  // ========== A1 Raid ==========
  logLine('=== A1 Raid wave2 ===')
  const party = {}
  let bots = []
  let G = null
  for (const n of PARTY) {
    party[n] = await mk(n, true)
    await wait(700)
  }
  const A = party[RA], B = party[RB], C = party[RC]
  bots = [A, B, C]

  for (const n of PARTY) {
    await c(`/deop ${n}`, 400)
    await c(`/clear ${n}`, 600)
    await c(`/gamemode survival ${n}`, 400)
    await c(`/mvtp ${n} ember_hub`, 1600)
    await c(`/effect ${n} clear`, 400)
  }
  for (const n of PARTY) {
    await c(`/effect ${n} resistance 1800 4 true`, 400)
    await c(`/effect ${n} regeneration 1800 4 true`, 400)
    await c(`/effect ${n} strength 1800 9 true`, 400)
    await c(`/ni give ${n} gear_ember_blade 1`, 1000)
    await c(`/ni give ${n} ticket_ember_raid 1`, 900)
    await c(`/corerpg stamina set ${n} 200`, 700)
    await ensureLevel(op, party[n], 35)
  }
  for (const b of bots) {
    const it = b.inventory.items().find(i => i.name === 'iron_sword' || i.name === 'diamond_sword')
    if (it) try { await b.equip(it, 'hand') } catch (e) {}
  }

  // dungeon-team
  A.chat('/dungeon-team disband'); await wait(700)
  A.chat('/dungeon-team create'); await wait(1200)
  A.chat(`/dungeon-team invite ${RB}`); await wait(900)
  B.chat(`/dungeon-team request join ${RA}`); await wait(1200)
  A.chat(`/dungeon-team invite ${RC}`); await wait(900)
  C.chat(`/dungeon-team request join ${RA}`); await wait(1500)
  r.a1.teamChat = since(A, Math.max(0, A.chatLog.length - 30)).slice(0, 400)

  // enter via TicketEntry path
  const enterN = { [RA]: A.chatLog.length, [RB]: B.chatLog.length, [RC]: C.chatLog.length }
  const enterT0 = Date.now()
  A.chat('/corerpg enter raid')
  let entered = false
  let enterProof = ''
  const enterRe = /团本大厅已集结|【左道】|左道卫兵|已扣除余烬团本票|团本已点燃|正在进入/
  const failRe = /人数 3～5|需要余烬 Lv|体力不足|冷却|间隔|请稍后再试|进本失败|未组队/
  while (Date.now() - enterT0 < ENTER_MS) {
    await wait(500)
    const logs = PARTY.map(n => since(party[n], enterN[n])).join(' || ')
    if (/团本大厅已集结|【左道】|左道卫兵/.test(logs)) {
      entered = true
      enterProof = 'banner:' + (logs.match(/团本大厅已集结|【左道】[^|]*|左道卫兵[^|]*/)?.[0] || 'hit')
      break
    }
    // world-name fallback: check via console / mvtp fail / server will log dungeon
    if (failRe.test(logs) && Date.now() - enterT0 > 8000) {
      enterProof = 'fail:' + logs.slice(0, 500)
      break
    }
  }
  // also scrape server log for dungeon world
  let worldEvidence = ''
  try {
    const latest = fs.readFileSync('/workspace/minecraft/server-runtime/logs/latest.log', 'utf8')
    const tail = latest.slice(-8000)
    const wm = tail.match(/dungeon_EmberRaid_[A-Za-z0-9_\-]+/g)
    if (wm && wm.length) worldEvidence = wm[wm.length - 1]
    if (!entered && worldEvidence) {
      entered = true
      enterProof = (enterProof ? enterProof + ';' : '') + 'world:' + worldEvidence
    }
  } catch (e) {}

  r.a1.entered = entered
  r.a1.enterMs = Date.now() - enterT0
  r.a1.enterProof = enterProof
  r.a1.worldEvidence = worldEvidence
  r.a1.enterChat = PARTY.map(n => n + ':' + since(party[n], enterN[n]).slice(0, 280))
  r.a1.worldHints = PARTY.map(n => ({ n, ...worldHint(party[n]) }))
  logLine('[A1] entered=' + entered + ' proof=' + enterProof + ' world=' + worldEvidence)

  if (!entered) {
    r.a1.result = 'FAIL_ENTER'
    r.a1.note = '未出现团本横幅且无 dungeon_EmberRaid_ 世界证据 — 禁仅凭菜单点了判进本'
  } else {
    await wait(2500)
    // re-equip
    for (const b of bots) {
      const it = b.inventory.items().find(i => i.name === 'iron_sword' || i.name === 'diamond_sword')
      if (it) try { await b.equip(it, 'hand') } catch (e) {}
    }

    const fightN = A.chatLog.length
    const tFight0 = Date.now()
    let wave1Done = false, wave2Start = false, wave2Cleared = false
    let spotPartialOk = null // after killing only guards in wave2, no 通道打开
    let phase = 'wave1' // wave1 → wave2_guards_only → wave2_archers → done
    const hits = { [RA]: 0, [RB]: 0, [RC]: 0 }
    const lastTp = {}
    let running = true

    const loops = bots.map(async f => {
      while (running && Date.now() - tFight0 < MAX_WAVE_MS) {
        let prefer = null
        if (phase === 'wave2_guards_only') prefer = GUARD
        else if (phase === 'wave2_archers') prefer = ARCHER
        // prefer target set, else any
        let list = prefer ? mobsNear(f, 80, prefer) : []
        if (!list.length) list = mobsNear(f, 80)
        // in guards_only phase, skip archers entirely
        if (phase === 'wave2_guards_only') list = list.filter(e => GUARD.has(e.name))
        if (phase === 'wave2_archers') list = list.filter(e => ARCHER.has(e.name))
        list.sort((a, b) => a.position.distanceTo(f.entity.position) - b.position.distanceTo(f.entity.position))
        const e = list[0]
        if (!e) { await wait(600); continue }
        const d = e.position.distanceTo(f.entity.position)
        if (d > 3.2 && Date.now() - (lastTp[f.username] || 0) > 1500) {
          op.chat(`/tp ${f.username} ${(e.position.x + 1.1).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.5).toFixed(1)}`)
          lastTp[f.username] = Date.now()
        }
        if (d <= 3.6) {
          try { await f.lookAt(e.position.offset(0, (e.height || 1.8) * 0.8, 0), true) } catch (x) {}
          f.attack(e); hits[f.username]++
        }
        await wait(550)
      }
    })

    let guardsClearedAt = null
    let waitPartialUntil = null
    while (Date.now() - tFight0 < MAX_WAVE_MS) {
      const log = since(A, fightN)
      if (!wave1Done && /左道肃清/.test(log)) {
        wave1Done = true
        r.a1.wave1AtMs = Date.now() - tFight0
        logLine('[A1] wave1 done @' + r.a1.wave1AtMs)
      }
      if (!wave2Start && /【右道】|射手就位/.test(log)) {
        wave2Start = true
        phase = 'wave2_guards_only'
        r.a1.wave2StartAtMs = Date.now() - tFight0
        r.a1.wave2Banner = (log.match(/【右道】[^|]*/) || [''])[0]
        logLine('[A1] wave2 start @' + r.a1.wave2StartAtMs)
      }
      if (phase === 'wave2_guards_only' && wave2Start) {
        const guards = mobsNear(A, 90, GUARD).length
        const archers = mobsNear(A, 90, ARCHER).length
        if (guards === 0 && archers > 0 && !guardsClearedAt) {
          guardsClearedAt = Date.now()
          waitPartialUntil = Date.now() + 12000
          r.a1.spotGuardsClearedArchersLeft = archers
          logLine('[A1] spot: guards=0 archers=' + archers + ' waiting for no-pass')
        }
        if (waitPartialUntil && Date.now() >= waitPartialUntil) {
          const stillNo = !/通道打开/.test(since(A, fightN))
          spotPartialOk = stillNo
          r.a1.spotPartialNoPass = stillNo
          phase = 'wave2_archers'
          logLine('[A1] spot partial → ' + (stillNo ? 'PASS (no 通道打开)' : 'FAIL (passed early)') + '; now clear archers')
        }
      }
      if (/通道打开/.test(log)) {
        wave2Cleared = true
        r.a1.wave2ClearAtMs = Date.now() - tFight0
        r.a1.wave2ClearBanner = (log.match(/通道打开[^|]*/) || ['通道打开'])[0]
        logLine('[A1] wave2 cleared @' + r.a1.wave2ClearAtMs)
        break
      }
      // if somehow no archers left either and phase still guards_only with 0+0, flip
      if (phase === 'wave2_guards_only' && wave2Start && mobsNear(A, 90).length === 0 && Date.now() - tFight0 > (r.a1.wave2StartAtMs || 0) + 8000) {
        phase = 'wave2_archers' // nothing left? keep waiting for banner
      }
      await wait(800)
    }
    running = false
    await Promise.all(loops)
    r.a1.hits = hits
    r.a1.fightMs = Date.now() - tFight0
    r.a1.wave1Done = wave1Done
    r.a1.wave2Start = wave2Start
    r.a1.wave2Cleared = wave2Cleared
    r.a1.spotPartialOk = spotPartialOk
    r.a1.fightChatTail = since(A, fightN).slice(-800)

    if (entered && wave2Cleared && (spotPartialOk === true || spotPartialOk === null)) {
      r.a1.result = 'PASS'
    } else if (entered && wave2Cleared && spotPartialOk === false) {
      r.a1.result = 'FAIL_SPOT' // cleared but spot check showed early pass
    } else if (entered && !wave2Cleared) {
      r.a1.result = 'FAIL_WAVE2'
    } else {
      r.a1.result = 'FAIL'
    }

    // early stop — leave dungeon
    for (const b of bots) { b.chat('/dp leave'); await wait(400) }
    await wait(2000)
    A.chat('/dungeon-team disband'); await wait(800)
  }

  // cleanup party to hub
  for (const n of PARTY) {
    await c(`/mvtp ${n} ember_hub`, 1200)
    await c(`/effect ${n} clear`, 300)
  }

  // ========== A2 GuildBoss ==========
  logLine('=== A2 GuildBoss wave1 ===')
  G = await mk(GL, true)
  await wait(1200)
  await c(`/deop ${GL}`, 400)
  await c(`/clear ${GL}`, 600)
  await c(`/gamemode survival ${GL}`, 400)
  await c(`/mvtp ${GL} ember_hub`, 1600)
  await c(`/effect ${GL} clear`, 400)
  await c(`/effect ${GL} resistance 1800 4 true`, 400)
  await c(`/effect ${GL} regeneration 1800 4 true`, 400)
  await c(`/effect ${GL} strength 1800 9 true`, 400)
  await c(`/give ${GL} diamond_sword 1`, 800)
  await c(`/corerpg coin give ${GL} 8000`, 800)
  await c(`/corerpg stamina set ${GL} 200`, 700)
  await ensureLevel(op, G, 20) // guild boss has no high level gate in option; keep reasonable
  { const it = G.inventory.items().find(i => i.name === 'diamond_sword'); if (it) try { await G.equip(it, 'hand') } catch (e) {} }

  // leave any old guild / create fresh
  G.chat('/corerpg guild leave'); await wait(1000)
  const gCreateN = G.chatLog.length
  const gName = '杀债' + String(Date.now()).slice(-4)
  G.chat(`/corerpg guild create ${gName}`); await wait(2000)
  r.a2.guildCreate = since(G, gCreateN).slice(0, 300)
  r.a2.guildName = gName

  // seed contribution: 20 needed; shard rate 10 → 200 shards; daily cap 50
  await c(`/ni give ${GL} mat_ember_shard 250`, 1200)
  const donN = G.chatLog.length
  G.chat('/corerpg guild donate mat_ember_shard 200'); await wait(1500)
  r.a2.donateChat = since(G, donN).slice(0, 250)
  const infoN = G.chatLog.length
  G.chat('/corerpg guild info'); await wait(1200)
  r.a2.guildInfo = since(G, infoN).slice(0, 400)
  const cm = r.a2.guildInfo.match(/我的贡献\s*(\d+)/)
  r.a2.contrib = cm ? Number(cm[1]) : null

  // dungeon-team solo (min=1)
  G.chat('/dungeon-team disband'); await wait(600)
  G.chat('/dungeon-team create'); await wait(1000)

  const bossN = G.chatLog.length
  const bossT0 = Date.now()
  G.chat('/corerpg guild boss')
  let gbEntered = false
  while (Date.now() - bossT0 < ENTER_MS) {
    await wait(500)
    const log = since(G, bossN)
    if (/已点燃|盟 Boss 波次|深渊潮|盟约周 Boss/.test(log)) { gbEntered = true; break }
    if (/贡献不足|不在任何盟约|周限|本周已|请由|冷却/.test(log) && Date.now() - bossT0 > 6000) break
  }
  let gbWorld = ''
  try {
    const latest = fs.readFileSync('/workspace/minecraft/server-runtime/logs/latest.log', 'utf8')
    const tail = latest.slice(-8000)
    const wm = tail.match(/dungeon_EmberGuildBoss_[A-Za-z0-9_\-]+/g)
    if (wm && wm.length) gbWorld = wm[wm.length - 1]
    if (!gbEntered && gbWorld) gbEntered = true
  } catch (e) {}
  r.a2.entered = gbEntered
  r.a2.enterMs = Date.now() - bossT0
  r.a2.enterChat = since(G, bossN).slice(0, 500)
  r.a2.worldEvidence = gbWorld
  logLine('[A2] entered=' + gbEntered + ' world=' + gbWorld)

  if (!gbEntered) {
    r.a2.result = 'FAIL_ENTER'
  } else {
    await wait(2000)
    { const it = G.inventory.items().find(i => i.name === 'diamond_sword'); if (it) try { await G.equip(it, 'hand') } catch (e) {} }

    const fightN = G.chatLog.length
    const t0 = Date.now()
    let wave1Start = false, wave1Cleared = false
    let spotPartialOk = null
    let phase = 'all' // then zombies_only → skeletons → done
    let lastTp = 0
    let hits = 0
    let zombiesClearedAt = null
    let waitPartialUntil = null

    // wait for wave1 banner
    while (Date.now() - t0 < MAX_WAVE_MS) {
      const log = since(G, fightN)
      if (!wave1Start && /盟 Boss 波次 1|深渊潮/.test(log)) {
        wave1Start = true
        phase = 'zombies_only'
        r.a2.wave1StartAtMs = Date.now() - t0
        logLine('[A2] wave1 start')
      }
      // combat tick
      let prefer = phase === 'zombies_only' ? GUARD : (phase === 'skeletons' ? ARCHER : null)
      let list = prefer ? mobsNear(G, 80, prefer) : mobsNear(G, 80)
      if (phase === 'zombies_only') list = list.filter(e => GUARD.has(e.name))
      if (phase === 'skeletons') list = list.filter(e => ARCHER.has(e.name))
      list.sort((a, b) => a.position.distanceTo(G.entity.position) - b.position.distanceTo(G.entity.position))
      const e = list[0]
      if (e) {
        const d = e.position.distanceTo(G.entity.position)
        if (d > 3.2 && Date.now() - lastTp > 1500) {
          op.chat(`/tp ${GL} ${(e.position.x + 1.1).toFixed(1)} ${e.position.y.toFixed(1)} ${(e.position.z + 0.5).toFixed(1)}`)
          lastTp = Date.now()
        }
        if (d <= 3.6) {
          try { await G.lookAt(e.position.offset(0, (e.height || 1.8) * 0.8, 0), true) } catch (x) {}
          G.attack(e); hits++
        }
      }
      if (phase === 'zombies_only' && wave1Start) {
        const z = mobsNear(G, 90, GUARD).length
        const s = mobsNear(G, 90, ARCHER).length
        if (z === 0 && s > 0 && !zombiesClearedAt) {
          zombiesClearedAt = Date.now()
          waitPartialUntil = Date.now() + 12000
          r.a2.spotZombiesClearedSkeletonsLeft = s
          logLine('[A2] spot: zombies=0 skel=' + s)
        }
        if (waitPartialUntil && Date.now() >= waitPartialUntil) {
          const stillNo = !/第一波肃清|蛮兵将至/.test(since(G, fightN))
          spotPartialOk = stillNo
          r.a2.spotPartialNoPass = stillNo
          phase = 'skeletons'
          logLine('[A2] spot partial → ' + (stillNo ? 'PASS' : 'FAIL'))
        }
      }
      if (/第一波肃清|蛮兵将至/.test(log)) {
        wave1Cleared = true
        r.a2.wave1ClearAtMs = Date.now() - t0
        r.a2.wave1ClearBanner = (log.match(/第一波肃清[^|]*|蛮兵将至[^|]*/) || ['第一波肃清'])[0]
        logLine('[A2] wave1 cleared')
        break
      }
      await wait(550)
    }
    r.a2.hits = hits
    r.a2.fightMs = Date.now() - t0
    r.a2.wave1Start = wave1Start
    r.a2.wave1Cleared = wave1Cleared
    r.a2.spotPartialOk = spotPartialOk
    r.a2.fightChatTail = since(G, fightN).slice(-600)

    if (gbEntered && wave1Cleared && (spotPartialOk === true || spotPartialOk === null)) {
      r.a2.result = 'PASS'
    } else if (gbEntered && wave1Cleared && spotPartialOk === false) {
      r.a2.result = 'FAIL_SPOT'
    } else if (gbEntered && !wave1Cleared) {
      r.a2.result = 'FAIL_WAVE1'
    } else {
      r.a2.result = 'FAIL'
    }

    G.chat('/dp leave'); await wait(1500)
    G.chat('/dungeon-team disband'); await wait(600)
  }

  // ========== cleanup ==========
  logLine('=== cleanup ===')
  const allNames = PARTY.concat([GL, OP])
  for (const n of allNames) {
    try { await c(`/dp leave`, 300) } catch (e) {}
  }
  // leave guild if possible
  try { if (G) { G.chat('/corerpg guild leave'); await wait(800) } } catch (e) {}
  for (const n of allNames) {
    await c(`/deop ${n}`, 400)
    await c(`/lp user ${n} permission unset corerpg.admin`, 500)
    await c(`/lp user ${n} parent remove admin`, 500)
  }
  // kick bots (not needed but keeps server clean)
  for (const n of PARTY.concat([GL])) {
    await c(`/kick ${n} killany-done`, 500)
  }
  await c(`/deop ${OP}`, 400)
  try {
    fs.writeFileSync('/workspace/minecraft/server-runtime/console.in', `deop ${OP}\n`)
    await wait(800)
  } catch (e) {}

  // verify ops empty
  try {
    const opsPlay = JSON.parse(fs.readFileSync('/workspace/minecraft/server-runtime/ops.json', 'utf8'))
    const opsLogin = JSON.parse(fs.readFileSync('/workspace/minecraft/login-runtime/ops.json', 'utf8'))
    r.opsEnd = { play: opsPlay, login: opsLogin }
  } catch (e) { r.opsEnd = { err: e.message } }

  r.finishedAt = new Date().toISOString()
  r.overall = (r.a1.result === 'PASS' && r.a2.result === 'PASS') ? 'PASS'
    : (r.a1.result === 'PASS' || r.a2.result === 'PASS') ? 'PARTIAL' : 'FAIL'

  fs.writeFileSync(OUT, JSON.stringify(r, null, 2))
  logLine('KILLANY_LIVE_RESULT', JSON.stringify(r, null, 2))

  // disconnect
  try { op.quit() } catch (e) {}
  for (const b of bots) try { b.quit() } catch (e) {}
  try { if (G) G.quit() } catch (e) {}
  await wait(1000)
  process.exit(r.overall === 'PASS' ? 0 : 2)
})().catch(e => {
  console.error(e)
  try { fs.appendFileSync(LOG, String(e && e.stack || e) + '\n') } catch (x) {}
  process.exit(1)
})
