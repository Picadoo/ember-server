# 余烬 · 观察期过时 tip 批量关闭包（决策页）

STATUS=**已批 A · 批 M · D349**（docs-only 关闭规格 · **≠关观察** · **≠**薄抽复述 · **不**抢 K3 · **不**改 ×0.97 · **不**开样本 R）· 2026-10-09 · 上游 tip `STATUS-ember-next-hard-debt-observe-stale-tip-close-pack-need-design-2026-10-09.md` · 落字 STATUS [`STATUS-ember-observe-stale-tip-close-pack-d349-2026-10-09.md`](../status/STATUS-ember-observe-stale-tip-close-pack-d349-2026-10-09.md)

> **一句话：** 已施工/HOLD/关清的 `next-hard-debt` tip 缺标准「已关」头——荐 **方案 M**：模板 + 清单 + 另号批量旁注。

### 0. 证据

| # | 现象 | 对照 |
|---|------|------|
| E1 | ≥15 条 tip 正文已写「已施工 D3xx / 已上线 / HOLD」，头无 `旁注（已关）` | `live-yml-protect` 等已有标准头 |
| E2 | 同形噪音仍见于 D297–D311 / HOLD / D326–D327 tip 头 | D348 已示范关清 D346 tip |
| E3 | HOLD tip（灰印/天赋）未盖章 → 易被误读为「待开施工」 | 须标 **tip 已关 · 债 HOLD** |
| E4 | 本窗硬禁再交已 PASS 薄抽清单 | 关闭包 ≠ spot checklist |

**本窗不做：** 关观察签字、K3 live、改玩法/菜单、重跑任何薄抽、动 hub_legacy、假关未结案债。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2 模板 + §3 关闭清单；批后另号只加旁注/改「本窗性质」；可选 backlog 脏句同步 | **荐** |
| L | M + 重写全部 tip 正文史 / 合并删除 tip 文件 | 过厚；史不删 |
| W | 借关闭包关观察 / 开 K3 / 开 R / 改 ×0.97 | **否决** |

---

## §2 关闭旁注模板（必须统一）

每个目标 tip **文首**插入（或替换过时「待批」头）：

```markdown
> **旁注（已关 · <裁决摘要>）：** tip `<commit或路径>` 已关；指向 STATUS [`<STATUS文件>`](<相对路径>) · <一句话结果> · **≠关观察**（若适用）· <其它禁读歧义>。
```

并改一行：
- `**本窗性质：** tip 已关 · …`  
- `**硬规格（…）：**` 去掉「待批 A」，改为已批/已施工/HOLD 等实态。

**规则：**
1. **只加旁注 / 改性质句**；不删证据表、不改史。  
2. **HOLD ≠ 已上线**：灰印/天赋必须写 `HOLD · tip 已关 · 禁当施工债重开`。  
3. **D326 观察关窗 tip**：写 `已关 · 观察执行中`；**明确 ≠ 绿出口已签 / ≠ 本号关观察**。  
4. **K3 refine tip**：写 `已批 · T0‴ PASS · 施工等绿出口`；**≠开闸**。  
5. **禁止**把未结案 tip 塞进清单。  
6. **禁止**本号跑薄抽或改 TrMenu。

---

## §3 关闭清单（方案 M · 批后另号落字）

### 3.1 体验波已施工（旁注：已关 · 已施工 Dx）

| tip（`STATUS-ember-next-hard-debt-…`） | 关闭指向 |
|----------------------------------------|----------|
| `sig-attune-need-design-2026-10-07` | D297 · `STATUS-ember-sig-attune-decision-d297.md` |
| `playfeel-telemetry-need-design-2026-10-07` | D298 · `STATUS-ember-playfeel-telemetry-d298.md` |
| `refarm-short-feedback-need-design-2026-10-07` | D299 · `STATUS-ember-refarm-short-feedback-d299.md` |
| `corridor-feel-need-design-2026-10-07` | D300 · `STATUS-ember-corridor-feel-diff-d300.md` |
| `guard-skill-need-design-2026-10-07` | D301 · `STATUS-ember-guard-skill-parry-d301.md` |
| `abyss-feel-need-design-2026-10-07` | D302 · `STATUS-ember-abyss-feel-diff-d302.md` |
| `weekly-raid-feel-need-design-2026-10-08` | D303 · `STATUS-ember-weekly-raid-feel-diff-d303.md` |
| `boss-telegraph-need-design-2026-10-08` | D304 · `STATUS-ember-boss-telegraph-honesty-d304.md` |
| `hang-farm-identity-need-design-2026-10-08` | D305 · `STATUS-ember-hang-farm-soft-identity-d305.md` |
| `hub-daily-routing-need-design-2026-10-08` | D306 · `STATUS-ember-hub-daily-routing-d306.md` |
| `workshop-menu-honesty-need-design-2026-10-08` | D307 · `STATUS-ember-workshop-menu-honesty-d307.md` |

