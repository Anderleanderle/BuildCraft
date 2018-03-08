/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.client.guide.parts.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import buildcraft.lib.client.guide.parts.GuidePartFactory;
import buildcraft.lib.misc.StackUtil;
import buildcraft.lib.recipe.ChangingItemStack;
import buildcraft.lib.recipe.IRecipeViewable;

public enum GuideCraftingRecipes implements IStackRecipes {
    INSTANCE;

    @Override
    public List<GuidePartFactory> getUsages(@Nullable ItemStack target) {
        List<GuidePartFactory> list = new ArrayList<>();

        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            if (checkRecipeUses(recipe, target)) {
                GuidePartFactory factory = GuideCraftingFactory.getFactory(recipe);
                if (factory != null) {
                    list.add(factory);
                }
            }
        }
        return list;
    }

    private static boolean checkRecipeUses(IRecipe recipe, @Nullable ItemStack target) {
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shaped = (ShapedRecipes) recipe;
            for (ItemStack in : shaped.recipeItems) {
                if (StackUtil.doesEitherStackMatch((in), target)) {
                    return true;
                }
            }
        } else if (recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
            for (ItemStack in : shapeless.recipeItems) {
                if (StackUtil.doesEitherStackMatch((in), target)) {
                    return true;
                }
            }
        } else if (recipe instanceof ShapedOreRecipe) {
            ShapedOreRecipe ore = (ShapedOreRecipe) recipe;
            for (Object in : ore.getInput()) {
                if (matches(target, in)) {
                    return true;
                }
            }
        } else if (recipe instanceof ShapelessOreRecipe) {
            ShapelessOreRecipe ore = (ShapelessOreRecipe) recipe;
            for (Object in : ore.getInput()) {
                if (matches(target, in)) {
                    return true;
                }
            }
        } else if (recipe instanceof IRecipeViewable) {
            IRecipeViewable viewable = (IRecipeViewable) recipe;
            for (ChangingItemStack changing : viewable.getRecipeInputs()) {
                if (changing.matches(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matches(@Nullable ItemStack target, @Nullable Object in) {
        if (in instanceof ItemStack) {
            return StackUtil.doesEitherStackMatch((ItemStack) in, target);
        } else if (in instanceof List) {
            for (Object obj : (List<?>) in) {
                if (obj instanceof ItemStack) {
                    if (StackUtil.doesEitherStackMatch((ItemStack) obj, target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<GuidePartFactory> getRecipes(@Nullable ItemStack target) {
        List<GuidePartFactory> list = new ArrayList<>();

        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            if (recipe instanceof IRecipeViewable) {
                ChangingItemStack changing = ((IRecipeViewable) recipe).getRecipeOutputs();
                if (changing.matches(target)) {
                    GuidePartFactory factory = GuideCraftingFactory.getFactory(recipe);
                    if (factory != null) {
                        list.add(factory);
                    }
                }
            } else {
                ItemStack out = (recipe.getRecipeOutput());
                if (OreDictionary.itemMatches(target, out, false) || OreDictionary.itemMatches(out, target, false)) {
                    GuidePartFactory factory = GuideCraftingFactory.getFactory(recipe);
                    if (factory != null) {
                        list.add(factory);
                    }
                }
            }
        }

        return list;
    }
}
