package magic.model.action;

import magic.ai.ArtificialScoringSystem;
import magic.model.MagicPayedCost;
import magic.model.MagicCardDefinition;
import magic.model.MagicGame;
import magic.model.MagicLocationType;
import magic.model.MagicObject;
import magic.model.MagicPermanent;
import magic.model.stack.MagicCardOnStack;
import magic.model.trigger.MagicTrigger;
import magic.model.trigger.MagicTriggerType;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class MagicPlayCardFromStackAction extends MagicAction {

    private final MagicCardOnStack cardOnStack;
    private final MagicCardDefinition cardDef;
    
    private MagicPermanent permanent = MagicPermanent.NONE;
    private MagicPermanent enchantedPermanent = MagicPermanent.NONE;
    private MagicPayedCost payedCost = MagicPayedCost.NO_COST;
    private boolean validEnchanted = false;
    private List<? extends MagicPermanentAction> modifications = Collections.<MagicPermanentAction>emptyList();
    
    public MagicPlayCardFromStackAction(final MagicCardOnStack aCardOnStack, final MagicCardDefinition aCardDef, final List<? extends MagicPermanentAction> aModifications) {
        cardOnStack = aCardOnStack;
        cardDef = aCardDef;
        payedCost = aCardOnStack.getPayedCost();
        modifications = aModifications;
    }

    public MagicPlayCardFromStackAction(final MagicCardOnStack aCardOnStack, final MagicCardDefinition aCardDef, final MagicPermanentAction... aModifications) {
        this(aCardOnStack, aCardDef, Arrays.asList(aModifications));
    }
    
    public MagicPlayCardFromStackAction(final MagicCardOnStack aCardOnStack, final MagicPermanentAction... aModifications) {
        this(aCardOnStack, aCardOnStack.getCardDefinition(), aModifications);
    }
    
    public MagicPlayCardFromStackAction(final MagicCardOnStack cardOnStack, final MagicPermanent aEnchantedPermanent, final MagicPermanentAction... aModifications) {
        this(cardOnStack, aModifications);
        enchantedPermanent = aEnchantedPermanent;
    }

    static final public MagicPlayCardFromStackAction EnterAsCopy(final MagicCardOnStack cardOnStack, final MagicObject obj) {
        return new MagicPlayCardFromStackAction(cardOnStack, obj.getCardDefinition());
    }
    
    static final public MagicPlayCardFromStackAction EnterAsCopy(final MagicCardOnStack cardOnStack, final MagicObject obj, final MagicPermanentAction... aModifications) {
        return new MagicPlayCardFromStackAction(cardOnStack, obj.getCardDefinition(), aModifications);
    }

    protected MagicPermanent createPermanent(final MagicGame game) {
        cardOnStack.setMoveLocation(MagicLocationType.Play);
        return game.createPermanent(cardOnStack.getCard(),cardDef,cardOnStack.getController());
    }
    
    @Override
    public void doAction(final MagicGame game) {
        permanent=createPermanent(game);
        permanent.getFirstController().addPermanent(permanent);
        game.update();

        final int score=ArtificialScoringSystem.getTurnScore(game)-permanent.getStaticScore();

        validEnchanted = enchantedPermanent.isValid();
        if (validEnchanted) {
            enchantedPermanent.addAura(permanent);
            permanent.setEnchantedPermanent(enchantedPermanent);
        }
        
        //execute comes into play with
        for (final MagicTrigger<MagicPayedCost> trigger : permanent.getComeIntoPlayTriggers()) {
            if (trigger.getPriority() == MagicTrigger.REPLACEMENT) {
                game.executeTrigger(trigger,permanent,permanent,payedCost);
            }
        }

        //comes into play with
        for (final MagicPermanentAction action : modifications) {
            action.doAction(permanent);
        }
        for (final MagicPermanentAction action : cardOnStack.getModifications()) {
            action.doAction(permanent);
        }

        game.addStatics(permanent);
        game.update();

        //execute come into play triggers
        for (final MagicTrigger<MagicPayedCost> trigger : permanent.getComeIntoPlayTriggers()) {
            if (trigger.getPriority() > MagicTrigger.REPLACEMENT) {
                game.executeTrigger(trigger,permanent,permanent,payedCost);
            }
        }

        //execute other come into player triggers
        game.executeTrigger(MagicTriggerType.WhenOtherComesIntoPlay,permanent);

        setScore(permanent.getController(),permanent.getScore()+permanent.getStaticScore()+score);

        game.checkUniquenessRule(permanent);
        game.setStateCheckRequired();
    }

    @Override
    public void undoAction(final MagicGame game) {
        // for change of control Auras, enchantedPermanent.isValid is false as
        // change of control action is undone. Thus we store the validity in
        // variable validEnchanted during doAction.
        if (validEnchanted) {
            enchantedPermanent.removeAura(permanent);
            permanent.setEnchantedPermanent(MagicPermanent.NONE);
        }
        permanent.getFirstController().removePermanent(permanent);
        game.removeTriggers(permanent);
        game.removeAllStatics(permanent);
    }

    @Override
    public String toString() {
        if (enchantedPermanent.isValid()) {
            return getClass().getSimpleName()+" ("+permanent+','+enchantedPermanent+')';
        } else {
            return getClass().getSimpleName()+" ("+permanent+')';
        }
    }
}
