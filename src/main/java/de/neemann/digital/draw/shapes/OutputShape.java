package de.neemann.digital.draw.shapes;

import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.Observer;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.draw.elements.IOState;
import de.neemann.digital.draw.elements.Pin;
import de.neemann.digital.draw.elements.Pins;
import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Orientation;
import de.neemann.digital.draw.graphics.Style;
import de.neemann.digital.draw.graphics.Vector;

/**
 * @author hneemann
 */
public class OutputShape implements Shape {
    public static final int SIZE = GenericShape.SIZE * 3 / 4;
    public static final Vector RAD = new Vector(SIZE - 6, SIZE - 6);
    public static final Vector RADL = new Vector(SIZE, SIZE);
    private final String label;
    private IOState ioState;

    public OutputShape(ElementAttributes attr) {
        this.label = attr.getLabel();
    }

    @Override
    public Pins getPins() {
        return new Pins().add(new Pin(new Vector(0, 0), "in", Pin.Direction.input));
    }

    @Override
    public Interactor applyStateMonitor(IOState ioState, Observer guiObserver) {
        this.ioState = ioState;
        ioState.getInput(0).addObserverToValue(guiObserver);
        return null;
    }

    @Override
    public void drawTo(Graphic graphic, boolean highLight) {
        Style style = Style.NORMAL;
        if (ioState != null) {
            ObservableValue value = ioState.getInput(0);
            style = Style.getWireStyle(value);
            if (value.getBits() > 1) {
                Vector textPos = new Vector(1 + SIZE, -4 - SIZE);
                graphic.drawText(textPos, textPos.add(1, 0), value.getValueString(), Orientation.CENTERBOTTOM, Style.NORMAL);
            }
        }

        Vector center = new Vector(1 + SIZE, 0);
        graphic.drawCircle(center.sub(RAD), center.add(RAD), style);
        graphic.drawCircle(center.sub(RADL), center.add(RADL), Style.NORMAL);
        Vector textPos = new Vector(SIZE * 3, 0);
        graphic.drawText(textPos, textPos.add(1, 0), label, Orientation.LEFTCENTER, Style.NORMAL);
    }
}
