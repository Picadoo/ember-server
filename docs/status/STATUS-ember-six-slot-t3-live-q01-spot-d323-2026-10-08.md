# 余烬 · 六槽 T3 · D323 线上 Q01 结算掉甲抽查（余烬-测试 · 2026-10-08）

- 上游：总控放行 S2→按全员观察（S3 口径）；用户确认测试服、迁移无需犹豫；开关保持 `enabled+migrate` 开；不必等白名单
- tip / jar：线上 `CoreRpg.jar` sha256 `abc96aca51517216afa25d8ee14649acc55aed5d6b569826dba2308240139661`（与测服同）
- plugin.yml：`1.65.98-d322.local` · bv(runs)=**61** · `gear.six_slot.enabled=true` · `migrate=true`
- 环境：线上 play PID **107524** · 代理 25565 → login 25566 → play 25567 · MariaDB 3306 · storage=mysql ping 1ms · DB GUARD 未拦截
- 测号：`D323Q01`（新建；uuid `417bbfc2-2d42-34d0-ab18-0790c6bb1297`）· 经 `joinPlay()`→25565 · **未动真人档**
- 脚本：`/workspace/tmp/d322/D12-q01-clear-script.md` + `/workspace/tmp/d323-q01-spot/d323-q01-spot.js`
- 证据：`/workspace/tmp/d323-q01-spot/`（打包 `/workspace/tmp/d323-q01-spot.tgz`）
- 执行：2026-10-08 17:01–17:03 CST
- **结论：D12-① PASS（进本→三房清→boss spawned→玩家最后一击→settle rows+16 first clear；聊天结算/到账；DB `source=drop` 护甲 ≥1）。OPS §9 无未处理严重告警。DB 健康。开关未动。**
- **建议：可按 S3 全员观察继续；本窗无阻塞。不建议关 migrate。**

## 1 预检

| 项 | 结果 |
|---|---|
| jar sha | ✅ `abc96aca…`（通关前后一致） |
| bv | ✅ `balance_version: 61` |
| 两开关 | ✅ `enabled=true` `migrate=true`（yml；通关后仍 true） |
| storage | ✅ mysql · ping 1ms · DB GUARD 未拦截 |
| play PID | ✅ **107524**（全程未换） |
| 在线 | 0→1（仅测号）→0 |
| 内存 | available ≈ 6.3 Gi |

## 2 用例结果

| 编号 | 验收 | 结果 | 关键证据 |
|---|---|---|---|
| **D12-①** | `/corerpg enter q01` → 三房清完 → boss spawned → boss killed（玩家最后一击）→ settle | **PASS** | runId `q01-muzb5gki-ljt` · world `dungeon_EmberQ01_F771375D` · r1/r2/r3 均 `active=-` 门开 · `boss spawned hp=200` · `boss killed after 6.8 s` · `settle … rows+16 (first clear)` · Kick=0 · 无 `/dp leave` · 无「首领非玩家击杀」。`q01-clear.json` · `12-boss-settle.txt` |
| **聊天** | 「已击败·结算完成」+「结算到账」含护甲 | **PASS** | 到账含 T1 炽愈护腿（护甲已放进背包…）及首通相关件。`q01-chat.txt` / `run.log` |
| **DB drop** | `source=drop` 护甲 ≥1（或 settle rows 增加） | **PASS** | 测号 active：`drop` legs `ember_v1_sustain_legs_t1` + charm；全局 `drop active` 25→**27**。`11-db-drop.txt` · `11-db-drop-armor.txt` |
| **D12-②** | 掉落甲分解 ×0.1（可选） | **未跑** | 本窗以 ① 为核心；drop 甲已入账确认 |
| **OPS §9** | 近窗无未处理 `[P1 six]` 严重告警 | **PASS** | 仅测号进服瞬态 `DB_PENDING`→同秒 `ROLLED_FORWARD`（预期）；**无** SAVE_FAILED / 模板缺失 / 迁移中断卡住 / 长时间 DB_PENDING。`12-ops9-alerts.txt` |
| **DB 健康** | storage ping OK；计数合理无爆炸 | **PASS** | ping 1ms；全局 active：admin13 / drop27 / migrate8（+4 测号起步迁） / quest56；无异常膨胀。`11-db-after.txt` · `13-storage.txt` |

### 出口对照

| # | 标准 | 判定 |
|---|---|---|
| 1 | 三房 + 首领玩家击杀 + settle | ✅ |
| 2 | 聊天结算到账含护甲文案 | ✅ |
| 3 | DB `source=drop` 护甲 ≥1 | ✅ legs×1（另 charm×1） |
| 4 | OPS §9 无未处理严重项 | ✅ |
| 5 | DB ping OK；开关未关 | ✅ |

## 3 约束与安全

- **未**改 jar / bv / 菜单 / NI / proxy / login / MariaDB 配置；**未**部署；**未**关 `enabled`/`migrate`
- **未** `git stash` / `reset --hard` / force-push
- 特权经 console FIFO（weaken/heal/tp/stamina/give）；**未**给测号长期 op
- 测号资产：仅 `D323Q01`；真人号未碰
- 通关硬规则：零 Kick / 零 `/dp leave`（全程遵守）

## 4 告警 / DB 摘要

| 类 | 内容 |
|---|---|
| `[P1 six]` 近窗 | `D323Q01 migration DB_PENDING … 4 of 4` → `ROLLED_FORWARD`（进服起步甲迁移，瞬态，已收敛） |
| SAVE_FAILED / 模板缺失 / 中断卡住 | **无** |
| storage | mysql · ping 1ms · GUARD 未拦截 |
| cr_p1_item 全局 active | drop 25→27；migrate 4→8；quest 50→56；admin 13 |

## 5 建议

**建议按 S3 全员观察继续；本窗无阻塞结案项。不要关 migrate。**

依据：线上 jar/bv/两开关与测服一致；D12-① 结算掉甲路径在线上实测 PASS；OPS §9 仅见预期瞬态 DB_PENDING 并立即 ROLLED_FORWARD；DB 健康。

观察期注意：
1. 继续盯 OPS §9（尤其长时间 DB_PENDING / SAVE_FAILED / 模板缺失）；
2. 定时 `/corerpg storage` + 粗查 `cr_p1_item` source 计数；
3. 真人申告资产问题时优先 `armor status` / `audit`，勿先关开关；
4. DBDOWN 持续才按 OPS §3.2 `migrate=false`（本窗无此迹象）。

## 6 清场

- 测号已下线；`list` = 0
- 实例随结算关闭；未手动 `/dp leave`

## 总控签字（D324）

| 项 | 签认 |
|---|---|
| 线上 Q01 结算掉甲抽查 | **[x] PASS**（本报告 · `b2cfc017`） |
| 进入 | **S3 全员观察期**；开关 `enabled` / `migrate` **保持开**；不要关 migrate |
| 残余 | D12-② 掉落甲分解可后续补（不挡观察期） |
| 观察 | 继续盯 OPS §9；定时 `/corerpg storage`；真人申告优先 `armor status` / `audit`；DBDOWN 持续才按 OPS §3.2 关 migrate |

**签字：总控 · 2026-10-08 · D324**
