# EmberCrypt 掉落（MM 4.11 复查结论）

- 文件：`Mobs/EmberCrypt.yml`（唯一）；已删 `EmberCryptMobs.yml` / `DropTables/EmberCryptDrops.yml`
- 占位：**`<trigger.name>`**（`~onDeath` + `@Trigger`）
- **不要用 `<killer.name>`** — MythicMobs 4.11.0 jar 无此占位
- 双通道：`NeigeItems.Drops`（地面）+ `ni give`（背包）；材料 give 为必掉，装备给低概率，避免与 NI 表同概率叠满双份材料
