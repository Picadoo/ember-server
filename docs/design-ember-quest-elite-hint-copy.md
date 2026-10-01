# B2.114 · live `plugins/CoreRpg/quest.yml` L389 主线 hint「精英试炼（每周 1 次）」→ 每人每周限通关 1 次

- **STATUS：已批 A（总控 · 2026-10-01 13:02 Asia/Shanghai）· 交插件岗**（荐案 L389 `hint: 打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）`，numstat `1 1`）
- **tip 路径：**`docs/design-ember-quest-elite-hint-copy.md`
- **目标范围：**只改 live `plugins/CoreRpg/quest.yml` L389 这一行的 `hint` 值（玩家可见），numstat `1 1`。src 模板 `CoreRpg/src/main/resources/quest.yml:389` 是同一句，本窗不动，与 B2.107 归为同一类。
- **来由：**测岗报告；总控 03:31 排为 B2.114。口径与 B2.101（DP 开本句）、B2.102（hub L249）一致。
- **施工岗：**批 A 后交 **插件岗（CoreRpg live 配置文案）**。
- **本窗纪律：**一文件一窗；不改数值、xp、事件；不写扣费和费用数字；NI 票物保留；勿 git push。

## 1. 现行 L389（完整旧行）

```yaml
        hint: 打开枢纽菜单 → 精英试炼（每周 1 次）
```

- 位于 `chapters.9.steps[2]`（第二层誓火，`type: event`，`event: elite_weekly_clear`，`count: 1`，`desc: 通关 余烬·精英试炼 一次`）。
- 「每周 1 次」会被读成每周只能进一次。现行规则是每人每周限通关 1 次，没通关同周可以再进（见 B2.101 §2、B2.112 §2）。

## 2. 展示路径核对（`QuestService.java`）

- **读取：**`reload()`（L122-）读 `getDataFolder()/quest.yml`，也就是 live 的 `plugins/CoreRpg/quest.yml`；只有文件缺失时才从 src 模板 `saveResource`（L124）。所以玩家看到的是 live 文件，只改 live 就生效。
- **装载：**L168 `s.hint = color(str(m.get("hint"), ""))`。`str`（L184）只是 `String.valueOf`，`color`（L189）只转换 `&` 颜色码，都**不截断、不限长**。
- **展示，三处都是整串输出到聊天：**
  - L293 `announceStep`：接在「目标：」后面，写成 `（hint）`，外层已有全角括号，因此 hint 里的括号会成为嵌套括号，见 §3。
  - L471 NPC 交谈未完成时：单独一行 `  hint`。
  - L569 `/quest` 查看：单独一行 `  提示：hint`。
  - 三处都用 `ChatColor.stripColor` 去掉颜色后整串发送，没有 `substring` 或长度判断（全文件的 `substring` 只在 L452、L1155，与 hint 无关）。
- **不进 actionBar 和侧栏：**L294、L439 的 actionBar 和 PAPI `%corerpg_quest%`/`quest_objective`（`CoreRpgExpansion` L21）都走 `objective()`（L202-209），只用 `desc` 加进度，不含 hint。所以 hint 长一点不会挤占 actionBar。

## 3. 荐案

L389 精确替换为（行首 8 个空格，无引号，与原行一致）：

```yaml
        hint: 打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）
```

- 只把括号里的「每周 1 次」改成「每人每周限通关 1 次」，与 hub L249「每人每周限通关 1 次」逐字同源。
- 「1」是通关次数，不是费用。不写扣费、不写体力。
- L293 展示效果为「目标：通关 余烬·精英试炼 一次（打开枢纽菜单 → 精英试炼（每人每周限通关 1 次））」。嵌套括号是原行就有的，本窗不改结构。
- 值里没有 `:`、`#` 等 YAML 特殊起始字符，保持无引号写法；js-yaml 解析已确认。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] `git show --numstat <build>` 只有 `1	1	plugins/CoreRpg/quest.yml`，改动行恰为 L389，逐字等于荐案；src 模板没有改动。
- [ ] **js-yaml 对比（`<build>^` 对 `<build>`，断言旧值和新值）**：下面整段可以直接粘贴到 shell 落盘（无缩进），然后在仓库根目录跑 `node /tmp/chk-b2114.js <build>`。
      - 脚本把 YAML 展平成「路径 → 值」，空对象记作 `{}`、空数组记作 `[]`，所以这两类变化也会被比较。
      - 要求恰好 1 处不同，且路径是 `/chapters/9/steps/2/hint`，否则 exit 1；旧值不是改前原文 exit 3；新值不等于荐案 exit 4。
      - 期望输出 `only-L389 /chapters/9/steps/2/hint`。
      - 策划已干跑：以 `WT` 为参数，拿 HEAD 对改后的工作区，输出与期望一致，随后已还原。

