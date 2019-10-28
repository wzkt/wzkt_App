package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.MonthProfileGet;
import com.Alan.eva.model.profile.FeverCountItem;
import com.Alan.eva.model.profile.MonthProfileData;
import com.Alan.eva.model.profile.TempRateData;
import com.Alan.eva.model.profile.TempTrendItem;
import com.Alan.eva.result.MonthProfileRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.core.AbsActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by CW on 2017/3/29.
 * 健康分析页面
 */
public class HealthDetailActivity extends AbsActivity implements IResultHandler, View.OnClickListener {
    AppCompatImageButton ib_health_detail_pre_month;
    AppCompatImageButton ib_health_detail_next_month;
    private AppCompatTextView tv_health_detail_calendar;
    private AppCompatTextView tv_health_detail_total_score;
    private AppCompatTextView tv_health_data_epilogue;
    private AppCompatTextView tv_health_data_tips;
    private AppCompatTextView tv_health_detail_fever_tips;
    private AppCompatTextView tv_health_detail_rate_tips;
    private LineChart lineChart_health_temp_trend;
    private BarChart bar_chart_health_fever_counts;
    private PieChart pie_chart_health_temp_distribute;

    private String cid;
    private int currMonth, month, currYear, year;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_healty_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        cid = intent.getStringExtra("cid");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        year = currYear = c.get(Calendar.YEAR);
        month = currMonth = c.get(Calendar.MONTH) + 1;
        hookMonth(0);
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_child_detail_title = (Toolbar) getView(R.id.tool_bar_health_detail_title);
        tool_bar_child_detail_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_child_detail_title.setTitle(R.string.health_analyze);
        setSupportActionBar(tool_bar_child_detail_title);
        tool_bar_child_detail_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_child_detail_title.setNavigationOnClickListener((View v) -> currFinish());

        ib_health_detail_pre_month = (AppCompatImageButton) getView(R.id.ib_health_detail_pre_month);
        tv_health_detail_calendar = (AppCompatTextView) getView(R.id.tv_health_detail_calendar);
        ib_health_detail_next_month = (AppCompatImageButton) getView(R.id.ib_health_detail_next_month);
        ib_health_detail_pre_month.setOnClickListener(this);
        ib_health_detail_next_month.setOnClickListener(this);

        tv_health_detail_total_score = (AppCompatTextView) getView(R.id.tv_health_detail_total_score);
        tv_health_data_epilogue = (AppCompatTextView) getView(R.id.tv_health_data_epilogue);
        tv_health_data_tips = (AppCompatTextView) getView(R.id.tv_health_data_tips);
        tv_health_detail_fever_tips = (AppCompatTextView) getView(R.id.tv_health_detail_fever_tips);
        tv_health_detail_rate_tips = (AppCompatTextView) getView(R.id.tv_health_detail_rate_tips);
        //线形图
        lineChart_health_temp_trend = (LineChart) getView(R.id.lineChart_health_temp_trend);
        lineChart_health_temp_trend.setDrawGridBackground(false);
        lineChart_health_temp_trend.getDescription().setEnabled(false);
        lineChart_health_temp_trend.setTouchEnabled(false);
        lineChart_health_temp_trend.setDragEnabled(false);
        lineChart_health_temp_trend.setScaleEnabled(false);
        lineChart_health_temp_trend.setPinchZoom(true);
        XAxis lineXAxis = lineChart_health_temp_trend.getXAxis();
        lineXAxis.enableGridDashedLine(10f, 10f, 0f);
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        LimitLine ll1 = new LimitLine(39f, getString(R.string.high_temp));
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(5f, 2f, 0f);
        ll1.setTextColor(Color.GRAY);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        LimitLine ll2 = new LimitLine(34f, getString(R.string.low_temp));
        ll2.setLineWidth(1f);
        ll2.enableDashedLine(5f, 2f, 0f);
        ll2.setTextColor(Color.GRAY);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        YAxis lineYAxis = lineChart_health_temp_trend.getAxisLeft();
        lineYAxis.setInverted(false);
        lineYAxis.removeAllLimitLines();
        lineYAxis.addLimitLine(ll1);
        lineYAxis.addLimitLine(ll2);
        lineYAxis.setAxisMaximum(42f);
        lineYAxis.setAxisMinimum(32f);
        lineYAxis.enableGridDashedLine(10f, 10f, 0f);
        lineYAxis.setDrawZeroLine(false);
        lineYAxis.setDrawLimitLinesBehindData(true);
        lineChart_health_temp_trend.getAxisRight().setEnabled(false);

