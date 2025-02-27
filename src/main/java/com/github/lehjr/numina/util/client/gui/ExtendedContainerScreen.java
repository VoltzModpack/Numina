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

package com.github.lehjr.numina.util.client.gui;

import com.github.lehjr.numina.util.client.gui.frame.IGuiFrame;
import com.github.lehjr.numina.util.client.gui.gemoetry.DrawableRelativeRect;
import com.github.lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import com.github.lehjr.numina.util.client.gui.slot.IHideableSlot;
import com.github.lehjr.numina.util.client.gui.slot.IIConProvider;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: inventory label
 * @param <T>
 */
public class ExtendedContainerScreen<T extends Container> extends ContainerScreen<T> {
    protected long creationTime;
    protected DrawableRelativeRect tooltipRect;
    /** The outer gui rectangle */
    protected DrawableRelativeRect backgroundRect;
    private List<IGuiFrame> frames;

    public ExtendedContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn, boolean growFromMiddle) {
        super(screenContainer, inv, titleIn);
        frames = new ArrayList();
        tooltipRect = new DrawableRelativeRect(
                0, 0, 0, 0,
                false,
                Colour.BLACK.withAlpha(0.9F),
                Colour.PURPLE);
        backgroundRect = new DrawableRelativeRect(0, 0, 0, 0, growFromMiddle, Colour.GREY_GUI_BACKGROUND, Colour.BLACK);
        this.minecraft = Minecraft.getInstance();
    }

    /**
     *
     * @param screenContainer
     * @param inv
     * @param titleIn
     * @param guiWidth sets the "imageWidth" parameter to determine the
     * @param guiHeight
     */
    public ExtendedContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn, int guiWidth, int guiHeight, boolean growFromMiddle) {
        this(screenContainer, inv, titleIn, growFromMiddle);
        this.imageWidth = guiWidth;
        this.imageHeight = guiHeight;
    }

    MusePoint2D getUlOffset () {
        return new MusePoint2D(getGuiLeft(), getGuiTop());
    }

    public IContainerULOffSet.ulGetter ulGetter() {
        return new IContainerULOffSet.ulGetter() {
            @Override
            public MusePoint2D getULShift() {
                return getUlOffset();
            }
        };
    }

    @Override
    protected void renderSlot(MatrixStack matrixStack, Slot slot) {
        if (slot!= null && slot instanceof IHideableSlot) {
            if (slot.isActive()) {
                super.renderSlot(matrixStack, slot);
            }
        } else {
            super.renderSlot(matrixStack, slot);
        }

        if (slot instanceof IIConProvider && slot.getItem().isEmpty() && slot.isActive() ) {
            this.setBlitOffset(100);
            this.itemRenderer.blitOffset = 100.0F;
            ((IIConProvider) slot).drawIconAt(matrixStack, slot.x, slot.y, Colour.WHITE);
            this.itemRenderer.blitOffset = 0.0F;
            this.setBlitOffset(0);
        }
    }

    public void renderBackgroundRect(MatrixStack matrixStack, int mouseX, int mouseY, float frameTime) {
        backgroundRect.render(matrixStack, mouseX, mouseY, frameTime);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
    }


    @Override
    public void init() {
        super.init();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        creationTime = System.currentTimeMillis();
        backgroundRect.init(absX(-1), absY(-1), absX(1), absY(1));
    }

    /**
     * Adds a frame to this gui's draw list.
     *
     * @param frame
     */
    public void addFrame(IGuiFrame frame) {
        frames.add(frame);
    }

    /**
     * inherited from ContainerScreen..
     * @param matrixStack
     * @param frameTime
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void renderBg(MatrixStack matrixStack, float frameTime, int mouseX, int mouseY) {
        renderBackgroundRect(matrixStack, mouseX, mouseY , frameTime);
        update(mouseX, mouseY);
        renderFrames(matrixStack, mouseX, mouseY, frameTime);
    }

    public void update(double x, double y) {
        frames.forEach(frame->frame.update(x, y));
    }

    public void renderFrames(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        frames.forEach(frame->frame.render(matrixStack, mouseX, mouseY, partialTicks));
    }

    @Override
    public void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        renderFrameLabels(matrixStack, mouseX, mouseY);
    }

    public void renderFrameLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        frames.forEach(frame -> frame.renderLabels(matrixStack, mouseX, mouseY));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        if (frames.stream().anyMatch(frame->frame.mouseScrolled(mouseX, mouseY, dWheel))) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, dWheel);
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (frames.stream().anyMatch(frame->frame.mouseClicked(mouseX, mouseY, button))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return slot.isActive() && this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    /**
     * Called when the mouse is moved or a mouse button is released. Signature:
     * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
     * mouseUp
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int which) {
        if (frames.stream().anyMatch(frame->frame.mouseReleased(mouseX, mouseY, which))) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, which);
    }

    public void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        List<ITextComponent> tooltip = getToolTip(mouseX, mouseY);
        if (tooltip != null) {
            renderComponentTooltip(matrixStack,tooltip, mouseX,mouseY);
        }
    }

    public List<ITextComponent> getToolTip(int x, int y) {
        List<ITextComponent> hitTip;
        for (IGuiFrame frame : frames) {
            hitTip = frame.getToolTip(x, y);
            if (hitTip != null) {
                return hitTip;
            }
        }
        return null;
    }

    public void setXSize(int xSize) {
        this.imageWidth = xSize;
        this.leftPos = (this.width - getXSize()) / 2;
    }

    public void setYSize(int ySize) {
        this.imageHeight = ySize;
        this.topPos = (this.height - getYSize()) / 2;
    }

    public MusePoint2D center() {
        return new MusePoint2D(getGuiLeft(), getGuiTop()).plus(getXSize() * 0.5, getYSize() * 0.5);
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param relx Relative X coordinate
     * @return Absolute X coordinate
     */
    public int absX(double relx) {
        int absx = (int) ((relx + 1) * getXSize() / 2);
        int xpadding = (width - getXSize()) / 2;
        return absx + xpadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relX(double absx) {
        int padding = (width - getXSize()) / 2;
        return (int) ((absx - padding) * 2 / getXSize() - 1);
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param rely Relative Y coordinate
     * @return Absolute Y coordinate
     */
    public int absY(double rely) {
        int absy = (int) ((rely + 1) * getYSize() / 2);
        int ypadding = (height - getYSize()) / 2;
        return absy + ypadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relY(float absy) {
        int padding = (height - getYSize()) / 2;
        return (int) ((absy - padding) * 2 / getYSize() - 1);
    }

    @Override
    public Minecraft getMinecraft() {
        this.minecraft = Minecraft.getInstance();
        return this.minecraft;
    }
}