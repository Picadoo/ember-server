# 状态 · TIP-EX 占点路径选打菜单化抽测

> **抽测 PASS · CLOSED live** · feat `ad2720cfe` · tip批 `6b5357baf` · menu `ddb1f6ce6` · jar `1.65.874-d13381.local` · 2026-10-15 15:05 Asia/Shanghai · ancestor TIP-EW CLOSED live `2efc3058c`

**裁决：** **PASS** · jar live `CoreRpg 1.65.874-d13381.local` · TrMenu **129** · MySQL connected · bv123 · 证据 `/workspace/tmp/tip-ex-hold-path/`

## 必验

| # | 项 | 结果 |
|---|----|------|
| 1 | 菜单 `ember_p1_hold_path` 存在；零改 `ember_p1_beacon_path` | **PASS** · beacon md5=`55457ccae0b53e78977f4c9e182bf6aa` · hold md5=`90acf254c41e96d8cc123efc9956559a` |
| 2 | jar/bv live 1.65.874-d13381.local · ECONOMY_BV 123 | **PASS** · sha256 `3c16b01347fa…` · Enabling CoreRpg v1.65.874-d13381.local |
| 3 | 单测 HoldPathPick 8/8 + HoldPath 1/1 + BeaconPathPick 8/8 | **PASS** |
| 4 | offerPick→openPathPick(`/ember_p1_hold_path`)；禁 ConfirmTokens 教裸 holdpath 主 UI | **PASS** 硬红门 |
| 5 | 局内「占点」仍 `/corerpg p1 hold`；「改路径」开薄页 PATH_PICK_CMD | **PASS** |
| 6 | 零抬伤害/CD/掉落/体力价；零改 D566 占点语义 | **PASS** |

## 旁注
- W1b 已于 `ddb1f6ce6` 先落；W1a feat `ad2720cfe`；本窗总控抽测并收口 CLOSED
- Affix Pack 仍 FROZEN；L13 stage1 HOLD
- 下一窗 Guard 等另窗证据逐支（禁全族大扫 · 禁与 Guard 等同窗 · 勿默念 Pace）
