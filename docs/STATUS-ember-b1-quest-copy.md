# STATUS · B1.4 主线第一卷 hint / join 文案扫尾（仅文档）

- 时间：2026-09-27（Asia/Shanghai）
- 设计：余烬-策划
- 依据：`docs/design-ember-content-backlog.md` §B1.4；UX：玩家入口走 TrMenu / 右键 NPC，不教聊天指令
- 范围：**玩家可见**文案：`plugins/CoreRpg/config.yml` → `join_message`；`quest.yml` 第一卷 **ch1～6** 的 `hint` / `done` / `intro`（本扫未改 YAML）
- 落地：余烬-插件改 YAML（建议同步 `src/main/resources` 与 `plugins/CoreRpg`）；本岗不落配置
- 第二卷 ch7～10：无 `/corerpg` `/dp` `/hub`，但仍写「打开 /ember」——见文末「顺带可选」；**本单验收不强制改卷二**

## 1. 验收硬条

1. `join_message` 四行（及续行）玩家可见文本 **无** `/corerpg` `/dp` `/hub`，也 **无**「指令：…」清单式教学。
2. `quest.yml` ch1～6 的 **hint / done / intro** 玩家句 **无** `/corerpg` `/dp` `/hub`，也 **无** `covenant set …` / `talent unlock …` / `afk N` 等参数教学。
3. 指引只留：**打开枢纽菜单 → …**、**右键灰烛 / 工坊 NPC**、场景方位（出生点北 · 工坊旁）。
4. 管理/测试命令可留在注释或运维文档；**不得**出现在上述玩家字段。

## 2. 扫查摘要

| 文件 | 命中（玩家可见） | 说明 |
|------|------------------|------|
| `config.yml` `join_message` | 3/4 行含指令 | 第 2 行已干净 |
| `quest.yml` ch1～6 `hint` | 12 处含 `/ember` 和/或 `/corerpg` | 见下表 |
| `quest.yml` ch1～6 `done` | 4 处含 `/corerpg` 或「或 /corerpg…」解锁播报 | 挂机解锁三条 + skill 一句 |
| `quest.yml` ch1～6 `intro` / `desc` | 0 | 已干净 |
| `/dp` `/hub` | **0** | 本范围未出现 |

## 3. 替换文案表（插件按行改）

约定：路径一律写成「打开枢纽菜单 → …」；不写斜杠命令。颜色码保持原 § 风格。

### 3.1 `config.yml` · `join_message`

| # | 现文（问题点加粗语义） | 建议替换 |
|---|------------------------|----------|
| 1 | `跟着主线走：引路人·灰烛 就在出生点北边（/corerpg quest 查看目标）。` | `跟着主线走：引路人·灰烛就在出生点北边——右键他看当前目标。` |
| 2 | （已干净）挂机庭…打开枢纽菜单可返回 | **不改** |
| 3–4 | `指令：/ember 主菜单 · /corerpg enhance 强化 · /corerpg socket 镶嵌 · /corerpg sign 签到`（跨两行） | 合并为一行：`打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。` |
| 5 | `进本门槛：…（/ember 进入）` | `进本门槛：周本 Lv.20 · 深渊 Lv.25 · 灾厄 Lv.30 · 团本 Lv.35——打开枢纽菜单进入。` |

### 3.2 `quest.yml` · 第一卷 ch1～6

