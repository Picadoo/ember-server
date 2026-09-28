# STATUS · 庭院+潮蚀房2 start链式 · 短抽

**日期：** 2026-09-28 10:31 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-daily-courtyard-tide-room2-chain.md` §6 · `docs/STATUS-ember-daily-courtyard-tide-room2-chain.md` · commit `eb81942`（tip `8b49f3c`）  
**测前：** play FIFO `dp reload` → `[10:26:19] [DungeonPlus] 插件重载完毕` / EmberDaily · EmberDailyTide 导入完毕（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（五条验收全过 · 两线均 PASS · ops=`[]`）

---

## 一句话

庭院 `daily` / 潮蚀 `daily_tide` 房2 start 链式 live 实锤：对射/浪矢在场时 **Δ≈602/603ms** 涌尸/桥头尸已出（skel 仍×2/×3）；**door2 仅 wave2b `$kill` 后开**（wave2a.end「对射侧/对岸弓侧已清」时门仍 iron×9）；door2 后仍进 **boss_prep**（门槛尸×2 / 浪矢×2）再 Boss，prep YAML 相对 `eb81942^` **零 diff**；`$kill` 按组独立、无 kill-any；commit 仅两线 `monster.yml`+STATUS，他五线零 wt diff；小怪仍 **12+Boss/线**；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 五条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 房2重叠：对射/浪矢在场约 2s 内涌尸/桥头尸已出 | **PASS** | 庭院 tSkel=100ms → tZombie=702ms · **Δ=602ms** · zb 出时 sk=2；潮蚀 tSkel=100 → tZombie=703 · **Δ=603ms** · zb 出时 sk=3；文案「涌尸将重叠压上/潮蚀尸将重叠冲锋」→「僵尸/潮蚀尸重叠压上」 |
| 2 | door2 仅 wave2b 清完后开；wave2a.end 无开门/无再拉 wave2b | **PASS** | 重叠中 / wave2a 清完 door2 **iron×9** +「对射侧已清/对岸弓侧已清」**无**开门文案；wave2b 清完 **air×9** +「回廊已清 · Boss 门开了 / 折桥已清 · 闸厅门开了」；YAML `wave2a.end` 无 operation-block / 无再拉 wave2b |
| 3 | `$kill` 独立；无 kill-any | **PASS** | 庭院 骷×2 / 尸×4 / prep 尸×2；潮蚀 浪矢×3 / 尸×3 / prep 浪矢×2；两线 YAML 无 `$kill-any`；去色名与数量同现网 |
| 4 | door2 后仍进 boss_prep 再 Boss；prep 零 diff | **PASS** | 庭院 prepSeen=200ms · prep×2 ·「终厅·门槛」→「门槛已清 · 蛮兵现身」→ Boss；潮蚀 prepSeen=200ms · prep 浪矢×2 ·「闸厅·门槛」→「门槛卫已清 · 潮闸蛮兵现身」→ Boss；`boss_prep`/`wave1`/`boss` body 相对 `eb81942^` **零非预期 diff** |
| 5 | 仅两线 monster.yml 房2 diff；他五线零误伤；12+Boss/线 | **PASS** | commit `eb81942` 仅 EmberDaily/Tide `monster.yml` + STATUS；Ash/Rail/Spire/Crypt/Frost wt diff 空；MM 未动；庭院 4+2+4+2=12 +Boss；潮蚀 4+3+3+2=12 +Boss |

**总评：** PASS

---

## 分线表

| 线 | enter | 玩家 | 重叠 Δ | door2 中/后 w2a / 后 w2b | prep | 通关 | 结果 |
|----|-------|------|--------|--------------------------|------|------|------|
| 庭院 EmberDaily | `daily` | `Ct2Y487` | **602ms** | iron×9 / iron×9 / **air×9** | 门槛尸×2 @200ms | ✅ | **PASS** |
| 潮蚀 EmberDailyTide | `daily_tide` | `Ct2T683` | **603ms** | iron×9 / iron×9 / **air×9** | 门槛浪矢×2 @200ms | ✅ | **PASS** |

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| commit | `eb81942` fix(daily): 庭院+潮蚀房2 start链式重叠 |
| tip | `8b49f3c` |
| OP / 玩家 | `Ct2Op26` / 庭院 `Ct2Y487` · 潮蚀 `Ct2T683` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push；HEAD 仍 `8b49f3c` |
| 证据 JSON | `/tmp/courtyard-tide-room2-chain-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Ct2Op26 + wipe） |

### 静态 YAML（测前）

