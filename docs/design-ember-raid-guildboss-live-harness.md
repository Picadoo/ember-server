# 设计稿 · Raid / GuildBoss kill-any live 测法·工具链

> **未批准前不施工。** 玩法 YAML 零改；勿 git push。  
> 债源：`docs/design-ember-content-backlog.md`「仍挂 · Raid/GuildBoss kill-any live 复测」· `STATUS-ember-killany-debt-cleanup-test.md`（静态 PASS · live SKIP）。  
> 对照：消 kill-any 剩债已批 A 落地（`design-ember-killany-debt-cleanup.md` / 施工 `3194355`）；团本使徒 TTK 校准已 PASS、人数门已还原 min=3（`aff517c`/`8b3190f`）。  
> 排除本轮：**不**重开使徒 TTK / HP；**不**开霜晶·锈轨 Boss 前压；**不**升断塔环廊方案 B；**不**动周本 prep B / 精英厅二 B；**不**开空许愿旁支；**不**借机砍 wave amount / 改 condition。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | Raid / GuildBoss · kill-any live 测法·工具链薄窗 |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | **仅**测岗进本工具链 + 测报口径；**不**改 `monster.yml` / option 人数门 / Boss 数值 |
| 状态 | **待批 · 未批准前不施工** |
| 关联 STATUS | `STATUS-ember-killany-debt-cleanup(-test).md` · backlog 仍挂 · 旁证 `smoke-raid-combat-20260926.md` |

### 硬约束（本稿）

| 可动（若批 A） | 不可动（硬禁） |
|----------------|----------------|
| 测岗脚本 / 测报协议 / 临时测服 seed（票、盟、贡献、OP 测毕清） | `EmberRaid` / `EmberGuildBoss` / `EmberAbyss` 的 **monster·option·MM HP/伤/技能** |
| 复用/薄改 `mineflayer-tests/*` 进本 harness（文档化步骤） | 永久改团本人数门 `min=3`；禁借使徒校准窗再降门当「正式测法」 |
| 测报写入 STATUS：live PASS/FAIL + 证据行 | 票价 / 体力 / 掉落 / 同袍 / 贡献消耗数值 |
| | 日常七线 / 周本 / 精英 YAML；未批准前任何玩法 YAML |

---

## 1. 问题一句话

**kill-any 剩债静态已 PASS，但 Raid wave2 / GuildBoss wave1 的 live 双 `$kill` AND 仍 SKIP——卡的是「真进实例」工具链，不是数值超标。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 |
|----|------|
| YAML 债 | Abyss F2/F7 · Raid wave2 · GuildBoss wave1 已改同波双 `$kill` AND（`STATUS-ember-killany-debt-cleanup.md`） |
| 静态验收 | 有效 `$kill-any{` 归零；amount 2+2 / 3+4 / 5+3；双树 identical → **PASS** |
| Live Abyss | F2 / F7 真击杀 **PASS**（同窗测报） |
| Live Raid | **SKIP**：3 bot 已组+给票，**未**出现左/右道横幅；脚本误判进本后 bot 卡墙窒息；「该命令只能在地牢内」→ **未进 EmberRaid 实例** |
| Live GuildBoss | **SKIP**：`/corerpg guild info` →「你不在任何盟约中」→ 无盟不可开 |
| 团本人数门 | `EmberRaid/option.yml` `$team-condition` **min=3 max=5**（使徒校准测毕已还原；对外仍 3～5） |
| 盟 Boss 人数门 | `EmberGuildBoss/option.yml` **min=1 max=5** + `%corerpg_guildboss_pass%` / OP 豁免 |
| 进本主路径（B0.1 后） | `/corerpg enter raid` → 扣票 → `dp start-console`；裸 `/dp start` 对 default **无权限**（`dungeon.start=false`） |
| 既有成功先例 | `mineflayer-tests/raid-combat-smoke.js` + `docs/smoke-raid-combat-20260926.md`：**3 bot + dungeon-team** 曾实战通关（彼时 `/dp start`；**早于** TicketEntry 主路径） |
| 盟 Boss 门控先例 | `guildboss-gate-smoke.js` / `guild-boss-smoke.js`：`guild create` + 贡献 seed + `/corerpg guild boss` 曾验门 |

