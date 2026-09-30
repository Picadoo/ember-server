# B2.94 · DP `EmberWeekly/option.yml` 历史「扣票 / B0.1」注释 → 体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-10-01 02:11 Asia/Shanghai）** · 设计 `9d20149` · 批准 `4c7e7eb` · 插件 `ef7e350` · 测 `b6501af` · close 本提交。
- **tip 路径：**`docs/design-ember-dp-weekly-option-ticket-comment-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml` 的 4 行注释：L4、L5、L16、L18。键值、脚本、`text=` 全部零改。
- **前序对齐：**B2.90/B2.91/B2.93 cash `free_tickets` 旁注已 PASS · 勾销（口径「维护备忘：遗留票物/体力口径…」）。本窗是 HANDOFF-ember-grokbot §6 候选 3 的第一个文件。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 注释）**。
- **本窗纪律：**一文件一窗；其它 option.yml 不动；勿 git push。

## 1. 现行行为核对（先核对，再改稿）

结论：**周本进本现行扣的是体力，不是 NI 票**，所以四行旧注释与现行不符，可以按体力口径改；无需收窄范围。依据（只读）：

- 入口：TrMenu `ember_weekly.yml` L77 `command: corerpg enter weekly`，文件头 L2 已写「S0 体力 45 / 本周首次免费」。
- `TicketEntryService.tryEnter`：非 OP 先查余烬等级门（`progress.yml` weekly=20），再调 `StaminaService.consumeForEnter`，然后 `dp start-console … EmberWeekly`。`Kind.WEEKLY` 里的 `ticket_ember_weekly` 注明是 legacy id，只做迁移。
- `StaminaService.consumeForEnter`：周本先扣账户里的「本周首次免费」额度（`weeklyGrantCreditWeekly`，每周刷新为 1，不是 NI 物品）；没有额度时扣体力 `costOf(weekly)`。live `cash.yml` `stamina.costs.weekly: 45`。
- 退还：DP 拒绝开本（dispatch 失败，或约 2 秒后玩家仍不在本内）时，`refundEnter` 退还本次体力或首免额度。所以旧 L16「放在扣票条件前 → 被拒不扣票」已不准确：现行是先扣、未进本再退。
- 遗留票物：`stamina.ticket_convert.ticket_ember_weekly: 45`，`convertInventoryTickets` 把背包旧票按张折成体力。`/corerpg ticket consume` 只是管理测扣口，不是进本入口。
- `TicketGrantService`：周本票物发放为 stub（返回 0），与 B2.90 旁注「现行 0」一致。

**B0.1 口径：**本窗删掉的是注释里的「B0.1:」前缀，因为它描述的「按 NI id 扣票」已不是现行行为；这**不代表 B0.1 已清**。B0.1 状态仍以 backlog 为准，本稿不作任何结论。

## 2. 荐案（逐行替换，行首缩进保持原样）

旧行（live）：

```yaml
# 次数：扣 NI id ticket_ember_weekly ×1（显示名「余烬周票」；周发 1）
# B0.1: 扣票改由 CoreRpg TicketEntryService（NI id）；本文件不再 <item:显示名> 扣次
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.weekly=20；DP 逐队员判定；放在扣票条件前 → 被拒不扣票；OP 豁免）
    # B0.1: 票已在 /corerpg enter 按 NI id 扣除；此处仅保留人数/等级门
```

新行精确文本（L4、L5 顶格；L16、L18 行首 **4 个空格**，与原行一致）：

```yaml
# 次数：维护备忘：遗留票物/体力口径；现行进本扣体力（本周首次免费，之后按 cash.yml stamina.costs.weekly，现行 45）
# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_weekly 按 stamina.ticket_convert 折体力
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.weekly=20；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力/首免；OP 豁免）
    # 维护备忘：体力/首免已在 corerpg enter 扣除（未进本自动退还）；此处仅保留人数/等级门
```

逐行说明：

- **L4**：沿用「次数：」开头，接 cash.yml 同款「维护备忘：遗留票物/体力口径」，写明现行扣法；45 标「现行」并指向配置键，改数值时以 cash.yml 为准。
- **L5**：说明扣次在 CoreRpg，本文件不扣次（保留原意），补一句遗留票物去向。去掉「<item:显示名>」等旧实现细节。
- **L16**：只替换「放在扣票条件前 → 被拒不扣票」这一段为现行退还语义，日期、等级门、逐队员判定、OP 豁免原样保留。
- **L18**：保留「此处仅保留人数/等级门」原意。

### 2.1 锁定口径

- 不写「票已废」「B0.1 已清」「已停用 NI 票」；遗留票物仍可折体力，这是现行事实。
- 不写菜单承诺以外的新规则；「本周首次免费」与 TrMenu 文件头一致。
- 不改 L3「启动：/dp start EmberWeekly」（管理备注，不是玩家文案），不改其它注释。

## 3. 施工与验收

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`，`4 insertions(+), 4 deletions(-)`，改动行恰为 L4、L5、L16、L18，逐字等于荐案（含 L16/L18 行首 4 空格）。
- [ ] 解析结果前后完全一致（只动注释）：在仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberWeekly/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'`
      输出 `same`（HEAD 按施工 commit 替换；策划已用同样比较对荐案干跑，结果 same）。
- [ ] 施工后该文件 `rg -n "扣票|B0\.1|余烬周票"` 无命中；`text=`、`condition`、`action-script`、`dungeon-reward-script` 零改。
- [ ] 静态核对即可；不重启、不 `/dp reload`、不长测。
- [ ] 本设计提交仅包含本 tip 与 backlog。

## 4. 明确不做

- 不改任何键值与脚本，包括 L20 开本提示 `text=§c深核·周 开始！已消耗体力 ×1`（见 §5 发现）。
- 不动其它 option.yml（EmberAbyss / EmberRaid / EmberEliteWeekly 等），不动 TrMenu、CoreRpg 代码与 cash.yml。
- 不改 NI 票物 `ticket_ember_weekly` 的显示名或兑换比例（§6 候选 4）。
- 禁写「票已废」「B0.1 已清」；**勿 git push**。

## 5. 顺带发现（不在本窗，供排窗）

- **玩家可见文案与现行不符：**`EmberWeekly` L20、`EmberRaid` L22、`EmberAbyss` L22 的开本提示都写「已消耗体力 ×1」，但现行是扣 45/50/30 体力或用首免，「×1」是票时代遗留。改的是 `text=` 值（玩家可见），建议注释三窗做完后单开一窗统一处理（例如改成日常本那样只写「已消耗体力」不带数），需另行设计。

## 6. 后续候选顺序（未立项，一文件一窗）

1. **B2.95 `EmberAbyss/option.yml`**：L3、L4、L18、L20 与本窗同构；深渊无首免，只扣体力 30，最简单，建议先做。
2. **B2.96 `EmberRaid/option.yml`**：L3、L4、L17、L19 同构，另有 L30「团票每周 1 张 ⇒ 同周再通需额外票」奖励段注释要一并核对（团本有首免 + 体力 50）。
3. **B2.97 `EmberEliteWeekly/option.yml`**：L3、L4、L16、L18；L16 提到「有票（%corerpg_gate_elite%）」，需先核对该 PAPI 现行判定再写，且要避免暗示精英壳已开放，放最后。
