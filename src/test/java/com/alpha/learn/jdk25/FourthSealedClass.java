package com.alpha.learn.jdk25;

public sealed class FourthSealedClass implements SealedClass permits FifthSealedClass {
    @Override
    public void test() {

    }
}
