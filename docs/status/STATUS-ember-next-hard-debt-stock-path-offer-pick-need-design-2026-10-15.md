# 状态 · 下一档硬债选定 · 需策划（拟 D13376 · StockPath.offerPick / applyAndReply 备药路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠急清路径选打同题 · ≠集火/灾临/卡点/终救/续招/诱空/闪招/撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D599 玩法本体）

> **上游结案：** **TIP-ER 已批 A·M + PASS live**（D13375 急清路径选打 · feat `f7fc5125f` · tip批 `4e0e9c6d2` · menu `5a4e37268` · jar `1.65.868-d13375.local` · PASS `dfe0e4724` · 96/96 · 证据 `/workspace/tmp/tip-er-hurry-path/` · **急清路径选打菜单化同题勿再交** · 待总控 CLOSED live）。总控派下一窗：只钉 **StockPath.offerPick**；挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–ER 选打勿复；PacePath（本仓无）后置；本窗只钉 Stock 一支 · Alert D600+ 等另窗证据逐支（禁全族大扫 · 禁与 Alert 等同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置后置；禁全族大扫；禁与其它 *Path 同窗；半行 REJECT `d00ed84bc` 保持。

**日期：** 2026-10-15（上海时间）
**本窗性质：** tip thin · **CLOSED live** · feat `e1b2553d4` · tip `2ab321047` · menu `ca133c931` · jar `1.65.869-d13376.local` · PASS `1201f5d52` · 2026-10-15 09:28 Asia/Shanghai · 备药路径选打菜单化同题勿再交 · Alert D600+ 另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置
**硬规格：** [`DESIGN-ember-stock-path-offer-pick-visible-2026-10-15.md`](../design/DESIGN-ember-stock-path-offer-pick-visible-2026-10-15.md) · backlog `B-stock-path-offer-pick-visible`
**打开理由：** 挂机/短征无新薄债；各 settle 下一环+选打 DE–ER 已齐（急清 PASS·待 CLOSED）；备药**选打**仍 ConfirmTokens 教裸 stockpath——总控钉死本支；内容闭环断在备药路径选打（≠急清选打；≠ settle 同题；局内备药钮≠选打页）。

---

## 0. 局势一句话

挂机近饱和、短征薄债+选打已齐、settle/选打 DE–ER 已齐（急清 PASS·待 CLOSED）；补 **StockPath.offerPick / applyAndReply 选打薄页真可点**——**不是**急清选打同题复读，**不是**半行，**不是**全族 Path 大扫，**不是**复开 D599。

---

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 2 | 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 3 | 已齐 settle / 选打 DE–ER | **否** | 派单禁；刚 CLOSED |
| 4 | soft-reward / 各 settle 下一环 | **否** | 派单禁；已 CLOSED |
| 5 | PacePath  / 其它剩支 等 *Path | **后置** | Pace 不存在；Focus D599+ 等另窗证据逐支（禁全族大扫后置；禁全族大扫；逐支另窗 |
| 6 | legacy evacuate | **后置** | 另窗证据 |
| 7 | **StockPath.offerPick 选打菜单化** | **是** | 总控钉死；ConfirmTokens 硬债；真可点；镜像 TIP-DE–ER |

---

## 2. 证据要点

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打已齐；sx20 禁 | tips | **短征无新硬债** |
| E4 | 总控钉死 Sequel；settle+选打 DE–ER 已齐（急清 PASS·待 CLOSED） | 派单 · tips | **合法未 CLOSED 后置** |
| E5 | offerPick ConfirmTokens→stockpath；applyAndReply→p1/stockpath；无参/周要约/Q01；无 stock_path；局内备药钮≠选打页 | Java · menus | **硬债（本支）** |
| E8 | ≠ 诱空选打 / 闪招·撞墙·闪线·侧安·收招·半血·增援·分摊·铁壁·分身·压伤·打断·限时·净伤·连斩·避焰·避旋·断链·避毒·解锢·闪榴·闪冲·身法·轻技·破绽·誓约·试炼·深渊·短征·团本·硬本·精选·步态选打；≠ 全族 Path；≠ 其它 *Path 同窗 | 本 STATUS | 边界 |
| E9 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |

---

## 3. 派单摘要（待批 A · 荐 M）

```
【派单·内容真债待批A·拟D13376·TIP-ES】StockPath.offerPick/applyAndReply备药路径选打菜单化（真可点 · ≠急清选打同题 · ≠集火/灾临/卡点/终救/续招/诱空/闪招/撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题 · ≠已齐settle同题 · ≠半行空转 · ≠全族Path大扫 · ≠Pace同窗 · ≠其它*Path同窗 · ≠复开D599）
优先级：总控 TIP-ER CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-stock-path-offer-pick-visible-2026-10-15.md，STATUS=待批 A · 荐 M。
```

---

## 4. 交付索引

| 项 | 路径 |
|----|------|
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-stock-path-offer-pick-need-design-2026-10-15.md` |
| DESIGN | `docs/design/DESIGN-ember-stock-path-offer-pick-visible-2026-10-15.md` |
| RESEARCH | `docs/design/RESEARCH-ember-stock-path-offer-pick-visible-2026-10-15.md` |
| 薄 tip | `docs/tips/TIP-ember-stock-path-offer-pick-visible-thin-d13376-2026-10-15.md` |
| backlog | `B-stock-path-offer-pick-visible` |
| 号段 | **D13376** · 字母窗 **TIP-ES** · STATUS=**CLOSED live** · feat `e1b2553d4` · tip `2ab321047` · menu `ca133c931` · jar `1.65.869-d13376.local` · PASS `1201f5d52` · 2026-10-15 09:28 Asia/Shanghai · 备药路径选打菜单化同题勿再交 · Alert D600+ 另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 |
| 上游 | TIP-ER PASS live `dfe0e4724` · feat `f7fc5125f` · tip `4e0e9c6d2` · PASS `dfe0e4724` · jar `1.65.868-d13375.local` · 批 sha `4e0e9c6d2` · menu tip `5a4e37268` · D13375；半行 REJECT `d00ed84bc` 保持否决；急清路径选打菜单化同题勿再交 |

*选题 StockPath.offerPick / applyAndReply 备药路径选打菜单化 · tip **CLOSED live** · feat `e1b2553d4` · tip `2ab321047` · menu `ca133c931` · jar `1.65.869-d13376.local` · PASS `1201f5d52` · 2026-10-15 09:28 Asia/Shanghai · 备药路径选打菜单化同题勿再交 · Alert D600+ 另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 · L不批 · D13376 · TIP-ES · 半行 REJECT `d00ed84bc` **保持否决** · Alert D600+ 另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-ER CLOSED `dc6838c45`.*

## STATUS: **CLOSED live** · feat `e1b2553d4` · tip `2ab321047` · menu `ca133c931` · jar `1.65.869-d13376.local` · PASS `1201f5d52` · 2026-10-15 09:28 Asia/Shanghai · 备药路径选打菜单化同题勿再交 · Alert D600+ 另窗证据逐支（禁全族大扫）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 · D13376 · TIP-ES
