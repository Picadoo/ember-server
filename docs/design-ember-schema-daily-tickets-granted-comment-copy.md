# B2.83 · schema `dailyTicketsGranted` 行内「今日已发票」→ 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 21:46 Asia/Shanghai）** · 设计 `1da0023` · 批准 `81ce2be` · 插件 `37e5170` · 测 `8799866`
- **tip 路径：**`docs/design-ember-schema-daily-tickets-granted-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/players/_schema-example.yml` 约 L34 的 `dailyTicketsGranted` 行内注释；键名、键值与其它字段零改。
- **前序对齐：**B2.82 已批 `efecbc6`；本窗承接 schema `dailyTicketsGranted` 行内注释，backlog 对齐 B2.83。
- **施工岗：**批 A 后交插件岗，仅施工 **CoreRpg schema 注释**。
- **本窗纪律：**未批准前不改玩法 YAML；仅提交本 tip 与 backlog；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/players/_schema-example.yml` 约 L30–L40，目标是只维护 `dailyTicketsGranted` 的行内说明，不改变 schema 数据或其它字段。

旧行（live）：

```yaml
dailyTicketsGranted: 0     # 今日已发票（免费+氪+月卡），硬顶 6
```

锁定项：

- 键名 `dailyTicketsGranted` 保持不变，键值 `0` 保持不变。
- `dailyTicketsBought` 与其它字段、空格和行外内容均不在本窗施工。
- 「遗留票物/体力口径维护备忘」只作维护备注，不表示改动发放、消耗、数值或玩法逻辑。

## 2. 荐案（批 A 后仅替换该行内注释）

```yaml
dailyTicketsGranted: 0     # 遗留票物/体力口径维护备忘：免费+氪+月卡，硬顶 6
```

- 保留「免费+氪+月卡」与「硬顶 6」事实；仅把「今日已发票」改为遗留票物/体力口径维护备忘。
- 仅作文案维护，不写成玩法已落地，不推导发放、消耗或体力逻辑变更。
- 旁记不捆 cash `daily`（已结）、断塔/霜锈/AFK；精英壳勿硬开。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/players/_schema-example.yml` 零 diff；批后仅由插件岗替换约 L34 这一条行内注释。
- [ ] 仅目标行内注释变化；键名 `dailyTicketsGranted`、键值 `0`、`dailyTicketsBought` 及其它字段零改。
- [ ] 静态核对旧行与荐案、免费+氪+月卡及硬顶 6 口径；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧行 vs 荐案、施工岗、验收、schema 零改确认、旁记零捆绑确认、pull、commit/ahead。
- 不作票物废止或 B0.1 状态结论；本窗不改玩法 YAML。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改 cash `daily`、断塔/霜锈/AFK 或其它玩法 YAML；勿 git push。
