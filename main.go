// Copyright 2020 The Nakama Authors
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

package main

import (
	"context"
	"database/sql"
	"github.com/heroiclabs/nakama-common/runtime"
	"surival2d/server/src"
	"time"
)

//noinspection GoUnusedExportedFunction
func InitModule(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, initializer runtime.Initializer) error {
	logger.Info("Tien log o day")
	initStart := time.Now()

	//marshaler := &protojson.MarshalOptions{
	//	UseEnumNumbers: true,
	//}
	//unmarshaler := &protojson.UnmarshalOptions{
	//	DiscardUnknown: false,
	//}

	if err := initializer.RegisterRpc(src.RpcIdRewards, src.RpcRewards); err != nil {
		return err
	}

	if err := initializer.RegisterRpc("healthcheck", src.RpcHealthcheck); err != nil {
		return err
	}

	if err := initializer.RegisterRpc("TienTest", TienTest); err != nil {
		return err
	}
	if err := initializer.RegisterRpc("NewTest", TienTest); err != nil {
		return err
	}

	if err := src.RegisterSessionEvents(db, nk, initializer); err != nil {
		return err
	}

	logger.Info("Plugin loaded in '%d' msec.", time.Now().Sub(initStart).Milliseconds())
	return nil
}

func TienTest(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, payload string) (string, error) {
	response := "Xin chao " + payload
	return response, nil
}
