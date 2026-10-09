# 状态 · D398：工坊配方速览（已批 A·M · 已落地 · ≠关观察 ≠抬日表）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-afk-forge-recipe-visible-2026-10-10.md`](../design/DESIGN-ember-afk-forge-recipe-visible-2026-10-10.md) @ tip `43d9d6f3` · tip [`STATUS-ember-next-hard-debt-afk-forge-recipe-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-forge-recipe-visible-need-design-2026-10-10.md) · backlog `B-afk-forge-recipe-visible`  
**裁决：** **已批 A · 批 M · D398 · 已落地** · 总控采纳方案 M · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠改 EmberUpgradeRules** · **≠开 gate_daily / R / K3** · **≠动 Stage2 三开关**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

工坊底栏多了「配方速览」：未持装备也能看见强化代表档（+1/+4/+7/+10）和升阶/精工/成色费用，并对照挂机四层打满「约」够哪档。挂机「去哪花」半行指到速览。产量和价表一行没改。

## 落地对照（方案 M）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 速览入口 | **PASS** | `ember_p1_forge` Layout `# A P bB#` · 格 **P** → `menu: ember_p1_forge_recipes` |
| W1b 静态数字表 | **PASS** | 薄页 E/U/R/Q：强化代表档 + 升阶/精工/成色；甲不可用与极品不可养成诚实 |
| W1c 挂机对照「约」 | **PASS** | 薄页 A：四层日量 +「约」句；纪律：按现网日表、不保证 N 天、不加快产量 |
| W1d 互指半行 | **PASS** | 挂机 S「去哪花」+配方速览半行 + +1 一例；速览 Z→工坊、V→挂机庭；工坊 A 材料哪来保留 |
| W1e 枢纽半行 | **本窗未做** | 可选；另号 |
| W1f 仓余额 | **本窗未做** | 薄页 K 跳仓库查看；不新写 Java PAPI |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge_recipes.yml` | **新建** 配方速览薄页 |
| `plugins/TrMenu/menus/ember_p1_forge.yml` | 底栏 P 格 + Open 半行 |
| `plugins/TrMenu/menus/ember_p1_afk.yml` | 去哪花 lore 指速览 |
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已落地** |
| 本 STATUS | 施工结案 |

## 验收（本号）

| ID | 结果 | 备注 |
|----|------|------|
| 1 未持件能进速览 | **PASS（菜单）** | P → 薄页 |
| 2 数字抽样 | **PASS（照抄 DESIGN§2.2）** | +1/+10、T1→T2、精工0→1、极品不可养成 |
| 3 去哪花指速览 | **PASS** | 挂机 S 半行 |
| 4 零改表 | **PASS（纪律）** | 未 stage Java / afk.tiers / daily_kills / ember-v1 开关 |
| 5 无假平面 | **PASS** | 无旧日常假开；无魂尘挂机产；无裸指令教学 |
| trmenu reload | 见旁注 | 本号热更 |

## 不动

- `EmberUpgradeRules` / afk.tiers / daily_kills / Stage2 / gate_daily / jar  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |
