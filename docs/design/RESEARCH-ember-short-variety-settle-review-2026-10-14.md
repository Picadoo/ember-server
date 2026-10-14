# RESEARCH · 短征花样结算回看（真可点/菜单可感 · ≠soft-reward同题 · ≠variety再拧 · ≠完整variety经济 · ≠settle下一环同题 · ≠半行 · ≠庭内旁路 · ≠抬 event_core）· 2026-10-14

> **状态：** **待批 A** · 荐方案 M · TIP-CY · 拟 D13330 · 2026-10-14 08:14 Asia/Shanghai
> **起因：** 总控【派·TIP-CY / D13330】上游 TIP-CX CLOSED live `934fc0fe2`（jar `1.65.822-d13329` · feat `1b93bc6b4` · tip `0749d92c7` · PASS `ed440e0e1` · 批 `e469ba35e`）。请交下一薄窗。方向：内容优先拉长在线——短征再一趣味薄窗优先；挂机加厚须新硬债证据；DESIGN §6 仍缓：宝石、Cast/Aid/variety完整经济、节/灾厄、sx20、全服结算去指令化。**≠ soft-reward 同题复读**。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+settle下一环+soft-reward **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-CX CLOSED live `934fc0fe2` · feat `1b93bc6b4` · tip `0749d92c7` · PASS `ed440e0e1` · jar `1.65.822-d13329.local` · 批 sha `e469ba35e` · D13329
> **硬禁：** **零** 半行独批 · **零** Cast粒子主交付 · **零** Aid/variety再拧 · **零** todayRot/W1e独批 · **零** 庭内旁路再拧 · **零** soft-reward同题 · **零** settle下一环同题 · **零** 路径追同题 · **零** 日差本同题 · **零** sx20 · **零** 抬日帽/S/targets · **零** 抬 event_rate/event_core/affix_shard · **零** 新 variety kind · **零** 完整 variety 经济 · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄/fest重开 · **零** 教裸指令 · **零** densify · **零** stage脏runtime

---

## 0. 一句话

房内花样 cue 已齐，结算 **看不见本局打了什么花样**——§6③ 后置回看。本债=**settle 记忆 + 薄页 V 真可读**，**不是**再拧/发核心，**不是** soft-reward，**不是**半行。

---

## 1. 选题扫描（TIP-CX 后 · 必须真动作 · 偏向短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚齐 / 仍缓 |
| 半行独批 | **否** | REJECT `d00ed84bc` 保持 |
| 庭内旁路再拧 / 宝石 | **否** | 近饱和 / 庭面 gap 未亮；无新消耗/产出硬债 |
| soft-reward 同题 | **否** | 派单钉死勿再交；刚 CLOSED |
| settle下一环 / 路径追 / 日差本同题 | **否** | 已齐勿再交 |
| 今日推 / 周目标 / 三冲 复读 | **否** | 已齐勿复读 |
| fest / 灾厄 / sx20 / 全服 settle 去指令化 | **否** | 硬禁或仍缓 |
| 挂机区资源环加厚 | **否 · 本窗** | 实扫无新硬债（非第五花/已齐旁路镜像） |
| **短征花样结算回看（荐）** | **采纳** | §6③；房内齐结算丢；菜单无 V；真可读 |

**择优结论：** 总控钉短征趣味薄窗且禁 soft-reward 复读；庭内无新硬债证据故不交挂机旁路。实扫 variety 房内 cue 齐、ShortSettle 只记 map、settle 页无花样格——日更「打完回看本局花样」乐趣断在结算。故交回看；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-CY · 短征趣味优先 · soft-reward 同题勿再交 · 庭内默认勿交 | 派单 | 选题边界 |
| E2 | D13321 / D13328 / D13329 DESIGN §6③ 皆后置「短征花样结算回看」；D13321§6② 另后置「发核心/碎片」——本窗只回看不发奖 | 三份 DESIGN §6 | **合法未 CLOSED 后置**；**≠**完整经济 |
| E3 | soft-reward / settle下一环 / 路径追 / 日差 / 今日推 / 周目标 / 三冲 / Cast / Aid / todayRot / variety(W1a–d) / 庭内全弧 **已齐** | tips CLOSED | ≠ 同题复读 |
| E4 | `EmberShortVarietyFeel`：房内 AFFIX/EVENT tell+AB；Javadoc 钉 settle 零发经济 | VarietyFeel.java | 房内齐 · 禁经济回看误绑 |
| E5 | `EmberShortSettle`：仅 `C_LAST=p1_short_settle_last` 记 sx 图号；**无** affix/event period | ShortSettle.java | **缺回看冲钉** |
| E6 | `EmberRunSession`：`affix`/`eventKind`/`affixDone`/`eventDone`/`varietyRolled` 局内有；settle 后无 PAPI 外露 | RunSession.java L64–68 | **局内有、结算丢** |
| E7 | `ember_p1_short_settle.yml`：Layout `I/E/S/P/F/Z`；Icons 无花样；Open 只 day_line/path | short_settle.yml | **菜单无回看点**；同行可落 V |
| E8 | **为何不是半行：** 主交付=插件 settle 记忆 + `short_var_*` PAPI + 薄页 V 真态；Open/半行不可独批 | 本 RESEARCH | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 否决只口号 |
| E10 | 挂机庭 Layout 已塞满四花+推荐+拆+换+洗+烙+转+随机锻；无新 gap/消耗轨亮面 | ember_p1_afk.yml | **否决挂机加厚本窗** |

**一句话缺口：** 玩家房内已能感到花样，但 **结算页看不见本局词缀/事件**——日更闭环断在回看。

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** 短征 settle 时写入本局 `affix`/`eventKind`（空=无花样）；PAPI `short_var_hit` / `_affix_label` / `_event_label` / `_line`；覆盖策略=下次短征 settle；单测  
2. **菜单：** `ember_p1_short_settle` 增 V「本局花样」；有 hit 亮态显示标签；无 hit 灰「本局无花样」；**不改** E/S/P/F  
3. **经济：** **零** NI / **零** event_core / **零** affix_shard / **零** 改 rate / **零** 新 kind  
4. **Open/半行：** 若需辅指针仅同批且不可独批  
5. **零改**日帽 / S40–S58 / targets / Cast / Aid / todayRot / variety yaml / 庭内 / soft-reward / ShortPath resolve / ShortSettle map 语义（可并列记 var）

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅 lore「本局有花样」无记忆无 V | 弱于真动作；默认不批 |
| W 抬表/sx20/发核心/variety再拧/庭内/soft-reward同题 | 硬禁 |

---

*RESEARCH · 短征花样结算回看 · **待批 A** · 荐 M · TIP-CY / 拟 D13330 · 半行 REJECT `d00ed84bc` 保持 · soft-reward 同题勿再交。*

## STATUS: **待批 A** · 荐 M · D13330 · TIP-CY · 2026-10-14 08:14 Asia/Shanghai
