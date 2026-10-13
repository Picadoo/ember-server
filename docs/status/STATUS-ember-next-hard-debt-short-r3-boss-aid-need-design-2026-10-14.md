# 状态 · 下一档硬债选定 · 需策划（拟 D13318 · 短征 R3 Boss 半血轻添头 · docs-only · ≠Cast粒子打磨 · ≠半行 · ≠sx20 · ≠抬日表）

> **旁注（APPROVED · 批 A·M · 已派怪物 · 拟 D13318 · TIP-CM · 2026-10-14 01:17 Asia/Shanghai）：** tip 已交 · DESIGN [`DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md`](../design/DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-r3-boss-aid-readable-2026-10-14.md`](../design/RESEARCH-ember-short-r3-boss-aid-readable-2026-10-14.md) · backlog `B-short-r3-boss-aid-readable` · **TIP-CM** · **≠关观察 ≠抬日表 ≠sx20 ≠开 R/K3 ≠假开旧日常 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠第五件花料 ≠推荐花复读 ≠P格兼确认 ≠庭内再叠顺手 ≠复开 D423 ≠stage 脏 runtime ≠撤分格 ≠教裸指令**。

> **上游结案：** **TIP-CL CLOSED** `95a0d5fe4`（D13317 庭内顺手互换齐 · PASS `86a105307` · menu `4671d3ea4`）。总控连开：优先短征日更花样（真可点/房内可感）；挂机庭环已齐勿再叠；半行 REJECT `d00ed84bc` **保持否决**。

**日期：** 2026-10-14（上海时间）  
**本窗性质：** tip **待批 A** · 荐 **方案 M** · docs-only · 拟 D13318 · **批准前禁改 YAML/Java**  
**硬规格：** [`DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md`](../design/DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md) · backlog `B-short-r3-boss-aid-readable`  
**打开理由：** Cast 波齐后 R3 终厅仍无半血换挡；团本 SummonAid 范式可薄复用；真召唤≠半行≠粒子打磨。

---

## 0. 局势一句话

互换已齐；短征终厅补「半血轻添头」——日更旧本终局可感接上。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| **1** | **短征 R3 Boss 半血轻添头** | **采纳 · 需策划** | E1–E7；见 §1.1 |
| 2 | 再交选页可躲半行 | **否** | REJECT 保持 |
| 3 | Cast 粒子打磨 / 再叠 Cast | **否** | 硬禁；Cast 已齐 |
| 4 | 庭内再叠顺手 / 第五花料 / 推荐花复读 | **否** | 派单禁 / 硬禁 |
| 5 | 只 Open「Boss 会添头」半行 | **否** | 空转硬禁 |
| 6 | sx20 / 抬表 / 灾厄 / Pack6 | **否** | 硬禁 |

### 1.1 证据核验（实扫 · 2026-10-14）

| # | 证据 | 出处 | 含义 |
|---|------|------|------|
| E1 | CLOSED 后优先短征真动作 | `95a0d5fe4` | 过半行/打磨硬禁 |
| E2 | Cast 四档全线 live | D13305/11/14 · BossCast | 再开易触粒子禁 |
| E3 | Warden 无 summon/半血 | EmberSx*.yml | 终厅缺口 |
| E4 | Raid SummonAid live | DiffSkills | 范式可复用 |
| E5 | 挂机庭环齐 | D13304–17 | 勿回资源环 |
| E6 | 半行 REJECT 保持 | `d00ed84bc` | 禁口号独批 |
| E7 | **为何不是半行** | 主交付=真 summon；Open 不可独批 | 过硬禁 |

**玩家价值：** Boss 半血多一只近战——终厅有换挡，日更短本不糊。

**荐方案 M：** MM BossAid ×19 + Warden 挂接；召唤 1×本本 R1；零改 Cast/HP/掉落/日帽。

---

## 2. 派单句

```
【派单·内容真债待批A·拟D13318·TIP-CM】短征 R3 Boss「半血轻添头」（真 summon · ≠Cast粒子打磨 · ≠半行空转 · ≠抬表）
优先级：总控 TIP-CL CLOSED 后连开 · Cast波齐后终厅换挡真债 · docs-only
禁：半行独批·sx20·抬daily_kills/afk.tiers·抬伤表·Pack6·开gate_daily·样本R·天赋/灰印·关观察·开K3·L13·Cast粒子主交付·第五花料·推荐花复读·P格兼确认·庭内再叠顺手·复开D423·重开calamity·教裸指令

请出 DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md，STATUS=待批 A · 荐 M。
交付：只 docs；批准前禁改 YAML/Java；勿 stage 脏 runtime。
```

---

## 3. 本窗不做

不关观察；不开样本 R；不开 K3 live；**不抬**挂机日表/短征日帽/伤表；不 Pack6/天赋/灰印；假开旧日常；**不交 sx20**；不开 L13 施工；**不以半行独批当主交付**；**不以 Cast 粒子打磨当主交付**；**不复开 D423**；不教裸指令；不重开 calamity；不新开 fest；**不叠挂机庭第五件花料/再叠顺手**；**不复读推荐花**；**不交 P 格兼确认**；**本 tip 零代码、且不得 stage 脏 runtime**。

---

## 4. 文件指针

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-short-r3-boss-aid-readable-2026-10-14.md` |
| DESIGN | `docs/design/DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md` |
| tip（本文件） | `docs/status/STATUS-ember-next-hard-debt-short-r3-boss-aid-need-design-2026-10-14.md` |
| 薄 tip | `docs/tips/TIP-ember-short-r3-boss-aid-readable-thin-d13318-2026-10-14.md` |
| backlog | `B-short-r3-boss-aid-readable` |
| 号段 | **拟 D13318** · 字母窗 **TIP-CM** |
| 上游 | TIP-CL CLOSED `95a0d5fe4` · D13317；半行 REJECT `d00ed84bc` 保持否决 |

---

*选题短征 R3 半血轻添头 · tip **待批 A** · 荐 M · 拟 D13318 · TIP-CM · ≠关观察 ≠抬日表 ≠sx20 ≠半行空转 ≠Cast粒子主交付。*
