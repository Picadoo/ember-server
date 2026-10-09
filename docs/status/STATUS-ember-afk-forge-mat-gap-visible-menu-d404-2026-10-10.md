# 状态 · D404：挂机→工坊仓材料还差 · 菜单 W1c/W1d/W1e（已批 A·M · 菜单侧壳已落 · 插件未齐 · ≠关观察 ≠抬日表 ≠sx07）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md`](../design/DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md) @ tip `110acd63` · tip [`STATUS-ember-next-hard-debt-afk-forge-mat-gap-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-forge-mat-gap-visible-need-design-2026-10-10.md) · backlog `B-afk-forge-mat-gap-visible`  
**裁决：** **已批 A · 批 M · D404 · 施工中** · 本号只交 **W1c 速览挂键壳 + W1d 挂机半行 + W1e 仓库半行** · 插件 W1a/W1b（`vault_*` / `recipe_gap_*` PAPI）**另派未齐** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠改 EmberUpgradeRules** · **≠开 gate_daily / R / K3** · **≠交 sx07** · **≠动 Stage2 三开关** · **≠假写仓余额/还差数字**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

配方速览多了「仓内现有」和「代表档·还差」两格——键上线后会显示仓里碎片/骨尘/核心/胚料和对照强化+1、升阶 T1→T2、精工 0→1 还差多少。现在键还没 live，只写诚实预留，不假写数字。挂机「去哪花」和仓库半行都指到「配方速览可看仓内还差」。

## 落地对照（方案 M · 本号 = W1c/W1d/W1e）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 仓余额四键 | **另派** | 插件未交 live（源码 WIP 不属本号）；菜单**注释预留** |
| W1b 代表档还差 | **另派** | enhance1 / upgrade_t2 / refine1 键未 live → 注释预留 |
| W1c 速览挂键 | **PASS（菜单壳）** | `ember_p1_forge_recipes` 格 **C** 仓内现有 · 格 **G** 代表档还差 · 禁假写 |
| W1d 挂机半行 | **PASS** | `ember_p1_afk` S「去哪花」+「配方速览可看仓内还差」 |
| W1e 仓库半行 | **PASS（可选）** | `ember_storage` V 半行同指 |
| W1f AFK remain/eta | **本窗不做** | 后置 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge_recipes.yml` | Layout 增 C/G；仓内现有/还差诚实预留；K 文案对齐；Open 半行 |
| `plugins/TrMenu/menus/ember_p1_afk.yml` | 去哪花 +仓内还差半行 |
| `plugins/TrMenu/menus/ember_storage.yml` | 材料仓 V 半行指速览还差 |
| DESIGN / tip / backlog | 勾批 A·M 已齐 · **施工中**（插件未齐） |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| 1 速览可见仓内/还差格 | **PASS（菜单）** | C/G 格在；**无**假写个人余额/gap |
| 2 去哪花半行 | **PASS** | 挂机 S 含「配方速览可看仓内还差」 |
| 3 仓库半行 | **PASS** | storage V 半行 |
| 4 零改表 | **PASS（纪律）** | 未 stage Java / afk.tiers / daily_kills / UpgradeRules / ember-v1 开关 |
| 5 无假平面 | **PASS** | 无旧日常假开；无魂尘挂机产；无裸指令教学；无 sx07 |
| trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (88 ms)`（**06:44:47 CST**）|

## 不动

- Java / `EmberUpgradeRules` / afk.tiers / daily_kills / Stage2 / gate_daily / jar  
- 假挂 `%corerpg_p1_vault_*%` / `%corerpg_p1_recipe_gap_*%` 造成字面量或假数  
- 本号**不派全链路测**（等插件键齐后再派；菜单可另派薄抽）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (88 ms)`（**2026-10-10 06:44:47 CST**）
