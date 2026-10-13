# RESEARCH · 短征 R2 Elite 半血轻添头（短本玩法花样续 · ≠BossAid终厅复读 · ≠Cast粒子打磨 · ≠半行 · ≠sx20 · ≠抬表）· 2026-10-14

> **状态：** **待批 A** · 荐 **方案 M** · 拟 D13319 · TIP-CN · docs-only · 2026-10-14 01:35 Asia/Shanghai  
> **起因：** 总控【连开·TIP-CN / 拟 D13319】上游 TIP-CM CLOSED live `3edadfb12`（tip `ee36f5af9` · PASS `84448394a` · BossAid×19 半血添头齐）。请立刻交下一档硬债 docs-only 待批 A。**优先短征日更花样 / 乐趣系统**（真可点或房内可感）；挂机庭环已齐勿再叠；Cast 波+BossAid 已齐 · 勿以 Cast 粒子二次打磨当主交付。  
> **上游：** TIP-CM CLOSED live `3edadfb12` · D13318；四花+推荐花+拆+换齐；今日推/冲一下/周短征/三冲齐；Cast 全波 + BossAid 齐；半行 REJECT `d00ed84bc` **保持否决**  
> **硬禁：** **零** 半行独批 · **零** sx20 · **零** L13 · **零** K3 live · **零** Pack6/灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/`short:5`/伤乘 · **零** Affix densify · **零** P 兼确认 · **零** 第五花料 · **零** 推荐花复读 · **零** Cast 粒子当主交付 · **零** 假开旧日常 · **零** 教裸指令 · **零** 庭内再叠同类顺手无新证据 · **零** 灾厄重开 · **零** 复开 D423 · **零** stage 脏 runtime

---

## 0. 一句话

短征 **R3 终厅 BossAid 已齐**，但 **R2 中房 Elite 仍是单 EliteCast 循环**——无半血换挡。D13318§6② 明文后置「Elite 半血添头」。本债=全线 sx01–19 Elite **半血轻添头 1 只本本 R1 近战**（真召唤 · 房内必须先清），**不是**再叠 Cast 粒子，**不是**选页半行，**不是**终厅 BossAid 复读。

---

## 1. 选题扫描（TIP-CM 后 · 必须真动作 · 优先短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| 庭内再叠镜像顺手（洗炼/reroll 等） | **否** | 派单明文：资源环已齐，无新硬债勿叠 |
| 选页「可躲」半行再交 | **否** | REJECT `d00ed84bc` 保持否决 |
| Guard/Elite/Archer/Boss Cast 粒子二次打磨 | **否** | Cast 粒子当主交付硬禁；梯队已齐 |
| BossAid 再拧 / Boss 半血第二 Cast | **否** | BossAid 刚 CLOSED；易触复读/打磨 |
| P 兼确认 / 第五花料 / 推荐花复读 | **否** | 硬禁 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 挂机庭资源环新键（无新证据） | **否** | 派单优先别回资源环 |
| **短征 R2 Elite 半血轻添头（荐）** | **采纳** | D13318§6②；Elite 扫线无 summon；真召唤=房内可感；补三房弧中段；≠半行；≠粒子打磨；≠终厅复读 |

**择优结论：** 挂机庭环已齐；半行禁；Cast/BossAid 再开易触打磨/复读。扫线发现 **sx01–19 Elite Skills = SoftHit + EliteCast only，无 summon/半血**（HP120）——与刚 live 的 BossAid 落差即中段真债。故交短征 R2 Elite 半血轻添头；**证据表见 §2**；**为何不是半行见 E7**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CM CLOSED · 连开 TIP-CN · **首选短征日更花样** · 禁半行/Cast粒子主交付/庭内再叠 · Cast+BossAid 已齐勿打磨主交 | `3edadfb12` | 选题边界 |
| E2 | D13318§6② 明文后置「Elite 半血添头」 | DESIGN-ember-short-r3-boss-aid-readable | 合法接力 |
| E3 | GuardCast / ArcherCast / EliteCast / BossCast / **BossAid** **全线 live** | MM · DiffSkills · D13305/11/14/18 | Cast+终厅添头齐；再开终厅=复读 |
| E4 | sx01–19 `EmberSxNNElite` Skills：**仅** SoftHit + EliteCast `~onTimer:100`；**无** summon / onDamaged / 半血；**HP=120** | `plugins/MythicMobs/Mobs/EmberSx*.yml` | 中房无换挡 |
| E5 | BossAid 范式：`health{h=<120}` + CD9999 + summon 1×R1 + Warden `~onDamaged` live | DiffSkills · tip `ee36f5af9` | 可薄复用到 Elite（阈改 `h=<60`） |
| E6 | 挂机庭四花+P+D+W 齐；导航今日推/冲一下/周短征/三冲齐 | backlog · tip live | 勿回资源环/导航复读 |
| E7 | 半行 REJECT 保持 | `d00ed84bc` | 禁入口口号独批 |
| E8 | **为何不是半行：** 主交付=**新 MM 技能 + Elite 挂接** → 半血触发 **真 summon 1 只本本 R1 近战**（房内出现新敌、须先清）；玩家感知=中房换挡，**不是** lore/Open 口号 | 本 RESEARCH | 过硬禁 |

**一句话缺口：** 短征打到中房 Elite 只会躲同一条 EliteCast——终厅已有添头，中段仍「平循环」；日更旧本三房弧断在 R2 换挡。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **MM Skills：** sx01–19 各增 `EmberSxNNEliteAid`：半血 `health{h=<60}` + **Cooldown 9999（一场一次）** + 喊话「精英添头——先清！」+ `summon{mob=<本本R1近战>;amount=1;noise=3}` + 轻粒子（辅，**非主交付**）  
2. **R1 近战键钉死：** 对齐 BossAid（sx01 Zombie · sx02 Guard · sx03 Patrol · sx04–19 Guard）  
3. **MM Mobs：** `EmberSxNNElite` Skills **追加一行**挂 EliteAid（荐 `~onDamaged`；fallback `~onTimer:120`+超大 CD 须记 STATUS）  
4. **零改** EliteCast 伤/Timer · HP/掉落/日帽/地图 · SoftHit · Guard/Archer/Boss Cast · **BossAid/Warden**  
5. **施工岗：** 批后 **余烬-怪物** · MM-only · 默认不派插件/物品/地图

---

## 4. 明确不做

- 只改选页/Open「Elite 会添头」半行无 summon · Cast 粒子二次打磨当主交付 · 再叠第四档 Cast · 拧 BossAid/Warden  
- 庭内再叠顺手 · 第五花料 · P 兼确认 · 抬产/抬伤表 · sx20 · Pack6/灰印/R/L13/K3  
- 重开 calamity · 复开 D423 · 教裸指令 · 假开旧日常 · 一次召唤≥2 / 抬小兵掉落当主交付  

## STATUS: **待批 A** · 荐 **方案 M** · 拟 D13319 · TIP-CN · docs-only · 2026-10-14 01:35 Asia/Shanghai
