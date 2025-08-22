package com.alpha.learn.jdk;

public sealed interface SealedClass permits OnSealedClass, SecondlySealedClass, FourthSealedClass{

    void test();
}
