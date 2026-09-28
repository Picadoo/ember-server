# B2.56 · 图录阶段奖玩家可见 tell

- **STATUS：已批 A（总控 · 2026-09-29 05:40 Asia/Shanghai）**
- **范围：**仅 `plugins/TrMenu/menus/ember_bestiary.yml` 阶段奖 tell 一行；本稿不施工玩法 YAML。
- **施工岗：**批后交 **插件岗（TrMenu）**。

## 定位与对照

阶段奖 tell 当前在 `plugins/TrMenu/menus/ember_bestiary.yml:101`，怪物与装备对照分别在 L54、L71。

### 旧 tell（L101）

```yaml
- 'tell: §e[图录·阶段奖] §7称号/币/天赋点 · 插件岗按 DESIGN 接线'
```

### 新 tell（荐案，批后施工替换同行）

```yaml
- 'tell: §e[图录·阶段奖] §7暂未开放，敬请期待'
```

## 荐案与理由

采用与怪物、装备一致的「暂未开放，敬请期待」口径，保留阶段奖前缀以便玩家识别入口；奖励类型已在 lore 展示，tell 不重复奖励清单，整体更短、更像玩家提示，不带管理或接线语气。

## 验收

- 批准后仅替换阶段奖 tell 这一行；其它 lore、actions、菜单结构零改。
- 新 tell 与怪物/装备对照保持「暂未开放，敬请期待」口径。
- 新句不含插件岗、DESIGN、STATUS、斜杠等管理提示。
- 施工前后核对目标 YAML diff，确认除该 tell 外无变化。
- 本窗不做长测、不做挑刺；不宣称 B0.1 已清。

## 禁项与状态

- **已批 A**：仅替换 `ember_bestiary.yml` L101 阶段奖 tell；lore/其它 actions/菜单零改；交插件岗（TrMenu）。
- 不扩写奖励逻辑，不接线，不改 lore/actions 或其它菜单。
- 批后施工交插件岗（TrMenu）；本稿只交玩家可见文案建议。

## 总控批示

- [x] **批 A** · 仅 L101 tell 按荐案对齐怪物/装备「暂未开放，敬请期待」
- [ ] **驳回** · 说明
