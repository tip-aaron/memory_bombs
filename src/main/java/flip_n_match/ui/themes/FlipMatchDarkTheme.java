package flip_n_match.ui.themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class FlipMatchDarkTheme extends FlatDarkLaf {
    public static final String NAME = "FlipMatchDarkTheme";

    public static void installLafInfo() {
        installLafInfo(NAME, FlipMatchDarkTheme.class);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean setup() {
        return setup(new FlipMatchDarkTheme());
    }

    public FlipMatchDarkTheme() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
