package com.alpha.learn.jdk;

public sealed class FourthSealedClass implements SealedClass permits FifthSealedClass {
    @Override
    public void test() {

    }
}
