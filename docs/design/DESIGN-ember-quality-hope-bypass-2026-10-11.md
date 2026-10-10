# DESIGN · 成色希望旁路（D602 · Stage A2）· 2026-10-11

> **状态：** 已批 A · 方案 M · D602  
> **上游：** [`DESIGN-ember-growth-hope-loop-2026-10-11.md`](DESIGN-ember-growth-hope-loop-2026-10-11.md) A2  
> **tip：** [`STATUS-ember-next-hard-debt-quality-hope-need-design-2026-10-11.md`](../status/STATUS-ember-next-hard-debt-quality-hope-need-design-2026-10-11.md)  
> **硬禁：** 无 K3 · 无 AFK 表抬 · 无 sx20 · 无新伤乘 · 不改掉落成色权重 · 不改 qualityCost · 极品仍仅掉落 · 不复开 legacy socket · 不碰 gem sock0/sock1

---

## 0. 一句话

工坊升成色（标准→精良→卓越）**本来就是材料确定升一档**；本档只把这条旁路做成和 D601 同风格的**希望表盘**，让玩家一眼看到「离卓越还差几步 / 下次确定升」。

## 1. 诊断

| 症状 | 根因 | 不是根因 |
|------|------|----------|
| 成色靠脸挫败 | 掉落 / 随机锻造（D430）成色权重是 RNG；**工坊确定档入口不够显眼** | quality() 会失败（它不会） |
| 看不到进度 | 无「成色 n/2」表盘；hub 只挂强化/洗练 pity | 缺第三种成色赌 |

## 2. 方案 M（已定）

| 做 | 不做 |
|----|------|
| `EmberHopeMeter.quality(d)`：q0→「成色进度 0/2 · 下次确定升精良」；q1→「1/2 · 下次确定升卓越」；q2→「工坊顶 · 极品仅掉落」；q3→「已满」 | 改 `qualityCost` / 掉落 `QUALITY_WEIGHTS` / D430 `QUALITY_W` |
| PAPI：`quality_blade_hope` / `quality_charm_hope` / `held_quality_hope` | 给随机锻造加成色保底（DESIGN-forge-random 已禁：会比养成更便宜） |
| hub 希望表盘 + forge 成色格挂同风格文案 | 开放卓越→极品工坊升（极品只掉落，站立硬规则） |
| 费用行仍走既有 `qualityLine` / `held_quality_lack` | 粘性路径 |

**进度定义（可见计数）：** 工坊可确定到达的顶是卓越（q=2）。`cur = min(quality, 2)`，`need = 2`。满则诚实「极品仅掉落」。

## 3. 验收

- 单测：q0/q1/q2/q3 文案钉死；与 `qualityCost` 可升档一致  
- 冒烟：PAPI 手持/穿着对齐真实成色；SEVERE0  
- **未改** qualityCost / 掉落权重 / sock 字段  

## 4. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-11 · 服主授权连续变强希望环 · 本文件即方案 M
