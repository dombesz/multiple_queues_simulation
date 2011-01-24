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
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import cz.zcu.fav.kiv.jsim.JSimSimulationAlreadyTerminatedException;
import cz.zcu.fav.kiv.jsim.JSimSystem;
import cz.zcu.fav.kiv.jsim.JSimTooManyProcessesException;

/**
 * Periodicly gets data from the queue.
 */
public class ClientJockeyProcess extends JSimProcess
{
	private JSimHead[] queue;
    private JSimProcess[] teller;
    private boolean got=false;
    private int shortest=0;
    private int longest=0;
    public int nr_of_jockeys=0;

	/**
	 * It is better to throw the exceptions from the constructor than to catch them.
	 */
	public ClientJockeyProcess(String name, JSimSimulation simulation, JSimHead[] q, JSimProcess[] t) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
	{
		super(name, simulation);
		if (q == null)
			throw new JSimInvalidParametersException("ConsumerProcess.ConsumerProcess(): q");
		queue = q;
        teller= t;
       
	} // constructor

	/**
	 * We don't care about stopping &ndash; we run infinitely.
	 */
	protected void life()
	{
		try
		{
			JSimLink link;
			// Yes, it's really a neverending cycle
			while (true)
			{   
                //Finding the longest queue
                for(int i=0;i<queue.length;i++)
                    if(queue[longest].cardinal()<queue[i].cardinal()){longest=i;}
                //Finding the shortest path
				for(int i=0;(i<queue.length)&&(!got);i++){
                     if(queue[shortest].cardinal()>queue[i].cardinal()){shortest=i;}
                     if(teller[i].isIdle()){shortest=i; got=true;}

                }

                //Determining the jockeying
                //message("itt nincs shortest is ["+shortest+"]="+queue[shortest].cardinal()+" longest["+longest+"]="+queue[longest].cardinal());
                if(queue[longest].cardinal() > queue[shortest].cardinal()+1){
                message("Old Queues={ 0=["+queue[0].cardinal()+"]\n"+
                        "             1=["+queue[1].cardinal()+"]\n"+
                        "             2=["+queue[2].cardinal()+"]\n"+
                        "             3=["+queue[3].cardinal()+"]\n"+
                        "             4=["+queue[4].cardinal()+"]\n"
                        );
                //Changing the last clients place
                link= queue[longest].last();
                queue[longest].last().out();
                link.into(queue[shortest]);
                //!! Now the client is in the "shortest queue"
                message("Client "+queue[shortest].getLastData().toString()
                        +" from "+queue[longest].getHeadName()+
                        " jockeyed to "+queue[shortest].getHeadName());
                message("Old "+queue[longest].getLastData()+" != "+queue[shortest].getLastData());
                message("Queues={ 0=["+queue[0].cardinal()+"]\n"+
                        "         1=["+queue[1].cardinal()+"]\n"+
                        "         2=["+queue[2].cardinal()+"]\n"+
                        "         3=["+queue[3].cardinal()+"]\n"+
                        "         4=["+queue[4].cardinal()+"]\n"
                        );
                nr_of_jockeys++;
                
                }
                if(teller[shortest].isIdle())teller[shortest].activateNow();
                
                passivate();
               
			} // while
		} // try
		catch (JSimException e)
		{
			e.printStackTrace();
			e.printComment();
		} // catch
	} // life

} // class TellerProcess

