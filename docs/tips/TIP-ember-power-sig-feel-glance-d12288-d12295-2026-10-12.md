# tip · TIP-G 签名手感一眼（枢纽/装备 · 手感名 · 点进签名/追烙）· D12288–D12295

> **状态：** CLOSED · live 1.65.764-d12295（2026-10-12 16:05 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) · D174 / D455 / D464 / D604  
> **前置：** TIP-A～F CLOSED live（至 1.65.763-d12287）  
> **施工岗：** 插件岗（TrMenu + 纯展示短串；**零改签名 mods / stamp / 烙印价**）  
> **硬禁：** **零伤乘 · 零 Affix Pack · 零抬 RATE（stamp）/ 徽记公式** · 不发明新主循环 / 新签名键 · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

枢纽（或装备侧）一眼看到 **当前签名手感名**（改打法、不加伤）；点一下进签名页，或去追烙指针。只表面化既有 L01–L15，不新造签名。

---

## 1. 既有资产（研究结论 · 勿另发明）

| 资产 | 用途 |
|------|------|
| `EmberSignature.Def.name` / `.good` / `.bad` | 手感名 + 得/代价文案（行为改打法；代价可含乘区，**本 tip 不改数值**） |
| `EmberSigFeel.runLine` / `procLine` | 本局签名名 / 触发短闪（D455） |
| `%corerpg_p1_sig_blade%` / `sig_charm` → `wornLine` | 装备页长串：件名+签名名+生效否 |
| `%corerpg_p1_sig_chase%` / `EmberSigChase.glance` / `remainLine` | 追烙 n/N · 目标 Lxx 名 |
| `%corerpg_p1_sig_acq%` | 获取半行 |
| 装备页 `L` → `corerpg p1 sig from gear`；`ember_p1_sig.yml` | 签名传奇整页已在 |

**缺口：** 枢纽无签名格；装备 `L` 挂 `sig_head`+`sig_blade` 偏图鉴统计，**第一眼不像「手感名」**；希望轨有追烙但需切轨。

---

## 2. 方案 M（定稿）

### 2.1 菜单短串（展示 only）

| ID | 项 |
|----|----|
| D12288 | 本 tip |
| D12289 | `EmberSigFeel.menuLine(activeDefs, chaseDef, chaseMarks)`（或 GrowthService 薄封装）：**一行**，目标形态见下；只拼 `Def.name`（可半截 `good` 四～八字压缩）+ 追烙 remain——**禁止**新 period、新 mods、新签名 code |
| D12290 | PAPI：**优先**新增一处 `%corerpg_p1_sig_feel%`（或 `sig_feel_line`）挂 `menuLine`；**禁止**再拆 blade_feel/charm_feel 丛林。若坚持零新 key，则改 `sig_blade` 在菜单语境过短不可行时，以 `sig_run`/`sig_chase` 二选一短串进 lore（仍 ≤1 新 key 上限） |

**`menuLine` 目标形态：**

| 状态 | 例 |
|------|-----|
| 有生效签名（≤2） | `§d签名 §f残门焚斧` 或双签 `§d签名 §f残门焚斧§7·§f门楼余烬` + 半截 `§8改打法·不加伤`（「不加伤」= 口径提示，非改公式） |
| 有签名但关闭/不生效 | `§c签名关闭 §7· §f{名} §8点页重开` |
| 无穿着、有追烙 | `§e追烙 §fL01 残门焚斧 §7· 徽记 n/N`（`remainLine`） |
| 皆无 | `§8无签名 · 点左开页 · 右选追烙` |

`good` 长句 **不**整段进枢纽；最多半行动词（可从既有 `EmberSigFeel` 触发 verb 表或 `good` 截断）。

### 2.2 枢纽邻格（优先）+ 装备侧压薄

| ID | 项 |
|----|----|
| D12291 | `ember_hub.yml` Layout 第二行空位插入格 `e`：现网 `'TBmXg k i'` → `'TBmXgek i'`（`g` 与 `k` 之间；**不挤** 希望/宝石/套装/技能） |
| D12292 | Icon `e`：name `§d签名`；material 建议 `enchanted book` / `nether star`（与套装 `v` 区分）；`update: 20`；lore **恰好 3 非空行**：`§f%corerpg_p1_sig_feel%` / `§8左：签名页 · 右：追烙目标` / `§e➥ §f手感名 · 改打法不加伤` |
| D12293 | actions：left → sound + `command: corerpg p1 sig`（或 `sig from hub` / `menu: ember_p1_sig` 与现网一致）；right → `command: corerpg p1 sig chase`（现网选 Lxx）；**零**抬 stamp RATE / 烙印价 |
| D12294 | 装备页 `L`：lore 改为手感优先三行——`§f%corerpg_p1_sig_feel%`（或 blade/charm 各半行名） / `§7追烙 §f%corerpg_p1_sig_chase%` 压缩 / `§e➥ §f签名页`；若 chase 串过长则 L2 改固定 `§8详细图鉴在签名页`；**overs≤3** |

### 2.3 验收

| ID | 项 |
|----|----|
| D12295 | 验收：枢纽悬停能读出 **手感名或追烙目标**；左进签名页、右 chase；装备 `L` 同步可读；`STAMP_RATE`/imprintMarks/伤乘/Affix 单测 **不动**；无 Economy/pom 脏进 commit |

### 2.4 玩家口径（固定）

- 「签名手感」= **名字**（+ 可选极短得效果词）；强调 **改打法 / 改形状或触发**，菜单写「不加伤」指不把本 tip 当伤乘入口。  
- 追烙 = 攒徽记烙既有 Lxx；**不**新造第 16 条签名。

---

## 3. 明确不做

- 抬 stamp RATE、改烙印徽记/币价、改 Def.mods 数值  
- Affix Pack / 新签名主循环 / 第四主动  
- 伤乘、skill_mult；希望 celebration densify  
- 把 15 条 `good` 全文贴进枢纽 lore  

---

## 4. 验收清单

- [x] 枢纽 `e`：三行内见手感名或追烙；overs>3=0  
- [x] 左 → 签名页；右 → `sig chase`  
- [x] 装备 `L` lore≤3 且偏手感名  
- [x] `git show` 无 Affix / Economy stamp 无关脏文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 16:01 CST
- tip 已推 · 交插件岗
- 变强途径续：**TIP-G**；复用 D174/D455/D464，不发明新主循环

## STATUS: CLOSED · live 1.65.764-d12295 · 2026-10-12 16:05 CST
- feat `5ad890e2` · chore 版本/STATUS 另附
- hub `e` + gear `L` + `%corerpg_p1_sig_feel%`；零 STAMP_RATE/imprintMarks/Affix
- 偏差接受：gear L2 固定「详细图鉴在签名页」
