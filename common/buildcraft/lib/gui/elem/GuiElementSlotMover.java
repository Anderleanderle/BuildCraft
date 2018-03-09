package buildcraft.lib.gui.elem;

import net.minecraft.inventory.Slot;

import buildcraft.lib.expression.api.IExpressionNode.INodeBoolean;
import buildcraft.lib.gui.GuiElementSimple;
import buildcraft.lib.gui.json.GuiJson;
import buildcraft.lib.gui.pos.IGuiArea;
import buildcraft.lib.gui.pos.IGuiPosition;

public class GuiElementSlotMover extends GuiElementSimple<GuiJson<?>> {

    public final INodeBoolean visible;
    public final Slot toMove;

    public GuiElementSlotMover(GuiJson<?> gui, IGuiPosition pos, INodeBoolean visible, Slot toMove) {
        super(gui, IGuiArea.create(pos, 18, 18));
        this.visible = visible;
        this.toMove = toMove;
    }

    @Override
    public void drawBackground(float partialTicks) {
        if (visible.evaluate()) {
            toMove.xDisplayPosition = 1 + (int) Math.round(getX());
            toMove.yDisplayPosition = 1 + (int) Math.round(getY());
        } else {
            toMove.xDisplayPosition = -10000;
            toMove.yDisplayPosition = -10000;
        }
    }
}
