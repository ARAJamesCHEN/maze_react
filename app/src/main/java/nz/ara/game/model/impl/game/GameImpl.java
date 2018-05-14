/**
 * This file is created by Yang(James) CHEN.
 */
package nz.ara.game.model.impl.game;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nz.ara.game.bean.maze.MazeBean;
import nz.ara.game.em.constvalue.Const;
import nz.ara.game.logger.MyLogger;
import nz.ara.game.model.em.direction.Direction;
import nz.ara.game.model.impl.loadable.LoadableImpl;
import nz.ara.game.model.impl.point.PointImpl;
import nz.ara.game.model.impl.saveable.SaveableImpl;
import nz.ara.game.model.impl.saver.SaverImpl;
import nz.ara.game.model.in.game.Game;
import nz.ara.game.model.in.point.Point;
import nz.ara.game.model.in.saver.Saver;
import nz.ara.game.model.role.minotaur.Minotaur;
import nz.ara.game.model.role.theseus.Theseus;
import nz.ara.game.util.tools.UtilTools;

/**
 * @author yac0105
 *
 */
public class GameImpl implements Game {
	
	Logger logger = new MyLogger().getLogger(GameImpl.class);
	
	private int level = -1;
	
	private String filePath = Const.FILE_PATH.getValue();
	
	private int stepWidth = 1;
	
	private int stepHeight = 1;
	
	private LoadableImpl loadable;
	
	private SaveableImpl saveable;
	
	private Theseus theseus = new Theseus();
	
	private Minotaur minotaur = new Minotaur();
	
	private MazeBean mazeBean;
	
	/**
	 * 0: play 1:killed 2: exit
	 */
	private Const status = Const.STATUS_PLAY;
	//the
	private int moveCount = 0;
	
	private int minMoveCount = 0;
	
	public GameImpl(){
		this.level = 1;
		this.setUp(null);
	}
	
	public GameImpl(int level) {
		this.level = level;
		this.setUp(null);
	}
	
	public GameImpl(int level, Const loadType) {
		this.level = level;
		this.setUp(loadType);
	}
	
	private String setUp(Const loadType) {
		
		this.status = Const.STATUS_PLAY;
		
		this.moveCount = 0;
		
		this.minMoveCount = 0;
		
		if(loadType == null) {
			if(!this.loadGameByFile(this.level)) {
				logger.debug("Load from string");
				
				this.loadGameByString(this.level);
			}
		}else if(loadType.equals(Const.LOAD_BY_FILE)) {
			this.loadGameByFile(this.level);
		}else if(loadType.equals(Const.LOAD_BY_STR)) {
			this.loadGameByString(this.level);
		}else {
			logger.error("no match load style");
		}
		
		try {
			//to keep the loadable side and game side independent 
			//as the game msg may change time by time
			this.mazeBean = (MazeBean) UtilTools.copyObj(loadable.getMazeBean());
		} catch (Exception e) {
			logger.error("has error when copy from loadable mazebean",e);
		}
		
		this.stepWidth = loadable.getStepWidth();
		this.stepHeight = loadable.getStepHeight();
		
		theseus.setPosition(this.mazeBean.getTheStartPoint());
		theseus.setStartPosition(this.mazeBean.getTheStartPoint());
		theseus.setHasWon(false);
		
		minotaur.setPosition(this.mazeBean.getMinStartPoint());
		minotaur.setStartPosition(this.mazeBean.getMinStartPoint());
		minotaur.setHasEaten(false);
		minotaur.setCanNotMove(0);
		
		return "sucess";
	}
	
