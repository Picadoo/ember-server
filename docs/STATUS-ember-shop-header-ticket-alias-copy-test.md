# STATUS · B2.89 ember_shop.yml 文件头票别名 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 22:35 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **依据：**设计 `21121c0` · 批准 `b6ff3d6` · 施工 tip `245fe40`
- **施工 tip 全 SHA：**`245fe409f0e530a1a920ea8c80cab205bb71353e`
- **范围：**仅 `plugins/TrMenu/menus/ember_shop.yml` L2–L3 文件头注释两行
- **口径：**纯静态薄验收；未开服、未长测、未挑刺、未改配置键值
- **是否已 push：**否（仅本地 commit）
- **阻塞点：**无

## 一句话

`ember_shop.yml` L2–L3 与 tip 荐案逐字一致：展示名为「体力药 / 周体力包」，SKU 别名仍为 `daily_ticket / weekly_ticket`，L3 在售摘要无别名括号；`b6ff3d6..245fe40` 相对批准基线仅 L2–L3 两行变更，L1 / L151 / 价 / 命令 / tell / Icons 零改；目标文件无「原日票/周票」「票已废」「B0.1」。

## 总评

| # | 验收点 | 结果 |
|---|---|---|
| 1 | L2–L3 与 tip 荐案逐字一致；展示名、SKU 别名及无别名括号的在售摘要均正确 | **PASS** |
| 2 | 仅 L2–L3 变更；L1 / L151 / 价 / 命令 / tell / Icons 相对 `b6ff3d6..245fe40` 零改 | **PASS** |
| 3 | 目标行及整份 live 文件无「原日票/周票」「票已废」「B0.1」 | **PASS** |

## 各点证据

### 1 · 目标行原文

```yaml
# S0：体力药 / 周体力包（展示名；SKU 别名仍 daily_ticket / weekly_ticket）
# 在售：月卡、战令解锁、体力药、周体力包
```

以上为 `plugins/TrMenu/menus/ember_shop.yml` L2–L3；与设计 tip §2 荐案逐字一致。L2 保留展示名及 `daily_ticket / weekly_ticket` 别名事实，L3 仅保留在售摘要，无别名括号。

### 2 · 施工 diff / 零漂旁证

```text
git show 245fe40 --stat
→ plugins/TrMenu/menus/ember_shop.yml | 4 ++--
  1 file changed, 2 insertions(+), 2 deletions(-)

git diff b6ff3d6..245fe40 -- plugins/TrMenu/menus/ember_shop.yml
→ 仅 L2–L3：
  - # S0：体力药 / 周体力包（原日票/周票）
  - # 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）
  + # S0：体力药 / 周体力包（展示名；SKU 别名仍 daily_ticket / weekly_ticket）
  + # 在售：月卡、战令解锁、体力药、周体力包
```

施工 tip 仅触及目标文件头 L2–L3。由该单一 hunk 旁证，L1、L151、价、购买命令、tell、Icons 及其它内容均相对批准基线零改；当前 `245fe40` 与 live 目标文件亦无额外 diff。

### 3 · 违禁核对

```text
rg -n '原日票/周票|票已废|B0\.1' plugins/TrMenu/menus/ember_shop.yml
→ 无命中（exit 1）
```

目标行单独核对同样无上述违禁词；SKU 命中仅保留合法的 `daily_ticket` / `weekly_ticket` 配置命令事实，未改价、命令、tell 或 Icons。

## 纪律确认

- `git pull` 已执行并显示 Already up to date。
- 未起服、未长测、未挑刺、未硬开精英壳。
- 未改配置键值；仅新增本 STATUS 测报文件。
- 脏工作区中与本窗无关的文件未 stage。
- **禁止 push：已遵守；本测报只做本地 commit。**

## 回报摘要（交总控）

| 项 | 值 |
|---|---|
| 总评 | **PASS** |
| 各点 | 1 PASS · 2 PASS · 3 PASS |
| 施工 tip SHA | `245fe40` · `245fe409f0e530a1a920ea8c80cab205bb71353e` |
| 是否已 push | **否** |
| 报告路径 | `docs/STATUS-ember-shop-header-ticket-alias-copy-test.md` |
| ahead 相对 origin/main | 见最终回报；本报告 commit 后为 ahead 1 |
| 阻塞点 | 无 |
| 目标行原文（L2–L3） | `# S0：体力药 / 周体力包（展示名；SKU 别名仍 daily_ticket / weekly_ticket）`；`# 在售：月卡、战令解锁、体力药、周体力包` |
