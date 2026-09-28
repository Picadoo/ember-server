# STATUS · B2.7 主线日票→体力文案 B

| 字段 | 值 |
|---|---|
| 时间 | 2026-09-28 23:01 Asia/Shanghai |
| 批准 | `a6c48bc` |
| 设计 tip | `b7ccbb8` · `docs/design-ember-quest-stamina-copy.md` |
| 范围 | live 与 src 镜像各 6 处玩家可见字符串 |
| 状态 | **PASS · 勾销**（测 `f2edc25` · tip 已 push） |

## 双路径 6 处

| # | 行（live/src） | 旧 → 新 |
|---|---:|---|
| 1 | 68 / 68 | `日票拿去` → `体力药拿去` |
| 2 | 73 / 73 | `每日 3 张免费日票，主线再送 1 张` → `日回体力约可刷 3 次日常，主线再送 1 瓶体力药` |
| 3 | 81 / 81 | `需日票` → `耗体力` |
| 4 | 140 / 140 | `带上周票` → `留够体力` |
| 5 | 243 / 243 | `每日 3 张免费日票` → `日回体力约可刷 3 次日常` |
| 6 | 355 / 355 | `每日免费日票` → `日回体力` |

路径：
- `plugins/CoreRpg/quest.yml`（live）
- `CoreRpg/src/main/resources/quest.yml`（src 镜像）

## 验收

- 两路径内容一致，均为 6 insertions / 6 deletions；`rg '日票|周票'` 两路径：**EMPTY**。
- 禁项（步骤 `type/event/count/mobs/complete_on/items` 键与数量、体力 cost、进本、TrMenu、cash、DP/MM/loot）：**EMPTY / 零 diff**。
- 热更证据（2026-09-28 23:01 Asia/Shanghai）：已执行 `printf 'corerpg reload\n' > server-runtime/console.in`；`server-runtime/logs/latest.log` 与 `stdout.log` 最新记录停在 22:57，未观察到本次 reload 回执（FIFO 另有 `cat` reader），需服侧复核。
- Git：仅本 STATUS 与两份 `quest.yml` 纳入本提交；**未 push**。
