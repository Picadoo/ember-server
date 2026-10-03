# B2.65 · DP README 进本文案 · 纯静态薄验收测报

- **总评：PASS**
- **测岗：**余烬-测试岗执行器
- **测时：**2026-09-30 20:19 Asia/Shanghai（CST）
- **施工 tip SHA：**`a6d0851`（`a6d08513a075f3b97936df5b5ba2ffe7d5f010ae` · `docs: B2.65 update DungeonPlus README entry note`）
- **批准 tip：**`48003da` · 设计 tip：`2c9b58c` · tip 文档 `docs/design/design-ember-dp-readme-enter-copy.md`
- **范围：**仅静态核对 `plugins/DungeonPlus/README-ember-dungeons.md` L4；未改配置、未长测、未挑刺、未起服

## 各点对照

| # | PASS 条件 | 结果 | 旁证 |
|---|-----------|------|------|
| 1 | `git show a6d0851 --stat` 仅该 README（+1/−1） | **PASS** | 1 file · `2 +-` · numstat `1 1` |
| 2 | L4 恰为 `维护备忘：玩家入口=TrMenu 点击进本（日/周/深渊/团本/精英）· 体力扣次；进本门控仍挂` | **PASS** | Python 逐字 `L4_EXACT True` |
| 3 | 无 `/corerpg enter`、无「扣票」、无「B0.1 已清」宣称；保留 TrMenu + 体力扣次 + 门控仍挂 | **PASS** | 违禁词 rg 无匹配（exit 1）；L4 含三保留口径 |
| 4 | 地牢 YAML / 表 / 票表 / L5 本提交零动 | **PASS** | commit 仅 README；L5 相对父树不变；无 yml |

## L4 原文

```text
维护备忘：玩家入口=TrMenu 点击进本（日/周/深渊/团本/精英）· 体力扣次；进本门控仍挂
```

与 tip §2 荐案逐字一致（`L4_EXACT True`）。

## 违禁词 rg

```text
$ rg -n '/corerpg enter|扣票|B0\.1 已清' plugins/DungeonPlus/README-ember-dungeons.md
(no matches; exit 1)
```

保留口径（L4）：含 `TrMenu`、`体力扣次`、`门控仍挂`。

## `git show a6d0851 --stat`

```text
commit a6d08513a075f3b97936df5b5ba2ffe7d5f010ae
Author: Picadoo <Picadoo@users.noreply.github.com>
Date:   Wed Sep 30 20:18:08 2026 +0800

    docs: B2.65 update DungeonPlus README entry note

 plugins/DungeonPlus/README-ember-dungeons.md | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
```

numstat：`1	1	plugins/DungeonPlus/README-ember-dungeons.md`

## 旁证摘要

- 施工 diff 仅 L4：旧 `玩家进本（B0.1）：\`/corerpg enter …\`（TrMenu 按钮）· NI id 扣票` → 新维护备忘口径
- `git show a6d0851 --name-only` 仅 `plugins/DungeonPlus/README-ember-dungeons.md`
- L5 相对 `a6d0851^` 不变：`管理/测本：\`dp start-console <玩家> <DungeonId>\` · 灾厄测本 \`/dp start EmberCalamity\`（须标仅测试）· 盟 Boss：\`/corerpg guild boss\``
- 本提交未触及 `plugins/DungeonPlus/dungeon/**`、票表或其它 YAML；表段未出现于 diff
- 纪律：纯静态；未宣称 B0.1 已清；本岗未改配置

## 阻塞点

无。

## 纪律自检

- 纯静态；禁长测/挑刺/起服 — 已遵守
- 本岗不改配置 — 已遵守（仅写本测报）
- 勿宣称 B0.1 已清 — 已遵守
