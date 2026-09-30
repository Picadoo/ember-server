# B2.98 · DP `EmberWeekly/option.yml` L20 开本提示「已消耗体力 ×1」→ 去掉扣费字样

- **STATUS：tip · pending A（策划 · 2026-10-01 02:38 Asia/Shanghai）** · 待总控批 A
- **tip 路径：**`docs/design-ember-dp-weekly-start-text-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml` L20 一行 `text=` 值（玩家可见）。其它键、脚本、注释全部零改。
- **来由：**B2.94 §5 顺带发现；backlog L20「票时代旧文案余项」首位。本 tip 同时给出余项的排序（§6）。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 文案）**。
- **本窗纪律：**一文件一窗；不改数值；NI 票物保留；勿 git push。

## 1. 现行行为与问题

- L20 现为 `- "$message{type=text;text=§c深核·周 开始！已消耗体力 ×1} @dungeon"`，`@dungeon` 发给本内所有队员（周本 1～3 人）。
- 现行扣费（B2.94 已核）：只扣**发起者**，本周首次免费，之后扣 `stamina.costs.weekly`（现行 45）；未进本退还。发起者在进本瞬间已收到 CoreRpg 私聊「[周本] 正在进入……（本周首次免费 / 体力 -45 / 管理免扣）」，这条才是准确的扣费提示。
- 「已消耗体力 ×1」的三处问题：
  1. **读成 1 点：**玩家看到「体力 ×1」会理解成扣了 1 点，与实扣 45 不符。
  2. **首免时不成立：**用本周首次免费进本时并未消耗体力。
  3. **对队员不成立：**广播给全队，但队员本人没有被扣任何东西。
- **前例说明：**`docs/design-ember-dp-ticket-stamina-copy.md` L65 曾定口径「×1 = 一次进本结算，非宣称扣 1 点体力」。这个口径只写在设计稿里，玩家看不到；加上首免和队员两种情况，本窗建议不再沿用，改为开本广播不提扣费。

## 2. 荐案

L20 精确替换为（行首 4 个空格，引号与 `@dungeon` 保持原样）：

```yaml
    - "$message{type=text;text=§c深核·周 开始！} @dungeon"
```

**理由（总控请我定写法并写明理由）：**

- **不写数字：**数值以后调整时不必同步改 DP 文案，避免再次失真。
- **也不写「已消耗体力」：**这条是全队广播，而扣费只发生在发起者身上，且可能是首免；无论写不写数字，「已消耗体力」对队员和首免都不准确。扣费信息已由 CoreRpg 私聊按实际情况（首免 / 体力 -N / 管理免扣）告知发起者，广播里不必重复。
- **保留原标题：**「§c深核·周 开始！」原样保留，颜色与标点不变，玩家体验只少了一句不准确的话；不新增玩法描述，避免引入新口径。

## 3. 施工与验收

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`，`1 insertion(+), 1 deletion(-)`，改动行恰为 L20，逐字等于荐案。
- [ ] 解析后只有 `dungeon-start.action-script[0]` 一处变化：仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberWeekly/option.yml",g=r=>y.load(x("git show "+r+":"+f).toString());const a=g("HEAD~1"),b=g("HEAD");a["dungeon-start"]["action-script"][0]="$message{type=text;text=§c深核·周 开始！} @dungeon";if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L20")'`
      输出 `only-L20`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 only-L20）。
- [ ] 施工后该文件 `rg -n "×1|已消耗体力"` 无命中。
- [ ] 测试脚本不受影响：`mineflayer-tests/stamina-s0-smoke.js` 周本判定用「本周首次免费 / 体力 -45 / 正在进入」（L111、L120），不依赖本行；「已消耗体力」只出现在日常判定的备选条件里（L64-65）。
- [ ] 需要 `/dp reload`（或按惯例重启）后实测一次：周本开本时本内只见「深核·周 开始！」，发起者仍收到「正在进入……（本周首次免费 / 体力 -45）」。测岗可复用 B2.94 的账号。

