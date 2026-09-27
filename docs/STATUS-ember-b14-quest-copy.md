# STATUS · B1.4 主线第一卷 hint / join 文案落地

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-插件岗
- 依据：`docs/STATUS-ember-b1-quest-copy.md`

## 1. 落地片段

- `plugins/CoreRpg/config.yml`
  - 替换 `join_message` 玩家入口文案：引导右键灰烛、枢纽菜单路径、工坊 NPC、进本门槛。
  - 移除玩家可见的 `/corerpg`、`/ember` 与指令清单教学。
- `plugins/CoreRpg/quest.yml`
  - 替换第一卷 ch1～6 指定 `hint` / `done` 文案；`intro` 已检查，无需替换。
  - 文案统一为「打开枢纽菜单 → …」、右键 NPC 与场景方位，不改步骤 type/count、数值、票或底层逻辑。
- `plugins/CoreRpg/src/main/resources/` 不存在，因此无资源副本可同步。

## 2. 验收（grep）

对 `config.yml` 的 `join_message` 和 `quest.yml` 第一卷 ch1～6 的玩家字段执行残留扫描：

```text
/(corerpg|dp|hub|ember) + covenant set + talent unlock + afk N：0 命中
```

结果：无 `/corerpg`、`/dp`、`/hub`，无参数教学命中。`quest.yml` 中剩余的 1 个 `/ember` 仅在文件头文档路径 `docs/ember-mainline-spec.md`，不在玩家字段。

`git diff --check`：通过。

## 3. B1.4b

已做。第二卷 ch7～10 共 13 处 `打开 /ember`（含「或枢纽菜单」）已统一为 `打开枢纽菜单 → …`。

## 4. STATUS 路径

`docs/STATUS-ember-b14-quest-copy.md`

## 5. 风险

低：仅改玩家可见 YAML 文案；未改命令逻辑、数值、票、步骤结构，也未碰 TrMenu。资源源码副本目录不存在，运行时以 `plugins/CoreRpg/` 下文件为准。未 commit/push。
