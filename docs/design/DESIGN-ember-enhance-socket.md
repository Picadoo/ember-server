# 余烬强化 + 镶嵌 — 落地规格（对齐物品 / 插件 / RpgBot）

> **D242 指针（2026-10-06）：** 现行装备结构的权威说明已合并到 [`docs/design/DESIGN-ember-gear-structure-2026-10-06.md`](DESIGN-ember-gear-structure-2026-10-06.md)（CoreRpg 1.65.67 / bv58）。本文是**旧系统（镶嵌 / 孔石 / 保护券），P1 下不适用**；现行强化见新文档 §4（EmberUpgradeRules）。

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `docs/design/DESIGN-ember-rpg-systems.md` §3；装备 NI 见 `docs/design/DESIGN-ember-enchant-anvil.md`；孔石/保护券见 `plugins/NeigeItems/Items/ember-enhance-gems.yml`  
**栈：** Paper 1.12.2 · CoreRpg（插件岗实现）· NeigeItems · TrMenu  
**硬约束：** 不改 Paper；**不重建** `CoreRpg.jar`（本文件只定规格与菜单壳）；属性乘区仍进 YAML。

---

## 0. 范围与职责

| 岗 | 活 |
|----|----|
| 总控 / 本规格 | 定 ID、表、存储约定、命令面、`enhance.yml` 形状、TrMenu 接线、验收 |
| 余烬-物品 | NI 已落地保护券/稳固符/四石；日后补 lore 润色即可 |
| 余烬-插件 | CoreRpg：`enhance` / `socket` 子命令 + 读 `enhance.yml` + lore 读写 |
| 余烬-测试 (RpgBot) | §8 冒烟 |

**可强化白名单（本期）：** `gear_ember_blade`、`gear_ember_charm`  
**材料（已有）：** `mat_ember_shard`、`mat_ember_bone_dust`、`mat_ember_core_fragment`

---

## 1. 精确 NI 物品 ID

| NI ID | 显示名 | 用途 |
|-------|--------|------|
| `mat_ember_protect_scroll` | 余烬保护券 | +7～+9 失败时**防止掉级**（仍扣材料） |
| `mat_ember_stable_charm` | 余烬稳固符 | +9→+10 失败时**不掉档**（只失败，不掉到 +8） |
| `gem_ember_sharp` | 锋利石 | 孔石 · 微量攻击 |
| `gem_ember_steady` | 稳固石 | 孔石 · 微量防御 |
| `gem_ember_drain` | 汲取石 | 孔石 · 微量吸血 |
| `gem_ember_gale` | 疾风石 | 孔石 · 微量移速 |

文件：`plugins/NeigeItems/Items/ember-enhance-gems.yml`  
产出：氪金便利柜 / 周本 / 深渊 / 分解；**不进**地窟 MM 掉落表。

---

## 2. 强化表 +0..+10（成功率 · 消耗 · 失败）

玩家手持白名单装备执行 `/corerpg enhance`（无参 = 尝试升一级；`info` = 只读预览）。  
成功：等级 +1，扣表内材料；失败：按「失败」列处理，**材料仍扣**（保护券/稳固符按列另扣）。

| 当前 → 目标 | 成功率 | 消耗 | 失败行为 | 保护物（可选，失败时额外扣 1） |
|-------------|--------|------|----------|--------------------------------|
| +0 → +1 | 100% | `mat_ember_shard` ×3 | — | — |
| +1 → +2 | 90% | `mat_ember_shard` ×5 | 等级不变 | — |
| +2 → +3 | 80% | `mat_ember_shard` ×8 | 不变 | — |
| +3 → +4 | 70% | `mat_ember_shard` ×10 + `mat_ember_bone_dust` ×2 | **掉 1 级** | — |
| +4 → +5 | 55% | `mat_ember_shard` ×12 + `mat_ember_bone_dust` ×3 | 掉 1 级 | — |
| +5 → +6 | 40% | `mat_ember_shard` ×15 + `mat_ember_bone_dust` ×5 | 掉 1 级 | — |
| +6 → +7 | 35% | `mat_ember_core_fragment` ×1 + `mat_ember_shard` ×10 | 掉 1 级 | 有 `mat_ember_protect_scroll` → **不掉级** |
| +7 → +8 | 25% | `mat_ember_core_fragment` ×1 + `mat_ember_shard` ×15 | 掉 1 级 | 同上保护券 |
| +8 → +9 | 15% | `mat_ember_core_fragment` ×2 + `mat_ember_shard` ×20 | 掉 1 级 | 同上保护券 |
| +9 → +10 | 8% | `mat_ember_core_fragment` ×3 + `mat_ember_stable_charm` ×1 | **掉到 +8** | 若消耗的是稳固符路径：失败时**停在 +9**（符已在消耗里；不再另要保护券） |

