# 状态 · D328：六槽 K3 同部位熔炼 T0‴ 离线门禁汇总（tools/docs only）

**日期：** 2026-10-09（上海时间）  
**上游：** D327 已批 A · 批 M · [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md) @ `c4f534b4` · tip [`STATUS-ember-next-hard-debt-six-slot-k3-refine-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-six-slot-k3-refine-need-design-2026-10-08.md) · D326 观察批 M · D325 live bv62  
**证据：** `/workspace/tmp/d328/`（`run.log` 21:41 ALL DONE）· 门禁表见 `out/` · **p1sim `k3_merge` 建模 @ `113f8ae8` · tools/p1sim**（本 STATUS 不抢写 sim 文件）  
**版本：** **docs-only 本号** · jar / 线上 yml / bv / `set_bonus` **未动**（仍 bv62 · `set_bonus=true` · `1.65.99-d325`）· **零部署** · **总控已签 PASS** · **PASS 不自动施工**

## 人话

同部位熔炼（K3）在离线 p1sim 里**全部门禁过线**：动态前线相对 F 基线 21/21 几乎贴零；刷图 / 完整 W30 都在 ±0.5 周内；团本 9/9、挂机 21/21；团本池精工差从 T0″ 的中位约 **+2.0** 收到约 **+1.0**，H 中位约 **−0.46% → −0.30%**，尾部未恶化越线。材料少分解导致白板中位数略降（316→306），属设计预期。线上 jar/yml/开关**未动**。

**总控已签「T0‴ PASS」。** PASS **不**自动施工；施工/部署仍须 Stage2 绿出口（不早于 **2026-10-10 17:40 CST** + 观察另签）或总控另签「观察期并行施工」。

## 门禁（K3 · 容忍带同 T0″：点估 ±2pp 或 CI 伸进；W30 ±0.5 周）

| 门禁 | 关键数字 | 判定 |
|------|----------|------|
| **G0** 自检抽查 | `g0-selfcheck.txt`：**0 failed**（含 D328 `k3_merge` 四条断言） | **PASS** |
| **G2** 动态 21 格 | **有效对照** `g2-ci-vs-F.md`：K3 vs F **点估 21/21 · 含噪声 21/21** · 最大 CI 半宽 **0.08 pp**（早期 `g2-ci.md` 曾因 merge pickle 失败一次，后已用完整 `all-ab.pkl` 重出 vs-F 表；`g2-ci.md` 亦有 K3/F 各自 vs base 21/21） | **PASS** |
| **G3** 刷图 W30 | K3 base **Δ +0.00** [−0.07,+0.09] · rot **Δ +0.02** [−0.06,+0.11]（n=1200；相对 2 槽 base） | **PASS** |
| **G4** 完整 W30 | K3−F 各路线：深渊+周目标 **−0.15** · P2-2 **+0.27** · 团本+周目标 **−0.02** · 轮换+团本 **+0.05** · P2-1 **0.00** · 无轮换 **0.00**（`growthrun.py weeks` 于既有 `p2-K3.md`/`p2-F.md`；全 ∈ ±0.5） | **PASS** |
| **G5/G6** 抽查 | `g56-K3.txt` / `g56-F.txt`：G6 **101376/101376** 逐位相等 · G5 B drift **0** | **PASS** |
| **G8 团本** | `g8-raid-K3.md`：**9/9** 点估 ±2pp · 含噪声 9/9 · 整段 CI 在 ±2 外 **0** 格 · 最差约 R03 3 人 **−1.3** [−2.2,−0.4] | **PASS** |
| **G8 挂机** | `g8-afk-K3.md`：**21/21** · 最大 \|Δ\| **+0.5** @ 0.5 Q06 | **PASS** |
| **G10** 落后分布 | 精工差（护符−甲均）：均值 **+0.78** / 中位 **+1.00**（T0″ F：**+1.77 / +2.00** → **已收窄**）；H 中位 **−0.30%**（T0″ F **−0.46%**）；H 尾 ≤−2% **4.0%** · ≤−4% **3.6%**（与 T0″ F 同，未恶化越线）；有效阶/强化差仍全 0 | **PASS** |
| 白板经济（注明） | `blank-probe.txt`：K3 merges 中位 **5** / 均值 **4.62** · blank 中位 **306**（F **316**）· 甲精工均值 **1.85**（F **0.91**）——材料少分解、精工抬升，符合 §2 | 注明 · 非挡签 |

> **总判：** 必抽 G2/G3/G4/G8/G10 全 **PASS**；G0/G5/G6 抽查 **PASS**。**总控已签 T0‴ PASS。**

## 证据路径

| 项 | 路径 |
|----|------|
| 根目录 | `/workspace/tmp/d328/`（`run.sh` · `run.log` · `arms.json` · `k3.json` · `rules-bv60.json`） |
| 门禁输出 | `/workspace/tmp/d328/out/`（`g0-selfcheck.txt` · `g2-ci.md` · **`g2-ci-vs-F.md`** · `g56-*.txt` · `g8-raid-K3.md` · `g8-afk-K3.md` · `g10-K3.md` · `w30-*-*.txt` · `p2-K3.md` · `p2-F.md` · `blank-probe.txt`） |
| 臂定义 | `arms.json`：F = follow=all；K3 = F + `k3_merge:true` |
| p1sim 建模 | **@ `113f8ae8`** · `tools/p1sim`（`sim(D328): p1sim k3_merge 建模`；本号 STATUS **不** commit sim） |

## 本号改动

| 文件 | 改动 |
|------|------|
| 本 STATUS | 新增 · T0‴ 门禁汇总 |
| `docs/design/design-ember-content-backlog.md` | `B-six-slot-k3-same-slot-refine` 指针 → T0‴ 报告已交、待总控签 |
| DESIGN 变更记录（可选一行） | 指向本 STATUS；**下一步仍是总控签** |
| jar / 玩法 yml / 开关 / bv / set_bonus | **未动** |
| `tools/p1sim/**` / `CoreRpg/src/**` | **本号不碰**（建模已由插件 @ `113f8ae8`） |

## 总控签字栏（已签）

| 项 | 签认 |
|----|------|
| **T0‴ PASS（K3）** → 可另号施工（仍须绿出口闸或另签并行） | [x] |
| T0‴ FAIL → HOLD 施工；可改参数重跑（仍零价、仍不碰成色/族/阶） | [ ] |
| 签字 / 日期 | **签字 总控 · 2026-10-09 D328** |

**闸钉死（DESIGN §5.1）：**

```
T0‴ PASS（本栏总控签）
  → 【闸】Stage2 绿出口已签（不早于 2026-10-10 17:40 CST + 观察另签）
        或 总控另签「观察期并行施工」
  → 另号插件施工（开关默认关）+ 菜单
```

**PASS 不自动施工；施工等 Stage2 绿出口≥2026-10-10 17:40 CST + 观察另签，或另签并行。本号不部署、不另签并行。**

## 不动

样本 R 全表 · Pack6 · 天赋 / 灰印 HOLD · 改 ×0.97 / 拧 set_bonus · 动 F / 护符 ×1.0 · 改价表 · 熔炼改成色/族/阶 · 观察期部署 K3 / 写熔炼进 live · 重开 Stage1/2 施工本体

---

*D328 T0‴ 离线汇总 · 证据 `/workspace/tmp/d328/` · **总控已签 PASS** · 施工等 Stage2 绿出口或另签 · 上游 D327 批 M · D316/D317 T0″ F 基线。*
