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

package com.github.lehjr.numina.util.client.control;

import com.github.lehjr.numina.util.capabilities.player.CapabilityPlayerKeyStates;
import com.github.lehjr.numina.util.capabilities.player.IPlayerKeyStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerMovementInputWrapper {
    public static class PlayerMovementInput {
        public boolean forwardKey;
        public boolean strafeKey;
        public boolean jumpKey;
        public boolean downKey;
        public boolean sneakKey;

        public PlayerMovementInput(
                boolean forwardKey,
                boolean strafeKey,
                boolean jumpKey,
                boolean downKey,
                boolean sneakKey
        ) {
            this.forwardKey = forwardKey;
            this.strafeKey = strafeKey;
            this.jumpKey = jumpKey;
            this.downKey = downKey;
            this.sneakKey = sneakKey;
        }
    }

    public static PlayerMovementInput get(PlayerEntity player) {
        if (player.level.isClientSide) {
            if (player instanceof RemoteClientPlayerEntity) { // multiplayer not dedicated server
                return fromServer(player);
            }
            return fromClient(player);
        }
        return fromServer(player);
    }

    static LazyOptional<IPlayerKeyStates> getCapability(PlayerEntity player) {
        return player.getCapability(CapabilityPlayerKeyStates.PLAYER_KEYSTATES, null);
    }

    static PlayerMovementInput fromServer(PlayerEntity player) {
        boolean forwardKey = false;
        boolean strafeKey = false;
        boolean jumpKey = false;
        boolean downKey = false;
        boolean sneakKey = false;

        LazyOptional<IPlayerKeyStates> playerCap = getCapability(player);
        if (playerCap.isPresent()) {
            forwardKey = playerCap.map(m -> m.getForwardKeyState()).orElse(false);
            strafeKey = playerCap.map(m -> m.getStrafeKeyState()).orElse(false);
            jumpKey = playerCap.map(m -> m.getJumpKeyState()).orElse(false);
            downKey = playerCap.map(m -> m.getDownKeyState()).orElse(false);
            sneakKey = playerCap.map(m -> m.getSneakKeyState()).orElse(false);
        }

        return new PlayerMovementInput(
                forwardKey,
                strafeKey,
                jumpKey,
                downKey,
                player.isCrouching());
    }

    static PlayerMovementInput fromClient(PlayerEntity player) {
        boolean forwardKey = false;
        boolean strafeKey = false;
        boolean jumpKey = false;
        boolean downKey = false;
        boolean sneakKey = player.isCrouching();

        ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player;

//        if (clientPlayer.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
//            forwardKey = Mine
//
//        } else {
            LazyOptional<IPlayerKeyStates> playerCap = getCapability(player);
            if (playerCap.isPresent()) {
                forwardKey = playerCap.map(m -> m.getForwardKeyState()).orElse(false);
                strafeKey = playerCap.map(m -> m.getStrafeKeyState()).orElse(false);
                jumpKey = playerCap.map(m -> m.getJumpKeyState()).orElse(false);
                downKey = playerCap.map(m -> m.getDownKeyState()).orElse(false);
            }
//        }

        return new PlayerMovementInput(
                forwardKey,
                strafeKey,
                jumpKey,
                downKey,
                sneakKey);
    }
}