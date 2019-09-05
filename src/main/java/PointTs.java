public class PointTs {
    double lng;
    double lat;
    long timestamp;

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
    double getTimestamp() {
        return timestamp;
    }
}
