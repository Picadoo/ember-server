'use strict'

const fs = require('fs')
const path = require('path')

function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

function createRecorder () {
  const results = []
  return {
    results,
    record (name, status, notes) {
      const row = { name, status, notes: notes || '' }
      results.push(row)
      const tag = status === 'PASS' ? 'PASS' : status === 'FAIL' ? 'FAIL' : 'SKIP'
      console.log(`[suite] ${tag}  ${name}${notes ? ' — ' + notes : ''}`)
      return row
    },
    summaryTable () {
      const lines = [
        '| Mechanic | Status | Notes |',
        '|----------|--------|-------|'
      ]
      for (const r of results) {
        const notes = String(r.notes || '').replace(/\|/g, '\\|').replace(/\n/g, ' ')
        lines.push(`| ${r.name} | **${r.status}** | ${notes} |`)
      }
      return lines.join('\n')
    },
    counts () {
      const c = { PASS: 0, FAIL: 0, SKIP: 0 }
      for (const r of results) c[r.status] = (c[r.status] || 0) + 1
      return c
    }
  }
}

function attachChatLog (bot) {
  const lines = []
  bot.on('message', (msg) => {
    try {
      const s = msg.toString()
      lines.push(s)
      console.log('[chat]', s)
    } catch (_) {}
  })
  return {
    lines,
    joined () { return lines.join('\n') },
    clear () { lines.length = 0 },
    async waitFor (re, timeoutMs = 5000) {
      const start = Date.now()
      while (Date.now() - start < timeoutMs) {
        const hit = lines.find((l) => re.test(l))
        if (hit) return hit
        await sleep(100)
      }
      return null
    }
  }
}

function findItem (bot, pred) {
  const items = bot.inventory.items()
  return items.find(pred) || null
}

function findByNameOrLore (bot, needle) {
  const n = String(needle).toLowerCase()
  return findItem(bot, (it) => {
    const dn = (it.displayName || it.name || '').toLowerCase()
    if (dn.includes(n)) return true
    try {
      const lore = (it.nbt && it.nbt.value && it.nbt.value.display && it.nbt.value.display.value &&
        it.nbt.value.display.value.Lore && it.nbt.value.display.value.Lore.value) || null
      if (lore && lore.value) {
        const parts = lore.value.map((x) => (x && x.value) || String(x)).join(' ').toLowerCase()
        if (parts.includes(n)) return true
      }
    } catch (_) {}
    // customName / lore via prismarine helpers
    try {
      if (typeof it.customName === 'string' && it.customName.toLowerCase().includes(n)) return true
      if (Array.isArray(it.customLore) && it.customLore.join(' ').toLowerCase().includes(n)) return true
    } catch (_) {}
    return it.name === needle || it.name === n
  })
}

function countItemsMatching (bot, pred) {
  return bot.inventory.items().filter(pred).reduce((a, it) => a + it.count, 0)
}

function itemBrief (it) {
  if (!it) return '(none)'
  return `${it.name}#${it.type}x${it.count}` + (it.displayName ? `("${it.displayName}")` : '')
}

function writeStatusMarkdown (outPath, meta) {
  const dir = path.dirname(outPath)
  fs.mkdirSync(dir, { recursive: true })
  fs.writeFileSync(outPath, meta, 'utf8')
}

module.exports = {
  sleep,
  createRecorder,
  attachChatLog,
  findItem,
  findByNameOrLore,
  countItemsMatching,
  itemBrief,
  writeStatusMarkdown
}
