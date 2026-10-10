package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberQualityCraftDialTest {
    @Test public void qualityDialStepsAndCap() {
        EmberItemData q0 = stub(0, 0);
        String d0 = EmberQualityCraftDial.qualityDial(q0);
        assertTrue(d0.contains("0/2"));
        assertTrue(d0.contains("确定成功"));
        assertTrue(EmberQualityCraftDial.qualityDial(stub(2, 0)).contains("极品不可养成"));
        assertTrue(EmberQualityCraftDial.qualityDial(stub(3, 0)).contains("极品"));
    }

    @Test public void craftDialStepsAndFull() {
        assertTrue(EmberQualityCraftDial.craftDial(stub(0, 0)).contains("0/3"));
        assertTrue(EmberQualityCraftDial.craftDial(stub(0, 0)).contains("确定成功"));
        assertTrue(EmberQualityCraftDial.craftDial(stub(0, 3)).contains("已满"));
    }

    private static EmberItemData stub(int q, int craft) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, q, craft, 0, 0, true, "drop", 1, 1);
    }
}
