# STATUS · 日常菜单必改 2+3 · 体力灰显 live 修复

**日期：** 2026-09-28 05:27（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · 修体力不足灰显 live】；测报 `docs/status/STATUS-ember-daily-menu-mustfix-test.md`（FAIL）  
**范围：** 仅 `plugins/TrMenu/menus/ember_daily.yml`；未改 jar / 体力数值 / 平衡；**未 commit/push**。

## 结论

**priority: true · ✅ 结案（请复测）**

必改 3 live 灰显已修好：体力 &lt; 30 时七键灰 pane +「0:00 回满」，点击**不**执行 `corerpg enter`；≥30 正常材质并可进本。

| 项 | 结果 |
|----|------|
| hub lore（必改 2，既有） | 保持 PASS（本轮未改 hub） |
| 体力不足七键灰（S/A/C/D/E/F/G） | ✅ live PASS |
| 灰态点击不 enter | ✅ live PASS（仅 tell「体力不足，需 30…」） |
| 充足可进 | ✅ live PASS（庭院 enter → 扣 30 → 开本） |
| ops 终态 | play=`[]` · login=`[]` |

## 旧写法为何失败

测报确认：顶栏 `%corerpg_stamina%` 已正确显示 `10/90`，但子图标条件不成立 → **非体力未写入**。

旧 condition（双侧 PAPI）：

```yaml
condition: 'check papi *%corerpg_stamina% < *%corerpg_stamina_cost_daily%'
```

失败原因（TrMenu 3.12.5 / Kether）：

1. **`check papi` 右侧宜为字面量数字**。现网/文档范例均为 `check papi %xxx% < 100`（撬锁小游戏等）；双侧 `*%papi_a% < *%papi_b%` 在本版不会得到 true。
2. **`*` 前缀不宜套在 `check papi` 的占位符上**。范例写 `%placeholder%` 无星号；`perm *vip` / `money *1000` 的 `*` 是字面量语法，与 papi check 不同。
3. **仅有 `update: 20`、缺 `refresh:`**：`update` 只刷材质/名/lore 占位；**`refresh` 才按条件重算子图标**。开窗瞬间本应算一次，但因 condition 本身恒假，加不加 refresh 开窗也不灰——根因仍是 1/2。

## 新写法

文件：`plugins/TrMenu/menus/ember_daily.yml`  
七键 **S / A / C / D / E / F / G** 一并：

```yaml
icons:
  - condition: 'check papi %corerpg_stamina% < 30'
    priority: 2
    inherit: true
    display:
      material: gray stained glass pane
      name: '§8… · 体力不足'
      lore:
        - '§c体力不足 · 每日 0:00 回满'
        # … 当前/所需体力
    actions:
      all:
        - 'sound: BLOCK_NOTE_PLING-1-0.6'
        - 'tell: §c体力不足，需 %corerpg_stamina_cost_daily% 点体力；每日 0:00 回满。'
        # 无 corerpg enter
update: 20
refresh: 20
# 默认态 display + command: corerpg enter … 不变
```

要点：

- 日常 cost **固定 30**（与 `%corerpg_stamina_cost_daily%` / CoreRpg 一致）；lore/tell 仍可用 PAPI 展示「需 30」。
- 每键补 `refresh: 20` 与既有 `update: 20` 并列。
- 顶栏 `T` 顺手加 `update: 20`（避免体力变更后顶栏偶发陈旧；不影响灰态条件）。

TrMenu 自动重载：`良好 | 自动重新载入菜单 ember_daily.yml (19ms)`（05:22 CST）；随后为加载 OP 短重启 play 一次。

## Live 证据

- **探针脚本：** `/tmp/grayfix-stamina-live.js` · JSON `/tmp/grayfix-stamina-live.json` · log `/tmp/grayfix-stamina-live.log`
- **临时 OP：** `GrayFixOp42`（ops.json 离线 UUID → 短重启 play）；测后 `ops=[]`
- **玩家：** `GrayFixP7830` · Lv.10 · `/trmenu open ember_daily`

### 体力 = 10

| 键 | 名称 | 材质 |
|----|------|------|
| S | 余烬窟·庭院 · 体力不足 | stained_glass_pane |
| A | 余烬窟·焦骨甬道 · 体力不足 | stained_glass_pane |
| C | 余烬窟·残誓地窖 · 体力不足 | stained_glass_pane |
| D | 余烬窟·潮蚀水道 · 体力不足 | stained_glass_pane |
| E | 余烬窟·断塔回廊 · 体力不足 | stained_glass_pane |
| F | 余烬窟·霜晶裂隙 · 体力不足 | stained_glass_pane |
| G | 余烬窟·锈轨矿道 · 体力不足 | stained_glass_pane |

- 顶栏：`余烬体力 10/90`
- 点庭院 chat：**仅** `体力不足，需 30 点体力；每日 0:00 回满。`  
  **无**「正在进入」/ 建队 / 开本

### 体力 = 90

| 键 | 名称 | 材质 |
|----|------|------|
| S | 余烬窟·庭院 | iron_sword |
| A | 余烬窟·焦骨甬道 | netherrack |
| C | 余烬窟·残誓地窖 | mossy_cobblestone |
| D | 余烬窟·潮蚀水道 | prismarine |
| E | 余烬窟·断塔回廊 | cobblestone |
| F | 余烬窟·霜晶裂隙 | packed_ice |
| G | 余烬窟·锈轨矿道 | rail |

- 点庭院：`日 正在进入 余烬窟·庭院 …` → `[日常] 正在进入……（体力 -30）` → `成功创建了新的队伍` → `余烬窟·庭院 开始！`
- 注：同轮脚本里顶栏一度仍显示 10（`T` 当时无 update；图标条件已按 90 切正常态）。已补 `T.update: 20`；请复测时一并看顶栏。

## 成功标准对照

| 标准 | 状态 |
|------|------|
| live 体力不足七键灰 | ✅ |
| 点击不 enter | ✅ |
| 充足可进 | ✅ |
| STATUS 更新 | ✅（本文件） |
| 未 commit/push | ✅ |

## 请测试岗复测

重点：`stamina set 10` → 开 `ember_daily` → 七键灰 + 点 S 只有不足 tell；`set 90` → 七键正常 + 点 S 可进。顶栏应跟体力同步。
