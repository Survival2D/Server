package rpcs

import (
	"context"
	"database/sql"
	"encoding/json"
	"github.com/heroiclabs/nakama-common/runtime"
	"survival2d/common"
)

const (
	JoinOrCreateMatchRpc = "JoinOrCreateMatchRpc"
)

type JoinOrCreateMatchRes struct {
	matchId string
}

func JoinOrCreateMatch(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, payload string) (string, error) {
	limit := 1
	label, _ := json.Marshal(common.MatchLabel{Open: true})
	minSize := common.MinPlayers
	maxSize := common.MaxPlayers - 1
	var matchId string

	matches, err := nk.MatchList(ctx, limit, true, string(label), &minSize, &maxSize, "")
	if err != nil {
		logger.WithField("err", err).Error("Match list error.")
		return "", err
	}
	logger.Debug("Matches: ", matches)
	if len(matches) > 0 {
		matchId = matches[0].GetMatchId()
	} else {
		matchId, _ = nk.MatchCreate(ctx, common.MatchModuleName, map[string]interface{}{})
	}
	logger.Debug("MatchId: ", matchId)
	ret, err := json.Marshal(JoinOrCreateMatchRes{matchId: matchId})
	return string(ret), err
}
