package vector

import (
	"reflect"
	"testing"
)

func Test_add(t *testing.T) {
	v1 := NewVector(0, 0)
	v2 := NewVector(1, 1)
	want := NewVector(1, 1)
	got := Add(v1, v2)
	if !reflect.DeepEqual(got, want) {
		t.Error("#{got} #{want}")
	}
}
