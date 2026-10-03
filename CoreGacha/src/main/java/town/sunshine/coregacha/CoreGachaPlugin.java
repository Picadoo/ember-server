package town.sunshine.coregacha;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.coregacha.engine.GachaConfig;
import town.sunshine.coregacha.engine.RateMath;
import town.sunshine.coregacha.engine.Tier;

/** 余烬 · 外观扭蛋 — design docs/design/DESIGN-ember-gacha.md */
public final class CoreGachaPlugin extends JavaPlugin {
    private Db db;
    private CoreRpgBridge rpg;
    private GachaService service;
    private Render render;
    private Anim anim;
    private Sources sources;
    private GachaExpansion expansion;

    GachaService service() { return service; }
    Render render() { return render; }
    Anim anim() { return anim; }
    Sources sources() { return sources; }
    CoreRpgBridge rpg() { return rpg; }
    GachaExpansion expansion() { return expansion; }

    @SuppressWarnings("unchecked")
    GachaConfig loadGacha() throws Exception {
        File f = new File(getDataFolder(), "gacha.yml");
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            GachaConfig c = GachaConfig.parse((Map<String, Object>) new Yaml().load(r));
            for (String w : c.warnings) getLogger().warning("gacha.yml: " + w);
            return c;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), "gacha.yml").exists()) saveResource("gacha.yml", false);
        GachaConfig cfg;
        try { cfg = loadGacha(); } catch (Exception e) { getLogger().severe("gacha.yml unreadable: " + e + " — CoreGacha disabled"); setEnabled(false); return; }
        db = new Db();
        File root = getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
        if (!db.open(root, getConfig().getConfigurationSection("mysql"), getLogger())) { setEnabled(false); return; }
        rpg = new CoreRpgBridge(getLogger());
        rpg.init();
        service = new GachaService(this, db, rpg, cfg);
        render = new Render(this);
        anim = new Anim(this);
        sources = new Sources(this);
        render.cleanAll(); // stray pet stands from a crash
        getServer().getPluginManager().registerEvents(render, this);
        getServer().getPluginManager().registerEvents(sources, this);
        GachaCommand cmd = new GachaCommand(this);
        getCommand("gacha").setExecutor(cmd);
        getCommand("gacha").setTabCompleter(cmd);
        expansion = new GachaExpansion(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) expansion.register();
        Bukkit.getScheduler().runTaskTimer(this, render::tick, 20L, 2L);
        Bukkit.getScheduler().runTaskTimer(this, sources::minute, 1200L, 1200L);
        for (Player p : Bukkit.getOnlinePlayers()) service.load(p);
        RateMath rm = service.rates();
        getLogger().info("CoreGacha " + getDescription().getVersion() + " · 池 " + cfg.banners.keySet() + " · 物品 " + cfg.items.size()
                + (cfg.disabled.isEmpty() ? "" : " (停用 " + cfg.disabled + ")") + " · 传说综合 " + RateMath.pct(rm.effective.get(Tier.LEGEND))
                + " · 史诗综合 " + RateMath.pct(rm.effective.get(Tier.EPIC)) + " · 只出外观");
    }

    boolean reloadAll() {
        try {
            reloadConfig();
            GachaConfig c = loadGacha();
            service.setConfig(c);
            return true;
        } catch (Exception e) { getLogger().severe("reload failed: " + e); return false; }
    }

    @Override
    public void onDisable() {
        if (render != null) render.cleanAll();
        if (service != null) {
            for (Map.Entry<java.util.UUID, PCache> e : service.cache.entrySet()) if (e.getValue().onlineDirty) service.saveOnline(e.getKey(), e.getValue().onlineMin);
            service.shutdown();
        }
        if (expansion != null) try { expansion.unregister(); } catch (Throwable ignored) { }
        if (db != null) db.close();
    }
}
