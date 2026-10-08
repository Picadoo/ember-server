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
| **D12-① 结算 +1 甲** | **PASS（补跑）** | 见 §7；`boss killed` + `settle rows+16`；DB drop 胸甲 ≥1 |
| **D12-② 掉落甲分解** | **PASS（补跑）** | drop 胸甲 → dismantled；`p1_blank_tenths@all: 1`（×0.1） |

### 出口标准（设计 §1.3）逐条

| # | 标准 | 判定 |
|---|---|---|
| 1 | H/D 迁移前后逐位 | ✅ t3a/b/c |
| 2 | `p1_six_mig=1` + done_at/pieces | ✅ 五号均有记录 |
| 3 | 待领数 = 迁移前甲位件数 | ✅ t3d：1=1；t3a 初迁时 status 待领=4 与甲位占满一致 |
| 4 | 无预期外 ACTIVE_NOT_HELD；待领内 ACTIVE_IN_STASH | ✅（vanilla 待领无 DB 行报 NO_ROW，非 ACTIVE_NOT_HELD） |
| 5 | 无未决 journal | ✅ |
| 6 | H8 PASS | ✅ |
| 7 | D12 PASS | ✅ ①②③（§7 补跑） |
| 8 | ①② 不变量；线上未被动 | ✅；线上 PID 与测前一致 |

## 3 环境与约束

- 测服端口：代理 25576 / 游戏 25577 / 库 3317（均 127.0.0.1）
- 内存：演练期间 available 最低约 **5.0 GB**（未触 1.5G 暂停线）
- 线上：Waterfall/登录/游玩/MariaDB **PID 未变**（47100 / 47542 / 47617 / 101651）；未写 3306；未改 proxy-runtime / 线上 jar / bv / 开关
- 未 `git stash` / `reset --hard` / force-push；未给线上 op

## 4 残余 / 阻塞

| 级 | 项 | 说明 |
|---|---|---|
| ~~阻塞 ⑦~~ | D12 结算掉甲 + 掉落分解 | **已解除（§7）**：按通关脚本 PASS ①②；③ 抽查 PASS |
| 观察 | audit NO_ROW | 待领里的原版甲无 DB 行 → NO_ROW；与 ACTIVE_IN_STASH（有 DB 的 P1 件）区分正确 |
| 观察 | 锻造世界门 | `world` 在 P1 列表内不可锻；非 static 世界被 `isInstanceWorld` 挡住。测拒绝时需临时 `p1 world remove world` |

## 5 建议

**建议签「⑦ 部署」。**（§7 D12 补跑通过后更新）

已具备：H8、迁移主流程与出口 §1.3 全项（含 D12-①②③）、待领关开关可领、ACTIVE_IN_STASH、迁移件拒分解、测服 jar/bv61 草稿路径。

仍不升线上 jar/bv/开关，直至总控另签 ⑦ 后按 OPS 执行。

## 6 清场

见同次执行：停测服 Paper / 独立 Waterfall / 临时 mariadbd；删 `/workspace/tmp/t3-s1/`（证据可另打包）；核 `live-before.sha256` 与线上 PID。

---

## 7 D12 补跑（2026-10-08 15:43–15:55 CST · tip `d4ab102c` 不变）

- 依据：插件岗 `D12-q01-clear-script.md`（判定为脚本未打到结算，非掉甲 bug）；总控「⑦ 不签，补跑后再议」。
- 环境：重建 `/workspace/tmp/t3-s1/` · Paper `25577` · 独立 Waterfall `25576` · 临时 mariadbd `3317`/`ember_t3` · jar sha `abc96aca…` · bv61 · `gear.six_slot.enabled/migrate=true`（仅测服）。
- 通关硬规则：全程 **零 Kick / 零 `/dp leave`**；每房 weaken → **bot 补刀至 `active=-`**；首领 **玩家最后一击**。

### 7.1 结果

| 编号 | 结果 | 关键证据 |
|---|---|---|
| **D12-① 结算 +1 甲** | **PASS** | `enter q01` → 三房清完 → `boss spawned hp=200` → `boss killed after 4.8 s` → `settle … rows+16 (first clear)`；聊天「残门蛮兵 已击败 · 结算完成」+「结算到账」含 `T1 炽愈胸甲…（护甲已放进背包…）`；DB `source=drop` 护甲 ≥1（`ember_v1_sustain_chest_t1` uid `426fde62…`）。`ev/D12/q01-clear.json`、`log-keywords.txt`、`db-drop.txt` |
| **D12-② 掉落甲分解** | **PASS** | 手持 drop 胸甲 → 预览「胚料 +0.1」→ 确认后 DB `state=dismantled`；计数器 `p1_blank_tenths@all: 1`（=0.1 记账）。`ev/D12/dismantle.json`、`db-after-dismantle1.txt`、`blank-tenths.txt` |
| **D12-③ 迁移件拒拆** | **PASS（抽查）** | 手持 `src=migrate` 头盔：`这件是迁移（不可分解）：只有副本里随机掉落的装备能分解`。`ev/D12/refuse-migrate.json` |
| **H8 代理路径** | **PASS（抽查）** | 经 `25576` 进 play → 迁移 → 断线重连；uuid 不变；`p1_six_mig=1` + pieces；DB migrate×4 active。`ev/H8/reconnect.json` |
| **S1-M 迁移主流程** | **PASS（抽查）** | 新号 t3h8a 穿原版皮甲 → 进枢纽/armor mig → `p1_six_mig=1`、pieces×4、DB migrate active×4。`ev/S1-M/spot.json` |

### 7.2 出口标准（设计 §1.3）补跑后

| # | 标准 | 判定 |
|---|---|---|
| 6 | H8 PASS | ✅ 抽查 |
| 7 | D12 PASS | ✅ ①②③ |
| 8 | ①② 不变量；线上未被动 | ✅；线上 PID 47100/47542/47617/101651 未变；live jar sha 复核 OK |

### 7.3 建议（补跑后）

**建议签「⑦ 部署」。**

D12 阻塞已解除：按插件岗通关脚本实机打出首领结算 + drop 甲 + ×0.1 分解；迁移件仍拒拆；H8 / 迁移抽查仍绿。线上 jar / bv / 开关仍未动（本段仅隔离测服）。部署步骤仍按设计稿 / OPS，由总控另签后执行。

### 7.4 清场

证据包：`/workspace/tmp/t3-s1-evidence-d12-rerun-2026-10-08.tar.gz`（gzip）。随后停测服 Paper / 独立 Waterfall / 临时 mariadbd，删 `/workspace/tmp/t3-s1/`，再核线上 PID 与 sha。
