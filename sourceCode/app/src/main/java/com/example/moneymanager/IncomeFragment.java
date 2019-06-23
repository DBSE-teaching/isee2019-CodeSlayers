package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IncomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        MainActivity.isSummaryShown = false;

        getActivity().setTitle("Add New Transaction");

        tabLayout = (TabLayout) view.findViewById(R.id.tablelayout2_id);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        ViewPagerAdapter  adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new AddexpenseFragment(),"Addexpense");
        adapter.AddFragment(new AddincomeFragment(),"Addincome");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
