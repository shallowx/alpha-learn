package _go

import (
	"fmt"
	"sync"
	"testing"
)

func TestChannel(t *testing.T) {

	ch := make(chan int, 3)
	ch <- 1
	ch <- 2
	ch <- 3

	e := <-ch
	e1 := <-ch
	e2 := <-ch
	fmt.Println(e)
	fmt.Println(e1)
	fmt.Println(e2)

	var wg sync.WaitGroup
	wg.Add(1)
	go func() {
		ch <- 1
	}()

	go func() {
		defer wg.Done()
		element := <-ch
		fmt.Println(element)
	}()
	wg.Wait()
}
