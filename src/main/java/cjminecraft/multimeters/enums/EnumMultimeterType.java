package cjminecraft.multimeters.enums;

import net.minecraft.util.IStringSerializable;

public enum EnumMultimeterType implements IStringSerializable {

    ENERGY(0, "energy"), ITEM(1, "item"), FLUID(2, "fluid");

    private int id;
    private String name;

    EnumMultimeterType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
