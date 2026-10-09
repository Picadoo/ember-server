本号值班只读 · ≠关观察 · ≠改 live · ≠开闸

# 状态 · D364：Stage2 观察值班只读核对 · 首班实跑（只读 · ≠关观察）

- **性质：班次只读核对 · 未满窗 · 未改 yml/开关/bv/jar · 未 reload 改真值 · 未关观察 · 未开闸 · 未开 K3 · 未 stash/reset/checkout -- . · 未切分支 · 未擅自拧 live**
- DESIGN：[`DESIGN-ember-six-slot-observe-duty-readonly-check-2026-10-09.md`](../design/DESIGN-ember-six-slot-observe-duty-readonly-check-2026-10-09.md) §2.3 D1–D10 · tip **`0e75b7aa`**
- 清单 STATUS：[`STATUS-ember-six-slot-observe-duty-readonly-check-d364-2026-10-09.md`](STATUS-ember-six-slot-observe-duty-readonly-check-d364-2026-10-09.md)
- 证据目录：`/workspace/tmp/stage2-duty-20261009-2227/`
- 执行：2026-10-09 **22:28:05–22:28:50 CST**
- **结论：班次 OK · 观察续**（未满窗 ≥**2026-10-10 17:40 CST**；禁止签「观察结束」；本号 ≠ 关观察授权）

## 人话

首班按 D364 薄清单只读扫了一遍：三开关仍 true、bv 仍 62、×0.97 未动、没人提前勾关窗、K3 仍 offline、live 没被脏盖。全绿只说明观察仍维持，满窗另号再签。

## D1–D10

| ID | 结果 | 要点 | 证据 |
|----|------|------|------|
| **D1** 三开关 | **绿** | live `gear.six_slot`：`enabled/migrate/set_bonus` **均为 true**；255 行 | `d1-d2-switches-bv.txt` · `d1-d2-d3-d5-precise.txt` |
| **D2** bv | **绿** | `ember-v1-runs.yml` `balance_version: **62**` | 同上 |
| **D3** ×0.97 / set_bonus | **绿** | 源码 `SET_BONUS_TAKEN_MULT = 0.97`（`EmberSixSlot.java`）；`set_bonus=true`；无未授权「改倍率/关 set_bonus」STATUS；play jar `1.65.101-d335.local` | `d3-jar-mult.txt` · `d3-jar-version.txt` · `d3-d6-formula-and-live.txt` |
| **D4** 未提前关窗 | **绿** | D338 DESIGN §2.4「观察结束」仍 `[ ]` 未勾；日历门槛仍 ≥**2026-10-10 17:40 CST**；本班不交关窗；现 **22:28 CST** 未满窗 | `d4-calendar-detail.txt` · `d4-d5-calendar-k3.txt` |
| **D5** K3 offline | **绿** | live yml **无** `k3_refine` 键（缺省/关）；K3 仅离线 worktree `feat/d341-k3-refine-offline`；主仓 `main` | `d5-k3-detail.txt` · `d4-d5-calendar-k3.txt` |
| **D6** live 未脏盖 | **绿** | live md5 `48b963229dd261d0036cc8e7e65c3f8e`（= d335 备份）；含 `six_slot`；分支 `main`；本号未 stage 脏 runtime（`ladder.yml` / `p1-six/` 等仍脏但不碰） | `d6-dirty-cover.txt` · `d3-d6-formula-and-live.txt` |
| **D7** 白板注记 | **绿 / NA** | 本班未写分解/审计类报告 → NA（指针 D361 仍有效） | `d7-d8-d9.txt` · `SUMMARY.txt` |
| **D8** 禁误读 | **绿** | 本 STATUS 明文「班次 OK · 观察续」；≠关观察 ≠开闸 ≠勾 §2.4；未满窗不暗示可签结束（对照 D362/D363） | `d7-d8-d9.txt` · 本文件首行 |
| **D9** 未开 R/Pack6 | **绿** | 近期 STATUS/提交均禁开样本 R / Pack6；无未授权上线记录 | `d7-d8-d9.txt` |
| **D10** 汇总 | 见表 | §2.4 输出表如下 | `SUMMARY.txt` |

## §2.4 输出表（粘贴值班 STATUS）

| 项 | 结果（绿/红/未查） | 备注 / 证据 |
|----|-------------------|-------------|
| D1 三开关 | **绿** | enabled/migrate/set_bonus = true/true/true · `d1-d2-*.txt` |
| D2 bv | **绿** | **62** · `ember-v1-runs.yml` |
| D3 ×0.97 / set_bonus | **绿** | `SET_BONUS_TAKEN_MULT=0.97` · set_bonus=true · jar `1.65.101-d335.local` |
| D4 未提前关窗 | **绿** | §2.4 未勾 · 门槛未改 · 未满窗 · 本班不交关窗 |
| D5 K3 offline | **绿** | live 缺省/关 · 仅 offline worktree |
| D6 live 未脏盖 | **绿** | md5=`48b96322…` = d335 备份 · six_slot 在 · main |
| D7 白板注记（若本班有相关报告） | **绿 / NA** | 本班无相关报告 |
| D8 禁误读（若本班写观察旁注） | **绿** | 明文观察续 · ≠关观察≠开闸 |
| D9 未开 R/Pack6 | **绿** | 无未授权开 R / Pack6 |
| **结论** | **班次 OK · 观察续** | 未满窗（≥2026-10-10 17:40 CST）；**≠**本号关窗；红项无 |

## 结束态（只读抄录 · 22:28:50 CST）

| 项 | 值 |
|----|-----|
| 分支 | `main` @ `0e75b7aa`（开跑 tip） |
| `gear.six_slot` | enabled/migrate/set_bonus **true/true/true** · 255 行 |
| md5 `ember-v1.yml` | `48b963229dd261d0036cc8e7e65c3f8e` |
| `balance_version` | **62** |
| jar | `1.65.101-d335.local` · sha256 `2bd11b903c703163ec201075793b34b53c9d9e04a524a6ac85f2461bee9e850f` |
| k3_refine | 缺省/关（offline） |
| 满窗 | **未到**（≥2026-10-10 17:40 CST） |

## 给总控

- **班次 OK · 观察续**；未满窗 → **禁止**签「观察结束」
- 三开关 / bv62 / ×0.97 / jar / K3 offline / live 未脏盖 · 本号未改
- 脏 runtime（`ladder.yml` / `calamity-state.yml` / `p1-six/` 等）仍在工作区，本号**未 stage**
- 下一班同清单再扫；满窗另号走 D338 §2.4 + D342 R0–R10

*D364 测试岗 · 首班只读 D1–D10 PASS · tip `0e75b7aa` · 证据 `/workspace/tmp/stage2-duty-20261009-2227/` · ≠关观察 ≠改 live ≠开闸*
