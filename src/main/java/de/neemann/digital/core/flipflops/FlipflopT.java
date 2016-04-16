package de.neemann.digital.core.flipflops;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.AttributeKey;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * The T Flipflop
 *
 * @author hneemann
 */
public class FlipflopT extends Node implements Element {

    /**
     * The T-FF description
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription("T_FF", FlipflopT.class, input("C"))
            .addAttribute(AttributeKey.Rotate)
            .addAttribute(AttributeKey.Label)
            .addAttribute(AttributeKey.ValueIsProbe)
            .setShortName("T");

    private final boolean isProbe;
    private final String label;

    private ObservableValue clockVal;
    private ObservableValue q;
    private ObservableValue qn;
    private boolean lastClock;
    private boolean out;

    /**
     * Creates a new instance
     *
     * @param attributes the attributes
     */
    public FlipflopT(ElementAttributes attributes) {
        this.q = new ObservableValue("Q", 1);
        this.qn = new ObservableValue("\u00ACQ", 1);
        isProbe = attributes.get(AttributeKey.ValueIsProbe);
        label = attributes.get(AttributeKey.Label);
    }

    @Override
    public void readInputs() throws NodeException {
        boolean clock = clockVal.getBool();
        if (clock && !lastClock) {
            out = !out;
        }
        lastClock = clock;
    }

    @Override
    public void writeOutputs() throws NodeException {
        q.setBool(out);
        qn.setBool(!out);
    }

    @Override
    public void setInputs(ObservableValue... inputs) throws BitsException {
        clockVal = inputs[0].addObserverToValue(this).checkBits(1, this);
    }

    @Override
    public ObservableValue[] getOutputs() {
        return new ObservableValue[]{q, qn};
    }

    @Override
    public void registerNodes(Model model) {
        super.registerNodes(model);
        if (isProbe)
            model.addSignal(label, q);
    }

}