# B2.70 · DP README「已知占位」段标题 · 纯静态薄验收测报

- **总评：PASS**
- **测岗：**余烬-测试岗执行器
- **测时：**2026-09-30 20:42 Asia/Shanghai（CST）
- **施工 tip SHA：**`91c03c8`（`91c03c880fb160df40b2e69891354deb000cc80f` · `docs: B2.70 rename DP README placeholder heading`）
- **批准 tip：**`54669ac` · 设计 tip short SHA：`9d23f6e` · tip 文档 `docs/design/design-ember-dp-readme-known-placeholder-copy.md`
- **范围：**仅静态核对 `plugins/DungeonPlus/README-ember-dungeons.md` 约 L57 段标题一行；未改配置、未长测、未挑刺、未起服
- **是否已 push：否**

## 各点对照

| # | PASS 条件 | 结果 | 旁证 |
|---|-----------|------|------|
| 1 | L57 全文为 `## 维护备忘（地图与脚本）` | **PASS** | `sed -n '57p'` 逐字一致 |
| 2 | 不得再出现 `## 已知占位` | **PASS** | rg `已知占位` 无匹配 |
| 3 | 紧随三条 bullet 与 tip §3 锁定原文逐字一致 | **PASS** | L59–L61 vs tip L34–L36 `diff` exit 0 |
| 4 | 禁「票已废」「B0.1 已清」「地图已正式」及等价完成宣称 | **PASS** | 违禁词 rg 无匹配 |
| 5 | 次数段标题/L31/票表/发放句/mail/`dungeon/` YAML 相对 `54669ac` 零 diff（仅 README 该一行） | **PASS** | `git diff 54669ac..HEAD` 仅该 README 1 行；次数段 L29–41 `diff` exit 0；`dungeon/` 0 bytes |

## L57 原文

```text
## 维护备忘（地图与脚本）
```

## 三条 bullet 原文（L59–L61）

```text
- 地图已按本拆分（仍为测试区切片，出生 `-40,65,270`）；近出生点有主题方块标记；正式艺术面由 WorldEdit 再调。详见 `docs/status/STATUS-ember-maps.md`。
- `$kill` 依赖 MM Display「余烬地窟僵尸/骷髅/蛮兵」；若对不上，看 DP debug 或改 monster.yml。
- 周本装备保底目前固定发刃。
```

与 tip §3 锁定原文逐字一致（`diff` exit 0）。

## 违禁词 rg

```text
$ rg -n '## 已知占位|已知占位' plugins/DungeonPlus/README-ember-dungeons.md
(no matches)

$ rg -n '票已废|B0\.1 已清|地图已正式|票制废止|B0\.1.*清账|地图正式化' plugins/DungeonPlus/README-ember-dungeons.md
(no matches)
```

## `git show 91c03c8 --stat`

```text
commit 91c03c880fb160df40b2e69891354deb000cc80f
Author: Picadoo <Picadoo@users.noreply.github.com>
Date:   Wed Sep 30 20:41:16 2026 +0800

    docs: B2.70 rename DP README placeholder heading

 plugins/DungeonPlus/README-ember-dungeons.md | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
```

施工 diff 仅：

```diff
-## 已知占位
+## 维护备忘（地图与脚本）
```

## 旁证摘要

- HEAD=`91c03c8`；相对 `54669ac` 仅触及 `plugins/DungeonPlus/README-ember-dungeons.md`（标题一行）
- 次数段标题 L29 `## 次数（入场券）`、L31 维护备忘句、票表 L35–L37、发放句 L41 相对 `54669ac` 零 diff
- `plugins/DungeonPlus/dungeon/` 相对 `54669ac` 零 diff（`wc -c`=0）；本提交未触及 mail / 票 YAML
- 三条 bullet 事实（地图切片 / `$kill` Display / 周本保底发刃）零改
- 纪律：纯静态 rg / `git show`；未宣称 B0.1 已清；本岗未改配置；未 push

## 阻塞点

无。

## 纪律自检

- 纯静态；禁长测/挑刺/起服 — 已遵守
- 本岗不改配置 — 已遵守（仅写本测报）
- 勿宣称 B0.1 已清 — 已遵守
- 勿 git push — 已遵守
