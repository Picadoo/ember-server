# STATUS · 日常七线波次节奏差异（挑刺必改4）

**日期：** 2026-09-28 06:07（Asia/Shanghai）· 霜晶 door1 返工  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-daily-wave-variance.md` §3；测报 `docs/status/STATUS-ember-daily-wave-variance-test.md` FAIL 霜晶；总控【返工 · 霜晶 door1】  
**Verdict：** **✅ 霜晶已返工**：`$kill` 按组独立 → 对齐锈轨链式；wave1 kill×3 不开门；wave1b kill×2 **开门**；交错 delay3s 保留；门/Boss/去色名未动；YAML 合法；未 commit/push

---

## 一句话

七线波次已落地；测报 6 PASS · **霜晶 FAIL**（wave1 `$kill×5` 不计 wave1b）。已返工霜晶：左×3→delay3s 右×2→**wave1b 末开门**（对齐锈轨）。请**只复测霜晶 door1**。

---

## 1. 七线变更摘要

| 线 | DP | 改哪 | 新节奏 | 小怪合计 | 门条件（`$kill`） |
|----|-----|------|--------|----------|-------------------|
| 庭院 | `EmberDaily` | **房2** | 柱后骷×2 → delay4s 涌尸×4 | 5+2+4=**11** | door1 尸×5；door2 骷×2→尸×4 |
| 焦骨 | `EmberDailyAsh` | **房1** | 主甬尸×4 + 假岔尸×2 **同组** | 6+3+3=**12** | door1 焦骨尸×**6**（含假岔）；door2 尸×3→燃矢骷×3 |
| 地窖 | `EmberDailyCrypt` | **房2** | 高台誓印骷×3 → delay5s 地面窖卫×3 | 5+3+3=**11** | door1 窖卫×5；door2 誓印骷×3→窖卫×3 |
| 潮蚀 | `EmberDailyTide` | **房2** | 对岸浪矢骷×3 → delay4s 桥头潮蚀尸×3 | 5+3+3=**11** | door1 潮蚀尸×5；door2 浪矢骷×3→潮蚀尸×3 |
| 断塔 | `EmberDailySpire` | **房2** | 东西裂隙箭骷 2+2 → delay3s 卫尸×2 | 5+4+2=**11** | door1 卫尸×5；door2 裂隙箭骷×4→卫尸×2 |
| 霜晶 | `EmberDailyFrost` | **房1** | 左×3 → start delay3s 右×2（**链式**） | 3+2+3+3=**11** | door1：**wave1** 霜晶尸×3（不开门）→ **wave1b** ×2 **开门**；door2 旧序 |
| 锈轨 | `EmberDailyRail` | **房2** | 主巷尸×3 → delay2s 支洞矿矢骷×3 | 5+3+3=**11** | door1 锈轨尸×5；door2 锈轨尸×3→矿矢骷×3（支洞计入） |

**未改房 / Boss：** 各线未点名房与 Boss 数量级、坐标、`$operation-block` 门位、`$end` 通关文案均保持。

---

## 2. 逐线细节

### 2.1 庭院 `EmberDaily` · 房2

| 组 | 刷点 | `$kill` | 开门/链 |
|----|------|---------|---------|
| wave1（未改） | 尸×5 @ (-5,65,7)(5,65,7)(0,65,10) | 灰烬庭院僵尸×5 | 门1 z=13 → wave2a |
| **wave2a** | **骷×2** @ (-6,65,21)(6,65,21) 左右柱后 | 灰烬庭院骷髅×**2** | → wave2b delay4 |
| **wave2b** | **尸×4** @ (-6,65,17)×2 (6,65,17)(0,65,19) | 灰烬庭院僵尸×**4** | 门2 z=25 → boss |
| boss（未改） | 蛮兵 @ (0,66,31) | 灰烬庭院蛮兵×1 | COMPLETE |

### 2.2 焦骨 `EmberDailyAsh` · 房1

| 组 | 刷点 | `$kill` | 开门/链 |
|----|------|---------|---------|
| **wave1** | 主甬 (-3,65,8)×2 (3,65,8)×2 + **假岔尽头** (8,65,10)(7,65,11) | 焦骨尸×**6** | 门1 z=16 → wave2a |
| wave2a/b（未改节拍） | 尸×3 → 燃矢骷×3 | 焦骨尸×3 / 燃矢骷×3 | 门2 z=36 → boss |
| boss（未改） | 焦核蛮兵 @ (0,65,44) | 焦核蛮兵×1 | COMPLETE |

假岔几何：`docs/status/STATUS-ember-daily-s2.md` x=3..9 · z=9..12 · 封墙 x=9。

### 2.3 地窖 `EmberDailyCrypt` · 中层

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| wave1（未改） | 上层窖卫×5 | 窖卫尸×5 | 门1 z=18@y72 |
| **wave2a** | **誓印骷×3** @ (-5,66,36)(4,66,36) | 誓印骷×3 | → wave2b delay5 |
| **wave2b** | **窖卫×3** @ (-5,66,30)(4,66,30) | 窖卫尸×3 | 门2 z=40@y66 → boss |

### 2.4 潮蚀 `EmberDailyTide` · 房2

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| wave1（未改） | 沿岸尸×5 | 潮蚀尸×5 | 门1 z=18 |
| **wave2a** | **浪矢骷×3** 对岸 @ (2,64,28)(-1,64,32) | 浪矢骷×3 | → wave2b delay4 |
| **wave2b** | **潮蚀尸×3** 桥头 @ (-2,64,26)(1,64,34) | 潮蚀尸×3 | 门2 z=38 → boss |

### 2.5 断塔 `EmberDailySpire` · 环廊

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| wave1（未改） | 底层卫尸×5 | 断塔卫尸×5 | 门1 z=4@y64 |
| **wave2a** | **裂隙箭骷 2+2** @ (-6,70,0)×2 (6,70,0)×2 | 裂隙箭骷×**4** | → wave2b delay3 |
| **wave2b** | **卫尸×2** @ (0,70,-6)(0,70,2) 近阶 | 断塔卫尸×2 | 门2 z=4@y70 → boss |

### 2.6 霜晶 `EmberDailyFrost` · 房1 交错（2026-09-28 返工）

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| **wave1** | **左** (-3,70,8)×2 (-3,70,14)×1 | 霜晶尸×**3** | start→**wave1b delay3**；end **不开门**（仅「左侧冻台已清」） |
| **wave1b** | **右** (3,70,8)(3,70,6) | 霜晶尸×**2** | end：**门1 z=16 AIR×9** +「冻台已清 · 冰闸开了」→ wave2a |
| wave2a/b（旧序保留） | 尸×3 → 霜矢骷×3 | … | 门2 z=34 → boss |

**根因（测报）：** DP `$kill` **按组独立**；旧版 wave1 `$kill×5` 不计入 wave1b 的 2 击杀 → 全清后门仍铁栅。  
**修法：** 对齐锈轨链式——开门条件放在**末波 end**；交错仍靠 wave1 **start** `delay=3` 刷右（左未必要清完即可侧袭）。焦骨能过是同组刷 6；锈轨能过是开门在末波。

### 2.7 锈轨 `EmberDailyRail` · 房2 支洞

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| wave1（未改） | 主巷前段尸×5 | 锈轨尸×5 | 门1 z=15 |
| **wave2a** | 主巷尸×3 @ (-5,64,20)(0,64,22) | 锈轨尸×3 | → wave2b delay2 |
| **wave2b** | **支洞内** 矿矢骷×3 @ (8,64,20)(10,64,20) | 矿矢骷×3 | 门2 z=33 → boss（支洞计入） |

支洞几何：`docs/status/STATUS-ember-daily-s4.md` x=4..12 · z=18..22。  
门安全：wave2a→wave2b 仍为「清完再刷」顺序（仿 wave2a/2b），避免异名并行早开门；侧袭体感靠支洞坐标 + 文案。

---

## 3. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 房2 对射→涌尸 |
| `plugins/DungeonPlus/dungeon/EmberDailyAsh/monster.yml` | 房1 主甬+假岔×6 |
| `plugins/DungeonPlus/dungeon/EmberDailyCrypt/monster.yml` | 中层先骷后尸 |
| `plugins/DungeonPlus/dungeon/EmberDailyTide/monster.yml` | 房2 先桥射后冲锋 |
| `plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml` | 环廊 2+2 对射→卫尸 |
| `plugins/DungeonPlus/dungeon/EmberDailyFrost/monster.yml` | 房1 左右交错 + wave1b；**返工** 链式开门 |
| `plugins/DungeonPlus/dungeon/EmberDailyRail/monster.yml` | 房2 支洞骷计入 door2 |
| `docs/status/STATUS-ember-daily-wave-variance.md` | 本文件 |

**未改：** option/门块坐标/Boss/体力/通关箱/MM id/Display 去色名；小写 stub `ember_daily*/monster.yml`（非运行时，仍为 default 空壳，**未同步**）。  
**未 commit/push。**

---

## 4. 自检

| 检查 | 结果 |
|------|------|
| 七线 YAML `yaml.safe_load` 合法 | ✅ |
| 无 `$kill-any{` 条件 | ✅ |
| 小怪 10～12 + Boss×1 | ✅ 11/12/11/11/11/11/11 |
| 门 `$operation-block` 坐标与 Boss 未改 | ✅ 抽样比对 |
| Display `$kill` 去色名未改 | ✅ |
| 热更 `/dp reload` | ⚠ **未成功**（play 端无 OP 树权限；RCON `25575` 未启用 `ECONNREFUSED`）；文件已在 `server-runtime/plugins`→`../plugins` **同源 symlink**，测前请控制台/`dp reload` 一次 |
| 结构抽样 | ✅ Ash 假岔 kill×6 同组；Frost **返工** wave1×3→wave1b×2 开门（无跨组合并） |
| `ops.json` play+login | ✅ **`[]`** |
| commit/push | ✅ **未做** |

---

## 5. 给总控的结案转发正文（霜晶返工）

```
【结案 · 霜晶 door1 返工】priority=true
岗：余烬-插件
修：EmberDailyFrost/monster.yml 对齐锈轨链式
- wave1：左×3，$kill×3；start 仍 delay3s→wave1b；end 不开门
- wave1b：右×2，$kill×2；end 开门（z=16 AIR×9）+「冻台已清 · 冰闸开了」→wave2a
硬约束：小怪合计/去色名/门坐标/Boss 未动；无 kill-any
自检：YAML 合法；STATUS 已记修法；未 commit/push
测前控制台 dp reload。请只复测霜晶 door1：左刷≈3s 右刷 + 左右全清后门 AIR。
```

