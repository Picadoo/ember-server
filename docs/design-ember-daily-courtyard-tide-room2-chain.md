# 设计稿 · 庭院 + 潮蚀 房2 排队→start 链式重叠

> 债源：`docs/STATUS-ember-daily-room2-expand-closeout-review.md` 软债#2「庭院/潮蚀房2故意等清」  
> 体例：霜晶房2链式 PASS（`36e98c8` / `91eee00` · `design-ember-daily-frost-room2.md`）  
> **已批准（总控 2026-09-28）：** 方案 A · 两线房2 `wave2a.start`→delay=2 `wave2b`；开门仍挂 wave2b.end；**不碰 boss_prep**；仅 EmberDaily + EmberDailyTide。方案 B 否决。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 庭院 + 潮蚀 第二房 start 链式重叠 |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | 仅 `EmberDaily` + `EmberDailyTide` 的 `monster.yml` **房2**（`wave2a`/`wave2b`） |
| 状态 | **已批准 · 方案 A（总控 2026-09-28）** |
| 对照 | 霜晶房2链式（推荐体例）；断塔/焦骨/地窖房2已 PASS |

### 硬约束

| 可动 | 不可动（硬禁） |
|------|----------------|
| 两线 **房2** `wave2a.start`→delay≈**2** `wave2b`；开门仍在 `wave2b.end` | **体力 / 掉落 / MM Health**；门宽与 `$operation-block` **门坐标** |
| 房2 Display 文案（重叠体感关键词） | **Boss / Boss HP / 刷点坐标**（默认）；**`boss_prep` 组全文零动** |
| | 新建 / 沿用 **`kill-any`**；跨组合并 `$kill` |
| | **断塔 / 焦骨 / 地窖 / 霜晶 / 锈轨** YAML；房1 / option / MM id |

**总量：** 庭院 12+Boss、潮蚀 12+Boss（含既有 prep×2）——本条 **零增怪、零裁怪**。

---

## 1. 问题一句话

收口验尸软债#2：庭院/潮蚀用 Boss 前压换差异后，**房2 仍「对射/桥射清完再涌」等清排队**；肝这两条时中段仍像复印机。霜晶房2已证 start 链式可用——本轮同杠杆扩两线房2，**不碰已落地的 boss_prep**。

---

## 2. 现状表（调研 · 现网事实 · 本稿未改）

来源：`plugins/DungeonPlus/dungeon/EmberDaily/monster.yml`、`…/EmberDailyTide/monster.yml`；对照 `…/EmberDailyFrost/monster.yml` 房2链式。时点 2026-09-28。

### 2.1 庭院 `EmberDaily`

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 灰烬庭院僵尸 | 4 | (±5,65,7) | ×4 | door1 @ **z=13** → wave2a delay=3 |
| **wave2a** | door1 后 | 灰烬庭院骷髅 | **2** | (±6,65,21) | 骷 ×**2** | **end** → wave2b delay=**4**（**等清**） |
| **wave2b** | wave2a 清完后 | 灰烬庭院僵尸 | **4** | (±6,65,17)×2+1；(0,65,19)×1 | 尸 ×**4** | door2 @ **z=25** → **boss_prep** delay=2 |
| **boss_prep** | door2 后 | 灰烬庭院僵尸 | **2** | (±3,65,27) | 尸 ×**2** | → boss delay=2 · **本条不动** |
| boss | prep 后 | 灰烬庭院蛮兵 | 1 | (0,66,31) | ×1 | COMPLETE |

**房2一句话：** 柱后对射骷×2 → **等清 delay4** → 涌尸×4 → 开门 → 门槛 prep（已有）。

### 2.2 潮蚀 `EmberDailyTide`

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 潮蚀尸 | 4 | (±2,64,8) | ×4 | door1 @ **z=18** → wave2a delay=3 |
| **wave2a** | door1 后 | 浪矢骷 | **3** | (2,64,28)×2；(-1,64,32)×1 | 浪矢 ×**3** | **end** → wave2b delay=**4**（**等清**） |
| **wave2b** | wave2a 清完后 | 潮蚀尸 | **3** | (-2,64,26)×2；(1,64,34)×1 | 尸 ×**3** | door2 @ **z=38** → **boss_prep** delay=2 |
| **boss_prep** | door2 后 | 浪矢骷 | **2** | (±3,64,41) | 浪矢 ×**2** | → boss delay=2 · **本条不动** |
| boss | prep 后 | 潮闸蛮兵 | 1 | (0,64,46) | ×1 | COMPLETE |

