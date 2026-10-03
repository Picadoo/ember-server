# STATUS · B2.62 CoreRpg VIP stub 文案 · 纯静态薄验收

| 字段 | 值 |
|---|---|
| 时间 | 2026-09-30 20:06 Asia/Shanghai |
| 角色 | 余烬-测试岗执行器 |
| 施工 tip | `a65c6ee`（`B2.62: update CoreRpg cash VIP stub comments`） |
| 设计 tip | `docs/design/design-ember-corerpg-vip-stub-copy.md`（`2cf9328`） |
| 批准 tip | `bd87894` |
| 范围 | 仅 `plugins/CoreRpg/cash.yml` L98、L104 两行注释；纯静态薄验收；本岗未改配置 |

## 总评

**PASS**

## 验收点

1. **PASS**：`git show a65c6ee --stat` 仅 `plugins/CoreRpg/cash.yml`（`4 ++--` → 1 file, 2 insertions, 2 deletions）。
2. **PASS**：L98 恰为 `# 维护备忘：勋阶轻量档（当前无 LuckPerms）`（与 tip §2 / 荐案逐字一致）。
3. **PASS**：L104 注释正文恰为 `# 维护备忘：tiers — PlayerData.vipTier 默认 0`（live 行带 YAML 缩进两空格：`  # 维护备忘：tiers — PlayerData.vipTier 默认 0`；与 tip §2 荐案正文一致）。
4. **PASS**：vip 数值/键零改。`enabled: true`、`daily_claim_coin: 120`、`tier0_daily_claim_coin: 20`、`tiers:` 及其 `0`/`1` 条目与 parent 逐字相同；diff 仅两行注释。
5. **PASS**：该文件不再出现「勋阶轻量 stub」或「tiers stub —」旧句（`rg` 无匹配）。

## L98 / L104 原文

```text
98|# 维护备忘：勋阶轻量档（当前无 LuckPerms）
104|  # 维护备忘：tiers — PlayerData.vipTier 默认 0
```

## 旧句 rg

```text
rg -n '勋阶轻量 stub|tiers stub —' plugins/CoreRpg/cash.yml
# → 无输出（旧句已清除）
# parent (a65c6ee^) 曾有：
#   98:# 勋阶轻量 stub（无 LuckPerms）
#   104:  # tiers stub — PlayerData.vipTier default 0
```

## git show a65c6ee --stat

```text
commit a65c6ee67511ee7f45145c4d18af3d717a06563b
B2.62: update CoreRpg cash VIP stub comments

 plugins/CoreRpg/cash.yml | 4 ++--
 1 file changed, 2 insertions(+), 2 deletions(-)
```

## 数值零改旁证

```text
# diff 仅注释两行（无数值/键）：
-# 勋阶轻量 stub（无 LuckPerms）
+# 维护备忘：勋阶轻量档（当前无 LuckPerms）
-  # tiers stub — PlayerData.vipTier default 0
+  # 维护备忘：tiers — PlayerData.vipTier 默认 0

# live vip 段（非注释行与 parent 一致）：
vip:
  enabled: true
  daily_claim_coin: 120        # 勋阶 ≥1
  tier0_daily_claim_coin: 20
  tiers:
    0: { name: "无" }
    1: { name: "勋阶I" }
```

## 边界与操作

- 未开服、未长测、未挑刺；纯静态核对。
- 本岗未改任何 YAML/配置；仅新增本测报文档。
- 未宣称 LuckPerms/勋阶完整落地；未宣称 B0.1。

## 阻塞点

无。