**解读：** 剩债是 **可测性 / 回归 harness**，不是「双 `$kill` 写错要砍怪」。空砍 amount / 回退 kill-any / 再降人数门 **不还债**。使徒校准已证明单 bot+临时 min=1 能进团本，但 **正式门已还原 3～5**；本窗应用 **真 3 bot 队** 验 wave2，勿把校准降门当长期测法。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 测法·工具链收口（推荐）** | 固化「Raid 三 bot 真进本 + GuildBoss 植盟开本」协议与薄脚本；只验 live 双 `$kill` 过波；**零碰玩法 YAML** | 改动面在测岗/文档；现栈有 dungeon-team + guild seed 先例；关 backlog 仍挂 live；以后团本/盟本回归可复用 | 需测窗排期（约 1 次 Raid 短打到 wave2 + 1 次 GuildBoss wave1）；脚本要对齐 `corerpg enter` / `guild boss` | **推荐** |
| **B. 继续诚实 SKIP** | 静态 PASS 维持；等 3～5 真人团 / 真人盟约再采 live | 零脚本工时；正式体验一字不改 | live 债无限挂；每次「杀怪债复测」重复卡同门；可维护性不涨 | 备选 |

### 方案 A · 施工清单（批准后 · 薄 · 仅测岗）

#### A1 · Raid wave2 live（人数门保持 3～5）

1. **组队：** 3 非 OP bot；`/dungeon-team create` → invite → `request join`（对齐 `raid-combat-smoke.js`）。  
2. **进本：** 队长走 **`/corerpg enter raid`**（或菜单等价）；全队持 `ticket_ember_raid`；**禁**依赖永久 `min=1`；**禁**教玩家裸 `/dp start`。  
3. **进本硬证（缺一即 FAIL，不得当 PASS）：**  
   - 聊天出现团本集结/左道或右道就位类横幅；**或**  
   - 三 bot 世界名含 `dungeon_EmberRaid_` / 坐标落入通道区；  
   - **不得**仅凭「菜单点了 / 无拒进句」判进本（对照 killany 测窗卡墙假阳性）。  
4. **wave2 验收：** 通道卫兵×3 + 通道射手×4 均清后出现「通道打开」（或等价下一波横幅）；**只清一类不得过波**（抽检：留射手清卫兵 → 仍铁门/无汇合）。  
5. **可早停：** 证明 dual `$kill` 后可 `dp leave` / 踢本，**不必**再打使徒（使徒 ΔTTK 已关账）。

#### A2 · GuildBoss wave1 live（min=1，卡点是盟）

1. **植盟：** 测服 `/corerpg guild create …`（或既有测盟）；队长+可选队员；贡献 seed 至可开本（对齐 `guildboss-gate-smoke` 门槛，**不改贡献数值表**）。  
2. **开本：** 队长 `/corerpg guild boss`（扣贡献 → console `dp start-console`）；确认 `guildboss_pass` 路径，非裸 `/dp start`。  
3. **wave1 验收：** 潮尸×5 + 骨潮×3 双 `$kill` AND 过波（横幅/进下一波）；只清一类不过。  
4. **测毕：** 贡献/周次按既有退还或测服重置纪律；ops=`[]`。

#### A3 · 交付物（无玩法 diff）

- 薄脚本（新建或从 `raid-combat-smoke` / `guildboss-gate-smoke` **分叉短打到目标波**）落 `mineflayer-tests/` 或 `/tmp`+STATUS 引用。  
- `STATUS-ember-killany-live-retest.md`（或更新既有 killany STATUS）：Raid live / GuildBoss live 各 PASS|FAIL + 证据时刻。  
- **玩法树 `git diff` 对 monster/option/MM 必须为空。**

### 方案 B · 若总控选 B

