package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketCancellerModule extends Module {

    private Value<Mode> mode = (new Value("Packets", Mode.CLIENT));
    private Value<Integer> page = (new Value("SPackets",1, 1, 10, v -> mode.getValue() == Mode.SERVER));
    private Value<Integer> pages = (new Value("CPackets", 1, 1, 4, v -> mode.getValue() == Mode.CLIENT));

    private Value<Boolean> AdvancementInfo = (new Value("AdvancementInfo", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> Animation = (new Value("Animation", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> BlockAction = (new Value("BlockAction", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> BlockBreakAnim = (new Value("BlockBreakAnim", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> BlockChange = (new Value("BlockChange", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> Camera = (new Value("Camera", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> ChangeGameState = (new Value("ChangeGameState", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));
    private Value<Boolean> Chat = (new Value("Chat", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 1));

    private Value<Boolean> ChunkData = (new Value("ChunkData", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> CloseWindow = (new Value("CloseWindow", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> CollectItem = (new Value("CollectItem", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> CombatEvent = (new Value("Combatevent", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> ConfirmTransaction = (new Value("ConfirmTransaction", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> Cooldown = (new Value("Cooldown", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> CustomPayload = (new Value("CustomPayload", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));
    private Value<Boolean> CustomSound = (new Value("CustomSound", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 2));

    private Value<Boolean> DestroyEntities = (new Value("DestroyEntities", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> Disconnect = (new Value("Disconnect", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> DisplayObjective = (new Value("DisplayObjective", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> Effect = (new Value("Effect", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> Entity = (new Value("Entity", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> EntityAttach = (new Value("EntityAttach", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> EntityEffect = (new Value("EntityEffect", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));
    private Value<Boolean> EntityEquipment = (new Value("EntityEquipment", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 3));

    private Value<Boolean> EntityHeadLook = (new Value("EntityHeadLook", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> EntityMetadata = (new Value("EntityMetadata", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> EntityProperties = (new Value("EntityProperties", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> EntityStatus = (new Value("EntityStatus", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> EntityTeleport = (new Value("EntityTeleport", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> EntityVelocity = (new Value("EntityVelocity", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> Explosion = (new Value("Explosion", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));
    private Value<Boolean> HeldItemChange = (new Value("HeldItemChange", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 4));

    private Value<Boolean> JoinGame = (new Value("JoinGame", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> KeepAlive = (new Value("KeepAlive", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> Maps = (new Value("Maps", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> MoveVehicle = (new Value("MoveVehicle", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> MultiBlockChange = (new Value("MultiBlockChange", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> OpenWindow = (new Value("OpenWindow", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> Particles = (new Value("Particles", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));
    private Value<Boolean> PlaceGhostRecipe = (new Value("PlaceGhostRecipe", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 5));

    private Value<Boolean> PlayerAbilities = (new Value("PlayerAbilities", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> PlayerListHeaderFooter = (new Value("PlayerListHeaderFooter", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> PlayerListItem = (new Value("PlayerListItem", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> PlayerPosLook = (new Value("PlayerPosLook", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> RecipeBook = (new Value("RecipeBook", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> RemoveEntityEffect = (new Value("RemoveEntityEffect", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> ResourcePackSend = (new Value("ResourcePackSend", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));
    private Value<Boolean> Respawn = (new Value("Respawn", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 6));

    private Value<Boolean> ScoreboardObjective = (new Value("ScoreboardObjective", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SelectAdvancementsTab = (new Value("SelectAdvancementsTab", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> ServerDifficulty = (new Value("ServerDifficulty", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SetExperience = (new Value("SetExperience", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SetPassengers = (new Value("SetPassengers", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SetSlot = (new Value("SetSlot", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SignEditorOpen = (new Value("SignEditorOpen", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));
    private Value<Boolean> SoundEffect = (new Value("SoundEffect", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 7));

    private Value<Boolean> SpawnExperienceOrb = (new Value("SpawnExperienceOrb", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnGlobalEntity = (new Value("SpawnGlobalEntity", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnMob = (new Value("SpawnMob", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnObject = (new Value("SpawnObject", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnPainting = (new Value("SpawnPainting", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnPlayer = (new Value("SpawnPlayer", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> SpawnPosition = (new Value("SpawnPosition", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));
    private Value<Boolean> Statistics = (new Value("Statistics", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 8));

    private Value<Boolean> TabComplete = (new Value("TabComplete", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> Teams = (new Value("Teams", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> TimeUpdate = (new Value("TimeUpdate", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> Title = (new Value("Title", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> UnloadChunk = (new Value("UnloadChunk", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> UpdateBossInfo = (new Value("UpdateBossInfo", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> UpdateHealth = (new Value("UpdateHealth", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));
    private Value<Boolean> UpdateScore = (new Value("UpdateScore", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 9));

    private Value<Boolean> UpdateTileEntity = (new Value("UpdateTileEntity", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 10));
    private Value<Boolean> UseBed = (new Value("UseBed", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 10));
    private Value<Boolean> WindowItems = (new Value("WindowItems", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 10));
    private Value<Boolean> WindowProperty = (new Value("WindowProperty", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 10));
    private Value<Boolean> WorldBorder = (new Value("WorldBorder", false, v -> mode.getValue() == Mode.SERVER && page.getValue() == 10));

    private Value<Boolean> Animations = (new Value("Animations", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ChatMessage = (new Value("ChatMessage", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ClickWindow = (new Value("ClickWindow", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ClientValues = (new Value("ClientValues", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ClientStatus = (new Value("ClientStatus", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> CloseWindows = (new Value("CloseWindows", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ConfirmTeleport = (new Value("ConfirmTeleport", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));
    private Value<Boolean> ConfirmTransactions = (new Value("ConfirmTransactions", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 1));

    private Value<Boolean> CreativeInventoryAction = (new Value("CreativeInventoryAction", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> CustomPayloads = (new Value("CustomPayloads", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> EnchantItem = (new Value("EnchantItem", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> EntityAction = (new Value("EntityAction", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> HeldItemChanges = (new Value("HeldItemChanges", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> Input = (new Value("Input", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> KeepAlives = (new Value("KeepAlives", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));
    private Value<Boolean> PlaceRecipe = (new Value("PlaceRecipe", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 2));

    private Value<Boolean> Player = (new Value("Player", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> PlayerAbility = (new Value("PlayerAbility", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> PlayerDigging = (new Value("PlayerDigging", false, v -> mode.getValue() == Mode.CLIENT && page.getValue() == 3));
    private Value<Boolean> PlayerTryUseItem = (new Value("PlayerTryUseItem", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> PlayerTryUseItemOnBlock = (new Value("TryUseItemOnBlock", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> RecipeInfo = (new Value("RecipeInfo", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> ResourcePackStatus = (new Value("ResourcePackStatus", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));
    private Value<Boolean> SeenAdvancements = (new Value("SeenAdvancements", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 3));

    private Value<Boolean> PlayerPackets = (new Value("PlayerPackets", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> Spectate = (new Value("Spectate", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> SteerBoat = (new Value("SteerBoat", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> TabCompletion = (new Value("TabCompletion", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> UpdateSign = (new Value("UpdateSign", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> UseEntity = (new Value("UseEntity", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));
    private Value<Boolean> VehicleMove = (new Value("VehicleMove", false, v -> mode.getValue() == Mode.CLIENT && pages.getValue() == 4));

    public enum Mode {
        CLIENT, SERVER
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {

        if (!this.isEnabled()) {
            return;
        }

        if (event.getPacket() instanceof CPacketAnimation && Animations.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketChatMessage && ChatMessage.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketClickWindow && ClickWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketClientStatus && ClientStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCloseWindow && CloseWindows.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport && ConfirmTeleport.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTransaction && ConfirmTransactions.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCreativeInventoryAction && CreativeInventoryAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCustomPayload && CustomPayloads.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketEnchantItem && EnchantItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketEntityAction && EntityAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketHeldItemChange && HeldItemChanges.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketInput && Input.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketKeepAlive && KeepAlives.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlaceRecipe && PlaceRecipe.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer && Player.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerAbilities && PlayerAbility.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerDigging && PlayerDigging.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && PlayerTryUseItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && PlayerTryUseItemOnBlock.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketRecipeInfo && RecipeInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketResourcePackStatus && ResourcePackStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSeenAdvancements && SeenAdvancements.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSpectate && Spectate.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketSteerBoat && SteerBoat.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketTabComplete && TabCompletion.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketUpdateSign && UpdateSign.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketUseEntity && UseEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketVehicleMove && VehicleMove.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!this.isEnabled()) {
            return;
        }

        if (event.getPacket() instanceof SPacketAdvancementInfo && AdvancementInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketAnimation && Animation.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockAction && BlockAction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim && BlockBreakAnim.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketBlockChange && BlockChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCamera && Camera.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChangeGameState && ChangeGameState.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChat && Chat.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChunkData && ChunkData.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCloseWindow && CloseWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCollectItem && CollectItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCombatEvent && CombatEvent.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketConfirmTransaction && ConfirmTransaction.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCooldown && Cooldown.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCustomPayload && CustomPayload.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCustomSound && CustomSound.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDestroyEntities && DestroyEntities.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDisconnect && Disconnect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketChunkData && ChunkData.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCloseWindow && CloseWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketCollectItem && CollectItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketDisplayObjective && DisplayObjective.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEffect && Effect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntity && Entity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityAttach && EntityAttach.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityEffect && EntityEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityEquipment && EntityEquipment.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityHeadLook && EntityHeadLook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityMetadata && EntityMetadata.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityProperties && EntityProperties.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityStatus && EntityStatus.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityTeleport && EntityTeleport.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity && EntityVelocity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketExplosion && Explosion.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketHeldItemChange && HeldItemChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketJoinGame && JoinGame.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketKeepAlive && KeepAlive.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMaps && Maps.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMoveVehicle && MoveVehicle.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketMultiBlockChange && MultiBlockChange.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketOpenWindow && OpenWindow.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketParticles && Particles.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlaceGhostRecipe && PlaceGhostRecipe.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerAbilities && PlayerAbilities.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerListHeaderFooter && PlayerListHeaderFooter.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerListItem && PlayerListItem.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && PlayerPosLook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRecipeBook && RecipeBook.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRemoveEntityEffect && RemoveEntityEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketResourcePackSend && ResourcePackSend.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketRespawn && Respawn.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketScoreboardObjective && ScoreboardObjective.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSelectAdvancementsTab && SelectAdvancementsTab.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketServerDifficulty && ServerDifficulty.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetExperience && SetExperience.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetPassengers && SetPassengers.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSetSlot && SetSlot.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSignEditorOpen && SignEditorOpen.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && SoundEffect.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnExperienceOrb && SpawnExperienceOrb.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnGlobalEntity && SpawnGlobalEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnMob && SpawnMob.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnObject && SpawnObject.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPainting && SpawnPainting.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPlayer && SpawnPlayer.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnPosition && SpawnPosition.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketStatistics && Statistics.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTabComplete && TabComplete.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTeams && Teams.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTimeUpdate && TimeUpdate.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketTitle && Title.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUnloadChunk && UnloadChunk.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateBossInfo && UpdateBossInfo.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateHealth && UpdateHealth.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateScore && UpdateScore.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateTileEntity && UpdateTileEntity.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketUseBed && UseBed.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWindowItems && WindowItems.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWindowProperty && WindowProperty.getValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketWorldBorder && WorldBorder.getValue()) {
            event.setCanceled(true);
        }
    }

    public PacketCancellerModule() {
        super("PacketCanceller", Category.MISC);
    }

}
