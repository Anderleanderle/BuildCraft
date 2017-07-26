/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.transport.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

import buildcraft.lib.misc.StackUtil;
import buildcraft.lib.recipe.ChangingItemStack;
import buildcraft.lib.recipe.IRecipeViewable;

import buildcraft.transport.BCTransportItems;
import buildcraft.transport.item.ItemPluggableFacade;
import buildcraft.transport.plug.FacadeStateManager;
import buildcraft.transport.plug.FacadeStateManager.FacadeBlockStateInfo;
import buildcraft.transport.plug.FacadeStateManager.FullFacadeInstance;

public enum FacadeSwapRecipe implements IRecipe, IRecipeViewable.IViewableGrid {
    INSTANCE;

    private static final int TIME_GAP = 500;

    private static final ChangingItemStack[] INPUTS;
    private static final ChangingItemStack OUTPUTS;

    static {
        INPUTS = new ChangingItemStack[1];
        List<ItemStack> list1 = new ArrayList<ItemStack>();
        List<ItemStack> list2 = new ArrayList<ItemStack>();
        for (FacadeBlockStateInfo info : FacadeStateManager.validFacadeStates.values()) {
            if (info.isVisible) {
                ItemStack stack = createFacade(info, false);
                ItemStack stackHollow = createFacade(info, true);
                list1.add(stack);
                list1.add(stackHollow);
                list2.add(stackHollow);
                list2.add(stack);
            }
        }
        INPUTS[0] = new ChangingItemStack(list1);
        INPUTS[0].setTimeGap(TIME_GAP);

        OUTPUTS = new ChangingItemStack(list2);
        OUTPUTS.setTimeGap(TIME_GAP);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return !(getCraftingResult(inv) == null);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack stackIn = StackUtil.EMPTY;
        for (int s = 0; s < inv.getSizeInventory(); s++) {
            ItemStack stack = inv.getStackInSlot(s);
            if (!(stack == null)) {
                if (stackIn == null) {
                    stackIn = stack;
                } else {
                    return StackUtil.EMPTY;
                }
            }
        }
        if (stackIn == null) {
        	return StackUtil.EMPTY;
        }
        if (stackIn.getItem() != BCTransportItems.plugFacade) {
            return StackUtil.EMPTY;
        }
        FullFacadeInstance states = ItemPluggableFacade.getStates(stackIn);
        states = states.withSwappedIsHollow();
        return BCTransportItems.plugFacade.createItemStack(states);
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return StackUtil.EMPTY;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    @Override
    public ChangingItemStack[] getRecipeInputs() {
        return INPUTS;
    }

    @Override
    public ChangingItemStack getRecipeOutputs() {
        return OUTPUTS;
    }

    private static ItemStack createFacade(FacadeBlockStateInfo info, boolean isHollow) {
        FullFacadeInstance state = FullFacadeInstance.createSingle(info, isHollow);
        return BCTransportItems.plugFacade.createItemStack(state);
    }

    @Override
    public int getRecipeWidth() {
        return 1;
    }

    @Override
    public int getRecipeHeight() {
        return 1;
    }
}
