package de.neemann.digital.core.memory;

import de.neemann.digital.TestExecuter;
import de.neemann.digital.core.Model;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.Keys;
import junit.framework.TestCase;

import static de.neemann.digital.TestExecuter.HIGHZ;
import static de.neemann.digital.core.ObservableValues.ovs;

/**
 * @author hneemann
 */
public class Belt16x16Test extends TestCase
{
  public void testRAM() throws Exception
  {
    ObservableValue clk  = new ObservableValue( "CLK",  1 );
    ObservableValue str0 = new ObservableValue( "STR0", 1 );
    ObservableValue wd0  = new ObservableValue( "WD0",  4 );
    ObservableValue str1 = new ObservableValue( "STR1", 1 );
    ObservableValue wd1  = new ObservableValue( "WD1",  4 );
    ObservableValue str2 = new ObservableValue( "STR2", 1 );
    ObservableValue wd2  = new ObservableValue( "WD2",  4 );
    ObservableValue str3 = new ObservableValue( "STR3", 1 );
    ObservableValue wd3  = new ObservableValue( "WD3",  4 );
    ObservableValue str4 = new ObservableValue( "STR4", 1 );
    ObservableValue wd4  = new ObservableValue( "WD4",  4 );
    ObservableValue str5 = new ObservableValue( "STR5", 1 );
    ObservableValue wd5  = new ObservableValue( "WD5",  4 );
    ObservableValue str6 = new ObservableValue( "STR6", 1 );
    ObservableValue wd6  = new ObservableValue( "WD6",  4 );
    ObservableValue str7 = new ObservableValue( "STR7", 1 );
    ObservableValue wd7  = new ObservableValue( "WD7",  4 );
    ObservableValue ra0  = new ObservableValue( "RA0",  4 );
    ObservableValue ra1  = new ObservableValue( "RA1",  4 );

    Model model = new Model( );
    Belt16x16 out = model.add( new Belt16x16(
      new ElementAttributes( )
        .set( Keys.ADDR_BITS, 4 )
        .setBits( 4 ) ) );
    out.setInputs( ovs( clk,
                        str0,
                        wd0,
                        str1,
                        wd1,
                        str2,
                        wd2,
                        str3,
                        wd3,
                        str4,
                        wd4,
                        str5,
                        wd5,
                        str6,
                        wd6,
                        str7,
                        wd7,
                        ra0,
                        ra1 ) );

    TestExecuter sc =
      new TestExecuter( model ).setInputs(
        clk,
        str0,
        wd0,
        str1,
        wd1,
        str2,
        wd2,
        str3,
        wd3,
        str4,
        wd4,
        str5,
        wd5,
        str6,
        wd7,
        str7,
        wd7,
        ra0 )
        .setOutputs( out.getOutputs( ) );
    //        C  ST0, WD0, ST1, WD1, ST2, WD2, ST3, WD3, ST4, WD4, ST5, WD5, ST6, WD6, ST7, WD7, RA0, RA1
    sc.check( 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0, 0 );  // def
  }
}
