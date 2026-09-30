# B2.77 · progress 战令「日票×5」注释 → 遗留票物/体力口径维护备忘

- **STATUS：已批 A（总控 · 2026-09-30 21:14 Asia/Shanghai）** · 设计 tip `65a16bc` · 交**插件岗（CoreRpg progress 注释）**
- **范围：**仅 `plugins/CoreRpg/progress.yml` 约 L60 的战令注释单行；未批准前不改 `progress.yml`，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-progress-pass-ticket-comment-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg progress 注释）**。
- **前序对齐：**B2.76 已批 `6e7456a`；本窗只承接 progress 这一行，不扩写 B2.76 批注施工范围。
- **本窗纪律：**只改注释；`pass_rewards` 及其邻近键值、发放数值语义零改；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/progress.yml` 约 L50–L80。目标是将 L60 的旧票物措辞改成遗留票物/体力口径维护备忘；`pass_rewards` 从键名、等级、物品到数量均不在本窗施工。

旧注释（live）：

```yaml
#   付费轨累计日票 ×5（Lv6/12/18/24/30，规格「日票×5 分散等级发」。）
```

## 2. 荐案

批后仅替换上述注释行：

```yaml
#   付费轨累计遗留票物 ×5（体力口径维护备忘；Lv6/12/18/24/30，规格「×5 分散等级发」。）
```

### 2.1 锁定口径

- 保留 **Lv6/12/18/24/30** 与 **累计 ×5、分散等级发** 的事实说明；仅把「日票」改成遗留票物/体力维护口径。
- 「遗留票物」是维护备注，不宣称「票已废」；「体力口径」不代表改动发放数值或玩法逻辑。
- `pass_rewards` 及其它键值零改；不改 `pass_rewards` 的任何发放数值、等级或物品。
- 不捆绑 cash L47/L92、`event_box`、schema、断塔/霜锈/AFK 等旁记。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg progress 注释）**施工 `plugins/CoreRpg/progress.yml` 约 L60 这一行。
- [ ] `progress.yml` 除目标注释外零 diff；`pass_rewards` 及其它键值零改。
- [ ] 静态核对新旧注释、Lv 档位、累计 ×5 与「分散等级发」事实；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧注释与荐案全文、`progress.yml` 零改确认、`pass_rewards` 键值零改确认、旁记零捆绑确认、验收、pull、commit/ahead。
- 禁写「票已废」「B0.1 已清」或战令已完整落地等完成宣称。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改玩法 YAML、`pass_rewards`、cash L47/L92、`event_box`、schema 或断塔/霜锈/AFK 旁记；勿 git push。
