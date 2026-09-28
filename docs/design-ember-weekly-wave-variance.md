# 设计稿 · 周本深核廊波次差异（中核链式重叠）

> 债源 / 派工：总控【周本深核廊波次差异薄设计】priority=true  
> 背景：日常七线房2节奏扩线 + 霜晶房2链式已收口 PASS；周本 `EmberWeekly` 仍是 **wave1→delay→wave2→delay→wave3→Boss** 等清骨架，软尾巴风险高于日常。  
> 对照：`design-ember-daily-ash-room2.md` / `design-ember-daily-room2-variance.md` / 霜晶房2；链式先例断塔/焦骨/霜晶。  
> **已批准（总控 2026-09-28）：** 方案 A 中核 start 链式；**仅** `EmberWeekly/monster.yml`；**EmberEliteWeekly 本轮不扩**；备选前压不施工。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 / 功能名 | 周本 · 深核廊波次差异（中核房内 start 链式重叠） |
| 负责人 / 日期 | 余烬-策划 / 2026-09-28（Asia/Shanghai） |
| 关联世界 / 地图 | `ember_weekly`（现网三室几何，不新挖房） |
| 目标验收里程碑 | 体验迭代（不挡可玩；挡「周本仍纯等清三段」） |
| 当前状态 | **已批准 · 本额度施工** |
| 关联实现 / STATUS | P3 三室：`STATUS-ember-weekly-elite-maps-p3.md`；日常软尾巴收口：`STATUS-ember-daily-room2-expand-closeout-review.md`；精英本轮不扩 |

### 硬约束（本条）

| 可动 | 不可动 |
|------|--------|
| **仅** `EmberWeekly/monster.yml` 中核 wave2 时序（拆 `wave2a`/`wave2b` 链式）及文案 | ≥现网 **三室** 结构；波末 teleport 落点 `(-40,68,308)` / `(-40,63,340)` |
| 组内 delay、start 链式 `$monstergroup`；按组独立 `$kill` | Boss / 小怪 **HP**；体力 / 票消耗；通关奖励脚本 / 掉落表 |
| Display 文案（重叠体感） | Display **去色名**（`$kill` 对齐）；`option.yml` / `obstacle.yml` / `task/` |
| | **`$kill-any`**（本轮顺带消掉中核现网 kill-any，禁止新建） |
| | `EmberEliteWeekly`（本轮不扩，见 §3.3）；`ember_weekly/` 小写空壳 |
| | 新 MM id；跨组合并 `$kill`；盲目加血 |

**总量：** 小怪仍 **8**（尸 3+2 + 骷 3）+ 蛮兵·甲×1 + 蛮兵·乙×1；**零增怪**。熟手仍约合理周本时长（重叠压不拉长等清空窗；不堆 prep）。

---

## 1. 问题一句话

日常房2/前压已打断「两段等清」复印机；周本深核廊仍是 **前厅清完传送 → 中核一团清完传送 → 甲 → 等 delay → 乙**，宏观时间轴比日常更「软尾巴」。

---

## 2. 现网一句话 vs 改后体感

| | 内容 |
|--|------|
| **现网** | 前厅尸×3 清完 → delay3 传中核 → **尸×2+骷×3 同刷同清（kill-any×5）** → delay3 传深室 → 甲 → delay4 → 乙 |
| **改后体感** | 前厅不变 → 进中核先打尸，**约 2s 内骷已远程重叠压上** → 两组都清完才传深室 → 甲→乙原样；玩家能感到「中核不是一团糊完」 |

玩家可感知的时间轴差异：**中核由「同时糊一团」变为「近战先压 + 远程交错重叠」**；前厅 / 深室双蛮兵链不动。

---

## 3. 杠杆比选（推荐结论前置）

