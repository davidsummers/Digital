package de.neemann.digital.core.wiring;

import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.element.AttributeKey;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;

/**
 * @author hneemann
 */
public class Driver extends Node implements Element {

    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(Driver.class, "in", "sel")
            .addAttribute(AttributeKey.Bits)
            .addAttribute(AttributeKey.FlipSelPositon);

    private final ObservableValue output;
    private final int bits;
    private ObservableValue input;
    private ObservableValue selIn;
    private long value;
    private boolean sel;

    public Driver(ElementAttributes attributes) {
        bits = attributes.get(AttributeKey.Bits);
        output = new ObservableValue("out", bits, true);
    }

    @Override
    public void readInputs() throws NodeException {
        value = input.getValue();
        sel = selIn.getBool();
    }

    @Override
    public void writeOutputs() throws NodeException {
        output.set(value, !sel);
    }

    public ObservableValue getOutput() {
        return output;
    }

    @Override
    public void setInputs(ObservableValue... inputs) throws NodeException {
        input = inputs[0].addObserver(this).checkBits(bits, this);
        selIn = inputs[1].addObserver(this).checkBits(1, this);
    }

    @Override
    public ObservableValue[] getOutputs() {
        return new ObservableValue[]{output};
    }

}