# STATUS · B-flex-4 踏步热键 · 潜行+Q · 施工 PASS

- **时间：** 2026-09-29 04:39 Asia/Shanghai
- **批准：** `docs/status/STATUS-ember-flex-step-hotkey-approve.md` · `7b4a8b6` · 批 A 候选 3
- **设计：** `docs/design/design-ember-flex-step-hotkey.md` · `a9311ac` · ribbon `bcb1ab6`
- **CoreRpg：** **1.15.28**（1.15.27 +1 patch）
- **热更：** play 短重启 Enabling **v1.15.28** @ **04:37:59 CST** · Done @ **04:38:01 CST** · `trmenu reload` 38 菜单 @ **04:39:31 CST**

## 改动

| 项 | 内容 |
|----|------|
| `FlexSkillService` | `implements Listener`；`@EventHandler(HIGH, ignoreCancelled)` `PlayerDropItemEvent`：潜行 + `hasFlexSkill()` + 非 GUI → cancel + `cast`；未潜行/未装配不拦 |
| `CoreRpgPlugin` | `registerEvents(flexSkillService, this)`（与 statService 同级） |
| `skills.yml` 双路径 | 根增 `flex.hotkey: sneak_drop`；**未改** `flex_ember_step` CD14 / distance 5.0 |
| TrMenu | `ember_flex_skill` C/I + `ember_hub` 轻技 lore 半行「战斗中 §e潜行+Q §7可释放」 |
| 版本 | pom + plugin.yml → 1.15.28 |

## 验收（静态）

- rg：`PlayerDropItemEvent` / `isSneaking` / `cast(` / `implements Listener` ∈ FlexSkillService；`registerEvents(flexSkillService` ∈ CoreRpgPlugin
- `cooldown_seconds: 14` / `distance: 5.0` 未变；誓约三技 CD 8/10/12 未变
- Enabling v1.15.28 已确认；jar 未入库

## 禁项（EMPTY）

誓约三主动 / 位移+保命双上 / 四件甲 / 锻炉 / 体力门 / 长测挑刺 / B0.1 声称 / disassemble·精英壳 / players·ops-local·server-runtime 脏文件 · **ZERO**
