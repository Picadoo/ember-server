# 状态 · D415 派测：挂机战况挂仓差（薄抽 · 菜单）

**日期：** 2026-10-10（上海时间）  
**号：** D415Spot · 证据 `/workspace/tmp/d415-afk-mat-gap/`  
**上游：** 施工 tip [`85e8d28a`](https://github.com/Picadoo/ember-server/commit/85e8d28a) · DESIGN [`DESIGN-ember-afk-status-mat-gap-2026-10-10.md`](../design/DESIGN-ember-afk-status-mat-gap-2026-10-10.md) · 挂盘 STATUS [`STATUS-ember-afk-status-mat-gap-d415-2026-10-10.md`](STATUS-ember-afk-status-mat-gap-d415-2026-10-10.md) · D404 vault/gap live  
**范围：** `ember_p1_afk` I 仓内四材 + enhance1/upgrade_t2/refine1 还差 · Open「战况可看仓内还差」· 去哪花半行诚实 · remain/next_farm/层差/短征剩余保留 · **零 jar** · **≠关观察** · **未改** daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily · **未切分支** · **未反复停服**（并行 D414 占 play 时仅菜单/PAPI）  
**总结果：** **PASS**

## 人话

打开挂机庭：Open 半行「战况可看仓内还差」；战况 I 同屏真仓「碎/骨/核/胚」与「强化+1 碎差 / 升阶T2 / 精工0→1」；remain / 下一层养签 / 层差一览 / 短征剩余有奖都还在。「去哪花」半行「战况/速览均可看仓差」。空仓碎0→还差4；存入碎片后战况即时碎4·碎差0·升阶碎差56。产量/Rules/Stage2/gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S1 | 静态：I 挂 vault_* + recipe_gap_*；Open 仓差半行；去哪花诚实；remain/next_farm/sx_left/层差仍在 | **PASS** | `10-menu-static.json` |
| V1 | I 仓内四材数字解析（非字面 `%corerpg_`）· 与 `/papi parse me` 同数 | **PASS** | `30-afk-window.json` · `20-papi-base.json` · `31-I-parsed.json` |
| V2 | I 还差 enhance1 + upgrade_t2 + refine1；空仓 4 / 碎差60·核差12·胚差6 / 胚差3·骨差5 | **PASS** | `31-I-parsed.json` |
| V2Δ | `ni give` + `/corerpg p1 stash` 后 shard 0→4 · enhance1 4→0 · 升阶碎差56；菜单同屏 live | **PASS** | `43-inject-hard.json` |
| V3 | Open tell「战况可看仓内还差」；S 去哪花「战况/速览均可看仓差」 | **PASS** | `30-afk-window.json` |
| V6 | remain_line / next_farm / sx_day_left_sum / 层差一览 仍在 | **PASS** | `31-I-parsed.json` |
| V4 | `%corerpg_gate_daily%` = `no` | **PASS** | `60-gate-daily.json` |
| V5 | six_slot 三开关 true · daily_kills=2400 · afk.tiers md5 未变 · jar 未换 | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- 施工 tip `85e8d28a` · live jar `1.65.121-d414.local`（零 jar 本号；含 D404 键）sha256 `cbcfb4c0…`  
- 菜单 md5 `af5b1c181b200b5de8ce60fa1e73de7c`（`ember_p1_afk.yml`）  
- 测号 `D415AfkGap2` · 起止 **2026-10-10 08:52:44～09:00:08 CST**  
- 空仓同屏：仓内碎0·骨0·核0·胚0 · 强化+1 碎差4 · 升阶T2 碎差60·核差12·胚差6 · 精工 胚差3·骨差5  
- 注入后同屏：仓内碎4 · 碎差0 · 升阶 碎差56·核差12·胚差6（`43-inject-hard.json`）  
- Open hits：战况可看仓内还差 · 抬头可见还差/约满 · 战况有下一层养签  
- 汇总 `99-summary.json`：25/25 PASS

## 旁注

- 并行 D414 sx10 全链路曾占 play；本号仅菜单/PAPI，**未停服**。  
- 一键存入正确命令为 `/corerpg p1 stash`（`vault stash` 无存入回执）。  
- ≠关观察 · ≠抬日表 · ≠交 sx11 · ≠动 Stage2。

## 不动确认

afk.tiers / daily_kills · EmberUpgradeRules（jar 类 md5 `821e4247f1b3`）· Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D415 挂机战况挂仓差薄抽 **PASS**（I 真数同屏 + Open/去哪花半行 + 注入后 live）。

---

*D415Spot · PASS · tip `85e8d28a` · 证据 `/workspace/tmp/d415-afk-mat-gap/` · ≠关观察。*
