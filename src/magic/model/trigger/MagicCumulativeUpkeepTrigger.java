package magic.model.trigger;

import magic.model.MagicCounterType;
import magic.model.MagicGame;
import magic.model.MagicManaCost;
import magic.model.MagicPermanent;
import magic.model.MagicPlayer;
import magic.model.action.MagicChangeCountersAction;
import magic.model.action.MagicSacrificeAction;
import magic.model.choice.MagicMayChoice;
import magic.model.choice.MagicPayManaCostChoice;
import magic.model.event.MagicEvent;

public class MagicCumulativeUpkeepTrigger extends MagicAtUpkeepTrigger {

    private final MagicManaCost manaCost;
    
    public MagicCumulativeUpkeepTrigger(final MagicManaCost manaCost) {
        this.manaCost = manaCost;
    }
    
    private final String genDescription(final int amount) {
        return amount == 1 ?
             " pay " + manaCost + "." :
             " pay " + amount + " times " + manaCost + ".";
    }

    @Override
    public MagicEvent executeTrigger(
            final MagicGame game,
            final MagicPermanent permanent,
            final MagicPlayer upkeepPlayer) {
        if (permanent.isController(upkeepPlayer)) {
            game.doAction(new MagicChangeCountersAction(
                    permanent,
                    MagicCounterType.Charge,
                    1,
                    true));
            final int amount = permanent.getCounters(MagicCounterType.Charge);
            final StringBuilder totalCost = new StringBuilder();
            for (int i=0;i<amount;i++) {
                totalCost.append(manaCost.toString());
            }
            return new MagicEvent(
                    permanent,
                    new MagicMayChoice(
                        new MagicPayManaCostChoice(
                            MagicManaCost.create(totalCost.toString())
                        )
                    ),
                    this,
                    "PN may$ " + genDescription(amount) +  
                    " If he or she doesn't, sacrifice SN.");
        }
        return MagicEvent.NONE;
    }

    @Override
    public void executeEvent(
            final MagicGame game,
            final MagicEvent event,
            final Object[] data,
            final Object[] choiceResults) {
        if (MagicMayChoice.isNoChoice(choiceResults[0])) {
            game.doAction(new MagicSacrificeAction(event.getPermanent()));
        }
    }
}

