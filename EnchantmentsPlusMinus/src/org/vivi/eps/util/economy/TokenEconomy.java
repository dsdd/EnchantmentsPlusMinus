package org.vivi.eps.util.economy;

import java.util.UUID;

import org.vivi.eps.api.EPSPlayerData;

public class TokenEconomy implements Economy {
		
	@Override
	public double getBalance(UUID playerUUID)
	{
		return EPSPlayerData.getPlayerData(playerUUID).getTokens();
	}

	@Override
	public double changeBalance(UUID playerUUID, double amount)
	{
		EPSPlayerData playerData = EPSPlayerData.getPlayerData(playerUUID);
		playerData.setTokens(playerData.getTokens()+amount);
		return playerData.getTokens(); // Pretty useless to call twice but JUSTTTT to make sure you know
	}

	@Override
	public double setBalance(UUID playerUUID, double value)
	{
		EPSPlayerData playerData = EPSPlayerData.getPlayerData(playerUUID);
		playerData.setTokens(value);
		return playerData.getTokens();
	}
}
