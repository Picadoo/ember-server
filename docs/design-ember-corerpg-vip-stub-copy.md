# B2.62 · CoreRpg cash 勋阶注释维护备忘薄窗

- **STATUS：已批 A（总控 · 2026-09-30 20:05 Asia/Shanghai）**
- **范围：**批后仅替换 `plugins/CoreRpg/cash.yml` L98、L104 两行注释；`vip` 数值、键及其它 YAML 零改。批前不改玩法 YAML。
- **tip 路径：**`docs/design-ember-corerpg-vip-stub-copy.md`
- **施工岗：**批后施工交 **插件岗（CoreRpg 文档注释）**。
- **本窗纪律：**不把轻量档表述成 LuckPerms/勋阶完整落地；不宣称 B0.1；禁长测/挑刺；勿 git push。

## 1. 现况与范围

`plugins/CoreRpg/cash.yml` 当前 live 行号与上下文已核对：目标只有 L98、L104 两行注释。`vip` 下的 `enabled`、日礼数值、`tier0_daily_claim_coin`、`tiers` 键和值均不在本窗修改范围。

## 2. 新旧两行全文对照

| 文件 / 行 | 旧（live 全文） | 新（荐案，批后全文） |
|---|---|---|
| `plugins/CoreRpg/cash.yml` L98 | `# 勋阶轻量 stub（无 LuckPerms）` | `# 维护备忘：勋阶轻量档（当前无 LuckPerms）` |
| `plugins/CoreRpg/cash.yml` L104 | `# tiers stub — PlayerData.vipTier default 0` | `# 维护备忘：tiers — PlayerData.vipTier 默认 0` |

## 3. 荐案

```text
# 维护备忘：勋阶轻量档（当前无 LuckPerms）
# 维护备忘：tiers — PlayerData.vipTier 默认 0
```

仅去掉 `stub` 口吻，改成维护备忘式事实；保留当前无 LuckPerms 与 `PlayerData.vipTier` 默认 0 的事实。此案只是注释措辞，不代表 LuckPerms 或勋阶完整落地。

## 4. 验收

- [ ] 批后仅 L98、L104 与荐案逐字一致。
- [ ] `vip` 数值、键及其它 `cash.yml` 内容零改；目标文件除两行注释外零 diff。
- [ ] L98 保留当前无 LuckPerms 事实；L104 保留 `PlayerData.vipTier` 默认 0 事实。
- [ ] 验收确认 `cash.yml` 零玩法改动；本待批窗只提交 tip + backlog 文档。

## 5. 禁项与施工口径

- 不宣称 LuckPerms 已接入或勋阶完整落地。
- 不宣称 B0.1；不做长测、不做挑刺；勿 git push。
- 批后施工交 **插件岗（CoreRpg 文档注释）**；未批前不改 `cash.yml`。
