# B2.96 · DP `EmberRaid/option.yml` 历史「扣票 / B0.1」注释 + L30 团票注释 → 体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-10-01 02:27 Asia/Shanghai）** · 设计 `5552822` · 批准 `273a4f6` · 插件 `179a7a9` · 测 `d62e20c` · close 本提交。测岗更正：团戒配置读 `set.yml:29-32`（非「live 无 raid_ring」），值等于代码缺省，结论不变。
- **tip 路径：**`docs/design-ember-dp-raid-option-ticket-comment-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` 的 5 行注释：L3、L4、L17、L19、L30。键值、脚本、`text=` 全部零改。
- **前序对齐：**B2.94 Weekly（close `7a5aff2`）、B2.95 Abyss（close `61785ad`）同构注释已 PASS · 勾销，本窗句式一致。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 注释）**。
- **本窗纪律：**一文件一窗；其它 option.yml 不动；不改策划数值；勿 git push。

## 1. 现行行为核对

结论：**团本进本现行扣的是发起者的体力（每周首次免费，之后 50），不扣 NI 票**；同周再通扣的也是体力，不需要「额外票」。范围不用收窄。依据（只读）：

- **入口：**TrMenu `ember_raid.yml` L79 `command: corerpg enter raid`；文件头 L2「S0 体力 50 / 本周首次免费 · 3～5 人」，L109「本周首次免费 · 其后 50 体力」。
- **谁付：**`TicketEntryService.tryEnter` 只对执行 `corerpg enter` 的那名玩家（发起者）查等级、扣费，然后 `dp start-console <发起者> EmberRaid`；其余队员不在 CoreRpg 侧扣费，由 DP L16/L18 条件逐队员判人数和等级。
- **首免：**`StaminaService.ensureWeekCredits` 在 `DailyService.weekId()` 变化时（ISO 周，Asia/Shanghai，即每周一 0:00）把账户额度 `weeklyGrantCreditRaid` 重置为 1。`consumeForEnter` 对 RAID 先用这 1 次额度（账户额度，不是 NI 物品），用完才扣体力。
- **体力数：**live `cash.yml` `stamina.costs.raid: 50`（代码缺省同为 50）。
- **退还：**`dp start-console` 失败，或约 2 秒后发起者仍不在本内（人数/等级不符、冷却等），`refundEnter` 退还本次额度或体力；用首免的退首免，扣体力的退体力。进本后不再退。
- **同周再通：**发起者本周首免已用时扣 50 体力；换一位首免未用的队员发起，则用该队员自己的首免。没有任何票物参与。
- **团戒去重：**每次 COMPLETE，`dungeon-reward-script` 对每名队员执行 `corerpg raid grant-ring %player_name%`。`RaidService.grantRing` 若该玩家 `raidRingWeek` 等于当前 `weekId` 就不发并提示「本周团戒已领取」，否则发 `acc_ember_raid_ring` ×1 并写入当周 weekId。live 配置未设 `raid_ring`，走代码缺省 `weekly_first: true`。所以团戒是每人每周 1 枚，与发起者、首免、体力都无关。
- **遗留票物：**`stamina.ticket_convert.ticket_ember_raid: 50`，旧票按张折体力；`TicketGrantService` 团本发放为 stub；cash.yml `raid.free_tickets: 0` 旁注已是维护备忘口径（B2.90）。

**B0.1 口径：**删掉的是注释里的「B0.1:」前缀，因为它描述的「按 NI id 扣票」已不是现行行为；这**不代表 B0.1 已清**，B0.1 状态仍以 backlog 为准。

## 2. 荐案（逐行替换，行首缩进保持原样）

旧行（live）：

```yaml
# 次数：扣 NI id ticket_ember_raid ×1（显示名「余烬团本票」；周发 1）= 周首通实践保底
# B0.1: 扣票改由 CoreRpg TicketEntryService（NI id）；本文件不再 <item:显示名> 扣次
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.raid=35；DP 逐队员判定；放在扣票条件前 → 被拒不扣票；OP 豁免）
    # B0.1: 票已在 /corerpg enter 按 NI id 扣除；此处仅保留人数/等级门
# 团戒：周首通保底。团票每周 1 张 ⇒ 同周再通需额外票；
```

新行精确文本（依次对应 L3、L4、L17、L19、L30；L3、L4、L30 顶格，L17、L19 行首 **4 个空格**，与原行一致）：

```yaml
# 次数：维护备忘：遗留票物/体力口径；现行进本扣发起者体力（本周首次免费，之后按 cash.yml stamina.costs.raid，现行 50）
# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_raid 按 stamina.ticket_convert 折体力
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.raid=35；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力/首免；OP 豁免）
    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
# 团戒：周首通保底（每人每周 1 枚）。进本首免按发起者每周 1 次，用完后同周再通扣发起者体力；团戒不重复发；
```

逐行说明：

- **L3**：同 B2.94 句式，补「发起者」（团本多人，只有发起者付费）；删去「周发 1 = 周首通实践保底」这一票时代说法，首免即每周 1 次免费开团。
- **L4**：同 B2.94/B2.95 句式，只换 NI id。
- **L17**：只替换「放在扣票条件前 → 被拒不扣票」为现行退还语义（团本有首免，写「体力/首免」）。
- **L19**：与 B2.94 L18 完全同句。
- **L30**：保留「团戒：周首通保底」；补「每人每周 1 枚」（这是现行去重结果的描述，不是新数值）；「团票每周 1 张 ⇒ 同周再通需额外票」改为首免与体力的现行说法。L31「周首通去重：corerpg raid grant-ring（raidRingWeek=DailyService.weekId）」与代码一致，不改。

### 2.1 锁定口径

- 不改任何奖励、数值、NI id；L30 只修描述。
- 不写「票已废」「B0.1 已清」；不写测试岗边界情况。

## 3. 施工与验收

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`，`5 insertions(+), 5 deletions(-)`，改动行恰为 L3、L4、L17、L19、L30，逐字等于荐案（含 L17/L19 行首 4 空格）。
- [ ] 解析结果前后完全一致：仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberRaid/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'`
      输出 `same`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 same）。
- [ ] 施工后该文件 `rg -n "扣票|B0\.1|团本票|团票|额外票"` 无命中；`text=`（含 L22「已消耗体力 ×1」、L45「同周再通不重复发戒」）、`condition`、`action-script`、`dungeon-reward-script` 零改。
- [ ] 静态核对即可；不重启、不 `/dp reload`、不长测。
- [ ] 本设计提交仅包含本 tip 与 backlog。

## 4. 明确不做

- 不改键值与脚本，不改 L22「已消耗体力 ×1」（排在注释窗之后的 `text=` 窗）。
- 不动其它 option.yml、TrMenu、CoreRpg 代码、cash.yml；不改 NI 票物显示名与兑换比例。
- 不处理测试岗报的范围外两条（`CoreRpgPlugin.java:1356` 兜底文案、深渊状态页显示背包票数），已由总控排在 `text=` 窗之后。
- **勿 git push**。

## 5. 后续

- **B2.97 `EmberEliteWeekly/option.yml`**：L3/L4/L16/L18；先核 `%corerpg_gate_elite%` 现行判定（`tryEnter` 另有「本周已通关精英试炼」门），避免暗示精英壳已开放。
