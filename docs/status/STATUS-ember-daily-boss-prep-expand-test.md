# STATUS · Boss前压扩线（断塔/焦骨/地窖）· 短抽

**日期：** 2026-09-28 10:50 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-boss-prep-expand.md` §6 · `docs/status/STATUS-ember-daily-boss-prep-expand.md` · commit `8a69b97`（tip `4868bc1`）  
**测前：** play FIFO `dp reload` → `[10:36:34] [DungeonPlus] 插件重载完毕` / EmberDailySpire · EmberDailyAsh · EmberDailyCrypt 初始化完毕（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（五条验收全过 · 三线均 PASS · ops=`[]`）

---

## 一句话

断塔 `daily_spire` / 焦骨 `daily_ash` / 地窖 `daily_crypt`：door2 开后先 **boss_prep×2**（卫尸贴 / 燃矢木剑近战 / 誓印弓），清完才刷 Boss；房2 start 链式重叠仍在（Δ≈1.7s/2.9s/2.6s），门仍挂 wave2b.end；prep 独立 `$kill`×2、无 kill-any；庭院/潮蚀/霜晶/锈轨 YAML 与 Boss HP/门坐标相对 `8a69b97^` **零误伤**；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 五条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | door2 开后先 prep×2，清完才 Boss（≠开门见 Boss） | **PASS** | 断塔 prepSeen=**200ms**·prep×2·bossSeen=null →「门槛已清 · 守望现身」；焦骨 prepSeen=**201ms**·held=`wooden_sword` →「门槛已清 · 焦核现身」；地窖 prepSeen=**200ms**·prep×2·held=`bow` →「门槛卫已清 · 残誓现身」 |
| 2 | 品种可分：卫尸贴 / 燃矢近战 / 誓印射 | **PASS** | MM：SpireZombie 近战尸 / AshSkeleton **WOOD_SWORD** / CryptSkeleton **BOW**；live 文案「断塔卫尸贴脸 / 燃矢贴脸 / 誓印点射」；焦骨 held=`wooden_sword`、地窖 held=`bow` |
| 3 | 房2链式零回归（重叠仍在；门仍挂 wave2b.end） | **PASS** | 断塔 Δ=**1704ms**·door2 开时 skel 仍×4；焦骨 Δ=**2892ms**·w2a 后 door2 iron·skel×3；地窖 Δ=**2648ms**·order skel→zombie·w2a 后 door2 iron·zb×3；YAML door2 仍在 wave2b.end |
| 4 | prep 独立 `$kill`；无 kill-any | **PASS** | 三线 prep `$kill` 卫尸/燃矢/誓印 ×2 独立组；wave2a/2b 按组 `$kill`；全 YAML 无 `$kill-any` |
| 5 | 庭院/潮蚀/霜晶/锈轨 YAML 零误伤；Boss HP/门坐标零 diff | **PASS** | commit `8a69b97` 仅 Spire/Ash/Crypt `monster.yml`+STATUS；他四线 wt diff 空；MM 未动；Boss HP 215/200/210 仍在；门坐标 operation-block 相对 parent **零 diff** |

**总评：** PASS

---

## 分线表

| 线 | enter | 玩家 | 重叠 Δ | door2 中/后 w2a / 后 w2b | prep | 通关 | 结果 |
|----|-------|------|--------|--------------------------|------|------|------|
| 断塔 EmberDailySpire | `daily_spire` | `Bp2S491`（补测；首跑 `BpS653` 环廊丢怪未开门） | **1704ms** | iron×9 / — / **air×9**（开时 sk×4） | 卫尸×2 @200ms | ✅ | **PASS** |
| 焦骨 EmberDailyAsh | `daily_ash` | `BpA785` | **2892ms** | iron×9 / iron×9 / **air×9** | 燃矢×2 @201ms · wood_sword | ✅ | **PASS** |
| 地窖 EmberDailyCrypt | `daily_crypt` | `BpC801` | **2648ms** skel→zombie | iron×9 / iron×9 / **air×9** | 誓印×2 @200ms · bow | ✅ | **PASS** |

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `8a69b97` feat(daily): 断塔/焦骨/地窖 Boss前压 boss_prep×2 |
| tip | `4868bc1` |
| OP / 玩家 | `BpOp51`（主跑）· 补测 OP `Bp2Op*` / 断塔 `Bp2S491` · 焦骨 `BpA785` · 地窖 `BpC801` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false`；RCON 关 |
| Git | **未** commit / **未** push；HEAD 仍 `4868bc1` |
| 证据 JSON | `/tmp/boss-prep-expand-test.json` · 补测 `/tmp/boss-prep-spire-retest.json` |
| 测后 ops | play=`[]` · login=`[]` |

### 静态 YAML（测前）

- 三线 `wave2b.end` → door2 AIR×9 → `boss_prep;delay=2` → prep.end → `boss;delay=2` ✅  
- 三线 `wave2a.start` → `$monstergroup{wave2b;delay=2}`；`wave2a.end` 无 operation-block / 无再拉 wave2b / 无 boss ✅  
- prep×2：断塔 `EmberDailySpireZombie` @ (±3,76,2)；焦骨 `EmberAshSkeleton` @ (±2,65,38)；地窖 `EmberDailyCryptSkeleton` @ (±3,60,42) ✅  
- `$kill`：卫尸×2 / 燃矢×2 / 誓印×2；无 kill-any ✅  
- wave1 / wave2a / boss body 相对 `8a69b97^` 零 diff；门 operation-block 零 diff ✅  
- 他四线（庭院/潮蚀/霜晶/锈轨）wt diff 空；MM 未动；Boss HP 215/200/210 ✅  
- 小怪合计：断塔 **13**+Boss · 焦骨 **14**+Boss · 地窖 **13**+Boss ✅  

