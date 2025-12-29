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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ChatFragment extends Fragment {

    private LinearLayout messagesContainer;
    private ScrollView scrollView;
    private TextInputEditText etMessage;

    private int orangeAction;
    private int white;
    private int black;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        messagesContainer = view.findViewById(R.id.messagesContainer);
        scrollView = view.findViewById(R.id.scrollMessages);
        etMessage = view.findViewById(R.id.etMessage);

        MaterialButton btnSend = view.findViewById(R.id.btnSend);
//        MaterialButton btnList = view.findViewById(R.id.btnList);

        // colors
        orangeAction = requireContext().getColor(R.color.orange_action);
        white = requireContext().getColor(R.color.white);
        black = requireContext().getColor(R.color.black);

        // Optional: style buttons explicitly
        btnSend.setBackgroundColor(orangeAction);
        btnSend.setTextColor(white);

//        btnList.setBackgroundColor(orangeAction);
//        btnList.setTextColor(white);

        // Hardcoded initial messages (UI mock)
        addReceivedMessage("Zdravo! Dobrodošli u podršku.");
        addSentMessage("Zdravo, imam pitanje u vezi vožnje.");
        addReceivedMessage("Naravno, recite u čemu je problem.");

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            addSentMessage(text);
            etMessage.setText("");
        });

//        btnList.setOnClickListener(v ->
//                Toast.makeText(requireContext(), "List clicked", Toast.LENGTH_SHORT).show()
//        );
    }

    /* =========================
       Message UI helpers
       ========================= */

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
