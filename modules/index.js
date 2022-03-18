"use strict";
// Copyright 2021 The Nakama Authors & Contributors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
var ClanNotificationCode;
(function (ClanNotificationCode) {
    ClanNotificationCode[ClanNotificationCode["Refresh"] = 2] = "Refresh";
    ClanNotificationCode[ClanNotificationCode["Delete"] = 3] = "Delete";
})(ClanNotificationCode || (ClanNotificationCode = {}));
/**
 * Send an in-app notification to all clan members when a new member joins.
 */
var afterJoinGroupFn = function (ctx, logger, nk, data, request) {
    var _a;
    sendGroupNotification(nk, (_a = request.groupId) !== null && _a !== void 0 ? _a : "", ClanNotificationCode.Refresh, "New Member Joined!");
};
/**
 * Send an in-app notification to all clan members when one or more members are kicked.
 */
var afterKickGroupUsersFn = function (ctx, logger, nk, data, request) {
    var _a;
    sendGroupNotification(nk, (_a = request.groupId) !== null && _a !== void 0 ? _a : "", ClanNotificationCode.Refresh, "Member(s) Have Been Kicked!");
};
/**
 * Send an in-app notification to all clan members when a member leaves.
 */
var afterLeaveGroupFn = function (ctx, logger, nk, data, request) {
    var _a;
    sendGroupNotification(nk, (_a = request.groupId) !== null && _a !== void 0 ? _a : "", ClanNotificationCode.Refresh, "Member Left!");
};
/**
 * Send an in-app notification to all clan members when one or more members are promoted.
 */
var afterPromoteGroupUsersFn = function (ctx, logger, nk, data, request) {
    var _a;
    sendGroupNotification(nk, (_a = request.groupId) !== null && _a !== void 0 ? _a : "", ClanNotificationCode.Refresh, "Member(s) Have Been Promoted!");
};
/**
 * Send an in-app notification to the clan members when the superadmin deletes it.
 */
var beforeDeleteGroupFn = function (ctx, logger, nk, request) {
    var _a;
    var members = nk.groupUsersList(request.groupId, 100, 0);
    // Check delete request user is a superadmin in the group.
    (_a = members.groupUsers) === null || _a === void 0 ? void 0 : _a.every(function (user) {
        var _a;
        if (user.user.userId == ctx.userId) {
            sendGroupNotification(nk, (_a = request.groupId) !== null && _a !== void 0 ? _a : "", ClanNotificationCode.Delete, "Clan Deleted!");
            return false;
        }
        return true;
    });
    return request;
};
function sendGroupNotification(nk, groupId, code, subject) {
    var _a, _b;
    var members = nk.groupUsersList(groupId, 100);
    var count = ((_a = members.groupUsers) !== null && _a !== void 0 ? _a : []).length;
    if (count < 1) {
        return;
    }
    var notifications = [];
    (_b = members.groupUsers) === null || _b === void 0 ? void 0 : _b.forEach(function (user) {
        var n = {
            code: code,
            content: {},
            persistent: false,
            subject: subject,
            userId: user.user.userId,
        };
        notifications.push(n);
    });
    nk.notificationsSend(notifications);
}
/*
According to this issue https://github.com/heroiclabs/nakama-js/issues/54
and corresponding solution https://github.com/heroiclabs/nakama-js/pull/56

Also see https://stackoverflow.com/a/30106551
*/
function decodeMessageData(data) {
    try {
        return JSON.parse(decodeURIComponent(data.split('').map(function (c) { return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2); }).join('')));
    }
    catch (error) {
        return undefined;
    }
}
// https://github.com/dop251/goja/issues/283
function encodeMessageData(data) {
    try {
        var urlEnc = encodeURIComponent(JSON.stringify(data)) /*.replace(
                /%([0-9A-F]{2})/g,
                (_match:string, p1) => String.fromCharCode(Number('0x' + p1))
            )*/;
        return customReplace(urlEnc);
    }
    catch (error) {
        return undefined;
    }
}
function customReplace(urlEncoded) {
    var s = '';
    for (var i = 0; i < urlEncoded.length; ++i) {
        var char = urlEncoded.charAt(i);
        if (char === '%') {
            s += String.fromCharCode(Number('0x' + urlEncoded.substr(i + 1, 2)));
            i += 2;
        }
        else {
            s += char;
        }
    }
    return s;
}
// Copyright 2021 The Nakama Authors & Contributors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
var DeckPermissionRead = 2;
var DeckPermissionWrite = 0;
var DeckCollectionName = 'card_collection';
var DeckCollectionKey = 'user_cards';
var DefaultDeckCards = [
    {
        type: 1,
        level: 1,
    },
    {
        type: 1,
        level: 1,
    },
    {
        type: 2,
        level: 1,
    },
    {
        type: 2,
        level: 1,
    },
    {
        type: 3,
        level: 1,
    },
    {
        type: 4,
        level: 1,
    },
];
var DefaultStoredCards = [
    {
        type: 2,
        level: 1,
    },
    {
        type: 2,
        level: 1,
    },
    {
        type: 3,
        level: 1,
    },
    {
        type: 4,
        level: 1,
    },
];
/**
 * Swap a card in the user deck with one in its collection.
 */
