# 余烬 · 六槽 Stage2 观察期并行必抽 M1–M9（余烬-测试 · D327 · 2026-10-08）

- **性质：观察期并行抽查 · 攒绿出口证据 · 未签关观察**
- 规格：[`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md) §2 / §3（方案 M · 批 A D326）
- 上游 live：[`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) @ `6e734dc7`
- 现态：bv**62** · `set_bonus=true` · `enabled=true` · `migrate=true` · jar sha **`4c273c5a509dbbe81d771ff6e02cdaac89ec3459919e957479b6acba5fddc8cc`** · `1.65.99-d325.local` · tip `b02ca9f3`
- 环境：play PID **149359**（全程未变）· login **47542** · proxy **47617** · MariaDB **47100**（后三者未动）
- 测号：`D327Spot`（主矩阵）· `D327Aud`（M7 干净审计）；经代理 25565；**未动真人档**
- 证据：`/workspace/tmp/d327-observe-spot/` + `/workspace/tmp/d327-observe-spot.tgz`
- 备份回滚路径（未触发）：`/workspace/tmp/d325-bv62-backup-20261008173047/`
- 执行：2026-10-08 20:01–20:09 CST
- **日历：** 观察起点 2026-10-08 17:40 CST；**满窗仍须 ≥2026-10-10 17:40 CST**（本号不代替关观察签字）
- **结论：§2 必抽 M1–M9 整轮 PASS。不变量 ①② 无违例。未开 R/K3、未改 ×0.97、未关 Stage1。**
- **建议：维持观察（继续 bv62 / set_bonus=true）；满窗后再另签关观察。**

## 1 开跑前复核

| 项 | 结果 |
|----|------|
| jar sha / plugin.yml | ✅ `4c273c5a…` / `1.65.99-d325.local` |
| bv / 三开关 | ✅ 62 · enabled+migrate+set_bonus 全 true |
| play / sidecars PID | ✅ 149359 / 47542 / 47617 / 47100 |
| `list` | ✅ 0（开跑与收尾） |

## 2 M1–M9 结果表

| ID | 内容 | 结果 | 关键证据 |
|----|------|------|----------|
| **M1** | 菜单/PAPI 三态；无「后续开放」；PAPI 与 armor set 一致 | **PASS** | `0/2`→`1/2`→「已激活（焚烬族护甲 3/2）：受伤略减（−3%）」；PAPI `焚烬族 护甲 3/2 · 受伤 −3%` / `active=1`；`ember_p1_armor.yml`+菜单扫无「后续开放」。`fixup-M1-*.txt` / `fixup-M1-papi-active.txt` / `fixup-M1-menu.json` |
| **M2** | 四件套激活 + P1 世界受伤 ×0.97 | **PASS** | ember_afk 采毒：`D325 四件套受伤 ×0.97: 1.00 → 0.78` · `final 0.78`（相对未套装基线 0.80）。`fixup-poison-M2-active.log` |
| **M3** | 拆至同族 T2+ 甲 &lt;2 → ×1.0（优先拆件） | **PASS** | 拆头/胸→`进行中 1/2`；poison 无 D325 · `final 0.80`；**未**为 M3 关 `set_bonus`。`fixup-M3-broken.txt` / `fixup-poison-M3-broken.log` |
| **M4** | 2 甲位异族高成色；另 2 同族 T2+ 仍可激活；进度可懂 | **PASS** | 烬爆头/胸 T3 + 焚烬腿/靴 T2 →「已激活（焚烬族护甲 2/2）…另 2 甲位可穿异族追成色」。`fixup-M4-set.txt` / `fixup-M4-armor.json` |
| **M5** | 同装激活/开/关 B/H/D 逐位不变 | **PASS** | 同装短暂 `set_bonus=false` 对照：`攻击 47 · 生命 125 · 防御 10` 不变；**立即恢复 true**。`fixup-M5-on.txt` / `fixup-M5-off.txt` |
| **M6** | Stage1 mig/journal/待领/claim | **PASS** | `p1_six_mig=1 journal=false 待领=0 enabled=true migrate=true`；claim「没有待领物品」。`fixup-M6-status.txt` / `fixup-M6-claim.txt` |
| **M7** | audit：无非预期 ACTIVE_NOT_HELD / 同 uid 双持 | **PASS** | 新号 `D327Aud`：`held 11 · rows 11 · findings 0`；无 DUPLICATE。`M7-clean-audit.txt` / `M7-clean-summary.json`。（`D327Spot` 先前测号 `clear` 造成的 admin-give 孤儿属测试噪声，不计阻塞；见 §3） |
| **M8** | 同一受伤事件至多一条 D325 ×0.97；关效果后无该行 | **PASS** | 每条 `[P1伤害]` 行恰 **1** 条 D325；`final` 恒 0.78（≠双乘≈0.76）；关开关 / 拆套后 D325=0。`M8-per-line-analysis.json` + poison 日志 |
| **M9** | 结束时三开关 true · bv 62 | **PASS** | enabled/migrate/set_bonus 全 true · bv=62 · play **149359** 未变。`fixup-M9-final.json` |

## 3 手法与安全

- **M3 优先拆件**；仅 **M5** 同装面板对照短暂 `set_bonus=false`（reload），测完**立即恢复 true** 并再确认 armor set 已激活
- poison / 减伤采样均在 **ember_afk**（P1 `scope.worlds`）；`corerpg p1 debug console` 采伤前确认 true
- **未**改 jar / ×0.97 / bv；**未**开 R/K3/Pack6/档 S；**未**关 Stage1 enabled/migrate
- login / proxy / MariaDB PID 全程未变；无 play 重启
- 测号临时 op，用完 deop；真人档未碰
- **未** `git stash` / `reset --hard` / force-push；运行时 yml 变更**不提交**

### M7 噪声说明

对 `D327Spot` 连续 `clear` + admin `p1 give` 会产生 OPS 所述 give/clear 窗口 `ACTIVE_NOT_HELD` 孤儿（与 Stage2 无关）。改用新号 `D327Aud`、审计时物品仍在持有 → **findings 0**，作为 M7 过线证据。

## 4 红线 §3

本轮**未触发** R1–R5。无需 `set_bonus=false` 驻留、无需回滚 jar。

## 5 建议

**观察期并行必抽整轮 PASS，建议维持观察。**

- 本号只攒 §2 绿证据；**不**签关观察
- 绿出口日历门槛不变：满 **48h**（≥ **2026-10-10 17:40 CST**）+ 本轮（或后续）必抽 PASS + 无阻塞申告 → 总控**另签**
- 关 Stage2 观察 **≠** 关 Stage1 enabled/migrate

## 6 清场

- 测号已下线；`list`=0
- 线上保持：`1.65.99-d325.local` · bv62 · enabled+migrate+**set_bonus** 全 true · play **149359**
- 证据保留 `/workspace/tmp/d327-observe-spot/` + `d327-observe-spot.tgz`

---
*D327 测试岗 · Stage2 观察期并行必抽 · PASS · 未签关观察*
