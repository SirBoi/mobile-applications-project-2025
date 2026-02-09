package com.example.mobile_applications_project_2025;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccountFragment extends Fragment {
    private final ExecutorService imgExecutor = Executors.newSingleThreadExecutor();

    public MyAccountFragment() {
        // Required empty public constructor
    }

    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        TextView tvFullName = view.findViewById(R.id.tvFullName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);

        LinearLayout infoContainer = view.findViewById(R.id.infoContainer);

        LinearLayout driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        if (driverFieldsContainer != null) driverFieldsContainer.setVisibility(View.GONE);

        TextView tvDailyActiveMinutes = view.findViewById(R.id.tvDailyActiveMinutes);
        TextView tvCarModel = view.findViewById(R.id.tvCarModel);
        TextView tvCarType = view.findViewById(R.id.tvCarType);
        TextView tvPlateNumber = view.findViewById(R.id.tvPlateNumber);
        TextView tvCarSeats = view.findViewById(R.id.tvCarSeats);
        TextView tvBabyFriendly = view.findViewById(R.id.tvBabyFriendly);
        TextView tvPetFriendly = view.findViewById(R.id.tvPetFriendly);

        MaterialButton btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.updateAccountFragment)
        );

        MaterialButton btnChangePassword = view.findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.changePasswordFragment)
        );

        ivProfile.setImageResource(R.drawable.ic_launcher_foreground);

        RegisteredUser user = SessionManager.getUser();
        if (user == null) return;

        com.google.android.material.button.MaterialButton btnDriverUpdateRequests =
                view.findViewById(R.id.btnDriverUpdateRequests);

        if (user.role == Role.Admin) {
            btnDriverUpdateRequests.setVisibility(View.VISIBLE);
            btnDriverUpdateRequests.setOnClickListener(v ->
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.adminDriverAccountUpdateRequestFragment)
            );
        } else {
            btnDriverUpdateRequests.setVisibility(View.GONE);
        }

        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
        api.getProfilePicture(user.id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                ResponseBody body = response.body();
                imgExecutor.execute(() -> {
                    try {
                        byte[] bytes = body.bytes();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bmp == null) return;
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> ivProfile.setImageBitmap(bmp));
                    } catch (IOException ignored) {}
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });

        tvFullName.setText(
                (user.firstName != null ? user.firstName : "") + " " +
                        (user.lastName != null ? user.lastName : "")
        );
        tvEmail.setText(user.mail != null ? user.mail : "");

        // ---- Dynamic: show ALL user info (works for every role/subclass) ----
        infoContainer.removeAllViews();
        addAllFields(infoContainer, user);

        if (driverFieldsContainer != null && user.role == Role.Driver && user instanceof com.example.mobile_applications_project_2025.Model.Driver) {
            com.example.mobile_applications_project_2025.Model.Driver d = (com.example.mobile_applications_project_2025.Model.Driver) user;

            driverFieldsContainer.setVisibility(View.VISIBLE);

            tvDailyActiveMinutes.setText(d.dailyActiveMinutes != null ? String.valueOf(d.dailyActiveMinutes) : "—");
            tvCarModel.setText(d.model != null ? d.model : "—");
            tvCarType.setText(d.type != null ? d.type.name() : "—");
            tvPlateNumber.setText(d.plateNumber != null ? d.plateNumber : "—");
            tvCarSeats.setText(d.numberOfSeats != null ? String.valueOf(d.numberOfSeats) : "—");
            tvBabyFriendly.setText(Boolean.TRUE.equals(d.isBabyFriendly) ? "Yes" : "No");
            tvPetFriendly.setText(Boolean.TRUE.equals(d.isAnimalFriendly) ? "Yes" : "No");
        }
    }

    private void addAllFields(LinearLayout container, Object obj) {
        Class<?> c = obj.getClass();

        while (c != null && c != Object.class) {
            java.lang.reflect.Field[] fields = c.getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                if (f.isSynthetic()) continue;

                String name = f.getName().trim();

                // hide sensitive / redundant / internal fields
                if (name.equalsIgnoreCase("password")) continue;
                if (name.equalsIgnoreCase("firstName")) continue;
                if (name.equalsIgnoreCase("lastName")) continue;
                if (name.equalsIgnoreCase("mail")) continue;
                if (name.equalsIgnoreCase("picture")) continue;

                if (name.equalsIgnoreCase("isProfileActivated")) continue;
                if (name.equalsIgnoreCase("carStatus")) continue;
                if (name.equalsIgnoreCase("blockMessage")) continue;
                if (name.equalsIgnoreCase("id")) continue;
                if (name.equalsIgnoreCase("isBlocked")) continue;
                if (name.equalsIgnoreCase("role")) continue;
                if (name.equalsIgnoreCase("status")) continue;

                // hide passenger-only stuff conditionally
                if (obj instanceof com.example.mobile_applications_project_2025.Model.Passenger) {
                    if (name.equalsIgnoreCase("favouriteRoutes")) continue;
                    if (name.equalsIgnoreCase("dailyActiveMinutes")) continue;
                }

                // hide passenger-only stuff conditionally
                if (obj instanceof com.example.mobile_applications_project_2025.Model.Admin) {
                    if (name.equalsIgnoreCase("dailyActiveMinutes")) continue;
                }

                f.setAccessible(true);

                Object v;
                try {
                    v = f.get(obj);
                } catch (IllegalAccessException e) {
                    continue;
                }

                String value = formatValue(v);
                addRow(container, pretty(name), value);
            }
            c = c.getSuperclass();
        }
    }

    private void addRow(LinearLayout container, String label, String value) {
        View row = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_2, container, false);
        TextView t1 = row.findViewById(android.R.id.text1);
        TextView t2 = row.findViewById(android.R.id.text2);

        t1.setText(label);
        t2.setText(value);

        container.addView(row);
    }

    private String formatValue(Object v) {
        if (v == null) return "—";
        if (v instanceof Enum<?>) return ((Enum<?>) v).name();
        if (v instanceof Boolean) return (Boolean) v ? "Yes" : "No";
        return String.valueOf(v);
    }

    private String pretty(String raw) {
        if (raw == null || raw.isEmpty()) return raw;
        String s = raw.replaceAll("([a-z])([A-Z])", "$1 $2");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}