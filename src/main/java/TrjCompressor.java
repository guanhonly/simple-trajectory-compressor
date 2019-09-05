import java.util.ArrayList;
import java.util.List;

public class TrjCompressor {
    private double factor;
    public TrjCompressor(int precision) {
        factor = Math.pow(10, precision);
    }

    public String encode(List<PointTs> points) {
        List<Integer> output = new ArrayList<Integer>();
        PointTs prev = new PointTs(0.0,0.0,0);
        for (int i=0; i<points.size(); i++) {
            if (i > 0) {
                prev = points.get(i-1);
            }
            write(output, points.get(i).lat, prev.lat);
            write(output, points.get(i).lng, prev.lng);
            write(output, points.get(i).timestamp, prev.timestamp);
        }
        return toASCII(output);
    }

    public List<PointTs> decode(String trjCode) {
        long lat=0, lng=0, latD, lngD, timestamp=0, timestampD;
        List<PointTs> points = new ArrayList<PointTs>();
        for (int i=0; i<trjCode.length();) {
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
            PointTs point = new PointTs(lat, lng, timestamp);
            points.add(point);
        }
        return points;
    }
    private void write(List<Integer> output, Object currValue, Object prevValue) {
        long currV, prevV;
        if (currValue instanceof Long) {
            currV = (Long)currValue;
            prevV = (Long)prevValue;
        } else if (currValue instanceof Double) {
            currV = py2round((Double)currValue * factor);
            prevV = py2round((Double)prevValue * factor);
        } else {
            throw new IllegalArgumentException("The type parameters must be long or double!");
        }
        long offset = currV - prevV;
        offset <<= 1;
        if (offset < 0) {
            offset ^= offset;
        }
        while (offset >= 0x20) {
            output.add((0x20 | (int)(offset & 0x1f)) + 63);
            offset >>= 5;
        }
        output.add((int)(offset+63));
    }

    private Tuple read(String s, int i) {
        long b = 0x20;
        long result=0, shift=0, comp=0;
        while (b >= 0x20) {
            b = s.charAt(i) - 63;
            i++;
            result |= (b & 0x1f) << shift;
            shift += 5;
            comp = result & 1;
        }
        result >>= 1;
        if (comp == 1) {
            result ^= result;
        }
        return new Tuple(i, result);
    }

    private long py2round(double num) {
        return (long)Math.copySign(Math.ceil(Math.abs(num)), num);
    }

    private String toASCII(List<Integer> nums) {

        StringBuilder result = new StringBuilder();
        for (int i : nums) {
            result.append((char)i);
        }
        return result.toString();
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
