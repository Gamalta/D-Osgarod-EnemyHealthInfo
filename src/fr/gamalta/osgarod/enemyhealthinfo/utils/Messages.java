package fr.gamalta.osgarod.enemyhealthinfo.utils;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Messages {

	JavaPlugin main;
	Configuration Configuration;
	String path;
	String text = "";
	HashMap<String, TextComponent> variants = new HashMap<>();

	public Messages(JavaPlugin main, Configuration Configuration, String path) {

		this.main = main;
		this.Configuration = Configuration;
		this.path = path;
		init();
	}

	private void init() {

		Object obj = Configuration.get(path);

		if (obj instanceof String) {

			text = (String) obj;

		} else if (obj instanceof ConfigurationSection) {

			if (Configuration.contains(path + ".Variants")) {

				for (String variant : Configuration.getConfigurationSection(path + ".Variants").getKeys(false)) {

					TextComponent component = new TextComponent(Configuration.getString(path + ".Variants." + variant + ".Text").replace("&", "§"));
					component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(Configuration.getString(path + ".Variants." + variant + ".Action")), Configuration.getString(path + ".Variants." + variant + ".Result")));

					if (Configuration.contains(path + ".Variants." + variant + ".Hover")) {

						component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Configuration.getString(path + ".Variants." + variant + ".Hover")).create()));
					}

					variants.put(variant, component);
				}
			}

			if (Configuration.contains(path + ".Text")) {

				Object obj1 = Configuration.get(path + ".Text");

				if (obj1 instanceof String) {

					text = (String) obj1;

				} else if (obj1 instanceof List<?> && !((List<?>) obj1).isEmpty() && ((List<?>) obj1).get(0) instanceof String) {

					String string = "";

					for (String line : Configuration.getStringList(path + ".Text")) {

						string += line + "\n";
					}

					text = string.substring(0, string.length() - 1);
				}
			}

			for (String variant : variants.keySet()) {

				if (text.contains(variant)) {

					text = text.replace(variant, "&r" + variant + "&r");
				}
			}
		}
	}

	public Messages replace(String oldString, String newString) {

		text = text.replace(oldString, newString);

		return this;
	}

	public BaseComponent[] create() {

		List<BaseComponent> components = new ArrayList<>();

		for (BaseComponent component : TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text))) {

			if (variants.containsKey(component.toPlainText())) {

				components.add(variants.get(component.toPlainText()));

			} else {

				components.add(component);
			}
		}

		return components.toArray(new BaseComponent[components.size()]);
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public HashMap<String, TextComponent> getVariants() {
		return variants;
	}

	public void setVariants(HashMap<String, TextComponent> variants) {
		this.variants = variants;
	}

	public void addVariants(String path, TextComponent component) {

		variants.put(path, component);
	}
}