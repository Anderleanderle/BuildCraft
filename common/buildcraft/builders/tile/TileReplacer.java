/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.builders.tile;

import java.util.Date;

import net.minecraft.util.ITickable;

import buildcraft.api.core.InvalidInputDataException;
import buildcraft.api.enums.EnumSnapshotType;
import buildcraft.api.schematics.ISchematicBlock;

import buildcraft.lib.misc.NBTUtilBC;
import buildcraft.lib.misc.data.IdAllocator;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.lib.tile.item.ItemHandlerManager;
import buildcraft.lib.tile.item.ItemHandlerSimple;

import buildcraft.builders.BCBuildersItems;
import buildcraft.builders.item.ItemSchematicSingle;
import buildcraft.builders.snapshot.Blueprint;
import buildcraft.builders.snapshot.GlobalSavedDataSnapshots;
import buildcraft.builders.snapshot.SchematicBlockManager;
import buildcraft.builders.snapshot.Snapshot;
import buildcraft.builders.snapshot.Snapshot.Header;

public class TileReplacer extends TileBC_Neptune implements ITickable {
    public static final IdAllocator IDS = TileBC_Neptune.IDS.makeChild("replacer");

    public final ItemHandlerSimple invSnapshot = itemManager.addInvHandler("snapshot", 1, ItemHandlerManager.EnumAccess.NONE);
    public final ItemHandlerSimple invSchematicFrom = itemManager.addInvHandler("schematicFrom", 1, ItemHandlerManager.EnumAccess.NONE);
    public final ItemHandlerSimple invSchematicTo = itemManager.addInvHandler("schematicTo", 1, ItemHandlerManager.EnumAccess.NONE);

    @Override
    public void update() {
        if (worldObj.isRemote) {
            return;
        }
        if (!(invSnapshot.getStackInSlot(0) == null) &&
            !(invSchematicFrom.getStackInSlot(0) == null) &&
            !(invSchematicTo.getStackInSlot(0) == null)) {
            Header header = BCBuildersItems.snapshot.getHeader(invSnapshot.getStackInSlot(0));
            if (header != null) {
                GlobalSavedDataSnapshots store = GlobalSavedDataSnapshots.get(worldObj);
                Snapshot snapshot = store.getSnapshotByHeader(header);
                if (snapshot instanceof Blueprint) {
                    Blueprint blueprint = (Blueprint) snapshot;
                    try {
                        ISchematicBlock<?> from = SchematicBlockManager.readFromNBT(
                            NBTUtilBC.getItemData(invSchematicFrom.getStackInSlot(0))
                                .getCompoundTag(ItemSchematicSingle.NBT_KEY)
                        );
                        ISchematicBlock<?> to = SchematicBlockManager.readFromNBT(
                            NBTUtilBC.getItemData(invSchematicTo.getStackInSlot(0))
                                .getCompoundTag(ItemSchematicSingle.NBT_KEY)
                        );
                        Blueprint newBlueprint = blueprint.copy();
                        newBlueprint.replace(from, to);
                        Header nHeader = new Header(newBlueprint.computeHash(), getOwner().getId(), new Date(), header.name);
                        newBlueprint.header = nHeader;
                        store.snapshots.add(newBlueprint);
                        store.markDirty();
                        invSnapshot.setStackInSlot(
                            0,
                            BCBuildersItems.snapshot.getUsed(
                                EnumSnapshotType.BLUEPRINT,
                                newBlueprint.header
                            )
                        );
                        invSchematicFrom.setStackInSlot(0, null);
                        invSchematicTo.setStackInSlot(0, null);
                    } catch (InvalidInputDataException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
