# 设计稿 · NI 余烬烬火 lore 裸 id 人话对齐（B2.39）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-pet-cinder-ni-lore-copy-approve.md）。本稿只定玩家可见 NeigeItems `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_cinder` lore **1 行**灰字裸 id；批准后由物品岗施工。玩法 YAML 本窗零改，不改数值、给物、召唤/喂养逻辑或其它物品。
>
> B2.38 已 **PASS · 勾销**（设计 `965ab2b` · 批准 `bbded5f` · 测 `72b544e` · 施工 `7dbf09c` · close `b59d150`）。本窗只升 `pet_ember_cinder`；`pet_ember_ashling` 已清，任何斜杠行、精英壳与 B0.1 均不在 A 内。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 余烬使魔·烬火 · **玩家可见 lore 裸 id `pet_ember_cinder` 对齐**（UX · B2.39） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:42 Asia/Shanghai |
| 状态 | **已批 A** |
| 文件 / 键 | `plugins/NeigeItems/Items/ember-pets.yml` / `pet_ember_cinder:` |
| 专岗 / 验收 | **物品** / `rg` + 悬停 |

## 1. 问题一句话

`pet_ember_cinder` 的 name 已是「余烬烬火」，但 lore 首行仍显示管理侧裸 id `&7pet_ember_cinder`。本窗只删该件这一行，保留外观说明、出战说明与 `/corerpg pet summon` 斜杠行，避免裸 id 与斜杠双上。

## 2. 现网只读事实

只读扫描得到 `pet_ember_cinder` 整段全部 lore：

```yaml
# plugins/NeigeItems/Items/ember-pets.yml
pet_ember_cinder:                         # L16，键名保留
  material: MONSTER_EGG                    # L17，保留
  name: '&6余烬烬火'                        # L18，保留
  lore:                                     # L19
    - '&7pet_ember_cinder'                  # L20，本窗唯一目标
    - '&8使魔蛋 · 外观 / 余火'                # L21，保留
    - '&8出战 1 只 · 不卖满级战力'             # L22，保留
    - '&7用于 /corerpg pet summon'           # L23，保留；斜杠勿动
```

`pet_ember_ashling` 只读确认：其 lore 已无裸 id；现行段落保留 name「余烬灰灵」、两行说明与 `/corerpg pet summon`，不在本窗改动。

现网 live 扫描：

```bash
rg -n "&7pet_" plugins/NeigeItems/Items/
# 1 命中：ember-pets.yml L20 pet_ember_cinder
```

因此本窗目标实际行号是 **L20**，同件斜杠实际行号是 **L23**；ashling 裸 id 已无。

## 3. 推荐方案 A（已批）

**A · 荐：只删 `pet_ember_cinder` 实际 L20 的 `- '&7pet_ember_cinder'` 1 行。**

必须保留：

- name「余烬烬火」；
- L21「使魔蛋 · 外观 / 余火」；
- L22「出战 1 只 · 不卖满级战力」；
- **L23 `- '&7用于 /corerpg pet summon'` 斜杠行，勿动。**

不改 `pet_ember_ashling`，不改 material、enchantments、hideflags、数值或给物逻辑。管理侧键名 `pet_ember_cinder:` 保留。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 只删 cinder 裸 id lore 1 行（L20） | **已批 A** |
| A′ | 把裸 id 改成重复中文 | 不荐：name 已足够，增加重复 |
| B | 顺手删 summon/feed 或处理 ashling、其它 pet | **排除；勿与 A 双上** |

## 4. 硬约束与排除

- 本窗只处理 `pet_ember_cinder` 的裸 id；不得动已清的 `pet_ember_ashling`。
- **不得删除或改写** summon/feed 等斜杠行；尤其 L23 `/corerpg pet summon` 勿动。
- 不改玩法 YAML、NI 数值、material、enchantments、hideflags、配方、掉落、给物、召唤/喂养逻辑、TrMenu 或 CoreRpg。
- 其它 pet、其它 `/corerpg` 斜杠、精英预览壳、B0.1 仅 soft/排除；不宣称已清，不叫挑刺。
- 未批前 `plugins/NeigeItems/Items/` 零改；tip 与 backlog 可提交；**勿 git push**。

## 5. 验收口径（物品岗）

1. **静态 `rg`：**施工后本件 lore 无字面 `pet_ember_cinder`；管理侧键名可留。L21/L22/L23 保留。
2. **本件范围：**可写“本件 pet 烬火灰字清”；不得宣称 ashling、斜杠或 B0.1 已清。
3. **live 统计：**施工后全局 `rg -n "&7pet_" plugins/NeigeItems/Items/` 预期为 **0**（本轨归零）；这不等于斜杠或 B0.1 已清。
4. **悬停：**给/取 1 枚余烬烬火，确认 name「余烬烬火」、两行说明及 `/corerpg pet summon` 仍在；不做数值/战力测试。
5. **零越界：**除批准的 L20 单行外，其余 NI Items、玩法 YAML、斜杠与 ashling 零 diff。

失败条：误删 L21/L22/L23；触碰 ashling；顺手处理其它 pet/斜杠；修改玩法或物品逻辑；或把本件结果扩大宣称为斜杠/B0.1 已清，均为 FAIL。

## 6. 回总控

- **tip：**`docs/design-ember-pet-cinder-ni-lore-copy.md`
- **状态：**已批 A
- **荐 A：**只删实际 **L20** 的 `- '&7pet_ember_cinder'` 1 行；不动实际 **L23** `/corerpg pet summon`；不动已清的 ashling。
- **当前 live `&7pet_*`：**1 命中（仅 cinder L20）；批后预期 **0**（本轨归零）。
- **边界：**tip + backlog 可 commit；NI/玩法 YAML 本窗零改；勿 push；**勿宣称斜杠/B0.1 已清**。
