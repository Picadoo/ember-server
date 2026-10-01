# TEST B2.122 · TrMenu click tell 方括号丢失（实测修复）

- 时间：2026-10-01 18:35 CST（实测）· 游玩服 Paper 1.12.2 + TrMenu 3.12.5 + CoreRpg 1.15.28
- 方式：mineflayer 非 OP bot `SmokeT1001` 经代理 :25565 → AuthMe /login → play；`/ember` → 团本 / 深渊 → 点「开始协作」/「开始下潜」，抓原始 chat 包（`bot._client.on('chat')`）。

## 原因
TrMenu 3（TabooLib 6）的 `tell:` 文本走简易组件语法 `[文本](参数)`。`§9[团本] §7…` 里的 `[团本]` 被当成组件：
方括号被吃掉，组件内样式被重置（颜色只落在空串上）。B2.110 / B2.111 只做了静态检查，没有发现。

修复前实测包（B2.110/B2.111 文案）：
```
{"extra":[{"extra":[{"color":"blue","text":""},{"text":"团本"}],"text":""},{"extra":[{"text":" "},{"color":"gray","text":"尝试进入……"}],"text":""}],"text":""}
{"extra":[{"extra":[{"color":"dark_purple","text":""},{"text":"深渊"}],"text":""},{"extra":[{"text":" "},{"color":"gray","text":"尝试下潜……（需余烬 Lv.25）"}],"text":""}],"text":""}
```
→ 玩家看到白色「团本 尝试进入……」，无方括号、无蓝色。

## 修复
`plugins/TrMenu/menus/*.yml` 中所有 `tell:` 行的 `[…]` → `【…】`（25 个菜单、91 行；仅 tell 行，lore/name 不受该语法影响未改）。
TrMenu 文件监听自动重载 25 个菜单（`自动重新载入菜单 …`），无需重启。

## 修复后实测包
```
{"extra":[{"color":"blue","text":"【团本】 "},{"color":"gray","text":"需 3～5 人 · 消耗 50 体力（本周首次免费）"}],"text":""}
{"extra":[{"color":"blue","text":"【团本】 "},{"color":"gray","text":"尝试进入……"}],"text":""}
{"extra":[{"color":"dark_purple","text":"【深渊】 "},{"color":"gray","text":"尝试下潜……（需余烬 Lv.25）"}],"text":""}
```
紧随其后：`§8人数 3～5`、`§8需要余烬等级 §eLv.35§8（见菜单等级要求）`、`§c余烬团本需要余烬 Lv.35§7（当前 Lv.10）`；
深渊：`§8需要撤离：打开本菜单 → 上浮撤离`、`§c余烬深渊需要余烬 Lv.25§7（当前 Lv.10）`（等级门槛先拦，未扣体力）。

## 结论
**PASS**：括号与颜色（团本 §9 蓝 / 深渊 §5 紫）均保留；「人数 3～5」「需余烬 Lv.25」照常显示。
