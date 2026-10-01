# B2.111 · TrMenu `plugins/TrMenu/menus/ember_abyss.yml` L77 进本按钮 click tell → 去掉写死的「体力 30」和「等级不足不扣」

- **STATUS：已批 A（总控 · 2026-10-01 13:28 Asia/Shanghai）· 交插件岗**（方案 A：L77 `'tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25）'`，numstat `1 1`）
- **tip 路径：**`docs/design-ember-abyss-menu-enter-copy.md`
- **目标范围：**只改 `plugins/TrMenu/menus/ember_abyss.yml` L77 这一行，numstat `1	1`。L78 撤离句、L79 command、L80 close 不动。
- **来由：**总控 13:26 派单（B2.110 结案后）。口径与 B2.110（团本 L76-L77）一致。
- **施工岗：**批 A 后交 **插件岗（TrMenu 菜单文案）**。
- **本窗纪律：**一文件一窗；不改数值、条件、动作顺序；不写扣费和费用数字；NI 票物保留；勿 git push。

## 1. 现行 L77（完整旧行）

```yaml
        - 'tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）'
```

- 位于 `Icons.S.actions.all`（「开始下潜」obsidian 默认态）第 2 项，YAML 路径 `/Icons/S/actions/all/1`。
- 后面是 L78 `tell: §8需要撤离：打开本菜单 → 上浮撤离`、L79 `command: corerpg enter abyss`（路径 `/Icons/S/actions/all/3`）、L80 `close`。
- tell 在 `corerpg enter abyss` 之前无条件发出，所以括号里的话对所有点击者都会说一遍。

## 2. 代码核对：`corerpg enter abyss` 各情况私聊什么

`TicketEntryService.tryEnter`（L100-203）+ `StaminaService.consumeForEnter`（L293-327）。只校验、只扣发起者。

| 情况 | 插件私聊（发起者） | 是否扣 |
|---|---|---|
| 等级不足（非 OP，L109-113；`progress.yml` `level_gates.abyss: 25`） | `§c余烬深渊需要余烬 Lv.25§7（当前 Lv.x）` | 不扣，在 consume 之前返回 |
| 体力不足（非 OP，Stamina L319-322） | `§c体力不足（需 30，当前 x/上限）· 明日 0 点恢复` | 不扣 |
| 付费 | `§e[深渊] §7正在进入……（体力 -30）`（L144；cash.yml `stamina.costs.abyss: 30`） | 扣 30 |
| OP / `corerpg.admin` | `§e[深渊] §7正在进入……（管理免扣）`（L140），且跳过等级门 | 不扣 |
| DP 启动失败或约 2 秒后不在副本（含 DP L16 人数 1～2、L19 **队员**等级门） | DP 红字 + 缓存冷却提示（L205-211） | 先扣后 `refundEnter` 退还 |

- **深渊没有周首免**：`consumeForEnter` 的周免抵扣只有 WEEKLY（L301）、ELITE（L306）、RAID（L311）三支，ABYSS 直接按 cost 扣（与 DP option.yml L3 注释「深渊无首免」、菜单 L40 注释一致）。所以「体力 30」在周首免情况下不会错，**错在 OP**（实际管理免扣），以及 cash.yml 改价时不跟。
- **「等级不足不扣」核实**：
  - 发起者本人等级不足：准确，L109-113 在扣体力之前就返回。
  - 队员等级不足：不准确。发起者已经扣了 30，DP L19 拒绝后约 2 秒由 `refundEnter` 退还，是「扣了再退」而不是「不扣」。
  - OP：跳过等级门，这句话不适用。
  - 结论：这是一句扣费说明，而插件私聊（以及退还时的「体力已退还，若已扣」）已经覆盖所有情况，荐案删掉。
- 菜单侧：`S` 的 priority 2 子图标（L41 `%corerpg_stamina% < 30`）在体力不足时接管点击，不执行 L77-L79。

## 3. 两种方案对比

| 方案 | 写法 | 结论 |
|---|---|---|
| A 去掉体力数字（**荐**） | `（需余烬 Lv.25）` | 不报价，扣费只由插件私聊 costHint 说；OP、改价都不会错；与 B2.110 团本口径一致 |
| B 换占位符 | `（需余烬 Lv.25 · 体力 %corerpg_stamina_cost_abyss%）` | 能跟 cash.yml；但 OP 点击仍会看到「体力 30」，紧接着私聊「管理免扣」，矛盾还在 |

