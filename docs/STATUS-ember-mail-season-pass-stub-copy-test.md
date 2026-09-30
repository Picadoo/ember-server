# B2.73 · mail season_pass_stub 文案 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 20:57 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`7290f4d244cf5dd0375c05edd2fc3e88ebbb13fb`（当时与 origin/main 一致）
- **施工 tip SHA：**`7290f4d`（全：`7290f4d244cf5dd0375c05edd2fc3e88ebbb13fb`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design-ember-mail-season-pass-stub-copy.md`（`53b92d1` / 批 A `87e40af`）
- **是否已 push：**否（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/STATUS-ember-mail-season-pass-stub-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | `season_pass_stub.title`=`战令结算（维护备忘）` | **PASS** |
| 2 | `body`=`赛季奖励尚未开放；本邮件仅作维护备忘。` | **PASS** |
| 3 | 键名仍为 `season_pass_stub`；attachments 零改（`ember_crystal_cash: 30`、`coin: 200`） | **PASS** |
| 4 | 其它模板相对 `87e40af` 零 diff（除本模板 title/body 外） | **PASS** |
| 5 | 禁项：无「票已废」「B0.1 已清」/战令已落地宣称；无旧「占位」于该模板 title/body | **PASS** |

## title / body 原文

```yaml
  season_pass_stub:
    title: "战令结算（维护备忘）"
    body: "赛季奖励尚未开放；本邮件仅作维护备忘。"
    attachments:
      ember_crystal_cash: 30
      coin: 200
```

（与设计 tip §2 荐案逐字一致；`plugins/CoreRpg/mail.yml` L11–L16。）

## 违禁词 / 旧文案 rg

```text
rg -n '票已废|B0\.1 已清|B0\.1已清|占位' plugins/CoreRpg/mail.yml
→ (无命中)

# season_pass_stub title/body 专项：
#   title 无「占位」；body 无「占位」
#   块内无「票已废」「B0.1 已清」「战令已落地」「已完整落地」
```

设计 tip 内仅有「禁写」纪律句提及「票已废」「B0.1 已清」，属禁项说明，非宣称；目标 `mail.yml` 无此类宣称。本测报亦不宣称 B0.1 已清、不宣称战令已落地。

## 旁证（静态）

- `git show 7290f4d --stat`：仅 `plugins/CoreRpg/mail.yml`（`4 ++--` → 1 file, 2 insertions, 2 deletions）。
- `git show 7290f4d -- plugins/CoreRpg/mail.yml`：仅改 `season_pass_stub` 的 `title` / `body` 两行：
  - `- title: "战令结算（占位）"` → `+ title: "战令结算（维护备忘）"`
  - `- body: "赛季占位奖励。"` → `+ body: "赛季奖励尚未开放；本邮件仅作维护备忘。"`
- `git diff 87e40af HEAD -- plugins/CoreRpg/mail.yml`：同上仅两行 title/body；attachments 与其它模板零 diff。
- 键名核对：`rg -n '^\s+season_pass_stub:'` → L11；其它模板键（`maintenance_comp` / `vip_daily_gift` / `pass_track_free` / `pass_track_paid_welcome` / `event_box`）仍在且相对 `87e40af` 内容一致。
- 未起服、未长测、未挑刺；本岗未改配置 / YAML。

## 纪律确认

- 勿宣称 B0.1 已清：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
