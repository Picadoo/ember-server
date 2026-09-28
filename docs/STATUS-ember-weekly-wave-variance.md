# STATUS · 周本中核链式重叠

**日期：** 2026-09-28 09:03–09:04 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派工 · 周本中核链式重叠】· `docs/design-ember-weekly-wave-variance.md` §6  
**Verdict：** ✅ · `dp reload` ✅ 09:03:58 CST · **未 push**

---

## 一句话

`EmberWeekly` 中核：拆旧 `wave2`（kill-any×5）→ `wave2a` 尸×2 + `wave2b` 骷×3 链式重叠；传深室仍挂 `wave2b.end`；wave3/boss 零 diff。

---

## 改动

| 路径 | 动作 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml` | 仅中核链 |
| `docs/STATUS-ember-weekly-wave-variance.md` | 本 STATUS |

| 组 | 内容 |
|----|------|
| wave1.end | 调组 `wave2`→**wave2a**（tp/heal 不动） |
| wave2a | 尸×2 @ (-40,68,308)；`$kill{深核廊僵尸;2}`；start→message+wave2b delay=2；end 仅提示+heal |
| wave2b | 骷×3 @ (-42,68,312)；`$kill{深核廊骷髅;3}`；end：heal+tp (-40,63,340)+wave3 delay=3 |
| wave3 / boss | **零 diff** |

文案 §6.3：僵尸贴上/骷髅将交错压上；中核尸侧已清；骷髅重叠压上；中核已清·转入深室。

**已消：** `$kill-any`。

---

## 硬禁遵守

EmberEliteWeekly / 日常七线 / option / MM Health / 传送落点坐标 / 掉落体力 —— 未动。

## 热更

FIFO `dp reload` · **09:03:58** CST · `[DungeonPlus] 插件重载完毕` · EmberWeekly 初始化完毕。

## 验收

1. 入中核约 2s 内骷重叠；尸侧清完**不传**深室  
2. 骷清完才 tp 深室 + wave3  
3. 无 kill-any；甲→乙终局仍旧  

## Git

未 push（交总控推）· `ops.json=[]`
