# STATUS · ARCH S3-11（CoreRpg 1.65.66 / D240）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.65 / D239 / bv58（813b467）

## 为什么做 Papi 分节

ARCH §5 S3 余部：`EmberRunService` 末尾 `placeholder()` 是约 150 行、40 多个分支的 if 链（`%corerpg_p1_*%`），`CoreRpgExpansion` 另有 185 行平铺 if 链（`%corerpg_*%`）。两处都不薄，所以本刀做 Papi，不改做词缀原语。

## 做了什么

1. `p1/EmberRunPapi`：`Section` 枚举 + Bukkit-free `route(key)`（**逐条照抄旧链的首匹配顺序**）+ 每节一个方法（ENTRY / RUSH / GROWTH / GATE / ROTATION / BOARD / SEASON / COSMETIC / FESTIVAL / ABYSS / RAID / LOADOUT / CODEX / MAP）。旧链「落空继续往下」的地方（`rush_<非连战入口>`、无 season / cosmetics / festival 或其返回 null）节内返回 null → 交给 MAP 尾（`<map>_<field>`），与旧行为一致。
2. 文案拼接抽成静态助手并钉单测：`rushLine` / `rushEntryLine` / `needFirstClear` / `topRowText` / `topRank` / `abyssStateLine` / `statsLine` / `awakenRouteLine`。
3. `EmberRunService.placeholder` → 一行委托；新增只读包访问器 `passOf(UUID)` / `top()` / `papi()`。`EmberRunService` 2833 → 2694 行。
4. `CoreRpgExpansion` → 薄分发：`CorePapi`（Section + 键集合 + route）+ `CorePapiAccount`（账户 / 任务 / 邮件 / 公会 / 晶币）/ `CorePapiKit`（灵活技 + 技能套）/ `CorePapiProgress`（等级 / 天赋 / VIP / `gate_`）/ `CorePapiStamina`（S0 体力）。方法体逐字搬迁；未知键仍返回 null。
5. `EmberLadderExpansion`（`%ember_*%`，83 行）本就独立，未动。
6. 单测：`EmberRunPapiTest` ×5（全族路由、首匹配顺序、**live TrMenu / DP / 资源 yml 里每个 `%corerpg_p1_*%` 都落到具名节或合法 map 字段**、文案、薄委托）+ `CorePapiTest` ×3（旧链 60 个键一个不多一个不少且分节不相交、前缀、live 配置全键可路由）；`EmberCountersTest` 非计数器白名单 `CoreRpgExpansion.java:p1_` → `CorePapi.java:p1_`。**496/0**（JDK8 class 52，+8）。
7. 冒烟工具：`tools/p1map/papi-snapshot.sh`（一次解析 live 配置全部 756 个 corerpg/ember 键 + 边界键，输出 TSV）+ `tools/p1map/d240-arch-s3-papi-smoke.sh`。

## 不变

- 所有占位符键与值；`balance_version` **58**；所有发放 / 消耗；技能；C15；echotune；p1sim；R2 swap；内容包。

## 冒烟

见 `docs/tests/smoke-2026-10-06-d240-arch-s3-papi.md`：**PASS 17 / FAIL 0 / SOFT 0**；FreshQ824 部署前（1.65.65）↔ 部署后（1.65.66）751 键逐字相同；FreshQ827 第 2 会话 ↔ 第 3 会话 751 键逐字相同；MySQL×2；SEVERE 0；jar `76da71bd44243762…`；play PID 2304067。

## 发现（未改，非本刀）

- `%ember_daily_left%` / `%ember_weekly_left%` 在 live 配置里被引用，但 `EmberLadderExpansion` 没有这两个键（部署前后都原样显示字面量）。属既有缺口，未在行为保持刀里补。
- 新注册的 bot 首个会话里 `papi parse <name>` 找不到玩家（PAPI 按缓存名查，退出一次后才行）；冒烟因此以第 2 / 3 会话比对。

## 下一刀

- **词缀行为原语**（ARCH §5 S3 最后一项：每个词缀一个原语类 + 单测；p1sim 按同一原语建模；伤害轨迹回放比对）。
- 然后 **S4**：装备结构合并文档 + `source_map`。
