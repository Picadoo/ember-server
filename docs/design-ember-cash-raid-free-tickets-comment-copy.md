# B2.85 · cash `raid.free_tickets` 行内周登录/团本票注释 → 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 22:02 Asia/Shanghai）** · 设计 `dae3743` · 批准 `a0cf518` · 插件 `f568433` · 测 `66ed681`/`1bbcdd0`
- **tip 路径：**`docs/design-ember-cash-raid-free-tickets-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` 约 L86 的 `raid.free_tickets` 行内注释；未批准前不改玩法 YAML。
- **前序对齐：**B2.84 已批 `9051ebe`；本窗承接 `cash.yml` 团本 `free_tickets` 行内周登录/团本票口径。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**只改该行行内注释；键值 `0`、`ticket_ni_id` 及其它 cash 段零改；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` 约 L80–L95，确认 `weekly`、`raid`、`abyss` 段上下文。目标是将团本周登录/团本票旁注改为维护备忘，保留现有配置。

旧行（live）：

```yaml
free_tickets: 0          # 2026-09-27: weekly login grant (menu promises 每周团本票×1)
```

锁定项：

- 仅涉及上述行内注释；`free_tickets: 0`、`ticket_ni_id: ticket_ember_raid`、`weekly`/`abyss` 段及其它 cash 内容锁定零改。
- 「遗留票物/体力口径」仅作维护备注；保留 2026-09-27 日期与「菜单承诺每周团本票×1」事实，不宣称票已废或玩法已落地。
- 不扩写周票、团本门控、发放数值、消耗、迁移或任何玩法逻辑。

## 2. 荐案（批 A 后仅替换该行内注释）

```yaml
free_tickets: 0          # 2026-09-27: 维护备忘：遗留票物/体力口径；周登录团本票发放口径留档（菜单承诺每周团本票×1）
```

- 保留键值 `0`、日期与菜单承诺事实；点明遗留票物及周登录团本票发放口径，作为维护留档。
- `ticket_ni_id: ticket_ember_raid` 与其它键值零改；不捆已结窗、断塔/霜锈/AFK 或其它旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换约 L86 这一条行内注释。
- [ ] 仅目标注释变化；`free_tickets: 0`、`ticket_ni_id`、weekly/abyss 及其它 cash 内容零改。
- [ ] 静态核对旧行与荐案、遗留票物/体力口径、周登录团本票发放与菜单承诺事实；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、commit hash、ahead、pull、旧行 vs 荐案、施工岗、验收及 `cash.yml` 零改确认。
- 禁写「票已废」或 **B0.1 已清**；不宣称玩法已落地。
- 勿捆已结窗、断塔/霜锈/AFK、其它旁记或玩法 YAML；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
