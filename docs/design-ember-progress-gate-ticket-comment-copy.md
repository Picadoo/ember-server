# B2.87 · progress 等级门槛旁注 → 遗留票物/体力口径维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 22:19 Asia/Shanghai）** · 设计 `e4900d6` · 批准 `5881af9` · 插件 `5b41046` · 测 `89bbd57`/`b9c8f9e`
- **范围：**仅 `plugins/CoreRpg/progress.yml` 约 L137 的等级门槛旁注单行。
- **tip 路径：**`docs/design-ember-progress-gate-ticket-comment-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg progress 注释）**。
- **前序对齐：**B2.86 已 PASS · 勾销（close `0cb448a` · 测 `fc6d159`/`a116ef1` · 施工 `7cd818b`）；本窗只承接 `progress.yml` 等级门槛旁注，不扩写已结窗或其它旁记。
- **本窗纪律：**只改注释；`level_gates` 键值与其它段零改；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/progress.yml` 约 L125–L150。目标是将等级门槛旁注的旧票物措辞改成遗留票物/体力口径维护备忘；等级门槛、DP gate、拒门不扣的事实均保留。

旧注释（live，L137）：

```yaml
#   DP：各 option.yml dungeon-start 第一条条件 js '%corerpg_gate_<id>%'=='yes'（OP 豁免），在扣票条件之前 → 被拒不扣票。
```

## 2. 荐案

批后仅替换上述注释行：

```yaml
#   DP：各 option.yml dungeon-start 第一条条件 js '%corerpg_gate_<id>%'=='yes'（OP 豁免）；等级门槛在前，先于入场扣体力/遗留票物；被拒不扣体力/遗留票物（口径维护备忘）。
```

### 2.1 锁定口径

- 保留 DP gate 的 `js '%corerpg_gate_<id>%'=='yes'`、OP 豁免、等级门槛先于入场扣除、被拒不扣的事实；仅把「扣票」改为遗留票物/体力口径维护备忘。
- `level_gates` 键值零改：`daily: 10`、`weekly: 20`、`abyss: 25`、`calamity: 30`、`raid: 35`、`guild_boss: 0`、`elite: 40`；其余段零改。
- 不把维护备忘写成玩法迁移、清账或完成宣称；不改体力数值、消耗逻辑或 DP 行为。
- 不捆绑 cash、`event_box`、schema、已结窗、断塔/霜锈/AFK 或精英壳旁记。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg progress 注释）**施工 `plugins/CoreRpg/progress.yml` 约 L137 这一行。
- [ ] `progress.yml` 除目标注释外零 diff；`level_gates` 键值与其它段零改。
- [ ] 静态核对新旧注释全文、等级门槛在前、DP gate、OP 豁免及被拒不扣事实；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧注释与荐案全文、施工岗、`progress.yml` 零改确认、`level_gates` 键值零改确认、旁记零捆绑确认、验收、pull、commit/ahead。
- 禁把本窗写成票物废止、旧账清空或玩法已完成；禁长测/挑刺；精英壳勿硬开。
- 禁顺手改玩法 YAML、DP 配置、体力数值、cash、`event_box`、schema、断塔/霜锈/AFK 旁记；勿 git push。
