# 设计稿 · 断塔环廊坠落 `$kill`（wave2b 刷点内收）

> **已批准方案 A（总控 · 2026-09-28）。** 可施工；仅 Spire `monster.yml` wave2b location。  
> tip 参考：`97f1110`（节奏杠杆里程碑收口 · Next: Spire ringfall）  
> 债源：`docs/STATUS-ember-daily-rhythm-leverage-closeout-review.md` 软债#1「断塔坠落 `$kill`」；首跑笔记 `docs/STATUS-ember-daily-boss-prep-expand-test.md`  
> 坐标真相：`plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml` + `docs/STATUS-ember-b22-dp-coords.md` §3

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 断塔中层环廊 wave2b 卫尸防坠（刷点内收） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | **仅** `EmberDailySpire/monster.yml` 的 **wave2b** 两条 `$mob` location（默认） |
| 状态 | **已批准方案 A · 待插件施工** |
| 对照 | 原 wave-variance 意图「环廊内侧近阶处」；S3「环廊须有栏/宽台防误坠」；庭院/潮蚀无同构坠崖债 |

### 硬约束

| 可动 | 不可动（硬禁） |
|------|----------------|
| wave2b 卫尸刷点 **内收 1～2 格**（远离崖边） | **door2** `$operation-block` 坐标 / 门宽公式 |
| （备选 B）局部矮栏/半砖沿 — 须另派地图岗且写清坐标块 | **`$kill` 数量 / 品种 / kill-any**；计杀规则 |
| | 链式 delay、wave2a、boss_prep、boss、房1 |
| | **Boss HP / 体力 / 掉落**；MM id / Health |
| | 霜晶 / 锈轨 / 他线 YAML；借机加 prep |

**总量：** 小怪仍 **13**+Boss；零增怪、零裁怪。

---

## 1. 债源 / 现象

Boss前压短抽首跑（`BpS653`）：玩家 **先清对射（wave2a）** 时，环廊 **wave2b 卫尸易坠崖** → `$kill{断塔卫尸;amount=2}` **不满** → **door2 不开**。  
补测改「先清 wave2b」即 PASS——属 **测法债 + 潜在日刷痛点**（乱序清对射时顶门卡住），**非挡级 blocker**，但节奏收口软债#1 仍刺。

**明确反对：** 把 door2 改成 kill-any、降低 kill 计数、挪 door2 触发逻辑糊弄过关。

---

## 2. 现状（现网 · 本稿未改）

来源：`plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml`；`option.yml` spawn `(0,64,0)`；B2.2 断塔表。时点 2026-09-28。

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 断塔卫尸 | 5 | 塔底 (±5,64,±) | ×5 | door1 z=4@y64 → wave2a delay=3 |
| **wave2a** | door1 后 | 裂隙箭骷 | **4** | **(-6,70,0)×2** **(6,70,0)×2** | 裂隙箭骷 ×**4** | **start**→wave2b **delay=2**；end 仅文案+heal，**不开门** |
| **wave2b** | 重叠 | 断塔卫尸 | **2** | **(0,70,-6)×1** **(0,70,2)×1** · scattered=0.5 | 断塔卫尸 ×**2** | door2 **x=-1..1,y=70..72,z=4** AIR×9 → boss_prep delay=2 |
| boss_prep | door2 后 | 断塔卫尸 | 2 | (±3,76,2) | ×2 | → boss delay=2 |
| boss | prep 后 | 断塔守望 | 1 | (0,76,0) | ×1 | COMPLETE |

**链式一句话：** 上环廊即双侧箭 → delay2 卫尸重叠；**仅 wave2b `$kill`×2 开顶门**；再门槛 prep→守望。  
**坠点判断：** `(0,70,-6)` 落在南缘外沿（对门 z=4 的远端），与原设计「环廊**内侧**近阶处」不符，乱序清射时最易被挤/路径掉崖；`(0,70,2)` 已偏近阶侧，风险较低。

---

## 3. 方案 A / B 对比

| 候选 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. wave2b 刷点内收** | 仅改两条 `$mob` location：南缘点向环廊内侧（+z）收 **2** 格；近阶点略收 **1** 格靠廊心/靠墙；MM / `$kill`×2 / delay / door2 / prep **全不动** | 零改图；对齐原「内侧近阶」意图；不碰计杀纪律；改动面最小 | 须施工前核建议格 **可站**；极端击退仍可能坠（可再升 B） | **推荐** |
| **B. 局部矮栏 / 半砖沿** | 在南缘等崖边加矮栏或半砖沿（MCA 小改） | 物理挡坠更硬 | 须改图；施工岗/坐标块须另批；超过「只动 YAML」默认预算 | **备选**（A 验收后仍坠再升） |

