# STATUS · 霜晶房2链式重叠 · 短抽

**日期：** 2026-09-28 09:00 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-daily-frost-room2.md` · `docs/STATUS-ember-daily-frost-room2.md` · commit `36e98c8`  
**测前：** play FIFO `dp reload` → `[08:57:03] [DungeonPlus] 插件重载完毕` / `[EmberDailyFrost] 地牢内容初始化完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（四条验收全过 · ops=`[]`）

---

## 一句话

霜晶 `daily_frost` 房2链式重叠 live 实锤：折台尸后 **Δ=1325ms** 霜矢已在场（尸仍×3）；**霜厅门仅 wave2b `$kill` 后开**（wave2a.end「折台尸侧已清」时门仍 iron×9）；房1 左右交错仍旧（Δ=1707ms · 右存活时 door1 iron×9 · 开门挂 wave1b）；合计 11+Boss、HP 51/40/210 未改；commit 仅 `EmberDailyFrost/monster.yml`+STATUS；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 四条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | wave2a 启动后 ≈2s 内 wave2b 霜矢已出（尸未清完即重叠） | **PASS** | tZombie=120ms → tSkel=1445ms · **Δ=1325ms**；霜矢刷出时 zb=3；文案「霜晶尸来袭 —— 霜矢将交错压上」→「霜矢骷重叠压上」 |
| 2 | 霜厅门仅 wave2b 清完后开；wave2a.end 不开门 | **PASS** | 重叠中 / wave2a 清完 door2 **iron×9** +「折台尸侧已清」**无**霜厅开门文案；wave2b 清完 **air×9** +「折台已清 · 霜厅门开了」；YAML `wave2a.end` 无 operation-block/boss |
| 3 | 房1 左右交错仍旧（wave1→delay3 wave1b；开门在 wave1b） | **PASS** | tLeft=1004ms → tRight=2711ms · **Δ=1707ms**；右存活时 door1 **iron×9**；清完 **air×9** +「冻台已清 · 冰闸开了」；wave1+wave1b body 相对 `36e98c8^` **零非预期 diff**；YAML `delay=3` / 开门挂 wave1b |
| 4 | 坐标/HP/其他线未误伤（静态 diff 仅 Frost monster.yml） | **PASS** | commit 仅 `EmberDailyFrost/monster.yml` + STATUS；非霜六线 wt diff 空；MM Health 尸51/矢40/Boss210 未改；坐标门位 z=16/34 与刷点未动；小怪 **3+2+3+3=11** + Boss×1；无 kill-any |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `36e98c8` feat(dp): 霜晶房2链式重叠 |
| 玩家 / OP | `Fr2P933` / 临时 `Fr2Op20` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push；HEAD 仍 `8cdad3f`（测报未入仓） |
| 证据 JSON | `/tmp/frost-room2-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Fr2Op20） |

### 静态 YAML（测前）

- `wave2a.start` → `$monstergroup{wave2b;delay=2}` ✅  
- `wave2a.end` 仅「折台尸侧已清」+ heal；**无** operation-block / boss / 再拉 wave2b ✅  
- `wave2b.end` door2 AIR×9 @ z=34 + boss delay=3 ✅  
- 房1：`wave1.start` → wave1b delay=3；开门仅 wave1b @ z=16；body 相对 `36e98c8^` 零非预期 ✅  
- 坐标：左 `-3,70,*` / 右 `3,70,*` / 折台尸 `-4,70,24`+`0,70,30` / 霜矢 `4,70,*` ✅  
- 无 kill-any / 无 boss_prep；非霜六线 wt diff 空；commit 仅 Frost monster.yml + STATUS ✅  

### Live 关键时间线

1. **进本** `(0,70,0)` ·「体力 -30」·「左冻台…右侧将交错刷出」  
2. **房1 交错：** 左×3 @≈1004ms → 右×1+ @≈2711ms（Δ≈1.7s，delay=3 + 观测窗）· 右存活时 door1 iron×9 → 清完 air×9 +「冻台已清 · 冰闸开了」  
3. **重叠：** 尸×3 @≈120ms → 霜矢×3 @≈1445ms（Δ≈1.3s，delay=2 + 实体同步窗）· 重叠时 door2 iron×9 · zb 仍×3  
4. **wave2a only：**「折台尸侧已清」· door2 仍 iron×9 · 霜矢仍×3  
5. **wave2b：**「折台已清 · 霜厅门开了」· door2 air×9  
6. **Boss：** prepSeen=null ·「【霜厅】霜核蛮兵」→「通关！奖励发放中…」

### 关键文案（节选）

```
【霜晶】正在进入……（体力 -30）
余烬窟·霜晶裂隙 开始！已消耗体力 · 冻台安全 · 清冻台开闸
【裂隙·左冻台】霜晶尸压上 —— 右侧将交错刷出
【裂隙·右冻台】交错侧袭 —— 跨缝清对侧
冻台已清 · 冰闸开了
【裂隙·折台】霜晶尸来袭 —— 霜矢将交错压上
【裂隙·后段】霜矢骷重叠压上 —— 清完开霜厅门
折台尸侧已清
折台已清 · 霜厅门开了
【霜厅】霜核蛮兵！冻厅绕打再输出
余烬窟·霜晶裂隙 通关！奖励发放中…
```

### 门材质采样

| 门 | 时机 | 材质 |
|----|------|------|
| door1 z=16@y70 | 右存活时 | iron_bars×9 |
| door1 z=16@y70 | wave1b `$kill` 后 | air×9 |
| door2 z=34@y70 | 重叠中 / wave2a 清完 | iron_bars×9 |
| door2 z=34@y70 | wave2b `$kill` 后 | air×9 |

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前 08:57:03 reload） |
| 测后 ops=[] | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `8cdad3f` |

---

## Blocker

无。

## ops

`[]`
