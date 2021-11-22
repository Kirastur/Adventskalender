package de.scientarus.adventskalender.main;

import de.scientarus.adventskalender.api.AdventskalenderAPI;

public class AdventskalenderProvider {

	private static AdventskalenderAPI adventskalenderAPI;

	private AdventskalenderProvider() {
	}

	protected static void setAPI(AdventskalenderAPI newAPI) {
		adventskalenderAPI = newAPI;
	}

	public static AdventskalenderAPI getAPI() {
		return adventskalenderAPI;
	}

}