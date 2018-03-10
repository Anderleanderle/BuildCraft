/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.factory.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

import buildcraft.api.core.BuildCraftAPI;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.mj.IMjReceiver;

import buildcraft.lib.inventory.AutomaticProvidingTransactor;
import buildcraft.lib.misc.BlockUtil;
import buildcraft.lib.misc.CapUtil;
import buildcraft.lib.misc.InventoryUtil;
import buildcraft.lib.mj.MjBatteryReceiver;

import buildcraft.factory.BCFactoryBlocks;

public class TileMiningWell extends TileMiner {
    public TileMiningWell() {
        super();
        caps.addCapabilityInstance(CapUtil.CAP_ITEM_TRANSACTOR, AutomaticProvidingTransactor.INSTANCE, EnumPipePart.VALUES);
    }

    @Override
    protected void mine() {
        if (currentPos != null && canBreak()) {
            long target = BlockUtil.computeBlockBreakPower(worldObj, currentPos);
            progress += battery.extractPower(0, target - progress);
            if (progress >= target) {
                progress = 0;
                EntityPlayer fakePlayer = BuildCraftAPI.fakePlayerProvider.getFakePlayer((WorldServer) worldObj, getOwner(), pos);
                BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(worldObj, currentPos, worldObj.getBlockState(currentPos), fakePlayer);
                MinecraftForge.EVENT_BUS.post(breakEvent);
                if (!breakEvent.isCanceled()) {
                    List<ItemStack> stacks = BlockUtil.getItemStackFromBlock((WorldServer) worldObj, currentPos, getOwner());
                    if (stacks != null) {
                        for (ItemStack stack : stacks) {
                            InventoryUtil.addToBestAcceptor(worldObj, pos, null, stack);
                        }
                    }
                    worldObj.sendBlockBreakProgress(currentPos.hashCode(), currentPos, -1);
                    worldObj.destroyBlock(currentPos, false);
                }
                nextPos();
                updateLength();
            } else {
                if (!worldObj.isAirBlock(currentPos)) {
                    worldObj.sendBlockBreakProgress(currentPos.hashCode(), currentPos, (int) ((progress * 9) / target));
                }
            }
        } else {
            nextPos();
            updateLength();
        }
    }

    private boolean canBreak() {
        return !worldObj.isAirBlock(currentPos) && !BlockUtil.isUnbreakableBlock(worldObj, currentPos, getOwner());
    }

    private void nextPos() {
        for (currentPos = pos.down(); currentPos.getY() >= 0; currentPos = currentPos.down()) {
            if (canBreak()) {
                updateLength();
                return;
            } else if (!worldObj.isAirBlock(currentPos) && worldObj.getBlockState(currentPos).getBlock() != BCFactoryBlocks.tube) {
                break;
            }
        }
        updateLength();
        currentPos = null;
    }

    @Override
    protected void initCurrentPos() {
        if (currentPos == null) {
            nextPos();
        }
    }

    @Override
    public void invalidate() {
        if (currentPos != null) {
            worldObj.sendBlockBreakProgress(currentPos.hashCode(), currentPos, -1);
        }
        super.invalidate();
    }

    @Override
    protected IMjReceiver createMjReceiver() {
        return new MjBatteryReceiver(battery);
    }
}
