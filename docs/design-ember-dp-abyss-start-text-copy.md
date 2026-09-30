# B2.100 · DP `EmberAbyss/option.yml` L22 开本提示去掉「已消耗体力 ×1」

- **STATUS：PASS · 勾销（总控 · 2026-10-01 03:08 Asia/Shanghai）** · 设计 `0ef491e` · 批准 `2033207` · 插件 `252fe8e` · 测 `c3d662c` · close 本提交；报告 `docs/TEST-B2.100-dp-abyss-start-text.md`；reload 实测待恢复服后补。
- **tip 路径：**`docs/design-ember-dp-abyss-start-text-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` L22 一行 `text=` 值（玩家可见）。列表长度（5 项）、其它键、脚本、注释全部零改。
- **来由：**B2.98 tip §6 排序第 1 项（B2.99 §6 顺延）；沿用 B2.98/B2.99 口径：开本广播不写扣费，扣费只由 CoreRpg 私聊 costHint 告知。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 文案）**。
- **本窗纪律：**一文件一窗；不改数值；NI 票物保留；勿 git push。

## 1. 现行 L22（完整旧行）

```yaml
    - "$message{type=text;text=§5深渊已开启。§7已消耗体力 ×1 —— 能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
```

- 位于 `dungeon-start.action-script[0]`（列表共 5 项）。
- **问题：**深渊没有周首免，但扣的是 30 体力（`cash.yml` `stamina.costs.abyss`），「×1」容易读成 1 点；`@dungeon` 发给全队（1～2 人），实际只扣发起者；OP 免扣时也会看到这句。
- **唯一费用提示：**菜单 `ember_abyss.yml` L79 `corerpg enter abyss` → `TicketEntryService.tryEnter(Kind.ABYSS)`，扣费后私聊发起者 `[深渊] 正在进入……（costHint）`（L148-149）。深渊实际能出现的是 L140「管理免扣」和 L144「体力 -N」（N 现行 30）；L142「本周首次免费」深渊不适用；L146「无消耗」只在 cost ≤ 0 时兜底。

## 2. 「12」核对（下潜层数，不是费用）

与现行层数上限一致，保留：
- `EmberAbyss/monster.yml` 最后一组是 `floor12`（L208），其 end 段 `corerpg abyss progress … 12` 后以 `end-type=COMPLETE` 收尾（「顶层通关」），floor12 之后没有下一组。
- `EmberAbyss/option.yml` L7、L30 注释：COMPLETE 顶层 = floor12。
- `AbyssSettleService.java` L33 `WEEKLY12_MIN_FLOOR = 12`（周首通稳定符门槛）。
- `plugins/CoreRpg/abyss.yml` 结算档位最高档是 `min: 10, max: 999`，只是开放区间，不构成层数上限。

## 3. 荐案

L22 精确替换为（行首 4 个空格，引号与 `@dungeon` 保持原样）：

```yaml
    - "$message{type=text;text=§5深渊已开启。§7能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon"
```

- 只删「已消耗体力 ×1 —— 」这一段（含破折号和前后空格），其余逐字保留。
- 句式：删后是「深渊已开启。能走多深，就走多深。本期可下潜至第 12 层。」三个整句，都以句号收尾；破折号原本用来连接扣费句，删掉后没有悬空。
- 颜色：`§7` 保留在「能走多深」前，与原行一致（标题 `§5`、正文 `§7`、层数 `§f`）。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`，`1 insertion(+), 1 deletion(-)`，改动行恰为 L22，逐字等于荐案。
- [ ] **js-yaml only-L22**：解析后只有 `dungeon-start.action-script[0]` 变化，列表仍为 5 项。仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberAbyss/option.yml",g=r=>y.load(x("git show "+r+":"+f).toString());const a=g("HEAD~1"),b=g("HEAD");if(b["dungeon-start"]["action-script"].length!==5)process.exit(2);a["dungeon-start"]["action-script"][0]="$message{type=text;text=§5深渊已开启。§7能走多深，就走多深。本期可下潜至第 §f12 §7层。} @dungeon";if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L22")'`
      输出 `only-L22`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 only-L22）。
- [ ] **扣费字样（收窄口径）**：`rg -n "已消耗体力|体力 ×|体力 -|扣票|深渊票" plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`
      - 施工前全文件预跑：仅命中 L22 一处（已核）。
      - 施工后：无命中（策划干跑 rg exit 1）。
      - 不再用 `×1`：会误中奖励数量注释（B2.99 口径的问题，总控已指出）。
- [ ] **HANDOFF §8 查密码**（`HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：下潜开本时本内见新 L22 + L23 撤离提示，发起者收到「[深渊] 正在进入……（体力 -30）」。

## 5. 明确不做

- 不删行、不调整 action-script 顺序；不改 L23 撤离句、L21 维护备忘、L30 起注释与 reward-script、`monster.yml`。
- 不动其它 option.yml、TrMenu、CoreRpg、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」。
- mineflayer 相关归 B2.109；七个日常本「已消耗体力」只记不排。
- **勿 git push**。

## 6. 后续排序（含测岗新候选）

**新候选定为 B2.110：**`plugins/TrMenu/menus/ember_raid.yml` 进本按钮的 click `tell`（L76-77）。
- 问题：L77「§8消耗 §e50 §8体力 · 人数 3～5」在 `corerpg enter raid` 之前无条件发出，首免、OP 免扣、等级不足被拒时都和私聊 costHint 冲突；L76「§9团本已点燃……」同样在进本判定前发出，被拒时也会误报。
- 方向：参照 `ember_abyss.yml` L77「尝试下潜……」的写法，改成不写扣费的「尝试进入」提示，费用交给私聊 costHint。说明页的 L22、L118 已写「本周首次免费」，口径准确，不在范围内。具体文案到 B2.110 出稿时再定。
- 位置：插在 B2.102（`ember_hub.yml` L249，同属 TrMenu 菜单文案）之后、B2.103（代码）之前。它玩家可见且会给出错误信息，优先级高于后面的注释和代码窗。仍然每窗一个文件。

排序：B2.101 Elite L20 → B2.102 hub L249 → **B2.110 ember_raid.yml L76-77** → B2.103 CoreRpgPlugin cmdAbyss → B2.104 EliteService status → B2.105 set.yml → B2.106 cash.yml → B2.108 CoreRpgExpansion → B2.109 mineflayer（B2.107 src 模板挂起）。


## 总控批注（2026-10-01 03:00 Asia/Shanghai）
- 批荐案。「12」为下潜层数上限（monster.yml floor12 / WEEKLY12_MIN_FLOOR），非费用，保留。
- 验收全部静态，rg 用收窄口径；reload 实测记「待恢复服后实测」，不挡 PASS。
- B2.110（`ember_raid.yml:76-77`）排在 B2.102 后、B2.103 前，照准。