# 状态 · D405：挂机今日还差/约满 ETA · 菜单 W1c/W1d（已批 A·M · 菜单侧已挂 live 键 · ≠关观察 ≠抬日表 ≠sx07）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-afk-remain-eta-visible-2026-10-10.md`](../design/DESIGN-ember-afk-remain-eta-visible-2026-10-10.md) @ tip `778a9371` · tip [`STATUS-ember-next-hard-debt-afk-remain-eta-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-remain-eta-visible-need-design-2026-10-10.md) · backlog `B-afk-remain-eta-visible` · 插件 STATUS [`STATUS-ember-afk-remain-eta-papi-d405-2026-10-10.md`](STATUS-ember-afk-remain-eta-papi-d405-2026-10-10.md) tip `7123d944`  
**裁决：** **已批 A · 批 M · D405 · 施工中** · 本号只交 **W1c 战况挂键 + W1d Open/hub 半行** · 插件 W1a/W1b **已 live**（`7123d944` · jar `1.65.114-d405.local`）· **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠交 sx07** · **≠动 Stage2 三开关** · **≠假写固定分钟/保证满** · **零 Java**

**toplevel：** `/workspace/minecraft` @ `main`

## 人话

挂机战况「今日软目标」下面多了一行真 PAPI：还差几只、约几分钟满（未测速写「开打后估时」、已满写去冒险/去哪花）。Open tell 和枢纽挂机格半行都指「战况可看还差/约满」。不抬产量。

## 落地对照（方案 M · 本号 = W1c/W1d）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a remain 键 | **另号已齐** | tip `7123d944` · `%corerpg_p1_afk_remain%` |
| W1b eta_min / remain_line | **另号已齐** | tip `7123d944` · `eta_min` + `remain_line` |
| W1c 战况挂键 | **PASS** | `ember_p1_afk` 格 I · today 下挂 `%corerpg_p1_afk_remain_line%`（真键 · 禁假写） |
| W1d Open/hub 半行 | **PASS（可选）** | Open tell「战况可看还差/约满」· hub Z 半行同指 |
| W1e ActionBar / W1f 仓差挂战况 | **本窗不做** | 后置 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | I 软目标块 +remain_line；Open tell 半行 |
| `plugins/TrMenu/menus/ember_hub.yml` | Z 挂机格 +「战况可看还差/约满」 |
| DESIGN / tip / backlog | 勾批 A·M 已齐 · **施工中**（菜单本号 · 插件已齐） |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| 1 战况可见还差/约满行 | **PASS（菜单）** | I 挂 `remain_line` live |
| 2 Open/hub 半行 | **PASS** | 无假「保证 X 分钟满」 |
| 3 零改表 | **PASS（纪律）** | 未 stage Java / afk.tiers / daily_kills / ember-v1 开关 |
| 4 无假平面 | **PASS** | 无旧日常假开；无魂尘挂机产；无 sx07 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (164 ms)`（**06:52:14 CST**）|

## 不动

- Java / afk.tiers / daily_kills / Stage2 / gate_daily / jar（插件另号已交）
- 假写固定「约 N 分钟必满」字面量
- 本号**不派全链路测**（菜单可另派薄抽）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (164 ms)`（**2026-10-10 06:52:14 CST**）
