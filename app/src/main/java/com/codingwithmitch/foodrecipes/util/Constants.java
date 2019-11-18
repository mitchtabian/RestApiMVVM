package com.codingwithmitch.foodrecipes.util;

public class Constants {

    //    public static final String BASE_URL = "https://www.food2fork.com";
    public static final String BASE_URL = "https://recipesapi.herokuapp.com";

    // API_KEY is no longer necessary since food2fork has shutdown. This can be empty it doesn't matter.
//    public static final String API_KEY = "453556cb475252e7e42d65aa11912447";
    public static final String API_KEY = "";

    public static final int NETWORK_TIMEOUT = 3000;

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
