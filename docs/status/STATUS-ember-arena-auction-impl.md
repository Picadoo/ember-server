# STATUS — CoreRpg 竞技 + 寄售 (1.3.8)

**时间：** 2026-09-13 ~12:20 Asia/Shanghai  
**规格：** `docs/design/DESIGN-ember-arena-auction.md`  
**约束：** Paper **未改** · 盟约周 Boss **保留**

| 项 | 状态 |
|----|------|
| CoreRpg **1.3.8** | OK `plugins/CoreRpg.jar` **198825** bytes |
| Restart | OK `./start.sh custom` · log `CoreRpg 1.3.8 enabled (.../arena/auction)` |
| `arena.yml` / `auction.yml` | OK 已抽出到 `plugins/CoreRpg/` |
| Guild boss | OK（`/corerpg guild boss` 仍接线；冒烟「不在盟约」正常） |
| Paper | 未改 |

## Arena `[竞技]`
- `/corerpg arena` — 积分 / 段位 / 日箱 / 排队状态
- `/corerpg arena queue 1v1|2v2` — 内存队列；1v1 满 2 人 stub 匹配（随机胜者 +15 积分/×25 币，负方 +5 积分/×10 币）
- `/corerpg arena leave` · `stats` · `claim`（日箱：币 80 + `cosmetic_calamity_shard`×1，Asia/Shanghai 日限 1）
- PlayerData：`arenaPoints` / `arenaWins` / `arenaLosses` / `arenaDailyClaimDate` / `arenaQueueMode`

## Auction `[寄售]`
- `/corerpg auction` · `list [页]` · `sell <价>` · `buy <id>` · `cancel <id>`（别名 `ah`）
- 税 **10%**（卖方实收 90%）；列表持久化 `plugins/CoreRpg/auction.yml`
- 白名单：mats / gems / reforge / protect / tickets / pet eggs / cosmetic；**允许** `mat_ember_core_fragment`（走税）
- 禁：`ban_enhance_at: 7`（GearLore）；白名单不含 `gear_ember_blade/charm`

## 冒烟（mineflayer）
- ArenaA/B 1v1 匹配 stub PASS（胜/负结算）
- Arena claim PASS（币+外观碎片）
- 核心碎片上架 #2 → Tester buy · 花费 100 / 卖方实收 90 PASS
- cancel 返还 PASS
