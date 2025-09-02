package _go

import (
	"container/list"
	"fmt"
	"testing"
)

func TestList(t *testing.T) {
	var l list.List
	fmt.Println(l)

	l.PushFront("jimmy")
	front := l.PushFront("golang")
	fmt.Println(front)
}
