# B2.110 · TrMenu `plugins/TrMenu/menus/ember_raid.yml` L76-L77 进本按钮 click tell → 中性「尝试进入」、去扣费数字

- **STATUS：PASS · 勾销（总控 · 2026-10-01 13:25 Asia/Shanghai）**（设计 `4aff2ee` · 批准 `a63f4ac` · 插件 `22b1084` · 测 `e405013` · close 本提交；报告 `docs/tests/TEST-B2.110-raid-menu-enter-tell.md`）
- **tip 路径：**`docs/design/design-ember-raid-menu-enter-copy.md`
- **目标范围：**只改 `plugins/TrMenu/menus/ember_raid.yml` L76、L77 两行（相邻，并为一窗），numstat `2	2`。L78 等级句只记不动。
- **来由：**总控 13:12 派单（B2.114 结案后）。
- **施工岗：**批 A 后交 **插件岗（TrMenu 菜单文案）**。
- **本窗纪律：**一文件一窗；不改数值、条件、动作顺序；不写扣费和费用数字；NI 票物保留；勿 git push。

## 1. 现行 L76-L77（完整旧行）

```yaml
        - 'tell: §9团本已点燃。§7分路推进——左卫兵、右射手，汇合后再闯终厅。'
        - 'tell: §8消耗 §e50 §8体力 · 人数 3～5'
```

- 位于 `Icons.S.actions.all`（「开始协作」beacon 默认态）第 2、3 项，YAML 路径 `/Icons/S/actions/all/1`、`/Icons/S/actions/all/2`。
- 后面是 L78 `tell: §8需要余烬等级 §eLv.35§8（见菜单等级要求）`、L79 `command: corerpg enter raid`、L80 `close`。
- 问题：tell 在 `corerpg enter raid` 之前无条件发出。等级被拒、DP 人数被拒、周首免、OP 免扣时，玩家都会先看到「已点燃」和「消耗 50」，与随后插件私聊对不上。

## 2. 代码核对：`corerpg enter raid` 各情况私聊什么

`TicketEntryService.tryEnter`（L100-203）+ `StaminaService.consumeForEnter`（L293-327）。只校验、只扣发起者。

| 情况 | 插件私聊（发起者） | 是否扣 |
|---|---|---|
| 等级不足（非 OP，L109-113） | `§c余烬团本需要余烬 Lv.35§7（当前 Lv.x）` | 不扣，直接返回 |
| 体力不足（非 OP，无周免，L131-134 / Stamina L319-322） | `§c体力不足（需 50，当前 x/上限）· 明日 0 点恢复` | 不扣 |
| 周首免（Stamina L311-315，`weeklyGrantCreditRaid` 抵扣） | `§e[团本] §7正在进入……（本周首次免费）`（L148-149，L142） | 抵扣 1 次周免 |
| 付费 | `§e[团本] §7正在进入……（体力 -50）`（L144） | 扣 50（cash.yml `stamina.costs.raid`） |
| OP / `corerpg.admin` | `§e[团本] §7正在进入……（管理免扣）`（L140），且跳过等级门 | 不扣 |
| DP 启动失败或约 2 秒后不在副本（含 DP 人数 3～5 / 队员等级门拒绝） | DP 自己的红字（option.yml L16 人数、L18 等级）+ `§e出本后再进约等 5 秒（缓存冷却），不是进本坏了`，非短时重试再加 `§7若仍进不去，请稍后再试（体力已退还，若已扣）`（L205-211） | `refundEnter` 退还体力/周免 |

- 菜单侧：`S` 的 priority 2 子图标（L42-55）在 `%corerpg_stamina_blocked_raid% > 0` 时接管点击，只发 L55 体力不足提示、不执行 L76-L79。所以走到 L76 的人可能是：等级不足、周首免、付费、OP、人数不对（以及 20 tick 刷新间隙里的体力不足）。
- 结论：扣费和是否免费，插件私聊已经说清楚；菜单 tell 只需要说「在尝试」，不需要也不应该提前报价或宣布已开。

## 3. 与 DP 开本提示的重复核对（`plugins/DungeonPlus/dungeon/EmberRaid/option.yml`）

- L21 `§9团本大厅已集结。§7左道卫兵 · 右道射手 · 汇合后终厅使徒。`（真正进本后对全队发）
- L22 `§8通关箱 · 全队每人结算 · 周首通保底团戒`（B2.99）
- 新句不提奖励，与 L22 **不重复**。
- 旧 L76 的分路提示「左卫兵、右射手，汇合后再闯终厅」和 L21 是同一条路线：发起者在几秒内会先后看到两遍，而且 L76 只发给点按钮的人，L21 发给全队。菜单 lore L68 也已有路线。因此荐案把战术提示交给 L21，L76 只留中性句。
- 「已点燃 / 已集结」这类宣布已开的说法，只留在 DP L21（真正进本才发）。

