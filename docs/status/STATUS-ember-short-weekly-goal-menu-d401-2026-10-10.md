# 状态 · D401：短征周目标钩 · 菜单 W1d（已批 A·M · 菜单侧已落 · 插件未齐 · ≠关观察 ≠抬日表 ≠sx06）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-weekly-goal-2026-10-10.md`](../design/DESIGN-ember-short-weekly-goal-2026-10-10.md) @ tip `f75a44c8` · tip [`STATUS-ember-next-hard-debt-short-weekly-goal-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-weekly-goal-need-design-2026-10-10.md) · backlog `B-short-weekly-goal`  
**裁决：** **已批 A · 批 M · D401 · 施工中** · 本号只交 **W1d 菜单互指** · 插件 W1a–W1c（targets/OPTIONAL/结算/`goal_short` PAPI）**另派未齐** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠交 sx06** · **≠动 Stage2 三开关** · **≠假写 ✔**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

赛季周目标页多一格「周目标 · 短征（可选）」——点一下进短征选本。短征选页打开时会提醒：本周有可选周目标。枢纽赛季格旁注也写了。进度数字等插件键 live 后再挂；现在不假写 ✔。

## 落地对照（方案 M · 本号 = W1d）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 源表+OPTIONAL | **另派** | 插件未交；`weekly_goals.targets` 仍无 `short` |
| W1b 结算挂钩 | **另派** | 插件未交 |
| W1c PAPI `goal_short` | **另派** | 键未 live → 菜单**注释预留**，禁假 ✔ |
| W1d 菜单互指 | **PASS（菜单）** | 赛季格 6 → `ember_p1_short`；短征 Open/I 半行；hub 赛季格半行 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_season.yml` | Layout `#12345 6#`；格 6「周目标·短征（可选）」· 诚实预留进度文案 · 点击→短征选本 |
| `plugins/TrMenu/menus/ember_p1_short.yml` | Open tell + I 说明半行「本周可选周目标·短征」 |
| `plugins/TrMenu/menus/ember_hub.yml` | 赛季格 Y 半行「含可选短征周目标」 |
| DESIGN / tip / backlog | 勾批 A·M · **施工中**（插件未齐） |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| V6 赛季页+短征选页 | **PASS（菜单）** | 可见可选短征格；点击进真选本；**无**假 ✔；**无**sx06；**无**旧日常窟 |
| V6b 进度键 | **预留** | `%corerpg_p1_goal_short%` 未挂 live（YAML 注释写明）；键号后改挂 |
| V7 配置纪律 | **PASS** | 未改 afk / stamina / Stage2 / gate_daily / Java / runs.yml weekly_goals |
| 文案禁 | **PASS** | 不暗示发币/印记/装备；只写外观徽；无旧日常/sx06 |
| trmenu reload | **PASS** | `良好 \| 72 个菜单已加载 (129 ms)`（**06:28:06 CST**）；RegisterCommands `updateCommands` WARN 为 1.12 已知噪点 |

## 不动

- Java / `ember-v1-runs.yml#weekly_goals` / afk / Stage2 / gate_daily / sx06  
- 假挂 `%corerpg_p1_goal_short%` 造成菜单字面量或假 ✔  
- 本号**不派全链路测**（等插件键齐后再派；菜单可另派薄抽）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 72 个菜单已加载 (129 ms)`（**2026-10-10 06:28:06 CST**）
