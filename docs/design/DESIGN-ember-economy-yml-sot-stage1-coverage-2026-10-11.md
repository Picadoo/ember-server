# DESIGN · yml-SoT Stage1 覆盖盘点（2026-10-11 · D1015）

> **状态：文档。** 数值未改。上游 Stage0 / Stage1b / D948–D1014。

## 已 `amount()` 读表的 sink（有 golden）

| ID | 面 | 窗 |
|----|----|----|
| C01 | 默认体力 | D996 |
| C02 | 团本 r01–r03 体力 | D999 |
| C03–C06 | 强化/升阶/精工/成色 | D969–D980 |
| C07 | 印记兑换 | D962 |
| C08 | 深渊层费 + fee_mark_coin | D981 |
| C09/C10 | 天赋学会/重置 | D999 |
| C11 | 洗练 base | D981 |
| C12/C13 | 烙印/调律 | D957 |
| C14 | 回复药 | 既有 |
| C18 | 国庆店价 | D1011（PART） |
| C20–C24 | 烙纹/锻/转化/宝石 | D948–D995 |

## 仍外源 / 暂停

- **C15/C16** 外观/扭蛋兑券 — **暂停令 · 跳过**  
- **C19** 扭蛋抽 — CoreGacha 外库 · Stage0 见 [`DESIGN-ember-economy-yml-sot-c19-gacha-stage0-2026-10-11.md`](DESIGN-ember-economy-yml-sot-c19-gacha-stage0-2026-10-11.md)  
- **modelGaps** 仍 NONE/PART/OUT — **禁止假 FULL**

## Stage1 状态 · D1023

**COMPLETE（P1 CoreRpg-owned golden sinks）。** C17 已 D1017–D1020 读表（model 仍 NONE）。

## Stage2（D1040）

发放真源 Stage0：[`DESIGN-ember-economy-yml-sot-stage2-grant-stage0-2026-10-11.md`](DESIGN-ember-economy-yml-sot-stage2-grant-stage0-2026-10-11.md)。大搬家未开。

## 下一 ARCH 薄债（Stage1 后）

1. source-map / ledger 健康扫  
2. p1sim modelGaps **stub 政策文档**（仍不假 FULL）  
3. C19 仅当 CoreGacha 同读 API 就绪再 tip

## D1031 追记

`ember-v1-economy.yml` / `ECONOMY_BV` 对齐 runs **bv89**（先前停在 60，p1sim rules.validate 红）。金额未改。
