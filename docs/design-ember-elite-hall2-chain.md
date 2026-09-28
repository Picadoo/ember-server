# 设计稿 · 精英厅二是否套 start 链式重叠

> **已批准方案 B（总控 · 2026-09-28）：本轮不施工。** 厅二维持同帧双 `$kill` AND、无 start 链。  
> tip 背景：`870aba0`（断塔环廊防坠短抽 PASS）；厅一链式已落地见既批 `design-ember-elite-wave-variance.md`。  
> 债源 / 派工：总控【精英厅二是否套 start 链式 · 薄设计】  
> live：`plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml`

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 精英周常 · 厅二（蛮纹+混纹）是否对齐厅一 start 链式重叠 |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | **仅** `EmberEliteWeekly/monster.yml` 厅二组表时序（若批）；厅一 / 厅三 / option / MM **零 diff** |
| 状态 | **已批准 B · 本轮零改 YAML** |
| 对照 | 厅一 `wave1→wave1b` 链式；既往 `design-ember-elite-wave-variance.md` §3.2 / §6.5（厅二曾定 **no chain**） |

### 硬约束（本条）

| 可动（若批） | 不可动（硬禁） |
|--------------|----------------|
| 厅二组名拆分 / start→delay→第二组 / 独立 `$kill` 挂点 | **Boss / 小怪 HP**；门（本本无铁栅）；**票 / 体力** |
| 文案微调（须保留可测关键词） | **`$kill-any`**（新建或回退） |
| | teleport 落点 `(-4,72,270)` / `(22,68,270)`；wave3 / 执行官 |
| | `option.yml`；日常七线；`EmberWeekly`；MM id / Health / 掉落 |
| | 零增怪外的加怪；改厅一已落地链式 |

**总量：** 仍蛮纹×1 + 混纹×2 = 3 + 厅一 5 + Boss；**零增怪**。

---

## 1. 问题一句话

厅一已有 start 链式拉开节奏；厅二现网是否也要套「蛮纹 start→delay→混纹」对齐对称，还是维持双 `$kill` 同帧蛮压记忆。

---

## 2. 现网厅二结构摘要（调研 · 本稿未改 YAML）

调研时点：2026-09-28 Asia/Shanghai；来源 live `EmberEliteWeekly/monster.yml`。

| 项 | 现网事实 |
|----|----------|
| 组数 | **1 组** `wave2`（未拆 wave2a/2b） |
| 怪 | `EmberEliteBrute`×1 @ `(-4,72,270)` + `EmberEliteMix`×2 @ `(-6,72,268)` |
| 刷帧 | **同帧同刷**（同一 `monster:` 列表；无 start 拉另一组） |
| `$kill` | **双独立 `$kill` AND**：蛮纹×1 + 混纹×2（**已无 kill-any**） |
| 链式 | **无** — `wave2.start` 仅 message；**无** `$monstergroup` |
| 推进 | `wave2.end` → heal + tp 厅三 `(22,68,270)` + wave3 delay=3 |
| 空间 | 两刷点约 **2 格**（同厅二短廊，几何紧） |

厅一对照（已落地链式）：

| 项 | 厅一 |
|----|------|
| 链 | `wave1.start` → delay**2** → `wave1b` |
| `$kill` | 炽尸×3 / 骨刺×2 **分组独立** |
| 传厅二 | 挂 **wave1b.end** |

既往设计结论（`design-ember-elite-wave-variance.md` §3.2）：厅二 **不链式** — 蛮纹单锚记忆 + 10500 HP 叠混纹抬难 + 周本 PASS 亦未对深室做 start 重叠；纪律用双 `$kill` AND 即可。

---

## 3. 杠杆比选

| 候选 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 厅二套 start 链式** | 拆 `wave2`→`wave2a` 蛮纹 / `wave2b` 混纹；`wave2a.start`→delay≈2→`wave2b`；传厅三挂 wave2b.end；两组独立 `$kill` | 与厅一对称；拉开「一坨清完」体感 | 蛮纹存活期叠混纹显著抬瞬时压；冲「蛮压词缀」单锚；厅二几何紧、重叠收益有限；既批稿已否决 | 备选（须总控明示驳回「不链式」） |
| **B. 维持现状 / 不链式** | 不改组表时序；可仅观测；若日后另批「只调坐标重叠」亦属 B 族、本轮不写施工坐标 | 零抬难；纪律已清；记忆锚保留；与既批 §3.2 一致 | 厅二宏观仍「同刷等清」 | **推荐 · 不建议本轮动** |

### 推荐：**B · 不建议本轮动**

**一句理由：** 厅二已是同帧双种 + 双独立 `$kill` AND、地图空间紧，且既批稿因蛮纹高血叠压明确否决链式——再套厅一式 start 链抬难大于节奏收益。

---

## 4. 方案 A（若总控强制链式 · 默认不施工）

对齐厅一 / 周本中核写法；**零碰 option**（厅二非 dungeon-start 首组）。

| 组 | 时机 | MM | 数量 | 刷点（默认现网） | `$kill` | 链 / 传送 |
|----|------|-----|------|------------------|---------|-----------|
| **wave2a** | 厅一传厅二后（现 wave2 入口） | Brute | **1** | `(-4,72,270)` | 余烬试炼·蛮纹 ×1 | **start** → `$monstergroup{wave2b;delay=2}`；end **不** tp / 不调 wave3 |
| **wave2b** | wave2a start 后 delay **≈2s** | Mix | **2** | `(-6,72,268)` | 余烬试炼·混纹 ×2 | **end**：heal + tp `(22,68,270)` + wave3 delay=3 |
| wave3 | **不动** | Boss | 1 | `(24,68,270)` | ×1 | COMPLETE |

