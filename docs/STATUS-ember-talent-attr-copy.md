# STATUS · B2.15 天赋菜单裸属性键人话化 A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:29 Asia/Shanghai |
| Role | 余烬-插件 |
| Design tip | `26d7895` |
| Approval | `f4e2bcd` |
| Scope | 仅 `plugins/TrMenu/menus/ember_talent.yml` 九行玩家可见效果 lore |
| Result | A 文案施工完成；数值、正负号、`·` 分隔符及周边 YAML 保持不变 |

## 验收

- 9 个裸属性键已分别替换为：物攻、暴伤、暴击率、移速、攻速、击中减速、受伤、生命、物防。
- 验收命令：
  `rg 'phys_damage|crit_damage_pct|crit_chance_pct|move_speed_pct|attack_speed_pct|on_hit_slow_pct|damage_taken_pct|max_health|phys_defense' plugins/TrMenu/menus/ember_talent.yml`
- 结果：**0 行（rg=0）**。
- `git diff` 对菜单仅包含批准的九行 lore 替换。

## 热更证据

- 已执行：`printf 'trmenu reload\n' > server-runtime/console.in`
- TrMenu 日志证据（`server-runtime/logs/latest.log`，Asia/Shanghai）：`[00:28:47] [TConfigWatcherService-1/INFO]: [TrMenu] 良好 | 自动重新载入菜单 ember_talent.yml (5ms)`。

## 硬边界

- `talent.yml` 的 stats/keys/requires/cost、unlock command 实参、誓约/点数/等级/洗点逻辑未改。
- `passive` / `skill` 类型词未改；未执行方案 B；未处理 B0.1、reward-page `NI id:` 或 `gear_ember_*` 残余。
- 其它菜单、runtime/player/world 文件未纳入本次提交；工作区既有无关 dirty 未触碰。
- 本次施工禁项：**EMPTY**。

## 交付

- Commit message：`fix(trmenu): B2.15 talent attr keys humanize`
- Push：提交后推送 `origin/main`。
