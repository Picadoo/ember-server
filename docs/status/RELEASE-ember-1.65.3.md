# 发布凭证 · CoreRpg 1.65.3（2026-10-04）

可复查的「此刻线上是什么」清单（格式同 `RELEASE-ember-1.65.1.md`）。1.65.2（D163）没有单独部署，内容包含在本版。

## 线上状态（核对于 2026-10-04 13:50 Asia/Shanghai）

| 项 | 值 |
|---|---|
| 源码（CoreRpg 代码 / 菜单 / 规则） | 本版提交见下表；`git archive` 重编后与线上 jar 逐文件一致（见文末） |
| CoreRpg 版本 | **1.65.3**（`plugin.yml`、`pom.xml`） |
| CoreRpg JAR sha256 | `3a22e1085bb0081262bd0952379bb9492a529cd0de252682b52e4bb482e2d260` |
| CoreRpg class major | **52**（JDK 8，`tools/jdk8u504-b01`） |
| CoreGacha 版本 / sha256 | 1.0.1 / `4526359f4e961733287ca8bc0674b03c4c844d58fde2daa615f83180b705b2c6`（未换） |
| 部署时间 | **13:41 CST**（PID 587771） |
| Paper | 1.12.2（`server-runtime/paper-custom.jar`） |
| `balance_version` | **29**（不变） |
| growth.yml `version` | **3** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.1-pre-1653.jar`（1.65.1，sha256 `f415f897386404f2c649f8c435daccdb0b157875a296d069319a71c74e482504`）；回滚时同时把 `plugins/CoreRpg/ember-v1-growth.yml` / `ember-v1-runs.yml` 换回 1.65.1 版（`git show 64a0593:CoreRpg/src/main/resources/<文件>`），否则 1.65.1 不认识 `dmg_affix_shield` 等键，t2c 会变成无效果 |
| 故障注入 | **关**（进程环境无 `CORERPG_TEST_FAULTS`） |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected`；SEVERE 0 |
| ops.json | `server-runtime/ops.json` 与 `login-runtime/ops.json` 均为 `[]` |

## 规则文件哈希（sha256）

| 文件 | 源码 / JAR 内嵌 | 线上 `plugins/CoreRpg/` |
|---|---|---|
| `ember-v1-runs.yml` | `046b9af2f8a1ff77f58bb33d2988dbd73eacd055906270e6c4eaddb61ca00d8a` | 同左 |
| `ember-v1-growth.yml` | `56576ddbd00517afeaf47c5b8663bb9af2a3340754fc0a14e0de5ffd91fef5b9` | 同左 |
| `ember-v1-festival.yml` | `67faeeab1f3a9f22368ac3e49c2c62ba9b6d9732bef05f79d19f7df8f9f9ce68` | 同左 |

p1sim 规则快照哈希（`rules.py` stamp）：`f23d20ef0ce2779e`（bv29，含 D164 键）。

## 本版包含的裁决

| 裁决 | 提交 | 说明 |
|---|---|---|
| D163（1.65.2） | `cac6bc6` | B01 拆分净 +50% / 本体 −20%（已被 D164 取代）；开局药格「快捷栏第 5–9 格」 |
| D164 破甲 | `7866bc4` | t2c → 厚甲精英 ×1.6、炽热 / 分裂 ×0.8 |
| D165 裂身纹退池 | `7866bc4` | `b_split` `rollable: false`，已有照常生效 |
| D166 文案 | `7866bc4`、`4891555`、`a7ff400` | 「仅主线重打生效」；Q01 / 首领前 / R01 提示；菜单 |
| 模拟检查 | `c75893e` | `tools/p1sim/check_d164.py` + `out-d164-armorbreak-check.md`；`realcost.py` 池过滤 `rollable: false` |

## 验收

| 报告 | 路径 | 摘要 |
|---|---|---|
| 状态 + 冒烟 | `docs/status/STATUS-ember-armorbreak-1.65.3.md` | FreshQ100 Q01 kite 首通 PASS（提示出现）；FreshQ101 强制厚甲精英 ×1.600 PASS；FreshQ102 R01 招募提示 PASS |
| 模拟 | `tools/p1sim/out-d164-armorbreak-check.md` | 厚甲击杀 −30.5 ～ −36.2%；首通 / 挑战 42 格不变 |
| 单测 | `CoreRpg` `mvn -o package` | 229 / 0 |
| 设计 | `docs/design/DESIGN-ember-build-diversity-2026-10-04.md` §7 P7 | 提案来源 |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive <本版 CoreRpg 提交> CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# 逐文件比对 target/CoreRpg.jar 与 plugins/CoreRpg.jar 的条目内容（jar 时间戳不同，sha256 不比）
```

## 下一次发版时请更新本表

换 jar / 改 `balance_version` / 换地图包时，复制本文件为新版本号，填新哈希与测试链接；旧文件保留作历史。
