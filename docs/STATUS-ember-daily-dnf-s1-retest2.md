# STATUS · S1 门2→Boss 复测2（近战化后）

**日期：** 2026-09-28 03:53（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-dnf-s1.md`（方案 A · 骷髅近战木剑）；前序 `STATUS-ember-daily-dnf-s1-retest.md` FAIL  
**CoreRpg：** **1.15.14**（本环境 jar/`plugin.yml` + play 日志 `CoreRpg 1.15.14 enabled` @ 03:51:36 CST；派工标注 1.15.13，复测期间他岗已 bump，**本岗未改 jar**）  
**数值 / YAML / jar：** **未改**（只复测；MM 近战配置沿用插件岗落地）  
**真击杀：** **是**（全程禁止 `mm mobs killall` / 控制台清怪）  
**Verdict：** **✅ PASS**

---

## 一句话

近战化后独立复测单次跑通：真击杀 wave1 → wave2a（「回廊前段已清 · 骷髅压上」+ wave2b 启）→ wave2b 近战骷髅（「回廊已清 · Boss 门开了」）→ 门2 z=25 **AIR×12** → 终厅蛮兵真击杀至 **「余烬窟·庭院 通关！」**。三项必验全绿。

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| play | 本轮为 OP 热写无效而短重启（`stop.sh` + `server-runtime/start.sh custom`，**未**删 dungeon-caches） |
| 临时 OP | `S1r3Op55`；测后 `deop` + `ops.json=[]` |
| 账号 | OP `S1r3Op55` / 玩家 `S1r3Ac5525` |
| JSON | `/tmp/s1-retest2.json` |
| 日志 | `/tmp/s1-retest2.log` |

---

## 三项必验对照

| # | 必验 | 结果 | 证据 |
|---|------|------|------|
| 1 | 真击杀 **wave2a** →「回廊前段已清 · 骷髅压上」+ wave2b 启「【回廊·后段】骷髅×3」 | ✅ | hits=12 · ≈7.6s；播报双实锤（`S1r3Ac5525`） |
| 2 | 真击杀 **wave2b**（近战木剑骷髅）→「回廊已清 · Boss 门开了」+ 门2 z=25 **iron→AIR** | ✅ | hits=9 · ≈4.4s；门2 `iron=0 air=12`（十二格全 AIR） |
| 3 | 进终厅击杀 Boss 蛮兵 / 通关播报 | ✅ | 垫播报「【终厅·中央垫】Boss 蛮兵！…」+ 真击杀通关「**余烬窟·庭院 通关！奖励发放中…**」（hits=18 · ≈9.8s；pos≈(2.2,66,33)） |

---

## 旁证

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any{` · wave2a/2b 单 `$kill` | ✅ | `DungeonPlus/dungeon/EmberDaily/monster.yml` |
| MM 骷髅 Equipment **WOOD_SWORD**（非 BOW）· Display「灰烬庭院骷髅」 | ✅ | `MythicMobs/Mobs/EmberDaily.yml`（`PreventRandomEquipment: true`） |
| CoreRpg | ✅ **1.15.14** | jar + 日志（非本岗 bump；相对派工 1.15.13 为环境已变） |
| 真击杀 wave1 →「前厅已清 · 门开了」 | ✅ | hits=21 · ≈11.9s |
| 未用 `mm mobs killall` | ✅ | 脚本仅真挥击 + 限流 `/tp` 贴脸 |
| 无「was shot by」远程击杀 | ✅ | `deathNotes=[]` · `no_shot_death` |
| `ops.json` 终态 | ✅ | play + login = `[]` |
| 体力文案 | ✅ 未改 | 「体力 -30」可见 |

---

## 最佳连续跑（S1r3Op55 / S1r3Ac5525）

- 进本：`/corerpg enter daily` · spawn `(0,65,0)` · 体力 -30  
- wave1：真击杀 →「前厅已清 · 门开了」  
- wave2a start：「【回廊】混编来袭 —— 先清远程再推 Boss 门」  
- wave2a clear：「**回廊前段已清 · 骷髅压上**」  
- wave2b start：「**【回廊·后段】骷髅×3 —— 清完开 Boss 门**」  
- wave2b clear：「**回廊已清 · Boss 门开了**」  
- 门2 z=25：抽样 x=-2..1 y=65..67 → **AIR×12 / iron=0**  
- Boss：终厅播报 + 真击杀 →「**余烬窟·庭院 通关！奖励发放中…**」  

策略：preferName 区分 zombie/skeleton；贴脸追实体优先，无实体再巡逻 TP；幽灵实体黑名单；OP `/tp` 间隔 ≥1.2s（避 spam kick）。

---

## 过程备注（非失败）

1. 复测前期他岗 `RpgBot` 占 play 改 S2 地图并覆写 `ops.json`，曾导致中途 `Server closed`、wave2a 空转；待其结束后本岗独占短重启再跑通。  
2. CoreRpg 在复测窗口内由 **1.15.13→1.15.14**（jar mtime 03:42），本岗只记录、未改数值/YAML/MM。  
3. 回廊 start 文案仍写「先清远程」（flavor 债，骷髅已近战）。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**  
- `login-runtime/ops.json` = **`[]`**

---

## 债

1. 回廊 start 文案「先清远程」与近战现状不符，可顺手改 flavor。  
2. CoreRpg 版本相对派工说明已为 **1.15.14**，总控/文档对齐即可。  
3. 多岗并行占 play / 覆写 ops 易打断自动化；建议独占窗或 ops 合并写入。

---

## 回报主代理 / 总控

- **总评：** **PASS**（三项必验全绿 · 含通关播报）  
- **① wave2a 清 + w2b 启：** PASS  
- **② wave2b 清 + 门2 AIR×12：** PASS  
- **③ Boss 通关：** PASS（「余烬窟·庭院 通关！」）  
- **CoreRpg：** **1.15.14** · **ops：** `[]` · **未用 killall** · **木剑近战旁证 OK**  
- **报告：** `docs/STATUS-ember-daily-dnf-s1-retest2.md`  
- **JSON：** `/tmp/s1-retest2.json`
