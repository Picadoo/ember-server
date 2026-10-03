# STATUS · B1.3 精英试炼图/刷点排查（ember_elite）

**日期：** 2026-09-27 23:21–23:25（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** B0 TTK 窗 `docs/status/STATUS-ember-b0-ttk-test.md`（Boss@493s 后战 ~220s 未击杀 · TTK=null · 全本 713s）；对照 4.4f Boss~70s；总控：**先查图/刷点可达，不先削血**  
**Verdict：** **图有问题已修**（厅三东门通向 scrub 虚空崖）

---

## 1. 结论

| 项 | 结果 |
|----|------|
| 结论 | **图有问题已修** |
| 根因 | `EliteCorridorService.buildRoom` 默认开 **东/西** 中门；厅三（R3）**东门 x=29** 外是 P3 scrub 空带（仅 **y=59 石地**），Boss/怪可走出掉崖 → 玩家仍在 y68 平台，近战/bot 打不到或 DPS 崩盘 |
| 对照 | 周本 `ember_weekly` Boss 区地板连续（无同款东崖）；同装 L2 周本 Boss 仍 20s → **非装等/非 HP** |
| 未改 | 票 / 掉落 / Boss HP·伤·Pulse / MM yml / option 奖励脚本 |

---

## 2. 检查项

| # | 检查 | 结果 |
|---|------|------|
| 1 | EmberEliteWeekly `$setmap` / `$setspawn` | `ember_elite` / `-35,70,270`（与 P3 设计一致） |
| 2 | monster.yml 刷点 vs 建造常量 | 厅一 `-30,70,270` / `-28,70,272`；厅二 `-4,72,270` / `-6,72,268`；厅三 TP `22,68,270`；Boss `24,68,270` — **与 `EliteCorridorService` 对齐** |
| 3 | 脚下实心 / 头脚空（MCA） | spawn/w1/w2/w3/boss 均为 **gold(41) 脚下 + air 站立** |
| 4 | 玩家→Boss 近战可达（厅内） | BFS `(22,68,270)`→`(24,68,270)` **可达**；间距 2 格同高 |
| 5 | **厅三东门外** | 修前：`(30,60,270)` 可站在 y59 石地上（悬崖）；东门 `(29,68,269..271)` 为空气 |
| 6 | 脚高灯柱 | `placeLights` 曾在 **feetY** 放海晶灯/萤石；已埋入地板（feetY-1），站立层改 air |
| 7 | 周本对照 | Boss `(-40,63,342)` 黑曜石垫、邻域下界砖连续，无 scrub 虚空崖 |
| 8 | GuildBoss | 走 `ember_raid`，**与本 FAIL 无关**（本轮未改） |
| 9 | dungeon-cache | 已删 `dungeon_EmberEliteWeekly_*`，下次进本从模板重拷 |

---

## 3. 坐标对照表

| 角色 | 配置坐标 | MCA 脚下 / 站立 | 备注 |
|------|----------|-----------------|------|
| spawn | `-35,70,270` | gold / air | option `$setspawn` + 进本 TP |
| wave1 主/次 | `-30,70,270` · `-28,70,272` | gold / air | |
| wave1→2 TP | `-4,72,270` | gold / air | |
| wave2 主/次 | `-4,72,270` · `-6,72,268` | gold / air | |
| wave2→3 TP | `22,68,270` | gold / air | 厅三中心 |
| Boss | `24,68,270` | gold / air | 与玩家 TP 同高，Δx=2 |
| **东门（修前）** | `29,68,269..271` | **air 门洞** → 外崖 | **BUG** |
| **东门（修后）** | `29,68..71,269..271` | **quartz/gold 实墙** | 已封 |
| 东护台（修后） | `30..33,68,267..273` | gold/quartz 地板 | 防漏出软着陆 |

周本对照：Boss `(-40,63,342)` · 深室 `(-40,63,340)` · spawn `(-40,65,270)`。

---

## 4. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/map/ember_elite/region/r.0.0.mca` | 封厅三东门 + 东护台 + 脚灯改埋地 |
| `plugins/DungeonPlus/map/ember_elite/region/r.-1.0.mca` | R1/spawn 脚灯埋地 |
| `CoreRpg/.../EliteCorridorService.java` | 重建时：`sealRoomEastDoor` + `eastSafetyApron`；灯改 feetY-1 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.10** |
| `plugins/CoreRpg.jar` | 重编换入（**需重启 play 才加载新类**；地图模板已立即生效） |
| `plugins/DungeonPlus/dungeon-caches/dungeon_EmberEliteWeekly_*` | **已删除** |

**备份：** `server-runtime/config-backups/ember_elite-b13-mapfix-20260927-232333/`  
（含 map / EmberEliteWeekly dungeon / 改前 Java）

**未改：** `EmberEliteWeekly/monster.yml` · `option.yml` · MythicMobs Elite HP/伤/掉落 · 票。

---

## 5. 验收建议

1. **清缓存确认：** `plugins/DungeonPlus/dungeon-caches/` 无 `EmberEliteWeekly` 目录（进本会重建）。  
2. **进本抽检（OP creative 即可）：**
   ```
   /dp start EmberEliteWeekly
   /tp <p> -35 70 270   # spawn 脚下金砖
   /tp <p> 22 68 270    # 厅三
   /tp <p> 24 68 270    # Boss 垫
   /tp <p> 29 68 270    # 应顶在实墙（不能穿出）
   /tp <p> 31 68 270    # 护台可站（门已封，厅内走不到；仅确认护台存在）
   ```
3. **复测 B1.3：** 同装同脚本 `ONLY=elite`；期望 Boss TTK 回到 **~45–75s** 量级（对照 4.4f ~70s），全本可通关。  
4. **可选：** 重启 play 加载 CoreRpg **1.15.10**，避免日后 `/corerpg elitebuild` 用旧逻辑重开东门。

---

## 6. 证据链（为何是图不是血）

- 4.4f（P3 **前**，精英仍挂 `ember_weekly`）Boss TTK **70s** · HP **5200**  
- P3 后独立 `ember_elite`；B1.3 同 HP 5200、周本同装 Boss 仍 20s，精英 Boss 战 **220s 未击杀**  
- 日志：`was slain by 余烬试炼·烬纹执行官`（曾进过近战）→ 随后长时间无击杀，符合 **Boss 掉出平台/高差失联**  
- MCA：东门外无 y67 地板，直通 y59 scrub 石地  

