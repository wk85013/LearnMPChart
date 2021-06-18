package ute.com.learnmpchart;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String[] suppliers = new String[]{
            "19Q1", "19Q2", "19Q3", "19Q4", "20Q1", "20Q2", "20Q3", "20Q4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setCombinedChart();
        setScatterChart();
//        setBarChart();
//        setLineChart();
//        setBarChart2();

    }

    private void setScatterChart() {
        int max = 100;
        final String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月"};
        CombinedChart combinedChart = findViewById(R.id.chart_scatterChart);
        combinedChart.setDrawBorders(false); // 顯示邊界
        combinedChart.getDescription().setEnabled(false);  // 不顯示備註資訊
        combinedChart.setPinchZoom(false); // 比例縮放
        combinedChart.setDoubleTapToZoomEnabled(false); // 雙點擊縮放
        combinedChart.getLegend().setEnabled(false);
        combinedChart.setScaleEnabled(false);//XY軸縮放
//        combinedChart.setTouchEnabled(false);
//        combinedChart.getXAxis().setGridColor(Color.RED);//设置纵向网格线条颜色
//        combinedChart.getAxisLeft().setGridColor(Color.GREEN);//设置横向网格颜色


        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false);
        /*解決左右兩端柱形圖只顯示一半的情況 只有使用CombinedChart時會出現，如果單獨使用BarChart不會有這個問題*/
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(months.length - 0.5f);
        xAxis.setLabelCount(months.length); // 設定X軸標籤數量
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 設定X軸標籤位置，BOTTOM在底部顯示，TOP在頂部顯示
        xAxis.setValueFormatter(new ValueFormatter() {// 轉換要顯示的標籤文字，value值預設是int從0開始

            @Override
            public String getFormattedValue(float value) {
                return months[(int) value];
            }

        });


        YAxis axisLeft = combinedChart.getAxisLeft(); // 獲取左邊Y軸操作類
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisMinimum(0); // 設定最小值
        axisLeft.setAxisMaximum(max); // 設定最大值
        axisLeft.setGranularity(1); // 設定Label間隔
        axisLeft.setLabelCount(5);// 設定標籤數量
        axisLeft.setValueFormatter(new ValueFormatter() { // 在左邊Y軸標籤文字後加上%號
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "萬";
            }
        });

        YAxis axisRight = combinedChart.getAxisRight(); // 獲取右邊Y軸操作類
        axisRight.setDrawGridLines(false); // 不繪製背景線，上面左邊Y軸並沒有設定此屬性，因為不設定預設是顯示的
        axisRight.setGranularity(5000); // 設定Label間隔
        axisRight.setAxisMinimum(0); // 設定最小值
        axisRight.setAxisMaximum(50000); // 設定最大值
        axisRight.setLabelCount(10); // 設定標籤個數
        axisRight.setEnabled(false);//右侧Y轴关闭
