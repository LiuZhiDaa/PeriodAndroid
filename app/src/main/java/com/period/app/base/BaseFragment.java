package com.period.app.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.period.app.core.XCoreFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {
    private boolean mIsViewCreated = false;
    private boolean mVisibleToUser = false;
    private boolean mIsLoaded = false;
    Unbinder unbinder;
    protected Context mApplicationContext = XCoreFactory.getApplication();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(getLayoutId(), container, false);
        mIsViewCreated = true;
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    protected abstract void init(View rootView);

    protected abstract @LayoutRes
    int getLayoutId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        tryLazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        tryLazyLoad();
    }

    private void tryLazyLoad() {
        if (mIsLoaded) {
            return;
        }
        if (mIsViewCreated && mVisibleToUser) {
            onLazyLoad();
            mIsLoaded = true;
        }
    }

    /**
     * 懒加载
     */
    protected abstract void onLazyLoad();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (unbinder != null) {
//            unbinder.unbind();
//        }
//        mIsViewCreated = false;
//        mIsLoaded = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
