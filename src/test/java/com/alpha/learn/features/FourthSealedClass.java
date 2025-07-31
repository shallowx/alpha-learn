package com.alpha.learn.features;

public sealed class FourthSealedClass implements SealedClass permits FifthSealedClass {
    @Override
    public void test() {

    }
}
