package vector

type Vector struct {
	x int
	y int
}

func NewVector(x int, y int) Vector {
	v := new(Vector)
	v.x = x
	v.y = y
	return *v
}

func Add(v1 Vector, v2 Vector) Vector {
	return NewVector(v1.x+v2.x, v1.y+v2.y)
}

func Sub(v1 Vector, v2 Vector) Vector {
	return NewVector(v1.x-v2.x, v1.y-v2.y)
}