### 3.2 样本/决策 docs 已落（旁注：已关 · 已落仓 Dx）

| tip | 关闭指向 |
|-----|----------|
| `sample-window-readiness-need-design-2026-10-08` | D308 · `STATUS-ember-sample-window-readiness-d308.md` |
| `offhand-merge-decision-need-design-2026-10-08` | D309 · `STATUS-ember-offhand-merge-decision-d309.md` |
| `sample-week-report-need-design-2026-10-08` | D310 · `STATUS-ember-sample-week-report-d310.md` |
| `sample-week-report-script-need-design-2026-10-08` | D311 · `STATUS-ember-sample-week-report-script-d311.md` |

### 3.3 HOLD（旁注：tip 已关 · 债 HOLD · 禁当上线）

| tip | 关闭指向 |
|-----|----------|
| `talent-mech-pivot-need-design-2026-10-07` | HOLD · `STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md` |
| `ash-imprint-need-design-2026-10-08` | HOLD T0/T0b ❌ · `STATUS-ember-ash-imprint-t0b-2026-10-08.md` |

### 3.4 六槽观察链（旁注须防误读）

| tip | 关闭指向 | 必写歧义禁语 |
|-----|----------|--------------|
| `six-slot-stage2-observe-close-need-design-2026-10-08` | D326 · observe-close STATUS | **≠绿出口已签 · ≠本号关观察** |
| `six-slot-k3-refine-need-design-2026-10-08` | D327 · k3-refine STATUS | **≠开闸 ≠部署 K3 live** |
| `six-slot-d346-gear-set-spot-need-design-2026-10-09` | **已由 D348 关清**（对照样例） | **本包跳过 · 勿重复旁注** |

### 3.5 明确不进本包

| 项 | 理由 |
|----|------|
| 仍真正待批、无结案 STATUS 的 tip | 禁止假关 |
| hub_legacy 文案债 | 非玩家可达；后置 |
| 已有标准「旁注（已关）」的 tip（D329–D345 主链、D348 已关 D346 tip 等） | 勿重复 |
| 绿出口签字包交卷 / 关观察 | **否决**（日历未满） |

### 3.6 可选同号

- backlog 中仍写「待批 A」但债已结的条目 → 改「已关 · 见 STATUS」。  
- `six-slot-stage1-decision` tip 若头已有关清句则跳过。

---

## §4 验收（落字另号）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| C1 | §3.1–§3.4 清单 tip 均有 `旁注（已关` | 头可扫 | 仍「待批 A」无结案指针 |
| C2 | HOLD 两条含 HOLD / 禁当上线 | 字面 | 写成「已施工上线」 |
| C3 | D326/D327 旁注含歧义禁语 | 字面 | 写成已关观察/已开 K3 |
| C4 | 本号未改 TrMenu / 开关 / bv；未跑薄抽 | diff | 误动玩法 |

**绿出口：** C1–C4 PASS。  
**回滚：** 还原 tip 旁注行。

### 4.1 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 批 A · 本规格 |
| 文档 | 批后另号按 §3 落字 |
| 总控 | 批 A；**≠关观察** |

---

## §5 不动与否决

| 项 | 状态 |
|----|------|
| ×0.97 / set_bonus / jar / TrMenu / live yml | 不动 |
| 提前关观察 / K3 live / 样本 R / Pack6 / 天赋·灰印续跑 | 否决 |
| 再交已 PASS 薄抽清单 | **否决（硬禁）** |
| hub_legacy 玩家面施工 | 否决（不可达） |

---

## §6 批注栏

- [x] **批 A：采纳方案 M** → 另号按 §2–§3 批量旁注（D349 已落）  
- [ ] 升级 L（不荐）  
- [ ] 否决 / 改派  

**总控批注：** **已批 A · 批 M · D349** · 方案 M 落地：§3.1–§3.4 tip 批量旁注关闭（D346 tip 跳过）· **≠关观察 · ≠开 K3 · ≠薄抽**。

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 禁薄抽复述；hub_legacy 后置 | 策划执行手 |
| 2026-10-09 | **批 A · 批 M · D349** · 另号批量旁注落字 · C1–C4 自检 | 总控委派 executor |

*过时 tip 关闭包 · **已批 A · 批 M · D349** · docs-only · ≠关观察 · ≠薄抽复述。*