| 章·步骤 | 字段 | 现文要点 | 建议替换 |
|---------|------|----------|----------|
| ch1 杀挂机僵尸 | hint | `/ember → 挂机庭 → ① 灰坡 …` | `打开枢纽菜单 → 挂机庭 → ① 灰坡 · 落点往西偏南五十来格的草坡是怪区 · 手持余烬之刃 · 打不过就打开枢纽菜单返回` |
| ch1 签到 | hint | `/ember → 签到，或 /corerpg sign` | `打开枢纽菜单 → 签到` |
| ch2 日窟击杀 | hint | `/ember → 日常本…` | `打开枢纽菜单 → 日常本（需日票）· 本次通关也算完成` |
| ch2 誓约 | hint | `/ember → 誓约，或 /corerpg covenant set blaze\|ash\|warden …` | `打开枢纽菜单 → 誓约 · 首次免费 · 选烬刃 / 灰印 / 守墓之一（决定技能）` |
| ch2 誓约 | done | `试试 /corerpg skill。` | `试试打开枢纽菜单 → 技能。` |
| ch2 天赋 | hint | `/ember → 天赋，或 /corerpg talent … unlock <节点>` | `打开枢纽菜单 → 天赋 · Lv10 起有点数，点亮任一节点即可` |
| ch2 强化 | hint | `手持装备 /ember → 强化，或 /corerpg enhance …` | `手持装备，打开枢纽菜单 → 强化 · 耗碎片 + 少量余烬币` |
| ch2 Lv20 | done 挂机行 | `/ember → 挂机庭，或 /corerpg afk 2` | `§a[挂机] 挂机庭 ② 荒原（Lv20）已开放：打开枢纽菜单 → 挂机庭 → ② 荒原` |
| ch3 周本通关 | hint | `/ember → 周常本` | `打开枢纽菜单 → 周常本` |
| ch3 悬赏 | hint | `/corerpg bounty` | `打开枢纽菜单 → 悬赏（完成并领取）` |
| ch4 深渊击杀 | hint | `/ember → 深渊…` | `打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` |
| ch4 Lv30 | done 挂机行 | `/corerpg afk 3` | `§a[挂机] 挂机庭 ③ 焦土（Lv30）已开放，偶尔掉核心碎片：打开枢纽菜单 → 挂机庭 → ③ 焦土` |
| ch5 灾厄奔赴 | hint | `/ember → 灾厄 → 奔赴，或 /corerpg calamity join` | `打开枢纽菜单 → 灾厄 → 奔赴` |
| ch6 团本击杀 | hint | `/ember → 团本…` | `打开枢纽菜单 → 团本（3～5 人）· 本次通关也算完成` |
| ch6 团本通关 | hint | `/ember → 团本…` | `打开枢纽菜单 → 团本（3～5 人）` |
| ch6 卷终 | done 挂机行 | `（/corerpg afk 4）` | `§a[挂机] 到 Lv40 可去挂机庭 ④ 烬原深处（打开枢纽菜单 → 挂机庭 → ④）继续攒锻造材料。` |

**未改（已合规）示例：** 找灰烛的 talk hint（「出生点北边 / 工坊旁 / 右键」）、附魔 hint（工坊附魔台 + 余烬附魔晶）、纯玩法 hint（骷髅贴近、深渊每日次数等）。

## 4. 建议专岗与落地顺序

| 专岗 | 动作 |
|------|------|
| **余烬-插件** | 按 §3 改 `plugins/CoreRpg/config.yml` + `quest.yml`，并同步 `CoreRpg/src/main/resources/` 同源文件；`/corerpg reload` 或短重启 |
| **余烬-测试** | 新号进服看 join 四行；`quest set` 抽测含 `/corerpg` 的旧步骤 hint/done 是否已无斜杠命令 |
| 策划 | 本 STATUS 即设计片段；不改平衡、不改步骤 type/count |

## 5. 顺带可选（非本单硬验收）

第二卷 ch7～10 的 hint 仍写「打开 **/ember**（或枢纽菜单）→…」。无 `/corerpg`/`/dp`/`/hub`，但与「只留菜单路径、不教斜杠」不完全一致。若插件顺手统一，把「打开 /ember（或枢纽菜单）」一律改成「打开枢纽菜单」即可（约 13 处），可记为 B1.4b。

## 6. 结论

B1.4 **设计交付完成**：问题句已标、替换表可直接贴 YAML。插件改完后验收 §1 四条即可关单。
