# 状态 · D505：倒下退药路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D505**  
**版本：** jar **1.65.196-d505.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberRefundPath` · deathRefund 钩 · Q01 offer · `refundpath` |
| 禁 | 未抬退药表 · 无 ActionBar · 非组合粘性/卷轴/备药双胞 · 非工坊/入场 cue |

## 冒烟

| FreshQ1041 | `refundpath` light→上限2 · bare→0 · full→5 · 聊天钮 | **PASS** |
| 单元 | `EmberRefundPathTest` effectiveMax 永不抬表 | **PASS** |

*D505 · 新倒下退药获取偏好。*
