package dev.cammiescorner.boxtrot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public class EnderManMixin {
	@ModifyExpressionValue(method = "isLookingAtMe", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
	))
	public boolean boxtrot$cantSeeMe(boolean original, @Local ItemStack stack) {
		return BoxTrotConfig.doesBarrelFoolEndermen && stack.is(Items.BARREL) || original;
    }
}
