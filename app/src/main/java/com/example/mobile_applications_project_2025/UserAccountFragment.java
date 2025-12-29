package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.User;
import com.google.android.material.button.MaterialButton;

public class UserAccountFragment extends Fragment {
    public UserAccountFragment() {
        // Required empty public constructor
    }

    public static UserAccountFragment newInstance(String param1, String param2) {
        UserAccountFragment fragment = new UserAccountFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        TextView tvFullName = view.findViewById(R.id.tvFullName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);

        LinearLayout driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        TextView tvDailyActiveMinutes = view.findViewById(R.id.tvDailyActiveMinutes);
        TextView tvCarModel = view.findViewById(R.id.tvCarModel);
        TextView tvCarType = view.findViewById(R.id.tvCarType);
        TextView tvPlateNumber = view.findViewById(R.id.tvPlateNumber);
        TextView tvCarSeats = view.findViewById(R.id.tvCarSeats);
        TextView tvBabyFriendly = view.findViewById(R.id.tvBabyFriendly);
        TextView tvPetFriendly = view.findViewById(R.id.tvPetFriendly);

        ivProfile.setImageResource(R.drawable.ic_launcher_foreground);

        User u = SessionManager.getUser(requireContext());
        if (u != null) {
            String first = u.firstName != null ? u.firstName : "";
            String last = u.lastName != null ? u.lastName : "";
            String full = (first + " " + last).trim();
            tvFullName.setText(full.isEmpty() ? "User" : full);
            tvEmail.setText(u.mail != null ? u.mail : "");

            boolean isDriver = u.role == Role.driver;
            driverFieldsContainer.setVisibility(isDriver ? View.VISIBLE : View.GONE);

            if (isDriver) {
                tvDailyActiveMinutes.setText(u.dailyActiveMinutes != null ? String.valueOf(u.dailyActiveMinutes) : "—");
                tvCarModel.setText("—");
                tvCarType.setText("—");
                tvPlateNumber.setText("—");
                tvCarSeats.setText("—");
                tvBabyFriendly.setText("—");
                tvPetFriendly.setText("—");
            }
        } else {
            //tvFullName.setText("User");
            tvFullName.setText("Aleksa Aleksic");
            //tvEmail.setText("");
            tvEmail.setText("aleksic@gmail.com");
            //driverFieldsContainer.setVisibility(View.GONE);
            driverFieldsContainer.setVisibility(View.VISIBLE);

            // izbrisi nakon kt1
            tvDailyActiveMinutes.setText("67");
            tvCarModel.setText("Yugo");
            tvCarType.setText("Luxury");
            tvPlateNumber.setText("YU 60 45");
            tvCarSeats.setText("3");
            tvBabyFriendly.setText("No");
            tvPetFriendly.setText("No");
        }
    }
}