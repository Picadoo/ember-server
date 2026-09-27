# STATUS · S1 门2→Boss 复测

**日期：** 2026-09-28 03:29（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-dnf-s1.md`（门2 YAML 修复）；派工 · S1 门2→Boss 复测  
**CoreRpg：** **1.15.13**（jar `plugin.yml` + play 日志 `CoreRpg 1.15.13 enabled` @ 03:18:08 CST）  
**数值 / YAML / jar：** **未改**（只复测）  
**真击杀：** **是**（全程禁止 `mm mobs killall` / 控制台清怪）  
**Verdict：** **❌ FAIL**

---

## 一句话

YAML 条件链（wave2a/wave2b · 无 `$kill-any`）与 CoreRpg **1.15.13** 已确认；本轮自动化在 play 短重启后 **真击杀**打通 wave1（「前厅已清 · 门开了」）并见到回廊启动播报，但 **未能**在单次跑通中实锤 wave2a 清场播报 → wave2b → 门2 AIR → Boss。三项必验未全绿 → **FAIL**（卡在回廊真击杀/实体不同步）。

---

## 环境

| 项 | 值 |
|----|-----|
| 路径 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| play | 本轮曾短重启（`stop.sh` + `./start.sh custom`，**未**删 dungeon-caches） |
| 临时 OP | `S1r2Op*`；测后 `deop` + `ops.json=[]` |
| JSON | `/tmp/s1-retest.json` |

---

## 三项必验对照

| # | 必验 | 结果 | 证据 |
|---|------|------|------|
| 1 | 真击杀清 **wave2a** →「回廊前段已清 · 骷髅压上」+ wave2b 启动「【回廊·后段】骷髅×3」 | ❌ **未实锤** | 最佳连续跑（`S1r2Op63` / `S1r2Ac9484`）仅见「【回廊】混编来袭……」；**无**「回廊前段已清」。插件岗先前 STATUS 曾采到该播报，本复测未复现闭环 |
| 2 | 真击杀清 **wave2b** →「回廊已清 · Boss 门开了」+ 门2 z=25 铁门×12→AIR | ❌ **未达** | 未进入 wave2b；门2 抽样仍为 `iron_bars` **12/12**（未开门状态） |
| 3 | 进终厅打 Boss 蛮兵至击杀（通关/Boss 播报） | ❌ **未达** | 门2 未开，Boss 垫未验 |

---

## 旁证（本轮仍成立）

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any{` · 有 wave2a/wave2b | ✅ | `server-runtime/plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` |
| wave2a `$kill` 僵尸×3 / wave2b `$kill` 骷髅×3 · 门2 十二格 AIR | ✅ 设计落地 | 同文件 end 段 |
| CoreRpg **1.15.13** | ✅ | jar + 重启后日志 |
| 真击杀 wave1 →「前厅已清 · 门开了」 | ✅（本轮最佳跑） | `S1r2Ac9484` · hits≈29 · ≈18s；随后 TP 回廊 z≈17 |
| 未用 killall | ✅ | 脚本无 `mm mobs killall`；仅 `/tp` 贴近 |
| `ops.json` 终态 | ✅ | play+login = `[]` |

---

## 最佳连续跑（S1r2Op63 / S1r2Ac9484，play 重启后）

- 进本：`/corerpg enter daily` · 落地门廊 · 体力播报可见  
- wave1：**真击杀** →「前厅已清 · 门开了」（≈29 hits / ≈18s）  
- wave2a start：「【回廊】混编来袭 —— 先清远程再推 Boss 门」  
- wave2a clear：**未达成**（长时间贴脸挥击，客户端实体数卡住 / 疑似幽灵实体，`$kill` 未记满 3）  
- 门2 / Boss：未触及  

另：插件岗既有证据（`/tmp/s1-gate2-fasttp-verify.json`，`FtAc8953`）曾打出「回廊前段已清 · 骷髅压上」+「【回廊·后段】骷髅×3」，但同样卡在骷髅段；**不能**替代本岗本轮对三项的实锤。

---

## 失败归因（测试侧）

1. **mineflayer 回廊实体不稳**：可见僵尸坐标漂移到门廊 z≈-2；部分目标 TP 贴脸后距离仍 >10（客户端幽灵），空挥不计 DP `$kill`。  
2. **骷髅远程**（历史）：即使进入 wave2b，贴脸/追击仍不稳定（插件岗同结论）。  
3. `/ni give gear_ember_t2_blade` 本环境静默无效（背包仍仅新手「余烬之刃」）；wave1 仍可打通，说明非唯一阻断。  
4. OP `/tp`+`/effect` 过密会 `Kicked for spamming`，需限流。  

**非**数值/YAML 回退：monster.yml 仍为 wave2a/wave2b；CoreRpg 仍 1.15.13。

---

## ops 终态

- `server-runtime/ops.json` = **`[]`**  
- `login-runtime/ops.json` = **`[]`**

---

## 债

1. 回廊真击杀自动化（wave2a 收尾 + wave2b 骷髅贴脸）需更稳的实体跟踪/黑名单或人工抽检。  
2. NeigeItems `/ni give` T2 刃本环境无效，待环境岗确认。  
3. 门2 AIR + Boss 通关播报仍待单次跑通实锤。

---

## 回报主代理 / 总控

- **总评：** **FAIL**（三项必验未全绿）  
- **① wave2a 清 + w2b 启：** FAIL（仅见回廊 start；无「回廊前段已清」）  
- **② wave2b 清 + 门2 AIR：** FAIL（未达）  
- **③ Boss 击杀：** FAIL（未达）  
- **CoreRpg：** **1.15.13** · **ops：** `[]` · **未用 killall**  
- **报告：** `docs/STATUS-ember-daily-dnf-s1-retest.md`  
- **JSON：** `/tmp/s1-retest.json`
