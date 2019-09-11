import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TrjCompressorTest {

    private TrjCompressor trjCompressor;
    private List<PointTs> trajectory;

    private double randomLat() {
        return -90 + 180 * new Random().nextDouble();
    }

    private double randomLng() {
        return -180 + 360 * new Random().nextDouble();
    }

    private long randomTimestamp() {
        return (long)(827044338 + 1e8 * new Random().nextDouble());
    }

    private double cutDecimal(double decimal, int precision) {
        BigDecimal b = new BigDecimal(decimal);
        return b.setScale(precision, BigDecimal.ROUND_DOWN).doubleValue();
    }
    @Before
    public void setUp() {
        int precision = 6;
        trjCompressor = new TrjCompressor(precision);
        trajectory = new ArrayList<PointTs>();
        for (int i=0; i<100; i++) {
            trajectory.add(new PointTs(cutDecimal(randomLat(), precision), cutDecimal(randomLng(), precision), randomTimestamp()));
        }
    }
    @Test
    public void testEncodeAndDecode() {
        String encoding = trjCompressor.encode(trajectory);
        List<PointTs> decoding = trjCompressor.decode(encoding);

        for (int i=0; i<trajectory.size(); i++) {
            assertEquals(trajectory.get(i).getLat(), decoding.get(i).getLat(), 0);
            assertEquals(trajectory.get(i).getLng(), decoding.get(i).getLng(), 0);
            assertEquals(trajectory.get(i).getTimestamp(), decoding.get(i).getTimestamp(), 0);
        }
    }
}
