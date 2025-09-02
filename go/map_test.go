package _go

import (
	"fmt"
	"testing"
)

func TestMap(t *testing.T) {
	aMap := map[string]int{
		"one":   1,
		"two":   2,
		"three": 3,
	}

	v, ok := aMap["one"]
	if ok {
		fmt.Printf("The element of key one: %d\n", v)
	}
}