var rpcSwapDeckCard = function (ctx, logger, nk, payload) {
    var request = JSON.parse(payload);
    var userCards = loadUserCards(nk, logger, ctx.userId);
    // Check the cards being swapper are valid.
    if (Object.keys(userCards.deckCards).indexOf(request.cardOutId) < 0) {
        throw Error('invalid out card');
    }
    if (Object.keys(userCards.storedCards).indexOf(request.cardInId) < 0) {
        throw Error('invalid in card');
    }
    // Swap the cards
    var outCard = userCards.deckCards[request.cardOutId];
    var inCard = userCards.storedCards[request.cardInId];
    delete (userCards.deckCards[request.cardOutId]);
    delete (userCards.storedCards[request.cardInId]);
    userCards.deckCards[request.cardInId] = inCard;
    userCards.storedCards[request.cardOutId] = outCard;
    // Store the changes
    storeUserCards(nk, logger, ctx.userId, userCards);
    logger.debug("user '%s' deck card '%s' swapped with '%s'", ctx.userId);
    return JSON.stringify(userCards);
};
/**
 * Upgrade the level of a given card in the user collection.
 */
var rpcUpgradeCard = function (ctx, logger, nk, payload) {
    var request = JSON.parse(payload);
    var userCards = loadUserCards(nk, logger, ctx.userId);
    if (!userCards) {
        logger.error('user %s card collection not found', ctx.userId);
        throw Error('Internal server error');
    }
    var card = userCards.deckCards[request.id];
    if (card) {
        card.level += 1;
        userCards.deckCards[request.id] = card;
    }
    card = userCards.storedCards[request.id];
    if (card) {
        card.level += 1;
        userCards.storedCards[request.id] = card;
    }
    if (!card) {
        logger.error('invalid card');
        throw Error('invalid card');
    }
    try {
        storeUserCards(nk, logger, ctx.userId, userCards);
    }
    catch (error) {
        // Error logged in storeUserCards
        throw Error('Internal server error');
    }
    logger.debug('user %s card %s upgraded', ctx.userId, JSON.stringify(card));
    return JSON.stringify(card);
};
/**
 * Reset user card collection to the default set.
 */
var rpcResetCardCollection = function (ctx, logger, nk, payload) {
    var collection = defaultCardCollection(nk, logger, ctx.userId);
    storeUserCards(nk, logger, ctx.userId, collection);
    logger.debug('user %s card collection has been reset', ctx.userId);
    return JSON.stringify(collection);
};
/**
 * Get user card collection.
 */
var rpcLoadUserCards = function (ctx, logger, nk, payload) {
    return JSON.stringify(loadUserCards(nk, logger, ctx.userId));
};
/**
 * Add a random card to the user collection for 100 gems.
 */