//        axisRight.setValueFormatter(new ValueFormatter() { // 在右邊Y軸標籤文字
//            @Override
//            public String getFormattedValue(float value) {
//                return String.valueOf((int) value);
//            }
//
//        });

        //圖示1
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher, null);
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

        //圖示2
        Drawable drawableCheck = getResources().getDrawable(R.mipmap.outline_check_circle_black_20, null);
        Bitmap bitmapCheck = getBitmapFromDrawable(drawableCheck);
        Drawable newDrawableCheck = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmapCheck, 50, 50, true));

        ArrayList<Entry> yVals = new ArrayList<>();
        HashMap<Float, Building> buildingArrayList = new HashMap<>();

        for (int i = 0; i < months.length; i++) {//假資料
            Building building = new Building();
            float price = getRandom(max, 0);
            building.setPrice(price);
            building.setPing(getRandom(50, 0));
            building.setFloor((int) getRandom(20, 0));

            float xAxisValue = 0;
            if (i == 2) {
                xAxisValue = 2.5f;
                yVals.add(new Entry(xAxisValue, building.getPrice(), newDrawableCheck));//产生数据
            } else {
                xAxisValue = i;
                yVals.add(new Entry(xAxisValue, building.getPrice(), newDrawable));//产生数据
            }
            building.setxAxis(xAxisValue);
            buildingArrayList.put(xAxisValue, building);
        }
        //散布圖
        ScatterDataSet scatterDataSet = new ScatterDataSet(yVals, "小明每月支出");
        scatterDataSet.setDrawIcons(true);
        scatterDataSet.setDrawValues(false);
        scatterDataSet.setDrawHighlightIndicators(false);//關閉點擊顯示十字交叉線
        ScatterData scatterData = new ScatterData();
        scatterData.addDataSet(scatterDataSet);
        //背景長條圖
        BarDataSet barDatasetBackground1 = setBackground1BarChart(100000);
        BarDataSet barDatasetBackground2 = setBackground2BarChart(100000);
        BarData barData = new BarData();
        barData.addDataSet(barDatasetBackground1);// 新增一組柱形圖，如果有多組柱形圖資料，則可以多次addDataSet來設定
        barData.addDataSet(barDatasetBackground2);// 新增一組柱形圖，如果有多組柱形圖資料，則可以多次addDataSet來設定
        barData.setBarWidth(1f);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(scatterData);
        combinedChart.setData(combinedData);//设置对应数据

        XYMarkerView myMarkerView = new XYMarkerView(this, buildingArrayList);
        myMarkerView.setChartView(combinedChart);
        combinedChart.setMarker(myMarkerView);//點擊顯示文字敘述
    }

    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    private void setCombinedChart() {

        CombinedChart combinedChart = findViewById(R.id.chart_combinedChart);
        combinedChart.setDrawBorders(false); // 顯示邊界
        combinedChart.getDescription().setEnabled(false);  // 不顯示備註資訊
        combinedChart.setPinchZoom(false); // 比例縮放
        combinedChart.setTouchEnabled(false);
        combinedChart.getLegend().setEnabled(false);//圖例關閉
//        combinedChart.setDrawGridBackground(true);
//        combinedChart.setGridBackgroundColor(Color.RED);
//        combinedChart.animateY(1500);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false);
        /*解決左右兩端柱形圖只顯示一半的情況 只有使用CombinedChart時會出現，如果單獨使用BarChart不會有這個問題*/
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(suppliers.length - 0.5f);
        xAxis.setLabelCount(suppliers.length); // 設定X軸標籤數量
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 設定X軸標籤位置，BOTTOM在底部顯示，TOP在頂部顯示
        xAxis.setValueFormatter(new ValueFormatter() {// 轉換要顯示的標籤文字，value值預設是int從0開始

            @Override
            public String getFormattedValue(float value) {
                return suppliers[(int) value];
            }

        });

        YAxis axisLeft = combinedChart.getAxisLeft(); // 獲取左邊Y軸操作類
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisMinimum(0); // 設定最小值
        axisLeft.setAxisMaximum(100); // 設定最大值
        axisLeft.setGranularity(1); // 設定Label間隔
        axisLeft.setLabelCount(10);// 設定標籤數量
        axisLeft.setValueFormatter(new ValueFormatter() { // 在左邊Y軸標籤文字後加上%號
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        YAxis axisRight = combinedChart.getAxisRight(); // 獲取右邊Y軸操作類
        axisRight.setDrawGridLines(false); // 不繪製背景線，上面左邊Y軸並沒有設定此屬性，因為不設定預設是顯示的
        axisRight.setGranularity(5000); // 設定Label間隔
        axisRight.setAxisMinimum(0); // 設定最小值
        axisRight.setAxisMaximum(50000); // 設定最大值
        axisRight.setLabelCount(10); // 設定標籤個數
        axisRight.setValueFormatter(new ValueFormatter() { // 在右邊Y軸標籤文字後加上%號
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }

        });
        //長條圖
        BarDataSet barDataset = setTransBarChart();
        BarDataSet barDatasetBackground1 = setBackground1BarChart(100000);
        BarDataSet barDatasetBackground2 = setBackground2BarChart(100000);
        BarData barData = new BarData();
        barData.addDataSet(barDatasetBackground1);// 新增一組柱形圖，如果有多組柱形圖資料，則可以多次addDataSet來設定
        barData.addDataSet(barDatasetBackground2);// 新增一組柱形圖，如果有多組柱形圖資料，則可以多次addDataSet來設定
        barData.addDataSet(barDataset);// 新增一組柱形圖，如果有多組柱形圖資料，則可以多次addDataSet來設定
//        barData.setBarWidth(1f);

        //折線圖
        LineDataSet lineDataBuild = setBuildLineChart();
        LineDataSet lineDataDepartment = setDepartmentLineChart();
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataBuild);
        lineData.addDataSet(lineDataDepartment);

        CombinedData combinedData = new CombinedData(); // 建立組合圖的資料來源
        combinedData.setData(barData);  // 新增柱形圖資料來源
        combinedData.setData(lineData); // 新增折線圖資料來源
        combinedChart.setData(combinedData); // 為組合圖設定資料來源
    }


    @NonNull
    private BarDataSet setTransBarChart() {
        /**
         * 初始化柱形圖的資料
         * 此處用suppliers的數量做迴圈，因為總共所需要的資料來源數量應該和標籤個數一致
         * 其中BarEntry是柱形圖資料來源的實體類，包裝xy座標資料
         */
        /******************BarData start********************/
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < suppliers.length; i++) {
            barEntries.add(new BarEntry(i, getRandom(50000, 50)));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "買賣移轉");  // 新建一組柱形圖，"LAR"為本組柱形圖的Label
        barDataSet.setColor(Color.parseColor("#008800")); // 設定柱形圖顏色
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT); //设置线数据依赖于右侧y轴
        barDataSet.setDrawValues(false);
