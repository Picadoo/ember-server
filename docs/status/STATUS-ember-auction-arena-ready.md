# STATUS · Ember 寄售 / 竞技 smoke — ready / waiting for CoreRpg 1.3.8

**日期：** 2026-09-13 12:16（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-arena-auction.md` · `docs/status/STATUS-ember-arena-auction.md`（菜单壳）  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **177591** bytes · mtime **12:13 CST**（2026-09-13 04:13 UTC） |
| `plugin.yml` version | **1.3.7** |
| description / usage | 含盟约/使魔/天梯；**无** arena / auction / ah / pvp |
| `AuctionService.class` / `auction.yml` in jar | **无** |
| `ArenaService.class` / `arena.yml` in jar | **无** |
| 源码 | `AuctionService` · `ArenaService` · `resources/auction.yml` · `arena.yml` **已有**；插件 log 文案已写 **1.3.8**（arena/auction），但 **onCommand 与 plugin.yml 尚未把 auction\|ah / arena\|pvp 打进已部署 1.3.7 jar** |

**结论：** 寄售 / 竞技命令面 **尚未在已部署 jar** → **不跑** `auction-arena-smoke.js`，等插件岗正式部署 **CoreRpg 1.3.8**（含 AuctionService + ArenaService + 命令接线）后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/auction-arena-smoke.js` | RpgBot · coin/NI give → auction\|ah list/sell/buy/cancel/collect → arena\|pvp queue 1v1\|2v2 / leave / stats（+ claim） |
| `plugins/TrMenu/menus/ember_auction.yml` | 寄售菜单壳（既有；税 10% tell） |
| `plugins/TrMenu/menus/ember_arena.yml` | 竞技菜单壳（既有；1v1/2v2 排队 tell） |
| `docs/design/DESIGN-ember-arena-auction.md` | 命令面占位 · 税 10% · 1v1/2v2 |

### 脚本期望命令面（1.3.8）

```
/corerpg coin give RpgBot 5000
/ni give RpgBot mat_ember_shard 16

/corerpg auction
/corerpg auction list [page]
/corerpg auction sell <价>          # 手持白名单 NI
/corerpg auction buy <id>
/corerpg auction cancel <id>
/corerpg auction collect            # 取回成交款 / 过期物（以落地为准）

/corerpg ah …                       # auction 别名（list|sell|buy|cancel|collect）

/corerpg arena
/corerpg arena queue 1v1
/corerpg arena queue 2v2
/corerpg arena leave
/corerpg arena stats
/corerpg arena claim                # 日奖励箱（源码有；DESIGN 可选）

/corerpg pvp …                      # arena 别名（queue|leave|stats）
```

DESIGN 核心：寄售税 **10%**、白名单流通物；竞技 **1v1 / 2v2** 排队 stub（真实 PvP 世界可后接）。

---

## 等待插件岗

1. 将 `AuctionService` + `ArenaService` + `auction.yml` / `arena.yml` 编入 **`CoreRpg.jar` 1.3.8**  
2. `onCommand` 接线 `/corerpg auction|ah …` 与 `/corerpg arena|pvp …`；`plugin.yml` usage / description 出现 **auction / arena**（或 STATUS 标明已接线）  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（或可靠重载 CoreRpg）后跑 smoke  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg.jar == 1.3.8 且含 AuctionService + ArenaService
node auction-arena-smoke.js
```

期望：`[寄售]` list/sell/buy/cancel（+ collect）tell；`[竞技]` 积分/排队 1v1·2v2 / leave / stats tell；未知别名不崩或明确别名提示。匹配 stub 结算可接受（无真实 PvP 世界）。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 auction-arena smoke · 未改 TrMenu 竞技/寄售壳
