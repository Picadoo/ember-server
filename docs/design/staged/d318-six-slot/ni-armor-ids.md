# D318 六槽 T1 · NeigeItems 护甲模板 ID（给 余烬-物品）

插件只按 ID 引用（`EmberItemData.templateId(family, slot, tier)` → `NiBridge.createNiItem(id)`），不自己造物品。模板缺失时：结算掉甲 / Q01 起步件 / 迁移都**不发**（迁移返回 NO_TEMPLATE 并告警 OP，不置标记，可重跑）。

## ID 表（40 个）

| 阶 | 族 | 头 | 胸 | 腿 | 靴 |
|----|----|----|----|----|----|
| T0 | 无族 | `ember_v1_t0_head` | `ember_v1_t0_chest` | `ember_v1_t0_legs` | `ember_v1_t0_boots` |
| T1–T3 | 焚烬 scorch | `ember_v1_scorch_head_t{1,2,3}` | `ember_v1_scorch_chest_t{1,2,3}` | `ember_v1_scorch_legs_t{1,2,3}` | `ember_v1_scorch_boots_t{1,2,3}` |
| T1–T3 | 烬爆 burst | `ember_v1_burst_head_t{1,2,3}` | `ember_v1_burst_chest_t{1,2,3}` | `ember_v1_burst_legs_t{1,2,3}` | `ember_v1_burst_boots_t{1,2,3}` |
| T1–T3 | 炽愈 sustain | `ember_v1_sustain_head_t{1,2,3}` | `ember_v1_sustain_chest_t{1,2,3}` | `ember_v1_sustain_legs_t{1,2,3}` | `ember_v1_sustain_boots_t{1,2,3}` |

= 4（T0）+ 3 族 × 3 阶 × 4 部位（36）= **40**。

## 模板要求（spec §2.2 / D312 T2）

- 材质：`EmberSixSlot.materialFor(tier, slot)` 建议（T0 皮革 · T1 锁链 · T2 铁 · T3 钻石；可换成任意同部位护甲材质）；**必须是该部位能穿的物品**（头 / 胸 / 腿 / 靴），否则原版拖放穿不上。
- 原版属性：护甲值 = 0、韧性 = 0（AttributeModifiers 覆盖为 0）、Unbreakable、无附魔、HideAttributes / HideUnbreakable。
- lore：模板 lore 可留空或 1 行风味；插件会追加 5 行（名称 · 共鸣 · 成色 / 精工 · 掉落阶 · 绑定 / 来源），**不要**写 `物理伤害 / 生命力 / 物理防御` 这类 StatService 关键词。
- 不可堆叠（amount 1），不要 NI 自带的随机属性 / 绑定逻辑（绑定由 `ember_v1` NBT 管）。
