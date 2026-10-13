# STATUS · TIP-CV W1a 短征选页「路径追」可点 · short_path* PAPI + enter · D13327

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · **勿勾 tip CLOSED** · 批 A·M `ac2889160`

## 交付
- `%corerpg_p1_short_path%`（path token `0|1|2|3`）/ `_label` / `_tip`（复用 `EmberShortPath.label/tip`；未选诚实「未选」）
- `%corerpg_p1_short_path_resolve%` / `_resolve_name`：当前偏好 `EmberShortPath.resolve` 的 map key / 名；空=无可追
- `%corerpg_p1_short_path_ok%`：path 有效（非 NONE）∧ resolve 非空 → `1`；**未选 path → `0`**（禁静默改偏好）
- `%corerpg_p1_short_path_line%`：菜单友好一行（`路径追 · 未选` / `<label> · 下一本 SXNN name` / `· 暂无下一本`）
- `/corerpg p1 enter short-path`（别名 `short-chase-path` / `short_path` / `short_chase_path`）→ resolve 本 → tryEnter；门禁同该本
  - NONE → 拒进并提示先选偏好（选页「路径追」）
  - 有 path 无 resolve → 拒进 `resolveLine(null)` 诚实文案
- 切偏好**复用**已有：`corerpg p1 shortpath fc|day|late|clear`（零改语义）
- 单测：`EmberShortPathChaseTest`（NONE/ok/resolve key/line/label/tip；resolve 复用零改）；`EmberRunPapiTest` 路由七键
- jar：`1.65.819-d13327.local` · sha256 `197d6d3e7cbc…` · ECONOMY_BV **123** 未动
- play：live `1.65.819-d13327.local` · storage=mysql · bv123 · MySQL connected
- feat：`a61ebb18c` → origin/main
- **零改** resolve 排序/权重 / 日帽 / S40–S58 / weekly_goals.targets.short / Cast / Aid / variety / 日差 `short_div*` / 庭内 / forgeroll·convert 价 / 十九本格菜单 yml
- **未做** 菜单 W1b/c（物品岗）

## 键清单
| 键 | 口径 |
|----|------|
| `short_path` | path token `0`/`1`/`2`/`3`（NONE/FC/DAY/LATE） |
| `short_path_label` | `EmberShortPath.label`（未选→未选） |
| `short_path_tip` | `EmberShortPath.tip` |
| `short_path_resolve` | resolve map key；空=无可追 |
| `short_path_resolve_name` | 该本名（无 resolve→空） |
| `short_path_ok` | path 有效 ∧ resolve 非空→1 |
| `short_path_line` | 一行摘要（菜单友好） |
| enter `short-path` | 解析为 resolve 后同 enter &lt;map&gt; |

## 落点
- `EmberShortPathChase`：token / resolveKey / resolveName / ok / line（纯逻辑）
- `EmberRunPapi`：SHORT 节 `shortPathChase`；route `short_path*`
- `EmberRunService`：enter 别名 short-path / short-chase-path
- `pom.xml` / `plugin.yml`：1.65.819-d13327.local

## 零改边界
- `EmberShortPath.resolve` 排序/权重（仅复用）
- 日帽 / S40–S58 / weekly_goals.targets.short / short_div*
- Cast / Aid / variety / 庭内 / forgeroll·convert 价
- balance_version 123 · sx20 · 十九本格菜单 yml

## 下步
- 物品 W1b/c（选页 `0` + 薄路径页 + Open 辅半行）等键
