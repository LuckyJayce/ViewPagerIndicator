package com.shizhefei.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Copyright 2019 shizhefei（LuckyJayce）
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * https://github.com/LuckyJayce/ViewPagerIndicator
 * <p>
 * 该代理LazyFragment的思想源于 https://github.com/shenguojun/LazyFragmentTest
 */
public final class ProxyLazyFragment extends Fragment {
    private boolean isInit = false;
    private static final String EXTRA_CLASS_NAME = "extra_class_name";
    private static final String EXTRA_ARGUMENTS = "extra_arguments";
    private FrameLayout view;
    private static final int fragmentId = 666;
    private int isVisibleToUserState = VISIBLE_STATE_NOTSET;
    //未设置值
    private static final int VISIBLE_STATE_NOTSET = -1;
    //可见
    private static final int VISIBLE_STATE_VISIABLE = 1;
    //不可见
    private static final int VISIBLE_STATE_GONE = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = new FrameLayout(getContext());
        view.setId(fragmentId);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisibleToUserState = isVisibleToUser ? VISIBLE_STATE_VISIABLE : VISIBLE_STATE_GONE;
        if (isVisibleToUser && !isInit && view != null) {
            isInit = true;
            addRealFragment();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //为什么不直接getUserVisibleHint();而是通过自己存isVisibleToUserState变量判断
        //因为v4的25的版本 已经调用 setUserVisibleHint(true)，结果到这里getUserVisibleHint是false
        // （ps:看了FragmentManager源码Fragment被重新创建有直接赋值isVisibleToUser不知道是不是那里和之前v4有改动的地方）
        //所以我默认VISIBLE_STATE_NOTSET，之前没有调用setUserVisibleHint方法，就用系统的getUserVisibleHint，否则就用setUserVisibleHint后保存的值
        //总之就是调用了setUserVisibleHint 就使用setUserVisibleHint的值
        boolean isVisibleToUser;
        if (isVisibleToUserState == VISIBLE_STATE_NOTSET) {
            isVisibleToUser = getUserVisibleHint();
        } else {
            isVisibleToUser = isVisibleToUserState == VISIBLE_STATE_VISIABLE;
        }
        if (isVisibleToUser && !isInit) {
            isInit = true;
            addRealFragment();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
        isInit = false;
    }

    @Nullable
    private Fragment getRealFragment() {
        return getChildFragmentManager().findFragmentById(fragmentId);
    }

    private void addRealFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentById(fragmentId);
        if (fragment == null) {
            Bundle arguments = getArguments();
            String fragmentClassName = arguments.getString(EXTRA_CLASS_NAME);
            Bundle realArguments = arguments.getParcelable(EXTRA_ARGUMENTS);
            fragment = Fragment.instantiate(getContext(), fragmentClassName);
            fragment.setArguments(realArguments);
            getChildFragmentManager().beginTransaction().replace(fragmentId, fragment).commitAllowingStateLoss();
        }
    }

    public static ProxyLazyFragment lazy(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        ProxyLazyFragment fragment = new ProxyLazyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CLASS_NAME, fragmentClass.getName());
        bundle.putParcelable(EXTRA_ARGUMENTS, arguments);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ProxyLazyFragment lazy(@NonNull Class<? extends Fragment> fragmentClass) {
        return lazy(fragmentClass, null);
    }
}
