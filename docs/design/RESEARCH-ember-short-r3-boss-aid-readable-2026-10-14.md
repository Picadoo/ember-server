# RESEARCH · 短征 R3 Boss 半血轻添头（短本玩法花样续 · ≠Cast粒子打磨 · ≠半行 · ≠sx20 · ≠抬表）· 2026-10-14

> **状态：** **已批 A · 方案 M · 全线非 L** · 批 sha `661e73acf` · 拟 D13318 · TIP-CM · 已派怪物 · **勿勾 CLOSED** · fallback `~onTimer:160` 须进 STATUS/验收 · 2026-10-14 01:17 Asia/Shanghai  
> **起因：** 总控【TIP-CL CLOSED · 开下一 tip】收口 `95a0d5fe4` · live menu `4671d3ea4` · PASS `86a105307` · D13317 庭内顺手互换齐。请交待批 A · 荐 M · docs-only（拟 D13318 / TIP-CM）。**优先短征日更花样**（真可点/房内可感新机制 · ≠半行 · ≠Cast 粒子打磨主交付）；挂机庭资源环已齐——**勿再叠庭内镜像顺手**（无新硬债证据）。  
> **上游：** TIP-CL CLOSED live `95a0d5fe4` · D13317；四花+推荐花+拆+换齐；今日推/冲一下/周短征/三冲齐；R1 近战/远程 Cast · R2 EliteCast · R3 BossCast 齐；半行 REJECT `d00ed84bc` **保持否决**  
> **硬禁：** **零** 半行独批 · **零** sx20 · **零** L13 · **零** K3 live · **零** Pack6/灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/`short:5`/伤乘 · **零** Affix densify · **零** P 兼确认 · **零** 第五花料 · **零** 推荐花复读 · **零** Cast 粒子当主交付 · **零** 假开旧日常 · **零** 教裸指令 · **零** 庭内再叠同类顺手无新证据 · **零** 灾厄重开 · **零** 复开 D423

---

## 0. 一句话

短征三房 **Cast 读条已齐**，但 R3 Boss 终厅仍是「单招循环」——**无半血换挡、无添头**；团本 `EmberRaidSummonAid` 范式已在盘。本债=全线 sx01–19 Boss **半血轻添头 1 只本本 R1 近战**（真召唤 · 房内必须先清），**不是**再叠 Cast 粒子，**不是**选页半行。

---

## 1. 选题扫描（TIP-CL 后 · 必须真动作 · 优先短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| 庭内再叠镜像顺手（洗炼/reroll 等） | **否** | 派单明文：资源环已齐，无新硬债勿叠 |
| 选页「可躲」半行再交 | **否** | REJECT `d00ed84bc` 保持否决 |
| Guard/Elite/Archer/Boss Cast 粒子二次打磨 | **否** | Cast 粒子当主交付硬禁；梯队已齐 |
| 再叠新 SoftHit / 第四档 Cast | **否** | 等同粒子打磨族；无新机制类别 |
| P 兼确认 / 第五花料 / 推荐花复读 | **否** | 硬禁 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 挂机庭资源环新键（无新证据） | **否** | 派单优先别回资源环 |
| **短征 R3 Boss 半血轻添头（荐）** | **采纳** | Cast 齐后终厅仍缺「换挡」；Raid SummonAid 可薄复用；**真召唤=房内可感新机制**；≠半行；≠粒子打磨 |

**择优结论：** 挂机庭环已齐；半行禁；Cast 再开易触粒子硬禁。扫线发现 **sx01–19 Warden Skills = SoftHit + BossCast only，无 summon/半血**——与团本终厅添头范式落差即真债。故交短征 R3 半血轻添头；**证据表见 §2**；**为何不是半行见 E7**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CL CLOSED · 连开 TIP-CM · **首选短征日更花样** · 禁半行/Cast粒子主交付/庭内再叠 | `95a0d5fe4` | 选题边界 |
| E2 | GuardCast / ArcherCast / EliteCast / BossCast **全线 live** | MM EmberSx* · DiffSkills · D13305/11/14 | Cast 波齐；再开=打磨族 |
| E3 | sx01–19 `EmberSxNNWarden` Skills：**仅** SoftHit + 主题 BossCast `~onTimer:70`；**无** summon / onDamaged / 半血条件 | `plugins/MythicMobs/Mobs/EmberSx*.yml` | 终厅无换挡 |
| E4 | 团本 `EmberRaidSummonAid`：message + `summon{mob=…;amount=1}` live | `EmberDungeonDiffSkills.yml` | 添头范式可薄复用 |
| E5 | 挂机庭四花+P+D+W 齐；导航今日推/冲一下/周短征/三冲齐 | backlog · tip live | 勿回资源环/导航复读 |
| E6 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 禁入口口号独批 |
| E7 | **为何不是半行：** 主交付=**新 MM 技能 + Warden 挂接** → 半血触发 **真 summon 1 只本本 R1 近战**（房内出现新敌、须先清）；玩家感知=终厅换挡，**不是** lore/Open 口号 | 本 RESEARCH | 过硬禁 |

**一句话缺口：** 短征打到 Boss 只会躲同一条 Cast——团本有添头，短征终厅「平循环」；日更旧本可感断在 R3 换挡。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **MM Skills：** sx01–19 各增 `EmberSxNNBossAid`（或并列档）：半血条件 + **Cooldown 极大（一场一次）** + 喊话「半血添头——先清！」+ `summon{mob=<本本R1近战>;amount=1;noise=3}` + 轻粒子（辅，**非主交付**）  
2. **R1 近战键钉死：** sx01=`EmberSx01Zombie` · sx02=`EmberSx02Guard` · sx03=`EmberSx03Patrol` · sx04–19=`EmberSxNNGuard`  
3. **MM Mobs：** `EmberSxNNWarden` Skills **追加一行**挂 BossAid（荐 `~onDamaged`；若环境半血条件不稳则 fallback `~onTimer:160`+超大 CD=中段一次，文案仍「添头」）  
4. **零改** BossCast 伤/Timer · HP/掉落/日帽/地图 · SoftHit · Guard/Archer/Elite Cast  
5. **施工岗：** 批后 **余烬-怪物** · MM-only · 默认不派插件/物品/地图

---

## 4. 明确不做

- 只改选页/Open「Boss 会添头」半行无 summon · Cast 粒子二次打磨当主交付 · 再叠第四档 Cast  
- 庭内再叠顺手 · 第五花料 · P 兼确认 · 抬产/抬伤表 · sx20 · Pack6/灰印/R/L13/K3  
- 重开 calamity · 复开 D423 · 教裸指令 · 假开旧日常 · 一次召唤≥2 / 抬小兵掉落当主交付  

## STATUS: **已批 A · 方案 M · 全线非 L** · 批 sha `661e73acf` · 拟 D13318 · TIP-CM · 已派怪物 · **勿勾 CLOSED** · fallback `~onTimer:160` 须进 STATUS/验收 · 2026-10-14 01:17 Asia/Shanghai
