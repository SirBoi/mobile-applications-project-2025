package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserSearchFragment extends Fragment {

    public UserSearchFragment() {
        // Required empty public constructor
    }

    public static UserSearchFragment newInstance(String param1, String param2) {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // We ignore search UI completely (you can delete it later)
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);
        etSearch.setEnabled(false);
        etSearch.setAlpha(0.4f);
        btnSearch.setEnabled(false);
        btnSearch.setAlpha(0.4f);

        LinearLayout usersList = view.findViewById(R.id.usersList);

        MaterialButton btnPrev = view.findViewById(R.id.btnPrev);
        MaterialButton btnNext = view.findViewById(R.id.btnNext);
        TextView tvPageInfo = view.findViewById(R.id.tvPageInfo);

        final int pageSize = 8;
        final int[] page = {0};
        final int[] totalPages = {1};

        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);

        Runnable loadPage = () -> {
            api.getAllUsersPaged(page[0], pageSize).enqueue(new retrofit2.Callback<com.example.mobile_applications_project_2025.DTO.PageResponseDTO<com.example.mobile_applications_project_2025.Model.RegisteredUser>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.mobile_applications_project_2025.DTO.PageResponseDTO<com.example.mobile_applications_project_2025.Model.RegisteredUser>> call,
                                       retrofit2.Response<com.example.mobile_applications_project_2025.DTO.PageResponseDTO<com.example.mobile_applications_project_2025.Model.RegisteredUser>> response) {

                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(requireContext(), "Failed to load users (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    var body = response.body();
                    usersList.removeAllViews();

                    totalPages[0] = Math.max(1, body.totalPages);
                    tvPageInfo.setText("Page " + (body.number + 1) + "/" + totalPages[0]);

                    btnPrev.setEnabled(!body.first);
                    btnNext.setEnabled(!body.last);

                    btnPrev.setAlpha(btnPrev.isEnabled() ? 1f : 0.4f);
                    btnNext.setAlpha(btnNext.isEnabled() ? 1f : 0.4f);

                    if (body.content == null || body.content.isEmpty()) {
                        TextView empty = new TextView(requireContext());
                        empty.setText("No users.");
                        empty.setAlpha(0.7f);
                        usersList.addView(empty);
                        return;
                    }

                    for (com.example.mobile_applications_project_2025.Model.RegisteredUser u : body.content) {
                        View card = buildUserCard(u);
                        usersList.addView(card);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.mobile_applications_project_2025.DTO.PageResponseDTO<com.example.mobile_applications_project_2025.Model.RegisteredUser>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        };

        btnPrev.setOnClickListener(v -> {
            if (page[0] > 0) page[0]--;
            loadPage.run();
        });

        btnNext.setOnClickListener(v -> {
            if (page[0] < totalPages[0] - 1) page[0]++;
            loadPage.run();
        });

        loadPage.run();
    }

    private View buildUserCard(com.example.mobile_applications_project_2025.Model.RegisteredUser u) {
        com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(requireContext());
        card.setClickable(true);
        card.setFocusable(true);

        int pad = (int) (14 * getResources().getDisplayMetrics().density);

        LinearLayout inner = new LinearLayout(requireContext());
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(pad, pad, pad, pad);

        TextView tvName = new TextView(requireContext());
        tvName.setTextSize(16f);
        tvName.setTextColor(getResources().getColor(R.color.black, null));
        tvName.setTypeface(tvName.getTypeface(), android.graphics.Typeface.BOLD);

        String first = u.firstName != null ? u.firstName : "";
        String last = u.lastName != null ? u.lastName : "";
        String full = (first + " " + last).trim();
        tvName.setText(full.isEmpty() ? "User" : full);

        TextView tvMail = new TextView(requireContext());
        tvMail.setText(u.mail != null ? u.mail : "");
        tvMail.setAlpha(0.7f);

        TextView tvRole = new TextView(requireContext());
        tvRole.setText(u.role != null ? String.valueOf(u.role) : "—");
        tvRole.setAlpha(0.7f);

        TextView tvBlocked = new TextView(requireContext());
        boolean blocked = u.isBlocked != null && u.isBlocked;
        tvBlocked.setText(blocked ? "Blocked" : "Active");
        tvBlocked.setAlpha(0.7f);

        inner.addView(tvName);
        inner.addView(tvMail);
        inner.addView(tvRole);
        inner.addView(tvBlocked);

        card.addView(inner);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        card.setLayoutParams(lp);
        card.setRadius(16f);
        card.setCardElevation(3f);

        card.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putLong("userId", u.id != null ? u.id : -1L);
            NavHostFragment.findNavController(this).navigate(R.id.userAccountFragment, b);
        });

        return card;
    }
}
