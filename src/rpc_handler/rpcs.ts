let joinOrCreateMatch: nkruntime.RpcFunction = function (
    context: nkruntime.Context,
    logger: nkruntime.Logger,
    nakama: nkruntime.Nakama,
    payload: string
): string {
    let matches: nkruntime.Match[];
    const MatchesLimit = 1;
    const MinimumPlayers = 0;
    const label: MatchLabel = {open: true};
    matches = nakama.matchList(
        MatchesLimit,
        true,
        JSON.stringify(label),
        MinimumPlayers,
        MaxPlayers - 1
    );
    logger.debug("Matches: ", matches);
    let matchId: string;
    if (matches.length > 0) {
        matchId = matches[0].matchId;
    } else {
        matchId = nakama.matchCreate(MatchModuleName);
    }
    let ret = {
        "matchId": matchId
    }
    logger.debug(JSON.stringify(ret));
    return JSON.stringify(ret);
};