//        barDataSet.setValueTextColor(Color.parseColor("#0288d1")); //  設定柱形圖頂部文字顏色

        /******************BarData end********************/
        return barDataSet;
    }

    @NonNull
    private BarDataSet setBackground1BarChart(int max) {
        /**
         * 初始化柱形圖的資料
         * 此處用suppliers的數量做迴圈，因為總共所需要的資料來源數量應該和標籤個數一致
         * 其中BarEntry是柱形圖資料來源的實體類，包裝xy座標資料
         */
        /******************BarData start********************/
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < suppliers.length; i++) {
            if (i % 2 == 0) {
                barEntries.add(new BarEntry(i, max));
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");  // 新建一組柱形圖，"LAR"為本組柱形圖的Label
        barDataSet.setColor(Color.parseColor("#B4B4B4")); // 設定柱形圖顏色
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT); //设置线数据依赖于右侧y轴
        barDataSet.setDrawValues(false);
//        barDataSet.setValueTextColor(Color.parseColor("#0288d1")); //  設定柱形圖頂部文字顏色
//        barDataSet.setBarBorderColor(Color.parseColor("#B4B4B4"));
//        barDataSet.setBarBorderWidth(6f);
        /******************BarData end********************/
        return barDataSet;
    }

    @NonNull
    private BarDataSet setBackground2BarChart(int max) {
        /**
         * 初始化柱形圖的資料
         * 此處用suppliers的數量做迴圈，因為總共所需要的資料來源數量應該和標籤個數一致
         * 其中BarEntry是柱形圖資料來源的實體類，包裝xy座標資料
         */
        /******************BarData start********************/
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < suppliers.length; i++) {
            if (i % 2 == 1) {
                barEntries.add(new BarEntry(i, max));
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");  // 新建一組柱形圖，"LAR"為本組柱形圖的Label
        barDataSet.setColor(Color.parseColor("#787878")); // 設定柱形圖顏色
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT); //设置线数据依赖于右侧y轴
        barDataSet.setDrawValues(false);//不顯示數值
//        barDataSet.setValueTextColor(Color.parseColor("#0288d1")); //  設定柱形圖頂部文字顏色
//        barDataSet.setBarBorderColor(Color.parseColor("#787878"));
//        barDataSet.setBarBorderWidth(6f);
        /******************BarData end********************/
        return barDataSet;
    }

    @NonNull
    private LineDataSet setBuildLineChart() {
        /**
         * 初始化折線圖資料
         * 說明同上
         */
        /******************LineData start********************/
        List<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < suppliers.length; i++) {
            lineEntries.add(new Entry(i, getRandom(100, 0)));
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "大樓");
        lineDataSet.setColor(Color.parseColor("#E44D32"));
        lineDataSet.setCircleColor(Color.parseColor("#E44D32"));
        lineDataSet.setCircleHoleColor(Color.parseColor("#E44D32"));
        lineDataSet.setCircleRadius(5);
        lineDataSet.setDrawValues(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); //设置线数据依赖于左侧y轴
