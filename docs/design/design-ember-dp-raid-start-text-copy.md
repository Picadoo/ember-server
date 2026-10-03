# B2.99 · DP `EmberRaid/option.yml` L22 开本提示「已消耗体力 ×1」→ 改为奖励说明（不写扣费）

- **STATUS：PASS · 勾销（总控 · 2026-10-01 02:57 Asia/Shanghai）** · 设计 `4007fb3` · 批准 `27633ca` · 插件 `b90e999` · 测 `16d74cb` · close 本提交；报告 `docs/tests/TEST-B2.99-dp-raid-start-text.md`；reload 实测待恢复服后补。
- **tip 路径：**`docs/design/design-ember-dp-raid-start-text-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` L22 一行 `text=` 值（玩家可见）。列表长度、其它键、脚本、注释全部零改。
- **来由：**B2.98 tip §6 排序第 2 项；沿用 B2.98 口径：开本广播不写扣费、不写数字，扣费只由 CoreRpg 私聊 costHint 告知。
- **施工岗：**批 A 后交 **插件岗（DP option.yml 文案）**。
- **本窗纪律：**一文件一窗；不改数值；NI 票物保留；勿 git push。

## 1. 现行入口与费用提示（已核）

- **入口：**TrMenu `ember_raid.yml` L79 `command: corerpg enter raid` → `TicketEntryService.tryEnter(Kind.RAID)` → `dp start-console <发起者> EmberRaid`。`Kind.RAID` 的 `shortLabel` 是「团本」（`TicketEntryService.java` L29）。
- **唯一费用提示：**`tryEnter` 扣费后私聊发起者 `[团本] 正在进入……（costHint）`（L148-149）。costHint 共 4 种：L140「管理免扣」（OP/admin）、L142「本周首次免费」（用了 `weeklyGrantCreditRaid`）、L144「体力 -N」（N 取 `stamina.costs.raid`，现行 50）、L146「无消耗」（cost ≤ 0 时的兜底）。
- **L22 的问题：**`@dungeon` 发给全队（团本 3～5 人），但只有发起者被扣；首免时也没扣体力；「×1」易读成 1 点。与 B2.98 相同。

## 2. 两种改法对比

| | (a) 整行删除 | (b) 只改文案（荐） |
|---|---|---|
| 列表长度 | `dungeon-start.action-script` 由 4 项变 3 项，后两项下标前移 | 保持 4 项，下标不变 |
| 能否证明 DP 不依赖行序/下标 | **不能在本机证明**：仓库不含 DungeonPlus jar（按硬规则不入库），无法读源码确认；仓库内 DP 配置 `rg "action-script\[|\$index"` 无命中，各本 action-script 长度不一（Weekly/Elite 3、Raid 4、Abyss 5）也能正常开本，这只能说明「按顺序逐条执行」大概率成立，不是证明 | 无需证明 |
| 行为风险 | 若 DP 或 task 里有按下标引用，删除会错位；风险低但无法排除 | 仅文案变化，与 B2.98 同等级 |
| 玩家体验 | 开本只剩 L21 一句 | 多一句准确的奖励说明，与奖励预览菜单一致 |
| 测试脚本 | `killany-live-retest.js` L122 匹配「团本大厅已集结」，两案都不受影响 | 同左 |

**荐案取 (b)。**理由：(a) 的前提「DP 不依赖行序/下标」在本机拿不到源码证明，而 (b) 能达到同样目的（去掉不准确的扣费句），且不引入任何结构变化。新文案用奖励预览菜单已有的说法，不新增口径。

## 3. 荐案

L22 精确替换为（行首 4 个空格，引号与 `@dungeon` 保持原样）：

```yaml
    - "$message{type=text;text=§8通关箱 · 全队每人结算 · 周首通保底团戒} @dungeon"
```

- **文案来源：**`ember_raid_rewards.yml` L46 等「§8通关箱 · 全队每人结算」、`ember_raid.yml` L69「周首通保底团戒」；与本文件 reward-script（每次 COMPLETE 对全队每人发通关箱；`raid grant-ring` 每人每周 1 枚）一致。
- **不写扣费、不写数字：**扣费仍只由私聊 costHint 告知；奖励数量不写，调数值时无需同步。
- **颜色：**沿用原行的 `§8`（灰色小字），紧跟 L21 大厅集结句，不抢主标题。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] 施工 commit 只含 `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`，`1 insertion(+), 1 deletion(-)`，改动行恰为 L22，逐字等于荐案。
- [ ] **js-yaml 差异检查**：解析后只有 `dungeon-start.action-script[1]` 变化，列表仍为 4 项。仓库根目录跑
      `node -e 'const y=require("js-yaml"),{execSync:x}=require("child_process"),f="plugins/DungeonPlus/dungeon/EmberRaid/option.yml",g=r=>y.load(x("git show "+r+":"+f).toString());const a=g("HEAD~1"),b=g("HEAD");if(b["dungeon-start"]["action-script"].length!==4)process.exit(2);a["dungeon-start"]["action-script"][1]="$message{type=text;text=§8通关箱 · 全队每人结算 · 周首通保底团戒} @dungeon";if(JSON.stringify(a)!==JSON.stringify(b))process.exit(1);console.log("only-L22")'`
      输出 `only-L22`（HEAD 按施工 commit 替换；策划已对荐案干跑，结果 only-L22）。
- [ ] **扣费字样**：施工后 `rg -n "×1|已消耗体力|体力 -|扣票|团本票" plugins/DungeonPlus/dungeon/EmberRaid/option.yml` 无命中。
- [ ] **HANDOFF §8 查密码**（`docs/handoff/HANDOFF.md` §8 硬规则，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：团本开本时本内见 L21 + 新 L22，发起者收到「[团本] 正在进入……（本周首次免费 / 体力 -50）」。

## 5. 明确不做

- 不删行、不调整 action-script 顺序；不改 L21、L30 起的注释与 reward-script、`task/*.yml`。
- 不动其它 option.yml、TrMenu、CoreRpg、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」。
- mineflayer 相关（`killany-live-retest.js`、`gates-smoke.js:26-30`、`stamina-s0-smoke.js:120`）归 B2.109；七个日常本「已消耗体力」只记不排。
- **勿 git push**。

## 6. 后续（按 B2.98 §6，已批）

B2.100 Abyss L22 → B2.101 Elite L20 → B2.102 hub L249 → B2.103 CoreRpgPlugin cmdAbyss → B2.104 EliteService status → B2.105 set.yml → B2.106 cash.yml → B2.108 CoreRpgExpansion → B2.109 mineflayer（B2.107 src 模板挂起）。


## 总控批注（2026-10-01 02:50 Asia/Shanghai）
- 批 (b)。新文案两段均有现行出处（`ember_raid_rewards.yml:46`「通关箱 · 全队每人结算」；`EmberRaid/option.yml:30/44`「周首通保底」团戒），不引入新口径。
- 验收全部静态；reload 实测记「待恢复服后实测」，不挡 PASS。