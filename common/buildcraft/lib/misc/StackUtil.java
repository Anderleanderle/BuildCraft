/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.oredict.OreDictionary;

import buildcraft.api.items.IList;
import buildcraft.api.recipes.StackDefinition;

/** Provides various utils for interacting with {@link ItemStack}, and multiples. */
public class StackUtil {
	/**
	 * This constant contains a null {@link ItemStack}. Just kept this for compatibility purposes.
	 */
    // Actually the entire MC
    public static final ItemStack EMPTY;

    static {
        ItemStack stack = null;
        EMPTY = stack;
    }

    /** Checks to see if the two input stacks are equal in all but stack size. Note that this doesn't check anything
     * todo with stack size, so if you pass in two stacks of 64 cobblestone this will return true. If you pass in null
     * (at all) then this will only return true if both are null. */
    public static boolean canMerge(ItemStack a, ItemStack b) {
        // Checks item, damage
        if (!ItemStack.areItemsEqual(a, b)) {
            return false;
        }
        // checks tags and caps
        return ItemStack.areItemStackTagsEqual(a, b);
    }

    /** Attempts to get an item stack that might place down the given blockstate. Obviously this isn't perfect, and so
     * cannot be relied on for anything more than simple blocks. */
    @Nullable
    public static ItemStack getItemStackForState(IBlockState state) {
        Block b = state.getBlock();
        if (Item.getItemFromBlock(b) == null) {
            return StackUtil.EMPTY;
        }
        ItemStack stack = new ItemStack(b);
        if (stack.getHasSubtypes()) {
            stack = new ItemStack(stack.getItem(), 1, b.getMetaFromState(state));
        }
        return stack;
    }

    /** Checks to see if the given required stack is contained fully in the given container stack. */
    public static boolean contains(ItemStack required, ItemStack container) {
        if (canMerge(required, container)) {
            return container.stackSize >= required.stackSize;
        }
        return false;
    }

