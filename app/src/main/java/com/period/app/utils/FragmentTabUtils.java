package com.period.app.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import java.util.List;

/**
 * wy
 */
public class FragmentTabUtils {
    private List<Fragment> fragments; // 一个tab页面对应一个Fragment
    private FragmentManager fragmentManager; // Fragment所属的Activity
    private int fragmentContentId; // Activity中所要被替换的区域的id
    private int currentTab; // 当前Tab页面索引
    private int currentIndex, lastIndex;
    private View activity;
    private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

    /**
     * @param fragmentManager
     * @param fragments
     * @param fragmentContentId
     * @param onRgsExtraCheckedChangedListener
     */
    public FragmentTabUtils(FragmentManager fragmentManager, List<Fragment> fragments, int fragmentContentId, OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
        this.fragmentContentId = fragmentContentId;
        this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
        showFragment(0);
    }

    public FragmentTabUtils(FragmentManager fragmentManager, List<Fragment> fragments, int fragmentContentId) {
        this(fragmentManager, fragments, fragmentContentId, null);
    }

    public FragmentTabUtils(View context, FragmentManager fragmentManager, List<Fragment> fragments, int fragmentContentId) {
        this(fragmentManager, fragments, fragmentContentId, null);
        this.activity = context;
        initListener();
    }

    private void initListener() {

    }


    public void showFragment(int i) {
        Fragment fragment = fragments.get(i);
        FragmentTransaction ft = obtainFragmentTransaction(i);
//                getCurrentFragment().onPause(); // 暂停当前tab
        getCurrentFragment().onStop(); // 暂停当前tab
        if (fragment.isAdded()) {
            fragment.onStart(); // 启动目标tab的fragment onStart()
//                    fragment.onResume(); // 启动目标tab的onResume()
//            if (fragment instanceof ShelfFragment) {
//                fragment.onResume();
//            } else if (fragment instanceof StoreFragment) {
//                fragment.onResume();
//            }
        } else {
            ft.add(fragmentContentId, fragment);
            ft.commitAllowingStateLoss();
        }
        showTab(i); // 显示目标tab

    }

    /**
     * 切换tab
     *
     * @param idx
     */
    private void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 获取一个带动画的FragmentTransaction
     *
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // 设置切换动画
//        if (index > currentTab) {
//            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
//        } else {
//            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
//        }
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }

    public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
        return onRgsExtraCheckedChangedListener;
    }

    public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
        this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
    }

    /**
     * 切换tab额外功能功能接口
     */
    public static interface OnRgsExtraCheckedChangedListener {
        public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index);
    }
}