package com.alpha.learn.jdk25;

public sealed interface SealedClass permits OnSealedClass, SecondlySealedClass, FourthSealedClass{

    void test();
}
