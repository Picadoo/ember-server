# tip · TIP-AA 主线解锁 2c / 六槽结构研究薄窗（docs-only · 禁 densify）· D12544–D12551

> **状态：** CLOSED · 批 **A · 方案 M** · docs-only（总控 2026-10-12 17:39 Asia/Shanghai）  
> **上游：** [`RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md`](../design/RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md) · TIP-Z CLOSED docs-only `923e031f1` · TIP-L CLOSED live `1.65.767-d12351` · D174 2c 暂缓 · 六槽 Stage1+2 live + D453 绿出口 PASS  
> **前置：** TIP-Z 下阶段 P1 = 解锁研究稿；深化 densify **仍冻**；短征 O→X **闭合**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **无码**（策划 RESEARCH + tip；**不**派插件；勿借机改 Signature / Loadout / loot / STAMP / TrMenu）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **零把 2c 当薄 densify 施工** · 不改 Economy/pom 脏树

---

## 0. 一句话

TIP-Z 后开 **P1 解锁研究**：盘清 D174「2c 六槽」——甲槽已另轨上线，真债是 **防具签名 + 叠合门禁**；本窗 **docs-only**，**HOLD 施工至独立 DESIGN**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 主线 loot / FarmCue / STAMP 再薄 | **否** | TIP-L CLOSED；抬表 / invent |
| Q / sx 视觉 densify | **否** | 线闭合；硬禁 sx20 |
| 烬斩再开码 | **否** | TIP-Y 门闩未满足 |
| 六槽 honesty / 菜单 densify | **否** | 非 2c 结构债 |
| 当场开防具签名施工 tip | **否** | 须独立 DESIGN + p1sim；禁当薄 densify |
| **2c / 防具签名 RESEARCH（docs-only）** | **采纳** | 总控点名 P1；盘清 OPEN 债与下一 DESIGN 边界 |

---

## 2. 方案 M（定稿荐 · 无码）

| ID | 项 | 验收 |
|----|-----|------|
| D12544 | 本 tip + RESEARCH `RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md` | 路径可点 |
| D12545 | DESIGN power-paths 进度戳：**TIP-Z CLOSED `923e031f1`**；**TIP-AA OPEN**（主线解锁 2c 研究 · 无码）；深化 densify 仍冻 | 戳可见 |
| D12546 | 勘误写入 RESEARCH：D174「2c=六槽+防具签名」vs 现网「六槽已 live」裂口；2c 真债 = **防具签名 + W30/叠合** | 可核 |
| D12547 | OPEN 债表 S1–S8（防具签名规则 / stampable / 生效优先级 / W30 / 口径三角 / 旁支禁夹带） | 可核 |
| D12548 | 下一独立 DESIGN 大纲：stage0 规格 → 离线门禁 → 显式开工后才施工 tip；门禁草案 G-sig / G-42 / G-W30 / G-drop | 文档 |
| D12549 | HOLD 施工：本 tip **无码**；**不**派插件；防具签名 / 六槽改开关 / 抬 STAMP **本窗不开** | 无 jar |
| D12550 | 明确不做清单入 tip（§3）；成熟游戏对照指针入 RESEARCH §4 | 可核 |
| D12551 | 本 commit docs-only；密码检查 0；缓冲仅本文热修（错链/错戳） | PASS |

### 玩家口径（固定）

- 主线「每图自己的签名 + 徽记烙印」已在；本窗**不加新签名、不改掉落**。  
- 六槽甲已能穿；**签名暂时仍只在刃 / 护符**——若要「甲也能烙招牌效果」，等独立设计 + 模拟，不靠本窗塞补丁。  
- 变强仍走装备 / 锻造 / 宝石 / 已有签名；本窗不给新乘区。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / ember-v1-runs 改码  
- 防具签名 Java、`mods()` 扩甲、烙印目标扩甲、stampable 扩甲  
- 抬 STAMP_RATE / 徽记周池 / echo cap / Affix  
- 关或拧六槽三开关；开 K3 / 档 S / 白板补建模  
- L11b 调律、伤乘、skill_mult、glance twin、sx20  
- ForgeChoice·wear·pity·awkDelta densify · 深化 densify  
- 把本 RESEARCH 当成开工令或数值终表

---

## 4. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] RESEARCH + tip 路径可点；2c 勘误与 S1–S8 可核  
- [x] DESIGN power-paths：TIP-Z CLOSED `923e031f1` + TIP-AA CLOSED  
- [x] **无**插件施工派单；**无**独立 DESIGN 文件冒充已批施工（本窗仅 RESEARCH）  
- [x] `git show` docs-only；密码检查 0  

---

## 5. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-Z P1 · **主线解锁 2c 研究 docs-only**（甲槽已 live · 真债防具签名 · 禁薄 densify）  
- **批注：** 下一窗独立 DESIGN stage0（HOLD 施工）；门禁过线前零码；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 17:37 CST · 无码