- 两线 `wave2a.start` → `$monstergroup{wave2b;delay=2}` ✅  
- 两线 `wave2a.end` 仅「对射侧已清 / 对岸弓侧已清」+ heal；**无** operation-block / boss_prep / boss / 再拉 wave2b ✅  
- 两线 `wave2b.end` door2 AIR×9 → `boss_prep` delay=2 ✅  
- `boss_prep` / `wave1` / `boss` body 相对 `eb81942^` 零 diff ✅  
- 无 kill-any；他五线 wt diff 空；commit 仅两线 monster.yml + STATUS；MM 未动 ✅  
- 数量：庭院 4+2+4+2=12+Boss；潮蚀 4+3+3+2=12+Boss ✅  

### Live · 庭院

1. **进本** `(0,65,0)` ·「体力 -30」·「清前厅开门」  
2. **房1：** door1 z=13 air×9 +「前厅已清 · 门开了」  
3. **重叠：** 骷×2 @≈100ms → 涌尸×4 @≈702ms（**Δ≈602ms**）· 重叠时 door2 iron×9 · sk 仍×2  
4. **wave2a only：**「对射侧已清」· door2 仍 iron×9 · zb 仍×4  
5. **wave2b：**「回廊已清 · Boss 门开了 · 终厅门槛压」· door2 air×9  
6. **boss_prep：**「【终厅·门槛】两侧僵尸贴脸」· prep×2 @200ms →「门槛已清 · 蛮兵现身」  
7. **Boss：**「【终厅·中央垫】Boss 蛮兵」→「通关！奖励发放中…」

### Live · 潮蚀

1. **进本** `(0,64,0)` ·「体力 -30」·「清沿岸开闸」  
2. **房1：** door1 z=18 air×9 +「沿岸已清 · 桥闸开了」  
3. **重叠：** 浪矢×3 @≈100ms → 桥头尸×3 @≈703ms（**Δ≈603ms**）· 重叠时 door2 iron×9 · sk 仍×3  
4. **wave2a only：**「对岸弓侧已清」· door2 仍 iron×9 · zb 仍×3  
5. **wave2b：**「折桥已清 · 闸厅门开了 · 门槛卫」· door2 air×9  
6. **boss_prep：**「【闸厅·门槛】两侧浪矢点射」· prep×2 @200ms →「门槛卫已清 · 潮闸蛮兵现身」  
7. **Boss：**「【闸厅】潮闸蛮兵」→「通关！奖励发放中…」

### 关键文案（节选）

**庭院：**
```
【回廊·对射】左右柱后骷髅 —— 涌尸将重叠压上
【回廊·涌尸】僵尸重叠压上 —— 清完开 Boss 门
对射侧已清
回廊已清 · Boss 门开了 · 终厅门槛压
【终厅·门槛】两侧僵尸贴脸 —— 清完才出蛮兵
门槛已清 · 蛮兵现身
【终厅·中央垫】Boss 蛮兵！走位躲开砸击再输出
余烬窟·庭院 通关！奖励发放中…
```

**潮蚀：**
```
【水道·折桥】对岸浪矢骷 —— 潮蚀尸将重叠冲锋
【水道·桥头】潮蚀尸重叠压上 —— 清完开闸厅门
对岸弓侧已清
折桥已清 · 闸厅门开了 · 门槛卫
【闸厅·门槛】两侧浪矢点射 —— 清完才出蛮兵
门槛卫已清 · 潮闸蛮兵现身
【闸厅】潮闸蛮兵！池边绕打再输出
余烬窟·潮蚀水道 通关！奖励发放中…
```

### 门材质采样

| 线 | 门 | 时机 | 材质 |
|----|----|------|------|
| 庭院 | door2 z=25@y65 | 重叠中 | iron_bars×9 |
| 庭院 | door2 z=25@y65 | wave2a 清完 | iron_bars×9 |
| 庭院 | door2 z=25@y65 | wave2b `$kill` 后 | air×9 |
| 潮蚀 | door2 z=38@y64 | 重叠中 | iron_bars×9 |
| 潮蚀 | door2 z=38@y64 | wave2a 清完 | iron_bars×9 |
| 潮蚀 | door2 z=38@y64 | wave2b `$kill` 后 | air×9 |

---

## 共性守则

| 项 | 结果 |
|----|------|
| 真击杀 / 禁 killall | ✅ |
| 禁改配置 | ✅ |
| RCON 关 · FIFO 热更 | ✅（测前 10:26:19 reload） |
| 测后 ops=[] | ✅ play+login |
| 未 commit/push | ✅ HEAD 仍 `8b49f3c` |
| 短唯一 bot | ✅ 逐线单玩家；临时 OP 测完清空 |

---

## Blocker

无。

## ops

`[]`

## 告总控

可代推：测报 `docs/STATUS-ember-daily-courtyard-tide-room2-chain-test.md`（本文件）· 证据 `/tmp/courtyard-tide-room2-chain-test.json` · 施工 commit 已是 `eb81942`。
