package flip_n_match.ui.pages.leaderboard;

import flip_n_match.ui.pages.PageStartMenu;
import flip_n_match.ui.system.Navigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackToMenuListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Navigator.navigate(PageStartMenu.class);
    }
}