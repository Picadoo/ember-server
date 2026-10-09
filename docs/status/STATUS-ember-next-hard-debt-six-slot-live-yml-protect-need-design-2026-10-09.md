# 状态 · 下一档硬债选定 · 需策划（观察期 live yml 防源码默认冲掉 · docs-only）

> **旁注（已关 · 批 A · 批 M · D344）：** tip `7c9b8ddd` 已关；总控已批 A·M；STATUS [`STATUS-ember-six-slot-live-yml-protect-d344-2026-10-09.md`](STATUS-ember-six-slot-live-yml-protect-d344-2026-10-09.md) · OPS 防冲小节已落 · **批 A ≠ 改默认 true ≠ 关观察 ≠ 开 K3** · 加注禁主仓切分支 + skip-worktree。

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D342 现态复跑清单已批；预检实跑 **R1+R2 红**（21:00:03 live 被 src 整份替换）；**D343 已恢复**三 true + bv62。D341 K3 闸已批（≠开闸）。禁 Pack6 / 天赋 / 灰印 / 样本 R / K3 live / 提前关观察 / 拧 set_bonus / 改 ×0.97。**零 live 玩法大改（本稿 docs-only）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** tip 已关 · 授权见 D344 STATUS · **零改默认 true** · **本窗不关观察**  
**硬规格（已批 A · 批 M · D344 · OPS 已落 · ≠关观察）：** [`DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md`](../design/DESIGN-ember-six-slot-live-yml-protect-2026-10-09.md) · backlog `B-six-slot-live-yml-protect`  
**打开理由：** 刚发生「`CoreRpg/src/main/resources/ember-v1*.yml` 同步进 `plugins/CoreRpg/` → 六槽段消失 / bv 回落」；恢复后缺**防再冲纪律页**（方向、禁令、核对、与 `bundledConfigsMatchLive` 关系）。

---

## 0. 局势一句话

观察真源在 live；源码默认是关。再冲一次等于静默退出观察——docs 钉死「只能 live→resources，禁止反方向覆盖」。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 关观察 / K3 live / 改 ×0.97 / 样本 R | **否** | 硬禁 / 未满窗 |
| B | 重复 D329–D343 已交债 | **否** | 已落 |
| C | 菜单诚实残留（adventure 仅 awaken） | **否（本窗）** | 次于刚发生事故 |
| **H** | **live yml 防源码默认冲掉** | **采纳 · 需策划** | D342 红 + D343 恢复；根因硬 |

**证据：**
- [`STATUS-ember-six-slot-observe-state-check-run-d342-2026-10-09.md`](STATUS-ember-six-slot-observe-state-check-run-d342-2026-10-09.md)：21:00:03 `ember-v1.yml` / `ember-v1-runs.yml` 被替换为与 git resources 同文；`gear.six_slot` 消失；`balance_version`→**60**。  
- 线上现态（D343 后）：`gear.six_slot` 三 true；runs `balance_version: 62` + 注释「restored D343 after src overwrite」。  
- 破档备份：`/workspace/tmp/d343-restore-*/` · pin `/workspace/tmp/d343-pin/`。  
- `EmberSourceMapTest.bundledConfigsMatchLive`：要求 resources **与 live 逐字节一致**，断言文案写「**copy the live file into the resources**」——正确方向是 live→src；事故是**反方向** src→live。  
- 旧 handoff「两份要一起改」未钉观察期保护键，易误操。

**荐方案 M：** docs-only 防冲纪律 + OPS 小对齐规格（批后另号落 OPS）；钉保护键、禁止覆盖方向、同步/部署前核对；**不**把代码默认改成 true；**不**关观察。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不交关观察签字；不部署 K3；不改 ×0.97；本 tip 窗不擅自再拧 live（已恢复）；不 stage 脏 runtime。

---

*选题 H · 已关 · 批 A·M·D344 · 防再冲 · OPS 已落 · 勿关窗。*
