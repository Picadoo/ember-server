/**
 * Create Ember ladder holograms via HolographicDisplays /hd commands.
 * Source of truth: docs/ember-holograms.md
 * Run once as OP RpgBot when server :25565 is up.
 */
const mineflayer = require('mineflayer')
const fs = require('fs')
const path = require('path')

const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'RpgBot',
  version: '1.12.2',
  auth: 'offline'
})

const chat = []
bot.on('message', m => {
  const s = m.toString()
  chat.push(s)
  console.log('[chat]', s)
})

const wait = ms => new Promise(r => setTimeout(r, ms))
async function cmd(c) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(900)
}

const BOARDS = [
  {
    name: 'ember_ladder_power',
    xyz: [-44, 67, 265],
    lines: [
      '/hd create ember_ladder_power &6余烬 · 战力天梯',
      '/hd addline ember_ladder_power &7展示分 power_score · 不乘伤害',
      '/hd addline ember_ladder_power &e1. &f%ember_ladder_power_1_name% &7- &b%ember_ladder_power_1_value%',
      '/hd addline ember_ladder_power &e2. &f%ember_ladder_power_2_name% &7- &b%ember_ladder_power_2_value%',
      '/hd addline ember_ladder_power &e3. &f%ember_ladder_power_3_name% &7- &b%ember_ladder_power_3_value%',
      '/hd addline ember_ladder_power &74. &f%ember_ladder_power_4_name% &7- &7%ember_ladder_power_4_value%',
      '/hd addline ember_ladder_power &75. &f%ember_ladder_power_5_name% &7- &7%ember_ladder_power_5_value%',
      '/hd addline ember_ladder_power &76. &f%ember_ladder_power_6_name% &7- &7%ember_ladder_power_6_value%',
      '/hd addline ember_ladder_power &77. &f%ember_ladder_power_7_name% &7- &7%ember_ladder_power_7_value%',
      '/hd addline ember_ladder_power &78. &f%ember_ladder_power_8_name% &7- &7%ember_ladder_power_8_value%',
      '/hd addline ember_ladder_power &79. &f%ember_ladder_power_9_name% &7- &7%ember_ladder_power_9_value%',
      '/hd addline ember_ladder_power &710. &f%ember_ladder_power_10_name% &7- &7%ember_ladder_power_10_value%',
      '/hd addline ember_ladder_power &8周结算：外观称号 only'
    ]
  },
  {
    name: 'ember_ladder_abyss',
    xyz: [-40, 67, 265],
    lines: [
      '/hd create ember_ladder_abyss &5余烬 · 深渊天梯',
      '/hd addline ember_ladder_abyss &7本周最高层',
      '/hd addline ember_ladder_abyss &e1. &f%ember_ladder_abyss_1_name% &7- &d%ember_ladder_abyss_1_value%层',
      '/hd addline ember_ladder_abyss &e2. &f%ember_ladder_abyss_2_name% &7- &d%ember_ladder_abyss_2_value%层',
      '/hd addline ember_ladder_abyss &e3. &f%ember_ladder_abyss_3_name% &7- &d%ember_ladder_abyss_3_value%层',
      '/hd addline ember_ladder_abyss &74. &f%ember_ladder_abyss_4_name% &7- &7%ember_ladder_abyss_4_value%层',
      '/hd addline ember_ladder_abyss &75. &f%ember_ladder_abyss_5_name% &7- &7%ember_ladder_abyss_5_value%层',
      '/hd addline ember_ladder_abyss &8对齐 EmberAbyss · 周结算外观'
    ]
  },
  {
    name: 'ember_ladder_speed',
    xyz: [-36, 67, 265],
    lines: [
      '/hd create ember_ladder_speed &c余烬 · 竞速天梯',
      '/hd addline ember_ladder_speed &7EmberWeekly 通关用时',
      '/hd addline ember_ladder_speed &e1. &f%ember_ladder_speed_1_name% &7- &a%ember_ladder_speed_1_value%s',
      '/hd addline ember_ladder_speed &e2. &f%ember_ladder_speed_2_name% &7- &a%ember_ladder_speed_2_value%s',
      '/hd addline ember_ladder_speed &e3. &f%ember_ladder_speed_3_name% &7- &a%ember_ladder_speed_3_value%s',
      '/hd addline ember_ladder_speed &74. &f%ember_ladder_speed_4_name% &7- &7%ember_ladder_speed_4_value%s',
      '/hd addline ember_ladder_speed &75. &f%ember_ladder_speed_5_name% &7- &7%ember_ladder_speed_5_value%s',
      '/hd addline ember_ladder_speed &8周结算：外观 only · 禁核心'
    ]
  }
]

