# B2.13 天赋菜单 `*_cap` 人话化 A

- 设计 tip：`c86ebd6`
- 批准：`aa4a7bb`
- 范围：仅 `plugins/TrMenu/menus/ember_talent.yml`；未改 `talent.yml`、其它菜单或 runtime/player/world 数据。
- 方案：A，仅替换玩家可见 lore/tell；不改键、cost、requires、stats、unlock command 实参、§8 nodeId/前置。

## 变更

已完成 14 处玩家可见字符串替换：

- Open、U lore、规则速览 lore/tell：改为“燎原 / 烟幕 / 永护”的人话提示。
- 二层 a/b/c：改为 `§e先点满一层顶「燎原」`。
- 二层 d/e/f：改为 `§e先点满一层顶「烟幕」`。
- 二层 g/h/i：改为 `§e先点满一层顶「永护」`。

§8 未清：方案 A 不处理 §8 nodeId/前置；本文件未改动这些字段，且当前目标文件没有残留 `cap` 字样。不得据此宣称 §8 已清。

## 热更证据（Asia/Shanghai）

已执行：

```text
printf 'trmenu reload\n' > server-runtime/console.in
```

日志证据：

```text
server-runtime/logs/latest.log:80:[00:09:24] [TConfigWatcherService-1/INFO]: [TrMenu] 良好 | 自动重新载入菜单 ember_talent.yml (7ms)
server-runtime/logs/latest.log:81:[00:09:34] [ForkJoinPool.commonPool-worker-3/INFO]: [TrMenu] 良好 | 36 个菜单已加载 (42 ms)
```

## 验收

```text
$ rg '\*_cap' plugins/TrMenu/menus/ember_talent.yml
# 无输出（0 处）
```

- 禁项：EMPTY（仅目标菜单与本 STATUS 纳入交付；其它现网脏改未触碰）。
