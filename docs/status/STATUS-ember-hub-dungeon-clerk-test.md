# STATUS · 枢纽门吏 · 灰钥（日常场景锚）验收

**日期：** 2026-09-28 06:17–06:19 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-hub-dungeon-clerk.md` §7 + `docs/status/STATUS-ember-hub-dungeon-clerk.md`（落地 ✅）  
**范围：** 真实协议 mineflayer 右键 `ember_dungeon_clerk` + 萌新东侧路径 + 工坊/灰烛回归；**未改**配置 / 数值 / jar；**未** commit/push。

## 总评：PASS

六条硬条全过：广场可见灰钥、出生点东侧可达、右键 tell（体力+七线）开 `ember_daily`、无 Citizens / 话术不教指令、四岗回归、ops=`[]`。

## 分项结果（硬条）

| # | 硬条 | 结果 | 实测 / 证据 |
|---|------|------|-------------|
| 1 | `ember_hub` 可见 Ady「门吏 · 灰钥」id=`ember_dungeon_clerk`；站位 **(-14.5,58,110.5)±1**；不挡烬砧/余晶/灰粮/灰烛 | **PASS** | `hubnpc list`：`ember_dungeon_clerk 门吏 · 灰钥 @ ember_hub -14.5,58.0,110.5 yaw=90.0 action=daily menu=ember_daily`（n=4）。客户端村民 hitbox **id=9003** + Ady **id=450340** 同坐标 **(-14.5,58,110.5)**。与四岗欧氏距均 >1.2。 |
| 2 | 出生点 → 东侧见灰钥（不依赖先翻菜单大海） | **PASS** | 新号 `DkT7606` OP `tp` 至 MV spawn **(-18.5,58,110.5)** 后仅朝东行走（无 `/ember`/菜单）。约 2.4 格内见到灰钥实体；`east=true visible=true near=true`。 |
| 3 | 右键 → tell 含**体力语义** + **七线提法** → 打开 `ember_daily` | **PASS** | `activateEntity` hitbox **9003** → tell：`灰钥：日常窟靠体力进…一条扣 30，0 点回满。` / `庭院、焦骨、地窖、潮蚀、断塔、霜晶、锈轨——点菜单选一条。` / `灰钥：点开选本…` → 窗标题 **「日常 · 余烬窟」**（TrMenu `ember_daily`）。七线命中 **7/7**。 |
| 4 | 无 Citizens；话术无教指令（无 /hub /dp /mvtp /corerpg 等教学） | **PASS** | `plugins/` **无** Citizens jar；`/npc` 未知命令。灰钥三句 tell **无** `/hub` `/dp` `/mvtp` `/corerpg` `/ember` `/trmenu`。 |
| 5 | 回归：灰烛 talk、烬砧/余晶/灰粮 右键仍开各自菜单 | **PASS** | 烬砧 act=9002 → tell+窗 **「余烬锻炉 · 锻造」**；余晶 act=8902 → tell（手持附魔晶…）+ **「咒火师 · 附魔引导」**；灰粮 act=9104 → **「补给 · 生活」**；灰烛 act=8898 →「这城烧了三十年…」→「挂机庭有游荡的尸骸…」→ 目标 0/6（**未**用 `/corerpg quest talk`）。 |
| 6 | 测后 ops=[]（play+login） | **PASS** | `server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`（`/deop` + 强制写空）。 |

## 环境 / 账号

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.18**（latest.log `enabled`） |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 玩家 bot | `DkT7606`（新号，非 OP 交互路径） |
| 临时 OP | `RpgBot` uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`（测后清空） |
| 脚本 / JSON | `/tmp/dungeon-clerk-test.js` · `/tmp/dungeon-clerk-test.json` · out `/tmp/dungeon-clerk-test.out` |

## 窗名摘要

| NPC | 窗名（协议去色） |
|-----|------------------|
| 门吏 · 灰钥 | 日常 · 余烬窟 |
| 烬砧 | 余烬锻炉 · 锻造 |
| 余晶 | 咒火师 · 附魔引导 |
| 灰粮 | 补给 · 生活 |
| 灰烛 | （无窗；主线 talk） |

## 风险 / 备注

1. **hitbox 叠层**（工坊旧债）— 工坊三岗附近客户端仍可见大量隐形村民（本轮 smith≈101 / enchanter≈100 / qm≈102 / guide≈111）；交互以最新 Bukkit hitbox（高 id、<400000）为准可通。灰钥处仅 2 层（9003+450340），较干净。建议插件岗继续 purge；**不挡本轮 PASS**。
2. 灰烛主线目标文案仍含「/ember → 挂机庭」指路（quest 旧债，非灰钥话术）；本硬条只约束**灰钥 tell** 不教指令 — **PASS**。
3. 身旁可选告示（设计 §4）落地 STATUS 已标未落；非验收硬条 — **SKIP**（不计入总评）。
4. 本轮**未改**任何配置 / 体力数值 / jar；**未** commit/push。

## 收尾

- 测中：`console.in` + `ops.json` 临时 OP `RpgBot`；`hubnpc list/ensure`；`tp` 定位。
- 测后：`/deop RpgBot` + `/deop DkT7606`；play/login **`ops.json=[]`**。

**结论：** 枢纽门吏 · 灰钥验收 **PASS**（场景锚可用）。