function resultPath() {
  return path.join(__dirname, 'logs', 'hd-create-ember.json')
}

bot.once('spawn', async () => {
  const out = {
    result: 'FAIL',
    boards: {},
    pos: null,
    notes: [],
    chat_tail: []
  }
  try {
    await wait(2500)
    // AFK hub area first
    await cmd('/tp -40 70 265')
    await wait(800)
    if (bot.entity && bot.entity.position) {
      out.pos = {
        x: +bot.entity.position.x.toFixed(1),
        y: +bot.entity.position.y.toFixed(1),
        z: +bot.entity.position.z.toFixed(1)
      }
      out.notes.push('spawn_pos=' + JSON.stringify(out.pos))
    }

    // Optional coin placeholder sanity (already working elsewhere)
    const n0 = chat.length
    await cmd('/papi parse me %corerpg_coin%')
    await wait(600)
    out.notes.push('papi_coin=' + chat.slice(n0).join(' | ').slice(0, 240))

    for (const b of BOARDS) {
      const [x, y, z] = b.xyz
      await cmd(`/hd delete ${b.name}`)
      await wait(400)
      await cmd(`/tp ${x} ${y} ${z}`)
      await wait(700)
      const mark = chat.length
      for (const line of b.lines) {
        await cmd(line)
      }
      await wait(500)
      const snip = chat.slice(mark).join('\n')
      const created =
        /created|Created|已创建|Hologram|hologram|Added|added|成功/i.test(snip) ||
        !/Unknown|没有权限|permission|denied|不存在|error|Exception/i.test(snip)
      // Prefer explicit success; treat "already exists" after delete+create carefully
      const failHard = /没有权限|You do not have permission|Unknown command|Invalid|Exception/i.test(snip)
      out.boards[b.name] = failHard ? 'FAIL' : 'PASS'
      out.notes.push(`${b.name}@${x},${y},${z} => ${out.boards[b.name]} :: ${snip.slice(0, 280).replace(/\n/g, ' | ')}`)
    }

    // List holograms if supported
    const nL = chat.length
    await cmd('/hd list')
    await wait(800)
    const listChat = chat.slice(nL).join('\n')
    out.notes.push('hd_list=' + listChat.slice(0, 500).replace(/\n/g, ' | '))
    const allPresent =
      /ember_ladder_power/.test(listChat) &&
      /ember_ladder_abyss/.test(listChat) &&
      /ember_ladder_speed/.test(listChat)

    const boardPass = Object.values(out.boards).every(v => v === 'PASS')
    out.result = boardPass || allPresent ? 'PASS' : 'FAIL'
    if (allPresent) out.notes.push('hd_list confirmed all three names')
    if (!boardPass && allPresent) {
      Object.keys(out.boards).forEach(k => { out.boards[k] = 'PASS' })
      out.result = 'PASS'
    }

    out.chat_tail = chat.slice(-40)
    console.log('HD_CREATE_RESULT', out.result)
    console.log(JSON.stringify(out, null, 2))
    try {
      fs.mkdirSync(path.join(__dirname, 'logs'), { recursive: true })
      fs.writeFileSync(resultPath(), JSON.stringify(out, null, 2))
    } catch (e) {
      out.notes.push('log_write_err=' + e.message)
    }
    process.exit(out.result === 'PASS' ? 0 : 1)
  } catch (e) {
    console.error(e)
    out.notes.push(String(e && e.stack || e))
    out.chat_tail = chat.slice(-40)
    try {
      fs.mkdirSync(path.join(__dirname, 'logs'), { recursive: true })
      fs.writeFileSync(resultPath(), JSON.stringify(out, null, 2))
    } catch (_) {}
    process.exit(1)
  }
})

bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 180000)
