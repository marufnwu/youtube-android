package com.logicline.tech.stube.ui.views.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.view.accessibility.CaptioningManager;

import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.gson.internal.Streams;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelper {

    private static final String ACTION_MEDIA_CONTROL = "media_control";
    public static final String CONTROL_TYPE = "control_type";

    /**
     * Create a base64 encoded DASH stream manifest
     */
    public static Uri createDashSource(Streams streams, Context context, boolean audioOnly) {
        String manifest = DashHelper.createManifest(
                streams,
                DisplayHelper.supportsHdr(context),
                audioOnly
        );

        // encode to base64
        String encoded = Base64.encodeToString(manifest.getBytes(), Base64.DEFAULT);
        return Uri.parse("data:application/dash+xml;charset=utf-8;base64," + encoded);
    }

    /**
     * Get the system's default captions style
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi.class)
    public static CaptionStyleCompat getCaptionStyle(Context context) {
        CaptioningManager captioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
        if (captioningManager != null && !captioningManager.isEnabled()) {
            // system captions are disabled, using android default captions style
            return CaptionStyleCompat.DEFAULT;
        } else if (captioningManager != null) {
            // system captions are enabled
            return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
        }
        return null;
    }

    /**
     * Get the categories for sponsorBlock
     */
    public static List<String> getSponsorBlockCategories() {
        List<String> categories = new ArrayList<>();

        if (PreferenceHelper.getBoolean("intro_category_key", false)) {
            categories.add("intro");
        }
        if (PreferenceHelper.getBoolean("selfpromo_category_key", false)) {
            categories.add("selfpromo");
        }
        if (PreferenceHelper.getBoolean("interaction_category_key", false)) {
            categories.add("interaction");
        }
        if (PreferenceHelper.getBoolean("sponsors_category_key", true)) {
            categories.add("sponsor");
        }
        if (PreferenceHelper.getBoolean("outro_category_key", false)) {
            categories.add("outro");
        }
        if (PreferenceHelper.getBoolean("filler_category_key", false)) {
            categories.add("filler");
        }
        if (PreferenceHelper.getBoolean("music_offtopic_category_key", false)) {
            categories.add("music_offtopic");
        }
        if (PreferenceHelper.getBoolean("preview_category_key", false)) {
            categories.add("preview");
        }
        return categories;
    }
}
}