### 2.1 保护物规则（实现要点）

- **+6→+9：** 若背包有 `mat_ember_protect_scroll`，强化前询问或默认「有则自动用」（`enhance.yml` → `auto_use_protect: true`）。失败时扣 1 张券且等级不变；成功不扣券。  
- **+9→+10：** 表内**强制**消耗 1× `mat_ember_stable_charm`（成功/失败都扣）。失败默认掉到 +8；因稳固符已付，失败结果改为**停在 +9**（与总纲「用稳固符可只失败不掉」一致）。  
- 无保护券时 +7～+9 失败照常掉 1 级。  
- **+10 已满：** 拒绝再强化，提示已达上限。

### 2.2 强化数值（固定值，进 YAML）

| 装备 | 每级加成（展示 / 公式） |
|------|-------------------------|
| `gear_ember_blade` | 物理伤害 +2 / 级（相对 AP lore 基线） |
| `gear_ember_charm` | 生命力 +4 / 级；物理防御 +1 / 级（+5 起再 +1 防，可选） |

本期可用「lore 重写数值行」或 CoreRpg 伪属性；**禁止**散落魔法数——一律读 `enhance.yml` → `stat_per_level`。

---

## 3. 强化等级存储（1.12.2 · lore 可解析）

Paper 1.12.2 无可靠跨插件自定义 NBT 时，**以 lore 为主**（CoreRpg 解析；NI 不改 id）。

### 3.1 展示行（玩家可见）

```
§8强化 §f+N
```

例：`§8强化 §f+5`。无此行视为 **+0**。

### 3.2 机器标记行（建议同写，便于稳解析）

```
§8§o#ember_en:N
```

- 以 `#ember_en:` 前缀匹配，忽略颜色码后取整数 `N`（0～10）。  
- 写入时：**先更新/插入标记行，再同步展示行**；两者必须同值。  
- 若仅有展示行、无标记行：解析 `强化` 后的 `+N`，并在下次成功强化时补写标记行。

### 3.3 孔位 lore（同物品）

```
§8孔1: §7空
§8孔2: §7未解锁
```

镶入后：

```
§8孔1: §fgem_ember_sharp
```

机器行（可选，与展示同步）：

```
§8§o#ember_sk:1=gem_ember_sharp;2=
```

空槽 `2=` 表示已解锁未镶；缺 key 或 `2=-` 表示未解锁（实现择一，写进 `enhance.yml` → `socket.marker_empty` / `marker_locked`）。

### 3.4 禁止

- 改 NI 物品 id / 用不同 material 冒充强化档。  
- 只改 AP 属性数字却不写 `#ember_en:`（会导致重登/重载漂移）。  
- 依赖 1.13+ PersistentDataContainer。

---

## 4. 镶嵌孔解锁阈值

| 装备 | 孔 1 | 孔 2 |
|------|------|------|
| `gear_ember_blade` | **+0 起解锁**（天生 1 孔） | **强化 ≥ +5** 解锁第 2 孔 |
| `gear_ember_charm` | **强化 ≥ +3** 解锁第 1 孔 | 本期**无**第 2 孔（`max_sockets: 1`） |

未解锁孔在 lore 显示 `§7未解锁`；命令 `insert` 拒绝并提示差多少级。

### 4.1 镶嵌规则

- 手持装备；宝石从背包扣除 1。  
- `insert <gemId>`：写入第一个空的已解锁孔；满则失败。  
- `remove <slot>`：`slot` 为 1 或 2；卸下宝石**回背包**（绑定策略：本期可交易回背包；若日后绑孔石再加 `bound_on_insert`）。  
- 同石可重复镶多孔；无「同 id 互斥」除非 YAML 打开 `unique_gem_ids: true`（默认 false）。  
- 宝石效果微量，系数在 `enhance.yml` → `gems.<id>.stats`（插件岗接 AttributePlus 或伪表）。

