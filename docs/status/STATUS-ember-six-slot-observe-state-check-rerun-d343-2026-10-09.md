本号只读 · ≠关观察 · ≠开 K3

# 余烬 · D343：Stage2 观察现态复跑（预检）· 只读 · 总控二次恢复后重采样

- **性质：只读预检 · 未满窗 · 未改 yml/开关/bv/jar · 未 reload 改真值 · 未关观察 · 未开 K3 · 未 stash/reset/checkout/-- . · 未再切分支 · 未擅自从备份 cp**
- DESIGN：[`DESIGN-ember-six-slot-observe-state-check-2026-10-09.md`](../design/DESIGN-ember-six-slot-observe-state-check-2026-10-09.md) §2 · tip **`7dc7b54d`**
- 证据目录：`/workspace/tmp/d343-r0r10/`
- 执行：2026-10-09 **21:05:14–21:06:30 CST**（最终 R1/R2 重采样 **21:06:20–21:06:30**，+10s 监视未再冲）
- **结论：预检现态 OK · 仅可继续观察（未满窗 ≥2026-10-10 17:40 CST；禁止签「观察结束」）**

## 作废口径

| 文件 / tip | 新口径 |
|------------|--------|
| [`STATUS-ember-six-slot-observe-state-check-run-d342-2026-10-09.md`](STATUS-ember-six-slot-observe-state-check-run-d342-2026-10-09.md) @ `a3fbf2b5`/`60090a7a` | **作废为「覆盖窗口证据 / 事故快照」**；历史正文保留；以本号为准 |
| 开跑前约 21:03 快检（父代理提示 live 可能仍无 six_slot / bv60） | **不作本号结论**；属被冲窗口 |

## 人话

总控称 D342 红因（约 21:00 src 整份覆盖）已二次恢复。本号按批 A 只读重跑 R0–R10。跑中约 **21:05:56** 曾见瞬时冲红（`ember-v1.yml` md5 `a46e6f4b…`、bv60、缺 six_slot），本号**未**自行 cp 备份。总控覆盖指令要求重采样后：最终 **21:06:20–21:06:30** 连续读盘稳定——三开关 true、255 行、md5=`48b96322…`（=d335-backup）、bv**62**。未满窗，只签「预检现态 OK，满窗后再签」。

## R0–R10

| ID | 结果 | 要点 | 证据 |
|----|------|------|------|
| **R0** | 预检 | 开始 21:05:14 CST；满窗门槛 ≥**2026-10-10 17:40 CST** → **未满窗** | `R0-timestamp.txt` |
| **R1** | **PASS** | 最终重采样：`enabled/migrate/set_bonus=true`；255 行；md5 `48b963229dd261d0036cc8e7e65c3f8e`（=d335-backup / d343-pin）；mtime 文件戳 19:42:06（内容已恢复）；+10s 未冲 | `R1-switches.txt` · `R1-R2-RESAMPLE-*.txt` · `R1-forensic-vs-backup.txt` |
| **R2** | **PASS** | 最终重采样：`balance_version: 62`；md5 `2fa15766c0aa95f13a6ae14f62938331`；mtime 21:06:17；+10s 未冲 | `R2-bv.txt` · `R1-R2-RESAMPLE-*.txt` |
| **R3** | PASS | `SET_BONUS_TAKEN_MULT = 0.97` 未改；无「改成 ×0.9x」STATUS | `R3-verdict.txt` · `R3-x097.txt` |
| **R4** | 有记录 | play jar **`1.65.101-d335.local`** sha256 `2bd11b903c703163ec201075793b34b53c9d9e04a524a6ac85f2461bee9e850f`；PID **580488**；近 tip `985942be`（D335） | `R4-jar.txt` |
| **R5** | PASS | 线上无 `k3_refine` 键（缺省/关）；D341 闸关 | `R5-k3.txt` |
| **R6** | PASS | 近期 STATUS 均禁样本 R / Pack6；无未授权开 R / Pack6 上线 | `R6-sample-r.txt` |
| **R7** | PASS | 资产·减伤·迁移阻塞申告未关闭数 **0** | `R7-blocking.txt` |
| **R8** | PASS | D327 M1–M9 STATUS + `/workspace/tmp/d327-observe-spot.tgz` 可达（`54638e00`） | `R8-d327.txt` |
| **R9** | 已预写 | 「关 Stage2 观察 ≠ 关 Stage1 enabled/migrate」 | `R9-parallel-declaration.txt` |
| **R10** | 见表 | §2.3 输出表如下 | `R10-output-table.txt` |

## §2.3 输出表（粘贴关观察号）

| 项 | 结果 | 证据文件 |
|----|------|----------|
| 三开关 enabled/migrate/set_bonus | **PASS** true/true/true · 255 行 · md5 `48b96322…` | `R1-R2-RESAMPLE-*.txt` |
| bv | **PASS** **62** | 同上 |
| jar tip / sha | `1.65.101-d335.local` / `2bd11b903c…850f` / tip~`985942be` | `R4-jar.txt` |
| k3_refine（或等价） | **关/缺省** | `R5-k3.txt` |
| 阻塞申告数 | **0** | `R7-blocking.txt` |
| D327 证据路径 | `/workspace/tmp/d327-observe-spot.tgz` + `STATUS-…-d327-2026-10-08.md` | `R8-d327.txt` |
| OPS jar 文案漂移旁注 | **有**（OPS 钉 `1.65.99-d325.local`；进程真源 `1.65.101-d335.local`） | `R4-jar.txt` |
| 结论 | **预检现态 OK，满窗后再签** · **仅可继续观察**（未满窗；禁止签结束） | 本 STATUS |

## 跑中冲红旁注（不停本号最终结论，但须给总控）

约 **21:05:56 CST** 瞬时：`ember-v1.yml` md5→`a46e6f4b21d067fd88a990a7dcadc6cd`、缺 `six_slot`；`balance_version`→**60**。随后总控恢复；最终重采样已绿。证据：`WATCH-no-overwrite.txt` · `R1-R2-RED-forensic-rerun.txt`（窗口快照）。**根因仍可能复发**（疑 git/src 同步盖 live）；建议总控继续钉防冲（`7c9b8ddd` 待批设计）。本号未拧回。

## 结束态（只读抄录 · 21:06:30）

| 项 | 值 |
|----|-----|
| 分支 | `main` @ `7c9b8ddd` |
| `gear.six_slot` | enabled/migrate/set_bonus **true/true/true** · 255 行 |
| md5 `ember-v1.yml` | `48b963229dd261d0036cc8e7e65c3f8e` |
| `balance_version` | **62** |
| play PID | 580488 |
| jar | 1.65.101-d335.local · sha256 `2bd11b903c…850f` |
| k3_refine | 缺省/关 |
| 满窗 | 未到（≥2026-10-10 17:40 CST） |

## 给总控

- **预检现态 OK**；**未满窗 → 仅可继续观察 · 禁止签「观察结束」**
- D342 红停报已旁注作废；以本号为准
- 观察维持；三开关/bv62/jar 本号未改
- 防冲债：跑中仍见瞬时盖写，需跟 `7c9b8ddd` 设计

*D343 测试岗 · 只读预检 · R0–R10 重采样 PASS · tip DESIGN `7dc7b54d` · ≠关观察*