**明确否决：** door2→kill-any；降低 `$kill` amount；door2 改挂 wave2a.end / 任意清完即开；大改 MCA 重塑环廊。

**推荐：A。** 坐标允许内收且不动 MCA；只动 Spire `monster.yml` wave2b 两行 location。

---

## 4. 推荐与改动清单（方案 A）

### 4.1 建议坐标（施工前核可站；以实地为准可 ±1 微调，禁外扩回崖）

| 点 | 现网 | 建议内收后 | 意图 |
|----|------|------------|------|
| 南缘卫尸 | `(0,70,-6)` | **`(0,70,-4)`** | +z **2** 格，离南缘崖，仍在南臂廊面 |
| 近阶卫尸 | `(0,70,2)` | **`(0,70,1)`** | −z **1** 格，略靠廊心、远离门框挤位；仍近阶 |

- **不变：** `EmberDailySpireZombie`；`amount=1`×2；`scattered=0.5`；`$kill{断塔卫尸;amount=2}`；wave2a.start→delay=2；door2 九格；boss_prep / boss。  
- **文案：** 默认可不动；若要可加半句「廊内」但不作为硬条。

### 4.2 文件行级（仅 Spire）

- 文件：`plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml`  
- 改：`wave2b` 下两条 `$mob{...location=...}` 的 location 字段（上表）。  
- **不改：** `option.yml`；door2 `$operation-block`；wave2a / boss_prep / boss；他线。

### 4.3 若升方案 B（不默认施工）

- 地图：`ember_daily_spire` 中层 y≈70 南缘外侧沿 `(0,70,-6)` 一带加矮栏/半砖（具体块坐标施工时实地采样后写入 STATUS）。  
- 岗：余烬-地图（MCA）落地；插件岗不单独改门/kill。  
- 触发条件：方案 A 测岗「乱序清对射」仍复现坠杀不满。

---

## 5. 验收硬条（≤5）

1. **乱序清对射仍能开门：** 先清 wave2a 裂隙箭骷、后清 / 夹杂清 wave2b 时，卫尸 **不因坠崖** 导致 `$kill`×2 不满；door2 最终能开。  
2. **房2 链式零回归：** `wave2a.start`→delay=2 `wave2b` 仍在；wave2a.end **仍无** door / 再拉 wave2b；重叠窗可感。  
3. **door2 / `$kill` 纪律零回归：** 开门仍仅挂 `wave2b.end`；`$kill{断塔卫尸;amount=2}` 不变；**无** kill-any。  
4. **boss_prep 零回归：** door2 → prep×2 @ (±3,76,2) → boss；品种/数量/坐标相对批准前基线零非预期 diff。  
5. **误伤面：** 仅 `EmberDailySpire/monster.yml` wave2b location diff；霜晶/锈轨/他线 YAML 零动；Boss HP / 体力 / 掉落零动。

---

## 6. 禁项 / 不动清单

- 禁改 door2 为 kill-any / 降 amount / 挪触发到 wave2a.end  
- 禁改 Boss HP、体力、掉落、通关箱  
- 禁改门宽公式与 door1/door2 `$operation-block` 坐标  
- 禁借机加 / 改 boss_prep；禁动 wave2a 箭骷点  
- 禁动霜晶 / 锈轨 / 庭院 / 潮蚀 / 焦骨 / 地窖  
- 禁默认大改 MCA（B 仅升档且另批）

---

## 7. 施工岗

| 项 | 谁 |
|----|-----|
| 方案 A（推荐） | **余烬-插件** · 仅改 Spire `monster.yml` wave2b location |
| 方案 B（升档） | **余烬-地图** 矮栏/半砖；插件岗配合复测，不改 kill/门 |
| 测岗 | 乱序清对射 + 先清卫尸两条路径；对照验收 §5 |
| 发布 | 总控代推；**审批后再派插件**；本岗可 commit 文档、**勿 push** |

---

## 8. 给总控一句话

**推荐方案 A：** wave2b `(0,70,-6)→(0,70,-4)`、`(0,70,2)→(0,70,1)`；不碰 door2 / `$kill`；批准后再派插件施工。

---

## 9. 总控批注

**批准方案 A**（2026-09-28 Asia/Shanghai · 余烬-总控）。

- 仅内收 wave2b：`(0,70,-6)→(0,70,-4)`、`(0,70,2)→(0,70,1)`；施工前核可站，允许 ±1 微调但禁外扩回崖。
- 禁门/kill-any/降 amount；禁动链式 / door2 / boss_prep / 他线。
- 验收按 §5；A 测后仍坠再升 B（矮栏）另批。
