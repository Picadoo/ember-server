# 状态 · D385：使魔菜单挂等级与投喂进度同屏（P2 · TrMenu-only · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-pet-level-papi-2026-10-10.md`](../design/DESIGN-ember-pet-level-papi-2026-10-10.md) §2.2 · tip [`STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md) @ `a869c892` · P1 插件 D384 tip `1acd97da` · jar `1.65.102-d384.local`  
**裁决：** **已批 A · 批 M · D385** · 本号交 `ember_pet` W1a–W1c（+W1d Open）挂 `%corerpg_pet_*%` · **≠关观察** · **≠改 feed 公式** · **≠假写固定 Lv** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv** · **≠新空跳转**  
**版本：** TrMenu + docs · jar / feed.* / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

打开使魔页就能看见选定名、等级行（`Lv.N/max` 或满级）、下一级魂尘提示与展示战力加值；投喂格顶部也有等级/成本；出战格有选定短签。数字来自 D384 真 Placeholder，不是写死字。

## 落地摘要（方案 M · §2.2）

| 窗 | 本号 |
|----|------|
| W1a 状态格 I | Layout `#   I   #`；`§a使魔状态`；lore：`pet_active_name` · `pet_level_line` · `pet_feed_hint` · `pet_power_bonus` |
| W1b 投喂 S | lore 顶 +`level_line` + `feed_hint`；**actions 仍** `corerpg pet feed` |
| W1c 出战 E | lore +`选定：name · level_line`；summon/dismiss **未改** |
| W1d Open | +半行「本页可见等级与下一级魂尘」 |
| F/L | 生活跳转 / pet list **未改** |

## 挂键清单

| Placeholder | 挂点 |
|-------------|------|
| `%corerpg_pet_level_line%` | I · S · E |
| `%corerpg_pet_feed_hint%` | I · S |
| `%corerpg_pet_active_name%` | I · E |
| `%corerpg_pet_power_bonus%` | I |

## 验收自检（静态）

| ID | 结果 | 备注 |
|----|------|------|
| V2 | **PASS（静态）** | I/S/E 含等级行+投喂 hint；无字面固定个人 Lv |
| V4 形态 | **PASS（静态）** | 键空态由插件约定（尚无使魔/先解锁）；菜单未硬写崩点 |
| V5 | **PASS** | 本号未改 `pet.yml` feed.* |
| V6 | **PASS（静态）** | 无假写固定 Lv；无挂机产魂尘；F/L/E/S actions 语义保留；仅 hub+life 两处 menu |
| actions | **PASS** | F→`ember_life` · L→`pet list` · E summon/dismiss · S `pet feed` |

**活测（测岗）：** 有宠号开 `ember_pet` + 投喂后重开看数字变；`/papi parse me %corerpg_pet_level_line%`。

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_pet.yml` | Layout 插 I；I 状态格；E/S lore 挂键；Open tell |
| `DESIGN-ember-pet-level-papi-2026-10-10.md` | 勾菜单窗已落地 · STATUS→D385 |
| `design-ember-content-backlog.md` | `B-pet-level-papi` → **已关 · D385 菜单已落地** |
| `STATUS-ember-pet-level-papi-d384-2026-10-10.md` | 下一号指针→D385 已交 |
| 本 STATUS | 落地摘要 · 挂键 · 静态验收 |
| feed.* / jar / ×0.97 / 三开关 / bv / ladder / calamity / p1-six / MM | **未动 / 未 stage** |

## 热更

- play **已起**（PID **906214** · jar `1.65.102-d384.local`）· 已热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (137 ms)`（**03:53:05 CST**）
- **未**执行 `/corerpg reload`（零 feed / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 feed.* / max_level / power_per_level · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · 假写固定 Lv · 新空跳转格 · 改 Java · stage 脏 runtime · 复述 D373–D384 主交付

---

*D385 批 A·M · P2 菜单挂键 · tip 上游 `a869c892` / P1 `1acd97da` · ≠关观察 ≠改 feed。*
