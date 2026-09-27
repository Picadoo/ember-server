# STATUS · P2 ember_abyss 下行竖井实景

**日期：** 2026-09-27 20:53–21:04（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-multiworld-maps.md` §2 深渊行 · §4.2「深渊垂直可读」  
**Verdict：** **✅ P2 完成**（井顶入口、1～12 环台落差、井底开阔看守区、上浮撤离牌、刷点开阔、option/monster 对齐、ops=[]）

---

## 1. 备份路径

`server-runtime/config-backups/ember_abyss-p2-20260927-205353/`

| 内容 | 说明 |
|------|------|
| `region/` + `level.dat` | 施工前整份 DP map `ember_abyss` |
| `dungeon/option.yml` · `monster.yml` | 改坐标前的 EmberAbyss 脚本 |

---

## 2. 竖井结构与平台坐标表

**中心轴：** `CX=-40, CZ=270`（贴近旧占位，减少脚本大改面）  
**施工：** `/corerpg abyssbuild`（`AbyssShaftService`，CoreRpg **1.15.4**）→ 临时世界 `ember_abyss_build` 分批建造 → 导出回 `plugins/DungeonPlus/map/ember_abyss`  
**体量：** blocks≈**37151**；井筒 r≈9～11；F10～12 深域扩至 r≈14～16 + 基岩外圈

| 角色 | 脚 Y | 平台板 Y | 玩家/刷点建议 | 材质带 |
|------|------|----------|---------------|--------|
| **井顶入口 spawn** | **90** | 89 | **(-40, 90, 274)** 石英垫（中心孔可下望） | 石砖/圆石 + 石英 |
| floor1 | 85 | 84 | 主 **(-38,85,274)** · 次 (-42,85,274) | 石砖带 |
| floor2 | 80 | 79 | 主 **(-42,80,266)** · 次 (-38,80,266) | 石砖带 |
| floor3 | 75 | 74 | 主 **(-38,75,274)** · 次 (-42,75,274) | 石砖带 |
| floor4 | 70 | 69 | 主 **(-42,70,266)** · 次 (-38,70,266) | 圆石/石砖 |
| floor5 | 65 | 64 | 主 **(-38,65,274)** · 次 (-42,65,274) | 下界砖过渡 |
| floor6 | 60 | 59 | 主 **(-42,60,266)** · 次 (-38,60,266) | 下界砖/地狱岩 |
| floor7 | 55 | 54 | 主 **(-38,55,274)** · 次 (-42,55,274) | 同上 |
| floor8 | 50 | 49 | 主 **(-42,50,266)** · 次 (-38,50,266) | 同上 |
| floor9 | 45 | 44 | 主 **(-38,45,274)** · 次 (-42,45,274) | 加深 |
| floor10 | 38 | 37 | 主 **(-42,38,266)** · 次 (-38,38,266) | **开阔看守区** 下界砖/黑曜石 |
| floor11 | 33 | 32 | 主 **(-38,33,274)** · 次 (-42,33,274) | 开阔 |
| floor12 | 28 | 27 | 主 **(-42,28,266)** · 次 (-38,28,266) | 井底实心垫 + 看守牌 |

- 奇数层平台偏 **+Z**，偶数层偏 **-Z**（螺旋可读）。  
- 西壁梯子柱 `x=-49` 贯通（应急攀爬/视觉连续）。  
- 层间中空可下望；Y 步长约 **5**（F9→F10 起略加大）。

---

## 3. option / monster 坐标变更摘要

| 文件 | 变更 |
|------|------|
| `dungeon/EmberAbyss/option.yml` | `$setspawn` / `$teleport`：**(-40,65,270) → (-40,90,274)** |
| `dungeon/EmberAbyss/monster.yml` | 各 floor 刷点落到上表主/次坐标；**每层 `start:` 首条** `$teleport{location=主刷点} @player`（层脚本 progress/播报/heal/COMPLETE 保留） |
| TrMenu `ember_abyss` / hub 深渊入口 | **未改** |

**未改：** MM 血伤/掉落、精英图、周本、日、灾厄、hub/afk、票价。

---

## 4. 撤离锚

| 位置 | 文案（告示） |
|------|----------------|
| 井顶 (-40,90,276) | 「上浮撤离 / 打开深渊菜单 / → 上浮撤离 / 按最高层结算」 |
| 井顶 (-37,90,275) | 「余烬深渊 / 下行竖井 / 清层后下潜 / 可到第12层」 |
| 约 F5 侧壁 | 「上浮撤离 / 打开深渊菜单 / → 上浮撤离 / 票不退」 |
| 井底 F12 附近 | 「看守深域 / 第10～12层 / 上浮撤离 / 打开深渊菜单」 |

**不写「请打指令」。** 玩家撤离仍走菜单「上浮撤离」（既有通）。

---

## 5. 自检与 ops

| 检查 | 结果 |
|------|------|
| 备份整份 map | ✅ `ember_abyss-p2-20260927-205353` |
| `/corerpg abyssbuild` 导出 | ✅ blocks≈37151 → `map/ember_abyss/region` |
| 离线 anvil：F1～12 刷点脚下实心、脚/头空 | ✅ 全 `openOK` |
| 进本 smoke（`AbP2b_4480` `/dp start EmberAbyss`） | ✅ 落地 **(-40, 90, 274)** 脚下 `quartz_block` |
| 层传送垂直可读 | ✅ 约 6s 后 **Y≈85.4**（落差 ≈4.6）；脚下 `stonebrick`；未窒息 |
| 井顶撤离牌可见 | ✅ 「上浮撤离 / 打开深渊菜单…」 |
| TrMenu 深渊入口 | ✅ 未改 |
| `ops.json` | ✅ **`[]`**（测中临时 OP RpgBot，已 `/deop` + 空档重启） |

痕迹：`/tmp/abyss-p2-build-result.json` · `/tmp/abyss-p2-smoke2-result.json`（箱内）。

---

## 6. STATUS 路径

`docs/STATUS-ember-abyss-maps-p2.md`

---

## 7. 风险 / 未做

1. **实例缓存** — DP 启动时预缓存 map；大改后若见旧地形，重启 play 或清 `dungeon-caches` 后再进。本轮重启后进本已是新竖井。  
2. **floor1 落地 XZ** — smoke 落在约 (-45.7,85.4,274.6)（目标主点 -38,85,274 同台）；开阔环台可站，若要钉死格可再收紧 teleport/刷点。  
3. **摔落** — 中空下望；层间主要靠清层 `start` teleport，梯子为辅；未另加缓降。  
4. **告示朝向** — `SIGN_POST` 默认朝向，远处可读性一般。  
5. **工作目录** — `server-runtime/ember_abyss_build/` 可能残留，可手工删；不影响 DP 模板。  
6. **未做** 精英独立图、周本/日/灾厄美化、hub/afk 再施工、commit/push、MM 数值。  
7. **复跑** — `/corerpg abyssbuild`（需 admin）；会重建临时世界并覆盖 map region。

---

## 8. 变更文件清单

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../AbyssShaftService.java` | **新建** 竖井建造 + 导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `abyssbuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.4** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_abyss/region/*.mca` | 竖井地形 |
| `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` | 井顶 spawn |
| `plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml` | 层坐标 + start teleport |
| `docs/STATUS-ember-abyss-maps-p2.md` | 本文件 |
