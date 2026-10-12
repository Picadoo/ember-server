# DESIGN · 签名页防具侧生效摘要对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AJ 批 A·M · 已派插件+物品 · 2026-10-12 20:00 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-sig-page-defense-summary-align-2026-10-12.md`](RESEARCH-ember-sig-page-defense-summary-align-2026-10-12.md)  
> **对接 live：** TIP-AE defense-side · TIP-AH runPreview 对齐 · `ember_p1_sig.yml` C/W  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI `STAGE1_L13_OFFLINE=FAIL` · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance/深化 densify/sx20/ForgeChoice densify

---

## 0. 一句话

让签名传奇页 **防具侧摘要格**（原「已选护符」）与战斗 / AH 预览同一胜出槽；开关签名也作用于该槽；零功率表、不扩菜单格数。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 战斗 / run 预览 | 刃 + 防具侧 5 取 1（AE+AH） | 保持 |
| 签名页 C | 死写护符 · `wornLine(…,"charm")` | 展示 **胜出防具侧** `wornLine` |
| toggle C | 仅 `sigOff(charm)` | 开关 **胜出槽** `sigOff` |
| 图鉴 a_ / imprint_hope | 护符字面 / charm 阶 | 「护符或防具」· `imprintWornTarget` 阶 |
| 菜单格数 | 刃 + 护符 两格 | **仍两格**（刃 + 防具侧）；禁加四甲格 |

---

## 2. 方案 M（定稿荐）

### 2.1 胜出槽解析（复用，禁重写核）

新增私有辅助（名示意）：`defenseActiveSlot(d, lo)`：

1. 按序 `"charm","head","chest","legs","boots"`；  
2. 对每个 slot：若 `previewSlot(d,lo,slot) != null` **或**等价地 `worn` 有 def 且 `sigWhy==null` → 返回该 slot；  
3. 若无一生效：返回 `null`（摘要走「没有」口径）。  

**禁止**绕过 `sigWhy` / `pickDefense` 手写「有签就显示」。与 AH `previewActiveSlots` 防具支路同序。

### 2.2 PAPI（扩语义 · 少加键）

| 键 | 行为 |
|----|------|
| `charm` | `wornLine(d, lo, slot)` where `slot = defenseActiveSlot`；若 null → `§7防具侧：§8没有`（或沿用 slotName 风格） |
| `charm_o` | `wornOtherCost(d, lo, slot)` 同上；null → `""` |
| `charm_t` | toggle 提示针对 **同一 slot**（无签 / 已关 / 可关） |
| `blade` / `blade_o` / `blade_t` | **不变** |

**默认不**新增 `charm_n`；菜单静态名改「防具侧生效」，槽真名已在 lore `wornLine` 前缀。

### 2.3 toggle

`sigToggle(p,d,lo,arg)`：

- `blade`：不变。  
- `charm`（菜单仍发此子命令）：解析 `slot = defenseActiveSlot`；若 null → 诚实提示「防具侧没有生效签名可开关」并刷新菜单；否则对该 `slot` 翻转 `C_SIGOFF+slot`（甲槽首次也可写 period）。  
- **仍禁止**菜单发明 `toggle head` 四键（防 densify）；一键对准胜出件即可。

### 2.4 文案跟齐（薄）

| 点 | 改 |
|----|-----|
| `ember_p1_sig.yml` C `name` | `§6防具侧生效`（静态）；lore 仍两 PAPI + 不加成行 |
| 图鉴 `a_`（非刃） | `烙印到自己的护符或防具上`（对齐 confirm 串） |
| `imprint_hope` | 非刃：`imprintWornTarget(lo, cd)` 取阶；无目标则 tier=1 |
| `/corerpg p1 sig` 列表摘要 | `wornLine(blade)` + 防具侧胜出 `wornLine`（同 2.1） |

### 2.5 TrMenu / 物品

| 文件 | 动作 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_sig.yml` | C 名改「防具侧生效」；**禁**加 lore 行；W 不变 |
| 其它菜单 | **本窗不改**（runconfirm 已 AH；imprint 页靠 PAPI） |

### 2.6 单测（最小包）

| 用例 | 期望 |
|------|------|
| 仅 charm 生效 | `charm` PAPI 含护符·生效；同改前语义 |
| charm 无、head 胜出 | `charm` PAPI 含 `头盔` + 生效；**不含**「护符：没有」假空（应报头盔） |
| charm+head 皆有签 | 仅报护符（prio） |
| 无防具侧签 | `§8没有` 类诚实空 |
| toggle charm 当 head 胜出 | `sigOff(head)` 翻转；再开摘要显示不生效原因含关掉 |
| imprint_hope 追烙甲签 | 阶随甲件，非强制 charm |
| `git show` | 无 STAMP/C12/loot/L11/L13/Affix/skill_mult |

---

## 3. 验收

- [ ] C 格摘要槽 ≡ 战斗防具侧胜出槽（真 `slotName`）  
- [ ] 点 C 可开关该胜出槽签名（含甲）  
- [ ] 图鉴防具侧 action 不写死「仅护符」  
- [ ] 菜单仍 2 摘要格；lore≤3；无新功率表  
- [ ] L13 施工仍 HOLD；TIP-AD exclude 保留  

---

## 4. 明确不做

- 抬表 / 扩 stampable / 改 ALTS / 开 L13 施工 tip  
- 签名页加头胸腿靴四格；冒险 lore densify  
- 新 hope/glance PAPI 族；ForgeChoice densify  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberGrowthService.java`：`sigPapi` charm* · `sigToggle` · 列表摘要 · `imprint_hope` · 图鉴 `a_` |
| 复用 | `previewSlot` / `sigWhy` / `defenseWorn` / `imprintWornTarget` / AH `previewActiveSlots` 序 |
| 菜单 | `plugins/TrMenu/menus/ember_p1_sig.yml` Icon C |
| PAPI 键 | 沿用 `charm` / `charm_o` / `charm_t`（语义扩） |
| 单测 | Growth / Signature 现有 fixture + 最小新测 |
