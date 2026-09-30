# B2.74 · mail vip_daily_gift body · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 21:02 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`e7ee555e4e4dcfec21fdb48bb62df28090efac4d`（当时与 origin/main 一致）
- **施工 tip SHA：**`e7ee555`（全：`e7ee555e4e4dcfec21fdb48bb62df28090efac4d`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design-ember-mail-vip-daily-gift-copy.md`（`d0c7f27` / 批 A `607ced3`）
- **是否已 push：**否（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/STATUS-ember-mail-vip-daily-gift-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | `vip_daily_gift.body`=`轻量 QoL 日礼仍在维护；勋阶累进口径未定稿。` | **PASS** |
| 2 | 键名仍为 `vip_daily_gift`；title=`勋阶·日礼`；attachments 零改（`coin: 120`、`mat_ember_shard: 1`） | **PASS** |
| 3 | 其它模板相对 `607ced3` 零 diff | **PASS** |
| 4 | 禁项：无「票已废」「B0.1 已清」/勋阶已落地；无旧「勋阶正式累进前」 | **PASS** |

## title / body / attachments 原文

```yaml
  vip_daily_gift:
    title: "勋阶·日礼"
    body: "轻量 QoL 日礼仍在维护；勋阶累进口径未定稿。"
    attachments:
      coin: 120
      mat_ember_shard: 1
```

（与设计 tip §2 荐案逐字一致；`plugins/CoreRpg/mail.yml` L18–L23。）

## 违禁词 / 旧文案 rg

```text
rg -n '票已废|B0\.1 已清|B0\.1已清|勋阶正式累进前|正式已上线|完整落地|勋阶已落地' plugins/CoreRpg/mail.yml
→ (无命中；exit 1)

# vip_daily_gift 块内专项：
#   body 无旧「勋阶正式累进前」
#   块内无「票已废」「B0.1 已清」「勋阶已落地」「正式已上线」「完整落地」
```

设计 tip 内仅有「禁写」纪律句提及「票已废」「B0.1 已清」，属禁项说明，非宣称；目标 `mail.yml` 无此类宣称。本测报亦不宣称 B0.1 已清、不宣称勋阶已落地。

## 旁证（静态）

- `git show e7ee555 --stat`：仅 `plugins/CoreRpg/mail.yml`（`2 +-` → 1 file, 1 insertion, 1 deletion）。
- `git show e7ee555 -- plugins/CoreRpg/mail.yml`：仅改 `vip_daily_gift` 的 `body` 一行：
  - `- body: "轻量 QoL 日礼（勋阶正式累进前）。"` → `+ body: "轻量 QoL 日礼仍在维护；勋阶累进口径未定稿。"`
- `git diff 607ced3 HEAD -- plugins/CoreRpg/mail.yml`：同上仅一行 body；title / attachments 与其它模板零 diff。
- 其它模板逐块对照 `607ced3`：`maintenance_comp` / `season_pass_stub` / `pass_track_free` / `pass_track_paid_welcome` / `event_box` 均为 SAME。
- 键名核对：`rg -n '^  vip_daily_gift:'` → L18；`title: "勋阶·日礼"` 保留；attachments `coin: 120`、`mat_ember_shard: 1` 保留。
- `git diff e7ee555 HEAD -- plugins/CoreRpg/mail.yml`：空（HEAD 即施工 tip）。
- 未起服、未长测、未挑刺；本岗未改配置 / YAML。

## 纪律确认

- 勿宣称 B0.1 已清：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
