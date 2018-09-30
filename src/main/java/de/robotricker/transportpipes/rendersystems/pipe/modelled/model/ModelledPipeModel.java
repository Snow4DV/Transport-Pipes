package de.robotricker.transportpipes.rendersystems.pipe.modelled.model;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import de.robotricker.transportpipes.ducts.pipe.GoldenPipe;
import de.robotricker.transportpipes.ducts.types.BaseDuctType;
import de.robotricker.transportpipes.ducts.types.PipeType;
import de.robotricker.transportpipes.protocol.ArmorStandData;
import de.robotricker.transportpipes.rendersystems.pipe.modelled.model.data.ModelledExtractionPipeConnectionModelData;
import de.robotricker.transportpipes.rendersystems.pipe.modelled.model.data.ModelledIronPipeConnectionModelData;
import de.robotricker.transportpipes.rendersystems.pipe.modelled.model.data.ModelledPipeConnectionModelData;
import de.robotricker.transportpipes.location.RelativeLocation;
import de.robotricker.transportpipes.location.TPDirection;
import de.robotricker.transportpipes.ItemService;

public class ModelledPipeModel {

    private static ItemStack ITEM_IRONPIPE_CONN_OUTPUT;
    private static ItemStack ITEM_EXTRACTIONPIPE_CONN_EXTRACT;
    private static Map<PipeType, ItemStack> midItems;
    private static Map<PipeType, ItemStack> connItems;
    private static Map<GoldenPipe.Color, ItemStack> goldenPipeConnItems;

    public ArmorStandData createMidASD(PipeType pipeType) {
        return new ArmorStandData(new RelativeLocation(0.5f, 0.5f - 1.1875f, 0.5f), false, new Vector(1, 0, 0), new Vector(180f, 0f, 0f), new Vector(0f, 0f, 0f), midItems.get(pipeType), null);
    }

    public ArmorStandData createConnASD(ModelledPipeConnectionModelData connModelData) {
        ItemStack connItem = connItems.get(connModelData.getPipeType());
        if (connModelData.getPipeType().is("Iron") && ((ModelledIronPipeConnectionModelData) connModelData).isOutputSide()) {
            connItem = ITEM_IRONPIPE_CONN_OUTPUT;
        } else if (connModelData.getPipeType().is("Extraction") && ((ModelledExtractionPipeConnectionModelData) connModelData).isExtractionSide()) {
            connItem = ITEM_EXTRACTIONPIPE_CONN_EXTRACT;
        } else if (connModelData.getPipeType().is("Golden")) {
            connItem = goldenPipeConnItems.get(GoldenPipe.Color.getByDir(connModelData.getConnectionDir()));
        }

        ArmorStandData asd;
        if (connModelData.getConnectionDir() == TPDirection.UP) {
            asd = new ArmorStandData(new RelativeLocation(0.75f, 0.5f - 1.4369f, 0.5f), false, new Vector(1, 0, 0), new Vector(-90f, 0f, 0f), new Vector(0f, 0f, 0f), connItem, null);
        } else if (connModelData.getConnectionDir() == TPDirection.DOWN) {
            asd = new ArmorStandData(new RelativeLocation(0.25f, 0.5f - 1.1885f - 0.25f, 0.5f), false, new Vector(1, 0, 0), new Vector(90f, 0f, 0f), new Vector(0f, 0f, 0f), connItem, null);
        } else {
            asd = new ArmorStandData(new RelativeLocation(0.5f, 0.5f - 1.1875f, 0.5f), false, new Vector(connModelData.getConnectionDir().getX(), 0, connModelData.getConnectionDir().getZ()), new Vector(180f, 180f, 0f), new Vector(0f, 0f, 0f), connItem, null);
        }
        return asd;
    }

    public static void init(ItemService itemService) {
        ITEM_IRONPIPE_CONN_OUTPUT = itemService.createModelledItem(22);
        ITEM_EXTRACTIONPIPE_CONN_EXTRACT = itemService.createModelledItem(39);

        midItems = new HashMap<>();
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("White"), itemService.createModelledItem(1));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Blue"), itemService.createModelledItem(2));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Red"), itemService.createModelledItem(3));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Yellow"), itemService.createModelledItem(4));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Green"), itemService.createModelledItem(5));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Black"), itemService.createModelledItem(6));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Golden"), itemService.createModelledItem(13));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Iron"), itemService.createModelledItem(20));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Ice"), itemService.createModelledItem(23));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Void"), itemService.createModelledItem(35));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Extraction"), itemService.createModelledItem(37));
        midItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Crafting"), itemService.createModelledItem(42));

        connItems = new HashMap<>();
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("White"), itemService.createModelledItem(7));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Blue"), itemService.createModelledItem(8));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Red"), itemService.createModelledItem(9));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Yellow"), itemService.createModelledItem(10));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Green"), itemService.createModelledItem(11));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Black"), itemService.createModelledItem(12));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Iron"), itemService.createModelledItem(21));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Ice"), itemService.createModelledItem(24));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Void"), itemService.createModelledItem(36));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Extraction"), itemService.createModelledItem(38));
        connItems.put(BaseDuctType.valueOf("Pipe").ductTypeValueOf("Crafting"), itemService.createModelledItem(43));

        goldenPipeConnItems = new HashMap<>();
        goldenPipeConnItems.put(GoldenPipe.Color.WHITE, itemService.createModelledItem(14));
        goldenPipeConnItems.put(GoldenPipe.Color.BLUE, itemService.createModelledItem(15));
        goldenPipeConnItems.put(GoldenPipe.Color.RED, itemService.createModelledItem(16));
        goldenPipeConnItems.put(GoldenPipe.Color.YELLOW, itemService.createModelledItem(17));
        goldenPipeConnItems.put(GoldenPipe.Color.GREEN, itemService.createModelledItem(18));
        goldenPipeConnItems.put(GoldenPipe.Color.BLACK, itemService.createModelledItem(19));
    }

}
