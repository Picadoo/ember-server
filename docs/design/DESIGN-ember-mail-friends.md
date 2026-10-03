# 余烬邮寄 + 好友 + 设置 — 短规格（菜单壳）

**日期：** 2026-09-12（Asia/Shanghai）  
**承接：** `docs/design/DESIGN-ember-rpg-systems.md` §6 社交（好友 / 邮寄；师徒可选）  
**栈：** Paper 1.12.2 · TrMenu ·（插件岗）CoreRpg  
**硬约束：** 不改 Paper；**不重建 / 不覆盖** `CoreRpg.jar`（本文只定规格、菜单壳与命令面）。

---

## 0. 本期范围

| 交付 | 状态 |
|------|------|
| 本设计短稿 | 本文 |
| TrMenu `ember_mail.yml` / `ember_friends.yml` / `ember_settings.yml` | 菜单壳（tell + 建议命令） |
| hub 邮寄 / 好友 / 设置 | 去掉「即将点燃」，打开子菜单 |
| CoreRpg mail / friend / settings 逻辑 | **不做**（命令面占位，供插件岗后接） |

---

## 1. 邮寄（系统发奖）

总纲：好友 / 邮寄 = **基础**；邮件发奖励（战令、维护补偿）。

| 项 | 定案 |
|----|------|
| 定位 | 系统投递入口；奖励统一进邮箱，不弹背包散件 |
| 玩家互寄 | **本期不做**（防刷、防交易绕税） |
| 附件白名单 | 余烬币 / 日周票 / 外观碎片 / 保护券类便利物 |
| 附件红线 | **不发**核心、毕业刃、满孔装、战力扭蛋 |
| 菜单 | `ember_mail.yml` · hub **R** → `menu: ember_mail` |

### 1.1 命令面（插件岗后接）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg mail` | 列出收件箱（未读优先） |
| `/corerpg mail read <id>` | 读正文，不领附件 |
| `/corerpg mail claim <id>` | 领取单封附件 |
| `/corerpg mail claim all` | 一键领取可领附件 |
| `/corerpg mail delete <id>` | 删已领空信；未领附件不可删 |
| `/corerpg mail send <玩家> <模板id>` | **运营 / 控制台**：投递战令结算、维护补偿、活动箱 |

权限拟定：玩家 `corerpg.mail`；运营 `corerpg.mail.send`。  
未实装时菜单 `command:` 可能失败，**tell 回退**不崩。

模板示意：`pass_weekly`（战令周结算）· `maintenance`（维护补偿）· `activity_box`（活跃箱补发）。

---

## 2. 好友（基础社交）

总纲：好友列表与组队邀请；**师徒可选**（新人绑老手，双方活跃奖）。

| 项 | 定案 |
|----|------|
| 列表 cap | 示意 50（YAML 旋钮） |
| 申请 | 双方确认后入表 |
| 组队 | 向在线好友发邀请；接团本 / 周本；**不强制进队** |
| 师徒 | 可选；奖币 / 外观碎片，**不发核心** |
| 菜单 | `ember_friends.yml` · hub **c** → `menu: ember_friends` |

### 2.1 命令面（插件岗后接）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg friend` | 列出好友（在线优先） |
| `/corerpg friend add <名>` | 发出申请 |
| `/corerpg friend accept <名>` | 接受申请 |
| `/corerpg friend deny <名>` | 拒绝申请 |
| `/corerpg friend remove <名>` | 解除关系 |
| `/corerpg friend invite <名>` | 组队邀请（聊天提示，不强制） |
| `/corerpg friend mentor <名>` | 可选：发起师徒绑定 |
| `/corerpg friend mentor accept\|break` | 接受 / 解除师徒 |

权限拟定：`corerpg.friend`（默认真玩家有）。

---

## 3. 设置（低优先级）

总纲 / 文案包：音效、提示与隐私等个人选项。

| 项 | 定案 |
|----|------|
| 音效 | 菜单点击与领取提示音；默认开；仅本人 |
| 提示 | 签到 / 邮件 / 悬赏聊天提示；关提示不关奖励 |
| 隐私 | 拒陌生人好友申请；**不影响**盟约 / 天梯展示 |
| 菜单 | `ember_settings.yml` · hub **d** → `menu: ember_settings` |

### 3.1 命令面（可选，插件岗后接）

| 命令 | 行为（拟定） |
|------|----------------|
| `/corerpg settings` | 打印当前开关 |
| `/corerpg settings sound` | 切换音效 |
| `/corerpg settings tip` | 切换聊天提示 |
| `/corerpg settings privacy` | 切换陌生人申请 |

设置不进战力、不进商城。

---

## 4. TrMenu 接线

| 菜单 | hub 图标 | 行为 |
|------|----------|------|
| `ember_mail` | R 邮寄 | `menu: ember_mail`；无「即将点燃」 |
| `ember_friends` | c 好友 | `menu: ember_friends`；无「即将点燃」 |
| `ember_settings` | d 设置 | `menu: ember_settings`；无「即将点燃」 |

---

## 5. 验收（壳）

- [ ] `/ember` → 邮寄 / 好友 / 设置 打开子菜单，lore 无「即将点燃」
- [ ] 子菜单返回 hub；图标有 tell + 建议 `/corerpg mail` / `friend` / `settings`
- [ ] 未改 Paper / CoreRpg.jar
