//InputCountGate.java
//Author: Benjamin Alston
//Section: CS:2820:0A01

/** Any kind of gate that needs to track its input count
 *  @author Douglas Jones
 *  @author Ben Alston
 *  @version 2019-04-11
 *  @see Gate
 *  @see XorGate
 *  @see ThresholdGate
 */
public abstract class InputCountGate extends Gate {

    private int inputCount = 0; // number of inputs that are currently one
    private int oldOutput = 0;  // the previous output of this gate
    protected float delay = Float.NaN; // default value allows this in err msgs

    private boolean pendingOutputEvent = false;

    /** constructor, used only from within subclasses
     *  @param n the name of the new gate
     *  this is a pass-through to Gate
     */
    protected InputCountGate(String n) {
	super(n);
    }

    /** tell the gate that one of its inputs has changed
     *  @param now the time at which the change occurs
     *  @param value the new value of that input
     *  each subclass must implement this method
     */
    public void inputChange(float now, int value) {
	if (value == 1) { // it change to 1
	    inputCount = inputCount + 1;
	} else { // it changed to 0
	    inputCount = inputCount - 1;
	}
	int newOutput = logicRule( inputCount );
	if (newOutput != oldOutput) { // the output changes
            if (!pendingOutputEvent) {
                Gate gateHandle=this;//make a handle for this object
                Simulation.schedule(
                    new Simulation.Event( now+delay ) {
                        void trigger() {
                            gateHandle.outputChange(time,newOutput);
                        }
                    }
                );
                pendingOutputEvent= true;
            }
	    oldOutput = newOutput;
        }
    }
    protected void outputChange(float now, int value) { //consider private?
        //if value and oldValue are unequal do nothing
        if (value==oldOutput) {
            super.outputChange(now,value);
        }
        pendingOutputEvent=false;
    }

    /** compute the gate's logical value
     *  @param count the number of ones on the input
     *  each subclass must implement this method
     */
    public abstract int logicRule(int count);
}
