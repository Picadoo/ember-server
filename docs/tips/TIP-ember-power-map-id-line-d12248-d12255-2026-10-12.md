# tip · TIP-C 图装「这图掉什么」（Q01–Q07 一眼身份）· D12248–D12255

> **状态：** 批 **A · 方案 M**（总控 2026-10-12 11:38 Asia/Shanghai）· 交插件岗  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) §2C · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) Batch2 MG1/MG2 · D610/D1816 已有资产  
> **前置：** TIP-A 希望一眼 **CLOSED**（1.65.758-d12247）  
> **施工岗：** 插件岗（TrMenu + 纯展示 Java）  
> **硬禁：** **零 clear-gate · 零掉率 hack · 零伤乘** · 无 Affix Pack · 无抬 AFK · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

打开冒险页，Q01–Q07 **每一格第二行**直接写清「这图掉什么 / 图鉴还差几件」；进本只留 **一行**身份提示。不改掉落，不锁通关。

---

## 1. 缺口（码已核对）

| 已有 | 问题 |
|------|------|
| `%corerpg_p1_mapid_q0N%` → `EmberHopeMeter.mapGearIdentity`（D610） | 冒险页 Q 格 **未挂**；现 lore 是状态 / 消耗 / 点击，**看不见掉什么** |
| `EmberMapIdentity.methodLine` / `remainLine` / `nextMissing`（EM1-A） | 进本聊天常跟 ActionBar 叠长句；菜单 Open tell（adventure L35）仍是教学墙 |
| `%corerpg_p1_mapid_board%` / 格 `n` | 总览在，但玩家点 Q 进本前仍要猜本图 |

---

## 2. 方案 M（定稿）

### 2.1 Q 格 lore 三行骨架（`ember_p1_adventure.yml` Icons `1`–`7`）

每格 **恰好 3 非空行**（可保留前导空行 0～1）：

| 行 | 内容 | 例（Q01） |
|----|------|-----------|
| L1 | 状态（可并入消耗半截，禁止再占第 4 行） | `§7%corerpg_p1_q01_state% §8· §e%corerpg_p1_q01_cost%` |
| L2 | **这图掉什么**（短句） | `§6这图掉：§f%corerpg_p1_mapid_q01%` ※见 D12249 短串 |
| L3 | 点击 | `§e➥ §f点击进本`（解锁条件已在 state；删「需 Q0x 首通」长尾若 L1 已含） |

- name 保持 `§aQ0x §f%corerpg_p1_q0x_name%`（可带短后缀，勿超长）。  
- **actions / command / condition 一律不动。**  
- 挑战版 / 深渊 / 团本 / 短征本批 **不扩**（短征若同构另开 tip）。

### 2.2 短串 API（展示 only · 复用 have/need）

| ID | 项 |
|----|----|
| D12248 | 本 tip |
| D12249 | `EmberMapIdentity.menuLine(MapDef, PlayerData)`（或 HopeMeter 薄封装）：**菜单用短句**，目标形态 `焚烬 · 图鉴 1/2 · 还差1` / `焚烬 · 图鉴齐`；**禁止**再拼「每局1装·差装可分解·不锁通关」长尾（长尾留给进本一行） |
| D12250 | `%corerpg_p1_mapid_q0N%` **改走** `menuLine`（或新增 `%corerpg_p1_mapdrop_q0N%` 仅一处——**优先改显示串、不新造第二套计数**；若改 mapid_ 显示，全服其它挂点须目测仍可读） |
| D12251 | TrMenu：七格 L2 挂短串；L1 合并 state+cost；L3 短点击；验收 overs≤3 |
| D12252 | adventure **Open** tell：删 L35 教学长句，改 **一行**：`§6这图掉什么：§f看各 Q 第二行 · 图鉴进度 · 不锁通关`；`mapid_board` 可保留一行或并入（合计 Open 希望类 tell ≤2 行） |
| D12253 | 进本：`EmberMapEnterFeel` 聊天 **只发一行** —— `remainLine` 或 `methodLine` 二选一短版（荐 `remainLine`）；ActionBar 可留 `下一缺` 半截；**禁止** methodLine + chase + 教学连发三行 |
| D12254 | MG2 文案：凡写「首通后才开身份」的误导 → 统一「进过/刷过即计图鉴 · **通关不锁掉落**」（只改 tell/lore，**零改** `requires` / unlock 码） |
| D12255 | 单测：`menuLine` 空图/无族/齐/缺件；TrMenu 七格非空 lore=3；**掉率/lootFamily/clear-gate 单测不动** |

### 2.3 玩家可见口径（固定）

- 「这图掉」= 本图 `lootFamily` 偏向装族 + 图鉴 have/need（刃+符至本图阶）。  
- 签名 / 徽记：**不**塞进 Q 格第二行（避免 >3）；进本一行或图录页已有则保持。  
- 差装→分解：只出现在分解成功聊天 / 工坊，**不**占 Q 格第三行以外。

---

## 3. 明确不做

- Affix Pack / 房间难度 densify / Boss 半血扩包  
- clear-gate、改 `lootFamily`、改掉率权重、新永久收集战力  
- 按图锁部位；新伤乘；K3；新 hope PAPI 丛林（本批只动 mapid **显示串** 或等价一处）  
- 改 Economy / pom / players 脏文件  

---

## 4. 验收

- [ ] 冒险页 Q01–Q03（至少）悬停：第二行能读出 **不同族或不同图鉴进度**  
- [ ] 七格 lore 非空行 = 3；全文件 overs>3 = 0（目标族格 `b` 现有 3 行可保留）  
- [ ] Open 无旧「偏向装族+签名+徽记…」长墙  
- [ ] 进本聊天身份 ≤1 行；通关条件码与改前一致  
- [ ] `git show` 无 Affix / Economy 无关文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 11:38 CST  
- tip 已推 · 交插件岗  
- 下窗：TIP-B 工坊选择感（薄窗序 A→**C**→B）
