package fun.qianxiao.originalassistant.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.bean.PostStatisticsResult;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivityPostStatisticBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.HLXStatisticsManager;
import fun.qianxiao.originalassistant.other.TextChanngedWatcher;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;

/**
 * PostStatisticActivity
 *
 * @Author QianXiao
 * @Date 2023/5/15
 */
public class PostStatisticActivity extends BaseActivity<ActivityPostStatisticBinding> implements ILoadingView {
    private MyLoadingDialog loadingDialog;
    private String key;
    private long userId;
    private HLXUserInfo hlxUserInfo;
    private PostStatisticsResult mPostStatisticsResult;

    @Override
    protected void initListener() {
        binding.tvStartStatistic.setOnClickListener(v -> {
            binding.tvStartStatistic.requestFocus();
            KeyboardUtils.hideSoftInput(getWindow());
            mPostStatisticsResult = null;
            postStatistic(getCurrentIncludeThreshold());
        });
        binding.etHlxIncludeScoreThreshold.addTextChangedListener(new TextChanngedWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!TextUtils.isEmpty(str)) {
                    int threshold = Integer.parseInt(str);
                    SPUtils.getInstance().put(SPConstants.KEY_STATISTIC_INCLUDE_SCORE_THRESHOLD, threshold);
                    if (mPostStatisticsResult == null) {
                        return;
                    }
                    changeIncludeThreshold(mPostStatisticsResult, threshold);
                }
            }
        });
        binding.rgPlateStatistic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_all_plate_statistic) {
                    SPUtils.getInstance().put(SPConstants.KEY_STATISTIC_PLATE, 0);
                } else if (checkedId == R.id.rb_original_plate_statistic) {
                    SPUtils.getInstance().put(SPConstants.KEY_STATISTIC_PLATE, 1);
                }
                if (mPostStatisticsResult == null) {
                    return;
                }
                setPrefixPieChartDataByRbId(mPostStatisticsResult, checkedId);
                setPieChartCenterDataByRbId(mPostStatisticsResult, checkedId);
                binding.chatPiePostPrefixCount.invalidate();
            }
        });
    }

    private int getCurrentIncludeThreshold() {
        String includeScoreThresholdStr = binding.etHlxIncludeScoreThreshold.getText().toString();
        if (TextUtils.isEmpty(includeScoreThresholdStr)) {
            return 0;
        }
        return Integer.parseInt(includeScoreThresholdStr);
    }

    private void setPrefixPieChartDataByRbId(PostStatisticsResult postStatisticsResult, int checkedId) {
        if (checkedId == R.id.rb_all_plate_statistic) {
            setPrefixPieChartData(postStatisticsResult.getPrefixCountMap());
        } else if (checkedId == R.id.rb_original_plate_statistic) {
            setPrefixPieChartData(postStatisticsResult.getPrefixCountOriginalMap());
        }
    }

    private void setPieChartCenterDataByRbId(PostStatisticsResult postStatisticsResult, int checkedId) {
        if (checkedId == R.id.rb_all_plate_statistic) {
            setPieChartCenterData(postStatisticsResult.getPostCount(), getIncludeCntByThreshold(postStatisticsResult, getCurrentIncludeThreshold()),
                    postStatisticsResult.getGoodPostCnt());
        } else if (checkedId == R.id.rb_original_plate_statistic) {
            setPieChartCenterData(postStatisticsResult.getOriginalCount(), getIncludeCntByThreshold(postStatisticsResult, getCurrentIncludeThreshold()),
                    postStatisticsResult.getGoodPostCnt());
        }
    }

    private int getIncludeCntByThreshold(PostStatisticsResult postStatisticsResult, int threshold) {
        List<Integer> scoreOriginalList = postStatisticsResult.getScoreOriginalList();
        if (scoreOriginalList == null) {
            return 0;
        }
        List<Integer> list2 = new ArrayList<>(scoreOriginalList.size() / 2);
        for (Integer integer : scoreOriginalList) {
            if (integer >= threshold) {
                list2.add(integer);
            }
        }
        return list2.size();
    }

    private void changeIncludeThreshold(PostStatisticsResult postStatisticsResult, int threshold) {
        setPieChartCenterDataByRbId(postStatisticsResult, binding.rgPlateStatistic.getCheckedRadioButtonId());
        binding.chatPiePostPrefixCount.invalidate();
    }

    @Override
    protected void initData() {
        showBackIcon();
        setTitle("发帖统计");
        key = HlxKeyLocal.read();
        userId = SPUtils.getInstance().getLong(SPConstants.KEY_HLX_USER_ID);
        if (TextUtils.isEmpty(key)) {
            finish();
            return;
        }
        if (userId == -1L) {
            finish();
            return;
        }
        initViewBySp();
        initChart(binding.chatPiePostPrefixCount);
        initUserInfo();
    }

    private void initViewBySp() {
        int threshold = SPUtils.getInstance().getInt(SPConstants.KEY_STATISTIC_INCLUDE_SCORE_THRESHOLD, 100);
        binding.etHlxIncludeScoreThreshold.setText(String.valueOf(threshold));
        int plateIndex = SPUtils.getInstance().getInt(SPConstants.KEY_STATISTIC_PLATE, 1);
        if (plateIndex == 0) {
            binding.rbAllPlateStatistic.setChecked(true);
        } else if (plateIndex == 1) {
            binding.rbOriginalPlateStatistic.setChecked(true);
        }
    }

    private void initChart(PieChart chart) {
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText("帖子前缀\n帖子数统计");
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setNoDataText("No Data");
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(5f);
        l.setWordWrapEnabled(true);
        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    private void initUserInfo() {
        HLXApiManager.INSTANCE.getUserInfo(key, userId, new HLXApiManager.OnGetUserInfoResult() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg) {
                if (success) {
                    PostStatisticActivity.this.hlxUserInfo = hlxUserInfo;
                    Glide.with(context).load(hlxUserInfo.getAvatarUrl())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(binding.ivAvatar);
                    binding.tvNick.setText(hlxUserInfo.getNick());
                    binding.tvId.setText("ID: " + hlxUserInfo.getUserId());
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void postStatistic(int includeScrollThreshold) {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("请先登录");
            return;
        }
        long userId = SPUtils.getInstance().getLong(SPConstants.KEY_HLX_USER_ID);
        if (userId == -1L) {
            ToastUtils.showShort("请设置userId");
            return;
        }
        if (hlxUserInfo == null) {
            ToastUtils.showShort("获取用户信息失败");
            return;
        }
        openLoadingDialog("开始统计");
        HLXStatisticsManager.StatisticParams statisticParams = new HLXStatisticsManager.StatisticParams();
        statisticParams.setIncludeScrollThreshold(includeScrollThreshold);
        statisticParams.setPostCount(hlxUserInfo.getPostCount());
        HLXStatisticsManager.statisticsPostData(key, userId, statisticParams, new HLXStatisticsManager.OnStatisticsListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int percent, String msg) {
                ThreadUtils.runOnUiThread(() -> openLoadingDialog(percent + "%"));
            }

            @Override
            public void onSuccess(PostStatisticsResult postStatisticsResult) {
                PostStatisticActivity.this.mPostStatisticsResult = postStatisticsResult;
                ThreadUtils.runOnUiThread(() -> {
                    displayStatisticData(postStatisticsResult);
                });
            }

            @Override
            public void onError(String errMsg) {
                ThreadUtils.runOnUiThread(() -> ToastUtils.showShort(errMsg));
            }

            @Override
            public void onComplete() {
                ThreadUtils.runOnUiThread(() -> closeLoadingDialog());
            }
        });
    }

    private void displayStatisticData(PostStatisticsResult postStatisticsResult) {
        setPrefixPieChartDataByRbId(postStatisticsResult, binding.rgPlateStatistic.getCheckedRadioButtonId());
        int postCount = 0;
        if (binding.rgPlateStatistic.getCheckedRadioButtonId() == R.id.rb_all_plate_statistic) {
            postCount = postStatisticsResult.getPostCount();
        } else if (binding.rgPlateStatistic.getCheckedRadioButtonId() == R.id.rb_original_plate_statistic) {
            postCount = postStatisticsResult.getOriginalCount();
        }
        setPieChartCenterData(postCount, postStatisticsResult.getInclusionCount(), postStatisticsResult.getGoodPostCnt());
        binding.chatPiePostPrefixCount.invalidate();
    }

    private void setPrefixPieChartData(Map<String, Integer> preChatData) {
        if (preChatData == null) {
            return;
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        Set<String> prefixs = preChatData.keySet();
        LogUtils.i(prefixs.size() + " ");
        for (String prefix : prefixs) {
            entries.add(new PieEntry(preChatData.get(prefix), prefix));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        binding.chatPiePostPrefixCount.setData(data);
        binding.chatPiePostPrefixCount.highlightValues(null);
        binding.chatPiePostPrefixCount.setMinimumHeight(ScreenUtils.getScreenWidth() + (int) binding.chatPiePostPrefixCount.getLegend().mNeededHeight);
    }

    private void setPieChartCenterData(int totalPost, int include, int good) {
        binding.chatPiePostPrefixCount.setCenterText("总发帖：" + totalPost + "\n" +
                "收录：" + include + "\n" +
                "精贴：" + good);
    }

    @Override
    public void openLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MyLoadingDialog(context);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        } else {
            loadingDialog.updateMessage(msg);
        }
    }

    @Override
    public void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