落地组名备忘（须改 wave1b.end 调组名：现调 `wave2` → 改调 `wave2a`）：

```
# wave1b.end 现：... + $monstergroup{group=wave2;delay=3}
# 若批 A：改为调 wave2a；或保留组名 wave2=蛮纹组、另增 wave2b（等价）

wave2.start:   message（蛮压）+ $monstergroup{group=wave2b;delay=2}
wave2.end:     仅侧清提示 + heal；禁止 teleport / wave3
wave2b.start:  message（混纹重叠压上）
wave2b.end:    heal + $teleport{22,68,270} + $monstergroup{wave3;delay=3}
wave2 / wave2b.condition: 各自独立 $kill（禁 kill-any）
```

**坐标是否重叠：** 现网两点已近（约 2 格）；链式不强制再挪点。若测岗报无站位再 ±1 格，**另派**，本稿不预写坐标施工。

**与厅一对称点：** start→delay2→第二组；末组才推进；分组独立 `$kill`。差异：厅二第二组是伴生混纹而非远程骨刺；蛮纹 HP 远高于炽尸，叠压风险更高。

---

## 5. 方案 B（推荐）

| 项 | 内容 |
|----|------|
| 动作 | **本轮不改** `EmberEliteWeekly/monster.yml` |
| 体感 | 厅二保持「蛮纹单锚 + 混纹同刷伴生」；双 `$kill` AND 门槛 |
| 可选远期 | 「只调坐标重叠、仍不链式」若另有坠点/站位债再开薄稿；**非本轮交付** |
| 与既批关系 | 延续 `design-ember-elite-wave-variance.md` §3.2 / §6.5「厅二不链式」 |

---

## 6. 验收硬条（≤5 · 若批 A 才适用；批 B 则本条免测施工）

| # | 硬条 | PASS 标准 |
|---|------|-----------|
| 1 | 骨架 | 仍厅一→tp 厅二→tp 厅三→Boss→COMPLETE；传送落点坐标不变 |
| 2 | 链式窗（仅 A） | 蛮纹刷出后约 2s 内混纹在场；**仅** wave2b `$kill` 完成后才 tp 厅三 |
| 3 | `$kill` 纪律 | 全本无 kill-any；蛮纹 / 混纹独立 `$kill`；wave2（a）end **无** teleport / wave3 |
| 4 | 数值边界 | 无改 Boss/小怪 HP；票/体力/掉落未改；怪数仍 8+Boss |
| 5 | 范围 | 仅 `EmberEliteWeekly/monster.yml` 预期 diff；厅一链式 / 日常 / 周本 / option / MM **零 diff** |

批 **B** 时验收口径：**monster.yml 相对 tip 零 diff**；厅二仍同帧双 `$kill` AND、无 start 链。

---

## 7. 禁项清单

| 禁项 | 说明 |
|------|------|
| `$kill-any` | 禁止新建或把双 `$kill` 合并回 kill-any |
| Boss / 小怪 HP | 蛮纹 10500 / 混纹 2700 / 执行官等 **禁止改** |
| 门 / 票 / 体力 | 本本无铁栅真门；票体力发放消耗不动 |
| teleport 落点 | `(-4,72,270)` / `(22,68,270)` 不动 |
| wave3 / 厅一已落地链 | 零 diff |
| option / MM / 日常 / EmberWeekly | 零 diff |
| 增怪 | 零增怪 |
| 未批准施工 | **未批准前不改玩法 YAML**；勿 git push |

---

## 8. 交付结论

- [x] 方案 A/B 写清；推荐 **B · 不建议本轮动**。  
- [x] 现网厅二：1 组同帧蛮纹+混纹、双 `$kill` AND、**无链式**。  
- [x] 若强制 A：start→delay≈2→wave2b、独立 `$kill`、传厅三挂末组、坐标默认同现网。  
- [x] 验收 ≤5；禁项清单；文首「未批准前不施工」。  
- [ ] **总控批准前 YAML 零改、勿 commit 玩法文件。**

**最终结论：** **推荐 B · 不建议本轮动。** 厅二纪律与重叠空间已够；链式抬难大于对称收益。

---

## 9. 交卷推荐摘要（给总控）

- **问题：** 厅二要不要套厅一式 start 链式。  
- **现网：** wave2 同帧 Brute×1+Mix×2；双 `$kill` AND；无链式。  
- **推荐：** **不建议本轮动（B）** — 同帧双 kill 已清纪律、空间紧、既批否决叠压抬难。  
- **备选 A：** wave2a.start→delay2→wave2b；独立 `$kill`；传厅三挂末组（须总控明示批准）。  
- **改动文件（若批 A）：** 仅 `EmberEliteWeekly/monster.yml`；批 B 则 **零 diff**。  
- **落盘：** `docs/design-ember-elite-hall2-chain.md`

---

## 10. 总控批注

**批准方案 B**（2026-09-28 Asia/Shanghai · 余烬-总控）。

- 厅二同帧蛮纹+混纹 + 双独立 `$kill` 已够；链式叠压抬难大于对称收益。
- **本轮不改** `EmberEliteWeekly/monster.yml`；方案 A 搁置，除非日后口碑点名再开。
- 验收口径：相对 tip `monster.yml` 零 diff。
