package com.mateus.ordemmod;

import com.mateus.ordemmod.Data.IPlayerData;
import com.mateus.ordemmod.Data.PlayerData;
import com.mateus.ordemmod.Registries.CapabilityRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OrdemMod.MOD_ID)
public class OrdemMod
{
    public static final String MOD_ID = "ordemmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public OrdemMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof PlayerEntity){
            event.addCapability(new ResourceLocation(OrdemMod.MOD_ID, "data"), new ICapabilityProvider() {
                private final LazyOptional<IPlayerData> instance = LazyOptional.of(PlayerData::new);

                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return cap == CapabilityRegistry.PLAYER_DATA ? instance.cast() : LazyOptional.empty();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event) {
        event.getPlayer().getCapability(CapabilityRegistry.PLAYER_DATA).ifPresent(data -> {
            CompoundNBT nbt = event.getPlayer().getPersistentData();
            CompoundNBT customData = new CompoundNBT();
            customData.putInt("NEX", data.getNex());
            customData.putString("Classe", data.getClasse());

            for(Map.Entry<String, Integer> entry : data.getAttributes().entrySet()){
                customData.putInt(entry.getKey(), entry.getValue());
            }

            nbt.put("PlayerData", customData);
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            event.getOriginal().getCapability(CapabilityRegistry.PLAYER_DATA).ifPresent(oldData -> {
                event.getPlayer().getCapability(CapabilityRegistry.PLAYER_DATA).ifPresent(newData -> {
                    newData.setNex(oldData.getNex());
                    newData.setClasse(oldData.getClasse());


                    String[] attrs = {"forca", "agilidade", "vigor", "presenca", "intelecto"};
                    List<String> attrList = new ArrayList<>(Arrays.asList(attrs));
                    for (String key : oldData.getAttributes().keySet()) {
                        if (attrList.contains(key)) {
                            newData.setAttributes(key, oldData.getAttributes().get(key));
                        }
                    }

                });
            });
        }
    }

    @SubscribeEvent
    public void PlayerTickEvent(TickEvent.PlayerTickEvent event){
        event.player.getCapability(CapabilityRegistry.PLAYER_DATA).ifPresent(data -> {
            int nex = data.getNex();
            event.player.sendMessage(ITextComponent.getTextComponentOrEmpty("NEX: " + nex), event.player.getUniqueID());
        });
    }

    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event){
        if(event.getEntity() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) event.getEntity();
            player.getCapability(CapabilityRegistry.PLAYER_DATA).ifPresent(data -> {
                data.setNex(data.getNex()+1);
            });
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event){
            CapabilityRegistry.register();
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
        }
    }
}
