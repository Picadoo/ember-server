# B2.75 · mail pass_track_free body · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 21:07 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`07e427d6fc0a3cba36ee80faf4ddfded6eabc416`（当时与 origin/main 一致）
- **施工 tip SHA：**`07e427d`（全：`07e427d6fc0a3cba36ee80faf4ddfded6eabc416`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design/design-ember-mail-pass-track-free-copy.md`（设计 `41e0140` / 批 A `773f46d`）
- **是否已 push：**否（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/status/STATUS-ember-mail-pass-track-free-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | `pass_track_free.body`=`赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时领取。` | **PASS** |
| 2 | 键名仍为 `pass_track_free`；title=`战令·免费轨补给`；attachments 零改（`coin: 80`、`mat_ember_shard: 2`） | **PASS** |
| 3 | 其它模板相对 `773f46d` 零 diff | **PASS** |
| 4 | 禁项：无「票已废」「B0.1 已清」/战令已落地；无残留英文 `claim`（本模板 body） | **PASS** |

## title / body / attachments 原文

```yaml
  pass_track_free:
    title: "战令·免费轨补给"
    body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时领取。"
    attachments:
      coin: 80
      mat_ember_shard: 2
```

（与设计 tip §2 荐案逐字一致；`plugins/CoreRpg/mail.yml` L24–L29。）

## 违禁词 / 旧文案 rg

```text
rg -n '票已废|B0\.1 已清|B0\.1已清|战令已落地|战令已完整落地|正式已上线|完整落地' plugins/CoreRpg/mail.yml
→ (无命中；exit 1)

rg -n 'claim' plugins/CoreRpg/mail.yml
→ (无命中；exit 1)

# pass_track_free 块内专项：
#   body 无残留英文 claim（已改为「领取」）
#   块内无「票已废」「B0.1 已清」「战令已落地」「战令已完整落地」
```

设计 tip 内仅有「禁写」纪律句提及「B0.1 已清」/战令已完整落地，属禁项说明，非宣称；目标 `mail.yml` 无此类宣称。本测报亦不宣称 B0.1 已清、不宣称战令已落地。

## 旁证（静态）

- `git show 07e427d --stat`：仅 `plugins/CoreRpg/mail.yml`（`2 +-` → 1 file, 1 insertion, 1 deletion）。
- `git show 07e427d -- plugins/CoreRpg/mail.yml`：仅改 `pass_track_free` 的 `body` 一行：
  - `- body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时 claim。"` → `+ body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时领取。"`
- `git diff 773f46d HEAD -- plugins/CoreRpg/mail.yml`：同上仅一行 body；title / attachments 与其它模板零 diff。
- 其它模板逐块对照 `773f46d`：`maintenance_comp` / `season_pass_stub` / `vip_daily_gift` / `pass_track_paid_welcome` / `event_box` 均为 ZERO_DIFF；顶层 `enabled`/`max_inbox` 亦同。
- 键名核对：`rg -n '^  pass_track_free:'` → L24；`title: "战令·免费轨补给"` 保留；attachments `coin: 80`、`mat_ember_shard: 2` 保留。
- `git diff 07e427d HEAD -- plugins/CoreRpg/mail.yml`：空（HEAD 即施工 tip）。
- YAML 解析：`templates.pass_track_free.body` 与荐案逐字相等；`has_claim=False`。
- 未起服、未长测、未挑刺；本岗未改配置 / YAML。

## 纪律确认

- 勿宣称 B0.1 已清：本测报未作此宣称。
- 勿宣称战令已落地：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
