package de.neemann.digital.gui.components.table;

import de.neemann.digital.analyse.expression.Expression;
import de.neemann.digital.analyse.expression.ExpressionException;
import de.neemann.digital.analyse.expression.format.FormatterException;
import de.neemann.digital.analyse.parser.ParseException;
import de.neemann.digital.analyse.parser.Parser;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by helmut.neemann on 01.12.2016.
 */
public class ExpressionListenerJKTest extends TestCase {

    public void testSimple() throws IOException, ParseException, FormatterException, ExpressionException {
        ExpressionListenerOptimizeJKTest.TestEL exp = new ExpressionListenerOptimizeJKTest.TestEL();

        ExpressionListener elojk = new ExpressionListenerJK(exp);
        Expression e1=new Parser("(Sn*A)+(!Sn*B)").parse().get(0);
        elojk.resultFound("Sn+1", e1);
        elojk.close();

        assertEquals(3, exp.getList().size());
        assertEquals(e1, exp.getList().get(0));
        assertEquals("B", exp.getList().get(1).toString());
        assertEquals("not(A)", exp.getList().get(2).toString());
    }

}