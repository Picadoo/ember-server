# B2.93 · cash `elite.free_tickets` 行内旁注 → 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-10-01 02:02 Asia/Shanghai）** · 设计 `b53fa71` · 批准 `df742c8` · 插件 `6203a87` · 测 `869fd38` · close 本提交。
- **tip 路径：**`docs/design-ember-cash-elite-free-tickets-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` L94 的 `elite.free_tickets` 行内注释（当前无旁注）；未批准前不改玩法 YAML。
- **前序对齐：**B2.90 weekly（L83）、B2.91 abyss（L89）已 PASS · 勾销；B2.85 raid（L86）已 PASS；B2.92 已 PASS · 勾销（close `39e61d7`）。本窗是 HANDOFF-ember-grokbot §6 候选 2，只补 elite 这一行。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**只改该行行内注释；键值 `0`、`ticket_ni_id: ticket_ember_elite`、`hard_cap: 1`、L92 段头注释及其它段零改；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` L82–L97。live 上下文：

```yaml
# Stage 4.4 精英试炼周票 — 周一 0:00 Asia/Shanghai 与周本同节奏；不进商城遗留票物/体力相关池；维护备忘；持有硬顶 1
elite:
  free_tickets: 0
  ticket_ni_id: ticket_ember_elite
  hard_cap: 1
```

事实核对（只读）：

- `TicketGrantService.java` 读取 `elite.free_tickets`（缺省 0）；`grantEliteIfNeeded` 现为返回 0 的 stub，`needsEliteGrant` 恒为 false。即现行不发精英票物，本键只是口径留档。
- L92 段头已写明节奏（周一 0:00）、不进商城池、「维护备忘」、「持有硬顶 1」。行内旁注不重复这些，只补本行自己的口径。

锁定项：

- 仅涉及 L94 行内注释；`free_tickets: 0`、`ticket_ni_id`、`hard_cap: 1`、L92 段头、weekly/raid/abyss/vip/shop 及其它 cash 内容锁定零改。
- 旁注不写节奏、硬顶、商城池（段头已有），不写「试炼」「开放」「可进」等字样，不暗示精英壳已开放。
- 不宣称票已废、B0.1 已清；不编造菜单承诺。

## 2. 荐案（批 A 后仅替换该行，追加行内注释）

新行精确文本（行首 2 空格缩进；`0` 后 **10 个空格** 再接 `#`，与 L83/L86/L89 注释列对齐）：

```yaml
  free_tickets: 0          # 维护备忘：遗留票物/体力口径；精英 free_tickets 发放口径留档（现行 0）
```

与 weekly/abyss 逐字同构，只换玩法名：

```text
L83  free_tickets: 0          # 维护备忘：遗留票物/体力口径；周本 free_tickets 发放口径留档（现行 0）
L89  free_tickets: 0          # 维护备忘：遗留票物/体力口径；深渊 free_tickets 发放口径留档（现行 0）
L94  free_tickets: 0          # 维护备忘：遗留票物/体力口径；精英 free_tickets 发放口径留档（现行 0）
```

### 2.1 锁定口径

- 用「精英」两字，不用「精英试炼」全称，避免和段头重复，也不带开放意味。
- 与段头的「维护备忘」字样重复可接受：同文件 weekly/raid/abyss 行内都以「维护备忘：」开头，本行保持同构方便以后 grep；段头说的是整段节奏与硬顶，行内说的是本键发放口径，内容不重叠。
- 不加日期前缀、不加菜单承诺括注。

### 2.2 src 模板惯例

`CoreRpg/src/main/resources/cash.yml`（jar 内缺省模板）**不同步**。惯例依据：B2.90（`57cd7de`）与 B2.91（`93724a4`）施工 commit 都只改 live `plugins/CoreRpg/cash.yml`，src 模板 weekly/abyss 行至今无旁注；模板段头仍是旧文案「不进商城日票池」，与 live 不一致属已知历史差异，本窗不处理。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换 L94 这一行。
- [ ] 静态 diff：施工 commit 只含 `plugins/CoreRpg/cash.yml`，`1 insertion(+), 1 deletion(-)`；`-  free_tickets: 0`（L94）→ `+` 荐案整行，逐字一致（含 10 空格）。L92 段头、L95–L96 不在 diff 里。
- [ ] YAML 解析正常，值不变：在仓库根目录跑
      `node -e 'const d=require("js-yaml").load(require("fs").readFileSync("plugins/CoreRpg/cash.yml","utf8")).elite;if(d.free_tickets!==0||d.ticket_ni_id!=="ticket_ember_elite"||d.hard_cap!==1)process.exit(1);console.log("ok")'`
      输出 `ok`（已对现文件跑通）。
- [ ] src 模板 `CoreRpg/src/main/resources/cash.yml` 零改。
- [ ] 静态核对即可；不重启、不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog。

## 4. 明确不做

- 不改 `elite.free_tickets` 数值、`ticket_ni_id`、`hard_cap`、L92 段头注释、TicketGrantService 逻辑、src 模板。
- 不开、不预告精英壳；不碰精英预览菜单、DP 精英本配置、NI 精英票物。
- 不捆 DP `option.yml` 票注释（§6 候选 3）、NI 票物显示名（候选 4）、软玩法债。
- 禁写「票已废」「B0.1 已清」；勿编造菜单承诺；**勿 git push**。
