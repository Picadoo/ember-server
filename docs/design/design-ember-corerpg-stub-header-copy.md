# B2.60 · CoreRpg 文件头维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:00 Asia/Shanghai）** · 测 `5abd67d` · 施工 `96b931a` · 批准 `45f2dcf` · 设计 `262bfa8`
- **范围：**仅 `plugins/CoreRpg/cash.yml`、`covenant.yml`、`talent.yml` 指定文件头注释行；玩法数值、键及其它 YAML 不在本窗施工。
- **tip 路径：**`docs/design/design-ember-corerpg-stub-header-copy.md`
- **施工岗：**批后施工交 **插件岗（CoreRpg 文档注释）**。
- **本窗纪律：**未批前不改玩法 YAML；文档可 commit；勿 git push；不做长测/挑刺。

## 1. 现况与问题

以下为 live 三行全文，问题仅是注释带有 `STUB` /「插件岗」指派口吻；本窗不改变任何玩法值、配置键或其它 YAML。

```text
# STUB：插件岗实现命令前可先落盘；挂到 /corerpg reload（落地后）
# STUB：插件岗实现命令前可先落盘；热重载进 /corerpg reload（落地后）
# STUB：插件岗扩树与实现 unlock/reset；本文件为契约形状
```

对应位置：`cash.yml` L2、`covenant.yml` L2、`talent.yml` L4。

## 2. 荐案：维护备忘式三行

仅替换上述三行，推荐如下：

```text
# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload
# 维护备忘：配置可先落盘；热重载纳入 CoreRpg reload（落地后）
# 维护备忘：扩树与 unlock/reset 尚待实现；本文件为契约形状
```

### 2.1 新旧三行全文对照

| 文件 / 行 | 旧（live 全文） | 新（荐案，批后全文） |
|---|---|---|
| `plugins/CoreRpg/cash.yml` L2 | `# STUB：插件岗实现命令前可先落盘；挂到 /corerpg reload（落地后）` | `# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload` |
| `plugins/CoreRpg/covenant.yml` L2 | `# STUB：插件岗实现命令前可先落盘；热重载进 /corerpg reload（落地后）` | `# 维护备忘：配置可先落盘；热重载纳入 CoreRpg reload（落地后）` |
| `plugins/CoreRpg/talent.yml` L4 | `# STUB：插件岗扩树与实现 unlock/reset；本文件为契约形状` | `# 维护备忘：扩树与 unlock/reset 尚待实现；本文件为契约形状` |

## 3. 口径硬线与禁项

- 新案只描述维护事实：配置可先落盘、reload 路径作为维护备忘、文件为契约形状。
- 不宣称玩法命令已完成，不宣称扩树、`unlock/reset` 已完成；`CoreRpg reload` 不是玩家操作教学。
- 不宣称 B0.1；B0.1 仍按 backlog 现状保留。
- 三文件同一薄窗，批后仅改上述三条注释；数值、键、其它 YAML 零改。
- 不做长测、不做挑刺；勿 git push。

## 4. 验收

- [ ] `cash.yml` L2、`covenant.yml` L2、`talent.yml` L4 与荐案逐字一致，且不含 `STUB` 或「插件岗」。
- [ ] 三目标文件除指定注释行外零 diff；玩法数值与键零改。
- [ ] 文案保留「可先落盘」「reload」及「契约形状」事实，但不把命令/扩树说成已完成。
- [ ] 仅文档 tip 与 backlog 可在本待批窗提交；批后施工岗固定为**插件岗（CoreRpg 文档注释）**。