STATUS 明文「本轮选 B · live 继续 SKIP · 静态仍算 kill-any YAML 债已还」；**不得**把 SKIP 写成 PASS。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；`EmberRaid`/`EmberGuildBoss` 玩法 YAML **未改**（本 commit 仅文档）。  
2. **若批 A · Raid：** 3 bot 真进实例（横幅或世界名证据）+ wave2 双 `$kill` 过波证据写入 STATUS；人数门测后仍 **min=3**。  
3. **若批 A · GuildBoss：** 有盟开本 + wave1 双 `$kill` 过波证据；测后 ops 空、无残留永久 pass。  
4. **若批 A · 禁区：** monster/option/MM Health 相对 tip **零 diff**；未借机改票/贡献/掉落。  
5. **若批 B：** STATUS 保持 live SKIP，并写明总控选 B——**不得**报 live PASS。

---

## 5. 禁项清单

- 禁改 `EmberRaid` / `EmberGuildBoss` / Abyss 的 **condition amount、组名、刷点、HP、技能**（无超标证据不砍）  
- 禁永久 `min=1` 当正式测法；使徒校准降门协议 **已关账，不重开**  
- 禁把裸 `/dp start` 写回玩家路径或 default 权限  
- 禁 Citizens；禁空许愿旁支  
- 禁顺手开霜晶·锈轨前压、断塔方案 B、周本 prep、精英厅二、B0.4 数值施工  
- 禁 git push；未批准前禁动玩法 YAML

---

## 6. 站岗 / 动线（本债无新锚）

无新 NPC / 菜单。玩家正式动线不变：

| 本 | 玩家路径 |
|----|----------|
| 团本 | 枢纽 → 团本菜单 → **3～5 人组队** → enter → 通道 → 终厅 |
| 盟 Boss | 盟约 → 队长 `guild boss`（贡献）→ 1～5 人实例 |

测岗动线（仅批 A 后）：测服 seed 票/盟 → dungeon-team / guild boss → 打到目标波 → 写 STATUS → 清 OP/队。

| UX 硬条 | 判定 |
|---------|------|
| 零手打指令（玩家路径） | **N/A→不改玩家路径** |
| 功能锚可点 | **N/A**（无新锚） |
| 占位有债务里程碑 | **PASS**：债务名「Raid/GuildBoss kill-any live」；还债里程碑 = 批 A 双本 live 证据（或批 B 明文挂账） |

---

## 7. 为何选这扇窗（给总控）

| 候选 | 本轮 | 一句否因 |
|------|------|----------|
| **本窗 · Raid/GuildBoss live 测法·工具链** | **选** | 仍挂实债；静态已 PASS；卡点可测可维护；改动面在脚本不在数值；不与刚收口使徒 TTK 抢同杠杆 |
| B0.4 挂机二档数值 | 未选 | 文档债已闭环；**无可新给的可施工薄标定**——缺 2h 同账号产出基线，再写曲线稿=重复 `design-ember-afk-b04-softcap.md`；诚实等经济窗 + 数据后再批 B1 |
| 使徒 TTK / HP | 排除 | 刚 PASS（Δ=+10.83%）；禁重开 |
| 霜晶/锈轨 Boss 前压 | 排除 | 设计刻意；口碑未点名 |
| 断塔环廊方案 B | 排除 | 仍软观察；无新坠落产品证据 |
| 周本 prep / 精英厅二 | 排除 | 已批 B 不施工 |
| B1.3 精英 Boss TTK | 未选 | 属战斗数值精调；优先级低于「先打通多人/盟本 live 回归门」；且易与刚收口 TTK 窗抢测力 |

---

## 8. 推荐摘要（可转发）

**推荐方案 A（固化 Raid 三 bot + GuildBoss 植盟的 live 测法·工具链，只验双 `$kill` 过波，玩法 YAML 零改）。**  
一句话理由：把「静态已还、live 永久 SKIP」收成可复用回归门，不砍怪、不降正式人数门、不重开使徒校准。

**未批准前不施工。**

---

## 总控批注

（待填）
