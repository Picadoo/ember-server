# 状态 · D507：败退路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D507**  
**版本：** jar **1.65.198-d507.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberFailPath` · failRefund 钩 · Q07 offer · `failpath` |
| 禁 | 未抬 fail_refund · 无 ActionBar · 非倒退药双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟

| FreshQ1043 | `failpath` light→25%/例退7 · skip→0% · keep→50%/例退15 · clear | **PASS** |
| 单元 | `EmberFailPathTest` effectiveShare 永不抬表 | **PASS** |

*D507 · 新失败退体获取偏好。*
