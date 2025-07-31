package com.alpha.learn.features;

public sealed interface SealedClass permits OnSealedClass, SecondlySealedClass, FourthSealedClass{

    void test();
}
