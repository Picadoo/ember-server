# STATUS · 断塔环廊防坠（wave2b 刷点内收）· 短抽

**日期：** 2026-09-28 11:03 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-spire-ringfall.md` §5 · `docs/status/STATUS-ember-daily-spire-ringfall.md` · 施工 `29fb133` · tip `74e39d7`  
**测前：** play FIFO `dp reload` → `[11:00:05] [DungeonPlus] 插件重载完毕` / `EmberDailySpire` 初始化完毕（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（五条验收全过 · 路径「先清对射」· ops=`[]`）

---

## 一句话

`daily_spire`：**先清 wave2a 裂隙箭骷**（乱序）后清 wave2b 卫尸，door2 **air×9** 开；重叠 Δ=**2307ms**；南缘刷点内收后留廊；近阶卫尸曾一度落到 y64（未虚空消亡，仍计入 `$kill`）；door2→prep×2@顶台→Boss 通关；YAML 仅 wave2b location / 无 kill-any；真击杀、禁 killall、禁改配置、**未** commit/push；测后 ops play+login=`[]`。

---

## 五条验收

| # | 验收点 | 结果 | 要点证据 |
|---|--------|------|----------|
| 1 | 乱序清对射仍能开门：卫尸不因坠崖致 `$kill`×2 不满；door2 最终开 | **PASS** | 路径 `clear_wave2a_first` ·「对射侧已清」时 door2 **iron×9** · zb 仍×2（环廊1 + 底厅1）→ 清卫尸后「环廊已清 · 顶门开了」· door2 **air×9**；南缘首现 `(0.7,71,-3.9)` 留廊；近阶首现 `(-0.1,70,1.5)` 清对射中曾降至 y64（**未**虚空消亡） |
| 2 | 房2 链式零回归：wave2a.start→delay2 wave2b；wave2a.end 无 door；重叠可感 | **PASS** | 重叠 Δ=**2307ms** · skel 与 zb 同窗 · 重叠中 door2 iron×9；清 w2a 后 door2 **仍 iron**；YAML：`wave2a.start`→`wave2b;delay=2`；`wave2a.end` 仅文案+heal、**无** operation-block |
| 3 | door2 仍仅挂 wave2b.end；`$kill` 断塔卫尸×2；无 kill-any | **PASS** | 静态：door2 九格 + `boss_prep;delay=2` 在 wave2b.end；`$kill{断塔卫尸;amount=2}`；条件行无 `$kill-any`；落地 loc=`(0,70,-4)` / `(-1,69,1)`；live：door2 开在清卫尸后 |
| 4 | boss_prep 零回归：door2→prep×2@顶台→boss | **PASS** | prepSeen=**200ms** · bossSeen=null →「【顶台·门槛】两侧断塔卫尸贴脸」→「门槛已清 · 守望现身」→「【顶台】断塔守望」→通关；prep 坐标仍 `(±3,76,2)` |
| 5 | 他线/Boss HP/门坐标零误伤 | **PASS** | `29fb133` 仅 Spire `monster.yml`+STATUS；MM Spire 相对 parent **零 diff**；Boss HP **215**；door2 operation-block 在施工 commit **未动**；他线未进该 commit |

**总评：** PASS

---

## 分项证据

### 环境

| 项 | 值 |
|----|-----|
| tip | `74e39d7` docs: backlog — Spire ringfall awaiting short-check |
| 施工 | `29fb133` fix(spire): ringfall — wave2b spawns inward (scheme A) |
| 路径 | **先清对射**（必测）；对照「先清卫尸」本轮未跑（可选，Boss前压补测 `Bp2S491` 已证开门） |
| OP / 玩家 | `RfOp91` / `RfS795`（唯一短抽 bot） |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 击杀 | 仅 `attack`/`swingArm`；`killall_used=false` |
| 配置 | 未改；`config_changed=false`；RCON 关 |
| Git | **未** commit / **未** push；HEAD 仍 `74e39d7` |
| 证据 JSON | `/tmp/spire-ringfall-test.json` |
| 测后 ops | play=`[]` · login=`[]` |

### 静态 YAML（测前）

- wave2b loc：**`(0,70,-4)`** · **`(-1,69,1)`**（对齐施工 STATUS）  
- `wave2a.start`→`$monstergroup{wave2b;delay=2}`；`wave2a.end` **无** door / **无** 再拉 wave2b  
- `wave2b.end`→door2 AIR×9（x=-1..1,y=70..72,z=4）→`boss_prep;delay=2`  
- `$kill{断塔卫尸;amount=2}`；条件行无 kill-any  
- boss_prep：`EmberDailySpireZombie` @ `(±3,76,2)` · `$kill`×2  
- Boss MM Health **215**；`29fb133` 文件集仅 Spire monster.yml + 施工 STATUS  

### Live · 断塔（RfS795 · 先清对射）

1. **进本** `(0,64,0)` ·「体力 -30」·「清底层上阶」  
2. **房1：** door1 z=4@y64 air +「底层已清 · 上阶门开了」  
3. **重叠：** 裂隙箭骷 @≈101ms → 卫尸 @Δ=**2307ms** · 重叠中 door2 iron×9 · zb×2  
4. **刷点首现：** 南缘 `(0.7,71,-3.9)` · 近阶 `(-0.1,70,1.5)`（对齐内收意图）  
5. **先清 wave2a：**「对射侧已清」· door2 **仍 iron×9** · zb 仍×2（环廊 y70 + 底厅 y64）· 清对射过程 zbFall 采样有近阶尸一度 y64（楼梯/位移，**非**南崖虚空）  
6. **再清 wave2b：**「环廊已清 · 顶门开了 · 门槛压」· door2 **air×9**  
7. **boss_prep：**「【顶台·门槛】两侧断塔卫尸贴脸」· prep×2 @200ms · bossSeen=null →「门槛已清 · 守望现身」  
8. **Boss：**「【顶台】断塔守望」→「通关！奖励发放中…」

### 关键文案（节选）

```
【中层环廊·对射】双侧裂隙箭骷 —— 一上环廊就双边挨箭 · 卫尸将交错压上
【环廊·卫尸】断塔卫尸×2 —— 与对射重叠压 · 清完开顶门
对射侧已清
环廊已清 · 顶门开了 · 门槛压
【顶台·门槛】两侧断塔卫尸贴脸 —— 清完才出守望
门槛已清 · 守望现身
【顶台】断塔守望！有栏可绕 · 当心推离
余烬窟·断塔回廊 通关！奖励发放中…
```

### 软观察（不挡 PASS）

- 近阶卫尸在「先清对射」窗内曾位移至 y64 底厅（仍存活、可击杀、计入 `$kill`），**未**导致开门失败。  
- 南缘内收点 `(0,70,-4)` 本跑留在环廊 y≈70–71，未再现原南缘崖坠。  
- 若日刷仍频繁「卫尸掉底厅/虚空」再升方案 B（矮栏）另批；本轮方案 A 主硬条已过。

---

## 禁项确认

| 禁项 | 勾 |
|------|----|
| killall | [x] 未用 |
| 改配置 / RCON | [x] 未改；RCON 关 |
| 改门 / kill-any / 降 amount | [x] 静态+live 零动 |
| commit / push | [x] 测岗未做；HEAD=`74e39d7` |
| 测后 ops 残留 | [x] play=`[]` login=`[]` |

---

## 回执（告总控）

| 项 | 内容 |
|----|------|
| 总评 | **PASS** |
| 五条 | 1乱序开门 ✅ · 2链式 ✅ · 3 door2/`$kill` ✅ · 4 prep ✅ · 5 零误伤 ✅ |
| 报告 | `docs/status/STATUS-ember-daily-spire-ringfall-test.md` |
| JSON | `/tmp/spire-ringfall-test.json` |
| ops | play=`[]` · login=`[]` |
| 阻塞点 | **无**（软观察：近阶尸偶发掉底厅，不挡 PASS；升 B 另批） |
| 代推 | 请总控代推施工 `29fb133` + 本测 STATUS（测岗未 push） |
