# 设计稿 · B1.3 精英 Boss TTK 关账采数

> **已批准方案 A（总控 · 2026-09-28）。** 测岗关账采数；默认 MM/DP **零改**；出窗只记数，微抬须另批。  
> 债源：`docs/design/design-ember-content-backlog.md` **B1.3** · `docs/status/STATUS-ember-elite-weekly-4.4.md` §7「Boss 数值初值需 bot 再调」· 上一窗未选语：「数值精调，先打通多人 live」。  
> 前置已结：Raid/GuildBoss kill-any live **PASS**（`docs/status/STATUS-ember-killany-live-retest.md` · tip `87b9193`/`7c8e61b`）；团本使徒校准 **PASS**（禁重开）。  
> 排除本轮：霜晶/锈轨前压；断塔方案 B；已批 B 不施工（周本 prep / 精英厅二）；空许愿；**再开 kill-any YAML** / 重开 `docs/design/design-ember-raid-guildboss-live-harness.md`。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | B1.3 · 精英试炼 Boss TTK **关账采数**（默认定数 · 非砍血） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | `EmberEliteWeekly` + MM `EmberEliteBoss`；对照 4.4f / B1.3 图修复测 |
| 状态 | **已批准 A · 待测岗关账采数** |
| 关联 STATUS | `docs/status/STATUS-ember-elite-weekly-4.4.md` · `docs/status/STATUS-ember-b13-elite-map-check.md` · `docs/status/STATUS-ember-b13-elite-retest.md` · `docs/status/STATUS-ember-elite-weekly-4.4-close.md` |

### 硬约束（本稿）

| 可动（若批 A） | 不可动（硬禁） |
|----------------|----------------|
| 测岗 bot 通关采数 + 写入 STATUS | **默认**改 `EmberEliteBoss` / 波次怪 **Health · Damage · Pulse · Rush · Skills** |
| 测报口径表（TTK / 全本 / 剩血 / 死亡） | 票价 / 周首通稳定符规则 / 掉落箱 / 体力 |
| 若**出窗**且总控另批：才开怪物微抬（另 STATUS，非本窗默认） | 日常七线 / 周本 / 团本 / 盟本 YAML；人数门；kill-any |
| | 未批准前任何玩法 YAML；git push |

---

## 1. 问题一句话

**B1.3「精英 Boss 初值待 bot 精调」仍挂在 backlog，但图修后单次复测已落进设计窗——缺的是正式关账采数口径，不是先砍 HP。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 |
|----|------|
| 设计窗（stage4.4） | 全本 **8～12 分（480～720s）**；Boss TTK **45～75s**；结束剩血 **25～55%**；单人可过（人数 1～2） |
| MM `EmberEliteBoss` | Health **5200** / Damage **12**；Pulse **100**；Rush **200**（`EmberEliteWeekly.yml` 注释：**4.4f 禁止改**） |
| 波次 HP（4.4f 锁） | Z5250 / Sk3300 / Mix2700 / Brute10500 · 伤 4/4/4/5 |
| 4.4f 收口（挂 `ember_weekly` 时） | 全本 **510s** · Boss **70s** · 有效剩血 **34%** → **PASS 锁数**（`docs/status/STATUS-ember-elite-weekly-4.4-close.md`） |
| P3 后 | 独立图 `ember_elite`；厅三东门曾通 scrub 崖 → Boss 失联；**非削血可解** |
| B1.3 图修 | 封东门 + 东护台（CoreRpg 1.15.10 · `docs/status/STATUS-ember-b13-elite-map-check.md`） |
| B1.3 图修后复测 | 通关 **594s PASS** · Boss TTK **63s PASS** · 剩血快照 **58%** 略超 25–55 · 死亡 1/1 · **未改数**（`docs/status/STATUS-ember-b13-elite-retest.md`） |
| 进本 | `/corerpg elite start` 或菜单「精英试炼」；超时 **720s**；Lv≥40 · 周票 1 · 本周未通 |
| 对照装 | 4.4f / B13R：Lv42 · 烬刃 T2+7 Sharpness3 · 护符 T2+4 · 无战斗 buff |

**解读：**

