# 状态 · D284：付费口径诚实化（VIP/战令挡 · 月卡照常）

**日期：** 2026-10-07（上海时间）  
**上游：** 批 A · DESIGN playfeel §8-2 · D281 挡发币 · D282 月卡开 · CoreRpg **1.65.80** · bv **58**

## 人话

代码闸早就挡了勋阶日礼 / 战令补给·等级奖；月卡登录礼照常发。菜单却还在写「点领取」「每日补给」，玩家会以为能领。本窗只改**诚实文案**与灰字提示，**零发奖逻辑改动**。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember_vip.yml` / `ember_pass.yml` | 入口改「P1 暂停 / 不会到账」；Open tell 指向签到·周目标·外观/挂机 |
| `ember_shop.yml` | 战令/勋阶货架诚实；月卡写清「每日登录礼：币+体力」且与两轨无关 |
| `ember_hub_legacy.yml` / `ember_character.yml` | 同上口径 |
| `CashService` / `ProgressService` | 灰字统一「P1 下暂不发放旧…币礼 · 请用签到 / 周目标 / …」；`cmdVipShow` 在 P1 下不再广告 claim |
| 版本 | **1.65.79 → 1.65.80**（仅提示串） |

## 验收

- `rg '暂不发放旧|每日登录礼|P1 暂停' plugins/TrMenu/menus/`
- `mvn -o test -Dtest=CashCoinRulesTest` 仍绿（规则未改）
- play Enabling **1.65.80**；`trmenu reload`；login/proxy 不停（短重启 play）

## 不变

- 不暗开 vip claim / pass free / pass claim
- 月卡 `processMonthlyLogin` 仍发（D282）
- 六槽 / 数值 / Pack / bv58
