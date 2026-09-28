# STATUS · 精英周本厅一链式 + 消 kill-any

**日期：** 2026-09-28 09:16 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派工 · 精英周本厅一链式+消 kill-any】· `docs/design-ember-elite-wave-variance.md` §6（零碰 option）  
**Verdict：** ✅ · `dp reload` ✅ 09:16:43 CST · **未 push**

---

## 一句话

`EmberEliteWeekly` 厅一：炽尸×3（wave1）start→delay2 骨刺×2（wave1b）；传厅二挂 wave1b.end；厅二双 `$kill` AND；全本无 kill-any；wave3 零 diff。

---

## 改动

| 路径 | 动作 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml` | 仅本文件 |
| `docs/STATUS-ember-elite-wave-variance.md` | 本 STATUS |

| 组 | 内容 |
|----|------|
| wave1 | 炽尸×3 @ (-30,70,270)；`$kill{余烬试炼·炽尸;3}`；start→文案+wave1b delay=2；end 仅提示+heal（**无** tp/wave2） |
| wave1b | 骨刺×2 @ (-28,70,272)；`$kill{余烬试炼·骨刺;2}`；end：heal+tp (-4,72,270)+wave2 delay=3 |
| wave2 | 删 kill-any；双 `$kill` AND（蛮纹×1 + 混纹×2）；刷点/end tp/wave3 **零 diff** |
| wave3 | **零 diff** |

文案 §6.3：炽尸贴上/骨刺将交错压上；厅一炽尸侧已清；骨刺重叠压上；第一层词缀散了；厅二/三沿用现网句。

**已消：** `$kill-any`（厅一×5、厅二×3）。

**零碰 option：** dungeon-start 仍调 `wave1`。

---

## 硬禁遵守

option / EmberWeekly / 日常七线 / MM HP / 传送落点 / 厅二 start 链式 / 掉落体力 —— 未动。

## 热更

FIFO `dp reload` · **09:16:43** CST · `[DungeonPlus] 插件重载完毕` · `EmberEliteWeekly` 初始化完毕。

## 验收

1. 厅一炽尸刷出约 2s 内骨刺重叠；炽尸侧清完**不传**厅二  
2. 骨刺清完才 tp (-4,72,270) + wave2  
3. 厅二蛮纹+混纹同刷；双 `$kill` AND；无 kill-any；无 start 链式  
4. 厅三执行官零 diff  

## Git

未 push（交总控推）· `ops.json=[]`
