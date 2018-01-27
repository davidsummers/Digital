package de.neemann.digital.core.memory;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import static de.neemann.digital.core.element.PinInfo.input;
import java.util.HashSet;

/**
 * Belt implementation (16 frames, 16 positions)
 *
 * @author david@summersoft.fay-ar.us
 */
public class Belt16x16 extends Node implements Element, RAMInterface
{
  /**
   * The RAMs {@link ElementTypeDescription}
   */
  public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription( Belt16x16.class,
    input( "C"      ),
    input( "STR0"   ),
    input( "WD0"    ),
    input( "STR1"   ),
    input( "WD1"    ),
    input( "STR2"   ),
    input( "WD2"    ),
    input( "STR3"   ),
    input( "WD3"    ),
    input( "STR4"   ),
    input( "WD4"    ),
    input( "STR5"   ),
    input( "WD5"    ),
    input( "STR6"   ),
    input( "WD6"    ),
    input( "STR7"   ),
    input( "WD7"    ),
    input( "RA0"    ),
    input( "RA1"    ),
    input( "NFRAME" ),
    input( "PFRAME" ),
    input( "ADDR"   ),
    input( "WDATA"  ),
    input( "WSTR"   ))
    .addAttribute( Keys.ROTATE    )
    .addAttribute( Keys.BITS      )
    .addAttribute( Keys.ADDR_BITS )
    .addAttribute( Keys.LABEL     );

  private final DataField       m_belt;
  private final DataField       m_Counters;
  private final DataField       m_Pointers;
  private final int             m_addrBits;
  private final int             m_bits;
  private final String          m_label;
  private final int             m_size;
  
  private int                   m_raddr0;
  private int                   m_raddr1;
  private boolean               m_str0;
  private boolean               m_str1;
  private boolean               m_str2;
  private boolean               m_str3;
  private boolean               m_str4;
  private boolean               m_str5;
  private boolean               m_str6;
  private boolean               m_str7;
  private boolean               m_nframe;
  private boolean               m_pframe;
  private long                  m_wd0;
  private long                  m_wd1;
  private long                  m_wd2;
  private long                  m_wd3;
  private long                  m_wd4;
  private long                  m_wd5;
  private long                  m_wd6;
  private long                  m_wd7;
  private int                   m_addr;
  private boolean               m_WStr;
  private long                  m_WData;
  
  private int                   m_CurrentFrame = 0;
  private boolean               m_lastClk = false;
  
  private ObservableValue       m_data1In;
  private ObservableValue       m_str0In;
  private ObservableValue       m_str1In;
  private ObservableValue       m_str2In;
  private ObservableValue       m_str3In;
  private ObservableValue       m_str4In;
  private ObservableValue       m_str5In;
  private ObservableValue       m_str6In;
  private ObservableValue       m_str7In;
  private ObservableValue       m_clk1In;
  private ObservableValue       m_wd0In;
  private ObservableValue       m_wd1In;
  private ObservableValue       m_wd2In;
  private ObservableValue       m_wd3In;
  private ObservableValue       m_wd4In;
  private ObservableValue       m_wd5In;
  private ObservableValue       m_wd6In;
  private ObservableValue       m_wd7In;
  private ObservableValue       m_ra0In;
  private ObservableValue       m_ra1In;
  private ObservableValue       m_nframeIn;
  private ObservableValue       m_pframeIn;
  private ObservableValue       m_AddrIn;
  private ObservableValue       m_WDataIn;
  private ObservableValue       m_WStrIn;
  
  private final ObservableValue m_out0;
  private final ObservableValue m_out1;
  private final ObservableValue m_cframe;
  private final ObservableValue m_data;


  /**
   * Creates a new instance
   *
   * @param attr the elements attributes
   */
  public Belt16x16( ElementAttributes attr_ )
  {
    super( true ); // true = hasState
    m_bits     = attr_.get( Keys.BITS      );
    m_addrBits = attr_.get( Keys.ADDR_BITS );
    m_out0     = new ObservableValue( "RD0",    m_bits     ).setPinDescription( DESCRIPTION );
    m_out1     = new ObservableValue( "RD1",    m_bits     ).setPinDescription( DESCRIPTION );
    m_cframe   = new ObservableValue( "CFRAME", m_addrBits ).setPinDescription( DESCRIPTION );
    m_data     = new ObservableValue( "DATA",   m_bits     ).setPinDescription( DESCRIPTION );
    m_size     = 1 << m_addrBits;
    m_belt     = new DataField( m_size * m_size );
    m_Counters = new DataField( m_size );
    m_Pointers = new DataField( m_size );
    m_label    = attr_.getCleanLabel( );
  }

