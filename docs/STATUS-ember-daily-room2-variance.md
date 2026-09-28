# STATUS · 日常第二房 / Boss 前门压试点

**日期：** 2026-09-28（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** `docs/design-ember-daily-room2-variance.md` §5.1–5.3（**已批准 · 施工落地 · §8 验收 PASS**）

## Verdict

**✅ 三线 YAML 已落地 + `dp reload` PASS**（庭院 / 潮蚀 / 断塔）  
§8 短抽验收仍待测本岗（未做进本实打）。

| 线 | 杠杆 | 要点 |
|----|------|------|
| EmberDaily | Boss 前门压 | door2→`boss_prep` 门槛尸×2→Boss；wave1=4；房2不动 |
| EmberDailyTide | Boss 前门压 | door2→`boss_prep` 门槛浪矢×2→Boss；wave1=4；房2不动 |
| EmberDailySpire | 房2链式重叠 | wave2a start→delay2 wave2b；开门仅在 wave2b；坐标数量不动 |

- 霜晶 / 锈轨 / 焦骨 / 地窖：**零 diff**（git 确认）
- 不加血；不动体力/箱/掉落/Boss HP
- 无 `$kill-any`；`$kill` 未跨组合并
- `ops.json=[]`（server-runtime / login-runtime）

## Diff 范围（仅 3 yaml + 本 STATUS）

| 路径 | 变更摘要 |
|------|----------|
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | wave1 删中央 `(0,65,10)` → `$kill`×4；wave2b end → `boss_prep` delay=2；新组 `boss_prep` 尸×2 @ `(-3,65,27)` `(3,65,27)` → boss delay=2 |
| `plugins/DungeonPlus/dungeon/EmberDailyTide/monster.yml` | wave1 删 `(0,64,14)` → `$kill`×4；wave2b end → `boss_prep` delay=2；新组 `boss_prep` 浪矢×2 @ `(-3,64,41)` `(3,64,41)` → boss delay=2 |
| `plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml` | wave2a.start 链式 `$monstergroup{wave2b;delay=2}`；wave2a.end 仅提示+heal（禁 door/boss）；wave2b.end 顶门×9 AIR + boss delay=3 |
| `docs/STATUS-ember-daily-room2-variance.md` | 本结案 STATUS |

**未改线：** EmberDailyFrost / EmberDailyRail / EmberDailyAsh / EmberDailyCrypt（及非日常线）零 diff。

## 组表（落地）

### EmberDaily（庭院）

| 组 | `$kill` | 开门 / 下一段 |
|----|---------|----------------|
| wave1 | 灰烬庭院僵尸 ×4 | door1 @ z=13 → wave2a |
| wave2a | 灰烬庭院骷髅 ×2 | → wave2b delay=4（**不动**） |
| wave2b | 灰烬庭院僵尸 ×4 | door2 @ z=25 → **boss_prep** delay=2 |
| boss_prep | 灰烬庭院僵尸 ×2 | → boss delay=2 |
| boss | 灰烬庭院蛮兵 ×1 | COMPLETE |

小怪合计 **4+2+4+2=12** + Boss。

### EmberDailyTide（潮蚀）

| 组 | `$kill` | 开门 / 下一段 |
|----|---------|----------------|
| wave1 | 潮蚀尸 ×4 | door1 @ z=18 → wave2a |
| wave2a | 浪矢骷 ×3 | → wave2b delay=4（**不动**） |
| wave2b | 潮蚀尸 ×3 | door2 @ z=38 → **boss_prep** delay=2 |
| boss_prep | 浪矢骷 ×2 | → boss delay=2 |
| boss | 潮闸蛮兵 ×1 | COMPLETE |

小怪合计 **4+3+3+2=12** + Boss。

### EmberDailySpire（断塔）

| 组 | `$kill` | 链 / 门 |
|----|---------|---------|
| wave1 | 断塔卫尸 ×5 | door1 → wave2a（**不动**） |
| wave2a | 裂隙箭骷 ×4 | **start** → wave2b delay=2；end 仅提示+heal |
| wave2b | 断塔卫尸 ×2 | 顶门 ×9 AIR @ z=4 y70 → boss delay=3 |
| boss | 断塔守望 ×1 | COMPLETE（**不动**） |

小怪合计 **5+4+2=11** + Boss。

## 热更

| 项 | 结果 |
|----|------|
| 方式 | play FIFO `server-runtime/console.in` ← `dp reload` |
| 时间 | **2026-09-28 08:08:52 CST** |
| 日志 | `[DungeonPlus] 插件重载完毕`；`EmberDaily` / `EmberDailyTide` / `EmberDailySpire` 地牢内容初始化完毕 |
| RCON | 未用（`enable-rcon=false` 惯例） |

## 验收要点（§8 · ✅ PASS · `docs/STATUS-ember-daily-room2-variance-test.md`）

1. **骨架**：三线仍 ≥2 真门 → Boss；铁栅语义在  
2. **庭院前压**：door2 后先门槛尸×2，清完才蛮兵  
3. **潮蚀前压**：door2 后先门槛浪矢×2，清完才潮闸蛮兵（射 vs 贴可区分）  
4. **断塔重叠**：对射刷出后约 2s 内卫尸在场；顶门仅 wave2b `$kill` 后开  
5. **`$kill` 纪律**：无跨组合并；无 kill-any  
6. **与必改4不冲**：庭院房2 对射→涌；潮蚀房2 桥射→冲；断塔双侧箭坐标/数量仍在  
7. **数值**：无加血；体力/箱/掉落/Boss HP 未改  
8. **范围**：霜晶/锈轨/焦骨/地窖 YAML 零 diff  

## Checklist

- [x] 设计批准
- [x] DP `monster.yml` 三线落地 + `yaml.safe_load` OK
- [x] FIFO `dp reload` PASS（08:08:52 CST）
- [x] 霜晶/锈轨/焦骨/地窖 git diff 空
- [x] `ops.json=[]`
- [x] §8 短抽验收 **PASS**（`STATUS-ember-daily-room2-variance-test.md` · 2026-09-28 08:28 CST）

## Blocker

无。§8 已 PASS；非试点扩展另开设计稿，不在本 STATUS 施工范围。
