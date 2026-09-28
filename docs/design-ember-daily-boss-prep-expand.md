# 设计稿 · Boss 前压扩线（断塔 / 焦骨 / 地窖）

> 债源：`docs/STATUS-ember-daily-room2-expand-closeout-review.md` 软债#3「Boss 前压只在庭院/潮蚀」  
> 前置：庭院/潮蚀 `boss_prep` 已 PASS（`3459d53` · room2-variance）；断塔/焦骨/地窖房2链式已 PASS（`eb81942` / `b310aa4` 等）  
> **已批准（总控 2026-09-28）：** 方案 A · 断塔卫尸×2 / 焦骨燃矢×2 / 地窖誓印×2；wave2b.end→boss_prep delay=2→boss；合计 +6；霜晶锈轨不加。方案 B 否决。
> **本条不加霜晶 / 锈轨**（既有杠杆够）。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 断塔 / 焦骨 / 地窖 door2 后薄 `boss_prep` |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | 仅 `EmberDailySpire` / `EmberDailyAsh` / `EmberDailyCrypt` 的 `monster.yml` **door2→Boss 段** |
| 范例 | `EmberDaily` / `EmberDailyTide` 现网 `boss_prep`（数量≈2、独立 `$kill`、delay≈2→boss） |
| 状态 | **已批准 · 方案 A（总控 2026-09-28）** |

### 硬约束（本条）

| 可动 | 不可动（硬禁） |
|------|----------------|
| 三线 **door2 开后** 新组 `boss_prep`；`wave2b.end` 改调 prep；prep.end→boss | **房1 / 房2 链式**（start 重叠、数量、刷点）一律零动 |
| prep 刷点落在 door2 内侧既有空间 | **门坐标** `$operation-block`；**Boss HP / 坐标 / `$kill`** |
| prep Display 文案（门槛体感关键词） | **体力 / 掉落 / 通关箱**；MM Health；新建 MM id |
| | **`kill-any`**；跨组合并 `$kill` |
| | **霜晶 / 锈轨 / 庭院 / 潮蚀** YAML（庭院/潮蚀 prep 作范例，零改） |
| | **新挖图**；第三道必清主门 |

**增怪：** 仅 prep 小怪；方案 A 每线 +2，合计 **+6**（见 §3）。

---

## 1. 问题一句话

收口软债#3：庭院/潮蚀开门后有门槛戏（尸贴 vs 浪矢射），断塔/焦骨/地窖仍 **door2 → 空厅直 Boss**。房2链式已 PASS，本轮只补终厅前压，体感对齐庭院/潮蚀差异，三线品种可分。

---

## 2. 现状表（调研 · 现网事实 · 本稿未改）

时点 2026-09-28；来源 `plugins/DungeonPlus/dungeon/EmberDaily{Spire,Ash,Crypt}/monster.yml` + 庭院/潮蚀范例；MM 口径见 `plugins/MythicMobs/Mobs/EmberDaily{Spire,Ash,Crypt}.yml`。

### 2.1 范例 · 庭院 `boss_prep`（零改）

| 项 | 现网 |
|----|------|
| 链 | `wave2b.end`：door2 AIR @ z=25 → `$monstergroup{boss_prep;delay=2}` → prep.end → `boss;delay=2` |
| prep | `EmberDailyZombie`（灰烬庭院僵尸）×**2** @ `(-3,65,27)` `(3,65,27)` |
| `$kill` | 灰烬庭院僵尸 ×**2**（独立组） |
| 手感 | **近战尸贴脸**；开门≠立刻 Boss |

### 2.2 范例 · 潮蚀 `boss_prep`（零改）

| 项 | 现网 |
|----|------|
| 链 | `wave2b.end`：door2 AIR @ z=38 → `boss_prep;delay=2` → prep.end → `boss;delay=2` |
| prep | `EmberDailyTideSkeleton`（浪矢骷）×**2** @ `(-3,64,41)` `(3,64,41)` |
| `$kill` | 浪矢骷 ×**2** |
| 手感 | **远程浪矢点射**（BOW）；与庭院可贴分 |

