let joinOrCreateMatch: nkruntime.RpcFunction = function (
  context: nkruntime.Context,
  logger: nkruntime.Logger,
  nakama: nkruntime.Nakama,
  payload: string
): string {
  let matches: nkruntime.Match[];
  const MatchesLimit = 1;
  const MinimumPlayers = 0;
  const label: MatchLabel = { open: true };
  matches = nakama.matchList(
    MatchesLimit,
    true,
    JSON.stringify(label),
    MinimumPlayers,
    MaxPlayers - 1
  );
  logger.debug("Matches: ", matches);
  if (matches.length > 0) return matches[0].matchId;
  return nakama.matchCreate(MatchModuleName);
};
