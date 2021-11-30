package de.scientarus.adventskalender.messages;

public enum Message {

	ERROR("error"),
	JAVA_EXCEPTION("javaException"),
	MUST_BE_PLAYER("playerOnly"),
	RELOAD_OK("reloadOK"),
	MISSING_DEFINITION("missingDefinition"),
	WRONG_MATERIAL("wrongMaterial"),
	WRONG_ENCHANTMENT_TYPE("wrongEnchantmentType"),
	INCOMPATIBLE_ENCHANTMENT("incompatibleEnchantment"),
	WRONG_ENCHANTMENT_LEVEL("wrongEnchantmentLevel"),
	GUI_PAST("guiPast"),
	GUI_TODAY_CLOSED("guiTodayClosed"),
	GUI_TODAY_OPEN("guiTodayOpen"),
	GUI_FUTURE("guiFuture"),
	GUI_TITLE("guiTitle"),
	GUI_LAYOUT("guiLayout"),
	OUT_OF_TIMEFRAME("outOfTimeframe"),
	ALREADY_OPEN("alreadyOpen"),
	INVENTORY_FULL("inventoryFull"),
	OPEN_OK("openOK");

	private final String messageName;

	private Message(String messageName) {
		this.messageName = messageName;
	}

	public String getMessageName() {
		return messageName;
	}

}
