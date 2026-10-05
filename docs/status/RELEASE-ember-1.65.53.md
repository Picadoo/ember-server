# RELEASE · CoreRpg 1.65.53（D227）

| 项 | 内容 |
|---|---|
| 主题 | 徽记 option B：首领残响厅扩至 Q01–Q07，共用周领奖帽 3（周发放总量不变） |
| 版本 | CoreRpg **1.65.53**；`balance_version` **58**（S18 `insignia`/`weekly` 数量不变；economy bv 与 runs 对齐） |
| 代码 / 配置 | `echo_q05`/`echo_q06`/`echo_q07` 入 `ember-v1-runs.yml`（共用 `p4_echo_claim`）；进阶模式页 7 厅；`modeUnlock` / REG S18；`insignia.py` 默认 maps=q01–q07 |
| 不变 | S18 每次 2 枚、每周 3 次；不发明兑换口；技能 / Cosmetics 未动 |
| 测试 | unit 401/0（JDK8）；冒烟 FreshQ787 PASS（modes Q05/Q07 + echo bind + echo_q01 claim 1/3） |
| 文档 | `STATUS-ember-insignia-b-1.65.53.md`；REG S18；source-table D227 |
