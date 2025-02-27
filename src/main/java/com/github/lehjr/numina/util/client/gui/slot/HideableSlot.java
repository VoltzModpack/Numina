/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.lehjr.numina.util.client.gui.slot;

import com.github.lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class HideableSlot extends Slot implements IHideableSlot {
    boolean isEnabled = false;

    public HideableSlot(IInventory iInventory, int slotIndex, int xPosition, int yPosition) {
        super(iInventory, slotIndex, xPosition, yPosition);
    }

    public HideableSlot(IInventory iInventory, int slotIndex, int xPosition, int yPosition, boolean enabled) {
        super(iInventory, slotIndex, xPosition, yPosition);
        this.isEnabled = enabled;
    }

//    @Override
//    public boolean mayPlace(ItemStack itemStack) {
//        return this.isEnabled ? super.mayPlace(itemStack) : false;
//    }

    @Override
    public void enable() {
        this.isEnabled = true;
    }

    @Override
    public void disable() {
        this.isEnabled = false;
    }

    @Override
    public boolean isActive() {
        return isEnabled;
    }

    @Override
    public void setPosition(MusePoint2D position) {
        this.x = (int) position.getX();
        this.y = (int) position.getY();
    }
}