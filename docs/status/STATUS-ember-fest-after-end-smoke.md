# STATUS · 国庆活动结束后路径冒烟（D156，2026-10-04 09:17 CST）

- **服务器**：CoreRpg **1.63.2** + CoreGacha **1.0.0**（未改 jar、未重启）
- **测试号**：**FreshQ47**（UUID `cfa5bb84-5764-3b4c-8dbd-906588707bbb`），测完已下线
- **方法**：只改 live `plugins/CoreRpg/ember-v1-festival.yml` 的 `end` 为 `2026-10-04T00:00:00+08:00` → `/corerpg p1 fest reload`（日志 `now ended`）→ 冒烟 → **立刻改回** `2026-10-08T00:00:00+08:00` → reload（日志 `now open`）。resources 副本与 git 未动；备份 `/workspace/backup/ember-v1-festival.yml.pre-d156-0913`

## 结果

| 项 | 步骤 | 期望 | 结果 |
|---|---|---|---|
| D145 主菜单入口 | `trmenu open ember_hub` 第 0 格 | 「国庆纪念 · 烟火符常驻商店」+ 常驻价文案，不是灰玻璃 | **PASS** |
| D145 装备页入口 | `ember_p1_gear` 第 16 格 | 「盛世烟火符（活动护符位）」+ 15000 币 / 300 徽 | **PASS** |
| 活动页状态 | `ember_p1_fest` | 状态「已结束」；烟火符价「15000 余烬币 或 300 余烬徽（活动结束后的常驻价）」；纪念称号 / 换徽格仍在 | **PASS** |
| 常驻价购符（币） | firstclear q01–q04 → `fest buy charm` | 「获得盛世烟火符…付了 余烬币 15000」；日志 `buy FreshQ47 fest_charm_gq26 for 余烬币 15000` | **PASS** |
| 结束后足迹（剩币） | `fest buy trail` | 「获得足迹红金烟火…付了 国庆币 30」 | **PASS** |
| 剩币换徽 | `fest buy badge all` | 「国庆币 10 → 余烬徽 +2（2/40）」 | **PASS** |
| 常驻价购符（徽）门闩 | reset 后徽不足 300 → `fest buy charm badge` | 「余烬徽不够（要 300，有 52）」——证明走了结束后徽分支 | **PASS**（拒绝即证） |
| 恢复窗口 | reload 后 hub 第 0 格 | 「国庆 · 烟火庙会（限时）」· 进行中 · 10-08 | **PASS** |

## 未测

- 徽付满 300 真买到符（门闩已证；结束后再改日期会打断线上活动窗，本窗不做）
- 活动本真打用时 / 三人活动本（handoff 原条）

## 结论

D145 / D146 的「活动结束后入口 + 常驻价 + 剩币兑换」在实机可走通。**不改数值、不升版本。** 下次测试号 **FreshQ48** / FreshG04。
