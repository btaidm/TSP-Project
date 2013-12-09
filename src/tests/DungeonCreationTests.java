package tests;

import com.tsp.game.map.Dungeon;
import org.junit.Test;

import static org.junit.Assert.*;

public class DungeonCreationTests {

	@Test
	public void test() {
		fail("Not yet implemented");

		Dungeon dungeon = new Dungeon();

		assert(sucMapGen(dungeon));
		assert(hasStairs(dungeon));
		assert(isCorrectSize(dungeon));

	}

	private boolean sucMapGen(Dungeon dungeon){
		return dungeon.getDungeon() != null;
	}

	private boolean hasStairs(Dungeon dungeon){
		for(int k = 0; k < dungeon.getFloors(); k++){
			boolean hasUp = false;
			boolean hasDown = false;
			for(int i = 0; i < dungeon.getRows(); i++){
				for(int j = 0; j < dungeon.getColumns(); j++){
					if(dungeon.getDungeon()[k][i][j] == dungeon.STAIR_DOWN){
						hasDown = true;
					}
					else if(dungeon.getDungeon()[k][i][j] == dungeon.STAIR_UP){
						hasUp = true;
					}
				}
			}
			if((!hasUp && k != dungeon.getFloors() - 1) || (!hasDown && k != 0)){
				return false;
			}
		}
		return true;
	}
	
	private boolean isCorrectSize(Dungeon dungeon){
		return (dungeon.getRows() == dungeon.getDungeon()[0].length) && (dungeon.getColumns() == dungeon.getDungeon()[0][0].length) && (dungeon.getFloors() == dungeon.getDungeon().length);
	}

}