1. 「初值 2800/4 待调」是 stage4.4 **落地板**；现网已是 **4.4f 锁数 5200/12**，再写「按初值精调」会误导砍已验收锁。  
2. P3 后 FAIL 根因是 **图**，不是血；图修后 Boss TTK / 全本已回窗。  
3. 剩血 58% 为脚本硬条略超；B13 测报已建议「观察复活灌血 / 或放宽 ≤60%」，**勿借此削 Boss**。  
4. 上一窗未选 B1.3，是因「先打通多人 live」；**live 门已 PASS** → 本轮可评估关账。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 关账采数（默认不动 HP）** | 测岗按下方口径跑 **≥1** 次正式通关（建议同装再跑 1 次复核）；写入 `docs/status/STATUS-ember-b13-elite-ttk-close.md`；**窗内 → 勾销 B1.3**；**仅当 Boss TTK 或全本严重出窗**才另开怪物微抬（须总控二次批，本稿不预授权改数） | 可测、改动面在测报；现栈单人精英即可派；与 4.4f 锁数纪律一致；真关 backlog | 需 1 次测窗（约 10～12 分战斗）；剩血口径需在 STATUS 写清软条 | **推荐** |
| **B. 本轮挂** | 维持 backlog B1.3 仍挂；承认图修复测为旁证但不关账 | 零测力 | 债名与事实脱节（TTK 已像关账）；下轮仍占名额 | 备选 |

### 方案 A · 采数口径（批准后 · 测岗）

#### A1 · 装等与环境（对齐 4.4f / B13）

| 项 | 要求 |
|----|------|
| 账号 | 非 OP 主测；临时 OP 仅 seed 票/等级，测后 `ops=[]` |
| 等级 | 余烬 **Lv40～45**（门=40；对照档 ~42） |
| 装 | 烬刃 T2 **+6～+8** · Sharpness≥III · 护符 T2+4 量级 |
| 誓约/天赋 | 一层满即可（**不强制** L1 vs L2 双跑；B0.2 周本/深渊/使徒已 PASS，本窗不绑二层 Δ） |
| buff | **`/effect clear`**；禁 strength/resistance 战斗药 |
| 进本 | **`/corerpg elite start`**（或菜单等价）；持 `ticket_ember_elite`；禁教玩家裸 `/dp start` |
| 图 | 确认厅三东门仍为实墙（抽检可 `testforblock`）；预缓存与模板一致 |

#### A2 · 时钟与指标

| 指标 | 窗 | 记法 |
|------|----|------|
| **Boss TTK** | **45～75s** | 横幅「试炼终：烬纹执行官」→「试炼通过」/ Boss 死亡 |
| **全本** | **480～720s** | 进本成功 → COMPLETE |
| **通关前剩血%** | **硬观察 25～55%**；**软放宽 ≤60%** 若死亡复活后灌血导致略超（须在 STATUS 注明死亡次数与是否复活灌血） | 通关瞬间有效 HP/最大 HP；忌把波末 `instant_health` 后满血当「战中压力」 |
| 死亡/复活 | 记录 | 不单独硬 Fail（4.4f / B13 均为 1/1） |
| 掉崖/失联 | **0** | 若再现东崖失联 → **图债**，禁砍 HP |

#### A3 · 关账判定树（写进 STATUS）

```
若 Boss TTK ∈[45,75] 且 全本 ∈[480,720] 且 无掉崖失联
  → B1.3 **勾销**；MM 保持 5200/12；表述「图修后关账采数 PASS，非再精调砍血」
  → 剩血 25～55 PASS；或 56～60 且有复活灌血说明 → **软 PASS 不挡关账**
  → 剩血 >60 且无合理解释 → 记观察，仍可不改 Boss HP（另议难度叙事）

若 Boss TTK <45 或 >75，或 全本出 480～720，或掉崖失联
  → **本窗只记数不改数**；出 STATUS 证据
  → 掉崖 → 回图/插件岗，禁止怪物岗削血冒充修复
  → TTK/全本数值出窗 → 总控另批「怪物微抬/微削」窗（须明示动哪几个字段）；**禁止**借本关账窗静默改 YAML
```

#### A4 · 交付物

- `docs/status/STATUS-ember-b13-elite-ttk-close.md`（或等价名）：表格式 TTK/全本/剩血/死亡 + 关账判定  
- 可选：复用/薄改既有 `ONLY=elite` 脚本路径写入 STATUS  
- **`git diff` 对 `EmberEliteWeekly` monster/option + `EmberEliteWeekly.yml` 必须为空**（批 A 默认）

