# 状态 · D523：邮件路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D523**  
**版本：** jar **1.65.214-d523.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberMailPath` auto/ask/mute · `claimAllAttachments`/`countClaimable` · join+deliver · Q01 offer · `mailpath` |
| 禁 | 未改邮件模板表 · 无 ActionBar · 非补领/签到/图录双胞 · 非组合粘性/入场 cue |

## 冒烟

| 项 | 结果 |
|----|------|
| FreshQ1060 AUTO + `mail send maintenance_comp` | 邮件·自动 已领 1 封附件 PASS |
| FreshQ1060a ASK + `vip_daily_gift` | 邮件有未领附件（1）[一键领取] PASS |
| FreshQ1060m MUTE + `pass_track_free` | 仅「收到新邮件」、无路径提醒/自动 PASS |
| EmberMailPathTest | PASS |

*D523 · 新邮件附件领奖打法 · ≠ Claim/Sign/Codex 双胞。*
