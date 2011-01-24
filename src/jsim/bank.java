/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsim;

import cz.zcu.fav.kiv.jsim.JSimHead;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import cz.zcu.fav.kiv.jsim.JSimException;

/**
 *
 * @author dombesz
 */
public class bank {

    public static void main(String[] args) {
        JSimSimulation sim = null;
        JSimHead[] queue = new JSimHead[5];
        ClientProcess client = null;
        ClientJockeyProcess jockey=null;
        TellerProcess[] teller = new TellerProcess[5];
        int i;

        try {
            // It is necessary to create a simulation before creating a queue -- the queue needs simulation's time functions.
            sim = new JSimSimulation("Producer-Consumer Simulation");

            // Let's create the processes.
            
            
           
            //Initiating the 5 TellerProcesses and 5 Queue

            //Init queue
            for (i = 0; i < 5; i++){
                queue[i]  = new JSimHead(" Queue #" + i, sim);
                teller[i] = new TellerProcess("Teller #" + i, sim, queue[i],1/4.5);
            }

            jockey=new ClientJockeyProcess("Jockey", sim, queue, teller);

            for (i=0;i<5;i++)teller[i].setJockey(jockey);
            
            
            client=new ClientProcess("ClientGen", sim, queue,teller, 0.7);
            
            client.activate(1.0);
            //jockey.activate(1.0);

            // Now, we will do steps until all clients will be served
            do{}while(sim.step() == true);
           
            sim.message("Mean waiting times for the queues \n"+
                         "Queue[0] = "+ queue[0].getTwForAllLinks()+"\n"+
                         "Queue[1] = "+ queue[1].getTwForAllLinks()+"\n"+
                         "Queue[2] = "+ queue[2].getTwForAllLinks()+"\n"+
                         "Queue[3] = "+ queue[3].getTwForAllLinks()+"\n"+
                         "Queue[4] = "+ queue[4].getTwForAllLinks()+"\n");
            sim.message("# of jockeys:"+jockey.nr_of_jockeys);
            sim.message("Bye!");
        } // try
        catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        } // We must not forget to shut the simulation down!
        finally {
            sim.shutdown();
        }
    }
}// main
