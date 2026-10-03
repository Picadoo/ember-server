# STATUS · B2.3 灾厄/团本 TrMenu 测试钮降权抽检

- 时间：2026-09-27 22:33 CST（Asia/Shanghai）
- 依据：`docs/status/STATUS-ember-b23-trmenu-fold.md`
- 总评：**静态 PASS；live 条件 PASS（已尝试但因并行 TTK/服务端重启未完成玩家菜单目视）**
- 本岗未改菜单、CoreRpg、数值、MM 或票据；未执行手工 `/trmenu reload`。

## 静态验收

### `plugins/TrMenu/menus/ember_calamity.yml` — PASS

- 主行是 `# S P R #`，无 T。
- T 位于底行右角：`####B##T#`。
- S「§a奔赴灾厄」动作仍为 `command: corerpg calamity join`。
- T 为 `barrier`，标题为 §8 灰色；lore 含「非正式」「仅管理/测试」「正式请走奔赴」。
- `command: dp start EmberCalamity` 仍保留。

### `plugins/TrMenu/menus/ember_raid.yml` — PASS

- 玩家可见文案无 `/ni give`。
- 等级提示为「见菜单等级要求」；无 `/corerpg level` 教学。
- 主入口「§a开始协作」及 `command: corerpg enter raid` 保持。

## Live

- 已用一次非 OP 临时玩家 `B23Menu1480` 尝试登录并打开灾厄/团本菜单；AuthMe 登录成功，但在到达 play 服并取得窗口前服务端于 22:31:41 CST 重启，最终 joinPlay 超时，故无 live 窗口截图/槽位证据。
- 并行 TTK 正在使用 `TtkOpBot`/`TtkR7721`，未抢端口、未再重试、未动其权限。
- 运行日志显示 TrMenu 在 22:31:48 CST 启动并加载 30 个菜单，未见本两菜单的加载错误；因此无「需 trmenu reload」债。

## 版本 / ops / 收尾

- CoreRpg jar 与日志均为 **1.15.9**（jar `plugin.yml`；日志 22:31:42/47/48 CST）。
- `login-runtime/ops.json` 为 `[]`。
- `server-runtime/ops.json` 当前由并行 TTK 保留 `TtkOpBot` OP；本岗未改动，未擅自 deop，以免抢占/中断其测试。TTK 收尾后应恢复为 `[]`。
