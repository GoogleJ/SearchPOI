package per.j;

import android.os.Environment;
import android.text.TextUtils;

import com.amap.api.services.core.PoiItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {

    //jxl 自动设置宽的方式：  每次添加一个cell 都要判断一下插入内容的长度是否比当前设置的宽度长，长则重新设置
    public static void export2Excel(ArrayList<PoiItem> data) {

        try {
            WritableCellFormat wc = new WritableCellFormat();
            wc.setAlignment(Alignment.CENTRE);
            wc.setBorder(Border.ALL, BorderLineStyle.THIN);

            File file = new File(Environment.getExternalStorageDirectory(), "infoJ.xls");
            if (file.exists()) {
                file.delete();
            }

            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(file));

            WritableSheet sheet = wwb.createSheet("商铺信息", 0);

            Label label;
            label = new Label(0, 0, "名称", wc);
            sheet.setColumnView(0, new String("名称").getBytes().length + 4);
            sheet.addCell(label);

            label = new Label(1, 0, "电话", wc);
            sheet.setColumnView(1, new String("电话").getBytes().length + 4);
            sheet.addCell(label);

            label = new Label(2, 0, "经度,纬度", wc);
            sheet.setColumnView(2, new String("经度,纬度").getBytes().length + 4);
            sheet.addCell(label);

            label = new Label(3, 0, "详细地址", wc);
            sheet.setColumnView(3, new String("详细地址").getBytes().length + 4);
            sheet.addCell(label);

            label = new Label(4, 0, "类型", wc);
            sheet.setColumnView(4, new String("类型").getBytes().length + 4);
            sheet.addCell(label);

            label = new Label(5, 0, "所在楼层", wc);
            sheet.setColumnView(5, new String("所在楼层").getBytes().length + 4);
            sheet.addCell(label);

            for (int i = 0; i < data.size(); i++) {
                PoiItem item = data.get(i);

                label = new Label(0, i + 1, item.getTitle(), wc);
                if (sheet.getColumnWidth(0) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(0, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);

                label = new Label(1, i + 1, TextUtils.isEmpty(item.getTel()) ? "暂无" : item.getTel(), wc);
                if (sheet.getColumnWidth(1) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(1, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);

                label = new Label(2, i + 1, item.getLatLonPoint().getLongitude() + "," + item.getLatLonPoint().getLatitude(), wc);
                if (sheet.getColumnWidth(2) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(2, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);

                label = new Label(3, i + 1, item.getSnippet(), wc);
                if (sheet.getColumnWidth(3) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(3, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);

                label = new Label(4, i + 1, item.getTypeDes(), wc);
                if (sheet.getColumnWidth(4) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(4, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);

                label = new Label(5, i + 1, TextUtils.isEmpty(item.getIndoorData().getFloorName()) ? "暂无" : item.getIndoorData().getFloorName(), wc);
                if (sheet.getColumnWidth(5) < label.getContents().getBytes().length + 4) {
                    sheet.setColumnView(5, label.getContents().getBytes().length + 4);
                }
                sheet.addCell(label);
            }

            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
