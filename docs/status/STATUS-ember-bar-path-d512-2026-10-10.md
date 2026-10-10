# 状态 · D512：药栏路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D512**  
**版本：** jar **1.65.203-d512.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberBarPath` · `EmberSupplyService.emptyHotbarFor` · Q01 offer @255L · `/corerpg p1 barpath` |
| 禁 | 未改药价 · 无 ActionBar · 非备药双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟 FreshQ1048b

| 路径 | 落点 | 结果 |
|------|------|------|
| key5 | hotbar index 4（数字 5） | **PASS** |
| left | hotbar index 1（数字 2） | **PASS** |
| right | hotbar index 8（数字 9） | **PASS** |

单元：`EmberBarPathTest` slotOrder PASS。

*D512 · 新药栏落点打法。*