| 候选 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 中核房内 start 链式重叠** | 拆 wave2 → `wave2a` 尸×2 + `wave2b` 骷×3；`wave2a.start`→delay2 `wave2b`；**传送挂 wave2b.end** | 直打中核软段；对齐断塔/焦骨/霜晶 PASS；**零增怪**；顺带消 **kill-any** | 与日常同杠杆族（但周本中核 = 近战台+远程点，手感可分） | **推荐 · 主杠杆** |
| B. 中室门槛前压 | wave1 传中核后先 `mid_prep`×1～2 再进主波 | 对齐庭院/潮蚀「门槛」叙事 | 须 +怪或裁前厅；中核已有混合内容，再叠 prep 易挤；时长风险高于链式 | **备选**（见 §6.5） |
| C. 深室甲前 / 甲→乙前压 | 传深室后 prep，或缩短甲→乙空窗并加小怪 | 动终局 | 易冲双蛮兵记忆；动 Boss 邻域风险高；与派工「中室」优先级不符 | **不优先** |
| D. 只改文案 / 刷点 | 不动时序 | 改动极小 | **不打断时间轴** | 否 |

**推荐：A · 中核房内 start 链式重叠。**  
理由：派工优先「房内链式 / 中室」；日常同杠杆已 PASS；零增怪保周本时长；现网中核 `$kill-any` 本就违反日常已立的 `$kill` 纪律，拆组后一次清债。

### 3.3 精英周本是否本轮扩？

**本轮不扩 `EmberEliteWeekly`。**  
理由：精英独立 map（`ember_elite` · +X 轴）、厅结构与 kill 条件不同；同杠杆需另开组表与测面，**非「极便宜」**；周本先单点验证后再议精英。

---

## 4. 站岗表（功能锚）

本条 **无新入口 / 无新 NPC**；玩家路径仍走现网周常菜单 → `EmberWeekly`。

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 周常选本（既有） | 既有枢纽锚 | — | `ember_hub` | 既有 TrMenu 周常 | 无 | 本条不改菜单 |
| 深核廊战斗节奏 | — | — | `ember_weekly` 内（见 §5/§6） | 无（进本后自动波次） | 波末 teleport 语义不变 | 专岗只改 `EmberWeekly/monster.yml` |

**站岗表自检：** 无新锚；不伪报场景完成；批准后施工 + 抽检。

---

## 5. 调研摘要（现网事实）

调研时点：2026-09-28 Asia/Shanghai；**本稿未改 YAML**。

### 5.1 目录与命名

| 路径 | 角色 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberWeekly/` | **现网周本**（monster / option / obstacle / task/timeout） |
| `plugins/DungeonPlus/dungeon/ember_weekly/` | OP 地图编辑空壳（无怪）；**勿当施工目标** |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/` | 精英试炼（本轮不扩） |
| `plugins/MythicMobs/Mobs/EmberWeekly.yml` | 周本 MM 口径 |

### 5.2 室结构（无铁栅真门 · 以 teleport 分室）

现网 **无** `$operation-block` 铁栅门；分室靠波末 `$teleport`。硬约束「≥现网真门/室结构」本条解读为：**保留三室 + 两道波末传送落点**，不删室、不改落点坐标。

| 室 | 脚 Y | 关键坐标 | 触发 |
|----|------|----------|------|
| spawn / 前厅入口 | 65 | `(-40,65,270)` | `option` setspawn / dungeon-start |
| **前厅** wave1 | 65 | 刷点 `(-40,65,280)` | 清完 → tp **中核** `(-40,68,308)` |
| **中核** wave2 | 68 | 尸 `(-40,68,308)` · 骷 `(-42,68,312)` | 清完 → tp **深室** `(-40,63,340)` |
| **深室** wave3 / boss | 63 | 甲 `(-40,63,340)` · 乙 `(-40,63,342)` | 甲→乙 delay4 → COMPLETE |

来源：`EmberWeekly/monster.yml` + `STATUS-ember-weekly-elite-maps-p3.md` §2。

### 5.3 现网组表 / MM 真名 / 数量

