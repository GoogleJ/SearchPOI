package per.j;

import android.util.Log;

import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

public class T {
    /**
     * 根据一个点的经纬度和距离得到另外一个点的经纬度
     *
     * @param distance
     * @param latlngA
     * @param angle：角度
     * @return
     */
    public static LatLonPoint getLatlng(float distance, LatLonPoint latlngA, double angle) {
        return new LatLonPoint(latlngA.getLatitude() + (distance * Math.cos(angle * Math.PI / 180)) / 111,
                latlngA.getLongitude() + (distance * Math.sin(angle * Math.PI / 180)) / (111 * Math.cos(latlngA.getLatitude() * Math.PI / 180))
        );
    }

    public static List<LatLonPoint> getFour(LatLonPoint latLonPoint, float line) {
        LatLonPoint latlng1 = getLatlng(line, latLonPoint, 45);
        LatLonPoint latlng2 = getLatlng(line, latLonPoint, 135);
        LatLonPoint latlng3 = getLatlng(line, latLonPoint, 225);
        LatLonPoint latlng4 = getLatlng(line, latLonPoint, 315);
        Log.e("l1", "la:" + latlng1.getLatitude() + " lon:" + latlng1.getLongitude());
        Log.e("l2", "la:" + latlng2.getLatitude() + " lon:" + latlng2.getLongitude());
        Log.e("l3", "la:" + latlng3.getLatitude() + " lon:" + latlng3.getLongitude());
        Log.e("l4", "la:" + latlng4.getLatitude() + " lon:" + latlng4.getLongitude());

        List<LatLonPoint> points = new ArrayList<>();
        points.add(new LatLonPoint(latlng1.getLatitude(), latlng1.getLongitude()));
        points.add(new LatLonPoint(latlng2.getLatitude(), latlng2.getLongitude()));
        points.add(new LatLonPoint(latlng3.getLatitude(), latlng3.getLongitude()));
        points.add(new LatLonPoint(latlng4.getLatitude(), latlng4.getLongitude()));
        return points;
    }

    //根据矩形右上、右下两点确定中心点
    public static LatLonPoint getCenter(LatLonPoint l1, LatLonPoint l2) {
        float distance = calculateLineDistance(l1, l2);

        return getLatlng((float) ((distance / 2 * Math.sqrt(2))), l1, 225);
    }

    //获取两点间的距离
    public static float calculateLineDistance(LatLonPoint l1, LatLonPoint l2) {
        double R = 6371;
        double distance;
        double dLat = (l2.getLatitude() - l1.getLatitude()) * Math.PI / 180;
        double dLon = (l2.getLongitude() - l1.getLongitude()) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(l1.getLatitude() * Math.PI / 180)
                * Math.cos(l2.getLatitude() * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
        return (float) distance;
    }
}
