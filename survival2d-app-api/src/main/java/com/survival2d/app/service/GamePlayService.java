package com.survival2d.app.service;

import com.survival2d.app.game.shared.PlayerHitData;
import com.survival2d.app.game.shared.PlayerInputData;
import com.survival2d.app.game.shared.PlayerSpawnData;
import java.util.List;

public interface GamePlayService {
	void handlePlayerInputData(String playerName, PlayerInputData inputData, float[] nextRotation);
	
	List<PlayerSpawnData> spawnPlayers(List<String> playerNames);
	
	boolean authorizeHit(String playerName, PlayerHitData playerHitData);
	
	void resetPlayersPositionHistory(List<String> playerNames);
}
