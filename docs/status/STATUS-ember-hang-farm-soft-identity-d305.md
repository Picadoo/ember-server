# 状态 · D305：挂机庭软身份 / 层可感差方案 M（W1a+W1b+W1c+W1d）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-hang-farm-soft-identity-2026-10-08.md`](../design/DESIGN-ember-hang-farm-soft-identity-2026-10-08.md) 方案 M（`02469f08`）  
**版本：** CoreRpg **1.65.95** · `balance_version` **60**（未抬）

## 人话

满额→去冒险（D285）已有；未满站场时四层仍像编号楼梯。本窗**零经济 / 零新挂机图 / 不抬 2400·离线25%·层表**：菜单名片+养什么 + 进层揭示 + 保守升层线索 + 降层/满额路径钉死——**不跑 p1sim**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `ember_p1_afk` 四层（锁/开）最上 `§d名片` + `§b养`；进层 chat 揭示；ActionBar 半行名片 |
| **W1b** | 保留 `death_stop`「去低一层」句；战况格降层白话；升层 cue 缺省开、可热关（`afk.feel.upgrade_hint`，≥40 杀且 ≥800 只/时且下一层已解锁且 0 阵亡 · 每场一次） |
| **W1c** | 满额仍 `capActionBar` / F「体力还在 · 去冒险」；名片不盖过满额 CTA |
| **W1d** | 枢纽挂机格半行 + HD `ember_afk_hub` 一行「四层名片见菜单」 |
| jar | `EmberAfkService` card/farm/enterReveal/upgradeHint；`AfkTierService` 进层揭示；**1.65.94 → 1.65.95** |
| 单测 | `softIdentity_D305_cardsMatchDesignAndTiers` |

## 不动

- `daily_kills` 2400 · 离线 25% / max 1200 · 各层每日量 · 怪 HP/ATK  
- Pack6 / 新挂机地图包 / 假平面空板  
- 天赋 / 灰印 HOLD / 守招 / 工坊 R / 事件 R/W / 调律 R  
- 方案 R（提示密度）**后置**  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：名片养签与 `ember-v1.yml` tiers（T1 无核心/胚料 · T2+核心 · T3+胚料 · T4 最高）一致；单测 PASS；满额文案仍含「去冒险」  
- 冒烟（live · **不跑 p1sim** · `mineflayer-tests/d305-hang-farm-identity-smoke.js`）：
  - 菜单 T1–T4 `§d名片` PASS；战况降层白话 PASS；枢纽「四层各有名片」PASS
  - 进 T1 / T4 chat 揭示 + ActionBar 名片半行 PASS
  - 菜单「去冒险」文案路径 PASS（D285 F 条件保留）；death-stop「去低一层」源码保留
- play Enabling **1.65.95**；`version CoreRpg` = 1.65.95；TrMenu reload 69 菜单；`corerpg reload` afk daily_kills=2400 offline=0.25  
- 回滚：`/workspace/backup/CoreRpg-1.65.94-pre-d305.jar` + 还原 afk/hub 菜单与 HD 行 + `feel:` 键

## 下一窗

- **R**（提示密度）**后置**；仅总控另批 M+R 时开  
- **禁** Pack6 / 抬封顶收益 / 假平面 / 天赋灰印续跑  

- **下一档硬债 tip：** [`STATUS-ember-next-hard-debt-hub-daily-routing-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-hub-daily-routing-need-design-2026-10-08.md)（枢纽「今天该打哪」日路由诚实 · 需策划）  
