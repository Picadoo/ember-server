白板经济模型未覆盖

# 余烬 · D332：D12-② 掉落甲分解实机抽测（批 A·M 执行）

**日期：** 2026-10-09（上海时间）  
**上游：** DESIGN [`DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md`](../design/DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md) @ `09a387a3` · 批 A·M 授权  
**号：** D332 · 余烬-测试岗 · 测号 `D332Dis`（uuid `405c6d70-cadc-371b-beda-ea6f55900a8e`）  
**结论：** **PASS**（A/B/D/E/F/G PASS；**C 已由 D337 关清** @ `2c999fab`）· **总控已签 D12-② PASS** · **D12-②-C PASS · 关清（D337）**  
**本号未改：** ×0.97 / set_bonus / enabled / migrate / bv / jar / 分解公式 / 价表 / K3 / ember_set（jar 被 **D333** 热换为 `1.65.100-d333.local`，非本号）

## 人话

线上测号通关攒到 2 件 `src=drop` 甲后：预览示意胚料 +0.1、确认后件 dismantled、零头 0→2；迁移/首通件拒绝分解且仍 active；护甲强化/精工/成色仍拒（仅分解可用）；audit findings=0。因 D333 jar 短重启打断续刷，未能凑满 10 件 T1 验「跨 10→整胚 +1」，C 记 NA 缺口。不挡 Stage2 观察日历。

## A–G 结果

| ID | 结果 | 证据 |
|----|------|------|
| **A** 掉落甲预览 | **PASS** | `26-A.json`：`胚料 +0.1（护甲按 0.1 × 掉落阶 记零头…现有零头 0.0）` + `[确认分解]` |
| **B** 确认分解 | **PASS** | `27-B.json` + `34-db-after.txt`：`ec1a7e46… head T1 drop → dismantled`；`p1_blank_tenths@all: 0→1`（后连拆靴子→2） |
| **C** 零头跨 10 | **NA→已关清（D337）** | 本窗仅 2×T1（D333 热换打断）；累计拆 2 件，零头=**2**，未跨 10。见 `29-C.json` / `33-blank-tenths`。**旁注：** D337 C0–C4 PASS @ `2c999fab` → **C 已关清**（STATUS [`STATUS-ember-six-slot-d12-c-cross10-d337-2026-10-09.md`](STATUS-ember-six-slot-d12-c-cross10-d337-2026-10-09.md)） |
| **D** 迁移拒绝 | **PASS** | `30-D.json`：`这件是迁移（不可分解）：只有副本里随机掉落的装备能分解`；DB migrate×4 仍 active |
| **E** 其它非 drop | **PASS** | `31-E.json`：quest 件 `这件是首通 / 开局赠送（不可分解）…`；quest×4 仍 active |
| **F** K0 养成拒 | **PASS** | `25-F.json`：enhance/refine/quality 均 `护甲随护符成长；成色和精工看掉落` |
| **G** audit | **PASS** | `32-G.json`：console `findings 0`；无 `ACTIVE_NOT_HELD` |

## 环境 / 结束态（本号未改开关）

| 项 | 值 |
|----|----|
| 测号 | `D332Dis` · 经 proxy `25565` → play |
| 开跑前 jar | `1.65.99-d325.local` · sha `4c273c5a…` · play PID 520664 |
| 结束 jar | **`1.65.100-d333.local`** · sha `4932eaa8…` · play PID **568465**（D333 热换，非本号） |
| bv | **62**（未改） |
| six_slot | enabled/migrate/set_bonus **true**（未改） |
| login/proxy/MariaDB | PID 520591 / 47617 / 47100（本号未动进程） |
| 证据目录 | `/workspace/tmp/d332-d12-armor-dismantle/` · 包 ` /workspace/tmp/d332-d12-armor-dismantle.tgz` |

## 建议

- **已签 D12-② 主路径 PASS**（A/B/D/E/F/G）；**C 已由 D337 关清**（跨 10→整胚 +1 · C0–C4 PASS @ `2c999fab`）。  
- **不挡** Stage2 观察绿出口日历；**不抢** K3。  
- 本号 **零改** 公式/价表/开关。

## 风险 / 注记

- 农场第 3 次通关中被 D333 kick（`jar hot-swap 短重启`）；续跑 rejoin 后完成分解验收。  
- `p1 give` / admin 发放未用作 drop 样件；样件来自 Q01 结算 `source=drop`。

## 总控签字栏（已签 · 2026-10-09）

| 项 | 签认 |
|----|------|
| **D12-② PASS** | [x] |
| A/B/D/E/F/G | [x] PASS（测试 @ `ad49850f`） |
| C 零头跨 10 | [x] **已关清（D337）**（原 NA；D337 C0–C4 PASS @ `2c999fab`） |
| 不挡 Stage2 观察绿出口日历 | [x] |
| 零改公式 / 价表 / set_bonus / bv / jar | [x] |
| 签字 / 日期 | **签字 总控 · 2026-10-09 · D332**（C 关清旁注 · D337） |

**总控批注（2026-10-09 · D332）：** 主路径 **PASS**（A/B/D/E/F/G）；当时 **C=NA** 不挡签。

**总控旁注（2026-10-09 · D337）：** **C 已关清**——D337 C0–C4 PASS @ `2c999fab`（STATUS [`STATUS-ember-six-slot-d12-c-cross10-d337-2026-10-09.md`](STATUS-ember-six-slot-d12-c-cross10-d337-2026-10-09.md)）；**总控签 D12-②-C PASS · 关清**。**不改** 分解公式 / 价表 / ×0.97 / set_bonus / bv。**不挡** 观察日历（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。

---

*D332 测试执行 · **总控已签 D12-② PASS** · **C 已由 D337 关清** · 白板经济模型未覆盖 · tip 旁 DESIGN `09a387a3` · 观察期薄窗。*
