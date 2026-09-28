# STATUS · 周本中核链式重叠 · 短抽

**日期：** 2026-09-28 09:05–09:13 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-weekly-wave-variance.md` §10 · `docs/STATUS-ember-weekly-wave-variance.md` · commit `75e06b0`  
**测前：** play FIFO `dp reload` → `[09:05:38] [DungeonPlus] 插件重载完毕` / `[09:05:39] [EmberWeekly] 地牢内容初始化完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（四条验收全过 · ops=`[]`）

---

## 一句话

周本 `EmberWeekly` 中核链式 live 实锤：尸刷出后 **Δ=3010ms** 骷已在场（尸仍×2）；**传深室仅 wave2b `$kill` 后**（wave2a.end「中核尸侧已清」时仍在 Y68 中核 · 骷仍×3）；无 kill-any · wave2a/2b 独立 `$kill`；wave1/wave3/boss 相对 `75e06b0^` 零内容 diff（wave1.end 仅组名→wave2a）；小怪 **3+2+3=8** + 甲 + 乙；HP 110/70/350/1500 未改；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 四条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 中核：尸刷出后 ≈2s 内骷已在场（尸未全清即重叠） | **PASS** | tZombie=2711ms → tSkel=5721ms · **Δ=3010ms**；骷刷出时 zb=2；文案「僵尸贴上 —— 骷髅将交错压上」→「骷髅重叠压上」 |
| 2 | 传深室仅 wave2b `$kill` 完成后；wave2a.end 无 teleport | **PASS** | wave2a 清完仍 **y=68 z≈306** +「中核尸侧已清」+ 骷×3 · **无**「转入深室」；wave2b 清完 **(-40,63,340)** +「中核已清 · 转入深室」；YAML `wave2a.end` 无 `$teleport`/`wave3` |
| 3 | 无 kill-any；wave2a/2b 独立 `$kill` | **PASS** | YAML 无 `$kill-any`；`wave2a`=`$kill{深核廊僵尸;2}` · `wave2b`=`$kill{深核廊骷髅;3}`；commit 相对父消除旧 kill-any×5 |
| 4 | wave1/wave3/boss 行为与落点不变；小怪仍 8+甲+乙；HP/其他线零误伤 | **PASS** | wave3/boss body 相对 `75e06b0^` **IDENTICAL**；wave1 仅 end 组名 wave2→wave2a；落点 (-40,68,308)/(-40,63,340) 未动；counts 3+2+3+1+1；MM Health 110/70/350/1500；commit 仅 `EmberWeekly/monster.yml`+STATUS；live：前厅→中核 tp · 甲清完「终局将至」· 乙 start「【深核·终局】蛮兵·乙」 |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `75e06b0` feat(dp): 周本中核链式重叠（消 kill-any） |
| 玩家 / OP | `Ww2P277` / 临时 `Ww2Op43` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 进本 | `/trmenu open ember_weekly` → 回落 `/corerpg enter weekly`（Lv.24 · 体力已设） |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push；HEAD 仍 `c4a8bae`（测报未入仓） |
| 证据 JSON | `/tmp/weekly-wave-variance-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Ww2Op43） |

### 静态 YAML（测前）

- `wave2a.start` → `$monstergroup{wave2b;delay=2}` ✅  
- `wave2a.end` 仅「中核尸侧已清」+ heal；**无** `$teleport` / wave3 ✅  
- `wave2b.end` tp `(-40,63,340)` + wave3 delay=3 ✅  
- wave1.end → wave2a delay=3 + tp 中核 `(-40,68,308)` ✅  
- wave3 → boss delay=4；甲/乙 `$kill` 与刷点相对父 **零 diff** ✅  
- 无 kill-any；独立 `$kill` 尸×2 / 骷×3 ✅  
- 小怪 **8** + 甲×1 + 乙×1；MM Health 110/70/350/1500 未改 ✅  
- commit 仅 `EmberWeekly/monster.yml` + `STATUS-ember-weekly-wave-variance.md` ✅  

### Live 关键时间线

1. **进本** `(-40,65,270)` ·「深核·周 开始！已消耗体力 ×1」·「【深核·前厅】清杂：僵尸×3」  
2. **wave1 清完** → tp 中核 `(-40,68,308)`  
3. **重叠：** 尸×2 @≈2711ms → 骷×3 @≈5721ms（**Δ≈3.0s**，delay=2 + 实体同步窗）· 重叠时 zb 仍×2  
4. **wave2a only：**「中核尸侧已清」· 仍 y=68 · 骷仍×3 · **无**深室传送  
5. **wave2b：**「中核已清 · 转入深室」· tp `(-40,63,340)`  
6. **wave3：**「【深核·深室】蛮兵·甲」→「甲倒下 · 终局将至」（hits=16 · 6.0s）  
7. **boss start：**「【深核·终局】蛮兵·乙——别贪刀」· 全清超时（bot 黑名单 50 hits · harness；非 YAML/落点问题）

### 关键文案（节选）

```
深核·周 开始！已消耗体力 ×1
【深核·前厅】清杂：僵尸×3
【深核·中核】僵尸贴上 —— 骷髅将交错压上
【深核·中核】骷髅重叠压上 —— 清完进入深室
中核尸侧已清
中核已清 · 转入深室
【深核·深室】蛮兵·甲！清完还有终局·乙
甲倒下 · 终局将至
【深核·终局】蛮兵·乙——别贪刀
```

### 位置采样（传深室语义）

| 时机 | 坐标 |
|------|------|
| 进本 / 前厅 | (-40, 65, 270) |
| wave1 后（中核） | (-40, 68, 308) |
| wave2a 清完（仍中核） | (-40.62, 68, 306.41) |
| wave2b 清完（深室） | (-40, 63, 340) |

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前 09:05:38 reload） |
| 测后 ops=`[]` | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `c4a8bae` |

---

## 备注（非挡）

- Boss 全清超时：mineflayer 近战黑名单（50 hits 未有效结算）；**乙 start 文案与波次挂点已 live 见到**；YAML boss 组相对父零 diff。中核链式四条硬验收不依赖乙击杀完成。  
- 首跑（WwP829）重叠观测窗偏晚 Δ=0 已废弃；本报以 Ww2P277 重测为准。

## Blocker

无。

## ops

`[]`
