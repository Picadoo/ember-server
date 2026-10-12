# tip · TIP-I 希望表盘补漏 + 图装下一缺（HD1/MG1 剩余表面化）· D12304–D12311

> **状态：** 待批 A（策划 tip · 2026-10-12 16:10 Asia/Shanghai）· 方案 **M**  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) **§3.1 HD1** · **§3.3 MG1** · TIP-A/C 已 CLOSED（薄窗单轨 / Q 格 mapid）  
> **前置：** TIP-A～H CLOSED live（至 TIP-H `82512b4d`）  
> **施工岗：** 插件岗（展示短串 + TrMenu；**零改 pity/掉率**）  
> **硬禁：** **零 Affix Pack · 零伤乘 · 零抬 RATE / skill_mult / gem flat** · 零 clear-gate · 不发明新主循环 / 新 hope 计数器 · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

希望格悬停就能看见 **当前轨 n/N**（不必先点左）；冒险 Q 第二行在「这图掉」上补 **下一缺件**。只补 DESIGN §3 漏口，不叠 twin。

---

## 1. 漏口对照（§3）

| 轴 | DESIGN 目标 | 现网 | 本 tip |
|----|-------------|------|--------|
| **HD1** | 单面板：手持 1 + 焦点图 1 + 宝石 1 | TIP-A 单轨切换；lore **无进度**；宝石已由 TIP-E 邻格 `g` 覆盖 | **补：** 希望格 L1 挂当前轨进度串；L2 挂焦点图身份短串（宝石不重复占行） |
| **MG1** | Q 行身份 `族·缺件·下一稳线` | TIP-C `menuLine` = `族 · 图鉴 have/need · 还差N`，**缺「下一缺/下一稳」** | **补：** `menuLine` 末尾拼 `nextMissing` 压缩（仍一行） |
| HD2/HD3/MG2/WS* | 另轴 | 已有或非本窗优先 | **本 tip 不做**（禁 densify） |

---

## 2. 方案 M（定稿）

### 2.1 HD1 补漏 — 希望格可见当前轨

| ID | 项 |
|----|----|
| D12304 | 本 tip |
| D12305 | `EmberHopeMeter.menuNow(...)`（或等价）：返回 **当前优先轨** 一行，形态 `§e{轨名} §f{progressLine}`（复用 `current`/`priorityTracks` 已有字段；**禁止**新 period / 新 pity 数学） |
| D12306 | PAPI：**仅加一处** `%corerpg_p1_hope_now%` 挂 `menuNow`（对位「优先改显示」；**不是**第二套 HopeMeter）。TIP-A「lore 不挂新 PAPI」本批 **显式修订为允许这一处进度镜**，仍禁 celebration / 多 key 丛林 |
| D12307 | 焦点图半行：`EmberMapIdentity.menuLine(suggestMapForFamily(setfocus), pd)`；PAPI 优先复用/改短 `%corerpg_p1_mapid_focus%` **一处**（无焦点则 `§8未选套装焦点 · 右键套装格`）。禁再拆 mapid_focus_have/need |
| D12308 | `ember_hub.yml` Icon `w` lore **恰好 3 非空行**：`§f%corerpg_p1_hope_now%` / `§6图：§f%corerpg_p1_mapid_focus%` / `§e➥ §f左：汇报并开工坊 · 右：下一条`；actions 保持 hope / hope cycle（宝石轨左键进宝石页的 TIP-E 行为保持） |

### 2.2 MG1 补漏 — menuLine 带下一缺

| ID | 项 |
|----|----|
| D12309 | `EmberMapIdentity.menuLine`：**仍一行**，在齐/缺后追加短尾——缺件时 `§8· §e{nextMissing 去色压缩}`（例 `焚烬 · 图鉴 1/2 · 还差1 · 下一缺T1刃`）；已齐不加「下一缺」。**优先改显示串**；冒险七格 L2 仍只挂 `%corerpg_p1_mapid_q0N%`，无需改 TrMenu 结构 |
| D12310 | 「下一稳线」口径：本批用 **下一缺件**（`nextMissing`）代表图装追方向；工坊稳线 n/N 仍走希望/工坊，**不**把 held pity 塞进 Q 格（防 >3 / 串太长） |
| D12311 | 验收：hub `w` 悬停见轨进度+焦点图；切轨后 L1 变；Q01–Q03 L2 含下一缺或齐；overs≤3；pity/掉率/伤乘单测不动 |

### 2.3 明确不做

- Affix Pack、伤乘、抬 RATE / skill_mult / gem flat  
- 新 hope 计数器、celebration、HD2 AB densify、HD3 装备 tip 大改  
- clear-gate、改 lootFamily、MG3 soft gift  
- 希望格回潮三轨并行墙（仍单轨 + 邻格宝石/签名）  

---

## 3. 验收清单

- [ ] 枢纽希望：不点左也能读出 **当前轨 n/N 或回落句**  
- [ ] L2 焦点图：有焦点时见族/图鉴短串  
- [ ] 冒险 Q 第二行：缺件时含 **下一缺**；齐则无冗尾  
- [ ] hub + adventure lore overs>3 = 0  
- [ ] `git show` 无 Affix / Economy / pom 无关脏文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 4. 批注

- [ ] 总控批 **A · 方案 M**
- tip 待推 · 交插件岗
- 变强途径续：**TIP-I · HD1/MG1 补漏**；§3 其余轴不借机 densify