## 4. 荐案

L76、L77 精确替换为（行首 8 个空格，单引号，与原行一致）：

```yaml
        - 'tell: §9[团本] §7尝试进入……'
        - 'tell: §8人数 3～5'
```

- L76：中性句，不说已点燃，不说扣费。前缀 `§9[团本]` 与本菜单 L22 Open tell 同色同写法；句式参照 `ember_abyss.yml` L77「§5[深渊] §7尝试下潜……」。
- 拼起来的效果：
  - 等级不足：「[团本] 尝试进入…… / 人数 3～5 / 需要余烬等级 Lv.35（见菜单等级要求）/ 余烬团本需要余烬 Lv.35（当前 Lv.30）」，不再出现「已点燃」「消耗 50」。
  - 周首免：「[团本] 尝试进入…… / … / [团本] 正在进入……（本周首次免费）」，扣费口径只有插件这一句。
  - OP：「… / [团本] 正在进入……（管理免扣）」。
- L77：去掉「消耗 §e50 §8体力」，只留人数；人数 3～5 与 DP L16 `min=3;max=5` 一致。
- 备选（总控若想保留战术句）：L76 `'tell: §9[团本] §7尝试进入……分路推进：左卫兵、右射手，汇合后再闯终厅。'`，代价是与 DP L21 路线重复一遍。策划推荐主案。

## 5. 施工与验收（静态；本机无运行中的服）

- [ ] `git show --numstat <build>` 只有 `2	2	plugins/TrMenu/menus/ember_raid.yml`，改动行恰为 L76、L77，逐字等于荐案；L78-L80 不变。
- [ ] **js-yaml 对比（`<build>^` 对 `<build>`，断言旧值和新值）**：下面整段可以直接粘贴到 shell 落盘（无缩进），然后在仓库根目录跑 `node /tmp/chk-b2110.js <build>`。
      - 脚本把 YAML 展平成「路径 → 值」，空对象记作 `{}`、空数组记作 `[]`，所以这两类变化也会被比较。
      - 要求恰好 2 处不同，且路径是 `/Icons/S/actions/all/1`、`/Icons/S/actions/all/2`，否则 exit 1；任一旧值不是改前原文 exit 3；任一新值不等于荐案 exit 4；`/Icons/S/actions/all/4` 不是 `command: corerpg enter raid` 或前后不一致 exit 5。
      - 期望输出 `only-L76-L77 /Icons/S/actions/all/1 /Icons/S/actions/all/2`。
      - 策划已干跑：以 `WT` 为参数，拿 HEAD 对改后的工作区，输出与期望一致，随后已还原。

```sh
cat > /tmp/chk-b2110.js <<'JS'
const y=require("js-yaml"),{execSync:x}=require("child_process"),fs=require("fs");
const f="plugins/TrMenu/menus/ember_raid.yml",B=process.argv[2];
if(!B){console.error("usage: node /tmp/chk-b2110.js <build>|WT");process.exit(9)}
const load=r=>y.load(r==="WT"?fs.readFileSync(f,"utf8"):x("git show "+r+":"+f).toString());
const a=load(B==="WT"?"HEAD":B+"^"),b=load(B==="WT"?"WT":B);
const flat=(o,p="",m={})=>{if(Array.isArray(o)){if(!o.length)m[p]="[]";o.forEach((v,i)=>flat(v,p+"/"+i,m))}else if(o&&typeof o==="object"){const k=Object.keys(o);if(!k.length)m[p]="{}";k.forEach(q=>flat(o[q],p+"/"+q,m))}else m[p]=JSON.stringify(o);return m};
const A=flat(a),C=flat(b);
const E={
"/Icons/S/actions/all/1":["tell: §9团本已点燃。§7分路推进——左卫兵、右射手，汇合后再闯终厅。","tell: §9[团本] §7尝试进入……"],
"/Icons/S/actions/all/2":["tell: §8消耗 §e50 §8体力 · 人数 3～5","tell: §8人数 3～5"]};
const d=[...new Set([...Object.keys(A),...Object.keys(C)])].filter(k=>A[k]!==C[k]).sort();
const want=Object.keys(E).sort();
if(JSON.stringify(d)!==JSON.stringify(want)){console.log(d);process.exit(1)}
for(const k of want){if(A[k]!==JSON.stringify(E[k][0]))process.exit(3);if(C[k]!==JSON.stringify(E[k][1]))process.exit(4)}
if(A["/Icons/S/actions/all/4"]!==JSON.stringify("command: corerpg enter raid")||C["/Icons/S/actions/all/4"]!==A["/Icons/S/actions/all/4"])process.exit(5);
console.log("only-L76-L77 "+want.join(" "));
JS
```

