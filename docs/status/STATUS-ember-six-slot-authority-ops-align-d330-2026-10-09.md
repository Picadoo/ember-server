# 状态 · D330：六槽权威文档 + OPS 对齐勘误（批 A·M · docs 落字）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-authority-ops-align-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-authority-ops-align-need-design-2026-10-09.md) @ `80d41c5c` · DESIGN [`DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md`](../design/DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D330** · 总控授权同号 docs-only 落字  
**版本：** **docs only**（gear-structure · OPS · staged README · tip · backlog 旁注）· jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / TrMenu **未动**

## 人话

线上早已是 Stage1 F + Stage2 档 C（bv62 · 三开关开 · 观察中），但权威装备文档还写「六槽未上线」、OPS 还写「1.65.97 / 功能没有」且缺 `set_bonus`、staged README 还写四件套「后续开放」。本号只改文档口径，避免观察期运维误关开关；玩法与菜单都不动。

## 改动

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-gear-structure-2026-10-06.md` | §1 六槽→已上线观察中；§9 Stage1+2 已部署观察；§10 变更记录 |
| `OPS-ember-six-slot-migration.md` | 文首对齐 jar/bv62/三开关；§1 增 `set_bonus`；新 §1.4 观察期 |
| `docs/design/staged/d318-six-slot/README.md` | 四件套「后续开放」→真实三态（D325）· 装备入口镜像（D329） |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-authority-ops-align` → 已批 A · 已落字；stage1/catchup/t1-spec 历史旁注 |
| jar / CoreRpg yml / bv / set_bonus / TrMenu | **未动** |

## 口径钉死

- 线上：**F + Stage2 C** · **bv62** · jar **`1.65.99-d325.local`** · 三开关开 · **观察中**
- 真源：**代码 / 线上配置 > 文档**
- 手册**不擅自授权**切开关（须总控签）
- 不抢 K3；不改 ×0.97；不重做 D329

## 验收

| # | 项 | 结果 |
|---|----|------|
| A1 | gear-structure §1 不再写六槽「未上线」 | PASS |
| A2 | OPS 文首与线上一致；§1 含 set_bonus | PASS |
| A3 | staged README 无「后续开放」指四件套 | PASS |
| A4 | 未改 jar / yml 玩法 / ×0.97 / set_bonus 真值 | PASS（本号 diff 仅 docs） |

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · TrMenu（D329 已落地）· 样本 R / Pack6 / 天赋 / 灰印

---

*D330 批 A·M · tip `80d41c5c` · docs-only · 观察期薄窗。*
