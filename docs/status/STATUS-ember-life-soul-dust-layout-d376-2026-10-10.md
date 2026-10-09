# 状态 · D376：生活页魂尘兑换/孵化回 Layout（使魔养成旁轨可点 · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-life-soul-dust-layout-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-life-soul-dust-layout-need-design-2026-10-10.md) @ `b24427a8` · DESIGN [`DESIGN-ember-life-soul-dust-layout-2026-10-10.md`](../design/DESIGN-ember-life-soul-dust-layout-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D376** · 总控采纳方案 M（W1a/W1b/W1c/W1d/W1e）· tip 旁注已关 · backlog 已对齐 · **≠关观察** · **≠改 life.yml 价/daily/weekly** · **≠改 afk.tiers** · **≠开 R** · **≠改 ×0.97 / 三开关 / bv**  
**版本：** TrMenu + docs · jar / life.yml / afk / set_bonus / bv **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

补给·生活页现在能点「旧靴/碎骨→魂尘」和「孵化灰灵/烬火」；开页与使魔入口文案和盘面一致；枢纽补给格半行互指。价表与挂机产量零改。

## 落地摘要（方案 M）

| 窗 | 本号 |
|----|------|
| W1a 魂尘兑换回盘 | Layout `# G D K #` · Icons G/K 既有 actions 保留 |
| W1b 孵化回盘 | Layout `# H   J #` · H/J 成功后仍 `menu: ember_pet` |
| W1c D99 注释 | 改为「D376 回盘；E/F/O/W/V 仍 stowed」 |
| W1d Open / P | Open tell「魂尘：本页旧靴/碎骨兑换 · 非挂机」；P lore「魂尘见本页 G/K」 |
| W1e 枢纽半行 | `ember_hub` 格 `k` +「可兑魂尘养使魔」 |

推荐盘形：

```
#########
# A B C #
# G D K #
# H   J #
###I#P#Z#
```

## 验收自检

| ID | 结果 | 备注 |
|----|------|------|
| V1 | **PASS（静态）** | Layout 含 G、K、H、J；保留 A–D / I / P / Z |
| V2 | **PASS（静态）** | G→`life buy soul_dust`；K→`soul_dust_bone`；H/J→孵化+`menu: ember_pet` |
| V3 | **PASS（静态）** | P lore 对齐真盘；Open tell 有魂尘半行；**无**教裸 `/corerpg life buy` |
| V4 | **PASS** | 本号未改 `plugins/CoreRpg/life.yml`（价/daily/weekly） |
| V5 | **PASS** | 本号未改 `ember-v1.yml` afk.tiers；观察三开关/bv **未动** |
| V6 | **PASS（静态）** | hub `k` lore 含「可兑魂尘养使魔」 |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_life.yml` | Layout 纳入 G/K/H/J；D99 注释订正；Events.Open；P lore |
| `plugins/TrMenu/menus/ember_hub.yml` | 补给·生活格 lore +半行 |
| `DESIGN-ember-life-soul-dust-layout-2026-10-10.md` | 勾批 M；STATUS→已批·D376；变更记录 |
| tip `…-life-soul-dust-layout-need-design-…` | 旁注已关 |
| `design-ember-content-backlog.md` | `B-life-soul-dust-layout` → **已关 · D376 已施工** |
| 本 STATUS | 落地摘要 · 验收 |
| life.yml / afk.tiers / ×0.97 / 三开关 / bv / jar / ladder / calamity / p1-six | **未动 / 未 stage** |

## 热更

- play 热更：`scripts/console.sh play "trmenu reload"`（见落字后执行日志）
- **未**执行 `/corerpg reload`（零 life / 零 afk / 零开关变更）

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1 / life.yml；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 改 life.yml 价/daily/weekly · 改 afk.tiers / daily_kills · 改 ×0.97 / set_bonus / 三开关 / bv · 开 R · 开 K3 · Pack6 / 天赋 / 灰印 · stage 脏 runtime · 玩家面教裸 `/corerpg life buy …` · 复述 D373 出战主交付

---

*D376 批 A·M · tip `b24427a8` · 生活页魂尘/孵化回盘可点 · ≠关观察 ≠改价/次 ≠抬挂机表。*
