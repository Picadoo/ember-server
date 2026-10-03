# 设计稿 · NI 余烬保护券 lore 裸 id 人话对齐（B2.31）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 docs/status/STATUS-ember-mat-protect-scroll-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `mat_ember_protect_scroll` **1** 行 lore 裸 NI id；**未获批前不改 NI Items YAML**。不改 NI 数值、配方、给物、强化逻辑或其它物品。
>
> B2.30 已 **PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）。本窗承接下一件最薄债；只推保护券 1 件，**勿宣称 B0.1 已清**。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 余烬保护券 · **玩家可见 lore 裸 id `mat_ember_protect_scroll` 对齐**（UX · B2.31） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:58 Asia/Shanghai |
| 状态 | **已批 A** |
| 关联 | live `plugins/NeigeItems/Items/ember-enhance-gems.yml` · B2.30 勾销后下一件 `&7mat_*` |
| 专岗 | **物品**；验收 **`rg` + 悬停** |

## 1. 问题一句话

`mat_ember_protect_scroll` 的显示名已经是「余烬保护券」，但 lore 首行仍显示管理侧裸 id `&7mat_ember_protect_scroll`；玩家背包悬停会看到内部代号。本窗只处理该物品这一行，属于 B2.24–30 同构的单件薄文案窗。

## 2. 现网只读事实

| 项 | 现网证据 |
|---|---|
| 文件 | `plugins/NeigeItems/Items/ember-enhance-gems.yml` |
| 键 | `mat_ember_protect_scroll:`（L5，管理侧键名保留） |
| material | `PAPER`（L6，保留） |
| 显示名 | `&d余烬保护券`（L7，中文，**不改 name**） |
| 目标 lore | L9 `- '&7mat_ember_protect_scroll'`（本窗唯一目标） |
| 其余 lore | L10 `- '&8强化失败时防止掉级'`；L11 `- '&7用于 +7～+9 等高危强化'`（保留） |
| 同文件邻项 | `mat_ember_stable_charm` 及其 `&7mat_ember_stable_charm` lore **不动** |
| live 扫描 | 施工前排除 `*.bak*` 后共有 2 件 `&7mat_*`：本件 + `mat_ember_stable_charm`；本窗后预期只剩 stable charm 1 件 |

只读目标段：

```yaml
mat_ember_protect_scroll:
  material: PAPER
  name: '&d余烬保护券'
  lore:
    - '&7mat_ember_protect_scroll'  # 本窗唯一目标，L9
    - '&8强化失败时防止掉级'          # L10 保留
    - '&7用于 +7～+9 等高危强化'      # L11 保留
```

## 3. 推荐方案 A（待批）

**只删 `mat_ember_protect_scroll` lore 的 1 行 `- '&7mat_ember_protect_scroll'`。** 显示名已经是「余烬保护券」，不改 `name`；L10/L11 保留。此方案零改数值、配方、给物与既有强化逻辑。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 删除目标 lore 灰字裸 id 1 行 | **已批 A；交物品岗施工** |
| A′ | 改为灰字「余烬保护券」 | 不荐：与显示名重复 |
| B | 顺手处理其它 mat / gem / 斜杠 | **只作 soft，勿双上** |

管理侧键名 `mat_ember_protect_scroll:` 仍保留；本稿不是键名、材质、强化、配方或给物改动。

## 4. B soft 与明确排除

仅旁附观察，不在本件 A 中施工：

- `mat_ember_stable_charm`：`&7mat_ember_stable_charm`，**勿厚批其余 1 件**；
- 其它 `/corerpg` 斜杠：另开窗口，**勿斜杠双上**；
- `gem_*`：另开窗口，**不动**；
- 精英预览壳：hub 一点进本、无 P / 无独立 rewards 的证据不足，**勿硬开**；
- 其它 NI 文件、其它 lore、NI 键名 / `name` / `material` / enchantments / hideflags、配方 / 给物 / 掉落 / 强化数值、TrMenu、CoreRpg、四件甲、锻炉重做、誓约主动大改、墙钟 / DPS 均不做；不叫挑刺，**勿宣称 B0.1 已清**。

## 5. 硬约束与施工边界

| 可动（仅批 A 且另开施工） | 不可动（本窗硬禁） |
|---|---|
| 目标 YAML 中 `mat_ember_protect_scroll` lore 的 1 行裸 id | `mat_ember_stable_charm`；`gem_*`；其它 YAML；斜杠；精英壳 |
| 批后按服约定 reload | `name`「余烬保护券」、键名、material、L10/L11、enchantments、hideflags |
| 物品岗静态核验 + 玩家悬停 | 数值 / 配方 / 给物 / 掉落 / 强化逻辑 / TrMenu / CoreRpg；git push |

**本设计窗仅写 tip + backlog；未批前 NI Items YAML 零改。**

## 6. 验收口径（物品岗 / 测试）

1. **静态 `rg`：**
   ```bash
   rg -n "mat_ember_protect_scroll" plugins/NeigeItems/Items/ember-enhance-gems.yml
   ```
   批 A 施工后只命中管理侧键名 L5，不命中 lore；L10「强化失败时防止掉级」与 L11「用于 +7～+9 等高危强化」保留。
2. **live 计数：**
   ```bash
   rg -n "&7mat_" plugins/NeigeItems/Items/ --glob '!*.bak*'
   ```
   批 A 后只剩 `mat_ember_stable_charm` 1 件；备份文件不计 live。`gem_*` 不纳入本窗。
3. **悬停：** 给 / 取 1 枚「余烬保护券」，悬停确认显示名仍为「余烬保护券」，lore 不出现 `mat_ember_protect_scroll`，且「强化失败时防止掉级」「用于 +7～+9 等高危强化」仍在。
4. **零越界：** `mat_ember_stable_charm`、`gem_*`、其它斜杠、精英壳、键名、数值 / 配方 / 给物 / 掉落 / 强化逻辑均无额外变更；不以 wall-clock / DPS 验收。

失败条：施工后悬停仍显示裸 id；借机改 name、stable charm、gem、斜杠或精英壳；未批先改 YAML；或宣称 B0.1 / 其它 `&7mat_*` 已清，均为 **FAIL**。

## 7. 专岗与回报

- **策划：** 本稿 + backlog，向总控申请批 A。
- **物品：** 批 A 后只删上述 1 行 lore。
- **测试：** `rg` + 悬停轻测；不做 wall-clock / DPS，不叫挑刺。
- **回总控：** tip 路径 `docs/design/design-ember-mat-protect-scroll-ni-lore-copy.md`；本窗建议 **A：只删 `&7mat_ember_protect_scroll` 这一行，显示名「余烬保护券」已中文则不改 name，保留 L10/L11**；tip/backlog 可 commit，**勿 push**。
