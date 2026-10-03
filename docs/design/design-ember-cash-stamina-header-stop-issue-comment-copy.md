# B2.84 · cash 体力段旁注「停发票」→「遗留票物停发」

- **STATUS：PASS · 勾销（总控 · 2026-09-30 21:54 Asia/Shanghai）** · 设计 `2b6d3ef` · 批准 `9051ebe` · 插件 `d29ea14` · 测 `c5d483a`/`3a756a2`
- **tip 路径：**`docs/design/design-ember-cash-stamina-header-stop-issue-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` 约 L8 的体力段旁注；未批准前不改玩法 YAML。
- **前序对齐：**B2.83 已批 `81ce2be`；本窗承接 `cash.yml` 体力段旁注，与 `free_tickets` L47 的「遗留票物停发」口径对齐。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **本窗纪律：**仅计划替换该注释行；勿 git push。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` 约 L1–L20，目标是只维护 S0 余烬体力旁注，不改变体力配置或其它 cash 内容。

旧注释（live）：

```yaml
# S0 余烬体力（design-ember-stamina-dnf-daily §A）— 停发票；进本扣体力
```

锁定项：

- 仅涉及上述注释行；`stamina` 键值及其它字段、空格和行外内容均锁定零改。
- 不在本窗改动体力上限、回补、消耗、迁移或任何玩法逻辑。
- 「遗留票物停发」是维护旁注，按 `free_tickets` L47 口径对齐，不写成额外玩法变更。

## 2. 荐案（批 A 后仅替换该注释行）

```yaml
# S0 余烬体力（design-ember-stamina-dnf-daily §A）— 遗留票物停发；进本扣体力
```

- 仅将「停发票」改为总控给定的「遗留票物停发」；保留 S0、设计引用与「进本扣体力」。
- `stamina` 键值零改；不捆 cash L86 周登录团本票注释、schema、断塔/霜锈/AFK 或其它旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换约 L8 这一条注释。
- [ ] 仅目标注释行变化；`stamina` 键值及其它 cash 内容零改。
- [ ] 静态核对旧注释与荐案、`free_tickets` L47「遗留票物停发」口径；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、commit hash、ahead、pull、旧注释 vs 荐案、施工岗、验收与 `cash.yml` 零改确认。
- 禁写「票已废」或 **B0.1 已清**；不宣称玩法已落地。
- 勿捆 schema（已结）、cash L86 周登录团本票注释、断塔/霜锈/AFK；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
