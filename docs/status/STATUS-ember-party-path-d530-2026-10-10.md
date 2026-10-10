# 状态 · D530：组队路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D530**  
**版本：** jar **1.65.221-d530.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberPartyPath` open/gate/busy · FriendService invite wire · Q01 offer · `partypath` |
| 禁 | 未改战力 · 无 ActionBar · 非招募/好友/盟约双胞 · 非组合粘性/入场 cue |

## 冒烟

| 项 | 结果 |
|----|------|
| FreshQ1067/a OPEN | 组队·敞开 已自动接受 FreshQ1067a 的组队邀请 PASS |
| FreshQ1067 BUSY + invite | 「对方开启了组队·静拒，暂不接受组队邀请。」 PASS |
| FreshQ1067 GATE + invite | 「邀请你组队 [入队] [改路径]」 PASS |
| EmberPartyPathTest | PASS |
| MySQL×2 · SEVERE0 | Enabling 1.65.221-d530 · CoreRpg/CoreGacha MySQL connected |

*D530 · 新好友组队打法 · ≠ Recruit/Friend/Guild 双胞。*
