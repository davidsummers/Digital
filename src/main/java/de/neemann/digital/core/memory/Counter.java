package de.neemann.digital.core.memory;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.*;

import static de.neemann.digital.core.ObservableValues.ovs;
import static de.neemann.digital.core.element.PinInfo.input;

/**
 * A simple counter.
 *
 * @author hneemann
 */
public class Counter extends Node implements Element {

    /**
     * The counters {@link ElementTypeDescription}
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription(Counter.class, input("en"), input("C"), input("clr"))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.INVERTER_CONFIG)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.VALUE_IS_PROBE);

    private final ObservableValue out;
    private final ObservableValue ovf;
    private final long maxValue;
    private final boolean probe;
    private final String label;
    private ObservableValue clockIn;
    private ObservableValue clrIn;
    private ObservableValue enable;
    private boolean lastClock;
    private long counter;
    private boolean ovfOut = false;

    /**
     * Creates a new instance
     *
     * @param attributes the elements attributes
     */
    public Counter(ElementAttributes attributes) {
        super(true);
        int bits = attributes.getBits();
        this.out = new ObservableValue("out", bits).setPinDescription(DESCRIPTION);
        this.ovf = new ObservableValue("ovf", 1).setPinDescription(DESCRIPTION);
        maxValue = (1L << bits) - 1;
        probe = attributes.get(Keys.VALUE_IS_PROBE);
        label = attributes.getCleanLabel();
    }

    @Override
    public void readInputs() throws NodeException {
        boolean clock = clockIn.getBool();
        boolean enable = this.enable.getBool();
        if (clock && !lastClock) {
            if (enable) {
                if (counter == maxValue)
                    counter = 0;
                else
                    counter++;
            }
            if (clrIn.getBool())
                counter = 0;
        }

        lastClock = clock;

        ovfOut = (counter == maxValue) && enable;
    }

    @Override
    public void writeOutputs() throws NodeException {
        ovf.setBool(ovfOut);
        out.setValue(counter);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws BitsException {
        enable = inputs.get(0).addObserverToValue(this).checkBits(1, this, 0);
        clockIn = inputs.get(1).addObserverToValue(this).checkBits(1, this, 1);
        clrIn = inputs.get(2).addObserverToValue(this).checkBits(1, this, 2);
    }

    @Override
    public ObservableValues getOutputs() {
        return ovs(out, ovf);
    }


    @Override
    public void registerNodes(Model model) {
        super.registerNodes(model);
        if (probe)
            model.addSignal(new Signal(label, out, (v, z) -> {
                counter = v;
                boolean o = (counter == maxValue) && enable.getBool();
                out.setValue(counter);
                ovf.setBool(o);
            }));
    }

}
