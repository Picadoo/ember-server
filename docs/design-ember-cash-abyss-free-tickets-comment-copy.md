# B2.91 · cash `abyss.free_tickets` 行内旁注 → 遗留票物/体力口径维护备忘

- **STATUS：已批 A（总控 · 2026-10-01 01:47 Asia/Shanghai）· 交插件岗** · 批后仅插件岗改 `plugins/CoreRpg/cash.yml` L89 行内注释，不改玩法 YAML。设计 tip `759c7d7`。
- **tip 路径：**`docs/design-ember-cash-abyss-free-tickets-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` L89 的 `abyss.free_tickets` 行内注释（当前无旁注）；未批准前不改玩法 YAML。
- **前序对齐：**B2.90 已 PASS · 勾销（设计 `838cb7d` · 批准 `74509b6` · 插件 `57cd7de` · 测 `fb06ed1` · close `12db71d`）；B2.85 raid `free_tickets` 已 PASS；本窗只补 abyss 旁注。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**只改该行行内注释；键值 `0`、`ticket_ni_id: ticket_ember_abyss`、其它段（含 weekly/raid/elite/shop）零改；勿捆 elite；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` L82–L97，确认 `weekly`（L83，B2.90 旁注）、`raid`（L86，B2.85 旁注）、`abyss`（L88–L90）、`elite`（L92–L96）上下文。

旧行（live L89，当前无旁注）：

```yaml
abyss:
  free_tickets: 0
  ticket_ni_id: ticket_ember_abyss
```

事实核对（只读，不入稿外宣）：

- `CoreRpg/src/.../TicketGrantService.java` 读取 `abyss.free_tickets`（缺省 0）；`grantAbyssIfNeeded` 现为返回 0 的 stub，按日记账字段仍在写入。即现行不发深渊票物，本键只是口径留档。
- 未发现任何菜单或 quest 承诺「每日/每周深渊票」。quest.yml 里「深渊箱每周首次结算必出」说的是奖励箱，与票无关，**不写进旁注**。
- `CoreRpg/src/main/resources/cash.yml` L89（jar 内缺省模板）同样无旁注；本窗只动 live `plugins/CoreRpg/cash.yml`，与 B2.90 做法一致，不动 src 模板。

锁定项：

- 仅涉及上述行内注释；`free_tickets: 0`、`ticket_ni_id: ticket_ember_abyss`、weekly/raid/elite/shop 及其它 cash 内容锁定零改。
- 「遗留票物/体力口径」仅作维护备注；点明深渊 `free_tickets` 发放口径留档与现行 `0`，不宣称票已废、B0.1 已清或深渊完整落地，不编造菜单承诺。
- 不扩写深渊票、进本体力（`stamina` 深渊 30）、等级门槛（25）、发放数值、消耗、迁移或任何玩法逻辑。

## 2. 荐案（批 A 后仅替换该行，追加行内注释）

新行精确文本（行首 2 空格缩进；`0` 后 **10 个空格** 再接 `#`，与 L83/L86 注释列对齐）：

```yaml
  free_tickets: 0          # 维护备忘：遗留票物/体力口径；深渊 free_tickets 发放口径留档（现行 0）
```

与 weekly（L83）逐字同构，只把「周本」换成「深渊」：

```text
L83  free_tickets: 0          # 维护备忘：遗留票物/体力口径；周本 free_tickets 发放口径留档（现行 0）
L89  free_tickets: 0          # 维护备忘：遗留票物/体力口径；深渊 free_tickets 发放口径留档（现行 0）
```

### 2.1 锁定口径

- 保留键值 `0`；注释必须含「维护备忘」+「遗留票物/体力口径」+「深渊 free_tickets 发放口径留档（现行 0）」。
- 不加日期前缀（raid 行的 `2026-09-27:` 是历史遗留，weekly 行未加，本窗对齐 weekly）；不加菜单承诺括注（深渊无此承诺）。
- 不写「按日发放」等节奏描述：发放逻辑现为 stub，写节奏容易被读成仍在发。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换 L89 这一行。
- [ ] 静态 diff：`git diff --stat -- plugins/CoreRpg/cash.yml` 为 `1 insertion(+), 1 deletion(-)`；`-  free_tickets: 0` → `+` 荐案整行，逐字一致（含 10 空格）。
- [ ] YAML 解析正常：在仓库根目录跑 `node -e 'const y=require("js-yaml"),d=y.load(require("fs").readFileSync("plugins/CoreRpg/cash.yml","utf8"));if(d.abyss.free_tickets!==0||d.abyss.ticket_ni_id!=="ticket_ember_abyss")process.exit(1);console.log("ok")'` 输出 `ok`（盒内无 PyYAML，用已装的 js-yaml）。
- [ ] 值仍为 `0`；`ticket_ni_id`、weekly/raid/elite/shop 及其它 cash 内容零改；src 模板零改。
- [ ] 静态核对即可；不重启、不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 明确不做

- 不改 `abyss.free_tickets` 数值、`ticket_ni_id`、TicketGrantService 逻辑、src 模板 `cash.yml`。
- 不捆 elite 段（L94 `elite.free_tickets` 段首已有维护备忘，另议）、DP `option.yml` 票注释、NI 票物显示名。
- 不碰已结窗、断塔/霜锈/AFK 软债、精英壳。
- 禁写「票已废」「B0.1 已清」「深渊完整落地」；勿编造菜单承诺；**勿 git push**。
