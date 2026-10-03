# STATUS · Ember 盟约 guild smoke — ready / waiting for jar

**日期：** 2026-09-13 12:05（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-guild-ladder.md` §1  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **160594** bytes · mtime **12:01 CST**（2026-09-13 04:01 UTC） |
| `plugin.yml` version | **1.3.5** |
| description | 含天梯/使魔；**无**盟约 / guild / alliance |
| usage | spawn…ladder\|pet… · **无** `guild` / `alliance` |
| `GuildService.class` / `guild.yml` in jar | **无** |
| 源码 | 盟约逻辑 **未**见 `GuildService`（仅 Ladder 引用 DESIGN） |

**结论：** guild / alliance 模块 **尚未打进 jar** → **不跑** `guild-smoke.js`，等插件岗出含盟约接线的包后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/guild-smoke.js` | RpgBot · `/corerpg guild\|alliance` → create / info / donate / invite / leave / disband |
| `plugins/TrMenu/menus/ember_guild.yml` | 盟约菜单壳（既有；tell + 建议命令） |
| `docs/design/DESIGN-ember-guild-ladder.md` | 创建 / 捐献 / Boss 命令占位；展示名盟约、ID 前缀 `guild` |

### 脚本期望命令面

```
/corerpg guild
/corerpg guild create <名>          # 2～8 字；示意名「余烬测」
/corerpg guild info
/corerpg guild donate <item_id> <amt>   # mat_ember_shard 10 · mat_ember_bone_dust 5
/corerpg guild invite <玩家>
/corerpg guild leave
/corerpg guild disband

/corerpg alliance …                 # 同上子命令（别名；以落地 jar 为准）
```

DESIGN 另有 `/corerpg guild boss`（周 Boss）— 本期 smoke **未**打，等 DP 接线后再补。

菜单壳建议：`donate mat_ember_shard 10`；创建费币 5000 / 晶钻 40（YAML 旋钮）。

---

## 等待插件岗

1. 将盟约/`GuildService`（或等价）+ `guild.yml` 编入 `CoreRpg.jar`（接线 `/corerpg guild|alliance …`）  
2. `plugin.yml` usage / description 出现 **guild**（或 STATUS 标明已接线）  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（或可靠重载）后跑 smoke  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg.jar 已含 guild/alliance 命令面
node guild-smoke.js
```

期望：`[盟约]` 创建/查询/捐献/邀请/离开/解散 tell；未知子命令不崩；`alliance` 与 `guild` 行为一致或明确别名提示。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 guild smoke · 未改 TrMenu 盟约壳
