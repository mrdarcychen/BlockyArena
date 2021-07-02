/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.commands;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MessageBroker {
    private static final Text PREDIVIDER = Text
            .builder("------------<<< BlockyArena >>>------------")
            .color(TextColors.GRAY)
            .build();
    private static final Text POSTDIVIDER = Text
            .builder("-----------------------------------------")
            .color(TextColors.GRAY)
            .build();

    public static Text wrap(Text text) {
        return Text.builder().append(PREDIVIDER).append(text).append(POSTDIVIDER).build();
    }
}
