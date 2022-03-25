package fr.gamalta.osgarod.enemyhealthinfo.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

public class Configuration {

	private File file;
	private JavaPlugin plugin;
	private String fileName;
	String parentFileName;
	FileConfiguration config;

	public Configuration(JavaPlugin plugin, String parentFileName, String fileName) {

		this.plugin = plugin;
		this.fileName = fileName.contains(".yml") ? fileName : fileName + ".yml";
		this.parentFileName = parentFileName.substring(parentFileName.length() - 1).equals("/") ? parentFileName : parentFileName + File.separator;
		file = new File("plugins/" + this.parentFileName + this.fileName);
		createConfig();

	}

	private void createConfig() {

		if (!file.exists()) {

			try {

				if (!file.getParentFile().exists()) {

					file.getParentFile().mkdirs();

				}

				file.createNewFile();

			} catch (IOException e) {
			}

			InputStream in = plugin.getResource(parentFileName + file.getName());

			try {

				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void deleteConfig() {

		file.delete();

	}

	public FileConfiguration getConfig() {

		return config;
	}

	public void loadConfig() {

		config = YamlConfiguration.loadConfiguration(file);
	}

	public File getFile() {

		return file;

	}

	public void saveConfig(FileConfiguration config) {

		try {

			config.save(file);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public boolean contains(String path) {

		Object obj;

		try {

			obj = config.get(path);

		} catch (Exception e) {
			return false;
		}

		return obj != null;
	}

	public Object get(String path) {

		return config.get(path);
	}

	public void set(String path, Object obj) {

		config.set(path, obj);
		saveConfig(config);
	}

	public ConfigurationSection getConfigurationSection(String path) {

		return config.getConfigurationSection(path);
	}

	public List<Byte> getByteList(String path) {

		return config.getByteList(path);
	}

	public List<Short> getShortList(String path) {

		return config.getShortList(path);
	}

	public Integer getInt(String path) {

		return config.getInt(path);
	}

	public Long getLong(String path) {

		return config.getLong(path);
	}

	public List<Long> getLongList(String path) {

		return config.getLongList(path);
	}

	public List<Float> getFloatList(String path) {

		return config.getFloatList(path);
	}

	public double getDouble(String path) {

		return config.getDouble(path);
	}

	public List<Double> getDoubleList(String path) {

		return config.getDoubleList(path);
	}

	public boolean getBoolean(String path) {

		return config.getBoolean(path);
	}

	public List<Boolean> getBooleanList(String path) {

		return config.getBooleanList(path);
	}

	public String getString(String path) {

		return config.getString(path);
	}

	public List<String> getStringList(String path) {

		return config.getStringList(path);
	}

	@Override
	public String toString() {

		return fileName;
	}
}