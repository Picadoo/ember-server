# 余烬地窟（演示副本）

## 循环
打怪 → 掉 NI 材料/装备（lore 带 AttributePlus 属性行）→ 穿戴强化属性

## 入口（测试）
- 命令：`/mm m spawn EmberCryptZombie` 等（正式入口可后接传送点）
- 简易场地：世界出生点附近用 `/setblock` 围出石砖小馆（由测试脚本搭建）

## 怪物（MythicMobs）
| ID | 类型 | 掉落 |
|----|------|------|
| EmberCryptZombie | ZOMBIE | mat_ember_shard, chance gear |
| EmberCryptSkeleton | SKELETON | mat_ember_bone_dust, chance gear |
| EmberCryptBrute | HUSK/ZOMBIE | mat_ember_core_fragment + better gear |

## 材料 / 装备（NeigeItems）
| ID | 用途 |
|----|------|
| mat_ember_shard | 基础材料（可合成/展示） |
| mat_ember_bone_dust | 材料 |
| mat_ember_core_fragment | 稀有材料 |
| gear_ember_blade | 武器，lore 含 AP 攻击力 |
| gear_ember_charm | 饰品，lore 含 AP 生命/防御 |

## AttributePlus
- 依赖 PlaceholderAPI（已装）
- 物品 lore 使用中文属性行（与 AP 默认 format 对齐，装上 AP 后识别）
- jar 需放入 plugins/（若未自动下载成功则用户上传）

## 验收
- MM 能刷三只怪
- 击杀掉 NI
- AP 在线时属性生效（有 jar 时）