## 4. 明确不做

- 不改 `task/timeout.yml`「周常超时失败（体力不返还）」、L4/L5/L16/L18 注释、其它键。
- 不动其它 option.yml（各自一窗，见 §6）、TrMenu、CoreRpg、cash.yml。
- 不改 NI 票物，不写「票已废」「B0.1 已清」；**勿 git push**。

## 5. 同类窗口的统一写法（供 B2.99–B2.101 沿用）

- 开本广播只保留原有标题与氛围句，删去「已消耗体力 ×1」一段，不写数字。
- 与已上线的日常七线「已消耗体力 ·」不强求统一：日常是否也改另议（日常同样有「广播给全队」问题，但不在票时代余项清单内，本轮不排）。

## 6. 票时代旧文案余项排序（backlog L20 候选）

原则：先玩家可见文案，再配置注释，最后代码注释与测试脚本；一文件一窗；只改文案或注释，不改数值、不删票物。

**玩家可见 · DP 文案（只改 `text=`，需 reload）**
1. **B2.98 `EmberWeekly/option.yml` L20**（本窗）。
2. **B2.99 `EmberRaid/option.yml` L22**：`§8已消耗体力 ×1` 整条 message 只剩扣费句，建议整行删除还是改成别的，出稿时再定。删整行会改动 `action-script` 列表长度，需测岗确认 DP 不依赖行序。
3. **B2.100 `EmberAbyss/option.yml` L22**：删去「§7已消耗体力 ×1 ——」这一段，保留「能走多深，就走多深。本期可下潜至第 12 层」。
4. **B2.101 `EmberEliteWeekly/option.yml` L20**：「本周只有一次」改成与现行一致的「每人每周通关一次」语义（未通关可再进）。
5. **B2.102 `TrMenu/menus/ember_hub.yml` L249**：lore「每周 1 次」同样改成通关语义，与 B2.101 同一措辞。

**玩家可见 · CoreRpg 代码输出（需构建部署，排在 DP 文案之后）**
6. **B2.103 `CoreRpgPlugin.java` `cmdAbyss`（约 L1350–1360）**：「日限 1 次 · 进本扣余烬深渊票」、背包深渊票数、「软计数 x/1（以票为准）」、「进本：/dp start EmberAbyss」一并改成体力口径。
7. **B2.104 `EliteService.java` status 段（约 L130–140）**：去掉「精英票」数量显示，改显示首免与体力；同窗顺带改该文件 L37-38、L61 的票时代代码注释（同文件，避免二次构建）。

**配置注释（只改 `#` 注释，不改键值）**
8. **B2.105 `plugins/CoreRpg/set.yml` L27**：团票旧注。
9. **B2.106 `plugins/CoreRpg/cash.yml` L86 + L92 段头**：团票旧注；「持有硬顶 1」改为维护备忘，写明 `elite.hard_cap` 现行无消费方。键本身不删。
10. **B2.107 src 模板 `CoreRpg/src/main/resources/cash.yml` L92**：按惯例 src 模板不同步 live。建议只在总控认为需要时排，否则记录不排。

**代码注释与测试脚本**
11. **B2.108 `CoreRpgExpansion.java` L51**：注释去掉「票在 TicketEntryService 扣」这类说法。
12. **B2.109 mineflayer 旧正则**：`killany-live-retest.js` L122 的「已扣除余烬团本票」等旧匹配，改成现行文案（放最后，等 B2.99 定稿后按新文案改）。

**只记录、不排**
- `ProgressService` 的 level_gates 段存在但缺 elite 键时门槛为 0：属代码改动，本轮不排（总控边界）。
- option 注释写「corerpg enter」而精英菜单入口是 `corerpg elite start`：两者进的是同一个 `tryEnter`，注释不算错，不排。
- 日常七线「已消耗体力 ·」广播给全队的问题：不在票时代余项内，另议。
