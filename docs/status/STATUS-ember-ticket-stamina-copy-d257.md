# 状态 · D257：票→体力旁记同步（打包配置 + 旧规格头注）

**日期：** 2026-10-07（上海时间）  
**上游：** D256（`1b8e88ed`）· CoreRpg 仍 **1.65.73**（不发版）

## 这扇窗做什么

线上 `cash.yml` / `progress.yml` 注释已按体力口径写过，但**打包进 jar 的 resources 副本**还留着「日票限购 / 扣票」。晶钻月卡旧规格全文仍像门票教程。本窗只改注释/文档。

## 改了什么

| 文件 | 改动 |
|---|---|
| `CoreRpg/src/main/resources/{cash,progress,warehouse}.yml` | 与线上注释对齐（日票→体力/遗留票物） |
| `plugins/CoreRpg/cash.yml` / `warehouse.yml` | 精英「周票」头注、白名单票物行加遗留说明 |
| `DESIGN-ember-cash-monthly.md` | D257 头注：现行=体力 |
| `DungeonPlus/README-ember-dungeons.md` | 票表列名强调遗留物名 |

## 不变

- 不改数值键/发奖逻辑；不停服；不开六槽；跳过 S0-9

## 下一扇候选

- 其它旁记，或 `DESIGN-ember-cash-monthly` 正文逐段历史化（更厚，另开窗）
