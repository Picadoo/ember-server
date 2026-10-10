# 状态 · D416 派测：短征选页挂仓差（薄抽 · 菜单）

**日期：** 2026-10-10（上海时间）  
**号：** D416Spot · 证据 `/workspace/tmp/d416-short-mat-gap/`  
**上游：** 施工 tip [`aebfc1c9`](https://github.com/Picadoo/ember-server/commit/aebfc1c9) · DESIGN [`DESIGN-ember-short-select-mat-gap-2026-10-10.md`](../design/DESIGN-ember-short-select-mat-gap-2026-10-10.md) · 挂盘 STATUS [`STATUS-ember-short-select-mat-gap-d416-2026-10-10.md`](STATUS-ember-short-select-mat-gap-d416-2026-10-10.md) · D404 vault/gap live · D409 满态去工坊花 · D415 战况仓差旁注  
**范围：** `ember_p1_short` I 仓内四材 + enhance1/upgrade_t2/refine1 还差 · Open「选本说明可看仓内还差」· 满态 D409「去工坊花」主 CTA 保留 · day_line/fc/周钩/去哪花保留 · **零 jar** · **≠关观察** · **未改** daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily · **未切分支** · **未反复停服**（并行 D414/D415 占 play 时仅菜单/PAPI）  
**总结果：** **PASS**

## 人话

打开短征选页：Open 半行「选本说明可看仓内还差」；说明 I 同屏真仓「碎/骨/核/胚」与「强化+1 碎差 / 升阶T2 / 精工0→1」。未满（短征说明）与近满（sum=3）仓差都可读；两满态分支静态仍挂「今日有奖已满 · 去工坊花」主名，仓差挂在日帽块下不盖 CTA。day_line / 首通 fc / 周钩 / 「去哪花」都还在。产量/日帽数值/Stage2/gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S1 | 静态：I 三分支各挂 vault_* + recipe_gap_*（×3）；两满态 `name: 今日有奖已满 · 去工坊花`；Open「选本说明可看仓内还差」；day_line/fc/周钩/B 去哪花仍在 | **PASS** | `10-menu-static.json` |
| U1 | 未满 live（`D416ShortGap` sum=30）：I=`短征说明` 仓内 0/0/0/0 · 还差 4 / 碎差60·核差12·胚差6 / 胚差3·骨差5 · 与 papi 同数 | **PASS** | `U-window.json` · `U-I-parsed.json` · `U-papi.json` |
| U2 | Open tell「选本说明可看仓内还差」；day_line/fc/周钩/B「去哪花」仍在 | **PASS** | `U-window.json` · `U-I-parsed.json` |
| F1 | 近满 live（`D409Spend` sum=3）：I 仓内碎36·骨18·核0·胚2 · 还差与 papi 同数（满态号日帽已回落，见旁注） | **PASS** | `F-window.json` · `F-I-parsed.json` · `F-papi.json` |
| F2 | 满态主 CTA：静态两满态分支保留「去工坊花」；仓差行在日帽块下不盖 name（live 满态因日帽回落改由静态证） | **PASS** | `10-menu-static.json` · S1_full_cta_retained |
| V4 | `%corerpg_gate_daily%` = `no`（两测号） | **PASS** | `U-gate-daily.json` · `F-gate-daily.json` |
| V5 | six_slot 三开关 true · daily_kills=2400 · afk.tiers md5 未变 · jar 未换 · 菜单 md5 未变 | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- 施工 tip `aebfc1c9` · live jar `1.65.121-d414.local`（**零 jar** 本号；含 D404 键）sha256 `cbcfb4c0…`  
- 菜单 md5 `fca25c287aca431ea1c791fb283eb3cb`（`ember_p1_short.yml`）  
- 测号 `D416ShortGap`（未满）+ `D409Spend`（近满复用）· 起止见 `timestamp-start.txt` / `timestamp-end.txt`  
- 未满同屏：仓内碎0·骨0·核0·胚0 · 强化+1 碎差4 · 升阶T2 碎差60·核差12·胚差6 · 精工 胚差3·骨差5  
- 近满同屏：仓内碎36·骨18·核0·胚2 · 碎差0 · 升阶 碎差24·核差12·胚差4 · 精工 胚差1·骨差0  
- Open hits：选本说明可看仓内还差（满/未满共用）  
- 汇总 `99-summary.json`：34/34 PASS

## 旁注

- 并行 D414/D415 曾占 play；本号仅菜单/PAPI，**未停服**、**未 trmenu reload 二次**（施工号已于 **08:55:36 CST** reload PASS）。  
- `D409Spend` 在 D409 补验时曾 sum=0，本抽复用时已回落为 sum=3（仅 sx10 日帽未满）；**禁离线 inject 日帽**（对齐 D409 补验约束）。满态 CTA 以静态两分支 + 仓差挂载位置验收；仓差 live 在 sum=30 / sum=3 两态均已解析。  
- ≠关观察 · ≠抬日表 · ≠交 sx11 · ≠动 Stage2 · ≠改产量。

## 不动确认

afk.tiers / daily_kills · EmberUpgradeRules · Stage2 三开关 / ×0.97 · gate_daily · 日帽数值 / S40–S49 · K3 · 关观察 · 切分支 · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D416 短征选页挂仓差薄抽 **PASS**（I 真数同屏 + Open 半行 + 满态去工坊花 CTA 静态保留）。

---

*D416Spot · PASS · tip `aebfc1c9` · 证据 `/workspace/tmp/d416-short-mat-gap/` · ≠关观察。*
