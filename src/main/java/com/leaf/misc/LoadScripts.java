package com.leaf.misc;

import com.leaf.Leaf;
import ch.njol.skript.ScriptLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* 
public class LoadScripts {
	
	// TOO BAD!
    private static File copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
		return file;

    }	
    
	@SuppressWarnings("deprecation")
	public static void test(){
	   
	   File test = null;
	try {
		test = LoadScripts.copyInputStreamToFile(Leaf.getInstance().getResource("Library.sk"), new File(Leaf.getInstance().getDataFolder() + "/Library.sk"));
	} catch (IOException e) {
		e.printStackTrace();
	}
	   ScriptLoader.loadScripts(test);
	   
    }
}
*/  