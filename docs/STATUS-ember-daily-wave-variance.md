# STATUS · 日常七线波次节奏差异（挑刺必改4）

**日期：** 2026-09-28 05:39（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-daily-wave-variance.md` §3（已批准）；总控【派工 · 必改4 七线波次】  
**Verdict：** **✅ 七线 `EmberDaily*/monster.yml` 已按设计改 groups；门/Boss/体力/通关箱/Display 去色名未动；YAML 合法 · 无 `$kill-any`；小怪 11～12 + Boss；ops=`[]`；未 commit/push**

---

## 一句话

每线只改一房节拍：庭院房2 先对射后涌尸；焦骨房1 主甬+假岔计入 door1；地窖中层先弓；潮蚀房2 桥面先射；断塔环廊双侧对射；霜晶房1 左右交错；锈轨房2 支洞侧袭计入 door2。顺序组仿现有 wave2a/2b（单 `$kill`）。请派测抽检节拍。

---

## 1. 七线变更摘要

| 线 | DP | 改哪 | 新节奏 | 小怪合计 | 门条件（`$kill`） |
|----|-----|------|--------|----------|-------------------|
| 庭院 | `EmberDaily` | **房2** | 柱后骷×2 → delay4s 涌尸×4 | 5+2+4=**11** | door1 尸×5；door2 骷×2→尸×4 |
| 焦骨 | `EmberDailyAsh` | **房1** | 主甬尸×4 + 假岔尸×2 **同组** | 6+3+3=**12** | door1 焦骨尸×**6**（含假岔）；door2 尸×3→燃矢骷×3 |
| 地窖 | `EmberDailyCrypt` | **房2** | 高台誓印骷×3 → delay5s 地面窖卫×3 | 5+3+3=**11** | door1 窖卫×5；door2 誓印骷×3→窖卫×3 |
| 潮蚀 | `EmberDailyTide` | **房2** | 对岸浪矢骷×3 → delay4s 桥头潮蚀尸×3 | 5+3+3=**11** | door1 潮蚀尸×5；door2 浪矢骷×3→潮蚀尸×3 |
| 断塔 | `EmberDailySpire` | **房2** | 东西裂隙箭骷 2+2 → delay3s 卫尸×2 | 5+4+2=**11** | door1 卫尸×5；door2 裂隙箭骷×4→卫尸×2 |
| 霜晶 | `EmberDailyFrost` | **房1** | 左×3 立刻 + **start delay3s** 右×2 | 3+2+3+3=**11** | door1 霜晶尸×**5**（wave1 合并；wave1b 侧计×2）；door2 旧序 |
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

假岔几何：`STATUS-ember-daily-s2.md` x=3..9 · z=9..12 · 封墙 x=9。

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

### 2.6 霜晶 `EmberDailyFrost` · 房1 交错

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| **wave1** | **左** (-3,70,8)×2 (-3,70,14)×1 | 霜晶尸×**5**（含右） | start→**wave1b delay3**；清完门1 z=16 |
| **wave1b** | **右** (3,70,8)(3,70,6) | 霜晶尸×2（组内收尾） | 不单独开门 |
| wave2a/b（旧序保留） | 尸×3 → 霜矢骷×3 | … | 门2 z=34 → boss |

实现说明：禁 `$kill-any` 且左右同 Display 名，故 door1 用 wave1 单 `$kill×5` 合并计数；右波由 wave1 **start** 链 `delay=3` 刷出（左未必要清完即可侧袭），对齐设计「交错」。

### 2.7 锈轨 `EmberDailyRail` · 房2 支洞

| 组 | 刷点 | `$kill` | 链 |
|----|------|---------|-----|
| wave1（未改） | 主巷前段尸×5 | 锈轨尸×5 | 门1 z=15 |
| **wave2a** | 主巷尸×3 @ (-5,64,20)(0,64,22) | 锈轨尸×3 | → wave2b delay2 |
| **wave2b** | **支洞内** 矿矢骷×3 @ (8,64,20)(10,64,20) | 矿矢骷×3 | 门2 z=33 → boss（支洞计入） |

支洞几何：`STATUS-ember-daily-s4.md` x=4..12 · z=18..22。  
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
| `plugins/DungeonPlus/dungeon/EmberDailyFrost/monster.yml` | 房1 左右交错 + wave1b |
| `plugins/DungeonPlus/dungeon/EmberDailyRail/monster.yml` | 房2 支洞骷计入 door2 |
| `docs/STATUS-ember-daily-wave-variance.md` | 本文件 |

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
| 结构抽样 | ✅ Ash 假岔 (8,65,10)+kill×6；Frost wave1b delay3 + kill×5 合并 |
| `ops.json` play+login | ✅ **`[]`** |
| commit/push | ✅ **未做** |

---

## 5. 给总控的结案转发正文

```
【结案 · 必改4 七线波次】priority=true
岗：余烬-插件
DP：七线 EmberDaily{,Ash,Crypt,Tide,Spire,Frost,Rail}/monster.yml 已按 design §3 改一房节拍
- 庭院房2：先骷×2→尸×4｜焦骨房1：主甬4+假岔2 计入 door1｜地窖中层：先骷×3→尸×3
- 潮蚀房2：先浪矢×3→尸×3｜断塔环廊：箭骷2+2→卫尸×2｜霜晶房1：左3+delay3s右2｜锈轨房2：主巷尸×3→支洞矿矢×3 计入 door2
硬约束：门/Boss/体力/箱/去色名未动；仅 $kill（无 kill-any）；小怪 11～12+Boss
自检：YAML 合法；ops=[]；热更需测前控制台 dp reload（RCON 关）
未 commit/push。请派测：七线各打一房节拍可辨 + 焦骨假岔/锈轨支洞计入开门。
```

