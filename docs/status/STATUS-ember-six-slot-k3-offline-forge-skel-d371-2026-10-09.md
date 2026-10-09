# 状态 · D371：K3 离线 Forge/服务骨架指针（主仓真源 · ≠开闸 ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** 插件闸关离线续施工交付 · wt tip `e8a7e2c2` · 代码 `859750e3` · 19/19 PASS · wt STATUS `docs/status/STATUS-ember-six-slot-k3-offline-forge-skel-d341-2026-10-09.md`（路径仅在 wt；仅隔离树描述）· 主仓观察 docs 真源规则 D368 · 预备 D359 · 闸页 D341 · 禁误读 D362  
**裁决：** **主仓指针已落 · D371** · 记录 wt 离线 Forge/服务骨架就位 · **明文 ≠ 开闸 ≠ 关观察 ≠ 开 `k3_refine` ≠ 装 play ≠ 改 live ≠ 合并 wt 进 main ≠ 开 R**  
**版本：** **docs-only**（本 STATUS · OPS §1.4 半行 · backlog 半行指针）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / live yml / 日历门槛 **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

插件在隔离 worktree 把同部位熔炼接到可测的 preview / commitIntent 骨架了（单测 19/19）。本号只在**主仓**钉指针——**不开闸、不装 play、不关观察、不合并分支。**

## 指针（本机核验 · 2026-10-09）

| 项 | 值 |
|----|-----|
| worktree | `/workspace/minecraft-wt-d341` |
| 分支 | `feat/d341-k3-refine-offline` |
| tip（含 STATUS） | `e8a7e2c2`（`docs(D341): STATUS tip → 859750e3…`） |
| 代码 tip | `859750e3`（`feat(D341): K3 熔炼离线 Forge/服务骨架…`） |
| 单测 | Rules 13 + Service 6 = **19/19 PASS**（插件交付口径；本号未重跑） |
| 主仓 | `/workspace/minecraft` @ `main` · **未**为 K3 切分支 · **未**合并 wt |
| live | Stage2 三开关 / bv62 **未动**（本号未碰 `ember-v1*.yml`） |

## 接线摘要（只读复述 · 真源在 wt）

| 项 | 口径 |
|----|------|
| 服务 | `EmberK3RefineService.preview` / `commitIntent` |
| Forge | `EmberForgeService.k3Preview` / `k3CommitIntent`（**未**挂入 `cmd()`） |
| 开关 | `gear.six_slot.k3_refine` 默认关；关=拒 |
| 未做 | 玩家命令 · TrMenu live · play 热更 · `commitTxn` 挂接 |

## 闸态

| 项 | 口径 |
|----|------|
| 当前 | **闸关**（D341 §2.1 路径 A/B 本号仍未满足） |
| 「已就位 / PASS」 | **≠开闸**（D362） |
| 开闸后另号 | 再挂 `commitTxn` + TrMenu；本号不授权 |
| 绿出口日历 | ≥**2026-10-10 17:40 CST**（观察续；本号 ≠ 关窗） |

## 确认（钉死）

- [x] 观察期 docs 真源 = 主仓（D368）；本 STATUS 写在 main
- [x] **未**装 play / 热更现网 jar
- [x] **未**开 `k3_refine`
- [x] **未**改 set_bonus / ×0.97 / bv / live yml
- [x] **未**合并 `feat/d341-k3-refine-offline` 进 main
- [x] **未**关观察 / 勾选绿出口 §2.4
- [x] wt→主仓 **未**拷 resources 盖 live

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| 本 STATUS | 主仓指针：tip/sha · 19/19 · wt 路径 · 未装 play/未开闸 · 开闸后另号 |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：K3 离线 Forge 骨架已就位（wt `e8a7e2c2`）→ D371；仍 ≠开闸 |
| `design-ember-content-backlog.md` | `B-six-slot-k3-offline-prep` +半行指针 D371（仍 ≠开闸） |
| `STATUS-ember-six-slot-k3-offline-prep-d359-2026-10-09.md` | +旁注指针 D371（仍 ≠开闸） |
| jar / 三开关 / bv / ×0.97 / TrMenu / live | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` / MM SavedData / 未授权 ember-v1 | **未 stage / 未碰** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs 路径 |
| C3 | **PASS** | 显式 `git add` 各 docs 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live / resources；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

开闸 · 开 `k3_refine` · 关观察 · 装 play · 合并 wt→main · 改 live · 改 ×0.97 / ×0.1 · 开 R · 空转附录 · 脏 runtime stage · 主仓为 K3 切分支

---

*D371 主仓指针 · wt tip `e8a7e2c2` · 代码 `859750e3` · 19/19 · ≠开闸≠关观察≠装 play≠改 live · 零玩法。*
