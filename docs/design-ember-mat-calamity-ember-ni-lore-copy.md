# 设计稿 · NI 灾厄余烬 lore 裸 id 人话对齐（B2.30）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-mat-calamity-ember-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-abyss-calamity.yml` 内 `mat_calamity_ember` **1** 行 lore 裸 NI id；**未获批前不改 NI Items YAML**。不改 NI 数值、配方、给物、掉落、体力、TrMenu 或 CoreRpg。
>
> B2.29 已 **PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`）。本窗承接其余 live `&7mat_*` 的下一件最薄债；只推灾厄余烬 1 件，**勿宣称 B0.1 已清**。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 灾厄余烬 · **玩家可见 lore 裸 id `mat_calamity_ember` 对齐**（UX · B2.30） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:53 Asia/Shanghai |
| 状态 | **已批 A** |
| 关联 | live `plugins/NeigeItems/Items/ember-abyss-calamity.yml` · B2.29 勾销后下一件 `&7mat_*` |
| 专岗 | **物品**；验收 **`rg` + 悬停** |

## 1. 问题一句话

`mat_calamity_ember` 的显示名已经是「灾厄余烬」，但 lore 首行仍显示管理侧裸 id `&7mat_calamity_ember`；玩家背包悬停会看到内部代号。本窗只处理该物品这一行，属于 B2.24–29 同构的单件薄文案窗。

## 2. 现网只读事实

| 项 | 现网证据 |
|---|---|
| 文件 | `plugins/NeigeItems/Items/ember-abyss-calamity.yml` |
| 键 | `mat_calamity_ember:`（L4，管理侧键名保留） |
| material | `BLAZE_POWDER`（L5，保留） |
| 显示名 | `&4灾厄余烬`（L6，已中文，**不改 name**） |
| 目标 lore | L8 `- '&7mat_calamity_ember'`（本窗唯一目标） |
| 其余 lore | L9 `- '&8世界 Boss · 变异材料'`；L10 `- '&8用于外观兑换 / 重铸'`（保留） |
| 交互检查 | 本段未见 `/corerpg` 或其它命令斜杠；本窗不借机新增或改斜杠 |
| 同文件邻项 | `cosmetic_calamity_shard` 及其 `&7cosmetic_*` lore **不动** |
| live 扫描 | 施工前 live Items（排除 `*.bak*`）共有 3 件 `&7mat_*`：本件 + `mat_ember_protect_scroll` + `mat_ember_stable_charm`；本窗后预期剩后两件 2 件 |

只读目标段：

```yaml
mat_calamity_ember:
  material: BLAZE_POWDER
  name: '&4灾厄余烬'
  lore:
    - '&7mat_calamity_ember'  # 本窗唯一目标
    - '&8世界 Boss · 变异材料'
    - '&8用于外观兑换 / 重铸'
```

## 3. 推荐方案 A（待批）

**只删 `mat_calamity_ember` lore 的 1 行 `- '&7mat_calamity_ember'`。** 显示名已经是「灾厄余烬」，不改 `name`；L9/L10 保留。此方案零改数值、配方、给物、掉落与既有物品逻辑。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 删除目标 lore 灰字裸 id 1 行 | **已批 A；交物品岗施工** |
| A′ | 改为灰字「灾厄余烬」 | 不荐：与显示名重复 |
| B | 顺手处理其它 mat 裸 id | **只作 soft，勿双上** |

管理侧键名 `mat_calamity_ember:` 仍保留；本稿不是键名、材质、强化或配方改动。

## 4. B soft 与明确排除

仅旁附观察，不在本件 A 中施工：

- `mat_ember_protect_scroll`：`&7mat_ember_protect_scroll`；
- `mat_ember_stable_charm`：`&7mat_ember_stable_charm`；
- 其它 `/corerpg` 斜杠：另开窗口，**勿与本窗斜杠双上**；
- `cosmetic_*`（含同文件 `cosmetic_calamity_shard`）：非本窗目标，**不动**；
- 精英预览壳：hub 一点进本、无 P / 无独立 rewards 的证据不足，**勿硬开**。

同样不做：其它 NI 文件整批、其它 lore、NI 键名 / `name` / `material` / enchantments / hideflags、配方 / 给物 / 掉落 / 体力、TrMenu、CoreRpg、四件甲、锻炉重做、誓约主动大改、墙钟 / DPS；不叫挑刺，**勿宣称 B0.1 已清**。

## 5. 硬约束与施工边界

| 可动（仅批 A 且另开施工） | 不可动（本窗硬禁） |
|---|---|
| 目标 YAML 中 `mat_calamity_ember` lore 的 1 行裸 id | 其它 2 件 mat；`cosmetic_*`；精英壳；其它 YAML |
| 批后按服约定 reload | `name`「灾厄余烬」、键名、material、L9/L10、enchantments、hideflags |
| 物品岗静态核验 + 玩家悬停 | 数值 / 配方 / 给物 / 掉落 / 体力 / TrMenu / CoreRpg；git push |

**本设计窗仅写 tip + backlog；未批前 NI Items YAML 零改。**

## 6. 验收口径（物品岗 / 测试）

1. **静态 `rg`：**
   ```bash
   rg -n "mat_calamity_ember" plugins/NeigeItems/Items/ember-abyss-calamity.yml
   ```
   批 A 施工后只命中管理侧键名行，不命中 lore；目标文件其余两行 lore 保留。
2. **live 计数：**
   ```bash
   rg -n "&7mat_" plugins/NeigeItems/Items/ --glob '!*.bak*'
   ```
   批 A 后只剩 `mat_ember_protect_scroll` 与 `mat_ember_stable_charm` 两件；bak 文件不计 live。
3. **悬停：** 给 / 取 1 枚「灾厄余烬」，悬停确认显示名仍为「灾厄余烬」，lore 不出现 `mat_calamity_ember`，且「世界 Boss · 变异材料」「用于外观兑换 / 重铸」仍在。
4. **零越界：** `cosmetic_*`、其它两件 mat、键名、数值 / 配方 / 给物 / 掉落 / 斜杠、精英壳均无额外变更；不以 wall-clock / DPS 验收。

失败条：施工后悬停仍显示裸 id；借机改 name、其它两件、cosmetic、斜杠或精英壳；未批先改 YAML；或宣称 B0.1 / 其它 `&7mat_*` 已清，均为 **FAIL**。

## 7. 专岗与回报

- **策划：** 本稿 + backlog，向总控申请批 A。
- **物品：** 批 A 后只删上述 1 行 lore。
- **测试：** `rg` + 悬停轻测；不做 wall-clock / DPS，不叫挑刺。
- **回总控：** tip 路径 `docs/design-ember-mat-calamity-ember-ni-lore-copy.md`；本窗建议 **A：只删 `&7mat_calamity_ember` 这一行，显示名「灾厄余烬」已中文则不改 name**；tip/backlog 可 commit，**勿 push**。
