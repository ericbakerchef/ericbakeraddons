package com.example.mixinmod;

import net.minecraft.class_2246;
import net.minecraft.class_2248;

public enum DoorType {
    WITHER(class_2246.field_10381),
    BLOOD(class_2246.field_10176),
    ENTRANCE(class_2246.field_10328);

    private final class_2248 block;

    DoorType(class_2248 block) {
        this.block = block;
    }

    public class_2248 getBlock() {
        return this.block;
    }
}
