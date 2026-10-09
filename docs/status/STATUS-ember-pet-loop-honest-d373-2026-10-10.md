# 状态 · D373：使魔闭环诚实（出战可点 + 魂尘来源诚实 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-ember-pet-loop-honest-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-ember-pet-loop-honest-need-design-2026-10-10.md) @ `1d938a07` · DESIGN [`DESIGN-ember-pet-loop-honest-2026-10-10.md`](../design/DESIGN-ember-pet-loop-honest-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D373** · 总控采纳方案 M（含 W1d）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 afk.tiers** · **≠挂机加 soul_dust** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + NI lore + docs · jar / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

使魔页出战终于真出宠（左键出战 / 右键收回）；魂尘文案改成生活·钓鱼兑换，不再谎称挂机副产。挂机日表没动。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 出战可点 | `ember_pet` 格 E：`left` → `corerpg pet summon`；`right` → `corerpg pet dismiss` |
| W1b lore 诚实 | 出战/投喂删「挂机副产」→「生活·钓鱼兑换」；Open tell 半行魂尘真源 |
| W1c NI | `mat_ember_soul_dust` lore →「生活·钓鱼兑换 · 可堆叠」 |
| W1d 生活闭环 | `ember_life` 加格 P「打开使魔」；孵化 H/J 成功后 `menu: ember_pet`（图标仍可后置入 layout） |

## 验收自检 P1–P6

| ID | 结果 | 备注 |
|----|------|------|
| P1 | **PASS（静态）** | `ember_pet` 无「挂机副产」；写生活·钓鱼 |
| P2 | **待测（动态）** | 需在线玩家已解锁使魔点左键出战 / 右键收回；服已起且已 `trmenu reload` |
| P3 | **待测（动态）** | 无解锁无蛋点出战 → 现网失败提示 |
| P4 | **待测（动态）** | 投喂仍 `pet feed`（actions 未改命令） |
| P5 | **PASS（静态+已 ni reload）** | NI 无「挂机 / 副本副产」；`ni reload` 02:56:20 CST |
| P6 | **PASS** | `ember-v1.yml` afk 段 **仍无** `soul_dust`；本号未改 afk.tiers |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_pet.yml` | 出战左 summon / 右 dismiss；lore/Open tell 诚实 |
| `plugins/TrMenu/menus/ember_life.yml` | W1d：格 P 打开使魔；孵化后开使魔页 |
| `plugins/NeigeItems/Items/ember-pets.yml` | `mat_ember_soul_dust` lore 真源 |
| `DESIGN-ember-pet-loop-honest-2026-10-10.md` | 勾批 M；STATUS→已批·D373；变更记录 |
| tip `…-pet-loop-honest-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-ember-pet-loop-honest` → **已关 · D373 已施工** |
| 本 STATUS | 落地摘要 · P1–P6 |
| afk.tiers / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play **已起** · 已热更：
  - `scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (146 ms)`（**02:56:17 CST**）
  - `scripts/console.sh play "ni reload"` → 日志 `NeigeItems > 重载完毕`（**02:56:20 CST**）
- **未**执行 `/corerpg reload`（零 afk / 零开关变更）。

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/NI/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 afk.tiers / 挂机加 soul_dust · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 玩家面教裸 `/corerpg pet …`

---

*D373 批 A·M · tip `1d938a07` · 使魔出战可点 + 魂尘来源诚实 · ≠关观察 ≠挂机加魂尘。*
