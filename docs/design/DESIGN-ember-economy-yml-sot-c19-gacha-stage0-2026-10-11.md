# DESIGN · C19 CoreGacha Stage0 盘点（2026-10-11 · D1022）

> **状态：文档 only · 不改 gacha 数值 · 不碰 Cosmetics / C15/C16。**  
> **上游：** Stage1 coverage；`EmberEconomy` C19 OUT；`plugins/CoreGacha/`.

## 1. 归属

| 面 | 住处 | CoreRpg 职责 |
|----|------|----------------|
| 抽券消耗 | `CoreGacha/gacha.yml` `cost_per_pull: 1` | 登记 C19 标签；**不发不扣** |
| 日帽 | `daily_pull_cap: 50` | 同上 |
| 券发放 | CoreGacha `config.yml` tickets → S37 | 标签 |
| 产出外观 | gacha items → S38 | OUT / 化妆品暂停 |

## 2. 现状（2026-10-11 实测）

```
cost_per_pull: 1
daily_pull_cap: 50
```

C19 `model(OUT)` — 正确；**禁止**为绿测改 FULL。

## 3. 以后若开 Stage1（非本窗）

- 仅当 CoreGacha 提供可读 API / 或明确同意把 `cost_per_pull` mirror 进 `ember-v1-economy.yml` 且 CoreGacha 同读。  
- 本服 Cosmetics 暂停令仍有效 → **不开 C15/C16**。  
- 任何数值变必须独立 tip + p1sim 无关（外观）。

## 4. 本窗产出

文档落地；ARCH / Stage1 coverage 互链。无 jar 行为变化（C19）。
