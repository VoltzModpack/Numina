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

package com.github.lehjr.numina.client.gui;

import com.github.lehjr.numina.container.ArmorStandContainer;
import com.github.lehjr.numina.util.client.gui.ExtendedContainerScreen;
import com.github.lehjr.numina.util.client.gui.IContainerULOffSet;
import com.github.lehjr.numina.util.client.gui.frame.*;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ArmorStandGui extends ExtendedContainerScreen<ArmorStandContainer> {
    InnerFrame innerFrame;
    int spacer = 7;
    ArmorStandEntity armorStandEntity;

    public ArmorStandGui(ArmorStandContainer containerIn, PlayerInventory inv, ITextComponent titleIn) {
        super(containerIn, inv, titleIn, 176,  172, true);
        innerFrame = new InnerFrame(containerIn, ulGetter());
        addFrame(innerFrame);
        this.armorStandEntity = containerIn.getArmorStandEntity();
    }

    @Override
    public void init() {
        super.init();
        innerFrame.setPosition(backgroundRect.getPosition());
        innerFrame.setPlayerEntity(getMinecraft().player);
        innerFrame.setArmorStandEntity(armorStandEntity);
        innerFrame.setGuiLeft(getGuiLeft());
        innerFrame.setGuiTop(getGuiTop());
    }

    @Override
    public void renderBg(MatrixStack matrixStack, float frameTime, int mouseX, int mouseY) {
        super.renderBg(matrixStack, frameTime, mouseX, mouseY);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float frameTIme) {
        renderBackground(matrixStack);
        if (backgroundRect.doneGrowing()) {
            super.render(matrixStack, mouseX, mouseY, frameTIme);
            this.drawToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
//        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY + 8, 4210752);
        this.innerFrame.renderLabels(matrixStack, mouseX, mouseY);
    }

    class ArmorInventoryFrame extends InventoryFrame {
        public ArmorInventoryFrame(Container containerIn, List<Integer> slotIndexesIn, IContainerULOffSet.ulGetter ulGetter) {
            super(containerIn, Colour.LIGHT_GREY, Colour.DARK_GREY, Colour.DARK_GREY, 1, 4, slotIndexesIn, ulGetter);
        }
    }

    class InnerFrame extends MultiRectHolderFrame {
        protected ArmorInventoryFrame playerArmor, armorStandArmor;
        protected EntityRenderFrame playerFrame, armorStandFrame;
        InventoryFrame playerShield, armorStandHands;
        protected PlayerInventoryFrame playerInventoryFrame;

        public InnerFrame(Container containerIn, IContainerULOffSet.ulGetter ulGetter) {
            super(false, true, 0, 0);

            /** Center box with player armor and both armor stand hand slots */
            MultiRectHolderFrame centerFrame = new MultiRectHolderFrame(false, true, 0, 0);
            // slot 4-5
            armorStandHands = new InventoryFrame(containerIn,
                    1, 2, new ArrayList<Integer>(){{
                IntStream.range(4, 6).forEach(i-> add(i));
            }}, ulGetter);
            centerFrame.addRect(armorStandHands);
            centerFrame.addRect(new GUISpacer(18, 18));

            // slot 46
            playerShield = new InventoryFrame(containerIn,
                    1, 1, new ArrayList<Integer>(){{
                IntStream.range(46, 47).forEach(i-> add(i));
            }}, ulGetter);
            centerFrame.addRect(playerShield);
            centerFrame.doneAdding();

            /** Horizontal layout with all of the standing top boxes */
            MultiRectHolderFrame topHorizontalLayout = new MultiRectHolderFrame(true, true, 0, 0);

            // slot 6-9
            playerArmor = new ArmorInventoryFrame(containerIn,
                    new ArrayList<Integer>(){{
                        IntStream.range(6, 10).forEach(i-> add(i));
                    }}, ulGetter);
            topHorizontalLayout.addRect(new GUISpacer(spacer, playerArmor.finalHeight()));
            topHorizontalLayout.addRect(playerArmor);
            topHorizontalLayout.addRect(new GUISpacer(3, playerArmor.finalHeight()));

            playerFrame = new EntityRenderFrame(false);
            playerFrame.setWidth(48);
            playerFrame.setHeight(playerArmor.finalHeight());
            topHorizontalLayout.addRect(playerFrame);

            topHorizontalLayout.addRect(new GUISpacer(3, playerArmor.finalHeight()));
            topHorizontalLayout.addRect(centerFrame);
            topHorizontalLayout.addRect(new GUISpacer(3, playerArmor.finalHeight()));

            armorStandFrame = new EntityRenderFrame(false);
            armorStandFrame.setWidth(48);
            armorStandFrame.setHeight(playerArmor.finalHeight());
            topHorizontalLayout.addRect(armorStandFrame);
            topHorizontalLayout.addRect(new GUISpacer(3, playerArmor.finalHeight()));

            // slot 0-3
            armorStandArmor = new ArmorInventoryFrame(
                    containerIn,
                    new ArrayList<Integer>(){{
                        IntStream.range(0, 4).forEach(i-> add(i));
                    }}, ulGetter
            );
            topHorizontalLayout.addRect(armorStandArmor);
            topHorizontalLayout.addRect(new GUISpacer(spacer, playerArmor.finalHeight()));
            topHorizontalLayout.doneAdding();

            /** final complete layout with all boxes in place */
            GUISpacer topSpacer = new GUISpacer(topHorizontalLayout.finalWidth(), spacer -3);
            addRect(topSpacer);

            addRect(topHorizontalLayout);

            playerInventoryFrame = new PlayerInventoryFrame(containerIn, 10, 37, ulGetter);
            addRect(playerInventoryFrame);
            doneAdding();
        }

        public void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
            playerInventoryFrame.renderLabels(matrixStack, mouseX, mouseY);
        }

        public void setGuiLeft(double guiLeft) {
            playerFrame.setGuiLeft(guiLeft);
            armorStandFrame.setGuiLeft(guiLeft);
        }

        public void setGuiTop(double guiTop) {
            playerFrame.setGuiTop(guiTop);
            armorStandFrame.setGuiTop(guiTop);
        }

        public void setArmorStandEntity(LivingEntity armorStand) {
            armorStandFrame.setLivingEntity(armorStand);
        }

        public void setPlayerEntity(LivingEntity player) {
            playerFrame.setLivingEntity(player);
        }
    }
}