- PAPI 在 TrMenu tell 里能否解析：本机没有 TrMenu jar，代码无法直接读；但现行配置已有多处 tell 用 `%corerpg_…%`（`ember_raid.yml` L55、`ember_hub.yml` L240、`ember_daily.yml` L68 等 7 处、`ember_flex_skill.yml` L22），`plugins/TrMenu/kether.yml` 存在，说明是 TrMenu 3 的 Kether 动作体系。方案 B 技术上大概率可行，但因为 OP 矛盾，策划不推荐。
- 占位符 `stamina_cost_abyss` 在 `CoreRpgExpansion` L102 存在（`st.costOf("abyss")`）。

## 4. 荐案（方案 A）

L77 精确替换为（行首 8 个空格，单引号，与原行一致）：

```yaml
        - 'tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25）'
```

- 保留「尝试下潜……」和等级要求。Lv.25 与 `progress.yml` `level_gates.abyss: 25`、DP L19 红字、菜单 lore L71「需余烬 Lv.25」一致。
- 去掉「· 体力 30；等级不足不扣」：不写扣费数字，也不写扣费规则。
- 拼起来的效果：
  - 付费：「[深渊] 尝试下潜……（需余烬 Lv.25）/ 需要撤离：… / [深渊] 正在进入……（体力 -30）」。
  - OP：「… / [深渊] 正在进入……（管理免扣）」，不再先说体力 30。
  - 本人等级不足：「… / 余烬深渊需要余烬 Lv.25（当前 Lv.20）」。
- DP 开本提示（option.yml L22-23「深渊已开启…」「需要撤离：…体力不退」）只在真正进本后发，与新句不重复。

## 5. 施工与验收（静态；本机无运行中的服）

- [ ] `git show --numstat <build>` 只有 `1	1	plugins/TrMenu/menus/ember_abyss.yml`，改动行恰为 L77，逐字等于荐案；L78-L80 不变。
- [ ] **js-yaml 对比（`<build>^` 对 `<build>`，断言旧值和新值）**：下面整段可以直接粘贴到 shell 落盘（无缩进），然后在仓库根目录跑 `node /tmp/chk-b2111.js <build>`。
      - 脚本把 YAML 展平成「路径 → 值」，空对象记作 `{}`、空数组记作 `[]`，所以这两类变化也会被比较。
      - 要求恰好 1 处不同，且路径是 `/Icons/S/actions/all/1`，否则 exit 1；旧值不是改前原文 exit 3；新值不等于荐案 exit 4；`/Icons/S/actions/all/3` 不是 `command: corerpg enter abyss` 或前后不一致 exit 5。
      - 期望输出 `only-L77 /Icons/S/actions/all/1`。
      - 策划已干跑：以 `WT` 为参数，拿 HEAD 对改后的工作区，输出与期望一致，随后已还原。

```sh
cat > /tmp/chk-b2111.js <<'JS'
const y=require("js-yaml"),{execSync:x}=require("child_process"),fs=require("fs");
const f="plugins/TrMenu/menus/ember_abyss.yml",B=process.argv[2];
if(!B){console.error("usage: node /tmp/chk-b2111.js <build>|WT");process.exit(9)}
const load=r=>y.load(r==="WT"?fs.readFileSync(f,"utf8"):x("git show "+r+":"+f).toString());
const a=load(B==="WT"?"HEAD":B+"^"),b=load(B==="WT"?"WT":B);
const flat=(o,p="",m={})=>{if(Array.isArray(o)){if(!o.length)m[p]="[]";o.forEach((v,i)=>flat(v,p+"/"+i,m))}else if(o&&typeof o==="object"){const k=Object.keys(o);if(!k.length)m[p]="{}";k.forEach(q=>flat(o[q],p+"/"+q,m))}else m[p]=JSON.stringify(o);return m};
const A=flat(a),C=flat(b),P="/Icons/S/actions/all/1",K="/Icons/S/actions/all/3";
const O=JSON.stringify("tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25 · 体力 30；等级不足不扣）");
const N=JSON.stringify("tell: §5[深渊] §7尝试下潜……（需余烬 Lv.25）");
const d=[...new Set([...Object.keys(A),...Object.keys(C)])].filter(k=>A[k]!==C[k]);
if(d.length!==1||d[0]!==P){console.log(d);process.exit(1)}
if(A[P]!==O)process.exit(3);
if(C[P]!==N)process.exit(4);
if(A[K]!==JSON.stringify("command: corerpg enter abyss")||C[K]!==A[K])process.exit(5);
console.log("only-L77 "+P);
JS
```

- [ ] **rg（施工前已对全文件预跑）**：
      - `rg -n "体力 30|等级不足不扣|已消耗体力|体力 -|扣票|深渊票" plugins/TrMenu/menus/ember_abyss.yml`：施工前只命中 L77；施工后无命中（干跑 exit 1）。
