# 状态 · D413 抽测：挂机 ActionBar 叠 remain / 满额短征追

**日期：** 2026-10-10（上海时间）  
**号：** D413Spot · 证据 `/workspace/tmp/d413-actionbar-remain/`  
**上游：** tip [`cd9a19f2`](https://github.com/Picadoo/ember-server/commit/cd9a19f2) · live jar `1.65.121-d414.local`（含 D413+D414；产物亦有 d413 `1.65.120`）· DESIGN [`DESIGN-ember-afk-actionbar-remain-eta-2026-10-10.md`](../design/DESIGN-ember-afk-actionbar-remain-eta-2026-10-10.md) · 施工 STATUS [`STATUS-ember-afk-actionbar-remain-eta-d413-2026-10-10.md`](STATUS-ember-afk-actionbar-remain-eta-d413-2026-10-10.md)  
**范围：** 活体 ActionBar（未满 W1a + 满额 W1b）+ 纯函数单测 + 菜单 remain_line 仍在 · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily · **未切分支** · **未无故停服**  
**总结果：** **PASS**

## 人话

挂机自动打时抬头 ActionBar 能看见「还差 X 只 · 开打后估时 / 约 Y 分钟满」，和战况 remain_line 同量级，没有「保证分钟满」。打满后 ActionBar 仍写「去冒险」，并叠「短征还可追 · 剩余有奖 N」；体力不足时改诚实「短征需30体力」。产量日顶仍 2400，没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V0 | six_slot 三开 true · daily_kills=2400 · jar 含 D413 文案串 · tip `cd9a19f2` 祖先 | **PASS** | `00-switches-pre.json` · `TIP-ANCESTOR.txt` · jarStrings |
| V-pure | fight/cap ActionBar 单测：有/无 kph · 可追/需30 · 无「保证」· 去冒险保留 | **PASS** | `15-unit-actionBarRemain.txt` · `actionBarRemain_D413_W1aW1b` |
| V6 | 战况 remain_line / next_farm 菜单仍挂 | **PASS** | `10-menu-static.json` |
| V1 | 未满自动打：ActionBar 可见还差+估时；与 remain 同量级；无「保证」 | **PASS** | `30-actionbar-unfull.json` · `31-remain-magnitude.json` |
| V2 | 无 kph →「开打后估时」（诚实占位） | **PASS** | 同上（kph=0 窗） |
| V3a | 满额 ActionBar 含「去冒险」 | **PASS** | `41-actionbar-full-hi.json` |
| V3b | 满额+体力≥30+sum>0 →「短征还可追 · 剩余有奖 N」 | **PASS** | `41-actionbar-full-hi.json`（剩余有奖 **30**） |
| V3c | 满额+体力&lt;30 →「短征需30体力」· 不假喊可追 | **PASS** | `51-actionbar-full-low.json` · `50b-stamina-low-recheck.json` |
| V5 | gate_daily=`no` · daily_kills/tiers/Stage2 零改 | **PASS** | `60-gate-daily.json` · `90-endstate.json` |

## 关键证据摘要

- tip `cd9a19f2` · jar `CoreRpg v1.65.121-d414.local` · sha256 `cbcfb4c0…`（= `/workspace/tmp/d414/` 装盘）
- 测号：`D413AbRem`（未满）· `D405AfkEta`（满额 2400 既有键）
- 未满 ActionBar 样例：`挂机 灰坡 · 入门稳挂 · 今日 0/2400 只 · 0 只/小时 · 还差 2400 只 · 开打后估时`
- 满额高体：`今日挂机已满 2400/2400 · 体力还在 · 去冒险 · 短征还可追 · 剩余有奖 30`
- 满额低体：`今日挂机已满 2400/2400 · 体力还在 · 去冒险 · 短征需30体力`
- 旁注：首次 `%corerpg_stamina%` parse 曾被 ActionBar 串污染（`99-results` L3）；`50b` 复核 stamina=**12**；W1b 低体 ActionBar 本已绿

## 不动确认

daily_kills / afk.tiers · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · 无故停服

## 总控旁注

**≠关观察**。D413 挂机 ActionBar 叠 remain/满额短征追抽测结案 **PASS**。

---

*D413Spot · PASS · tip `cd9a19f2` · jar `1.65.121-d414.local` · 证据 `/workspace/tmp/d413-actionbar-remain/` · ≠关观察 · checked 2026-10-10 CST.*
