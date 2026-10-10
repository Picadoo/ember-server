# 状态 · D513：招募路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D513**  
**版本：** jar **1.65.204-d513.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberRecruitPath` · RecruitService join auto-accept · onJoinShow mute · Q07 offer @180L · `/corerpg p1 recruitpath` |
| 禁 | 未改掉落 · 无 ActionBar · 非 ModePath 双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟 FreshQ1049a/b

| 路径 | 结果 |
|------|------|
| open | 队长自动同意 · B「已加入」· A「招募·敞开 已自动同意」 **PASS** |
| mute | 路径设置 + glance UX **PASS** |
| gate | 单元 `wantsAutoAccept=false` **PASS** |

*D513 · 新团本招募同意打法。*
