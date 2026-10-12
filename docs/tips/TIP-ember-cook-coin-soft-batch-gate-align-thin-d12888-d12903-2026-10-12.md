# tip · TIP-AY 生活烹饪日首 CookCoinSoft 批次门禁/费用诚实对齐（可施工薄 · 可感 UX）· D12888–D12903

> **状态：** **OPEN** · 荐总控批 **A · 方案 M** · 2026-10-12 22:15 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-cook-coin-soft-batch-gate-align-2026-10-12.md`](../design/RESEARCH-ember-cook-coin-soft-batch-gate-align-2026-10-12.md) · DESIGN [`DESIGN-ember-cook-coin-soft-batch-gate-align-2026-10-12.md`](../design/DESIGN-ember-cook-coin-soft-batch-gate-align-2026-10-12.md) · TIP-AX CLOSED live `1.65.789-d12887` · feat `95ea4e614` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径** — **离开 CostSoft·pity·soft-glance / ConvertCap / firstsoft / 甲闸 / 追烙**（AS→AV / AP–AR / AW / AX / AK–AM / AN–AO 已齐）；烹饪币 soft 批次门禁 leftover（面包/熬药已 peek、烹饪漏）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberCookCoinSoft.peekFee` · `LifeService.cook` 裁批/实付文案/退款 · 可选 show SOFT_TAIL + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/cook.coin_each · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft 再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify**

---

## 0. 一句话

日首烹饪币减负待用时，代烤按减负后费用裁批与报账；预览/裁批不吞 soft，扣币才消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **生活烹饪 CookCoinSoft 批次门禁/费用诚实对齐** | **采纳** | 离开 CostSoft/pity/Cap/firstsoft/甲闸/追烙；假拒/欠烤可感；零功率表；面包/熬药同族已 peek；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft 再叠** |
| FestivalEntrySoft 满次预览 | **否** | firstsoft 同构；prefer 非再叠 |
| LifeDailyCapSoft show「满」 | **否** | Cap densify 感；AX 已否 |
| gearlibHope 撤销窗 +5 | **否** | UndoWindow densify STOP |
| mapid 扩甲 haveNeed | **否** | 扩 stampable / SetChase 邻域 |
| CostSoft / pity / ConvertCap / firstsoft / 甲闸再扫 | **否** | 链已齐 / 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感假拒/欠烤债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12888 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12889 | DESIGN / tip power-paths 进度戳：**TIP-AX CLOSED live**；**TIP-AY OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12890 | 错位钉死：E1–E7 裁批全额 vs soft 半价（RESEARCH §1–2） | 可核 |
| D12891 | 「对齐」钉死：peekFee；裁批吃 peek；consume 仅扣币前；文案/退款对齐实付；零改 MULT/coin_each；零新 PAPI（DESIGN §2） | 可核 |
| D12892 | **插件：** `EmberCookCoinSoft.peekFee` | 代码+单测 |
| D12893 | **插件：** `LifeService.cook` 裁批用 peek 有效单价/批费 | 可核 |
| D12894 | **插件：** 成功文案 + 部分退款对齐实付 `cookFee` | 可核 |
| D12895 | **插件：** 可选 `show()` cook 行 ready 尾「今日减负待用」 | 可核 |
| D12896 | **插件：** 单测最小包（DESIGN §2.3）+ consume 不吞于 peek | PASS |
| D12897 | CostSoft glance / pity remain / ConvertCap / firstsoft / TrMenu lore / 甲闸 / SetChase：**本窗不改**；禁加 lore | lore≤3 |
| D12898 | NON 核：`git show` 无 MULT/C_USED/cook.coin_each 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity/Cap/firstsoft densify；无 LifeCap/UndoWindow/FestivalEntry densify | PASS |
| D12899 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft 再叠；LifeCap/UndoWindow/FestivalEntry densify | 文内硬句 |
| D12900 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12901 | 玩家口径：减负待用 → 按减负后每条币裁批/报账；扣币才消耗；MULT/表价不变 | 可核 |
| D12902–D12903 | 缓冲：错链/错戳/文案过长压行；禁借号抬 MULT/coin_each 或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/ConvertCap densify/CostSoft·pity·soft-glance·firstsoft 再叠 | 超额新 tip |

### 玩家口径（固定）

- 今日烹饪减负还没用：代烤按 **减负后每条币** 判断能烤几条；可标 **今日减负待用**；点烤才消耗减负。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；`cook.coin_each` 表价本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-cook-coin-soft-batch-gate-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-cook-coin-soft-batch-gate-align-2026-10-12.md` |
| SoT soft | `CoreRpg/.../EmberCookCoinSoft.java` |
| SoT 裁批 | `CoreRpg/.../LifeService.java` cook / show |
| 对照已齐 | BreadBuySoft / BrewCoinSoft 门禁 peek · TIP-AX firstsoft peek · TIP-AS–AV CostSoft glance |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · ConvertCap densify 禁 · CostSoft/pity/soft-glance/firstsoft 再叠禁 · LifeCap/UndoWindow/FestivalEntry densify 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / `cook.coin_each`  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12892–D12896；D12898 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] peek 不吞 soft；扣币才 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AX 后 · **离开 CostSoft/pity/ConvertCap/firstsoft/甲闸/追烙** — 烹饪 CookCoinSoft 批次门禁/费用诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft 再叠**）  
- **批注：** 面包/熬药日首币减负门禁已 peek；烹饪 `cook` 仍全额裁批 → 假拒/欠烤/文案多报；peekFee + 裁批/实付文案对齐；零改 MULT/coin_each；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance·firstsoft 再叠 仍冻  

## STATUS: OPEN · TIP-AY · 荐 A·M · 2026-10-12 22:15 CST
