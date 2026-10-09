本号只读 · ≠关观察 · ≠开 K3

> **路径说明：** 批 A 采纳 STATUS 已占  @ `7dc7b54d`；本文件为**另号只读预检实跑**结果。


# 余烬 · D342：Stage2 观察现态复跑（预检）· 只读

- **性质：只读预检 · 未满窗 · 未改 yml/开关/bv/jar · 未 reload 改真值 · 未关观察 · 未开 K3**
- DESIGN：[`DESIGN-ember-six-slot-observe-state-check-2026-10-09.md`](../design/DESIGN-ember-six-slot-observe-state-check-2026-10-09.md) §2 · tip **`76b8eb49`**
- 证据目录：`/workspace/tmp/stage2-state-check-20261009/`
- 执行：2026-10-09 **20:59:29–21:01** CST
- **结论：R1+R2 红 → 须回滚评估 · 仅可继续观察（未满窗；禁止签「观察结束」）· 本号不擅自拧回**

## 人话

开跑时（20:59）线上 `ember-v1.yml` 三开关仍为 true，但约 **21:00:03** 该文件与 `ember-v1-runs.yml` 被整份替换成与 git `src/main/resources` 同文：`gear.six_slot` 段消失（代码默认 false）、`balance_version` 回落到 **60**（期望 **62**）。本号全程只读、未拧回。R5 K3 仍关。满窗门槛未到。**停报总控，走回滚/恢复评估。**

## R0–R10

| ID | 结果 | 要点 | 证据 |
|----|------|------|------|
| **R0** | 预检 | 开始 20:59:29 CST；满窗门槛 ≥**2026-10-10 17:40 CST** → **未满窗**，只做预检 | `R0-timestamp.txt` |
| **R1** | **FAIL** | T1（20:59）`enabled/migrate/set_bonus=true`；T2（21:00+）文件缺 `gear.six_slot`（≡默认 false）。mtime/birth=21:00:03，md5 与 src 同文。本号未改 | `R1-FAIL-overwrite.txt` · `R1-R5-six-slot-block.txt` · `forensic-d335-backup-ember-v1.yml` |
| **R2** | **FAIL** | 现网 `ember-v1-runs.yml` **`balance_version: 60`** ≠ **62**；与 plugins/、git src 同 md5。D325 曾只改线上至 62（未进 git） | `R2-FAIL-bv60.txt` |
| **R3** | PASS | `SET_BONUS_TAKEN_MULT = 0.97` 未改；近期 STATUS 无「改成 ×0.9x」记录 | `R3-x097-confirm.txt` |
| **R4** | 有记录 | play jar **`1.65.101-d335.local`** sha256 `2bd11b903c703163ec201075793b34b53c9d9e04a524a6ac85f2461bee9e850f`；PID **580488**；近 tip `985942be`（D335） | `R4-jar-summary.txt` |
| **R5** | PASS | 线上无 `k3_refine` 键（缺省/关）；D341 闸关 ≠开闸 | `R5-k3-confirm.txt` |
| **R6** | PASS | 近期 STATUS 均禁样本 R / Pack6；无未授权开 R / Pack6 上线记录 | `R6-verdict.txt` |
| **R7** | PASS | 资产·减伤·迁移阻塞申告未关闭数 **0**（STATUS 口径） | `R7-blocking-count.txt` |
| **R8** | PASS | D327 M1–M9 STATUS + `/workspace/tmp/d327-observe-spot.tgz` 可达；SUMMARY verdict PASS | `R8-d327-pointer.txt` |
| **R9** | 已预写 | 「关 Stage2 观察 ≠ 关 Stage1 enabled/migrate」 | `R9-parallel-declaration.txt` |
| **R10** | 见表 | §2.3 输出表如下 | `R10-output-table.txt` |

## §2.3 输出表（粘贴关观察号）

| 项 | 结果 | 证据文件 |
|----|------|----------|
| 三开关 enabled/migrate/set_bonus | **FAIL**（21:00:03 后文件缺段；20:59 快照曾 true/true/true） | `R1-FAIL-overwrite.txt` · `R1-R5-six-slot-block.txt` |
| bv | **FAIL** **60** ≠ 62 | `R2-FAIL-bv60.txt` |
| jar tip / sha | `1.65.101-d335.local` / `2bd11b903c…850f` / tip~`985942be` | `R4-jar-summary.txt` |
| k3_refine（或等价） | **关/缺省** | `R5-k3-confirm.txt` |
| 阻塞申告数 | **0** | `R7-blocking-count.txt` |
| D327 证据路径 | `/workspace/tmp/d327-observe-spot.tgz` + `STATUS-ember-six-slot-stage2-observe-spot-d327-2026-10-08.md` | `R8-d327-pointer.txt` |
| OPS jar 文案漂移旁注 | **有**（OPS 钉 `1.65.99-d325.local`；进程真源 `1.65.101-d335.local`） | `R4-jar-summary.txt` |
| 结论 | **须回滚评估**（R1+R2 红）· **仅可继续观察**（未满窗；禁止签结束） | 本 STATUS |

## 红项说明（停报 · 不拧回）

1. **R1**：观察期真源 `ember-v1.yml` 在预检窗内被替换为无 `gear.six_slot` 的 git 源文。若随后 `/corerpg reload` 或重启，三开关将按默认 **false** 生效，等于静默退出 Stage1/Stage2 观察。法医对照：`/workspace/tmp/d335-backup-20261009195542/ember-v1.yml` 与本号 `forensic-d335-backup-ember-v1.yml` 仍含三 true。
2. **R2**：`balance_version` 现网 **60**，与 D325/OPS/D327 M9 钉死的 **62** 不符；疑随同次 yml 覆盖从「仅线上 62」回落到 git 60。
3. **本号禁止**：改 yml、reload 改真值、关观察、开 K3、擅自恢复备份。

## 结束态（只读抄录）

| 项 | 值 |
|----|-----|
| 文件 `gear.six_slot` | **缺失**（21:00:03 后） |
| 文件 `balance_version` | **60** |
| play PID | 580488（自 19:55；本号未动） |
| jar | 1.65.101-d335.local · sha256 `2bd11b903c…850f` |
| k3_refine | 缺省/关 |
| 满窗 | 未到（≥2026-10-10 17:40 CST） |

## 给总控

- 红项 **R1+R2** → 按 DESIGN §2.4 / D338 回滚栏评估；**不得**勾「观察结束」
- 建议另号：**只读**确认 play 内存开关是否仍 true；**另签**后再决定是否从 `d335-backup` / D325 口径恢复 yml（bv62 + 三 true），并查清 21:00:03 覆盖来源（疑与 `feat/d341-k3-refine-offline` 或资源同步有关）
- 本号 **未**拧回、**未** reload、**未**关观察

*D342 测试岗 · 只读预检 · R1+R2 红停报 · tip DESIGN `76b8eb49`*
