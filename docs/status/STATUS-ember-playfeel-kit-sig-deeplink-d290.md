# 状态 · D290：技能↔签名深链 + 团本炽愈提示

**日期：** 2026-10-07（上海时间）  
**上游：** DESIGN playfeel §3.3 构筑轻量 · CoreRpg **1.65.82**

## 为何这窗

批 A 体验窗做完后，构筑两页（技能组 / 签名传奇）仍互不跳转；玩家换形状看不到「会被签名盖掉」，团本只有 R01 写炽愈。选 §3.3-1+3：可感、已批、非 Pack / 非六槽 / 非文案债。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberGrowthService` | `SIG_FROM.skill` → `ember_skill_kit`；返回键「返回技能组」 |
| `ember_skill_kit.yml` | Y「签名传奇」→ `sig from skill`；I/D/E/F 写清 L07/L09/L13 覆盖 |
| `ember_p1_sig.yml` | N「技能组」→ `ember_skill_kit`；说明补覆盖句 |
| `ember_p1_adventure.yml` | R02 / R03 补「建议队伍里有炽愈」（对齐 R01） |
| 版本 | **1.65.81 → 1.65.82** |

## 不动

- 签名数值 / 形状数值 / 共享充能 / 守招搁置
- 六槽 · VIP/pass · Pack · Director 大分支
- 「推荐打法」整页（§3.3-2 · 另窗）

## 验收

- `rg 'from skill|返回技能组|SIG_FROM.put\("skill"' CoreRpg/`
- `rg '签名传奇|L13 蓄力|建议队伍里有炽愈' plugins/TrMenu/menus/ember_skill_kit.yml plugins/TrMenu/menus/ember_p1_sig.yml plugins/TrMenu/menus/ember_p1_adventure.yml`
- play Enabling **1.65.82**；`trmenu reload`；login/proxy 不停
