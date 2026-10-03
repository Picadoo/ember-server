# STATUS · 枢纽 hitbox + 灰烛去指令 · 短验收

**日期：** 2026-09-28 06:25–06:27 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/status/STATUS-ember-hub-hitbox-quest-cleanup.md`（CoreRpg **1.15.19** live）  
**范围：** `hubnpc count` / ensure×2 不叠层；灰钥/工坊菜单回归；灰烛 talk 零 slash 教学；测后 ops=`[]`。  
**禁项：** **未改**配置 / 数值 / jar；**未** commit/push。

## 总评：PASS

四条硬条全过。工坊四岗 + 灰烛 Bukkit hitbox 各 **1**（Ady 另 1）；ensure×2 后仍各 1；交互菜单回归；灰烛可见句无 `/ember` `/corerpg` `/dp` 等教学且能推进主线目标。

## 分项结果（硬条）

| # | 硬条 | 结果 | 实测 / 证据 |
|---|------|------|-------------|
| 1 | `hubnpc count`：灰钥/烬砧/余晶/灰粮 **各 Bukkit hitbox≈1**（Ady 另计）；ensure 再跑不叠层 | **PASS** | 基线 count：四岗均为 `near_candidates=1 tagged_or_meta=1`。`hubnpc ensure`×2 后再 count：仍各 **1**。`quest npc count`：`near_candidates=1 tagged_or_meta=1` @ -16.5,58,106.5；ensure 后再 count 仍 1。客户端扫描（半径 2.2）：四岗+灰烛均为 `bukkit=1 ady=1`（总 2/岗，非百层）。 |
| 2 | 右键灰钥开 `ember_daily`；工坊三岗菜单回归 | **PASS** | 灰钥 act=767 → tell（体力+七线）+ 窗 **「日常 · 余烬窟」**。烬砧 act=766 → **「余烬锻炉 · 锻造」**。余晶 act=764 → **「咒火师 · 附魔引导」**。灰粮 act=765 → **「补给 · 生活」**。 |
| 3 | 右键灰烛 talk：可见句 **无** `/ember` `/corerpg` `/dp` 等教学；仍能推进目标 | **PASS** | 灰烛 act=768 →「这城烧了三十年…」「挂机庭有游荡的尸骸…」→ 主线推进至「在挂机庭击败 挂机庭僵尸 0/6（**打开枢纽菜单 → 挂机庭 → …**）」。可见句 **无** `/ember` `/corerpg` `/hub` `/dp` `/trmenu`。`quest.yml` 玩家可见 hint/done 扫描亦无 slash 教学。 |
| 4 | 测后 ops=`[]`（play+login） | **PASS** | `server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`（`/deop` + 强制写空）。 |

## 环境 / 账号

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.19**（jar `plugin.yml` + live） |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 玩家 bot | `HbQ4721`（新号，非 OP 交互） |
| 临时 OP | `RpgBot`（测后清空） |
| 脚本 / JSON | `/tmp/hub-hitbox-quest-test.js` · `/tmp/hub-hitbox-quest-test.json` · out `/tmp/hub-hitbox-quest-test.out` |

## hubnpc / quest count 摘要

| 岗位 | ensure 前 | ensure×2 后 | 客户端 bukkit/ady |
|------|-----------|-------------|-------------------|
| ember_dungeon_clerk（灰钥） | near=1 tagged=1 | near=1 tagged=1 | 1 / 1 |
| ember_smith（烬砧） | 1 / 1 | 1 / 1 | 1 / 1 |
| ember_enchanter（余晶） | 1 / 1 | 1 / 1 | 1 / 1 |
| ember_quartermaster（灰粮） | 1 / 1 | 1 / 1 | 1 / 1 |
| ember_guide（灰烛） | quest near=1 tagged=1 | ensure 后再 count=1 | 1 / 1 |

## 窗名 / 灰烛可见句摘要

| NPC | 窗名或可见句 |
|-----|-------------|
| 门吏 · 灰钥 | 日常 · 余烬窟 |
| 烬砧 | 余烬锻炉 · 锻造 |
| 余晶 | 咒火师 · 附魔引导 |
| 灰粮 | 补给 · 生活 |
| 灰烛 | 「这城烧了三十年…」→「挂机庭有游荡的尸骸…」→ 目标「打开枢纽菜单 → 挂机庭 → ① 灰坡…」（**无** slash 指令） |

## 风险 / 备注

1. 客户端仍为 **Ady + 1 Bukkit hitbox**（设计如此；本轮稳定为 2/岗，已非百层叠层）。  
2. 本轮**未改**任何配置 / 体力数值 / jar；**未** commit/push。  
3. 前债（工坊≈100 层 / 灰烛目标含 `/ember`）在 1.15.19 live 上已清；本验收复测确认。

## 收尾

- 测中：临时 OP `RpgBot`；`hubnpc count|ensure` · `quest npc count|ensure`；玩家右键交互。  
- 测后：`/deop RpgBot` + `/deop HbQ4721`；play/login **`ops.json=[]`**。

**结论：** 枢纽 hitbox + 灰烛去指令短验收 **PASS**。
