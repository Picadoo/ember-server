# DESIGN · 防具签名规格 + 叠合门禁（stage0 · HOLD 施工）· 2026-10-12

> **状态：** OPEN · stage0 · **可批、不可施工** · 对接 TIP-AB · 2026-10-12 Asia/Shanghai  
> **对接 tip：** [`TIP-ember-armor-signature-gate-design-stage0-thin-d12552-d12559-2026-10-12.md`](../tips/TIP-ember-armor-signature-gate-design-stage0-thin-d12552-d12559-2026-10-12.md)  
> **上游：** TIP-AA CLOSED docs-only `79247dab2` · [`RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md`](RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md) · D174 [`DESIGN-ember-mainline-unlocks-2026-10-04.md`](DESIGN-ember-mainline-unlocks-2026-10-04.md) §2.1 / §5 · COORD-legendary-effects（行为型 · ≤2 生效 · 禁伤乘）  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · **零 jar / 零插件派单 / 零 Economy-pom** 直至门禁过线 **且** 总控显式开工

---

## 0. 文首勘误 · D174「2c」

| 旧口径（D174 §5） | 现网（2026-10-12） | 本 DESIGN 管什么 |
|-------------------|-------------------|------------------|
| `2c（暂缓）` = **6 槽护甲** + **防具签名 1 条上限** + W30 重跑 | **甲槽已另轨 live**（D312→D317→T1→Stage2→**D453 绿出口 PASS**；共鸣 F · 四件套 ×0.97 · 三开关开） | **不**再开甲槽、不关/拧六槽三开关 |
| 「护符签名改叫防具签名」尚未施工 | `EmberSignature.DEFS` 仍全为 `blade` \| `charm`；`mods()` / 烙印仍按「刃 1 + 护符 1」 | **防具签名规格** + **叠合门禁 S1–S4** |
| COORD-mainline-unlocks：`2c on hold` | 标签过时；甲半已完成 | 正式把「2c 真债」钉为：**防具签名 + 与现网六槽/签名经济的叠合** |

**钉死（勘误一句）：** D174「2c」里「上六槽」一半已完成；本文件**不是**再开甲槽工程，而是把仍 OPEN 的 **防具签名（5 防具取 1）** 与 **叠合门禁** 落成可批 stage0 规格。施工 **HOLD** 至 S1–S4 过线 + 总控显式开工令。

---

## 1. 一句话

把 RESEARCH 已列规则写成可批规格：防具侧签名行为型、同时生效仍 ≤2（刃 1 + 防具侧 1）、默认「可烙不可掉」扩甲；四道叠合门禁不过线 **零 jar**。

---

## 2. 防具签名规格（stage0 · 可批）

### 2.1 命名与槽族

| 项 | 规格 |
|----|------|
| 旧名 | 「护符签名」（`slot=charm`） |
| 新名（玩家 / 文档） | **防具签名**（defense-side signature） |
| 可落槽 | `charm` **或** `head` / `chest` / `legs` / `boots`（六槽 `ARMOR_SLOTS`） |
| 刃 | **不变**：刃签名仍只能落 `blade`；刃生效规则不变 |
| 定义表 | 既有 `L02/L03/L06/L08/L10/L12/L14/L15` 等原 `charm` 行，语义升格为「防具侧」；**本 stage0 不新加签名 id / 不改修饰键数值** |
| 族绑定 | 沿用 D174：族绑签名只在对应套装事件下生效；`any` 通用任意套 |

### 2.2 同时生效封顶（传奇线四条精神）

对齐 COORD-legendary-effects + D174 §0 / §2.2：

1. **效果改行为，不加伤害乘区**（已有代价乘区写法不变；**禁止**新开伤乘 / skill_mult）。  
2. **同时最多 2 条生效**：**刃 1** + **防具侧 1**（原「护符 1」扩成「5 防具取 1」）。  
3. **同 tag 不叠**：刃与防具侧同 tag → 只留刃；两件防具侧同 tag → 按 §2.6 只留 1。  
4. **与天赋同 tag 不叠**：沿用 `Def.excl`；选了对应天赋则该签名显示「与天赋 X 同类，不生效」。

**禁止：** 5 件甲各算一条；防具侧 + 护符再各算一条（护符已计入「5 防具」）。

