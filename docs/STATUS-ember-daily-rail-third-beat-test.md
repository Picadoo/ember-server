# STATUS · 日常第三拍扩线 A · 锈轨矿监砸地读条短抽

**日期：** 2026-09-28 11:28 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-daily-rail-third-beat.md` §5 · `docs/STATUS-ember-daily-rail-third-beat.md` · tip `d1e57ef`  
**对照霜晶测法：** `docs/STATUS-ember-daily-third-beat-test.md`  
**测前：** MM 已热更（`mm reload` @ 11:24:30 CST · 59 怪 · 24 技能）  
**Verdict：** **✅ PASS**（五条验收全过 · ops=`[]`）

---

## 一句话

锈轨 `daily_rail` Boss **矿监砸地读条** live 实锤：提示「矿监蓄力砸地」后 **maxDown=1.2 @1193ms**（delay 窗内无该次砸地形伤；early 可有 SoftHit/普攻）；结算时 avgDist≈**4.01**（&lt;5）吃 **1.2**；提示后拉开至 **minDist=7.73** → **drop=0**。Skill **无 SLOW**（与霜晶差）。DP `EmberDailyRail` 相对 tip **零 diff**；门1/门2 iron→air、体力 -30、无 boss_prep、SoftHit/掉落/`dungeon-reward-script`/逼近 message/禁 kill-any/无 MCA 均过。测后 ops play+login=`[]`；**未** commit / **未** push。

---

## 五条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 提示「矿监蓄力砸地」先于该次读条伤（delay≈25 内可吃普攻/SoftHit，尚无该次砸地） | **PASS** | msg1 @ hp≈32.6 · **maxDownAt=1193ms** · 砸地形伤不在 early；earlyDrop 含 SoftHit/普攻（允许） |
| 2 | 结算时 r≈5 内 → damage≈1.2（**无 SLOW**） | **PASS** | **maxDown=1.2** @ el≈1193ms · avgDistSettle≈**4.01**（&lt;5）；Skill 静态 **no_slow=true** |
| 3 | 提示后立刻拉开 → 该次读条伤不中 | **PASS** | msg2 后 TP 至 z≈35 · **minDist=7.73** · **drop=0** · yMin=64（无落图） |
| 4 | 铁栅零回归：房2/门/无 boss_prep/通关箱/体力扣 30 | **PASS** | tip DP **零 diff**；door1 z15 / door2 z33 中段 iron×9→清完 air；进本「体力 -30」；无 boss_prep；通关「奖励发放中」 |
| 5 | 禁 kill-any / MCA；HP/SoftHit/掉落/reward-script 零改；逼近 message 仍在 | **PASS** | 无 `$kill-any`；commit 无 MCA；option/reward **零 diff**；HP210 + SoftHit + mmgive 掉落相对 tip^ **零改**；逼近 message 静态+live；`killall_used=false` |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| tip / HEAD | `d1e57ef` feat(mm): Rail Boss EmberRailSlamCast telegraph (third-beat A) |
| 玩家 / OP | `R3P737` / 临时 `R3Op55` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push |
| 证据 JSON | `/tmp/rail-third-beat-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped R3Op55） |

### 静态（测前）

- tip `d1e57ef` 相对 parent：`plugins/DungeonPlus/` **零 diff**（仅 MM Mobs/Skills + docs）✅  
- `EmberRailSlamCast`：message 矿监蓄力砸地 → lava/crit → `delay 25` → `damage 1.2` `@r=5` · **无 SLOW** · flame · CD8 ✅  
- `EmberDailyRailWarden`：删 `onTimer:40` 光环；挂 `EmberRailSlamCast ~onTimer:80`；Health **210**；SoftHit / 逼近 message / mmxp / mmgive×5 **零改** ✅  
- `monster.yml`：wave2a→wave2b delay1；门 z=15/33；**无** boss_prep；**无** `$kill-any` ✅  
- `option.yml`：相对 tip^ **零 diff**；注释扣体力 30；`dungeon-reward-script` 未动 ✅  
- working tree `EmberDailyRail/` 干净 ✅  

### Live · 读条时间线

1. **进本** `(0,64,0)` ·「[锈轨] 正在进入……（体力 -30）」  
2. **房1** door1 中段 iron → 清完 air +「轨闸开了」  
3. **房2** door2 中段 iron → 清完 air +「机房门开了」· 无 prep 文案  
4. **Boss**「锈轨矿监」@≈(0,64,46) ·「【机房】锈轨矿监！」  
5. **圈内试**（清 effect、无饱和盖伤；贴脸 dx=4）  
   - 提示：`【锈轨】矿监蓄力砸地——拉开！` · hp≈32.6  
   - el&lt;1000ms：无砸地形单跳（maxDown 落在 settle）  
   - el≈1193ms：**maxDown=1.2** · 结算均距 ≈4.01  
6. **拉开试**（提示后立刻 TP `(0,64,35)`）  
   - minDist=7.73 · drop=0 · 该次读条伤不中  
7. **通关**「余烬窟·锈轨矿道 通关！奖励发放中…」· live 见「矿监逼近」

### 关键文案（节选）

```
[锈轨] 正在进入……（体力 -30）
余烬窟·锈轨矿道 开始！已消耗体力 · 矿口安全 · 清主巷开闸
【矿道·前段】清尽锈轨尸再开轨闸 —— 别撞铁栅
主巷前段已清 · 轨闸开了
【矿道·主巷】锈轨尸压上 —— 当心支洞侧袭
【矿道·支洞】矿矢骷侧袭 —— 清完开机房门
后段已清 · 机房门开了
【机房】锈轨矿监！柱间绕打再输出
【锈轨】矿监蓄力砸地——拉开！
【锈轨】矿监逼近——Unknown！
余烬窟·锈轨矿道 通关！奖励发放中…
```

### 门材质采样

| 采样点 | iron | air |
|--------|------|-----|
| door1 中段（z=15, y=64..66） | ≥6 | — |
| door1 清完 | — | ≥6 |
| door2 中段（z=33, y=64..66） | ≥6 | — |
| door2 清完 | — | ≥6 |

---

## 回执（给总控）

| 项 | 内容 |
|----|------|
| 总评 | **PASS** |
| 五条 | 1✅ 提示先于伤 · 2✅ 圈内 maxDown=1.2（无 SLOW） · 3✅ 拉开 miss · 4✅ 铁栅/体力/无 prep · 5✅ 禁项+逼近保留 |
| 报告 | `docs/STATUS-ember-daily-rail-third-beat-test.md` |
| JSON | `/tmp/rail-third-beat-test.json` |
| ops | `[]`（测后已清空） |
| 阻塞点 | **无** |
| 备注 | 与霜晶差：本 Skill **无 SLOW**；earlyDrop 可含 SoftHit/普攻（验收允许）；delay 结算包约 +0.2s 网络/tick 窗，体感仍可读可躲；**未**拧数值 |
| Git | 测岗 **未** commit / **未** push；请总控代推本测报（若入库） |