| 组 | 时机 | MM id | Display 去色名 | 数量 | 刷点 | `$kill` | 下一段 |
|----|------|-------|----------------|------|------|---------|--------|
| **wave1** | 进本 delay2 | `EmberWeeklyZombie` | 深核廊僵尸 | **3** | `(-40,65,280)` | `$kill` ×3 | tp 中核 + wave2 delay=**3** |
| **wave2** | 中核 | Zombie×2 + Skeleton×3 | 深核廊僵尸 / 深核廊骷髅 | **2+3=5** | `(-40,68,308)` / `(-42,68,312)` | **`$kill-any` ×5**（债） | tp 深室 + wave3 delay=**3** |
| **wave3** | 深室 | `EmberWeeklyBruteA` | 深核廊蛮兵·甲 | **1** | `(-40,63,340)` | `$kill` ×1 | boss delay=**4** |
| **boss** | 甲后 | `EmberWeeklyBruteB` | 深核廊蛮兵·乙 | **1** | `(-40,63,342)` | `$kill` ×1 | COMPLETE |

小怪合计 **8** + 甲 + 乙。超时 `task/timeout.yml`：**1500s**（本轮不动）。

### 5.4 MM 口径（禁止本轮改 Health / 掉落技能）

| MM id | Type | Display | Health | 备注 |
|-------|------|---------|--------|------|
| `EmberWeeklyZombie` | ZOMBIE | 深核廊僵尸 | **110** | 近战 |
| `EmberWeeklySkeleton` | SKELETON | 深核廊骷髅 | **70** | **BOW** 远程 |
| `EmberWeeklyBruteA` | ZOMBIE | 深核廊蛮兵·甲 | **350** | 深室第一蛮 |
| `EmberWeeklyBruteB` | ZOMBIE | 深核廊蛮兵·乙 | **1500** | 终局；禁动 |

### 5.5 option / 票体力（不动）

- map `ember_weekly` · spawn `(-40,65,270)` · 人数 1～3 · 等级门 Lv.20  
- 票：CoreRpg `ticket_ember_weekly`（option 注释；**不在本文件扣**）  
- 进本文案含「已消耗体力 ×1」；奖励脚本 NI / `corerpg loot weekly_t1` / progress / `mvtp ember_hub` —— **一律不动**

### 5.6 既有设计 / STATUS

- 无单独「周本波次差异」旧稿；P3 只落三室几何与刷点。  
- 日常软尾巴杠杆族已齐（前压 / 链式）；本稿把 **链式** 迁到周本中核单点。

---

## 6. 可执行方案（推荐 · 中核链式重叠）

约定：

- `delay` 单位秒；`$kill` **按 monstergroup 独立**；禁止 `kill-any`、禁止跨组合并数量。  
- 链式先例：断塔 / 焦骨 / 霜晶（`start` 拉下一组；**末组**才推进门/传送/Boss）。  
- **wave1 / wave3 / boss / teleport 坐标 / MM / option 一律不动。**

### 6.1 组表（相对现网，只改中核）

| 组 | 时机 | MM | 数量 | 刷点 | `$kill`（按组） | 门 / 链 / 传送 |
|----|------|-----|------|------|-----------------|----------------|
| wave1 | **不动** | 僵尸 | 3 | `(-40,65,280)` | ×3 | tp `(-40,68,308)` → **wave2a** delay=3（原调 wave2 改为调 wave2a） |
| **wave2a**（新拆） | 入中核后 | `EmberWeeklyZombie` | **2** | `(-40,68,308)` | 深核廊僵尸 ×**2** | **start** → `$monstergroup{wave2b;delay=2}`；**end 不传深室、不调 wave3**（仅提示+heal） |
| **wave2b**（新拆） | wave2a start 后 delay **2s** | `EmberWeeklySkeleton` | **3** | `(-42,68,312)` | 深核廊骷髅 ×**3** | **end**：heal + tp `(-40,63,340)` + `$monstergroup{wave3;delay=3}` |
| wave3 | **不动** | 蛮兵·甲 | 1 | `(-40,63,340)` | ×1 | boss delay=4 |
| boss | **不动** | 蛮兵·乙 | 1 | `(-40,63,342)` | ×1 | COMPLETE |

小怪合计仍 **3+2+3=8**（零增怪）。原 wave2 组名废弃（由 2a/2b 取代）。

### 6.2 链式写法（施工备忘 · 对齐焦骨/断塔）

