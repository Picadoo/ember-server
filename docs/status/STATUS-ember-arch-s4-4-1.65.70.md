# STATUS · ARCH S4-4（CoreRpg 1.65.70 / D245）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.69 / D244（76ed3e5）/ bv58 · **总控裁决：** 先做 Stage 1 前置，Stage 1 代码仍关闭

系统图 S4 第 4 步「物品身份加 `source_map`」：新掉的余烬装备在物品上记**来源**（图 / 模式、来源行 S##、run id、时间），与 D208 物品键并列。**行为中性**：属性、lore、分解 / 图录 / 洗练判定、数量全不变（`balance_version` 仍 58）。旧件（没有来源）照常有效。

## 1 · 来源键

| 层 | 内容 |
|---|---|
| NBT（`ember_v1` 里） | `om` 图 / 模式 id（小写 `[a-z0-9_]` ≤ 24）· `os` 来源行（`[SX]\d\d`，非法 → X00）· `or` run id / 请求 id（`[A-Za-z0-9_@:.-]` ≤ 48）· `ot` unix 秒（int）。只在有来源时写 |
| DB | `cr_p1_item.origin VARCHAR(128) NOT NULL DEFAULT ''`，打包串 `map\|src\|run\|at`；启动时 `addColumnIfMissing`。三个 INSERT 写入；`doTxn` UPDATE 用 `origin=IF(?<>'',?,origin)`、upsert 用 `IF(VALUES(origin)<>'',VALUES(origin),origin)` → 非空来源**永不被覆盖成空**。`lookupFull` / `loadByState`（装备库取出）/ `recentDismantles`（撤销分解）都读回来源 |
| 签名 | v2 规范串只在有来源时追加 `\|o:<packed>` → 旧件规范串 / HMAC **逐字节不变**（`EmberProvenanceTest` 钉住旧形状）；来源被改 → 验签失败 |
| 复制 | `withRev` / `withItemKeys`（→ `withAffix` / `withSig`）/ `EmberUpgradeRules.copy` 都带上来源；新 `withOrigin` |
| 显示 | 只有 OP（`corerpg.admin`）在 `/corerpg p1 inspect` 看到「§8来源记录：map · src · run · at」或「无（1.65.70 之前的件）」。**没加灰色 lore 行**（可选项，不确定安全就跳过） |

## 2 · 映射（`EmberProvenance`，纯函数）

| 来源 | `om` | `os` | `or` | `ot` |
|---|---|---|---|---|
| 结算随机件 `base_item`（普通 / 重复 / 挑战 `q03c`） | run 的图段 | S01 | 账本 run id | 账本行 `created`（确定性：重投递同一行 → 同一份签名数据） |
| 深渊层 `q05a2-…` | `q05a2` | S13 | 〃 | 〃 |
| 宝箱额外件 `extra_chest_item` | 图 | S34 | 〃 | 〃 |
| 团本件 `raid_item` | `r01` … | S12 | 〃 | 〃 |
| 首通自选 `fc_<map>_*_item` | 图 | S06 | 〃 | 〃 |
| 起步包 `starter_*` | `starter` | S35 | `starter` | 〃 |
| 8 印记兑换 | `forge` | S28 | 兑换请求 id `redeem:…` | 当前时间 |
| OP `give` / `givedup` | `admin` | X03 | `give` / `givedup` | 当前时间 |
| 未登记 | run 图段 | X00 | — | — |

`EmberSourceMapTest`（+1 `itemProvenanceMatchesCode`）：`ember-source-map.yml` 新 `item_provenance:` 块（NBT 键 = `toMap` 实际多出的键、列定义 = 建表串、打包顺序、每条规则的示例 → `forReward` 的 map / src、src 必须是 sources 条目、代码引用可解析）。`EmberProvenanceTest`（7）：旧规范串形状、NBT 往返 + 篡改、复制保留（含 `EmberUpgradeRules.copy` / `EmberItemKeys.fold`）、DB 打包往返、校验、奖励映射、p1 源码里每个 `item(in, "…")` 键都解析到非 X00 行。

## 3 · 测试 / 部署 / 冒烟

- **单测（JDK8）：** 全量 **547 / 0 / 0**（539 + `EmberProvenanceTest` 7 + `itemProvenanceMatchesCode` 1）。
- **部署：** jar `f1e65f0f61ad9f6f…`（= 当前源码树构建，`target/CoreRpg.jar` 同 sha，源码无更新）· 10:07:51 首启 1.65.70 · DEPLOY LOCK 10:07 → RELEASED 10:08（PID 2458932）。冒烟里另做两次优雅重启往返（10:17、10:45）。现 play PID **2487583**。每次都有：`Enabling CoreRpg v1.65.70`、CoreRpg MySQL、CoreGacha MySQL、`amount() SoT (bv58)`、`cr_p1_item … ready`、无 FAIL-CLOSED、SEVERE 0。列 `origin varchar(128) NOT NULL ''` 已建；1.65.70 之前的 1520 行全部 `''`。回滚：`/workspace/backup/CoreRpg-1.65.69-pre-1.65.70.jar`（旧 jar 不读、不写这列，`''` 默认值无害）。
- **冒烟：** `tools/p1map/d245-arch-s4-4-smoke.sh`，详见 `docs/tests/smoke-2026-10-06-d245-arch-s4-4.md`。每条来源检查都至少有一次干净 PASS（起步包、Q01 首通 + 重复 base_item = 账本行、装备库存入 → 取出、强化、8 印记兑换 → 分解 → 撤销 → 取出、OP give、老号、重启往返、背包 ↔ DB 一致 × 每步、无发放拒绝、SEVERE 0）。几次 FAIL 都是脚本问题（见冒烟文档），没有一条是插件问题。

## 4 · 不变

`balance_version` **58**；所有发放 / 消耗数量；lore；分解 / 图录 / 洗练重复判定（只看 `source`，不看来源）；旧件签名与有效性。仓库（EmberVault）只放材料，没有物品身份 → 装备的「仓库往返」= 装备库（stored 状态）。

## 5 · Stage 1 前置 / 部分 B

- 迁移风险降低（服主 10-02：线上没有老玩家）已写进 `DESIGN-ember-gear-staged-plan-2026-10-04.md` §2 / §5：最小迁移，保留测试 / 管理号的安全路径。
- D246（离线 6 槽掉落模型重跑 + 刷图 W30 诊断）**未完成**，10:42 例行检查要求本次只交 A。部分结果（未入库）：`tools/p1sim/out-d246-partial.md` + `/workspace/d245/`。**Stage 1 仍不可开。**
