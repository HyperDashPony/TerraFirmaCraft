/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.forge.ForgeRule;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Anvil")
@ZenRegister
public class Anvil
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(IItemStack output, int minTier, crafttweaker.api.item.IIngredient input, String... rules)
    {
        if (output == null || input == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient ingredient = CTHelper.getInternalIngredient(input);
        if (rules.length == 0 || rules.length > 3)
            throw new IllegalArgumentException("Rules length must be within the closed interval [1, 3]");
        ForgeRule[] forgeRules = new ForgeRule[rules.length];
        for (int i = 0; i < rules.length; i++)
        {
            String str = rules[i];
            ForgeRule rl = ForgeRule.valueOf(str.toUpperCase());
            forgeRules[i] = rl;
        }
        Metal.Tier tier = Metal.Tier.valueOf(minTier);
        ItemStack outputItem = (ItemStack) output.getInternal();
        AnvilRecipe recipe = new AnvilRecipe(new ResourceLocation("crafttweaker", outputItem.getTranslationKey()),
            ingredient, outputItem, tier, forgeRules);
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<Remove> removeList = new ArrayList<>();
        TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(x -> removeList.add(new Remove(x.getRegistryName())));
        for (Remove rem : removeList)
        {
            CraftTweakerAPI.apply(rem);
        }
    }

    private static class Add implements IAction
    {
        private final AnvilRecipe recipe;

        Add(AnvilRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void apply()
        {
            TFCRegistries.ANVIL.register(recipe);
        }

        @Override
        public String describe()
        {
            return "Adding anvil recipe for " + recipe.getOutputs().get(0).getDisplayName();
        }
    }

    private static class Remove implements IAction
    {
        private final ResourceLocation location;

        Remove(ResourceLocation location)
        {
            this.location = location;
        }

        @Override
        public void apply()
        {
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.ANVIL;
            modRegistry.remove(location);
        }

        @Override
        public String describe()
        {
            return "Removing anvil recipe " + location.toString();
        }
    }
}