### 2.3 断塔 `EmberDailySpire`（待加 prep）

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 断塔卫尸 | 5 | 塔底 | ×5 | door1 @ z=4 y64 → wave2a |
| wave2a | door1 后 | 裂隙箭骷 | 4 | (±6,70,0) | ×4 | **start**→wave2b delay=2；end 不开门 |
| wave2b | 重叠 | 断塔卫尸 | 2 | (0,70,-6)(0,70,2) | ×2 | door2 @ **z=4 y70** → **直调 boss delay=3** |
| boss | door2 后 | 断塔守望 | 1 | **(0,76,0)** | ×1 | COMPLETE |

**一句话：** 环廊重叠已有；顶门开后 **空顶台直守望**。小怪合计 **11**+Boss。

### 2.4 焦骨 `EmberDailyAsh`（待加 prep）

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 焦骨尸 | 6 | 主甬+假岔 | ×6 | door1 @ z=16 → wave2a |
| wave2a | door1 后 | 焦骨尸 | 3 | 弯折 | ×3 | **start**→wave2b delay=2 |
| wave2b | 重叠 | 燃矢骷 | 3 | (1,65,28)×2；(-1,65,32)×1 | ×3 | door2 @ **z=36** → **直调 boss delay=3** |
| boss | door2 后 | 焦核蛮兵 | 1 | **(0,65,44)** | ×1 | COMPLETE |

**一句话：** 假岔+房2重叠已有；鼓室门开后 **空厅直焦核**。小怪合计 **12**+Boss。  
**MM 口径：** `EmberAshSkeleton`（燃矢骷）= **WOOD_SWORD 近战** + 轻燃，**不是弓**。

### 2.5 地窖 `EmberDailyCrypt`（待加 prep）

| 组 | 时机 | MM / Display | 数量 | 刷点（现网） | `$kill` | 开门 / 下一段 |
|----|------|--------------|------|--------------|---------|----------------|
| wave1 | 进本 | 窖卫尸 | 5 | 上层 | ×5 | door1 @ z=18 y72 → wave2a |
| wave2a | door1 后 | 誓印骷 | 3 | 高台 | ×3 | **start**→wave2b delay=2 |
| wave2b | 重叠 | 窖卫尸 | 3 | 地面 | ×3 | door2 @ **z=40 y66** → **直调 boss delay=3** |
| boss | door2 后 | 残誓守墓 | 1 | **(0,60,48)** | ×1 | COMPLETE |

**一句话：** 高台先箭+地面重叠已有；底层门开后 **空圆厅直残誓**。小怪合计 **11**+Boss。  
**MM 口径：** `EmberDailyCryptSkeleton`（誓印骷）= **BOW**。

### 2.6 三线 prep 品种选型（须与现网 MM 真名一致 · 可分）

| 线 | prep MM id | Display（`$kill` 去色） | 武器 / 气质 | 对齐杠杆 |
|----|------------|-------------------------|-------------|----------|
| **断塔** | `EmberDailySpireZombie` | 断塔卫尸 | 近战尸 **贴脸** | ≈庭院门槛尸贴 |
| **焦骨** | `EmberAshSkeleton` | 燃矢骷 | **木剑近战** + 轻燃（第三种） | 异于尸贴 / 弓射 |
| **地窖** | `EmberDailyCryptSkeleton` | 誓印骷 | **BOW 点射** | ≈潮蚀门槛浪矢射 |

**勿三线同皮：** 近战卫尸 / 燃矢近战骷 / 誓印弓 — 三角可分。  
**禁选：** 断塔裂隙箭骷作 prep（会与地窖弓撞杠杆族）；焦骨尸作 prep（与断塔卫尸同「贴脸尸」）。

---

## 3. 方案比选

| 候选 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 三线各 +2 prep** | 断塔/焦骨/地窖均 `wave2b.end`→`boss_prep`×2→boss；delay≈2 | 一次还清软债#3；品种三角可分；对齐庭院/潮蚀范例 | 小怪合计上浮（见下）；鼓室偏窄须核站位 | **推荐** |
| B. 只做两条 | 例如断塔+地窖（跳过焦骨鼓室窄场） | 改动更小；避窄鼓室挤怪 | 焦骨终厅仍直 Boss；软债只还一半 | **一句备选** |

