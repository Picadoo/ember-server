# DESIGN · 宝石 Stage3 获取（D608 · 希望环 C2）

> **状态：** 已批 A · 方案 M · 2026-10-11 · 施工  
> **禁：** AFK 表 / sx20 / K3 / 抬 gem flat / legacy socket / 主线 MM 掉石

## 1. 目标

玩家总能看到「离下一颗石还有几步」；合成指定石零抽卡。

## 2. 方案 M（已批）

| 轨 | 规则 | 希望 |
|----|------|------|
| 通关保底 | 每次 Q01–Q07 成功通关（含挑战；不含团本/深渊层/活动图）`p1_gem_clear_pity` +1；满 5 发 1 石并清零 | `EmberHopeMeter.gemPity` n/5 |
| 偏好 | 发石时读 `EmberGemPath`；无偏好 → 锋利 | — |
| 合成 | `/corerpg p1 gem craft <sharp|…>`：碎 8 + 骨 4 + 币 40 → 指定石 | 工坊文案 |

拆卸已在 Stage2 免费返还。本档不改 scrap 权。

## 3. 落点

- `EmberGemAcquire` · settle 挂钩 · HopeMeter · PAPI `gem_pity_hope` · forge/hub 行  
- 不改 `EmberGems` 战斗常数

## 4. 验收

- 单测 pity 阈值 / craft cost  
- 冒烟：craft + pity 宣告  
- SEVERE0 · sock/flat 常数 git 无抬
