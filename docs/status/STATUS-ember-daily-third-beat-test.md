# STATUS · 日常第三拍 A · 霜晶霜暴读条短抽

**日期：** 2026-09-28 11:21 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-third-beat.md` §8 · `docs/status/STATUS-ember-daily-third-beat.md` · tip `3e8c969`  
**测前：** MM 已热更（`mm reload` @ 11:14:25 CST · 59 怪 · 23 技能）  
**Verdict：** **✅ PASS**（五条验收全过 · ops=`[]`）

---

## 一句话

霜晶 `daily_frost` Boss **霜暴读条** live 实锤：提示「霜核蓄力」后 **earlyDrop=0**（delay 窗内无该次霜暴），结算时 r≈3.7 内 **lateDrop=1.2**；提示后拉开至 **minDist=13.25** → **drop=0**（该次 miss）。DP `EmberDailyFrost` 相对 tip **零 diff**；门1/门2 iron→air、体力 -30、无 boss_prep、SoftHit/掉落/`dungeon-reward-script`/禁 kill-any/无 MCA 均过。测后 ops play+login=`[]`；**未** commit / **未** push。

---

## 五条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 提示「霜核蓄力」先于该次读条伤（delay≈25 内可吃普攻/SoftHit，尚无该次霜暴） | **PASS** | msg1 @ hp=40 · pos≈(4.7,70,48.7) · **earlyDrop=0**（el&lt;1000ms） |
| 2 | 读条结束 r≈5 内 → damage≈1.2 | **PASS** | **lateDrop=1.2** · maxDown≈1.0 @ el≈1895ms · avgDistSettle≈**3.67**（&lt;5） |
| 3 | 提示后立刻拉开出半径 → 该次读条伤不中 | **PASS** | msg2 后 TP 至门侧 z≈36 · **minDist=13.25** · **drop=0** · yMin=70（无落图） |
| 4 | 铁栅零回归：房2/门/无 boss_prep/通关箱/体力扣 30 | **PASS** | tip DP **零 diff**；door1/2 中段 iron×9→清完 air；进本文案「体力 -30」；无 boss_prep；通关「奖励发放中」 |
| 5 | 禁 kill-any / MCA / 掉落与 reward-script 改动 | **PASS** | 静态：无 `$kill-any`；commit 无 MCA；option.yml 零 diff；Boss SoftHit+mmgive 掉落行相对 tip^ **零改**；`killall_used=false` |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| tip / HEAD | `3e8c969` feat(mm): Frost Boss EmberFrostNovaCast telegraph |
| 玩家 / OP | `Tb3P696` / 临时 `Tb3Op52` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false` |
| Git | **未** commit / **未** push |
| 证据 JSON | `/tmp/third-beat-test.json` |
| 测后 ops | play=`[]` · login=`[]`（De-opped Tb3Op52） |

### 静态（测前）

- tip `3e8c969` 相对 parent：`plugins/DungeonPlus/` **零 diff**（仅 MM Mobs/Skills + docs）✅  
- `EmberFrostNovaCast`：message 霜核蓄力 → `cloud` 粒子 → `delay 25` → `damage 1.2` + SLOW `@r=5` · CD8 · **无** snowballpoof ✅  
- `EmberDailyFrostBrute`：删 `onTimer:45` 双光环；挂 `EmberFrostNovaCast ~onTimer:80`；Health **210**；SoftHit / mmxp / mmgive×5 **零改** ✅  
- `monster.yml`：wave2a→wave2b delay2；门 z=16/34；**无** boss_prep；**无** `$kill-any` ✅  
- `option.yml`：相对 tip^ **零 diff**；注释扣体力 30；`dungeon-reward-script` 未动 ✅  
- working tree `EmberDailyFrost/` 干净 ✅  

### Live · 读条时间线

1. **进本** `(0,70,0)` ·「[霜晶] 正在进入……（体力 -30）」  
2. **房1** door1 中段 iron → 清完 air +「冰闸开了」  
3. **房2** door2 中段 iron → 清完 air +「霜厅门开了」· 无 prep 文案  
4. **Boss**「霜核蛮兵」@≈(0.7,70,48.7)  
5. **圈内试**（清 effect、无饱和盖伤；贴脸 dx=4）  
   - 提示：`【霜厅】霜核蓄力——拉开！` · hp=40  
   - el&lt;1000ms：**earlyDrop=0**（尚无该次霜暴）  
   - el≈1.1～2.0s：**lateDrop=1.2** · 结算均距 ≈3.67  
6. **拉开试**（提示后立刻 TP `(0,70,36)`）  
   - minDist=13.25 · drop=0 · 该次读条伤不中  
7. **通关**「余烬窟·霜晶裂隙 通关！奖励发放中…」

### 关键文案（节选）

```
[霜晶] 正在进入……（体力 -30）
余烬窟·霜晶裂隙 开始！已消耗体力 · 冻台安全 · 清冻台开闸
【裂隙·左冻台】霜晶尸压上 —— 右侧将交错刷出
冻台已清 · 冰闸开了
【裂隙·折台】霜晶尸来袭 —— 霜矢将交错压上
折台已清 · 霜厅门开了
【霜厅】霜核蛮兵！冻厅绕打再输出
【霜厅】霜核蓄力——拉开！
余烬窟·霜晶裂隙 通关！奖励发放中…
```

### 门材质采样

| 采样点 | iron | air |
|--------|------|-----|
| door1 中段（z=16） | ≥6 | — |
| door1 清完 | — | ≥6 |
| door2 中段（z=34） | ≥6 | — |
| door2 清完 | — | ≥6 |

---

## 回执（给总控）

| 项 | 内容 |
|----|------|
| 总评 | **PASS** |
| 五条 | 1✅ 提示先于伤 · 2✅ 圈内 lateDrop=1.2 · 3✅ 拉开 miss · 4✅ 铁栅/体力/无 prep · 5✅ 禁项静态 |
| 报告 | `docs/status/STATUS-ember-daily-third-beat-test.md` |
| JSON | `/tmp/third-beat-test.json` |
| ops | `[]`（测后已清空） |
| 阻塞点 | **无** |
| 备注 | 粒子 cloud 可见路径已在 Skill 静态确认；delay 结算包约 +0.6s 网络/tick 窗，体感仍可读可躲；**未**拧数值 |
| Git | 测岗 **未** commit / **未** push；请总控代推本测报（若入库） |
