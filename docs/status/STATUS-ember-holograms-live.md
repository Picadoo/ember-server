# STATUS · Ember 天梯全息 live create

**日期：** 2026-09-12 15:25（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-guild-ladder.md` §3 · `docs/ember-holograms.md`  
**约束：** 未改 Paper；未覆盖任何 jar；未手改 HD `database.yml`（仅 `/hd create` / `addline`）

---

## 总结果

| 项 | 结果 |
|----|------|
| **hd create（三板）** | **PASS** |
| 脚本 | `mineflayer-tests/hd-create-ember.js`（一次跑通） |
| Bot | OP `RpgBot` · `127.0.0.1:25565` · 1.12.2 offline |
| 日志 | `mineflayer-tests/logs/hd-create-ember.log` · `.json` |

---

## 板位（`/hd list`）

| 名 | 世界坐标（插件落点） | 行数 |
|----|----------------------|------|
| `ember_ladder_power` | world **-43, 68, 265** | 13 |
| `ember_ladder_abyss` | world **-39, 68, 265** | 8 |
| `ember_ladder_speed` | world **-35, 68, 265** | 8 |

站位目标：`-44/-40/-36 · 67 · 265`（AFK `-40 70 265` 一带）。HD 在地面自动上抬约 +1 Y；块坐标与 `/tp` 半格偏移符合 1.12 行为。

聊天确认样例：`You created a hologram named 'ember_ladder_power'.` · `Line added!`

---

## 菜单 / 文档

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_ladder.yml` | 展开 P/A/S/M/Open **tell**：板名、AFK 坐标、建议命令、PAPI |
| `docs/ember-holograms.md` | §6 备注 `%corerpg_coin%` **已工作**；天梯 `%ember_ladder_*%` 未接线 |
| `plugins/HolographicDisplays/README-ember-holograms.md` | 指向 docs + 脚本 |

热重载：`/trmenu reload`（TrMenu 也可能已自动载入）。

---

## PAPI 抽检

| 占位符 | 结果 |
|--------|------|
| `%corerpg_coin%` | **工作** → `/papi parse me` 返回 `118` |
| `%ember_ladder_*_%` | 未接线（全息行可先显示字面量/空） |

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · MythicMobs · DungeonPlus · 手改 `database.yml`

---

## 复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线
node hd-create-ember.js
```

脚本会先 `/hd delete` 再按 docs 重建三板。
