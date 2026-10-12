# RESEARCH · 签名盖章软保底「连空 n/N」菜单诚实（离开烙纹直办链 · SUR3）· 2026-10-13

> **状态：** APPROVED · TIP-BW · 批 A·M · 已派余烬-插件 + 余烬-物品 · 2026-10-13 01:06 Asia/Shanghai  
> **起因：** TIP-BV CLOSED live 后总控「离开烙纹直办链 · 开下一可感变强途径」。硬禁含烙纹直办再叠 · forge/洗练 sure·keep·预览直办半行族 · Affix densify · STAMP densify。扫 AH…BV / power-paths / BV 旁债 #14：**SigStampPity/Dry 已 live（结算/glance「连空 n/N · 软保底已满 · 下次必出盖章候选」）**，签名/烙印菜单仍写「连空局数见结算提示」——**无 n/N 一眼**。  
> **上游：** TIP-BV CLOSED live items `9bac5aef8` · docs `dda74b0d5` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · `EmberSigStampPity` · `EmberSigStampDry.glance` · `ember_p1_sig.yml` / `ember_p1_sig_imprint.yml` · power-paths SUR3 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零抬 STAMP_RATE/C12 · 零扩 stampable · 零 SigStampPity densify（禁改 need/force 数学）· 零伤乘 · 零抬表 · 零 Affix densify · 零改解锁数学 · 零 ConvertCap densify · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 Anvil densify / soft·开孔再叠 / topUpTo · 零 SF4 U2U3 · 零灰印·聚火 · 零签名 lore（效果持件）再叠 · 零套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定 / 洗练直留 / 烙纹直办再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 forgeroll 权重 densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3 · 零改 pity/pool/craftShards · 零发明第二套 dry 计数器 / celebration**

---

## 0. 一句话

签名页一眼看见 **连空 n/N（或软保底已满）**，对齐 `EmberSigStampDry.glance`；不抬掉率、不改保底数学。

---

## 1. 选题扫描（离开烙纹直办 + AH…BV 已齐诚实族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **签名盖章软保底「连空 n/N」菜单诚实** | **采纳** | BV 旁债 #14；离开 sure/keep/预览直办半行族；SoT glance 已有 · 菜单无；SUR3 表面化；**非** STAMP densify / 非签名效果 lore 再叠 |
| 烙纹直办 / forge EQRUS / 洗练直留 / 互换 / 成色精工确定成功再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| forgeroll 普通格成色权重半行 | **否** | 赌线 · 禁 ForgeChoice densify 边 |
| 分解菜单「确定」字面 | **否** | 弱 · 已有 yield 预览 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 |
| hub/raid/abyss access 扫尾 | **否** | access_* 再叠硬禁 |
| 主线 Q methodLine / hope glance 再叠 | **否** | MG/HD 已齐或 TIP-J 停窗 |
| cook/AFK/attune soft / life-buy / supply 再叠 | **否** | 链已齐或硬禁 |
| 签名效果 lore / 套装 blurb / 宝石 / 天赋 / 烙纹档幅度 / 副手再叠 | **否** | 硬禁（效果 lore≠盖章 pity 展示） |
| Anvil densify / soft·开孔 / SF4 U2U3 | **否** | 硬禁 |
| 灰印 / 聚火 / L13 施工 | **否** | 仍冻 / HOLD |
| Affix densify / ConvertCap densify / 伤乘 / 抬 STAMP_RATE | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberSigStampPity.need()` = `ceil(1/stampRate)`；`ready` 当 dry≥need | `EmberSigStampPity.java` | **软保底数学已有 · 零改** |
| E2 | `EmberSigStampDry.glance`：`连空 n/need` / `软保底已满 · 下次重打必出盖章候选` | `EmberSigStampDry.java` | **玩家面 SoT 已有** |
| E3 | 结算 missAppend / forceStamp cue 已喊软保底 | `EmberRunService` / `EmberSettleService` | **结算可见 · 菜单不可见** |
| E4 | Growth `sig test show` 打 glance · 注释 **not PAPI** | `EmberGrowthService` ~D1292 | **无菜单键** |
| E5 | `sigPapi` 有 `chase_hope` / `imprint_hope` · **无 stamp_hope / dry** | `EmberGrowthService.sigPapi` | **PAPI 缺口** |
| E6 | 签名页 Open：`连空局数见结算提示` — **无 n/N** | `ember_p1_sig.yml` Events | **菜单缺口** |
| E7 | Icon I lore：静态「基础掉落 12%」— **无连空进度** | 同文件 I | **说明格缺口** |
| E8 | 烙印页 Open：同构「掉落连空见结算提示」 | `ember_p1_sig_imprint.yml` | **烙印页缺口** |
| E9 | power-paths SUR3：SigStampPity **并入可见表面 · STOP densify twin** | DESIGN power-paths §3.4 | **本窗 = 表面化 · 非抬 rate / 非第二套计数器** |
| E10 | BV DESIGN 旁债 #14：SigStampPity 菜单 n/N 一眼 · prefer 离 brand 后再评 | BV DESIGN §4 | **本窗兑现** |

### SoT 展示（只读 · 禁改 STAMP_RATE / need）

| 键 | 口径 |
|----|------|
| 未连空 | `签名掉落：未连空 · 约R%`（R=`EmberSigAcq.ratePct()`） |
| 连空中 | `签名连空 n/need · 约R% · 烙印保底` |
| 软保底满 | `签名软保底已满 · 下次重打必出盖章候选 · 连空n` |
| 禁 | 抬 `stampRate` / 改 `need` / 扩 stampable / 发明第二 dry 计数 / celebration 墙 |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件：** `sigPapi` 新键 `stamp_hope`（别名可 `stamp_dry`/`dry`）→ `EmberSigStampDry.glance(d)`；同构 `chase_hope`；**零改** pity 数学 / STAMP_RATE。  
2. **物品：** `ember_p1_sig` Icon `I`（+可选 Open tell）写入 `%corerpg_p1_sig_stamp_hope%`；`ember_p1_sig_imprint` Open tell 对齐；守 lore≤3。  
3. **零** sure/keep/直办半行再叠；零签名效果 lore 再叠；零扩 stampable。  
4. **施工岗：** 批后 **余烬-插件** + **余烬-物品**。

---

## 4. 明确不做

- 抬 STAMP_RATE / C12 · 改 SigStampPity.need · 扩 stampable · 甲闸再扫 · SetChase 六槽  
- 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 烙纹直办 / forge EQRUS / 洗练直留 / 互换 / 成色精工确定成功再叠 · access_* · 升阶保留 / 强化率 / 转化 / 形缓 / 签名效果 lore / 套装·宝石·天赋·烙纹档幅度·副手再叠  
- 效果长墙 · lore>3 · 并轨 B · 发明第二套 dry 计数器 / hope celebration · 改 chase_hope / imprint_hope 数学

## STATUS: OPEN · TIP-BW · 荐批 A·M · 2026-10-13 01:04 Asia/Shanghai
