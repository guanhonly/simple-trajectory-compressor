/**
 * @author guanhonly
 * create on 2019-9-6
 */
public class PointTs implements Comparable<PointTs> {
    private double lng;
    private double lat;
    private long timestamp;

    PointTs(double lat, double lng, long timestamp) {
        this.lng = lng;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public int compareTo(PointTs p) {
        return (int) (timestamp - p.timestamp);
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
