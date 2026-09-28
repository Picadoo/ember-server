# 设计稿 · NI 余烬稳固石 lore 裸 id 人话对齐（B2.34）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-gem-steady-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `gem_ember_steady` **1** 行 lore 灰字裸 id；批后由物品岗改 NI Items YAML。不改 NI 数值、配方、给物、镶嵌逻辑或其它物品。
>
> B2.33 已 **PASS · 勾销**（设计 `48b5218` · 批准 `6a43ab1` · 施工 `fbef72e` · 测 `90797d7` · close `973e941`）。当前 `&7gem_*` 仍有 3 件；本窗只处理稳固石，**勿宣称 B0.1 已清**。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 余烬稳固石 · **玩家可见 lore 裸 id `gem_ember_steady` 对齐**（UX · B2.34） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:14 Asia/Shanghai |
| 状态 | **已批 A** |
| 关联 | live `plugins/NeigeItems/Items/ember-enhance-gems.yml` · B2.33 锋利石 PASS 后 gem 轨单件薄窗 |
| 专岗 | **物品**；验收 **`rg` + 悬停** |

## 1. 问题一句话

`gem_ember_steady` 的显示名已经是「稳固石」，但 lore 首行仍显示管理侧裸 id `&7gem_ember_steady`；玩家背包悬停会看到内部代号。本窗只处理该物品这一行，延续 B2.33 的单件薄文案窗。

## 2. 现网只读事实

| 项 | 现网证据 |
|---|---|
| 文件 | `plugins/NeigeItems/Items/ember-enhance-gems.yml` |
| 键 | `gem_ember_steady:`（L39，管理侧键名保留） |
| material | `IRON_INGOT`（L40，保留） |
| 显示名 | `&9稳固石`（L41，中文，**不改 name**） |
| 目标 lore | L43 `- '&7gem_ember_steady'`（本窗唯一目标） |
| 说明 lore | L44 `- '&8镶嵌石 · 微量防御'`；L45 `- '&8刃 / 护符孔可用'`（均保留） |
| live 扫描 | `rg -n "&7gem_" plugins/NeigeItems/Items/` 当前 **3 件**：`gem_ember_steady`、`gem_ember_drain`、`gem_ember_gale`；本窗只处理稳固石，批准并施工后预期剩 **2 件** |

只读目标段（实际行号）：

```yaml
gem_ember_steady:
  material: IRON_INGOT
  name: '&9稳固石'
  lore:
    - '&7gem_ember_steady' # L43，本窗唯一目标
    - '&8镶嵌石 · 微量防御' # L44，保留
    - '&8刃 / 护符孔可用'   # L45，保留
```

当前 live `&7gem_*` 计数为 **3**；批准并施工本稿后，验收期望 Items 下剩余 **2**。变化只指本窗目标 1 件，**不宣称 gem 轨、斜杠、cosmetic、pet 或其它 soft 已清**。

## 3. 推荐方案 A（待批）

**只删 `gem_ember_steady` lore 的 1 行 `- '&7gem_ember_steady'`（实际 L43）。** 显示名已经是「稳固石」，不改 `name`；L44/L45 两行说明保留。此方案零改数值、配方、给物与镶嵌逻辑。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 删除目标 lore 灰字裸 id 1 行 | **已批 A；交物品岗施工** |
| A′ | 改为灰字「稳固石」 | 不荐：与显示名重复 |
| B | 顺手处理 `gem_ember_drain` / `gem_ember_gale`、斜杠、cosmetic/pet 或精英壳 | **仅作 soft，勿双上** |

管理侧键名 `gem_ember_steady:` 仍保留；本稿不是键名、材质、强化、配方、给物或镶嵌逻辑改动。

## 4. B soft 与明确排除

仅旁附观察，不在本件 A 中施工：

- 其余 2 件 gem：`gem_ember_drain` / `gem_ember_gale`，**不动**；
- 其它 `/corerpg` 斜杠：另开窗口，**勿斜杠双上**；
- `cosmetic_*` / `pet` 灰字：仅 soft，**不宣称已清**；
- 精英预览壳：证据不足，**勿硬开**；
- B0.1：**仍挂**，无新证据不重开，**勿宣称 B0.1 已清**；
- 其它 NI 文件、其它 lore、NI 键名 / `name` / `material` / enchantments / hideflags、配方 / 给物 / 掉落 / 镶嵌逻辑、TrMenu、CoreRpg、四件甲、锻炉重做、誓约主动大改、墙钟 / DPS 均不做；不叫挑刺。

## 5. 硬约束与施工边界

| 可动（仅批 A 且另开施工） | 不可动（本窗硬禁） |
|---|---|
| 目标 YAML 中 `gem_ember_steady` lore 的 1 行裸 id | `gem_ember_drain` / `gem_ember_gale`；其它 `&7gem_*`；其它 YAML；斜杠；cosmetic/pet |
| 批后按服约定 reload | `name`「稳固石」、键名、material、L44/L45、enchantments、hideflags |
| 物品岗静态核验 + 玩家悬停 | 数值 / 配方 / 给物 / 掉落 / 镶嵌逻辑 / TrMenu / CoreRpg；git push |

**本设计窗仅写 tip + backlog；未批前 NI Items YAML 零改。**

## 6. 验收口径（物品岗 / 测试）

1. **静态 `rg`：**
   ```bash
   rg -n "gem_ember_steady" plugins/NeigeItems/Items/ember-enhance-gems.yml
   ```
   批 A 施工后只命中管理侧键名 L39，不命中 lore；L44「镶嵌石 · 微量防御」与 L45「刃 / 护符孔可用」保留。
2. **live 计数：**
   ```bash
   rg -n "&7gem_" plugins/NeigeItems/Items/
   ```
   批 A 后预期 **2 命中**，对应 `gem_ember_drain` / `gem_ember_gale`；这是本窗单件收口，不等于 gem 轨、斜杠、cosmetic、pet 或其它 soft 已清。
3. **悬停：** 给 / 取 1 枚「稳固石」，悬停确认显示名仍为「稳固石」，lore 不出现 `gem_ember_steady`，且「镶嵌石 · 微量防御」「刃 / 护符孔可用」仍在。
4. **零越界：** 其余 2 件 gem、其它斜杠、cosmetic/pet、精英壳、B0.1、键名、数值 / 配方 / 给物 / 掉落 / 镶嵌逻辑均无额外变更；不以 wall-clock / DPS 验收。

失败条：施工后悬停仍显示裸 id；借机改 name、其它 gem、斜杠、cosmetic/pet 或精英壳；未批先改 YAML；或宣称 B0.1 / 其它 soft 已清，均为 **FAIL**。

## 7. 专岗与回报

- **策划：** 本稿 + backlog，向总控申请批 A。
- **物品：** 批 A 后只删上述实际 L43 的 1 行 lore。
- **测试：** `rg` + 悬停轻测；不做 wall-clock / DPS，不叫挑刺。
- **回总控：** tip 路径 `docs/design-ember-gem-steady-ni-lore-copy.md`；本窗建议 **A：只删实际 L43 的 `&7gem_ember_steady` 一行，不改「稳固石」name，保留 L44/L45 两行说明**；tip/backlog 可 commit，**勿 push**。
