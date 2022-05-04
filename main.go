package main

import (
	"context"
	"database/sql"
	"github.com/heroiclabs/nakama-common/runtime"
	"survival2d/common"
	"survival2d/rpcs"
	"time"
)

//noinspection GoUnusedExportedFunction
func InitModule(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, initializer runtime.Initializer) error {
	initStart := time.Now()
	logger.Info(common.LogicLoadedLoggerInfo, time.Now().Sub(initStart).Milliseconds())
	if err := initializer.RegisterRpc(rpcs.JoinOrCreateMatchRpc, rpcs.JoinOrCreateMatch); err != nil {
		logger.Debug("ERROR:", err)
		return err
	}

	logger.Info(common.LogicLoadedLoggerInfo, time.Now().Sub(initStart).Milliseconds())
	return nil
}

////noinspection GoUnusedExportedFunction
//func InitModule(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, initializer runtime.Initializer) error {
//	initStart := time.Now()
//
//	//marshaler := &protojson.MarshalOptions{
//	//	UseEnumNumbers: true,
//	//}
//	//unmarshaler := &protojson.UnmarshalOptions{
//	//	DiscardUnknown: false,
//	//}
//
//	if err := initializer.RegisterRpc(match.RpcIdRewards, match.RpcRewards); err != nil {
//		return err
//	}
//
//	if err := initializer.RegisterRpc("healthcheck", match.RpcHealthcheck); err != nil {
//		return err
//	}
//
//	if err := initializer.RegisterRpc("TienTest", TienTest); err != nil {
//		return err
//	}
//	if err := initializer.RegisterRpc("NewTest", TienTest); err != nil {
//		return err
//	}
//
//	if err := match.RegisterSessionEvents(db, nk, initializer); err != nil {
//		return err
//	}
//
//	logger.Info("Hello Multiplayer!")
//	err := initializer.RegisterMatch("standard_match", match.NewMatch)
//	if err != nil {
//		logger.Error("[RegisterMatch] error: ", err.Error())
//		return err
//	}
//
//	if err := initializer.RegisterMatchmakerMatched(MakeMatch); err != nil {
//		logger.Error("Unable to register: %v", err)
//		return err
//	}
//
//	logger.Info("Plugin loaded in '%d' msec.", time.Now().Sub(initStart).Milliseconds())
//
//	logger.Info("Init complete!")
//	return nil
//}

func MakeMatch(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, entries []runtime.MatchmakerEntry) (string, error) {
	for _, e := range entries {
		logger.Info("Matched user '%s' named '%s'", e.GetPresence().GetUserId(), e.GetPresence().GetUsername())
		for k, v := range e.GetProperties() {
			logger.Info("Matched on '%s' value '%v'", k, v)
		}
	}

	matchId, err := nk.MatchCreate(ctx, "standard_match", map[string]interface{}{})
	if err != nil {
		return "", err
	}

	return matchId, nil
}