- [ ] **HANDOFF §8 查密码**（`HANDOFF.md` §8，提交前跑，结果必须为 0）：
      `set -a; source secrets/mysql-ember.env; set +a`
      `(git diff; git ls-files -o --exclude-standard | xargs -r cat) | grep -c -- "$MYSQL_PASSWORD"`
      策划本设计提交前已跑，结果 0。
- [ ] reload 实测记为「待恢复服后实测」，不挡 PASS：`trm reload`（或重启）后，用 Lv.25 以下非 OP 号、付费号、OP 号各点一次「开始下潜」，确认聊天是新句 + L78 + 对应插件私聊，没有「体力 30」「等级不足不扣」。

## 6. 明确不做

- 不改 L78 撤离句、L79 命令、L80 close、动作顺序。
- 不改 `S` 灰显子图标（L40-54）、lore、`E`、`P`、`R`。
- 不动 DP option.yml、CoreRpg 代码、cash.yml；不改 NI 票物；不写「票已废」「B0.1 已清」；不暗示精英壳已开。
- **勿 git push**。

## 7. 同文件其它写死的「30」和相关口径（只记，不改）

扫描命令：`rg -n "30" plugins/TrMenu/menus/ember_abyss.yml`（施工前命中 L40、L41、L49、L54、L77、L130、L139）。

| 行 | 现文 | 说明 | 建议 |
|---|---|---|---|
| L41 | `condition: 'check papi %corerpg_stamina% < 30'` | 灰显条件写死 30，不跟 cash.yml；也不看 OP（OP 体力 <30 会被菜单挡住，实际进本免扣）。L40 注释写明「禁双侧 PAPI」 | 只记。要改需加 `stamina_blocked_abyss` 占位符（现只有 weekly/raid/elite，`CoreRpgExpansion` L110-124），属于 build 窗 |
| L49 | `§7需要 §e30 §7体力 · 当前 …` | 灰显 lore | 可换 `%corerpg_stamina_cost_abyss%` |
| L54 | `tell: §c体力不足，需 30 点体力；每日 0:00 回满。` | 灰显点击 | 同上（与 raid L55、daily 同写法） |
| L130 | `§7消耗 §e30 §7体力（与日常同池）` | R 图标 lore | 同上 |
| L139 | `tell: §5深渊消耗 30 体力；0:00（上海）回满；撤离不退。` | R 图标点击 | 同上 |
| L132 | `§7硬顶建议 ≤2/日（免费+购）` | 深渊没有免费次数，「免费+购」像旧票制口径，玩家看不懂 | 建议排，改写待定 |

- L66、L90、L98、L134「撤离 / 超时 / 团灭不退体力」：与代码一致（进本后不退），OP 本来不扣，不算错，不动。
- 若总控要处理 L49/L54/L130/L139 换占位符，建议与 B2.113（hub 换占位符）、B2.118（raid 换占位符）同类，可排在 B2.118 后（如开 B2.120）；L41 和 L132 另议。

排序（按总控更新）：B2.111 → B2.113 → B2.118 → B2.115 → B2.116 → B2.117 → B2.103 → B2.104 → B2.105 → B2.106 → B2.108 → B2.119（退还提示例外，需 build）→ B2.109（B2.107 挂起）。L41 不区分 OP 与已记的「blocked_raid 不区分 OP」同类。

## 总控批注（2026-10-01 13:28 Asia/Shanghai）
- **批 A（方案 A，去数字）**：只改 `plugins/TrMenu/menus/ember_abyss.yml` L77 一行，行首 8 空格、单引号，逐字按荐案；YAML 路径 `/Icons/S/actions/all/1`。L78 撤离句、L79 `command: corerpg enter abyss` 不动。方案 B（占位符）不采用：OP 仍会先见「体力 30」再见「管理免扣」。
- 代码核对认可：深渊无周首免（consumeForEnter 仅 WEEKLY/ELITE/RAID 抵扣）；「等级不足不扣」对队员等级门为先扣后退、OP 跳过等级门，删去合理，退还由插件私聊说明。
- **旁记排期**：同文件 L49/L54/L130/L139 写死 30 → 占位符，另开 **B2.120**，排 B2.118 后；L132「硬顶建议 ≤2/日（免费+购）」旧票制口径，另开 **B2.121**，排 B2.120 后。L41 灰显 `%corerpg_stamina% < 30` 不区分 OP，需新占位符（build），与「blocked_raid 不区分 OP」同列只记。
- 验收：稿中 heredoc `/tmp/chk-b2111.js`（`<build>^` 对 `<build>`，旧 exit 3 / 新 exit 4 / 多处差异 exit 1 / all/3 非 enter abyss exit 5）、rg 全文件预跑、HANDOFF.md §8 计数 0、`ops.json` 为 `[]`；reload 记「待恢复服后实测」。
- 施工岗：插件岗，本地 commit，不 push、不 reload。
