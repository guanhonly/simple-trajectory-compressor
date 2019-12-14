import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhonly
 * create on 2019-9-6
 */
public class TrjCompressor {
    private double factor;

    public TrjCompressor(int precision) {
        factor = Math.pow(10, precision);
    }

    public String encode(List<PointTs> points) {
        List<Long> output = new ArrayList<Long>();
        PointTs prev = new PointTs(0.0, 0.0, 0);
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) {
                prev = points.get(i - 1);
            }
            write(output, points.get(i).getLat(), prev.getLat());
            write(output, points.get(i).getLng(), prev.getLng());
            write(output, points.get(i).getTimestamp(), prev.getTimestamp());
        }
        return toASCII(output);
    }

    public List<PointTs> decode(String trjCode) {
        long lat = 0, lng = 0, timestamp = 0, latD, lngD, timestampD;
        List<PointTs> points = new ArrayList<PointTs>();
        for (int i = 0; i < trjCode.length(); ) {
            Tuple tuple = read(trjCode, i);
            latD = tuple.result;
            i = tuple.index;

            tuple = read(trjCode, i);
            lngD = tuple.result;
            i = tuple.index;

            tuple = read(trjCode, i);
            timestampD = tuple.result;
            i = tuple.index;

            lat += latD;
            lng += lngD;
            timestamp += timestampD;
            PointTs point = new PointTs((double) lat / factor, (double) lng / factor, timestamp);
            points.add(point);
        }
        return points;
    }

    private void write(List<Long> output, Object currValue, Object prevValue) {
        long currV, prevV;
        if (currValue instanceof Long) {
            currV = (Long) currValue;
            prevV = (Long) prevValue;
        } else if (currValue instanceof Double) {
            currV = getRound((Double) currValue * factor);
            prevV = getRound((Double) prevValue * factor);
        } else {
            throw new IllegalArgumentException("The type parameters must be long or double!");
        }
        long offset = currV - prevV;
        offset <<= 1;
        if (offset < 0) {
            offset = ~offset;
        }
        while (offset >= 0x20) {
            output.add((0x20 | (offset & 0x1f)) + 63);
            offset >>= 5;
        }
        output.add((offset + 63));
    }

    private Tuple read(String s, int i) {
        long b = 0x20;
        long result = 0, shift = 0, comp = 0;
        while (b >= 0x20) {
            b = s.charAt(i) - 63;
            i++;
            result |= (b & 0x1f) << shift;
            shift += 5;
            comp = result & 1;
        }
        result >>= 1;
        if (comp == 1) {
            result = ~result;
        }
        return new Tuple(i, result);
    }

    private String toASCII(List<Long> nums) {

        StringBuilder result = new StringBuilder();
        for (long i : nums) {
            result.append((char) i);
        }
        return result.toString();
    }

    private long getRound(double x) {
        return (long) Math.copySign(Math.round(Math.abs(x)), x);
    }
}

class Tuple {
    long result;
    int index;

    Tuple(int index, long result) {
        this.index = index;
        this.result = result;
    }
}
