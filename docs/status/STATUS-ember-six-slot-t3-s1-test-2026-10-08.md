# 余烬 · 六槽 T3 · S1 隔离测服报告（余烬-测试 · 2026-10-08）

- 计划：`docs/status/PLAN-ember-six-slot-t3-s1.md`（`765dca18` / tip 收讫 `191bc490`）
- tip / jar：`d4ab102c` · `CoreRpg-1.65.98-d322.local.jar` · sha256 `abc96aca51517216afa25d8ee14649acc55aed5d6b569826dba2308240139661`
- 设计：`DESIGN-ember-six-slot-t3-rollout-2026-10-08.md`（D322 ⑥ 仅 S1）
- 环境：`/workspace/tmp/t3-s1/` · Paper `127.0.0.1:25577` · 独立 Waterfall `127.0.0.1:25576` · 临时 mariadbd `3317`/`ember_t3` · 堆 ≤1.5G
- 执行：2026-10-08 15:04–15:37 CST
- **结论：§1.3 主出口 + H8 + 待领关开关 + ACTIVE_IN_STASH + 迁移件拒分解 均 PASS；D12 结算掉甲 / 掉落甲分解 未在时限内实机 PASS（DP 进本/削弱/房间触发已通）。不变量 ①② 无违例。线上 4 服务 PID 未变。**
- **建议：暂不签「⑦ 部署」** —— 等 D12 结算 +1 甲与 `src=drop` 分解补跑 PASS 后再签。

## 1 预检

| 项 | 结果 |
|---|---|
| tip ≥ `4d82b518` | ✅ `d4ab102c`（ancestor_ok） |
| jar sha | ✅ `abc96aca…` 与 `/workspace/tmp/d322/` 一致；日志 `Enabling CoreRpg v1.65.98-d322.local` |
| bv 测服 | ✅ `ember-v1-runs.yml` → `balance_version: 61`（仅测服） |
| 两开关 | 起步 false → S1 同开 true（仅测服）；线上未动 |
| 代理 | 独立 Waterfall，**未改** `proxy-runtime/` |
| DP | 已装；预缓存含 EmberQ01 等 |
| 线上快照 | `snap/live-before.sha256` / pid / 只读 DB 计数（cr_p1_item=82 active） |

## 2 用例结果

| 编号 | 结果 | 关键证据 |
|---|---|---|
| **S1-M 迁移主流程** | **PASS** | t3a/t3b/t3c：进枢纽自动迁移；`p1_six_mig=1`；H/D 与迁移前逐位（例 t3a `B=12.00 H=40.00 D=2`）；journal 空；日志 `DB_PENDING`→`ROLLED_FORWARD`。`ev/S1-M/` |
| **S1-H2 幂等** | **PASS** | `armor mig t3a` → `ALREADY`；DB migrate 行不增。`ev/S1-H2/` |
| **S1-C 待领·开关全关可领** | **PASS** | 两开关 false 后 t3d：`已领取 1 件`，`待领=0`。`ev/S1-C/claim-off.json` |
| **S1-A audit ACTIVE_IN_STASH** | **PASS** | t3d 穿 P1 头盔后迁移：`ACTIVE_IN_STASH e66d6047… · 在六槽待领，勿 audit restore`；`audit restore` → `该 uid 在六槽待领里，勿补发`。无 `ACTIVE_NOT_HELD`。`ev/S1-A/t3d-audit.json` |
| **S1-G8 开关关** | **PASS** | `护甲功能尚未开放`；待领仍可领（t3a 关开关领取 4 件）。`ev/S1-G8/` |
| **H8 代理路径** | **PASS** | 经 `25576` 进 play → 迁移后断线重连；uuid 不变；`p1_six_mig=1`；H/D 不变；audit 无 DUP / 无跨服双持。`ev/H8/reconnect.json` |
| **D12-③ 迁移件拒分解** | **PASS** | 临时 `p1 world remove world` 后在 hub 锻造门放开：`这件是迁移（不可分解）：只有副本里随机掉落的装备能分解`；inspect `src=migrate`。`ev/D12/refuse-final.json` |
| **D12-① 结算 +1 甲** | **FAIL / 未完成** | `/corerpg enter q01` 成功（DP 实例 `dungeon_EmberQ01_*`）；`runs weaken` 生效；房间触发文案出现。多轮清房/首领尝试未出现结算完成；DB `source=drop` 计数 **0**。证据：`ev/D12/q01-run.json`、`settle-final.json`、`boss-try.json`、`db-final.txt` |
| **D12-② 掉落甲分解** | **SKIP** | 依赖 ① 拿到 `src=drop` 甲；未取得实物。单测 `armorDismantleTenths` 仍由 T1/T2 覆盖 |

### 出口标准（设计 §1.3）逐条

| # | 标准 | 判定 |
|---|---|---|
| 1 | H/D 迁移前后逐位 | ✅ t3a/b/c |
| 2 | `p1_six_mig=1` + done_at/pieces | ✅ 五号均有记录 |
| 3 | 待领数 = 迁移前甲位件数 | ✅ t3d：1=1；t3a 初迁时 status 待领=4 与甲位占满一致 |
| 4 | 无预期外 ACTIVE_NOT_HELD；待领内 ACTIVE_IN_STASH | ✅（vanilla 待领无 DB 行报 NO_ROW，非 ACTIVE_NOT_HELD） |
| 5 | 无未决 journal | ✅ |
| 6 | H8 PASS | ✅ |
| 7 | D12 PASS | ❌ ①② 未过；③ 过 |
| 8 | ①② 不变量；线上未被动 | ✅；线上 PID 与测前一致 |

## 3 环境与约束

- 测服端口：代理 25576 / 游戏 25577 / 库 3317（均 127.0.0.1）
- 内存：演练期间 available 最低约 **5.0 GB**（未触 1.5G 暂停线）
- 线上：Waterfall/登录/游玩/MariaDB **PID 未变**（47100 / 47542 / 47617 / 101651）；未写 3306；未改 proxy-runtime / 线上 jar / bv / 开关
- 未 `git stash` / `reset --hard` / force-push；未给线上 op

## 4 残余 / 阻塞

| 级 | 项 | 说明 |
|---|---|---|
| **阻塞 ⑦** | D12 结算掉甲 + 掉落分解 | 隔离服上 Q01 完整通关未在窗口内打通（进本/削弱/房间 OK；首领结算未触发；无 drop 行）。建议：补跑脚本（清房开门→首领区 `weaken`→击杀至结算）或插件岗提供测服用结算钩子后再验 ①② |
| 观察 | audit NO_ROW | 待领里的原版甲无 DB 行 → NO_ROW；与 ACTIVE_IN_STASH（有 DB 的 P1 件）区分正确 |
| 观察 | 锻造世界门 | `world` 在 P1 列表内不可锻；非 static 世界被 `isInstanceWorld` 挡住。测拒绝时需临时 `p1 world remove world` |

## 5 建议

**暂不建议签「⑦ 部署」。**

已具备：H8、迁移主流程与出口 §1.3 主项、待领关开关可领、ACTIVE_IN_STASH、迁移件拒分解、测服 jar/bv61 草稿路径。

缺：D12 结算 +1 甲与掉落甲 ×0.1 分解的实机 PASS。按 D322 / 设计 §4.2，该项须过后再签部署。

补跑通过后可改结论为建议签 ⑦（仍不升线上 jar/bv/开关，直至总控另签）。

## 6 清场

见同次执行：停测服 Paper / 独立 Waterfall / 临时 mariadbd；删 `/workspace/tmp/t3-s1/`（证据可另打包）；核 `live-before.sha256` 与线上 PID。
