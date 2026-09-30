# B2.81 · mail event_box.body · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 21:35 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`699a7cbdd3015f7cfb6f690a9f5ef192b32893aa`（与 origin/main 一致）
- **施工 tip SHA：**`699a7cb`（全：`699a7cbdd3015f7cfb6f690a9f5ef192b32893aa`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 commit 后 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design-ember-mail-event-box-body-copy.md`（设计 `85291bf` / 批 A `30b739b`）
- **是否已 push：否**（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/STATUS-ember-mail-event-box-body-copy-test.md`
- **阻塞点：**无
- **ahead：**相对 `origin/main` ahead 1（未 push）

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | `event_box.body` 现为：`活动箱邮件仍在维护；奖励按活动发放。` | **PASS** · live L38 与 tip 荐案逐字一致 |
| 2 | 不得再出现该字段旧文「活动奖励。」 | **PASS** · `rg` 整文件无命中「活动奖励。」 |
| 3 | `title` / `attachments` 及其它邮件模板相对 `30b739b..699a7cb` 零改（仅 body +1/-1） | **PASS** · numstat `1	1`；全量 diff 仅 body 一行 |
| 4 | 勿宣称 B0.1 已清/票已废/活动系统完整落地；精英壳勿硬开 | **PASS** · 禁词 rg 无命中；本岗仅静态、未起服、未改配置、未硬开精英壳 |

## title / body / attachments 原文

```yaml
  event_box:
    title: "活动箱"
    body: "活动箱邮件仍在维护；奖励按活动发放。"
    attachments:
      consumable_ember_stamina_30: 1
      gem_ember_sharp: 1
```

（与设计 tip §2 荐案逐字一致；`plugins/CoreRpg/mail.yml` L36–L41。）

## 违禁词 / 旧文案 rg

```text
rg -n '票已废|B0\.1 已清|B0\.1已清|活动系统完整落地|活动奖励。' plugins/CoreRpg/mail.yml
→ (无命中；exit 1)

rg -n '活动箱邮件仍在维护；奖励按活动发放。' plugins/CoreRpg/mail.yml
→ 38:    body: "活动箱邮件仍在维护；奖励按活动发放。"
```

设计 tip 内仅有「禁写」纪律句提及「票已废」「B0.1 已清」「活动系统完整落地」，属禁项说明，非宣称；目标 `mail.yml` 无此类宣称。本测报亦不宣称 B0.1 已清、票已废或活动系统完整落地。

## 旁证（静态）

1. **施工 tip `git show 699a7cb --stat`：**仅 `plugins/CoreRpg/mail.yml`（`2 +-` → 1 file, 1 insertion, 1 deletion）。
2. **`git diff 30b739b..699a7cb -- plugins/CoreRpg/mail.yml`：**仅改 `event_box` 的 `body` 一行：
   - `- body: "活动奖励。"` → `+ body: "活动箱邮件仍在维护；奖励按活动发放。"`
   - `title: "活动箱"` 与 `attachments`（`consumable_ember_stamina_30: 1`、`gem_ember_sharp: 1`）零改。
3. **numstat：**`1	1	plugins/CoreRpg/mail.yml`（相对 `30b739b..699a7cb`）。
4. **其它模板逐块对照 `30b739b`：**`maintenance_comp` / `vip_daily_gift` / `season_pass_stub` / `pass_track_free` / `pass_track_paid_welcome` 均为 SAME。
5. **键名核对：**`rg -n 'event_box:'` → L36；键名未改。
6. **`git diff 699a7cb HEAD -- plugins/CoreRpg/mail.yml`：**空（验收时 HEAD 即施工 tip）。
7. **做法纪律：**仅静态 `rg` / `diff` / `git show 699a7cb`；未长测、未挑刺、未起服；本岗未改配置 / YAML；未宣称 B0.1 已清或票已废；精英壳未硬开。

## 纪律确认

- 勿宣称 B0.1 已清 / 票已废 / 活动系统完整落地：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
- 精英壳勿硬开：本岗未触。

## 回报摘要

- 总评：PASS
- 各点：1 PASS / 2 PASS / 3 PASS / 4 PASS
- 施工 tip SHA：`699a7cb`
- 测报 tip short SHA：本 commit（`git log -1 --format=%h`）
- 是否已 push：否
- 报告路径：`docs/STATUS-ember-mail-event-box-body-copy-test.md`
- 阻塞点：无
- ahead：相对 origin/main ahead 1（未 push）
- title/body/attachments 原文：见上
- 违禁词 rg：无命中
- 旁证：见上
