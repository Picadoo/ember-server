# STATUS · 精英周本厅一链式 · 短抽

**日期：** 2026-09-28 09:18–09:58 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-elite-wave-variance.md` §10 · `docs/status/STATUS-ember-elite-wave-variance.md` · commit `03ee076`  
**测前：** play FIFO `dp reload` → `[09:18:14] [DungeonPlus] 插件重载完毕` / `[09:18:15] [EmberEliteWeekly] 地牢内容初始化完毕`（**RCON 关**）  
**Verdict：** **✅ PASS**（四条验收全过 · ops=`[]`）

---

## 一句话

精英 `EmberEliteWeekly` 厅一链式 live 实锤：炽尸刷出后 **Δ=3008ms** 骨刺已在场（炽尸仍×3）；**传厅二仅 wave1b `$kill` 后**（wave1.end「厅一炽尸侧已清」时仍在厅一 · 骨刺仍×2 · **无** tp）；全本无 kill-any · wave1/wave1b 独立 `$kill` · wave2 双 `$kill` AND 同刷（蛮纹+混纹 **Δ=0ms** 不链式）；wave3/落点/HP/EmberWeekly·日常零误伤；小怪 **3+2+3=8** + Boss；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 四条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 厅一：炽尸刷出后 ≈2s 内骨刺已在场（炽尸未全清即重叠） | **PASS** | tZombie=2616ms → tSkel=5624ms · **Δ=3008ms**（文案 Δ=3000ms）；骨刺刷出时 zb=3；「炽尸贴上 —— 骨刺将交错压上」→「骨刺重叠压上」 |
| 2 | 传厅二仅 wave1b `$kill` 后；wave1.end 无 teleport | **PASS** | wave1 清完仍 **y≈60 x≈-28** +「厅一炽尸侧已清」+ 骨刺×2 · **无**「第一层词缀散了/蛮压」；wave1b 清完 **(-4,72,270)** +「第一层词缀散了」；YAML `wave1.end` 无 `$teleport`/`wave2` |
| 3 | 全本无 kill-any；wave1/wave1b 独立 `$kill`；wave2 双 `$kill` AND 同刷不链式 | **PASS** | YAML 无 `$kill-any`；`wave1`=`$kill{炽尸;3}` · `wave1b`=`$kill{骨刺;2}` · `wave2` 蛮纹×1 **与** 混纹×2；`wave2.start` 无 `$monstergroup`；live 蛮纹+混纹同帧出现 **Δ=0ms** |
| 4 | wave3/传送落点/HP/EmberWeekly·日常零误伤；合计仍 8+Boss | **PASS** | wave3 body 相对 `03ee076^` **IDENTICAL**；落点 (-4,72,270)/(22,68,270) 未动；counts 3+2+3+1；MM Health 5250/3300/2700/10500/5200；commit 仅 `EmberEliteWeekly/monster.yml`+STATUS；live：厅三「烬纹执行官」→「试炼通过」 |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `03ee076` fix(elite): 厅一链式 wave1→wave1b + 消 kill-any |
| HEAD（测时） | `46f66f8`（测报未入仓；**未** commit/push） |
| 玩家 / OP | `Ew2P779` / 临时 `Ew2Op19` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 进本 | `/corerpg elite start`（本周首次免费 · Lv.42 · 烬刃 T2+7） |
| 击杀 | 仅 `attack` + `/corerpg skill`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| 证据 JSON | `/tmp/elite-wave-variance-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Ew2Op19） |

### 静态 YAML（测前）

- `wave1.start` → `$monstergroup{wave1b;delay=2}` ✅  
- `wave1.end` 仅「厅一炽尸侧已清」+ heal；**无** `$teleport` / wave2 ✅  
- `wave1b.end` tp `(-4,72,270)` + wave2 delay=3 ✅  
- `wave2` 双 `$kill` AND（蛮纹×1 + 混纹×2）；start **无** 链式 `$monstergroup` ✅  
- `wave2.end` tp `(22,68,270)` + wave3 delay=3 ✅  
- wave3 相对父 **零 diff**；无 kill-any ✅  
- 小怪 **8** + Boss×1；MM Health 5250/3300/2700/10500/5200 未改 ✅  
- commit 仅 `EmberEliteWeekly/monster.yml` + `docs/status/STATUS-ember-elite-wave-variance.md`；EmberWeekly/日常/option/MM **零 diff** ✅  

### Live 关键时间线

1. **进本** 「精英试炼开启」·「【试炼·厅一】炽尸贴上 —— 骨刺将交错压上」  
2. **重叠：** 炽尸×3 @≈2616ms → 骨刺×2 @≈5624ms（**Δ≈3.0s**，delay=2 + 实体同步窗）· 重叠时 zb 仍×3  
3. **wave1 only：**「厅一炽尸侧已清」· 仍厅一（x≈-28 y≈60）· 骨刺仍×2 · **无**厅二传送  
4. **wave1b：**「第一层词缀散了」· tp `(-4,72,270)`  
5. **wave2：** 蛮纹×1+混纹×2 **同帧**刷出（Δ=0）·「—— 试炼二：蛮压词缀 ——」→「蛮压退潮。执行官在前。」· tp `(22,68,270)`  
6. **wave3：**「—— 试炼终：烬纹执行官 ——」→「试炼通过。火还在你这边。」（hits=29 · 25.0s）

### 关键文案（节选）

```
[精英] 正在进入……（本周首次免费）
精英试炼开启。本周只有一次——词缀会咬人。
【试炼·厅一】炽尸贴上 —— 骨刺将交错压上
【试炼·厅一】骨刺重叠压上 —— 清完进入蛮压
厅一炽尸侧已清
第一层词缀散了。
—— 试炼二：蛮压词缀 ——
蛮压退潮。执行官在前。
—— 试炼终：烬纹执行官 ——
试炼通过。火还在你这边。
```

### 位置采样（传厅语义）

| 时机 | 坐标 |
|------|------|
| wave1 清完（仍厅一） | (-27.56, 60.3, 272.77) |
| wave1b 清完（厅二） | (-4, 72, 270) |
| wave2 清完（厅三） | (22, 68, 270) |

### 击杀耗时（参考）

| 段 | hits | ms |
|----|------:|---:|
| wave1 炽尸×3 | 98 | 67776 |
| wave1b 骨刺×2 | 43 | 45153 |
| wave2 蛮纹+混纹 | 84 | 60761 |
| wave3 Boss | 29 | 25030 |

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前 09:18:14 reload） |
| 测后 ops=`[]` | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `46f66f8` |

---

## 备注（非挡）

- 厅一实体观测时偶发掉至 Y≈60（地图坑/寻路）；击杀过滤已覆盖 Y55–76，不影响波次与传送落点验收。  
- 战斗参照 4.4f：烬刃 T2+7 + 护符+4 + blaze 誓约；另加 timing 向 strength/resistance（非数值平衡验收）。  
- 1.12 攻速窗须 ≈600ms/刀，过快挥砍会导致有效伤害接近 0（首轮踩坑已弃）。

## Blocker

无。

## ops

`[]`
