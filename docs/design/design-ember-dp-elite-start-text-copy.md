# B2.101 · DP `EmberEliteWeekly/option.yml` L20 开本提示「本周只有一次」→ 每人每周限通关一次

- **STATUS：PASS · 勾销（总控 · 2026-10-01 03:19 Asia/Shanghai）** · 设计 `86d83c1` · 批准 `910d284` · 插件 `e55540a` · 测 `713b407` · close 本提交；报告 `docs/tests/TEST-B2.101-dp-elite-start-text.md`；reload 实测待恢复服后补。
- **tip 路径：**`docs/design/design-ember-dp-elite-start-text-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml` L20 一行 `text=` 值（玩家可见）。列表长度（3 项）、其它键、脚本、注释全部零改。
- **来由：**B2.98 tip §6 排序；「本周只有一次」暗示每周只能进一次，与现行规则不符。B2.102（`ember_hub.yml` L249）沿用本窗句式。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 文案）**。
- **本窗纪律：**一文件一窗；不改数值；不写扣费与费用数字；NI 票物保留；勿 git push。

## 1. 现行 L20（完整旧行）

```yaml
    - "$message{type=text;text=§e精英试炼开启。§7本周只有一次——词缀会咬人。} @dungeon"
```

位于 `dungeon-start.action-script[0]`（列表共 3 项）。

## 2. 现行规则（对照 EliteService 已核）

- **限的是通关，不是进本：**
  - `EliteService.passesGate`（L40-47）只判两件事：余烬等级 ≥ `level_gates.elite`，以及 `isClearedThisWeek`（L49-53，查 lootWeekMarks `elite_weekly_clear=<周>`）。
  - 发起者在 `TicketEntryService` L115-121 先判同一个通关标记，已通关则提示「本周已通关精英试炼，下周再来」，不扣费。
  - 队员由 DP L17 `%corerpg_gate_elite%` 判同样两项。
- **通关标记在 COMPLETE 时按人写入：**reward-script L36 `corerpg progress %player_name% elite_weekly`（`@player`，逐人执行）→ `ProgressService` L471-481 写 `elite_weekly_clear=<周>`；同周重复只报 already，并跳过双倍经验。
  - 周界是 ISO 周，Asia/Shanghai 周一 0:00（`DailyService.weekId`）。
- **未通关可以再进：**失败、团灭或撤出都不写通关标记，同周可以再次 `corerpg elite start`。
  - 第一次用本周免费；之后按 `stamina.costs.elite` 扣体力（首免 `weeklyGrantCreditElite`，现行 40）。
  - 费用只由私聊 costHint 告知（L148-149）：精英可能出现 L140「管理免扣」、L142「本周首次免费」、L144「体力 -N」；L146「无消耗」为兜底。
- **奖励口径：**
  - 每次 COMPLETE 对每人发通关箱（L30-34：材料、附魔晶、`elite_gem`）。
  - 稳定符由 `corerpg elite weekly-first` 发（L35 → `EliteService` L85-105），按 `elite_weekly_first=<周>` 每人每周 1 枚，已发过就报 already。
  - 因为通关后同周进不去，实际上每人每周最多拿一次通关奖励。

结论：准确的说法是「每人每周限通关一次；没通关可以再来」。

## 3. 荐案

L20 精确替换为（行首 4 个空格，引号与 `@dungeon` 保持原样）：

```yaml
    - "$message{type=text;text=§e精英试炼开启。§7每人每周限通关一次，没过可以再来——词缀会咬人。} @dungeon"
```

- 「每人」：标记按人写，队友各算各的，发起者没有特殊地位。
- 「限通关一次」：限的是通关，不暗示只能进一次。
- 「没过可以再来」：明确未通关可以重进；不写代价、不写数字，费用交给私聊 costHint。
- 句式与颜色：保留标题 `§e`、正文 `§7` 和原来的「——词缀会咬人。」收尾，语气不变。

**给 B2.102 的同一句式：**hub L249 是菜单 lore，用「·」分段、数字用阿拉伯数字。建议把 L249 改为 `'§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符'`（只把「每周 1 次」改成「每人每周限通关 1 次」）。lore 行宽有限，不加「没过可以再来」。具体以 B2.102 出稿为准。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`，`1 insertion(+), 1 deletion(-)`，改动行恰为 L20，逐字等于荐案。
- [ ] **js-yaml only-L20（`<build>^` 对 `<build>`，并断言旧值是改前原文）**。仓库根目录跑，`B` 填施工提交：
      `B=<build>; node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml",B=process.argv[1],g=r=>y.load(x("git show "+r+":"+f).toString());const O="$message{type=text;text=§e精英试炼开启。§7本周只有一次——词缀会咬人。} @dungeon",N="$message{type=text;text=§e精英试炼开启。§7每人每周限通关一次，没过可以再来——词缀会咬人。} @dungeon";const a=g(B+"^"),b=g(B),A=a["dungeon-start"]["action-script"],C=b["dungeon-start"]["action-script"];if(A[0]!==O)process.exit(3);if(C[0]!==N)process.exit(4);if(A.length!==3||C.length!==3)process.exit(2);A[0]=N;if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L20")' "$B"`
      - 期望输出 `only-L20`。exit 3 表示旧值不是改前原文（比错了提交），exit 4 表示新值不等于荐案。
      - 策划已干跑：旧值取 HEAD，新值取改后的工作区，结果 only-L20，随后还原。
- [ ] **rg（收窄口径）**：`rg -n "已消耗体力|体力 ×|体力 -|扣票|精英票|只有一次" plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`
      - 施工前全文件预跑：只命中 L20 一处（已核）。
      - 施工后：无命中（干跑 rg exit 1）。
- [ ] **HANDOFF §8 查密码**（`docs/handoff/HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：
      - 开本时本内见新 L20。
      - 发起者收到「[精英] 正在进入……（本周首次免费 / 体力 -40）」。
      - 失败后同周可以再进；通关后再点，提示「本周已通关精英试炼，下周再来」。

## 5. 明确不做

- 不删行、不调顺序；不改 L3-4、L16-18 注释、L17 条件、reward-script。
- 不动 hub（B2.102 另窗）、TrMenu、CoreRpg、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」；不暗示精英壳已开放。
- **勿 git push**。

## 6. 顺带发现（只记，不在本窗）

`ember_hub.yml` L250 lore「§8需余烬 Lv.40 · 消耗 §e40 §8体力（本周首次免费）」写死了 40，不跟随 cash.yml；OP 免扣时也不准。它和 B2.102 同一个文件，是否并入 B2.102 由总控定：并入则 B2.102 改两行；否则单列新号。

排序（按总控更新）：B2.102 hub L249 → B2.110 ember_raid.yml L76-77 → B2.111 ember_abyss.yml L77 → B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.109（B2.107 挂起）。


## 总控批注（2026-10-01 03:11 Asia/Shanghai）
- 批荐案。「每人每周限通关一次，没过可以再来」与 EliteService / ProgressService 现行口径一致，不写扣费与数字。
- `ember_hub.yml` L250 硬编码「消耗 §e40 §8体力（本周首次免费）」：**并入 B2.102**（同文件、相邻 lore，一窗两行），B2.102 验收须覆盖 L249+L250。
- 验收全部静态；reload 实测记「待恢复服后实测」，不挡 PASS。