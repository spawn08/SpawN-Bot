package com.spawn.ai.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LancasterStemmer {

    private ArrayList<String> rules = new ArrayList<>();

    private int[] index = new int[26];

    private boolean stripPrefix;

    private void readRules(InputStream is) {

        try (BufferedReader input = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = input.readLine()) != null) {
                String rule = line.trim();
                if (!rule.isEmpty()) {
                    int j = rule.indexOf(' ');
                    if (j != -1) {
                        rule = rule.substring(0, j);
                    }
                    rules.add(rule);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        char ch = 'a';

        for (int j = 0; j < rules.size() - 1; j++) {
            while (rules.get(j).charAt(0) != ch) {
                ch++;
                index[charCode(ch)] = j;
            }
        }
    }

    /**
     * Constructor with default rules. By default, the stemmer will not strip prefix from words.
     */
    public LancasterStemmer(Context context) {
        this(false, context);
    }

    /**
     * Constructor with default rules.
     *
     * @param stripPrefix true if the stemmer will strip prefix such as kilo,
     *                    micro, milli, intra, ultra, mega, nano, pico, pseudo.
     */
    public LancasterStemmer(boolean stripPrefix, Context context) {
        this.stripPrefix = stripPrefix;
        readRules(loadJSONFromAsset(context));
    }

    private InputStream loadJSONFromAsset(Context context) {

        try {
            InputStream is = context.getAssets().open("lancaster-rules.txt");
            return is;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Constructor with customized rules. By default, the stemmer will not strip prefix from words.
     *
     * @param customizedRules an input stream to read customized rules.
     */
    public LancasterStemmer(InputStream customizedRules) throws IOException {
        this(customizedRules, false);
    }

    /**
     * Constructor with customized rules.
     *
     * @param customizedRules an input stream to read customized rules.
     * @param stripPrefix     true if the stemmer will strip prefix such as kilo,
     *                        micro, milli, intra, ultra, mega, nano, pico, pseudo.
     */
    private LancasterStemmer(InputStream customizedRules, boolean stripPrefix) throws IOException {
        this.stripPrefix = stripPrefix;
        readRules(customizedRules);
    }

    /**
     * Checks lowercase word for position of the first vowel
     */
    private int firstVowel(String word, int last) {
        int i = 0;
        if ((i < last) && (!(vowel(word.charAt(i), 'a')))) {
            i++;
        }
        if (i != 0) {
            while ((i < last) && (!(vowel(word.charAt(i), word.charAt(i - 1))))) {
                i++;
            }
        }
        if (i < last) {
            return i;
        }
        return last;
    }

    /**
     * Strips suffix off word
     */
    private String stripSuffixes(String word) {
        //integer variables 1 is positive, 0 undecided, -1 negative equiverlent of pun vars positive undecided negative
        int ruleok = 0;
        int Continue = 0;

        //integer varables

        int pll = 0; //position of last letter
        int xl;  //counter for nuber of chars to be replaced and length of stemmed word if rule was aplied
        int pfv; //poition of first vowel
        int prt; //pointer into rule table
        int ir;  //index of rule
        int iw;  //index of word

        //char variables

        char ll; // last letter

        //String variables eqiverlent of tenchar variables

        String rule = ""; //varlable holding the current rule
        String stem = ""; // string holding the word as it is being stemmed this is returned as a stemmed word.

        //boolean varable

        boolean intact = true; //intact if the word has not yet been stemmed to determin a requirement of some stemming rules

        //set stem = to word
        stem = cleanup(word.toLowerCase());

        // set the position of pll to the last letter in the string
        pll = 0;

        //move through the word to find the position of the last letter before a non letter char
        while ((pll + 1 < stem.length()) && ((stem.charAt(pll + 1) >= 'a') && (stem.charAt(pll + 1) <= 'z'))) {
            pll++;
        }
        if (pll < 1) {
            Continue = -1;
        }
        //find the position of the first vowel
        pfv = firstVowel(stem, pll);
        iw = stem.length() - 1;

        //repeat until continue == negative ie. -1
        while (Continue != -1) {
            Continue = 0;

            //SEEK RULE FOR A NEW FINAL LETTER
            ll = stem.charAt(pll);

            //last letter
            //Check to see if there are any possible rules for stemming
            if ((ll >= 'a') && (ll <= 'z')) {
                prt = index[charCode(ll)];
                //pointer into rule-table
            } else {
                prt = -1;
                //0 is a vaild rule
            }

            if (prt == -1) {
                Continue = -1;
                //no rule available
            }

            if (Continue == 0) {
                // THERE IS A POSSIBLE RULE (OR RULES) : SEE IF ONE WORKS
                rule = rules.get(prt);
                // Take first rule
                while (Continue == 0) {
                    ruleok = 0;
                    if (rule.charAt(0) != ll) {
                        //rule-letter changes
                        Continue = -1;
                        ruleok = -1;
                    }
                    ir = 1;
                    //index of rule: 2nd character
                    iw = pll - 1;
                    //index of word: next-last letter
                    //repeat untill the rule is not undecided find a rule that is acceptable
                    while (ruleok == 0) {
                        if ((rule.charAt(ir) >= '0') && (rule.charAt(ir) <= '9')) //rule fully matched
                        {
                            ruleok = 1;
                        } else if (rule.charAt(ir) == '*') {
                            //match only if word intact
                            if (intact) {
                                ir = ir + 1;
                                // move forwards along rule
                                ruleok = 1;
                            } else {
                                ruleok = -1;
                            }
                        } else if (rule.charAt(ir) != stem.charAt(iw)) {
                            // mismatch of letters
                            ruleok = -1;
                        } else if (iw <= pfv) {
                            //insufficient stem remains
                            ruleok = -1;
                        } else {
                            //  move on to compare next pair of letters
                            ir = ir + 1;
                            // move forwards along rule
                            iw = iw - 1;
                            // move backwards along word
                        }
                    }

                    //if the rule that has just been checked is valid
                    if (ruleok == 1) {
                        //  CHECK ACCEPTABILITY CONDITION FOR PROPOSED RULE
                        xl = 0;
                        //count any replacement letters
                        while (!((rule.charAt(ir + xl + 1) >= '.') && (rule.charAt(ir + xl + 1) <= '>'))) {
                            xl++;
                        }
                        xl = pll + xl + 48 - ((int) (rule.charAt(ir)));
                        // position of last letter if rule used
                        if (pfv == 0) {
                            //if word starts with vowel...
                            if (xl < 1) {
                                // ...minimal stem is 2 letters
                                ruleok = -1;
                            } else {
                                //ruleok=1; as ruleok must alread be positive to reach this stage
                            }
                        } //if word start swith consonant...
                        else if ((xl < 2) | (xl < pfv)) {
                            ruleok = -1;
                            // ...minimal stem is 3 letters...
                            // ...including one or more vowel
                        } else {
                            //ruleok=1; as ruleok must alread be positive to reach this stage
                        }
                    }
                    // if using the rule passes the assertion tests
                    if (ruleok == 1) {
                        //  APPLY THE MATCHING RULE
                        intact = false;
                        // move end of word marker to position...
                        // ... given by the numeral.
                        pll = pll + 48 - ((int) (rule.charAt(ir)));
                        ir++;
                        stem = stem.substring(0, (pll + 1));
                        // append any letters following numeral to the word
                        while ((ir < rule.length()) && (('a' <= rule.charAt(ir)) && (rule.charAt(ir) <= 'z'))) {
                            stem += rule.charAt(ir);
                            ir++;
                            pll++;
                        }
                        //if rule ends with '.' then terminate
                        if ((rule.charAt(ir)) == '.') {
                            Continue = -1;
                        } else {
                            //if rule ends with '>' then Continue
                            Continue = 1;
                        }
                    } else {
                        //if rule did not match then look for another
                        prt = prt + 1;
                        // move to next rule in RULETABLE
                        if (prt >= rules.size()) {
                            Continue = -1;
                        } else {
                            rule = rules.get(prt);
                            if (rule.charAt(0) != ll) {
                                //rule-letter changes
                                Continue = -1;
                            }
                        }
                    }
                }
            }
        }

        return stem;
    }

    /**
     * Determin whether ch is a vowel or not uses prev determination
     * when ch == y
     */
    private boolean vowel(char ch, char prev) {
        switch (ch) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            case 'y': {
                switch (prev) {
                    case 'a':
                    case 'e':
                    case 'i':
                    case 'o':
                    case 'u':
                        return false;
                    default:
                        return true;
                }
            }
            default:
                return false;
        }
    }

    /**
     * Returns the relavent array index for specified char 'a' to 'z'.
     */
    private static int charCode(char ch) {
        return ((int) ch) - 97;
    }

    /**
     * Removes prefixes so that suffix removal can commence.
     */
    private String stripPrefixes(String word) {
        String[] prefixes = {"kilo", "micro", "milli", "intra", "ultra", "mega",
                "nano", "pico", "pseudo"};

        int last = prefixes.length;
        for (int i = 0; i < last; i++) {
            if ((word.startsWith(prefixes[i])) && (word.length() > prefixes[i].length())) {
                word = word.substring(prefixes[i].length());
                return word;
            }
        }

        return word;
    }

    /**
     * Remove all non letter or digit characters from word
     */
    private String cleanup(String word) {
        int last = word.length();
        String temp = "";
        for (int i = 0; i < last; i++) {
            if ((word.charAt(i) >= 'a') & (word.charAt(i) <= 'z')) {
                temp += word.charAt(i);
            }
        }
        return temp;
    }

    public String stem(String word) {
        // Convert input to lowercase and remove all chars that are not a letter.
        word = cleanup(word.toLowerCase());

        //if str's length is greater than 2 then remove prefixes
        if ((word.length() > 3) && (stripPrefix)) {
            word = stripPrefixes(word);
        }

        // if str is not null remove suffix
        if (word.length() > 3) {
            word = stripSuffixes(word);
        }

        return word;
    }
}
