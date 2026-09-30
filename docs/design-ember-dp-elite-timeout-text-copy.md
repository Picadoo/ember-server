# B2.112 · DP `EmberEliteWeekly/task/timeout.yml` L6 超时失败提示「体力已扣，下周再来」→ 不记通关、本周可再来

- **STATUS：PASS · 勾销（总控 · 2026-10-01 03:41 Asia/Shanghai）** · 设计 `e47a7ed` · 批准 `ed3e16e` · 插件 `684ede2` · 测 `4adadc4` · close 本提交；报告 `docs/TEST-B2.112-dp-elite-timeout-text.md`；reload 实测待恢复服后补。
- **tip 路径：**`docs/design-ember-dp-elite-timeout-text-copy.md`
- **目标范围：**仅 `plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml` L6 一行 `text=` 值（玩家可见）。L7 `$end`、`auto-start`、时长 720 全部零改；numstat `1 1`。
- **来由：**测岗报：L6 与 B2.101 新 L20「没过可以再来」和代码都矛盾。总控 03:31 排为 B2.112。
- **施工岗：**批 A 后交 **插件岗（DP task 文案）**。
- **本窗纪律：**一文件一窗；不改数值与时长；不写扣费与数字；NI 票物保留；勿 git push。

## 1. 现行 L6（完整旧行）

```yaml
      - "$message{type=text;text=§c试炼失败。§7体力已扣，下周再来。} @dungeon"
```

位于 `timeout[0].720[0]`，后面紧跟 L7 `$end{type=text;text=§c精英试炼超时失败;reward=false;delay=1;end-type=FAILURE} @dungeon`。

## 2. 核对

- **触发条件：**
  - L1-2 `auto-start: "timeout false timing"`：进本即开始计时。
  - L5 `720`：秒，约 12 分钟。单位按同目录其它本的注释对照：`EmberDaily/task/timeout.yml` 的 720 注「约 12 分钟」，`EmberGuildBoss` 的 900 注「约 15 分钟」，`EmberRaid` 的 2400 注「约 40 分钟」。
  - 到时后先发 L6，1 秒后 L7 以 `end-type=FAILURE` 结束。这是本本唯一的 task，失败提示只有这一处。
- **reward=false 不写通关标记：**
  - 通关标记 `elite_weekly_clear=<周>` 只有一个写入点：`option.yml` L36 reward-script 里的 `corerpg progress %player_name% elite_weekly` → `ProgressService` L471-481。
  - `EliteService.markClearedThisWeek`（L55）全仓库没有调用方。
  - L7 `reward=false` 不跑 `dungeon-reward-script`，所以超时失败不写标记，也不发通关箱、稳定符、战令经验和 quest 事件。
  - 旁证：`EmberAbyss/task/timeout.yml` L1 注释「reward=false 防双箱」。DP jar 不在仓库里，这一条是配置层面的核对，实测记为待恢复服后。
- **同周可以再挑战：**没有标记，`passesGate` 和 `TicketEntryService` L115-121 都会放行。进本仍按首免、体力走 `tryEnter`。
- **「体力已扣」不准：**
  - 本次可能用的是首免（L142），也可能是 OP 管理免扣（L140），都没扣体力。
  - 超时发生在进本约 12 分钟后，早已过了 `refundEnter` 约 2 秒的在本检查，所以不退还。本次的首免或体力确实用掉了，但不一定是体力。
  - 费用仍只由进本时的私聊 costHint 告知，本行不再提。

## 3. 荐案

L6 精确替换为（行首 6 个空格，引号与 `@dungeon` 保持原样）：

```yaml
      - "$message{type=text;text=§c试炼失败。§7这次不算通关，本周还能再来。} @dungeon"
```

- 「这次不算通关」：对应 reward=false 不写标记。
- 「本周还能再来」：对应同周可再进，替换掉原来错误的「下周再来」；语气与 B2.101 L20「没过可以再来」一致。
- 不写扣费、不写数字，保留标题 `§c` 和正文 `§7`。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] `git show --numstat <build>` 只有 `1	1	plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml`，改动行恰为 L6，逐字等于荐案。
- [ ] **js-yaml 对比（`<build>^` 对 `<build>`，断言旧值和新值）**：下面整段可以直接粘贴到 shell 落盘，没有缩进。然后在仓库根目录跑 `node /tmp/chk-b2112.js <build>`。
      - 脚本把 YAML 展平成「路径 → 值」，空对象记作 `{}`、空数组记作 `[]`，所以这两类变化也会被比较。
      - 要求恰好 1 处不同，且路径是 `/timeout/0/720/0`（否则 exit 1）；旧值必须是改前原文（否则 exit 3），新值必须等于荐案（否则 exit 4）。
      - 期望输出 `only-L6 /timeout/0/720/0`。
      - 策划已干跑：以 `WT` 为参数，拿 HEAD 对改后的工作区，输出与期望一致，随后已还原。

