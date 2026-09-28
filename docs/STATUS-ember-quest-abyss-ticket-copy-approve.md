# STATUS · B2.11 quest abyss-ticket copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-28 23:48 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `4a2a671` · `docs/design-ember-quest-abyss-ticket-copy.md` (verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | live + src `quest.yml` Q1/Q2 player-visible strings only |
| Excluded | calamity OP #6; no quest YAML changed in this approval |

## Conclusion

Approved **A** for B2.11. Replace only the two approved player-visible strings in both quest paths. This approval changes documentation only; the plugin role performs the YAML replacement and reload separately.

**Hard boundary:** do not alter quest step structure (`type` / `event` / `count` / `mobs` / `complete_on` / `floor`), `items` keys or quantities, stamina costs (`costs.*` / `free_tickets`), entry logic, TrMenu, DP/MM/loot, or calamity. Do not do calamity OP #6.

## A scope — exact Q1/Q2 replacements

| # | File | Current player-visible string | Approved player-visible string |
|---:|---|---|---|
| Q1 | `plugins/CoreRpg/quest.yml` and `CoreRpg/src/main/resources/quest.yml` · 卷4 kill hint | `打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` | `打开枢纽菜单 → 深渊（耗体力）· 本次结算也算完成` |
| Q2 | same two files · 卷8 talk done | `§6灰烛：§f票还是一天一张。能走多深，就走多深。` | `§6灰烛：§f体力日回，能走多深就走多深。` |

Preserve the surrounding YAML, keys, step structure, formatting, and all non-string values. The live and src files must receive the same two string-only edits.

## Acceptance boundary

- `rg '深渊票|票还是一天一张' plugins/CoreRpg/quest.yml CoreRpg/src/main/resources/quest.yml` returns zero.
- The diff is limited to the four corresponding string lines (two replacements in each path).
- No changes to step structure, items, costs, entry logic, DP/MM/loot, TrMenu, or calamity.
- No new slash-command teaching; do not claim calamity OP #6 or other ticket debt is cleared.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准 tip `4a2a671` / `docs/STATUS-ember-quest-abyss-ticket-copy-approve.md` 施工 B2.11 方案 A。只改以下两个玩家可见字符串，并在 live + src 两份 `quest.yml` 做完全相同的字符串替换：

1. `plugins/CoreRpg/quest.yml` 卷4 kill hint：
   `打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` → `打开枢纽菜单 → 深渊（耗体力）· 本次结算也算完成`
2. `CoreRpg/src/main/resources/quest.yml` 卷4 kill hint：同上，同样替换。
3. `plugins/CoreRpg/quest.yml` 卷8 talk done：
   `§6灰烛：§f票还是一天一张。能走多深，就走多深。` → `§6灰烛：§f体力日回，能走多深就走多深。`
4. `CoreRpg/src/main/resources/quest.yml` 卷8 talk done：同上，同样替换。

完成后按服约定 reload quest / `corerpg reload`，并回报施工 tip、reload 结果与静态检查。验收：两路径均无 `深渊票` / `票还是一天一张`；diff 仅四条字符串行。

禁改：步骤 `type` / `event` / `count` / `mobs` / `complete_on` / `floor`，`items` 键与数量，体力 `costs.*` / `free_tickets`，进本逻辑，TrMenu，DP/MM/loot，任何灾厄配置；**不要做 calamity OP #6**；不要新增斜杠教学。

### 余烬-策划 — priority false FYI

FYI：B2.11 主线 quest「深渊票」两句已批准 **A**（design tip `4a2a671`，已在 `main` / `origin/main` 核验）。范围仅 live + src `quest.yml` 的 Q1/Q2：`需深渊票` → `耗体力`；`票还是一天一张。能走多深，就走多深。` → `体力日回，能走多深就走多深。`。已同步 backlog，当前待插件施工；插件完成后再派测试。calamity OP #6 不在本轮，步骤结构/items/cost 零改。
