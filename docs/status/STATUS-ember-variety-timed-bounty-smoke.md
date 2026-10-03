# STATUS · D144 花样委托·限时清房 冒烟（2026-10-04 06:47 CST）

- 服务器：CoreRpg **1.62.0**（未重启；日志仍有 `[storage] MySQL connected`）。
- 测试号：**FreshQ43**（测完已下线）。botd `127.0.0.1:8765`。
- 脚本：`tools/p1map/timed-bounty-smoke.sh FreshQ43`。
- 目标：handoff §7 写的「限时清房那一档花样委托还没测」——本窗补测。词缀精英档此前已冒烟。

## 步骤

1. `join FreshQ43` → AuthMe 自动登录
2. `corerpg stamina set FreshQ43 120`
3. `corerpg p1 runs firstclear FreshQ43 q01`（管理员首通，让本局算「已首通重打」）
4. `corerpg p1 runs variety event:r1`（下一局普通版强制限时清房在 r1）
5. 玩家 `/corerpg p1 enter q01` → 创造模式 TP 各房 + `corerpg p1 runs weaken` + bot 近战清房 → 首领结算

## 结果

| 项 | 证据 | 判定 |
|---|---|---|
| 强制花样 | 日志 `[P1 run] q01-… variety forced event:r1 (admin test)`；聊天「限时清房 · 30 秒内清完…」 | **PASS** |
| 限时清房完成 | 聊天「限时清房完成 （12.0 秒）· 余烬核心碎片 +1 记为待结算」；日志 `event done 12.0s` | **PASS** |
| 花样委托 timed | 聊天「花样委托 完成：限时清房达标（20 余烬币） · … · ✔ 限时清房达标 1/1」 | **PASS** |
| 结算到账 | 结算行含「余烬币 20」（委托档）+「限时清房 余烬核心碎片 ×1」；日志 `settle … rows+12` | **PASS** |

顺带：本局随机出了词缀精英「炽热」（r2），也记了进度「击败词缀精英 1/2」——与本次限时档无关，不挡 PASS。

## 备注

- 削弱清房即可；真人节奏不必。限时窗 30 秒，本局 12 秒完成。
- 未重启、未改 jar、未动 CoreGacha / p1sim。
- 下次测试号：**FreshQ44**。