	/**
	 * 1. Load a level from a file
	 * @param theLevel
	 * @return
	 */
	public boolean loadGameByFile(int theLevel) {
		
		this.moveCount = 0;
		
		boolean isLoaded = false;
		
		loadable = new LoadableImpl(theLevel, this.filePath);
		try {
			loadable.loadByFile();
			isLoaded = true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		
		return isLoaded;
	}
	
	/**
	 * 21. load by string
	 * @param theLevel
	 * @param string
	 */
	public void loadGameByString(int theLevel) {
		this.moveCount = 0;
		loadable = new LoadableImpl(theLevel, this.filePath);
		
		String levelString = this.getLevelStringByConst(this.level);
		logger.debug("Load from string: " + levelString);
		loadable.loadByString(levelString);
	}
	
	/**
	 * 2. Save a level to a file
	 */
	public boolean save() {
		
		boolean isSucess = true;
		
		try {
			saveable = new SaveableImpl(this);
			
			Saver saver = new SaverImpl(this.level);
			
			saver.save(saveable);
		} catch (Exception e) {
			isSucess = false;
			logger.error(e);
		}
		
		return isSucess;
	}
	

	/**
	 * 22. reset or go to another level
	 * @param aNewLevel
	 */
	public void reLoad(int aNewLevel) {
		if(aNewLevel<1) {
			this.level = 1;
		}
		
		this.level = aNewLevel;
		
		this.setUp(null);
	}
	
	
	/* (non-Javadoc)
	 * one step, loop twice for each move (one the step, two min steps)
	 * @see nz.ara.game.model.in.game.Game#moveMinotaur()
	 */
	@Override
	public void moveMinotaur() {
		//18. Stops after kills
		//19. Stops after exits
		if(!this.status.equals(Const.STATUS_PLAY)) {
			logger.error("moveMinotaur error status:" + this.status);
			return;
		}
		
		this.minMoveCount = 0;
		this.minotaur.setCanNotMove(0);
		
		boolean hasFinish = false;
		
		while(!hasFinish) {
			
			Point thePosition = this.theseus.getPosition();
			
			Point minPoint = this.minotaur.getPosition();
			
			if(this.moveMinotaurLogic(thePosition, minPoint)) {
				this.minMoveCount++;
			}else {
				int canNotMove = this.minotaur.getCanNotMove();
				canNotMove++;
				this.minotaur.setCanNotMove(canNotMove);
			}
			
			if((this.minMoveCount + this.minotaur.getCanNotMove())>=2) {
				hasFinish = true;
				this.minMoveCount = 0;
				this.minotaur.setCanNotMove(0);
			}
		}
		
	}
	
	
	public boolean moveMinotaurLogic(Point thePosition, Point minPoint) {
		if(this.moveMinotaurHorizontally(thePosition,minPoint)) {
			return true;
		}
		
		if(this.moveMinotaurVertically(thePosition, minPoint)) {
			return true;
		}
		
		return false;
	}
	
	public boolean moveMinotaurHorizontally(Point thePosition, Point minPoint) {
		
        Point nextPosition = null;
		
		int nextAcross = -1;
		
		int nextDepth = -1;
		
        if(minPoint.across() > thePosition.across()) {
			
			//move left
			//16. No Minotaur moving through a wall – loses move
			if(this.checkCanMoveLeftOrRight(minPoint)) {
				//9. Minotaur moves LEFT
				nextAcross = minPoint.across() + Direction.LEFT.getAcross();
				nextDepth = minPoint.down() + Direction.LEFT.getDown();
				nextPosition = new PointImpl(nextAcross, nextDepth);
				//20. No Minotaur onto exit
				if(!this.cheackExit(nextPosition)) {
					this.minotaur.setPosition(nextPosition);
				}else {
					this.status = Const.STATUS_ERROR;
				}
				
				if(this.checkEaten()) {
					logger.debug("Killed!");
		    		this.minotaur.setHasEaten(true);
		    		this.status = Const.STATUS_EATEN;
				}
				
				if(!this.cheackExit(nextPosition)) {
					return true;
				}
				
			}
			
		}else if(minPoint.across() < thePosition.across()){
			
			//10. Minotaur moves RIGHT
			nextAcross = minPoint.across() + Direction.RIGHT.getAcross();
			nextDepth = minPoint.down() + Direction.RIGHT.getDown();
			
			nextPosition = new PointImpl(nextAcross, nextDepth);
			
			//move right
			if(this.checkCanMoveLeftOrRight(nextPosition)) {
				if(!this.cheackExit(nextPosition)) {
					this.minotaur.setPosition(nextPosition);
				}else {
					this.status = Const.STATUS_ERROR;
				}
				
				//12. Minotaur kills Theseus
				if(this.checkEaten()) {
					logger.debug("Killed!");
		    		this.minotaur.setHasEaten(true);
		    		this.status = Const.STATUS_EATEN;
				}
				
				if(!this.cheackExit(nextPosition)) {
					return true;
				}
				
			}
			
		}
        
        return false;
		
	}
	
	
	public boolean moveMinotaurVertically(Point thePosition, Point minPoint) {
		
        Point nextPosition = null;
		
		int nextAcross = -1;
		
		int nextDepth = -1;
		
		if(minPoint.down() > thePosition.down()) {
			//move up
			if(this.checkCanMoveUpOrDown(minPoint)) {
				//8. Minotaur moves UP
				nextAcross = minPoint.across() + Direction.UP.getAcross();
				nextDepth = minPoint.down() + Direction.UP.getDown();
				nextPosition = new PointImpl(nextAcross, nextDepth);
				if(!this.cheackExit(nextPosition)) {
					this.minotaur.setPosition(nextPosition);
				}else {
					this.status = Const.STATUS_ERROR;
				}
				
				if(this.checkEaten()) {
					logger.debug("Killed!");
		    		this.minotaur.setHasEaten(true);
		    		this.status = Const.STATUS_EATEN;
				}
				
				if(!this.cheackExit(nextPosition)) {
					return true;
				}
			}
		}else if(minPoint.down() < thePosition.down()) {
			//move down
			//11. Minotaur moves DOWN
			nextAcross = minPoint.across() + Direction.DOWN.getAcross();
			nextDepth = minPoint.down() + Direction.DOWN.getDown();
			
			nextPosition = new PointImpl(nextAcross, nextDepth);
			if(this.checkCanMoveUpOrDown(nextPosition)) {
				
				if(!this.cheackExit(nextPosition)) {
					this.minotaur.setPosition(nextPosition);
				}else {
					this.status = Const.STATUS_ERROR;
				}
				
				if(this.checkEaten()) {
					logger.debug("Killed!");
		    		this.minotaur.setHasEaten(true);
		    		this.status = Const.STATUS_EATEN;
				}
				
				if(!this.cheackExit(nextPosition)) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean checkEaten() {
		if(this.minotaur.getPosition().across() == this.theseus.getPosition().across() &&
				this.minotaur.getPosition().down() == this.theseus.getPosition().down()) {
			return true;
		}
		
		return false;
	}

	@Override
	public void moveTheseus(Direction direction) {
		//18. Stops after kills
		//19. Stops after exits
		if(!this.status.equals(Const.STATUS_PLAY)) {
			logger.error("moveTheseus error status:" + this.status);
			return;
		}
		
		Point currentPoint = this.theseus.getPosition();
		
		Point position = null;
		
		int nextAcross = -1;
		
		int nextDepth = -1;
		
		
        if(direction.equals(Direction.PAUSE)) {
        	//7. Theseus pauses
        	logger.debug(Direction.PAUSE);
			
        	//this.moveMinotaur();
			
		}else if(direction.equals(Direction.UP)) {
			//3. Theseus moves UP
			logger.debug(Direction.UP);
			//17. No Theseus moving through a wall – not a valid move
			if(this.checkCanMoveUpOrDown(currentPoint)) {
				nextAcross = currentPoint.across() + Direction.UP.getAcross()*this.stepHeight;
				nextDepth = currentPoint.down() + Direction.UP.getDown()*this.stepHeight;
				
				position = new PointImpl(nextAcross,nextDepth);
				
				this.theseus.setPosition(position);
				//14. Counts moves
				this.moveCount++;
				
				//this.moveMinotaur();
				//13. Theseus wins/escapes
				if(this.cheackExit(position)) {
					logger.debug("Win!");
		    		this.theseus.setHasWon(true);
		    		this.status = Const.STATUS_WIN;
				}
			}
			
		}else if(direction.equals(Direction.DOWN)) {
			//6. Theseus moves DOWN
			logger.debug(Direction.DOWN);
		    nextAcross = currentPoint.across() + Direction.DOWN.getAcross()*this.stepHeight;
			nextDepth = currentPoint.down() + Direction.DOWN.getDown()*this.stepHeight;
			
			position = new PointImpl(nextAcross,nextDepth);
			
			if(this.checkCanMoveUpOrDown(position)) {
				
                this.theseus.setPosition(position);
                this.moveCount++;
				//this.moveMinotaur();
				
				if(this.cheackExit(position)) {
					logger.debug("Win!");
		    		this.theseus.setHasWon(true);
		    		this.status = Const.STATUS_WIN;
				}
				
			}
			
		}else if(direction.equals(Direction.LEFT)) {
			//4. Theseus moves LEFT
			logger.debug(Direction.LEFT);
			if(this.checkCanMoveLeftOrRight(currentPoint)) {
				nextAcross = currentPoint.across() + Direction.LEFT.getAcross()*this.stepWidth;
				nextDepth = currentPoint.down() + Direction.LEFT.getDown()*this.stepWidth;
				
				position = new PointImpl(nextAcross,nextDepth);
				
                this.theseus.setPosition(position);
                this.moveCount++;
				//this.moveMinotaur();
				
				if(this.cheackExit(position)) {
					logger.debug("Win!");
		    		this.theseus.setHasWon(true);
		    		this.status = Const.STATUS_WIN;
				}
			}
			
		}else if(direction.equals(Direction.RIGHT)) {
			//5. Theseus moves RIGHT
			logger.debug(Direction.RIGHT);
			nextAcross = currentPoint.across() + Direction.RIGHT.getAcross()*this.stepWidth;
			nextDepth = currentPoint.down() + Direction.RIGHT.getDown()*this.stepWidth;
			
			position = new PointImpl(nextAcross,nextDepth);
			
			if(this.checkCanMoveLeftOrRight(position)) {
                this.theseus.setPosition(position);
                this.moveCount++;
				//this.moveMinotaur();
				
				if(this.cheackExit(position)) {
					logger.debug("Win!");
		    		this.theseus.setHasWon(true);
		    		this.status = Const.STATUS_WIN;
				}
			}
			
		}else {
			logger.error("cannot match your direction!");
		}
		
		
	}
	
	public boolean checkCanMoveUpOrDown(Point tp) {
		
		if(tp.down() <0) {
			return false;
		}
		
		List<Point> aboveWallList = this.getMazeBean().getWallAbovePointList();
		
		Iterator<Point> iter = aboveWallList.iterator();
		
		while(iter.hasNext()) {
			Point p = iter.next();
			
			if(tp.across()==p.across() && tp.down()==p.down()) {
				logger.debug("Wall! Cannot move:" + tp.across() + "," + tp.down());
				return false;
			}
			
		}
		
		return true;
	}
	
    public boolean checkCanMoveLeftOrRight(Point tp) {
    	
    	if(tp.across()<0) {
    		return false;
    	}
		
    	List<Point> leftWallPointList = this.getMazeBean().getWallLeftPointList();
		
    	Iterator<Point> iter = leftWallPointList.iterator();
    	
		while(iter.hasNext()) {
			Point p = iter.next();
			
			if(tp.across()==p.across() && tp.down()==p.down()) {
				logger.debug("Wall! Cannot move:" + tp.across() + "," + tp.down());
				return false;
			}
			
		}
		
		return true;
	}
	
    public boolean cheackExit(Point tp) {
    	
    	Point p = this.mazeBean.getExitPoint();
    	
    	if(tp.across() == p.across() && tp.down() == p.down()) {
    		return true;
    	}
    	
    	return false;
    }
    
    
    public String getLevelStringByConst(int level) {
    	switch(level) {
    	    case 1: 
    	    	return Const.LEVEL_ONE.getValue();
    	    case 2: 
    	    	return Const.LEVEL_TWO.getValue();
    	    case 3: 
    	    	return Const.LEVEL_THREE.getValue();
    	    case 4:
    	    	return Const.LEVEL_FOUR.getValue();
    	    case 5:
    	    	return Const.LEVEL_FIVE.getValue();
    	    case 6:
    	    	return Const.LEVEL_SIX.getValue();
    	    case 7:
    	    	return Const.LEVEL_SEVEN.getValue();
    	    case 8:
    	    	return Const.LEVEL_EIGHT.getValue();
    	    case 9:
    	    	return Const.LEVEL_NINE.getValue();
    	    case 10:
    	    	return Const.LEVEL_TEN.getValue();
    	    default: 
    	    	return Const.LEVEL_ONE.getValue();
    	}
    }

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		
		this.level = level;
		
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LoadableImpl getLoadable() {
		return loadable;
	}

	public void setLoadable(LoadableImpl loadable) {
		this.loadable = loadable;
	}

	public Theseus getTheseus() {
		return theseus;
	}

	public void setTheseus(Theseus theseus) {
		this.theseus = theseus;
	}

	public Minotaur getMinotaur() {
		return minotaur;
	}

	public void setMinotaur(Minotaur minotaur) {
		this.minotaur = minotaur;
	}

	public MazeBean getMazeBean() {
		return mazeBean;
	}

	public void setMazeBean(MazeBean mazeBean) {
		this.mazeBean = mazeBean;
	}

	public int getStepWidth() {
		return stepWidth;
	}

	public void setStepWidth(int stepWidth) {
		this.stepWidth = stepWidth;
	}

	public int getStepHeight() {
		return stepHeight;
	}

	public void setStepHeight(int stepHeight) {
		this.stepHeight = stepHeight;
	}

	public SaveableImpl getSaveable() {
		return saveable;
	}

	public void setSaveable(SaveableImpl saveable) {
		this.saveable = saveable;
	}

	public Const getStatus() {
		return status;
	}

	public void setStatus(Const status) {
		this.status = status;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

}