    /** Checks to see if the given required stack is contained fully in a single stack in a list. */
    public static boolean contains(ItemStack required, Collection<ItemStack> containers) {
        for (ItemStack possible : containers) {
            if (contains(required, possible)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that passed stack meets stack definition requirements
     */
    public static boolean contains(StackDefinition stackDefinition, ItemStack stack) {
        return !(stack == null) && stackDefinition.filter.matches(stack) && stack.stackSize >= stackDefinition.count;
    }

    /**
     * Checks that passed stack definition acceptable for stack collection
     */
    public static boolean contains(StackDefinition stackDefinition, List<ItemStack> stacks) {
        return stacks.stream().anyMatch((stack) -> contains(stackDefinition, stack));
    }

    /** Checks to see if the given required stacks are all contained within the collection of containers. Note that this
     * assumes that all of the required stacks are different. */
    public static boolean containsAll(Collection<ItemStack> required, Collection<ItemStack> containers) {
        for (ItemStack req : required) {
            if (req == null) continue;
            if (!contains(req, containers)) {
                return false;
            }
        }
        return true;
    }

    public static NBTTagCompound stripNonFunctionNbt(ItemStack from) {
        NBTTagCompound nbt = NBTUtilBC.getItemData(from).copy();
        if (nbt.getSize() == 0) {
            return nbt;
        }
        nbt.removeTag("_data");
        // TODO: Remove all of the non functional stuff (name, desc, etc)
        return nbt;
    }

    public static boolean doesStackNbtMatch(ItemStack target, ItemStack with) {
        NBTTagCompound nbtTarget = stripNonFunctionNbt(target);
        NBTTagCompound nbtWith = stripNonFunctionNbt(with);
        return nbtTarget.equals(nbtWith);
    }

    public static boolean doesEitherStackMatch(ItemStack stackA, ItemStack stackB) {
        return OreDictionary.itemMatches(stackA, stackB, false) || OreDictionary.itemMatches(stackB, stackA, false);
    }

    public static boolean canStacksOrListsMerge(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return false;
        }

        if (stack1.getItem() instanceof IList) {
            IList list = (IList) stack1.getItem();
            return list.matches(stack1, stack2);
        } else if (stack2.getItem() instanceof IList) {
            IList list = (IList) stack2.getItem();
            return list.matches(stack2, stack1);
        }

        return stack1.isItemEqual(stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);

    }

    /** Merges mergeSource into mergeTarget
     *
     * @param mergeSource - The stack to merge into mergeTarget, this stack is not modified
     * @param mergeTarget - The target merge, this stack is modified if doMerge is set
     * @param doMerge - To actually do the merge
     * @return The number of items that was successfully merged. */
    public static int mergeStacks(ItemStack mergeSource, ItemStack mergeTarget, boolean doMerge) {
        if (!canMerge(mergeSource, mergeTarget)) {
            return 0;
        }
        int mergeCount = Math.min(mergeTarget.getMaxStackSize() - mergeTarget.stackSize, mergeSource.stackSize);
        if (mergeCount < 1) {
            return 0;
        }
        if (doMerge) {
            mergeTarget.stackSize = mergeTarget.stackSize + mergeCount;
        }
        return mergeCount;
    }

    /* ITEM COMPARISONS */
    /** Determines whether the given ItemStack should be considered equivalent for crafting purposes.
     *
     * @param base The stack to compare to.
     * @param comparison The stack to compare.
     * @param oreDictionary true to take the Forge OreDictionary into account.
     * @return true if comparison should be considered a crafting equivalent for base. */
    public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean oreDictionary) {
        if (isMatchingItem(base, comparison, true, false)) {
            return true;
        }

        if (oreDictionary) {
            int[] idBase = OreDictionary.getOreIDs(base);
            if (idBase.length > 0) {
                for (int id : idBase) {
                    for (ItemStack itemstack : OreDictionary.getOres(OreDictionary.getOreName(id))) {
                        if (comparison.getItem() == itemstack.getItem() && (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE || comparison.getItemDamage() == itemstack.getItemDamage())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean isCraftingEquivalent(int[] oreIDs, ItemStack comparison) {
        if (oreIDs.length > 0) {
            for (int id : oreIDs) {
                for (ItemStack itemstack : OreDictionary.getOres(OreDictionary.getOreName(id))) {
                    if (comparison.getItem() == itemstack.getItem() && (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE || comparison.getItemDamage() == itemstack.getItemDamage())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isMatchingItemOrList(final ItemStack base, final ItemStack comparison) {
        if (base == null || comparison == null) {
            return false;
        }

        if (base.getItem() instanceof IList) {
            IList list = (IList) base.getItem();
            return list.matches(base, comparison);
        } else if (comparison.getItem() instanceof IList) {
            IList list = (IList) comparison.getItem();
            return list.matches(comparison, base);
        }

        return isMatchingItem(base, comparison, true, false);
    }

    /** Compares item id, damage and NBT. Accepts wildcard damage. Ignores damage entirely if the item doesn't have
     * subtypes.
     *
     * @param base The stack to compare to.
     * @param comparison The stack to compare.
     * @return true if id, damage and NBT match. */
    public static boolean isMatchingItem(final ItemStack base, final ItemStack comparison) {
        return isMatchingItem(base, comparison, true, true);
    }

    /** This variant also checks damage for damaged items. */
    public static boolean isEqualItem(final ItemStack base, final ItemStack comparison) {
        if (isMatchingItem(base, comparison, false, true)) {
            return isWildcard(base) || isWildcard(comparison) || base.getItemDamage() == comparison.getItemDamage();
        } else {
            return false;
        }
    }

    /** Compares item id, and optionally damage and NBT. Accepts wildcard damage. Ignores damage entirely if the item
     * doesn't have subtypes.
     *
     * @param base ItemStack
     * @param comparison ItemStack
     * @param matchDamage
     * @param matchNBT
     * @return true if matches */
    public static boolean isMatchingItem(final ItemStack base, final ItemStack comparison, final boolean matchDamage, final boolean matchNBT) {
        if (base == null || comparison == null) {
            return false;
        }

        if (base.getItem() != comparison.getItem()) {
            return false;
        }
        if (matchDamage && base.getHasSubtypes()) {
            if (!isWildcard(base) && !isWildcard(comparison)) {
                if (base.getItemDamage() != comparison.getItemDamage()) {
                    return false;
                }
            }
        }
        if (matchNBT) {
            NBTTagCompound baseTag = base.getTagCompound();
            if (baseTag != null && !baseTag.equals(comparison.getTagCompound())) {
                return false;
            }
        }
        return true;
    }

    /** Checks to see if the given {@link ItemStack} is considered to be a wildcard stack - that is any damage value on
     * the stack will be considered the same as this for recipe purposes.
     * 
     * @param stack The stack to check
     * @return True if the stack is a wildcard, false if not. */
    public static boolean isWildcard(ItemStack stack) {
        return isWildcard(stack.getItemDamage());
    }

    /** Checks to see if the given {@link ItemStack} is considered to be a wildcard stack - that is any damage value on
     * the stack will be considered the same as this for recipe purposes.
     * 
     * @param damage The damage to check
     * @return True if the damage does specify a wildcard, false if not. */
    public static boolean isWildcard(int damage) {
        return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
    }

    /** @return An empty, nonnull list that cannot be modified (as it cannot be expanded and it has a size of 0) */
    public static List<ItemStack> listOf() {
    	return new ArrayList<ItemStack>(0);
    }

    /** Creates a {@link ArrayList} of {@link ItemStack}'s with the elements given in the order that they are given.
     * 
     * @param stacks The stacks to put into a list
     * @return A {@link ArrayList} of all the given items. Note that the returned list of of a specified size, and
     *         cannot be expanded. */
    public static List<ItemStack> listOf(ItemStack... stacks) {
        switch (stacks.length) {
            case 0:
                return listOf();
            case 1:
            	ArrayList<ItemStack> list = new ArrayList<ItemStack>();
            	list.add(stacks[0]);
            	return list;
            	
            default:
        }
        List<ItemStack> list = new ArrayList<ItemStack>(stacks.length);
        for (int i = 0; i < stacks.length; i++) {
            list.set(i, stacks[i]);
        }
        return list;
    }

    /* Takes a {@link Nullable} {@link Object} and checks to make sure that it is really {@link Nonnull}, like it is
     * everywhere else in the codebase. This is only required if some classes do not use the {@link Nonnull} annotation
     * on return values.
     * 
     * @param obj The (potentially) null object.
     * @return A {@link Nonnull} object, which will be the input object
     * @throws NullPointerException if the input object was actually null (Although this should never happen, this is
     *             more to catch bugs in dev.) 
    @Nonnull
    public static <T> T asNonNull(@Nullable T obj) {
        if (obj == null) {
            throw new NullPointerException("Object was null!");
        }
        return obj;
    }
    */

    @Nonnull
    public static <T> T Soft(@Nullable T obj, @Nonnull T fallback) {
        if (obj == null) {
            return fallback;
        } else {
            return obj;
        }
    }

    @Nonnull
    public static ItemStack Soft(ItemStack stack) {
        return Soft(stack, EMPTY);
    }

    /** @return A {@link Collector} that will collect the input elements into a {@link NonNullList} */
    public static <E> Collector<E, ?, List<E>> nonNullListCollector() {
        //return Collectors.toCollection(List::create);
    	return Collectors.toCollection(ArrayList::new); //TODO Check if this works
    }

    /** Computes a hash code for the given {@link ItemStack}. This is based off of {@link ItemStack#serializeNBT()},
     * except if {@link ItemStack#isEmpty()} returns true, in which case the hash will be 0. */
    public static int hash(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return stack.serializeNBT().hashCode();
    }

    public static List<ItemStack> mergeSameItems(List<ItemStack> items) {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (ItemStack toAdd : items) {
            boolean found = false;
            for (ItemStack stack : stacks) {
                if (canMerge(stack, toAdd)) {
                    stack.stackSize += toAdd.stackSize;
                    found = true;
                }
            }
            if (!found) {
                stacks.add(toAdd.copy());
            }
        }
        return stacks;
    }
}
