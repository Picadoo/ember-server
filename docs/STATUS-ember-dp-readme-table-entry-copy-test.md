# B2.72 · DP README 表说明列 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 20:52 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull --ff-only` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`db12737cb9163bd99315f559dafb347ced242ce0`（当时与 origin/main 一致）
- **施工 tip SHA：**`db12737`（全：`db12737cb9163bd99315f559dafb347ced242ce0`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design-ember-dp-readme-table-entry-copy.md`（`a5d1ce1` / 批 A `9a7bd68`）
- **是否已 push：**否（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/STATUS-ember-dp-readme-table-entry-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | L11 说明列=`地图/出生/遗留票物入场条件/通关 ni give`（与 tip 荐案一致） | **PASS** |
| 2 | 无旧「入场券条件」（该表说明列语境） | **PASS** |
| 3 | 路径列与其它表行相对批准基线零扩改 | **PASS** |
| 4 | option.yml / dungeon YAML / mail 相对 `9a7bd68` 零 diff | **PASS** |
| 5 | 禁项：无「票已废」「B0.1 已清」宣称 | **PASS** |

## L11 原文

```text
| `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | 地图/出生/遗留票物入场条件/通关 ni give |
```

说明列片段：`地图/出生/遗留票物入场条件/通关 ni give`（与设计 tip §2 荐案逐字一致）。

## 违禁词 / 旧文案 rg

```text
rg -n '票已废|B0\.1 已清|入场券条件' plugins/DungeonPlus/README-ember-dungeons.md
→ (无命中)
```

设计 tip 内仅有「禁写」纪律句提及「票已废」「B0.1 已清」，属禁项说明，非宣称；目标 README 无此类宣称。

## 旁证（静态）

- `git show db12737`：仅改 `plugins/DungeonPlus/README-ember-dungeons.md` 1 行说明列（`入场券条件` → `遗留票物入场条件`）；路径列未动。
- `git diff 9a7bd68 -- plugins/DungeonPlus/README-ember-dungeons.md`：同上单行说明列；路径列与 L12+ 其它表行零 diff。
- `git diff 9a7bd68 --name-status -- plugins/DungeonPlus/dungeon/`：空（option.yml / dungeon YAML 零 diff）。
- `git diff 9a7bd68 --name-only -- . | rg -i mail`：空（mail 零 diff）。
- 未起服、未长测、未挑刺、未硬开精英壳；本岗未改配置。

## 纪律确认

- 勿宣称 B0.1 已清：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
