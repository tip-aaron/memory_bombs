package flip_n_match.ui.icons;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.ColorFunctions;
import flip_n_match.App;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.swing.*;
import java.awt.*;

@Setter
@Getter
@ToString
public class SVGIconUIColor extends FlatSVGIcon {
    public static final String ICONS_BASE_PATH = App.class.getPackageName().replace('.', '/') + "/ui/assets/icons/";
    private float alpha;

    private String colorKey;

    public SVGIconUIColor(final String name, final float scale, final String colorKey) {
        this(name, scale, colorKey, 1f);
    }

    public SVGIconUIColor(final String name, final float scale, final String colorKey, final float alpha) {
        super(ICONS_BASE_PATH + name, scale);
        this.colorKey = colorKey;
        this.alpha = alpha;
        setColorFilter(new ColorFilter(color -> {
            final Color uiColor = UIManager.getColor(getColorKey());

            if (uiColor != null) {
                return getAlpha() == 1 ? uiColor : ColorFunctions.fade(uiColor, getAlpha());
            }

            return color;
        }));
    }

}