**房2一句话：** 对岸浪矢×3 → **等清 delay4** → 桥头尸×3 → 开门 → 门槛 prep（已有）。

### 2.3 霜晶体例（对照 · 零改）

| 项 | 霜晶现网（PASS） |
|----|------------------|
| 链 | `wave2a.start` → `$monstergroup{wave2b;delay=2}` |
| wave2a.end | 仅「折台尸侧已清」+ heal；**无** door / boss / 再拉 wave2b |
| 开门 | 仅 `wave2b.end` |
| 测 | live Δ≈1.3s 重叠窗；commit `36e98c8`；测报 `91eee00` |

### 2.4 Boss 前压说明（本条边界）

room2-variance 试点已给庭院/潮蚀加 **`boss_prep`**（门槛尸贴 / 浪矢点射）。  
**本稿只改房2等清→链式；`boss_prep` / boss / 房1 若已存在则整组不动。**  
「Boss 前压覆盖不均」仍软挂，**本条不扩**到其它线。

---

## 3. 方案比选

| 候选 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 房2 start 链式** | `wave2a.start` → delay≈**2** `wave2b`；开门仍 `wave2b.end`；`$kill` 按组独立 | 直还软债#2；对齐霜晶 PASS；零增怪；**不碰 prep** | 与霜晶/断塔等同杠杆族（对射重叠 vs 霜矢重叠，手感仍可分） | **推荐** |
| B. 仅缩短 end delay | 等清保留，delay4→2 | 改动更小 | **仍排队**；不打断「清完再刷」节拍 | **不推荐**（一句备选） |

**推荐：A。** 理由：总控解锁的是「故意等清」债；霜晶已证 delay=2 start 链可测；前压差异保留，中段不再亲戚。

---

## 4. 方案 A · 施工要点（批准后）

约定：`delay` 秒；`$kill` **按组独立**；禁 `kill-any`；坐标/门位/`boss_prep`/boss/房1 **默认零动**。

### 4.1 链式写法（两线同构 · 对齐霜晶）

```
wave2a.start: message（重叠预告）+ $monstergroup{group=wave2b;repeat=false;delay=2}
wave2a.end:   仅「×侧已清」类提示 + heal；禁止 door / boss_prep / boss / 再调 wave2b
wave2b.start: message（重叠压上 · 清完开门）
wave2b.end:   door2 ×9 AIR（现网坐标）+ heal + $monstergroup{boss_prep;delay=2}  【保持现网，勿改门/prep】
```

**相对现网唯一结构性变更：** 把 `$monstergroup{wave2b;delay=4}` 从 **wave2a.end** 挪到 **wave2a.start**，且 delay **4→2**；wave2a.end 去掉拉下一组。

### 4.2 组表（改后 · 数量/坐标同现网）

| 线 | wave2a | 链 | wave2b | 开门挂 | prep |
|----|--------|----|--------|--------|------|
| 庭院 | 骷×2 | start delay=**2** | 尸×4 | wave2b.end → door2 z=25 → **boss_prep 不动** | 门槛尸×2 不动 |
| 潮蚀 | 浪矢×3 | start delay=**2** | 尸×3 | wave2b.end → door2 z=38 → **boss_prep 不动** | 门槛浪矢×2 不动 |

### 4.3 建议文案（可微调 · 保留可测关键词）

| 线 | 节点 | 文案意图 |
|----|------|----------|
| 庭院 | wave2a.start | `【回廊·对射】左右柱后骷髅 —— 涌尸将重叠压上` |
| 庭院 | wave2a.end | `对射侧已清`（**勿**写开门 / 涌尸已出） |
| 庭院 | wave2b.start | `【回廊·涌尸】僵尸重叠压上 —— 清完开 Boss 门` |
| 庭院 | wave2b.end | 保留现网「回廊已清 · Boss 门开了 · 终厅门槛压」 |
| 潮蚀 | wave2a.start | `【水道·折桥】对岸浪矢骷 —— 潮蚀尸将重叠冲锋` |
| 潮蚀 | wave2a.end | `对岸弓侧已清` |
| 潮蚀 | wave2b.start | `【水道·桥头】潮蚀尸重叠压上 —— 清完开闸厅门` |
| 潮蚀 | wave2b.end | 保留现网「折桥已清 · 闸厅门开了 · 门槛卫」 |

