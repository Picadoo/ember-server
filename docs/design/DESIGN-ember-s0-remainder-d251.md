# S0 残余规格草稿 · S0-9 世界兜底 / S0-10 旧仓库写路径（D251 · 2026-10-07）

> **性质：离线规格草稿，不是开工令。** 不改 Java / yml / TrMenu / p1sim，不构建、不部署、不停服、不开六槽护甲。
> **上游：** AUDIT §5 S0-9 / S0-10 · ARCH §0 #2 / R1 / R5 ② · D202 探针（S0-8 关闭、S0-9 降为可选）· D250 文档对齐。
> **基线（起草时）：** `main` @ D250 `39fcb810`；CoreRpg **1.65.70**。
> **落地：** S0-10 = **D252 / CoreRpg 1.65.71**（`e5e5c5bc`）；S0-9 仍 HOLD。本文 §2 保留为当时规格，现况以 STATUS-d252 / RELEASE-1.65.71 为准。

---

## 0. 一页结论

| 项 | 建议状态 | 为何 | 下一实现窗何时开 |
|---|---|---|---|
| **S0-9** 进世界兜底 | **HOLD（可选纵深）** | D202：普通玩家无 `mvtp` / `trmenu open` 旧菜单 / 传送门；已知入口已被 S0-1～S0-4 封 | 出现**新的**非 OP 进禁世界路径（插件、脚本、后门命令）后再做 |
| **S0-10** 旧仓库写路径 | **DONE（D252 / CoreRpg 1.65.71）** | P1 非 OP：只读 list/info；拒 deposit/withdraw/unlock（`LegacyGate` + `WarehouseService` 双闸） | 已落地；完整 persist-roundtrip 未在本窗跑（写路径对玩家已关闭，EmberVault 未改）；轻量实服拒写冒烟 |

---

## 1. S0-9 · 世界级传送兜底（HOLD）

### 1.1 问题（审计当时）

若普通玩家凭未知路径进入 `ember_event` / `world` / 下界 / 末地 / 旧日常常驻图，可能触发旧击杀币等（已被 S0-4 纵深挡住发奖，但仍可能迷路 / 触发未建模内容）。

### 1.2 已有证据（不必重做）

- D202（`docs/tests/smoke-2026-10-05-d202-s08-probe.md`）：非 OP 无 `multiverse.teleport.*`，无传送门，旧菜单无命令绑定。
- S0-3 已关 `calamity join` 等入口；S0-4 已关非白名单世界旧击杀发奖。

### 1.3 若将来实现（仅规格，本窗不写码）

**钩子：** `PlayerChangedWorldEvent`（可挂在 `CoreRpgPlugin` 或小型 `LegacyWorldGuard`）。

**生效条件：** `EmberMode.active()` 且发送者非 OP / 无 `corerpg.admin`。

**目标世界白名单（建议默认，可配 `legacy_gate.world_allow`）：**

| 允许 | 说明 |
|---|---|
| `ember_hub` | 枢纽 |
| `ember_afk` | 挂机庭 |
| 前缀 `dungeon_EmberQ0` | P1 副本实例（与 `EmberMode` scope 一致） |
| （可选）竞技场世界名 | **仅当**日后重开竞技且由控制台合法送入时再加；当前 S0-3 关竞技 → 默认不加 |

**动作：** 目标不在白名单 → 取消停留，传送回 `ember_hub` 出生点，tell 一句人话（勿提内部 gate id）。

**不做：** 不改 Multiverse 权限、不加 LuckPerms 依赖、不拦控制台/OP、不在本窗改 `server-runtime/permissions.yml`。

**验证（将来）：** Fresh 号若被 OP 故意 `mvtp` 到 `ember_event` 应弹回 hub；正常进 Q01 / 挂机庭不受影响；SEVERE 0。

**本窗裁决：** **不实现。** 文档层标记 HOLD；发现新传送口时把路径写进 AUDIT 再开窗。

---

## 2. S0-10 · 旧仓库写路径（**DONE · D252 / 1.65.71**）

### 2.1 问题【码】

- P1 玩家菜单走 `corerpg p1 vault from hub`（`ember_hub.yml`）→ `EmberVault`（无格上限、有 `cr_vault_log`、绑定材料不可取出）。
- **历史问题：** 旧命令 `/corerpg warehouse deposit|withdraw|unlock|…` 曾对所有人开放（`warehouse: '*'`）。
- `WarehouseService.cmdDeposit` / `cmdWithdraw` / `cmdUnlock` 直接改同一份 `PlayerData` 仓库槽 + `flushMutation`，**不走** `EmberVault` 守卫与流水；`unlock` 扣余烬币扩格，与 P1「无格子上限」矛盾（`EmberVault` 类注释）。

### 2.2 推荐方案（薄、可回滚）

**方案 A（推荐，单窗）：** P1 开着时，对非 OP：

| 子命令 | 行为 |
|---|---|
| `warehouse` / `list` / `overview` / `info` | **只读保留**（或 tell「请用枢纽仓库」后仍可 list——二选一，默认：**保留 list/info**，方便排障） |
| `deposit` / `withdraw` / `unlock`（及中文别名 存/取） | **拒绝**，tell：请打开枢纽「仓库」（`/ember` → 仓库），勿手打旧命令 |
| OP / `corerpg.admin` / 控制台 | 全部放行（测本 / 修复用） |

**落地（D252，两闸都上）：**

1. `legacy_gate.allow.warehouse: ['', list, overview, info]`（线上 + 打包 yml）。
2. `WarehouseService.refuseWriteWhileP1` 纵深拒绝写子命令，并提示用枢纽仓库。

**方案 B（更重，不推荐作首窗）：** 写路径委托 `EmberVault.depositAll` / `withdraw`——行为要对齐绑定材料、流水、自动入仓；改动面大，必跑完整资产回归。留给方案 A 之后若仍需要兼容旧命令时再做。

### 2.3 明确不做（本规格）

- 不删 `WarehouseService`、不迁表、不改 `cr_warehouse` schema。
- 不动 `EmberVault` 数值 / 白名单材料 id。
- （起草时）不在 D251 离线窗改 yml；D252 已改。

### 2.4 验证清单（D252）

1. **静态：** `LegacyGateTest` 15/0（含 s010）；yml allow 与 DEFAULT_ALLOW 一致。
2. **实服：** CoreRpg **1.65.71** 已部署；完整 bot 拒写冒烟 / persist-roundtrip 可另窗补（玩家写路径已关，EmberVault 未改）。
3. **p1sim：** 不建模仓库命令 → 不变。
4. **发版：** 1.65.71；`balance_version` 58 不动。

### 2.5 风险

| 风险 | 缓解 |
|---|---|
| 有人书签了旧 `warehouse withdraw` | tell 指向枢纽仓库；list 仍在 |
| allow 子命令解析与 `'*'` 语义不一致 | 实现前用单测表覆盖 warehouse 各 sub |
| 误伤自动入仓 | 自动入仓走 `EmberVault`，不经 `cmdRoot` —— 勿在监听器上误加闸 |

---

## 3. 与 ARCH / AUDIT 的关系

- AUDIT / ARCH：S0-10 已标 DONE（D252）；S0-9 仍 HOLD。
- 六槽护甲 / Stage 1：无关，本流水线不开。

---

## 4. 建议的下一窗顺序

1. **文案/注释其它薄债**（D253 起继续），或  
2. 继续 HOLD S0-9，直到出现新传送证据。
