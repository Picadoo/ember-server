# STATUS · CoreRpg 1.62.0 · P1 材料仓 / 装备库 / DbGuard / 背包快照

日期：2026-10-04（Asia/Shanghai）
版本：CoreRpg **1.62.0**

## 上线内容

1. **材料仓库（EmberVault）** — P1 白名单材料（碎片 / 核心碎片 / 骨尘 / 胚料 / 国庆币）无格子上限、每种近 20 亿；自动入库；强化/升阶直接扣仓；`/corerpg p1 vault`
2. **装备库（EmberGearLib）** — MySQL `cr_p1_item state=stored` + `cr_p1_gearlib` 锁/收藏；背包快满时新掉落自动入库；筛选翻页、批量分解 10 分钟可撤销；`/corerpg p1 gearlib|stash|undo|itemlog`
3. **DbGuard** — `storage: mysql` + P1 + `storage_guard.enabled` 且启动连库失败 → 拒绝进服（不再静默 YAML 开玩家档）；`/corerpg storage guard [status|release]`
4. **InvSnap** — 登录/登出/死亡/进副本世界/每 10 分钟/恢复前 快照背包+盔甲+副手+末影箱；MySQL `cr_inv_snapshot` 或 `plugins/CoreRpg/snapshots/<uuid>/`；保留最近 50 + 30 天每日最新；`/corerpg invsnap list|view|restore|diff|take`

## 配置

- `ember-v1.yml` → `storage.vault` / `storage.gearlib` / `storage.invsnap`
- `config.yml` → `storage_guard.enabled`、`invsnap.*`（与 ember-v1 对齐的默认）
- TrMenu：主菜单「仓库」→ `/corerpg p1 vault`；`ember_storage.yml` 去掉付费解锁文案

## 单测（纯规则，无 Bukkit）

- `InvSnapRulesTest` · `EmberStorageRulesTest` · `DbGuardTest`

## 注意

- 本批不碰 CoreGacha
- 运行时 `plugins/CoreRpg/config.yml` 密码保持服务器本机值；资源模板 / 工作区副本仍为占位
- ops.json 必须保持 `[]`
