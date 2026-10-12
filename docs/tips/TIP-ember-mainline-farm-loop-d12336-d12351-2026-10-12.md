# tip · TIP-L 主线每图农场闭环（内容/掉落结构 · 非 glance）· D12336–D12351

> **状态：** 待总控批 **A · 方案 M** · docs-first  
> **上游：** [`RESEARCH-ember-mainline-farm-loop-2026-10-12.md`](../design/RESEARCH-ember-mainline-farm-loop-2026-10-12.md) · [`DESIGN-ember-mainline-unlocks-2026-10-04.md`](../design/DESIGN-ember-mainline-unlocks-2026-10-04.md)（D174）· TIP-C / TIP-J P1  
> **前置：** TIP-K CLOSED live `1.65.766-d12335`；power-glance **仍停**（TIP-J）  
> **施工岗：** 插件岗（yml 偏置对齐 + 既有下一环按钮优先级；**零抬表**）  
> **硬禁：** **零 Affix Pack · 零伤乘 · 零抬 STAMP_RATE / 徽记发放 / skill_mult** · **零 power-glance densify twin** · 不发明新主线模式 · 不改 Economy/pom 脏树

---

## 0. 一句话

身份已经能看见；本窗把 **Q01–Q07 掉落偏置与签名农场对齐**，并理顺「图鉴齐 → 追烙/烙印」结构下一环——不是再铺枢纽一眼。

---

## 1. 研究结论（摘要）

完整矩阵见 RESEARCH。唯一硬结构断环：

- **Q07** `loot: {family: burst}` **无 `slot`** → 部位不偏，FarmCue 糊成「刃/护符」。  
- Q01–Q06 偏置与主签名对齐（Q05 主农场 L09 形状刃 = **故意**，不改）。  
- 徽记→烙印→残响/前哨/誓约 **已上线**；缺的是齐身份后的 **下一环指针**，不是新掉率。

---

## 2. 方案 M（定稿）

| ID | 项 | 验收 |
|----|-----|------|
| D12336 | 本 tip + RESEARCH | 路径可点 |
| D12337 | DESIGN power-paths / growth-variety **指针** TIP-L（非 glance） | 进度戳可见 |
| D12338 | **Q07** `ember-v1-runs.yml`：`loot: {family: burst, slot: charm}` | `lootLabel` 含「护符」；FarmCue 追护符；**不改** family / STAMP_RATE / 徽记数 |
| D12339 | 单测或 source-map 抽检：q07 lootSlot=`charm`；q01–q06 矩阵不变 | 无旁改 |
| D12340 | **下一环结构**：结算或进本 cue ——若本图图鉴齐 **且** 玩家有追烙目标（或本图徽记≥1 未满烙）→ ConfirmTokens **优先** `[追烙/签名]` / `[去烙印]`（复用既有命令）；工坊成色按钮置次。若图鉴未齐 → 保持现 MapFarmCue 工坊/再进本 | 禁新 remainLine / 禁新 hope PAPI |
| D12341 | Q05 注释（yml 或 DESIGN 一行）：主农场 = L09 形状刃；L10 次；**不改** loot | 文档可核 |
| D12342 | HOLD：枢纽一眼格折叠（另 tip）；2c 扩签；抬 STAMP_RATE；Affix；echo 周池扩 cap | 本包不开 |
| D12343 | 验收：进 Q07 身份串见「烬爆 · 护符」；追烙路径仍 0 改率；密码检查 0 | PASS/记待恢复服 |
| D12344–D12351 | 缓冲：仅本包热修（文案/空指针）；禁借号 glance densify | 超额须新 tip |

### 玩家口径

- 每张主线图：**偏置基装**（族×部位）+ **本图签名** + **徽记烙印**；晚游戏回刷 = 换件重烙 / 残响加速。  
- 通关不锁掉落；本窗不改 12% 烙率。

---

## 3. 明确不做

- Affix Pack、伤乘、抬 STAMP_RATE / CLEAR_MARKS / FC_MARKS / skill_mult  
- power-glance twin（希望/宝石/签名菜单再一眼）  
- 枢纽挤度折叠（TIP-J P0 观察后另开）  
- 新残响次数、新模式、按图锁部位  
- 改 Q05 loot「纠偏」成 charm  

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] D12338 q07 slot=charm；lootLabel / FarmCue 部位可辨  
- [ ] D12340 仅改按钮优先级，无新进度轨  
- [ ] `git show` 无 Affix / 数值表无关脏文件  
- [ ] glance 停窗未被本 tip 打开  

---

## 5. 批注

- [ ] 总控批 **A · 方案 M** · _待填_  
- 身份入口：TIP-C CLOSED；本 tip = **掉落结构闭环**  
- 枢纽折叠：不排本窗

## STATUS: OPEN · 待批 A·M · 2026-10-12 Asia/Shanghai
