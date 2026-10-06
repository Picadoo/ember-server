# RELEASE · CoreRpg 1.65.68（D243 / ARCH S4-2）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.68** · deliverable **D243** · `balance_version` **58**（数量全不变） |
| 玩家可见 | 只有文案：团本 r01 / r02 说明「与 r02 / r03 合计」「与 r01 / r03 合计」（原来漏了 r03）。图录阶段币、宝箱额外件、起步包、誓约徽记发的东西和数量都不变 |
| 登记 | `EmberEconomy` S01–S35：**S33** 图录阶段奖励（200 / 400 / 600 / 1000，币走带标签 `grantCoin`）、**S34** 宝箱额外装备（5 %，从 S01 拆出）、**S35** 起步包（2 件 T0 + 5 瓶药）；S09 新键 `per_rule` 1；`ember-v1-economy.yml` 加块 |
| 修复 | G4：OP `givedup` 生成 `source=admin`（不可分解、不进图录；洗练仍可当重复件，`duplicateOk` 接受 drop / admin）。G5：打包的 5 个 `ember-v1*.yml` = 线上（`bundledConfigsMatchLive` 守住）。G6 / G7 / G11 注释 |
| p1sim | `sourcemap.py` 读 `docs/design/ember-source-map.yml`，计入 S33 图录币 + S35 起步药；21 格 A/B 全部 ±2pp 内（最大 0.6 pp），首通天数不变；W30 5.42 → 5.50 周；不调参 |
| 单测 | **533/0**（JDK8） |
| 冒烟 | **47/0/0**（FreshQ831 / FreshQ832）：起步包、Q01 回归、图录领取 +200 整无拒绝、givedup 分解被拒 + 洗练照常、8 次背包 ↔ DB 一致、MySQL×2、SEVERE 0 |
| 部署 | jar `b885c11f88c14fd7…` · play PID 2389433 · 08:37 Asia/Shanghai |
| 回滚 | `/workspace/backup/CoreRpg-1.65.67-pre-1.65.68.jar` |
| 余部 | G10（REG §5 物品级缺口）；下一刀 p1sim 读词缀原语导出表；6 槽 Stage 1 待服主 |
