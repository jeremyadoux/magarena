[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Draw),
        "Reveal"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [new MagicTapEvent(source)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "PN reveals the top card of his or her library. If it's a land card, "+
                "PN puts it onto the battlefield. Otherwise, PN puts it into his or her hand."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            final MagicCard card = player.getLibrary().getCardAtTop();
            game.doAction(new RevealAction(card));
            if (card.hasType(MagicType.Land)) {
                game.doAction(new RemoveCardAction(card, MagicLocationType.OwnersLibrary));
                game.doAction(new PlayCardAction(card, player));
            } else {
                game.doAction(new RemoveCardAction(card, MagicLocationType.OwnersLibrary));
                game.doAction(new MoveCardAction(card, MagicLocationType.OwnersLibrary, MagicLocationType.OwnersHand));
            }
        }
    }
]