# B2.86 · cash `shop.weekly_ticket.hard_cap` 行内注释 → 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 22:10 Asia/Shanghai）** · 设计 `bb16ce5` · 批准 `2e81466` · 插件 `7cd818b` · 测 `fc6d159`/`a116ef1`
- **tip 路径：**`docs/design/design-ember-cash-weekly-shop-hardcap-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` 约 L67 的 `shop.weekly_ticket.hard_cap` 行内注释；未批准前不改玩法 YAML。
- **前序对齐：**B2.85 已 PASS · 勾销（close `c2fa2bf` · 测 `66ed681`/`1bbcdd0` · 施工 `f568433`）；本窗承接 `shop.weekly_ticket.hard_cap` 的遗留票物/体力口径维护备注。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**只改该行行内注释；键值 `2`、`weekly_ticket` 段及其它 cash 内容零改；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` 约 L60–L75，确认 `shop.weekly_ticket` 上下文。目标是把商城周票硬顶旁注改为维护备忘，同时保留现有硬顶事实。

旧行（live）：

```yaml
hard_cap: 2          # 免费1+氪≤2
```

锁定项：

- 仅涉及上述行内注释；键值 `2`、`sku_limit: 1`、`grant_ni: true`、`ticket_ni_id: ticket_ember_weekly`、`weekly_ticket` 段及其它 cash 内容锁定零改。
- 保留「免费1+氪≤2」这一硬顶事实；新增内容只作遗留票物/体力口径维护留档，不对历史票物状态或 B0.1 做结论。
- 不扩写周票价格、限购、发放数值、消耗、迁移或任何玩法逻辑。

## 2. 荐案（批 A 后仅替换该行内注释）

```yaml
hard_cap: 2          # 维护备忘：遗留票物/体力口径；免费1+氪≤2
```

- 保留键值 `2` 与「免费1+氪≤2」硬顶事实；以「维护备忘：遗留票物/体力口径」作为短前缀，便于后续维护识别。
- `sku_limit: 1`、`grant_ni`、`ticket_ni_id`、`weekly_ticket` 段及其它 cash 内容零改；不捆已结窗、断塔/霜锈/AFK 或其它旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换约 L67 这一条行内注释。
- [ ] 仅目标注释变化；键值 `2`、weekly_ticket 上下文及其它 cash 内容零改。
- [ ] 静态核对旧行与荐案、遗留票物/体力口径及「免费1+氪≤2」硬顶事实；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、commit hash、ahead、pull、旧行 vs 荐案、施工岗、验收及 `cash.yml` 零改确认。
- 不对历史票物状态或 B0.1 作已清结论；不宣称玩法已落地。
- 勿捆已结窗、断塔/霜锈/AFK、其它旁记或玩法 YAML；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
