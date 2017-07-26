package buildcraft.lib.inventory;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import buildcraft.api.core.IInvSlot;

// Ok, this class is HORRID. IInvSlot needs to be redone to be based off of IItemHandler rather than IInventory
class InventoryIteratorHandler implements Iterable<IInvSlot> {

    private final IItemHandler inv;

    InventoryIteratorHandler(IItemHandler inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return new Iterator<IInvSlot>() {
            int slot = 0;

            @Override
            public boolean hasNext() {
                return slot < inv.getSlots();
            }

            @Override
            public IInvSlot next() {
                return new InvSlot(slot++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }
        };
    }

    class InvSlot implements IInvSlot {

        private int slot;

        InvSlot(int slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack getStackInSlot() {
            return inv.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(ItemStack stack) {
            if (inv instanceof IItemHandlerModifiable) {
                ((IItemHandlerModifiable) inv).setStackInSlot(slot, stack);
            } else {
                throw new IllegalStateException("Invalid IItemHandler class " + inv.getClass());
            }
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.insertItem(slot, stack, true) == null;
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return !(inv.extractItem(slot, 1, true) == null);
        }

        @Override
        public boolean isItemValidForSlot(ItemStack stack) {
            return inv.insertItem(slot, stack, true) == null;
        }

        @Override
        public ItemStack decreaseStackInSlot(int amount) {
            return inv.extractItem(slot, amount, false);
        }

        @Override
        public int getIndex() {
            return slot;
        }
    }
}
