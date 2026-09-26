# STATUS · Ember 天梯 PAPI smoke — ready / waiting

**日期：** 2026-09-13 11:52（Asia/Shanghai）  
**规格：** `DESIGN-ember-guild-ladder.md` · `docs/ember-holograms.md` §5  
**约束：** 未改 Paper；未覆盖任何 jar；未对本机跑 server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **129854** bytes |
| mtime (UTC) | 2026-09-13 03:50:07 → **11:50 CST** |
| `plugin.yml` version | **1.3.3** |
| `description` | 币/签到/活跃/悬赏/强化/镶嵌/分解/重铸/灾厄/誓约/天赋/晶钻商城/月卡/邮寄/好友/设置 + 记分板/刷怪 |
| ladder 字样 | **无**（description / commands / jar 内类名均无 ladder） |
| `CoreRpgExpansion` | 仅 `corerpg_*`（coin/signed/activity/…）；**无** `ember_ladder_*` / `power_score` |
| `STATUS-ember-guild-ladder.md` | 仍标 PAPI `%ember_ladder_*%` **未接线** |

**结论：** ladder 扩展 **尚未部署** → **不跑** server smoke。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/ladder-papi-smoke.js` | RpgBot · `/papi parse me` 抽检 ranks **1–3** power/abyss/speed **name+value** + `%ember_power_score%`；另 sanity `%corerpg_coin%`；打印每条 result |
| `docs/ember-holograms.md` | 占位符名已正确（未改） |
| `plugins/HolographicDisplays/README-ember-holograms.md` | 补充完整 `%ember_ladder_*%` 对照 + smoke 脚本指针 |

### 脚本覆盖占位符（与 docs §5 对齐）

```
%ember_ladder_power_{1,2,3}_name% / %ember_ladder_power_{1,2,3}_value%
%ember_ladder_abyss_{1,2,3}_name% / %ember_ladder_abyss_{1,2,3}_value%
%ember_ladder_speed_{1,2,3}_name% / %ember_ladder_speed_{1,2,3}_value%
%ember_power_score%
```

---

## 等待插件岗

1. CoreRpg 接线 ladder + `power_score` 计算  
2. 注册 PAPI（`%ember_ladder_*%` / `%ember_power_score%`；可选 `%ember_abyss_best%` / `%ember_weekly_best_sec%`）  
3. 新 jar 的 `plugin.yml` **description**（或等价 STATUS）出现 **ladder** 字样后再跑 smoke  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；服务端已加载新 CoreRpg
node ladder-papi-smoke.js
```

期望：各 `%ember_ladder_*_%` / `%ember_power_score%` **非**字面量（可为空榜 `---` / `0`，但不应原样回显占位符）。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · HD `database.yml` 手改 · 未对本机执行 ladder PAPI smoke