### 4.4 改后体感

- **庭院：** 躲柱打对射时约 **2s** 内涌尸已压上；两组都清完才开 door2；进门仍门槛贴脸再蛮兵。  
- **潮蚀：** 拆对岸弓时约 **2s** 内桥头尸已冲；两组清完才开闸厅门；进门仍浪矢门槛再蛮兵。

---

## 5. 硬禁（再申）

- 不改体力 / 掉落 / MM Health / Boss 组内容与坐标  
- 不改门宽、门 `$operation-block` 坐标（庭院 z=13/25；潮蚀 z=18/38）  
- 不新建 `kill-any`；不跨组合并 `$kill` 数量  
- **不碰 `boss_prep`（本条不动前压）**  
- 不碰断塔 / 焦骨 / 地窖 / 霜晶 / 锈轨；不改房1  

---

## 6. 验收（草案 · 对齐霜晶测报）

| # | 硬条 | PASS 标准 |
|---|------|-----------|
| 1 | 房2重叠 | 庭院：对射骷在场时约 **2s** 内涌尸已出；潮蚀：浪矢在场时约 **2s** 内桥头尸已出（均无需等第一段全清） |
| 2 | 开门挂末波 | door2 仅在 **wave2b** `$kill` 完成后开；wave2a.end **无** operation-block / 无再拉 wave2b |
| 3 | `$kill` 纪律 | 两组独立；无 kill-any；去色名与数量同现网 |
| 4 | 前压仍在 | door2 后仍进 **boss_prep**（庭院门槛尸×2 / 潮蚀浪矢×2）再 Boss；prep YAML **相对批准前基线零非预期 diff** |
| 5 | 数值 / 范围 | 无加血；体力箱掉落不动；小怪合计仍 12+Boss/线；**仅**两线 `monster.yml` 房2 diff；他线零 diff |
| 6 | 口述 | ≠「清完对射/桥射才涌」；能感到重叠，且仍记得门槛前压 |

---

## 7. 施工勾选（批准后 · 插件岗）

- [ ] **主路径：** 改 `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` 仅 wave2a/wave2b start·end 链与文案  
- [ ] **主路径：** 改 `plugins/DungeonPlus/dungeon/EmberDailyTide/monster.yml` 仅 wave2a/wave2b start·end 链与文案  
- [ ] **同步：** `server-runtime/plugins` → `../plugins`（**符号链接，同 inode**）——改 plugins 即 runtime；**无需另拷**；热更仍走 FIFO `server-runtime/console.in` → `dp reload`  
- [ ] 静态自检：wave2a.start 含 `delay=2`→wave2b；wave2a.end 无 door/prep/boss/再拉；wave2b.end 仍拉 **boss_prep**（非直 boss）  
- [ ] `boss_prep` / wave1 / boss / 门坐标 / 刷点 / `$kill` 数量 · **diff 应为空或仅注释**  
- [ ] 非本两线（Spire/Ash/Crypt/Frost/Rail）`git diff` 空  
- [ ] 写 `docs/STATUS-ember-daily-courtyard-tide-room2-chain.md`；交测岗按 §6；**未授权勿 push**

**专岗：** 余烬-插件（DP 主）→ 测试 §6 → 总控批 commit/push。策划本条只交稿，**批准前零 YAML**。

---

## 8. 交卷摘要（给总控）

| 项 | 内容 |
|----|------|
| 两线现网房2 | **庭院**对射骷×2→等清 delay4→涌尸×4；**潮蚀**浪矢×3→等清 delay4→桥头尸×3（均已有 boss_prep） |
| 推荐 | **方案 A** · start 链式 delay≈2（对齐霜晶） |
| 碰前压？ | **否**（`boss_prep` 本条不动） |
| 改动量 | 小 · 2 文件房2 start/end + 文案 |
| 状态 | **薄可批 · 待批准施工**；本稿已落盘；**未改 YAML / 未 commit / 未 push** |

**落盘：** `docs/design-ember-daily-courtyard-tide-room2-chain.md`
