# RELEASE · CoreRpg 1.65.43（D211，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D211 · 技能组 S1（D210 定稿落地） |
| 内容 | 输入分流（潜行+F→烬突 Q02+）+ 烬突（共享充能、4 格、1.5B×3 首领×0.5）+ 烬斩三形状符文（Q04+）+ 技能页 TrMenu + hub 入口；火痕步 defer S2；守招/灰印/聚火未做 |
| 不变 | bv57；无永久乘区；挂机不放烬突 |
| 代码 | `EmberDash` / `EmberSkillKit`；`SkillService` / `FlexSkillService` / `EmberAfkService` / `EmberRunService.isRunBoss` / `EmberCounters` / PAPI / TrMenu |
| 测试 | unit **363 / 0**（+`EmberSkillKitTest`） |
| 配置 | 无数值改；菜单 `ember_skill_kit.yml` + hub `S` |

| jar sha256 | `766a50d7fc16d144b53f56cd53018ab64f236c1e16f2750edf1b53d4a171f9de` |
| 备份 | `/workspace/backup/CoreRpg-1.65.42-pre-1.65.43.jar` |

| 服务器 | play PID 见启动后；Enabling CoreRpg v1.65.43；两条 MySQL connected；SEVERE 0 |
| 冒烟 | FreshQ771 kit/shape/菜单 PASS；FreshQ772 Q01 首通 settle PASS |
