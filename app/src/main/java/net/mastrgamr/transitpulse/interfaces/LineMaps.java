package net.mastrgamr.transitpulse.interfaces;

import net.mastrgamr.transitpulse.R;

import java.util.HashMap;

/**
 * Project: Transit Pulse
 * Created: Stuart Smith
 * Date: 6/4/2015.
 */
public class LineMaps {
    public static HashMap<String, Integer> subwayLines = new HashMap<>(26);
    static {
        subwayLines.put("1", R.drawable.blip1);
        subwayLines.put("2", R.drawable.blip2);
        subwayLines.put("3", R.drawable.blip3);
        subwayLines.put("4", R.drawable.blip4);
        subwayLines.put("5", R.drawable.blip5);
        subwayLines.put("6", R.drawable.blip6);
        subwayLines.put("6X", R.drawable.blip6e);
        subwayLines.put("7", R.drawable.blip7);
        subwayLines.put("A", R.drawable.blipa);
        subwayLines.put("C", R.drawable.blipc);
        subwayLines.put("E", R.drawable.blipe);
        subwayLines.put("B", R.drawable.blipb);
        subwayLines.put("D", R.drawable.blipd);
        subwayLines.put("F", R.drawable.blipf);
        subwayLines.put("M", R.drawable.blipm);
        subwayLines.put("N", R.drawable.blipn);
        subwayLines.put("Q", R.drawable.blipq);
        subwayLines.put("R", R.drawable.blipr);
        subwayLines.put("G", R.drawable.blipg);
        subwayLines.put("S", R.drawable.blips);
        subwayLines.put("GS", R.drawable.blips);
        subwayLines.put("FS", R.drawable.blips);
        subwayLines.put("H", R.drawable.blips);
        subwayLines.put("L", R.drawable.blipl);
    }
}
