# 余烬 · D441 真地图：sx14 强制折角 + sx15 绕心螺旋

STATUS=**已批 A · 方案 M · D441 · PASS** · 2026-10-10 · tip 打开（用户：「maps weren't optimized」；薄反馈 D440 HOLD）  
上游：D423/D424 规格已上线但地图仍为 **raid 壳**（`@d423-pending` / `@d424-pending`）  
禁：抬日表 · 开 R/K3 · AFK 拧表 · sx20 · talent/灰印 · `git add -f` map · stash/reset --hard/checkout -- .

## 1. 问题
短征 sx14/sx15 键与经济已 PASS，但 `plugins/DungeonPlus/map/ember_short_sx14|15` 仍是团本白盒拷贝，**无** DESIGN 要求的强制折角裂隙廊 / 绕心螺旋坡；Cast 预警在短战里不易看见。

## 2. 方案 M（已批）
1. **地形**：Anvil 1.12 脚本 `tools/p1map/d441_build_sx14_sx15_terrain.py`（仓/play 无 WE/FAWE）生成主题地形，装入本地 DP map（gitignore）。
2. **sx14**：裂门庭 → 折裂廊 **≥3 强制折角**（角点 (22,31)(36,31)(36,44)(22,44)）+ 裂隙缝（岩浆/铁栏）→ 裂冠终厅侧龛。
3. **sx15**：坡门庭 → 绕心螺旋 **≥2.25 圈**（心 (30,40)，Y64→72）→ 旋冠终厅侧台。
4. **Cast 可读**：Boss HP 180→240；RiftCast/SpiralCast delay 25→30、预警粒子加浓、结算伤 1.2→0.85；**不改** S53/S54 奖励表。
5. **坐标**：short.sx14/sx15 `map_version @d441`；R2 points 落到可走折角/螺旋坪。

## 3. 验收
- 本地 map 非 raid 壳；走位不能直线穿廊 / 不能穿心。
- 烟测：进本 → 三房路径可站 → Cast 消息可见 → 结算。
- Stage2 观察仍 ≥17:40 CST。
