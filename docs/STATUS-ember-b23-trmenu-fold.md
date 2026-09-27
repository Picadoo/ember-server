# STATUS · B2.3 TrMenu 灾厄/团本测试入口降权

- 时间：2026-09-27 22:30（Asia/Shanghai）
- 范围：仅 `plugins/TrMenu/menus/ember_calamity.yml`、`plugins/TrMenu/menus/ember_raid.yml`
- `server-runtime/plugins` 是指向 `../plugins` 的 symlink，因此只改源目录一处即可；两边实际读取同一份文件。
- CoreRpg 仍随现网 1.15.9，本轮未动 jar、数值、票据或 ops。

## B2.3 改动

### 灾厄

改动前 Layout：

```yaml
- '#########'
- '#       #'
- '# S P R T#'
- '#       #'
- '####B####'
```

改动后 Layout：

```yaml
- '#########'
- '#       #'
- '# S P R #'
- '#       #'
- '####B##T#'
```

`S` 仍为唯一正式入口「§a奔赴灾厄」，动作仍是 `corerpg calamity join`。`T` 移到底行右侧角落，保持 `barrier`、§8 灰色标题，并在 lore 明确「非正式入口 · 仅管理/测试 · 正式请走奔赴」；底层管理测试动作 `dp start EmberCalamity` 保留。

### 团本

- 无独立测试钮，主入口「§a开始协作」及 `corerpg enter raid` 不变。
- 次数说明移除玩家可见的 `/ni give <玩家> ticket_ember_raid 1`。
- 进本提示将「（/corerpg level）」改为「（见菜单等级要求）」，保留 Lv.35 门槛信息但不教手打命令。

## 验收

- 玩家打开灾厄菜单时，主行不再放置测试钮；主路径突出正式门「奔赴灾厄」，P/R 仅为奖励与窗口说明。
- 测试钮位于底行远角，灰色 barrier 且 lore 标明非正式、仅管理/测试，不与正式门并列抢眼。
- 团本菜单不显示 `/ni give` 或 `/corerpg level` 玩家手打提示。
- YAML 仅做上述视觉/玩家提示调整；未 reload、未升 jar。

## B1.4（本岗未动）

否。本轮追加调度明确本执行器只收口 B2.3，`quest.yml`/`config.yml` 由并行执行器负责；本岗未改 quest/config。改动前抽查仍可见口吻债务，供 B1.4 执行器验收定位：

- `plugins/CoreRpg/config.yml`：`join_message` 中的 `/corerpg` 指令式引导。
- `plugins/CoreRpg/quest.yml`：第一卷 ch1～6 的 `hint`/`done` 中的 `/corerpg`（及 `/ember`）引导。
- `plugins/CoreRpg/quest.yml`：后续章节仍有 `/corerpg afk` 等命令式 hint。

## 风险

- 未在运行中的 TrMenu 实例执行 reload；需由部署/测试岗按现网流程重载或重启后做一次玩家视角验收。
- `T` 仍保留管理测试命令，管理权限/OP 约束沿用原配置；本轮只降低可见性，未改变命令权限。
