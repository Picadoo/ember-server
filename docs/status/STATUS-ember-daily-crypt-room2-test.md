# STATUS · 地窖房2链式重叠 · §10 硬条短抽

**日期：** 2026-09-28 08:44 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-crypt-room2.md` §10 · `docs/status/STATUS-ember-daily-crypt-room2.md` · commit `dd99538`  
**测前：** play FIFO `dp reload` → `[08:42:35] [DungeonPlus] 插件重载完毕` / `[EmberDailyCrypt] 地牢内容初始化完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（六条硬条全过 · ops=`[]`）

---

## 一句话

地窖 `daily_crypt` 房2链式重叠 live 实锤：高台誓印骷后 **Δ=3009ms** 地面窖卫尸在场（骷仍在）；**door2 仅 wave2b `$kill` 后开**（wave2a.end「高台箭侧已清」时门仍 iron×9）；顺序 **skel→zombie**（禁先尸后骷）；合计 11+Boss、无加血、无 Boss prep；非地窖六线 YAML 零 diff；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 六条硬条

| # | 硬条 | 结果 | 要点证据 |
|---|------|------|----------|
| 1 | 骨架 ≥2真门→Boss；玩家零指令 | **PASS** | door1 z=18@y72 **air×9**；door2 z=40@y66 **air×9**；Boss wither_skeleton@圆厅 →「【圆厅】残誓守墓」→通关；进本仅 `/corerpg enter daily_crypt` |
| 2 | 房2重叠 ≈2s；door2 仅 wave2b | **PASS** | tSkel=240ms → tZombie=3249ms · **Δ=3009ms**；尸刷出时 sk≥1；重叠时 door2 **iron×9**；wave2a 清完仍 iron×9 +「高台箭侧已清」**无**底层开门文案；wave2b 清完 **air×9** +「中层已清 · 底层门开了」 |
| 3 | 禁止先尸后骷 | **PASS** | live 顺序 **skel→zombie**；YAML wave2a=`EmberDailyCryptSkeleton` / wave2b=`EmberDailyCryptZombie` |
| 4 | `$kill` 纪律 | **PASS** | YAML 无 `$kill-any`；wave2a.end 无 door/boss；开门挂 wave2b；live：先清骷门不开、再清尸才开 |
| 5 | 数值边界 | **PASS** | MM Health 尸48/骷38/Boss210 未改（commit 未碰 MythicMobs）；小怪 **5+3+3=11** + Boss×1；无 `boss_prep`；prepSeen=null；wave1 body 相对 `dd99538^` **零非预期 diff** |
| 6 | 范围 | **PASS** | commit 仅 `EmberDailyCrypt/monster.yml` + STATUS；Ash/Frost/Rail/Daily/Tide/Spire `git diff HEAD` 空 |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `dd99538` feat(dp): 地窖房2链式重叠（方案A） |
| 玩家 / OP | `Cr2P548` / 临时 `Cr2Op91` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push；HEAD 仍 `dd99538` |
| 证据 JSON | `/tmp/crypt-room2-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Cr2Op91） |

### 静态 YAML（测前）

- `wave2a.start` → `$monstergroup{wave2b;delay=2}` ✅  
- `wave2a.end` 仅「高台箭侧已清」+ heal；**无** operation-block / boss ✅  
- `wave2b.end` door2 AIR×9 @ z=40 y66 + boss delay=3 ✅  
- wave1 `$kill`×5 / 门 z=18@y72 / 刷点 **零非预期** ✅  
- 无 kill-any / 无 boss_prep ✅  
- 非地窖六线 wt diff 空；commit 仅 Crypt monster.yml + STATUS ✅  

### Live 关键时间线

1. **进本** `(0,72,0)` ·「体力 -30」·「【上层厅】清尽窖卫尸」  
2. **wave1：** 窖卫×5 · door1 前 iron×9 → 清完 **air×9** +「上层已清 · 下阶门开了」  
3. **重叠：** 骷×3 @≈240ms → 尸×3 @≈3249ms（Δ≈3.0s，delay=2 + 实体同步窗）· 重叠时 door2 iron×9 · 顺序 **skel→zombie**  
4. **wave2a only：**「高台箭侧已清」· door2 仍 iron×9 · 地面尸仍×3  
5. **wave2b：**「中层已清 · 底层门开了」· door2 air×9  
6. **Boss：** prepSeen=null · bossSeen≈200ms wither_skeleton@圆厅 ·「【圆厅】残誓守墓」→「通关！奖励发放中…」

### 关键文案（节选）

```
【残誓】正在进入……（体力 -30）
余烬窟·残誓地窖 开始！已消耗体力 · 井口安全 · 清上层开门
【上层厅】清尽窖卫尸再开下阶门
上层已清 · 下阶门开了
【中层·高台】誓印骷对射 —— 窖卫将交错从地面压上
【中层·地面】窖卫尸重叠压上 —— 清完开底层门
高台箭侧已清
中层已清 · 底层门开了
【圆厅】残誓守墓！外廊风筝再输出
余烬窟·残誓地窖 通关！奖励发放中…
```

### 门材质采样

| 门 | 时机 | 材质 |
|----|------|------|
| door1 z=18@y72 | wave1 前 | iron_bars×9 |
| door1 z=18@y72 | wave1 `$kill` 后 | air×9 |
| door2 z=40@y66 | 重叠中 / wave2a 清完 | iron_bars×9 |
| door2 z=40@y66 | wave2b `$kill` 后 | air×9 |

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前 08:42:35 reload） |
| 测后 ops=[] | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `dd99538` |

---

## Blocker

无。

## ops

`[]`
