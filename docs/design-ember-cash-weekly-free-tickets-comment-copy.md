# B2.90 · cash `weekly.free_tickets` 行内旁注 → 遗留票物/体力口径维护备忘

- **STATUS：待批 A**（总控 · 2026-09-30 22:39 Asia/Shanghai） · 未批准前不改 `plugins/CoreRpg/cash.yml`，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-cash-weekly-free-tickets-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` 约 L83 的 `weekly.free_tickets` 行内注释（当前无旁注）；未批准前不改玩法 YAML。
- **前序对齐：**B2.89 已 PASS · 勾销（设计 `21121c0` · 批准 `b6ff3d6` · 插件 `245fe40` · 测 `b04af5b` · close `f1977ce`）；B2.85 raid `free_tickets` 已 PASS；本窗只补 weekly 旁注。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**只改该行行内注释；键值 `0`、`ticket_ni_id: ticket_ember_weekly`、其它段（含 raid/abyss/elite/shop）零改；勿捆 abyss/elite；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` 约 L70–L100，确认 `weekly`、`raid`、`abyss`、`elite` 段上下文。目标是为 weekly `free_tickets` 行补遗留票物/体力口径维护备忘，对齐 raid 风格；勿编造不存在的菜单承诺。

旧行（live，当前无旁注）：

```yaml
  free_tickets: 0
```

锁定项：

- 仅涉及上述行内注释；`free_tickets: 0`、`ticket_ni_id: ticket_ember_weekly`、raid/abyss/elite/shop 及其它 cash 内容锁定零改。
- 「遗留票物/体力口径」仅作维护备注；点明周本 `free_tickets` 发放口径留档与现行 `0`，不宣称票已废或 B0.1 已清，不编造菜单承诺。
- 不扩写周票、团本门控、发放数值、消耗、迁移或任何玩法逻辑。

## 2. 荐案（批 A 后仅替换该行内注释）

```yaml
  free_tickets: 0          # 维护备忘：遗留票物/体力口径；周本 free_tickets 发放口径留档（现行 0）
```

### 2.1 锁定口径

- 保留键值 `0`；注释必须含「维护备忘」+「遗留票物/体力口径」；以周本 free_tickets 发放口径留档（现行 0）作事实对齐，勿写菜单承诺。
- `ticket_ni_id: ticket_ember_weekly` 与其它键值零改；不捆 abyss/elite、已结窗、断塔/霜锈/AFK 或其它旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换约 L83 这一条行内注释。
- [ ] 仅目标注释变化；`free_tickets: 0`、`ticket_ni_id`、raid/abyss/elite/shop 及其它 cash 内容零改。
- [ ] 静态核对旧行与荐案、遗留票物/体力口径、周本 free_tickets 发放口径（现行 0）；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、commit hash、ahead、pull、旧行 vs 荐案、施工岗、验收及 `cash.yml` 零改确认。
- 禁写「票已废」或 **B0.1 已清**；不宣称玩法已落地；勿编造不存在的菜单承诺。
- 勿捆 abyss/elite、已结窗、断塔/霜锈/AFK、其它旁记或玩法 YAML；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
