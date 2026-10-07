# 余烬 · 材料仓库（单种近无限 + 可解锁格）

**日期：** 2026-09-13（Asia/Shanghai）  
**诉求：** 玩家仓库；**单种物品几乎无限堆**；**可解锁新格子**。  
**承接：** `docs/design/DESIGN-ember-character-storage.md`（末影箱保留）；本文件替换「扩展页占位」。

> **D253 追记（2026-10-07）：** P1 现行玩家存取走枢纽「仓库」→ `EmberVault`（§8）。旧 `/corerpg warehouse` 在 P1 下对非 OP **只读**（S0-10 / D252 / CoreRpg 1.65.71）：可 `list`/`info`，不可 `deposit`/`withdraw`/`unlock`。下文 §1–§7 保留作旧命令能力说明与 OP 测本参考，**不要写成给玩家的操作教程**。

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
> **P1 现行（D252）：** 非 OP 仅总览 / info；写命令被拒并提示用枢纽仓库。OP / `corerpg.admin` 仍可全套（测本）。

| 命令 | 行为 | P1 非 OP |
|------|------|----------|
| `/corerpg warehouse` | 总览：已用/总格、每格 id×数量 | ✅ 可 |
| `/corerpg warehouse info <slot>` | 单格详情 | ✅ 可 |
| `/corerpg warehouse deposit` | 将 **主手** NI 物品全部存入（同 id 叠；无空格且无同 id → 失败） | ❌ 拒（用枢纽仓库） |
| `/corerpg warehouse withdraw <slot\|id> [n]` | 取出；默认取出 64 或整堆（可配） | ❌ 拒 |
| `/corerpg warehouse unlock [coin\|cash]` | 解锁 +1 格；默认 coin；cash=晶钻 | ❌ 拒（P1 本无格上限） |

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
  - ticket_ember_daily   # 遗留票物；不是玩家「门票」货币（见 DP README）
  - ticket_ember_weekly
  - ticket_ember_abyss
```

（具体 id 以服内 NI 实名为准；P1 现行白名单以 `ember-v1.yml storage.vault.whitelist` 为准，见 §8。）

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

## 5. 菜单（TrMenu）

| 入口 | 现行行为 |
|------|----------|
| 主菜单「仓库」 | `corerpg p1 vault from hub` → **EmberVault**（§8） |
| `ember_storage.yml` | 备用二级页（末影箱等）；**不要**再挂 `warehouse deposit/unlock` 给普通玩家 |
| 旧命令面 | 见 §2；P1 非 OP 只读 |

---

## 6. 不做（本期）

- 真实 Chest GUI 拖放（可二期）
- 绑定仓 / 流通仓分仓（白名单已限制）
- LuckPerms 勋阶自动加格（可后接：vip 层 +N）

---

## 7. 验收（历史 · 旧命令 / OP）

下列针对 **OP 测本** 或 P1 关闭时的旧路径；P1 普通玩家请用 §8 枢纽仓库验收。

- [ ]（OP）`deposit` 白名单碎片成功叠数量
- [ ]（OP）同 id 再存合并；异 id 占新格；满格拒存
- [ ]（OP）`unlock` 扣币成功 +1；到 max 拒绝
- [ ]（OP）`withdraw` 回背包 NI；数量正确
- [ ] MySQL 重启后数据仍在
- [ ] 非白名单装备拒存并提示
- [x] **D252：** P1 非 OP `deposit`/`withdraw`/`unlock` 被拒；`warehouse`/`list`/`info` 仍可看


## 8. P1 材料仓库 + 装备库（CoreRpg 1.62.0，10-04）

P1（余烬模式）里，仓库改成**免费、无限**的两部分。旧的「解锁格」和「付费扩容」不再用于 P1。

**材料仓库（EmberVault）**

- 复用 `cr_warehouse` 的数据，但 P1 不限格数，每种材料上限 2e9。
- 能入库的只有白名单材料，配置在 `ember-v1.yml` `storage.vault.whitelist`，默认是余烬碎片、核心碎片、骨尘、胚料和国庆币。
- 自动入库：捡起地上的物品、结算发材料时，白名单材料直接进仓库，动作栏会显示合并后的提示。玩家可以在界面里关掉自己的自动入库，服务器总开关是 `storage.vault.auto_pickup`。
- 所有 P1 消耗（强化、升阶、精工、洗练等）都能直接扣仓库里的材料：仓库通过 NiBridge 的 ExtraSource 接口计入。扣的时候先扣背包，背包不够再扣仓库。
- 分解退还的胚料也进仓库。

**装备库（EmberGearLib）**

- 存进装备库的装备，就是 `cr_p1_item` 里状态为 `stored` 的那一行，锁定和收藏另记在 `cr_p1_gearlib`。所以装备库只在 MySQL 模式下能用。
- **新装备自动入库**：只针对新掉落、而且不是升级的装备，并且该部位已经有在用的装备。有三档：
  - 背包快满时（默认）：空格少于 `auto_stash_below_free: 4` 时才存
  - 总是
  - 关闭
- **存取限制**：装备只能在主城存取，副本里不行；材料在哪里都能存取。
- **装备库界面**：
  - 每页 45 件，可以按成色、套装族、部位、阶筛选，有 4 种排序。
  - 左键取出，右键锁定，Shift+右键收藏。
  - 批量分解只作用于当前筛选结果，会跳过锁定、收藏、正穿着的装备和不能分解的装备；要点确认令牌才执行，10 分钟内可以撤销（`/corerpg p1 undo`）。
- **一键存入**：背包里的白名单材料进材料仓库；背包第 9–35 格里没穿着的 P1 装备进装备库。快捷栏不动。

**命令**

- `/corerpg p1 vault [auto|take <id> <n>]`
- `/corerpg p1 gearlib [take <uid>|bulk|mode]`
- `/corerpg p1 stash`
- `/corerpg p1 undo [uid]`
- `/corerpg p1 itemlog <玩家>|uid <前缀>`（管理员）

**入口**：主菜单「仓库」直接打开材料仓库界面。`ember_storage.yml` 留作备用的二级页面（末影箱和跳转按钮）。

**流水**：stash、unstash、glibdis、undo 都会写进 `cr_p1_txn`，详见 `DESIGN-ember-data-protection.md` §4。

**冒烟（10-04，FreshQ41）**：

- 地上掉落的 7 个余烬碎片被捡起后进了仓库，提示「+7 余烬碎片（共 7）自动入库」。
- 在界面里左键取出 7 个，再一键存入，碎片回到仓库。
- 管理员发的 2 件装备挪进背包格后执行 `stash`，提示「装备 2 件 → 装备库」。装备库界面显示这 2 件，左键取出 1 件。数据库里这件的 rev 变成 2、状态 active，另一件仍是 stored。
- `itemlog` 显示了这 3 条 stash / unstash 记录。
- 批量分解和撤销没有用机器人测：管理员发的装备来源是 admin，不能分解。这部分有单测 `EmberStorageRulesTest`。
