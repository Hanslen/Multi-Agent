package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;

/**
 * A simple example Tanker
 * 
 * @author Julian Zappala
 */
/*
 * 
 * Copyright (c) 2011 Julian Zappala
 * 
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class DemoTanker extends Tanker {
	public class PointWithXY{
		int x;
		int y;
		Point point;
	}
	private PointWithXY moveToAnotherTank = null;
	private int tankId = -1;							//tankId for differentiating each tank
	private PointWithXY tank = new PointWithXY();
	private int directionHelper = 1;  					// 1 for North, 2 for West, 3 for South, 4 for East
	private PointWithXY nearestWell = new PointWithXY();
	private PointWithXY nearestFuelPump = new PointWithXY();

	private class ActionHelper{
		Action moveAction;
		int relativeX;
		int relativeY;
	};
    public DemoTanker() {
    	tank.x = 0;
    	tank.y = 0;
    	nearestFuelPump.x = 0;
    	nearestFuelPump.y = 0;
    	nearestFuelPump.point = FUEL_PUMP_LOCATION;
    	nearestWell.x = Integer.MAX_VALUE;
    	nearestWell.y = Integer.MAX_VALUE;
    	nearestWell.point = null;
    }
    /*
     * The following is a very simple demonstration of how to write a tanker. The
     * code below is very stupid and pretty much randomly picks actions to perform. 
     */
    public Action senseAndAct(Cell[][] view, long timestep) {
    	tank.point = getCurrentCell(view).getPoint();
    	if(moveToAnotherTank != null){
    		if(moveToAnotherTank.point.equals(tank.point)){
    			moveToAnotherTank = null;
    		}
    	}
    	balanceEnvironment(timestep);
    	storeAvailableStation(view);
    	findJob(view,FuelPump.class,26);
    	findJob(view, Well.class, 26);
    	int distanceToFuelPump = calculateDistance(tank.x,tank.y,nearestFuelPump.x,nearestFuelPump.y);
    	for(DemoFleet.ActionHelper fuelPump : DemoFleet.availableFuelPump){
    		int tempDis = calculateDistance(tank.x,tank.y,fuelPump.relativeX,fuelPump.relativeY);
    		if(tempDis < distanceToFuelPump){
    			distanceToFuelPump = tempDis;
    			nearestFuelPump.point = fuelPump.pos;
    			nearestFuelPump.x = fuelPump.relativeX;
    			nearestFuelPump.y = fuelPump.relativeY;
    		}
    	}
    	if((getFuelLevel()-1 <= distanceToFuelPump)&& !(getCurrentCell(view) instanceof FuelPump)){
    		updateTankPosByconstant(nearestFuelPump.x,nearestFuelPump.y);
    		return new MoveTowardsAction(nearestFuelPump.point);
    	}
        else {
        	// refuel, (at the FuelPump)
        	if(getCurrentCell(view) instanceof FuelPump && getFuelLevel() != MAX_FUEL){
        		if(timestep > 25){
	        		int temp = directionHelper;
	        		directionHelper = (int)(Math.random() * 4) + 1;
	        		while(directionHelper == temp && directionHelper != temp-2 && directionHelper != temp+2){
	        			directionHelper = (int)(Math.random() * 4)+1;
	        		}
        		}
        		return new RefuelAction();
        	}
        	
        	//dispose waste, (at Well)
        	if((getCurrentCell(view) instanceof Well && getWasteLevel() != 0)){
        		return new DisposeWasteAction();
        	}
        	
        	//When the waste tank is full, it needs to find the well, (go to well)
        	if(getWasteCapacity() == 0){
        		if(nearestWell.point != null){
        			int distanceFromWell = calculateDistance(tank.x,tank.y, nearestWell.x,nearestWell.y);
        			int fuelDistance = calculateDistance(nearestWell.x,nearestWell.y,nearestFuelPump.x,nearestFuelPump.y);
        			for(DemoFleet.ActionHelper fuelPump : DemoFleet.availableFuelPump){
        	    		int tempDis = calculateDistance(nearestWell.x,nearestWell.y,fuelPump.relativeX,fuelPump.relativeY);
        	    		if(tempDis < fuelDistance){
        	    			fuelDistance = tempDis;
        	    			nearestFuelPump.point = fuelPump.pos;
        	    			nearestFuelPump.x = fuelPump.relativeX;
        	    			nearestFuelPump.y = fuelPump.relativeY;
        	    		}
        	    	}
//        			find the nearest fuelPump to that well
        			for(DemoFleet.ActionHelper well : DemoFleet.availableWell){
        				int tempWellDistance = calculateDistance(tank.x,tank.y,well.relativeX,well.relativeY);
        				if(tempWellDistance < distanceFromWell){
        					distanceFromWell = tempWellDistance;
        					nearestWell.point = well.pos;
        					nearestWell.x = well.relativeX;
        					nearestWell.y = well.relativeY;
        					for(DemoFleet.ActionHelper fuelPump : DemoFleet.availableFuelPump){
                	    		int tempDis = calculateDistance(nearestWell.x,nearestWell.y,fuelPump.relativeX,fuelPump.relativeY);
                	    		if(tempDis < fuelDistance){
                	    			fuelDistance = tempDis;
                	    			nearestFuelPump.point = fuelPump.pos;
                	    			nearestFuelPump.x = fuelPump.relativeX;
                	    			nearestFuelPump.y = fuelPump.relativeY;
                	    		}
                	    	}
        				}
        			}
//        			determine whether it is worth to go to that well, (Depend on fuel cost)
	        		if(getFuelLevel()-distanceFromWell-1>fuelDistance){
	        			updateTankPosByconstant(nearestWell.x,nearestWell.y);
	            		return new MoveTowardsAction(nearestWell.point);
	        		}
	        		else{
	        			for(DemoFleet.ActionHelper fuelPump : DemoFleet.availableFuelPump){
            	    		int tempDis = calculateDistance(tank.x,tank.y,fuelPump.relativeX,fuelPump.relativeY);
            	    		if(tempDis < fuelDistance){
            	    			fuelDistance = tempDis;
            	    			nearestFuelPump.point = fuelPump.pos;
            	    			nearestFuelPump.x = fuelPump.relativeX;
            	    			nearestFuelPump.y = fuelPump.relativeY;
            	    		}
            	    	}
	        			updateTankPosByconstant(nearestFuelPump.x,nearestFuelPump.y);
	        			return new MoveTowardsAction(nearestFuelPump.point);
	        		}
        		}
        	}//if the tank is not empty, it still needs to find a station to load waste
        	else{
        		//if the tank is at a Station with task, just load the waste
        		if(getCurrentCell(view) instanceof Station){
            		Task current = ((Station)getCurrentCell(view)).getTask();
            		if(current != null){
            			if(current.getWasteRemaining() - getWasteCapacity() <= 0){
//                			remove the station from availableStation
                			for(DemoFleet.ActionHelper t : DemoFleet.availableStation){
                				if(t.pos.equals(getCurrentCell(view).getPoint())){
                					DemoFleet.availableStation.remove(t);
                					break;
                				}
                			}
            			}
            			for(DemoFleet.MoveStationHelper t : DemoFleet.otherTankMoveStation){
            				if(t.tankId == tankId){
            					t.pos = null;
            				}
            			}
            			return new LoadWasteAction(current);
            		}else{
            			for(DemoFleet.ActionHelper t : DemoFleet.availableStation){
            				if(t.pos.equals(getCurrentCell(view).getPoint())){
            					DemoFleet.availableStation.remove(t);
            					break;
            				}
            			}
            		}
            	}
        		
//	        	go to memory best station
	    		if(DemoFleet.availableStation.size() != 0 && getWasteCapacity() != 0){
	    			//Ensure that the station this tank will go is not the same as other tank
	    			int repeated = 0;
	    			DemoFleet.ActionHelper bestMemoryStation = new DemoFleet.ActionHelper();
		    		bestMemoryStation = null;
		    		int distance = Integer.MAX_VALUE;
		    		for(DemoFleet.ActionHelper memoryStation:DemoFleet.availableStation){
		    			repeated = 0;
		    			for(DemoFleet.MoveStationHelper i:DemoFleet.otherTankMoveStation){
		    				if(i.pos != null && i.tankId != tankId){
			    				if(memoryStation.pos.equals(i.pos)){
			    					repeated = 1;
			    				}
		    				}
		    			}
		    			if(repeated == 0){
			    			int tempDistance = calculateDistance(tank.x,tank.y,memoryStation.relativeX,memoryStation.relativeY);
			    			if(tempDistance < distance && tempDistance != 0){
			    				bestMemoryStation = memoryStation;
			    				distance = tempDistance;
			    				
			    			}
		    			}
		    		}
		    		//determine whether it is worth to go to that station
	    			if(bestMemoryStation != null){
	    				int distanceFromFuelPump = calculateDistance(bestMemoryStation.relativeX, bestMemoryStation.relativeY, nearestFuelPump.x, nearestFuelPump.y);
	    				for(DemoFleet.ActionHelper fuelPump : DemoFleet.availableFuelPump){
	    		    		int tempDis = calculateDistance(bestMemoryStation.relativeX,bestMemoryStation.relativeY,fuelPump.relativeX,fuelPump.relativeY);
	    		    		if(tempDis < distanceFromFuelPump){
	    		    			distanceFromFuelPump = tempDis;
	    		    			nearestFuelPump.point = fuelPump.pos;
	    		    			nearestFuelPump.x = fuelPump.relativeX;
	    		    			nearestFuelPump.y = fuelPump.relativeY;
	    		    		}
	    		    	}
	    				if(getFuelLevel() >= distance+distanceFromFuelPump){
	        				updateTankPosByconstant(bestMemoryStation.relativeX,bestMemoryStation.relativeY);
	        				DemoFleet.MoveStationHelper tempmoveStation = new DemoFleet.MoveStationHelper();
	        				tempmoveStation.tankId = tankId;
	        				tempmoveStation.pos = bestMemoryStation.pos;
	        				for(DemoFleet.MoveStationHelper t:DemoFleet.otherTankMoveStation){
	        					if(t.tankId == tankId){
	        						t =tempmoveStation;
	        					}
	        				}
	        				return new MoveTowardsAction(bestMemoryStation.pos);
	    				}
	    			}
	    		}
        	}
        	//Go to the well to avoid wander randomly
        	if(getWasteLevel() != 0){
        		if(nearestWell.point != null){
        			int distanceFromWell = calculateDistance(tank.x,tank.y, nearestWell.x,nearestWell.y);
        			int fuelDistance = calculateDistance(tank.x,tank.y,nearestFuelPump.x,nearestFuelPump.y);
        			for(DemoFleet.ActionHelper well : DemoFleet.availableWell){
        				int tempWellDistance = calculateDistance(tank.x,tank.y,well.relativeX,well.relativeY);
        				if(tempWellDistance < distanceFromWell){
        					distanceFromWell = tempWellDistance;
        					nearestWell.point = well.pos;
        					nearestWell.x = well.relativeX;
        					nearestWell.y = well.relativeY;
        				}
        			}
	        		if(getFuelLevel()>distanceFromWell+fuelDistance){
	        			updateTankPosByconstant(nearestWell.x,nearestWell.y);
	            		return new MoveTowardsAction(nearestWell.point);
	        		}
	        		else{
	        			updateTankPosByconstant(nearestFuelPump.x,nearestFuelPump.y);
	        			return new MoveTowardsAction(nearestFuelPump.point);
	        		}
        		}
        	}
        	
        	//Go to the fuel Pump to avoid wander randomly
        	if(moveToAnotherTank != null){
        		updateTankPosByconstant(moveToAnotherTank.x, moveToAnotherTank.y);
        		return new MoveTowardsAction(moveToAnotherTank.point);
        	}
        	
        	if(getFuelLevel() < MAX_FUEL-12){
        		ActionHelper findFuel = findJob(view, FuelPump.class, 6);
				if(findFuel != null){
        			updateTankPos(findFuel.relativeX, findFuel.relativeY);
					return findFuel.moveAction;
				}
        	}
        	if(timestep<25){
        		updataTankPosRandom(tankId%8);
        		return new MoveAction(tankId%8);
        	}
        	int direction = (int)(Math.random() * 8);
        	int avoidDirection[] = avoidDirection(directionHelper);
        	while(direction == avoidDirection[0] || direction == avoidDirection[1] || direction == avoidDirection[2]|| direction == avoidDirection[3]|| direction == avoidDirection[4]){
        		direction = (int)(Math.random() * 8);
        	}
    		updataTankPosRandom(direction);
        	return new MoveAction(direction);
        }
    }
    public void setTankId(int id){
    	this.tankId = id;
    	
    }
    private void balanceEnvironment(long time){
    	for(DemoFleet.ScoreHelper t : DemoFleet.allScore){
    		if(t.tankId == tankId){
    			t.score = getScore();
    			t.pos = tank.point;
    			t.x = tank.x;
    			t.y = tank.y;
    		}
    	}
    	if(time%5001 == 0){
        	int max = Integer.MIN_VALUE;
        	int maxPosX = 0;
        	int maxPosY = 0;
        	Point maxPoint = null;
        	int min = Integer.MAX_VALUE;
        	int minTankId = -1;
    		for(DemoFleet.ScoreHelper scoreT:DemoFleet.allScore){
    			if(scoreT.score > max){
    				max = scoreT.score;
    				maxPosX = scoreT.x;
    				maxPosY = scoreT.y;
    				maxPoint = scoreT.pos;
    			}
    			if(scoreT.score < min){
    				min = scoreT.score;
    				minTankId = scoreT.tankId;
    			}
    		}
    		if(max-min>5000){
    			if(tankId == minTankId){
    				decideGoToAnotherTank(maxPosX, maxPosY, maxPoint);
    			}
    		}
    	}
    }

    private int[] avoidDirection(int direct){
    	switch(directionHelper){
			case 1:int[] north = {1,6,7,2,3};return north;
			case 2:int[] west = {2,4,6,0,1};return west;
			case 3:int[] south = {0,4,5,2,3};return south;
			case 4:int[] east = {3,5,7,0,1};return east;
			default:break;
    	}
    	return null;
    }
    
    private void storeAvailableStation(Cell[][] view){
    	int center = 25;
      	for(int n = 1; n < VIEW_RANGE+1; n++){
      		for(int i = -n+center; i < n+1+center; i++){
      			if(i == -n+center || i == n+center){
      				for(int j = -n+center; j < n + 1+center;j++){
      					DemoFleet.ActionHelper possibleAction = storeAvailableStationHelper(view[i][j]);
      					if(possibleAction != null){
      						possibleAction.relativeX = i-25+tank.x;
      						possibleAction.relativeY = tank.y-(j-25);
      						if(view[i][j].getClass() == Station.class){
      							DemoFleet.availableStation.add(possibleAction);
      						}
      						else if(view[i][j].getClass() == FuelPump.class){
      							DemoFleet.availableFuelPump.add(possibleAction);
      						}
      						else if(view[i][j].getClass() == Well.class){
      							DemoFleet.availableWell.add(possibleAction);
      						}
      					}
      				}
      			}
      			else{
      				for(int bool=-1; bool < 2&&bool != 0;bool++){
      					DemoFleet.ActionHelper possibleAction = storeAvailableStationHelper(view[i][n*bool+center]);
      					if(possibleAction != null){
      						possibleAction.relativeX = i-25+tank.x;
      						possibleAction.relativeY = tank.y-(n*bool+center-25);
      						if(view[i][n*bool+center].getClass() == Station.class){
      							DemoFleet.availableStation.add(possibleAction);
      						}
      						else if(view[i][n*bool+center].getClass() == FuelPump.class){
      							DemoFleet.availableFuelPump.add(possibleAction);
      						}
      						else if(view[i][n*bool+center].getClass() == Well.class){
      							DemoFleet.availableWell.add(possibleAction);
      						}
      					}
      				}
      			}
      		}
      	}
    }
    
    private DemoFleet.ActionHelper storeAvailableStationHelper(Cell currentCell){
    	for(DemoFleet.ActionHelper i:DemoFleet.availableStation){
    		if(i.pos.equals(currentCell.getPoint())){
    			return null;
    		}
    	}
    	for(DemoFleet.ActionHelper i:DemoFleet.availableFuelPump){
    		if(i.pos.equals(currentCell.getPoint())){
    			return null;
    		}
    	}
    	for(DemoFleet.ActionHelper i:DemoFleet.availableWell){
    		if(i.pos.equals(currentCell.getPoint())){
    			return null;
    		}
    	}
    	if(currentCell.getClass() == Station.class){
    		DemoFleet.ActionHelper temp = new DemoFleet.ActionHelper();
    		Station pos = (Station)currentCell;
        	temp.pos = pos.getPoint();
        	if(pos.getTask() != null){
        		if(pos.getTask().getWasteRemaining() > 0){
            		return temp;
        		}
        	}        	
    	}
    	else if(currentCell.getClass() == FuelPump.class){
    		DemoFleet.ActionHelper temp = new DemoFleet.ActionHelper();
    		FuelPump pos = (FuelPump)currentCell;
    		temp.pos = pos.getPoint();
    		return temp;
    	}
    	else if(currentCell.getClass() == Well.class){
    		DemoFleet.ActionHelper temp = new DemoFleet.ActionHelper();
    		Well pos = (Well)currentCell;
    		temp.pos = pos.getPoint();
    		return temp;
    	}
    	return null;
    }
    private int calculateDistance(int x1, int y1, int x2, int y2){
    	return Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
    }
    public void decideGoToAnotherTank(int x,int y, Point p){
    	if(calculateDistance(tank.x,tank.y,x,y) < 200){
    		moveToAnotherTank = new PointWithXY();
    	    this.moveToAnotherTank.x = x;
    	    this.moveToAnotherTank.y = y;
    	    this.moveToAnotherTank.point = p;
    	}
    }
    
