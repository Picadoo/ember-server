# STATUS · B1.4 软抛光文案短抽

- **测试岗：** 余烬-测试岗执行器
- **依据：** `docs/design-ember-b14-quest-join-copy.md` §4.2、`docs/STATUS-ember-b14-quest-join-soft.md`、commit `ee3cfbc`
- **时间：** 2026-09-28 08:49 CST（UTC+08:00）
- **方式：** 静态抽检；未进服，未改配置，未 commit/push。
- **抽检文件：** `CoreRpg/src/main/resources/quest.yml` 与部署副本 `plugins/CoreRpg/quest.yml`；两者本次 quest 文案一致。

## 总评

**PASS（3/3 主抽检项）。** ch2 Lv20、ch3 Lv25、ch7 Lv40 均已使用「打开枢纽菜单」前缀，目标 hint 内无裸斜杠并列；ch7 并列符为「·」。

## 证据与结果

### 1. ch2 Lv20 hint — PASS

- `CoreRpg/src/main/resources/quest.yml:123-125`
- `plugins/CoreRpg/quest.yml:123-125`

原文：

```text
level: 20
hint: 打开枢纽菜单 → 签到 · 日常本 · 悬赏 · 挂机庭①灰坡，都能补经验
```

含「打开枢纽菜单」；「签到」与「日常本」等并列使用「·」，无裸「签到 / 日常」斜杠并列。

### 2. ch3 Lv25 hint — PASS

- `CoreRpg/src/main/resources/quest.yml:163-166`
- `plugins/CoreRpg/quest.yml:163-166`

原文：

```text
level: 25
hint: 打开枢纽菜单 → 日常本 · 悬赏 · 签到 · 挂机庭②荒原
```

含「打开枢纽菜单」；无裸「签到 / 日常」斜杠并列。

### 3. ch7 Lv40 hint — PASS

- `CoreRpg/src/main/resources/quest.yml:315-318`
- `plugins/CoreRpg/quest.yml:315-318`

原文：

```text
level: 40
hint: 打开枢纽菜单 → 日常本 · 深渊 · 挂机庭 · 悬赏
```

已有菜单前缀；四项并列均使用「·」，不是「/」。

## 可选：join_message 硬关单回归

**PASS（按实际部署文件）。** `plugins/CoreRpg/config.yml:4-8` 的 `join_message` 为菜单/NPC 引导，未出现 `/corerpg`、`/dp`、`/hub`，也未出现「请打」指令教学。

原文摘录：

```text
- §6§l[余烬服] §e跟着主线走：引路人·灰烛就在出生点北边——右键他看当前目标。
- §7打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。
- §7进本门槛：周本 Lv.20 · 深渊 Lv.25 · 灾厄 Lv.30 · 团本 Lv.35——打开枢纽菜单进入。
```

**注意：** `CoreRpg/src/main/resources/config.yml:5-8` 是未同步的源码模板，仍残留 `/corerpg`、`/ember` 文案；本任务明确禁改配置，故未动。该差异不影响上述实际部署文件与三项 quest hint 抽检，但若验收范围包含源码模板，应另开配置同步项。

## 阻塞点

主抽检无阻塞；未进服，因此未做 live 展示或进服后清空操作。
