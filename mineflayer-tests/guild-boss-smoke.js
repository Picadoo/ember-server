/**
 * CoreRpg guild boss smoke — DESIGN-ember-guild-ladder.md §1.3
 * Ensure guild exists (create + cash/coin give if needed), donate contribution,
 * then /corerpg guild boss; also try /dp start EmberGuildBoss if that dungeon appears.
 * Do NOT run until boss.enabled + guild→DP wiring (or STATUS says ready).
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

const chatLog = []
bot.on('message', m => {
  const s = m.toString()
  chatLog.push(s)
  console.log('[chat]', s)
})

const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(1200)
}
function recent(n = 24) {
  return chatLog.slice(-n).join('\n')
}
function saw(re, n = 24) {
  return re.test(recent(n))
}

bot.once('spawn', async () => {
  try {
    await wait(2500)

    // funds for create: prefer coin 5000, else crystal 40 (guild.yml)
    await chat('/corerpg coin give RpgBot 10000')
    await chat('/corerpg cash give RpgBot 100')
    await chat('/corerpg coin')
    await chat('/corerpg cash')

    // --- ensure guild ---
    await chat('/corerpg guild')
    await chat('/corerpg guild info')
    const needCreate = saw(/还没有盟约|不在任何盟约|你还没有盟约/i)
    if (needCreate) {
      console.log('[plan] no guild → create 余烬测')
      await chat('/corerpg guild create 余烬测')
      // if name taken, try alternate
      if (saw(/已被占用|已占用/i)) {
        await chat('/corerpg guild create 余烬测乙')
      }
    } else {
      console.log('[plan] already in guild (or info ambiguous) — skip create')
    }
    await chat('/corerpg guild info')

    // --- donate enough contribution (shard×10 → 1; daily cap 50) ---
    // give materials; donate up to ~20 contrib if still room
    await chat('/ni give RpgBot mat_ember_shard 200')
    await chat('/ni give RpgBot mat_ember_bone_dust 50')
    await wait(400)
    await chat('/corerpg guild donate mat_ember_shard 100') // +10
    await chat('/corerpg guild donate mat_ember_shard 100') // +10 more if room
    if (saw(/今日贡献已达上限|日.*上限/i)) {
      console.log('[plan] daily donate cap hit — OK for smoke')
    }
    await chat('/corerpg guild info')

    // --- guild boss gate ---
    const cBoss = chatLog.length
    await chat('/corerpg guild boss')
    await wait(1500)
    const bossChat = chatLog.slice(cBoss).join('\n')
    console.log('GUILD_BOSS_SNIP', bossChat.slice(0, 600))
    if (/暂未开放/.test(bossChat)) {
      console.log('[note] guild boss still stub — expected until boss.enabled + DP wire')
    }

    // --- DP EmberGuildBoss if dungeon appears / is registered ---
    await chat('/dp leave')
    await wait(800)
    const cDp = chatLog.length
    await chat('/dp start EmberGuildBoss')
    await wait(5000)
    const dpChat = chatLog.slice(cDp).join('\n')
    console.log('DP_GUILD_BOSS_SNIP', dpChat.slice(0, 800))
    const dpHit = /EmberGuildBoss|盟约周 Boss|盟 Boss|已点燃|创建了新的队伍|地牢|人数|组队|不存在|未知|无法|错误|条件/i.test(dpChat)
    console.log('[note] dp_start_seen=' + dpHit)
    await chat('/dp leave')
    await wait(1000)

    console.log('GUILD_BOSS_SMOKE_DONE')
    process.exit(0)
  } catch (e) {
    console.error(e)
    process.exit(1)
  }
})

bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 150000)