### Live · 断塔（补测 Bp2S491）

1. **进本** `(0,64,0)` ·「体力 -30」·「清底层上阶」  
2. **房1：** door1 z=4@y64 air +「底层已清 · 上阶门开了」  
3. **重叠：** 裂隙箭骷 @≈tSkel → 卫尸 @Δ=**1704ms** · 重叠时 door2 iron · zb×2  
4. **先清 wave2b 卫尸：**「环廊已清 · 顶门开了 · 门槛压」· door2 air · **skel 仍×4**（门挂 wave2b）  
5. **boss_prep：**「【顶台·门槛】两侧断塔卫尸贴脸」· prep×2 @200ms · bossSeen=null →「门槛已清 · 守望现身」  
6. **Boss：**「【顶台】断塔守望」→「通关！奖励发放中…」  

> 首跑 `BpS653`：重叠 Δ=603ms / door2 中 iron / w2a 后 iron 已实锤；先清对射时环廊卫尸掉落致 `$kill`×2 未满、门未开——属测法顺序问题，YAML/链式无回归；补测改「先清 wave2b」即 PASS。

### Live · 焦骨（BpA785）

1. **进本** `(0,65,0)` ·「体力 -30」·「假岔也要清」  
2. **房1：** 主甬+假岔清完 door1 air +「前段已清 · 门开了」  
3. **重叠：** 焦骨尸 → 燃矢 @Δ=**2892ms** · 重叠中 door2 iron  
4. **wave2a only：**「弯折尸侧已清」· door2 仍 iron · skel 仍×3  
5. **wave2b：**「弯折已清 · 鼓室门开了 · 门槛压」· door2 air  
6. **boss_prep：**「【鼓室·门槛】两侧燃矢贴脸」· prep @201ms · held=**wooden_sword** →「门槛已清 · 焦核现身」  
7. **Boss：**「【鼓室】焦核蛮兵」→「通关！奖励发放中…」  

### Live · 地窖（BpC801）

1. **进本** `(0,72,0)` ·「体力 -30」·「清上层开门」  
2. **房1：** door1 z=18@y72 air +「上层已清 · 下阶门开了」  
3. **重叠：** 誓印 → 窖卫 @Δ=**2648ms** · order **skel→zombie** · 重叠中 door2 iron  
4. **wave2a only：**「高台箭侧已清」· door2 仍 iron · zb 仍×3  
5. **wave2b：**「中层已清 · 底层门开了 · 门槛压」· door2 air  
6. **boss_prep：**「【圆厅·门槛】两侧誓印点射」· prep×2 @200ms · held=**bow** →「门槛卫已清 · 残誓现身」  
7. **Boss：**「【圆厅】残誓守墓」→「通关！奖励发放中…」  

### 关键文案（节选）

**断塔：**
```
【中层环廊·对射】双侧裂隙箭骷 —— 一上环廊就双边挨箭 · 卫尸将交错压上
【环廊·卫尸】断塔卫尸×2 —— 与对射重叠压 · 清完开顶门
环廊已清 · 顶门开了 · 门槛压
【顶台·门槛】两侧断塔卫尸贴脸 —— 清完才出守望
门槛已清 · 守望现身
【顶台】断塔守望！有栏可绕 · 当心推离
余烬窟·断塔回廊 通关！奖励发放中…
```

**焦骨：**
```
【甬道·弯折】焦骨尸来袭 —— 燃矢将交错贴脸压上
【甬道·后段】燃矢骷重叠压上 —— 清完开鼓室门
弯折尸侧已清
弯折已清 · 鼓室门开了 · 门槛压
【鼓室·门槛】两侧燃矢贴脸 —— 清完才出焦核
门槛已清 · 焦核现身
余烬窟·焦骨甬道 通关！奖励发放中…
```

**地窖：**
```
【中层·高台】誓印骷对射 —— 窖卫将交错从地面压上
【中层·地面】窖卫尸重叠压上 —— 清完开底层门
高台箭侧已清
中层已清 · 底层门开了 · 门槛压
【圆厅·门槛】两侧誓印点射 —— 清完才出守墓
门槛卫已清 · 残誓现身
余烬窟·残誓地窖 通关！奖励发放中…
```

### 门材质采样

| 线 | 门 | 时机 | 材质 |
|----|----|------|------|
| 断塔 | door2 z=4@y70 | 重叠中 | iron×9 |
| 断塔 | door2 z=4@y70 | wave2b `$kill` 后（skel 仍×4） | air×9 |
| 焦骨 | door2 z=36@y65 | 重叠中 / w2a 清完 | iron×9 / iron×9 |
| 焦骨 | door2 z=36@y65 | wave2b `$kill` 后 | air×9 |
| 地窖 | door2 z=40@y66 | 重叠中 / w2a 清完 | iron×9 / iron×9 |
| 地窖 | door2 z=40@y66 | wave2b `$kill` 后 | air×9 |

---

## 阻塞 / 备注

- **无产品阻塞。** 首跑断塔因测法「先清对射」导致环廊卫尸掉落、`$kill`×2 未满未开门；补测改先清 wave2b 即 PASS（与既有 room2 短抽口径一致）。  
- 告总控：**可代推** `8a69b97`（及 tip `4868bc1` backlog 文档）；测岗 **未** commit/push。  
- 测后 ops 已清空：`[]`。
