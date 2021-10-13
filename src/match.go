package src

import (
	"context"
	"database/sql"
	"github.com/heroiclabs/nakama-common/runtime"
)

type MatchState struct {
	presences map[string]runtime.Presence
}

type Match struct{}

func NewMatch(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule) (m runtime.Match, err error) {
	logger.Info("NewMatch")
	return &Match{}, nil
}

func (m *Match) MatchInit(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, params map[string]interface{}) (interface{}, int, string) {
	logger.Info("MatchInit")
	state := &MatchState{
		presences: make(map[string]runtime.Presence),
	}
	tickRate := 1
	label := ""
	return state, tickRate, label
}

func (m *Match) MatchJoinAttempt(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, dispatcher runtime.MatchDispatcher, tick int64, state interface{}, presence runtime.Presence, metadata map[string]string) (interface{}, bool, string) {
	logger.Info("MatchJoinAttempt")
	acceptUser := true
	return state, acceptUser, ""
}

func (m *Match) MatchJoin(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, dispatcher runtime.MatchDispatcher, tick int64, state interface{}, presences []runtime.Presence) interface{} {
	logger.Info("MatchJoin")
	mState, _ := state.(*MatchState)
	for _, p := range presences {
		mState.presences[p.GetUserId()] = p
	}
	return mState
}

func (m *Match) MatchLeave(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, dispatcher runtime.MatchDispatcher, tick int64, state interface{}, presences []runtime.Presence) interface{} {
	logger.Info("MatchLeave")
	mState, _ := state.(*MatchState)
	for _, p := range presences {
		delete(mState.presences, p.GetUserId())
	}
	return mState
}

func (m *Match) MatchLoop(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, dispatcher runtime.MatchDispatcher, tick int64, state interface{}, messages []runtime.MatchData) interface{} {
	logger.Info("MatchLoop")
	mState, _ := state.(*MatchState)
	for _, presence := range mState.presences {
		logger.Info("Presence %v named %v", presence.GetUserId(), presence.GetUsername())
	}
	for _, message := range messages {
		logger.Info("Received %v from %v", string(message.GetData()), message.GetUserId())
		reliable := true
		err := dispatcher.BroadcastMessage(1, message.GetData(), []runtime.Presence{message}, nil, reliable)
		if err != nil {
			return err
		}
	}
	return mState
}

func (m *Match) MatchTerminate(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, dispatcher runtime.MatchDispatcher, tick int64, state interface{}, graceSeconds int) interface{} {
	logger.Info("MatchTerminate")
	//message := "Server shutting down in " + strconv.Itoa(graceSeconds) + " seconds."
	//reliable := true
	//dispatcher.BroadcastMessage(2, []byte(message), []runtime.Presence{}, reliable)
	return state
}
