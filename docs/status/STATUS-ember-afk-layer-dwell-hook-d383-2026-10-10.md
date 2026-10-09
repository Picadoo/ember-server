# 状态 · D383：挂机庭今日软目标与层差展示钩（多坐一会儿 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-afk-layer-dwell-hook-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-layer-dwell-hook-need-design-2026-10-10.md) @ `23ca4162` · DESIGN [`DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md`](../design/DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D383** · 总控采纳方案 M（W1a+W1b+W1c 必做；W1d 同批；W1e 可选已做）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 afk.tiers / daily_kills** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv** · **≠新空跳转格** · **≠新 PAPI**  
**版本：** TrMenu + docs · jar / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

打开挂机庭战况就能看见「今日软目标」框住 N/2400，未满继续坐、满了去冒险/去哪花；层差一览告诉下一层多养什么；②③④层格有「相对下层多养」半行。产量与日表零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 今日软目标 | I：`§e今日软目标：%corerpg_p1_afk_today%` + 未满/满了指针；session/kph/cap **合并去重**（去掉重复「今日击杀」行） |
| W1b 层差一览 | I：灰坡→荒原多核心 · 荒原→焦土多胚料 · 焦土→烬原最高档 · T4=已最高；+「看上排①–④换层」 |
| W1c 相对多养 | 已开 ②`相对灰坡：+核心` · ③`相对荒原：+胚料` · ④`相对焦土：币/碎片/核心最高`；锁态未加 |
| W1d 去哪花旁文案 | S +`今日条未满先攒 · 满了来这花 / 或去冒险`；**actions 未改** |
| W1e Open / hub | Open tell「战况有今日软目标·层差」；hub Z +半行指针 |
| Layout / 产量 | **未加格** · `daily_kills`/tiers **未动** |

## 验收自检 V1–V5

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | I 含「今日软目标」+ today；未满句与满额「去冒险」不矛盾 |
| V2 | **PASS（静态）** | I 层差一览 + ②③④相对多养短签对齐真表 |
| V3 | **PASS（静态）** | ①–④ / S / F actions 未改；无新 Layout 格 |
| V4 | **PASS** | 本号未改 `ember-v1.yml` afk；skip-worktree 仍在 |
| V5 | **PASS（静态）** | 无假可达日更、无「挂机产魂尘」、无新造 PAPI 键 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | I 软目标+层差；②③④相对多养；S 旁文案；Open tell |
| `plugins/TrMenu/menus/ember_hub.yml` | Z 半行「今日软目标·层差一览」 |
| `DESIGN-ember-afk-layer-dwell-hook-2026-10-10.md` | 勾批 M；STATUS→已批·D383 |
| tip `…-afk-layer-dwell-hook-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-afk-layer-dwell-hook` → **已关 · D383 已落地** |
| 本 STATUS | 落地摘要 · V1–V5 |
| afk.tiers / daily_kills / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six / MM | **未动 / 未 stage** |

## 热更

- play **已起** · 已热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (82 ms)`（**03:42:27 CST**）
- **未**执行 `/corerpg reload`（零 afk / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · 新空跳转格 · 新 PAPI（remain/eta）· 主题 B 使魔 · stage 脏 runtime · 复述 D373–D381 主交付

---

*D383 批 A·M · tip `23ca4162` · 挂机庭今日软目标+层差展示 · ≠关观察 ≠抬日表。*