### 2.3 Tag family

| Tag 族（既有） | 规则 |
|----------------|------|
| burn / burst / heal·mend / move·dodge* / control·shape·guard·supply… | 同 tag 最强/刃优先；不跨件叠乘叙事 |
| 与四件套 ×0.97 | **正交**：套装减伤是 Stage2 C 既有；签名**不得**再叠出「新乘区叙事」或改 ×0.97 |
| 与 Stage1 F 共鸣 | 甲跟护符共鸣公式 **不动**；签名只挂行为修饰，不改共鸣权重 / 成色输入 |

### 2.4 Stampable / 烙印 / 掉落（默认候选）

| 路径 | stage0 默认 | 理由 |
|------|-------------|------|
| **烬炉烙印** | **扩**：图鉴里原护符签名可烙到「正在用的」同定义防具槽（charm **或** 任一一甲），费用/徽记门槛 **不变**（5 徽记 + 300×阶级币；T1+） | D4 拓印精神；件数/成色输入不变 → W30 友好 |
| **重打掉落 stamp** | **不扩甲**：`stampable(map, slot, family)` 对 `head/chest/legs/boots` 仍空；掉落签名件仍只打 **刃 / 护符** | 保「每局件数/成色分布」；禁顺手抬 `STAMP_RATE` |
| 软保底 / pity | **不改**阈值与期望局；不借扩甲抬表 | 硬禁 pity densify |
| 若将来改「甲也可掉签名」 | 须 **重开** S2 + S4，显式建模件数，**另** tip；本 stage0 **不**预授权 |

`fits` / `stampable` / 烙印目标校验的实现口径（施工期才动码）：防具侧 Def 的 `slot` 语义从「仅 charm」改为「defense-side」（实现可选：slot 字段扩枚举，或 `fits` 对原 charm-def 接受五槽）——**具体 Java 切片属 stage2 施工 tip，本文件只钉行为**。

### 2.5 与六槽边界

| 系统 | 本 DESIGN |
|------|-----------|
| 六槽 `enabled` / `migrate` / `set_bonus` | **只读**；不关不拧 |
| 四件套 ×0.97 / 共鸣 F | **不动** |
| K3 / 档 S / 白板补建模 | **非本主题**；禁夹带 |
| 甲路径 auto/ask/mute（D549） | 正交；扩签不改换装 UX |

### 2.6 多甲带签时的选取（生效恰好 ≤1）

当已装备的 charm+四甲中 **多于 1 件** 带防具侧签名时，按序取 **恰好 1**（全部被 excl/tag/双签锁关掉则 0）：

1. **双签名未解锁**（Q03 前）：整侧防具签名均不生效（与现行「双签前只 1 条且偏刃」精神一致；刃优先）。  
2. **天赋 excl**：候选中与已选天赋同 tag 的剔除。  
3. **与刃同 tag**：整侧剔除（只留刃）。  
4. **剩余 ≥2**：固定优先级 **charm > head > chest > legs > boots**（稳定、可单测；不按「战力」动态抢）。  
5. UI：未生效的防具签名悬停显示「防具侧已有生效签名：xxx」或「与刃同 tag / 与天赋同类」。

---

## 3. 叠合门禁表 S1–S4（HOLD 施工门槛）

> 四门 **全部 PASS** 之前：**零 jar、零插件派单、零改 `EmberSignature` / Loadout / loot / STAMP / TrMenu**。  
> 任一 FAIL → 停；可改规格或补 sim，**禁止**抬 STAMP / 伤乘 / Affix 凑线。

| ID | 门禁 | 过线条件 | 主要产出 | 与 RESEARCH |
|----|------|----------|----------|-------------|
| **S1** | **防具签名规则落地可核** | 书面规格冻结：5 防具取 1、刃规则不变、tag/excl、§2.6 优先级；单测用例表（0/1/多甲、excl、同 tag）可勾选 | 本 DESIGN §2 + 用例清单 | G-sig-1 + 规则债 |
| **S2** | **烙印/掉落扩甲边界** | 默认候选锁定：**烙印扩甲 · 掉落不扩甲**；`STAMP_RATE` / 徽记周池 / imprint 价 **不变**；若改掉落须另开设计并重跑 S4 | 门禁纪要 +（施工前）stampable 预期表 | G-drop |
| **S3** | **生效优先级 + 组合可达** | p1sim：防具侧 1 规则下，单条 + 合法刃×防具组合 + 天赋 excl；**不**与 ×0.97 叠出新乘区叙事；上偏精神对齐 D174（不超既有门槛） | `out-*-armor-sig-42.md`（命名施工期定） | G-sig-2 + G-42 |
| **S4** | **W30 / 经济叠合** | 相对 **现行六槽 live + 现行签名经济** 基线，两件极品时间线 ±0.5 周（或 DESIGN 重钉并经总控签）；输入含 Stage1 F / Stage2 C，**不含**抬表 | `out-*-armor-sig-w30.md` | G-W30 |

