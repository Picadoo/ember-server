# 状态 · D386：生活页副手守腕/生坠回 Layout（灰粮可购成真 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-life-offhand-layout-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-life-offhand-layout-need-design-2026-10-10.md) @ `0cfa2ca5` · DESIGN [`DESIGN-ember-life-offhand-layout-2026-10-10.md`](../design/DESIGN-ember-life-offhand-layout-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D386** · 总控采纳方案 M（W1a/W1b/W1c/W1d）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 life.yml 价/weekly** · **≠回盘 E/F/V** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / life.yml / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

补给·生活页现在能看见并点「守腕 / 生坠」周购；套装·副手格一点进生活；开页半行提醒副手不进主线战力。价表与周顶零改；重铸/稳固/旧票仍不回盘。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 副手购键回盘 | Layout 增 `# O   W #`；Icons O/W 既有 `life buy offhand_ward` / `offhand_vita` 保留 |
| W1b 注释订正 | 「D386+：O/W 副手回盘；E/F 与 V 仍 stowed（E/F=P1 旧轨拒用）」 |
| W1c set 互指 | `ember_set` O → `menu: ember_life`；lore +「➥ 打开补给·生活购买」 |
| W1d Open 半行 | +「副手：本页守腕/生坠周购 · 不进主线战力」 |
| W1e hub/help | **本号未做**（可选） |

推荐盘形（已落）：

```
#########
# A B C #
# G D K #
# H   J #
###I#P#Z#
# O   W #
```

## 验收自检

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | Layout 含 O、W；保留 A–D / G/K / H/J / I/P/Z；**无** E/F/V |
| V2 | **PASS（静态）** | O→`life buy offhand_ward`；W→`offhand_vita`；actions 未改命令 |
| V3 | **PASS（静态）** | set O → `menu: ember_life`；lore 含「打开补给·生活」 |
| V4 | **PASS** | 本号未改 `plugins/CoreRpg/life.yml`（价/weekly/give） |
| V5 | **PASS** | 本号未改 `ember-v1.yml`；观察三开关/bv **未动** |
| V6 | **PASS（静态）** | Open tell 有副手半行；**无**教裸 `/corerpg life buy` |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_life.yml` | Layout 纳入 O/W；stowed 注释；Events.Open 半行 |
| `plugins/TrMenu/menus/ember_set.yml` | 副手格 O → life + lore 互指 |
| `DESIGN-ember-life-offhand-layout-2026-10-10.md` | 勾批 M；STATUS→已批·D386；变更记录 |
| tip `…-life-offhand-layout-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-life-offhand-layout` → **已关 · D386 已施工** |
| 本 STATUS | 落地摘要 · 验收 |
| life.yml / afk.tiers / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play **已热更**：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 70 个菜单已加载 (129 ms)`（**04:00:04 CST**）
- **未**执行 `/corerpg reload`（零 life / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1 / life.yml；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 life.yml 价/weekly · 回盘 E/F/V · 改 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 玩家面教裸 `/corerpg life buy …` · 复述 D373–D385 主交付

---

*D386 批 A·M · tip `0cfa2ca5` · 生活页副手 O/W 回盘可点 · ≠关观察 ≠改价/周顶 ≠回盘 E/F。*
