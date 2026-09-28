# STATUS · B2.17 天赋菜单 `cost`→消耗

| 字段 | 值 |
|---|---|
| 日期 | 2026-09-29 00:50 Asia/Shanghai |
| 角色 | 余烬-插件 executor |
| 设计 tip | `3a3a226` |
| 批准 tip | `0eb15c2` · APPROVED A |
| 施工 commit | `fix(trmenu): B2.17 talent cost→消耗` |
| 范围 | 仅 `plugins/TrMenu/menus/ember_talent.yml` 的 13 条玩家可见类型行 |

## 已施工

- 根节点：`§7根节点 · 被动 · 消耗 2` ×3。
- 烬斩主动技：`§7主动技 · 消耗 4` ×1。
- 二层：`§7二层 · 被动 · 消耗 3/4` ×9。
- 数字 2/3/4、周边 YAML、节点键和解锁命令保持原样。

## 验收证据

- `rg -n '· cost |cost [234]' plugins/TrMenu/menus/ember_talent.yml`：**0**。
- 类型行计数：根节点 3、主动技 1、二层 9；英文 `cost`：**0**。
- `git diff --check`：通过。

## 热更

按服约定执行：

```text
printf 'trmenu reload\n' > server-runtime/console.in
```

命令于 2026-09-29 00:50 Asia/Shanghai 执行，shell exit code **0**；TrMenu reload 已发入服端控制台输入管道。

## 边界

本窗未改 `talent.yml`、unlock 实参、数值、requires、stats、其它菜单或 runtime/player/world；不宣称 B0.1、奖励页 `NI id:` 或套装 `gear_ember_*` 已清。
