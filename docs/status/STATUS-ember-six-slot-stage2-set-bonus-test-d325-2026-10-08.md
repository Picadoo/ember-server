# 余烬 · 六槽 Stage2 档 C · D325 隔离测服验收（余烬-测试 · 2026-10-08）

- 上游：总控 D325 派单；规格 `docs/design/DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md`（档 C）；施工 `docs/status/STATUS-ember-six-slot-stage2-set-bonus-d325-2026-10-08.md`
- tip / jar：`b02ca9f3` · `CoreRpg-1.65.99-d325.local.jar` · sha256 **`4c273c5a509dbbe81d771ff6e02cdaac89ec3459919e957479b6acba5fddc8cc`**
- 环境：`/workspace/tmp/d325-iso/` · Paper `127.0.0.1:25577` · 临时 mariadbd `3318`/`ember_d325` · 堆 ≤1.5G · **无** Waterfall（直连）
- 证据：`/workspace/tmp/d325-iso-evidence/`（及同目录 tgz）
- 执行：2026-10-08 17:16–17:30 CST
- **结论：G-S0～G-S4 实机 PASS；不变量 ①② 无违例；线上 4 PID / Stage1 jar（`abc96aca…` / `1.65.98-d322.local`）未动、未写 set_bonus。**
- **建议：签部署（bv62）** —— 隔离实机过线；部署签时再升 bv62（只含 Stage2 四件套）；观察期现网继续 bv61，直至总控另签热更。

## 1 预检

| 项 | 结果 |
|---|---|
| jar sha | ✅ `4c273c5a…` 与 `/workspace/tmp/d325/JAR.sha256` 一致；日志 `Enabling CoreRpg v1.65.99-d325.local` |
| tip | ✅ `b02ca9f3`（施工 STATUS） |
| 离线冒烟 | ✅ `offline-smoke.sh` → ALL OFFLINE SMOKE PASS（单测+菜单无「后续开放」+jar 含 set_bonus） |
| bv 测服 | ✅ `ember-v1-runs.yml` → `balance_version: 61`（仅测服） |
| 开关起步 | ✅ 测服 `enabled=true` `migrate=true` `set_bonus=false` → reload 切 true/false |
| 线上快照 | ✅ PID 47100/47542/47617/107524；live jar `abc96aca…` / `1.65.98-d322.local`；`ember-v1.yml` **无** `set_bonus` 键 |

## 2 必验结果

| # | 必验 | 结果 | 关键证据 |
|---|------|------|----------|
| 1 | 菜单/PAPI D169 计数；护甲页无「后续开放」；S 格三态 | **PASS** | 凑套后 `/corerpg p1 armor set` →「四件套已激活（焚烬族护甲 3/2）：受伤略减（−3%）」；进行中「护甲 1/2」；未激活「先让刃与护符同族」。PAPI `%corerpg_p1_armor_set%`=`焚烬族 护甲 3/2 · 受伤 −3%`。TrMenu 护甲页 slot28 Book lore 同文案；yml 无「后续开放」。`fixup-set*.txt` / `fixup-papi.txt` / `matrix-menu.json` |
| 2 | set_bonus 开：激活 ×0.97；未激活 ×1.0；关 set_bonus 始终 ×1.0 | **PASS** | poison 管道（debug console）：关开关 `final 0.80`（仅 ×M）；开+激活 `… D325 四件套受伤 ×0.97: 1.00 → 0.78`；开+拆至 1/2 `final 0.80` 无 D325 行。`matrix-A/B/C*.log` / `matrix-results.json` |
| 3 | 不进 B/H 面板 | **PASS** | 同装 set_bonus 关/开 status：`B=47.04 H=124.98 D=10` 逐位不变；减伤仅受伤日志。`fixup-status-off/on.txt` |
| 4 | Stage1 迁移/待领不冲突 | **PASS** | 测服 enabled+migrate 开；`armor status` → `p1_six_mig=1 journal=false 待领=0 enabled=true migrate=true`；claim「没有待领物品」；DB `cr_p1_item` active 行正常。`13-post-mig-status.txt` / `15-db.txt` |

### 门禁对照

| # | 门禁 | 结果 |
|---|------|------|
| G-S0 | 开关关=基线（无四件套减伤） | **PASS**（实机 poison final 0.80，无 D325 行） |
| G-S1 | 仅掉落阶≥T2 同族甲计件 | **PASS**（离线单测+实机 3×T2 计 3/2；拆至 1 件→进行中） |
| G-S2 | 激活 ×0.97；B/H 不变 | **PASS** |
| G-S3 | 菜单进度 / 三态；无命令教学 | **PASS**（菜单+PAPI+set 回复） |
| G-S4 | 资产 loss/dup 不回归（迁/待领） | **PASS**（抽查） |
| G-S6 | 不改 F/护符×1.0/K0 | **PASS**（审阅依赖施工单测；实机未改价表） |

## 3 环境与约束

- 测服端口：游戏 **25577** / 库 **3318**（均 127.0.0.1）；**未**占 25565/66/67/3306
- 内存：演练期间 available ≥ **5.6 GB**（未触 1.5G 暂停线）
- 线上：MariaDB / login / proxy / play **PID 未变**；未写 3306；未改 proxy-runtime / 线上 jar / bv / enabled / migrate / set_bonus
- 未 `git stash` / `reset --hard` / force-push

## 4 残余 / 观察

| 级 | 项 | 说明 |
|---|---|---|
| 观察 | debug console 为开关翻转 | reload 后需再开一次才能采伤害日志；验收脚本已处理 |
| 观察 | TrMenu displayName | mineflayer 对自定义名解析为材质名；以 lore / PAPI / set 回复为准 |
| 非阻 | 测服缺部分旧 NI 材料模板 | 仅 WARN；P1 give 刃/符/甲模板齐全 |

## 5 建议

**建议签部署（bv62）。**

- 隔离实机：菜单/PAPI D169、×0.97/×1.0 矩阵、B/H 不变、迁移/待领抽查均 PASS
- 部署签时再升 **bv62**（只含 Stage2 四件套）；**观察期现网继续 bv61**，直至总控另签换 jar / 开 `set_bonus`
- 失败回退：关 `set_bonus` → 无减伤；Stage1 迁移数据保留

## 6 清场

停隔离 Paper / 临时 mariadbd；释放 25577/3318；核 live-before.sha256 与线上 4 PID；本报告 commit+push（仅 STATUS）。

---
*D325 测试岗隔离验收 · 档 C · 不动线上*
