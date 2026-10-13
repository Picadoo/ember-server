# 状态 · 下一档硬债选定 · 需策划（拟 D13327 · 短征选页「路径追」可点 · docs-only · ≠日差同题 · ≠今日推/周目标复读 · ≠D406 首通同屏复读 · ≠半行 · ≠sx20 · ≠抬日表 · ≠庭内旁路 · ≠Cast/Aid/variety再拧）

> **旁注（勾毕 · CLOSED live · feat `c31c780a9` · tip `a206b9893` · PASS `f9e4ca402` · jar `1.65.819-d13327.local` · 批 sha `ac2889160` · 2026-10-14 04:33 Asia/Shanghai · 路径追同题勿再交）：** tip **CLOSED live** · DESIGN [`DESIGN-ember-short-path-chase-visible-2026-10-14.md`](../design/DESIGN-ember-short-path-chase-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-path-chase-visible-2026-10-14.md`](../design/RESEARCH-ember-short-path-chase-visible-2026-10-14.md) · backlog `B-short-path-chase-visible` · **TIP-CV** · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠日差同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠改 resolve 算法**。

> **上游结案：** **TIP-CU CLOSED** live `3e3b14fd1`（D13326 短征日差本可冲齐 · feat `74a2b5a01` · tip `b7e9a5317` · jar `1.65.818-d13326.local` · PASS `c206b7b6f` · 批 sha `52b4ed097` · 软目标3零发奖 · **日差本同题勿再交**）。总控连开：优先**强烈偏向短征日更/乐趣**（真可点或房内可感）；庭内旁路已近饱和（默认勿再交）；宝石须庭面 gap；日差软目标发奖须另批。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **CLOSED live** · feat `c31c780a9` · tip `a206b9893` · PASS `f9e4ca402` · jar `1.65.819-d13327.local` · D13327 · TIP-CV · 2026-10-14 04:33 Asia/Shanghai
**硬规格：** [`DESIGN-ember-short-path-chase-visible-2026-10-14.md`](../design/DESIGN-ember-short-path-chase-visible-2026-10-14.md) · backlog `B-short-path-chase-visible`
**打开理由：** 日差/今日推/周目标冲口已齐后，D503 ShortPath 粘性追猎仍 **chat-only**——选页无入口、无 PAPI、无 `enter short-path`，glance 教裸指令；日更「追哪本」乐趣断在可点动作。

---

## 0. 局势一句话

短征选页补「路径追」——粘性偏好（首通/有奖/后段）菜单可点 + 一键进 resolve，乐趣从单次冲口扩展到可切换追猎路线。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **短征选页路径追可点** | **采纳 · 需策划** | E1–E9；见 §1.1 |
| 2 | Cast/Aid/todayRot/W1e/variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚 CLOSED |
| 3 | 庭内旁路再拧 / 宝石 | **否** | 硬禁 / gap 未亮 |
| 4 | 日差同题 / 软目标发奖 | **否** | 派单禁；经济另批 |
| 5 | 今日推 / 周目标 / 三冲 / D406 复读 | **否** | 已齐勿复读 |
| 6 | 只 Open「去聊天选路径」半行 | **否** | REJECT 保持 |
| 7 | sx20 / 抬表 / densify / 灾厄 / Pack6 | **否** | 硬禁 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CU CLOSED 后连开；强烈偏向短征日更/乐趣；日差同题勿再交；庭内默认勿再交 | 派单 · live `3e3b14fd1` | 选题边界 |
| E2 | 今日推/周目标/日差/三冲冲口已齐 | short/adventure yml | ≠再交同轨 |
| E3 | D406 首通同屏已齐 | fc_left/pending_line | ≠本债 |
| E4 | ShortPath 类注释 Chat buttons only；glance 教 `/corerpg p1 shortpath` | EmberShortPath.java | **chat-only 硬证** |
| E5 | 无 short_path* PAPI；无 enter short-path；选页无路径 Icon；Open 仅刷 shortpath 聊天 | RunPapi · short.yml | **缺口** |
| E6 | resolve(FC/DAY/LATE) live；Layout `'#I H B Z#'` 空位可落 `0` | ShortPath · short.yml | 原料+可落格 |
| E7 | Cast+Aid+todayRot+variety+庭内+日差齐 | D13304–26 | 勿复读主交 |
| E8 | **为何不是半行：** 主交付=薄页真切偏好 + 真 enter short-path | 本 STATUS | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持 | backlog | 否决只口号 |

---

## 2. 荐方案

| 方案 | 一行 | 本窗 |
|------|------|------|
| **M（荐）** | PAPI `short_path_*` + `enter short-path` + 选页 `0` + 薄路径页切偏好/进本 + Open 辅半行；零抬日帽/S/算法 | **荐** |
| L | 仅入口刷聊天无 enter/无薄页切偏好 | **默认不批** |
| A | 只半行 | **否决**（REJECT） |
| W | 抬表/sx20/日差同题/庭内/Cast·Aid·variety再拧 | **否决** |

---

## 3. 派单草案（批 A 后）

```
【派单·内容真债待批A·拟D13327·TIP-CV】短征选页「路径追」可点（ShortPath 菜单化 + enter short-path · ≠日差同题 · ≠今日推/周目标/D406复读 · ≠半行空转 · ≠庭内旁路 · ≠抬表）
优先级：总控 TIP-CU CLOSED 后连开 · 强烈偏向短征日更 · docs-only
请出 DESIGN-ember-short-path-chase-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 链接

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-short-path-chase-visible-2026-10-14.md`](../design/DESIGN-ember-short-path-chase-visible-2026-10-14.md) |
| RESEARCH | [`RESEARCH-ember-short-path-chase-visible-2026-10-14.md`](../design/RESEARCH-ember-short-path-chase-visible-2026-10-14.md) |
| 薄 tip | [`TIP-ember-short-path-chase-visible-thin-d13327-2026-10-14.md`](../tips/TIP-ember-short-path-chase-visible-thin-d13327-2026-10-14.md) |
| backlog | `B-short-path-chase-visible` |
| 上游 | TIP-CU CLOSED live `3e3b14fd1` · feat `74a2b5a01` · tip `b7e9a5317` · PASS `c206b7b6f` · 批 sha `52b4ed097` · D13326；半行 REJECT `d00ed84bc` 保持否决 |

---

*选题短征路径追可点 · tip **CLOSED live** · `c31c780a9` / `a206b9893` · PASS `f9e4ca402` · D13327 · TIP-CV · 半行 REJECT `d00ed84bc` **保持否决** · 路径追同题勿再交。*
