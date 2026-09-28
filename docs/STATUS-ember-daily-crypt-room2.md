# STATUS · 地窖房2链式重叠

**日期：** 2026-09-28 08:41 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【批准 · 地窖房2链式】· `docs/design-ember-daily-crypt-room2.md`（方案 A）  
**Verdict：** ✅ **施工+验收 PASS**（`dd99538` · 测报另 commit）

---

## 一句话

地窖 `EmberDailyCrypt` 房2：**仍先刷高台誓印骷**；`wave2a.start` 即 `delay=2` 刷地面窖卫尸（原 end `delay=5` 等清）；`wave2a.end` 仅「高台箭侧已清」+heal；**door2 + boss** 仍挂 `wave2b.end`。坐标 / wave1 / 门 / Boss / option **零动**；其它六线 **零 diff**。

---

## 改动

| 路径 | 动作 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyCrypt/monster.yml` | 仅 wave2a/wave2b 触发与文案（§6.4） |
| `docs/STATUS-ember-daily-crypt-room2.md` | 本 STATUS |

### 链式

| 组 | 改后 |
|----|------|
| wave2a.start | `【中层·高台】誓印骷对射 —— 窖卫将交错从地面压上` + `$monstergroup{wave2b;delay=2}` |
| wave2a.end | `高台箭侧已清` + heal；**禁** door / boss / 再调 wave2b |
| wave2b.start | `【中层·地面】窖卫尸重叠压上 —— 清完开底层门` |
| wave2b.end | 底层门 AIR×9 @ z=40 y66 + heal + boss delay=3（**未改**） |

**未改：** wave1、刷点、`$kill` 去色名/数量、门坐标、boss、MM id（仍 EmberDailyCrypt*）、option；禁止先尸后骷。

---

## 热更

- **时间：** 2026-09-28 **08:41:26** CST  
- **命令：** FIFO → `dp reload`  
- **结果：** `[DungeonPlus] 插件重载完毕`；`[EmberDailyCrypt] 地牢内容初始化完毕`

---

## 验收要点（设计 §10 · ✅ PASS · `STATUS-ember-daily-crypt-room2-test.md`）

1. 下阶后仍先见高台誓印骷，约 **2s** 内地面窖卫出现  
2. 底层门仅在 wave2b `$kill` 窖卫×3 后开；wave2a 清完不开门  
3. 无跨组 `$kill` / kill-any；无 Boss prep  
4. diff 仅本 monster.yml（+STATUS）

---

## Git

- 仅 add：本 monster.yml + 本 STATUS  
- **未 push**（交总控推）  
- `ops.json` = `[]`

## Blocker

无。进本短抽交测岗。

## Checklist

- [x] 设计批准
- [x] `EmberDailyCrypt/monster.yml` 房2链式落地 + dp reload
- [x] 非地窖六线零 diff
- [x] §10 短抽 **PASS**（`STATUS-ember-daily-crypt-room2-test.md`）
