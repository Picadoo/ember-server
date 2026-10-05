# 冒烟 + 丢失/复制回归 · CoreRpg 1.65.42 / D208 物品键迁到物品本身（ARCH S1-4 · REG-counter-registry §4-4）— 2026-10-05

- 版本：CoreRpg 1.65.42 / bv57（不改数值）；线上 jar `2b6f303c…`（= `CoreRpg/target/CoreRpg.jar`，JDK8 构建，class major 52，单测 354/0，新增 `EmberItemKeysTest` 6 条 + `EmberItemDataTest` v1/v2 用例）；备份 `/workspace/backup/CoreRpg-1.65.41-pre-d208-20261005-2244.jar`。
- 部署：routine-2223 于 22:44 只重启 play（login 未动）；persist-roundtrip 自己做了多次正常重启和 kill -9，最后一次 23:07 正常启动：`Enabling CoreRpg v1.65.42`、`[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`，SEVERE 0。play PID 1932837。`cr_p1_item` 新增 4 列 `affix / af_pity / sig_code / reroll_n`（addColumnIfMissing）。
- routine-2223 在 23:13 后中断，源码未提交；routine-2351 核对源码时间早于 jar 构建时间、重新跑单测、补冒烟后提交。

## 资产路径回归：`tools/p1map/persist-roundtrip.sh` 全部 PASS=253 FAIL=0（bot FreshQ749–757）
洗练属于资产路径（扣币 / 扣碎片 + 改物品），所以按规定整套跑完，包括故障注入开 / 关、两次 kill -9。h3 段的断言从 `p4_*` 计数器改成看 `cr_p1_item` 列：词条在物品行上、`reroll_n` = 已提交洗练次数、`data_version` 2、不再写任何 `p4_af_/p4_afp_/p4_rrn_/p4_rro_`；kill -9 后扣费和洗练结果同在一笔 `cr_p1_txn` 里，要么都在、要么都不在，背包里的那件物品会重新同步。

## 冒烟：`tools/p1map/d208-itemkeys-smoke.sh`（本地，tools/ 在 .gitignore 里）
| 检查 | 结论 |
|---|---|
| 启动 + 新列 | 版本行、两条 MySQL 行、`cr_p1_item` 4 个新列都在 |
| 新号 FreshQ760 真实 Q01 首通 | 三房间 + Boss，`settle … (first clear)`；名下 3 件 P1 装备全部 `data_version` 2 |
| 碎片洗练（新刃） | 词条 21 写在物品行上，`reroll_n=1`、`data_version=2`，正好一笔 `afx:` 流水，没有 `p4_*` 计数器，背包物品 rev = 数据库 rev |
| 管理员签名写入 L07 | `sig_code=7` 在物品行上，词条保留，没有 `p1_sig_` 计数器，rev 一致 |
| 装备库存入 + 取出 | 手动补查（23:57）：刃挪到背包后一键存入 → 行 `stored`、词条 21 / 签名 7 / `reroll_n` 1 都在、背包没有残留；`gearlib take` → 行 `active`、键全在、背包正好 1 件、rev 4 = 数据库 rev 4、名下仍无任何旧计数器 |
| 旧号 FreshQ35（v1 刃 + `p4_af_` 计数器 12） | 旧计数器照常读到；第一次写入（签名）把它折进 v2：`data_version 2 / affix 12 / sig 7`，旧计数器被删，rev 一致 |
| 收尾 | 无 SEVERE / Exception；botd `list=[]` |

脚本里「装备库存入」一步三次都报 FAIL（23:10 / 23:13 / 23:54）：原因是脚本没把刃从快捷栏挪进背包（一键存入本来就不收快捷栏和手上的刃），不是插件问题；同一件刃按上表手动补查通过。脚本已改成固定挪到背包第 11 格，下次直接用。

## 漏洞审查
- **旧计数器复活**：v2 物品只认物品上的字段，同 uid 的旧计数器一律忽略，所以没法靠残留的 `p4_afp_` 把保底或签名「复活」。
- **v1 → v2 折叠只发生一次**：折叠在那笔物品流水提交成功后才删旧计数器；流水失败则物品仍是 v1，旧计数器还在，下次再折叠，不会丢也不会翻倍。
- **洗练结果窗口**：以前「已付款、结果待定」靠 `p4_rro_`，现在结果和扣费在同一笔 `cr_p1_txn` 里提交，没有中间态可利用（kill -9 回归已验证）；老的 `p4_rro_` 进服时仍会被结算。
- **复制**：键跟着物品走（NBT + 数据库行 + HMAC），装备库存取、补发、分解撤销都带着同一行；HMAC 把新字段算进去，改 NBT 伪造词条会验签失败。v1 物品按旧格式仍能验签。
- 不改数值（bv57），p1sim 不受影响。
