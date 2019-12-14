import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;


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
        return (long) (827044338 + 1e8 * new Random().nextDouble());
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
        for (int i = 0; i < 100; i++) {
            trajectory.add(new PointTs(cutDecimal(randomLat(), precision), cutDecimal(randomLng(), precision), randomTimestamp()));
        }
    }

    @Test
    public void testEncodeAndDecode() {
        String encoding = trjCompressor.encode(trajectory);
        List<PointTs> decoding = trjCompressor.decode(encoding);

        for (int i = 0; i < trajectory.size(); i++) {
            assertEquals(trajectory.get(i).getLat(), decoding.get(i).getLat(), 0);
            assertEquals(trajectory.get(i).getLng(), decoding.get(i).getLng(), 0);
            assertEquals(trajectory.get(i).getTimestamp(), decoding.get(i).getTimestamp(), 0);
        }
    }

    // This ut is to test the compression rate of this
    // algorithm(compared to json format), which uses
    // some trajectory data from Microsoft GeoLife.
    // Not well written, but correct at least. :P
    @Test
    public void compareWithOtherFormat() throws ParseException {
        String path = "data/Trajectory";
        File directory = new File(path);
        File[] fs = directory.listFiles();
        double avgCompressionRate = 0;
        List<Double> allCompressionRates = new ArrayList<Double>();
        for (File f : fs) {
            if (!f.isDirectory()) {
                InputStream is = null;
                Reader reader = null;
                BufferedReader bufferedReader = null;
                ArrayList<PointTs> trj = new ArrayList<PointTs>();
                try {
                    is = new FileInputStream(f);
                    reader = new InputStreamReader(is);
                    bufferedReader = new BufferedReader(reader);
                    String line;
                    int headNum = 6;
                    int headCount = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (headCount < headNum) {
                            headCount++;
                            continue;
                        }
                        String[] args = line.split(",");
                        if (args.length < 4) {
                            continue;
                        }
                        double lat = Double.valueOf(args[0]);
                        double lng = Double.valueOf(args[1]);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                        Date date = df.parse(args[args.length - 2] + args[args.length - 1]);
                        long timestamp = date.getTime();
                        PointTs p = new PointTs(lat, lng, timestamp);
                        trj.add(p);
                    }
                    Collections.sort(trj);
                    String jsonString = JSON.toJSONString(trj);
                    String encoding = trjCompressor.encode(trj);
                    double ratioOfTwo = 0;
                    if (jsonString.length() != 0) {
                        ratioOfTwo = (double) encoding.length() / (double) jsonString.length();
                    }
                    avgCompressionRate += ratioOfTwo;
                    allCompressionRates.add(ratioOfTwo);
                    System.out.println("json size: " + jsonString.length());
                    System.out.println("polyline encoding size: " + encoding.length());
                    System.out.println("compression rate: " + ratioOfTwo * 100 + " %");
                    System.out.println();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != bufferedReader)
                            bufferedReader.close();
                        if (null != reader)
                            reader.close();
                        if (null != is)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        int size = allCompressionRates.size();
        Collections.sort(allCompressionRates);
        double median = 0;
        if (size > 0) {
            avgCompressionRate = avgCompressionRate / size;
            if ((size & 1) == 0) {
                median = (allCompressionRates.get(size/2) + allCompressionRates.get(size/2-1))/2;
            } else {
                median = allCompressionRates.get(size/2);
            }
        }

        System.out.println("mean of compression rate: " + avgCompressionRate*100 + " %");
        System.out.println("median of compression rate: " + median*100 + " %");
    }
}
