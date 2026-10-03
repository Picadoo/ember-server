# STATUS · 余烬天梯 + PAPI（CoreRpg 1.3.4）

**日期：** 2026-09-13（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-guild-ladder.md` §2 · `docs/ember-holograms.md`  
**约束：** 未改 Paper；未做盟约/公会全系统

---

## 已落地

| 项 | 状态 |
|----|------|
| `LadderService` | **DONE** · power/abyss/speed 榜 · 60s 刷新 · `ladder.yml` |
| `EmberLadderExpansion` (`ember`) | **DONE** · HD 精确占位符名 |
| `CoreRpgExpansion` (`corerpg`) | **保留** |
| `PlayerData` powerScore / abyssBest / week / weeklyBestSec | **DONE** |
| `recordAbyssFloor(int)` | **DONE**（供深渊日后调用） |
| `/corerpg ladder` 命令面 | **DONE** |
| CoreRpg.jar **1.3.4** | **DONE** |
| `_golden` sync | **DONE** |

### PAPI

| 占位符 | 说明 |
|--------|------|
| `%ember_ladder_power_N_name%` / `_value%` | 战力 TOP N（1～10） |
| `%ember_ladder_abyss_N_name%` / `_value%` | 深渊 TOP N |
| `%ember_ladder_speed_N_name%` / `_value%` | 竞速秒 TOP N |
| `%ember_power_score%` | 本人战力展示分 |
| `%ember_abyss_best%` | 本人深渊最高层 |
| `%ember_weekly_best_sec%` | 本人周本最快秒 |

空位：`---` / `0`。

### 命令

| 命令 | 行为 |
|------|------|
| `/corerpg ladder` / `ladder me` | 本人战力/深渊/竞速 |
| `/corerpg ladder power\|abyss\|speed` | 打印榜 |
| `/corerpg ladder set abyss <玩家> <层>` | admin |
| `/corerpg ladder set speed <玩家> <秒>` | admin |
| `/corerpg ladder refresh` | admin 重建 |

### power_score

展示分 only（不乘伤害）。YAML `plugins/CoreRpg/ladder.yml`：base + enhance×8 + 孔填×5 + 天赋节点×3 + 誓约×10 + coin/100 cap。在线扫白名单装备 lore；离线 stub。

---

## jar

| 项 | 结果 |
|----|------|
| 路径 | `plugins/CoreRpg.jar` |
| 版本 | **1.3.4** |
| 大小 | **148366** bytes |

**Restart required** — 新类不可热重载。

---

## 未做

全盟约/公会系统 · 周结算外观发放 · EmberGuildBoss DP

---

## 验样（重启后）

```
/papi parse me %ember_power_score%   → 数值（非字面量）
/papi parse me %ember_ladder_power_1_name% → RpgBot（或 ---）
/corerpg ladder me
/corerpg ladder set abyss <玩家> 7
```

空位返回 `---` / `0`。`%corerpg_coin%` 仍可用。

**Restart required** — 已重启 Paper，CoreRpg **1.3.4** + PAPI `ember` 已注册。