//    update tank's position based on relative position
    private void updateTankPos(int x, int y){
    	if(x > 25){
    		tank.x += 1;
    	}
    	else if(x < 25){
    		tank.x -= 1;
    	}
    	if(y > 25){
    		tank.y -= 1;
    	}
    	else if(y < 25){
    		tank.y += 1;
    	}
    }
    
//    update tank's position based on two constant position
    private void updateTankPosByconstant(int x, int y){
    	if(x > tank.x){
			tank.x++;
		}
		else if(x < tank.x){
			tank.x--;
		}
		if(y > tank.y){
			tank.y++;
		}
		else if(y < tank.y){
			tank.y--;
		}
    }
    
//    update Tank's position by direction
    private void updataTankPosRandom(int direct){
    	switch (direct) {
        case MoveAction.NORTH:
            tank.y++;
            break;
        case MoveAction.SOUTH:
        	tank.y--;
            break;
        case MoveAction.EAST:
        	tank.x++;
            break;
        case MoveAction.WEST:
        	tank.x--;
            break;
        case MoveAction.NORTHEAST:
        	tank.x++; tank.y++;
            break;
        case MoveAction.NORTHWEST:
        	tank.x--; tank.y++;
            break;
        case MoveAction.SOUTHEAST:
        	tank.x++; tank.y--;
            break;
        case MoveAction.SOUTHWEST:
        	tank.x--; tank.y--;
            break;
        default:break;
    }	
    }
    
