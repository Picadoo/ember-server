# Paper 1.12.2-1620 核心魔改路线图

> 版本：1.0 · 日期：2026-09-09（Asia/Shanghai）  
> 目标版本：Paper **1.12.2 build 1620**  
> 物品体系：**仅 NeigeItems（NI）**  
> 架构：**混合** — Paper/NMS 管底层规则（时长、数量、判定）；附属插件管 YAML 灵活注册、热重载、NI 解析与命令

---

## 0. 原则

1. **原版经济闭环打破**：能清的原版配方/战利品尽量清掉，产出与关键消耗改为 NI 物品。
2. **底层与配置分离**：改「物理规则」进 Paper；改「表数据」进附属 YAML，避免每次改配方都重编译。
3. **可测**：每个阶段有启动日志断言 + mineflayer/控制台检查命令。
4. **可回滚**：stock jar 与 custom jar 并存；`start.sh stock|custom`。
5. **Java 8**：插件与 1.12.2 服务端保持 Java 8 工具链（`tools/jdk8u504-b01`）。

### 不做 / 缓做

- 依赖公网联机的稳定开服（本机无公网入站；隧道非官方支持）。
- 把 NI 编译进 Paper（禁止 Paper 依赖 NI）。
- 一次重写全部战斗到「完全自定义客户端」级别（无协议模组前提下有上限）。

---

## 1. 仓库与运行时布局

| 路径 | 用途 |
|------|------|
| `paper/paper-1.12.2-1620.jar` | 官方 stock |
| `Paper/` | 源码与补丁；产物 `Paper-Server/target/paper-1.12.2.jar` → 部署为 custom |
| `plugins/NeigeItems-*.jar` + `plugins/NeigeItems/Items/` | 物品定义 |
| `CoreSmelt/` | 熔炉注册附属 |
| `CoreBrew/` | 炼药注册附属（规划/实现中） |
| `CoreCraft/` | 工作台（规划） |
| `CoreFish/` | 钓鱼（规划） |
| `CoreCombat/` | 盾/图腾/冷却等（规划，可拆分） |
| `server-runtime/` | 测试服 |
| `mineflayer-tests/` | 冒烟与机制脚本 |
| `DESIGN-*.md` / `STATUS-*.md` / 本文件 | 设计与验收 |

---

## 2. 分层 API 约定

### 2.1 Paper 暴露（无 NI 依赖）

运行时钩子（名称可落在 `pers.coresystem.paper.*`）：

- `FurnaceNmsHooks`：`cookTimeTicks`、`resultAmount`、可选 `CookRuleProvider` 回调
- `BrewNmsHooks`：`brewTimeTicks`、自定义酿造匹配入口
- （后续）`FishNmsHooks`、`ShieldNmsHooks`、`TotemNmsHooks`、`EnchantNmsHooks`…

原则：Paper 只认 **规则 ID / 回调 / 原始 ItemStack**；是否 NI 由附属判断。

### 2.2 附属约定

- `depend: [NeigeItems]`
- YAML 热重载：`/xxx reload`
- 检查命令：`/xxx check`（配方数、钩子是否挂上、抽样断言）
- 物品：`ItemManager.INSTANCE.hasItem / getItemStack / isNiItem`

### 2.3 1.12.2 硬限制（路线图必须承认）

- 熔炉 Bukkit `FurnaceRecipe` **无 cookTime API** → 时长必须 NMS。
- 熔炉输入匹配偏 **Material(+data)** → 同材质原版/NI 需事件或 NBT 门闩（已有 `FurnaceSmeltEvent` 思路）。
- 盾、图腾、冷却条均存在于 1.12.2（盾 1.9+，图腾 1.11+）。
- 无 1.13+ 原版 datapack；战利品用 NMS 表或附属监听。

---

## 3. 阶段总览