```
wave1.end:     （其余不动）$monstergroup{group=wave2a;delay=3}   # 原 wave2 → wave2a
wave2a.start:  message（中核·重叠压）+ $monstergroup{group=wave2b;delay=2}
wave2a.end:    仅「中核尸侧已清」类提示 + heal；禁止 teleport / wave3 / boss
wave2b.start:  message（骷重叠压上 · 清完传深室）
wave2b.end:    heal + $teleport{location=-40,63,340} + $monstergroup{wave3;delay=3}
```

**禁止：** `$kill-any`；把尸×2 与骷×3 合并进同一 condition；wave2a.end 传深室。

### 6.3 建议文案（可施工微调，须保留可测关键词）

| 节点 | 文案意图 |
|------|----------|
| wave2a.start | `【深核·中核】僵尸贴上 —— 骷髅将交错压上` |
| wave2a.end | `中核尸侧已清`（**不要**写传送） |
| wave2b.start | `【深核·中核】骷髅重叠压上 —— 清完进入深室` |
| wave2b.end | `中核已清 · 转入深室`（可保留） |

### 6.4 与前厅 / 深室如何叠加不冲掉

| 段 | 记忆点 | 本轮 |
|----|--------|------|
| 前厅 | 尸×3 清杂再传中核 | **零 diff**（仅 end 组名 wave2→wave2a） |
| 中核 | （新）尸与骷 **时序重叠**；分 `$kill` | 只改拆组+链；数量与坐标保留 |
| 深室 | 甲 → delay4 → 乙 | **零 diff** |

时间轴目标：**前厅清杂 → 中核重叠压 → 双蛮兵终局**，而非三段等清亲戚。

### 6.5 备选（不推荐本轮）：中室门槛前压

若总控驳回链式、改批前压，最小草案（**默认不施工**）：

- wave1 end：tp 中核后 → `mid_prep` delay=2（骷×1 @ `(-42,68,308)` 门槛侧）→ 再调原 wave2（或瘦身主波）  
- 保合计 ≤8：须裁 wave1 3→2 或 wave2 骷 3→2；且 **仍须消 kill-any**（拆 `$kill`）→ 改动量 ≥ 链式且多一组  
- 故仍推荐 A。

---

## 7. 动线（差异段）

入口 / 回枢纽与现网周常相同。下表只写节奏差异段。

| 步骤 | 世界 / 区域 | 玩家看到什么 | 发生什么 | 失败 / 撤离 |
|---|---|---|---|---|
| 前厅（不动） | `ember_weekly` Y65 | 僵尸×3 | 清完 tp 中核 | 超时/撤离沿用现网 |
| **中核重叠（本条）** | Y68 下界砖台 | 尸在场约 2s 内骷已压上 | 链式；末组才 tp 深室 | 同上 |
| 深室（不动） | Y63 | 甲 → 乙 | 双蛮兵链 | 同上 |

### 地图与观感

- **独立 map：** `ember_weekly` 既有；**不新挖**。  
- **刷点：** 默认现网坐标。  
- **玩法可测 vs 视觉：** 本条只验收节拍；地图视觉沿用 P3。

---

## 8. 专岗分工（批准后；策划不施工）

| 专岗 | 交付 |
|------|------|
| **余烬-插件（DP）** | 改 **仅** `plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml`：wave2 拆 2a/2b 链式；wave1.end 改调 wave2a；消 kill-any；**禁止**改 teleport 坐标 / wave3 / boss |
| **余烬-怪物（MM）** | 核口径：沿用 `EmberWeekly*`；Display 去色名不变；**禁止**涨 Health / 改死亡掉落 |
| **地图** | 不要求（坐标沿用）；仅当刷点无站位时补 1～2 格 |
| **余烬-测试** | 周本打一遍：§10 硬条；重叠窗可测；wave2a.end 无 tp；深室甲乙链不变 |
| **余烬-策划** | 本稿；批准前 YAML 零改；勿 commit |

**TrMenu / 精英 / 日常七线：** 本轮禁止触碰。

---

## 9. 不动清单

