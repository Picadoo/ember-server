package town.sunshine.corerpg.p1;

import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * TIP-BG / D13022：烬斩推荐形状命中 → Slow I · 20t · lim3（U1_sl10）。
 * 零 skill_mult；不改 CD/几何/D438 slashLandSlowTicks。
 */
public final class EmberSkillSf4 {

    public static final int MARK_TICKS = 20;
    public static final int SLOW_AMP = 0;
    public static final int HIT_LIM = 3;

    private EmberSkillSf4() {}

    /** DESIGN §3.1：fam 空/none → LINE；否则 setShapeSynced。 */
    public static boolean shapeOk(String fam, int shapeId) {
        if (fam == null || fam.isEmpty() || "none".equals(fam)) {
            return shapeId == EmberSkillKit.SHAPE_LINE;
        }
        return EmberSkillKit.setShapeSynced(fam, shapeId);
    }

    /**
     * 形状门过且 hit 非空时，对前 lim 目标挂 Slow I 20t。
     * @return true 当至少一目标成功上缓
     */
    public static boolean maybeApply(Player player, List<LivingEntity> hit, int shapeId, String fam) {
        if (player == null || !player.isOnline() || player.isDead()) return false;
        if (hit == null || hit.isEmpty()) return false;
        if (!shapeOk(fam, shapeId)) return false;
        int lim = Math.min(hit.size(), HIT_LIM);
        int applied = 0;
        for (int i = 0; i < lim; i++) {
            LivingEntity le = hit.get(i);
            if (le == null || le.isDead() || !le.isValid()) continue;
            try {
                le.addPotionEffect(new PotionEffect(
                        PotionEffectType.SLOW, MARK_TICKS, SLOW_AMP, false, true), true);
                applied++;
            } catch (Throwable ignored) { }
        }
        if (applied > 0) {
            try { player.sendActionBar("§e烬斩·形缓"); } catch (Throwable ignored) { }
            return true;
        }
        return false;
    }
}
