# B2.97 · DP `EmberEliteWeekly/option.yml` 历史「扣票 / B0.1 / 有票」注释 → 体力口径维护备忘

- **STATUS：tip · pending A（策划 · 2026-10-01 02:29 Asia/Shanghai）** · 待总控批 A
- **tip 路径：**`docs/design-ember-dp-elite-option-ticket-comment-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml` 的 4 行注释：L3、L4、L16、L18。键值、脚本、`text=`（含 L20「本周只有一次」）全部零改。DP option 注释系列最后一份。
- **前序对齐：**B2.94 Weekly、B2.95 Abyss、B2.96 Raid（close `2dc9b39`）同构注释已 PASS · 勾销，本窗句式一致。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 注释）**。
- **本窗纪律：**一文件一窗；其它文件不动；不暗示精英壳已开放；勿 git push。

## 1. 现行行为核对

结论：**精英试炼进本现行扣发起者体力（每周首次免费，之后 40），不扣 NI 票；DP 门 `%corerpg_gate_elite%` 不查票、不查首免**。四行旧注释与现行不符，按体力口径改，范围不用收窄。配置来源先看各 Service 的 `reload()`。依据（只读）：

- **入口：**TrMenu `ember_hub.yml` L256 `command: corerpg elite start` → `EliteService.cmdStart` → `TicketEntryService.tryEnter(ELITE)` → `dp start-console <发起者> EmberEliteWeekly`。
- **`%corerpg_gate_elite%`：**`CoreRpgExpansion` 对 `gate_elite` 调 `EliteService.passesGate`，只判两项：余烬等级 ≥ `progress.yml` `level_gates.elite`（现行 40），且本周未通关（`lootWeekMarks` 不含 `elite_weekly_clear=<weekId>`）。代码注释写明已不再要求持票；**不查票，也不查首免或体力**。旧 L16「有票」不成立。
- **通关标记：**COMPLETE 时 reward-script 对每名队员跑 `corerpg progress %player_name% elite_weekly`，`ProgressService` 写入 `elite_weekly_clear=<weekId>`（ISO 周，Asia/Shanghai，周一 0:00 换周）。所以是每人每周通关 1 次；未通关可以再进（首免用完后扣体力）。
- **发起者预检：**`tryEnter` 对非 OP 发起者先判等级门，再对 ELITE 判 `isClearedThisWeek`，任一不符直接拒绝、**不扣**。
- **首免：**`StaminaService.ensureWeekCredits` 换周时把账户额度 `weeklyGrantCreditElite` 重置为 1（账户额度，不是 NI 物品）；`consumeForEnter` 对 ELITE 先用这 1 次，用完才扣体力。
- **体力数：**`StaminaService.reload` 读 `cash.yml` `stamina.costs.elite`，live 为 40（代码缺省同为 40）。
- **退还：**`dp start-console` 失败，或约 2 秒后发起者仍不在本内（如某队员等级不足或本周已通关，被 DP L17 拒绝），`refundEnter` 退还本次首免或体力；进本后不退。
- **`hard_cap`：**`TicketGrantService.reload` 从 `cash.yml` `elite.hard_cap` 读入 `eliteHardCap`，但全仓库没有任何地方使用这个字段；精英票发放 `grant*` 是 stub（返回 0）。所以 **`hard_cap: 1` 现行不起作用**，旧 L3「持有上限 1」不再描述现行行为，本窗从注释里删去；cash.yml 本身不动（见 §5）。
- **遗留票物：**`stamina.ticket_convert.ticket_ember_elite: 40`，旧票按张折体力。

**B0.1 口径：**删掉的是注释里的「B0.1:」前缀，因为它描述的「按 NI id 扣票」已不是现行行为；这**不代表 B0.1 已清**，B0.1 状态仍以 backlog 为准。
**精英壳口径：**本稿只描述 DP 本文件现有门与扣费，不涉及精英内容是否对玩家开放，注释中不出现「开放 / 上线」类字样。

## 2. 荐案（逐行替换，行首缩进保持原样）

旧行（live）：

```yaml
# 次数：扣 NI id ticket_ember_elite ×1（显示名「余烬精英票」；周一发 1 · 持有上限 1）
# B0.1: 扣票改由 CoreRpg TicketEntryService（NI id）；本文件不再 <item:显示名> 扣次
    # 等级 + 本周未通关 + 有票（%corerpg_gate_elite%）；OP 豁免；放在扣票前 → 被拒不扣票
    # B0.1: 票已在 /corerpg enter 按 NI id 扣除；此处仅保留人数/等级门
```

新行精确文本（依次对应 L3、L4、L16、L18；L3、L4 顶格，L16、L18 行首 **4 个空格**，与原行一致）：

```yaml
# 次数：维护备忘：遗留票物/体力口径；现行进本扣发起者体力（本周首次免费，之后按 cash.yml stamina.costs.elite，现行 40）；每人每周通关 1 次
# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_elite 按 stamina.ticket_convert 折体力
    # 等级 + 本周未通关（%corerpg_gate_elite%，不查票/首免）；OP 豁免；发起者在 CoreRpg 先判同两项，不符不扣；队员被拒未进本时 CoreRpg 退还本次体力/首免
    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
```

逐行说明：

- **L3**：同 B2.96 L3 句式（多人本写「发起者」），数值 40 标「现行」并指向配置键；补「每人每周通关 1 次」对应现行通关标记；删去「周一发 1 · 持有上限 1」。
- **L4**：同系列句式，只换 NI id。
- **L16**：去掉「有票」，写明 PAPI 只判等级 + 本周未通关、不查票/首免；区分发起者（CoreRpg 预检，不符不扣）与队员（DP 条件拒绝，未进本退还）。
- **L18**：与 B2.94/B2.96 同句。

## 3. 施工与验收

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`，`4 insertions(+), 4 deletions(-)`，改动行恰为 L3、L4、L16、L18，逐字等于荐案（含 L16/L18 行首 4 空格）。
- [ ] 解析结果前后完全一致：仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'`
      输出 `same`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 same）。
- [ ] 施工后该文件 `rg -n "扣票|B0\.1|精英票|有票|持有上限"` 无命中；`text=`（含 L17 message、L20「本周只有一次」）、`condition`、`action-script`、`dungeon-reward-script` 零改。
- [ ] 静态核对即可；不重启、不 `/dp reload`、不长测。
- [ ] 本设计提交仅包含本 tip 与 backlog。

## 4. 明确不做

- 不改键值、脚本、`text=`；不改 L2「启动：…」管理备注。
- 不动 cash.yml（含 `elite.hard_cap` 与 L92 段头「持有硬顶 1」）、TrMenu、CoreRpg 代码。
- 不写测试岗边界情况；**勿 git push**。

## 5. 顺带发现（不在本窗，并入 backlog L20 候选）

- `cash.yml` `elite.hard_cap: 1` 现行无消费方，L92 段头注释「持有硬顶 1」同样不再对应现行行为。建议与 `cash.yml:86` 团票旧注同窗处理（只改注释；是否删键另议）。
- 精英状态页（`/corerpg elite status`，`EliteService` 约 L136–139）仍显示背包「精英票」数量，与深渊状态页同类，建议与深渊状态页同窗。
- L20「本周只有一次」：现行是每人每周通关一次，未通关可再进（扣首免或体力）。属 `text=`，可并入「×1」文案窗一并斟酌。
