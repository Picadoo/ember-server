# STATUS · B2.17 天赋 `cost`→消耗 · 轻测

| 字段 | 值 |
|---|---|
| 时间 | 2026-09-29 00:53 Asia/Shanghai |
| 角色 | 余烬-测试岗执行器 |
| 施工 tip | `9631343` |
| 设计 tip | `3a3a226` |
| 批准 tip | `0eb15c2` |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml`；静态轻测 |

## 总评

**PASS**

## 验收点

1. **PASS**：`rg -n -i '\\bcost\\b' plugins/TrMenu/menus/ember_talent.yml` 无输出；玩家可见类型行英文 `cost` 为 0。
2. **PASS**：13 条玩家可见类型行均含「消耗」及数字：`2` ×3、`3` ×6、`4` ×4；合计覆盖 2/3/4。
3. **PASS**：PyYAML 静态解析通过；`git diff --check` 通过。未改配置内容。

## 边界与操作

- 未开服打本、未做长测；本次以静态检查为准，未进行游戏内目视。
- 未宣称 B0.1 已清。
- `ops=[]`
