package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> datesWithTotalCalories = getDatesWithTotalCalories(meals);
        return getUserMealsWithExcess(meals, startTime, endTime, caloriesPerDay, datesWithTotalCalories);
    }

    private static Map<LocalDate, Integer> getDatesWithTotalCalories(List<UserMeal> meals) {
        int totalCalories = 0;
        LocalDate date = LocalDate.now();
        Map<LocalDate, Integer> datesWithTotalCalories = new HashMap<>();
        for (UserMeal meal :
                meals) {
            if (date.equals(LocalDate.from(meal.getDateTime()))) {
                totalCalories += meal.getCalories();
            } else {
                date = LocalDate.from(meal.getDateTime());
                totalCalories = meal.getCalories();
            }
            datesWithTotalCalories.put(date, totalCalories);
        }
        return datesWithTotalCalories;
    }

    private static List<UserMealWithExcess> getUserMealsWithExcess(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay, Map<LocalDate, Integer> datesWithTotalCalories) {
        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        for (UserMeal meal :
                meals) {
            if (datesWithTotalCalories.get(LocalDate.from(meal.getDateTime())) > caloriesPerDay) {
                UserMealWithExcess userMealWithExcess = new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), true);
                if (TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime)) {
                    mealsWithExcess.add(userMealWithExcess);
                }
            }
        }
        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> datesWithTotalCalories = meals.stream()
                .collect(Collectors.toMap(k -> LocalDate.from(k.getDateTime()), UserMeal::getCalories, Integer::sum));
        List<LocalDate> datesWithExcess = datesWithTotalCalories.entrySet().stream()
                .filter(v -> v.getValue() > caloriesPerDay)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<UserMealWithExcess> mealsWithExcess = meals.stream()
                .filter(userMeal -> {
                    for (LocalDate date : datesWithExcess) {
                        if (LocalDate.from(userMeal.getDateTime()).equals(date))
                            return true;
                    }
                    return false;
                })
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), true))
                .filter(meal -> TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime))
                .collect(Collectors.toList());
        return mealsWithExcess;
    }
}
