# B2.80 · schema 分区注释「日票硬顶」→ 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 21:32 Asia/Shanghai）** · 设计 `6a9070e` · 批准 `a046b99` · 插件 `5a8e340` · 测 `b289812/d46303c`
- **tip 路径：**`docs/design-ember-schema-ticket-cap-comment-copy.md`
- **范围：**仅 `plugins/CoreRpg/players/_schema-example.yml` 约 L27 的分区注释行；schema 键值零改。
- **前序对齐：**B2.79 已批 `358a306`；本窗承接 schema 分区注释，backlog 对齐 B2.80。
- **施工岗：**批 A 后交插件岗，仅施工 CoreRpg schema 注释行。

## 1. 现况与目标

旧注释（live）：

```yaml
# —— 晶钻 / 日票硬顶 / 月卡（DESIGN-ember-cash-monthly.md §4）——
```

目标是保留晶钻、月卡与 `DESIGN-ember-cash-monthly.md §4` 引用，将「日票硬顶」改为遗留票物/体力口径维护备忘短词；未批前 `_schema-example.yml` 零 diff。

## 2. 荐案（批 A 后仅替换注释行）

```yaml
# —— 晶钻 / 遗留票物/体力口径维护备忘 / 月卡（DESIGN-ember-cash-monthly.md §4）——
```

- 保留晶钻、月卡与 DESIGN 引用；「遗留票物/体力口径维护备忘」只作维护口径，不表示改动发放、消耗或玩法逻辑。
- schema 键值零改；旁记不捆绑 mail `event_box`、断塔/霜锈/AFK 或 cash elite；精英壳勿硬开。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/players/_schema-example.yml` 零 diff；批后仅由插件岗替换约 L27 注释。
- [ ] 仅目标注释行变化；schema 键值及其它行零改。
- [ ] 静态核对旧注释与荐案、晶钻/月卡/DESIGN 引用保留、旁记未捆绑；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧注释 vs 荐案、施工岗、验收、schema 零改确认、旁记零捆绑确认、pull、commit/ahead。
- 本窗只提交文案稿，不写成玩法已落地。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改 mail `event_box`、断塔/霜锈/AFK、cash elite 或其它玩法 YAML；勿 git push。
