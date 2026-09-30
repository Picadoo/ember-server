# B2.76 · cash 文件头票物文案 · 纯静态薄验收

- **总评：PASS**
- **测报时间：**2026-09-30 21:12 Asia/Shanghai（CST）
- **验收岗：**余烬-测试岗执行器
- **工作区：**`/workspace/minecraft`（`git pull` 已执行，Already up to date）
- **验收基线 HEAD（施工 tip）：**`9fb576ff0bb0cf79548b32b1d21e9e9c3d879c18`（当时与 origin/main 一致）
- **施工 tip SHA：**`9fb576f`（全：`9fb576ff0bb0cf79548b32b1d21e9e9c3d879c18`）
- **测报 tip：**本文件本地 commit（未 push；`main` 相对 origin ahead 1；short SHA 以 `git log -1 --format=%h` 为准）
- **设计 tip：**`docs/design-ember-cash-header-ticket-copy.md`（设计 `b39a214` / 批 A `6e7456a`）
- **是否已 push：**否（本岗仅本地 commit 本测报；未执行 `git push`）
- **报告路径：**`docs/STATUS-ember-cash-header-ticket-copy-test.md`
- **阻塞点：**无

## 各点

| # | 条件 | 结果 |
|---|------|------|
| 1 | L1=`# CoreRpg cash — 晶钻 / 体力 / 遗留票物 / 月卡（DESIGN-ember-cash-monthly.md）` | **PASS** |
| 2 | L2 仍为 `# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload`（零改） | **PASS** |
| 3 | L3=`# 红线：不直售碎片/晶/核心；总进本次数硬顶仍为 6（免费+氪+月卡），按体力口径维护` | **PASS** |
| 4 | `enabled`/timezone/体力段/商城键值相对 `6e7456a` 零 diff | **PASS** |
| 5 | 禁项：无「票已废」「B0.1 已清」 | **PASS** |

## L1–L3 原文

```yaml
# CoreRpg cash — 晶钻 / 体力 / 遗留票物 / 月卡（DESIGN-ember-cash-monthly.md）
# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload
# 红线：不直售碎片/晶/核心；总进本次数硬顶仍为 6（免费+氪+月卡），按体力口径维护
```

（`plugins/CoreRpg/cash.yml` L1–L3；与 PASS 条件逐字一致。注：设计 tip §2 荐案块曾写过加长 L2，但 §2.1 / 批 A / 施工均锁定 L2 保持 live 短原文；施工 tip `9fb576f` 仅改 L1、L3，L2 零改，符合批准口径。）

## 违禁词 rg

```text
rg -n '票已废|B0\.1 已清' plugins/CoreRpg/cash.yml
→ (无命中；exit 1)

git show 9fb576f | rg -n '票已废|B0\.1 已清'
→ (无命中)
```

设计 tip 内仅有「禁写」纪律句提及「票已废」/「B0.1 已清」，属禁项说明，非宣称；目标 `cash.yml` 与施工 tip 无此类宣称。本测报亦不宣称 B0.1 已清、不宣称票已废。

## 旁证（静态）

- `git show 9fb576f --stat`：仅 `plugins/CoreRpg/cash.yml`（`2 +-` → 1 file, 2 insertions, 2 deletions）。
- `git show 9fb576f -- plugins/CoreRpg/cash.yml`：仅改文件头 L1、L3：
  - L1：`日票限购` → `体力 / 遗留票物`
  - L2：未动（仍为「配置可先落盘；落地后纳入 CoreRpg reload」）
  - L3：`日票硬顶是总进本 6（免费+氪+月卡）` → `总进本次数硬顶仍为 6（免费+氪+月卡），按体力口径维护`
- `git diff 6e7456a HEAD -- plugins/CoreRpg/cash.yml`：同上仅两行注释；无非注释键值行变更。
- L4 起 body sha256（`tail -n +4`）相对 `6e7456a`：**相同**（`6055560d…1697`）。
- 顶层键位对照 `6e7456a`：`enabled` L4 / `timezone` L6 / `stamina` L9 / `currency` L38 / `daily` L46 / `shop` L51 / `monthly` L72 — 行号与键名一致。
- 施工 tip `name-only` 仅 `plugins/CoreRpg/cash.yml`；未捆绑 `progress.yml` / `event_box` / 断塔 / 霜锈 / AFK。
- `git diff 9fb576f HEAD -- plugins/CoreRpg/cash.yml`：空（验收时 HEAD 即施工 tip）。
- Python 逐字比对 L1–L3 与 PASS 条件：三行均 PASS。
- 未起服、未长测、未挑刺；本岗未改配置 / YAML。

## 纪律确认

- 勿宣称 B0.1 已清：本测报未作此宣称。
- 勿宣称票已废：本测报未作此宣称。
- 勿 git push：本提交仅为本地测报 commit。
- 本岗不改配置：仅新增本 STATUS 测报文件。
