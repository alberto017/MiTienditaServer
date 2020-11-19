package com.example.mitienditaserver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mitienditaserver.Fragments.CategoriaListaFragment;
import com.example.mitienditaserver.Fragments.PromocionListaFragment;
import com.example.mitienditaserver.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    //Declaracion de variables
    private HomeViewModel homeViewModel;
    private FrameLayout fl_main;
    private BottomNavigationView bottomNavigationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Referencia de controladores
        bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        fl_main = view.findViewById(R.id.fl_home);
        bottomNavigationView.setSelectedItemId(R.id.icon_food);
        //bottomNavigationView.setSelectedItemId(R.id.icon_mandadito);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.icon_food:
                        setFrament(new PromocionListaFragment());
                        return true;
                    case R.id.icon_product:
                        setFrament(new CategoriaListaFragment());
                        return true;
                }//switch
                return false;
            }//onNavigationItemSelected
        });

        setFrament(new PromocionListaFragment());

        return view;
    }//onCreateView

    //Llamo fragment de menu de productos
    private void setFrament(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(fl_main.getId(), fragment);
        fragmentTransaction.commit();
    }//setFrament

}//HomeFragment