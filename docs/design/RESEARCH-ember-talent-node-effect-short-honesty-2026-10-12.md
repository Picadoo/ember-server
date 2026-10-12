# RESEARCH · 天赋节点效果短诚实（TrMenu 悬停 · 压回 D12200 误删）· 2026-10-12

> **状态：** CLOSED live items `4c2c38485` · TIP-BK · 2026-10-12 23:52 Asia/Shanghai  
> **起因：** TIP-BJ CLOSED live 后总控「离开宝石短效果链 · 开下一可感变强途径」。扫 AH…BJ / power-paths：`talent.yml` **stats SoT 已有**；B2.15 曾把二层裸键人话化上菜单；**D12200 lore≤3 压缩把效果半行整段删掉**——现网节点悬停只剩「被动 · 消耗 N」，玩家点天赋不知变强点。  
> **上游：** TIP-BJ CLOSED live `1.65.799-d13079` · feat `72cba2db4` · items `bc1e63407` · docs `05a2676fd` · `talent.yml` · `ember_talent.yml` · B2.15 `design-ember-talent-attr-copy.md` · D12200 `c66ecdf3b` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 talent.yml stats/cost/requires · 零 skill_mult · 零抬 gem flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 行**

---

## 0. 一句话

天赋菜单已上线节点用 **`talent.yml` stats** 写回 **≤1 行短效果**（合进消耗行，守 lore≤3）；不改点数/数值、不扩菜单节点墙。

---

## 1. 选题扫描（离开宝石短效果 + soft/开孔 + SF4 + 签名/套装）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **天赋节点效果短诚实（TrMenu）** | **采纳** | D12200 误删 B2.15 效果；SoT 已有；可感变强途径；非 densify；离开宝石链 |
| 宝石短效果 / 持件孔位长墙再叠 | **否** | 刚 CLOSED / 硬禁效果长墙 |
| 套装 blurb / 签名 lore 再叠 | **否** | 硬禁 / 离链 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 烙纹定向幅度菜单诚实 | **否（本窗）** | Affix 旁近；可另 tip；本窗优先天赋回归 |
| 天赋一层中段节点进 TrMenu 扩容 | **否** | 非薄；菜单 densify |
| 抬 talent stats / skill_mult | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `talent.yml` 各节点 `stats:` / `skill_id:` 齐全（如 `blaze_root` phys_damage:1 · `ash_root` attack_speed_pct:0.02） | `CoreRpg/.../talent.yml` | **战斗 SoT 已有** |
| E2 | B2.15 已批并把二层 9 行裸键→中文（物攻/暴伤/…） | `design-ember-talent-attr-copy.md` · commit `da1719687` | **展示口径已有** |
| E3 | D12200 lore≤3 压缩 **删除** 二层效果行（`§7物攻 +1` 等） | `c66ecdf3b` diff `ember_talent.yml` | **误删可感变强信息** |
| E4 | 现网二层悬停 =「二层 · 被动 · 消耗 N」+ 前置顶 + 点击；**无效果** | `ember_talent.yml` a–i | **点前不知加什么** |
| E5 | 根节点 1/2/3 与烬斩 4 **从未**写效果半行（只消耗/前置） | 同文件 | **同构缺口** |
| E6 | 一层中段（燃眼/饮烬/…）**不在**本 TrMenu 布局 | Layout 仅根+烬斩+二层九格 | **本窗不扩节点墙** |
| E7 | Karen lore≤3 | D12200 起因 | **效果须合进消耗行，禁再叠字墙** |
| E8 | 抬 `talent.yml` stats = 抬表 | 总控硬禁 | **诚实 ≠ 抬表** |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **定稿短效果表**：以 `talent.yml` stats / skill_id 为准，沿用 B2.15 键→中文；根三 + 烬斩 + 二层九 = **本窗菜单已有 13 格**。  
2. **合行**：`§7{层} · 消耗 N · {短效果}`（可省「被动」二字换位）；前置行保留；点击行保留；**每格 lore ≤3**。  
3. **施工岗**：批后 **余烬-物品**（只改 `ember_talent.yml`）；**不派插件**（零 jar · 禁改 talent.yml）。  
4. **零改** talent 点数/stats/requires/cost · soft/开孔 · Anvil · SF4 · 宝石/签名/套装展示再叠。

---

## 4. 明确不做

- 改 `talent.yml` stats / cost / requires / 解锁 command 实参  
- lore>3 · 效果长墙 · 一层中段节点塞进 TrMenu  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 宝石短效果 / 套装 blurb / 签名 lore / 调律 live bad 再叠  
- Affix / 伤乘 / skill_mult / sx20 / glance remain twin 族再叠  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| PoE 天赋悬停写真实数值 | 节点悬停写 SoT 短效果 |
| D3 技能树悬停写加成 | 合进消耗行 · 守 lore≤3 |
| 禁止压版时把效果一并删掉 | 压回 D12200 误删 |