```sh
cat > /tmp/chk-b2114.js <<'JS'
const y=require("js-yaml"),{execSync:x}=require("child_process"),fs=require("fs");
const f="plugins/CoreRpg/quest.yml",B=process.argv[2];
if(!B){console.error("usage: node /tmp/chk-b2114.js <build>|WT");process.exit(9)}
const load=r=>y.load(r==="WT"?fs.readFileSync(f,"utf8"):x("git show "+r+":"+f).toString());
const a=load(B==="WT"?"HEAD":B+"^"),b=load(B==="WT"?"WT":B);
const flat=(o,p="",m={})=>{if(Array.isArray(o)){if(!o.length)m[p]="[]";o.forEach((v,i)=>flat(v,p+"/"+i,m))}else if(o&&typeof o==="object"){const k=Object.keys(o);if(!k.length)m[p]="{}";k.forEach(q=>flat(o[q],p+"/"+q,m))}else m[p]=JSON.stringify(o);return m};
const A=flat(a),C=flat(b),P="/chapters/9/steps/2/hint";
const O=JSON.stringify("打开枢纽菜单 → 精英试炼（每周 1 次）");
const N=JSON.stringify("打开枢纽菜单 → 精英试炼（每人每周限通关 1 次）");
const d=[...new Set([...Object.keys(A),...Object.keys(C)])].filter(k=>A[k]!==C[k]);
if(d.length!==1||d[0]!==P){console.log(d);process.exit(1)}
if(A[P]!==O)process.exit(3);
if(C[P]!==N)process.exit(4);
console.log("only-L389 "+P);
JS
```

- [ ] **rg（施工前已对全文件预跑）**：
      - `rg -n "每周 1 次" plugins/CoreRpg/quest.yml`：施工前只命中 L389；施工后无命中（干跑 exit 1）。
      - `rg -n "已消耗体力|体力 -|扣票|精英票" plugins/CoreRpg/quest.yml`：施工前后都无命中。
- [ ] **HANDOFF §8 查密码**（`HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：重载 CoreRpg 配置（或重启）后，让处在第二层誓火第 3 步的玩家打 `/quest`、和灰烛交谈，各见一次新 hint；步骤切换时 announceStep 那一行也显示新 hint。

## 5. 明确不做

- 不改 src 模板 `CoreRpg/src/main/resources/quest.yml`（与 B2.107 同类，挂起）。
- 不改 desc、xp、done、event、count，不改其它章节。
- 不动 DP、TrMenu、CoreRpg 代码、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」；不暗示精英壳已开。
- **勿 git push**。

## 6. quest.yml 里的同类文案（只记，不改）

扫描命令：`rg -n "每周 1 次|每周一次|体力|[0-9]+ ?体力" plugins/CoreRpg/quest.yml`。

| 行 | 现文 | 说明 | 建议 |
|---|---|---|---|
| L377 | `done: ['§6灰烛：§f试炼每周一次。稳定符在周首通里。']` | 同一章第 1 步完成时 NPC 说的话，玩家可见。「试炼每周一次」与 L389 是同一个问题 | **建议排**：同一文件，可并入本窗（改两行）或单开一窗，由总控定。候选句：「试炼每人每周限通关一次。稳定符在周首通里。」 |
| L134 | `intro: '§6灰烛：§f每周一次，地窟深处的火会醒来。…'` | 周本章节开场白，属于氛围叙述；周本也有首免后扣体力再进的规则 | 优先级低，只记 |
| L73 | `…地窟的门每天大约开三次（日回体力约可刷 3 次日常，主线再送 1 瓶体力药）…` | 「3 次」由 `stamina.base_max: 90` ÷ `costs.daily: 30` 推出，不是写死的单价，但会随数值变 | 只记；改数值时要同步 |
| L243 | `hint: 日回体力约可刷 3 次日常` | 同 L73 | 只记 |
| L81、L180、L335、L355 | 「耗体力」「日回体力」等 | 没有数字，口径准确 | 无 |

quest.yml 里没有写死的体力单价（没有「消耗 N 体力」「体力 -N」这类写法）。

排序（按总控更新）：B2.110 → B2.111 → B2.113 →（若排：Weekly/Raid timeout、quest L377）→ B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.109（B2.107 挂起，src quest.yml:389 同类）。

## 总控批注（2026-10-01 13:02 Asia/Shanghai）
- **批 A**：只改 live `plugins/CoreRpg/quest.yml` L389 一行，按荐案逐字施工（行首 8 空格、不加引号），YAML 路径 `/chapters/9/steps/2/hint`。src 模板不动（与 B2.107 同列）。
- hint 展示路径核对认可：L168 读入不截断，L293/L471/L569 整串发聊天，actionBar/PAPI 走 `objective()` 不含 hint；L293 外层全角括号嵌套为原有结构，本窗不改。
- **L377 不并入本窗**：与 L389 不相邻，保持一窗一行、验收脚本不重出。另开 **B2.117**（L377 done「试炼每周一次」→「试炼每人每周限通关一次。稳定符在周首通里。」），排在 B2.116 后、B2.103 前。
- L134 周本氛围句、L73/L243「约 3 次日常」只记不排。
- 验收：稿中 heredoc js-yaml 脚本（`<build>^` 对 `<build>`，旧 exit 3 / 新 exit 4 / 多处差异 exit 1）、rg 全文件预跑、HANDOFF.md §8 计数 0、`ops.json` 为 `[]`；reload 记「待恢复服后实测」。
- 施工岗：插件岗，本地 commit，不 push、不 reload。
