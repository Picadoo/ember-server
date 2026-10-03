# 依赖插件下载状态 — 2026-09-12

## 已安装并启用

| 插件 | 版本 | 说明 |
|------|------|------|
| PlaceholderAPI | 2.10.9 | 保持 1.12/Java8；勿盲目升 2.12 |
| Vault | 1.7.3 | API 层；尚无经济实现（无 Essentials/CMI） |
| ProtocolLib | 4.4.0 | 1.12 适配 |
| HolographicDisplays | 2.4.9 | DungeonPlus 全息脚本已注册成功 |

## PAPI 扩展（Java8 可用）

已加载：`player` `server` `progress` `localtime`（+ 插件自带 dungeon/team/trmenu/ni）

已剔除（Java11/21，会拖垮整个扩展加载器）：BetterStatistics、Math 2.0.2、OtherPlayer、PlayerList

## 未装（软依赖）

- **Citizens / Adyeshach**：DungeonPlus NPC 脚本仍缺；CI/论坛直链下不了，需你上传 jar
- AttributePlus：仍在 `_parked`（起服卡死）

## 验证

- `%player_name%` → RpgBot
- `%server_online%` → 数字
- DungeonHologramScript：注册完毕