var rpcBuyRandomCard = function (ctx, logger, nk, payload) {
    var _a, _b;
    var type = Math.floor(Math.random() * 4) + 1;
    var userCards;
    try {
        userCards = loadUserCards(nk, logger, ctx.userId);
    }
    catch (error) {
        logger.error('error loading user cards: %s', error.message);
        throw Error('Internal server error');
    }
    var cardId = nk.uuidv4();
    var newCard = {
        type: type,
        level: 1,
    };
    userCards.storedCards[cardId] = newCard;
    try {
        // If no sufficient funds are available, this will throw an error.
        nk.walletUpdate(ctx.userId, (_a = {}, _a[currencyKeyName] = -100, _a));
        // Store the new card to the collection.
        storeUserCards(nk, logger, ctx.userId, userCards);
    }
    catch (error) {
        logger.error('error buying card: %s', error.message);
        throw error;
    }
    logger.debug('user %s successfully bought a new card', ctx.userId);
    return JSON.stringify((_b = {}, _b[cardId] = newCard, _b));
};
function loadUserCards(nk, logger, userId) {
    var storageReadReq = {
        key: DeckCollectionKey,
        collection: DeckCollectionName,
        userId: userId,
    };
    var objects;
    try {
        objects = nk.storageRead([storageReadReq]);
    }
    catch (error) {
        logger.error('storageRead error: %s', error.message);
        throw error;
    }
    if (objects.length === 0) {
        throw Error('user cards storage object not found');
    }
    var storedCardCollection = objects[0].value;
    return storedCardCollection;
}
function storeUserCards(nk, logger, userId, cards) {
    try {
        nk.storageWrite([
            {
                key: DeckCollectionKey,
                collection: DeckCollectionName,
                userId: userId,
                value: cards,
                permissionRead: DeckPermissionRead,
                permissionWrite: DeckPermissionWrite,
            }
        ]);
    }
    catch (error) {
        logger.error('storageWrite error: %s', error.message);
        throw error;
    }
}
function getRandomInt(min, max) {
    return min + Math.floor(Math.random() * Math.floor(max));
}
function defaultCardCollection(nk, logger, userId) {
    var deck = {};
    DefaultDeckCards.forEach(function (c) {
        deck[nk.uuidv4()] = c;
    });
    var stored = {};
    DefaultStoredCards.forEach(function (c) {
        stored[nk.uuidv4()] = c;
    });
    var cards = {
        deckCards: deck,
        storedCards: stored,
    };
    storeUserCards(nk, logger, userId, cards);
    return {
        deckCards: deck,
        storedCards: stored,
    };
}
// Copyright 2021 The Nakama Authors & Contributors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
var currencyKeyName = 'gems';
var rpcAddUserGems = function (ctx, logger, nk) {
    var walletUpdateResult = updateWallet(nk, ctx.userId, 100, {});
    var updateString = JSON.stringify(walletUpdateResult);
    logger.debug('Added 100 gems to user %s wallet: %s', ctx.userId, updateString);
    return updateString;
};
function updateWallet(nk, userId, amount, metadata) {
    var _a;
    var changeset = (_a = {},
        _a[currencyKeyName] = amount,
        _a);
    var result = nk.walletUpdate(userId, changeset, metadata, true);
    return result;
}
var JoinOrCreateMatchRpc = "JoinOrCreateMatchRpc";
var LogicLoadedLoggerInfo = "Custom logic loaded.";
var MatchModuleName = "match";
function InitModule(ctx, logger, nk, initializer) {
    initializer.registerRpc(JoinOrCreateMatchRpc, joinOrCreateMatch);
    initializer.registerMatch(MatchModuleName, {
        matchInit: matchInit,
        matchJoinAttempt: matchJoinAttempt,
        matchJoin: matchJoin,
        matchLeave: matchLeave,
        matchLoop: matchLoop,
        matchTerminate: matchTerminate,
    });
    logger.info(LogicLoadedLoggerInfo);
}
// // Copyright 2021 The Nakama Authors & Contributors
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// // http://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.
//
// const winnerBonus = 10;
// const towerDestroyedMultiplier = 5;
// const speedBonus = 5;
// const winReward = 180;
// const loseReward = 110;
//
// function calculateScore(isWinner: boolean, towersDestroyed: number, matchDuration: number): number {
//   let score = isWinner ? winnerBonus : 0;
//
//   score += towersDestroyed * towerDestroyedMultiplier;
//
//   let durationMin = Math.floor(matchDuration / 60);
//
//   let timeScore = 0;
//   if (isWinner) {
//     timeScore = Math.max(1, speedBonus - durationMin);
//   } else {
//     timeScore = Math.max(1, Math.min(durationMin, speedBonus));
//   }
//
//   score += timeScore;
//
//   //changeset values must be whole numbers
//   return Math.round(score);
// }
//
// function rpcGetMatchScore(ctx: nkruntime.Context, logger: nkruntime.Logger, nk: nkruntime.Nakama, payload: string): string {
//   let matchId = JSON.parse(payload)['match_id'];
//   if (!matchId) {
//     throw Error('missing match_id from payload');
//   }
//
//   let items = nk.walletLedgerList(ctx.userId, 100);
//   while (items.cursor) {
//     items = nk.walletLedgerList(ctx.userId, 100, items.cursor);
//   }
//
//   let lastMatchReward = {} as nkruntime.WalletLedgerResult;
//   for (let update of items.items) {
//     if (update.metadata.source === 'match_reward'
//         && update.metadata.match_id === matchId) {
//       lastMatchReward = update;
//     }
//   }
//
//   return JSON.stringify(lastMatchReward);
// }
//
// enum MatchEndPlacement {
//   Loser = 0,
//   Winner = 1
// }
//
// interface MatchEndRequest {
//   matchId: string;
//   placement: MatchEndPlacement;
//   time: number;
//   towersDestroyed: number;
// }
//
// interface MatchEndResponse {
//   gems: number;
//   score: number;
// }
//
// const rpcHandleMatchEnd: nkruntime.RpcFunction = function (ctx: nkruntime.Context, logger: nkruntime.Logger, nk: nkruntime.Nakama, payload: string): string {
//
//   if (!payload) {
//     throw Error('no data found in rpc payload');
//   }
//
//   let request: MatchEndRequest = JSON.parse(payload);
//
//   let score = calculateScore(request.placement == MatchEndPlacement.Winner, request.towersDestroyed, request.time);
//
//   let metadata = {
//     source: 'match_reward',
//     match_id: request.matchId,
//   };
//
//   updateWallet(nk, ctx.userId, score, metadata);
//
//   nk.leaderboardRecordWrite(globalLeaderboard, ctx.userId, ctx.username, score);
//
//   let response: MatchEndResponse = {
//     gems: request.placement == MatchEndPlacement.Winner ? winReward : loseReward,
//     score: score
//   };
//
//   logger.debug('match %s ended', ctx.matchId);
//
//   return JSON.stringify(response);
// }
var matchInit = function (context, logger, nakama, params) {
    var label = { open: true };
    var gameState = {
        players: [],
        playersWins: [],
        roundDeclaredWins: [[]],
        roundDeclaredDraw: [],
        scene: 3 /* Lobby */,
        countdown: DurationLobby * TickRate,
        endMatch: false,
    };
    return {
        state: gameState,
        tickRate: TickRate,
        label: JSON.stringify(label),
    };
};
var matchJoinAttempt = function (context, logger, nakama, dispatcher, tick, state, presence, metadata) {
    var gameState = state;
    return {
        state: gameState,
        accept: gameState.scene == 3 /* Lobby */,
    };
};
var matchJoin = function (context, logger, nakama, dispatcher, tick, state, presences) {
    var gameState = state;
    if (gameState.scene != 3 /* Lobby */)
        return { state: gameState };
    var presencesOnMatch = [];
    gameState.players.forEach(function (player) {
        if (player != undefined)
            presencesOnMatch.push(player.presence);
    });
    for (var _i = 0, presences_1 = presences; _i < presences_1.length; _i++) {
        var presence = presences_1[_i];
        var account = nakama.accountGetId(presence.userId);
        var player = {
            presence: presence,
            displayName: account.user.displayName,
        };
        var nextPlayerNumber = getNextPlayerNumber(gameState.players);
        gameState.players[nextPlayerNumber] = player;
        gameState.playersWins[nextPlayerNumber] = 0;
        dispatcher.broadcastMessage(1 /* PlayerJoined */, JSON.stringify(player), presencesOnMatch);
        presencesOnMatch.push(presence);
    }
    dispatcher.broadcastMessage(0 /* Players */, JSON.stringify(gameState.players), presences);
    gameState.countdown = DurationLobby * TickRate;
    return { state: gameState };
};
var matchLoop = function (context, logger, nakama, dispatcher, tick, state, messages) {
    var gameState = state;
    processMessages(messages, gameState, dispatcher);
    processMatchLoop(gameState, nakama, dispatcher, logger);
    return gameState.endMatch ? null : { state: gameState };
};
var matchLeave = function (context, logger, nakama, dispatcher, tick, state, presences) {
    var gameState = state;
    for (var _i = 0, presences_2 = presences; _i < presences_2.length; _i++) {
        var presence = presences_2[_i];
        var playerNumber = getPlayerNumber(gameState.players, presence.sessionId);
        delete gameState.players[playerNumber];
    }
    if (getPlayersCount(gameState.players) == 0)
        return null;
    return { state: gameState };
};
var matchTerminate = function (context, logger, nakama, dispatcher, tick, state, graceSeconds) {
    return { state: state };
};
function processMessages(messages, gameState, dispatcher) {
    for (var _i = 0, messages_1 = messages; _i < messages_1.length; _i++) {
        var message = messages_1[_i];
        var opCode = message.opCode;
        if (MessagesLogic.hasOwnProperty(opCode))
            MessagesLogic[opCode](message, gameState, dispatcher);
        else
            messagesDefaultLogic(message, gameState, dispatcher);
    }
}
function messagesDefaultLogic(message, gameState, dispatcher) {
    dispatcher.broadcastMessage(message.opCode, message.data, null, message.sender);
}
function processMatchLoop(gameState, nakama, dispatcher, logger) {
    switch (gameState.scene) {
        case 4 /* Battle */:
            matchLoopBattle(gameState, nakama, dispatcher);
            break;
        case 3 /* Lobby */:
            matchLoopLobby(gameState, nakama, dispatcher);
            break;
        case 5 /* RoundResults */:
            matchLoopRoundResults(gameState, nakama, dispatcher);
            break;
    }
}
function matchLoopBattle(gameState, nakama, dispatcher) {
    if (gameState.countdown > 0) {
        gameState.countdown--;
        if (gameState.countdown == 0) {
            gameState.roundDeclaredWins = [];
            gameState.roundDeclaredDraw = [];
            gameState.countdown = DurationRoundResults * TickRate;
            gameState.scene = 5 /* RoundResults */;
            dispatcher.broadcastMessage(5 /* ChangeScene */, JSON.stringify(gameState.scene));
        }
    }
}
function matchLoopLobby(gameState, nakama, dispatcher) {
    if (gameState.countdown > 0 && getPlayersCount(gameState.players) > 1) {
        gameState.countdown--;
        if (gameState.countdown == 0) {
            gameState.scene = 4 /* Battle */;
            dispatcher.broadcastMessage(5 /* ChangeScene */, JSON.stringify(gameState.scene));
            dispatcher.matchLabelUpdate(JSON.stringify({ open: false }));
        }
    }
}
function matchLoopRoundResults(gameState, nakama, dispatcher) {
    if (gameState.countdown > 0) {
        gameState.countdown--;
        if (gameState.countdown == 0) {
            var winner = getWinner(gameState.playersWins, gameState.players);
            if (winner != null) {
                var storageReadRequests = [
                    {
                        collection: CollectionUser,
                        key: KeyTrophies,
                        userId: winner.presence.userId,
                    },
                ];
                var result = nakama.storageRead(storageReadRequests);
                var trophiesData = { amount: 0 };
                for (var _i = 0, result_1 = result; _i < result_1.length; _i++) {
                    var storageObject = result_1[_i];
                    trophiesData = storageObject.value;
                    break;
                }
                trophiesData.amount++;
                var storageWriteRequests = [
                    {
                        collection: CollectionUser,
                        key: KeyTrophies,
                        userId: winner.presence.userId,
                        value: trophiesData,
                    },
                ];
                nakama.storageWrite(storageWriteRequests);
                gameState.endMatch = true;
                gameState.scene = 6 /* FinalResults */;
            }
            else {
                gameState.scene = 4 /* Battle */;
            }
            dispatcher.broadcastMessage(5 /* ChangeScene */, JSON.stringify(gameState.scene));
        }
    }
}
function playerWon(message, gameState, dispatcher) {
    if (gameState.scene != 4 /* Battle */ || gameState.countdown > 0)
        return;
    var data = JSON.parse(message.data);
    var tick = data.tick;
    var playerNumber = data.playerNumber;
    if (gameState.roundDeclaredWins[tick] == undefined)
        gameState.roundDeclaredWins[tick] = [];
    if (gameState.roundDeclaredWins[tick][playerNumber] == undefined)
        gameState.roundDeclaredWins[tick][playerNumber] = 0;
    gameState.roundDeclaredWins[tick][playerNumber]++;
    if (gameState.roundDeclaredWins[tick][playerNumber] <
        getPlayersCount(gameState.players))
        return;
    gameState.playersWins[playerNumber]++;
    gameState.countdown = DurationBattleEnding * TickRate;
    dispatcher.broadcastMessage(message.opCode, message.data, null, message.sender);
}
function draw(message, gameState, dispatcher) {
    if (gameState.scene != 4 /* Battle */ || gameState.countdown > 0)
        return;
    var data = JSON.parse(message.data);
    var tick = data.tick;
    if (gameState.roundDeclaredDraw[tick] == undefined)
        gameState.roundDeclaredDraw[tick] = 0;
    gameState.roundDeclaredDraw[tick]++;
    if (gameState.roundDeclaredDraw[tick] < getPlayersCount(gameState.players))
        return;
    gameState.countdown = DurationBattleEnding * TickRate;
    dispatcher.broadcastMessage(message.opCode, message.data, null, message.sender);
}
function getPlayersCount(players) {
    var count = 0;
    for (var playerNumber = 0; playerNumber < MaxPlayers; playerNumber++)
        if (players[playerNumber] != undefined)
            count++;
    return count;
}
function playerObtainedNecessaryWins(playersWins) {
    for (var playerNumber = 0; playerNumber < MaxPlayers; playerNumber++)
        if (playersWins[playerNumber] == NecessaryWins)
            return true;
    return false;
}
function getWinner(playersWins, players) {
    for (var playerNumber = 0; playerNumber < MaxPlayers; playerNumber++)
        if (playersWins[playerNumber] == NecessaryWins)
            return players[playerNumber];
    return null;
}
function getPlayerNumber(players, sessionId) {
    for (var playerNumber = 0; playerNumber < MaxPlayers; playerNumber++)
        if (players[playerNumber] != undefined &&
            players[playerNumber].presence.sessionId == sessionId)
            return playerNumber;
    return PlayerNotFound;
}
function getNextPlayerNumber(players) {
    for (var playerNumber = 0; playerNumber < MaxPlayers; playerNumber++)
        if (!playerNumberIsUsed(players, playerNumber))
            return playerNumber;
    return PlayerNotFound;
}
function playerNumberIsUsed(players, playerNumber) {
    return players[playerNumber] != undefined;
}
// Copyright 2021 The Nakama Authors & Contributors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
var QuestsCollectionKey = 'quests';
var AddFriendQuestKey = 'add_friend';
var AddFriendQuestReward = 1000;
var AddFriendQuestNotificationCode = 1;
function addFriendQuestInit(userId) {
    return {
        collection: QuestsCollectionKey,
        key: AddFriendQuestKey,
        permissionRead: 1,
        permissionWrite: 0,
        value: { done: false },
        userId: userId,
    };
}
function getFriendQuest(nk, logger, userId) {
    var storageReadReq = {
        collection: QuestsCollectionKey,
        key: AddFriendQuestKey,
        userId: userId,
    };
    var objects;
    try {
        objects = nk.storageRead([storageReadReq]);
    }
    catch (error) {
        logger.error('storageRead error: %s', error.message);
        throw error;
    }
    if (objects.length === 0) {
        throw Error('user add_friend quest storage object not found');
    }
    return objects[0];
}
var afterAddFriendsFn = function (ctx, logger, nk, data, request) {
    var storedQuest = getFriendQuest(nk, logger, ctx.userId);
    var addFriendQuest = storedQuest.value;
    if (!addFriendQuest.done) {
        var quest = addFriendQuestInit(ctx.userId);
        quest.value.done = true;
        try {
            nk.storageWrite([quest]);
        }
        catch (error) {
            logger.error('storageWrite error: %q', error);
            throw error;
        }
        // Notify that the quest was completed.
        var subject = JSON.stringify('A new friend!');
        var content = { reward: AddFriendQuestReward };
        var code = AddFriendQuestNotificationCode;
        var senderId = null; // Server sent
        var persistent = true;
        nk.notificationSend(ctx.userId, subject, content, code, senderId, persistent);
        logger.info('user %s completed add_friend quest!', ctx.userId);
    }
};
var Matrix = /** @class */ (function () {
    function Matrix(n) {
        this.n = n;
        this.v = arrayFill(new Array(n * n), 0);
    }
    Matrix.prototype.getValueAt = function (row, col) {
        return this.v[row * this.n + col];
    };
    Matrix.prototype.setValueAt = function (row, col, value) {
        this.v[row * this.n + col] = value;
    };
    Matrix.prototype.toString = function () {
        var s = "";
        for (var i = 0; i < this.n; ++i) {
            s +=
                this.v
                    .slice(i * this.n, (i + 1) * this.n)
                    .map(function (o) {
                    var val = String(o);
                    return "   ".substr(0, 3 - val.length) + val;
                })
                    .join("") + "\n";
        }
        return s;
    };
    return Matrix;
}());
function arrayFill(array, value) {
    for (var i = 0; i < array.length; ++i) {
        array[i] = value;
    }
    return array;
}
function dfs(v, used, matching, n, hasEdge) {
    if (used[v]) {
        return false;
    }
    used[v] = true;
    for (var i = 0; i < n; ++i) {
        if (hasEdge(v, i) &&
            (matching[i] === -1 || dfs(matching[i], used, matching, n, hasEdge))) {
            matching[i] = v;
            return true;
        }
    }
    return false;
}
function kuhn(n, hasEdge) {
    var matching = arrayFill(new Array(n), -1);
    var used = arrayFill(new Array(n), false);
    // trying to make random greedy matching
    var usedGreedy = arrayFill(new Array(n), false);
    var rnd = new Array(n);
    var rndLength = 0;
    for (var i = 0; i < n; ++i) {
        rndLength = 0;
        for (var j = 0; j < n; ++j) {
            if (hasEdge(i, j) && matching[j] == -1) {
                rnd[rndLength++] = j;
            }
        }
        if (rndLength > 0) {
            matching[rnd[Math.floor(Math.random() * rndLength)]] = i;
            usedGreedy[i] = true;
        }
    }
    for (var v = 0; v < n; ++v) {
        if (!usedGreedy[v]) {
            arrayFill(used, false);
            dfs(v, used, matching, n, hasEdge);
        }
    }
    return matching;
}
/**
 * Returns "random" matrix aka Latin square with normalized first row
 * See https://en.wikipedia.org/wiki/Latin_square
 *
 * @param {number} n Number of players
 * @returns {Matrix} "Random" matrix
 */
