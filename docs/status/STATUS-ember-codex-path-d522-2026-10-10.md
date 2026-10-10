# 状态 · D522：图录路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D522**  
**版本：** jar **1.65.213-d522.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberCodexPath` · claimCodexStages · join/loadout/settle · Q01 offer · `codexpath` |
| 禁 | 未改阶段表 · 无 ActionBar · 非签到/在线/补领双胞 · 非组合粘性/入场 cue |

## 冒烟 FreshQ1059

| 路径 | 结果 |
|------|------|
| `/corerpg p1 codexpath` | `选图录阶段奖励领取方式` + `[自动] [提醒] [静默] [取消]` **PASS** |
| `codexpath auto` | `图录 → 图录·自动 · 图录阶段达标时自动领取余烬币` **PASS** |
| `codexpath ask` | `图录 → 图录·提醒 · 达标时聊天提醒，点按钮领取（默认）` **PASS** |
| `codexpath mute` | `图录 → 图录·静默 · 达标不刷提醒；可 /corerpg p1 codex claim` **PASS** |
| 预检 | Enabling `1.65.213-d522` · MySQL×2 · SEVERE=0 **PASS** |

单元：`EmberCodexPathTest` PASS（1/0）。

*D522 · 新图录领奖打法。*