func TienTest(ctx context.Context, logger runtime.Logger, db *sql.DB, nk runtime.NakamaModule, payload string) (string, error) {
	logger.Info("Tien log o day, '%s'", payload)
	response := "Xin chao " + payload
	return response, nil
	//monster := sample.GetRootAsMonster([]byte(payload), 0)
	//
	//// Note: We did not set the `mana` field explicitly, so we get the
	//// default value.
	//assert(monster.Mana() == 150, "`monster.Mana()`", strconv.Itoa(int(monster.Mana())), "150")
	//assert(monster.Hp() == 300, "`monster.Hp()`", strconv.Itoa(int(monster.Hp())), "300")
	//assert(string(monster.Name()) == "Orc", "`string(monster.Name())`", string(monster.Name()),
	//	"\"Orc\"")
	//assert(monster.Color() == sample.ColorRed, "`monster.Color()`",
	//	strconv.Itoa(int(monster.Color())), strconv.Itoa(int(sample.ColorRed)))
	//
	//// Note: Whenever you access a new object, like in `Pos()`, a new temporary accessor object
	//// gets created. If your code is very performance sensitive, you can pass in a pointer to an
	//// existing `Vec3` instead of `nil`. This allows you to reuse it across many calls to reduce
	//// the amount of object allocation/garbage collection.
	//assert(monster.Pos(nil).X() == 1.0, "`monster.Pos(nil).X()`",
	//	strconv.FormatFloat(float64(monster.Pos(nil).X()), 'f', 1, 32), "1.0")
	//assert(monster.Pos(nil).Y() == 2.0, "`monster.Pos(nil).Y()`",
	//	strconv.FormatFloat(float64(monster.Pos(nil).Y()), 'f', 1, 32), "2.0")
	//assert(monster.Pos(nil).Z() == 3.0, "`monster.Pos(nil).Z()`",
	//	strconv.FormatFloat(float64(monster.Pos(nil).Z()), 'f', 1, 32), "3.0")
	//
	//// For vectors, like `Inventory`, they have a method suffixed with 'Length' that can be used
	//// to query the length of the vector. You can index the vector by passing an index value
	//// into the accessor.
	//for i := 0; i < monster.InventoryLength(); i++ {
	//	assert(monster.Inventory(i) == byte(i), "`monster.Inventory(i)`",
	//		strconv.Itoa(int(monster.Inventory(i))), strconv.Itoa(int(byte(i))))
	//}
	//
	//expectedWeaponNames := []string{"Sword", "Axe"}
	//expectedWeaponDamages := []int{3, 5}
	//weapon := new(sample.Weapon) // We need a `sample.Weapon` to pass into `monster.Weapons()`
	//// to capture the output of that function.
	//for i := 0; i < monster.WeaponsLength(); i++ {
	//	if monster.Weapons(weapon, i) {
	//		assert(string(weapon.Name()) == expectedWeaponNames[i], "`weapon.Name()`",
	//			string(weapon.Name()), expectedWeaponNames[i])
	//		assert(int(weapon.Damage()) == expectedWeaponDamages[i],
	//			"`weapon.Damage()`", strconv.Itoa(int(weapon.Damage())),
	//			strconv.Itoa(expectedWeaponDamages[i]))
	//	}
	//}
	//
	//// For FlatBuffer `union`s, you can get the type of the union, as well as the union
	//// data itself.
	//assert(monster.EquippedType() == sample.EquipmentWeapon, "`monster.EquippedType()`",
	//	strconv.Itoa(int(monster.EquippedType())), strconv.Itoa(int(sample.EquipmentWeapon)))
	//
	//unionTable := new(flatbuffers.Table)
	//if monster.Equipped(unionTable) {
	//	// An example of how you can appropriately convert the table depending on the
	//	// FlatBuffer `union` type. You could add `else if` and `else` clauses to handle
	//	// other FlatBuffer `union` types for this field. (Similarly, this could be
	//	// done in a switch statement.)
	//	if monster.EquippedType() == sample.EquipmentWeapon {
	//		unionWeapon := new(sample.Weapon)
	//		unionWeapon.Init(unionTable.Bytes, unionTable.Pos)
	//
	//		assert(string(unionWeapon.Name()) == "Axe", "`unionWeapon.Name()`",
	//			string(unionWeapon.Name()), "Axe")
	//		assert(int(unionWeapon.Damage()) == 5, "`unionWeapon.Damage()`",
	//			strconv.Itoa(int(unionWeapon.Damage())), strconv.Itoa(5))
	//	}
	//}
	//
	//fmt.Printf("The FlatBuffer was successfully created and verified!\n")

}

// A helper function to print out if an assertion failed.
func assert(assertPassed bool, codeExecuted string, actualValue string, expectedValue string) {
	if assertPassed == false {
		panic("Assert failed! " + codeExecuted + " (" + actualValue +
			") was not equal to " + expectedValue + ".")
	}
}
