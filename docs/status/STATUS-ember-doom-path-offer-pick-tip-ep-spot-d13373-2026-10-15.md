# 状态 · TIP-EP 灾临路径选打菜单化抽测

> **抽测 PASS · CLOSED live** · feat `274c3299f` · tip批 `1064e9b7f` · menu `635d5b670` · jar `1.65.866-d13373.local` · 2026-10-15 07:50 Asia/Shanghai · ancestor TIP-EO CLOSED live `f4fcc3e60`

**裁决：** **PASS** · jar live `CoreRpg 1.65.866-d13373.local` · TrMenu **121** · MySQL connected · bv123 · 证据 `/workspace/tmp/tip-ep-doom-path/`

## 必验

| # | 项 | 结果 |
|---|----|------|
| 1 | 菜单 `ember_p1_doom_path` 存在；零改 `ember_p1_nest_path` | **PASS** · nest md5=`7a69bcd8a3bab01a9832020f4712de91`（TIP-EO 基线） |
| 2 | jar/bv live 1.65.866-d13373 · ECONOMY_BV 123 | **PASS** |
| 3 | 单测 DoomPathPick + NestPathPick + RunPapi | **PASS** |
| 4 | offerPick→openPathPick；禁 ConfirmTokens 教裸 doompath | **PASS** 硬红门 |
| 5 | 局内「奔赴」仍 `/corerpg p1 doom`；「改路径」开薄页 | **PASS** |
| 6 | 零抬伤害/CD/掉落/体力价；零改 D596 语义 | **PASS** |

## 旁注
- W1b 已于 `635d5b670` 先落；本窗补 W1a 插件并收口 CLOSED
- Affix Pack 仍 FROZEN；L13 stage1 HOLD
