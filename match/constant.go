package match

import (
	"github.com/heroiclabs/nakama-common/runtime"
	"survival2d/vector"
)

var (
	errInternalError  = runtime.NewError("internal server error", 13) // INTERNAL
	errMarshal        = runtime.NewError("cannot marshal type", 13)   // INTERNAL
	errNoInputAllowed = runtime.NewError("no input allowed", 3)       // INVALID_ARGUMENT
	errNoUserIdFound  = runtime.NewError("no user ID in context", 3)  // INVALID_ARGUMENT
	errUnmarshal      = runtime.NewError("cannot unmarshal type", 13) // INTERNAL

	vectortest = vector.NewVector(1, 1)
	test2      = vector.Sub(vectortest, vectortest)
)

const (
	RpcIdRewards   = "rewards"
	rpcIdFindMatch = "find_match"
)
