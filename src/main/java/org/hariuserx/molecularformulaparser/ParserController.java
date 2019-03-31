package org.hariuserx.molecularformulaparser;

import org.hariuserx.molecularformulaparser.logic.GeneralFormualParser;
import org.hariuserx.molecularformulaparser.logic.IFormulaParser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hari kishore
 */
@RestController
public class ParserController {

    private final IFormulaParser generalFormualParser = new GeneralFormualParser();

    @RequestMapping("/")
    public String index(){
        return "Hello World";
    }

    @RequestMapping("/parse/{formula}")
    public String parseFormula(@PathVariable("formula") String formula){
        generalFormualParser.parse(formula);
        return generalFormualParser.getBreakUpInReadableFormat();
    }
}
