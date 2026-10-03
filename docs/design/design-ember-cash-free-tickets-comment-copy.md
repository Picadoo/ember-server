# B2.78 · cash `free_tickets` 行内注释 → 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 21:22 Asia/Shanghai）** · 设计 `8146e9c` · 批准 `811984d` · 插件 `b531320` · 测 `e9a3cf0`/`095639e`
- **范围：**仅 `plugins/CoreRpg/cash.yml` `daily.free_tickets` 行内注释；未批准前不改 `cash.yml`，不改玩法 YAML。
- **tip 路径：**`docs/design/design-ember-cash-free-tickets-comment-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg cash 注释）**。
- **前序对齐：**B2.77 已批 `d579edf`；本窗承接 cash `free_tickets` 这一行，不扩写其它旁记或玩法范围。
- **本窗纪律：**只改该行注释；`free_tickets: 0`、`hard_cap`、`ticket_ni_id` 及其它键值零改；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/cash.yml` 约 L45–L50。目标是保留 `free_tickets: 0`，将行内旧注释改成遗留票物/体力口径维护备忘；`hard_cap`、`ticket_ni_id` 及其它键值均不在本窗施工。

旧行（live）：

```yaml
free_tickets: 0  # S0: 停发日票，改体力日回满
```

## 2. 荐案

批后仅替换该行注释：

```yaml
free_tickets: 0  # S0 维护备忘：遗留票物停发；现行按体力日回满
```

### 2.1 锁定口径

- 保留 `free_tickets: 0`；注释点明遗留票物停发，现行按体力日回满。
- 「遗留票物」是维护备注，不写「票已废」；「体力口径」不代表改动键值、发放数值或玩法逻辑。
- `hard_cap`、`ticket_ni_id` 及其它键值零改；不碰 cash L92 日票池或其它 cash 行。
- 不捆绑 schema「日票硬顶」、`event_box`、断塔/霜锈/AFK 等旁记。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg cash 注释）**施工 `plugins/CoreRpg/cash.yml` `daily.free_tickets` 这一行的注释。
- [ ] `cash.yml` 除目标注释外零 diff；`free_tickets: 0`、`hard_cap`、`ticket_ni_id` 及其它键值零改。
- [ ] 静态核对旧行与荐案、键值零改、遗留票物停发及体力日回满口径；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧行与荐案、`cash.yml` 零改确认、键值零改确认、旁记零捆绑确认、验收、pull、commit/ahead。
- 禁写「票已废」或 **B0.1 已清**；不宣称玩法已落地。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改 cash L92 日票池、schema「日票硬顶」、`event_box`、断塔/霜锈/AFK 或其它玩法 YAML；勿 git push。
