# STATUS · S2 DP 波次/开门 + 菜单去灰

**日期：** 2026-09-28 03:57（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · S2 DP 波次/开门 + 菜单去灰】；坐标 `docs/status/STATUS-ember-daily-s2.md`；怪 `docs/status/STATUS-ember-daily-s2-mobs.md`；庭院范式 `EmberDaily`  
**CoreRpg 版本：** **1.15.15**（bump：`daily_ash` / `daily_crypt` 入口）  
**Verdict：** **✅ 两本 DP 分房 $kill + 门 AIR + TrMenu B/C 去灰可进；ash 真击杀开门闭环；crypt 进本+门前抽样**

---

## 一句话

新建 `EmberDailyAsh` / `EmberDailyCrypt` 完整 option+monster（分房 `$kill`、禁 kill-any、门 AIR、Boss、`daily_clear` 回枢纽）。TrMenu B/C 去「筹备中」，`corerpg enter daily_ash|daily_crypt` 各扣 30（同池）。ash 真击杀跑通门1/门2 AIR；crypt 孤立进本井口+铁栅门前 OK。未改体力上限/刷新；未 commit/push。

---

## 1. 波次设计摘要

### 线 B · EmberDailyAsh（map `ember_daily_ash` · spawn 0,65,0）

| 组 | 刷怪 | `$kill` | 开门 |
|----|------|---------|------|
| wave1 | EmberAshZombie×5 @ (-3,65,8)(3,65,8)(0,65,13) | 焦骨尸×5 | 门1 z=16 x=-1..1 y=65..67 → AIR×9 |
| wave2a | EmberAshZombie×3 @ (-2,65,24)(0,65,22) | 焦骨尸×3 | — → wave2b |
| wave2b | EmberAshSkeleton×3 @ (1,65,28)(-1,65,32) | 燃矢骷×3 | 门2 z=36 → AIR×9 → boss |
| boss | EmberAshBrute×1 @ (0,65,44) | 焦核蛮兵×1 | COMPLETE + daily_clear |

### 线 C · EmberDailyCrypt（map `ember_daily_crypt` · spawn 0,72,0）

| 组 | 刷怪 | `$kill` | 开门 |
|----|------|---------|------|
| wave1 | EmberDailyCryptZombie×5 上层 | 窖卫尸×5 | 门1 z=18@y72 → AIR×9 |
| wave2a | CryptZombie×3 中层 | 窖卫尸×3 | — → wave2b |
| wave2b | CryptSkeleton×3 中层 | 誓印骷×3 | 门2 z=40@y66 → AIR×9 → boss |
| boss | EmberDailyCryptWarden×1 @ (0,60,48) | 残誓守墓×1 | COMPLETE + daily_clear |

入口：`TicketEntryService.Kind.DAILY_ASH|DAILY_CRYPT` → `dp start-console … EmberDailyAsh|EmberDailyCrypt`；门槛仍 `gate=daily`（Lv.10）；体力 key `daily_ash`/`daily_crypt` = **30**（与 `daily` 同池，**未改**上限/刷新）。

---

## 2. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyAsh/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/dungeon/EmberDailyCrypt/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/config.yml` | 预缓存 ash/crypt ×1 |
| `plugins/TrMenu/menus/ember_daily.yml` | B/C 去筹备中 → enter daily_ash/crypt |
| `CoreRpg/.../TicketEntryService.java` | Kind DAILY_ASH / DAILY_CRYPT |
| `CoreRpg/.../StaminaService.java` | costs daily_ash/crypt=30 + fallback |
| `plugins/CoreRpg/cash.yml` · `src/.../cash.yml` | costs 同步 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.14 → 1.15.15** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `docs/status/STATUS-ember-daily-s2-dp.md` | 本文件 |
| 备份 | `server-runtime/config-backups/ember-daily-s2-dp-20260928-035218/` |

**未改：** 体力上限/刷新规则 / MM 怪 / 地图地形 / Stamina 数值以外的玩法。  
**未 commit/push。**

---

## 3. 验证证据

账号：`S2DpOp88` / `S2Ac7546`（ash）· `S2Cr5455`（crypt 孤立）  
日志：`/tmp/s2-dp-verify.log` · `/tmp/s2-crypt-probe.json` · JSON `/tmp/s2-dp-verify.json`

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any` · Display `$kill` 对齐 | ✅ | ash/crypt monster.yml |
| TrMenu 无「筹备中」· enter daily_ash/crypt | ✅ | ember_daily.yml |
| ash 进本 · 体力 -30 | ✅ | 「[焦骨] 正在进入……（体力 -30）」· spawn (0,65,0) |
| ash 真击杀 wave1 → 门1 AIR×9 | ✅ | 「前段已清 · 门开了」· iron=0 air=9 @z=16 |
| ash wave2a → wave2b | ✅ | 「弯折前段已清 · 燃矢骷压上」 |
| ash 真击杀 wave2b → 门2 AIR×9 | ✅ | 「弯折已清 · 鼓室门开了」· air=9 @z=36 |
| ash Boss 鼓室可达 | ✅ | tp (0,65,42) + 播报链 |
| 禁 mm killall 推进 | ✅ | 脚本仅真挥击 |
| crypt 进本 · 体力 -30 · 井口 | ✅ | 孤立探针：「残誓地窖 开始」· pos (0,72,0) · stonebrick |
| crypt 门1 关闭铁栅 | ✅ | (-1/0/1,72,18)=iron_bars |
| 联合跑 crypt 曾 FAIL | ⚠ | leave 后 `start-interval` 冷却；孤立复测 PASS |
| 体力上限/刷新 | ✅ **未改** | 仍 30/同池 |
| `ops.json` | ✅ `[]` | |
| jar bump | ✅ **1.15.15** | |
| commit/push | ✅ **未做** | |

---

## 4. 给总控的结案转发正文

```
【结案 · S2 DP 波次/开门 + 菜单去灰】priority=true
岗：余烬-插件 · CoreRpg 1.15.15
DP：EmberDailyAsh / EmberDailyCrypt 分房 $kill（无 kill-any）· 门 AIR · Boss · daily_clear 回枢纽
菜单：B/C 去「筹备中」· corerpg enter daily_ash|daily_crypt · 各扣 30（同池）
验证：ash 真击杀开门1/2 AIR 闭环；crypt 进本+门前铁栅抽样；ops=[]
未改体力上限/刷新；未 commit/push。可派测。
```
