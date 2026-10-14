# 状态 · 下一档硬债选定 · 需策划（拟 D13366 · BoltPath.offerPick / applyAndReply 闪线路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠侧安路径选打同题 · ≠收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D589 玩法本体）

> **上游结案：** **TIP-EH CLOSED** live（D13365 侧安路径选打齐 · feat `65ae19a37` · tip `b42ce4a52` · jar `1.65.858-d13365.local` · PASS `cff0d78d0` · 批 sha `b42ce4a52` · menu tip `f2c690343` · CLOSED `e4bccf93b` · 证据 `/workspace/tmp/tip-eh-shift-path/` · **侧安路径选打菜单化同题勿再交**）。总控派下一窗：只钉 **BoltPath.offerPick**；挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–EH 选打勿复；PacePath（本仓无）后置；其它剩支 D590+（Crash…）后置；禁全族大扫；禁与其它 *Path 同窗；半行 REJECT `d00ed84bc` 保持。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip thin · **已批 A** · 方案 M · APPROVED A·M · tip `b38e143b4` · 2026-10-14 19:50 Asia/Shanghai · 勿勾 CLOSED · 可施工
**硬规格：** [`DESIGN-ember-bolt-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-bolt-path-offer-pick-visible-2026-10-14.md) · backlog `B-bolt-path-offer-pick-visible`
**打开理由：** 挂机/短征无新薄债；各 settle 下一环+选打 DE–EH 已齐；闪线**选打**仍 ConfirmTokens 教裸 boltpath——总控钉死本支；内容闭环断在闪线路径选打（≠侧安选打；≠ settle 同题；局内闪线钮≠选打页）。

---

## 0. 局势一句话

挂机近饱和、短征薄债+选打已齐、settle/选打 DE–EH 已齐；补 **BoltPath.offerPick / applyAndReply 选打薄页真可点**——**不是**侧安选打同题复读，**不是**半行，**不是**全族 Path 大扫，**不是**复开 D589。

---

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 2 | 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 3 | 已齐 settle / 选打 DE–EH | **否** | 派单禁；刚 CLOSED |
| 4 | soft-reward / 各 settle 下一环 | **否** | 派单禁；已 CLOSED |
| 5 | PacePath  / 其它剩支 等 *Path | **后置** | Pace 不存在；D590+（Crash…）后置；禁全族大扫；逐支另窗 |
| 6 | legacy evacuate | **后置** | 另窗证据 |
| 7 | **BoltPath.offerPick 选打菜单化** | **是** | 总控钉死；ConfirmTokens 硬债；真可点；镜像 TIP-DE–EH |

---

## 2. 证据要点

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打已齐；sx20 禁 | tips | **短征无新硬债** |
| E4 | 总控钉死 Bolt；settle+选打 DE–EH 已齐 | 派单 · tips | **合法未 CLOSED 后置** |
| E5 | offerPick ConfirmTokens→boltpath；applyAndReply→p1/boltpath；无参/周要约/Q01；无 bolt_path；局内闪线钮≠选打页 | Java · menus | **硬债（本支）** |
| E8 | ≠ 侧安选打 / 收招·半血·增援·分摊·铁壁·分身·压伤·打断·限时·净伤·连斩·避焰·避旋·断链·避毒·解锢·闪榴·闪冲·身法·轻技·破绽·誓约·试炼·深渊·短征·团本·硬本·精选·步态选打；≠ 全族 Path；≠ 其它 *Path 同窗 | 本 STATUS | 边界 |
| E9 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |

---

## 3. 派单摘要（待批 A · 荐 M）

```
【派单·内容真债待批A·拟D13366·TIP-EI】BoltPath.offerPick/applyAndReply闪线路径选打菜单化（真可点 · ≠侧安选打同题 · ≠收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题 · ≠已齐settle同题 · ≠半行空转 · ≠全族Path大扫 · ≠Pace同窗 · ≠其它*Path同窗 · ≠复开D589）
优先级：总控 TIP-EH CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-bolt-path-offer-pick-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 交付索引

| 项 | 路径 |
|----|------|
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-bolt-path-offer-pick-need-design-2026-10-14.md` |
| DESIGN | `docs/design/DESIGN-ember-bolt-path-offer-pick-visible-2026-10-14.md` |
| RESEARCH | `docs/design/RESEARCH-ember-bolt-path-offer-pick-visible-2026-10-14.md` |
| 薄 tip | `docs/tips/TIP-ember-shell-path-offer-pick-visible-thin-d13360-2026-10-14.md` |
| backlog | `B-bolt-path-offer-pick-visible` |
| 号段 | **D13366** · 字母窗 **TIP-EI** · STATUS=**已批 A** · 方案 M · APPROVED A·M · tip `b38e143b4` · 2026-10-14 19:50 Asia/Shanghai · 勿勾 CLOSED
| 上游 | TIP-EH CLOSED live `e4bccf93b` · feat `65ae19a37` · tip `b42ce4a52` · PASS `cff0d78d0` · jar `1.65.858-d13365.local` · 批 sha `b42ce4a52` · menu tip `f2c690343` · D13365；半行 REJECT `d00ed84bc` 保持否决；侧安路径选打菜单化同题勿再交 |

*选题 BoltPath.offerPick / applyAndReply 闪线路径选打菜单化 · tip **待批 A** · 荐 M · 勿勾 CLOSED · L不批 · D13366 · TIP-EI · 半行 REJECT `d00ed84bc` **保持否决** · 其它剩支 D590+（Crash…） / PacePath（本仓无）/挂机若新硬债/legacy evacuate 后置 · ancestor TIP-EH CLOSED `e4bccf93b` · 2026-10-14 19:48 Asia/Shanghai.*

## STATUS: **待批 A** · 荐 M · 勿勾 CLOSED · L不批 · D13366 · TIP-EI · 2026-10-14 19:48 Asia/Shanghai
