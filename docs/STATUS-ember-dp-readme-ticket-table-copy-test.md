# B2.67 · DP README 票表文案 · 纯静态薄验收测报

- **总评：PASS**
- **测岗：**余烬-测试岗执行器
- **测时：**2026-09-30 20:28 Asia/Shanghai（CST）
- **施工 tip SHA：**`32ce94f`（`32ce94fc98d0523d24769d80ae566145f1f20221` · `docs: B2.67 update DungeonPlus ticket table rules`）
- **批准 tip：**`7195926` · 设计 tip：`f4d646e` · tip 文档 `docs/design-ember-dp-readme-ticket-table-copy.md`
- **范围：**仅静态核对 `plugins/DungeonPlus/README-ember-dungeons.md` 次数表三行「规则」列（L35–L37）；未改配置、未长测、未挑刺、未起服

## 各点对照

| # | PASS 条件 | 结果 | 旁证 |
|---|-----------|------|------|
| 1 | `git show 32ce94f --stat` 仅该 README（+3/−3） | **PASS** | 1 file · `6 +++---` · numstat `3 3` |
| 2 | 票表三行规则列恰为 tip §2；ID/显示名不变 | **PASS** | Python 逐字 `EXACT True`；diff 仅规则列三格 |
| 3 | 无「日发 3」「周发 1」「日发 1」「票已废」「B0.1 已清」 | **PASS** | 违禁词 rg 无匹配（exit 1） |
| 4 | DEBT/发放句/L4/mail/地牢 YAML 本提交零动 | **PASS** | commit 仅 README；L4/DEBT/发放相对父树不变；无 yml/mail |

## 三行规则列原文

```text
| `ticket_ember_daily` | 余烬日票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
| `ticket_ember_weekly` | 余烬周票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
| `ticket_ember_abyss` | 余烬深渊票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
```

与 tip §2 荐案逐字一致（`EXACT True`）；NI ID / 显示名相对 `32ce94f^` 未改。

## 违禁词 rg

```text
$ rg -n '日发 3|周发 1|日发 1|票已废|B0\.1 已清' plugins/DungeonPlus/README-ember-dungeons.md
(no matches; exit 1)
```

## `git show 32ce94f --stat`

```text
commit 32ce94fc98d0523d24769d80ae566145f1f20221
Author: Picadoo <Picadoo@users.noreply.github.com>
Date:   Wed Sep 30 20:27:27 2026 +0800

    docs: B2.67 update DungeonPlus ticket table rules

 plugins/DungeonPlus/README-ember-dungeons.md | 6 +++---
 1 file changed, 3 insertions(+), 3 deletions(-)
```

numstat：`3	3	plugins/DungeonPlus/README-ember-dungeons.md`

## 旁证摘要

- 施工 diff 仅 L35–L37 规则列：旧「日发 3 / 周发 1 / 日发 1」口吻 → tip §2 遗留票物 / DP 入场条件；现行次数=体力
- `git show 32ce94f --name-only` 仅 `plugins/DungeonPlus/README-ember-dungeons.md`
- L4 相对 `32ce94f^` 不变：`维护备忘：玩家入口=TrMenu 点击进本（日/周/深渊/团本/精英）· 体力扣次；进本门控仍挂`
- DEBT 段与发放句相对父树不变；物品草案路径不变
- 本提交未触及 `plugins/DungeonPlus/dungeon/**`、mail、票 YAML
- 纪律：纯静态；未宣称 B0.1 已清；本岗未改配置

## 阻塞点

无。

## 纪律自检

- 纯静态；禁长测/挑刺/起服 — 已遵守
- 本岗不改配置 — 已遵守（仅写本测报）
- 勿宣称 B0.1 已清 — 已遵守