### 4.2 默认宝石微量（调参用）

| gemId | stats（示意） |
|-------|----------------|
| `gem_ember_sharp` | `phys_damage: 2` |
| `gem_ember_steady` | `phys_defense: 2` |
| `gem_ember_drain` | `life_steal_pct: 0.01`（cap 另设） |
| `gem_ember_gale` | `move_speed_pct: 0.02` |

---

## 5. 命令面（CoreRpg）

权限：玩家 `corerpg.enhance` / `corerpg.socket`（默认真玩家有）；管理 `corerpg.admin`。

| 命令 | 行为 |
|------|------|
| `/corerpg enhance` | 手持白名单装备，按表尝试 +1；聊天反馈成功/失败/缺料 |
| `/corerpg enhance info` | 只读：当前 +N、下一档成功率、消耗清单、失败后果、孔解锁状态 |
| `/corerpg socket list` | 手持装备：列出孔 1..max（锁定/空/宝石 id） |
| `/corerpg socket insert <gemId>` | 从背包扣对应 NI，写入首个空解锁孔 |
| `/corerpg socket remove <slot>` | 卸下该孔宝石回背包；`slot` ∈ {1,2} |

别名：可挂在已有 `/crpg` `/rpg` 下同一子命令树。  
错误码文案（固定前缀便于 Bot 断言）：

- `§c[强化] 请手持余烬刃或护符`
- `§c[强化] 材料不足：…`
- `§a[强化] 成功 → +N`
- `§e[强化] 失败…`（含是否掉级/是否吃保护）
- `§c[镶嵌] 孔位未解锁 / 无效宝石 / 背包无此石`

---

## 6. YAML 旋钮 · `plugins/CoreRpg/enhance.yml`

插件岗新建此文件（热重载进 `/corerpg reload` 或专令）。形状如下（数值与 §2/§4 对齐）：

```yaml
# CoreRpg enhance + socket — 余烬成长层
enabled: true

whitelist_ni_ids:
  - gear_ember_blade
  - gear_ember_charm

lore:
  display_prefix: "§8强化 §f+"          # 后接 N
  marker_prefix: "§8§o#ember_en:"       # 后接 N
  socket_display_format: "§8孔{slot}: §f{value}"  # value=空|未解锁|gemId
  socket_empty: "§7空"
  socket_locked: "§7未解锁"
  socket_marker_prefix: "§8§o#ember_sk:"

auto_use_protect: true
protect_scroll_ni_id: mat_ember_protect_scroll
stable_charm_ni_id: mat_ember_stable_charm

levels:
  # key = 目标等级（从 current=key-1 升上来）
  1: { chance: 1.00, fail: stay, costs: { mat_ember_shard: 3 } }
  2: { chance: 0.90, fail: stay, costs: { mat_ember_shard: 5 } }
  3: { chance: 0.80, fail: stay, costs: { mat_ember_shard: 8 } }
  4: { chance: 0.70, fail: down1, costs: { mat_ember_shard: 10, mat_ember_bone_dust: 2 } }
  5: { chance: 0.55, fail: down1, costs: { mat_ember_shard: 12, mat_ember_bone_dust: 3 } }
  6: { chance: 0.40, fail: down1, costs: { mat_ember_shard: 15, mat_ember_bone_dust: 5 } }
  7: { chance: 0.35, fail: down1, protect: true, costs: { mat_ember_core_fragment: 1, mat_ember_shard: 10 } }
  8: { chance: 0.25, fail: down1, protect: true, costs: { mat_ember_core_fragment: 1, mat_ember_shard: 15 } }
  9: { chance: 0.15, fail: down1, protect: true, costs: { mat_ember_core_fragment: 2, mat_ember_shard: 20 } }
  10:
    chance: 0.08
    fail: set8
    stable_required: true
    costs:
      mat_ember_core_fragment: 3
      mat_ember_stable_charm: 1

stat_per_level:
  gear_ember_blade:
    phys_damage: 2
  gear_ember_charm:
    max_health: 4
    phys_defense: 1

socket:
  unique_gem_ids: false
  gear_ember_blade:
    max: 2
    unlock_at: [0, 5]    # 孔1@+0, 孔2@+5
  gear_ember_charm:
    max: 1
    unlock_at: [3]       # 孔1@+3

gems:
  gem_ember_sharp:  { phys_damage: 2 }
  gem_ember_steady: { phys_defense: 2 }
  gem_ember_drain:  { life_steal_pct: 0.01 }
  gem_ember_gale:   { move_speed_pct: 0.02 }

caps:
  life_steal_pct: 0.05
  move_speed_pct: 0.08
```

