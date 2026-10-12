# tip · TIP-D 套装追一眼（枢纽一格 · 焦点 have/need · 还差哪件）· D12264–D12271

> **状态：** CLOSED · live 1.65.761-d12271（2026-10-12 15:51 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md)（A→C→B 已 CLOSED live；本窗续变强途径）  
> **前置：** TIP-A CLOSED（d12247）· TIP-C CLOSED（d12255）· TIP-B CLOSED（1.65.760-d12263）  
> **施工岗：** 插件岗（TrMenu 文案 + 纯展示短串；**零改套装战力**）  
> **硬禁：** **零伤乘 · 零 Affix Pack · 零 clear-gate · 零抬 RATE/pity** · 零同构 forge twin / pity densify twin · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

枢纽（或工坊）多一格 **「套装」**：一眼报清 **当前焦点套（焚烬 / 烬爆 / 炽愈）have/need + 还差哪件**；点一下进成套追（图装指针或工坊强化）。不叠新计数器，不改套装公式。

---

## 1. 缺口（对照现网）

| 已有 | 问题 |
|------|------|
| `%corerpg_p1_set_focus%` → `EmberSetFocus.glance`（D465） | 聊天/半行可读；**枢纽无专格**，装备页 `o` 只写「套装：觉醒名」 |
| `corerpg p1 setchase` → `EmberSetChase.tell`（进度 + 觉醒缺 + focusGap + 图鉴还差 + [换上]/[进图]/[工坊]） | 工坊 Open 已刷，但 **菜单悬停看不见**；玩家要先点开才知「还差哪件」 |
| `%corerpg_p1_set_progress%` / `awaken_next` / `set_run` | 装备页拆成多格且 S 格仍有族说明长句；不符合「一格三行」 |
| `focusGap` / `focusCodexRemain` / `awakeningRemain` | 短串已在 Java；**缺菜单用一行合成**（对位 TIP-C `menuLine`） |

---

## 2. 方案 M（定稿）

### 2.1 菜单短串（展示 only · 复用 have/need）

| ID | 项 |
|----|----|
| D12264 | 本 tip |
| D12265 | `EmberSetChase.menuLine(PlayerData, EmberLoadout)`：**一行**菜单短句，目标形态见下；内部只拼已有 `EmberSetFocus.label` / `setProgress` 数字 / `focusGap` 缺口词 / `awakeningRemain` / `focusCodexRemain` 件数——**禁止**新 period 计数、新 pity、新 set 战力字段 |
| D12266 | PAPI：**优先**让 `%corerpg_p1_set_focus%`（及 `set_focus_line`）在菜单语境走 `menuLine`；若改显示会弄乱聊天/AB，则 **仅加** `%corerpg_p1_setchase_line%` 一处挂 `menuLine`（对位 TIP-C「优先改显示、不新造第二套计数」）。禁止再拆 `setchase_have` / `setchase_need` 丛林 |

**`menuLine` 目标形态（固定口径）：**

| 状态 | 例 |
|------|-----|
| 未选焦点 | `§8套装焦点：未选 · 点右选焚烬/烬爆/炽愈` |
| 焦点未成套 | `§e焦点§f焚烬 §7· 同族 1/2 §8· §e还差同族护符`（have/need 来自刃+符同族计数；「还差」优先 `focusGap` 词） |
| 焦点已成套、觉醒未满 | `§e焦点§f烬爆 §a穿着 §7· 觉醒II §8· §e还差 刃 +6→+9`（`awakeningRemain` 压缩） |
| 焦点齐且觉醒满 | `§a焦点§f炽愈 §a觉醒III · 齐`；可选半截 `§8图鉴齐` 或 `还差N种`（`focusCodexRemain` 件数，**不**占第 4 lore 行） |

签名 / 徽记：**不**进本格主行；若施工时发现必须带手感，**最多**半行 `§8烙：{手感名}`（类型名 only · **不加伤** · 不作进度分子）。

### 2.2 TrMenu（优先枢纽；工坊可选指针）

| ID | 项 |
|----|----|
| D12267 | `ember_hub.yml` Layout 第一行空位插入格 `v`：现网 `'LnHowp FS'` → `'LnHowpvFS'`（`p` 与 `F` 之间；**不挤** H/o/w/p/F/S） |
| D12268 | Icon `v`：name `§d套装`；material 建议 `iron chestplate` 或 `nether star`（与装备页区分即可）；`update: 20`；lore **恰好 3 非空行**：`§f%corerpg_p1_setchase_line%`（或改后的 `set_focus`） / `§8左：成套追（图/工坊指针）· 右：选焦点` / `§e➥ §f一眼还差哪件 · 不改数值` |
| D12269 | actions：left → sound + `command: corerpg p1 setchase`（复用现网 tell + [换上]/[进·Qx]/[工坊] 按钮，**禁**新命令树）；right → `command: corerpg p1 setfocus`（现网选焚烬/烬爆/炽愈）；**零**伤乘/掉率 |
| D12270 | 装备页 `o` 或工坊底栏：若现 lore 仍有空档，可加半行 `§8套装追 → 枢纽「套装」`；**超 lore≤3 则本批不做**装备/工坊改动。工坊 Open 已 `setchase` 的 tell **不删**（与枢纽格互补） |
| D12271 | 验收：短串空焦点/缺口/齐/满觉醒；hub `v` 非空 lore=3；全文件 overs>3=0；`setchase` 按钮仍指向现有 enter/forge/equip；**套装公式 / p1sim 伤乘单测不动** |

### 2.3 点击去向（玩家可见）

1. **未成套 / 缺件在包** → setchase 出 `[换上]`（现网 D741）  
2. **图鉴还差** → `[进·Qx]`（`suggestMapForFamily`，不锁通关）  
3. **已成套觉醒未满** → `[工坊]`（强化下一档）  
4. 枢纽格本身 **不**直接改掉落目标族；焦点与掉落目标仍可不同（`EmberSetFocus` 既有语义）

---

## 3. 明确不做

- Affix Pack / 同构 forge twin / pity densify twin / soft densify  
- 伤乘、skill_mult、套装数值表、觉醒阈值改档  
- clear-gate、抬 RATE、新经济叶子、改 Economy/pom  
- 新 hope/setchase 计数器丛林；把签名战力或徽记加成写进本格  
- 装备页 S 格族说明长墙回潮；把 setchase 聊天四行整墙贴进 lore  

---

## 4. 验收清单

- [ ] 枢纽悬停「套装」：能读出 **焦点族名 + have/need 或还差哪件**（三态至少测：未选 / 缺口 / 齐）  
- [ ] lore 非空行 = 3；hub overs>3 = 0  
- [ ] 左键：聊天走现网 `setchase`（含图/工坊指针按钮）；右键：`setfocus` 选族  
- [ ] `git show --stat` 无 Affix / Economy / pom 无关脏文件；密码检查 0  
- [ ] 伤乘 / 套装觉醒数学单测 **不动**  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 15:46 CST
- tip 已推 · 交插件岗
- 薄窗变强途径续：**TIP-D**；A→C→B 已收；本批后仍禁 Affix / forge twin / pity densify twin

## STATUS: CLOSED · live 1.65.761-d12271 · 2026-10-12 15:51 CST
