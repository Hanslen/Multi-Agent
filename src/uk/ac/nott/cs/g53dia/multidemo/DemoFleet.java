package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.*;

public class DemoFleet extends Fleet {

    /** 
     * Number of tankers in the fleet
     */
    private static int FLEET_SIZE = 1;
    public static ArrayList<ActionHelper> availableStation = new ArrayList<ActionHelper>();
	public static ArrayList<ActionHelper> availableFuelPump = new ArrayList<ActionHelper>();
	public static ArrayList<ActionHelper> availableWell = new ArrayList<ActionHelper>();
	public static ArrayList<MoveStationHelper> otherTankMoveStation = new ArrayList<MoveStationHelper>();
	public static ArrayList<ScoreHelper> allScore = new ArrayList<ScoreHelper>();
	public static class ActionHelper{
		Point pos;
		int relativeX;
		int relativeY;
	};
	public static class MoveStationHelper{
		int tankId;
		Point pos;
	}
	public static class ScoreHelper{
		int tankId;
		int score;
		Point pos;
		int x;
		int y;
	}
    public DemoFleet() {
    	
	// Create the tankers
		for (int i=0; i<FLEET_SIZE; i++) {
		    this.add(new DemoTanker());
		}
		availableStation = new ArrayList<ActionHelper>();
		availableFuelPump = new ArrayList<ActionHelper>();
		availableWell = new ArrayList<ActionHelper>();
		otherTankMoveStation = new ArrayList<MoveStationHelper>();
		
		for(int i = 0; i < FLEET_SIZE; i++){
			MoveStationHelper temp = new MoveStationHelper();
			temp.tankId = i;
			temp.pos = null;
			otherTankMoveStation.add(temp);
		}
		
		for(int i = 0; i < FLEET_SIZE; i++){
			ScoreHelper temp = new ScoreHelper();
			temp.tankId = i;
			temp.score = 0;
			temp.pos = null;
			allScore.add(temp);
		}
		
/**		Set Tank id to avoid moving towards the same direction, but there are only eight directions, so
		when there are more than eight tanks, there will be some tank move to the same direction.
**/
		for(int i = 0; i < FLEET_SIZE; i++){
	    	DemoTanker tempTank = (DemoTanker)this.get(i);
	    	tempTank.setTankId(i);
	    }
    }
}
