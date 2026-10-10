# 状态 · D520：签到路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D520**  
**版本：** jar **1.65.211-d520.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberSignPath` · SignService onJoin · Q01 offer @345L · `/corerpg p1 signpath` |
| 禁 | 未改签到表 · 无 ActionBar · 非在线路双胞 · 非组合粘性/入场 cue |

## 冒烟

| 路径 | 结果 |
|------|------|
| FreshQ1056 auto 重登 | `签到成功 · 本月第 1 次：余烬币 20、余烬经验 5` **PASS** |
| FreshQ1056m mute 重登 | 无提醒、无自动签 **PASS** |

单元：`EmberSignPathTest` PASS。

*D520 · 新签到打法。*