  @Override
  public void setInputs( ObservableValues inputs_ ) throws NodeException
  {
    m_clk1In   = inputs_.get(  0 ).checkBits( 1,              this ).addObserverToValue( this );
    m_str0In   = inputs_.get(  1 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd0In    = inputs_.get(  2 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str1In   = inputs_.get(  3 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd1In    = inputs_.get(  4 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str2In   = inputs_.get(  5 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd2In    = inputs_.get(  6 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str3In   = inputs_.get(  7 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd3In    = inputs_.get(  8 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str4In   = inputs_.get(  9 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd4In    = inputs_.get( 10 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str5In   = inputs_.get( 11 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd5In    = inputs_.get( 12 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str6In   = inputs_.get( 13 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd6In    = inputs_.get( 14 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_str7In   = inputs_.get( 15 ).checkBits( 1,              this ).addObserverToValue( this );
    m_wd7In    = inputs_.get( 16 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_ra0In    = inputs_.get( 17 ).checkBits( m_addrBits,     this ).addObserverToValue( this );
    m_ra1In    = inputs_.get( 18 ).checkBits( m_addrBits,     this ).addObserverToValue( this );
    m_nframeIn = inputs_.get( 19 ).checkBits( 1,              this ).addObserverToValue( this );
    m_pframeIn = inputs_.get( 20 ).checkBits( 1,              this ).addObserverToValue( this );
    m_AddrIn   = inputs_.get( 21 ).checkBits( m_addrBits * 2, this ).addObserverToValue( this );
    m_WDataIn  = inputs_.get( 22 ).checkBits( m_bits,         this ).addObserverToValue( this );
    m_WStrIn   = inputs_.get( 23 ).checkBits( 1,              this ).addObserverToValue( this );
  }

  @Override
  public ObservableValues getOutputs( )
  {
    return new ObservableValues( m_out0, m_out1, m_cframe, m_data );
  }

  @Override
  public void readInputs( ) throws NodeException
  {
    int storeCount = 0;
    int currentCount = 0;
    int currentPointer = 0;
    boolean clk = m_clk1In.getBool( );
    m_str0 = m_str0In.getBool( );
    m_str1 = m_str1In.getBool( );
    m_str2 = m_str2In.getBool( );
    m_str3 = m_str3In.getBool( );
    m_str4 = m_str4In.getBool( );
    m_str5 = m_str5In.getBool( );
    m_str6 = m_str6In.getBool( );
    m_str7 = m_str7In.getBool( );
    m_WStr = m_WStrIn.getBool( );
    
    if ( !m_lastClk && clk )
    {      
      if ( m_str0 )
      {
        m_wd0 = m_wd0In.getValue();
        storeCount++;
      }

      if ( m_str1 )
      {
        m_wd1 = m_wd1In.getValue();
        storeCount++;
      }

      if ( m_str2 )
      {
        m_wd2 = m_wd2In.getValue();
        storeCount++;
      }

      if ( m_str3 )
      {
        m_wd3 = m_wd3In.getValue();
        storeCount++;
      }

      if ( m_str4 )
      {
        m_wd4 = m_wd4In.getValue();
        storeCount++;
      }

      if ( m_str5 )
      {
        m_wd5 = m_wd5In.getValue();
        storeCount++;
      }

      if ( m_str6 )
      {
        m_wd6 = m_wd6In.getValue();
        storeCount++;
      }

      if ( m_str7 )
      {
        m_wd7 = m_wd7In.getValue();
        storeCount++;
      }
      
      m_nframe = m_nframeIn.getBool( );
      m_pframe = m_pframeIn.getBool( );
      
      if ( m_WStr )
      {
        m_WData  = m_WDataIn.getValue( );
      }
    }
    else
    {
      m_str0 = false;
      m_str1 = false;
      m_str2 = false;
      m_str3 = false;
      m_str4 = false;
      m_str5 = false;
      m_str6 = false;
      m_str7 = false;
      m_WStr = false;
    }
        
    if ( storeCount > 0 )
    {
      currentCount = GetCurrentCount( );
      currentCount = MaxSize( currentCount + storeCount );            
      SetCurrentCount( currentCount );

      currentPointer = GetCurrentPointer( );
      currentPointer = ModSize( currentPointer - storeCount );
      SetCurrentPointer( currentPointer );
    }
    
    
    m_raddr0 = (int) m_ra0In.getValue(  );
    m_raddr1 = (int) m_ra1In.getValue(  );
    m_addr   = (int) m_AddrIn.getValue( );
    
    m_lastClk = clk;
  }

  @Override
  public void writeOutputs( ) throws NodeException
  {
    int currentCount = 0;

    if ( m_str0 )
    {
      SetBelt( currentCount, m_wd0 );
      currentCount++;
    }

    if ( m_str1 )
    {
      SetBelt( currentCount, m_wd1 );
      currentCount++;
    }

    if ( m_str2 )
    {
      SetBelt( currentCount, m_wd2 );
      currentCount++;
    }

    if ( m_str3 )
    {
      SetBelt( currentCount, m_wd3 );
      currentCount++;
    }

    if ( m_str4 )
    {
      SetBelt( currentCount, m_wd4 );
      currentCount++;
    }

    if ( m_str5 )
    {
      SetBelt( currentCount, m_wd5 );
      currentCount++;
    }

    if ( m_str6 )
    {
      SetBelt( currentCount, m_wd6 );
      currentCount++;
    }

    if ( m_str7 )
    {
      SetBelt( currentCount, m_wd7 );
      currentCount++;
    }

    if ( m_nframe )
    {
      boolean minusOne = true;
      m_CurrentFrame = MaxSize( m_CurrentFrame + 1, minusOne );
      m_nframe = false;
    }
    
    if ( m_pframe )
    {
      boolean minusOne = true;
      m_CurrentFrame = MaxSize( m_CurrentFrame - 1, minusOne );
      m_pframe = false;
    }
    
    if ( m_WStr )
    {
      m_belt.setData( m_addr, m_WData );
      m_WStr = false;
    }
    
    m_out0.setValue( GetBelt( m_raddr0 ) );    
    m_out1.setValue( GetBelt( m_raddr1 ) );
    m_cframe.setValue( m_CurrentFrame );
    m_data.setValue( m_belt.getDataWord( m_addr ) );
  }

  @Override
  public DataField getMemory()
  {
    return m_belt;
  }

  @Override
  public String getLabel()
  {
    return m_label;
  }

  @Override
  public int getSize()
  {
    return m_size;
  }

  @Override
  public int getDataBits()
  {
    return m_bits;
  }

  @Override
  public int getAddrBits()
  {
    return m_addrBits;
  }  
  
  private int MaxSize( int value_ )
  {
    return MaxSize( value_, false );
  }
  
  private int MaxSize( int value_, boolean minusOne_ )
  {
    int ret = value_;
    
    if ( value_ < 0 )
    {
      ret = 0;
    }
    
    if ( minusOne_ )
    {
      if ( value_ >= m_size )
      {
        ret = m_size - 1;
      }
    }
    else
    {
      if ( value_ > m_size )
      {
        ret = m_size;
      }
    }
    
    return ret;
  }
  
  private int ModSize( int value_ )
  {
    int ret = value_;
    
    if ( ret < 0 )
    {
      ret += m_size;
    }
    
    if ( ret >= m_size )
    {
      ret -= m_size;
    }
    
    return ret;
  }
  
  private int GetCurrentCount( )
  {
    return (int) m_Counters.getDataWord( m_CurrentFrame );
  }
  
  private void SetCurrentCount( int value_ )
  {
    m_Counters.setData( m_CurrentFrame, value_ );
  }
  
  private int GetCurrentPointer( )
  {
    return (int) m_Pointers.getDataWord( m_CurrentFrame );
  }
  
  private void SetCurrentPointer( int value_ )
  {
    m_Pointers.setData( m_CurrentFrame, value_ );
  }
  
  private long GetBelt( int offset_ )
  {
    long value = 0;
    
    if ( GetCurrentCount( ) > 0 )
    {
      int realAddr = CalculateAddress( offset_ );
      value = m_belt.getDataWord( realAddr );
    }
    
    return value;
  }
  
  private void SetBelt( int offset_, long value_ )
  {
    int realAddr = CalculateAddress( offset_ );
    m_belt.setData( realAddr, value_);
  }
  
  private int CalculateAddress( int offset_ )
  {
    return m_CurrentFrame * m_size + ModSize( GetCurrentPointer( ) + offset_ );
  }
}
