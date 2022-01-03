package ca.rttv.reagenchant;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Reagenchant implements ModInitializer {
	public static final Identifier withoutReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_without_reagent.png");
	public static final Identifier withReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_with_reagent.png");

	@Override
	public void onInitialize() {

	}
}
