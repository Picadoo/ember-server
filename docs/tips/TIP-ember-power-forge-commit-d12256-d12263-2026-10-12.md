# tip · TIP-B 工坊选择感 · 非加伤（稳/赌/烙/洗可区分）· D12256–D12263

> **状态：** CLOSED · live 1.65.760-d12263（2026-10-12 11:49 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) §2B · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) Batch3 WS1–WS3 · forge-choice Stage0（K1/K2 已 SHIP）  
> **前置：** TIP-A CLOSED（d12247）· TIP-C CLOSED（1.65.759-d12255）  
> **施工岗：** 插件岗（TrMenu 文案 + 纯展示/commit 接线；**零改价**）  
> **硬禁：** **零伤乘 · 零 Affix Pack · 零抬 forgeroll 极品权 · 零 K3 · 零抬 enhance/quality/refine 表** · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

玩家在工坊要分得清：**稳线养件 / 旁路赌锻 / 烙纹选手感 / 洗练换词条**——确认前能说出「我选了什么」，但 **dps 公式不动**。

---

## 1. 缺口（对照现网）

| 路径 | 已有 | 缺口 |
|------|------|------|
| 稳线强化/成色/精工 | 预览+确认格；Open 有「稳线/旁路」tell | 预览格仍偏「进度+费用」同质；成功后 commit 钮偶发漏（WS1） |
| 随机锻 + floor | `ember_p1_forgeroll` 上排普通 / 下排下限；K2 已 SHIP | 工坊主页 F 格仍只写「旁路赌线」，**未并排点出「有下限可买」**（WS3） |
| 烙纹定向 | 类型按钮 → 聊天确认 | 确认前缺半行 **「将得到：{类型}手感 · 不加伤」** |
| 每周转化 | convert 页 | 成功后缺 **「本周还可转 n · 下缺口」** 一句 |
| 词条洗练 | reroll 说明已压 ≤3 | 保持；确认/预览聊天已有则只查漏，**池子不回 lore** |

---

## 2. 方案 M（定稿）

### 2.1 四条「选了什么」半行（展示 only）

| ID | 项 |
|----|----|
| D12256 | 本 tip |
| D12257 | **烙纹**：`corerpg p1 brand` / pin 预览与确认 tell 前插一行：`§e将得到：§f{词条名} §7手感 · §8不加伤`（类型名用现网 brand 表；**不改**消耗与档位 RNG） |
| D12258 | **转化**：convert 成功 AB/聊天末行：`§7本周还可转 §f{left} §7次 · 下缺口：{gap或「齐」}`（复用 convert 周余 + SinkGlance/gap；零改周帽） |
| D12259 | **洗练**：预览聊天已有则补半行 `§7选的是：§f随机词条` / `§7选的是：§f锁定·只重抽档`；菜单说明格保持 ≤3，**禁止**池 dump 回 lore |
| D12260 | **随机锻**：工坊主页 F 格 lore 三行改骨架：`§8旁路赌线` / `§e有下限：§f下页第二排加料买下限（极品仍0）` / `§e➥ §f打开`；forgeroll Open 保留稳/赌一句即可 |

### 2.2 稳线预览三态 + commit 漏口（WS2 / WS1）

| ID | 项 |
|----|----|
| D12261 | 工坊强化/成色/精工 **预览与确认** lore 统一 ≤3：`花费…` / `n/N 或下次必成或确定升` / `§e➥ 点确认`（可继续用已有 held_* / pity PAPI；**零改 cost**） |
| D12262 | 审计 `EmberForgeCommitNext`：enhance / quality / refine / brand / convert / forgeroll **成功路径**是否都 `send3`；漏则补调用，**禁**自动跳菜单（K3）· **禁**新 ConfirmTokens 种类海 |
| D12263 | 验收文案 + 单测：commit 三钮命令仍指向现有 `/corerpg p1 …`；TrMenu `ember_p1_forge` / `forgeroll` / `brand` / `convert` / `reroll` lore overs>3 = 0 |

### 2.3 选择感验收（玩法，非数值）

同一件持件：

1. 走 **烙纹·猎缀** → 聊天能读到「猎缀手感 · 不加伤」  
2. 走 **洗练锁定** → 聊天能读到「锁定·只重抽档」  
3. 打开随机锻 → 能区分 **普通赌** vs **下限排**  
4. p1sim / 伤乘单测 **不动**；within range 即可  

---

## 3. 明确不做

- 伤乘、skill_mult、gem flat、抬 forgeroll 极品权、抬强化率  
- Affix Pack / 难度 densify / K3 自动跳页  
- 新经济叶子、改 C22/C06 单价（除非另批；本 tip **禁止**）  
- 希望表盘回潮长墙；把洗练词条池塞回菜单 lore  

---

## 4. 验收清单

- [ ] F/G/V/W 四入口悬停即可说出路径差异（稳旁路 / 烙手感 / 转化周次 / 洗练锁否）  
- [ ] 烙纹确认含「不加伤」；转化成功含周余  
- [ ] 强化成功后必现下一稳线三钮（或诚实「已满」）  
- [ ] 相关菜单 lore 非空 ≤3；密码检查 0；无 Economy/pom/Affix 进 commit  
- [ ] reload 记「待恢复服后实测」  

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 11:44 CST  
- tip 已推 · 交插件岗  
- 薄窗 A→C→**B** 本批收束；收尾后不再开同构 forge twin

## STATUS: CLOSED · live 1.65.760-d12263 · 2026-10-12 11:49 CST
