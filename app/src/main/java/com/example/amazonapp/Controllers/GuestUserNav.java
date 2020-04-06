package com.example.amazonapp.Controllers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amazonapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuestUserNav extends BottomSheetDialogFragment {
    NavigationView navigationView;

    public GuestUserNav() {
        // Required empty public constructor
    }
/*
* Bottom navigation for Guest User
 */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_user_nav, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView=view.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.login:

                        Intent intent=new Intent(getActivity(),LoginActivity.class);
                        getActivity().startActivity(intent);

                        break;
                    // Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
                    //break;

                    case R.id.contact_us:
                        Toast.makeText(getActivity(), "Contact US", Toast.LENGTH_SHORT).show();
                        ContactFragment contactFragment =new ContactFragment(getContext());
                        FragmentManager fragmentManagerContact=getFragmentManager();
                        fragmentManagerContact.beginTransaction().replace(R.id.main_layout,contactFragment,contactFragment.getTag()).commit();
                        navigationView.setVisibility(View.GONE);

                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                return true;
            }
        });
    }


}
