# 发布凭证 · CoreRpg 1.65.11（2026-10-04）

D174 主线签名传奇 · 第 2a 阶段（Q04–Q06 签名 L07–L12 + 战斗钩子）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`（§5 状态、§9 上线记录）。

## 线上状态（核对于 2026-10-04 19:39 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.11**（日志 `Enabling CoreRpg v1.65.11`）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `14adafd2a82f277d38358af9fbca983bbbed9aad21c5af17ce5db682cbc1440e` |
| jar 条目数 | 2352 |
| balance_version | **34** |
| 代码提交 | `c4bd355` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.10-pre-16511.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 930354（19:39），`server-runtime/stop.sh` → `start.sh` |

## 内容

6 件签名：L07 潮闸长杆（烬斩直线穿刺，最多 3 个）、L08 潮蚀护符（烬斩点燃 1 个 / 燃烧 ×0.92）、L09 断塔双斧（烬斩环形，最多 3 个）、L10 回廊护符（躲开 / 被打中都 +1 烬爆计数 / 烬爆 ×0.9）、L11 霜封长刀（烬斩每命中 0.5% 护盾，最多 1%，5 秒 / 烬斩 ×0.9，受首领伤害 ×1.05）、L12 统领护符（生命 < 40% 时炽愈少 2 下触发 / 炽愈 ×0.99）。钩子：`SkillService`（line / ring / plus / cap / skill_mult、点燃、护盾）、`EmberSetEngine`（低血 everyAt）、`EmberCombatListener`（护盾吸收）；图鉴 Q04–Q06 行

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 254 / 0 |
| p1sim | 见设计 §8.4（单条 / 组合 / 叠天赋全部过上偏门槛，最高 +2.3）；W30 见 §8.5 |
| 冒烟 | FreshQ190（脚本里的发件参数写错了，`bad quality 4`，修正）→ FreshQ194 / FreshQ195 7 / 8：烙 L09 + L08 → 生效 9,8，mods 带 `skill_ring` / `skill_ignite`；L11 → `skill_shield` + `taken_boss`；L07 → `skill_line`；图鉴 Q06 行；无异常。烬斩施放在大厅里只走到「附近没有目标」（大厅刷不出怪）；在 1.65.14 的 FreshQ199 首领残响局里对首领实际施放成功（「释放 烬斩」+ 冷却） |
| 资产回归 | 没有新物品 / 资产路径 → 没跑 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive c4bd355 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
