package com.example.baitapbuoi10;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvCounter;
    private Button btnStart;

    // Sử dụng ExecutorService để quản lý luồng phụ (Background Thread)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // Sử dụng Handler để gửi thông điệp về luồng chính (UI Thread)
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ các thành phần giao diện
        tvCounter = findViewById(R.id.tvCounter);
        btnStart = findViewById(R.id.btnStart);

        // 2. Thiết lập sự kiện khi nhấn nút "Start Counting"
        btnStart.setOnClickListener(v -> {
            startCountingTask();
        });
    }

    private void startCountingTask() {
        // Vô hiệu hóa nút bấm trong khi đang đếm để tránh lỗi xung đột luồng
        btnStart.setEnabled(false);
        tvCounter.setText("0");

        // Chạy vòng lặp trong luồng phụ (Background Thread) bằng ExecutorService
        executorService.execute(() -> {
            for (int i = 1; i <= 10; i++) {
                final int step = i;

                try {
                    // Ngủ 1 giây (1000ms) theo yêu cầu đề bài
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Sau mỗi giây, dùng Handler để cập nhật kết quả lên TextView (UI Thread)
                mainHandler.post(() -> {
                    tvCounter.setText(String.valueOf(step));

                    // Khi đếm đến 10, thực hiện các thông báo kết thúc
                    if (step == 10) {
                        Toast.makeText(MainActivity.this, "Đã đếm xong!", Toast.LENGTH_SHORT).show();
                        btnStart.setEnabled(true); // Kích hoạt lại nút bấm
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên Executor khi đóng ứng dụng để tránh rò rỉ bộ nhớ
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}