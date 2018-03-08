/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.inventory;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import buildcraft.api.core.IStackFilter;
import buildcraft.api.inventory.IItemTransactor;

import buildcraft.lib.misc.StackUtil;

public enum NoSpaceTransactor implements IItemTransactor {
    INSTANCE;

    @Nullable
    @Override
    public ItemStack insert(@Nullable ItemStack stack, boolean allOrNone, boolean simulate) {
        return stack;
    }

    @Override
    public List<ItemStack> insert(List<ItemStack> stacks, boolean simulate) {
        return stacks;
    }

    @Nullable
    @Override
    public ItemStack extract(IStackFilter filter, int min, int max, boolean simulate) {
        return StackUtil.EMPTY;
    }
}