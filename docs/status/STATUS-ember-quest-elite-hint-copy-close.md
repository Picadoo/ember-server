# STATUS — B2.114 CoreRpg quest.yml L389 精英试炼 hint · 结案

Date: 2026-10-01 13:11 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `d7cdcc7` · 批准 `f6531db` · 插件 `6817eb9` · 测 `13771d1`
- 报告：`docs/tests/TEST-B2.114-quest-elite-hint.md`

## Acceptance (测岗)
- 仅 live `plugins/CoreRpg/quest.yml` L389 1+/1-：「精英试炼（每周 1 次）」→「精英试炼（每人每周限通关 1 次）」，与荐案逐字一致（8 空格缩进）；按字节查无 tab/CR/行尾空格
- `/tmp/chk-b2114.js 6817eb9` → `only-L389 /chapters/9/steps/2/hint`；旧对旧/新对新 exit 1；三类变异 exit 4/3/1 均拦住；独立 deep diff 仅一处
- rg「每周 1 次」/B0.1 已清/票已废 0 命中
- 口径：QuestService:168 读入不截断，:293/:471/:569 整串发聊天，actionBar/PAPI 走 objective() 不含 hint；与 EliteService:40-53、ProgressService:471-481、B2.101/B2.102 一致
- 无脚本依赖旧 hint；HANDOFF §8 查密码 0；`ops.json` = `[]`

## Corrections (不影响结论)
- src 模板 quest.yml:389 仍为旧文；QuestService:124 只在 live 缺失时 saveResource，reload/重启不覆盖 live。B2.107 解挂时同步 src。
- chapters 为映射，9 是章节号键非下标；路径正确。
- 设计 §6 扫描表漏 L68「体力药拿去」、L140「留够体力」，均口径无误。

## Later candidates
- **B2.117**：quest.yml L377 done「试炼每周一次」（已排 B2.116 后）

## Next
- **B2.110**：TrMenu `ember_raid.yml` L76-77 进本 click tell → 策划出稿
