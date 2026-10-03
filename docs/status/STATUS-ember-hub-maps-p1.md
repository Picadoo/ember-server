# STATUS · P1 ember_hub 最小主城（广场 + 工坊/灰烛锚）

**日期：** 2026-09-27 20:33–20:36（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-multiworld-maps.md` §2 hub 行 · §4.2「枢纽像家」  
**Verdict：** **✅ P1 最小可验收完成**（广场高低差/回廊、工坊锚、灰烛可见、回枢纽同点、ops 清空）

---

## 1. 备份路径

`server-runtime/config-backups/ember_hub-p1-20260927-203334/`

| 内容 | 说明 |
|------|------|
| `region/` | 施工前 `r.-1.-1.mca` / `r.-1.0.mca` / `r.0.0.mca` |
| `level.dat` (+ `_old`) | 世界级数据 |
| `configs/` | `worlds.yml` · `quest.yml` · `config.yml` · Adyeshach `ember_guide` json |

---

## 2. 广场 / 工坊 / 灰烛坐标

| 锚点 | 坐标 | 备注 |
|------|------|------|
| **MV /hub 出生** | **(-18.5, 58.0, 110.5)** yaw **180**（朝北） | XZ/Y 未改；仅 yaw 0→180，面向灰烛 |
| 脚下垫 | 石英 3×3 @ y57 中心 (-19,57,110) | 去平板感 |
| **灰烛 NPC** | **(-16.5, 58.0, 106.5)** | **未挪**；海晶灯垫 (-17,57,106)；村民实体自检可见 |
| 工坊（余烬锻炉外观） | 棚屋约 x=-12…-8 · z=103…107 · 门朝西 | 熔炉 (-9,58,105) · 铁砧 (-9,58,104) · 工作台 (-10,58,106) |
| 告示 · 入口 | (-19,58,113) | 「枢纽广场 / 打开 /ember / 看菜单与玩法」 |
| 告示 · 灰烛 | (-19,58,107) | 「引路人·灰烛 / 右键交谈」 |
| 告示 · 锻炉 | (-13,58,105) | 「余烬锻炉 / 打开/ember / →锻炉」 |
| 告示 · 回枢纽语义 | (-16,58,115) | 「回枢纽 / 你已在枢纽 / 打开枢纽菜单」（**不写请打指令**） |

**广场要点（最小）：** 石砖/圆石/砂砾混铺地面 + 南北落差台阶 + 东西矮墙/石柱回廊 + 北向石英小路通向灰烛；禁止空出生平板。

施工命令（可复跑）：`/corerpg hubbuild`（admin；`HubPlazaService`，CoreRpg **1.15.2**）。

---

## 3. MV spawn 是否变更

| 字段 | 旧 | 新 |
|------|----|----|
| x/y/z | -18.5 / 58.0 / 110.5 | **不变** |
| yaw | 0.0（朝南，背对灰烛） | **180.0**（朝北，面向灰烛） |
| pitch | 0.0 | 0.0 |

- 落盘：`plugins/Multiverse-Core/worlds.yml`（施工后 `/mv set spawn` 固化）。  
- CoreRpg `/hub` = console `mvtp <player> ember_hub` → **与菜单「回枢纽」同 MV spawn**。  
- NPC / `quest.yml` npc 坐标 **未改**（talk 半径仍覆盖）。

---

## 4. 菜单 / lore 调整

| 文件 | 变更 |
|------|------|
| `plugins/CoreRpg/quest.yml`（及 resources 同步） | intro 去掉「随时 /hub 回城」→「打开枢纽菜单可返回」；ch1 kill hint 两处 `/hub` → 菜单返回话术；**未大改主线剧情** |
| `plugins/CoreRpg/config.yml` join_message | 「/hub 回城」→「打开枢纽菜单可返回」 |
| TrMenu | **本轮未改**（P0 已去挂机庭 `/hub` 教学；hub 菜单「回枢纽」仍底层 `hub`） |
| 场景牌 | 见上表；**不教打 `/hub`** |

未改：战斗数值、`afk_caps`、票、挂机②～④、深渊/精英地图。

---

## 5. 自检与 ops

| 检查 | 结果 |
|------|------|
| Maven `CoreRpg` 1.15.2 → `plugins/CoreRpg.jar` | ✅ |
| `/corerpg hubbuild` | ✅ blocks≈**2651** |
| `/hub` 落地 | ✅ **(-18.5, 58.0, 110.5)** · 脚下 `quartz_block` |
| 见灰烛 | ✅ 村民 @ (-16.5,58,106.5) + 海晶灯垫 |
| 见工坊 | ✅ anvil / furnace / crafting_table |
| 四块告示牌文案 | ✅ 含 `/ember` / 锻炉 / 回枢纽菜单语义 |
| `ops.json` | ✅ **`[]`**（测后 `/deop`） |

脚本痕迹：`/tmp/hub-p1-build.js` · `/tmp/hub-p1-out.txt` · `/tmp/hub-p1-result.json`（箱内，未入库）。

---

## 6. STATUS 路径

`docs/status/STATUS-ember-hub-maps-p1.md`

---

## 7. 风险 / 未做

1. **最小城而非大城** — 无完整峡谷小镇；仅广场+回廊+工棚，满足 §4.2「广场+至少一处功能锚」。  
2. **告示牌朝向** — `SIGN_POST` 默认朝向，远处可读性一般；需要时可改 WALL_SIGN。  
3. **MV yaw 持久化** — 依赖 `worlds.yml`；异常停服若回写旧档需再 `/mv set spawn`。  
4. **Adyeshach 对矿车/客户端可见性** — 自检用的是协议村民实体；若个别客户端不见 NPC，仍以 json 坐标为准（未挪）。  
5. **未做** 深渊/精英换图、挂机再施工、commit/push。  
6. **`/corerpg hubbuild` 可复跑** — 会清 y58–63 施工盒内空气再铺；勿在玩家堆放区物品时盲跑。

---

## 8. 变更文件清单

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../HubPlazaService.java` | **新建** 广场建造 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `hubbuild`/`hubplaza` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.2** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/CoreRpg/quest.yml` · `config.yml` | lore 去 `/hub` 教学 |
| `CoreRpg/src/main/resources/{quest,config}.yml` | 同步 |
| `plugins/Multiverse-Core/worlds.yml` | hub yaw **180** |
| `server-runtime/ember_hub/region/*.mca` | 广场方块 |
| `docs/status/STATUS-ember-hub-maps-p1.md` | 本文件 |
