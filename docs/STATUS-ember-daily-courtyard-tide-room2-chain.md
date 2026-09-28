# STATUS · 庭院 + 潮蚀 房2 start 链式

**日期：** 2026-09-28 10:25 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派活 · 庭院+潮蚀房2 start链式 · 方案 A】· `docs/design-ember-daily-courtyard-tide-room2-chain.md`（`f62bb35`）§4/§7  
**Verdict：** ✅ · `dp reload` ✅ 10:25:05 CST · **未 push**

---

## 一句话

庭院 / 潮蚀房2：`wave2a.start`→delay=2 `wave2b`；`wave2a.end` 仅提示+heal；开门仍 `wave2b.end`→door2→`boss_prep`（prep 零动）。

---

## 改动

| 路径 | 动作 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 仅 wave2a/2b start·end·文案 |
| `plugins/DungeonPlus/dungeon/EmberDailyTide/monster.yml` | 同上同构 |
| `docs/STATUS-ember-daily-courtyard-tide-room2-chain.md` | 本 STATUS |

（`server-runtime/plugins/DungeonPlus` 同 inode，无需另拷。）

| 线 | 链 | 开门 |
|----|----|------|
| 庭院 | 对射骷×2 start→涌尸×4 delay=2 | wave2b.end door2 z=25 → boss_prep |
| 潮蚀 | 浪矢×3 start→桥头尸×3 delay=2 | wave2b.end door2 z=38 → boss_prep |

文案 §4.3：涌尸/潮蚀尸将重叠压上；对射侧/对岸弓侧已清；重叠压上；wave2b.end 现网句保留。

---

## §7 施工勾选

- [x] EmberDaily 房2 链+文案  
- [x] EmberDailyTide 房2 链+文案  
- [x] 同 inode 同步（无需另拷）  
- [x] 静态：start delay=2；wave2a.end 无 door/prep/再拉；wave2b.end 仍拉 boss_prep  
- [x] boss_prep / wave1 / boss / 门坐标 / 刷点 / `$kill` 数量 · 零 diff（仅房2 start/end/文案+注释）  
- [x] 他五线 git diff 空  
- [x] 本 STATUS；未 push  

---

## 硬禁遵守

不碰 boss_prep/wave1/boss/门坐标/刷点/$kill 数量/他五线；禁 kill-any；零增怪。

## 热更

FIFO `dp reload` · **10:25:05** CST · 插件重载完毕 · EmberDaily / EmberDailyTide 初始化完毕。

## 验收

1. 对射/浪矢在场约 2s 内涌尸/桥头尸已出  
2. door2 仅 wave2b 清完后开；wave2a.end 无开门  
3. door2 后仍进 boss_prep 再 Boss  
4. 无 kill-any；他线零 diff  

## Git

未 push（交总控代推）· `ops.json=[]`
