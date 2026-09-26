# STATUS · 余烬副本地图规划 / Hub

**日期：** 2026-09-13 11:45 Asia/Shanghai  
**规格：** `DESIGN-ember-map-plan.md`  
**约束：** 未改 Paper；未覆盖 `plugins/DungeonPlus/map/*`

---

## 1. CoreRpg jar · mail 检查

| 项 | 结果 |
|----|------|
| `plugins/CoreRpg.jar` | `-rw-r--r--` **105418** bytes · mtime **2026-09-12 07:30** |
| `plugin.yml` version | **1.3.2** |
| description | `币/签到/活跃/悬赏/强化/镶嵌/分解/重铸/灾厄/誓约/天赋/晶钻商城/月卡 + 记分板/刷怪` — **不提 mail** |
| jar 内 class / yml | **无** `MailService` · **无** `mail.yml` · **无** `friend.yml`（含 cash/covenant/enhance/scrap/talent 等） |
| 源码 | 有 `MailService.java`（553 行，mtime 07:34，**晚于** jar）+ `resources/mail.yml` + `friend.yml`；`CoreRpgPlugin.java` **未检索到** Mail/Friend 接线 |

**STATUS：** 部署 jar **不含邮寄**；源码侧 mail 为未编入 / 未接线草稿。菜单壳见 `STATUS-ember-mail-friends.md`（TrMenu only）。要上线需：接线 → 升 version 文案 → rebuild → 部署（本轮未做）。

---

## 2. Hub 平台（主世界）

| 项 | 结果 |
|----|------|
| 服务器 | **UP** · pid 在跑 · `:25565` |
| RpgBot | `rpg-loop.js` **长循环占线**（刷怪/死亡），不宜再抢同名会话做 `/fill` |
| 施工 | **SKIP（风险）** — 见 DESIGN §6；建议坐标 `(-20,66,250)` 待空窗再做 |

---

## 3. DungeonPlus map 同步（不打架）

并行岗已落盘（目录 + region×3 + level.dat 821B 同模板）：

- `ember_daily` / `ember_weekly` / `ember_abyss` / `ember_calamity` / `ember_raid` / `ember_arena`
- `config.yml` 已登记各 map ↔ 本
- 各本 `$setmap` 已指向独立名；出生仍占位 `-40,65,270`

本轮：**只写设计文档**，不改 map 文件、不改 option/monster YAML。

---

## 4. 已写文档

| 路径 | 内容 |
|------|------|
| `DESIGN-ember-map-plan.md` | 五本主题（灰烬庭院/深核廊/下行竖井/开放祭坛/大厅+通道）、体量、本地出生表、WE 流程 |
| `STATUS-ember-map-plan.md` | 本文 |

---

## 5. 未改

Paper / CoreRpg.jar / MythicMobs / DungeonPlus map 二进制 / dungeon YAML / TrMenu
