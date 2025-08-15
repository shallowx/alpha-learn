package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import static com.alpha.learn.jdk.EnumTests.Worker.schedule;

@Slf4j
public class EnumTests {

    @Test
    public void test() {
        Worker alice = new Worker("Alice", EnumSet.range(Day.MONDAY, Day.FRIDAY));
        Worker bob = new Worker("Bob", EnumSet.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));
        Worker charlie = new Worker("Charlie", EnumSet.of(Day.SATURDAY, Day.SUNDAY));

        EnumMap<Day, Set<String>> works = new EnumMap<>(Day.class);
        for (Day day : Day.values()) {
            works.put(day, Sets.newHashSet());
        }

        schedule(works, alice);
        schedule(works, bob);
        schedule(works, charlie);

        for (Day day : Day.values()) {
            log.info("{} : {}",day ,works.get(day));
        }
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    static class Worker {
        EnumSet<Day> days;
        String name;

        public Worker(String name, EnumSet<Day> days) {
            this.days = days;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Worker{" +
                    "days=" + days +
                    ", name='" + name + '\'' +
                    '}';
        }

        static void schedule(EnumMap<Day, Set<String>> schedule, Worker worker) {
            for (Day day : worker.days) {
                schedule.get(day).add(worker.name);
            }
        }
    }
}