### 方案 B · 若总控选 B

STATUS/backlog 明文「本轮选 B · B1.3 继续挂 · 图修复测仅旁证」；**不得**把旁证写成「已精调 PASS」关账。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；精英 MM/DP **未改**（本 commit 仅文档）。  
2. **若批 A · 采数：** STATUS 含 Boss TTK、全本秒、剩血%、死亡、进本路径证据（时刻 CST）。  
3. **若批 A · 关账：** 判定树结论写清「勾销 / 只记数」；勾销时 backlog B1.3 可标已结。  
4. **若批 A · 禁改：** 相对 tip，`EmberEliteBoss` HP/伤/Pulse/Rush 与波次 HP **零 diff**（除非总控**另批**微抬并单列 commit）。  
5. **若批 B：** backlog 保持 B1.3 仍挂，并写明选 B——**不得**报精调 PASS。

---

## 5. 禁项清单

- 禁默认改 `EmberEliteBoss` / 波次怪 Health·Damage·Skills·Pulse·Rush（4.4f 锁）  
- 禁改票、周首通稳定符、掉落箱、体力、等级门  
- 禁借关账重开 kill-any / 厅二链式 / 周本 prep / 霜晶·锈轨前压 / 断塔 B  
- 禁把掉崖失联当「HP 太高」砍血  
- 禁 Citizens；禁教玩家手打 `/dp start`  
- 禁 git push；未批准前禁动玩法 YAML

---

## 6. 站岗 / 动线（无新锚）

无新 NPC / 菜单。玩家动线不变：枢纽 →「精英试炼」→ 厅一词缀 → 厅二蛮压 → 厅三执行官 → 回枢纽。

| UX 硬条 | 判定 |
|---------|------|
| 零手打指令（玩家路径） | **N/A→不改玩家路径** |
| 功能锚可点 | **N/A**（无新锚） |
| 占位有债务里程碑 | **PASS**：债务名 B1.3 精英 Boss TTK；还债里程碑 = 批 A 关账采数 STATUS（或批 B 明文挂账） |

---

## 7. 为何选这扇窗（给总控）

| 候选 | 本轮 | 一句否因 |
|------|------|----------|
| **本窗 · B1.3 精英 Boss TTK 关账采数** | **选** | live 门已通；上一窗明确让路；现网单人可派；默认 YAML 零改即可关实名债；证据链（4.4f+图修）已齐，差正式关账口径 |
| B0.4 挂机二档数值 | **未选 · 继续挂** | 文档债已闭环（`docs/status/STATUS-ember-afk-b04-debt.md`）；**仍无 2h 同账号产出基线**——硬写经济标定=空转重复 `docs/design/design-ember-afk-b04-softcap.md`；等经济窗+数据后再批 B1 |
| 使徒 TTK / 降门 | 排除 | 刚 PASS；禁重开 |
| Raid/GuildBoss kill-any live / harness | 排除 | 刚 PASS；禁再开 YAML/重开测法稿 |
| 霜晶/锈轨 Boss 前压 | 排除 | 设计刻意；口碑未点名 |
| 断塔环廊方案 B | 排除 | 仍软观察；无新坠落产品证据 |
| 周本 prep / 精英厅二 | 排除 | 已批 B 不施工 |
| B0.1 票 NI id | 未选 | 插件大面；且进本主路径已走 NI consume（非本薄窗） |

---

## 8. 推荐摘要（可转发）

**推荐方案 A（B1.3 关账采数：同装单人通关记 Boss TTK/全本/剩血；默认不动 5200/12，窗内勾销债；出窗只记数另批）。**  
一句话理由：多人 live 已通，精英 TTK 差的是正式关账不是砍血——先采数锁结论，避免把已 4.4f 锁的 Boss 再当「初值」误削。

**未批准前不施工。**

---

## 总控批注

**批准方案 A**（2026-09-28 Asia/Shanghai · 余烬-总控）。

- 测岗：同装单人 `/corerpg elite start` 通关；记 Boss TTK / 全本 / 剩血 / 死亡。
- 窗内（TTK 45～75 · 全本 480～720 · 无掉崖）→ 勾销 B1.3；**保持 5200/12**。
- 剩血 56～60 且有复活灌血说明 → 软 PASS 不挡关账。
- 出窗或掉崖 → 只记数；禁静默改 YAML；掉崖回图岗。
