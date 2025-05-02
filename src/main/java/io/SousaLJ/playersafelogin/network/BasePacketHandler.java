package io.SousaLJ.playersafelogin.network;

import io.SousaLJ.playersafelogin.PlayerSafeLogin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public abstract class BasePacketHandler {
    private final String protocolVersion;

    protected BasePacketHandler(IEventBus modEventBus, String protocolVersion) {
        this.protocolVersion = protocolVersion;
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
    }

    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PlayerSafeLogin.MODID)
                .versioned(protocolVersion);

        registerClientToServer(new PacketRegistrar(registrar, true));
        registerServerToClient(new PacketRegistrar(registrar, false));
    }

    protected abstract void registerClientToServer(PacketRegistrar registrar);
    protected abstract void registerServerToClient(PacketRegistrar registrar);

    protected record SimplePacketPayLoad(CustomPacketPayload.Type<CustomPacketPayload> type) implements CustomPacketPayload {

        private SimplePacketPayLoad(ResourceLocation id) {
            this(new CustomPacketPayload.Type<>(id));
        }
    }

    protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {

        public <MSG extends IPlayerSafeLoginPacket> void configuration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            if (toServer) {
                registrar.configurationToServer(type, reader, IPlayerSafeLoginPacket::handle);
            } else {
                registrar.configurationToClient(type, reader, IPlayerSafeLoginPacket::handle);
            }
        }

        public <MSG extends IPlayerSafeLoginPacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
            if (toServer) {
                registrar.playToServer(type, reader, IPlayerSafeLoginPacket::handle);
            } else {
                registrar.playToClient(type, reader, IPlayerSafeLoginPacket::handle);
            }
        }

        public SimplePacketPayLoad playInstanced(ResourceLocation id, IPayloadHandler<CustomPacketPayload> handler) {
            SimplePacketPayLoad payload = new SimplePacketPayLoad(id);
            if (toServer) {
                registrar.playToServer(payload.type(), StreamCodec.unit(payload), handler);
            } else {
                registrar.playToClient(payload.type(), StreamCodec.unit(payload), handler);
            }
            return payload;
        }
    }
}