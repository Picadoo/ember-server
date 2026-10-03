# STATUS · P1b 灰烛真实右键交互验收

**日期：** 2026-09-27 20:52 CST（Asia/Shanghai）  
**岗位：** 余烬-测试岗  
**依据：** `docs/status/STATUS-ember-hub-maps-p1b.md`（CoreRpg 1.15.3）；对照 `docs/status/STATUS-ember-hub-maps-p1-test.md` 项 2b  
**范围：** 真实协议右键与 P1 hub/地图抽检；未改玩法数值、afk 配置或地图配置。

## 总评：PASS

P1b 灰烛真实右键链路已通过，原 P1 项 2b **PASS**；其余必测抽检继续通过。

## 分项结果

| 项目 | 结果 | 实测 / 证据 |
|---|---|---|
| 2b 灰烛真实右键、推进 talk | **PASS** | 新号 `P1bR927` 从出生点走近后，mineflayer `activateEntity`（等价 `use_entity`）点击坐标 `(-16.5,58,106.5)` 的 **Bukkit 隐形 hitbox entity id=45**；服务端返回「灰烛：这城烧了三十年，火没灭，只是躲进了地底。」「灰烛：挂机庭有游荡的尸骸，先去活动活动筋骨。」并推进为「在挂机庭击败 挂机庭僵尸 0/6」。`P1bT927` 复验结果相同。未使用 `/corerpg quest talk` 代替真实右键；仅用 `/corerpg quest` 做前后状态旁证。|
| 双村民识别 | PASS | 客户端同时看到 `villager`：id **45**（hitbox，无名）及 id **450191**（Ady 包实体，名「引路人 · 灰烛」）；本次实际点通的是 id **45**。|
| 回枢纽落点 | **PASS** | `AfkMap1378` 从②菜单点击「挂机庭」→「回枢纽」，落点 **(-18.5,58.0,110.5)**，聊天确认「已回到枢纽」。新号出生点同坐标。|
| `/ember` | **PASS** | 可打开，窗口标题「余烬 · 冒险枢纽」。|
| 工坊锚可辨 | **PASS** | `(-9,58,105)=furnace`；`(-9,58,104)=anvil`；`(-10,58,106)=crafting_table`；工坊牌 `(-13,58,105)=standing_sign`。|
| 挂机②入口贴地 | **PASS** | 通过 `/ember` →「挂机庭」→「② 荒原 Lv20」进入，坐标 **(200.5,62.0,250.5)**；脚下 `(200,61,250)=quartz_block`，玩家 y62，入口上方 y62 为空，未见旧空岛入口。|

## 环境 / 版本证据

- 服务端日志确认 `CoreRpg 1.15.3 enabled`。
- 日志确认 Adyeshach `core.event` 与 `api.event` 两个 interact hook 均 active=2；Bukkit hitbox 已在 `ember_hub -16.5,58.0,106.5` 建立。
- 测试使用新号 `P1bR927`、`P1bT927`；地图回程使用已有 Lv42 账号 `AfkMap1378`。
- 本轮未改玩法数值、任务数值、掉落、等级、afk 配置或地图配置。

## 收尾 / ops

- `server-runtime/ops.json`: **`[]`**
- `login-runtime/ops.json`: **`[]`**
- 本轮未启用临时 OP；收尾时两侧 ops 均为空。

**结论：** P1b 真实右键交互验收通过，项 2b 从 FAIL 修复为 **PASS**。
