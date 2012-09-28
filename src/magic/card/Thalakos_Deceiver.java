package magic.card;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.action.MagicGainControlAction;
import magic.model.action.MagicPermanentAction;
import magic.model.action.MagicSacrificeAction;
import magic.model.choice.MagicMayChoice;
import magic.model.choice.MagicTargetChoice;
import magic.model.event.MagicEvent;
import magic.model.target.MagicExileTargetPicker;
import magic.model.trigger.MagicWhenAttacksUnblockedTrigger;

public class Thalakos_Deceiver {
    public static final MagicWhenAttacksUnblockedTrigger T = new MagicWhenAttacksUnblockedTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent creature) {
            return (creature == permanent) ?
                new MagicEvent(
                    permanent,
                    new MagicMayChoice(MagicTargetChoice.NEG_TARGET_CREATURE),
                    MagicExileTargetPicker.create(),
                    this,
                    "PN may$ sacrifice SN. " +
                    "If you do, gain control of target creature$."
                ):
                MagicEvent.NONE;
        }
        
        @Override
        public void executeEvent(
                final MagicGame game,
                final MagicEvent event,
                final Object[] data,
                final Object[] choiceResults) {
            if (MagicMayChoice.isYesChoice(choiceResults[0])) {
                game.doAction(new MagicSacrificeAction(event.getPermanent()));
                event.processTargetPermanent(game,choiceResults,1,new MagicPermanentAction() {
                    public void doAction(final MagicPermanent creature) {
                        game.doAction(new MagicGainControlAction(event.getPlayer(),creature));
                    }
                });
            }
        }
    };
}
