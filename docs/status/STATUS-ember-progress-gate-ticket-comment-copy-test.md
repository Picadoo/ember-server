# B2.87 · progress.yml 等级门槛旁注 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 22:17 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线：**批准 `5881af9` → 施工 tip `5b41046`
- **施工 tip SHA：**`5b41046`（全：`5b4104635ee7b5b30c752dbf3b17c6c0de540a8c`）
- **测报正文 SHA：**`89bbd57`（全：`89bbd57871af6795276759506093d12383008b1b`）。
- **设计 tip：**`docs/design/design-ember-progress-gate-ticket-comment-copy.md`
- **是否已 push：**否（仅本地 commit，未执行 `git push`）
- **报告路径：**`docs/status/STATUS-ember-progress-gate-ticket-comment-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | live `progress.yml` 约 L137 旁注与 tip 荐案逐字一致（含标点/空格） | **PASS** |
| 2 | 仅注释变更；`level_gates` 键值相对 `5881af9..5b41046` 零改 | **PASS** |
| 3 | `progress.yml` 不含「票已废」「B0.1 已清」 | **PASS** |

## 目标行原文

```yaml
#   DP：各 option.yml dungeon-start 第一条条件 js '%corerpg_gate_<id>%'=='yes'（OP 豁免）；等级门槛在前，先于入场扣体力/遗留票物；被拒不扣体力/遗留票物（口径维护备忘）。
```

（`plugins/CoreRpg/progress.yml` L137；与设计 tip §2 荐案逐字一致。）

## 违禁词 rg

```text
rg -n -F -e '票已废' -e 'B0.1 已清' plugins/CoreRpg/progress.yml
→ (无命中；exit 1)
```

目标 `progress.yml` 未出现上述禁词；本测报不作相关完成或废止宣称。

## 旁证（静态）

- `git show 5b41046 --stat`：仅 `plugins/CoreRpg/progress.yml`（`2 +-` → 1 file, 1 insertion, 1 deletion）。
- `git show 5b41046 -- plugins/CoreRpg/progress.yml`：仅 L137 注释一行由旧旁注替换为目标旁注。
- `git diff 5881af9..5b41046 -- plugins/CoreRpg/progress.yml`：仅目标注释行变化；过滤注释行后的内容 diff 为空。
- `level_gates` 相对批准基线 `5881af9` 与施工 tip `5b41046` 的键值一致：`daily: 10`、`weekly: 20`、`abyss: 25`、`calamity: 30`、`raid: 35`、`guild_boss: 0`、`elite: 40`。
- 目标旁注保留 DP gate、OP 豁免、等级门槛在前、先于入场扣体力/遗留票物、被拒不扣体力/遗留票物及维护备忘口径。
- 未起服、未长测、未挑刺；未改玩法键值；未捆绑其它旁记。

## 纪律确认

- 本验收仅作纯静态薄验收。
- 不作「票已废」或「B0.1 已清」宣称。
- 不执行 `git push`；仅本地提交测报。
- 仅 stage 本 STATUS 文件；工作区其它脏文件不纳入。

- **测报 tip/HEAD（回填提交）：**`89bbd57` 后回填为本次最终本地 tip；最终 SHA 见回报。
