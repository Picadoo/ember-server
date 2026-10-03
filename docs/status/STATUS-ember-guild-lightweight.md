# STATUS — CoreRpg 盟约轻量 (1.3.6)

**时间：** 2026-09-13 Asia/Shanghai  
**范围：** docs/design/DESIGN-ember-guild-ladder.md §1 create+donate；boss stub；pet 短名别名

| 项 | 状态 |
|----|------|
| CoreRpg **1.3.6** jar | OK `plugins/CoreRpg.jar` **176275** bytes |
| Restart | OK `./start.sh custom` · log `CoreRpg 1.3.6 enabled …/guild` |
| Guilds loaded | 0（空目录 `plugins/CoreRpg/guilds/`） |
| Paper | 未改 |

## 命令
- `/corerpg guild` · 别名 **`alliance`** / `盟约` → 同一套子命令
- `create <名>` · `info [名]` · `invite` / `accept` / `leave` / `kick` · `donate <niId> <amt>` · `boss`→「暂未开放」
- 前缀 `[盟约]`
- pet：`ashling`→`pet_ember_ashling` · `cinder`→`pet_ember_cinder`（summon/unlock）

## 存储 / 配置
- `plugins/CoreRpg/guilds/<uuid>.yml` · PlayerData `guildId` / 日捐
- `guild.yml`：创建 5000币或40晶钻、日 cap 50、shard×10 / bone×5 → 1 贡献

## 未做
全盟仓库、Boss 本、盟等级升 cap（仅 stub 人数 20）
