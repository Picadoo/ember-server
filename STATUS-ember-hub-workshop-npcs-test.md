# STATUS · 枢纽工坊 NPC（烬砧/余晶/灰粮）验收

**日期：** 2026-09-27 21:52–21:58 CST（Asia/Shanghai）  
**岗位：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-hub-workshop-npcs.md`（设计基线 CoreRpg **1.15.6**；现场 jar **1.15.8**，含 HubNpc）  
**范围：** 真实协议右键三工坊 NPC + 灰烛 talk 回归 + 方案 A 锻炉内跳转 + 锻炉告示；**未改**玩法数值 / NI / 票 / 挂机 / 灰烛 quest.yml。

## 总评：PASS

三项工坊 NPC 真实右键开窗、灰烛主线 talk 仍通、方案 A「去强化/去镶嵌」可跳转、告示仅指路、ops=[]。

## 分项结果

| 项目 | 结果 | 实测 / 证据 |
|---|---|---|
| 1 烬砧 @(-11.5,58,105.5) 右键 → 仅开 `ember_forge` | **PASS** | 新号 `HwT8176`：`activateEntity` hitbox id=**118** → tell「烬砧：刃与护符，放到菜单里锻…」+ 窗 **「余烬锻炉 · 锻造」**。右键**未**直接开强化/镶嵌。 |
| 1b 方案 A：forge 内「去强化」「去镶嵌」 | **PASS** | NBT 按钮名 slot**20**=「去强化」、slot**24**=「去镶嵌」。点 20 → **「余烬 · 强化」**；点 24 → **「余烬 · 镶嵌」**。 |
| 2 余晶 @(-21.5,58,104.5) 右键 → tell + 必开 `ember_enchant_guide` | **PASS** | hitbox id=**119** → tell 含「**手持附魔晶点附魔台**」+ Open 再 tell「不要打指令」+ 窗 **「咒火师 · 附魔引导」**。文案/菜单**无**教 `/enchant` 主路径（仅「禁止 /enchant」类禁令若存在亦不计教学）。 |
| 3 灰粮 @(-18.5,58,114.5) 右键 → `ember_life` | **PASS** | hitbox id=**120** → tell「灰粮：面包、竿子、代烤…」+ 窗 **「补给 · 生活」**。 |
| 4 灰烛 talk 仍通、坐标未漂 | **PASS** | 约 **(-16.5,58,106.5)** hitbox id=**110**：右键推进「这城烧了三十年…」→「挂机庭有游荡的尸骸…」→ 目标「在挂机庭击败 挂机庭僵尸 0/6」。**未**用 `/corerpg quest talk` 代替。 |
| 5 锻炉告示 (-13,58,105) | **PASS** | `standing_sign` 文案：**锻炉师·烬砧 / 就在棚里 / 右键他 / 勿手打指令**；无教打 `/enchant` `/hub` `/corerpg` 当主路径。 |
| 5b `/corerpg hubnpc ensure` 自检 | **PASS*** | 测中临时 OP 执行：`hubnpc ensured · n=3 hooked=true`；list 三 NPC 坐标/菜单与设计一致。*客户端仍见叠层村民（见风险）。 |
| ops=[] | **PASS** | 测后 `server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`。 |

## 三项右键窗名（摘要）

| NPC | 窗名（去色） |
|---|---|
| 烬砧 | 余烬锻炉 · 锻造 |
| 余晶 | 咒火师 · 附魔引导 |
| 灰粮 | 补给 · 生活 |

## 环境 / 版本证据

- 服务端日志：`CoreRpg 1.15.8 enabled`（基线文档写 1.15.6；本轮现场 jar 为 **1.15.8**，HubNpc 已加载）。
- `HubNpc: Adyeshach interact hooks active=2`；三工坊 hitbox ensure 日志在位。
- 账号：`HwT8176`（新号）；临时 OP：`RpgBot` uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`（测后 `/deop` + 清空 ops）。
- 原始结果：`/tmp/hub-workshop-npcs-accept.json`；脚本 `/tmp/hub-workshop-npcs-accept2.js`。
- 本轮**未改**数值、掉落、等级、afk、地图、灰烛 quest 配置。

## 收尾 / ops

- 测中：曾写入 ops 并重启 play 以启用 `RpgBot` OP，执行 `hubnpc ensure` 后复测。
- 测后：`/deop RpgBot`；`server-runtime/ops.json`：**`[]`**；`login-runtime/ops.json`：**`[]`**。

## 风险 / 备注

1. **hitbox 叠层** — ensure 后客户端仍可能看到同坐标多只隐形村民（本轮 smith/enchanter/life≈14、guide≈28）。交互以**最新 Bukkit hitbox**（高 entity id、<400000）为准可通；建议插件岗继续收紧 purge。验收交互本身 **PASS**。
2. mineflayer `clickWindow` 偶发 “Server didn't respond to transaction”，但服务端已切菜单（强化/镶嵌窗已开），以窗名为准。
3. 窗标题协议层为 `{"text":"…"}` JSON；去色后含「锻炉/附魔引导/补给」即判定通过。

**结论：** 枢纽工坊 NPC 验收 **PASS**。
