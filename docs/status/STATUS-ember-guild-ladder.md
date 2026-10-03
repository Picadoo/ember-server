# STATUS · 余烬盟约 / 天梯（菜单壳 + HD ops）

**日期：** 2026-09-12（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-guild-ladder.md`  
**全息 ops：** `docs/ember-holograms.md`  
**约束：** 未改 Paper；未覆盖 `CoreRpg.jar`

---

## 已落地

| 路径 | 内容 |
|------|------|
| `docs/design/DESIGN-ember-guild-ladder.md` | 短规格：创建/捐献/周 Boss；power/abyss/speed；HD+PAPI |
| `plugins/TrMenu/menus/ember_guild.yml` | 盟约壳：创建 / 捐献 / Boss / 查询 |
| `plugins/TrMenu/menus/ember_ladder.yml` | 天梯壳：战力 / 深渊 / 竞速 / 本人 / 全息说明 |
| `plugins/TrMenu/menus/ember_hub.yml` | Y→`ember_guild`；Z→`ember_ladder`；去掉二者「即将点燃」 |
| `plugins/TrMenu/menus/ember_hub.yml` | a 竞技 / b 寄售：仍 stub → `ember_coming` |
| `docs/ember-holograms.md` | 精确 `/hd create` + power TOP placeholder 行 |
| `plugins/HolographicDisplays/README-ember-holograms.md` | 指向 docs；勿手改 database.yml |
| `server-runtime/plugins/TrMenu/menus/*` | 已同步 guild/ladder/hub（若存在 runtime） |

### Hub 文案要点

- **盟约：** `§e➥ 打开盟约菜单` · `menu: ember_guild`
- **天梯：** `§e➥ 打开天梯菜单` · `menu: ember_ladder`
- **竞技 / 寄售：** `§8占位 stub · 即将点燃` · `menu: ember_coming`

---

## 热重载

```
/trmenu reload
```

全息需 ops 在大厅执行 `docs/ember-holograms.md` 中命令（首次建板）。

---

## 未接线（插件岗）

1. `/corerpg guild create|donate|boss` 与存档  
2. `/corerpg ladder power|abyss|speed` 与 `power_score` 计算  
3. PAPI：`%ember_ladder_power_N_*%` 等  
4. `EmberGuildBoss` DP 本  
5. 周结算外观发放  

---

## 未改

Paper / `CoreRpg.jar` / MythicMobs / DungeonPlus / NI 物品 / HD `database.yml` 手改

---

## 验收（壳）

- [ ] `/ember` → 盟约 / 天梯进入子菜单；lore **无**「即将点燃」
- [ ] 子菜单可返回 hub；点击有 tell + 建议命令
- [ ] 竞技 / 寄售仍进 `ember_coming` stub
- [ ] ops 可按 docs 建 `ember_ladder_power`（PAPI 未接可先见字面量）

---

## 插件岗更新（2026-09-13）

天梯 + PAPI 已在 CoreRpg **1.3.4** 落地，详见 `docs/status/STATUS-ember-ladder-papi.md`。
盟约/公会逻辑仍未接线。

