# B2.102 · TrMenu `ember_hub.yml` L249-L250 精英试炼 lore：限通关口径 + 体力数字改占位符

- **STATUS：PASS · 勾销（总控 · 2026-10-01 03:31 Asia/Shanghai）** · 设计 `4045245` · 批准 `6c2913a` · 插件 `862d21c` · 测 `0884f55` · close 本提交；报告 `docs/tests/TEST-B2.102-hub-elite-lore.md`；reload 实测待恢复服后补。
- **tip 路径：**`docs/design/design-ember-hub-elite-lore-copy.md`
- **目标范围：**仅 `plugins/TrMenu/menus/ember_hub.yml` L249、L250 两行（精英试炼图标 `Icons.2.display.lore[1]`、`[2]`，玩家可见）。其它行、条件图标、actions 全部零改；numstat `2 2`。
- **来由：**B2.101 §3 预案（L249 沿用「每人每周限通关」句式）；总控 03:20 定：L250 写死的 40 并入本窗。
- **施工岗：**批 A 后交 **插件岗（TrMenu 文案）**。
- **本窗纪律：**一文件一窗；不改数值；NI 票物保留；不暗示精英壳已开；勿 git push。

## 1. 现行两行（完整旧行）

```yaml
        - '§7每周 1 次 · 词缀精英 · 周首通稳定符'
        - '§8需余烬 Lv.40 · 消耗 §e40 §8体力（本周首次免费）'
```

## 2. 核对

- **L249 口径：**同 B2.101 §2。`EliteService.passesGate` 限的是通关（标记 `elite_weekly_clear=<周>`，按人写）；没通关同周可以再进，第一次用首免，之后扣体力。「每周 1 次」会读成每周只能进一次。
- **L250 占位符（已有，荐用）：**
  - `CoreRpgExpansion` L103：`stamina_cost_elite` → `StaminaService.costOf("elite")`。
  - `costOf` 读的是 `StaminaService.reload` 从 cash.yml `stamina.costs` 装入的表（L110-119），现行 `elite: 40`。
  - 同一文件同一图标的灰显分支 L235、L240 已经在用 `%corerpg_stamina_cost_elite%`，`ember_abyss.yml:63`、`ember_daily.yml` 多处 lore 也在用。
  - 本图标有 `update: 20` / `refresh: 20`，lore 会定时刷新。
  - 所以不用去掉数字：换成占位符后，数值跟 cash.yml 走，改价时无需同步菜单。
- **「（本周首次免费）」保留：**这是规则说明，与 `weeklyGrantCreditElite` 一致。本周免费用完后，体力不足时灰显分支（L227-240）会接管，提示「本周免费已用完」。
- **OP 冲突：**OP 走 `TicketEntryService` L140「管理免扣」，与 lore 写的价不同。本窗**不处理**，理由如下：
  - lore 面向玩家写规则，OP 免扣只是管理测试通道，私聊 costHint 才是权威的费用信息。
  - 要按 OP 显示不同 lore，得加一个 condition 图标分支，属于结构改动，超出两行文案的范围。
  - 其余菜单（L197、L235 和 abyss、daily）也都不区分 OP，口径一致。
  - 冲突点记录在本节，不另排。

## 3. 荐案（选「换占位符」这条路）

L249、L250 精确替换为（行首 8 个空格，单引号保持）：

```yaml
        - '§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符'
        - '§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）'
```

- L249：只把「每周 1 次」改成「每人每周限通关 1 次」。「1」是通关次数，不是费用。lore 行宽有限，不加「没过可以再来」（B2.101 的开本句里已经有）。
- L250：只把 `40` 换成 `%corerpg_stamina_cost_elite%`，颜色码 `§e`/`§8` 不动。`Lv.40` 是等级门，不在本窗范围。
- 备选（不荐）：去掉数字，写成「消耗体力（本周首次免费）」。已有现成占位符，去掉数字反而丢信息。

## 4. 施工与验收（静态；本机无运行中的服）

- [ ] `git show --numstat <build>` 只有 `2	2	plugins/TrMenu/menus/ember_hub.yml`，改动行恰为 L249、L250，逐字等于荐案。
- [ ] **YAML 对比（`<build>^` 对 `<build>`，断言两行旧值与新值）**：
      - 把下面脚本存为 `/tmp/chk-b2102.js`，在仓库根目录跑 `node /tmp/chk-b2102.js <build>`。
      - 脚本把整个 YAML 展平成「路径 → 值」再比较：要求恰好 2 处不同；旧值必须是改前原文（否则 exit 3），新值必须等于荐案（否则 exit 4）；不同处多于或少于 2 则 exit 1。
      - 期望输出 `only-L249-L250 /Icons/2/display/lore/1 /Icons/2/display/lore/2`。
      - 策划已干跑：以 `WT` 为参数，拿 HEAD 对改后的工作区，输出与期望一致，随后已还原。

