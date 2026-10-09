# 状态 · 下一档硬债选定 · 需策划（使魔闭环诚实 · 挂机副产口径 + 出战可点）

> **上游结案：** 总控加急·内容真债 1（用户要丰富玩法 / 在线时长 / 有趣系统）。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。挂机区加厚选题**作废本号**（改题使魔）。样本 R / Pack6 / 天赋·灰印 HOLD / 开 K3 live / 关观察 **仍禁**。排队债 2（日更短本第三拍）**本轮不写**。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **待批 A** · **docs-only** · **零 jar / 零改 daily_kills / 零改 ×0.97** · **≠关观察** · **≠开闸** · **≠开样本 R** · **≠ stage 脏 runtime**  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-pet-loop-honest-2026-10-10.md`](../design/DESIGN-ember-pet-loop-honest-2026-10-10.md) · backlog `B-ember-pet-loop-honest`  
**打开理由：** 使魔页宣称「升级吃魂尘（挂机副产）」，但 P1 挂机 RES **无** `soul_dust`；「出战 / 收回」只 tell + `pet list`，**不** `summon`/`dismiss`——闭环断在诚实与可点两处。

---

## 0. 局势一句话

魂尘来源说挂机是假的，出战按钮点了不出宠——先接真命令并改诚实文案。

---

## 1. 选题（加急指定）

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **使魔闭环（挂机副产诚实 + 出战可点）** | **采纳 · 需策划** | 总控加急债 1；见 §1.1 证据核验 |
| 2 | 日更短本第三拍 | **排队 · 本轮不写** | 总控标债 2；本号只交使魔 |
| — | 挂机区日/周节奏加厚 | **作废本号** | 加急改题停掉 |

### 1.1 证据核验（实扫）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | `ember_pet` 出战格 lore「升级吃魂尘（挂机副产）」；投喂格「挂机副产魂尘升级使魔」 | `plugins/TrMenu/menus/ember_pet.yml` L64 / L82 | **玩家面宣称挂机产魂尘** |
| E2 | P1 挂机 `afk.tiers` 每日量键 = `coin/xp/shard/bone/core/blank` · **无 soul_dust**；`legacy_payouts: false` | `plugins/CoreRpg/ember-v1.yml` `afk:` L128–131 | **现行挂机不发魂尘** |
| E3 | 魂尘真源：生活兑换 `life buy soul_dust` / `soul_dust_bone`（旧靴/碎骨 + 币，日 2）；战令 Lv15 等 | `plugins/CoreRpg/life.yml` · `ember_life.yml` G/K | **真源=生活/钓鱼杂物兑换** |
| E4 | 出战格 actions：tell ×2 + `command: corerpg pet list` · **无 summon/dismiss** | `ember_pet.yml` L69–74 | **点了不出宠、不收回** |
| E5 | 投喂格已接 `corerpg pet feed`（可点） | `ember_pet.yml` L88–92 · `PetService.cmdFeed` | 投喂链路在；卡在来源谎言 + 出战空壳 |
| E6 | 真命令已存在：`pet summon [id]` / `dismiss` / `feed` / `list` | `PetService.java` cmdRoot | **接菜单即可，不必新机制** |
| E7 | 玩家进使魔：枢纽「图录」→ `ember_bestiary`「使魔图鉴」→ `ember_pet`；生活孵化 lore 写「打开使魔菜单」但**未** `menu: ember_pet` | `ember_hub.yml` 图录 · `ember_bestiary.yml` P · `ember_life.yml` H/J | 入口绕；孵化后无直达 |
| E8 | NI `mat_ember_soul_dust` lore 仍写「挂机 / 副本副产」 | `plugins/NeigeItems/Items/ember-pets.yml` | 物品悬停同谎 |
| E9 | 旧 `config.yml afk_caps` 仍登记魂尘日顶文案；P1 委托 `EmberAfkService` 后该路径非现行产出 | `config.yml` afk_caps · AfkTierService 委托 | **勿把旧表当 P1 真源** |

**玩家价值：** 孵化→投喂→出战一条可玩闭环，文案不骗；外观使魔重新「看得见」，拉长生活/挂机周边在线趣味（不靠抬挂机表）。

**荐方案 M：** lore 改对齐生活/钓鱼真源 + 出战/收回接真 `summon`/`dismiss`（TrMenu 封装，玩家不打指令）；可选生活孵化后跳使魔页。

---

## 2. 派单句

```
【派单·内容真债待批A】使魔闭环诚实（挂机副产口径 + 出战可点）
优先级：总控加急债1 · docs-only · 对齐 ember_pet / life / P1 afk RES
禁：抬daily_kills/层表·样本R·Pack6·天赋/灰印HOLD·关观察·改×0.97·开K3·假平面·纯lore只改半句却仍谎称挂机

请出 DESIGN-ember-pet-loop-honest-2026-10-10.md，STATUS=待批 A · 荐 M（三叉 A/M/L）。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3；不改 ×0.97 / set_bonus；不抬 `daily_kills` / 层表；不 Pack6 / 天赋 / 灰印；不写日更短本第三拍；不默默给挂机加魂尘（=方案 L，须另批经济）；**本 tip 零代码、且不得 stage 脏 runtime**。

---

*选题使魔闭环 · tip 待批 A · 荐 M · ≠关观察 ≠抬挂机表。*
