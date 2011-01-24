/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsim;

import cz.zcu.fav.kiv.jsim.*;

/**
 *
 * @author dombesz
 */
public class ClientProcess extends JSimProcess {

    private JSimHead[] queue;
    private double lambda;
    private JSimProcess[] teller;
    private int nr;
    /**
     * It is better to throw the exceptions from the constructor than to catch them.
     */
    public ClientProcess(String         name,       //Name of process
                         JSimSimulation simulation, //The simulation where belongs to
                         JSimHead[]     q,          //The queue array
                         JSimProcess[]  t,          //The teller array
                         double         l)          //Random variable parameter
                         
            throws JSimSimulationAlreadyTerminatedException,
            JSimInvalidParametersException,
            JSimTooManyProcessesException {
        
        super(name, simulation);
        if (q == null) {
            throw new JSimInvalidParametersException("ProducerProcess.ProducerProcess(): q");
        }
        queue  = q;
        lambda = l;
        teller = t;
    } // constructor

    /**
     * We don't care about stopping &ndash; we run infinitely.
     */

    protected void life(){
        try {
            JSimLink link;
            double time;
            int i = 0;
            String ClientName;
            // The clients will arrive from 9am to 5pm
            while (myParent.getCurrentTime()<=480) {
                i++;
                time = myParent.getCurrentTime();
                ClientName = new String("Client #" + i);
                message(time + " -- " + getName() + ": " + ClientName + " arrived.");

                // We create a new item and insert it into the queue.
                link = new JSimLink(ClientName);
               
                nr=getShortestPath(queue); //Gets the shortest queue number
                link.into(queue[nr]);      //The client joins to the queue with the shortest 
                //message("From "+getName()+": Teller #"+nr+" is "+teller[nr].getProcessState().toString()+" Idle status="+teller[nr].isIdle());
                hold(JSimSystem.negExp(lambda));
 
           }
            message("The clock is 5PM, the bank will close. Only clients in the queues will be served.");
            // while
        } // try
        catch (JSimException e) {
            e.printStackTrace();
            e.printComment();
        } // catch
    } // life

    private int getShortestPath(JSimHead[] queue) throws JSimSecurityException {
        int shortest = 0;
        int i=0;
        boolean got=false;
        //Checking for the shortest queue or idle teller and activating teller if necesary
       
        for(i=0;(i<queue.length)&&(!got);i++){
        if(queue[shortest].cardinal()>queue[i].cardinal()){shortest=i;}
        if(teller[i].isIdle()){shortest=i; teller[i].activateNow();got=true;}
        }
        /*if(teller[shortest].isIdle() )
                    teller[shortest].activateNow();*/
        message("and goes to the  queue #"+shortest+".");
        return shortest;
    }
}
