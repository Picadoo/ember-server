# B2.71 · DP README 次数段标题 · 测试岗测报

- **总评：PASS**
- **测报时间：**2026-09-30 20:46 Asia/Shanghai（CST / UTC+8）
- **工作区：**`/workspace/minecraft`
- **方法：**纯静态 `rg` / `git show a888348` / `git diff 1227e0c..a888348`；未长测、未挑刺、未起服；精英壳未硬开
- **是否已 push：否**

## 1. 锚点

| 项 | 值 |
|----|-----|
| tip | `docs/design-ember-dp-readme-ticket-section-heading-copy.md` |
| tip 设计 SHA | `aadfe7785f98e37441b3e0f22b5a7e44cc6d0348`（`aadfe77`） |
| 批 A 基线 | `1227e0ce2f3af466bc5cf227cf55aac335ab48ed`（`1227e0c`） |
| 施工 tip SHA（全） | `a888348cde7ab722d242180f8cd45e36d2069996` |
| 施工 tip short SHA | `a888348` |
| 目标文件 | `plugins/DungeonPlus/README-ember-dungeons.md` 约 L29 |
| 测报路径 | `docs/STATUS-ember-dp-readme-ticket-section-heading-copy-test.md` |

## 2. 各点验收

| # | 条件 | 结果 |
|---|------|------|
| 1 | L29 全文须为：`## 次数（遗留票物 / 体力）` | **PASS**（逐字一致） |
| 2 | 不得再出现 `## 次数（入场券）` | **PASS**（`rg` 无命中） |
| 3 | L31 导语、票表三行、发放句与 tip §3 锁定原文逐字一致 | **PASS**（Python 逐行比对均 True） |
| 4 | L11「入场券条件」仍保留 | **PASS**（L11 含「入场券条件」） |
| 5 | 禁「票已废」「B0.1 已清」及等价完成宣称 | **PASS**（`rg` 无命中） |
| 6 | mail / `plugins/DungeonPlus/dungeon/` YAML：相对 `1227e0c` 零 diff（仅 README 该一行） | **PASS** |

## 3. L29 / L11 / L31 与票表 / 发放原文

```text
L11: | `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | 地图/出生/入场券条件/通关 ni give |
L29: ## 次数（遗留票物 / 体力）
L31: 维护备忘：DP 1.4 无原生「每日 N 次」字段，物品仅用于 DP 入场条件；现行玩家次数以体力为准。
L35: | `ticket_ember_daily` | 余烬日票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
L36: | `ticket_ember_weekly` | 余烬周票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
L37: | `ticket_ember_abyss` | 余烬深渊票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
L41: 维护备忘：自动发放未落地前，仅管理/测试可用 `/ni give <玩家> ticket_ember_daily 3` 做物测；不作为玩家操作说明。
```

## 4. 违禁词 rg

命令（节选）：

```bash
rg -n '## 次数（入场券）|票已废|B0\.1 已清|B0.1已清|票已作废|票制已废|入场券已废' plugins/DungeonPlus/README-ember-dungeons.md
```

结果：无命中（空输出 / exit 1）。

正向旁证：

```bash
rg -n '## 次数（遗留票物 / 体力）|入场券条件' plugins/DungeonPlus/README-ember-dungeons.md
# 29:## 次数（遗留票物 / 体力）
# 11:…入场券条件…
```

## 5. 旁证

- `git pull`：Already up to date；`HEAD` = `a888348`。
- `git show a888348`：仅改 `plugins/DungeonPlus/README-ember-dungeons.md`（1 insertion / 1 deletion）；旧 `## 次数（入场券）` → 新 `## 次数（遗留票物 / 体力）`。
- `git diff 1227e0c..a888348 --numstat` 预期等同：仅 README 一行；实测相对 `1227e0c` 的 README diff 仅 L29 标题一行。
- `git diff 1227e0c -- plugins/DungeonPlus/dungeon/`：空（YAML 零 diff）。
- `git diff 1227e0c -- plugins/CoreRpg/mail plugins/CoreRpg/mail.yml`：空（mail 零 diff）。
- `git diff 1227e0c --name-only -- plugins/DungeonPlus/`：仅 `plugins/DungeonPlus/README-ember-dungeons.md`。
- tip §3 锁定原文（L31 / 票表三行 / 发放句）与 live README 逐字一致；表头/分隔线/L39–L40 未动。
- 本岗未改配置；未宣称 B0.1 已清；未 git push。

## 6. 阻塞点

无。

## 7. 纪律核对

- 勿 git push：**遵守**（本测报仅本地 commit）
- 本岗不改配置：**遵守**
- 勿宣称 B0.1 已清：**遵守**
- 禁长测/挑刺/起服；精英壳勿硬开：**遵守**
