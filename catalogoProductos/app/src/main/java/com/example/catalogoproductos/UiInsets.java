package com.example.catalogoproductos;

import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// El espacio seguro se aplica una sola vez: los hijos no duplican los insets.
public final class UiInsets {
    private UiInsets() {}

    public static void apply(View root) {
        int left = root.getPaddingLeft();
        int top = root.getPaddingTop();
        int right = root.getPaddingRight();
        int bottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(left + bars.left, top + bars.top, right + bars.right,
                    bottom + Math.max(bars.bottom, keyboard.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
