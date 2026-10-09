# 余烬 · 使魔闭环诚实（挂机副产口径 + 出战可点）

STATUS=**待批 A · 荐方案 M** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-ember-pet-loop-honest-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-ember-pet-loop-honest-need-design-2026-10-10.md) · backlog `B-ember-pet-loop-honest` · 总控加急内容真债 1

> **一句话玩家价值：** 使魔从「菜单空壳」变成可出战的外观闭环；魂尘来源说真话（生活/钓鱼），点出战真的出宠——提高趣味与在线粘性，不抬挂机日顶。

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_pet.yml` | 出战格 lore「升级吃魂尘（**挂机副产**）」；投喂格「**挂机副产**魂尘升级使魔」；出战 actions = tell + `corerpg pet list`（**无** summon/dismiss） |
| E2 | `plugins/CoreRpg/ember-v1.yml` `afk.tiers` | P1 挂机每日量键 = `coin/xp/shard/bone/core/blank` · **无 `soul_dust`**；`legacy_payouts: false` |
| E3 | `plugins/CoreRpg/life.yml` + `ember_life.yml` | 魂尘真源：`soul_dust` / `soul_dust_bone`（旧靴×2 / 碎骨×2 + 5 币 · 日 2 · 生活 Lv2）；孵化蛋 `pet_ashling`/`pet_cinder`（魂尘×10 + 600 币 · 周 1 · Lv4） |
| E4 | `PetService` | 真命令已在：`list` · `summon [id]` · `dismiss` · `unlock` · `feed`；`max_active: 1`；feed 吃 `mat_ember_soul_dust` |
| E5 | 入口 | 枢纽「图录」→ `ember_bestiary`「使魔图鉴」→ `ember_pet`；`ember_hub` **无**直达使魔（D63 旧入口在 legacy）；生活孵化 lore 写「打开使魔菜单出战」但 actions **无** `menu: ember_pet` |
| E6 | NI `ember-pets.yml` `mat_ember_soul_dust` | lore「挂机 / 副本副产」——与 E2 同谎；投喂指引已人话化（B2.44） |
| E7 | 旧 `config.yml afk_caps` 魂尘日顶 | 旧分层挂机文案残留；P1 现行走 `EmberAfkService`，**不得**当本窗「挂机仍产魂尘」证据 |
| E8 | Stage2 / 硬禁 | 观察续；禁抬 `daily_kills`/层表、样本 R、Pack6、天赋/灰印、关观察、改 ×0.97、开 K3 |

**一句话问题：** 菜单说挂机给魂尘（假）+ 出战按钮不出宠（断）——要在**不抬挂机产能**前提下把口径改真、把出战接上真命令。

---

## 1. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 lore** | 使魔页 / NI 魂尘文案改对齐生活·钓鱼真源；出战格仍只 list | 止谎；**出战闭环仍断**；不荐独批 |
| **M · 出战接真命令 + lore 诚实（荐）** | TrMenu 封装 `summon`/`dismiss`（玩家不打指令）+ lore/NI 改真源；投喂保留；可选生活→使魔直达 | **荐** — 闭环可玩；零抬挂机表；复用现有 PetService |
| **L · 挂机极薄加魂尘** | P1 `afk.tiers` 另加极薄 `soul_dust`（日顶另钉）使「挂机副产」变真 | **本窗不荐** — 动挂机 RES = 经济窗；须另批 + p1sim/`afk.py`；易撞「抬层表/挂机 R」过敏区 |

**批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 开 R ≠ 默默给挂机加魂尘。**

---

## 2. 可落地（方案 M）

### 2.1 玩家路径（TrMenu · 零裸指令教学）

| 步 | 路径 | 说明 |
|----|------|------|
| 进使魔 | 枢纽 **图录** → **使魔图鉴** → `ember_pet` | 现网已通；本窗**不**强制把使魔抬回主枢纽一级（D63 仍有效）；可选 W1d 在「补给·生活」加半行跳转 |
| 拿魂尘 | 枢纽 **补给·生活** → 旧靴/碎骨 → 余烬魂尘 | 真源；日 2×2 |
| 孵化 | 同页 → 孵化灰灵/烬火 | 得蛋；首次出战可消耗蛋 unlock（现网 PetService） |
| 出战/收回 | `ember_pet` **出战/收回** 格（或拆成两格） | **接真命令**（§2.2） |
| 升级 | `ember_pet` **魂尘投喂** | 已接 `pet feed`；只改 lore |

玩家面文案**禁止**写「请执行 /corerpg pet …」；底层可用 `command:` 封装。

### 2.2 出战真命令对接边界

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 出战可点（必做）** | `ember_pet` 出战入口改为封装真命令。荐形（施工择一，设计钉边界）：① **左键出战 / 右键收回**（同格）；或 ② **拆两格**「出战」「收回」。出战：`corerpg pet summon`（无参 = 上次 active / 最近解锁，对齐 `cmdSummon`）；可选再加灰灵/烬火分格 `summon ashling` / `summon cinder`（已解锁才亮）。收回：`corerpg pet dismiss`。成功/失败靠现网 tell，菜单可再加一行短 tell「已出战/已收回/没有可出战」 | 不改 `max_active: 1`；不改 power_bonus 公式；不新永久乘区 |
| **W1b · lore 诚实（必做）** | 出战格：删「挂机副产」；改为「升级吃魂尘（**生活·钓鱼兑换**）」类短句。投喂格：同改「生活兑换魂尘升级使魔 · 非挂机日表」。Open tell 可半行「魂尘：补给·生活」 | 不改 feed 数值表（`pet.yml` cost_*） |
| **W1c · NI 魂尘悬停（必做同批或紧随）** | `mat_ember_soul_dust`「挂机 / 副本副产」→「生活·钓鱼兑换 · 可堆叠」（或等价短句）；保留「用于枢纽 · 使魔 · 投喂」 | 不改 NI 给物/配方键 |
| **W1d · 生活闭环轻补（可选同批）** | 孵化成功后 `menu: ember_pet`（或 tell + 打开）；`ember_life` 加「打开使魔」格 → `menu: ember_pet` | 不改兑换价/周顶 |

**插件边界：** 若仅 TrMenu `command:` 封装即可闭环 → **本号可不改 Java**。若需「已出战态」菜单条件/PAPI（如 `%corerpg_pet_active%`）→ 另开极薄插件半窗，**仍禁**动挂机 RES。

### 2.3 产资源 / 奖励边界

| 项 | 本窗 M |
|----|--------|
| 魂尘来源 | **揭示真源**（生活兑换为主；战令等既有保留）；**不**把 P1 挂机改成产魂尘 |
| 挂机 `daily_kills` / 层每日量 / 离线 25% | **不动** |
| 使魔战力 | 仍为展示/天梯 `power_bonus` 微量；**不加**战斗属性层；不卖满级战力宠 |
| 方案 L | 另号经济设计 + 总控显式批；建议量级若日后开：日顶个位数魂尘且须 `afk.py` 对照——**本窗不批 L** |

### 2.4 地图约束

使魔为跟随盔甲架外观；**无**新挂机图、**无**假平面 AFK 板。挂机世界仍 `ember_afk` 既有真地形落点（本债不改地图）。

### 2.5 与 Stage2 观察边界

| 项 | 口径 |
|----|------|
| 本窗 | **docs-only** 待批 A |
| 施工 | 总控批 A 后**另号**（TrMenu ± NI；插件仅当 PAPI/条件必需） |
| 观察 | **≠关观察**；**≠改 ×0.97 / set_bonus**；**≠开 K3 live** |
| 样本 R | **≠开**；本债 ≠ 挂机 R（不抬日表） |

### 2.6 验收（施工号用 · 本号只定规格）

| ID | 步骤 | 预期 |
|----|------|------|
| P1 | 打开 `ember_pet` | 出战/投喂 lore **无**「挂机副产」；写生活/钓鱼 |
| P2 | 已解锁使魔 → 点出战 | 盔甲架使魔出现；再点收回消失（或右键收回） |
| P3 | 无解锁无蛋 → 点出战 | 现网失败提示；不崩菜单 |
| P4 | 有魂尘 → 点投喂 | 仍升级（回归） |
| P5 | 悬停魂尘 NI | 无「挂机副产」谎称 |
| P6 | `ember-v1.yml afk.tiers` | **仍无** soul_dust 键（M 路径） |

**不跑 p1sim**（零挂机 RES 变更）。若误批 L 动挂机表 → 必须挂机对照模拟（本荐不做）。

---

## 3. 本窗不做（硬禁）

- 抬 `daily_kills` / 离线% / 各层每日量·币表（含默默加魂尘当「修文案」）  
- 开样本 R 全表 / Pack6 / 天赋·灰印 HOLD 重开  
- 提前关观察 / 改 ×0.97 / 翻 set_bonus / 开 K3 live / `k3_refine`  
- 假平面 AFK / 新挂机地图包  
- 纯 lore 只改半句却仍写「挂机副产」  
- 玩家面教裸 `/corerpg pet …`  
- 日更短本第三拍（排队债 2）  
- stage 脏 runtime（ladder / calamity-state / p1-six / MM SavedData 等）

---

## 4. 邻域边界

| 邻域 | 边界 |
|------|------|
| D305 挂机软身份 / D285 满额 | **不重开**；本债不改挂机菜单名片 |
| D306 日路由 | 不抢；使魔不强制进枢纽「今天卡」 |
| 生活 `ember_life` | 真源；可 W1d 互跳 |
| B2.44 魂尘去斜杠 | 保留投喂人话；本窗改「来源」行 |
| 挂机 R（D308） | **仍不得开**；L ≠ 偷开 R，须另批经济 |

---

## 5. 批注勾选（总控）

- [ ] **批 M**（W1a 出战接真命令 + W1b/W1c lore·NI 诚实必做；W1d 可选）→ 另派施工号 · **策划荐**  
- [ ] **批 A**（仅 lore/NI 诚实；出战后置）· **不荐独批**  
- [ ] **批 M+L**（M + 另开经济号议极薄挂机魂尘）· **不荐本窗绑死 L**  
- [ ] **批 L 独开**（先动挂机 RES）· **否决默认**  
- [ ] 驳回改派（理由：________）

**策划荐勾：** **批 M**。

**说明：** 批 A ≠ 施工 ≠ 关观察 ≠ 开 R ≠ 授权挂机加魂尘。

---

## 6. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · 初稿 STATUS **待批 A** · 荐 M；加急改题停挂机节奏稿；只交使魔号 |

---

## 7. 参考

- tip · 本 STATUS 同 slug  
- `plugins/TrMenu/menus/ember_pet.yml` · `ember_life.yml` · `ember_bestiary.yml` · `ember_hub.yml`  
- `plugins/CoreRpg/ember-v1.yml` `afk:` · `pet.yml` · `life.yml`  
- `PetService.java` · NI `ember-pets.yml`  
- afk-p1 · [`DESIGN-ember-afk-p1-2026-10-04.md`](DESIGN-ember-afk-p1-2026-10-04.md)  
- 图鉴壳 · [`DESIGN-ember-pet-bestiary.md`](DESIGN-ember-pet-bestiary.md)

---

*待批 A · 荐方案 M · docs-only · ≠关观察 ≠抬挂机表 ≠开样本 R。*
