package com.mateus.ordemmod.Registries;

import com.mateus.ordemmod.Data.IPlayerData;
import com.mateus.ordemmod.Data.PlayerData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.*;

public class CapabilityRegistry {

    @CapabilityInject(IPlayerData.class)
    public static final Capability<IPlayerData> PLAYER_DATA = null;
    public static void register(){
        CapabilityManager.INSTANCE.register(IPlayerData.class, new Capability.IStorage<IPlayerData>() {

            @Override
            public INBT writeNBT(Capability<IPlayerData> capability, IPlayerData instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("NEX", instance.getNex());
                tag.putString("Classe", instance.getClasse());
                for(Map.Entry<String, Integer> entry : instance.getAttributes().entrySet()){
                    tag.putInt(entry.getKey(), entry.getValue());
                }
                return tag;
            }

            @Override
            public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.setNex(tag.getInt("NEX"));
                instance.setClasse(tag.getString("Classe"));

                String[] attrs = {"forca", "agilidade", "vigor", "presenca", "intelecto"};
                List<String> attrList = new ArrayList<>(Arrays.asList(attrs));
                for (String key : tag.keySet()) {
                    if (attrList.contains(key)) {
                        instance.setAttributes(key, tag.getInt(key));
                    }
                }
            }
        }, PlayerData::new);
    }
}
