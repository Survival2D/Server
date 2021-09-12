package src

import (
	"context"
	"database/sql"
	"encoding/json"
	"github.com/heroiclabs/nakama-common/runtime"
)

type HealCheckResponse struct {
	Success bool `json:"success"`
}

func RpcHealthcheck(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, payload string) (string, error) {
	logger.Error("Healthcheck RPC called")
	response := &HealCheckResponse{Success: true}
	out, err := json.Marshal(response)
	if err != nil {
		logger.Error("Error marshalling response type to JSON: %v", err)
		return "", runtime.NewError("Cannon marshal type", 13)
	}

	return string(out), nil
}
