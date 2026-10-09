# 状态 · D349：观察期过时 tip 批量旁注关闭包（批 A·M · docs-only · ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-observe-stale-tip-close-pack-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-observe-stale-tip-close-pack-need-design-2026-10-09.md) @ `5834dab8` · DESIGN [`DESIGN-ember-observe-stale-tip-close-pack-2026-10-09.md`](../design/DESIGN-ember-observe-stale-tip-close-pack-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D349** · 总控委派 executor 按 §2 模板 + §3.1–§3.4 清单落字 · **≠关观察** · **≠开 K3** · **≠薄抽** · **零 TrMenu / 零 live yml / 零开关**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · 本 STATUS · 目标 tip 旁注）· 三开关 / bv / ×0.97 / K3 / 关窗 **未动**

## 人话

大量 `next-hard-debt` tip 正文已写「已施工 / HOLD」，头却缺标准「旁注（已关）」——扫目录仍像待开窗。本窗只加旁注、改性质句，**不改史、不关观察、不开 K3、不跑薄抽**。D346 tip 已由 D348 关清，本包跳过。

## 关闭清单实绩

| 段 | 目标 | 结果 |
|----|------|------|
| §3.1 体验波 | 11 tip（D297–D307） | **已关** 11 |
| §3.2 样本/决策 docs | 4 tip（D308–D311） | **已关** 4 |
| §3.3 HOLD | 2 tip（天赋 / 灰印） | **已关** 2（含 **HOLD · 禁当上线**） |
| §3.4 六槽观察链 | D326 / D327 / D346 | **已关** 2；D346 **SKIP**（D348 已关清 · 勿重复） |
| **合计** | 19 关 + 1 SKIP | **关闭条数 = 19**（另关本 tip 自身） |

### 已关 tip 一览

| tip（`STATUS-ember-next-hard-debt-…`） | 指向 | 旁注要点 |
|----------------------------------------|------|----------|
| `sig-attune-need-design-2026-10-07` | D297 | 已施工 |
| `playfeel-telemetry-need-design-2026-10-07` | D298 | 已施工 |
| `refarm-short-feedback-need-design-2026-10-07` | D299 | 已施工 |
| `corridor-feel-need-design-2026-10-07` | D300 | 已施工 |
| `guard-skill-need-design-2026-10-07` | D301 | 已施工 |
| `abyss-feel-need-design-2026-10-07` | D302 | 已施工 |
| `weekly-raid-feel-need-design-2026-10-08` | D303 | 已施工 |
| `boss-telegraph-need-design-2026-10-08` | D304 | 已施工 |
| `hang-farm-identity-need-design-2026-10-08` | D305 | 已施工 |
| `hub-daily-routing-need-design-2026-10-08` | D306 | 已施工 |
| `workshop-menu-honesty-need-design-2026-10-08` | D307 | 已施工 |
| `sample-window-readiness-need-design-2026-10-08` | D308 | 已落仓 · ≠开样本 R |
| `offhand-merge-decision-need-design-2026-10-08` | D309 | 已落仓 |
| `sample-week-report-need-design-2026-10-08` | D310 | 已落仓 |
| `sample-week-report-script-need-design-2026-10-08` | D311 | 已落仓 |
| `talent-mech-pivot-need-design-2026-10-07` | HOLD T0'/T0'' ❌ | **HOLD · tip 已关 · 禁当上线** |
| `ash-imprint-need-design-2026-10-08` | HOLD T0/T0b ❌ | **HOLD · tip 已关 · 禁当上线** |
| `six-slot-stage2-observe-close-need-design-2026-10-08` | D326 | **≠绿出口已签 · ≠本号关观察** |
| `six-slot-k3-refine-need-design-2026-10-08` | D327 | **≠开闸 ≠部署 K3 live** |
| `six-slot-d346-gear-set-spot-need-design-2026-10-09` | D348 | **SKIP** · 本包勿重复旁注 |

**MISSING / 假关：** 无（glob `docs/status/STATUS-ember-next-hard-debt-*` 对齐清单，全部 FOUND）。

## C1–C4 自检

| ID | 结果 | 说明 |
|----|------|------|
| C1 | **PASS** | §3.1–§3.4 目标 tip（除 D346 SKIP）头均可扫到 `旁注（已关` |
| C2 | **PASS** | 天赋 / 灰印旁注含 **HOLD** + **禁当上线** |
| C3 | **PASS** | D326 旁注含 **≠绿出口已签 · ≠本号关观察**；D327 含 **≠开闸 ≠部署 K3 live** |
| C4 | **PASS** | 本号未改 TrMenu / CoreRpg live yml / 三开关 / bv / ×0.97；未跑薄抽；未开 K3；未关观察 |

## 本号改动清单

| 文件 | 改动 |
|------|------|
| 19× tip（§3.1–§3.4，除 D346） | 文首 `旁注（已关…）` + 改「本窗性质」/「硬规格」实态 |
| DESIGN `…-observe-stale-tip-close-pack-…` | STATUS→已批 A·M·D349；勾批 A；总控批注；变更记录 |
| tip `…-observe-stale-tip-close-pack-need-design-…` | 旁注已关 · 硬规格→已批 A·D349 |
| backlog `B-observe-stale-tip-close-pack` | → **已关 · 已批 A · 批 M · D349** |
| 本 STATUS | 关闭条数 / SKIP / C1–C4 |
| TrMenu / live yml / 三开关 / bv / ×0.97 / K3 | **未动** |

## 不动与否决（复核）

- 绿出口日历仍 ≥**2026-10-10 17:40 CST**；**本号不关观察**  
- K3 live / 样本 R / Pack6 / 天赋·灰印续跑 / 改 ×0.97 / hub_legacy：**否决**  
- 再交已 PASS 薄抽清单：**硬禁**

*D349 · 过时 tip 旁注关闭包 · 已关 19 + SKIP 1 · C1–C4 PASS · ≠关观察。*
