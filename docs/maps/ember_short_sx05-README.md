# 地图骨架 · `ember_short_sx05`（短征 · 裂谷风廊 · D400）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx05-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx05-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx05-d400-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx05-d400-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx05/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前四本同观感**：本号用 `ember_daily_crypt_v1`（深板岩/残誓石廊气质），**不用** `ember_short_sx01..04` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_crypt_v1 \
      plugins/DungeonPlus/map/ember_short_sx05
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与 sx01（daily_v1）/ sx02（rail_v1）/ sx03（frost_v1）/ sx04（ash_v1）观感分离，对齐裂谷深板岩气质。
- **非目的：** 不宣称裂谷风廊主题完工；不宣称崖口台/窄桥过隙/对岸风心垫已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 崖口台 | 半露天崖缘 + 对岸/桥口可读 | 1～2 波崖卫/风廊弩 |
| R2 风廊窄桥 | **真窄桥过虚空/深谷**（桥宽建议 ≤3～4；桥下空隙 ≥6～10） | 禁假画「看起来像桥」的平坦宽廊 |
| R3 对岸风心 | 终厅 · Cast 风剪条/侧风斩 | `EmberSx05…Warden/WindCast` · 退进侧龛或离开条带 |
| 体量目标 | 约 28×28～36×36 · 空隙可读 | 主题：stonebrick / cobble / deepslate 感 + 少量铁栏；禁整图冰/灯笼/井心岩浆 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S44 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..04` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第五条短征完工
