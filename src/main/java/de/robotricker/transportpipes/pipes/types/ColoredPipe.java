package de.robotricker.transportpipes.pipes.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.api.TransportPipesContainer;
import de.robotricker.transportpipes.pipeitems.PipeItem;
import de.robotricker.transportpipes.pipes.BlockLoc;
import de.robotricker.transportpipes.pipes.WrappedDirection;
import de.robotricker.transportpipes.pipes.PipeType;
import de.robotricker.transportpipes.pipes.colored.PipeColor;
import de.robotricker.transportpipes.pipeutils.NBTUtils;
import de.robotricker.transportpipes.pipeutils.PipeItemUtils;

public class ColoredPipe extends Pipe {

	private PipeColor pipeColor;
	private int lastOutputIndex = 0;

	public ColoredPipe(Location blockLoc, PipeColor pipeColor) {
		super(blockLoc);
		this.pipeColor = pipeColor;
	}

	@Override
	public Map<WrappedDirection, Integer> handleArrivalAtMiddle(PipeItem item, WrappedDirection before, Collection<WrappedDirection> possibleDirs) {
		Map<BlockLoc, TransportPipesContainer> containerMap = TransportPipes.instance.getContainerMap(getBlockLoc().getWorld());

		Map<WrappedDirection, Integer> maxSpaceMap = new HashMap<WrappedDirection, Integer>();
		Map<WrappedDirection, Integer> map = new HashMap<WrappedDirection, Integer>();

		//update maxSpaceMap
		for (WrappedDirection pd : WrappedDirection.values()) {
			maxSpaceMap.put(pd, Integer.MAX_VALUE);
			if (containerMap != null) {
				BlockLoc bl = BlockLoc.convertBlockLoc(getBlockLoc().clone().add(pd.getX(), pd.getY(), pd.getZ()));
				if (containerMap.containsKey(bl)) {
					TransportPipesContainer tpc = containerMap.get(bl);
					int freeSpace = tpc.howMuchSpaceForItemAsync(pd.getOpposite(), item.getItem());
					maxSpaceMap.put(pd, freeSpace);
				}
			}
		}

		for (int i = 0; i < item.getItem().getAmount(); i++) {
			WrappedDirection nextDir = getNextItemDirection(item, before, new ArrayList<>(possibleDirs), map, maxSpaceMap);
			if (nextDir != null) {
				if (map.containsKey(nextDir)) {
					map.put(nextDir, map.get(nextDir) + 1);
				} else {
					map.put(nextDir, 1);
				}
			}
		}

		return map;
	}

	private WrappedDirection getNextItemDirection(PipeItem item, WrappedDirection before, Collection<WrappedDirection> possibleDirs, Map<WrappedDirection, Integer> outputMap, Map<WrappedDirection, Integer> maxSpaceMap) {

		Iterator<WrappedDirection> it = possibleDirs.iterator();
		while (it.hasNext()) {
			WrappedDirection pd = it.next();
			if (pd.equals(before.getOpposite())) {
				it.remove();
			} else {
				int currentAmount = outputMap.containsKey(pd) ? outputMap.get(pd) : 0;
				currentAmount += getSimilarItemAmountOnDirectionWay(item, pd);
				int maxFreeSpace = maxSpaceMap.get(pd);
				if (currentAmount >= maxFreeSpace) {
					it.remove();
				}
			}
		}
		WrappedDirection[] array = possibleDirs.toArray(new WrappedDirection[0]);
		lastOutputIndex++;
		if (lastOutputIndex >= possibleDirs.size()) {
			lastOutputIndex = 0;
		}
		if (possibleDirs.size() > 0) {
			return array[lastOutputIndex];
		}
		return null;
	}

	public PipeColor getPipeColor() {
		return pipeColor;
	}

	@Override
	public int[] getBreakParticleData() {
		return new int[] { getPipeColor().getDyeItem().getTypeId(), getPipeColor().getDyeItem().getDurability() };
	}

	@Override
	public PipeType getPipeType() {
		return PipeType.COLORED;
	}

	@Override
	public List<ItemStack> getDroppedItems() {
		List<ItemStack> is = new ArrayList<>();
		is.add(PipeItemUtils.getPipeItem(getPipeType(), getPipeColor()));
		return is;
	}

	@Override
	public void saveToNBTTag(CompoundMap tags) {
		super.saveToNBTTag(tags);
		NBTUtils.saveStringValue(tags, "PipeColor", pipeColor.name());
	}

	@Override
	public void loadFromNBTTag(CompoundTag tag) {
		super.loadFromNBTTag(tag);
		pipeColor = PipeColor.valueOf(NBTUtils.readStringTag(tag.getValue().get("PipeColor"), PipeColor.WHITE.name()));
	}

}
