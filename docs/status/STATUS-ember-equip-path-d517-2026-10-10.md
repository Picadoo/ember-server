# 状态 · D517：换装路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D517**  
**版本：** jar **1.65.208-d517.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberEquipPath` · `offerUpgrade` `adjust` · Q01 offer @315L · `/corerpg p1 equippath` |
| 禁 | 未改数值 · 无 ActionBar · 非补领/工坊双胞 · 非组合粘性/入场 cue |

## 冒烟 FreshQ1053

| 项 | 结果 |
|----|------|
| keep/quick/bold UX | 设置文案 **PASS** |
| 单元 adjust | KEEP:AUTO→ASK · BOLD:ASK→AUTO · ASK_SWAP 不变 **PASS** |

注：admin `give`/`givedup` 直塞背包不走 `offerUpgrade`；真掉落交付路径已钩 adjust。

*D517 · 新换装打法。*
