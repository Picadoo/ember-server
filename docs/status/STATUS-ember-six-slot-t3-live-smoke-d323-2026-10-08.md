# 余烬 · 六槽 T3 · D323 线上管理号冒烟（余烬-测试 · 2026-10-08）

- 上游：总控 D323「线上⑦已完成，请立刻管理号冒烟」；部署签 `ee73480c`
- tip / jar：线上 `CoreRpg.jar` sha256 `abc96aca51517216afa25d8ee14649acc55aed5d6b569826dba2308240139661`（与测服同）
- plugin.yml：`1.65.98-d322.local` · bv(runs)=**61** · `gear.six_slot.enabled=true` · `migrate=true`
- 环境：线上 play PID **107524** · 代理 Waterfall **25565** → login 25566 → play 25567 · MariaDB 3306 · storage=mysql ping OK · DB GUARD 未拦截
- 备份：`/workspace/tmp/d323-backup-20261008164536/`（未动；本岗未回滚）
- 测号：`D323MigA`（新建短命测试号；**未动真人档**）· 经 `lib/proxy-login.js` → 25565
- 证据：`/workspace/tmp/d323-smoke/`（另打包 `/workspace/tmp/d323-smoke.tgz`）
- 执行：2026-10-08 16:49–16:55 CST
- **结论：必查 1–4 全 PASS；可选 Q01 本窗未跑（测服 D12 已 PASS）。不变量 ①② 无违例。开关未改。线上 jar/PID/菜单/NI/proxy/login/MariaDB 配置未改。**
- **建议：放行 S2 白名单（5–20 真人）。**

## 1 预检（复核总控 D323）

| 项 | 结果 |
|---|---|
| jar sha | ✅ `abc96aca…`（冒烟前后一致） |
| plugin.yml | ✅ `1.65.98-d322.local` |
| bv | ✅ `balance_version: 61` |
| 两开关 | ✅ `enabled=true` `migrate=true`（yml + `/corerpg p1 armor status` 末尾两键） |
| storage | ✅ `storage=mysql` · MySQL ping 1ms · DB GUARD 未拦截 |
| play PID | ✅ **107524**（全程未换） |
| 在线 | 冒烟时 0→1（仅测号）→0；无真人同服 |

## 2 用例结果

| 编号 | 总控必查 | 结果 | 关键证据 |
|---|---|---|---|
| **C1** | `/corerpg p1 armor status` 末尾 enabled/migrate 均为 true | **PASS** | `D323MigA p1_six_mig=0 … enabled=true migrate=true` → 迁后仍两键 true。`ev/02-armor-status.json` |
| **C2-M** | 有护符号进枢纽迁移；`p1_six_mig`；H/D 逐位；待领守恒 | **PASS** | AFK 停自动迁 → 发 T0×4 穿上 → `/hub` 自动迁移：聊天「生命和防御不变」；`p1_six_mig=1 journal=false 待领=4`；H/D **攻击12·生命40·防御2** 迁前=迁后；pieces×4；DB `source=migrate` active×4（头胸腿靴各 1）。`04/07/12-hd-fixed.json` · `11-db-migrate.txt` · `14-p1-six-record.yml` |
| **C2-A** | audit `ACTIVE_IN_STASH`；待领内勿 restore | **PASS** | 4× `ACTIVE_IN_STASH`（原 T0 甲进待领）；无 `ACTIVE_NOT_HELD` / `DUPLICATE`；`audit restore <uid前缀>` →「该 uid 在六槽待领里，勿补发」。`08-audit.json` · `08-audit-restore.json` |
| **C3** | 开关暂勿关全服；抽查护甲页可开、待领入口 | **PASS** | 未改开关；`/trmenu open ember_p1_armor` 开窗；`armor claim` →「已领取 4 件」；`armor worn head` 有穿着文案。`09-armor-page.json` |
| **C4 H8** | 经代理进服；断线重连；uuid 不变；无跨服双持 | **PASS** | 经 25565 进出；uuid `47f1aaca-90d6-3a63-8375-4f340cd55e48` 重连不变；`p1_six_mig=1`；audit findings 0 / 无 DUPLICATE；同服仅 1 会话。`10-h8.json` |
| **C5 Q01** | 可选短通关结算掉甲 | **未跑** | 本窗优先必查；测服 D12 ①②③ 已在 `dec1c625` / STATUS S1 §7 PASS。线上有 DungeonPlus，可另窗补 |

### 出口对照（设计 §1.3 / 总控必查）

| # | 标准 | 判定 |
|---|---|---|
| 1 | enabled/migrate 运行时 true | ✅ C1 |
| 2 | H/D 迁移前后逐位 | ✅ B=12 H=40 D=2 |
| 3 | `p1_six_mig=1` + done_at/pieces | ✅ record + DB migrate×4 |
| 4 | 待领数 = 迁移前甲位件数 | ✅ 待领=4（穿 4 件） |
| 5 | ACTIVE_IN_STASH；勿 restore | ✅ |
| 6 | 无未决 journal | ✅ |
| 7 | H8 代理路径 | ✅ |
| 8 | ①② 不变量；未关全服开关 | ✅；开关始终 true |

## 3 约束与安全

- **未**改 jar / bv / 菜单 / NI / proxy / login / MariaDB 配置；**未**部署；**未**关 `enabled`/`migrate`
- **未** `git stash` / `reset --hard` / force-push；pull 未执行（干净 docs 提交）
- **未**给测号长期 op（特权经 `console.sh` / fifo）
- 测号资产：仅 `D323MigA`；真人号未碰
- 内存：available ≈ 6.2–6.3 Gi（冒烟期间）

## 4 残余 / 观察

| 级 | 项 | 说明 |
|---|---|---|
| 观察 | 控制台 `p1 status <玩家>` | 非玩家 sender 只打模式/表，不打目标号 B/H/D；线上核对用玩家聊天「攻击·生命·防御」或管理号自己执行 |
| 观察 | 进服 120 tick 空甲迁移 | 空甲会直接 `p1_six_mig=1`；测「有甲迁移」需先停 AFK/非枢纽再穿甲，或赶在 120 tick 前穿好。本测用 `mv tp ember_afk` 规避 |
| 未做 | 线上 Q01 掉甲 | 可选；建议 S2 窗口内用白名单号或管理号补一条结算掉甲 |

## 5 建议

**建议放行 S2 白名单（设计稿：5–20 真人）。**

依据：线上 jar/bv/两开关与测服一致；管理/测试号路径下迁移主流程、待领守恒、ACTIVE_IN_STASH+拒 restore、护甲页/待领入口、H8 代理重连均 PASS；开关保持双开未动。

S2 注意：
1. 公告「护甲页可领回原甲」；
2. 观察 OPS §9 告警 + DB §3.1；
3. 副本内延后迁移属预期；卡住号用 `armor mig`；
4. 建议 S2 内补 1 条线上 Q01/日常结算掉甲抽查后再签 S3。

## 6 清场

- 测号已下线；`list` = 0
- 证据保留 `/workspace/tmp/d323-smoke/` + `d323-smoke.tgz`
- 线上进程与开关保持 D323 部署态（本岗未改）
