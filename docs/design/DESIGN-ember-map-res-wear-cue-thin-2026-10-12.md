# 余烬 · 图装身份 · 局内换装共鸣升沿薄窗

STATUS=**待批 A · 荐方案 M** · 2026-10-12  
上游 RESEARCH [`RESEARCH-ember-map-res-wear-cue-thin-2026-10-12.md`](RESEARCH-ember-map-res-wear-cue-thin-2026-10-12.md) · tip `TIP-ember-map-res-wear-cue-thin-d12424-d12439-2026-10-12.md`  
资产：`EmberMapResonance` · `EmberLoadoutService.refresh` · `EmberMapOriginWearBone`

局内换上本图件时说清「共鸣亮了」· **禁**改加宽 · **禁** glance twin · **禁** soft densify。

## 方案 M（荐）

| 项 | 内容 |
|----|------|
| 检测 | 玩家在副本中；`refresh` 后 `active(lo, mapKey)` 相对上一快照 **升沿** |
| 文案 | 对齐 enter：`图源共鸣 · {Q0X} · 本图掉落件加宽烬斩（零伤乘）` + AB；日加宽句若已 latch 可附 |
| Latch | 每 `runId`（或 mapKey+run）一次；离图清快照；禁 inventory 抖屏 |
| 挂点 | `EmberMapResonance.maybeWearCue(p,d,mapKey)`；由 loadout refresh / 换装路径调用（仅 in-run） |
| WearBone | 升沿时若 `ready` → 既有 `maybeGift`（仍 BONUS=1/日）；**不**加量 |
| 热关 | `map_res.wear_cue: true` 缺省 true |
| 验收 | 入场裸装→局内换本图件见一句；再抖背包无刷；加宽表 `git show` 不变 |

## 明确不做

- 改共鸣加宽幅度 / skill_mult / 掉率  
- 枢纽 mapid glance densify  
- Affix · 新每日礼 · WearBone BONUS+  
- 工坊稳赌再一眼  

## 方案 R / W

| 方案 | 裁决 |
|------|------|
| R · 掉落 giveItem 每件喊出处墙 | **否**（结算已挤；粒子已有） |
| W · 抬加宽当假变强 | **否决** |
