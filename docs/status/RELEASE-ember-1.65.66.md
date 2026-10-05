# RELEASE · CoreRpg 1.65.66（D240 / ARCH S3-11）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.66** · deliverable **D240** · `balance_version` **58** |
| 代码 | `p1/EmberRunPapi`（14 节 + 有序 route）；`CorePapi` + `CorePapiAccount` / `CorePapiKit` / `CorePapiProgress` / `CorePapiStamina`；`EmberRunService.placeholder` / `CoreRpgExpansion` 薄分发 |
| 单测 | `EmberRunPapiTest` ×5 + `CorePapiTest` ×3 → 496/0（JDK8） |
| 冒烟 | 17/0/0：756 键快照跨 jar（FreshQ824）+ 跨会话（FreshQ827 第 2→3 会话）751 键逐字相同；MySQL×2；SEVERE 0 |
| 回滚 | `/workspace/backup/CoreRpg-1.65.65-pre-1.65.66.jar` |
| 余部 | 词缀原语；S4 装备结构 |
