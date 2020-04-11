package com.leaf.yaml.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.ParameterizedType;
import com.leaf.Leaf;

public class SkriptYamlUtils {

	public static File[] directoryFilter(String name, boolean root, String errorPrefix) {
		File dir = null;

		if (root) {
			dir = new File(StringUtil.checkRoot(name));
		} else {
			//Path server = Paths.get("").normalize().toAbsolutePath();
			String server = new File("").getAbsoluteFile().getAbsolutePath();
			dir = new File(server + File.separator + name);
		}

		if(!dir.isDirectory()) {
			Leaf.warn("[" + errorPrefix + " Yaml] " + name + " is not a directory!");
			return null;
		}

		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".yml") | filename.endsWith(".yaml"))
					return true;
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getType(Class<T> c) {
		return (Class<T>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
	}
}
