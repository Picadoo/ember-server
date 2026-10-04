# 发布凭证 · CoreRpg 1.65.15（2026-10-04）

P1 挂机庭 rev 2：自动战斗（D177 rev 2）。研究 `docs/design/RESEARCH-ember-afk-2026-10-04.md` §5 · 设计 `docs/design/DESIGN-ember-afk-p1-2026-10-04.md`。

## 线上状态（核对于 2026-10-04 约 20:41 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.15**（日志 `Enabling CoreRpg v1.65.15`，20:41:13）|
| class major | **52**（JDK 8）|
| CoreRpg JAR sha256 | `cf4cc1b0c2698a6cd8c4453b9d180b0aaa485caed5476cca5e8bf635dc5e96b1`（2354 条目）|
| balance_version | **38** |
| 代码提交 | `1e685d3`（基于 mainline 1.65.14 `07b3ec3` + docs `7966b22`）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.14-pre-1.65.15.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 1020105（21:19，persist-roundtrip 最后一次重启，无 CORERPG_TEST_FAULTS；20:41 部署时 988172）|
| 启动日志 | `[P1 afk] on auto-combat world=ember_afk pack=3 respawn=4.0s swing=14t daily_kills=2400 offline=0.25 max 1200/12h tiers=4 legacy_payouts=false` |

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 259 / 0（`EmberAfkServiceTest` 5 项含绑定材料 / BMAT / 容器判定）|
| p1sim 21 格 | **21/21**：1600 人复测（seed0 50000）最大 1.7pp · 800 人最大 1.2pp（规则 = bv37 + 本版）|
| W30 | 4.86 → 5.00 周（+0.14，±0.5 内；碎片 2/3/3/4 + 经验 30–60 的上界组，定稿只有经验更低）|
| 冒烟 FreshQ210 / FreshQ211（临时 daily_kills 20 / settle 5 / T1 怪 hp 40，测完恢复并 reload）| 进 T1 自动开打 ✔ · ActionBar / 速度 ✔ · 每 5 只结算：币 / 经验进账号，碎片 / 骨尘只进仓库（账本 `bmat:` 行，背包里没有实物）✔ · 20/20 封顶，合计正好 60 币 / 10 经验 / 2 碎片 / 2 骨尘 ✔ · 离线 2 分 → 按实测 7 只/分 × 25% 补 3 只 ✔ · 开关 on/off ✔ · 挂机庭内无旧刷怪 ✔ |
| 2 机器人转移（A → B）| P1 仓库取出绑定碎片：拒绝 ✔ · 旧 `/corerpg warehouse withdraw`：拒绝 ✔ · 混合（2 绑定 + 3 普通）只能取出 3 ✔ · A 把 3 个普通碎片丢给身边的 B：拦截，地上 0 物品 ✔ · A 丢装备：拦截 ✔ · A 往箱子放碎片（点击 + Shift）：拦截，箱子空 ✔ · B 背包 / 仓库只剩自己的 2 碎片 2 骨尘 ✔ · B 打 A 的怪（5 下）：0 伤害（被打那只与没被打的两只掉血相同，都是 A 的烬斩）✔ |
| 资产回归 | 仓库扣除 / 取出 / 旧取出 / 绑定入账都改了 → `persist-roundtrip.sh` ONLY=a,f,g,h（FreshQ240–248）：**a–f 48 / 0（FreshQ249–257）+ g,h 203 / 0（FreshQ240–248），无丢失、无复制** |
| config.yml | **未改** |
