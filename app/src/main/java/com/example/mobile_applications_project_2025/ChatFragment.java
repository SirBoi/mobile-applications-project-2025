package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.DTO.MessageResponseDTO;
import com.example.mobile_applications_project_2025.DTO.MessageSendRequestDTO;
import com.example.mobile_applications_project_2025.Network.APIs.ChatAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 2.11 - Live podrska. Ako se otvori bez argumenata, prikazuje "moj" chat sa
// supportom (za putnika/vozaca). Admin ga otvara sa "userId" (cim chata pripada
// tom korisniku) preko AdminChatListFragment - u tom slucaju admin odgovara
// kao posiljalac, ali gleda isti chat kao svi ostali admini.
public class ChatFragment extends Fragment {

    private static final String ARG_USER_ID = "chat_user_id";

    public static ChatFragment forUser(Long userId) {
        ChatFragment f = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        f.setArguments(args);
        return f;
    }

    private LinearLayout messagesContainer;
    private ScrollView scrollView;
    private TextInputEditText etMessage;

    private int orangeAction;
    private int white;
    private int black;

    private ChatAPI chatAPI;
    private Long chatUserId; // ciji je ovo chat (vlasnik)
    private Long myUserId;   // ko je ulogovan (posiljalac kad se salje poruka)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        messagesContainer = view.findViewById(R.id.messagesContainer);
        scrollView = view.findViewById(R.id.scrollMessages);
        etMessage = view.findViewById(R.id.etMessage);
        MaterialButton btnSend = view.findViewById(R.id.btnSend);

        orangeAction = requireContext().getColor(R.color.orange_action);
        white = requireContext().getColor(R.color.white);
        black = requireContext().getColor(R.color.black);
        btnSend.setBackgroundColor(orangeAction);
        btnSend.setTextColor(white);

        chatAPI = ApiClient.getRetrofit().create(ChatAPI.class);

        if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null) {
            Toast.makeText(requireContext(), "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        myUserId = SessionManager.getUser().getId();
        chatUserId = (getArguments() != null && getArguments().containsKey(ARG_USER_ID))
                ? getArguments().getLong(ARG_USER_ID) : myUserId;

        loadMessages();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            sendMessage(text);
        });
    }

    private void loadMessages() {
        chatAPI.getMessages(chatUserId).enqueue(new Callback<List<MessageResponseDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponseDTO>> call, @NonNull Response<List<MessageResponseDTO>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Could not load chat.", Toast.LENGTH_SHORT).show();
                    return;
                }
                messagesContainer.removeAllViews();
                for (MessageResponseDTO m : response.body()) {
                    addBubble(m.text, m.senderId != null && m.senderId.equals(myUserId));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponseDTO>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String text) {
        MessageSendRequestDTO body = new MessageSendRequestDTO(chatUserId, myUserId, text);
        chatAPI.sendMessage(body).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponseDTO> call, @NonNull Response<MessageResponseDTO> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to send message.", Toast.LENGTH_SHORT).show();
                    return;
                }
                addBubble(response.body().text, true);
                etMessage.setText("");
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponseDTO> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* =========================
       Message UI helpers
       ========================= */

    private void addBubble(String text, boolean sentByMe) {
        if (sentByMe) addSentMessage(text); else addReceivedMessage(text);
    }

    private void addSentMessage(String text) {
        Context ctx = requireContext();

        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        wrapper.setGravity(Gravity.END);
        wrapper.setPadding(0, dp(4), 0, dp(4));

        MaterialCardView card = new MaterialCardView(ctx);
        card.setRadius(dp(18));
        card.setCardElevation(0f);
        card.setCardBackgroundColor(orangeAction);

        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(white);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setMaxWidth(dp(280));

        card.addView(tv);
        wrapper.addView(card);
        messagesContainer.addView(wrapper);

        scrollToBottom();
    }

    private void addReceivedMessage(String text) {
        Context ctx = requireContext();

        MaterialCardView card = new MaterialCardView(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = dp(8);
        card.setLayoutParams(lp);
        card.setRadius(dp(18));
        card.setCardElevation(0f);
        card.setCardBackgroundColor(Color.parseColor("#EEEEEE"));

        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(black);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setMaxWidth(dp(280));

        card.addView(tv);
        messagesContainer.addView(card);

        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}