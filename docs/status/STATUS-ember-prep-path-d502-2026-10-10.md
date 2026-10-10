# 状态 · D502：备药路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D502**  
**版本：** jar **1.65.193-d502.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberPrepPath` · SupplyService topUp · SessionService 钩 · Q01 offer · `preppath` |
| 禁 | 未改药价/伤害表 · 无新增 ActionBar · 非组合粘性/卷轴偏好 · 非工坊/入场 cue |

## 冒烟

| FreshQ1037 | `preppath` light/full/bare 聊天钮 · 满备进本前 `top-up +5 → 5 (spent 50)` · 轻备 `+3 → 3 (spent 30)` · 不自动备无补货日志 | **PASS** |
| 单元 | `EmberPrepPathTest` | **PASS** |

*D502 · 新备药自动补货。*
