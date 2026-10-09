# 余烬 · D402：使魔选定盘 · 线上薄抽

- **性质：观察期薄抽 · `ember_pet` L→`ember_pet_select` · 点灰灵/烬火换宠出战 · 未改配置 / 未动开关 / 未关观察**
- 上游施工：[`STATUS-ember-pet-select-menu-d402-2026-10-10.md`](STATUS-ember-pet-select-menu-d402-2026-10-10.md) tip **`3852f07c`** · DESIGN [`DESIGN-ember-pet-select-menu-2026-10-10.md`](../design/DESIGN-ember-pet-select-menu-2026-10-10.md)
- 现态（抽测窗）：bv**69** · `enabled/migrate/set_bonus=true` · jar **`1.65.111-d401.local`** · 菜单 **73** · `daily_kills=2400`
- 测号：`D402Pet`（双宠解锁）· `D402PetNo`（未解锁）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d402-pet-select/`（`10-menu-static` · `20–24` · `30-no-unlock` · `50–59-v2-retest` · `90-endstate` · `99-results` · `run.log` / `run-v2.log`）
- 执行：2026-10-10 06:36–06:40 CST（与 D401 周目标抽并行；未停服；`trmenu reload` 热更）
- **结论：PASS** — L→选定盘；已解锁点烬火/灰灵切换 active+出战；未解锁无假「已拥有」且插件拒门；玩家菜单路径无教手打 `/corerpg pet`；E 出战/收回仍在；afk/Stage2/gate_daily 零改

## 人话

使魔页「选定使魔/换一只」打开薄选定盘（标题「使魔 · 选定」），不再刷 list。已解锁双宠：点烬火 → PAPI/同屏「余烬烬火」并出战；再点灰灵切回。未解锁号点灰灵得诚实拒门「未解锁，且背包无对应使魔蛋」。E 右键收回 / 左键出战仍绿。三开关仍 true，挂机日表未抬，观察不关。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | tip `3852f07c` + 73 菜单 + 静态 L/A/C/E | **PASS** | `10-menu-static.json`；reload「73 个菜单已加载 (94 ms)」· `11-trmenu-reload.json` |
| **V1** | L→选定盘（非 list） | **PASS** | L 名「选定使魔 / 换一只」→ title「使魔 · 选定」· A灰灵/C烬火 · `20/21/53` |
| **V2** | 已解锁点灰灵/烬火 → active+出战 | **PASS** | 复测：点 C → `余烬烬火` + chat「切换烬火…/出战 余烬烬火」；点 A → `余烬灰灵` · `51/52/59-v2-retest.json` |
| **V3** | 未解锁无假「已拥有」+ 拒门 | **PASS** | 选定盘无假拥有字；`D402PetNo` 点灰灵 →「未解锁，且背包无对应使魔蛋」· `30-no-unlock.json` |
| **V4** | 玩家菜单路径无教手打 `/corerpg pet` | **PASS** | tell/lore/name 无斜杠教学；L 去 list · `10-menu-static` / live `V1/V4` |
| **V5** | E 出战/收回仍在 | **PASS** | 右键收回「使魔已收回」· 左键出战「出战 余烬灰灵」· `24-E-summon-dismiss.json` |
| **V6** | 零改 afk / Stage2 / gate_daily / pet.yml | **PASS** | 抽窗 pre=post：三 true · `daily_kills=2400` · yml/runs/pet md5 不变 · `00/90` |

## 旁注

1. **首轮 V2 烬火**：click 事务超时且断言过宽（lore 含「烬火」字样）曾假绿；已用 `d402-v2-retest.js` 紧断言重测 **真 PASS**（PAPI `余烬烬火` + 出战 chat）。
2. **插件 summon 成功句**仍带 `/corerpg pet dismiss 收回`（Java `PetService` 既有 tell，非 TrMenu 玩家路径；本号菜单无教斜杠）。list 教斜杠收口仍属施工可选后置。
3. 选定盘 lore 有元注释「禁假写「已拥有」」——**非**宣称已拥有；未解锁仍走拒门。
4. 抽后并行 D403 将 `balance_version` 69→70（非本号）；本抽窗内 bv69、菜单/开关未拧。

## tip / 产物

| 项 | 值 |
|----|-----|
| 施工 tip（D402） | **`3852f07c`** |
| `ember_pet.yml` md5 | `f45255008aa805c7fd9dad0bc769c417` |
| `ember_pet_select.yml` md5 | `a4399c32b657bb2c7a21ccb355a2a48f` |
| `pet.yml` md5 | `ee8bef6eef1ee8e11fff336c79185a02`（未变） |
| jar（抽测窗） | `1.65.111-d401.local` |
| 分支 | main |

## 手法与注记

- mineflayer `D402Pet` / `D402PetNo` + `lib/proxy-login`；脚本 `d402-spot.js` + `d402-v2-retest.js`
- 开菜单：console `trmenu open ember_pet|ember_pet_select <名>`；选定格点击 = `corerpg pet summon pet_ember_*`
- 造条件：`ni give` 使魔蛋 + `/corerpg pet unlock`（测号）；**未**改 feed / afk / Stage2 / gate_daily / jar / 菜单内容（只 `trmenu reload`）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB **未碰**；**未**反复停服
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）

## 结束态（抽测窗）

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version（抽窗） | 69 |
| daily_kills | 2400 |
| jar | 1.65.111-d401.local |
| tip | 3852f07c |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D402 薄抽 PASS** | [ ] |
| tip `3852f07c` | [ ] |
| L→选定盘 · 点换宠出战 · 未解锁诚实 | [ ] |
| 零改 afk·Stage2·gate_daily·配置 | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

---

*D402 薄抽 · PASS · tip `3852f07c` · jar `1.65.111-d401.local` · 证据 `/workspace/tmp/d402-pet-select/` · ≠关观察。*
