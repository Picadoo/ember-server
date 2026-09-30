# B2.79 · cash 精英周票注释 → 遗留票物/体力口径维护备忘

- **STATUS：待批 A**（本窗仅交设计 tip；未批准前不改玩法 YAML）
- **tip 路径：**`docs/design-ember-cash-elite-ticket-pool-comment-copy.md`
- **范围：**仅 `plugins/CoreRpg/cash.yml` 约 L92 的 Stage 4.4 注释；`elite.free_tickets`、`ticket_ni_id`、`hard_cap` 及其它键值零改。
- **前序对齐：**B2.78 已批 `811984d`；本窗承接 cash 精英周票注释，backlog 对齐 B2.79。
- **施工岗：**批 A 后交插件岗，仅施工该注释行。

## 1. 现况与目标

旧注释（live）：

```yaml
# Stage 4.4 精英试炼周票 — 周一 0:00 Asia/Shanghai 与周本同节奏；不进商城日票池；持有硬顶 1
```

目标是保留 Stage 4.4、周一 0:00 Asia/Shanghai、与周本同节奏、持有硬顶 1 四项事实，把「不进商城日票池」改成「不进商城遗留票物/体力相关池；维护备忘」；未批前 `cash.yml` 零 diff。

## 2. 荐案（批 A 后仅替换注释行）

```yaml
# Stage 4.4 精英试炼周票 — 周一 0:00 Asia/Shanghai 与周本同节奏；不进商城遗留票物/体力相关池；维护备忘；持有硬顶 1
```

- 保留周节奏与持有硬顶 1 事实；「不进商城遗留票物/体力相关池；维护备忘」只作维护口径，不表示改动发放、消耗或玩法逻辑。
- `elite.free_tickets`、`ticket_ni_id`、`hard_cap` 及其它键值零改；精英壳不硬开。
- 不捆绑 schema「日票硬顶」、`event_box`、断塔/霜锈/AFK 等旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换 L92 注释。
- [ ] 仅目标注释行变化；`elite.free_tickets`、`ticket_ni_id`、`hard_cap` 及其它键值零改。
- [ ] 静态核对旧注释与荐案、四项保留事实、旁记未捆绑；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧注释 vs 荐案、施工岗、验收、`cash.yml` 零改确认、键值零改确认、旁记零捆绑确认、pull、commit/ahead。
- 不得宣称 B0.1 已清或票已废；不得把本窗文案稿写成玩法已落地。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改 schema「日票硬顶」、`event_box`、断塔/霜锈/AFK 或其它玩法 YAML；勿 git push。