```sh
cat > /tmp/chk-b2112.js <<'JS'
const y=require("js-yaml"),{execSync:x}=require("child_process"),fs=require("fs");
const f="plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml",B=process.argv[2];
if(!B){console.error("usage: node /tmp/chk-b2112.js <build>|WT");process.exit(9)}
const load=r=>y.load(r==="WT"?fs.readFileSync(f,"utf8"):x("git show "+r+":"+f).toString());
const a=load(B==="WT"?"HEAD":B+"^"),b=load(B==="WT"?"WT":B);
const flat=(o,p="",m={})=>{if(Array.isArray(o)){if(!o.length)m[p]="[]";o.forEach((v,i)=>flat(v,p+"/"+i,m))}else if(o&&typeof o==="object"){const k=Object.keys(o);if(!k.length)m[p]="{}";k.forEach(q=>flat(o[q],p+"/"+q,m))}else m[p]=JSON.stringify(o);return m};
const A=flat(a),C=flat(b),P="/timeout/0/720/0";
const O=JSON.stringify("$message{type=text;text=§c试炼失败。§7体力已扣，下周再来。} @dungeon");
const N=JSON.stringify("$message{type=text;text=§c试炼失败。§7这次不算通关，本周还能再来。} @dungeon");
const d=[...new Set([...Object.keys(A),...Object.keys(C)])].filter(k=>A[k]!==C[k]);
if(d.length!==1||d[0]!==P){console.log(d);process.exit(1)}
if(A[P]!==O)process.exit(3);
if(C[P]!==N)process.exit(4);
console.log("only-L6 "+P);
JS
```

- [ ] **rg（收窄口径，施工前已对全文件预跑）**：`rg -n "体力已扣|下周再来|已消耗体力|体力 -|扣票|精英票" plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml`
      - 施工前：只命中 L6。
      - 施工后：无命中（干跑 exit 1）。
- [ ] **HANDOFF §8 查密码**（`HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：
      - 精英试炼超时后本内见新 L6，随后 FAILURE 回城。
      - `corerpg elite status` 显示本周未通关。
      - 同周可以再次 `corerpg elite start`。

## 5. 明确不做

- 不改 L7 `$end` 文案与 `reward=false`，不改 720 时长。
- 不改其它本的 task、option.yml、CoreRpg、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」；不暗示精英壳已开。
- **勿 git push**。

## 6. 其它副本 `task/*.yml` 同类文案（只记，不动）

全部 task 文件都已扫过（`rg "体力|扣|下周|票|再来|失败" plugins/DungeonPlus/dungeon/*/task/*.yml`）。七个日常本里，只有 EmberDaily 有 task 目录，Ash/Crypt/Frost/Rail/Spire/Tide 没有。

| 文件:行 | 现文 | 问题 | 建议 |
|---|---|---|---|
| `EmberWeekly/task/timeout.yml:6` | `§c周常超时失败（体力不返还）` | 周本有首免；首免或 OP 进本时没扣体力，「体力不返还」不准 | 与本窗同类，可排一窗：去掉括号，或改成不提费用 |
| `EmberRaid/task/timeout.yml:7` | `§c团本超时失败（体力不返还）` | 同上（团本有首免） | 同上 |
| `EmberDaily/task/timeout.yml:7` | `§c挑战超时，本局失败（体力不返还）` | 日常没有首免，只有 OP 免扣时不准 | 优先级低；可与上两项口径统一 |
| `EmberAbyss/task/timeout.yml:8` | `§c深渊合拢。强制结算最高层（体力不返还）` | 深渊没有首免，只有 OP 时不准；「按最高层结算」是准确的 | 优先级低 |
| `EmberGuildBoss/task/timeout.yml:7` | `§c盟 Boss 超时失败（贡献不返还）` | 走公会贡献，不是体力账户，不属于本类 | 不排 |
| `EmberCalamity/task/timeout.yml:8` | 只有 `$end` 超时失败 | 没有费用字样 | 无 |

如果要排，建议 Weekly 和 Raid 各一窗（每窗一个文件），放在 B2.113 之后、B2.103 之前；Daily 和 Abyss 只记。编号由总控定。

排序（按总控更新）：B2.114 `plugins/CoreRpg/quest.yml:389` → B2.110 → B2.111 → B2.113 →（若排：Weekly、Raid timeout）→ B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.109（B2.107 挂起；src 模板 quest.yml:389 同类，可不排）。


## 总控批注（2026-10-01 03:34 Asia/Shanghai）
- 批荐案「这次不算通关，本周还能再来」：不写扣费/数字，与 B2.101 L20 口径一致。reward=false 不写标记为配置层核对（DP jar 不在仓），可接受。
- §6 同类新排：**B2.115** `EmberWeekly/task/timeout.yml:6`、**B2.116** `EmberRaid/task/timeout.yml:7`（「体力不返还」首免/OP 时不准），排 B2.113 后、B2.103 前；Daily:7 / Abyss:8 只记不排。
- 验收全部静态；reload 实测记「待恢复服后实测」，不挡 PASS。