# 状态 · D422 派测：冒险页短征入口数字化（薄抽 · 菜单）

**日期：** 2026-10-10（上海时间）  
**号：** D422AdvDayFc · 证据 `/workspace/tmp/d422-adventure-day-fc-spot/`  
**上游：** 菜单 tip [`9867aa5d`](https://github.com/Picadoo/ember-server/commit/9867aa5d) · DESIGN [`DESIGN-ember-adventure-short-day-fc-visible-2026-10-10.md`](../design/DESIGN-ember-adventure-short-day-fc-visible-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-adventure-short-day-fc-visible-d422-2026-10-10.md`](STATUS-ember-adventure-short-day-fc-visible-d422-2026-10-10.md) · live `sx_day_left_sum` / `sx_fc_left` / `sx_fc_pending_line` · 对照 D418/D420 薄抽  
**范围：** `ember_p1_adventure` H 解锁态挂 day/fc · Open 半行 · 灰锁禁个人数字 · 点进选本 · **零 jar** · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily / 日帽/S40–S52/体力 · **未切分支** · **未碰** login/proxy/MariaDB  
**总结果：** **PASS**

## 人话

打开冒险页：解锁态 H 真显「今日剩余有奖：39」+「生涯首通未领：13 本」+「还有 13 本生涯首通未领」（与 `/papi parse me`、枢纽体力可追、选页 I 同量级）。灰锁（未 Q01）无个人 day/fc 数字。Open 半行「短征入口可看今日剩余有奖/首通未领」。点 H → 短征选本页。产量/Stage2/gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S1 | 静态：H 解锁挂 day_left_sum+fc_left+pending；灰锁无个人键；Open 半行；无保证升阶/sx14/教裸/旧日常窟假开 | **PASS** | `10-menu-static.json` |
| V1 | 解锁态 H 真 PAPI（非假写固定 N）· 与 parse me 同文 | **PASS** | `30-adventure-window.json` · `20-papi-base.json` |
| V2 | 与枢纽 day_left_sum / 选页 I 的 fc 同量级 | **PASS** | `31-hub-compare.json` · `32-short-compare.json` |
| V3 | 灰锁（未 Q01）无个人 day/fc 数字 | **PASS** | `25-locked-window.json` |
| V4 | 静态规则仍在；点 H → ember_p1_short | **PASS** | `55-v4-click-short.json` · `10-menu-static.json` |
| V5 | Open 半行「短征入口可看今日剩余有奖/首通未领」 | **PASS** | `30-adventure-window.json` openChat |
| V6 | 文案无保证升阶/旧日常窟假开/sx14/教裸；零改日帽/S40–S52/体力 | **PASS** | `10-menu-static.json` · `90-endstate.json` |
| V7 | `%corerpg_gate_daily%` = `no` | **PASS** | `60-gate-daily.json` |
| V8 | six_slot 三开关 true · daily_kills=2400 · afk.tiers md5 未变 · 本号零改 jar | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- 施工 tip `9867aa5d` · adventure md5 `6a89381dc3c03a3019c4d7edcf3a5d2e`  
- live jar 抽测窗：`1.65.124-d421.local`（**零 jar 本号**；并行 D421 换装；day/fc 键 live 加性保留）sha256 `33958666…` · bv **77**  
- 测号 `D422AdvDayFc` · 起止约 **2026-10-10 09:59～10:00 CST**  
- V1：H「今日剩余有奖：39」+「生涯首通未领：13 本」+ pending「还有 13 本生涯首通未领」  
- V2：枢纽/选页 I 同为 day=39 · fc=13  
- V3：灰锁名含「需 Q01 首通」· lore 无今日剩余/首通未领数字  
- 汇总 `99-summary.json`：16/16 PASS  
- trmenu reload 抽前：`良好 | 73 个菜单已加载`（**2026-10-10 09:59:55 CST**）

## 旁注

- **零 jar**：本号仅菜单；day/fc 键沿用 live。  
- live 并行 jar `1.65.124-d421.local` bv77（D421 sx13）；本号未停服/未换 jar / 未拧日表 / 未动 Stage2。  
- ≠关观察 · ≠交 sx14 · ≠开 gate_daily。  
- `taken_tele: 0.97` 仍在 `ember-v1-growth.yml`（本号未改）。

## 不动确认

afk.tiers / daily_kills · Stage2 三开关 / ×0.97 · gate_daily · 日帽/S40–S52/体力 · 关观察 · 切分支 · stash/reset · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D422 冒险页短征入口数字化薄抽 **PASS**（H 真键同屏 + Open 半行 + 灰锁禁数字 + 与枢纽/选页同量级 + 零改产量）。

---

*D422AdvDayFc · PASS · tip `9867aa5d` · 证据 `/workspace/tmp/d422-adventure-day-fc-spot/` · ≠关观察 · checked 2026-10-10 10:00 CST。*