**推荐：A。** 理由：总控本条派活即三线；房2链式已稳，终厅前压是缺的那一拍；焦骨燃矢近战正好补「第三种」皮，跳过焦骨反而浪费可分点。

### 方案 A · 增怪合计

| 线 | 现网小怪 | prep | 改后小怪 | Boss |
|----|----------|------|----------|------|
| 断塔 | 11 | +2 卫尸 | **13** | 1 |
| 焦骨 | 12 | +2 燃矢 | **14** | 1 |
| 地窖 | 11 | +2 誓印 | **13** | 1 |
| **三线合计** | 34 | **+6** | **40** | 3 |

本条 **不裁房1/房2**（硬禁）；额度上浮接受，熟手仍约 5～8 分量级。

---

## 4. 方案 A · 可执行要点（批准后施工）

约定：`delay` 秒；`$kill` **按组独立**；禁 `kill-any`；房1/房2/门位/Boss/MM **零动**。

### 4.1 链式改法（三线同构 · 对齐庭院/潮蚀）

```
wave2b.end（现）:  door2 ×9 AIR + heal + $monstergroup{boss;delay=3}
wave2b.end（改）:  door2 ×9 AIR + heal + $monstergroup{boss_prep;delay=2}
                   （文案可加「· 门槛压」）
boss_prep（新）:   monster×2 + $kill×2（本线品种）
  start: 门槛提示
  end:   heal + $monstergroup{boss;delay=2}
boss（不动）:      坐标 / HP / $kill / COMPLETE 零 diff
```

**勿动：** wave1、wave2a、wave2b 的 monster 列表 / start 链式 / `$kill` 数量 / 门 `$operation-block` 坐标。

### 4.2 断塔 · 顶台门槛卫尸贴脸

| 项 | 内容 |
|----|------|
| prep MM | `EmberDailySpireZombie` · `$kill` 断塔卫尸 ×**2** |
| 刷点建议 | 顶台门槛两侧：`(-3,76,2)` `(3,76,2)`（door2 z=4@y70 上顶后 → Boss `(0,76,0)` 之间；廊内可站格） |
| 手感 | 进顶台先被卫尸贴脸，清完才出守望 |
| chat 例 | start：`【顶台·门槛】两侧断塔卫尸贴脸 —— 清完才出守望` / end：`门槛已清 · 守望现身` |
| 地图核 | 顶台 y=76 两侧须可站；占格则 ±1 格内挪，**勿新挖** |

### 4.3 焦骨 · 鼓室门槛燃矢近战

| 项 | 内容 |
|----|------|
| prep MM | `EmberAshSkeleton` · `$kill` 燃矢骷 ×**2** |
| 刷点建议 | 鼓室入口内侧：`(-2,65,38)` `(2,65,38)`（door2 z=36 与 Boss `(0,65,44)` 之间；略收 x 避窄场挤心） |
| 手感 | 过门先挨燃矢木剑贴脸（可带轻燃），清完才出焦核 |
| chat 例 | start：`【鼓室·门槛】两侧燃矢贴脸 —— 清完才出焦核` / end：`门槛已清 · 焦核现身` |
| 地图核 | 鼓室偏窄；优先门内侧左右，**勿**刷到 Boss 站位重叠 |

### 4.4 地窖 · 圆厅门槛誓印点射

| 项 | 内容 |
|----|------|
| prep MM | `EmberDailyCryptSkeleton` · `$kill` 誓印骷 ×**2** |
| 刷点建议 | 圆厅入口内侧：`(-3,60,42)` `(3,60,42)`（door2 z=40@y66 下至 Boss `(0,60,48)` 之间；**y 落至 60**） |
| 手感 | 下圆厅先挨誓印点射，清完才出残誓 |
| chat 例 | start：`【圆厅·门槛】两侧誓印点射 —— 清完才出守墓` / end：`门槛卫已清 · 残誓现身` |
| 地图核 | y 落差路径可站；若 60 层入口无左右格，±1 格内挪或略靠前 z=43 |

### 4.5 与现网前压差异一览（玩家能感知）

