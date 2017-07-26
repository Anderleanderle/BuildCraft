/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.client.render;

import net.minecraft.entity.player.EntityPlayer;

import buildcraft.lib.client.render.DetatchedRenderer.IDetachedRenderer;
import buildcraft.lib.marker.MarkerCache;
import buildcraft.lib.marker.MarkerConnection;
import buildcraft.lib.marker.MarkerSubCache;

public enum MarkerRenderer implements IDetachedRenderer {
    INSTANCE;
	
	@Override
	public void render(EntityPlayer player, float partialTicks) {
		internalRenderFunction(player, partialTicks);
	}
	
    @SuppressWarnings("unchecked")
	private <C extends MarkerConnection<C>> void internalRenderFunction(EntityPlayer player, float partialTicks) {
        for (MarkerCache<? extends MarkerSubCache<?>> cache : MarkerCache.CACHES) {
            renderCache((MarkerSubCache<C>) cache.getSubCache(player.worldObj));
        }
    }

	private static <C extends MarkerConnection<C>> void renderCache(MarkerSubCache<C> cache) {
        for (C connection : cache.getConnections()) {
            connection.renderInWorld();
        }
    }
}