```js
    const y=require("js-yaml"),{execSync:x}=require("child_process"),fs=require("fs");
    const f="plugins/TrMenu/menus/ember_hub.yml",B=process.argv[2];
    const load=r=>y.load(r==="WT"?fs.readFileSync(f,"utf8"):x("git show "+r+":"+f).toString());
    const a=load(B==="WT"?"HEAD":B+"^"),b=load(B==="WT"?"WT":B);
    const flat=(o,p="",m={})=>{if(o&&typeof o==="object"){for(const k of Object.keys(o))flat(o[k],p+"/"+k,m)}else m[p]=o;return m};
    const A=flat(a),C=flat(b);
    const exp=[["§7每周 1 次 · 词缀精英 · 周首通稳定符","§7每人每周限通关 1 次 · 词缀精英 · 周首通稳定符"],["§8需余烬 Lv.40 · 消耗 §e40 §8体力（本周首次免费）","§8需余烬 Lv.40 · 消耗 §e%corerpg_stamina_cost_elite% §8体力（本周首次免费）"]];
    const keys=new Set([...Object.keys(A),...Object.keys(C)]),d=[...keys].filter(k=>A[k]!==C[k]);
    if(d.length!==2){console.log(d);process.exit(1)}
    d.forEach((k,i)=>{if(A[k]!==exp[i][0])process.exit(3);if(C[k]!==exp[i][1])process.exit(4)});
    console.log("only-L249-L250 "+d.join(" "));
```

- [ ] **rg**（施工前已对全文件预跑）：
      - `rg -n "每周 1 次|消耗 §e40" plugins/TrMenu/menus/ember_hub.yml`：施工前只命中 L249、L250；施工后无命中（干跑 exit 1）。
      - `rg -n "已消耗体力|体力 -|扣票|精英票" plugins/TrMenu/menus/ember_hub.yml`：施工前后都无命中。
      - `rg -n "%corerpg_stamina_cost_elite%" plugins/TrMenu/menus/ember_hub.yml`：施工后命中 L235、L240、L250。
- [ ] **HANDOFF §8 查密码**（`docs/handoff/HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：`trm reload` 后打开枢纽，精英试炼 lore 显示「消耗 40 体力（本周首次免费）」，数字由占位符解析；把 cash.yml `stamina.costs.elite` 临时改值后 reload，lore 应跟着变（可选）。

## 5. 明确不做

- 不改 L227-240 灰显分支、actions、其它图标；不加 OP 分支；不改 `Lv.40`。
- 不动 DP、CoreRpg、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」。
- **勿 git push**。

## 6. 顺带发现（同文件，只记）

预跑 `rg "消耗 §e[0-9]"` 发现本文件还有 3 处写死的体力数字，与 L250 是同类问题，而且都已有现成占位符（`CoreRpgExpansion` L101-104）：
- L215 周本 `'§8进本消耗 §e45 §8体力 · 通关回枢纽'` → `%corerpg_stamina_cost_weekly%`
- L266 深渊 `'§8消耗 §e30 §8体力 · 进本扣体力'` → `%corerpg_stamina_cost_abyss%`
- L280 团本 `'§8消耗 §e50 §8体力（本周首次免费）· 3～5 人'` → `%corerpg_stamina_cost_raid%`

建议另开一窗（同一文件，3 行换占位符），排在 B2.111 之后、B2.103 之前。编号由总控定。

排序（按总控更新）：B2.112 EliteWeekly `task/timeout.yml:6` → B2.110 → B2.111 →（上述 hub 三行新窗）→ B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.109（B2.107 挂起）。


## 总控批注（2026-10-01 03:22 Asia/Shanghai）
- 批荐案：占位符方案优于去数字（`CoreRpgExpansion.java:103` 现成，L235/L240 已在用）。OP 冲突只记不改。
- 同文件 L215（周本 45）/ L266（深渊 30）/ L280（团本 50）硬编码 → 另开 **B2.113**（仅改 ember_hub.yml 这三行为 `stamina_cost_weekly/abyss/raid` 占位符），排 B2.111 后、B2.103 前。
- 验收全部静态；reload 实测记「待恢复服后实测」，不挡 PASS。