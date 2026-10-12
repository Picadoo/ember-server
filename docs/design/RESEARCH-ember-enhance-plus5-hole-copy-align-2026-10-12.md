# RESEARCH · 强化「+5开孔」硬文案诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-BD · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-BC CLOSED live 后总控显式旁债 **enhance「+5开孔」硬文案** — soft 门槛（刃≥4 / 护符≥2）已 live，强化预览 / Hope / 菜单仍只报表额 ≥5/≥3。**诚实 ≠ densify**（禁再降 DELTA / 禁改 unlock_at / sockUnlocked）。  
> **上游：** TIP-BC CLOSED live `1.65.794-d12967` · feat `b13c47c69` · DESIGN-BC 旁债#4 · GemHoleUnlockSoft D10785 + CUE live · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零改 DELTA/C_USED/sockUnlocked/unlock_at 硬表 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 GemHole Open/拒线再叠 · 零 AnvilRepair densify · 零 topUpTo 扩 soft · 零再降开孔门槛 densify**

---

## 0. 一句话

日首开孔门槛−1 已在镶嵌路径生效，强化预览 / Hope / 菜单不得再只写「冲到+5开孔」；文案跟 soft+硬 SoT 对齐，表与 DELTA 不动。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | UnlockSoft：ready→刃 hole1 ≥4、护符 ≥2；硬表 sockUnlocked 刃≥5 / 护符≥3；CUE live | `EmberGemHoleUnlockSoft` · `EmberItemData.sockUnlocked` | **能力 soft+硬双 SoT** |
| E2 | TIP-BC：GemSocket Open ready 尾 + insert 拒线 soft/硬已齐 | `EmberGemSocket` · feat `b13c47c69` | **宝石页已诚实 · 强化侧未跟** |
| E3 | P1 强化预览（`EmberForgeService.enhance` go=false）只报档位/成功率/材料 · **无开孔门槛半句** | `EmberForgeService` ~405–446 | **变强途径静默 · 玩家仍靠菜单「≥5」叙事** |
| E4 | Hope `enhance` 轨 / `enhance()` 串无 UnlockSoft / 开孔门槛 | `EmberHopeMeter` | **Hope 不说 soft 开孔** |
| E5 | TrMenu `ember_p1_gem.yml` 孔1 lore 硬写「强化≥5 解锁」×4；孔0「符需强化≥3」 | `plugins/TrMenu/menus/ember_p1_gem.yml` | **菜单撒谎 / 多报一档** |
| E6 | TrMenu `ember_socket.yml`「刃：孔1@+0 · 孔2@+5」 | `plugins/TrMenu/menus/ember_socket.yml` | **旧轨菜单硬表唯一** |
| E7 | 旧 `EnhanceService` info/list：`未解锁(需+N)` / `(解锁+N)` 只读 `unlock_at` 硬表 | `EnhanceService` · `enhance.yml` unlock_at `[0,5]`/`[3]` | **legacy 硬文案；本窗可双写诚实 · 禁改 unlock_at** |
| E8 | GemSocket 锁孔按钮副标「手持冲强化开孔」无 soft/硬档差 | `EmberGemSocket` ~112–114 | **从锁孔导向强化时无门槛诚实** |
| E9 | 再降 DELTA / 改 unlock_at / sockUnlocked = densify → **否** | 总控硬禁 | **诚实 ≠ densify** |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力 | 现行展示 | 体感 |
|------|--------|----------|------|
| UnlockSoft.ready · 刃 +3 · 开强化预览 | 再冲到 +4 可 soft 开孔1 | 预览无开孔句；菜单喊 ≥5 | **以为必须冲到+5** |
| UnlockSoft.ready · 护符 +1 · 宝石菜单 | 再冲到 +2 可 soft | lore「≥3」 | **多报一档** |
| UnlockSoft.ready · 刃 +4 已 softEligible · 仍看 gem lore | 可 insert（BC 拒线已对） | lore 仍「≥5」 | **菜单与能力打架** |
| 已用今日 soft | 硬表 ≥5/≥3 | 菜单≥5/≥3 | **对（静态双写须保留表额）** |
| 强化预览多次 | ready 不变 | （现无尾） | **预告不得调用 unlocked()** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 既有 `EmberGemHoleUnlockSoft.ready` / `usedToday` / `CUE` / `softEligible`；**禁**改 `DELTA` / `C_USED` / `sockUnlocked` / `enhance.yml unlock_at`。  
2. **P1 强化预览：** 手持件有未硬开孔且 ready → 半句 CUE 或等价「今日开孔门槛−1：刃二孔≥4·护符≥2」；**零** `unlocked()`。已用可不尾或只报表额。  
3. **Hope enhance 轨（可选同构）：** ready 且手持有锁孔 → soft 尾；禁新 PAPI / 禁改保底数字。  
4. **GemSocket 锁孔→强化按钮副标：** ready → 含 soft 档（≥4/≥2）；已用 → 表额 ≥5/≥3。  
5. **TrMenu（物品）：** 替换既有硬唯一 lore 为 **表额+日首−1** 双写静态句（YAML 读不到 ready）；**禁**加第四行 lore；**禁**改 Icons 布局/命令。  
6. **Legacy EnhanceService（可选）：** info 锁孔行 ready 时附「日首−1」；**禁**改 unlock_at 数组。  
7. **禁** Anvil / topUpTo / CostSoft / pity / soft-latch / xp-boost / GemHole Open·拒线再叠。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- 抬/降 DELTA / 改 unlock_at / sockUnlocked / 发明第二开孔 soft  
- soft-latch 假耗再叠 · CostSoft glance 再叠 · pity-remain 再叠 · firstsoft 再叠 · cook/life-buy soft 再叠 · xp-boost cue 再叠 · GemHole Open/拒线再叠  
- AnvilRepair soft densify · topUpTo 扩 soft · ConvertCap densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **enhance/Hope/菜单「+5开孔」硬文案诚实（可施工薄）** | **采纳** — BC 后旁债；soft 已 live；菜单/预览仍硬唯一；零改 DELTA/硬表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft 假耗/GemHole Open 再叠** |
| 再降 DELTA / 改 unlock_at | **否** — densify |
| AnvilRepair Soft 假耗 | **否** — 硬禁 densify |
| topUpTo 扩 soft | **否** — densify |
| GemHole Open/拒线再叠 | **否** — BC 已齐 |
| soft-latch / CostSoft / pity / firstsoft / cook·life-buy / xp-boost cue 再叠 | **否** — 链已齐 / 硬禁 |
| mapid 扩甲 / HopeMeter densify / 甲闸再扫 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |

## STATUS: OPEN · TIP-BD · 2026-10-12
