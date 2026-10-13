# RESEARCH · 短征日差软目标极薄有奖（真可点领 NI · ≠日差本同题 · ≠路径追同题 · ≠settle下一环同题 · ≠今日推/周目标复读 · ≠半行 · ≠庭内旁路 · ≠抬表 · ≠写 weekly_goals）· 2026-10-14

> **状态：** **待批 A · 荐方案 M** · 拟 D13329 · TIP-CX · docs-only · 批准前禁改 YAML/Java · 2026-10-14 07:48 Asia/Shanghai
> **起因：** 总控【派·TIP-CX / D13329】上游 TIP-CW CLOSED live `71db92dfb`（jar `1.65.821-d13328b` · feat `8c2dc7d95` · PASS `72e8e6a4e` · 批 `e73f5847e`）。请交下一薄窗。方向：短征日循环仍优先；可选 **soft-target 日分流极薄有奖**（此前零经济）。DESIGN §6 仍缓：宝石、Cast/Aid/variety 经济、节/灾厄、sx20、全服结算去指令化；**本窗例外开 soft-target 薄有奖**。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+settle下一环 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-CW CLOSED live `71db92dfb` · feat `8c2dc7d95` · tip `374a142fd` · PASS `72e8e6a4e` · jar `1.65.821-d13328b.local` · 批 sha `e73f5847e` · D13328
> **硬禁：** **零** 半行独批 · **零** Cast粒子主交付 · **零** Aid/variety再拧 · **零** todayRot/W1e独批 · **零** 庭内旁路再拧 · **零** 日差本同题 · **零** 路径追同题 · **零** settle下一环同题 · **零** sx20 · **零** 抬日帽/S/targets · **零** 写入 weekly_goals · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄/fest重开 · **零** 教裸指令 · **零** densify · **零** stage脏runtime

---

## 0. 一句话

日差进本冲口已齐，但软目标 3 **仍零经济**——D13326–28§6① 后置、总控本窗开「极薄有奖」。本债=**满 3 一日一次骨尘×2 + L 真点领**，**不是**日差本同题，**不是**半行，**不写** weekly_goals。

---

## 1. 选题扫描（TIP-CW 后 · 必须真动作 · 偏向短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚齐 |
| 半行独批 | **否** | REJECT `d00ed84bc` 保持 |
| 庭内旁路再拧 / 宝石 | **否** | 近饱和 / 庭面 gap 未亮 |
| 日差本同题 / 路径追同题 / settle 下一环同题 | **否** | 派单钉死勿再交 |
| 今日推 / 周目标 / 三冲 复读 | **否** | 已齐勿复读 |
| 短征花样结算回看 / variety 经济发核心 | **否 · 本窗** | §6 后置；易触再拧 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 挂机区资源环加厚 | **否 · 本窗** | 无新硬债证据；默认勿庭内旁路 |
| **soft-target 日分流极薄有奖（荐）** | **采纳** | §6①；总控开窗；零发奖硬证；缺领奖点；真可点 |

**择优结论：** 总控钉短征日循环并**显式开** soft-target 薄有奖；日差进本同题禁交。实扫 `SOFT_TARGET=3` 零经济、L 无领态——日更「换本满 3」乐趣断在发奖闭环。故交极薄有奖；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-CX · 短征日循环优先 · 开 soft-target 极薄有奖可选 · settle/路径追/日差进本同题勿再交 | 派单 | 选题边界 |
| E2 | D13326 / D13327 / D13328 DESIGN §6① 皆后置「日差软目标发奖 / 写入 weekly_goals（须另批）」 | 三份 DESIGN §6 | **合法未 CLOSED 后置**；本窗只开薄有奖、**禁写 weekly_goals** |
| E3 | 今日推/周目标/日差进本/三冲/路径追/settle下一环 **已齐** | short.yml · tips CLOSED/PASS | ≠ 同题复读 |
| E4 | `EmberShortDayDivChase`：`SOFT_TARGET=3`；Javadoc「仅展示 · 禁发额外币料 · 禁写 weekly_goals」 | DayDivChase.java L7–18 | **零发奖硬证** |
| E5 | PAPI `short_div`/`_n`/`_line`/`_chase*`/`_done` 皆只读；**无** `short_div_claim*` | RunPapi · DayDivChase | **缺领奖冲钉** |
| E6 | 选页 L：灰态 done/无冲口/体力不足；亮态仅 `command: corerpg p1 enter short-div`；**无 claim** | ember_p1_short.yml L827–868 | **菜单无领奖点** |
| E7 | 薄奖范式：`EmberOnlineClaimWinBone` 日一次 `mat_ember_bone_dust`×1 · Vault/NI | OnlineClaimWinBone.java | **可复用发奖骨** |
| E8 | **为何不是半行：** 主交付=插件一日一次 NI 真发 + `claim short-div-soft` + L 可领态真点；Open/半行不可独批 | 本 RESEARCH | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 否决只口号 |
| E10 | ≠ 日差本同题：进本/`enter short-div`/chase **不改**；本窗只加 claim 经济薄层 | 本 RESEARCH | 边界钉死 |

**一句话缺口：** 玩家已能点 L 换本冲软目标，但 **满 3 仍零发奖、菜单无领**——日更闭环断在极薄有奖。

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `n≥SOFT_TARGET` 且今日未领 → claimable；`corerpg p1 claim short-div-soft`（或等价）发 NI `mat_ember_bone_dust`×**2**（一日一次 · period `p1_short_div_soft_claim`）；PAPI `short_div_claim_ok` / `_claimed` / `_claim_line`；单测  
2. **菜单：** 选页 L 增「可领」态（优先于 enter）：点领→claim；已领/未满诚实灰态；**保留**未满时 enter short-div  
3. **经济幅度（供总控批）：** 骨尘×2 / 日 · 约等于 2×在线加赠骨尘；**远薄于**单次短征有奖通关；**零**徽 / **零** event_core / **零** affix_shard / **零** 抬日帽 / **零** 抬 `weekly_goals.targets.short` / **零** 写入 weekly_goals  
4. **Open/半行：** 若需辅指针仅同批且不可独批  
5. **零改**日帽 / S40–S58 / targets.short / Cast / Aid / variety / 庭内 / 路径追 / settle 薄页 / ShortPath resolve / enter short-div 谓词

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅 lore「满 3 有奖」无真发 | 弱于真动作；默认不批 |
| W 抬表/sx20/日差进本同题/写 weekly_goals/庭内/Cast·Aid·variety再拧 | 硬禁 |

---

*RESEARCH · 短征日差软目标极薄有奖 · **待批 A · 荐方案 M** · TIP-CX / 拟 D13329 · 半行 REJECT `d00ed84bc` 保持 · 日差本/路径追/settle下一环同题勿再交。*

## STATUS: **待批 A · 荐方案 M** · docs-only · D13329 · TIP-CX · 2026-10-14 07:48 Asia/Shanghai
