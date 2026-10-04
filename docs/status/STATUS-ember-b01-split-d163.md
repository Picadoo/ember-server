# D163：B01「拆分」净 +50% + 开局药格文案（CoreRpg 1.65.2）

## 内容
- **B01：** 天赋「拆分」`t2c` 改为 `mods: {dmg_split: 1.50, dmg_affix_body: 0.80}`。`EmberGrowthService.classMult`：分身 = `dmg_affix × dmg_split`，本体 = `dmg_affix × dmg_affix_body`。文案与实现一致（分身 +50% / 本体 −20%）。模拟结论：通关率几乎不变，拆分仍是第二排死选项（见 `tools/p1sim/out-build-diversity-b01-split.md`）。
- **开局药格：** `potionCheck` 改为「回复药在快捷栏第 5–9 格」（真人试玩建议，原「第 N 格」与低血提示数字键不一致）。
- **growth.yml** `version: 2`（两份同源）。单测 227 / 0（含 `b01SplitMeansNetPlus50OnClones_D163`）。JDK8 major 52。

## 部署
- 构建：`JAVA_HOME=tools/jdk8u504-b01` → `CoreRpg/target/CoreRpg.jar`。备份 `/workspace/backup/CoreRpg-1.65.2.jar`。
- 线上：等 `COORD-bot-kite` DONE 后再重启（本窗不抢服）。

## 下一测号
部署后短冒烟 FreshQ95+。下一裁决 D164。