**开工令（额外硬门槛）：** S1–S4 PASS **之后**，仍须 **总控显式批「开工」** 才可另开施工 tip；本 tip / 本 DESIGN **不是**开工令。

---

## 4. 与 legendary effects 预算关系

| 约束 | 本 stage0 |
|------|-----------|
| 行为型、非伤乘 | **继承**；防具签名不新开伤害乘区 |
| 生效帽 ≤2（刃 1 + 防具 1） | **继承并落地**「防具 1 = 5 槽取 1」 |
| 同 tag 最强 / 不叠 | **继承**；刃优先 |
| 单条预算用 B/H 当量 + p1sim | **继承**；数字终表属 S3/S4，不在本窗发明新乘区 |
| 每图 chase / 首领主题 | **不动**既有 L01–L15；本窗不扩件数包 |

暂停中的传奇线 COORD 已被主线签名轨 supersede；本 DESIGN 是其「甲侧封顶」条款的 **现网落地规格**，不是重启传奇包 densify。

---

## 5. NON-goals / 硬禁

- 再开「上六槽」或改三开关 / ×0.97 / 共鸣 F 权重  
- 把 2c 当薄 densify；本窗或未过门禁时改 jar / 派插件 / 动 Economy-pom  
- 抬 `STAMP_RATE`、徽记周池、imprint 价、Affix、永久功率表、伤乘、skill_mult  
- glance twin、sx20、粒子 densify、ForgeChoice·wear·pity·awkDelta densify、深化 densify  
- 5 甲各算一条签名；新地图数值套装  
- 夹带 K3 / 档 S / 白板补建模 / Pack6 / L11b 调律重开 / 烬斩门闩（TIP-Y）  
- 本文件冒充数值终表或开工令

---

## 6. 分阶（提醒）

| 阶 | 内容 | 本窗 |
|----|------|------|
| **0 · 规格** | 本文 + TIP-AB；门禁表可核 | **本窗** |
| **1 · 离线门禁** | 跑 S3/S4（p1sim）；不过 → 停 | **另窗**（可 docs+tools，仍禁业务 jar） |
| **2 · 施工 tip** | 仅门禁 PASS + 总控显式开工后：Java/UI 最小切片 | **另 tip 单主题** |

---

## 7. 成功标准（本 stage0）

- [ ] 文首勘误 D174「2c」可核（甲槽另轨 live；真债=防具签名+叠合）  
- [ ] 防具签名规格 §2 可批（槽、封顶、tag、stampable 默认、优先级）  
- [ ] 门禁 S1–S4 过线条件可核；HOLD / 开工令分离写清  
- [ ] legendary 预算关系与 NON-goals / 硬禁对齐  
- [ ] **零**施工指引冒充开工令；**零** jar

---

## 8. 参考

- [`RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md`](RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md) §3–§5  
- [`DESIGN-ember-mainline-unlocks-2026-10-04.md`](DESIGN-ember-mainline-unlocks-2026-10-04.md) §2.1 / §5 / §8  
- [`DESIGN-ember-gear-structure-2026-10-06.md`](DESIGN-ember-gear-structure-2026-10-06.md) §1 / §3.2 / §9  
- [`STATUS-ember-stage2-green-exit-d453-2026-10-10.md`](../status/STATUS-ember-stage2-green-exit-d453-2026-10-10.md)  
- `/workspace/COORD-legendary-effects.txt` · `/workspace/COORD-mainline-unlocks.txt`  
- 现网只读：`EmberSignature`（slot=blade\|charm；stampable；active ≤2）
