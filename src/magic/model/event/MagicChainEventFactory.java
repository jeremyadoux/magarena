package magic.model.event;

import magic.model.MagicSource;
import magic.model.choice.MagicTargetChoice;

public abstract class MagicChainEventFactory {

    abstract public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice);
    public MagicEvent getEvent(final MagicEvent event) {
        return getEvent(event.getSource(), event.getTargetChoice());
    }

    public static final MagicChainEventFactory Tap = new MagicChainEventFactory() {
        public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice) {
            return new MagicTapPermanentEvent(source, tchoice);
        }
    };

    public static final MagicChainEventFactory Untap = new MagicChainEventFactory() {
        public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice) {
            return new MagicUntapPermanentEvent(source, tchoice);
        }
    };

    public static final MagicChainEventFactory Sac = new MagicChainEventFactory() {
        public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice) {
            return new MagicSacrificePermanentEvent(source, tchoice);
        }
    };

    public static final MagicChainEventFactory Bounce = new MagicChainEventFactory() {
        public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice) {
            return new MagicBounceChosenPermanentEvent(source, tchoice);
        }
    };

    public static final MagicChainEventFactory ExileCard = new MagicChainEventFactory() {
        public MagicEvent getEvent(final MagicSource source, final MagicTargetChoice tchoice) {
            return new MagicExileCardEvent(source, tchoice);
        }
    };
}
