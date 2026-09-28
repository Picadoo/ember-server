# STATUS · 焦骨房2链式重叠 · §10 硬条短抽

**日期：** 2026-09-28 08:36 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-daily-ash-room2.md` §10 · `docs/STATUS-ember-daily-ash-room2.md` · commit `2ad874e`  
**测前：** play FIFO `dp reload` → `[08:31:54] [DungeonPlus] 插件重载完毕` / `[EmberDailyAsh] 地牢内容初始化完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（六条硬条全过 · ops=`[]`）

---

## 一句话

焦骨 `daily_ash` 房2链式重叠 live 实锤：弯折尸后 **Δ=3007ms** 内燃矢在场（尸仍在）；**door2 仅 wave2b `$kill` 后开**（wave2a.end「弯折尸侧已清」时门仍 iron×9）；房1假岔×2仍计入 door1；合计 12+Boss、无加血、无 Boss prep；非焦骨六线 YAML 零 diff；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 六条硬条

| # | 硬条 | 结果 | 要点证据 |
|---|------|------|----------|
| 1 | 骨架 ≥2真门→Boss；玩家零指令 | **PASS** | door1 z=16 **air×9**；door2 z=36 **air×9**；Boss husk@`(0.7,65,44)` →「【鼓室】焦核蛮兵」→通关；进本仅 `/corerpg enter daily_ash` |
| 2 | 房2重叠 ≈2s；door2 仅 wave2b | **PASS** | tZombie=481ms → tSkel=3488ms · **Δ=3007ms**；燃矢刷出时 zb=3；重叠时 door2 **iron×9**；wave2a 清完仍 iron×9 +「弯折尸侧已清」**无**鼓室开门文案；wave2b 清完 **air×9** +「弯折已清 · 鼓室门开了」 |
| 3 | `$kill` 纪律 | **PASS** | YAML 无 `$kill-any`；wave2a.end 无 door/boss；开门挂 wave2b；live：先清尸门不开、再清燃矢才开 |
| 4 | 假岔不冲 | **PASS** | 刷出主甬×**4** + 假岔×**2**（x≥5.5）；只清主甬后门仍 **iron×9**、假岔仍活×2；清岔后 door1 **air×9** +「前段已清 · 门开了」；wave1 body 相对 `2ad874e^` **零非预期 diff**（仅注释） |
| 5 | 数值边界 | **PASS** | MM Health 尸50/燃矢40/Boss200 未改（commit 未碰 MythicMobs）；小怪 **6+3+3=12** + Boss×1；无 `boss_prep`；文案直出鼓室蛮兵（prepSeen=null） |
| 6 | 范围 | **PASS** | commit 仅 `EmberDailyAsh/monster.yml` + STATUS；Frost/Rail/Daily/Tide/Spire/Crypt `git diff HEAD` 空 |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `2ad874e` feat(dp): 焦骨房2链式重叠（方案A） |
| 玩家 / OP | `Ash2P993` / 临时 `Ash2Op12` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push |
| 证据 JSON | `/tmp/ash-room2-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Ash2Op12） |

### 静态 YAML（测前）

- `wave2a.start` → `$monstergroup{wave2b;delay=2}` ✅  
- `wave2a.end` 仅「弯折尸侧已清」+ heal；**无** operation-block / boss ✅  
- `wave2b.end` door2 AIR×9 @ z=36 + boss delay=3 ✅  
- wave1 `$kill`×6 + 假岔坐标 `(8,65,10)` `(7,65,11)` 未改 ✅  
- 无 kill-any / 无 boss_prep ✅  

### Live 关键时间线

1. **进本** `(0,65,0)` ·「体力 -30」·「主甬+假岔焦骨尸」  
2. **假岔：** 主清后门 iron×9；清岔 → door1 air×9  
3. **重叠：** 尸×3 @≈481ms → 燃矢×3 @≈3488ms（Δ≈3.0s，delay=2 + 实体同步窗）· 重叠时 door2 iron×9  
4. **wave2a only：**「弯折尸侧已清」· door2 仍 iron×9 · 燃矢仍×3  
5. **wave2b：**「弯折已清 · 鼓室门开了」· door2 air×9  
6. **Boss：** prepSeen=null · bossSeen≈200ms husk@鼓室 ·「【鼓室】焦核蛮兵」→「通关！奖励发放中…」

### 关键文案（节选）

```
【甬道·前段】主甬+假岔焦骨尸 —— 假岔也要清才开门
前段已清 · 门开了
【甬道·弯折】焦骨尸来袭 —— 燃矢将交错贴脸压上
【甬道·后段】燃矢骷重叠压上 —— 清完开鼓室门
弯折尸侧已清
弯折已清 · 鼓室门开了
【鼓室】焦核蛮兵！窄场走位再输出
余烬窟·焦骨甬道 通关！奖励发放中…
```

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前已 reload） |
| 测后 ops=[] | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `2ad874e` |

---

## Blocker

无。

## ops

`[]`
