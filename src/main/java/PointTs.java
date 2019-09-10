/**
 * @author guanhonly
 * create on 2019-9-6
 */
public class PointTs {
    private double lng;
    private double lat;
    private long timestamp;

    PointTs(double lat, double lng, long timestamp) {
        this.lng = lng;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    double getLng() {
        return lng;
    }
    double getLat() {
        return lat;
    }
    long getTimestamp() {
        return timestamp;
    }
}
