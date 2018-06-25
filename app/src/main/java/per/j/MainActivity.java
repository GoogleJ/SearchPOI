package per.j;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText et_main;
    private TextView tv_tips;

    private String keyWord = "";

    private PoiSearch.Query query;
    private boolean isSearch;
    private ArrayList<PoiItem> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLocation();
        mLocationClient.startLocation();

        et_main = findViewById(R.id.et_main);
        tv_tips = findViewById(R.id.tv_tips);
    }

    PoiSearch poiSearch;
    LatLonPoint lp;
    float searchRate = 3; //范围 公里

    public void search(View view) {
        if (lp == null) {
            Toast.makeText(this, "暂无位置信息,请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSearch) {
            Toast.makeText(this, "正在查询中，请等待", Toast.LENGTH_SHORT).show();
            return;
        }

        keyWord = et_main.getText().toString().trim();

        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        results.clear();

        tv_tips.setText("正在查询" + searchRate + "公里内的" + keyWord);

        new Thread(new Runnable() {
            @Override
            public void run() {
                isSearch = true;
                final long l = System.currentTimeMillis();
                initPoiSearch(T.getFour(lp, (float) (searchRate * Math.sqrt(2))));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long l1 = System.currentTimeMillis() - l;
                        tv_tips.setText("查询完成,用了" + (l1 / 1000 / 60) + "分钟,结果数：" + results.size() + "条");
                        query = null;
                        poiSearch = null;
                        isSearch = false;
                    }
                });
            }
        }).start();
    }

    private void initPoiSearch(List<LatLonPoint> four) {
        if (query == null) {
            query = new PoiSearch.Query(keyWord, "", "");
            query.setPageSize(30);
            query.setPageNum(1);
        }

        if (poiSearch == null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setBound(new PoiSearch.SearchBound(four));
        }

        try {
            PoiResult poiResult = poiSearch.searchPOI();
            if (poiResult.getPageCount() >= 25) {
                List<LatLonPoint> points = new ArrayList<>();
                LatLonPoint center = T.getCenter(four.get(0), four.get(1));
                float v = T.calculateLineDistance(four.get(0), four.get(1)) / 2;

                LatLonPoint latlng1 = T.getLatlng(v, center, 0);
                LatLonPoint latlng2 = T.getLatlng(v, center, 90);
                LatLonPoint latlng3 = T.getLatlng(v, center, 180);
                LatLonPoint latlng4 = T.getLatlng(v, center, 270);

                points.add(new LatLonPoint(latlng1.getLatitude(), latlng1.getLongitude()));
                points.add(new LatLonPoint(center.getLatitude(), center.getLongitude()));
                points.add(new LatLonPoint(latlng4.getLatitude(), latlng4.getLongitude()));
                points.add(new LatLonPoint(four.get(3).getLatitude(), four.get(3).getLongitude()));
                query.setPageNum(1);
                poiSearch.setBound(new PoiSearch.SearchBound(points));
                initPoiSearch(points);
                points.clear();

                points.add(new LatLonPoint(four.get(0).getLatitude(), four.get(0).getLongitude()));
                points.add(new LatLonPoint(latlng2.getLatitude(), latlng2.getLongitude()));
                points.add(new LatLonPoint(center.getLatitude(), center.getLongitude()));
                points.add(new LatLonPoint(latlng1.getLatitude(), latlng1.getLongitude()));
                query.setPageNum(1);
                poiSearch.setBound(new PoiSearch.SearchBound(points));
                initPoiSearch(points);
                points.clear();

                points.add(new LatLonPoint(latlng2.getLatitude(), latlng2.getLongitude()));
                points.add(new LatLonPoint(four.get(1).getLatitude(), four.get(1).getLongitude()));
                points.add(new LatLonPoint(latlng3.getLatitude(), latlng3.getLongitude()));
                points.add(new LatLonPoint(center.getLatitude(), center.getLongitude()));
                query.setPageNum(1);
                poiSearch.setBound(new PoiSearch.SearchBound(points));
                initPoiSearch(points);
                points.clear();

                points.add(new LatLonPoint(center.getLatitude(), center.getLongitude()));
                points.add(new LatLonPoint(latlng3.getLatitude(), latlng3.getLongitude()));
                points.add(new LatLonPoint(four.get(2).getLatitude(), four.get(2).getLongitude()));
                points.add(new LatLonPoint(latlng4.getLatitude(), latlng4.getLongitude()));
                query.setPageNum(1);
                poiSearch.setBound(new PoiSearch.SearchBound(points));
                initPoiSearch(points);
                return;
            }

            listResult();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    private void listResult() throws AMapException {
        PoiResult poiResult = poiSearch.searchPOI();
        ArrayList<PoiItem> pois = poiResult.getPois();
        if (pois.size() != 0) {
//            for (int i = 0; i < pois.size(); i++) {
//                Log.e("商铺名", pois.get(i).getTitle() + pois.size());
//                Log.e("result", poiResult.getPageCount() + "");
//                    Log.e("距离", pois.get(i).getDistance() + "米");
//                    Log.e("地址", pois.get(i).getSnippet());
//
//            }
            results.addAll(pois);
            query.setPageNum(query.getPageNum() + 1);
            listResult();
        }
    }

    private AMapLocationClient mLocationClient;

    private double latitude;
    private double longitude;

    private void initLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode() != 0) {
                    tv_tips.setText("code:" + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    return;
                }
                String address = aMapLocation.getAddress();
                tv_tips.setText(address);

                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();

                lp = new LatLonPoint(latitude, longitude);
            }
        });
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);

        mLocationClient.setLocationOption(mLocationOption);
    }

    public void share(View view) {
        Toast.makeText(this, "暂无", Toast.LENGTH_SHORT).show();
    }

    public void export(View view) {
        if (results.size() != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ExcelUtil.export2Excel(results);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_tips.setText("导出完成，文件名为infoJ.xls，请在手机目录中查看");
                        }
                    });
                }
            }).start();
        } else {
            Toast.makeText(this, "当前无数据，请先查找", Toast.LENGTH_SHORT).show();
        }
    }
}