| 线 | prep 手感 | 开门后一句话 |
|----|-----------|--------------|
| 庭院（既有） | 尸贴 | 门槛两侧僵尸贴脸 |
| 潮蚀（既有） | 浪矢射 | 门槛两侧浪矢点射 |
| **断塔（本条）** | **卫尸贴** | 顶台门槛卫尸贴脸 |
| **焦骨（本条）** | **燃矢近战** | 鼓室门槛燃矢贴脸 |
| **地窖（本条）** | **誓印弓** | 圆厅门槛誓印点射 |

---

## 5. 硬禁清单（施工自检）

- [ ] 未改 wave1 / wave2a / wave2b 的 monster、start 链式、`$kill` 数量
- [ ] 未改任何 `$operation-block` 门坐标（断塔 z=4@y70；焦骨 z=36；地窖 z=40@y66）
- [ ] 未改 boss 组坐标 / HP / `$kill` / COMPLETE
- [ ] 无 `kill-any`；prep `$kill` 按组独立且 Display 去色名对齐 MM
- [ ] 未改体力 / 掉落 / option.yml / MM 文件
- [ ] 未动霜晶 / 锈轨 / 庭院 / 潮蚀
- [ ] 未新挖地形；prep 仅用 door2 内侧既有空间
- [ ] 三线 prep 品种未同皮（卫尸 / 燃矢 / 誓印）

---

## 6. §验收（测岗硬条）

| # | 条 | 期望 |
|---|----|------|
| 1 | 骨架 | 三线仍：≥2 真门 →（新）prep → Boss；玩家零指令 |
| 2 | 断塔前压 | door2 开后 **先** 顶台卫尸×2，清完才刷守望；口述≠「开门见 Boss」 |
| 3 | 焦骨前压 | door2 开后 **先** 燃矢×2（近战感），清完才刷焦核 |
| 4 | 地窖前压 | door2 开后 **先** 誓印×2（点射感），清完才刷残誓 |
| 5 | 三线可分 | 卫尸贴 / 燃矢近战 / 誓印射 — 玩家能说出差别 |
| 6 | 房2零回归 | 断塔对射重叠、焦骨尸箭重叠、地窖高台+地面重叠 — 链式仍在；门仍挂 wave2b.end |
| 7 | `$kill` 纪律 | prep 独立组；无 kill-any；清不够不开 Boss |
| 8 | 非本条零伤 | 霜晶/锈轨/庭院/潮蚀 YAML 无 diff；Boss HP/掉落/体力无 diff |

---

## 7. 施工勾选（批准后）

| # | 文件 | 动作 | 勾 |
|---|------|------|----|
| 1 | `plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml` | 新 `boss_prep`（卫尸×2）；wave2b.end→prep delay=2；prep.end→boss delay=2 | [ ] |
| 2 | `plugins/DungeonPlus/dungeon/EmberDailyAsh/monster.yml` | 新 `boss_prep`（燃矢×2）；同上链 | [ ] |
| 3 | `plugins/DungeonPlus/dungeon/EmberDailyCrypt/monster.yml` | 新 `boss_prep`（誓印×2）；同上链 | [ ] |
| 4 | `server-runtime/plugins` | **symlink → `../plugins`**（已存在）；改 plugins 即现网，**勿**在 runtime 另写一份 | [ ] 注明即可 |
| 5 | 小写 stub `ember_daily_{spire,ash,crypt}/monster.yml` | **勿改**（空 default 组，非现网入口） | [ ] 跳过 |
| 6 | MM / option / 门坐标 / 房1房2 | **零动** | [ ] |
| 7 | git | **勿 commit / push**（待总控批后另派） | [ ] |

**施工岗注意：** 只改上表 1～3 三份 PascalCase `monster.yml`；`server-runtime/plugins` 已是 symlink，无需双写。

---

## 8. 给总控的一句话

**薄可批 · 推荐方案 A。** 断塔卫尸贴 + 焦骨燃矢近战 + 地窖誓印弓，各 +2 prep（合计 +6）；`wave2b.end`→`boss_prep`→boss（delay≈2）；零碰房1/房2/门/Boss/体力掉落；霜晶锈轨不加。坐标建议见 §4.2～4.4（施工前地图核可站）。未改 YAML、未 commit/push。
