package magic.ui.screen.widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import magic.ui.ScreenController;
import magic.ui.utility.GraphicsUtils;
import magic.ui.utility.MagicStyle;
import magic.ui.widget.FontsAndBorders;

@SuppressWarnings("serial")
public class MenuButton extends JButton {

    private final static Color COLOR_NORMAL = Color.WHITE;
    private final static Color COLOR_DISABLED = Color.DARK_GRAY;

    private boolean isRunnable;
    private boolean hasSeparator;

    public MenuButton(final String caption, final AbstractAction action, final String tooltip, final boolean showSeparator) {
        super(caption);
        this.isRunnable = (action != null);
        this.hasSeparator = showSeparator;
        setFont(FontsAndBorders.FONT_MENU_BUTTON);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(COLOR_NORMAL);
        setButtonTransparent();
        setFocusable(true);
        setToolTipText(tooltip);
        if (isRunnable) {
            setMouseAdapter();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addActionListener(action);
        }
    }
    public MenuButton(final String caption, final AbstractAction action, final String tooltip) {
        this(caption, action, tooltip, true);
    }
    public MenuButton(final String caption, final AbstractAction action) {
        this(caption, action, null);
    }
    protected MenuButton() {
        isRunnable = false;
        hasSeparator = false;
    }

    public boolean isRunnable() {
        return isRunnable;
    }

    private void setButtonTransparent() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        if (!isRunnable) {
            setBorder(null);
        }
    }

    private void setMouseAdapter() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setForeground(MagicStyle.getRolloverColor());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setForeground(Color.WHITE);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    setForeground(MagicStyle.getPressedColor());
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                }

            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setForeground(Color.WHITE);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        isRunnable = b;
        setForeground(b ? COLOR_NORMAL : COLOR_DISABLED);
    }

    public boolean hasSeparator() {
        return hasSeparator;
    }

    public void setSeparator(boolean b) {
        hasSeparator = b;
    }


    //
    // Static convenience methods.
    //

    private final static AbstractAction closeScreenAction = new AbstractAction() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            ScreenController.closeActiveScreen(false);
        }
    };

    public static MenuButton getCloseScreenButton(final String caption) {
        return new MenuButton(caption, closeScreenAction);
    }

    @Override
    public void setIcon(final Icon defaultIcon) {
        super.setIcon(defaultIcon);
        setRolloverIcon(GraphicsUtils.getRecoloredIcon(
                (ImageIcon) defaultIcon,
                MagicStyle.getRolloverColor())
        );
        setPressedIcon(GraphicsUtils.getRecoloredIcon(
                (ImageIcon) defaultIcon,
                MagicStyle.getPressedColor())
        );
        setDisabledIcon(GraphicsUtils.getRecoloredIcon(
                (ImageIcon) defaultIcon,
                COLOR_DISABLED)
        );
    }

}
