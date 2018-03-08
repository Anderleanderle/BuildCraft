package buildcraft.lib.blockpos;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BlockPosRotator {
	
	/**
	 * Shouldn't be instantiated.
	 * @throws InstantiationException If this class is instantiated.
	 */
	private BlockPosRotator() throws InstantiationException {
		throw new InstantiationException("BlockPosRotator shouldn't be instanciated.");
	}
	
	/**
	 * Acts like 1.11's {@code BlockPos.rotate(Rotation)}
	 * 
	 * @param  blockPos The BlockPos object to rotate
	 * @param  rotation The rotation data
	 * @return          The rotated blockPos
	 */
	public static BlockPos rotate(BlockPos blockPos, Rotation rotation) {
        switch (rotation)
        {
            case NONE:
            default:
                return blockPos;
            case CLOCKWISE_90:
                return new BlockPos(-blockPos.getZ(), blockPos.getY(), blockPos.getX());
            case CLOCKWISE_180:
                return new BlockPos(-blockPos.getX(), blockPos.getY(), -blockPos.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPos(blockPos.getZ(), blockPos.getY(), -blockPos.getX());
        }
	}
}
