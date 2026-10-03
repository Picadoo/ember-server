# STATUS · CoreGacha craft / spark / exchange 冒烟（2026-10-04 04:51 CST）

- 服务器：CoreRpg **1.62.0** + CoreGacha **1.0.0**（JDK8），**未重启**（上一次重启 04:17，日志仍有 `[storage] MySQL connected` / `[CoreGacha] [db] MySQL connected`）。
- 测试号：**FreshG03**（UUID `300bd3ec-9a75-3ab6-9124-ff4841c32d78`），测完已下线。botd `127.0.0.1:8765`。
- 跳过：真时长在线/主线发券；限定池结束后 `spark_leftover`（不动 gq26 窗口）。

## 结果

| 路径 | 命令 / 准备 | 证据 | 判定 |
|------|-------------|------|------|
| 光屑兑换 craft | `gacha admin give-shards FreshG03 100` → `/gacha craft badge_clover confirm` | 聊天「光屑兑换：获得 普通 ☘ 三叶 · 剩余光屑 60」；日志 `[craft] FreshG03 badge_clover for 40 shards`；账本 `craft / badge_clover / d_shards=-40 / shards_after=60`；`gacha_owned` 有 `badge_clover` | **PASS** |
| 火花兑换 spark | SQL 将 `gacha_banner_state` standard.spark 置 200 → `/gacha spark standard pet_frost` | 聊天「火花兑换：获得 传说 霜晶灵」；日志 `[spark] FreshG03 pet_frost spark@standard`；spark 200→0；账本 `spark / pet_frost@standard`；owned `pet_frost` | **PASS** |
| 火花已拥有拒绝 | 再置 spark=200 → 再 `/gacha spark standard pet_frost` | 聊天「你已经有「霜晶灵」了。」；spark **仍为 200**（未扣） | **PASS** |
| 币换券 exchange coin | `corerpg coin give FreshG03 5000` → `/gacha exchange coin 1` | 聊天「用 1200 余烬币 换了 1 张…今天还能换 4 张」；日志 `[exchange] FreshG03 1200 coin → 1 tickets`；账本 `exchange_coin` | **PASS** |
| 徽换券 exchange badge | `corerpg p1 runs season badges FreshG03 50` → `/gacha exchange badge 1` | 聊天「用 20 余烬徽 换了 1 张…还能换 3 张」；日志 `[exchange] FreshG03 20 badge → 1 tickets`；账本 `exchange_badge` | **PASS** |
| 每日换券上限 | 再 `exchange coin 3`（凑满 5）→ 再 `exchange coin 1` | 「今天还能换 0 张」后出现「今天已经换了 5 张（每日上限）。」；钱包最终券 **10**（见面 5 + 换 5） | **PASS** |

终态 inspect：券 10 · 光屑 60 · 拥有 2 件（badge_clover / pet_frost）。无 bug，无 jar 变更。

## 备注

- FreshG01 在 04:16–04:18 已做过 craft（badge_moon / pet_lamp）和一次 exchange_badge；本窗用 FreshG03 把 craft + spark + coin/badge exchange + 日上限一并复核。
- 下次扭蛋测试号：**FreshG04**。CoreRpg 侧下次 **FreshQ44**（FreshQ43 = 限时清房花样委托 PASS）。
