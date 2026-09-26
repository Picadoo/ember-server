# STATUS — CoreRpg 盟约周 Boss 轻量 (1.3.7)

**时间：** 2026-09-13 ~12:13 Asia/Shanghai  
**范围：** EmberGuildBoss DP + GuildService boss（贡献门控）

| 项 | 状态 |
|----|------|
| CoreRpg **1.3.7** jar | OK `plugins/CoreRpg.jar` **177591** bytes |
| Restart | OK `./start.sh custom` · log `CoreRpg 1.3.7 enabled` · Guilds loaded 0 |
| DP EmberGuildBoss | OK 导入/预缓存/初始化完毕 · map `ember_raid` |
| MM | 仅复用 EmberAbyss* + EmberCalamityBoss（无新 MM） |
| Paper | 未改 |

## 行为
- `/corerpg guild boss`：须在盟 · 周限 1（`DailyService.weekId` Asia/Shanghai）· 扣个人贡献 20 · `Bukkit.dispatchCommand(player, "dp start EmberGuildBoss")`
- PlayerData：`guildBossWeekId` / `guildBossUsed`
- DP：team 1～5 · 无 NI 票 · 2 波 + CalamityBoss · 通关碎片/外观/核心×1/低档孔石 · 超时 900s
- 击杀名：余烬深渊僵尸/骷髅/蛮兵/余烬灾厄使

## 明确留给下期
- **拍卖 / 竞技**（auction / arena）— 本期未做
