# STATUS · B2.12 灾厄 OP 调试拒门文案 A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:00 Asia/Shanghai |
| Role | 余烬-插件 executor |
| Approval | `708c62a` · `docs/STATUS-ember-calamity-op-gate-copy-approve.md` |
| Design tip | `1e109b3` |
| Scope | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` 的 `message=` 单字符串 |

## 施工结果

已将唯一批准文案从：

`§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄`

替换为：

`§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄`

仅改 `message=`；保留 `text=`、team/OP 条件、人数门及其余 DungeonPlus 内容。未改 loot、MM、TrMenu、`calamity.yml`、小写 stub `ember_calamity` 或其它 YAML。

## 热更证据

已执行：

```text
printf 'dp reload\n' > server-runtime/console.in
```

日志证据（Asia/Shanghai，2026-09-29）：

- `server-runtime/logs/latest.log:40` `[00:00:05] ... [DungeonPlus] 插件重载完毕`
- `server-runtime/logs/latest.log:43` `[00:00:06] ... [DungeonPlus] [EmberCalamity] 地牢内容初始化完毕`

## 静态检查

- 字面执行 `rg -n '/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`：命中 1 行既有注释路径 `docs/ember-abyss-calamity.md`（L1）；该非 `message=` 注释未改，因本批硬边界为仅改 `message=`。
- `rg -n 'message=.*\/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`：0 行；玩家可见 `message=` 已无 `/ember`。
- `rg -n '枢纽菜单 → 灾厄' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`：命中 L17 `message=`。
- 目标文件 diff：仅一条 `message=` 玩家可见字符串替换。

## 交付边界

提交仅包含 `option.yml` 与本 STATUS；不带 dirty runtime/player/world。不要据此宣称 B0.1 或其它残余债已清。
