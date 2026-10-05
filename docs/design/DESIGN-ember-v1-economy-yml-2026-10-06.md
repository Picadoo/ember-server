# DESIGN · `ember-v1-economy.yml` 真源草案（REG §6.3 · D220 文档）

> **状态：草案，只是离线文档。** 没有改 CoreRpg / yml 部署 / p1sim / 重启（skill-d219 刚把 CoreRpg 发到 1.65.49；本窗不抢部署锁）。
> **上游：** `REG-ember-source-sink-cap-2026-10-05.md` §6.3；`EmberEconomy`（D213 登记 + D215/D216/D218 grant/spend 路由）。
> **目的：** 把现在散落在 Java 常量（`BASE_*`、`CLEAR_MARKS`、`MARKS_PER_EXCHANGE`…）与多份 yml（`ember-v1.yml` / `ember-v1-runs.yml` / `ember-v1-growth.yml` / `ember-v1-festival.yml`）里的**数量与上限**收成一份可读真源，供运行时与 `tools/p1sim/rules.py` 同读。本阶段**不改任何数值**（bv57）。

---

## 0. 给服主的话

今天发币 / 扣币已经大多走了 `EmberEconomy.grant*` / `spend*`（S01–S03 / S23–S24 / C03–C14 等），但**数量**仍写在代码金样或业务 yml 里。下一刀是：新建 `plugins/CoreRpg/ember-v1-economy.yml`，表里每一行对应一个 `Sxx`/`Cxx` 的 `golden` 键；Java 启动时加载进 `EmberEconomy`；单测继续钉金样；p1sim 只读这一份。数值不动，只搬家。

**还没路由完、本草案只占位的：** C18 国庆商店、S22 挂机庭发放入口、EmberDelivery 扣币、C15/C16（化妆品 DEFER）。实现窗另开 D 行。

---

## 1. 文件形态（提案）

```yaml
# ember-v1-economy.yml — P1 economy amounts + caps (REG §6.3)
# balance_version must match ember-v1.yml; bump only when numbers change.
balance_version: 57

sources:
  S01:  # 主线/挑战/深渊结算基线 — 现 EmberRunRules.BASE_*
    coin: 300
    shard: 24
    bone: 6
    core: 2
    xp: 120
    # marks / gear 按图阶另表或继承 runs yml
  S02: { treasure_coin: 100 }
  S03: { elite_coin: 10, elite_core: 1 }
  # … S04–S32：先从 EmberEconomy.golden 导出，缺键 = 该行尚无 amount() 路由

sinks:
  C07: { marks: 8 }
  C09:
    learn_row1: 800
    learn_row2: 2000
    learn_row3: 4000
  C10: { respec: 2000 }
  C14: { coin: 10 }
  # C03–C06 / C08 / C11–C13：价在 forge/abyss/growth yml；本文件可写 mirror 或 $ref
  C18: {}   # 国庆商店 — 待 C18 路由窗填 fest_coin / coin / badge 价目

caps:
  afk_kills_day: 2400
  afk_offline_kills_day: 1200
  # … 其余 REG §4 上限，先抄表
```

加载规则：

1. 缺文件 → 回退到今天的 Java `golden`（兼容；单测仍绿）。
2. 有文件 → `EmberEconomy.amount(id,key)` 读 yml；与内置 golden 不一致 → 启动 `WARNING` + 单测失败（防漂移）。
3. `tools/p1sim/rules.py` 增加 `economy_yml` 读取；`--mods` / skillkit 不改数值键名。

---

## 2. 分阶段（建议下一实现窗）

| 阶段 | 内容 | 碰 CoreRpg？ |
|---|---|---|
| **E0（本稿）** | 定形态 + 把现有 `EmberEconomy.golden` 键列成清单 | 否 |
| **E1** | 生成 `ember-v1-economy.yml`（从 golden 导出）、加载器、单测双钉（yml ↔ golden）；数量不变 | 是（小版本） |
| **E2** | C18 国庆商店 spend 走 `EmberEconomy`；S22 挂机庭 grant 单入口；Delivery debit 标签化 | 是 |
| **E3** | p1sim 改读 yml；徽记账户进模型（REG §5 #1） | p1sim 车道 |

---

## 3. 与未完 ARCH 的边界

| 项 | 状态 | 本草案 |
|---|---|---|
| C03–C13 / C14 / S01–S03 / S23–S24 | 已路由（D215–D218） | E1 搬家数量 |
| **C18 国庆商店** | 登记 PART，代码未走 spend | E2；价目在 `ember-v1-festival.yml` |
| **S22 挂机庭** | 登记 FULL，发放未统一入口 | E2；`EmberAfkService` → `grant*` |
| EmberDelivery 扣币 | 部分 grant 已路由 | E2 对称 debit |
| C15 / C16 化妆品 | DEFER | 只占位 |
| 徽记 p1sim | TODO（D216） | E3 / p1sim 车道 |

---

## 4. 验收（E1 落地时）

- [ ] 部署目录有 `ember-v1-economy.yml`，`balance_version: 57`
- [ ] `EmberEconomyTest`：yml 金样 = 内置 golden = 现网行为（FreshQ 冒烟数量不变）
- [ ] 缺文件仍可启动（回退）
- [ ] 不改 bv、不改资产路径 → 不跑 persist-roundtrip