//        lineDataSet.setValueTextColor(Color.parseColor("#f44336"));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setHighlightEnabled(false);

        return lineDataSet;
        /******************LineData end********************/
    }

    private LineDataSet setDepartmentLineChart() {
        /**
         * 初始化折線圖資料
         * 說明同上
         */
        /******************LineData start********************/
        List<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < suppliers.length; i++) {
            lineEntries.add(new Entry(i, getRandom(100, 0)));
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "公寓");
        lineDataSet.setColor(Color.parseColor("#1E336D"));
        lineDataSet.setCircleColor(Color.parseColor("#1E336D"));
        lineDataSet.setCircleHoleColor(Color.parseColor("#1E336D"));
        lineDataSet.setCircleRadius(5);
        lineDataSet.setDrawValues(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); //设置线数据依赖于左侧y轴
//        lineDataSet.setValueTextColor(Color.parseColor("#f44336"));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setHighlightEnabled(false);

        return lineDataSet;
        /******************LineData end********************/
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    private void setBarChart2() {
        BarChart chart_bar = findViewById(R.id.chart_bar2);
        chart_bar.setData(getBarData2());
        chart_bar.setScaleEnabled(false);//禁止雙指縮放
        chart_bar.setDoubleTapToZoomEnabled(false);//禁止雙擊縮放
        Description desc = new Description();
        desc.setText("");

        chart_bar.setDescription(desc);

        //Y軸取整數
        YAxis leftAxis = chart_bar.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });

        YAxis rightAxis = chart_bar.getAxisRight();
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });
    }

    private List<BarEntry> getChartData2() {


        List<BarEntry> chartData = new ArrayList<>();

        int revenue_A = (int) (Math.random() * 50 + 1);
        int revenue_B = (int) (Math.random() * 50 + 1);
        BarEntry barEntry = new BarEntry(0, new float[]{revenue_A, revenue_B});
        chartData.add(barEntry);

        return chartData;
    }

    private BarData getBarData2() {
        BarDataSet dataSetA = new BarDataSet(getChartData2(), "LabelA");
        dataSetA.setColors(getChartColors());
        BarDataSet dataSetB = new BarDataSet(getChartData2(), "LabelB");
        dataSetB.setColors(getChartColors());
        BarDataSet dataSetC = new BarDataSet(getChartData2(), "LabelC");
        dataSetC.setColors(getChartColors());
        BarDataSet dataSetD = new BarDataSet(getChartData2(), "LabelD");
        dataSetD.setColors(getChartColors());
        BarDataSet dataSetE = new BarDataSet(getChartData2(), "LabelE");
        dataSetE.setColors(getChartColors());
        BarDataSet dataSetF = new BarDataSet(getChartData2(), "LabelF");
        dataSetF.setColors(getChartColors());
        BarDataSet dataSetG = new BarDataSet(getChartData2(), "LabelG");
        dataSetG.setColors(getChartColors());

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        dataSets.add(dataSetB);
        dataSets.add(dataSetC);
        dataSets.add(dataSetD);
        dataSets.add(dataSetE);
        dataSets.add(dataSetF);
        dataSets.add(dataSetG);


        BarData barData = new BarData(dataSets);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        return barData;
    }

    private int[] getChartColors() {
        int[] colors = new int[]{Color.RED,
                Color.BLUE,
                Color.GREEN
        };
        return colors;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void setLineChart() {
        LineChart chart_line = findViewById(R.id.chart_line);
        chart_line.setData(getLinetData());
        chart_line.setScaleEnabled(false);//禁止雙指縮放
        chart_line.setDoubleTapToZoomEnabled(false);//禁止雙擊縮放
        Description desc = new Description();
        desc.setText("");
        chart_line.setDescription(desc);

        //Y軸取整數
        YAxis leftAxis = chart_line.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });

        YAxis rightAxis = chart_line.getAxisRight();
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });
    }

    private LineData getLinetData() {
        LineDataSet dataSetA = new LineDataSet(getChartData_Entry(), "LabelA");
        dataSetA.setColor(Color.RED);
/*        LineDataSet dataSetB = new LineDataSet(getChartData_Entry(), "LabelB");
        dataSetB.setColor(Color.YELLOW);
        LineDataSet dataSetC = new LineDataSet(getChartData_Entry(), "LabelC");
        dataSetC.setColor(Color.GREEN);
        LineDataSet dataSetD = new LineDataSet(getChartData_Entry(), "LabelD");
        dataSetD.setColor(Color.BLUE);
        LineDataSet dataSetE = new LineDataSet(getChartData_Entry(), "LabelE");
        dataSetE.setColor(Color.BLACK);
        LineDataSet dataSetF = new LineDataSet(getChartData_Entry(), "LabelF");
        dataSetF.setColor(Color.GRAY);
        LineDataSet dataSetG = new LineDataSet(getChartData_Entry(), "LabelG");
        dataSetG.setColor(Color.LTGRAY);*/

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
/*        dataSets.add(dataSetB);
        dataSets.add(dataSetC);
        dataSets.add(dataSetD);
        dataSets.add(dataSetE);
        dataSets.add(dataSetF);
        dataSets.add(dataSetG);*/

        LineData lineData = new LineData(dataSets);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        return lineData;
    }

    private List<String> getLabels_Line() {
        List<String> chartLabels = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            chartLabels.add("");
        }

        return chartLabels;
    }

    private List<Entry> getChartData_Entry() {
        List<Entry> chartData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int random = (int) (Math.random() * 50 + 1);
            Entry entry = new Entry(i, random);
            chartData.add(entry);
        }
        return chartData;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setBarChart() {
        BarChart chart_bar = findViewById(R.id.chart_bar);
        chart_bar.setData(getBarData());
        chart_bar.setScaleEnabled(false);//禁止雙指縮放
        chart_bar.setDoubleTapToZoomEnabled(false);//禁止雙擊縮放
//        Description desc = new Description();
//        desc.setText("");
//        chart_bar.setDescription(desc);

        //Y軸取整數
        YAxis leftAxis = chart_bar.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });

        YAxis rightAxis = chart_bar.getAxisRight();
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);//轉整數
            }
        });
    }

    private BarData getBarData() {
        BarDataSet dataSetA = new BarDataSet(getChartData(0), "LabelA");
        dataSetA.setColor(Color.RED);
        BarDataSet dataSetB = new BarDataSet(getChartData(1), "LabelB");
        dataSetB.setColor(Color.YELLOW);
        BarDataSet dataSetC = new BarDataSet(getChartData(2), "LabelC");
        dataSetC.setColor(Color.GREEN);
        BarDataSet dataSetD = new BarDataSet(getChartData(3), "LabelD");
        dataSetD.setColor(Color.BLUE);
        BarDataSet dataSetE = new BarDataSet(getChartData(4), "LabelE");
        dataSetE.setColor(Color.BLACK);
        BarDataSet dataSetF = new BarDataSet(getChartData(5), "LabelF");
        dataSetF.setColor(Color.GRAY);
        BarDataSet dataSetG = new BarDataSet(getChartData(6), "LabelG");
        dataSetG.setColor(Color.LTGRAY);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        dataSets.add(dataSetB);
        dataSets.add(dataSetC);
        dataSets.add(dataSetD);
        dataSets.add(dataSetE);
        dataSets.add(dataSetF);
        dataSets.add(dataSetG);

        BarData barData = new BarData(dataSets);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        return barData;
    }

    private List<BarEntry> getChartData(int position) {
        List<BarEntry> chartData = new ArrayList<>();

        int random = (int) (Math.random() * 50 + 1);
        BarEntry barEntry = new BarEntry(position, random);
        chartData.add(barEntry);

        return chartData;
    }
}
