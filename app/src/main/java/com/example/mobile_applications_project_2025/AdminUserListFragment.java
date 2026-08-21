package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobile_applications_project_2025.DTO.ChatResponseDTO;
import com.example.mobile_applications_project_2025.Network.APIs.ChatAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 2.11 - admin lista svih support chat-ova (najjednostavnija moguca verzija:
// dinamicka lista kartica umesto ranijih 5 hardkodovanih mock korisnika).
// Klik na karticu otvara chatFragment za tog korisnika - bilo koji admin
// koji klikne vidi/salje u isti chat.
public class AdminUserListFragment extends Fragment {

    private LinearLayout usersContainer;
    private ChatAPI chatAPI;

    public AdminUserListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        usersContainer = view.findViewById(R.id.usersContainer);
        chatAPI = ApiClient.getRetrofit().create(ChatAPI.class);
        loadChats();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatAPI != null) loadChats();
    }

    private void loadChats() {
        chatAPI.getAllChats().enqueue(new Callback<List<ChatResponseDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatResponseDTO>> call, @NonNull Response<List<ChatResponseDTO>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Could not load chats.", Toast.LENGTH_SHORT).show();
                    return;
                }
                usersContainer.removeAllViews();
                if (response.body().isEmpty()) {
                    TextView empty = new TextView(requireContext());
                    empty.setText("No support chats yet.");
                    usersContainer.addView(empty);
                    return;
                }
                for (ChatResponseDTO chat : response.body()) {
                    usersContainer.addView(buildCard(chat));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatResponseDTO>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View buildCard(ChatResponseDTO chat) {
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(10);
        card.setLayoutParams(lp);
        card.setRadius(dp(18));
        card.setCardElevation(dp(2));
        card.setCardBackgroundColor(requireContext().getColor(R.color.orange_action));
        card.setClickable(true);
        card.setFocusable(true);

        String name = chat.userName != null && !chat.userName.trim().isEmpty() ? chat.userName : ("User #" + chat.userId);
        String last = chat.lastMessageDateTime != null ? chat.lastMessageDateTime : "-";

        TextView tv = new TextView(requireContext());
        tv.setText("User: " + name + "\nLast message: " + last);
        tv.setPadding(dp(16), dp(16), dp(16), dp(16));
        tv.setTextColor(requireContext().getColor(R.color.white));
        tv.setTextSize(16f);

        card.addView(tv);

        card.setOnClickListener(v -> {
            if (chat.userId == null) return;
            Bundle args = new Bundle();
            args.putLong("chat_user_id", chat.userId);
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.chatFragment, args);
        });

        return card;
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}