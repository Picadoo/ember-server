# tip · TIP-AM SinkGlance 护甲诚实对齐（可施工薄 · 可感 UX）· D12696–D12711

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 20:29 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-sinkglance-armor-honesty-2026-10-12.md`](../design/RESEARCH-ember-sinkglance-armor-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-sinkglance-armor-honesty-2026-10-12.md`](../design/DESIGN-ember-sinkglance-armor-honesty-2026-10-12.md) · TIP-AL CLOSED live `1.65.777-d12695` · TIP-AK CLOSED live `1.65.776-d12679` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify）** — SinkGlance 甲闸诚实（**勿 densify**）；接 AL CommitNext；正交 L13 HOLD  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberSinkGlance.tellHeld` 甲早退 + forge 开入口预览三钮轻触 + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零 SinkGlance 功率 densify**

---

## 0. 一句话

手持护甲开工会话时「消耗一瞥」与 K0 / HeldNext / CommitNext 同句拒绝；不再假推成色精工费用。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SinkGlance 护甲诚实对齐** | **采纳** | AL 显式另债；开工会话可感说谎；SoT 一处；零功率表；**非 densify** |
| forge 开入口预览三钮甲闸 | **纳入轻触** | 与 tellHeld 同瞬 |
| GearNextHint 全局甲闸 | **否本窗主轴** | 面广；消费者闸更薄 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 甲待命半行 / 签名四甲格 | **否** | AJ/AK 另债；易 densify |
| Hope/SetChase/gem glance 再 twin | **否** | TIP-J/T 停窗 |
| ForgeChoice densify / SinkGlance 功率 densify | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12696 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12697 | DESIGN / tip power-paths 进度戳：**TIP-AL CLOSED live**；**TIP-AM OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12698 | 错位钉死：E4–E7 SinkGlance/开入口假推 vs E1–E3 K0/HeldNext/CommitNext 诚实（RESEARCH §1） | 可核 |
| D12699 | 「对齐」钉死：`tellHeld` 甲早退 ≡ `ARMOR_REFUSE`（±分解半行）；禁成色/精工/升阶费用行；零新 glance（DESIGN §2） | 可核 |
| D12700 | **插件：** `EmberSinkGlance.tellHeld` — `d.isArmor()` 早退诚实句 + ActionBar | 代码+单测 |
| D12701 | **插件：** 甲路径禁 `qualityLine`/`craftLine`/`upgradeLine` 与成色/精工 `lackHalf` | 可核 |
| D12702 | **插件轻触：** `EmberCommand` forge 开入口 — 持甲勿发 enhance/quality/refine 预览三钮；改诚实短包 | 可核 |
| D12703 | **插件可选：** `BlankLoop.followHint` 去假成色串 | 可核 |
| D12704 | **插件：** 单测最小包（DESIGN §2.3）+ 刃一瞥回归 | PASS |
| D12705 | `GearNextHint` **本窗不改**；TrMenu **禁**加 lore | lore≤3 |
| D12706 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无 ForgeChoice densify；无新 hope/glance PAPI | PASS |
| D12707 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲待命/四甲格；glance twin/深化 densify；ForgeChoice densify；SinkGlance 功率 densify | 文内硬句 |
| D12708 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12709 | 玩家口径：甲开 forge → 一瞥/预览诚实；刃护符路径与价/掉落/数字不变 | 可核 |
| D12710–D12711 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或 densify SinkGlance | 超额新 tip |

### 玩家口径（固定）

- 手里拿着护甲再开工坊：「消耗一瞥」会说 **护甲随护符成长**（或分解零头），不会列出成色/精工本次费用。  
- 同瞬不会再塞「成色预览/精工预览」去撞服务器拒绝。  
- 刃/护符怎么养本窗不动；价钱、掉落、数字不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-sinkglance-armor-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-sinkglance-armor-honesty-2026-10-12.md` |
| SoT | `CoreRpg/.../EmberSinkGlance.java` `tellHeld` |
| 轻触 | `CoreRpg/.../EmberCommand.java` forge 开入口预览三钮 |
| 可选 | `EmberBlankLoop.followHint` |
| 已诚实对照 | TIP-AK `EmberForgeHeldNext` · TIP-AL `EmberForgeCommitNext` · SixPapi `heldArmorLine` · K0 `ARMOR_REFUSE` |
| 单测 | 新 `EmberSinkGlanceTest`（或纯助手最小包） |
| 旁轨 HOLD | TIP-AI L13 FAIL · TIP-AD exclude L11/L13 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / glance twin / sx20 / 深化 densify / ForgeChoice densify / **SinkGlance 功率 densify**  
- 开 L13 施工 tip（**HOLD** 至显式开工）  
- 签名页甲待命半行 / 四甲格；改 `ARMOR_REFUSE` 为可锻  
- 全局改 `GearNextHint` 成色/精工串；新 hope/glance PAPI 族  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12700–D12704；D12706 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后策划 docs close  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AL 后 · **可感 UX** — SinkGlance 护甲诚实对齐（**非 densify**）  
- **批注：** AL CommitNext / AK HeldNext 甲闸已齐，开工会话 `tellHeld` 仍对甲拼成色/精工/升阶费用；`tellHeld` 甲早退 ≡ `ARMOR_REFUSE` + forge 开入口预览三钮轻触；零功率表；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / ForgeChoice densify / SinkGlance 功率 densify 仍冻  

## STATUS: OPEN · 荐 A·M · 2026-10-12
