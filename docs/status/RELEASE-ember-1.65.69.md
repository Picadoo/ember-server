# RELEASE · CoreRpg 1.65.69（D244 / ARCH S4-3）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.69** · deliverable **D244** · `balance_version` **58**（数量全不变） |
| 玩家可见 | **无**。词缀线上行为、钓鱼 / 扭蛋 / 生活玩法都不变 |
| 词缀导出 | `EmberAffixExportTest` 从 Java 词缀原语 × 线上 `variety` 生成入库表 `tools/p1sim/affix-table.json`；表和代码 / yml 漂移 → 单测失败；p1sim `affix_mob` / `AFFIX_STRESS` / `affixpack5` 改读表（不再手抄）。sim 节奏变化：blazing 周期 3.0 → 4.0、venom 6.0 → 7.3、jailer 7.0 → 8.2、arcane 11.0 → 12.5 s，首击晚 0–1.3 s；伤害不变 |
| 登记（G10） | `EmberEconomy` **S36** 钓鱼产出（LIFE_ITEM）、**S37** 扭蛋券发放（GACHA_TICKET）、**S38** 扭蛋抽取产出（COSMETIC，OUT）、**C19** 扭蛋抽取；C17 加 LIFE_ITEM。只打标签，无金样 / 路由。`ember-source-map.yml` 新 `stocks:` 块（徽记 / 余烬徽 / 生活件 / 扭蛋券 / 外观）+ `stocksMatchEconomyAndItemConfigs` |
| Gate（不调参） | 21 格前沿率 60 人 / 800 人逐格相同；Q07 首通中位 60 人 dodge 0.3 24 → 23（800 人 26 = 26）；`--ref` 逐位相同；affixpack5 MAX_ABS_DPP 1.1 → 0.8、压力 0.4 → 0.6（帽 3.0）；selfcheck 同 2 个已知 FAIL；W30 5.50 → 5.31 周。**无翻转** |
| 单测 | **539/0/0**（JDK8） |
| 冒烟 | **35/0/0**（FreshQ833 / FreshQ834）：boot + 表 = 线上 variety、起步包、Q01 回归、强制 venom / arcane 晋升 / 施放 / 击败 / 结算（实测首击 ≈ 9 s / 14 s = 导出表）、CoreGacha 进服、SEVERE 0 |
| 部署 | jar `5ddbe7526b6c90b1…` · play PID 2426288 · 09:27 Asia/Shanghai |
| 回滚 | `/workspace/backup/CoreRpg-1.65.68-pre-1.65.69.jar` |
| 余部 | 未建模的 5 个词缀（regen / charge / frost / mortar / molten）是否进 sim（会更难，需另跑 gate）；6 槽 Stage 1 待服主 |
