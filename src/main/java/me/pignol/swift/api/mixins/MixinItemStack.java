package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.misc.Tooltips;
import me.pignol.swift.client.modules.misc.TrueDurability;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Shadow
    public int itemDamage;

    private String cachedDisplayName;

    @Inject(method = "<init>(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)V", at = @At(value = "RETURN"))
    @Dynamic
    private void initHook(Item item, int idkWhatDisIsIPastedThis, int dura, NBTTagCompound compound, CallbackInfo info) {
        this.itemDamage = this.checkDurability(this.itemDamage, dura);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void initHook2(NBTTagCompound compound, CallbackInfo info) {
        this.itemDamage = this.checkDurability(this.itemDamage, compound.getShort("Damage"));
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getTranslatedName(I)Ljava/lang/String;"))
    public String replaceRomanNumerals(Enchantment instance, int level) {
        if (Tooltips.getInstance().isEnabled() && Tooltips.getInstance().numbers.getValue()) {
            return getTranslatedName(instance, level);
        }
        return instance.getTranslatedName(level);
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void returnCachedDisplayName(CallbackInfoReturnable<String> cir) {
        if (cachedDisplayName != null) {
            cir.setReturnValue(cachedDisplayName);
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    private void cacheDisplayName(CallbackInfoReturnable<String> cir) {
        cachedDisplayName = cir.getReturnValue();
    }

    @Inject(method = "setStackDisplayName", at = @At("HEAD"))
    private void resetCachedDisplayName(String displayName, CallbackInfoReturnable<ItemStack> cir) {
        cachedDisplayName = null;
    }

    private String getTranslatedName(Enchantment enchant, int level) {
        String s = I18n.translateToLocal(enchant.getName());

        if (enchant.isCurse()) {
            s = TextFormatting.RED + s;
        }

        return level == 1 && enchant.getMaxLevel() == 1 ? s : s + " " + level;
    }

    private int checkDurability(int damage, int dura) {
        int trueDura = damage;
        if (TrueDurability.getInstance().isEnabled() && dura < 0) {
            trueDura = dura;
        }
        return trueDura;
    }

}
