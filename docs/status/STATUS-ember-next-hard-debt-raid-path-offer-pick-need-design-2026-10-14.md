# 状态 · 下一档硬债选定 · 需策划（拟 D13338 · RaidPath.offerPick / applyAndReply 团本路径选打菜单化 · 选路径聊天钮去指令化 · docs-only · ≠硬本路径选打同题 · ≠精选路径选打同题 · ≠团本settle下一环同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠sx20 · ≠抬掉落/体力价/周帽 · ≠庭内旁路 · ≠Cast/Aid/todayRot再拧 · ≠全族 *Path.offerPick 大扫 · ≠FeaturedPath/ChallengePath同窗）

> **旁注（勾毕 · CLOSED live · 2026-10-14 11:29 Asia/Shanghai · feat `c81c9e7e2` · tip `fd80fd696` · PASS `7ec45e0a1` · jar `1.65.831-d13338.local` · 批 sha `fd80fd696` · menu tip `b9b8f688f` · 证据 `/workspace/tmp/tip-dg-raid-path/`）：** tip **CLOSED live** · DESIGN [`DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md) · RESEARCH [`RESEARCH-ember-raid-path-offer-pick-visible-2026-10-14.md`](../design/RESEARCH-ember-raid-path-offer-pick-visible-2026-10-14.md) · backlog `B-raid-path-offer-pick-visible` · **TIP-DG** · **团本路径选打菜单化同题勿再交** · 下一窗：内容优先（挂机若新硬债 / 短征若新薄债 / 其他菜单去指令残留如试炼/签律/步态等 *Path.offerPick 另窗逐支 / legacy evacuate 另窗；团本 settle+选打本支已齐） · **≠关观察 ≠抬日表 ≠sx20 ≠半行独批 ≠Cast/Aid/variety再拧 ≠硬本路径选打同题 ≠精选路径选打同题 ≠团本settle下一环同题 ≠已齐 settle 下一环同题 ≠短征settle回看同题 ≠soft-reward同题 ≠庭内旁路再拧 ≠教裸指令 ≠densify ≠抬掉落/体力价/周帽 ≠D673同题复读 ≠legacy evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠FeaturedPath/ChallengePath同窗**。

> **上游结案：** **TIP-DF CLOSED** live（D13337 硬本路径选打齐 · feat `5927286ca` · tip `17d0f7ddf` · jar `1.65.830-d13337.local` · PASS `6d98cb62a` · 批 sha `17d0f7ddf` · menu tip `bebeb7f17` · **硬本路径选打菜单化同题勿再交**）。总控派 TIP-DG：内容优先；挂机加厚须新硬债；短征乐趣非已齐路径；已齐勿复读 settle CW–DD + 精选选打 DE + 硬本选打 DF；**默认钉** RaidPath.offerPick；legacy evacuate 默认后置。半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）
**本窗性质：** tip **CLOSED live** · feat `c81c9e7e2` · tip `fd80fd696` · PASS `7ec45e0a1` · jar `1.65.831-d13338.local` · 拟 D13338 · TIP-DG · 2026-10-14 11:29 Asia/Shanghai · 团本路径选打菜单化同题勿再交
**硬规格：** [`DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md) · backlog `B-raid-path-offer-pick-visible`
**打开理由：** 挂机/短征无新薄债；各 settle 下一环+精选·硬本选打已齐；团本**选打**仍 ConfirmTokens 借 settle——DESIGN-DF §6⑦ 明文后置；内容闭环断在团本路径选打（≠结算面；≠硬本/精选选打）。

---

## 0. 局势一句话

挂机近饱和、短征薄债已齐、settle/精选·硬本选打已齐；补 **RaidPath.offerPick / applyAndReply 选打薄页真可点**——**不是**团本 settle 同题复读，**不是**硬本/精选选打同题，**不是**半行，**不是**全族 Path 大扫。

---

## 1. 选题

| # | 候选 | 结论 | 理由 |
|---|------|------|------|
| 1 | 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 2 | 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 3 | 已齐 settle / 精选选打 TIP-DE / 硬本选打 TIP-DF | **否** | 派单禁；刚 CLOSED |
| 4 | soft-reward / 各 settle 下一环 | **否** | 派单禁；已 CLOSED |
| 5 | legacy evacuate | **后置** | 另窗证据 |
| 6 | **RaidPath.offerPick 选打菜单化** | **是** | DESIGN-DF §6⑦；真可点；镜像 TIP-DE/DF |

---

## 2. 证据要点

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债已齐；sx20 禁 | tips | **短征无新硬债** |
| E4 | DESIGN-DF §6⑦；settle+精选·硬本选打已齐 | DESIGN-DF §6 | **合法未 CLOSED 后置** |
| E5 | offerPick ConfirmTokens→raid_settle；applyAndReply→settle；冒险 Open raidpath；无 `ember_p1_raid_path` | Java · menus | **硬债** |
| E8 | ≠ 团本settle / 硬本选打 / 精选选打；≠ 全族 Path | 本 STATUS | 边界 |
| E9 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 STATUS | 过硬禁 |

---

## 3. 派单摘要（已批 A·M · 可施工）

```
【派单·内容真债待批A·拟D13338·TIP-DG】RaidPath.offerPick/applyAndReply团本路径选打菜单化（真可点 · ≠团本settle同题 · ≠硬本选打同题 · ≠精选选打同题 · ≠已齐settle同题 · ≠半行空转 · ≠全族Path大扫）
优先级：总控 TIP-DF CLOSED 后连开 · 内容乐趣 · docs-only→批后施工
请出 DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md，STATUS=待批 A · 荐 M。
```

---

## 4. 交付索引

| 项 | 路径 |
|----|------|
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-raid-path-offer-pick-need-design-2026-10-14.md` |
| DESIGN | `docs/design/DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md` |
| RESEARCH | `docs/design/RESEARCH-ember-raid-path-offer-pick-visible-2026-10-14.md` |
| 薄 tip | `docs/tips/TIP-ember-raid-path-offer-pick-visible-thin-d13338-2026-10-14.md` |
| backlog | `B-raid-path-offer-pick-visible` |
| 号段 | **D13338** · 字母窗 **TIP-DG** · STATUS=**CLOSED live** · PASS `7ec45e0a1` |
| 上游 | TIP-DF CLOSED live `6b1fc942d` · feat `5927286ca` · tip `17d0f7ddf` · PASS `6d98cb62a` · jar `1.65.830-d13337.local` · 批 sha `17d0f7ddf` · D13337；半行 REJECT `d00ed84bc` 保持否决；硬本路径选打菜单化同题勿再交 |

*选题 RaidPath.offerPick / applyAndReply 团本路径选打菜单化 · tip **CLOSED live** · `c81c9e7e2` / `fd80fd696` · PASS `7ec45e0a1` · D13338 · TIP-DG · 半行 REJECT `d00ed84bc` **保持否决** · 团本路径选打菜单化同题勿再交 · 下一窗：内容优先（挂机若新硬债 / 短征若新薄债 / 其他菜单去指令残留如试炼/签律/步态等 *Path.offerPick 另窗逐支 / legacy evacuate 另窗；团本 settle+选打本支已齐） · ancestor TIP-DF CLOSED · 2026-10-14 11:29 Asia/Shanghai.*

## STATUS: **CLOSED live** · feat `c81c9e7e2` · tip `fd80fd696` · PASS `7ec45e0a1` · jar `1.65.831-d13338.local` · D13338 · TIP-DG · 2026-10-14 11:29 Asia/Shanghai · 团本路径选打菜单化同题勿再交
