# 状态 · D286：成长诚实 — 仅主线重打词缀精英

**日期：** 2026-10-07（上海时间）  
**上游：** 批 A · DESIGN playfeel §8-4 · CoreRpg **1.65.81**（本窗零 jar）· bv **58**

## 人话

天赋二排与猎缀类词条本来就只在主线普通重打的词缀精英上生效；文案以前写「仅主线重打生效」不够醒目。本窗统一成 **「仅主线重打词缀精英」**，并写清首通/挑战/团本/深渊无效。**不动 mods / 数值。**

## 改了什么

| 处 | 改动 |
|---|---|
| `ember-v1-growth.yml`（两份） | 第 2 排 theme / t2a–c good / 猎缀·抗缀·裂身 note 统一短语 |
| `ember_p1_spec.yml` / `_reroll.yml` | Open tell + 说明区诚实条 |
| `ember_p1_gear.yml` / `ember_hub.yml` | 入口旁注 |
| 设计稿 §8 | 窗 1–4 标已施工；窗 5 仍待另派 |

## 不动

- mods / balance / 六槽 / Pack / VIP
- CoreRpg 版本号（零 jar）

## 验收

- `rg '仅主线重打词缀精英'`
- play 短重启以重载 growth.yml + `trmenu reload`
