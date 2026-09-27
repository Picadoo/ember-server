# STATUS · 日常菜单体力灰显 · 复测

**日期：** 2026-09-28 05:36（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-menu-mustfix.md`（插件岗 live 修复结案 · 请复测）  
**范围：** 仅复测；**禁改配置**；**未 commit/push**  
**探针：** `/tmp/daily-menu-gray-retest.js` · JSON `/tmp/daily-menu-gray-retest.json` · log `/tmp/daily-menu-gray-retest.log`

## 总评

**✅ PASS**

| 项 | 结果 |
|----|------|
| YAML 静态（七键 cond=`check papi %corerpg_stamina% < 30` + gray pane + 无 enter + `refresh:20`） | ✅ |
| 体力&lt;30 七键灰 +「0:00 回满」 | ✅ live |
| 灰态点击不走 enter（仅 tell） | ✅ live |
| 体力≥30 七键正常材质 | ✅ live |
| 充足可进（庭院 enter → 扣 30 → 开本） | ✅ live |
| 顶栏同步 | ✅ `10/90` → `90/90` |
| ops 终态 | play=`[]` · login=`[]` |

## 环境

- 工作区 `/workspace/minecraft`；play 短重启加载临时 OP（`ops.json` 热写不生效，需停服写入）
- 端口 proxy 25565 / login 25566 / play 25567
- `/trmenu reload` 后测；菜单 `ember_daily` 已含新写法（与 mustfix 一致）
- 临时 OP：`DmGyOp88` · 玩家：`DmGyP4069` · Lv.10
- 测后 `deop` + `ops.json=[]`（play + login）

## 证据 1 · 体力 = 10（&lt;30）

顶栏：`余烬体力 10/90`

| 键 | 名称 | 材质 |
|----|------|------|
| S | 余烬窟·庭院 · 体力不足 | stained_glass_pane |
| A | 余烬窟·焦骨甬道 · 体力不足 | stained_glass_pane |
| C | 余烬窟·残誓地窖 · 体力不足 | stained_glass_pane |
| D | 余烬窟·潮蚀水道 · 体力不足 | stained_glass_pane |
| E | 余烬窟·断塔回廊 · 体力不足 | stained_glass_pane |
| F | 余烬窟·霜晶裂隙 · 体力不足 | stained_glass_pane |
| G | 余烬窟·锈轨矿道 · 体力不足 | stained_glass_pane |

- lore 含「体力不足」「0:00」「回满」；灰态 actions **无** `corerpg enter`
- 点庭院 chat：**仅** `体力不足，需 30 点体力；每日 0:00 回满。`  
  **无**「正在进入」/ 建队 / 开本 / 位移进本

## 证据 2 · 体力 = 90（≥30）

顶栏：`余烬体力 90/90`

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

## 与上次 FAIL 对照

| 上次 FAIL（mustfix-test） | 本次复测 |
|---------------------------|----------|
| &lt;30 live 不切换灰态 | ✅ 七键均灰 pane +「· 体力不足」 |
| 点仍走 enter | ✅ 仅不足 tell，无 enter |

根因修复（双侧 PAPI → 字面量 30 + `refresh:20`）在 live 复测成立。

## 成功标准对照

| 标准 | 状态 |
|------|------|
| 报告落盘 | ✅ 本文件 |
| 结论明确 | ✅ PASS |
| ops 清空 | ✅ play=`[]` login=`[]` |
| 未改配置 / 未 commit/push | ✅ |

## 阻塞点

无。