//  positionType is Well.class, FuelPump.class or Station.Class
  private <T extends DefaultCell> ActionHelper findJob(Cell[][] view, Class<T> positionType, int wide){
  	int center = 25;
  	for(int n = 1; n < wide; n++){
  		for(int i = -n+center; i < n+1+center; i++){
  			if(i == -n+center || i == n+center){
  				for(int j = -n+center; j < n + 1+center;j++){
  					ActionHelper possibleAction = findJobHelper(view[i][j], positionType, i, j);
  					if(possibleAction != null){
  						possibleAction.relativeX = i;
  						possibleAction.relativeY = j;
  						return possibleAction;
  					}
  				}
  			}
  			else{
  				for(int bool=-1; bool < 2&&bool != 0;bool++){
  					ActionHelper possibleAction = findJobHelper(view[i][n*bool+center], positionType, i, n*bool+center);
  					if(possibleAction != null){
  						possibleAction.relativeX = i;
  						possibleAction.relativeY = n*bool+center;
  						return possibleAction;
  					}
  				}
  			}
  		}
  	}
  	return null;
  }
  
  
//  n stands for fuel cost
  private <T extends DefaultCell> ActionHelper findJobHelper(Cell currentCell, Class<T> positionType,int x, int y){
	ActionHelper temp = new ActionHelper();
  	if(currentCell.getClass() == positionType){
			if(positionType == FuelPump.class){
				int tempFuelx = x-25+tank.x;
				int tempFuely = tank.y-(y-25);
				int tempDistanceFuel = calculateDistance(tank.x,tank.y,tempFuelx, tempFuely);
				int currentDistanceFuel = calculateDistance(tank.x,tank.y,nearestFuelPump.x,nearestFuelPump.y);
				if(tempDistanceFuel < currentDistanceFuel){
					nearestFuelPump.x = tempFuelx;
					nearestFuelPump.y = tempFuely;
					nearestFuelPump.point = ((FuelPump)currentCell).getPoint();
				}
				temp.moveAction = new MoveTowardsAction(nearestFuelPump.point);
				return temp;
			}
			else if(positionType == Well.class){
				int tempWellx = x-25+tank.x;
				int tempWelly = tank.y-(y-25);
				int tempDistanceWell = calculateDistance(tank.x,tank.y,tempWellx,tempWelly);
				int currentDistanceWell = calculateDistance(tank.x,tank.y,nearestWell.x,nearestWell.y);
				if(tempDistanceWell < currentDistanceWell){
					nearestWell.x = tempWellx;
					nearestWell.y = tempWelly;
					nearestWell.point = ((Well)(currentCell)).getPoint();
				}
				temp.moveAction = new MoveTowardsAction(currentCell.getPoint());
				return temp;
			}
		}
  	return null;
  }

}
