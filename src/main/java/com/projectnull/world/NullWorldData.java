package com.projectnull.world;

import com.projectnull.ProjectNull;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class NullWorldData extends SavedData {
    private static final String ID = ProjectNull.MODID + "_null_world";
    private boolean nullHasJoined;

    public static NullWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(NullWorldData::new, NullWorldData::load, null),
                ID
        );
    }

    public NullWorldData() {
    }

    public NullWorldData(CompoundTag tag) {
        this.nullHasJoined = tag.getBoolean("NullHasJoined");
    }

    public static NullWorldData load(CompoundTag tag, HolderLookup.Provider provider) {
        return new NullWorldData(tag);
    }

    public boolean hasNullJoined() {
        return nullHasJoined;
    }

    public void setNullJoined() {
        this.nullHasJoined = true;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("NullHasJoined", nullHasJoined);
        return tag;
    }
}
