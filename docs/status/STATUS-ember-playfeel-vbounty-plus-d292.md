# 状态 · D292：花样委托 +1 结算醒目 + G1 徽记一眼

**日期：** 2026-10-07（上海时间）  
**上游：** D288 尾巴 + DESIGN playfeel §4.3 G1 体验侧 · CoreRpg **1.65.83**

## 人话

打完花样进度时聊天栏以前只写「花样委托 · 完成：…」，不够「+1」。本窗结算固定打头 **「花样委托 +1」**；冒险/挑战每图加一行签名徽记进度（既有 PAPI）。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberSettleService.varietyBountyTip` | `§e花样委托 §a+1` 打头；完成项 / 进度行跟上 |
| `EmberSettleServiceTest` | D292 用例 |
| `ember_p1_adventure` / `_challenge` | 每图 `§7签名：%corerpg_p1_sig_m_qXX%` |
| 菜单文案 | 与「+1」对齐（短目标 / 誓约） |
| 版本 | **1.65.82 → 1.65.83** |

## 不动

- variety 池权重、affix_shard / event_core、委托币数量
- 六槽 · Pack · VIP · 图专属基础件

## 验收

- `rg '花样委托 §a\+1|varietyBountyTip' CoreRpg/`
- `rg 'sig_m_q0' plugins/TrMenu/menus/ember_p1_{adventure,challenge}.yml | wc -l` → 14
- 单测 `varietyBountyTip_D292`；play Enabling **1.65.83**；`trmenu reload`
