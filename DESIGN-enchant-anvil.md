# Enchanting table + Anvil custom design

**Date:** 2026-09-09 Asia/Shanghai  
**Stack:** Paper custom jar + `EnchantNmsHooks` / `AnvilNmsHooks` + `CoreEnchant` / `CoreAnvil`  
**Audience:** operators / builders (no internal phase codes)

## Goals

- Replace vanilla enchanting-table random rolls with **configurable YAML offer tables** (enchantment, level, XP cost, lapis cost).
- Make anvil costs and combine rules **config-driven**, and block overpowered vanilla merges we replace.
- Keep NeigeItems as the item system; Paper never depends on NI.
- Leave **blocks** (hardness / block drops) untouched.

---

## Enchanting table (`CoreEnchant`)

### Behavior

When `override_vanilla: true` (demo default):

1. NMS `ContainerEnchantTable` asks `EnchantNmsHooks.resolveOffers(...)`.
2. Offers come only from YAML tables — vanilla `EnchantmentManager` rolls are skipped.
3. Taking an offer applies the **shown** enchant (not a re-roll), and consumes the configured **lapis_cost** of the **NI catalyst** (not vanilla lapis).
4. Secondary slot accepts only the configured NeigeItems catalyst via `EnchantNmsHooks.CatalystValidator` (Paper has no NI dependency).

### Catalyst (replaces lapis)

| Key | Demo value | Notes |
|-----|------------|-------|
| `catalyst_ni_id` | `crystal_ember_enchant` | NI id **余烬附魔晶** (`INK_SACK:4` + glow so 1.12 client can place it) |
| `reject_vanilla_lapis` | `true` | Plain lapis / non-NI dye rejected |
| Give | `/ni give <player> crystal_ember_enchant 16` | |

### Demo config (seeded, cheap XP)

Two tables (`ember_default`, `ember_alt`); seed picks which table:

| Slot | Default | Alt | XP cost (base) | Catalyst |
|------|---------|-----|----------------|----------|
| 0 | Protection I | Fire Protection I | **1** / **1** | 1 |
| 1 | Unbreaking I | Efficiency I | **2** / **3** | 2 |
| 2 | Sharpness I | Looting I | **3** / **5** | 3 |

Bookshelves bump: `+min(bookshelf_cost_bump_cap, shelves/3)` — demo **cap 0** (no bump).

Optional later: `whitelist_ni_ids` / `require_ni_item` to limit which NI gear can be enchanted.

### Commands

- `/coreenchant check` — hooks, tables, slot offers  
- `/coreenchant reload` — hot-reload YAML and re-push hooks  

---

## Anvil (`CoreAnvil`) — demo policy choice

**Chosen demo policy** (documented here so it is intentional):

| Knob | Demo value | Why |
|------|------------|-----|
| `rename_cost` | **5** | Higher than vanilla 1 — rename is meaningful |
| `max_level_cost` | **30** | Soft-cap below vanilla’s 40 wall |
| `material_repair_only` | **true** | Only material repair + rename |
| `block_enchant_combines` | **true** | Blocks enchanted-book / item–item enchant merges (vanilla OP combines) |
| `prior_work_factor` | **2.0** | Same shape as vanilla `*2+1`, configurable |
| `require_ni_repair_ingredient` | **false** | Demo keeps vanilla repair materials; flip on + fill `repair_ingredient_ni_ids` for NI-only |
| `material_repair_extra_cost` | **2** | Flat extra levels on material repairs |

So: **higher rename cost + capped repair + no enchant-book combining**. NI-only repair ingredients are supported but off in the demo.

### Commands

- `/coreanvil check` / `/coreanvil reload`

---

## Related world rules (not block edits)

See `CoreWorldRules`:

- Villager trading fully disabled (no UI, recipes cleared — no trade items generated).
- Vanilla **mob item drops** cleared on death; XP kept by default; MythicMobs should grant NI rewards separately later.
- **Blocks not modified.**

---

## Rebuild / run

See `STATUS-enchant-world.md`.
