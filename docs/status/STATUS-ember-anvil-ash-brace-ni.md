# STATUS · B-anvil-1 批 A · NI 灰箍（物品岗）

- 时间：2026-09-29（Asia/Shanghai）
- 依据：设计 tip `ab33c21` · 批准 tip `f10779b`
- 执行：余烬-物品

## 产出

| NI id | 显示名 | material | 属性 | 文件 |
|-------|--------|----------|------|------|
| `part_ember_ash_brace` | 余烬灰箍 | FLINT（与守腕 IRON_NUGGET 区分） | `物理防御: +1` | `plugins/NeigeItems/Items/ember-gear-parts.yml` |

## 未改

- `mat_ember_shard` / `ingot_ember_iron` 定义与数值
- 刃/护符、`acc_ember_offhand_ward` / `vita`、`forge.yml` 升阶
- 未宣称 B0.1 已清

## 联调

插件：配方扣 `mat_ember_shard`×12 + `ingot_ember_iron`×2 → give 本 id；`stats.offhand` 加本 id。
