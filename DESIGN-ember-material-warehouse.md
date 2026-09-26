# 余烬 · 材料仓库（单种近无限 + 可解锁格）

**日期：** 2026-09-13（Asia/Shanghai）  
**诉求：** 玩家仓库；**单种物品几乎无限堆**；**可解锁新格子**。  
**承接：** `DESIGN-ember-character-storage.md`（末影箱保留）；本文件替换「扩展页占位」。

---

## 1. 产品定案

| 项 | 定案 |
|----|------|
| 形态 | **材料仓（虚拟格）**，不是普通箱子 GUI 一格一堆 64 |
| 一格含义 | 绑定 **一种** NeigeItems `id`（或白名单内的材料 id）+ `amount`（long，上限默认 `2_000_000_000`） |
| 不可入仓 | 成品高伤装 / 带强化等级装备 / 孔石满配（防把毕业装当材料堆）；白名单为主 |
| 默认格数 | **8** 格（YAML 可调） |
| 解锁 | `/corerpg warehouse unlock`：扣 **余烬币**（默认 5000，阶梯上涨）或 **晶钻**（默认 28，可选） |
| 最大格 | **48**（可配） |
| 存取 | 手持 NI 物品：`deposit` 全堆入对应格（同 id 合并）；`withdraw <id|slot> [数量]` |
| 持久化 | MySQL 表 + YAML 回退字段；与现网 `storage: mysql` 一致 |

---

## 2. 命令面（CoreRpg）

> 注意：现有 `/corerpg storage` = 显示 yaml|mysql ping。**材料仓用 `warehouse` 子命令**，避免撞名。

| 命令 | 行为 |
|------|------|
| `/corerpg warehouse` | 总览：已用/总格、每格 id×数量 |
| `/corerpg warehouse deposit` | 将 **主手** NI 物品全部存入（同 id 叠；无空格且无同 id → 失败） |
| `/corerpg warehouse withdraw <slot\|id> [n]` | 取出；默认取出 64 或整堆（可配） |
| `/corerpg warehouse unlock [coin\|cash]` | 解锁 +1 格；默认 coin；cash=晶钻 |
| `/corerpg warehouse info <slot>` | 单格详情 |

权限：`corerpg.warehouse`（含于 `corerpg.use`）。

---

## 3. YAML（`plugins/CoreRpg/warehouse.yml`）

```yaml
enabled: true
default_slots: 8
max_slots: 48
per_slot_cap: 2000000000
unlock:
  coin_base: 5000
  coin_step: 1500      # 第 n 次解锁价 = base + (unlockedExtra)*step
  cash_price: 28       # 可选晶钻解锁（不涨价或另配）
  allow_cash: true
# 仅这些 NI id 可入仓（流通材料）
whitelist:
  - mat_ember_shard
  - mat_ember_bone_dust
  - mat_ember_core_fragment
  - mat_ember_enchant_crystal
  - gem_ember_sharp
  - ticket_ember_daily
  - ticket_ember_weekly
  - ticket_ember_abyss
```

（具体 id 以服内 NI 实名为准；插件岗落地时扫一遍 Items/ 对齐。）

---

## 4. MySQL

表建议 `cr_warehouse`：

| 列 | 类型 |
|----|------|
| uuid | CHAR(36) PK |
| slots_unlocked | INT |
| slots_json | MEDIUMTEXT / JSON — `[{id, amount}, ...]` 仅已用格；或定长数组 |

YAML 玩家文件兼容键：`warehouseSlots` / `warehouseJson`。

---

## 5. 菜单（TrMenu `ember_storage.yml`）

| 图标 | 行为 |
|------|------|
| 材料仓 | `corerpg warehouse` |
| 存入主手 | `corerpg warehouse deposit` |
| 解锁一格 | `corerpg warehouse unlock` |
| 末影箱 | 保留 `/enderchest` |
| 返回 | hub |

---

## 6. 不做（本期）

- 真实 Chest GUI 拖放（可二期）
- 绑定仓 / 流通仓分仓（白名单已限制）
- LuckPerms 勋阶自动加格（可后接：vip 层 +N）

---

## 7. 验收

- [ ] 新号默认 8 格；`deposit` 白名单碎片成功叠数量
- [ ] 同 id 再存合并；异 id 占新格；满格拒存
- [ ] `unlock` 扣币成功 +1；到 max 拒绝
- [ ] `withdraw` 回背包 NI；数量正确
- [ ] MySQL 重启后数据仍在
- [ ] 非白名单装备拒存并提示

