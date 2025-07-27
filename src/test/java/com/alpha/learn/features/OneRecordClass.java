package com.alpha.learn.features;

import java.util.Objects;

public record OneRecordClass(String name, int age) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OneRecordClass that = (OneRecordClass) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "OneRecordClass {" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
