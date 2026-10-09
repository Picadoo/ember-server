# 状态 · D333：工坊手持甲菜单诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-forge-armor-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-forge-armor-honesty-need-design-2026-10-09.md) @ `5564ea55` · DESIGN [`DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D333** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_forge.yml` only** · jar / CoreRpg Java / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / ember_set / 价表公式 / D332 清单 **未动**

## 人话

手持甲打开工坊时，强化/升阶/精工/成色/互换格会灰显并标「不可用」，点了只短 tell 拒因，不再发养成命令。分解旁不再写整胚「1/2/3」，改成白板零头（0.1×阶）说明，预览产量行仍用现网 PAPI。刃/护符路径保持 D307 费用同屏。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge.yml` | E/e U/u R/r Q/q S/s：`icons` 条件 `check papi %corerpg_p1_held_is_armor% == 1` priority 5 → 灰显 + 不可用 + ARMOR_REFUSE lore + **仅 tell、无** enhance/upgrade/refine/quality/swap 命令；非甲保持 D307。分解 D/d：去「胚料 1/2/3」，改白板零头说明；保留 `%corerpg_p1_held_dismantle_yield%` 与 dismantle 预览命令 |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-forge-armor-honesty` → 已批 A · 已施工 |
| jar / CoreRpg Java / bv / set_bonus / ×0.97 / ember_set / 价表 | **未动** |

## 菜单摘要

| 格 | 手持甲（held_is_armor==1） | 非甲 |
|----|---------------------------|------|
| E/e 强化 | 灰 pane · 「不可用」· tell 拒因 · **无** enhance 命令 | D307 预览/确认 |
| U/u 升阶 | 同上 · **无** upgrade | D307 |
| R/r 精工 | 同上 · **无** refine craft | D307 |
| Q/q 成色 | 同上 · **无** refine quality | D307 |
| S/s 互换 | 同上 · **无** swap | D307 |
| D 分解 | 静态 lore：掉落件；甲→白板零头（0.1×阶）· 预览 PAPI 保留 · dismantle 命令可留 | 同（静态文案已去 1/2/3） |
| d 提示 | 补「甲为白板零头…非整胚 1/2/3」 | — |

## PAPI 依赖

| 键 | 状态 |
|----|------|
| `%corerpg_p1_held_is_armor%` | **条件已写入菜单**；**待插件岗上线**后灰显分支才生效。上线前：非甲路径正常；手持甲时仍走默认格（命令层仍拒养成），但视觉闸未开 |
| `%corerpg_p1_held_*_cost%` / `*_lack%` / `held_dismantle_yield%` | 现网已有（D307）；甲态已回 ARMOR_REFUSE / 白板产量 |

**STATUS 注记：** 灰显闸 **待插件 `held_is_armor` 上线后生效**；静态分解文案（去 1/2/3）**本号已生效**。

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| S1 | 养成格甲分支无 enhance/upgrade/refine/quality/swap 命令 | 对照 · PASS |
| S2 | 分解静态无「胚料 1/2/3」；有白板零头说明 | `rg` · PASS |
| S3 | 保留 `%corerpg_p1_held_dismantle_yield%` | 对照 · PASS |
| S4 | 非甲 D307 费用同屏仍在 | 对照 · PASS |
| S5 | ×0.97 / set_bonus / jar / 价表 / ember_set / CoreRpg Java 未改 | 本号未触 · PASS |
| YAML | 可解析 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (113 ms)`（约 19:44 CST）。玩家重开工坊即可见新文案；甲态灰显待 `held_is_armor` PAPI 进 jar 后生效。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · CoreRpg Java（本号）· K3 施工 · ember_set · 价表/分解公式 · D332 测试轨 · 样本 R / Pack6 / 天赋 / 灰印

---

*D333 批 A·M · tip `5564ea55` · 显示 only · 观察期薄窗 · held_is_armor 待插件。*