| 项 | 说明 |
|----|------|
| 体力 / 周票 | 消耗与发放逻辑不动 |
| 通关奖励 / 掉落 | `option` reward 脚本；MM `~onDeath` 掉落不动 |
| Boss / 小怪 HP | 甲 350 / 乙 1500 / 尸 110 / 骷 70 **禁止上调** |
| 三室结构 / teleport 落点 | 仍前厅→中核→深室；坐标见 §5.2 |
| wave1 / wave3 / boss 组内容 | 数量、刷点、`$kill`、甲→乙 delay4 **零 diff**（wave1 仅 end 组名指向） |
| `EmberEliteWeekly` | **本轮不扩** |
| `ember_weekly/` 小写空壳 | 不改 |
| 日常七线 YAML | 零 diff |
| 新 MM id / 新 NPC / 新房间 | 不要求 |
| `option.yml` / `obstacle.yml` / `task/timeout.yml` | 不动 |
| `$kill-any` | 禁止保留或新建（中核改为分组 `$kill`） |

---

## 10. 验收硬条（草案）

| # | 硬条 | PASS 标准 |
|---|------|-----------|
| 1 | 骨架 | 仍：选本 → 前厅 → tp 中核 → tp 深室 → 甲 → 乙 → COMPLETE；传送落点坐标不变；玩家零指令 |
| 2 | 中核重叠 | 尸刷出后 **约 2s 内** 骷已在场（无需等尸全清）；**仅** wave2b `$kill` 完成后才 tp 深室 |
| 3 | `$kill` 纪律 | 无 kill-any；wave2a / wave2b 独立 `$kill`；wave2a.end **无** teleport / wave3 |
| 4 | 数值边界 | 无加血；体力/票/奖励/掉落未改；小怪仍 **8** + 甲 + 乙；熟手时长仍合理（无明显无意义空等拉长） |
| 5 | 范围 | 仅 `EmberWeekly/monster.yml` 预期 diff；精英 / 日常 / option / MM Health **零 diff** |
| 6 | 口述可辨 | 熟手口述 ≠「中核还是一坨清完就传」；能感到尸↔骷重叠压，且仍记得甲→乙 |

---

## 11. UX 否决条

| 硬条 | PASS / FAIL | 证据或债务说明 |
|---|---|---|
| **零手打指令** | PASS | 沿用周常菜单进本；波次自动 |
| **自定义物均为 NI ID** | PASS | 本条不改掉落；奖励脚本既有 NI |
| **功能锚可点** | PASS（既有） | 无新锚；不宣称新场景完成 |
| **实景地图** | PASS（既有） | 不新挖；链式用现中核几何 |
| **换皮不算视觉 PASS** | PASS | 本条验收节拍非换皮地图 |
| **占位有名字、有债务、有里程碑** | PASS | 无新占位；已批准施工 |

---

## 12. 交付结论

- [x] 杠杆比选写清（推荐中核链式；门槛前压为备选；精英本轮不扩）。  
- [x] 可执行组表 / delay / `$kill` / 传送挂点已写。  
- [x] 验收硬条 + 不动清单 + 专岗已写。  
- [x] **总控已批准（2026-09-28）** — 方案 A；本额度仅改 `EmberWeekly/monster.yml`；精英不扩；测岗 §10。

**最终结论：** **已批准 · 施工中。** 中核链式 + 消 kill-any；wave1/wave3/boss 零内容 diff（wave1.end 仅组名指向）；精英不扩。

---

## 13. 交卷推荐摘要（给总控）

- **问题：** 周本仍是 wave1→delay→wave2→delay→wave3→Boss 等清骨架；软尾巴高于已扩线日常。  
- **推荐杠杆：** **中核房内 start 链式重叠**（wave2 → wave2a 尸×2 / wave2b 骷×3；start→delay2；传送挂 wave2b；对齐断塔/焦骨/霜晶；**零增怪**；顺带消 kill-any）。  
- **备选：** 中室门槛前压（须裁额或 +1；改动量更大 → 不优先）。  
- **改动文件：** **仅** `plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml`（wave1.end 组名指向 + 拆 wave2a/2b + 文案）。  
- **是否建议施工：** **建议批准后本额度施工**（杠杆已在日常 PASS；周本单点、改动面小）；未批准前 YAML 零改、勿 commit。  
- **是否碰精英：** **否**（本轮不扩 `EmberEliteWeekly`）。  
- **落盘：** `docs/design-ember-weekly-wave-variance.md`
