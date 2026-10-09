# 余烬 · D333 工坊手持甲诚实 · 线上薄抽验（余烬-测试 · 2026-10-09）

- **性质：观察期薄抽验 · 显示/菜单闸 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-forge-armor-honesty-d333-2026-10-09.md`](STATUS-ember-six-slot-forge-armor-honesty-d333-2026-10-09.md) · jar tip **`7804b6bd`** · 菜单 tip **`ee5217d6`**
- DESIGN：[`DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md) §2
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **568465** · jar **`1.65.100-d333.local`** sha256 `4932eaa8…`
- 测号：`D333Spot2`（经代理 25565；v1 用过 `D333Spot`）；**未动真人档**；login/proxy/MariaDB 未动
- 证据：`/workspace/tmp/d333-forge-armor-honesty-spot/`（主结果 `99-results-v2.json`）
- 执行：2026-10-09 19:51–19:54 CST
- **结论：PASS（分项全绿）**

## 人话

手持甲开工坊：养成格（强化/升阶/精工/成色/互换）全部灰显「不可用」，点强化只收到拒因「护甲随护符成长…」，**没有** enhance 等养成命令。分解文案是白板零头（0.1×阶），**没有**「胚料 1/2/3」。`%corerpg_p1_held_is_armor%` 甲=1 / 刃=0。换刃后 D307 费用同屏恢复。开关与 bv 未动。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **1a** | 手持甲 · 养成格灰显/不可用 | **PASS** | 10 格「… · 不可用」；0 存活养成格。`05-forge-armor-v2.json` / H3 |
| **1b** | 点养成格无 enhance 等命令 | **PASS** | 点「强化 · 不可用」→ tell `护甲随护符成长…`；无强化成功。H5 |
| **1c** | 分解 lore 白板零头、无「胚料 1/2/3」 | **PASS** | 静态 yml + 窗口 lore「甲→白板胚零头（0.1×阶）」「非整胚 1/2/3」。H4 / S1 |
| **1d** | `%corerpg_p1_held_is_armor%` == 1 | **PASS** | `/papi parse me` → `1`。`04-papi-armor-v2.json` |
| **2a** | 换刃 · D307 费用同屏恢复 | **PASS** | 强化/升阶/精工/成色预览均有「本次：」+「与预览一致」+碎片费用。B4 / `07-forge-blade-v2.json` |
| **2b** | 换刃 · `held_is_armor=0` | **PASS** | `/papi parse me` → `0`；养成格恢复可点。B1 / B3 |
| **3** | 开关 set_bonus/enabled/migrate / bv62 未动 | **PASS** | 始末 `true/true/true` · bv=62 · play **568465**。S5 / E1 / `00-precheck-v2.json` |

## 静态扫（`ember_p1_forge.yml` @ ee5217d6）

| 项 | 结果 |
|----|------|
| `held_is_armor == 1` 条件分支 ≥10 | PASS（10） |
| 甲分支内无 `command: corerpg p1 enhance\|upgrade\|refine\|quality\|swap` | PASS |
| 非注释 lore 无「胚料 1/2/3」；有白板/零头/0.1×阶 | PASS |

## 手法与注记

- mineflayer `D333Spot2` + `lib/proxy-login` / `lib/console`；`/trmenu open ember_p1_forge` dump
- 手持件：`corerpg p1 give scorch head|blade 1 0 0 0`（**source=admin**）。本用例验**菜单闸 + held_is_armor**；分解预览对 admin 甲显示「这件护甲不能分解」（命令拒非 drop，符合设计）。静态/窗口 lore 已对齐白板零头口径。**未**用 admin give 冒充 drop 分解实机（那是 D332 轨）。
- **未**改 yml / jar / set_bonus / bv；login/proxy/MariaDB 未动
- 旁注：白板经济模型未覆盖（对齐 D318/D332）

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 568465 |
| jar | 1.65.100-d333.local · sha256 `4932eaa8274385df5448243124ce2394d05a5625645026f61a0cbefc9a0b9c45` |
| 菜单 tip | ee5217d6 |
| jar tip | 7804b6bd |

---

*D333 薄抽验 PASS · 测号 D333Spot2 · 证据 `/workspace/tmp/d333-forge-armor-honesty-spot/` · 未改开关。*
