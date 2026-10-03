# STATUS · 余烬角色 / 套装 / 仓库（菜单壳）

**日期：** 2026-09-12（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-character-storage.md`  
**文案：** `docs/ember-hub-copy.md`  
**约束：** 未改 Paper；未覆盖 `CoreRpg.jar`

---

## 已落地

| 路径 | 内容 |
|------|------|
| `docs/design/DESIGN-ember-character-storage.md` | 短规格：角色 status/covenant/cash、2 件套、末影/扩展仓 |
| `plugins/TrMenu/menus/ember_character.yml` | 状态 / 誓约 / 晶钻 / 月卡 · command + tell |
| `plugins/TrMenu/menus/ember_set.yml` | 刃 / 护符 / 二件套效果说明壳 |
| `plugins/TrMenu/menus/ember_storage.yml` | 末影箱 / 扩展页 tell / 链勋阶 |
| `plugins/TrMenu/menus/ember_hub.yml` | A→character · V→set · X→storage · E→shop；**即将点燃 = 0** |
| `plugins/TrMenu/menus/README-ember.md` | 登记三菜单 |
| `docs/ember-hub-copy.md` | 角色/套装/仓库/御兽文案同步 |
| `docs/status/STATUS-ember-character-storage.md` | 本文 |

`server-runtime/plugins` → `plugins` **同一 inode**，无需镜像拷贝。

### Hub 文案要点

- **角色：** `menu: ember_character` · `/corerpg status` · cash · monthly
- **套装：** `menu: ember_set` · 刃+护符 2 件 · 效果待接线
- **仓库：** `menu: ember_storage` · 末影可用 · 扩展页待接线
- **御兽：** `menu: ember_shop`（与外观同路径）

---

## 热重载

```
/trmenu reload
```

---

## 未接线（插件岗）

1. 套装 2 件战斗效果（击杀回能等）· `/corerpg set`
2. 多页仓库 · 绑定/流通分仓 · `/corerpg storage`
3. `/enderchest` 若无第三方权限插件则依赖世界末影箱
4. 御兽独立系统（现仅商城货架入口）

---

## 未改

Paper / `CoreRpg.jar` / MythicMobs / DungeonPlus / NI 物品

---

## 验收（壳）

- [ ] `/ember` → 角色 / 套装 / 仓库进子菜单；lore **无**「即将点燃」
- [ ] hub 全文「即将点燃」计数为 **0**（含御兽进商城）
- [ ] 角色：status / covenant / cash / monthly 有 tell；可返回 hub
- [ ] 仓库：末影 tell；扩展页说明；可链勋阶
- [ ] 未改 Paper / CoreRpg.jar
