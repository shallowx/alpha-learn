package _go

import (
	"fmt"
	"testing"
)

func TestArr(t *testing.T) {
	a := [2]int{}
	a[0] = 1
	a[1] = 2

	t.Logf("a[0]: %v, a[1]: %v", a[0], a[1])
	fmt.Printf("len: %d, cap: %d \n", len(a), cap(a))

	as := a[:]
	newSlice := make([]int, len(a))
	copy(newSlice, as)
	newSlice[0] = 100
	fmt.Println(as)
	fmt.Println(newSlice)
	b := [2]int{}
	b = testCopyArray(a, b)
	fmt.Println(b)

	nc := make([]int, len(newSlice))
	testCopySlice(newSlice, nc)
	fmt.Println(nc)
}

func testCopyArray(src, dst [2]int) [2]int {
	for i := 0; i < len(src); i++ {
		dst[i] = src[i]
	}
	return dst
}

func testCopySlice(src, dst []int) {
	for i := 0; i < len(src); i++ {
		dst[i] = src[i]
	}
}
