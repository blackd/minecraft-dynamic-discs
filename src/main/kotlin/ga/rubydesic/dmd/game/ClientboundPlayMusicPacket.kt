package ga.rubydesic.dmd.game

import ga.rubydesic.dmd.MOD_ID
import ga.rubydesic.dmd.download.MusicId
import ga.rubydesic.dmd.download.MusicSource

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

data class ClientboundPlayMusicPacket constructor(
    val source: MusicSource,
    val pos: BlockPos,
    val id: String
) {
    companion object {
        val packetId = ResourceLocation(MOD_ID, "play_music")

        fun registerNg() {
            ClientPlayNetworking.registerGlobalReceiver(packetId, object : ClientPlayNetworking.PlayChannelHandler {
                override fun receive(client: Minecraft,
                                     handler: ClientPacketListener,
                                     data: FriendlyByteBuf,
                                     responseSender: PacketSender) {
                    val (source, pos, id) = ClientboundPlayMusicPacket(data)
                    client.execute {
                        client.levelRenderer.playYoutubeMusic(MusicId(source, id), pos)
                    }
                }
            })
        }
    }

    constructor(buf: FriendlyByteBuf) : this(
        MusicSource.values[buf.readByte().toInt()],
        buf.readBlockPos(),
        buf.readCharSequence(buf.readableBytes(), Charsets.UTF_8).toString()
    )

    fun write(buf: FriendlyByteBuf) {
        buf.writeByte(source.ordinal)
        buf.writeBlockPos(pos)
        buf.writeCharSequence(id, Charsets.UTF_8)
    }

}
