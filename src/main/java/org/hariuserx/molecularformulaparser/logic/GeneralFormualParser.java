package org.hariuserx.molecularformulaparser.logic;

import org.hariuserx.molecularformulaparser.parser.PluginParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a general formula parser specifically designed to parse molecular<br>
 * formulas or any formula which can be portioned in to a list of substrings
 * <br>
 * that are all present in a plugin file along with their respective weights<br>
 * It supports any user defined elements that are only alphabetical<br>
 * <br>
 * If any of the letter is is upper case, the parser gives it more ranking.
 * See @example<br>
 * <br>
 * For cases where weights are absent, extent this class overriding
 * <b>init</b><br>
 * method, replacing weights any <i>constant</i><br>
 * <p>
 * author: <b>hari kishore</b> <br>
 * <p>
 * example<br> <p width=\"300\"> Say N,A,C,L,Na,Cl are present in the
 * dictionary<br>
 * <style> table { font-family: arial, sans-serif; border-collapse:
 * collapse; width: 100%; }
 * <p>
 * td, th { border: 1px solid #dddddd; text-align: left; padding:
 * 8px; }
 * <p>
 * tr:nth-child(even) { background-color: #dddddd; } </style>
 * <table>
 * <tr>
 * <th>Input</th>
 * <th>OutPut</th>
 * </tr>
 * <tr>
 * <th>NACL</th>
 * <th>N,A,C,L</th>
 * </tr>
 * <tr>
 * <th>NaCL</th>
 * <th>Na,C,L</th>
 * </tr>
 * <tr>
 * <th>naCl</th>
 * <th>n,a,Cl</th>
 * </tr>
 * <tr>
 * <th>nacl</th>
 * <th>n,a,c,l</th>
 * </tr>
 * </table>
 * <p>
 * Complexity<br> Worst case time : O(Nx(N+1)/2)<br>
 * Best case time : O(N)<br>
 * Space : O(N)
 */

public class GeneralFormualParser implements IFormulaParser {

    private final PluginParser pluginParser;
    private final Logger logger = LoggerFactory.getLogger(GeneralFormualParser.class);
    private HashMap<String, Float> lower_case_plugin = new HashMap<>();
    private String[] dynamicBreakUps;
    private int iterations = 0;
    private String breakUp = "";
    private Map<String, Integer> contributionMap;
    private Map<String, Float> plugin_data;
    private Map<String, String> new_old_plugin_mapping;


    public GeneralFormualParser() {
        pluginParser = new PluginParser();
        init();
    }

    private void init() {

        new_old_plugin_mapping = new HashMap<>();
        plugin_data = pluginParser.getPluginData();

        for (String key : plugin_data.keySet()) {
            String lowerCaseKey = key.toLowerCase();
            new_old_plugin_mapping.put(lowerCaseKey, key);
            lower_case_plugin.put(lowerCaseKey, plugin_data.get(key));
        }
    }

    @Override
    public boolean isFormulaValid() {
        return !breakUp.equals("");
    }

    @Override
    public Float getTotalWeight() {
        float mass = 0.0f;
        if (contributionMap != null && contributionMap.size() > 0) {
            for (String element : contributionMap.keySet()) {
                mass += plugin_data.get(element) * contributionMap.get(element);
            }
        }
        return mass;
    }

    @Override
    public Map<String, Integer> getElementFrequency() {
        return contributionMap;
    }

    @Override
    public String getBreakUpInReadableFormat() {
        return breakUp;
    }

    @Override
    public Map<String, Float> getPluginData() {
        return plugin_data;
    }


    private String recursionHelper(String subSubString, String fragment, int j, String breakUp) {

        if (lower_case_plugin.containsKey(subSubString.toLowerCase())) {
            breakUp += subSubString + "*";
            String next = getBreakUp(fragment, j + 1);
            breakUp += next;
            if (next.length() > 0 && next.charAt(next.length() - 1) == '%') {
                dynamicBreakUps[j] = breakUp;
                return breakUp;
            }
            if (j == fragment.length() - 1) {
                breakUp += "%";
                dynamicBreakUps[j] = breakUp;
                return breakUp;
            }
        }
        return null;
    }

    private String getBreakUp(String fragment, int f) {
        if (f < fragment.length() && !dynamicBreakUps[f].equals("?")) {
            return dynamicBreakUps[f];
        }
        String breakUp = "";
        for (int i = f; i < fragment.length(); i++) {
            iterations++;
            String sub = fragment.substring(f, i + 1);
            if (sub.length() == 1 && sub.charAt(0) >= 'A' && sub.charAt(0) <= 'Z') {
                int last_small = fragment.length() - 1;
                for (int j = i + 1; j < fragment.length(); j++) {
                    if (fragment.charAt(j) >= 'A' && fragment.charAt(j) <= 'Z') {
                        last_small = j - 1;
                        break;
                    }
                }
                for (int j = last_small; j >= i; j--) {
                    iterations++;

                    breakUp = "";
                    String subSubString = fragment.substring(i, j + 1);
                    String retValue = recursionHelper(subSubString, fragment, j, breakUp);
                    if (retValue != null)
                        return retValue;
                }
            }

            breakUp = "";
            String retValue = recursionHelper(sub, fragment, i, breakUp);
            if (retValue != null)
                return retValue;
        }
        if (f < fragment.length())
            dynamicBreakUps[f] = breakUp;
        return breakUp;
    }

    private String[] getInitialFragments(String fragment) {
        String[] initialFragments;
        initialFragments = fragment.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        return initialFragments;
    }

    private boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private int updateContributionMap(int old, String[] splits, String split, Map<String, Integer> contributionMap,
                                      int i) {
        int skip = 0;
        int value = 0;
        if (i < splits.length - 1 && isNumeric(splits[i + 1])) {
            try {
                value = Integer.parseInt(splits[i + 1]);
                skip++;
            } catch (Exception e) {
                logger.warn("Integer parse exception");
            }
            contributionMap.put(split, old + value);
        } else
            contributionMap.put(split, old + 1);

        return skip;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.strandgenomics.marray.ms.viz.kendrick.IFormulaParser#parse(java.lang.
     * String)
     */
    @Override
    public void parse(String fragment) {
        breakUp = "";
        StringBuilder breakUpBuffer = new StringBuilder();
        iterations = 0;
        String[] initialFragments = getInitialFragments(fragment);
        contributionMap = new LinkedHashMap<>();
        if (initialFragments != null && initialFragments.length > 0 && !isNumeric(initialFragments[0])) {
            for (int a = 0; a < initialFragments.length; a += 2) { // all evens are strings.
                dynamicBreakUps = new String[initialFragments[a].length()];
                for (int i = 0; i < dynamicBreakUps.length; i++)
                    dynamicBreakUps[i] = "?";
                String chunkBreakUp = getBreakUp(initialFragments[a], 0);
                if (chunkBreakUp.equals("")) {
                    breakUpBuffer.delete(0, breakUpBuffer.length());
                    break;
                }
                breakUpBuffer.append(chunkBreakUp, 0, chunkBreakUp.length() - 1);

                if (a + 1 < initialFragments.length) {
                    breakUpBuffer.append(initialFragments[a + 1]).append("*");
                }

            }
        }

        breakUp = breakUpBuffer.toString();

        System.out.println("Break Up: " + breakUp);
        System.out.println("No of iterations required: " + iterations);
        if (breakUp.equals("")) {
            return;
        } else {
            String[] splits = breakUp.split("\\*");
            for (int i = 0; i < splits.length; i++) {
                String split = new_old_plugin_mapping.get(splits[i].toLowerCase());
                int old = 0;
                if (contributionMap.containsKey(split)) {
                    old = contributionMap.get(split);
                }
                i += updateContributionMap(old, splits, split, contributionMap, i);
            }
        }
        System.out.println(contributionMap);
    }

}
