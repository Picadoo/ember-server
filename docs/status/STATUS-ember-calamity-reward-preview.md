# STATUS · 灾厄奖励预览真分页 A

- 时间：2026-09-28 22:42 Asia/Shanghai
- 依据：`docs/design/design-ember-calamity-reward-preview.md` · 批 A · tip `97e9654`
- 状态：**已施工 · 待测岗抽样**

## 完成

- 新建 `plugins/TrMenu/menus/ember_calamity_rewards.yml`：6 行布局、公共窗纯展示、日箱限 1、保护券约 10%、周首次日箱凝核、尾刀必给与概率、T3 刃/护符各约 1% 不保底。
- 修改 `plugins/TrMenu/menus/ember_calamity.yml` 图标 P：保留开箱声，去除空壳 tell，改为 `menu: ember_calamity_rewards`；父 lore 将「参战」改为「尾刀」，加入点击提示。
- 预览页无 `join` / `dp` / 发物动作；底栏 B 返回 `ember_calamity`。

## 热更与证据

- 执行：`printf 'trmenu reload\\n' > /workspace/minecraft/server-runtime/console.in`
- 结果：见 `server-runtime/logs/latest.log` 中的 TrMenu reload 日志；菜单文件数以热更日志为准。

## 禁项自检

- `plugins/CoreRpg/calamity.yml`：未改。
- `plugins/MythicMobs/`：未改。
- `plugins/CoreRpg/loot.yml`：未改。
- 窗期 / 奔赴 / `over_chance*`：未改。
- 精英预览：未施工。
- 全员参战保底 T3：未写；T3 明确为尾刀概率、各约 1%、不保底。
- `git push`：未执行。