| 阶段 | 名称 | 状态 | 依赖 |
|------|------|------|------|
| P0 | 地基：JDK8、stock 服、NI、mineflayer | ✅ 完成 | — |
| P1 | 熔炉 NI 化（清原版 + 注册） | ✅ 完成 | P0 |
| P2 | 混合：熔炉时长/数量 NMS + CoreSmelt | 🔄 进行中 | P1 |
| P3 | 混合：炼药时长/配方 + CoreBrew | 🔄 进行中 | P2 钩子骨架 |
| P4 | 工作台合成 CoreCraft | ✅ 本轮完成 | P2 |
| P5 | 钓鱼 CoreFish | ✅ 本轮完成 | P2 |
| P6 | 图腾 + 盾牌 CoreCombat | ✅ 本轮完成 | P2 |
| P7 | 附魔 / 铁砧 | ✅ 本轮完成（CoreEnchant/CoreAnvil） | — |
| P8 | 伤害公式 / 冷却条深化 | ⏳ | P6 |
| P9 | 方块硬度与掉落 / 生物 loot | 🔄 生物 loot 已禁；方块不改 | — |
| P10 | 村民交易 / 刷怪规则 | 🔄 村民交易已禁；刷怪未动 | — |
| P11 | 打磨：统一配方 DSL、文档、回归套件 | ⏳ | 以上 |

---

## 4. 分系统规格

### 4.1 熔炉 Furnace（P1–P2）

**目标**

- 启动清除全部原版 `FurnaceRecipe`。
- 仅 NI→NI 配方；YAML 可热重载。
- **cook_time_ticks、result_amount 真实生效**（非文档字段）。

**已有演示链（余烬）**

| 输入 | 输出 |
|------|------|
| ore_ember_iron | ingot_ember_iron |
| ore_ember_gold | ingot_ember_gold |
| ore_ember_coal | crystal_ember_carbon |
| raw_ember_flesh | steak_ember |

**P2 验收**

- 日志：钩子启用；非默认时长（如 100/300）写入 TE。
- `/coresmelt check`：规则数、抽样 cook/amount。
- 手动或脚本：完成冶炼后堆叠数量符合 `result_amount`。

**风险**：同 Material 误匹配 → 保持 NI 门闩。

---

### 4.2 炼药 Brewing（P3）

**目标**

- 自定义酿造总时长；YAML 注册 ingredient / 基底瓶 / 结果（均为 NI 或明确允许的原版瓶策略——**默认全 NI**）。
- 灵活注册 + `/corebrew reload`。

**建议演示**

- `herb_ember_bitter` + `phial_ember_empty` → `potion_ember_rage`（brew 200 tick）
- 第二条更长时长对照（如 600 tick）便于体感。

**NMS**：`TileEntityBrewingStand` 默认 ~400 tick；配方匹配扩展或旁路 PotionBrewer。

**验收**：日志注册数；两套时长可区分；mineflayer 进服 + check 命令。

---

### 4.3 工作台 Crafting（P4）

**目标**

- 清除（或大规模禁用）原版有序/无序配方。
- YAML：有序/无序/特殊形状；输入输出 NI；产出数量。
- 附属 `CoreCraft` + 必要时 Paper 对 `CraftingManager` 的清理钩子。

**注意**：完全清空原版会导致无法合成木棍等工作台本身依赖；策略二选一写死进设计：

- **A. 白名单**：保留极少原版（工作台、熔炉方块等基建）+ 其余全 NI；或  
- **B. 基建也 NI 化**：开局发放 / NPC 发放基建套件。

**推荐默认：A**，基建白名单可配置。

---

### 4.4 钓鱼 Fishing（P5）

**目标**

- 咬钩等待、咬钩窗口、急拉判定可配置（NMS）。
- Loot 只出 NI（鱼类 / 宝藏 / 垃圾三表 YAML）。
- 可选：禁用原版附魔钓；鱼竿仅 NI。

**附属 `CoreFish`**：表与权重；`FishNmsHooks` 管时间参数。

---

### 4.5 不死图腾 Totem（P6a）

**目标**

- 原版图腾无效或立即消耗无效。
- 仅 `totem_ember_*` 等 NI 可触发；可配冷却、额外效果、是否真消耗。
- 优先：`EntityResurrectEvent`（若 1.12 Paper 有）或死亡事件拦截 + 取消死亡；不足再 NMS。

---

### 4.6 盾牌 Shield（P6b）

**目标**

- 仅 NI 盾可格挡；原版盾无效果或无法使用。
- 可配：弹射物格挡、斧破盾（1.9 机制）、冷却、耐久损耗倍率。
- 手感类参数进 NMS；物品认定进附属。

