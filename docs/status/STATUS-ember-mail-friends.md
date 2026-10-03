# STATUS · 余烬邮寄 / 好友 / 设置

**日期：** 2026-09-13（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-mail-friends.md`  
**约束：** 未改 Paper；保留 scrap/cash/covenant/enhance

---

## 已落地（插件岗 DONE）

| 项 | 状态 |
|----|------|
| `MailService` 接入 `CoreRpgPlugin` | **DONE** · `/corerpg mail` read/claim/claim all/delete/send |
| `FriendService` | **DONE** · add/accept/deny/remove/list/invite/mentor |
| `PlayerData` + `PlayerDataStore` friends/pending/mentor/settings | **DONE** |
| `/corerpg settings` sound/tip/privacy | **DONE** · privacy ON=拒陌生人申请 |
| `mail.yml` + `friend.yml` → `plugins/CoreRpg/` | **DONE** |
| PAPI `%corerpg_mail_unread%` | **DONE** |
| CoreRpg.jar **1.3.3** | **DONE** |
| `_golden` sync | **DONE** |

### Hub / TrMenu 壳（先前）

| 路径 | 内容 |
|------|------|
| `plugins/TrMenu/menus/ember_mail.yml` | 邮寄壳 |
| `plugins/TrMenu/menus/ember_friends.yml` | 好友壳 |
| `plugins/TrMenu/menus/ember_settings.yml` | 设置壳 |

`server-runtime/plugins` → `plugins` **同一 inode**。

---

## CoreRpg.jar 核对（2026-09-13）

| 项 | 结果 |
|----|------|
| jar | `plugins/CoreRpg.jar` · **1.3.3** |
| 内容 | `MailService.class` · `FriendService.class` · `mail.yml` · `friend.yml` |
| 命令 | mail / friend / settings 已接线 |

---

## 命令面

| 命令 | 行为 |
|------|------|
| `/corerpg mail` | 收件箱 |
| `/corerpg mail read <id>` | 读信 |
| `/corerpg mail claim <id\|all>` | 领附件 |
| `/corerpg mail delete <id>` | 删已领 |
| `/corerpg mail send <玩家> <模板>` | 运营投递 |
| `/corerpg friend` | 列表 |
| `/corerpg friend add\|accept\|deny\|remove\|invite <名>` | 社交 |
| `/corerpg friend mentor <名\|accept\|break>` | 师徒 |
| `/corerpg settings [sound\|tip\|privacy]` | 开关 |

权限：`corerpg.mail` / `corerpg.friend` / `corerpg.settings` default true；`corerpg.mail.send` op。

---

## 未改

Paper · MythicMobs · DungeonPlus · NI 物品定义

---

## 验收

- [x] jar 含 MailService / FriendService / mail.yml / friend.yml · version 1.3.3
- [x] Plugin 接线 mail/friend/settings + reload
- [ ] 服内需 **重启** 后 `/corerpg mail` 等可用（插件热重载不可靠）
