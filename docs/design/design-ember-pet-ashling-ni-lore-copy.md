# 设计稿 · NI 余烬灰灵 lore 裸 id 人话对齐（B2.38）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 docs/status/STATUS-ember-pet-ashling-ni-lore-copy-approve.md）。本稿只定玩家可见 NeigeItems `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_ashling` lore **1 行**灰字裸 id；批准后由物品岗施工。玩法 YAML 本窗零改，不改数值、给物、召唤/喂养逻辑或其它物品。
>
> B2.37 已 **PASS · 勾销**（设计 `3717031` · 批准 `d786875` · 施工 `3c49b53` · 测 `7f21335` · close `da8088d`）。本窗只升 `pet_ember_ashling`；`pet_ember_cinder`、任何斜杠行、精英壳与 B0.1 均不在 A 内。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 余烬使魔·灰灵 · **玩家可见 lore 裸 id `pet_ember_ashling` 对齐**（UX · B2.38） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:36 Asia/Shanghai |
| 状态 | **已批 A** |
| 文件 / 键 | `plugins/NeigeItems/Items/ember-pets.yml` / `pet_ember_ashling:` |
| 专岗 / 验收 | **物品** / `rg` + 悬停 |

## 1. 问题一句话

`pet_ember_ashling` 的 name 已是「余烬灰灵」，但 lore 首行仍显示管理侧裸 id `&7pet_ember_ashling`。本窗只删该件这一行，保留外观说明、出战说明与 `/corerpg pet summon` 斜杠行，避免裸 id 与斜杠双上。

## 2. 现网只读事实

只读扫描得到 `pet_ember_ashling` 整段全部 lore：

```yaml
# plugins/NeigeItems/Items/ember-pets.yml
pet_ember_ashling:                         # L4，键名保留
  material: MONSTER_EGG                    # L5，保留
  name: '&a余烬灰灵'                        # L6，保留
  lore:                                     # L7
    - '&7pet_ember_ashling'                 # L8，本窗唯一目标
    - '&8使魔蛋 · 外观 / 微光'                # L9，保留
    - '&8出战 1 只 · 不卖满级战力'             # L10，保留
    - '&7用于 /corerpg pet summon'           # L11，保留；斜杠勿动
```

`pet_ember_cinder` 只读确认：

```yaml
pet_ember_cinder:                           # L17，本窗不动
  name: '&6余烬烬火'                         # L19，不动
  lore:                                      # L20
    - '&7pet_ember_cinder'                   # L21，不动
    - '&8使魔蛋 · 外观 / 余火'                 # L22，不动
    - '&8出战 1 只 · 不卖满级战力'              # L23，不动
    - '&7用于 /corerpg pet summon'            # L24，不动
```

现网 live 扫描（不含 bak）：

```bash
rg -n "&7pet_" plugins/NeigeItems/Items/
# 2 命中：L8 pet_ember_ashling、L21 pet_ember_cinder
```

因此本窗目标实际行号是 **L8**，同件斜杠实际行号是 **L11**；`pet_ember_cinder` 实际裸 id 行是 **L21**，均不施工。

## 3. 推荐方案 A（已批）

**A · 只删 `pet_ember_ashling` 实际 L8 的 `- '&7pet_ember_ashling'` 1 行。**

必须保留：

- name「余烬灰灵」；
- L9「使魔蛋 · 外观 / 微光」；
- L10「出战 1 只 · 不卖满级战力」；
- **L11 `- '&7用于 /corerpg pet summon'` 斜杠行，勿动。**

不改 `pet_ember_cinder`（含 L21/L24），不改 material、enchantments、hideflags、数值或给物逻辑。管理侧键名 `pet_ember_ashling:` 保留。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 只删 ashling 裸 id lore 1 行（L8） | **已批 A** |
| A′ | 把裸 id 改成重复中文 | 不荐：name 已足够，增加重复 |
| B | 顺手删 summon/feed 或处理 cinder、其它 pet | **排除；勿与 A 双上** |

## 4. 硬约束与排除

- 本窗只处理 `pet_ember_ashling` 的裸 id；不得改 `pet_ember_cinder`。
- **不得删除或改写** summon/feed 等斜杠行；尤其 L11 `/corerpg pet summon` 勿动。
- 不改玩法 YAML、NI 数值、material、enchantments、hideflags、配方、掉落、给物、召唤/喂养逻辑、TrMenu 或 CoreRpg。
- 其它 pet、其它 `/corerpg` 斜杠、精英预览壳、B0.1 仅 soft/排除；不宣称已清，不叫挑刺。
- 未批前 `plugins/NeigeItems/Items/` 零改；tip 与 backlog 可提交；**勿 git push**。

## 5. 验收口径（物品岗）

1. **静态 `rg`：**施工后仅在本件 lore 中无字面 `pet_ember_ashling`；管理侧键名可留。L9/L10/L11 均保留。
2. **本件范围：**可写“本件 pet 灰字清”；不得宣称 `pet_ember_cinder`、斜杠或 B0.1 已清。
3. **live 统计：**施工后全局 `rg -n "&7pet_" plugins/NeigeItems/Items/` 预期仍为 **1**（仅 cinder L21），不是全局归零。
4. **悬停：**给/取 1 枚余烬灰灵，确认 name「余烬灰灵」、两行说明及 `/corerpg pet summon` 仍在；不做数值/战力测试。
5. **零越界：**除批准的 L8 单行外，其余 NI Items、玩法 YAML、斜杠与 cinder 零 diff。

失败条：误删 L9/L10/L11；触碰 cinder；顺手处理其它 pet/斜杠；修改玩法或物品逻辑；或把本件结果扩大宣称为另一 pet/斜杠/B0.1 已清，均为 FAIL。

## 6. 回总控

- **tip：**`docs/design/design-ember-pet-ashling-ni-lore-copy.md`
- **状态：**已批 A
- **荐 A：**只删实际 **L8** 的 `- '&7pet_ember_ashling'` 1 行；不动实际 **L11** `/corerpg pet summon`；不动 cinder 实际 L21/L24。
- **当前 live `&7pet_*`：**2 命中（ashling L8、cinder L21）；批后预期 1 命中（仅 cinder）。
- **边界：**tip + backlog 可 commit；NI/玩法 YAML 本窗零改；勿 push。
