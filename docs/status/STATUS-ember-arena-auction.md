# STATUS · 余烬竞技 / 寄售（菜单壳）

**日期：** 2026-09-12（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-arena-auction.md`  
**文案：** `docs/ember-hub-copy.md`  
**约束：** 未改 Paper；未覆盖 `CoreRpg.jar`

---

## 已落地

| 路径 | 内容 |
|------|------|
| `docs/design/DESIGN-ember-arena-auction.md` | 短规格：1v1/2v2 排队、寄售 list/sell、税率 10%；命令面 |
| `plugins/TrMenu/menus/ember_arena.yml` | 竞技壳：1v1 / 2v2 排队 tell · 取消 · 战绩 |
| `plugins/TrMenu/menus/ember_auction.yml` | 寄售壳：浏览 / 上架 / 税率 10% / 我的上架 |
| `plugins/TrMenu/menus/ember_hub.yml` | a→`ember_arena`；b→`ember_auction`；去掉二者「即将点燃」与 `ember_coming` |
| `plugins/TrMenu/menus/README-ember.md` | 登记两菜单 |
| `docs/ember-hub-copy.md` | 竞技 / 寄售文案同步（已接菜单壳） |
| `docs/status/STATUS-ember-arena-auction.md` | 本文 |

`server-runtime/plugins` → `plugins` **同一 inode**，无需镜像拷贝。

### Hub 文案要点

- **竞技：** `§e➥ 打开竞技菜单` · `menu: ember_arena` · 无「即将点燃」
- **寄售：** `§e➥ 打开寄售菜单` · `menu: ember_auction` · 税率 10% · 无「即将点燃」

---

## 热重载

```
/trmenu reload
```

---

## 未接线（插件岗）

1. `/corerpg arena` · `queue 1v1` · `queue 2v2` · `leave` · 匹配 / 独立世界 / CoreCombat
2. `/corerpg auction` · `list` · `sell <价>` · `buy` · `cancel` · 税 10% 入账
3. 赛季积分、日奖励箱、绑定白名单、禁毕业伤害校验

---

## 未改

Paper / `CoreRpg.jar` / MythicMobs / DungeonPlus / NI 物品 / `ember_coming.yml`（仍供其它空壳）

---

## 验收（壳）

- [ ] `/ember` → 竞技 / 寄售进入子菜单；lore **无**「即将点燃」
- [ ] 竞技：1v1 / 2v2 点击有排队 tell；可返回 hub
- [ ] 寄售：list / sell / 税 10% tell；可返回 hub
- [ ] 命令未装时 tell 不崩
- [ ] 未改 Paper / CoreRpg.jar
