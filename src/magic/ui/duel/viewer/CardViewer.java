package magic.ui.duel.viewer;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import magic.model.MagicCardDefinition;
import magic.ui.CachedImagesProvider;
import magic.ui.MagicImages;
import magic.ui.cardtable.ICardSelectionListener;
import magic.ui.utility.GraphicsUtils;

@SuppressWarnings("serial")
public class CardViewer extends JPanel implements ICardSelectionListener {

    private Image thisImage;
    private MagicCardDefinition thisCard;
    private boolean isSwitchedAspect = false;

    public CardViewer() {
        setCard(MagicCardDefinition.UNKNOWN);
        setTransformCardListener();
    }

    private void setTransformCardListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thisCard != null) {
                    SwingUtilities.invokeLater(() -> {
                        switchCardAspect();
                    });
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (thisCard.hasMultipleAspects() && thisCard.isValid()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (isSwitchedAspect) {
                    switchCardAspect();
                }
            }
        });
    }

    private void switchCardAspect() {
        if (thisCard.hasMultipleAspects()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (thisCard.isDoubleFaced()) {
                setCard(thisCard.getTransformedDefinition());
            } else if (thisCard.isFlipCard()) {
                setCard(thisCard.getFlippedDefinition());
            }
            isSwitchedAspect = !isSwitchedAspect;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public final void setCard(final MagicCardDefinition aCard) {

        if (aCard == null) {
            thisCard = MagicCardDefinition.UNKNOWN;
            setImage(MagicImages.getMissingCardImage());

        } else if (aCard != thisCard) {
            thisCard = aCard;
            final BufferedImage cardImage = CachedImagesProvider.getInstance().getImage(aCard, false);
            if (aCard.isInvalid() && cardImage != MagicImages.getMissingCardImage()) {
                setImage(GraphicsUtils.getGreyScaleImage(cardImage));
            } else {
                setImage(cardImage);
            }
        }
    }

    @Override
    public void newCardSelected(final MagicCardDefinition card) {
        SwingUtilities.invokeLater(() -> {
            setCard(card);
        });
    }
    
    private void setImage(final Image image) {
        this.thisImage = image;
        repaint();
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (thisImage != null) {
            g.drawImage(thisImage, 0, 0, null);
        } else {
            super.paintComponent(g);
        }
    }
}
