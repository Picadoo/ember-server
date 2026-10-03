# 设计稿 · NI 余烬稳固符 lore 裸 id 人话对齐（B2.32）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 docs/status/STATUS-ember-mat-stable-charm-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `mat_ember_stable_charm` **1** 行 lore 裸 NI id；**未获批前不改 NI Items YAML**。不改 NI 数值、配方、给物、强化逻辑或其它物品。
>
> B2.31 已 **PASS · 勾销**（设计 `1bdacd8` · 批准 `4359e89` · 施工 `c0e8fcc` · 测 `b2d9773` · close `c8ab22c`）。本窗承接 live 最后 1 件 `&7mat_*`；只推稳固符 1 件，**勿宣称 B0.1 已清**。

## 0. 稿件信息

| 字段 | 填写 |
|---|---|
| 稿件 | NI 余烬稳固符 · **玩家可见 lore 裸 id `mat_ember_stable_charm` 对齐**（UX · B2.32） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:03 Asia/Shanghai |
| 状态 | **已批 A** |
| 关联 | live `plugins/NeigeItems/Items/ember-enhance-gems.yml` · B2.31 勾销后 live 最后 1 件 `&7mat_*` |
| 专岗 | **物品**；验收 **`rg` + 悬停** |

## 1. 问题一句话

`mat_ember_stable_charm` 的显示名已经是「余烬稳固符」，但 lore 首行仍显示管理侧裸 id `&7mat_ember_stable_charm`；玩家背包悬停会看到内部代号。本窗只处理该物品这一行，延续 B2.24–31 的单件薄文案窗。

## 2. 现网只读事实

| 项 | 现网证据 |
|---|---|
| 文件 | `plugins/NeigeItems/Items/ember-enhance-gems.yml` |
| 键 | `mat_ember_stable_charm:`（L16，管理侧键名保留） |
| material | `NETHER_STAR`（L17，保留） |
| 显示名 | `&b余烬稳固符`（L18，中文，**不改 name**） |
| 目标 lore | L20 `- '&7mat_ember_stable_charm'`（本窗唯一目标） |
| 其余 lore | L21 `- '&8+10 强化失败时不掉级'`；L22 `- '&7仅失败，不掉档'`（均保留） |
| live 扫描 | `rg -n '&7mat_' plugins/NeigeItems/Items/ --glob '!*.bak*'` 当前仅 1 件：本件；原样全扫另命中 `ember-dungeon.yml.bak-ap-lore` 的 3 行备份，不计 live |

只读目标段（实际行号）：

```yaml
mat_ember_stable_charm:
  material: NETHER_STAR
  name: '&b余烬稳固符'
  lore:
    - '&7mat_ember_stable_charm' # L20，本窗唯一目标
    - '&8+10 强化失败时不掉级'   # L21，保留
    - '&7仅失败，不掉档'         # L22，保留
```

当前 live `&7mat_*` 计数为 **1**；批准并施工本稿后，验收期望 Items 下归零。这里的归零只指本轨 live `&7mat_*` 收口，**不宣称其它 soft 已清**。

## 3. 推荐方案 A（待批）

**只删 `mat_ember_stable_charm` lore 的 1 行 `- '&7mat_ember_stable_charm'`（实际 L20）。** 显示名已经是「余烬稳固符」，不改 `name`；L21/L22 两行说明保留。此方案零改数值、配方、给物与既有强化逻辑。

| 方案 | 做法 | 本窗 |
|---|---|---|
| **A · 荐** | 删除目标 lore 灰字裸 id 1 行 | **已批 A；交物品岗施工** |
| A′ | 改为灰字「余烬稳固符」 | 不荐：与显示名重复 |
| B | 顺手处理其它 mat / gem / 斜杠 | **只作 soft，勿双上** |

管理侧键名 `mat_ember_stable_charm:` 仍保留；本稿不是键名、材质、强化、配方或给物改动。

## 4. B soft 与明确排除

仅旁附观察，不在本件 A 中施工：

- `gem_*`：另开窗口，**不动**；
- 其它 `/corerpg` 斜杠：另开窗口，**勿斜杠双上**；
- 精英预览壳：hub 一点进本、无 P / 无独立 rewards 的证据不足，**勿硬开**；
- B0.1：**仍挂**，无新证据不重开，**勿宣称 B0.1 已清**；
- 其它 NI 文件、其它 lore、NI 键名 / `name` / `material` / enchantments / hideflags、配方 / 给物 / 掉落 / 强化数值、TrMenu、CoreRpg、四件甲、锻炉重做、誓约主动大改、墙钟 / DPS 均不做；不叫挑刺。

## 5. 硬约束与施工边界

| 可动（仅批 A 且另开施工） | 不可动（本窗硬禁） |
|---|---|
| 目标 YAML 中 `mat_ember_stable_charm` lore 的 1 行裸 id | `gem_*`；其它 `&7mat_*`；其它 YAML；斜杠；精英壳 |
| 批后按服约定 reload | `name`「余烬稳固符」、键名、material、L21/L22、enchantments、hideflags |
| 物品岗静态核验 + 玩家悬停 | 数值 / 配方 / 给物 / 掉落 / 强化逻辑 / TrMenu / CoreRpg；git push |

**本设计窗仅写 tip + backlog；未批前 NI Items YAML 零改。**

## 6. 验收口径（物品岗 / 测试）

1. **静态 `rg`：**
   ```bash
   rg -n "mat_ember_stable_charm" plugins/NeigeItems/Items/ember-enhance-gems.yml
   ```
   批 A 施工后只命中管理侧键名 L16，不命中 lore；L21「+10 强化失败时不掉级」与 L22「仅失败，不掉档」保留。
2. **live 计数：**
   ```bash
   rg -n "&7mat_" plugins/NeigeItems/Items/ --glob '!*.bak*'
   ```
   批 A 后 Items 下预期 **0 命中**；这是本轨 live `&7mat_*` 收口，备份文件不计 live，**不等于其它 soft 已清**，`gem_*` 不纳入本窗。
3. **悬停：** 给 / 取 1 枚「余烬稳固符」，悬停确认显示名仍为「余烬稳固符」，lore 不出现 `mat_ember_stable_charm`，且「+10 强化失败时不掉级」「仅失败，不掉档」仍在。
4. **零越界：** `gem_*`、其它 `&7mat_*`、其它斜杠、精英壳、B0.1、键名、数值 / 配方 / 给物 / 掉落 / 强化逻辑均无额外变更；不以 wall-clock / DPS 验收。

失败条：施工后悬停仍显示裸 id；借机改 name、gem、其它 mat、斜杠或精英壳；未批先改 YAML；或宣称 B0.1 / 其它 soft 已清，均为 **FAIL**。

## 7. 专岗与回报

- **策划：** 本稿 + backlog，向总控申请批 A。
- **物品：** 批 A 后只删上述 1 行 lore。
- **测试：** `rg` + 悬停轻测；不做 wall-clock / DPS，不叫挑刺。
- **回总控：** tip 路径 `docs/design/design-ember-mat-stable-charm-ni-lore-copy.md`；本窗建议 **A：只删实际 L20 的 `&7mat_ember_stable_charm` 一行，不改「余烬稳固符」name，保留 L21/L22 两行说明**；tip/backlog 可 commit，**勿 push**。