- [ ] **rg（施工前已对全文件预跑）**：
      - `rg -n "已点燃|消耗 §e50|体力 -|已消耗体力|扣票|团本票" plugins/TrMenu/menus/ember_raid.yml`：施工前只命中 L76、L77；施工后无命中（干跑 exit 1）。
- [ ] **HANDOFF §8 查密码**（`docs/handoff/HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：`trm reload`（或重启）后，用 Lv.35 以下非 OP 号、周首免号、OP 号各点一次「开始协作」，确认聊天只有新两行 + L78 + 对应插件私聊，没有「已点燃」「消耗 50」。

## 6. 明确不做

- 不改 L78 等级句（只记），不改 L79 命令、L80 close、动作顺序。
- 不改 `S` 灰显子图标（L42-55）、lore、`P`、`R`、Open 事件。
- 不动 DP option.yml、CoreRpg 代码、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」；不暗示精英壳已开。
- **勿 git push**。

## 7. 同文件其它写死的「50」（只记，不改）

扫描命令：`rg -n "50" plugins/TrMenu/menus/ember_raid.yml`（施工前命中 L2、L22、L77、L91、L109、L118）。

| 行 | 现文 | 说明 | 建议 |
|---|---|---|---|
| L22 | `tell: §9[团本] §7需 3～5 人 · 消耗 50 体力（本周首次免费）` | 打开菜单时的说明，含首免口径，准确；但 50 写死，不跟 cash.yml | 可排：换 `%corerpg_stamina_cost_raid%`（TrMenu tell 支持 PAPI，见 L55） |
| L109 | `§7本周首次免费 · 其后 50 体力` | R 图标 lore，同上 | 同上，可与 L118 并一窗（不相邻，需总控定） |
| L118 | `tell: §9团本本周首次免费，其后 50 体力；需 3～5 人。` | R 图标点击，同上 | 同上 |
| L2 | 注释 | 不对玩家 | 不动 |
| L91 | `锋利石约 50%` | 掉率，不是费用 | 不动 |

- L66「失败不返还」、L111「进本即扣 · 失败不返还」：指进本后打输不退，与 refundEnter（未进本才退）一致，口径准确，不动。

排序（按总控更新）：B2.110 → B2.111 → B2.113 → B2.115/B2.116（Weekly/Raid timeout）→ B2.117 → B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.109（B2.107 挂起）。若总控要处理 §7 的 L22/L109/L118，建议排在 B2.113 后，与 hub 换占位符同类。

## 总控批注（2026-10-01 13:15 Asia/Shanghai）
- **批 A（主案）**：只改 `plugins/TrMenu/menus/ember_raid.yml` L76、L77 两行（相邻，一窗），行首 8 空格、单引号，逐字按荐案；YAML 路径 `/Icons/S/actions/all/1`、`/Icons/S/actions/all/2`。L78 等级句、L79 `command: corerpg enter raid`、L80 `close` 不动。备选（保留战术句）不采用：战术路线由 DP option.yml L21 与 lore L68 承担。
- 代码核对认可：TicketEntryService.tryEnter L100-203 / StaminaService.consumeForEnter L293-327 对等级不足、体力不足、周首免、付费、OP、DP 启动失败退还均有私聊，菜单不必写扣费；blocked_raid>0 时灰显子图标接管点击。
- **旁记排期**：同文件 L22/L109/L118 写死「50」→ `%corerpg_stamina_cost_raid%`，另开 **B2.118**，排在 B2.113 后（同类占位符替换）。L66/L111「失败不返还」口径准确，不动。
- 验收：稿中 heredoc `/tmp/chk-b2110.js`（`<build>^` 对 `<build>`，两条路径新旧值断言，`/Icons/S/actions/all/4` 仍为 enter raid 否则 exit 5）、rg 全文件预跑、docs/handoff/HANDOFF.md §8 计数 0、`ops.json` 为 `[]`；reload 记「待恢复服后实测」。
- 施工岗：插件岗，本地 commit，不 push、不 reload。
