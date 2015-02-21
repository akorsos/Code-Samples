package ak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static String patternABBA = "abba";
    static String patternAAAA = "aaaa";
    static String patternAABB = "aabb";

    static String inputABBA = "redbluebluered";
    static String inputAAAA = "asdasdasdasd";
    static String inputAABB = "xyzabcxzyabc";

    public static void main(String[] args) {

        int abba = isMatched(patternABBA, inputABBA);
        System.out.println("ABBA should be 1: " +abba);

        int aaaa = isMatched(patternAAAA, inputAAAA);
        System.out.println("AAAA should be 1: " +aaaa);

        int aabb = isMatched(patternAABB, inputAABB);
        System.out.println("AABB should be 0: " +aabb);

    }

    public static int isMatched(String pattern, String input) {
        if(search(pattern.toCharArray(), 0, input.toCharArray(), 0, new HashMap<Character, String>())){
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean search(char[] pattern, int startPattern,
                                 char[] input, int startInput, Map<Character, String> definition) {

        boolean finishedPattern = startPattern >= pattern.length;
        boolean finishedInput = startInput >= input.length;

        if (finishedInput && finishedPattern) {
            return true;
        } else {
            if (finishedInput ^ finishedPattern) {
                return false;
            }
        }

        char nextPatternChar = pattern[startPattern];

        //if the next character in pattern is already defined, check its definition as prefix of the input
        if (definition.containsKey(nextPatternChar)) {
            String charDef = definition.get(nextPatternChar);

            if (startInput + charDef.length() - 1 >= input.length) {
                return false;
            }

            for (int i = 0; i < charDef.length(); i++) {
                if (charDef.charAt(i) != input[startInput + i]) {
                    return false;
                }
            }

            return search(pattern, startPattern + 1, input, startInput
                    + charDef.length(), definition);
        } else {

            StringBuilder builder = new StringBuilder();

            //try all possible definition of the current character in the pattern
            for (int i = startInput; i < input.length; i++) {
                builder.append(input[i]);

                String newDef = builder.toString();

                //make sure the definition is distinct
                if (!definition.containsValue(newDef)) {
                    definition.put(nextPatternChar, newDef);

                    boolean isMatch = search(pattern, startPattern + 1, input, startInput + newDef.length(), definition);

                    if (isMatch) {
                        return true;
                    }

                    definition.remove(nextPatternChar);
                }
            }

            return false;
        }
    }
}

