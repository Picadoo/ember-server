package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSealPathTest {
    @Test public void parse_map_brand_lead() {
        assertEquals(EmberSealPath.HUNT, EmberSealPath.parse("猎缀"));
        assertEquals(EmberSealPath.EMBER, EmberSealPath.parse("ember"));
        assertEquals(EmberSealPath.BIND, EmberSealPath.parse("定身"));
        assertEquals(EmberBrandPath.HUNT, EmberSealPath.brandId(EmberSealPath.HUNT));
        assertEquals(EmberDualLead.BLADE, EmberSealPath.leadId(EmberSealPath.EMBER));
        assertEquals(EmberDualLead.CHARM, EmberSealPath.leadId(EmberSealPath.BIND));
        assertTrue(EmberSealPath.tip(EmberSealPath.BIND).contains("护符"));
    }
}
