# STATUS · 焦骨房2链式重叠

**日期：** 2026-09-28 08:31 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【批准 · 焦骨房2链式】· `docs/design/design-ember-daily-ash-room2.md` §14（方案 A）  
**Verdict：** ✅ **施工+验收 PASS**（`2ad874e` · 测报另 commit）

---

## 一句话

焦骨 `EmberDailyAsh` 房2：`wave2a.start` 即 `delay=2` 刷 `wave2b`（尸与燃矢重叠压）；`wave2a.end` 仅提示+heal；**door2 + boss** 仍挂 `wave2b.end`。坐标 / wave1 假岔 / 门位 / Boss / MM / option **零动**。非焦骨六线 YAML **零 diff**。

---

## 改动

| 路径 | 动作 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyAsh/monster.yml` | 仅 wave2a/wave2b 触发与文案（§6.4） |
| `docs/status/STATUS-ember-daily-ash-room2.md` | 本 STATUS |

### 链式（对齐断塔）

| 组 | 改后 |
|----|------|
| wave2a.start | `【甬道·弯折】焦骨尸来袭 —— 燃矢将交错贴脸压上` + `$monstergroup{wave2b;delay=2}` |
| wave2a.end | `弯折尸侧已清` + heal；**禁** door / boss / 再调 wave2b |
| wave2b.start | `【甬道·后段】燃矢骷重叠压上 —— 清完开鼓室门` |
| wave2b.end | 鼓室门 AIR×9 @ z=36 + heal + boss delay=3（**未改**开门/boss） |

**未改：** wave1 / 假岔 / 刷点坐标 / `$kill` 数量与去色名 / 门坐标 / boss / option / 其它六线。

---

## 热更

- **时间：** 2026-09-28 **08:31:53** CST  
- **命令：** FIFO `server-runtime/console.in` → `dp reload`  
- **结果：** `[DungeonPlus] 插件重载完毕`；`[EmberDailyAsh] 地牢内容初始化完毕`

---

## 验收要点（设计 §10 · ✅ PASS · `docs/status/STATUS-ember-daily-ash-room2-test.md`）

1. 进弯折后约 **2s** 内燃矢出现（不必等尸清完）  
2. 鼓室门仅在燃矢 `$kill`×3 后开；wave2a 清完**不开门**  
3. 房1 假岔仍计入 door1（不扫岔开不了门）  
4. Boss 仍空厅直接焦核（本轮无 prep）  
5. `git diff` 仅本 monster.yml（+STATUS）；Frost/Rail/Daily/Tide/Spire/Crypt 零 diff

---

## Git

- 仅 add：本 monster.yml + 本 STATUS  
- **未 push**（交总控推）  
- `ops.json` = `[]`

## Blocker

无。进本短抽交测岗。

## Checklist

- [x] 设计批准
- [x] `EmberDailyAsh/monster.yml` 房2链式落地 + dp reload
- [x] 非焦骨六线零 diff
- [x] §10 短抽 **PASS**（`docs/status/STATUS-ember-daily-ash-room2-test.md`）
