package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class AdminUserListFragment extends Fragment {
    public AdminUserListFragment() {
        // Required empty public constructor
    }

    public static AdminUserListFragment newInstance(String param1, String param2) {
        AdminUserListFragment fragment = new AdminUserListFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View c1 = view.findViewById(R.id.cardUser1);
        View c2 = view.findViewById(R.id.cardUser2);
        View c3 = view.findViewById(R.id.cardUser3);
        View c4 = view.findViewById(R.id.cardUser4);
        View c5 = view.findViewById(R.id.cardUser5);

        View.OnClickListener goToChat = v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.chatFragment);
        };

        c1.setOnClickListener(goToChat);
        c2.setOnClickListener(goToChat);
        c3.setOnClickListener(goToChat);
        c4.setOnClickListener(goToChat);
        c5.setOnClickListener(goToChat);
    }
}
