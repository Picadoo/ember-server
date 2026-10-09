# 余烬 · D373：使魔闭环诚实薄抽（P2–P4）· 线上活菜单验收

- **性质：观察期薄抽验 · 使魔出战/收回/投喂 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-pet-loop-honest-d373-2026-10-10.md`](STATUS-ember-pet-loop-honest-d373-2026-10-10.md) · 菜单 tip **`03e83b4e`** · `trmenu`+`ni` reload 已做
- DESIGN：[`DESIGN-ember-pet-loop-honest-2026-10-10.md`](../design/DESIGN-ember-pet-loop-honest-2026-10-10.md) §2.6
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **827864** · jar **`1.65.101-d335.local`**
- 测号：`D373PetNo`（P3）· `D373Pet`（P2/P4）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d373-pet-loop-spot/`（主结果 `99-results.json`；菜单 dump `11/22/30-*.json`；出战/收回/投喂 `23/24/31-*.json`）
- 执行：2026-10-10 03:03 CST
- **结论：PASS（P2–P4 全绿；P1/P5 静态旁注仍绿；开关未动）**

## 人话

无宠新号点出战：提示「没有可出战的使魔」，菜单不崩。已解锁灰灵后，使魔页左键出战见盔甲架跟随、右键收回消失；有魂尘点投喂 Lv.1→2。三开关与 bv62 抽前抽后未动。观察不关。

## 验收表（DESIGN §2.6）

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **P2** | 已解锁 → 左键出战出现盔甲架使魔；右键收回消失 | **PASS** | 左键 chat「出战 余烬灰灵」；近距新 armor_stand；右键「使魔已收回」。`23-p2-after-summon.json` / `24-p2-after-dismiss.json` |
| **P3** | 无解锁无蛋 → 点出战：现网失败提示；不崩菜单 | **PASS** | 「没有可出战的使魔。先 unlock 或持蛋 summon。」；菜单仍开。`12-p3-after.json` / `11-p3-menu.json` |
| **P4** | 有魂尘 → 点投喂仍升级（回归） | **PASS** | 「消耗魂尘×1 · 余烬灰灵 Lv.1→2」；list Lv.2。`31-p4-feed.json` |
| P1（旁注） | 菜单无「挂机副产」；写生活·钓鱼 | **PASS（静态）** | `ember_pet.yml` lore/Open tell |
| P5（旁注） | NI 魂尘无挂机/副本副产 | **PASS（静态）** | `mat_ember_soul_dust` lore「生活·钓鱼兑换」 |
| 开关 | 本号未拧 enabled/migrate/set_bonus / bv / jar | **PASS** | 抽前/后三 true · bv62。`00-precheck-switches.json` / `90-endstate.json` |

## 手法与注记

- mineflayer `D373PetNo` / `D373Pet` + `lib/proxy-login`；脚本 `/workspace/tmp/d373-pet-loop-spot/d373-spot.js`（亦存 `mineflayer-tests/d373-spot.js`）
- 开菜单：console `trmenu open ember_pet <name>`（非 op 无 `/trmenu open` 权限；验收点击仍走 TrMenu 左/右键与投喂格）
- 造条件（管理/console）：`ni give` 蛋/魂尘；`/corerpg pet unlock`（P2 前置）；**未**教玩家打裸 summon/dismiss/feed
- 盔甲架：协议侧 customName 偶发未同步；以出战 chat + 近距 armor_stand 新增 + 收回 chat 三联认定
- **未**改 yml / jar / set_bonus / bv / afk.tiers；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**≠挂机加魂尘**；**≠开 R / K3**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 827864 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D373 施工） | 03e83b4e |
| 抽时 HEAD（开跑前） | 761b0678 → 跑中他号推进；本 STATUS 另 commit |
| 分支 | main |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D373 薄抽 PASS** | [ ] |
| P2–P4 | [ ] PASS（测试） |
| 左出战盔甲架 / 右收回 | [ ] |
| 无宠失败提示且菜单不崩 | [ ] |
| 魂尘投喂升级回归 | [ ] |
| 零改 yml·开关·jar·bv·afk | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

---

*D373 薄抽 · P2–P4 PASS · 测号 D373PetNo/D373Pet · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d373-pet-loop-spot/`。*
