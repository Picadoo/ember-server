# 状态 · D516：好友路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D516**  
**版本：** jar **1.65.207-d516.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberFriendPath` · FriendService cmdAdd 钩 · Q01 offer @300L · `/corerpg p1 friendpath` |
| 禁 | 未改掉落 · 无 ActionBar · 非招募双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟 FreshQ1052a/b/c

| 路径 | 结果 |
|------|------|
| open | B 加 A → 自动成为好友 · A「好友·敞开 已自动同意」 **PASS** |
| busy | C 加 A →「对方开启了好友·静拒」 **PASS** |

单元：`EmberFriendPathTest` PASS。

*D516 · 新好友申请打法。*
