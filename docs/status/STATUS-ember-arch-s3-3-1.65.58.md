# STATUS · ARCH S3-3（CoreRpg 1.65.58 / D232）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.57 / D231 / bv58（70fedec）

## 为什么选 Pledge（不是 Raid）

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / Abyss / Raid / Rush / **Pledge** / Recruit / Papi（行为不变）」。

- **Pledge**：`p1_pledge_*` 计数器 + toggle/off/list 菜单 + `encodeKey` / `countInModifier` + S09 settle grant，与 Rush/Abyss 同形的封闭计数器缝；`enter` 只在周规则 `mod == null` 时回落一行调用。
- **Raid**：与 `enter` 的 party/cap/stamina 主路径耦合更广，本刀不做。

上一刀（D231）因当时认为 Pledge↔周规则更缠而先拆 Abyss；本刀拆开后确认周规则选择仍留在 `EmberRunService.enter`，Pledge 只负责「队长已挂规则 → modifier 串 / 结算条数」。

## 做了什么

1. 新建 `EmberPledgeService`：isOn / toggle / off、activeIds、encodeKey、countInModifier、settleGrant、sessionKey、cmd、papi、head。
2. `EmberRunService` 薄委托（`pledged` / `pledgeCount` / `pledgeKey` / `cmdPledge` / `pledgePapi` / `pledgeHead`）；`enter` 走 `sessionKey`；settle 走 `count` + `settleGrant`。
3. Bukkit-free：`isOn` / `applyToggle` / `applyOff` / `countInModifier` / `encodeKey` / `activeIds` / `settleGrant`。
4. `EmberPledgeServiceTest` ×6：池金样；toggle/off；encode/count；activeIds 顺序；S09 grant；计数器归属。
5. `EmberCounters`：`p1_pledge_` 归属 → `EmberPledgeService`；S09 登记文案同步。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量
- 技能；C15；echotune；p1sim；R2 swap；内容包；Raid 行为

## 下一刀候选

1. S3 step 4：拆 `EmberRaidService`（或 Recruit / Papi / Entry）
2. S3 遭遇原语接口（可并行）
3. S14/S15 经 grant*（体力 / 退药）— S2 leftover

## 冒烟

FreshQ804+（见 `docs/tests/smoke-2026-10-06-d232-arch-s3-pledge.md`）。
