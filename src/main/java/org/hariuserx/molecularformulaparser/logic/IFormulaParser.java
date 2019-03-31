package org.hariuserx.molecularformulaparser.logic;

import java.util.Map;

/**
 * Implement this interface to parse/fragment a string in to <br>
 * substrings such that all the substrings belong to a pre-known dictionary. Any
 * such problem can be solved using a dynamic programming with. <br>
 * Time : <html>O(n<sup>2</sup>)</html><br>
 * Space : <html>O(n)</html>
 *
 * @author hari kishore
 */
public interface IFormulaParser {

    void parse(String fragment);

    boolean isFormulaValid();

    Map<String, Integer> getElementFrequency();

    String getBreakUpInReadableFormat();

    Float getTotalWeight();

    Map<String, Float> getPluginData();

}