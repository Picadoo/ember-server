# STATUS · S1 日常 MVP「余烬窟·庭院」分房

**日期：** 2026-09-28 00:51（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-stamina-dnf-daily.md` §B / §B.3 / §B.5；现网 P4；派工 · S1  
**CoreRpg 版本：** **1.15.13**（已部署 play：`plugins/CoreRpg.jar`）  
**Verdict：** **✅ S1 可玩落地**（门廊→≥2 道先清后开铁门→独立 Boss 终厅；体力数值未改）

---

## 一句话

P4 单院三波改为 **DNF 式分房**：有顶门廊安全区 → 前厅（僵尸×5）→ 铁门1 → 回廊侧厅混编（×6）→ 铁门2 → 终厅中央垫 Boss 蛮兵×1。`DailyCourtyardService` 已同步，防日后 `/corerpg dailybuild` 回退。

---

## 1. 结构说明

| 段 | Z（约） | 内容 |
|----|---------|------|
| 门廊（安全） | -4..2 | 有顶；spawn `(0,65,0)` 面朝 +Z；告示「清尽前厅再开门」 |
| 房1 前厅 | 3..12 | 开放庭院柱廊；灰烬材质；僵尸×5 |
| **门1** | **z=13** | IRON_FENCE 门洞 x=-2..1、y=65..67（12 格）；清完 `$operation-block`→AIR +「前厅已清 · 门开了」 |
| 房2 回廊/侧厅 | 14..24 | 西侧掩体 + 东侧砂砾 alcove；僵尸×3+骷髅×3 |
| **门2** | **z=25** | 同上铁门；清完「回廊已清 · Boss 门开了」 |
| Boss 终厅 | 26..38 | 独立厅；中央垫抬高 1；蛮兵 `(0,66,31)` |

- **轮廓：** x=-10..9 × z=-4..38（南北拉长），与周本深廊 / 深渊竖井俯视可区分  
- **材质：** 石砖 / 砂砾 / 圆石 / 地狱岩火盆 / 海晶灯（灰烬庭院意象保留）  
- **禁止：** 不再「同一院子三波刷怪」

---

## 2. 坐标表

| 角色 | 脚 Y | 坐标 | 说明 |
|------|------|------|------|
| **spawn / 门廊** | **65** | **(0, 65, 0)** | 面朝 +Z 第一道门 |
| 门1 平面 | 65–67 | z=**13**，x=-2..1 | 铁栅栏门 |
| wave1 | 65 | (-5,65,7)×2 · (5,65,7)×2 · (0,65,10)×1 | 僵尸合计 ×5 |
| 门2 平面 | 65–67 | z=**25**，x=-2..1 | 铁栅栏门 |
| wave2 | 65 | 僵尸 (-6,65,17)×2+(6,65,17)×1；骷髅 (-6,65,21)×2+(6,65,21)×1 | 合计 ×6 |
| **Boss 垫** | **66** | **(0, 66, 31)** | 蛮兵×1；垫面相对回廊 +1 |

`$setmap{name=ember_daily}` **未改**。体力消耗 **30 未改**（S0）。

---

## 3. 备份路径

`server-runtime/config-backups/ember_daily-s1-20260928-004438/`

| 内容 | 说明 |
|------|------|
| `map/ember_daily/` | 施工前整份 DP map |
| `dungeon/EmberDaily/` · `ember_daily/` | 改前 option/monster |
| `menus/ember_daily.yml` | 菜单快照（S0 已灰显 B/C，本岗仅确认） |

---

## 4. 改动文件

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../DailyCourtyardService.java` | **重写** S1 分房建造（门廊/房1/门/房2/门/Boss终厅） |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.12 → 1.15.13** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_daily/region/*.mca` | 分房地形（`/corerpg dailybuild` 导出，blocks≈10696） |
| `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | 开场「门廊安全 · 清前厅开门」；spawn 仍 `0,65,0` |
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 波次坐标+数量；阶段 `$operation-block` 开门；播报短句 |
| `docs/STATUS-ember-daily-dnf-s1.md` | 本文件 |

**未改：** 体力数值 / Paper/NMS / 线 B/C 完整图 / MM 血攻 / 通关箱内容。  
**TrMenu：** 线 B「焦骨甬道」、线 C「残誓地窖」仍灰显「筹备中」（S0 已做，本岗确认）。

---

## 5. 验收证据

| 检查 | 结果 | 证据 |
|------|------|------|
| dailybuild 导出 | ✅ | `[dailybuild] courtyard done · blocks≈10696 · door1z=13 door2z=25 · pad=(0,66,31)`（`/tmp/s1-daily-build-result.json`） |
| 模板门挡房 | ✅ | anvil：spawn 可达 z=-4..12；**无法**越过 z=13；iron 门1/门2 各 **12/12** |
| 进本落地 | ✅ | `(0, 65, 0)`；「已消耗体力 · 门廊安全 · 清前厅开门」+「【前厅】清尽僵尸再开门」 |
| 体力 | ✅ 未改 | 进本仍 -30（S0 路径） |
| TrMenu B/C | ✅ | `ember_daily.yml` 仍 §c筹备中 |
| `ops.json` 终态 | ✅ **`[]`** | 测后 `/deop` + 写空 |
| 清怪→开门实战 | 📎 | `mm mobs killall` **不计入** DP `$kill`；需玩家击杀掉落条件后触发 `$operation-block`（脚本已接线） |

痕迹：`/tmp/s1-daily-build-result.json` · `/tmp/s1-daily-smoke-result.json` · `/tmp/s1-backup-path.txt`

---

## 6. 验收命令（建议）

```
/corerpg dailybuild table          # admin：坐标表
/corerpg enter daily               # 有体力≥30、Lv≥10：落门廊，看 +Z 铁门
# 清前厅僵尸×5 → 应见「前厅已清 · 门开了」，铁门变通路
# 清回廊混编×6 → 「回廊已清 · Boss 门开了」→ 终厅垫打蛮兵
```

大改后若见旧院：清 `plugins/DungeonPlus/dungeon-caches/dungeon_EmberDaily_*` 后重进（本岗结案前已清）。

复跑建造：`/corerpg dailybuild`（admin）会覆盖 `map/ember_daily` region。

---

## 7. 不做 / 债务

1. 线 B/C 完整图（菜单占位至 S2）  
2. 改体力数值 / 无限体力 / 一图一服 / Paper·NMS  
3. 本岗 **未** commit/push（交主代理决定）  
4. 清怪开门依赖玩家击杀计数 — 自动化 killall 无法冒烟该段；挑刺玩家实战抽检即可  

---

## 8. 回报主代理

- **可玩证据：** 门廊 spawn `(0,65,0)` → 门1 z=13（铁门×12）→ 房2 → 门2 z=25 → Boss 垫 `(0,66,31)`；anvil 证明门关闭时不可越门  
- **STATUS：** `docs/STATUS-ember-daily-dnf-s1.md`  
- **关键坐标：** spawn `(0,65,0)` · 门1 `z=13` · 门2 `z=25` · Boss `(0,66,31)`  
- **改了：** DailyCourtyardService + EmberDaily option/monster + map region；**bump jar → 1.15.13**  
- **备份：** `server-runtime/config-backups/ember_daily-s1-20260928-004438/`  
- **ops：** `[]`  
- **验收建议：** 进日常看门廊朝向铁门；清前厅听「前厅已清 · 门开了」再进回廊  
