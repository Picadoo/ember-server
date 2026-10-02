# 余烬 TrMenu 草案

对应设计：`/workspace/minecraft/DESIGN-dungeon-daily-weekly.md`

| 文件 | 命令 / 打开方式 | 说明 |
|------|-----------------|------|
| `ember_hub.yml` | `/ember` `/menu` | 主入口 |
| `ember_daily.yml` | 主菜单「日常」 | 日副本子页 |
| `ember_weekly.yml` | 主菜单「周常」 | 周副本子页 |
| `ember_abyss.yml` | 主菜单「深渊」 | `/dp start EmberAbyss` |
| `ember_calamity.yml` | 主菜单「灾厄」 | `/dp start EmberCalamity` + TP |
| `ember_shop.yml` | 主菜单「商城」 | 标价 tell |
| `ember_covenant.yml` | 主菜单「誓约」 | 三誓约选定壳 |
| `ember_talent.yml` | 主菜单「天赋」 | 天赋查看/洗点壳 |
| `ember_enhance.yml` | 主菜单「强化」 | 强化命令壳 |
| `ember_socket.yml` | 主菜单「镶嵌」 | 镶嵌命令壳 |
| `ember_disassemble.yml` | 主菜单「分解」 | 分解/重铸命令壳 |
| `ember_guild.yml` | 旧系统，D102 起不在主菜单（管理员 trmenu open） | 创建/捐献/周 Boss 壳 |
| `ember_ladder.yml` | 主菜单「天梯」 | 战力/深渊/竞速壳 |
| `ember_mail.yml` | 主菜单「邮寄」 | 收件箱/领取/系统发奖壳 |
| `ember_friends.yml` | 主菜单「好友」 | 列表/申请/组队/师徒壳 |
| `ember_settings.yml` | 主菜单「设置」 | 音效/提示/隐私壳 |
| `ember_arena.yml` | 主菜单「竞技」 | 1v1/2v2 排队 tell 壳 |
| `ember_auction.yml` | 主菜单「寄售」 | list/sell · 税 10% 壳 |
| `ember_character.yml` | 主菜单「角色」 | status / covenant / cash / monthly 壳 |
| `ember_set.yml` | 主菜单「套装」 | 余烬同袍（团戒+刃）2 件套 |
| `ember_storage.yml` | 主菜单「仓库」 | 末影箱 / 扩展页 / 勋阶链 |

## 占位 / 待接线备忘

1. **挂机传送**：当前为 `tp %player_name% -40 65 270`（RpgBot 测试点）；后续可评估改为 `warp afk` 或真实大厅坐标。
2. **进本命令**：当前记录为 `dp start EmberDaily` / `dp start EmberWeekly`（官方命令）；入口接线时再核对实际用法。
3. **次数占位符**：菜单文案目前写死 3/1；PAPI `%ember_daily_left%` / `%ember_weekly_left%` 尚未接入，接线后再改 lore。
4. **音效**：当前使用 1.12 风格 `BLOCK_NOTE_PLING`；若 TrMenu 映射失败，再换成插件支持名。
5. **材质**：`stained glass pane` / `watch` / `ender chest` 等若在 1.12 + TrMenu 3.12 解析异常，再改成 `glass` / `clock` / `chest`。

DungeonPlus 配置等 jar 到位后再落，不在本目录。
