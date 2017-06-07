package com.omikronsoft.notepad.utils;

import java.util.Random;

/**
 * Created by Dariusz Lelek on 6/1/2017.
 * dariusz.lelek@gmail.com
 */

public class QuoteProvider {
    private static final String[] QUOTES = {
            "If you can dream it, you can do it.",
            "Failure will never overtake me if my determination to succeed is strong enough.",
            "Setting goals is the first step in turning the invisible into the visible.",
            "Accept the challenges so that you can feel the exhilaration of victory.",
            "The secret of getting ahead is getting started.",
            "Don't watch the clock; do what it does. Keep going.",
            "You are never too old to set another goal or to dream a new dream.",
            "Never, never, never give up.",
            "A creative man is motivated by the desire to achieve, not by the desire to beat others.",
            "Set your goals high, and don't stop till you get there.",
            "What you get by achieving your goals is not as important as what you become by achieving your goals.",
            "Either move or be moved."
    };

    private static final String[] AUTHORS = {
            "Walt Disney",
            "Og Mandino",
            "Tony Robbins",
            "George S. Patton",
            "Mark Twain",
            "Sam Levenson",
            "Les Brown",
            "Winston Churchill",
            "Ayn Rand",
            "Bo Jackson",
            "Zig Ziglar",
            "Ezra Pound"
    };

    private static final String QUOTE_MARK = "\"";
    private static final String AUTHOR_MARK = "- ";

    public static int getRandomInt(){
        Random rnd = new Random();
        return rnd.nextInt(QUOTES.length - 1);
    }

    public static String getQuote(int val){
        return QUOTE_MARK + QUOTES[val] + QUOTE_MARK;
    }

    public static String getAuthor(int val){
        return AUTHOR_MARK + AUTHORS[val];
    }
}
