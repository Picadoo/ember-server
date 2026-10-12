# tip · TIP-E 宝石通关保底一眼（枢纽邻希望 · n/N·下次必成 · 点进宝石页）· D12272–D12279

> **状态：** CLOSED · live 1.65.762-d12279（2026-10-12 15:55 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) HD1/HD2 宝石轨表面化  
> **前置：** TIP-A～D CLOSED live（至 1.65.761-d12271）  
> **施工岗：** 插件岗（TrMenu + 纯展示；**零改 gem flat / pity 阈值**）  
> **硬禁：** **零抬 gem flat · 零伤乘 · 零 Affix Pack** · 零抬 `PITY_NEED` / 通关计数公式 · 零 clear-gate · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

进枢纽就能看见 **宝石通关保底 n/N · 下次必成**；点一下进宝石页。不抬石头战力，不叠新 pity。

---

## 1. 缺口（对照现网）

| 已有 | 问题 |
|------|------|
| `%corerpg_p1_gem_pity_hope%` → `EmberGemAcquire.hopeLine` / `EmberHopeMeter.gemPity`（D608） | **宝石页**已挂；枢纽希望格 `w` lore **不挂进度**（TIP-A 禁 hope PAPI twin），玩家要右键切到「宝石通关保底」轨才在聊天看见 |
| `EmberGemAcquire.remainLine`（`通关保底 2/5 · 再通关3次得石`） | 聊天/镶嵌提示有；**枢纽第一眼无** |
| 希望轨含 `gem`，左键 `corerpg p1 hope` → 偏工坊 | 宝石轨也应 **一键进 `ember_p1_gem`**，勿只丢工坊 |
| 工坊底栏 `m` → 宝石页 | 要先开工坊；与「希望邻格一眼」目标不符 |

---

## 2. 方案 M（定稿）

### 2.1 优先：枢纽希望邻格（复用已有 PAPI）

| ID | 项 |
|----|----|
| D12272 | 本 tip |
| D12273 | **不新造** pity 计数 / 不新造 hope PAPI 族；菜单直接挂已有 `%corerpg_p1_gem_pity_hope%`。若希望串过短（仅 `保底 a/b`），可选让该 PAPI **改显示**走 `remainLine` 短形态 `通关保底 a/b · 再通关n次得石` / 满时 `通关保底已满 · 下次通关得石`——**优先改显示串、不新增第二套计数**（对位 TIP-C/D） |
| D12274 | `ember_hub.yml` Layout 第二行空位插入格 `g`：现网 `'TBmX k i '` → `'TBmXg k i'`（`X` 与 `k` 之间；**不挤** T/B/m/X/希望行 `w`/套装 `v`） |
| D12275 | Icon `g`：name `§5宝石`；material 建议 `emerald` / `prismarine shard`；`update: 20`；lore **恰好 3 非空行**：`§f%corerpg_p1_gem_pity_hope%` / `§7Q01–Q07 通关累计 · 满则下次通关得石` / `§e➥ §f打开宝石页` |
| D12276 | actions：all（或 left）→ sound + `menu: ember_p1_gem`（或等价 `command: corerpg p1 gem` 若现网有开页命令——**优先菜单热更**）；**禁止**改 `PITY_NEED`、掉落、flat |

### 2.2 希望轨内半行（薄补 · 与邻格互补）

| ID | 项 |
|----|----|
| D12277 | `corerpg p1 hope` / `hope peek`：当当前轨 id=`gem` 时，第三行指针改 `§e➥ §f打开宝石页`，并 **打开 `ember_p1_gem`**（勿再默认开工坊）；其它轨行为不变 |
| D12278 | 可选（超 thin 可砍）：Hub **Open** 在已有 hope peek 之外 **不**再堆宝石 tell（邻格已可见）。若施工发现无邻格方案被否，退化为 Open **+1** 行：`§5宝石：§f{remainLine或hopeLine}`，合计希望类 Open tell 仍 ≤2 |

### 2.3 验收 ID

| ID | 项 |
|----|----|
| D12279 | 验收：hub `g` 非空 lore=3；overs>3=0；悬停能读 n/N 或「下次通关得石」；点击进宝石页；希望轨切到 gem 后左键进宝石页；`PITY_NEED`/gem flat/伤乘单测 **不动**；无 Affix / Economy / pom 进 commit |

### 2.4 玩家可见口径（固定）

- 「宝石保底」= Q01–Q07 主线通关累计（现网 `countsForPity`；不含团本/深渊/短征）→ 满 `PITY_NEED`（现 5）下次通关发石。  
- 本 tip **只表面化**；合成价、镶嵌、flat **一字不动**。

---

## 3. 明确不做

- 抬 gem flat / skill_mult / 伤乘；改 `PITY_NEED` 或通关加码公式 densify  
- Affix Pack / forge twin / pity densify twin / soft densify  
- 新 `%corerpg_p1_gem_pity_*%` 计数丛林；legacy socket 回潮  
- 希望格 `w` 回潮三轨并行墙；把宝石页合成四石说明贴进枢纽 lore  

---

## 4. 验收清单

- [ ] 枢纽悬停「宝石」：能读出 **n/N 或下次通关得石**  
- [ ] 点击进入 `ember_p1_gem`；页内原有保底格仍可读  
- [ ] 希望轨 = 宝石时，左键进宝石页（非误开工坊）  
- [ ] hub lore overs>3 = 0；`g` 非空 = 3  
- [ ] `git show` 无 Affix / Economy / gem flat 数值文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 15:52 CST
- tip 已推 · 交插件岗
- 变强途径续：**TIP-E**；仍禁 Affix / 抬 flat / 伤乘

## STATUS: CLOSED · live 1.65.762-d12279 · 2026-10-12 15:55 CST
