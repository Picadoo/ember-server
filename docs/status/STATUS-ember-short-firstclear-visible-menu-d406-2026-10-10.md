# 状态 · D406：短征生涯首通态同屏（TrMenu · 已批 A·M · 已落地 · ≠关观察 ≠抬日表 ≠sx07）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-firstclear-visible-2026-10-10.md`](../design/DESIGN-ember-short-firstclear-visible-2026-10-10.md) @ tip `0203bf16` · tip [`STATUS-ember-next-hard-debt-short-firstclear-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-firstclear-visible-need-design-2026-10-10.md) · backlog `B-short-firstclear-visible`  
**裁决：** **已批 A · 批 M · D406 · 已落地** · 本号交 W1a/W1b + W1c（冒险半行）· **零 Java** · 合计键 `sx_fc_left`/`pending_line` **未 live → 未挂** · **≠关观察** · **≠抬日表** · **≠改 S40–S45 / 首通包数额 / 日帽** · **≠开 gate_daily / R / K3** · **≠交 sx07** · **≠动 Stage2 三开关**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

打开短征选本，各本悬停「生涯首通」跟真键走：未领看包摘要、已领变「已领取」；说明格六行可扫。不再静态谎称「另加：币…」。日帽行照旧。

## 落地对照（方案 M）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 六本格挂 fc | **PASS** | A/C/E/G/K/M 主 lore：`§7生涯首通：§f%corerpg_p1_sx0N_fc%` 替换静态「另加：币…」；`day_line` 保留 |
| W1b I 说明同屏 | **PASS** | 六行短名+fc；去掉「生涯首通另加薄包」独行；日帽六行+left_sum 保留 |
| W1c 可选合计/冒险半行 | **部分 PASS** | 合计键未 live → **未挂**；冒险 H +半行「选本页可见生涯首通」；Open tell +半行 |
| W1d 插件合计键 | **本号不做** | 未 live；禁假写 |
| W1e 改包/sx07 | **禁** | 未触 |

## 挂键清单

| Placeholder | 挂点 |
|-------------|------|
| `%corerpg_p1_sx01_fc%` … `%corerpg_p1_sx06_fc%` | A/C/E/G/K/M 各 1 · I 各 1 |
| `%corerpg_p1_sx0N_day_line%` / `sx_day_left_sum` | **保留**（非本债） |

未挂：`%corerpg_p1_sx_fc_left%` / `%corerpg_p1_sx_fc_pending_line%`（插件未交）。

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 六本+I 挂 fc；去静态另加；Open 半行 |
| `plugins/TrMenu/menus/ember_p1_adventure.yml` | H 短征入口 +半行 |
| DESIGN / tip / backlog | 批 A·M 已齐 · **已落地** |
| 本 STATUS | 菜单侧结案 |
| Java / jar / runs / S40–S45 / 日帽 / afk / Stage2 | **未动 / 未 stage** |

## 验收（本号 · 菜单面静态）

| ID | 结果 | 备注 |
|----|------|------|
| V 真键替换静态 | **PASS** | 无「生涯首通另加：币…」字面 |
| V I 六本可扫 | **PASS** | 六行 fc + 日帽保留 |
| V 合计未假写 | **PASS** | 无 left/pending 假数字 |
| V 零改表 | **PASS（纪律）** | 未 stage Java/runs/economy/afk/开关 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (103 ms)`（**06:59:46 CST**） |

## 不动

- 关观察 · 抬 afk / daily_kills · 开 gate_daily · 改 S40–S45 / 首通包 · 同号写 Java · 假挂合计键 · 交 sx07 · stage 脏 runtime · 派测（总控钉死本号勿派测）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (103 ms)`（**2026-10-10 06:59:46 CST**）
- 旁注：既有 TrMenu `Player.updateCommands` NoSuchMethodError（1.12）· 与本号无关 · 菜单数 73 正常
