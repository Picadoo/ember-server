# 状态 · D408-fix：挂机满额追短征 W1c（体力&lt;30 诚实）

**日期：** 2026-10-10（上海时间）  
**上游 FAIL：** spot tip **`f45cc48f`** · STATUS [`STATUS-ember-afk-full-short-chase-spot-d408-2026-10-10.md`](STATUS-ember-afk-full-short-chase-spot-d408-2026-10-10.md)  
**上游施工：** tip **`36f38d1b`** · DESIGN [`DESIGN-ember-afk-full-short-chase-2026-10-10.md`](../design/DESIGN-ember-afk-full-short-chase-2026-10-10.md) @ `191b845f`  
**裁决：** **W1c 菜单修 · 已落地** · 仅 TrMenu · **零 Java** · **≠派测（总控复测）** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠动 Stage2 三开关** · **≠改 jar / ×0.97**

**toplevel：** `/workspace/minecraft` @ `main`

## 人话

薄抽红点：挂机满额后体力=12，F 没切到「短征需30」档，右键仍打开短征选页。根因是图标级复合条件 `afk_full>=1 and stamina<30` 在本服未命中。本号删掉 `and` 独立档，只留一条满额刀；右键改成**嵌套单条件**只判体力——不够就 bass+tell，够才开选页。lore 满额常驻「短征需 30 · 当前 N」，不靠 and 换皮。

## 根因（对照 FAIL `f45cc48f`）

| 项 | 口径 |
|----|------|
| 红 | M5/M6：体力=12 时 F 仍「右键 ➥ 短征选本」；右键开 `ember_p1_short`，无诚实 tell |
| 绿对照 | 同号同体力下 `ember_p1_short` / hub 单条件 `stamina < 30` 正常命中 |
| 根因 | `icons` 分支 `check papi %corerpg_p1_afk_full% >= 1 and check papi %corerpg_stamina% < 30` **未命中**，落到仅 `afk_full>=1` 分支 |

## 修法（钉死 · 已落）

1. **删** F 的 priority-1 `and` 整块独立 icons。
2. **只留**一条 `afk_full >= 1` 展示（priority 1）。
3. 右键改为嵌套 **condition + actions + deny**，**单条件**只判 stamina：
   - `stamina < 30` → bass + tell「短征需 30 体力 · 当前 %corerpg_stamina%」· **不开**选页
   - deny（≥30）→ pling + `menu: ember_p1_short`
4. 左键仍 `menu: ember_p1_adventure`（D285 保留）。
5. lore 满额**常驻**「短征需 §e30 · 当前 %corerpg_stamina%」。
6. Open/I 满额半行**未**用同类 `and` → **未改**（仍常驻短征半行）。

## 最终右键 YAML 片段

```yaml
actions:
  left:
    - 'sound: BLOCK_NOTE_PLING-1-2'
    - 'menu: ember_p1_adventure'
  right:
    - condition: 'check papi %corerpg_stamina% < 30'
      actions:
        - 'sound: BLOCK_NOTE_BASS-1-0'
        - 'tell: §c短征需 30 体力 · 当前 %corerpg_stamina%'
      deny:
        - 'sound: BLOCK_NOTE_PLING-1-2'
        - 'menu: ember_p1_short'
```

`deny` 键本服 TrMenu v3.12.5 reload **可加载**（未改 `deny-actions`）。

## 验收（本号 · 菜单面 / reload）

| ID | 结果 | 备注 |
|----|------|------|
| R1 删 and 档 | **PASS** | `ember_p1_afk.yml` 无 `afk_full … and … stamina` |
| R2 满额单档 | **PASS** | 仅 `afk_full >= 1`；lore 常驻短征需30 |
| R3 右键嵌套 | **PASS（静态）** | condition+actions+deny 单条件 stamina |
| R4 左键 D285 | **PASS（静态）** | 仍 `ember_p1_adventure` |
| R5 Open/I | **PASS（未动）** | 无 and；半行保留 |
| R6 trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (111 ms)`（**2026-10-10 07:26:23 CST**） |
| R7 零改表 | **PASS（纪律）** | 未动 Java / jar / afk 产量 / Stage2 / gate_daily / ×0.97 |
| R8 live 复测 | **未派** | 总控复测 |

## tip / 产物

| 项 | 值 |
|----|-----|
| 菜单文件 | `plugins/TrMenu/menus/ember_p1_afk.yml` |
| md5 | `6497cd78992a7ce983b37e08ca770fd0` |
| FAIL 引用 | spot **`f45cc48f`** |
| trmenu reload | `良好 \| 73 个菜单已加载 (111 ms)` @ **07:26:23 CST** |
| 分支 | main |
| 派测 | **否**（总控复测） |

## 手法与注记

- 仅改 `ember_p1_afk.yml` F icons + 头注释；`scripts/console.sh play "trmenu reload"`
- reload 日志另有既有 `Player.updateCommands` NoSuchMethodError（1.12 已知噪音）· **菜单加载行仍绿**
- **未**切分支 / stash / reset / `checkout -- .` / force-push
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）
- 工作区并行脏文件：**未** `git add -A`；只 stage 本号文件

## 结束态

- Stage2：`enabled/migrate/set_bonus=true`（未拧）
- `afk.daily_kills=2400`（未抬）
- gate_daily 未开
- jar 未换