        //饼状图
        bar_chart_health_fever_counts = (BarChart) getView(R.id.bar_chart_health_fever_counts);
        bar_chart_health_fever_counts.getDescription().setEnabled(false);
        bar_chart_health_fever_counts.setDrawGridBackground(true);
        bar_chart_health_fever_counts.setTouchEnabled(false);
        bar_chart_health_fever_counts.setScaleEnabled(false);
        bar_chart_health_fever_counts.setDragEnabled(false);
        XAxis xAxis = bar_chart_health_fever_counts.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        YAxis barRight = bar_chart_health_fever_counts.getAxisRight();
        barRight.setEnabled(false);
        YAxis barLeft = bar_chart_health_fever_counts.getAxisLeft();
        barLeft.setInverted(false);
        barLeft.setAxisMaximum(20);
        barLeft.setAxisMinimum(0);
        barLeft.setLabelCount(5, true);
        barLeft.setSpaceTop(15f);

        pie_chart_health_temp_distribute = (PieChart) getView(R.id.pie_chart_health_temp_distribute);
        pie_chart_health_temp_distribute.setUsePercentValues(true);
        pie_chart_health_temp_distribute.getDescription().setEnabled(false);
        pie_chart_health_temp_distribute.setTouchEnabled(false);
        pie_chart_health_temp_distribute.setDrawHoleEnabled(true);
        pie_chart_health_temp_distribute.setRotationEnabled(false);
        pie_chart_health_temp_distribute.setHighlightPerTapEnabled(true);
        pie_chart_health_temp_distribute.setHoleColor(Color.WHITE);
        pie_chart_health_temp_distribute.setCenterText("温度占比");
        pie_chart_health_temp_distribute.setTransparentCircleColor(Color.LTGRAY);
        pie_chart_health_temp_distribute.setHoleRadius(58f);
        pie_chart_health_temp_distribute.setTransparentCircleRadius(61f);
        pie_chart_health_temp_distribute.setDrawCenterText(true);
        pie_chart_health_temp_distribute.setRotationAngle(0);
    }

    private final int MONTH_PROFILE = 0x0077;

    private void getProfile(int year, int month) {
        MonthProfileGet get = new MonthProfileGet();
        get.code(MONTH_PROFILE);
        get.handler(this);
        get.setYear(String.valueOf(year));
        get.setMonth(String.valueOf(month));
        get.setCid(cid);
        get.get();
    }


    @Override
    public void handleStart(int code) {
        if (code == MONTH_PROFILE) {
            LogUtil.info("孩子月统计请求开始");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == MONTH_PROFILE) {
            MonthProfileRes res = Tools.json2Bean(result, MonthProfileRes.class);
            if (res.isOk()) {
                MonthProfileData profileData = res.getData();
                int score = profileData.getScore();
                tv_health_detail_total_score.setText(String.valueOf(score));
                String epilogue = profileData.getEpilogue();
                tv_health_data_epilogue.setText(epilogue);
                String summary = profileData.getSummary();
                tv_health_data_tips.setText(summary);
                String feverTips = profileData.getFeverTips();
                tv_health_detail_fever_tips.setText(feverTips);
                String rateTips = profileData.getRateTips();
                tv_health_detail_rate_tips.setText(rateTips);
                ArrayList<TempTrendItem> trendAVGList = profileData.getTrend();
                ArrayList<Entry> entriesAVG = new ArrayList<>();
                if (!Tools.isListEmpty(trendAVGList)) {
                    for (TempTrendItem temp : trendAVGList) {
                        Entry entry = new Entry(temp.getDay(), temp.getTemp());
                        entriesAVG.add(entry);
                    }
                }
                LineDataSet lineDataSetAVG = new LineDataSet(entriesAVG, "温度均值");
                lineDataSetAVG.setDrawIcons(false);
                lineDataSetAVG.setDrawCircles(false);
                lineDataSetAVG.setDrawValues(false);
                lineDataSetAVG.setDrawFilled(false);
                lineDataSetAVG.setColor(Color.BLUE);
                lineDataSetAVG.setLineWidth(1.5f);
                LineData lineData = new LineData(lineDataSetAVG);
                List<ILineDataSet> sets = lineData.getDataSets();
                for (ILineDataSet iSet : sets) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                lineChart_health_temp_trend.setData(lineData);
                lineChart_health_temp_trend.animateY(700);

                ArrayList<FeverCountItem> countList = profileData.getFever();
                if (!Tools.isListEmpty(countList)) {
                    ArrayList<BarEntry> barEntries = new ArrayList<>(countList.size());
                    for (FeverCountItem item : countList) {
                        int count = item.getCount();
                        int day = item.getDay();
                        BarEntry entry = new BarEntry(day, count);
                        barEntries.add(entry);
                    }
                    BarDataSet barDataSet = new BarDataSet(barEntries, "发烧次数");
                    barDataSet.setColors(Color.rgb(251, 122, 122));
                    barDataSet.setBarShadowColor(Color.rgb(203, 203, 203));
                    ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
                    barDataSets.add(barDataSet);
                    BarData barData = new BarData(barDataSets);
                    barData.setBarWidth(0.9f);
                    barData.setDrawValues(false);
                    bar_chart_health_fever_counts.setData(barData);
                    bar_chart_health_fever_counts.animateY(700);
                }

                TempRateData rateData = profileData.getRate();
                ArrayList<PieEntry> pieEntries = new ArrayList<>(3);
                int normal = rateData.getNormal();
                int low = rateData.getLow();
                int high = rateData.getHigh();
                PieEntry normalEntry = new PieEntry(normal, "正常");
                PieEntry lowEntry = new PieEntry(low, "低温");
                PieEntry highEntry = new PieEntry(high, "发烧");
                pieEntries.add(normalEntry);
                pieEntries.add(lowEntry);
                pieEntries.add(highEntry);
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "温度比例图");
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.rgb(69, 139, 0));
                colors.add(Color.rgb(0, 154, 205));
                colors.add(Color.rgb(255, 130, 71));
                pieDataSet.setColors(colors);
                pieDataSet.setValueLinePart1OffsetPercentage(80.f);
                pieDataSet.setValueLinePart1Length(0.2f);
                pieDataSet.setValueLinePart2Length(0.4f);
                pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData data = new PieData(pieDataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.rgb(41, 41, 41));
                pie_chart_health_temp_distribute.setData(data);
                pie_chart_health_temp_distribute.highlightValues(null);
                pie_chart_health_temp_distribute.invalidate();
            } else {
                showTips(res.msg());
                int c = res.code();
                if (c == 104) {
                    tv_health_detail_total_score.setText("0");
                    tv_health_data_epilogue.setText("本月无体温记录");
                    tv_health_data_tips.setText("");
                    tv_health_detail_fever_tips.setText("");
                    tv_health_detail_rate_tips.setText("");
                    lineChart_health_temp_trend.clear();
                    bar_chart_health_fever_counts.clear();
                    pie_chart_health_temp_distribute.clear();
                }
            }
        }
    }


    @Override
    public void handleFinish(int code) {
        if (code == MONTH_PROFILE) {
            LogUtil.info("孩子月统计请求结束");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == MONTH_PROFILE) {
            LogUtil.info("孩子月统计请求错误");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_health_detail_pre_month:
                hookMonth(-1);
                break;
            case R.id.ib_health_detail_next_month:
                hookMonth(1);
                break;
        }
    }

    private void hookMonth(int deviation) {
        month += deviation;
        if (month <= 0) {
            month = 12;
            year -= 1;
        } else if (month >= 12) {
            month = 1;
            year += 1;
        }
        getProfile(year, month);
        if (year >= currYear) {
            ib_health_detail_next_month.setClickable(month < currMonth);
        } else {
            ib_health_detail_next_month.setClickable(true);
        }
        LogUtil.info(year + "年" + month + "月");
        tv_health_detail_calendar.setText(String.valueOf(year + "年" + month + "月"));
    }
}
