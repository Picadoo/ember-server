# STATUS · 深渊 F2 卡死排查（B0.2 债）

**日期：** 2026-09-27 23:51–00:00（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** B0.2 `abyss_note: SKIP/DEBT — live F2 卡死`（`/tmp/ttk-b0-b1-result.json`）；总控补充：同日更早 `/tmp/abyss-ttk-43c*` 已完整走过 F1～F12  
**Verdict：** **图/环台路径问题已修**（非测脚本假卡；非怪血）

---

## 1. 结论（假卡 vs 真卡）

| 假说 | 判定 | 证据 |
|------|------|------|
| **测脚本假卡**（B0 `runAbyss` vs 43c） | **否（主因）** | ① 43c/43b `floors_seen` 含 2，且跑在 **P2 竖井之前**（~17:17–17:46）；P2 地形 **20:53** 才建成。② B0 fighter 对 F2 仍打 `zombie/skeleton`（`preferBoss=wither_skeleton` 仅在看到看守时收窄；F2 无看守）。③ F2 MM 类型 Mix=ZOMBIE、Skeleton=SKELETON，均在 `MOBS` 集合内。 |
| **地图/环台真卡**（P2 后） | **是** | 见 §2；已做最小封口/外移刷点。 |
| DP progress / kill 条件 YAML 坏 | **否** | `monster.yml` F2 `$kill-any{混潮,骨潮;amount=4}` 与刷怪数一致；F1 同脚本 `$kill` 在 live 窗已能 `本局层 0→1`。 |
| Boss HP/DPS | **未改** | 本票禁止；MM mtime 不碰。 |

**一句话：** 43c 通关证明的是 **旧实心房**；B0 债记的是 **P2 环台竖井** 上 F2 推进失败。不是「脚本读不到第 2 层横幅」类假卡。

---

## 2. Live 证据（B0 窗服务器日志）

来源：`server-runtime/logs/2026-09-27-98.log.gz`

| 时间 (CST) | 账号 | 事件 |
|------------|------|------|
| 22:42:15 | TtkR9102 | EmberAbyss 创建 → `本局层 0→0` → **`moved wrongly!`** → 6s 后断线（跌落/拉回典型） |
| 22:45:39 | TtkR9205 | 进深渊后 ~66s 断线（短试） |
| **22:50:13** | **TtkR9311** | 进深渊 |
| **22:50:27** | **TtkR9311** | Instant Health + **`本局层 0 → 1`**（**F1 已清**） |
| 22:50:32 | TtkR9311 | 再一次 `/corerpg skill` |
| **22:50:32→22:59:22** | **TtkR9311** | **~9 分钟无再 progress / 无再清层奶** → 断线 |

最终正式跑 `TtkR9420`（22:59起）**未再进深渊**（周本→团→精英）；`result.json` 的 `abyss_note` / debts 为测岗据上表手记债。STATUS「hits 停在 11」与 bot 侧计数一致（服日志不打 hits）。

---

## 3. 根因（几何）

P2 `AbyssShaftService.buildFloorPlatform`：

- F1～9：**中空看井** `rHole=3` + 弱侧挖空（螺旋可读）+ 仅在「脚下已有实心」处放铁栅栏。  
- 偶层主刷点 **(-42, Y, 266)** 距井心 (-40,270) 仅 **≈4.47**（紧贴洞唇）。  
- F2：`EmberAbyssMix×2` + `EmberAbyssSkeleton×2`，`scattered=1.0` → AI/击退/pathfinder 易坠井。  
- DP `$kill-any` **不计摔死** → 怪没了但进度不满 → **hits 停转、层永不通过**（与 live 吻合）。

对照：旧图注释「开阔房 x -43..-37, z 267..273」无中空；故 43c 可 F1→F12。

---

## 4. 最小修复（已做）

**备份：** `server-runtime/config-backups/ember_abyss-f2stuck-20260927-235455/`  
（map region + level.dat · EmberAbyss monster/option · 改前 `AbyssShaftService.java`）

| 项 | 改前 | 改后 |
|----|------|------|
| 层刷点 / teleport | 奇 z=**274** / 偶 z=**266** | 奇 z=**276** / 偶 z=**264**（`CZ±6`） |
| F1～9 `rHole` | 3 | **2** |
| 弱侧挖空半径 | d²&lt;25 | d²&lt;**9** |
| 刷点实心垫 | 3×3 | **5×5** |
| 洞唇铁栅 | 仅脚下已有实心 | **补石砖领圈 + 全圈铁栅** |
| F2 `scattered` | 1.0 | **0.5**（仅 F2；不改血伤） |
| CoreRpg | 1.15.10 | **1.15.11** |
| 地形 | P2 旧环台 | `/corerpg abyssbuild` 重导出 `map/ember_abyss`（blocks≈19990） |

**离线 anvil 抽检（修后）：**

| 点 | 脚下 / 站立 | okStand |
|----|-------------|---------|
| 井顶 (-40,90,274) | quartz / air | ✅ |
| F1 (-38,85,276) | stone_bricks / air | ✅ |
| **F2 (-42,80,264)** | stone_bricks / air | ✅ |
| F2 洞唇铁栅 | iron_bars ×**20** | ✅ |
| F2 5×5 垫实心 | **25**/25 | ✅ |
| F10/F12 | nether_bricks | ✅ |

`option.yml` 井顶 spawn **未改** `(-40,90,274)`。  
**未改：** MM HP/伤/掉落、票、TrMenu、精英/周本/日/灾厄。

缓存：已删 `dungeon-caches/dungeon_EmberAbyss_*`。  
ops：`server-runtime/ops.json` / `login-runtime/ops.json` = **`[]`**（测中临时 RpgBot，已 deop）。

---

## 5. 变更文件

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../AbyssShaftService.java` | 外移刷点、缩洞、5×5 垫、全圈轨 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.11** |
| `plugins/CoreRpg.jar` | 重编换入（play 已重启加载） |
| `plugins/DungeonPlus/map/ember_abyss/region/*.mca` | abyssbuild 导出 |
| `plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml` | 层坐标 + F2 scattered |
| `docs/STATUS-ember-abyss-f2-stuck.md` | 本文件 |

---

## 6. 验收 / 下一探针

**可开测：** `ONLY=abyss` **F2→floor3 smoke**（不必全层 TTK）。

建议探针（mineflayer）：

1. 菜单进深渊 → 确认 F1 横幅 → 清潮尸 → 见 `—— 第 2 层 ——` + TP 约 **(-42,80,264)**。  
2. 战斗 60s 内采样：附近 Mix/Skeleton 的 **Y 是否仍 ≥79**（不应坠到 Y≪75）。  
3. 预期：`本局层 1→2` + Instant Health + `—— 第 3 层 ——`。  
4. 若仍卡：记录剩余实体名/坐标/是否摔死；再查 `$kill-any` 显示名。  
5. F2→F3 PASS 后再开 B0.2 深渊 10/12 L1/L2 ΔTTK 债。

**Ready for 测试 ONLY=abyss F2→floor3 smoke：YES**（图已修；需 live 战斗确认进度链）。