---

### 4.7 附魔 / 铁砧（P7）

- 附魔台消耗、等级曲线、宝藏附魔池。
- 铁砧惩罚、重命名费、NI 装备兼容。
- 附属管「可附魔 NI 列表」；数值曲线 NMS 或事件。

---

### 4.8 伤害与冷却（P8）

- 武器冷却（1.9+ attack cooldown）、弓、末影珍珠冷却。
- 护甲减伤曲线；击退；自定义暴击。
- 与 NI 属性（若用 NI 动作/技能）对齐，避免两套伤害打架——**需单独「伤害总线」设计短文**后再动代码。

---

### 4.9 方块与生物掉落（P9）

- 矿物/方块破坏只掉 NI；硬度/工具需求可调。
- 生物死亡 loot YAML 化。
- 可与钓鱼共享 loot 引擎。

---

### 4.10 村民 / 刷怪（P10）

- 清原版交易或替换为 NI 交易表。
- Mob cap、刷怪笼、自然生成白名单。

---

## 5. 统一配方 / 规则 DSL（P11 目标形态）

远期让各附属共用一种结构（示例）：

```yaml
id: furnace_ember_iron
system: furnace   # furnace|brew|craft|fish|totem|shield
input:
  - { ni: ore_ember_iron, amount: 1 }
output:
  - { ni: ingot_ember_iron, amount: 2 }
params:
  cook_time_ticks: 100
  exp: 0.7
```

P2–P6 可先各用专用 YAML；P11 再收敛。

---

## 6. 测试策略

| 级别 | 内容 |
|------|------|
| L0 | 服能起；NI / 各 Core* enable |
| L1 | 日志数字（清除 N、注册 M、钩子 on） |
| L2 | `/check` 命令断言 |
| L3 | mineflayer 进服冒烟 |
| L4 | 机制脚本（熔炼完成数量、酿造时长抽样） |

每个阶段合并前至少 L0–L2；P2+ 争取 L4。

---

## 7. 建议实施顺序（冻结版）

```
P2 熔炉时长/数量 ──┬── P3 炼药
                   ├── P4 工作台（基建白名单策略 A）
                   ├── P5 钓鱼
                   └── P6 图腾 → 盾牌
                            ↓
                     P7 附魔/砧 → P8 伤害总线 → P9 loot/方块 → P10 村民/刷怪
                            ↓
                          P11 DSL 统一与回归包
```

并行规则：Paper 钩子骨架可并行；附属注册可并行；**伤害总线（P8）不要和 P6 同时大改**。

---

## 8. 当前进度快照

- [x] Stock Paper 1620 + NI 1.21.151 可加载  
- [x] CoreSmelt 清 67 条熔炉配方 + 4 条余烬链  
- [x] Mineflayer smoke  
- [x] Paper 源码可重建  
- [ ] FurnaceNmsHooks 真实时长/数量  
- [ ] CoreBrew + BrewNmsHooks  
- [ ] 本路线图评审通过后启动 P4+  

---

## 9. 待你拍板的产品决策（文档内默认）

| 议题 | 默认 |
|------|------|
| 工作台是否保留原版基建配方 | **保留白名单（策略 A）** |
| 原版图腾 | **禁用** |
| 原版盾 | **禁用格挡** |
| 钓鱼是否允许原版鱼 | **否，只出 NI** |
| 开局资源 | 测试用 `/ni give`；正式服另做新手包（未排期） |
| 方块硬度 / 方块掉落 | **不改**（保持原版） |
| 村民交易 | **全面禁止**（无交易 UI、不生成交易物品） |
| 原版生物物品掉落 | **禁止**（默认清物品、保留经验；MM/NI 另发奖励） |
| 附魔台 / 铁砧 | **优先魔改**（YAML 报价表 + 铁砧费用/合并策略） |

若要改默认，只改本表并改对应阶段规格，不必重写全文。

---

## 10. 相关文档

- `DESIGN-furnace.md` — 余烬熔炉链  
- `STATUS-furnace.md` / `STATUS.md` / `STATUS-hybrid.md`（hybrid 完成后）  
- `ROADMAP-core-mod.md` — 本文件（唯一总路线图）
