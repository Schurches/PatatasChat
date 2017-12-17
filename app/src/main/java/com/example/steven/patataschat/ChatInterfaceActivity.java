package com.example.steven.patataschat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class ChatInterfaceActivity extends AppCompatActivity {

    private ViewPager fragments_visualizer;
    private SectionPagerAdapter fragments_adapter;
    private BottomNavigationView nav_view;
    //private Toolbar options_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_interface);

        fragments_adapter = new SectionPagerAdapter(getSupportFragmentManager());
        iniViewPager();
        iniNavigationView();
    }

    private void iniViewPager(){
        fragments_visualizer = findViewById(R.id.pager);
        setupViewPager(fragments_visualizer);
        fragments_visualizer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0: //Chat channel
                        nav_view.setSelectedItemId(R.id.chats);
                        break;
                    case 1:
                        nav_view.setSelectedItemId(R.id.profile);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void iniNavigationView(){
        nav_view = findViewById(R.id.navigation_view);
        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.chats:
                        fragments_visualizer.setCurrentItem(0,true);
                        break;
                    case R.id.profile:
                        fragments_visualizer.setCurrentItem(1,true);
                        break;
                    case R.id.configuration:
                        break;
                    default:
                        break;
                }
                item.setChecked(!item.isChecked());
                return false;
            }
        });
    }

    private void setupViewPager(ViewPager visor_fragments){
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatChannelsFragment(), "ChatChannel");
        adapter.addFragment(new ProfileFragment(), "Profile");
        visor_fragments.setAdapter(adapter);
    }


}
