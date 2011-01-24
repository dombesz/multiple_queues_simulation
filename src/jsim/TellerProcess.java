/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsim;

/**
 *
 * @author dombesz
 */
import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimHead;
import cz.zcu.fav.kiv.jsim.JSimInvalidParametersException;
import cz.zcu.fav.kiv.jsim.JSimLink;
import cz.zcu.fav.kiv.jsim.JSimProcess;
import cz.zcu.fav.kiv.jsim.JSimProcessState;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import cz.zcu.fav.kiv.jsim.JSimSimulationAlreadyTerminatedException;
import cz.zcu.fav.kiv.jsim.JSimSystem;
import cz.zcu.fav.kiv.jsim.JSimTooManyProcessesException;

/**
 * Periodicly gets data from the queue.
 */
public class TellerProcess extends JSimProcess {

    private JSimHead queue;
    private ClientJockeyProcess jockey;
    private double lambda;
  

    /**
     * It is better to throw the exceptions from the constructor than to catch them.
     */
    public TellerProcess(String name, JSimSimulation simulation, JSimHead q, double l)
            throws JSimSimulationAlreadyTerminatedException,
            JSimInvalidParametersException,
            JSimTooManyProcessesException {
        super(name, simulation);
        if (q == null) {
            throw new JSimInvalidParametersException("ConsumerProcess.ConsumerProcess(): q");
        }
        queue = q;
        lambda = l;
        
    } // constructor
    public void setJockey(ClientJockeyProcess j){
    jockey=j;
    }
    /**
     * We don't care about stopping &ndash; we run infinitely.
     */
    protected void life() {
        try {
            JSimLink link;
            double time;

            // Yes, it's really a neverending cycle
            while (true) {
                time = myParent.getCurrentTime();
                // If the queue is empty, we don't do anything.
                if (queue.empty()) {
                    message(time + " -- " + getName() + ": queue empty. ");
                } else {
                    // We must pick up the first item and take it out from the queue.
                    
                    link = queue.first();
                    link.out();
                    
                    message(time + " -- " + getProcessName() + ": Started serving " + link.getData());
                    if(jockey.isIdle())jockey.activateNow();
                    hold(JSimSystem.negExp(lambda));
                    message(myParent.getCurrentTime() + " -- " + getProcessName() + ": Finished serving " + link.getData());
                    
                } // else
                passivate();
            } // while
        } // try
        catch (JSimException e) {
            e.printStackTrace();
            e.printComment();
        } // catch
    } // life
} // class ConsumerProcess