`fail` 枚举：`stay` | `down1` | `set8`（+10 失败默认）；有 `protect: true` 且吃到保护券时强制等价 `stay`；`stable_required` 失败时改为 `stay`（停在 +9）。

---

## 7. TrMenu 接线

| 文件 | 作用 |
|------|------|
| `plugins/TrMenu/menus/ember_enhance.yml` | 强化子菜单（壳）：提示手持装备 + 跑命令 |
| `plugins/TrMenu/menus/ember_socket.yml` | 镶嵌子菜单（壳） |
| `ember_hub.yml` 图标 **S 强化** / **T 镶嵌** | `menu: ember_enhance` / `menu: ember_socket`；**去掉「即将点燃」** |

`server-runtime/plugins` → `plugins` 符号链接，**同一 inode**，无需镜像拷贝。

菜单动作约定：`tell` 提醒手持 → `command: corerpg …`（玩家身份）→ 可选 `close`。

---

## 8. RpgBot 验收冒烟

前置：OP 或有 `ni` / `corerpg`；`/ni give` 可用；热更 TrMenu + CoreRpg 配置（插件实装后）。

1. **菜单壳**  
   - `/ember` → 点「强化」打开 `ember_enhance`（无「即将点燃」lore）。  
   - 点「镶嵌」打开 `ember_socket`。  
   - 子菜单「返回」回 `ember_hub`。

2. **强化 info（空/非装备）**  
   - 空手 `/corerpg enhance info` → 拒绝文案含 `[强化]`。

3. **+0→+3 稳升**  
   - `/ni give <p> gear_ember_blade 1` + 足够 `mat_ember_shard`。  
   - 连续 `/corerpg enhance` 至 +3；lore 含 `§8强化 §f+3` 与 `#ember_en:3`。  
   - `enhance info` 显示下一档 70% 与骨尘消耗。

4. **失败掉级（可 seed 或反复试 +5→+6）**  
   - 无保护券时失败后等级 −1；有 `mat_ember_protect_scroll` 时失败等级不变且券 −1。

5. **+10 稳固**  
   - 管理可临时把 chance 调 0 测失败：有稳固符消耗后停在 +9，不落到 +8。

6. **镶嵌解锁**  
   - +0 刃：`socket list` → 孔1 空、孔2 未解锁。  
   - 强化到 +5 后孔2 解锁。  
   - 护符 +0：`insert` 拒绝；强化到 +3 后可 `insert gem_ember_sharp`。  
   - `remove 1` 宝石回包，lore 孔1 变空。

7. **回归**  
   - 附魔台 / 铁砧修理 / 地窟掉落抽测不受影响。  
   - **未**替换 `CoreRpg.jar` 由本任务触发（插件岗另发版）。

PASS：1～4、6 必过；5 可在插件实装后用 YAML 强失败测。

---

## 9. 非目标（本期不做）

- 分解产孔石、重铸：菜单壳 + 重铸石 NI 见 `docs/status/STATUS-ember-disassemble.md`（命令待插件岗）。套装 2 件效果（另规格）。  
- 商城上架保护券（经济文已有柜位，物品岗/经营岗另接）。  
- AttributePlus 公式深接（先 lore/伪表）。  
- 改 Paper / 重建 CoreRpg.jar。

---

## 10. 落地顺序（插件岗）

1. 落 `enhance.yml` + lore 读写工具类。  
2. 实现 `enhance` / `enhance info`。  
3. 实现 `socket list|insert|remove`。  
4. 热重载；交 RpgBot 跑 §8。  
5. 通过后写 `docs/status/STATUS-ember-enhance-socket.md`。
