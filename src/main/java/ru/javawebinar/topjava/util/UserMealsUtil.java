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

//        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
//        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> datesWithCaloriesPerDay = getDatesWithCaloriesPerDay(meals);
        List<UserMealWithExcess> listOfUserMealsWithExcess = getListOfUserMealsWithExcess(meals, caloriesPerDay, datesWithCaloriesPerDay);
        List<UserMealWithExcess> userMealsWithExcess = getMealsWithExcess(listOfUserMealsWithExcess);
        List<UserMealWithExcess> result = filteredByInterval(startTime, endTime, userMealsWithExcess);
        return result;
    }

    private static Map<LocalDate, Integer> getDatesWithCaloriesPerDay(List<UserMeal> meals) {
        return meals.stream()
                .collect(Collectors.toMap(k -> LocalDate.from(k.getDateTime()), UserMeal::getCalories, Integer::sum));
    }

    private static List<UserMealWithExcess> getListOfUserMealsWithExcess(List<UserMeal> meals, int caloriesPerDay, Map<LocalDate, Integer> datesWithTotalCalories) {
        return meals.stream()
                .map(userMeal -> {
                    UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
                    for (Map.Entry<LocalDate, Integer> pair : datesWithTotalCalories.entrySet()) {
                        if (pair.getValue() > caloriesPerDay)
                            userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true);
                    }
                    return userMealWithExcess;
                })
                .collect(Collectors.toList());
    }

    private static List<UserMealWithExcess> getMealsWithExcess(List<UserMealWithExcess> listOfUserMealsWithExcess) {
        return listOfUserMealsWithExcess.stream()
                .filter(UserMealWithExcess::isExcess)
                .collect(Collectors.toList());
    }

    private static List<UserMealWithExcess> filteredByInterval(LocalTime startTime, LocalTime endTime, List<UserMealWithExcess> userMealWithExcesses) {
        return userMealWithExcesses.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime))
                .collect(Collectors.toList());
    }
}
