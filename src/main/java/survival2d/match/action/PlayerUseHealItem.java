package survival2d.match.action;

import survival2d.match.type.ItemType;

public record PlayerUseHealItem(ItemType itemType) implements PlayerAction {}
