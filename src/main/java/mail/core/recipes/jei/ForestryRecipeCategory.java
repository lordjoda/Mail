package mail.core.recipes.jei;

import mail.core.config.Constants;
import mail.core.utils.Translator;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class ForestryRecipeCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> {
	private final IDrawable background;
	private final String localizedName;

	public ForestryRecipeCategory(IDrawable background, String unlocalizedName) {
		this.background = background;
		this.localizedName = Translator.translateToLocal(unlocalizedName);
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getModName() {
		return Constants.MOD_NAME;
	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}
}