function genRandomMatrix(n) {
    var m = new Matrix(n);
    // fill first step
    for (var i = 0; i < n; ++i) {
        m.setValueAt(i, i, 1);
    }
    // fill other steps
    var hasEdge = function (v1, v2) { return m.getValueAt(v1, v2) === 0; };
    for (var step = 2; step <= n; ++step) {
        var matching = kuhn(m.n, hasEdge);
        for (var i = 0; i < n; ++i) {
            if (matching[i] !== -1) {
                m.setValueAt(matching[i], i, step);
            }
        }
    }
    // convert matrix
    var m2 = new Matrix(n);
    for (var i = 0; i < n; ++i) {
        for (var j = 0; j < n; ++j) {
            m2.setValueAt(m.getValueAt(i, j) - 1, j, i);
        }
    }
    return m2;
}
// This file have to be in sync both on server and client
// NOTE: OpCode can't be equal to 0
var OpCode;
(function (OpCode) {
    // initiated by server
    OpCode[OpCode["HOST_CHANGED"] = 6] = "HOST_CHANGED";
    OpCode[OpCode["STAGE_CHANGED"] = 1] = "STAGE_CHANGED";
    OpCode[OpCode["NEXT_STEP"] = 5] = "NEXT_STEP";
    OpCode[OpCode["RESULTS"] = 8] = "RESULTS";
    OpCode[OpCode["READY_UPDATE"] = 11] = "READY_UPDATE";
    OpCode[OpCode["TERMINATING"] = 12] = "TERMINATING";
    // initiated by players
    OpCode[OpCode["KICK_PLAYER"] = 2] = "KICK_PLAYER";
    OpCode[OpCode["START_GAME"] = 3] = "START_GAME";
    OpCode[OpCode["NEW_ROUND"] = 9] = "NEW_ROUND";
    OpCode[OpCode["REVEAL_RESULT"] = 10] = "REVEAL_RESULT";
    OpCode[OpCode["SETTINGS_UPDATE"] = 7] = "SETTINGS_UPDATE";
    OpCode[OpCode["PLAYER_INPUT"] = 4] = "PLAYER_INPUT";
})(OpCode || (OpCode = {}));
var DEFAULT_HEATH_POINT = 100;
var TICK_PER_SECOND = 40;
var MIN_PLAYER = 2;
var MAX_PLAYER = 20;
var TickRate = 16;
var DurationLobby = 10;
var DurationRoundResults = 5;
var DurationBattleEnding = 3;
var NecessaryWins = 3;
var MaxPlayers = 4;
var PlayerNotFound = -1;
var CollectionUser = "User";
var KeyTrophies = "Trophies";
var MessagesLogic = {
    3: playerWon,
    4: draw,
};
var Map = /** @class */ (function () {
    function Map() {
        this.players = new Array();
    }
    return Map;
}());
// class Player {
//   id: string = "";
//   hp: number = DEFAULT_HEATH_POINT; //heath point
//   rotation: number = 0; // hướng xoay nhân vật
//
//   Player(id: string) {
//     this.id = id;
//   }
// }
var Square = /** @class */ (function () {
    function Square() {
    }
    return Square;
}());
var joinOrCreateMatch = function (context, logger, nakama, payload) {
    var matches;
    var MatchesLimit = 1;
    var MinimumPlayers = 0;
    var label = { open: true };
    matches = nakama.matchList(MatchesLimit, true, JSON.stringify(label), MinimumPlayers, MaxPlayers - 1);
    logger.debug("Matches: ", matches);
    var matchId;
    if (matches.length > 0) {
        matchId = matches[0].matchId;
    }
    else {
        matchId = nakama.matchCreate(MatchModuleName);
    }
    var ret = {
        "matchId": matchId
    };
    logger.debug(JSON.stringify(ret));
    return JSON.stringify(ret);
};
