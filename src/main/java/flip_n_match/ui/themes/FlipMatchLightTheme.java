package flip_n_match.ui.themes;

import com.formdev.flatlaf.FlatLightLaf;

public class FlipMatchLightTheme extends FlatLightLaf {
    public static final String NAME = "FlipMatchLightTheme";

    public static void installLafInfo() {
        installLafInfo(NAME, FlipMatchLightTheme.class);
    }

    public static boolean setup() {
        return setup(new FlipMatchLightTheme());
    }

    public FlipMatchLightTheme() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
