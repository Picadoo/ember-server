# B2.95 · DP `EmberAbyss/option.yml` 历史「扣票 / B0.1」注释 → 体力口径维护备忘

- **STATUS：tip · pending A（策划 · 2026-10-01 02:20 Asia/Shanghai）** · 待总控批 A
- **tip 路径：**`docs/design-ember-dp-abyss-option-ticket-comment-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` 的 4 行注释：L3、L4、L18、L20。键值、脚本、`text=` 全部零改。
- **前序对齐：**B2.94 `EmberWeekly/option.yml` 同构注释已 PASS · 勾销（close `7a5aff2`），本窗写法与之一致；cash.yml 深渊 `free_tickets` 旁注（B2.91）口径「维护备忘：遗留票物/体力口径…（现行 0）」。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 注释）**。
- **本窗纪律：**一文件一窗；其它 option.yml 不动；勿 git push。

## 1. 现行行为核对

结论：**深渊进本现行只扣体力 30，没有首免，不扣 NI 票**；四行旧注释与现行不符，按体力口径改，范围不用收窄。依据（只读）：

- 入口：TrMenu `ember_abyss.yml` L79 `command: corerpg enter abyss`；L40 注释「深渊无周免费，字面量 30」，按钮 lore L63 显示 `%corerpg_stamina_cost_abyss%` 体力，L66/L90「撤离不退体力」。
- `TicketEntryService.tryEnter`：非 OP 先查余烬等级门（`progress.yml` abyss=25，不足直接拒、不扣），再调 `StaminaService.consumeForEnter`，然后 `dp start-console … EmberAbyss`。`Kind.ABYSS` 里的 `ticket_ember_abyss` 是 legacy id。深渊没有精英那样的额外周通关门，代码里也没有每日次数门；旧 L3「日 1 张」已不是现行。
- `StaminaService.consumeForEnter`：首免额度只对 WEEKLY / ELITE / RAID 生效，**ABYSS 不走首免**，直接扣 `costOf(abyss)`；live `cash.yml` `stamina.costs.abyss: 30`（代码缺省同为 30）。
- **未进本退还：**`dp start-console` 失败，或约 2 秒后玩家仍不在本内（DP 人数/等级条件拒绝、冷却等），`refundEnter` 退还本次体力。
- **进本后撤离不退：**撤离走 `corerpg abyss evacuate` → `AbyssSettleService` 按最高层结算，该路径不碰体力（`AbyssSettleService` / `AbyssShaftService` 无任何体力或退还调用）。
- **两句不冲突：**「未进本退还」是开本失败时的退款；「撤离…体力不退」（L23 玩家提示，总控消息里写作 L21，实际在 L23）是开本成功、进本后主动撤离的情况。新 L20 注释把两种情况分开写明。
- 遗留票物：`stamina.ticket_convert.ticket_ember_abyss: 30`，旧票按张折体力；`TicketGrantService` 深渊发放为 stub，与 cash.yml 旁注「现行 0」一致。

**B0.1 口径：**删掉的是注释里的「B0.1:」前缀，因为它描述的「按 NI id 扣票」已不是现行行为；这**不代表 B0.1 已清**，B0.1 状态仍以 backlog 为准。

## 2. 荐案（逐行替换，行首缩进保持原样）

旧行（live）：

```yaml
# 次数：扣 NI id ticket_ember_abyss ×1（显示名「余烬深渊票」；日 1 张）
# B0.1: 扣票改由 CoreRpg TicketEntryService（NI id）；本文件不再 <item:显示名> 扣次
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.abyss=25；DP 逐队员判定；放在扣票条件前 → 被拒不扣票；OP 豁免）
    # B0.1: 票已在 /corerpg enter 按 NI id 扣除；此处仅保留人数/等级门
```

新行精确文本（L3、L4 顶格；L18、L20 行首 **4 个空格**，与原行一致）：

```yaml
# 次数：维护备忘：遗留票物/体力口径；现行进本扣体力（深渊无首免，按 cash.yml stamina.costs.abyss，现行 30）
# 维护备忘：扣次由 CoreRpg TicketEntryService/StaminaService 在 corerpg enter 时处理，本文件不扣次；遗留 NI 票物 ticket_ember_abyss 按 stamina.ticket_convert 折体力
    # 2026-09-26 等级门槛（CoreRpg progress.yml level_gates.abyss=25；DP 逐队员判定；被拒未进本时 CoreRpg 退还本次体力；OP 豁免）
    # 维护备忘：体力已在 corerpg enter 扣除；未进本自动退还，进本后撤离按最高层结算、体力不退；此处仅保留人数/等级门
```

逐行说明：

- **L3**：同 B2.94 L4 格式；写明「深渊无首免」，30 标「现行」并指向配置键；删去已不成立的「日 1 张」。
- **L4**：与 B2.94 L5 同句式，只换 NI id。
- **L18**：只替换「放在扣票条件前 → 被拒不扣票」为现行退还语义；因深渊无首免，只写「体力」不写「/首免」。其余原样。
- **L20**：区分两种情况：未进本自动退还；进本后撤离按最高层结算、体力不退（与 L23 玩家提示、菜单 lore 一致）。保留「此处仅保留人数/等级门」。

## 3. 施工与验收

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`，`4 insertions(+), 4 deletions(-)`，改动行恰为 L3、L4、L18、L20，逐字等于荐案（含 L18/L20 行首 4 空格）。
- [ ] 解析结果前后完全一致：仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberAbyss/option.yml",g=r=>JSON.stringify(y.load(x("git show "+r+":"+f).toString()));if(g("HEAD~1")!==g("HEAD"))process.exit(1);console.log("same")'`
      输出 `same`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 same）。
- [ ] 施工后该文件 `rg -n "扣票|B0\.1|深渊票"` 无命中；`text=`（含 L22「已消耗体力 ×1」、L23 撤离提示）、`condition`、`action-script` 及其余键零改。
- [ ] 静态核对即可；不重启、不 `/dp reload`、不长测。
- [ ] 本设计提交仅包含本 tip 与 backlog。

## 4. 明确不做

- 不改键值与脚本，不改 L22/L23 玩家提示（L22「已消耗体力 ×1」按总控排期，注释三窗做完后单开一窗）。
- 不动其它 option.yml、TrMenu、CoreRpg 代码、cash.yml；不改 NI 票物显示名与兑换比例。
- 不写测试岗上轮记下的边界情况。
- 禁写「票已废」「B0.1 已清」；**勿 git push**。

## 5. 后续

- **B2.96 `EmberRaid/option.yml`**：L3/L4/L17/L19 同构，另核 L30「团票每周 1 张 ⇒ 同周再通需额外票」奖励段注释（团本有首免 + 体力 50）。
- **B2.97 `EmberEliteWeekly/option.yml`**：L3/L4/L16/L18；先核 `%corerpg_gate_elite%` 现行判定，避免暗示精英壳已开放。
