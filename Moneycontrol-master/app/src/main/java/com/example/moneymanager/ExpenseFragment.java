package com.example.moneymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

public class ExpenseFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tablelayout_id);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        ViewPagerAdapter  adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new ShowexpenseFragment(),"Showexpense");
        adapter.AddFragment(new AddexpenseFragment(),"Addexpense");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
