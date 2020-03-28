package com.example.lesson.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lesson.R;
import com.example.lesson.app.base.MySupportFragment;
import com.example.lesson.app.data.entity.RecommendBean;
import com.example.lesson.app.eventbus.ChangeTag;
import com.example.lesson.di.component.DaggerHomeComponent;
import com.example.lesson.mvp.contract.HomeContract;
import com.example.lesson.mvp.presenter.HomePresenter;
import com.example.lesson.mvp.ui.activity.CategoryActivity;
import com.example.lesson.mvp.ui.adapter.TabAdapter;
import com.example.lesson.mvp.ui.view.NoAnimationViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.vondear.rxtool.RxSPTool;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class HomeFragment extends MySupportFragment<HomePresenter> implements HomeContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_home)
    SlidingTabLayout tabHome;
    @BindView(R.id.vp_content)
    NoAnimationViewPager vpContent;
    @BindView(R.id.toolbar_text)
    TextView mTitle;
    @BindView(R.id.ll_choose)
    LinearLayout choose;

    List<Integer> numbers;
    List<String> mTitles;
    List<String> tagIds;
    View view;
    TabAdapter adapter;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerHomeComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initToolBar();
        numbers = new ArrayList<>();
        numbers.add(432);
        numbers.add(1228);
        mPresenter.changeState(numbers, false);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    private void initToolBar() {
    }

    @Override
    public void setTitle(List<RecommendBean.DataBean.UserStagesBean> stagesBean) {
        if (stagesBean.size() > 0) {
            mTitle.setText(stagesBean.get(1).getTagName());
            choose.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                int leftId = Integer.parseInt(stagesBean.get(0).getTagId());
                int tagId = Integer.parseInt(stagesBean.get(1).getTagId());
                Bundle bundle = new Bundle();
                bundle.putInt("leftId", leftId);
                bundle.putInt("tagId", tagId);
                intent.putExtra("bundle", bundle);
                launchActivity(intent);
            });
        }
    }

    @Override
    public void setTabTitle(RecommendBean bean) {
        tagIds = new ArrayList<>();
        mTitles = new ArrayList<>();
        mTitles.add("精选");
        for (int i = 0; i < bean.getData().getSubTags().size(); i++) {
            mTitles.add(bean.getData().getSubTags().get(i).getTagName());
            tagIds.add(bean.getData().getSubTags().get(i).getTagId());
        }
        adapter = new TabAdapter(getChildFragmentManager(), mTitles, tagIds);
        vpContent.setAdapter(adapter);
        // 设置tab选项卡的默认选项
        tabHome.setViewPager(vpContent);
        tabHome.setOnClickListener(v ->
                vpContent.setCurrentItem(tabHome.getCurrentTab())
        );
        tabHome.setCurrentTab(0);
    }

    @Subscriber(tag = "ChangeTag")
    public void changTag(ChangeTag changeTag) {
        tabHome.setCurrentTab(0);
        mPresenter.changeState(changeTag.getList(), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        numbers = null;
        view = null;
        mTitles = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numbers = null;
        view = null;
        mTitles = null;
    }
}
