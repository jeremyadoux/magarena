[
    new MagicStatic(MagicLayer.CostIncrease) {
        @Override
        public MagicManaCost increaseCost(final MagicPermanent source, final MagicCard card, final MagicManaCost cost) {
            if (card.isSorcery()) {
                return cost.increase(3);
            } else {
                return cost;
            }
        }
    }
]
