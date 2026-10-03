# STATUS · Ember 盟约 Guild Boss smoke — ready / waiting

**日期：** 2026-09-13 12:12（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-guild-ladder.md` §1.3 · `docs/status/STATUS-ember-guild-lightweight.md`  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **176275** bytes · **1.3.6** · mtime **12:08 CST**（2026-09-13 04:08 UTC） |
| `GuildService` / `guild.yml` in jar | **有**（轻量 create/donate；**boss stub**） |
| `plugins/CoreRpg/guild.yml` · `boss.enabled` | **false** · stub「暂未开放」 |
| DP `plugins/DungeonPlus/dungeon/EmberGuildBoss/` | **有**（option/monster/task · map `ember_raid`） |
| `guild boss` → `dp start EmberGuildBoss` | **未接线**（`cmdBoss` 仍 stub /「配置已开但副本尚未接线」） |

**结论：** DP 本壳已落，CoreRpg 门控仍 stub → **不跑** `guild-boss-smoke.js`，等 `boss.enabled` + 进本接线后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/guild-boss-smoke.js` | RpgBot · coin/cash give → 确保盟约（无则 create）→ NI 捐 shard 换贡献 → `/corerpg guild boss` → 试 `/dp start EmberGuildBoss` + leave |
| `plugins/DungeonPlus/dungeon/EmberGuildBoss/` | 轻量周 Boss（复用 Abyss/Calamity MM；无新 jar） |
| `docs/design/DESIGN-ember-guild-ladder.md` §1.3 | `/corerpg guild boss` → 日后 `dp start EmberGuildBoss` |

### 脚本期望命令面

```
/corerpg coin give RpgBot 10000
/corerpg cash give RpgBot 100
/corerpg guild | guild info
/corerpg guild create <名>          # 仅当「还没有盟约」
/ni give RpgBot mat_ember_shard 200
/corerpg guild donate mat_ember_shard 100   # ×2 尽量攒贡献（日 cap 50）
/corerpg guild boss
/dp start EmberGuildBoss            # 本存在则试；人数/组队/门控失败可接受并记 snip
/dp leave
```

期望接线后：`[盟约]` 非「暂未开放」；贡献不足有明确 tell；成功时进 `EmberGuildBoss`（或明确组队条件）。

---

## 等待插件岗

1. `guild.yml` · `boss.enabled: true` + `GuildService.cmdBoss` 扣贡献/票并 `dp start EmberGuildBoss`  
2. 周重置 / 进本人数与 DP `team-condition`（1～5）对齐  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（或可靠重载 CoreRpg + `/dp reload`）后跑 smoke  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg boss 已接线；DP EmberGuildBoss 已 reload
node guild-boss-smoke.js
```

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 guild-boss smoke · 未改 DP / guild.yml 运行时开关
