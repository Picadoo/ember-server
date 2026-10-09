# 状态 · D389：生活页挂等级与次数同屏（P2 · TrMenu-only · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-life-level-papi-2026-10-10.md`](../design/DESIGN-ember-life-level-papi-2026-10-10.md) §2.2 · tip [`STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md) @ `8305129d` · P1 插件 D388 tip `fc26f9a0` · jar `1.65.103-d388.local`  
**裁决：** **已批 A · 批 M · D389** · 本号交 `ember_life` W1a–W1c（+W1d Open）挂 `%corerpg_life_*_line%` · **≠关观察** · **≠改 life.yml 价/曲线** · **≠假写固定 Lv** · **≠回盘 E/F** · **≠空跳转** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / life.yml / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

打开生活页就能看见生活等级行、今日兑尘剩余、本周孵化剩余；兑尘/孵化格上也有短签。数字来自 D388 真 Placeholder，不必关菜单刷聊天。

## 落地摘要（方案 M · §2.2）

| 窗 | 本号 |
|----|------|
| W1a 状态格 I | name→`§f生活进度`；lore：`life_level_line` · `soul_daily_line` · `hatch_weekly_line`；主路径左键 sound/tell（**不**强制 close）；右键次要 `close`+`corerpg life` |
| W1b G/K | lore +`soul_daily_line` +「各最多 2 / 合计顶 4」；**actions 仍** `life buy soul_dust(_bone)` |
| W1c H/J | lore +`hatch_weekly_line`；孵化命令 / 跳使魔 **未改** |
| W1d Open | +半行「本页可见生活等级与兑尘/孵化次数（I 格）」 |
| update | I/G/K/H/J 均 `update: 20`（对齐 D387） |
| E/F/V / Layout | **未回盘** E/F；Layout 仍 `A B C / G D K / H J / I P Z / O W` |

## 挂键清单

| Placeholder | 挂点 |
|-------------|------|
| `%corerpg_life_level_line%` | I |
| `%corerpg_life_soul_daily_line%` | I · G · K |
| `%corerpg_life_hatch_weekly_line%` | I · H · J |

## 验收自检（静态）

| ID | 结果 | 备注 |
|----|------|------|
| G4 形态 | **PASS（静态）** | I 不强制 close 主路径；三行真 PAPI；G/K/H/J 购键命令保留 |
| 假写 | **PASS** | 无字面固定个人 Lv |
| E/F | **PASS** | Layout 无 E/F；Icons E/F 仍 stowed |
| 空跳转 | **PASS** | 无新 menu 跳；H/J→pet 旧有保留 |
| life.yml / jar / 开关 | **PASS** | 本号未触 |

**活测（测岗）：** 有进度号开 `ember_life` + `/papi parse me %corerpg_life_level_line%`；兑换后 stay-open≤0.5s 看剩余变。

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_life.yml` | I 同屏；G/K/H/J 剩次短签；update:20；Open 半行 |
| `DESIGN-ember-life-level-papi-2026-10-10.md` | 勾菜单窗已落地 · STATUS→D389 |
| `design-ember-content-backlog.md` | `B-life-level-papi` → **已关 · D388+D389 已落地** |
| `STATUS-ember-life-level-papi-d388-2026-10-10.md` | 下一号指针→D389 已交 |
| 本 STATUS | 落地摘要 · 挂键 · 静态验收 |
| life.yml / jar / ×0.97 / 三开关 / bv / ladder / calamity / p1-six / MM | **未动 / 未 stage** |

## 热更

- play **已起**（PID **929948** · jar `1.65.103-d388.local`）· 已热更：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (155 ms)`（**04:10:01 CST**）
- **未**执行 `/corerpg reload`（零 life.yml / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 life.yml 价/daily/weekly/level_xp · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · 假写固定 Lv · 回盘 E/F · 新空跳转格 · 改 Java · stage 脏 runtime · 复述 D373–D388 主交付

---

*D389 批 A·M · P2 菜单挂键 · tip 上游 `8305129d` / P1 `fc26f9a0` · ≠关观察 ≠改 life.yml。*
