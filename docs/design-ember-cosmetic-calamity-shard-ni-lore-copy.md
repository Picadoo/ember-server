# 设计稿 · NI 灾厄外观碎片 lore 裸 id 人话对齐（B2.37）

> **STATUS：待批 A**（总控待批）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-abyss-calamity.yml` 内 `cosmetic_calamity_shard` **1** 行 lore 灰字裸 id；批后由物品岗改 NI Items YAML。不改 NI 数值、配方、给物、掉落或其它物品。
>
> B2.36 已 **PASS · 勾销**（设计 `eca16cd` · 批准 `dd582c8` · 施工 `3e3c63b` · 测 `a531ebf` · close `54ef5c4`）。当前 live Items `&7gem_*` / `&7mat_*` 本轨已归零；`&7cosmetic_*` 仅剩本件 1 件。本窗只处理灾厄外观碎片，批准并施工后预期 `&7cosmetic_*` **归零**，仅表示 cosmetic 本件收口，不宣称 pet、斜杠或 B0.1 已清。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 灾厄外观碎片 · **玩家可见 lore 裸 id `cosmetic_calamity_shard` 对齐**（UX · B2.37） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:30 Asia/Shanghai |
| 状态 | **待批 A** |
| 关联 | live `plugins/NeigeItems/Items/ember-abyss-calamity.yml` · B2.36 疾风石 PASS 后 cosmetic 单件薄窗 |
| 专岗 | **物品**；验收 **`rg` + 悬停** |

## 1. 问题一句话

`cosmetic_calamity_shard` 的显示名已经是「灾厄外观碎片」，但 lore 首行仍显示管理侧裸 id `&7cosmetic_calamity_shard`；玩家背包悬停会看到内部代号。本窗只处理该物品这一行，延续 B2.24–36 的单件薄文案窗。

## 2. 现网只读事实

| 项 | 现网证据 |
|---|---|
| 文件 | `plugins/NeigeItems/Items/ember-abyss-calamity.yml` |
| 键 | `cosmetic_calamity_shard:`（L15，管理侧键名保留） |
| material | `NETHER_STAR`（L16，保留） |
| 显示名 | `&d灾厄外观碎片`（L17，中文，**不改 name**） |
| 目标 lore | L19 `- '&7cosmetic_calamity_shard'`（本窗唯一目标） |
| 说明 lore | L20 `- '&8灾厄掉落 · 外观碎片'`；L21 `- '&8集齐可兑称号 / 特效'`（均保留） |
| live 扫描 | `rg -n "&7cosmetic_" plugins/NeigeItems/Items/` 当前 **1 件**：`cosmetic_calamity_shard`；本窗只处理该件，批准并施工后预期 **0 命中** |

只读目标段（实际行号）：

```yaml
cosmetic_calamity_shard:
  material: NETHER_STAR
  name: '&d灾厄外观碎片'
  lore:
    - '&7cosmetic_calamity_shard' # L19，本窗唯一目标
    - '&8灾厄掉落 · 外观碎片' # L20，保留
    - '&8集齐可兑称号 / 特效' # L21，保留
```

当前 live `&7cosmetic_*` 计数为 **1**，且仅命中本件；批准并施工本稿后验收预期 **0 命中**。这是本件 cosmetic 轨收口，不等于 pet、斜杠或其它 soft 已清。

## 3. 推荐方案 A（待批）

**只删 `cosmetic_calamity_shard` lore 的 1 行 `- '&7cosmetic_calamity_shard'`（实际 L19）。** 显示名已经是「灾厄外观碎片」，不改 `name`；L20/L21 两行说明保留。此方案零改数值、配方、给物与掉落。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 删除目标 lore 灰字裸 id 1 行 | **待批 A；批后交物品岗施工** |
| A′ | 改为灰字「灾厄外观碎片」 | 不荐：与显示名重复 |
| B | 顺手处理 pet、斜杠或精英壳 | **仅作 soft，勿双上** |

管理侧键名 `cosmetic_calamity_shard:` 仍保留；本稿不是键名、材质、`name`、配方、给物或掉落逻辑改动。

## 4. B soft 与明确排除

仅旁附观察，不在本件 A 中施工：

- 其它 `/corerpg` 斜杠：另开窗口，**勿斜杠双上**；
- `pet` 裸 id：仅 soft，**不宣称已清**；
- 精英预览壳：证据不足，**勿硬开**；
- B0.1：**仍挂**，无新证据不重开，**勿宣称 B0.1 已清**；
- 其它 NI 文件、其它 lore、NI 键名 / `name` / `material` / enchantments / hideflags、配方 / 给物 / 掉落、TrMenu、CoreRpg、四件甲、锻炉重做、誓约主动大改、墙钟 / DPS 均不做；不叫挑刺。

## 5. 硬约束与施工边界

| 可动（仅批 A 且另开施工） | 不可动（本窗硬禁） |
|---|---|
| 目标 YAML 中 `cosmetic_calamity_shard` lore 的 1 行裸 id | 其它 YAML；其它 `&7cosmetic_*`；pet / 斜杠；精英壳 |
| 批后按服约定 reload | `name`「灾厄外观碎片」、键名、material、L20/L21、enchantments、hideflags |
| 物品岗静态核验 + 玩家悬停 | 数值 / 配方 / 给物 / 掉落逻辑 / TrMenu / CoreRpg；git push |

**本设计窗仅写 tip + backlog；未批前 NI Items YAML 零改。**

## 6. 验收口径（物品岗 / 测试）

1. **静态 `rg`：**
   ```bash
   rg -n "cosmetic_calamity_shard" plugins/NeigeItems/Items/ember-abyss-calamity.yml
   ```
   批 A 施工后只命中管理侧键名 L15，不命中 lore；L20「灾厄掉落 · 外观碎片」与 L21「集齐可兑称号 / 特效」保留。
2. **live 计数：**
   ```bash
   rg -n "&7cosmetic_" plugins/NeigeItems/Items/
   ```
   批 A 后预期 **0 命中**；这是本件 cosmetic 轨收口，不等于 pet、斜杠或其它 soft 已清。
3. **悬停：** 给 / 取 1 枚「灾厄外观碎片」，悬停确认显示名仍为「灾厄外观碎片」，lore 不出现 `cosmetic_calamity_shard`，且「灾厄掉落 · 外观碎片」「集齐可兑称号 / 特效」仍在。
4. **零越界：** 其它 pet、斜杠、精英壳、B0.1、键名、数值 / 配方 / 给物 / 掉落逻辑均无额外变更；不以 wall-clock / DPS 验收。

失败条：施工后悬停仍显示裸 id；借机改 name、其它 YAML、pet、斜杠或精英壳；未批先改 YAML；或宣称 pet/斜杠 soft、B0.1 已清，均为 **FAIL**。

## 7. 专岗与回报

- **策划：** 本稿 + backlog，向总控申请批 A。
- **物品：** 批 A 后只删上述实际 L19 的 1 行 lore。
- **测试：** `rg` + 悬停轻测；不做 wall-clock / DPS，不叫挑刺。
- **回总控：** tip 路径 `docs/design-ember-cosmetic-calamity-shard-ni-lore-copy.md`；本窗建议 **A：只删实际 L19 的 `&7cosmetic_calamity_shard` 一行，不改「灾厄外观碎片」name，保留 L20/L21 两行说明；批后验收 `Items` 下 `&7cosmetic_*` 预期归零，仅宣称本件 cosmetic 轨归零，不宣称 pet、斜杠或 B0.1 已清**；tip/backlog 可 commit，**勿 push**。
