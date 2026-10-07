# 状态 · 体验薄窗告一段落 · 健康快照

**日期：** 2026-10-07（上海时间）  
**范围：** fetch/pull · proxy/login/play · MariaDB · 菜单断链扫 · **无新窗 / 无 Pack / 无六槽**

## 同步

- `git fetch` + `pull --ff-only` → **Already up to date**
- HEAD **`c67641e7`**（与 `origin/main` 齐）
- runtime 脏（players / calamity-state / holograms / paper.yml 等）**未 stash、未提交**；本轮只读 docs tip

## 服务

| 层 | 状态 |
|---|---|
| proxy Waterfall | PID 在；**`:25565` LISTEN**；近期 FreshW10 login→play 链路正常 |
| login Paper | **Done**；`:25566` |
| play Paper | **Done**；CoreRpg **1.65.83** Enabling；`:25567` |
| MariaDB | `mysqld is alive`；库 **`ember` 14 表**；CoreRpg `[storage] MySQL connected … pool=10` |

## 扫到 / 未修

- TrMenu 菜单交叉引用：**断链 NONE**（上轮已扫，本轮不再改菜单）
- **已知软伤（不薄修）：** stock Paper 下 `CoreSmelt/Fish/Enchant/Combat/Brew/Anvil.jar` Could not load（NMS 钩子，需自定义 Paper；与玩法薄窗无关）
- `secrets/mysql-ember.env` 与 `plugins/CoreRpg/config.yml` 口令不一致且未入库；运维脚本走 socket/`config.yml`，**CLI 用 secrets 会 Access denied**——记 tip，**不改密**
- proxy 控制台 `glist`/`server` → Command not found（Waterfall 裁剪命令，**不影响监听与转发**）

## 本轮改动

- **无代码 / 无菜单 / 无 jar bump**
- 仅本 STATUS tip
